package com.somake.somakespells.effect.ice;

import com.somake.somakespells.registries.ModAttributes;
import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class PermafrostEffect extends MagicMobEffect implements ISyncedMobEffect {
   public static final float ICE_SPELL_POWER_PER_LEVEL = 0.05F;
   public static final float COLD_DAMAGE_PER_LEVEL = 5.0F;

   public PermafrostEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
      this.addAttributeModifier(
         ModAttributes.ICE_DAMAGE, ResourceLocation.fromNamespaceAndPath("somakespells", "effect.permafrost.cold_damage"), 5.0, Operation.ADD_VALUE
      );
   }
}
