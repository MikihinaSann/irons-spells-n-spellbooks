package com.somake.somakespells.effect.aqua;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class WetEffect extends MagicMobEffect implements ISyncedMobEffect {
   public WetEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
      this.addAttributeModifier(
         AttributeRegistry.LIGHTNING_MAGIC_RESIST,
         ResourceLocation.fromNamespaceAndPath("somakespells", "effect.wet.lightning_resist"),
         -0.05,
         Operation.ADD_VALUE
      );
      this.addAttributeModifier(
         AttributeRegistry.ICE_MAGIC_RESIST, ResourceLocation.fromNamespaceAndPath("somakespells", "effect.wet.ice_resist"), -0.025, Operation.ADD_VALUE
      );
      this.addAttributeModifier(
         AttributeRegistry.FIRE_MAGIC_RESIST, ResourceLocation.fromNamespaceAndPath("somakespells", "effect.wet.fire_resist"), 0.05, Operation.ADD_VALUE
      );
   }
}
