package com.somake.somakespells.effect.nature;

import dev.shadowsoffire.apothic_attributes.api.ALObjects.Attributes;
import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class OvergrownEffect extends MagicMobEffect implements ISyncedMobEffect {
   public static final float ARROW_VELOCITY_PER_LEVEL = 0.1F;
   public static final float ARROW_DAMAGE_PER_LEVEL = 0.3F;
   public static final float NATURE_SPELL_POWER_PER_LEVEL = 0.05F;

   public OvergrownEffect(MobEffectCategory mobEffectCategory, int color) {
      super(mobEffectCategory, color);
      this.addAttributeModifier(
         Attributes.ARROW_DAMAGE, ResourceLocation.fromNamespaceAndPath("somakespells", "effect.overgrown.arrow_damage"), 0.3F, Operation.ADD_VALUE
      );
      this.addAttributeModifier(
         Attributes.ARROW_VELOCITY, ResourceLocation.fromNamespaceAndPath("somakespells", "effect.overgrown.arrow_velocity"), 0.1F, Operation.ADD_VALUE
      );
   }
}
