package net.warphan.iss_magicfromtheeast.entity.mobs.jade_executioner;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.goals.WarlockAttackGoal;
import io.redspace.ironsspellbooks.network.SyncAnimationPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.warphan.iss_magicfromtheeast.registries.MFTESoundRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESpellRegistries;

public class JadeExecutionerAttackGoal extends WarlockAttackGoal {
   final JadeExecutionerEntity jadeExecutioner;
   int meleeAnimTimer = -1;
   public JadeExecutionerEntity.AttackAnim currentAttack;
   public JadeExecutionerEntity.AttackAnim nextAttack;
   public JadeExecutionerEntity.AttackAnim queueCombo;

   public JadeExecutionerAttackGoal(JadeExecutionerEntity executionerEntity, double speedModifier, int minAttackInterval, int maxAttackInterval) {
      super(executionerEntity, speedModifier, minAttackInterval, maxAttackInterval);
      this.jadeExecutioner = executionerEntity;
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
         if (this.currentAttack.data.isHitFrame(this.meleeAnimTimer)) {
            Vec3 lunge = this.target.position().subtract(this.mob.position()).normalize().scale(-0.25);
            this.mob.push(lunge.x, lunge.y, lunge.z);
            float radius = 0.8F;
            float damage = (float)this.mob.getAttributeValue(Attributes.ATTACK_DAMAGE);
            Vec3 forward = this.mob.getForward().multiply(1.0, 0.0, 1.0).normalize();
            Vec3 start = this.mob.getEyePosition().subtract(0.0, 3.0, 0.0).add(forward.scale(1.5));
            if (distance <= meleeRange && Utils.hasLineOfSight(this.mob.level, this.mob, this.target, true)) {
               if (this.currentAttack == JadeExecutionerEntity.AttackAnim.AXE_DOWN) {
                  this.playChopSound();

                  for (int i = 0; i < 3; i++) {
                     Vec3 chopLocation = start.add(forward.scale(i));

                     for (Entity targetEntity : this.mob.level.getEntities(this.mob, AABB.ofSize(chopLocation, radius, radius * 3.0F, radius))) {
                        if (targetEntity.isAlive()
                           && targetEntity.isPickable()
                           && Utils.hasLineOfSight(this.mob.level, chopLocation.add(0.0, 1.0, 0.0), targetEntity.getBoundingBox().getCenter(), true)) {
                           DamageSources.applyDamage(
                              targetEntity, (float)(damage * 1.5), MFTESpellRegistries.PUNISHING_HEAVEN_SPELL.get().getDamageSource(this.mob)
                           );
                        }
                     }
                  }
               } else if (this.currentAttack == JadeExecutionerEntity.AttackAnim.AXE_LEFT) {
                  this.playSweepSound();
                  Vec3 leftSweepPos = start.add(forward.scale(0.5));

                  for (Entity targetEntityx : this.mob.level.getEntities(this.mob, AABB.ofSize(leftSweepPos, radius * 3.5, radius * 3.0F, radius * 3.5))) {
                     if (targetEntityx.isAlive()
                        && targetEntityx.isPickable()
                        && Utils.hasLineOfSight(this.mob.level, leftSweepPos.add(0.0, 0.5, 0.0), targetEntityx.getBoundingBox().getCenter(), true)) {
                        DamageSources.applyDamage(targetEntityx, damage, MFTESpellRegistries.PUNISHING_HEAVEN_SPELL.get().getDamageSource(this.mob));
                        if (this.currentAttack.data.isSingleHit() && (this.mob.getRandom().nextFloat() < 0.75F || this.target.isBlocking())) {
                           this.queueCombo = this.randomizeNextAttack(0.0F);
                        }
                     }
                  }
               } else if (this.currentAttack == JadeExecutionerEntity.AttackAnim.AXE_RIGHT) {
                  this.playSweepSound();
                  Vec3 rightSweepPos = this.mob.position().add(0.0, 0.5, 0.0).add(forward.scale(2.0));

                  for (Entity targetEntityxx : this.mob.level.getEntities(this.mob, AABB.ofSize(rightSweepPos, radius * 3.5, radius * 3.0F, radius * 3.5))) {
                     DamageSources.applyDamage(targetEntityxx, damage, MFTESpellRegistries.PUNISHING_HEAVEN_SPELL.get().getDamageSource(this.mob));
                     if (this.currentAttack.data.isSingleHit() && (this.mob.getRandom().nextFloat() < 0.75F || this.target.isBlocking())) {
                        this.queueCombo = this.randomizeNextAttack(0.0F);
                     }
                  }
               } else if (this.currentAttack == JadeExecutionerEntity.AttackAnim.BITE) {
                  boolean flag = this.mob.doHurtTarget(this.target);
                  if (flag) {
                     this.playBiteSound();
                     this.mob.heal((float)(damage * 0.75));
                     if (this.currentAttack.data.isSingleHit() && (this.mob.getRandom().nextFloat() < 0.75F || this.target.isBlocking())) {
                        this.queueCombo = this.randomizeNextAttack(0.0F);
                     }
                  }
               } else if (this.currentAttack == JadeExecutionerEntity.AttackAnim.SHIELD_BASH) {
                  this.playBashSound();
                  Vec3 bashPos = start.add(forward.scale(0.5));

                  for (Entity targetEntityxxx : this.mob.level.getEntities(this.mob, AABB.ofSize(bashPos, radius * 2.0, radius * 3.0F, radius * 2.0))) {
                     if (targetEntityxxx.isAlive()
                        && targetEntityxxx.isPickable()
                        && Utils.hasLineOfSight(this.mob.level, bashPos.add(0.0, 0.5, 0.0), targetEntityxxx.getBoundingBox().getCenter(), true)) {
                        DamageSources.applyDamage(targetEntityxxx, damage / 2.0F, MFTESpellRegistries.PUNISHING_HEAVEN_SPELL.get().getDamageSource(this.mob));
                        Vec3 knockBack = this.mob.position().subtract(targetEntityxxx.position());
                        this.target.knockback(2.0, knockBack.x, knockBack.z);
                        targetEntityxxx.hurtMarked = true;
                     }
                  }
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
      } else if (distance < meleeRange) {
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

   private JadeExecutionerEntity.AttackAnim randomizeNextAttack(float distance) {
      float meleeRange = this.meleeRange();
      int i;
      if (distance < meleeRange * 1.2F) {
         i = JadeExecutionerEntity.AttackAnim.values().length - 1;
      } else {
         i = JadeExecutionerEntity.AttackAnim.values().length;
      }

      return JadeExecutionerEntity.AttackAnim.values()[this.mob.getRandom().nextInt(i)];
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
         this.meleeAnimTimer = this.currentAttack.data.lengthInTicks;
         PacketDistributor.sendToPlayersTrackingEntity(
            this.jadeExecutioner, new SyncAnimationPacket(this.currentAttack.toString(), this.jadeExecutioner), new CustomPacketPayload[0]
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

   public void playSweepSound() {
      this.mob.playSound((SoundEvent)MFTESoundRegistries.AXE_SWEEP.get(), 1.0F, Mth.randomBetweenInclusive(this.mob.getRandom(), 9, 13) * 0.1F);
   }

   public void playChopSound() {
      this.mob.playSound((SoundEvent)MFTESoundRegistries.AXE_CHOP.get(), 1.0F, Mth.randomBetweenInclusive(this.mob.getRandom(), 9, 13) * 0.1F);
   }

   public void playBashSound() {
      this.mob.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, Mth.randomBetweenInclusive(this.mob.getRandom(), 9, 13) * 0.1F);
   }

   public void playBiteSound() {
      this.mob.playSound(SoundEvents.EVOKER_FANGS_ATTACK, 1.0F, Mth.randomBetweenInclusive(this.mob.getRandom(), 9, 13) * 0.1F);
   }
}
