package com.gametechbc.traveloptics.effects.frozen_sight;

import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.neoforged.neoforge.common.EffectCure;

public class FrozenSightEffect extends MobEffect {
   public FrozenSightEffect() {
      super(MobEffectCategory.HARMFUL, 5926017);
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.parse("debuff"), -1.0, Operation.ADD_MULTIPLIED_TOTAL);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return true;
   }

   public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
   }
}
