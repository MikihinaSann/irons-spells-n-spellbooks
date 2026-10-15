package com.somake.somakespells.effect.blood;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.phys.Vec3;

public class CrimsonRootEffect extends MagicMobEffect {
   public CrimsonRootEffect(MobEffectCategory category, int color) {
      super(category, color);
      this.addAttributeModifier(
         Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("somakespells", "crimson_root_movement"), -1.0, Operation.ADD_MULTIPLIED_TOTAL
      );
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      Vec3 movement = entity.getDeltaMovement();
      entity.setDeltaMovement(0.0, Math.min(movement.y, 0.0), 0.0);
      entity.setJumping(false);
      entity.setSprinting(false);
      if (entity instanceof Mob mob) {
         mob.getNavigation().stop();
      }

      return true;
   }
}
