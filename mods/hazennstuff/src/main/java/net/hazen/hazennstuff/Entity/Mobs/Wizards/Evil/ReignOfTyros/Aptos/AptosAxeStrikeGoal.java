package net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.ReignOfTyros.Aptos;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.goals.WarlockAttackGoal;
import io.redspace.ironsspellbooks.network.SyncAnimationPacket;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class AptosAxeStrikeGoal extends WarlockAttackGoal {
   final AptosEntity samurai;
   int meleeAnimTimer = -1;
   public AptosEntity.AttackType currentAttack;
   public AptosEntity.AttackType nextAttack;
   public AptosEntity.AttackType queueCombo;
   private boolean hasLunged;
   private boolean hasHitLunge;
   private Vec3 oldLungePos;

   public AptosAxeStrikeGoal(AptosEntity abstractSpellCastingMob, double pSpeedModifier, int minAttackInterval, int maxAttackInterval) {
      super(abstractSpellCastingMob, pSpeedModifier, minAttackInterval, maxAttackInterval);
      this.samurai = abstractSpellCastingMob;
      this.nextAttack = this.randomizeNextAttack(0.0F);
      this.wantsToMelee = true;
   }

   protected float meleeBias() {
      return 1.0F;
   }

   public boolean isActing() {
      return super.isActing() || this.meleeAnimTimer > 0;
   }

   protected void handleAttackLogic(double distanceSquared) {
      float meleeRange = this.meleeRange();
      float distance = Mth.sqrt((float)distanceSquared);
      this.mob.getLookControl().setLookAt(this.target);
      if (this.meleeAnimTimer > 0) {
         this.forceFaceTarget();
         this.meleeAnimTimer--;
         if (this.currentAttack.data.isHitFrame(this.meleeAnimTimer - 4)) {
            if (this.currentAttack != AptosEntity.AttackType.LUNGE) {
               this.playSwingSound();
            }
         } else if (this.currentAttack.data.isHitFrame(this.meleeAnimTimer)) {
            if (this.currentAttack != AptosEntity.AttackType.LUNGE) {
               Vec3 lunge = this.target.position().subtract(this.mob.position()).normalize().scale(0.55F);
               this.mob.push(lunge.x, lunge.y, lunge.z);
               if (distance <= meleeRange && Utils.hasLineOfSight(this.mob.level(), this.mob, this.target, true)) {
                  boolean flag = this.mob.doHurtTarget(this.target);
                  this.target.invulnerableTime = 0;
                  if (flag) {
                     this.playImpactSound();
                     if (this.currentAttack.data.isSingleHit() && (this.mob.getRandom().nextFloat() < 0.75F || this.target.isBlocking())) {
                        this.queueCombo = this.randomizeNextAttack(0.0F);
                     }
                  }
               }
            } else {
               if (!this.hasLunged) {
                  Vec3 lunge = this.target.position().subtract(this.mob.position()).normalize().multiply(2.4, 0.5, 2.4).add(0.0, 0.15, 0.0);
                  this.mob.push(lunge.x, lunge.y, lunge.z);
                  this.oldLungePos = this.mob.position();
                  this.mob.getNavigation().stop();
                  this.hasLunged = true;
                  this.playSwingSound();
               }

               if (!this.hasHitLunge && distance <= meleeRange * 0.45F) {
                  if (this.mob.doHurtTarget(this.target)) {
                     this.playImpactSound();
                  }

                  Vec3 knockback = this.oldLungePos.subtract(this.target.position());
                  this.target.knockback(1.0, knockback.x, knockback.z);
                  this.hasHitLunge = true;
               }
            }
         }
      } else if (this.queueCombo != null && this.target != null && !this.target.isDeadOrDying()) {
         this.nextAttack = this.queueCombo;
         this.queueCombo = null;
         this.doMeleeAction();
      } else if (this.meleeAnimTimer == 0) {
         this.nextAttack = this.randomizeNextAttack(distance);
         this.resetMeleeAttackInterval(distanceSquared);
         this.meleeAnimTimer = -1;
      } else if (distance < meleeRange * (this.nextAttack == AptosEntity.AttackType.LUNGE ? 3 : 1)) {
         if (this.hasLineOfSight && --this.meleeAttackDelay == 0) {
            this.doMeleeAction();
         } else if (this.meleeAttackDelay < 0) {
            this.resetMeleeAttackInterval(distanceSquared);
         }
      } else if (--this.meleeAttackDelay < 0) {
         this.resetMeleeAttackInterval(distanceSquared);
         this.nextAttack = this.randomizeNextAttack(distance);
      }
   }

   private AptosEntity.AttackType randomizeNextAttack(float distance) {
      float meleeRange = this.meleeRange();
      int i;
      if (distance < meleeRange * 1.5F) {
         i = AptosEntity.AttackType.values().length - 1;
      } else {
         if (this.mob.getRandom().nextFloat() < 0.25F && distance > meleeRange * 2.5F) {
            return AptosEntity.AttackType.LUNGE;
         }

         i = AptosEntity.AttackType.values().length;
      }

      return AptosEntity.AttackType.values()[this.mob.getRandom().nextInt(i)];
   }

   private void forceFaceTarget() {
      if (!this.hasLunged) {
         double d0 = this.target.getX() - this.mob.getX();
         double d1 = this.target.getZ() - this.mob.getZ();
         float yRot = (float)(Mth.atan2(d1, d0) * 180.0F / (float)Math.PI) - 90.0F;
         this.mob.setYBodyRot(yRot);
         this.mob.setYHeadRot(yRot);
         this.mob.setYRot(yRot);
      }
   }

   protected void doMeleeAction() {
      this.currentAttack = this.nextAttack;
      if (this.currentAttack != null) {
         this.mob.swing(InteractionHand.MAIN_HAND);
         this.meleeAnimTimer = this.currentAttack.data.lengthInTicks;
         this.hasLunged = false;
         this.hasHitLunge = false;
         PacketDistributor.sendToPlayersTrackingEntity(
            this.samurai, new SyncAnimationPacket(this.currentAttack.toString(), this.samurai), new CustomPacketPayload[0]
         );
      }
   }

   protected void doMovement(double distanceSquared) {
      float meleeRange = this.meleeRange();
      if (this.target.isDeadOrDying()) {
         this.mob.getNavigation().stop();
      } else if (distanceSquared > meleeRange * meleeRange) {
         this.mob.getNavigation().moveTo(this.target, this.speedModifier * 1.3F);
      }
   }

   public boolean canContinueToUse() {
      return super.canContinueToUse() || this.meleeAnimTimer > 0;
   }

   public void stop() {
      super.stop();
      this.meleeAnimTimer = -1;
      this.queueCombo = null;
   }

   public void playImpactSound() {
      this.mob.playSound((SoundEvent)SoundRegistry.KEEPER_SWING.get(), 1.0F, Mth.randomBetweenInclusive(this.mob.getRandom(), 9, 13) * 0.1F);
   }

   public void playSwingSound() {
      this.mob.playSound((SoundEvent)SoundRegistry.KEEPER_SWING.get(), 1.0F, Mth.randomBetweenInclusive(this.mob.getRandom(), 9, 13) * 0.1F);
   }
}
