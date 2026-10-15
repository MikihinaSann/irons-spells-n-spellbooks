package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.fire.ApocalypticBurstSpell;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class ApocalypticBurstChargeEntity extends Projectile implements AntiMagicSusceptible {
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(ApocalypticBurstChargeEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> DATA_IS_SOUL_FIRE = SynchedEntityData.defineId(
      ApocalypticBurstChargeEntity.class, EntityDataSerializers.BOOLEAN
   );
   private static final EntityDataAccessor<Boolean> DATA_IS_INFERNAL_FIRE = SynchedEntityData.defineId(
      ApocalypticBurstChargeEntity.class, EntityDataSerializers.BOOLEAN
   );
   private float maxRadius = 5.0F;
   private int duration = 60;
   private int age;

   public ApocalypticBurstChargeEntity(EntityType<? extends Projectile> entityType, Level level) {
      super(entityType, level);
   }

   public ApocalypticBurstChargeEntity(Level level, LivingEntity owner) {
      this((EntityType<? extends Projectile>)ModEntityTypes.APOCALYPTIC_BURST_CHARGE.get(), level);
      this.setOwner(owner);
      this.setPos(owner.position());
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_RADIUS, 5.0F);
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

   public float getRadius() {
      return (Float)this.entityData.get(DATA_RADIUS);
   }

   public void setRadius(float radius) {
      this.entityData.set(DATA_RADIUS, radius);
   }

   public float getLerpedRadius(float partialTicks) {
      float smoothAge = this.age + partialTicks;
      float progress = smoothAge / this.duration;
      if (progress > 1.0F) {
         progress = 1.0F;
      }

      return this.maxRadius * (1.0F - progress);
   }

   public void tick() {
      super.tick();
      Entity owner = this.getOwner();
      if (!this.level().isClientSide) {
         if (!(owner instanceof LivingEntity livingOwner)) {
            this.discard();
            return;
         }

         MagicData magicData = MagicData.getPlayerMagicData(livingOwner);
         if (!magicData.isCasting()
            || !magicData.getCastingSpellId().equals(((ApocalypticBurstSpell)ModSpells.APOCALYPTIC_BURST.get()).getSpellResource().toString())) {
            this.discard();
            return;
         }

         this.setPos(owner.position());
      }

      if (this.age++ >= this.duration) {
         this.discard();
      } else {
         float progress = (float)this.age / this.duration;
         float currentRadius = this.maxRadius * (1.0F - progress);
         this.setRadius(currentRadius);
      }
   }

   public void onAntiMagic(MagicData playerMagicData) {
      this.discard();
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      this.age = compound.getInt("Age");
      this.setIsSoulFire(compound.getBoolean("IsSoulFire"));
      this.setIsInfernalFire(compound.getBoolean("IsInfernalFire"));
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      compound.putInt("Age", this.age);
      compound.putBoolean("IsSoulFire", this.isSoulFire());
      compound.putBoolean("IsInfernalFire", this.isInfernalFire());
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return super.getAddEntityPacket(serverEntity);
   }
}
