package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
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
import net.minecraft.world.phys.Vec3;

public class AbyssalBurnEntity extends Projectile implements AntiMagicSusceptible {
   private static final EntityDataAccessor<Float> DATA_DAMAGE = SynchedEntityData.defineId(AbyssalBurnEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> DATA_IS_SOUL_FIRE = SynchedEntityData.defineId(AbyssalBurnEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_IS_INFERNAL_FIRE = SynchedEntityData.defineId(AbyssalBurnEntity.class, EntityDataSerializers.BOOLEAN);
   private int maxAge = 40;
   private int age;
   private int spawnDelay = 2;
   private int strikesSpawned = 0;
   private int maxStrikes = 10;
   private float distancePerStrike = 2.5F;

   public AbyssalBurnEntity(EntityType<? extends Projectile> entityType, Level level) {
      super(entityType, level);
   }

   public AbyssalBurnEntity(Level level, LivingEntity owner, float damage) {
      this((EntityType<? extends Projectile>)ModEntityTypes.ABYSSAL_BURN.get(), level);
      this.setOwner(owner);
      this.setDamage(damage);
      this.setPos(owner.position());
      this.setYRot(owner.getYRot());
      this.setXRot(0.0F);
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_DAMAGE, 10.0F);
      builder.define(DATA_IS_SOUL_FIRE, false);
      builder.define(DATA_IS_INFERNAL_FIRE, false);
   }

   public void setDamage(float damage) {
      this.entityData.set(DATA_DAMAGE, damage);
   }

   public float getDamage() {
      return (Float)this.entityData.get(DATA_DAMAGE);
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
      super.tick();
      if (this.age++ < this.maxAge && this.strikesSpawned < this.maxStrikes) {
         if (!this.level().isClientSide && this.age % this.spawnDelay == 0) {
            this.spawnStrike();
         }
      } else {
         this.discard();
      }
   }

   private void spawnStrike() {
      Entity owner = this.getOwner();
      if (owner != null) {
         float currentDist = (this.strikesSpawned + 1) * this.distancePerStrike;
         Vec3 look = Vec3.directionFromRotation(0.0F, this.getYRot());
         Vec3 spawnPos = this.position().add(look.scale(currentDist));
         AbyssalStrikeEntity strike = new AbyssalStrikeEntity(this.level(), (LivingEntity)owner, this.getDamage());
         strike.setIsSoulFire(this.isSoulFire());
         strike.setIsInfernalFire(this.isInfernalFire());
         strike.setPos(spawnPos);
         this.level().addFreshEntity(strike);
         this.strikesSpawned++;
      }
   }

   public void onAntiMagic(MagicData playerMagicData) {
      this.discard();
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      this.age = compound.getInt("Age");
      this.strikesSpawned = compound.getInt("StrikesSpawned");
      this.setDamage(compound.getFloat("Damage"));
      this.setIsSoulFire(compound.getBoolean("IsSoulFire"));
      this.setIsInfernalFire(compound.getBoolean("IsInfernalFire"));
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      compound.putInt("Age", this.age);
      compound.putInt("StrikesSpawned", this.strikesSpawned);
      compound.putFloat("Damage", this.getDamage());
      compound.putBoolean("IsSoulFire", this.isSoulFire());
      compound.putBoolean("IsInfernalFire", this.isInfernalFire());
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return super.getAddEntityPacket(serverEntity);
   }
}
