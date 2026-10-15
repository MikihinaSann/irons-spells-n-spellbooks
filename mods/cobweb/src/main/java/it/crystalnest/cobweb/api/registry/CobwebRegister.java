package it.crystalnest.cobweb.api.registry;

import java.util.function.Supplier;

public interface CobwebRegister<R> {
   <T extends R> CobwebEntry<T> register(String var1, Supplier<? extends T> var2);
}
