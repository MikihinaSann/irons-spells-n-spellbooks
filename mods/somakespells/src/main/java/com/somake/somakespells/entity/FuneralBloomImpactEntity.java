package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FuneralBloomImpactEntity extends Entity {
   private static final EntityDataAccessor<Float> DATA_SCALE = SynchedEntityData.defineId(FuneralBloomImpactEntity.class, EntityDataSerializers.FLOAT);
   public static final int LIFETIME = 12;

   public FuneralBloomImpactEntity(EntityType<?> type, Level level) {
      super(type, level);
      this.noPhysics = true;
      this.setNoGravity(true);
   }

   public FuneralBloomImpactEntity(Level level, Vec3 position, float scale) {
      this((EntityType<?>)ModEntityTypes.FUNERAL_BLOOM_IMPACT.get(), level);
      this.setPos(position);
      this.setScale(scale);
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_SCALE, 1.0F);
   }

   public float getScale() {
      return (Float)this.entityData.get(DATA_SCALE);
   }

   public void setScale(float scale) {
      this.entityData.set(DATA_SCALE, Mth.clamp(scale, 0.65F, 1.6F));
   }

   public float getOpenProgress(float partialTick) {
      float age = this.tickCount + partialTick;
      float open = Mth.clamp(age / 4.0F, 0.0F, 1.0F);
      float close = Mth.clamp((age - 7.0F) / 5.0F, 0.0F, 1.0F);
      return smoothstep(open) * (1.0F - smoothstep(close));
   }

   private static float smoothstep(float value) {
      return value * value * (3.0F - 2.0F * value);
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide && this.tickCount >= 12) {
         this.discard();
      }
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
      this.setScale(tag.getFloat("Scale"));
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
      tag.putFloat("Scale", this.getScale());
   }
}
