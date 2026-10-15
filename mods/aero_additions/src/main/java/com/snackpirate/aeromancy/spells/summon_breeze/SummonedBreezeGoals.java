package com.snackpirate.aeromancy.spells.summon_breeze;

import com.snackpirate.aeromancy.Aeromancy;
import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.monster.RangedAttackMob;

public class SummonedBreezeGoals {
   static class LongJump<T extends Mob> extends Goal {
      private final T mob;
      private int attackTime = -1;

      public LongJump(T mob) {
         this.mob = mob;
      }

      public boolean canUse() {
         return this.mob.target != null && this.mob.target.distanceToSqr(this.mob) <= 9.0;
      }
   }

   static class ShootWindCharge<T extends Mob & RangedAttackMob> extends Goal {
      private final T mob;
      private final double speedModifier;
      private int attackIntervalMin;
      private final float attackRadiusSqr;
      private int attackTime = -1;
      private int seeTime;
      private boolean strafingClockwise;
      private boolean strafingBackwards;
      private int strafingTime = -1;

      public ShootWindCharge(T mob, double speedMod, int attackIntMin, float atkRadius) {
         this.mob = mob;
         this.speedModifier = speedMod;
         this.attackIntervalMin = attackIntMin;
         this.attackRadiusSqr = atkRadius * atkRadius;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public void setMinAttackInterval(int attackCooldown) {
         this.attackIntervalMin = attackCooldown;
      }

      public boolean canUse() {
         return this.mob.getTarget() != null && !this.mob.getTarget().isDeadOrDying();
      }

      public boolean canContinueToUse() {
         return this.canUse() || !this.mob.getNavigation().isDone();
      }

      public void start() {
         super.start();
         this.mob.setAggressive(true);
      }

      public void stop() {
         super.stop();
         this.mob.setAggressive(false);
         this.seeTime = 0;
         this.attackTime = -1;
         this.mob.stopUsingItem();
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         LivingEntity livingentity = this.mob.getTarget();
         if (livingentity != null) {
            if (livingentity.isDeadOrDying()) {
               this.mob.target = null;
               this.stop();
               return;
            }

            double d0 = this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
            boolean flag = this.mob.getSensing().hasLineOfSight(livingentity);
            boolean flag1 = this.seeTime > 0;
            if (flag != flag1) {
               this.seeTime = 0;
            }

            if (flag) {
               this.seeTime++;
            } else {
               this.seeTime--;
            }

            if (!(d0 > this.attackRadiusSqr) && this.seeTime >= 20) {
               this.mob.getNavigation().stop();
               this.strafingTime++;
            } else {
               this.mob.getNavigation().moveTo(livingentity, this.speedModifier);
               this.strafingTime = -1;
            }

            if (this.strafingTime >= 20) {
               if (this.mob.getRandom().nextFloat() < 0.3) {
                  this.strafingClockwise = !this.strafingClockwise;
               }

               if (this.mob.getRandom().nextFloat() < 0.3) {
                  this.strafingBackwards = !this.strafingBackwards;
               }

               this.strafingTime = 0;
            }

            if (this.strafingTime > -1) {
               if (d0 > this.attackRadiusSqr * 0.75F) {
                  this.strafingBackwards = false;
               } else if (d0 < this.attackRadiusSqr * 0.25F) {
                  this.strafingBackwards = true;
               }

               this.mob.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
               if (this.mob.getControlledVehicle() instanceof Mob mob) {
                  mob.lookAt(livingentity, 30.0F, 30.0F);
               }

               this.mob.lookAt(livingentity, 30.0F, 30.0F);
            } else {
               this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
            }

            Aeromancy.LOGGER.info("atk time {}", this.attackTime);
            if (--this.attackTime <= 0) {
               this.mob.performRangedAttack(livingentity, 1.0F);
               this.attackTime = this.attackIntervalMin;
            }
         }
      }
   }
}
