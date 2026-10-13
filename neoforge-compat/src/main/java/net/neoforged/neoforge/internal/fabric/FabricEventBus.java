package net.neoforged.neoforge.internal.fabric;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Simple priority-aware event bus standing in for NeoForge's BusGroup/EventBus.
 * Handlers are dispatched synchronously, ordered by {@link EventPriority}.
 */
public class FabricEventBus implements IEventBus {
    private record Listener<T extends Event>(EventPriority priority, boolean receiveCanceled, Consumer<T> consumer) {
    }

    private final Map<Class<?>, List<Listener<?>>> listeners = new ConcurrentHashMap<>();
    private final Map<Object, List<Runnable>> registered = new ConcurrentHashMap<>();
    /** Cached merged listener lists per concrete event class; invalidated on registration changes. */
    private final Map<Class<?>, List<Listener<?>>> dispatchCache = new ConcurrentHashMap<>();

    @Override
    public void register(Object target) {
        Class<?> clazz = target instanceof Class<?> c ? c : target.getClass();
        List<Runnable> cleanup = new ArrayList<>();
        for (Method method : collectSubscribed(clazz)) {
            SubscribeEvent ann = method.getAnnotation(SubscribeEvent.class);
            Class<? extends Event> eventType = eventParameter(method);
            if (eventType == null) {
                continue;
            }
            Listener<Event> listener = new Listener<>(ann.priority(), ann.receiveCanceled(), event -> invoke(target, method, event));
            listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
            cleanup.add(() -> listeners.get(eventType).remove(listener));
        }
        if (!cleanup.isEmpty()) {
            registered.put(target instanceof Class<?> ? target : new Identity(target), cleanup);
        }
        sortAll();
    }

    private record Identity(Object target) {
        @Override
        public boolean equals(Object o) {
            return o instanceof Identity other && target == other.target;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(target);
        }
    }

