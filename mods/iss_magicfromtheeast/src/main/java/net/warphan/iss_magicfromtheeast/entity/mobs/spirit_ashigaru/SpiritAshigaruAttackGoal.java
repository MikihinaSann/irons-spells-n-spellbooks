package net.warphan.iss_magicfromtheeast.entity.mobs.spirit_ashigaru;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.goals.WarlockAttackGoal;
import io.redspace.ironsspellbooks.network.SyncAnimationPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.warphan.iss_magicfromtheeast.entity.spirit_bullet.SpiritBulletProjectile;
import net.warphan.iss_magicfromtheeast.registries.MFTESoundRegistries;

public class SpiritAshigaruAttackGoal extends WarlockAttackGoal {
   final SpiritAshigaruEntity ashigaru;
   int meleeAnimTimer = -1;
   public SpiritAshigaruEntity.AttackType currentAttack;
   public SpiritAshigaruEntity.AttackType nextAttack;

   public SpiritAshigaruAttackGoal(SpiritAshigaruEntity ashigaru, double pSpeedModifier, int minAttackInterval, int maxAttackInterval) {
      super(ashigaru, pSpeedModifier, minAttackInterval, maxAttackInterval);
      this.ashigaru = ashigaru;
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
         if (this.mob instanceof SpiritAshigaruEntity spiritAshigaru && this.currentAttack.data.isHitFrame(this.meleeAnimTimer)) {
            if (spiritAshigaru.isMeleeType()) {
               this.currentAttack = SpiritAshigaruEntity.AttackType.MELEE;
               Vec3 push = this.target.position().subtract(this.mob.position()).normalize().scale(0.1F);
               this.mob.push(push.x, push.y, push.z);
               if (distance <= meleeRange && Utils.hasLineOfSight(this.mob.level, this.mob, this.target, true)) {
                  boolean flag = this.mob.doHurtTarget(this.target);
                  this.target.invulnerableTime = 0;
                  if (flag) {
                     this.playPierceSound();
                  }
               }
            } else if (spiritAshigaru.isRangeType()) {
               this.currentAttack = SpiritAshigaruEntity.AttackType.RANGE;
               SpiritBulletProjectile spiritBullet = new SpiritBulletProjectile(this.ashigaru.level, this.ashigaru);
               spiritBullet.setPos(
                  this.ashigaru
                     .position()
                     .add(0.0, this.ashigaru.getEyeHeight() - spiritBullet.getBoundingBox().getYsize() * 0.5, 0.0)
                     .add(this.ashigaru.getForward())
               );
               spiritBullet.shoot(this.ashigaru.getLookAngle());
               spiritBullet.setDamage((float)this.ashigaru.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5F);
               this.ashigaru.level.addFreshEntity(spiritBullet);
               this.playShotSound();
            }
         }
      } else if (this.target != null && !this.target.isDeadOrDying()) {
         this.nextAttack = this.continueAttacking(distance);
         this.doMeleeAction();
      } else if (this.meleeAnimTimer == 0) {
         this.nextAttack = this.continueAttacking(distance);
         this.resetMeleeAttackInterval(distanceSquared);
         this.meleeAnimTimer = -1;
      } else if (distance < meleeRange) {
         if (this.hasLineOfSight && --this.meleeAttackDelay == 0) {
            this.doMeleeAction();
         } else if (this.meleeAttackDelay < 0) {
            this.resetMeleeAttackInterval(distanceSquared);
         }
      } else if (--this.meleeAttackDelay < 0) {
         this.resetMeleeAttackInterval(distanceSquared);
         this.nextAttack = this.continueAttacking(distance);
      }
   }

   private SpiritAshigaruEntity.AttackType continueAttacking(float distance) {
      float meleeRange = this.meleeRange();
      return this.ashigaru.isRangeType() && distance < meleeRange * 1.2F ? SpiritAshigaruEntity.AttackType.RANGE : SpiritAshigaruEntity.AttackType.MELEE;
   }

   private void forceFaceTarget() {
      double d0 = this.target.getX() - this.mob.getX();
      double d1 = this.target.getZ() - this.mob.getZ();
      float yRot = (float)(Mth.atan2(d1, d0) * 180.0F / (float)Math.PI) - 90.0F;
      this.mob.setYBodyRot(yRot);
      this.mob.setYHeadRot(yRot);
      this.mob.setYRot(yRot);
   }

   protected void doMeleeAction() {
      this.currentAttack = this.nextAttack;
      if (this.currentAttack != null) {
         this.mob.swing(InteractionHand.MAIN_HAND);
         this.meleeAnimTimer = this.currentAttack.data.lengthInTicks;
         PacketDistributor.sendToPlayersTrackingEntity(
            this.ashigaru, new SyncAnimationPacket(this.currentAttack.toString(), this.ashigaru), new CustomPacketPayload[0]
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
   }

   public void playPierceSound() {
      this.mob.playSound((SoundEvent)MFTESoundRegistries.ASHIGARU_SPEAR.get(), 1.0F, Mth.randomBetweenInclusive(this.mob.getRandom(), 7, 11) * 0.1F);
   }

   public void playShotSound() {
      this.mob.playSound((SoundEvent)MFTESoundRegistries.ASHIGARU_GUNSHOT.get(), 1.0F, Mth.randomBetweenInclusive(this.mob.getRandom(), 9, 13) * 0.1F);
   }
}
