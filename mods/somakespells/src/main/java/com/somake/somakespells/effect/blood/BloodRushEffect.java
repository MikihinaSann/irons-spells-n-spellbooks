package com.somake.somakespells.effect.blood;

import dev.shadowsoffire.apothic_attributes.api.ALObjects.Attributes;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class BloodRushEffect extends MagicMobEffect implements ISyncedMobEffect {
   public static final float BLOOD_SPELL_POWER_PER_LEVEL = 0.05F;
   public static final float LIFE_STEAL_PER_LEVEL = 0.025F;

   public BloodRushEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
      this.addAttributeModifier(
         Attributes.LIFE_STEAL, ResourceLocation.fromNamespaceAndPath("somakespells", "effect.blood_rush.life_steal"), 0.025F, Operation.ADD_VALUE
      );
      this.addAttributeModifier(
         AttributeRegistry.HOLY_MAGIC_RESIST,
         ResourceLocation.fromNamespaceAndPath("somakespells", "effect.blood_rush.holy_resist"),
         -0.05,
         Operation.ADD_VALUE
      );
   }
}
