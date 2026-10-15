package com.somake.somakespells.entity;

import com.github.L_Ender.cataclysm.init.ModParticle;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModParticles;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.fire.PumpkinBombSpell;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

public class PumpkinBombEntity extends ThrowableItemProjectile {
   private static final EntityDataAccessor<Boolean> DATA_IS_SOUL_FIRE = SynchedEntityData.defineId(PumpkinBombEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_IS_INFERNAL_FIRE = SynchedEntityData.defineId(PumpkinBombEntity.class, EntityDataSerializers.BOOLEAN);
   private float initialDamage = 0.0F;
   private float aoeDamage = 0.0F;
   private float radius = 3.0F;
   private int effectDuration = 200;

   public void setEffectDuration(int duration) {
      this.effectDuration = duration;
   }

   public PumpkinBombEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
      super(entityType, level);
   }

   public PumpkinBombEntity(Level level, LivingEntity shooter) {
      super((EntityType)ModEntityTypes.PUMPKIN_BOMB.get(), shooter, level);
   }

   public void setDamage(float initialDamage, float aoeDamage) {
      this.initialDamage = initialDamage;
      this.aoeDamage = aoeDamage;
   }

   public void setRadius(float radius) {
      this.radius = radius;
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
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

   protected Item getDefaultItem() {
      return Items.PUMPKIN;
   }

   protected void onHit(HitResult result) {
      super.onHit(result);
      if (!this.level().isClientSide) {
         this.explode();
         this.spawnCloud();
         this.discard();
      }
   }

   private void explode() {
      AABB area = this.getBoundingBox().inflate(this.radius, this.radius, this.radius);
      List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area);
      Entity owner = this.getOwner();

      for (LivingEntity target : targets) {
         if (target != owner && (owner == null || !target.isAlliedTo(owner)) && target.distanceToSqr(this) <= this.radius * this.radius) {
            target.hurt(((PumpkinBombSpell)ModSpells.PUMPKIN_BOMB.get()).getDamageSource(this, (Entity)(owner != null ? owner : this)), this.initialDamage);
         }
      }

      ParticleOptions particle = ParticleTypes.EXPLOSION;
      if (this.isSoulFire()) {
         particle = (ParticleOptions)ModParticle.IGNIS_SOUL_EXPLODE.get();
      } else if (this.isInfernalFire()) {
         particle = ModParticles.infernalFireExplodeOr((SimpleParticleType)ModParticle.IGNIS_EXPLODE.get());
      } else {
         particle = (ParticleOptions)ModParticle.IGNIS_EXPLODE.get();
      }

      MagicManager.spawnParticles(this.level(), particle, this.getX(), this.getY(), this.getZ(), 1, 0.0, 0.0, 0.0, 0.0, true);
   }

   private void spawnCloud() {
      PumpkinZoneEntity zone = new PumpkinZoneEntity(this.level(), this.getOwner() instanceof LivingEntity ? (LivingEntity)this.getOwner() : null);
      zone.setPos(this.getX(), this.getY(), this.getZ());
      zone.setDamage(this.aoeDamage);
      zone.setRadius(this.radius);
      zone.setAppliedEffectDuration(this.effectDuration);
      zone.setIsSoulFire(this.isSoulFire());
      zone.setIsInfernalFire(this.isInfernalFire());
      this.level().addFreshEntity(zone);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setIsSoulFire(compound.getBoolean("IsSoulFire"));
      this.setIsInfernalFire(compound.getBoolean("IsInfernalFire"));
      if (compound.contains("EffectDuration")) {
         this.effectDuration = compound.getInt("EffectDuration");
      }
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("IsSoulFire", this.isSoulFire());
      compound.putBoolean("IsInfernalFire", this.isInfernalFire());
      compound.putInt("EffectDuration", this.effectDuration);
   }
}
