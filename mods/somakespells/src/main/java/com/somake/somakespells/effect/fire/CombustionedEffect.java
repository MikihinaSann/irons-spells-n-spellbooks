package com.somake.somakespells.effect.fire;

import com.somake.somakespells.registries.ModAttributes;
import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class CombustionedEffect extends MagicMobEffect implements ISyncedMobEffect {
   public static final float FIRE_SPELL_POWER_PER_LEVEL = 0.05F;
   public static final float FIRE_DAMAGE_PER_LEVEL = 5.0F;

   public CombustionedEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
      this.addAttributeModifier(
         ModAttributes.FIRE_DAMAGE, ResourceLocation.fromNamespaceAndPath("somakespells", "effect.combustioned.fire_damage"), 5.0, Operation.ADD_VALUE
      );
   }
}
