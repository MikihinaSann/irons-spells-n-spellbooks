package com.somake.somakespells.entity;

import com.somake.somakespells.capabilities.ConnectionLinkData;
import com.somake.somakespells.registries.ModAttachments;
import com.somake.somakespells.registries.ModEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
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

public class ConnectionChainVisualEntity extends Entity {
   private static final EntityDataAccessor<Integer> FIRST_TARGET_ID = SynchedEntityData.defineId(ConnectionChainVisualEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> SECOND_TARGET_ID = SynchedEntityData.defineId(ConnectionChainVisualEntity.class, EntityDataSerializers.INT);

   public ConnectionChainVisualEntity(EntityType<?> entityType, Level level) {
      super(entityType, level);
      this.noPhysics = true;
   }

   public ConnectionChainVisualEntity(Level level, LivingEntity firstTarget, LivingEntity secondTarget) {
      this((EntityType<?>)ModEntityTypes.CONNECTION_CHAIN_VISUAL.get(), level);
      this.setTargets(firstTarget, secondTarget);
      this.updateMidpoint(firstTarget, secondTarget);
   }

   public void setTargets(LivingEntity firstTarget, LivingEntity secondTarget) {
      this.entityData.set(FIRST_TARGET_ID, firstTarget.getId());
      this.entityData.set(SECOND_TARGET_ID, secondTarget.getId());
   }

   public LivingEntity getFirstTarget() {
      return this.level().getEntity((Integer)this.entityData.get(FIRST_TARGET_ID)) instanceof LivingEntity livingEntity ? livingEntity : null;
   }

   public LivingEntity getSecondTarget() {
      return this.level().getEntity((Integer)this.entityData.get(SECOND_TARGET_ID)) instanceof LivingEntity livingEntity ? livingEntity : null;
   }

   public boolean matches(LivingEntity firstTarget, LivingEntity secondTarget) {
      int firstId = (Integer)this.entityData.get(FIRST_TARGET_ID);
      int secondId = (Integer)this.entityData.get(SECOND_TARGET_ID);
      return firstTarget.getId() == firstId && secondTarget.getId() == secondId || firstTarget.getId() == secondId && secondTarget.getId() == firstId;
   }

   public void tick() {
      super.tick();
      LivingEntity firstTarget = this.getFirstTarget();
      LivingEntity secondTarget = this.getSecondTarget();
      if (firstTarget != null && secondTarget != null && firstTarget.isAlive() && secondTarget.isAlive()) {
         this.updateMidpoint(firstTarget, secondTarget);
         if (!this.level().isClientSide && !this.isStillLinked(firstTarget, secondTarget)) {
            this.discard();
         }
      } else {
         this.discard();
      }
   }

   private void updateMidpoint(LivingEntity firstTarget, LivingEntity secondTarget) {
      Vec3 firstCenter = firstTarget.getBoundingBox().getCenter();
      Vec3 secondCenter = secondTarget.getBoundingBox().getCenter();
      Vec3 midpoint = firstCenter.lerp(secondCenter, 0.5);
      this.setPos(midpoint.x, midpoint.y, midpoint.z);
   }

   private boolean isStillLinked(LivingEntity firstTarget, LivingEntity secondTarget) {
      if (!(this.level() instanceof ServerLevel serverLevel)) {
         return true;
      } else {
         ConnectionLinkData var8 = (ConnectionLinkData)firstTarget.getData(ModAttachments.CONNECTION_LINK_DATA);
         ConnectionLinkData secondLink = (ConnectionLinkData)secondTarget.getData(ModAttachments.CONNECTION_LINK_DATA);
         long gameTime = serverLevel.getGameTime();
         return var8.hasLink()
            && secondLink.hasLink()
            && var8.getKind() != null
            && var8.getKind().isRestraining()
            && secondLink.getKind() == var8.getKind()
            && var8.isActive(gameTime)
            && secondLink.isActive(gameTime)
            && secondTarget.getUUID().equals(var8.getPartnerUuid())
            && firstTarget.getUUID().equals(secondLink.getPartnerUuid());
      }
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(FIRST_TARGET_ID, -1);
      builder.define(SECOND_TARGET_ID, -1);
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      if (compound.contains("FirstTargetId")) {
         this.entityData.set(FIRST_TARGET_ID, compound.getInt("FirstTargetId"));
      }

      if (compound.contains("SecondTargetId")) {
         this.entityData.set(SECOND_TARGET_ID, compound.getInt("SecondTargetId"));
      }
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      compound.putInt("FirstTargetId", (Integer)this.entityData.get(FIRST_TARGET_ID));
      compound.putInt("SecondTargetId", (Integer)this.entityData.get(SECOND_TARGET_ID));
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
      return new ClientboundAddEntityPacket(this, entity);
   }
}
