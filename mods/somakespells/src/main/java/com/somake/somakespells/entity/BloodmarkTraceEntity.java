package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BloodmarkTraceEntity extends Entity {
   private static final EntityDataAccessor<Float> START_X = SynchedEntityData.defineId(BloodmarkTraceEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> START_Y = SynchedEntityData.defineId(BloodmarkTraceEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> START_Z = SynchedEntityData.defineId(BloodmarkTraceEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> END_X = SynchedEntityData.defineId(BloodmarkTraceEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> END_Y = SynchedEntityData.defineId(BloodmarkTraceEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> END_Z = SynchedEntityData.defineId(BloodmarkTraceEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> LIFETIME = SynchedEntityData.defineId(BloodmarkTraceEntity.class, EntityDataSerializers.INT);
   private static final int BUILD_TICKS = 10;
   private static final int DEFAULT_LIFETIME = 60;

   public BloodmarkTraceEntity(EntityType<?> entityType, Level level) {
      super(entityType, level);
      this.noPhysics = true;
   }

   public BloodmarkTraceEntity(Level level, Vec3 start, Vec3 end) {
      this((EntityType<?>)ModEntityTypes.BLOODMARK_TRACE.get(), level);
      this.setEndpoints(start, end);
   }

   public BloodmarkTraceEntity(Level level, Vec3 start, Vec3 end, int lifetime) {
      this((EntityType<?>)ModEntityTypes.BLOODMARK_TRACE.get(), level);
      this.setEndpoints(start, end);
      this.entityData.set(LIFETIME, Math.max(14, lifetime));
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(START_X, 0.0F);
      builder.define(START_Y, 0.0F);
      builder.define(START_Z, 0.0F);
      builder.define(END_X, 0.0F);
      builder.define(END_Y, 0.0F);
      builder.define(END_Z, 0.0F);
      builder.define(LIFETIME, 60);
   }

   public void setEndpoints(Vec3 start, Vec3 end) {
      this.entityData.set(START_X, (float)start.x);
      this.entityData.set(START_Y, (float)start.y);
      this.entityData.set(START_Z, (float)start.z);
      this.entityData.set(END_X, (float)end.x);
      this.entityData.set(END_Y, (float)end.y);
      this.entityData.set(END_Z, (float)end.z);
      Vec3 midpoint = start.lerp(end, 0.5);
      this.setPos(midpoint.x, midpoint.y, midpoint.z);
   }

   public Vec3 getStart() {
      return new Vec3(
         ((Float)this.entityData.get(START_X)).floatValue(),
         ((Float)this.entityData.get(START_Y)).floatValue(),
         ((Float)this.entityData.get(START_Z)).floatValue()
      );
   }

   public Vec3 getEnd() {
      return new Vec3(
         ((Float)this.entityData.get(END_X)).floatValue(), ((Float)this.entityData.get(END_Y)).floatValue(), ((Float)this.entityData.get(END_Z)).floatValue()
      );
   }

   public float getBuildProgress(float partialTick) {
      return Math.min(1.0F, (this.tickCount + partialTick) / 10.0F);
   }

   public void tick() {
      super.tick();
      if (this.tickCount >= (Integer)this.entityData.get(LIFETIME)) {
         this.discard();
      }
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      Vec3 start = new Vec3(compound.getFloat("StartX"), compound.getFloat("StartY"), compound.getFloat("StartZ"));
      Vec3 end = new Vec3(compound.getFloat("EndX"), compound.getFloat("EndY"), compound.getFloat("EndZ"));
      this.setEndpoints(start, end);
      if (compound.contains("Lifetime")) {
         this.entityData.set(LIFETIME, compound.getInt("Lifetime"));
      }
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      compound.putFloat("StartX", (Float)this.entityData.get(START_X));
      compound.putFloat("StartY", (Float)this.entityData.get(START_Y));
      compound.putFloat("StartZ", (Float)this.entityData.get(START_Z));
      compound.putFloat("EndX", (Float)this.entityData.get(END_X));
      compound.putFloat("EndY", (Float)this.entityData.get(END_Y));
      compound.putFloat("EndZ", (Float)this.entityData.get(END_Z));
      compound.putInt("Lifetime", (Integer)this.entityData.get(LIFETIME));
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return super.getAddEntityPacket(serverEntity);
   }
}
