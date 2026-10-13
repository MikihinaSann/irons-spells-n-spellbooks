package com.grandsigil.effect;

import com.grandsigil.GrandSigil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class BoundEffect extends MobEffect {
   public static final int COLOUR = 13145386;

   public BoundEffect() {
      super(MobEffectCategory.HARMFUL, 13145386);
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, GrandSigil.id("bound_hold"), -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.FLYING_SPEED, GrandSigil.id("bound_hover"), -1.0, Operation.ADD_MULTIPLIED_TOTAL);
   }

   @Override
   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   @Override
   public boolean applyEffectTick(LivingEntity target, int amplifier) {
      if (!target.level().isClientSide) {
         Restraint.pin(target);
      }

      return true;
   }
}
