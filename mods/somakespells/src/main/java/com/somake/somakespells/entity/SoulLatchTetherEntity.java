package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SoulLatchTetherEntity extends Entity {
   private static final EntityDataAccessor<Boolean> RED_SOUL = SynchedEntityData.defineId(SoulLatchTetherEntity.class, EntityDataSerializers.BOOLEAN);
   private LivingEntity owner;
   private UUID ownerId;
   private LivingEntity target;
   private UUID targetId;

   public SoulLatchTetherEntity(EntityType<?> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public SoulLatchTetherEntity(Level level, LivingEntity owner, LivingEntity target, boolean redSoul) {
      this((EntityType<?>)ModEntityTypes.SOUL_LATCH_TETHER.get(), level);
      this.owner = owner;
      this.ownerId = owner.getUUID();
      this.target = target;
      this.targetId = target.getUUID();
      this.entityData.set(RED_SOUL, redSoul);
      this.refreshPosition();
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(RED_SOUL, false);
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      if (compound.hasUUID("Owner")) {
         this.ownerId = compound.getUUID("Owner");
      }

      if (compound.hasUUID("Target")) {
         this.targetId = compound.getUUID("Target");
      }
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      if (this.ownerId != null) {
         compound.putUUID("Owner", this.ownerId);
      }

      if (this.targetId != null) {
         compound.putUUID("Target", this.targetId);
      }
   }

   public void tick() {
      super.tick();
      this.resolveEntities();
      if (this.owner != null && this.target != null && this.owner.isAlive() && this.target.isAlive()) {
         this.refreshPosition();
      } else {
         this.discard();
      }
   }

   private void resolveEntities() {
      if (this.level() instanceof ServerLevel serverLevel) {
         if (this.owner == null && this.ownerId != null && serverLevel.getEntity(this.ownerId) instanceof LivingEntity livingEntity) {
            this.owner = livingEntity;
         }

         if (this.target == null && this.targetId != null && serverLevel.getEntity(this.targetId) instanceof LivingEntity livingEntity) {
            this.target = livingEntity;
         }
      }
   }

   private void refreshPosition() {
      if (this.owner != null && this.target != null) {
         Vec3 midpoint = this.getOwnerAnchor().lerp(this.getTargetAnchor(), 0.5);
         this.setPos(midpoint.x, midpoint.y, midpoint.z);
      }
   }

   public LivingEntity getOwnerEntity() {
      return this.owner;
   }

   public LivingEntity getTargetEntity() {
      return this.target;
   }

   public Vec3 getOwnerAnchor() {
      return this.owner != null ? this.owner.position().add(0.0, this.owner.getBbHeight() * 0.72, 0.0) : this.position();
   }

   public Vec3 getTargetAnchor() {
      return this.target != null ? this.target.position().add(0.0, this.target.getBbHeight() * 0.58, 0.0) : this.position();
   }

   public boolean isRedSoul() {
      return (Boolean)this.entityData.get(RED_SOUL);
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return super.getAddEntityPacket(serverEntity);
   }
}
