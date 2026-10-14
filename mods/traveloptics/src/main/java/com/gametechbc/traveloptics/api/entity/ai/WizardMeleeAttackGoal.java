package com.gametechbc.traveloptics.api.entity.ai;

import java.util.EnumSet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class WizardMeleeAttackGoal extends Goal {
   protected final PathfinderMob mob;
   private final double speedModifier;
   private final double meleeRangeSqr;
   private int ticksUntilNextAttack;
   private final int cooldownTicks;

   public WizardMeleeAttackGoal(PathfinderMob mob, double speedModifier, double meleeRange, int cooldownTicks) {
      super();
      this.mob = mob;
      this.speedModifier = speedModifier;
      this.meleeRangeSqr = meleeRange * meleeRange;
      this.cooldownTicks = cooldownTicks;
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean canUse() {
      LivingEntity target = this.mob.getTarget();
      if (target != null && target.isAlive()) {
         double distanceToTargetSqr = this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
         return distanceToTargetSqr <= this.meleeRangeSqr && this.ticksUntilNextAttack <= 0;
      } else {
         return false;
      }
   }

   public boolean canContinueToUse() {
      return false;
   }

   public void start() {
      LivingEntity target = this.mob.getTarget();
      if (target != null) {
         this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
         this.mob.getNavigation().moveTo(target, this.speedModifier);
         this.mob.swing(InteractionHand.MAIN_HAND, true);
         this.mob.doHurtTarget(target);
         this.resetAttackCooldown();
      }
   }

   public void stop() {
      this.mob.getNavigation().stop();
   }

   public void tick() {
      this.ticksUntilNextAttack--;
   }

   protected void resetAttackCooldown() {
      this.ticksUntilNextAttack = this.cooldownTicks;
   }
}
