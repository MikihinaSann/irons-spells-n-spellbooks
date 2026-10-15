package it.crystalnest.cobweb.api.registry;

import it.crystalnest.cobweb.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;

public final class CobwebRegistry {
   private CobwebRegistry() {
   }

   public static <R> CobwebRegister<R> of(Registry<R> registry, String namespace) {
      return Services.REGISTRY.of(registry, namespace);
   }

   public static <R> CobwebRegister<R> of(ResourceKey<? extends Registry<R>> registryKey, String namespace) {
      return Services.REGISTRY.of(registryKey, namespace);
   }

   public static CobwebRegister<Item> ofItems(String namespace) {
      return of(Registries.ITEM, namespace);
   }

   public static CobwebRegister<Block> ofBlocks(String namespace) {
      return of(Registries.BLOCK, namespace);
   }

   public static CobwebRegister<EntityType<?>> ofEntityTypes(String namespace) {
      return of(Registries.ENTITY_TYPE, namespace);
   }

   public static CobwebRegister<MobEffect> ofMobEffects(String namespace) {
      return of(Registries.MOB_EFFECT, namespace);
   }

   public static CobwebRegister<Potion> ofPotions(String namespace) {
      return of(Registries.POTION, namespace);
   }

   public static CobwebRegister<CreativeModeTab> ofCreativeModeTabs(String namespace) {
      return of(Registries.CREATIVE_MODE_TAB, namespace);
   }
}
