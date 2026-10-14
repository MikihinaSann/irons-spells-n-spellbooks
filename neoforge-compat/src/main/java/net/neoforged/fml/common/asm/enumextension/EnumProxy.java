package net.neoforged.fml.common.asm.enumextension;

import net.neoforged.neoforge.internal.fabric.EnumExtender;

import java.util.function.Supplier;

/**
 * EnumProxy backed by real runtime enum extension. The constructor parameters
 * are the enum's declared ctor args (matching the enumextensions.json entry);
 * the constant is materialized lazily through {@link EnumExtender}.
 */
public class EnumProxy<T> implements Supplier<T> {
    private final Class<T> enumClass;
    private final Object[] params;
    private volatile T value;

    public EnumProxy(Class<T> enumClass, Object... params) {
        this.enumClass = enumClass;
        this.params = params;
    }

    /** The declared-ctor parameters this proxy represents. */
    public Object[] enumParameters() {
        return params;
    }

    @SuppressWarnings("unchecked")
    private T resolve() {
        T v = value;
        if (v == null) {
            synchronized (this) {
                v = value;
                if (v == null) {
                    // Derive a stable constant name: prefer a "modid:path" style
                    // String param when present, else the enum name + params.
                    String name = null;
                    for (Object p : params) {
                        if (p instanceof String s && s.contains(":")) {
                            name = s.replace(':', '_').replace('/', '_').toUpperCase(java.util.Locale.ROOT);
                            break;
                        }
                    }
                    if (name == null) {
                        name = enumClass.getSimpleName().toUpperCase(java.util.Locale.ROOT)
                                + "_EXT_" + Integer.toHexString(java.util.Arrays.hashCode(params));
                    }
                    v = (T) EnumExtender.create((Class) enumClass, name, params);
                    value = v;
                }
            }
        }
        return v;
    }

    @Override
    public T get() {
        return resolve();
    }

    public T getValue() {
        return resolve();
    }
}
