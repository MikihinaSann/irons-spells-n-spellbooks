package it.crystalnest.prometheus.api.enchantment;

import com.mojang.serialization.MapCodec;
import it.crystalnest.cobweb.api.registry.CobwebRegister;
import it.crystalnest.cobweb.api.registry.CobwebRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;

public final class EnchantmentRegistry {
   private static final CobwebRegister<MapCodec<? extends EnchantmentEntityEffect>> ENTITY_ENCHANTMENT_EFFECTS = CobwebRegistry.of(
      Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, "prometheus"
   );

   private EnchantmentRegistry() {
   }

   public static void register() {
   }

   static {
      ENTITY_ENCHANTMENT_EFFECTS.register("ignite", () -> Ignite.CODEC);
   }
}
