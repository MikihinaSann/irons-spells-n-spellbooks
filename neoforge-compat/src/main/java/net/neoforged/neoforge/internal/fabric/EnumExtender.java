package net.neoforged.neoforge.internal.fabric;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * Runtime enum constant creation, reproducing NeoForge's enumextensions
 * mechanism on Fabric. New constants are instantiated by invoking the enum's
 * real constructor through a {@link MethodHandle} (which, unlike
 * {@link Constructor#newInstance}, does not reject enums) and then appended to
 * the synthetic {@code $VALUES} array so {@code values()}/{@code valueOf()}
 * see them.
 */
public final class EnumExtender {

    /**
     * {@code sun.misc.Unsafe} — required because {@code $VALUES} is
     * {@code static final} and {@link Field#set} rejects final-static writes
     * on modern JDKs even with {@code setAccessible}.
     */
    private static final Object UNSAFE;
    private static final java.lang.reflect.Method STATIC_FIELD_OFFSET;
    private static final java.lang.reflect.Method STATIC_FIELD_BASE;
    private static final java.lang.reflect.Method OBJECT_FIELD_OFFSET;
    private static final java.lang.reflect.Method GET_OBJECT;
    private static final java.lang.reflect.Method PUT_OBJECT;
    private static final long ENUM_CONSTANTS_OFF;
    private static final long ENUM_CONSTANT_DIR_OFF;

    static {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            UNSAFE = theUnsafe.get(null);
            STATIC_FIELD_OFFSET = unsafeClass.getMethod("staticFieldOffset", Field.class);
            STATIC_FIELD_BASE = unsafeClass.getMethod("staticFieldBase", Field.class);
            OBJECT_FIELD_OFFSET = unsafeClass.getMethod("objectFieldOffset", Field.class);
            GET_OBJECT = unsafeClass.getMethod("getObject", Object.class, long.class);
            PUT_OBJECT = unsafeClass.getMethod("putObject", Object.class, long.class, Object.class);
            // java.lang.Class caches enumConstants / enumConstantDirectory on
            // first use — Enum.valueOf reads the directory, not $VALUES, so a
            // constant appended after the caches populated is invisible until
            // both are rewritten below.
            ENUM_CONSTANTS_OFF = (long) OBJECT_FIELD_OFFSET.invoke(UNSAFE,
                    Class.class.getDeclaredField("enumConstants"));
            ENUM_CONSTANT_DIR_OFF = (long) OBJECT_FIELD_OFFSET.invoke(UNSAFE,
                    Class.class.getDeclaredField("enumConstantDirectory"));
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
    }

    private EnumExtender() {
    }

    private static final java.util.Set<Class<? extends Enum<?>>> EXTENDED_CLASSES = new java.util.HashSet<>();

    /** Enum classes that gained runtime constants through {@link #create}. */
    public static java.util.Set<Class<? extends Enum<?>>> extendedEnumClasses() {
        return EXTENDED_CLASSES;
    }

    /**
     * Creates (or returns the existing) enum constant {@code name} on
     * {@code enumClass}, calling the declared constructor whose arity is
     * {@code params.length} after the implicit {@code (String, int)} prefix.
     */
    @SuppressWarnings("unchecked")
    public static synchronized <T extends Enum<T>> T create(Class<T> enumClass, String name, Object... params) {
        try {
            return Enum.valueOf(enumClass, name);
        } catch (IllegalArgumentException ignored) {
        }
        try {
            Constructor<T> ctor = null;
            for (Constructor<?> c : enumClass.getDeclaredConstructors()) {
                if (c.getParameterCount() == params.length + 2) {
                    ctor = (Constructor<T>) c;
                    break;
                }
            }
            if (ctor == null) {
                // NeoForge enums carry extra constructor overloads the vanilla
                // class lacks (e.g. ArmPose's (boolean, IArmPoseTransformer)).
                // Fall back to the longest vanilla constructor: leading params
                // that fit are passed normally, overflow values are delivered
                // afterwards through IEnumExtensionCarrier.
                for (Constructor<?> c : enumClass.getDeclaredConstructors()) {
                    if (ctor == null || c.getParameterCount() > ctor.getParameterCount()) {
                        ctor = (Constructor<T>) c;
                    }
                }
            }
            if (ctor == null) {
                throw new IllegalArgumentException("No usable constructor on " + enumClass.getName());
            }
            int ordinal = enumClass.getEnumConstants().length;
            Class<?>[] formals = ctor.getParameterTypes();
            int passable = Math.min(params.length, formals.length - 2);
            Object[] args = new Object[formals.length];
            args[0] = name;
            args[1] = ordinal;
            System.arraycopy(params, 0, args, 2, passable);

            // The declared-ctor argument list is matched by arity, so values
            // may not fit the vanilla parameter types — coerce mismatched
            // reference-type slots to null and hand them to the constant
            // afterwards through IEnumExtensionCarrier. Params beyond the
            // ctor's arity (-1 slot) are delivered the same way.
            List<int[]> stashed = null;
            List<Object> stashedValues = null;
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg != null && !formals[i].isPrimitive() && !formals[i].isInstance(arg)) {
                    if (stashed == null) {
                        stashed = new java.util.ArrayList<>();
                        stashedValues = new java.util.ArrayList<>();
                    }
                    stashed.add(new int[]{i});
                    stashedValues.add(arg);
                    args[i] = null;
                }
            }
            for (int i = passable; i < params.length; i++) {
                if (params[i] != null) {
                    if (stashed == null) {
                        stashed = new java.util.ArrayList<>();
                        stashedValues = new java.util.ArrayList<>();
                    }
                    stashed.add(new int[]{-1});
                    stashedValues.add(params[i]);
                }
            }

            MethodHandle handle = MethodHandles.privateLookupIn(enumClass, MethodHandles.lookup())
                    .unreflectConstructor(ctor);
            T instance = (T) handle.invokeWithArguments(args);

            if (stashed != null && instance instanceof net.neoforged.neoforge.common.extensions.IEnumExtensionCarrier carrier) {
                for (int i = 0; i < stashed.size(); i++) {
                    int slot = stashed.get(i)[0];
                    carrier.neoforge$setExtraCtorArg(slot >= 0 ? formals[slot] : null, stashedValues.get(i));
                }
            }

            Field valuesField = null;
            // In production the synthetic $VALUES array is remapped to
            // field_XXXX — match by "static EnumClass[]" type instead of name.
            for (Field f : enumClass.getDeclaredFields()) {
                if (f.getName().equals("$VALUES") || f.getName().equals("ENUM$VALUES")
                        || (java.lang.reflect.Modifier.isStatic(f.getModifiers())
                                && f.getType().isArray()
                                && f.getType().getComponentType() == enumClass)) {
                    valuesField = f;
                    break;
                }
            }
            if (valuesField == null) {
                throw new IllegalStateException("No $VALUES field on " + enumClass.getName());
            }
            valuesField.setAccessible(true);
            T[] arr = (T[]) valuesField.get(null);
            T[] grown = Arrays.copyOf(arr, arr.length + 1);
            grown[arr.length] = instance;
            long offset = (long) STATIC_FIELD_OFFSET.invoke(UNSAFE, valuesField);
            Object base = STATIC_FIELD_BASE.invoke(UNSAFE, valuesField);
            PUT_OBJECT.invoke(UNSAFE, base, offset, grown);

            // Refresh the java.lang.Class caches: enumConstants feeds future
            // enumConstantDirectory builds; an already-built directory gets
            // the new name mapped directly.
            PUT_OBJECT.invoke(UNSAFE, enumClass, ENUM_CONSTANTS_OFF, grown);
            Object directory = GET_OBJECT.invoke(UNSAFE, enumClass, ENUM_CONSTANT_DIR_OFF);
            if (directory instanceof java.util.Map<?, ?> map) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, T> dir = (java.util.Map<String, T>) map;
                dir.put(name, instance);
            }
            EXTENDED_CLASSES.add((Class<? extends Enum<?>>) enumClass);
            return instance;
        } catch (Throwable t) {
            throw new RuntimeException("Failed to extend enum " + enumClass.getName()
                    + " with " + name, t);
        }
    }
}
