package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

public class FireBlastVisualEntity extends Entity implements IEntityWithComplexSpawn {
   public static final int lifetime = 8;
   public float distance;
   private static final EntityDataAccessor<Boolean> DATA_IS_SOUL_FIRE = SynchedEntityData.defineId(FireBlastVisualEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_IS_INFERNAL_FIRE = SynchedEntityData.defineId(
      FireBlastVisualEntity.class, EntityDataSerializers.BOOLEAN
   );

   public FireBlastVisualEntity(EntityType<?> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public FireBlastVisualEntity(Level level, Vec3 start, Vec3 end, LivingEntity owner) {
      super((EntityType)ModEntityTypes.FIRE_BLAST_VISUAL_ENTITY.get(), level);
      this.setPos(start);
      this.distance = (float)start.distanceTo(end);
      this.setRot(owner.getYRot(), owner.getXRot());
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_IS_SOUL_FIRE, false);
      builder.define(DATA_IS_INFERNAL_FIRE, false);
   }

   public void setIsSoulFire(boolean isSoulFire) {
      this.entityData.set(DATA_IS_SOUL_FIRE, isSoulFire);
   }

   public boolean isSoulFire() {
      return (Boolean)this.entityData.get(DATA_IS_SOUL_FIRE);
   }

   public void setIsInfernalFire(boolean isInfernalFire) {
      this.entityData.set(DATA_IS_INFERNAL_FIRE, isInfernalFire);
   }

   public boolean isInfernalFire() {
      return (Boolean)this.entityData.get(DATA_IS_INFERNAL_FIRE);
   }

   public void tick() {
      if (this.tickCount == 1) {
         if (this.level().isClientSide) {
            Vec3 forward = this.getForward();
            ParticleOptions particle = ParticleTypes.FLAME;
            if (this.isSoulFire()) {
               particle = ParticleTypes.SOUL_FIRE_FLAME;
            } else if (this.isInfernalFire()) {
               particle = ParticleTypes.FLAME;
            }
         }
      } else if (this.tickCount > 8) {
         this.discard();
      }
   }

   public Vec3 getForward() {
      return Vec3.directionFromRotation(this.getXRot(), this.getYRot());
   }

   public boolean shouldRender(double pX, double pY, double pZ) {
      return true;
   }

   public boolean shouldBeSaved() {
      return false;
   }

   protected void readAdditionalSaveData(CompoundTag pCompound) {
   }

   protected void addAdditionalSaveData(CompoundTag pCompound) {
   }

   public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
      buffer.writeInt((int)(this.distance * 10.0F));
      buffer.writeBoolean(this.isSoulFire());
      buffer.writeBoolean(this.isInfernalFire());
   }

   public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
      this.distance = additionalData.readInt() / 10.0F;
      this.setIsSoulFire(additionalData.readBoolean());
      this.setIsInfernalFire(additionalData.readBoolean());
   }
}
