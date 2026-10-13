package net.neoforged.fml.common.asm.enumextension;

import java.util.function.Supplier;

/**
 * EnumProxy stand-in. On Fabric, enum extension is implemented via mixin;
 * proxies simply expose the constant parameters.
 */
public class EnumProxy<T> implements Supplier<T> {
    private final T value;

    @SuppressWarnings("unchecked")
    public EnumProxy(Class<T> enumClass, Object... params) {
        T found = null;
        if (params != null && params.length > 0 && params[0] instanceof Supplier<?> sup) {
            found = (T) sup.get();
        }
        this.value = found;
    }

    @Override
    public T get() {
        return value;
    }

    public T getValue() {
        return value;
    }
}
