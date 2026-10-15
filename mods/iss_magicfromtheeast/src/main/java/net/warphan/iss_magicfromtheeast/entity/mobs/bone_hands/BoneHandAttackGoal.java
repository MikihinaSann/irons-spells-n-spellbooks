package net.warphan.iss_magicfromtheeast.entity.mobs.bone_hands;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.goals.WarlockAttackGoal;
import io.redspace.ironsspellbooks.network.SyncAnimationPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.warphan.iss_magicfromtheeast.registries.MFTESoundRegistries;

public class BoneHandAttackGoal extends WarlockAttackGoal {
   final BoneHandsEntity boneHands;
   int meleeAnimTimer = -1;
   public BoneHandsEntity.AttackAnim attack;
   float meleeRange = 9.0F;
   float watchRange = 12.0F;

   public BoneHandAttackGoal(BoneHandsEntity boneHandsEntity, double pSpeedModifier, int minAttackInterval, int maxAttackInterval) {
      super(boneHandsEntity, pSpeedModifier, minAttackInterval, maxAttackInterval);
      this.boneHands = boneHandsEntity;
      this.attack = this.actionAttack(0.0F);
      this.wantsToMelee = true;
   }

   protected float meleeBias() {
      return 1.0F;
   }

   protected void handleAttackLogic(double distanceSquared) {
      float distance = Mth.sqrt((float)distanceSquared);
      if (this.target != null) {
         this.mob.getLookControl().setLookAt(this.target);
      }

      if (distance > this.meleeRange) {
         if (this.mob instanceof BoneHandsEntity boneHands) {
            boneHands.stopAnimationAttack();
         }

         if (distance < this.watchRange) {
            this.forceFaceTarget();
         } else if (distance > this.watchRange) {
            this.mob.setTarget(null);
            this.mob.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
         }

         this.meleeAnimTimer = 0;
      } else if (distance <= this.meleeRange) {
         if (this.mob instanceof BoneHandsEntity boneHands) {
            boneHands.triggerAnimatingAttack();
         }

         if (this.meleeAnimTimer > 0) {
            this.forceFaceTarget();
            this.meleeAnimTimer--;
            if (this.attack.data.isHitFrame(this.meleeAnimTimer)) {
               this.playSlamSound();
               float radius = 1.0F;
               Vec3 forward = this.boneHands.getForward().multiply(1.0, 0.0, 1.0).normalize();
               Vec3 start = this.boneHands.getEyePosition().subtract(0.0, 8.0, 0.0).add(forward.scale(1.5));
               Vec3 bbBox = new Vec3(radius, radius * 5.0F, radius);
               float slamDamage = (float)this.boneHands.getAttributeValue(Attributes.ATTACK_DAMAGE);

               for (int i = 0; i <= 6; i++) {
                  Vec3 slamPos = start.add(forward.scale(i));
                  this.boneHands.level.getEntitiesOfClass(LivingEntity.class, new AABB(slamPos.subtract(bbBox), slamPos.add(bbBox))).forEach(entity -> {
                     if (entity.isPickable() && !DamageSources.isFriendlyFireBetween(this.boneHands, entity)) {
                        entity.hurt(this.boneHands.level().damageSources().mobAttack(this.boneHands), slamDamage);
                        entity.hurtMarked = true;
                     }
                  });
               }
            }
         } else if (this.target != null && !this.target.isDeadOrDying()) {
            this.doMeleeAction();
         } else if (this.meleeAnimTimer == 0) {
            this.resetMeleeAttackInterval(distanceSquared);
            this.meleeAnimTimer = -1;
         }

         if ((this.target == null || this.target.isDeadOrDying()) && this.mob instanceof BoneHandsEntity boneHands) {
            boneHands.stopAnimationAttack();
         }
      }
   }

   private BoneHandsEntity.AttackAnim actionAttack(float distance) {
      return BoneHandsEntity.AttackAnim.BONE_SLAM;
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
      if (this.attack != null) {
         this.meleeAnimTimer = this.attack.data.lengthInTicks;
         PacketDistributor.sendToPlayersTrackingEntity(
            this.boneHands, new SyncAnimationPacket(this.attack.toString(), this.boneHands), new CustomPacketPayload[0]
         );
      }
   }

   protected void doMovement(double distanceSquared) {
   }

   public boolean canContinueToUse() {
      return super.canContinueToUse() || this.meleeAnimTimer > 0;
   }

   public void stop() {
      super.stop();
      this.meleeAnimTimer = -1;
   }

   public void playSlamSound() {
      this.mob.playSound((SoundEvent)MFTESoundRegistries.BONE_SLAM.get(), 1.0F, Mth.randomBetweenInclusive(this.mob.getRandom(), 9, 13) * 0.1F);
   }
}
