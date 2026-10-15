package net.alshanex.alshanex_familiars.entity.misc;

import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MemoryFragmentEntity extends Entity {
   private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(MemoryFragmentEntity.class, EntityDataSerializers.ITEM_STACK);
   private double tx;
   private double ty;
   private double tz;
   private int life;

   public MemoryFragmentEntity(EntityType<? extends MemoryFragmentEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public MemoryFragmentEntity(Level pLevel, double pX, double pY, double pZ) {
      this((EntityType<? extends MemoryFragmentEntity>)EntityRegistry.MEMORY_FRAGMENT.get(), pLevel);
      this.setPos(pX, pY, pZ);
   }

   public boolean shouldRenderAtSqrDistance(double pDistance) {
      double radius = this.getBoundingBox().getSize() * 4.0;
      if (Double.isNaN(radius)) {
         radius = 4.0;
      }

      radius *= 64.0;
      return pDistance < radius * radius;
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
   }

   public void signalTo(BlockPos pPos) {
      double pPosX = pPos.getX();
      int pPosY = pPos.getY();
      double pPosZ = pPos.getZ();
      double distX = pPosX - this.getX();
      double distZ = pPosZ - this.getZ();
      double sqrt = Math.sqrt(distX * distX + distZ * distZ);
      if (sqrt > 12.0) {
         this.tx = this.getX() + distX / sqrt * 12.0;
         this.tz = this.getZ() + distZ / sqrt * 12.0;
         this.ty = this.getY() + 8.0;
      } else {
         this.tx = pPosX;
         this.ty = pPosY;
         this.tz = pPosZ;
      }

      this.life = 0;
   }

   public void lerpMotion(double pX, double pY, double pZ) {
      this.setDeltaMovement(pX, pY, pZ);
      if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
         double sqrt = Math.sqrt(pX * pX + pZ * pZ);
         this.setYRot((float)(Mth.atan2(pX, pZ) * 180.0F / (float)Math.PI));
         this.setXRot((float)(Mth.atan2(pY, sqrt) * 180.0F / (float)Math.PI));
         this.yRotO = this.getYRot();
         this.xRotO = this.getXRot();
      }
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_ITEM_STACK, ItemStack.EMPTY);
   }

   public void tick() {
      super.tick();
      Vec3 deltaMovement = this.getDeltaMovement();
      double moveX = this.getX() + deltaMovement.x;
      double moveY = this.getY() + deltaMovement.y;
      double moveZ = this.getZ() + deltaMovement.z;
      double horizontalDistance = deltaMovement.horizontalDistance();
      this.setXRot(lerpRotation(this.xRotO, (float)(Mth.atan2(deltaMovement.y, horizontalDistance) * 180.0F / (float)Math.PI)));
      this.setYRot(lerpRotation(this.yRotO, (float)(Mth.atan2(deltaMovement.x, deltaMovement.z) * 180.0F / (float)Math.PI)));
      if (!this.level().isClientSide) {
         double finalX = this.tx - moveX;
         double finalZ = this.tz - moveZ;
         float sqrt = (float)Math.sqrt(finalX * finalX + finalZ * finalZ);
         float atan2 = (float)Mth.atan2(finalZ, finalX);
         double lerp = Mth.lerp(0.0025, horizontalDistance, sqrt);
         double deltaY = deltaMovement.y;
         if (sqrt < 1.0F) {
            lerp *= 0.8;
            deltaY *= 0.8;
         }

         int finalY = this.getY() < this.ty ? 1 : -1;
         deltaMovement = new Vec3(Math.cos(atan2) * lerp, deltaY + (finalY - deltaY) * 0.015F, Math.sin(atan2) * lerp);
         this.setDeltaMovement(deltaMovement);
      }

      float angle = 0.25F;
      if (this.isInWater()) {
         for (int i = 0; i < 4; i++) {
            this.level()
               .addParticle(
                  ParticleTypes.BUBBLE,
                  moveX - deltaMovement.x * 0.25,
                  moveY - deltaMovement.y * 0.25,
                  moveZ - deltaMovement.z * 0.25,
                  deltaMovement.x,
                  deltaMovement.y,
                  deltaMovement.z
               );
         }
      } else {
         this.level()
            .addParticle(
               ParticleTypes.GLOW_SQUID_INK,
               moveX - deltaMovement.x * 0.25 + this.random.nextDouble() * 0.6 - 0.3,
               moveY - deltaMovement.y * 0.25 - 0.5,
               moveZ - deltaMovement.z * 0.25 + this.random.nextDouble() * 0.6 - 0.3,
               deltaMovement.x,
               deltaMovement.y,
               deltaMovement.z
            );
      }

      if (!this.level().isClientSide) {
         this.setPos(moveX, moveY, moveZ);
         this.life++;
         if (this.life > 80 && !this.level().isClientSide) {
            this.playSound(SoundEvents.ENDER_EYE_DEATH, 1.0F, 1.0F);
            this.discard();
         }
      } else {
         this.setPosRaw(moveX, moveY, moveZ);
      }
   }

   protected static float lerpRotation(float pCurrentRotation, float pTargetRotation) {
      while (pTargetRotation - pCurrentRotation < -180.0F) {
         pCurrentRotation -= 360.0F;
      }

      while (pTargetRotation - pCurrentRotation >= 180.0F) {
         pCurrentRotation += 360.0F;
      }

      return Mth.lerp(0.2F, pCurrentRotation, pTargetRotation);
   }

   public float getLightLevelDependentMagicValue() {
      return 1.0F;
   }

   public boolean isAttackable() {
      return false;
   }
}
