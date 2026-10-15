package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.ritual.RitualElementalMode;
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

public class RitualFireSigilEntity extends Entity {
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(RitualFireSigilEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(RitualFireSigilEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_MODE = SynchedEntityData.defineId(RitualFireSigilEntity.class, EntityDataSerializers.INT);

   public RitualFireSigilEntity(EntityType<? extends RitualFireSigilEntity> entityType, Level level) {
      super(entityType, level);
      this.noPhysics = true;
   }

   public RitualFireSigilEntity(Level level, double x, double y, double z, float radius, int duration) {
      this(level, x, y, z, radius, duration, RitualElementalMode.NORMAL);
   }

   public RitualFireSigilEntity(Level level, double x, double y, double z, float radius, int duration, RitualElementalMode mode) {
      this((EntityType<? extends RitualFireSigilEntity>)ModEntityTypes.RITUAL_FIRE_SIGIL.get(), level);
      this.setPos(x, y, z);
      this.setRadius(radius);
      this.setDuration(duration);
      this.setMode(mode);
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_RADIUS, 1.85F);
      builder.define(DATA_DURATION, 110);
      builder.define(DATA_MODE, RitualElementalMode.NORMAL.ordinal());
   }

   public float getRadius() {
      return (Float)this.entityData.get(DATA_RADIUS);
   }

   public void setRadius(float radius) {
      this.entityData.set(DATA_RADIUS, radius);
   }

   public int getDuration() {
      return (Integer)this.entityData.get(DATA_DURATION);
   }

   public void setDuration(int duration) {
      this.entityData.set(DATA_DURATION, duration);
   }

   public RitualElementalMode getMode() {
      return RitualElementalMode.fromOrdinal((Integer)this.entityData.get(DATA_MODE));
   }

   public void setMode(RitualElementalMode mode) {
      this.entityData.set(DATA_MODE, mode.ordinal());
   }

   public float getProgress(float partialTick) {
      return Math.min(1.0F, (this.tickCount + partialTick) / Math.max(1, this.getDuration()));
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide && this.tickCount >= this.getDuration()) {
         this.discard();
      }
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
      this.setRadius(tag.getFloat("Radius"));
      this.setDuration(tag.getInt("Duration"));
      this.setMode(RitualElementalMode.fromId(tag.getString("Mode")));
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
      tag.putFloat("Radius", this.getRadius());
      tag.putInt("Duration", this.getDuration());
      tag.putString("Mode", this.getMode().id());
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return super.getAddEntityPacket(serverEntity);
   }
}
