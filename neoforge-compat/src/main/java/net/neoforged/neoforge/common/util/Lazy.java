package net.neoforged.neoforge.common.util;

import java.util.function.Supplier;

/**
 * NeoForge {@code Lazy} — memoizing supplier.
 */
public final class Lazy<T> implements Supplier<T> {
    private final Supplier<T> supplier;
    private volatile T value;

    private Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> Lazy<T> of(Supplier<T> supplier) {
        return new Lazy<>(supplier);
    }

    @Override
    public T get() {
        T v = value;
        if (v == null) {
            synchronized (this) {
                v = value;
                if (v == null) {
                    v = value = supplier.get();
                }
            }
        }
        return v;
    }
}
