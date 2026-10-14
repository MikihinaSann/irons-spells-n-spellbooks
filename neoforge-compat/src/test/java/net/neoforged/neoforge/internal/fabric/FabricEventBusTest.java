package net.neoforged.neoforge.internal.fabric;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import org.junit.jupiter.api.Test;

/** FabricEventBus listener-resolution and dispatch semantics. */
public class FabricEventBusTest {

    public static class EventA extends Event {
    }

    public static class EventB extends EventA {
    }

    public static class Unrelated extends Event {
    }

    public static class Cancelable extends Event implements net.neoforged.bus.api.ICancellableEvent {
    }

    static class InstanceHolder {
        final AtomicInteger hits = new AtomicInteger();

        void onA(EventA event) {
            hits.incrementAndGet();
        }
    }

    static final AtomicInteger staticHits = new AtomicInteger();

    static void onAStatic(EventA event) {
        staticHits.incrementAndGet();
    }

    @Test
    void instanceMethodReferenceDelivers() {
        FabricEventBus bus = new FabricEventBus();
        InstanceHolder holder = new InstanceHolder();
        bus.addListener(holder::onA);
        bus.post(new EventA());
        assertEquals(1, holder.hits.get());
        bus.post(new Unrelated());
        assertEquals(1, holder.hits.get(), "unrelated event must not reach the listener");
    }

    @Test
    void staticMethodReferenceDelivers() {
        staticHits.set(0);
        FabricEventBus bus = new FabricEventBus();
        bus.addListener(FabricEventBusTest::onAStatic);
        bus.post(new EventA());
        assertEquals(1, staticHits.get());
    }

    @Test
    void typedLambdaDeliversOnlyItsType() {
        FabricEventBus bus = new FabricEventBus();
        AtomicInteger hits = new AtomicInteger();
        bus.addListener((EventB e) -> hits.incrementAndGet());
        bus.post(new EventB());
        assertEquals(1, hits.get());
        bus.post(new Unrelated());
        assertEquals(1, hits.get());
    }

    @Test
    void subclassEventReachesSuperclassListeners() {
        FabricEventBus bus = new FabricEventBus();
        AtomicInteger hits = new AtomicInteger();
        bus.addListener((EventA e) -> hits.incrementAndGet());
        bus.post(new EventB());
        assertEquals(1, hits.get(), "EventB must reach the EventA listener (superclass dispatch)");
    }

    @Test
    void cancellationHonorsReceiveCanceledAndPriority() {
        FabricEventBus bus = new FabricEventBus();
        AtomicBoolean normalSaw = new AtomicBoolean();
        AtomicBoolean canceledSaw = new AtomicBoolean();
        // HIGHEST cancels; NORMAL must not see it; a receiveCanceled listener must.
        bus.addListener(EventPriority.HIGHEST, false, Cancelable.class, e -> e.setCanceled(true));
        bus.addListener(EventPriority.NORMAL, false, Cancelable.class, e -> normalSaw.set(true));
        bus.addListener(EventPriority.NORMAL, true, Cancelable.class, e -> canceledSaw.set(true));
        Cancelable event = bus.post(new Cancelable());
        assertTrue(event.isCanceled());
        assertFalse(normalSaw.get());
        assertTrue(canceledSaw.get());
    }

    @Test
    void unregisterStopsDeliveryAndRefreshesCache() {
        FabricEventBus bus = new FabricEventBus();
        AtomicInteger hits = new AtomicInteger();
        Object fixture = new Object() {
            @SubscribeEvent
            public void onA(EventA e) {
                hits.incrementAndGet();
            }
        };
        bus.register(fixture);
        bus.post(new EventB()); // warms the subclass dispatch cache
        assertEquals(1, hits.get());
        bus.unregister(fixture);
        bus.post(new EventA());
        bus.post(new EventB());
        assertEquals(1, hits.get(), "listener removed after unregister");
    }

    @Test
    void handlerThrownClassCastExceptionPropagates() {
        FabricEventBus bus = new FabricEventBus();
        // A CCE raised INSIDE the handler body (not the erased bridge) must
        // propagate — its message does not name the posted event's class.
        bus.addListener(Event.class, e -> {
            throw new ClassCastException("bug inside handler");
        });
        assertThrows(ClassCastException.class, () -> bus.post(new Unrelated()));
    }
}
