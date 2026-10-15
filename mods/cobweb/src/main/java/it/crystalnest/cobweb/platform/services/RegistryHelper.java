package it.crystalnest.cobweb.platform.services;

import it.crystalnest.cobweb.api.pack.fixed.StaticResourcePack;
import it.crystalnest.cobweb.api.registry.CobwebRegister;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;

public abstract class RegistryHelper<T extends CobwebRegister<?>> {
   protected final Map<String, Map<ResourceLocation, T>> registries = new HashMap<>();

   public <R> CobwebRegister<R> of(Registry<R> registry, String namespace) {
      return this.of(registry.key(), namespace);
   }

   public abstract <R> CobwebRegister<R> of(ResourceKey<? extends Registry<R>> var1, String var2);

   public abstract void registerDynamicResourcePack(PackType var1, Supplier<Pack> var2);

   public abstract void registerStaticResourcePack(StaticResourcePack var1);
}
