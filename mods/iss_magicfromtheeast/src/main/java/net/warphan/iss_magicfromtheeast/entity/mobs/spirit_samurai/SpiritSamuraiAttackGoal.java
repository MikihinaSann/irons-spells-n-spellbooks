package net.warphan.iss_magicfromtheeast.entity.mobs.spirit_samurai;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.goals.WarlockAttackGoal;
import io.redspace.ironsspellbooks.network.SyncAnimationPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.warphan.iss_magicfromtheeast.registries.MFTESoundRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESpellRegistries;

public class SpiritSamuraiAttackGoal extends WarlockAttackGoal {
   final SpiritSamuraiEntity samurai;
   int meleeAnimTimer = -1;
   public SpiritSamuraiEntity.AttackAnim currentAttack;
   public SpiritSamuraiEntity.AttackAnim nextAttack;
   public SpiritSamuraiEntity.AttackAnim queueCombo;
   private boolean hasUsedTechnique;
   private boolean hasHitTechnique;
   private Vec3 oldTechniquePos;

   public SpiritSamuraiAttackGoal(SpiritSamuraiEntity abstractSpellCastingMob, double pSpeedModifier, int minAttackInterval, int maxAttackInterval) {
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
            if (this.currentAttack != SpiritSamuraiEntity.AttackAnim.KATANA_TECHNIQUE) {
               this.playSlashSound();
            }
         } else if (this.currentAttack.data.isHitFrame(this.meleeAnimTimer)) {
            if (this.currentAttack != SpiritSamuraiEntity.AttackAnim.KATANA_TECHNIQUE) {
               Vec3 lunge = this.target.position().subtract(this.mob.position()).normalize().scale(0.55F);
               this.mob.push(lunge.x, lunge.y, lunge.z);
               if (distance <= meleeRange && Utils.hasLineOfSight(this.mob.level, this.mob, this.target, true)) {
                  boolean flag = this.mob.doHurtTarget(this.target);
                  this.target.invulnerableTime = 0;
                  if (flag && this.currentAttack.data.isSingleHit() && (this.mob.getRandom().nextFloat() < 0.75F || this.target.isBlocking())) {
                     this.queueCombo = this.randomizeNextAttack(0.0F);
                  }
               }
            } else {
               if (!this.hasUsedTechnique) {
                  Vec3 charge = this.target.position().subtract(this.mob.position()).normalize().multiply(2.4, 0.5, 2.4).add(0.0, 0.15, 0.0);
                  this.mob.push(charge.x, charge.y, charge.z);
                  this.oldTechniquePos = this.mob.position();
                  this.mob.getNavigation().stop();
                  this.hasUsedTechnique = true;
                  this.playSlashSound();
                  Vec3 AABBPos = this.mob.position();

                  for (Entity targetEntity : this.mob.level.getEntities(this.mob, AABB.ofSize(AABBPos, 5.0, 3.0, 5.0))) {
                     if (targetEntity.isAlive()
                        && targetEntity.isPickable()
                        && Utils.hasLineOfSight(this.mob.level, AABBPos, targetEntity.getBoundingBox().getCenter(), true)) {
                        DamageSources.applyDamage(
                           targetEntity,
                           (float)this.mob.getAttributeValue(Attributes.ATTACK_DAMAGE),
                           MFTESpellRegistries.REVENANT_OF_HONOR_SPELL.get().getDamageSource(this.mob)
                        );
                     }
                  }
               }

               if (!this.hasHitTechnique && distance <= meleeRange * 0.45F) {
                  Vec3 knockback = this.oldTechniquePos.subtract(this.target.position());
                  this.target.knockback(1.0, knockback.x, knockback.z);
                  this.hasHitTechnique = true;
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
      } else if (distance < meleeRange * (this.nextAttack == SpiritSamuraiEntity.AttackAnim.KATANA_TECHNIQUE ? 3 : 1)) {
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

   private SpiritSamuraiEntity.AttackAnim randomizeNextAttack(float distance) {
      float meleeRange = this.meleeRange();
      int i;
      if (distance < meleeRange * 1.5F) {
         i = SpiritSamuraiEntity.AttackAnim.values().length - 1;
      } else {
         if (this.mob.getRandom().nextFloat() < 0.25F && distance > meleeRange * 2.5F) {
            return SpiritSamuraiEntity.AttackAnim.KATANA_TECHNIQUE;
         }

         i = SpiritSamuraiEntity.AttackAnim.values().length;
      }

      return SpiritSamuraiEntity.AttackAnim.values()[this.mob.getRandom().nextInt(i)];
   }

   private void forceFaceTarget() {
      if (!this.hasUsedTechnique) {
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
         this.hasUsedTechnique = false;
         this.hasHitTechnique = false;
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

   public void playSlashSound() {
      this.mob.playSound((SoundEvent)MFTESoundRegistries.SAMURAI_SLASH.get(), 1.0F, Mth.randomBetweenInclusive(this.mob.getRandom(), 9, 13) * 0.1F);
   }
}
