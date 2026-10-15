package com.somake.somakespells.entity;

import com.github.L_Ender.cataclysm.entity.effect.Flame_Strike_Entity;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModParticles;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.fire.AbyssalBurnSpell;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class AbyssalStrikeEntity extends Flame_Strike_Entity implements AntiMagicSusceptible {
   private static final EntityDataAccessor<Float> DATA_CUSTOM_DAMAGE = SynchedEntityData.defineId(AbyssalStrikeEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> DATA_IS_SOUL_FIRE = SynchedEntityData.defineId(AbyssalStrikeEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_IS_INFERNAL_FIRE = SynchedEntityData.defineId(AbyssalStrikeEntity.class, EntityDataSerializers.BOOLEAN);
   private int maxAge = 35;
   private int age;
   private int explosionDelay = 30;

   public AbyssalStrikeEntity(EntityType<? extends Flame_Strike_Entity> entityType, Level level) {
      super(entityType, level);
   }

   public AbyssalStrikeEntity(Level level, LivingEntity owner, float damage) {
      super((EntityType)ModEntityTypes.ABYSSAL_STRIKE.get(), level);
      this.setOwner(owner);
      this.setCustomDamage(damage);
      this.setRadius(1.5F);
      this.setDuration(this.maxAge);
      this.setWaitTime(this.explosionDelay);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_CUSTOM_DAMAGE, 10.0F);
      builder.define(DATA_IS_SOUL_FIRE, false);
      builder.define(DATA_IS_INFERNAL_FIRE, false);
   }

   public void setCustomDamage(float damage) {
      this.entityData.set(DATA_CUSTOM_DAMAGE, damage);
   }

   public float getCustomDamage() {
      return (Float)this.entityData.get(DATA_CUSTOM_DAMAGE);
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
      this.baseTick();
      if (this.age++ >= this.maxAge) {
         this.discard();
      } else {
         if (this.level().isClientSide) {
            boolean waiting = this.age < this.explosionDelay;
            float radius = this.getRadius();
            if (this.age > 15 && this.age % 2 == 0) {
               double spread = Math.PI * 2;
               int arcLen = Mth.ceil(radius * spread);
               float f1 = waiting ? 0.2F : radius;

               for (int j = 0; j < arcLen; j++) {
                  float f2 = this.random.nextFloat() * (float) (Math.PI * 2);
                  double d0 = this.getX() + Mth.cos(f2) * f1 * 0.9;
                  double d2 = this.getY();
                  double d4 = this.getZ() + Mth.sin(f2) * f1 * 0.9;
                  ParticleOptions particle = ParticleTypes.FLAME;
                  if (this.isSoulFire()) {
                     particle = ParticleTypes.SOUL_FIRE_FLAME;
                  } else if (this.isInfernalFire()) {
                     particle = ModParticles.infernalFireFlameOr((SimpleParticleType)ModParticles.FIRE.get());
                  }

                  if (particle != null) {
                     this.level().addParticle(particle, d0, d2, d4, this.random.nextGaussian() * 0.07, 0.125 * radius + 0.4, this.random.nextGaussian() * 0.07);
                  }
               }
            }

            if (waiting && this.random.nextInt(24) == 0) {
               this.level()
                  .playLocalSound(
                     this.getX() + 0.5,
                     this.getY() + 0.5,
                     this.getZ() + 0.5,
                     SoundEvents.BLAZE_BURN,
                     this.getSoundSource(),
                     1.0F + this.random.nextFloat(),
                     this.random.nextFloat() * 0.7F + 0.3F,
                     false
                  );
            }

            if (this.age == this.explosionDelay) {
               this.level()
                  .playLocalSound(
                     this.getX(),
                     this.getY(),
                     this.getZ(),
                     (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(),
                     this.getSoundSource(),
                     4.0F,
                     (1.0F + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2F) * 0.7F,
                     false
                  );
               if (!this.isSoulFire() && !this.isInfernalFire()) {
                  this.level().addParticle((ParticleOptions)ModParticle.FLARE_EXPLODE.get(), this.getX(), this.getY() + 0.05F, this.getZ(), 0.1, 0.0, 0.0);
               }

               for (int i = 0; i < 5; i++) {
                  double particleX = this.getX() + (this.random.nextFloat() - 0.5F) * 4.0F;
                  double particleY = this.getY() + this.random.nextFloat();
                  double particleZ = this.getZ() + (this.random.nextFloat() - 0.5F) * 4.0F;
                  ParticleOptions explodeParticle = (ParticleOptions)ModParticle.IGNIS_EXPLODE.get();
                  if (this.isSoulFire()) {
                     explodeParticle = (ParticleOptions)ModParticle.IGNIS_SOUL_EXPLODE.get();
                  } else if (this.isInfernalFire()) {
                     explodeParticle = ModParticles.infernalFireExplodeOr((SimpleParticleType)ModParticle.IGNIS_EXPLODE.get());
                  }

                  this.level().addParticle(explodeParticle, particleX, particleY, particleZ, 1.4, 0.0, 0.0);
               }
            }
         } else if (this.age == this.explosionDelay) {
            float radiusx = 1.5F;
            this.level()
               .getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(radiusx, 2.0, radiusx))
               .forEach(
                  entity -> {
                     if (entity != this.getOwner() && entity.distanceToSqr(this) < radiusx * radiusx) {
                        Entity owner = this.getOwner();
                        DamageSources.applyDamage(
                           entity,
                           this.getCustomDamage(),
                           ((AbyssalBurnSpell)ModSpells.ABYSSAL_BURN.get()).getDamageSource(this, (Entity)(owner != null ? owner : this))
                        );
                     }
                  }
               );
         }

         if (!this.level().isClientSide) {
            this.setSee(true);
            this.setWaiting(this.age < this.explosionDelay);
         }
      }
   }

   public void onAntiMagic(MagicData playerMagicData) {
      this.discard();
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.age = compound.getInt("MyAge");
      this.setCustomDamage(compound.getFloat("CustomDamage"));
      this.setIsSoulFire(compound.getBoolean("IsSoulFire"));
      this.setIsInfernalFire(compound.getBoolean("IsInfernalFire"));
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("MyAge", this.age);
      compound.putFloat("CustomDamage", this.getCustomDamage());
      compound.putBoolean("IsSoulFire", this.isSoulFire());
      compound.putBoolean("IsInfernalFire", this.isInfernalFire());
   }
}
