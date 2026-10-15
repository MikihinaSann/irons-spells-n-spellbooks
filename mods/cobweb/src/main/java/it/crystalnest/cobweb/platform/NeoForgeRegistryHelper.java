package it.crystalnest.cobweb.platform;

import it.crystalnest.cobweb.api.pack.fixed.StaticResourcePack;
import it.crystalnest.cobweb.api.registry.CobwebEntry;
import it.crystalnest.cobweb.api.registry.CobwebRegister;
import it.crystalnest.cobweb.platform.services.RegistryHelper;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class NeoForgeRegistryHelper extends RegistryHelper<NeoForgeRegistryHelper.Register<?>> {
   private IEventBus bus = null;

   private IEventBus bus() {
      return this.bus != null ? this.bus : net.neoforged.neoforge.internal.fabric.NeoForgeCompat.MOD_BUS;
   }

   public <R> NeoForgeRegistryHelper.Register<R> of(ResourceKey<? extends Registry<R>> registryKey, String namespace) {
      return (NeoForgeRegistryHelper.Register<R>)this.registries
         .computeIfAbsent(namespace, key -> new HashMap<>())
         .computeIfAbsent(registryKey.location(), key -> {
            NeoForgeRegistryHelper.Register<R> register = NeoForgeRegistryHelper.Register.create(registryKey, namespace);
            register.register(this.bus());
            return register;
         });
   }

   @Override
   public void registerDynamicResourcePack(PackType type, Supplier<Pack> supplier) {
      this.bus().addListener((AddPackFindersEvent event) -> {
         if (event.getPackType() == type) {
            event.addRepositorySource(consumer -> consumer.accept(supplier.get()));
         }
      });
   }

   @Override
   public void registerStaticResourcePack(StaticResourcePack pack) {
      this.bus().addListener((AddPackFindersEvent event) -> event.addPackFinders(pack.location(), pack.type(), pack.name(), pack.source(), pack.alwaysActive(), pack.position()));
   }

   private <R, T extends NeoForgeRegistryHelper.Register<R>> T of(
      Function<String, T> constructor, ResourceKey<? extends Registry<R>> registryKey, String namespace
   ) {
      return (T)this.registries.computeIfAbsent(namespace, key -> new HashMap<>()).computeIfAbsent(registryKey.location(), key -> {
         T register = constructor.apply(namespace);
         register.register(this.bus());
         return register;
      });
   }

   public void register(IEventBus bus) {
      this.bus = bus;
   }

   public static final class Register<R> implements CobwebRegister<R> {
      private final DeferredRegister<R> register;

      private Register(ResourceKey<? extends Registry<R>> registryKey, String namespace) {
         this.register = DeferredRegister.create(registryKey, namespace);
      }

      public static <R> NeoForgeRegistryHelper.Register<R> create(ResourceKey<? extends Registry<R>> registryKey, String namespace) {
         return new NeoForgeRegistryHelper.Register<>(registryKey, namespace);
      }

      public void register(IEventBus bus) {
         this.register.register(bus);
      }

      @Override
      public <T extends R> CobwebEntry<T> register(String name, Supplier<? extends T> supplier) {
         return new CobwebEntry<>((Holder<T>)(Holder<R>)this.register.register(name, supplier));
      }
   }
}