    private static List<Method> collectSubscribed(Class<?> clazz) {
        List<Method> out = new ArrayList<>();
        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            for (Method m : c.getDeclaredMethods()) {
                if (m.isAnnotationPresent(SubscribeEvent.class)) {
                    m.setAccessible(true);
                    out.add(m);
                }
            }
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Event> eventParameter(Method m) {
        if (m.getParameterCount() != 1) {
            return null;
        }
        Class<?> p = m.getParameterTypes()[0];
        if (Event.class.isAssignableFrom(p)) {
            return (Class<? extends Event>) p;
        }
        return null;
    }

    private static void invoke(Object target, Method m, Event event) {
        try {
            m.invoke(target instanceof Class<?> ? null : target, event);
        } catch (Exception e) {
            throw new RuntimeException("Exception in event handler " + m, e);
        }
    }

    @Override
    public void unregister(Object object) {
        List<Runnable> cleanup = registered.remove(object instanceof Class<?> ? object : new Identity(object));
        if (cleanup != null) {
            cleanup.forEach(Runnable::run);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Event> void addListener(Consumer<T> consumer) {
        addListener(EventPriority.NORMAL, false, resolveType(consumer), consumer);
    }

    @Override
    public <T extends Event> void addListener(Class<T> eventType, Consumer<T> consumer) {
        addListener(EventPriority.NORMAL, false, eventType, consumer);
    }

    @Override
    public <T extends Event> void addListener(EventPriority priority, Consumer<T> consumer) {
        addListener(priority, false, resolveType(consumer), consumer);
    }

    @Override
    public <T extends Event> void addListener(EventPriority priority, boolean receiveCanceled, Class<T> eventType, Consumer<T> consumer) {
        if (eventType == Event.class) {
            consumer = typeFilteringConsumer(consumer);
        }
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(new Listener<>(priority, receiveCanceled, consumer));
        sortAll();
    }

    /**
     * Method-reference consumers ({@code bus.addListener(X::y)}) erase their
     * generic type, so {@link #resolveType} falls back to {@link Event}. Wrap
     * them so the erasure-bridge {@link ClassCastException} on a type mismatch
     * acts as a type filter instead of propagating; CCEs thrown inside the
     * handler body itself still propagate.
     */
    private static <T extends Event> Consumer<T> typeFilteringConsumer(Consumer<T> consumer) {
        return event -> {
            try {
                consumer.accept(event);
            } catch (ClassCastException cce) {
                // A type mismatch thrown by the lambda's erased accept bridge
                // always names the posted event's runtime class in its message
                // ("X cannot be cast to class Y"). Anything else is a real bug
                // inside the handler and must propagate.
                String msg = cce.getMessage();
                if (msg == null || !msg.contains(event.getClass().getName())) {
                    throw cce;
                }
            }
        };
    }

    @Override
    public <T extends Event> void addListener(boolean receiveCanceled, Consumer<T> consumer) {
        addListener(EventPriority.NORMAL, receiveCanceled, resolveType(consumer), consumer);
    }

    @Override
    public <T extends Event> void addListener(boolean receiveCanceled, Class<T> eventType, Consumer<T> consumer) {
        addListener(EventPriority.NORMAL, receiveCanceled, eventType, consumer);
    }

    @Override
    public <T extends Event> void addListener(EventPriority priority, boolean receiveCanceled, Consumer<T> consumer) {
        addListener(priority, receiveCanceled, resolveType(consumer), consumer);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Event> Class<T> resolveType(Consumer<T> consumer) {
        // Attempt to resolve the event type from the consumer's generic signature.
        for (Type iface : consumer.getClass().getGenericInterfaces()) {
            if (iface instanceof ParameterizedType pt && pt.getRawType() == Consumer.class) {
                Type arg = pt.getActualTypeArguments()[0];
                if (arg instanceof Class<?> c && Event.class.isAssignableFrom(c)) {
                    return (Class<T>) c;
                }
            }
        }
        for (Method m : consumer.getClass().getDeclaredMethods()) {
            if (m.getName().equals("accept") && m.getParameterCount() == 1 && Event.class.isAssignableFrom(m.getParameterTypes()[0]) && !m.isSynthetic()) {
                return (Class<T>) m.getParameterTypes()[0];
            }
        }
        // Fallback: bridge method on lambdas
        for (Method m : consumer.getClass().getDeclaredMethods()) {
            if (m.getParameterCount() == 1 && Event.class.isAssignableFrom(m.getParameterTypes()[0])) {
                return (Class<T>) m.getParameterTypes()[0];
            }
        }
        return (Class<T>) Event.class;
    }

    @Override
    public <T extends Event> T post(T event) {
        return post(null, event);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Event> T post(EventPriority phase, T event) {
        event.setPhase(phase);
        for (Listener<?> l : listenersFor(event.getClass())) {
            Listener<T> listener = (Listener<T>) l;
            if (event.isCanceled() && !listener.receiveCanceled()) {
                continue;
            }
            listener.consumer().accept(event);
        }
        event.setPhase(null);
        return event;
    }

    /**
     * NeoForge dispatches an event to listeners registered for its type and
     * every supertype (e.g. a {@code LivingEvent} listener receives
     * {@code LivingDeathEvent}). Walks the class hierarchy, merges lists, and
     * keeps them priority-sorted; results are cached per concrete event type.
     */
    private List<Listener<?>> listenersFor(Class<?> eventClass) {
        return dispatchCache.computeIfAbsent(eventClass, c -> {
            List<Listener<?>> combined = new ArrayList<>();
            for (Class<?> type = c; type != null && Event.class.isAssignableFrom(type); type = type.getSuperclass()) {
                combined.addAll(listeners.getOrDefault(type, List.of()));
            }
            combined.sort(Comparator.comparing(Listener::priority));
            return List.copyOf(combined);
        });
    }

    private void sortAll() {
        for (List<Listener<?>> list : listeners.values()) {
            synchronized (list) {
                list.sort(Comparator.comparing(Listener::priority));
            }
        }
        dispatchCache.clear();
    }

    /** NeoForge's EVENT_BUS.start() - enables accepting listeners. Our bus always accepts. */
    public void start() {
    }
}
