package com.somake.somakespells.effect.holy;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.phys.Vec3;

public class SacredPhoenixExhaustionEffect extends MagicMobEffect {
   public SacredPhoenixExhaustionEffect(MobEffectCategory category, int color) {
      super(category, color);
      this.addAttributeModifier(
         Attributes.MOVEMENT_SPEED,
         ResourceLocation.fromNamespaceAndPath("somakespells", "sacred_phoenix_exhaustion_movement"),
         -1.0,
         Operation.ADD_MULTIPLIED_TOTAL
      );
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      entity.setDeltaMovement(Vec3.ZERO);
      entity.setJumping(false);
      entity.setSprinting(false);
      entity.stopUsingItem();
      if (entity instanceof Mob mob) {
         mob.getNavigation().stop();
         mob.setTarget(null);
      }

      return true;
   }
}
