package com.gametechbc.traveloptics.effects.casting;

import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.neoforged.neoforge.common.EffectCure;

public class CastingEffect extends MobEffect {
   public CastingEffect() {
      super(MobEffectCategory.NEUTRAL, 0);
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.parse("debuff"), -0.2, Operation.ADD_MULTIPLIED_TOTAL);
   }

   public boolean applyEffectTick(LivingEntity LivingEntityIn, int amplifier) {
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
   }
}
