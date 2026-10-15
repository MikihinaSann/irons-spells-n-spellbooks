package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModMobEffects;
import com.somake.somakespells.registries.ModParticles;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.fire.PumpkinBombSpell;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class PumpkinZoneEntity extends Projectile {
   private static final EntityDataAccessor<Boolean> DATA_IS_SOUL_FIRE = SynchedEntityData.defineId(PumpkinZoneEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_IS_INFERNAL_FIRE = SynchedEntityData.defineId(PumpkinZoneEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(PumpkinZoneEntity.class, EntityDataSerializers.FLOAT);
   private int duration = 100;
   private float radius = 3.0F;
   private float damage = 4.0F;
   private int appliedEffectDuration = 40;
   private int age;

   public void setAppliedEffectDuration(int duration) {
      this.appliedEffectDuration = duration;
   }

   public void setRadius(float radius) {
      if (!this.level().isClientSide) {
         this.entityData.set(DATA_RADIUS, radius);
      }

      this.radius = radius;
   }

   public float getRadius() {
      return (Float)this.entityData.get(DATA_RADIUS);
   }

   public void setDamage(float damage) {
      this.damage = damage;
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

   public PumpkinZoneEntity(EntityType<? extends Projectile> entityType, Level level) {
      super(entityType, level);
   }

   public PumpkinZoneEntity(Level level, LivingEntity owner) {
      super((EntityType)ModEntityTypes.PUMPKIN_ZONE.get(), level);
      this.setOwner(owner);
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_IS_SOUL_FIRE, false);
      builder.define(DATA_IS_INFERNAL_FIRE, false);
      builder.define(DATA_RADIUS, 3.0F);
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide) {
         this.radius = this.getRadius();
         ParticleOptions particleType = (ParticleOptions)ModParticles.FIRE.get();
         if (this.isSoulFire()) {
            particleType = (ParticleOptions)ModParticles.SOUL_FIRE.get();
         } else if (this.isInfernalFire()) {
            particleType = (ParticleOptions)ModParticles.SOUL_FIRE.get();
         }

         int particles = 60;

         for (int i = 0; i < particles; i++) {
            float angle = (float)((Math.PI * 2) * i / particles);
            double x = this.getX() + this.radius * Math.cos(angle);
            double z = this.getZ() + this.radius * Math.sin(angle);
            this.level().addParticle(particleType, x, this.getY(), z, 0.0, 0.0, 0.0);
         }

         ParticleOptions var12 = ParticleTypes.FLAME;
         if (this.isSoulFire()) {
            var12 = ParticleTypes.SOUL_FIRE_FLAME;
         } else if (this.isInfernalFire()) {
            var12 = ModParticles.infernalFireFlameOr((SimpleParticleType)ModParticles.FIRE.get());
         }

         for (int i = 0; i < 5; i++) {
            double r = this.radius * Math.sqrt(this.random.nextDouble());
            double theta = this.random.nextDouble() * 2.0 * Math.PI;
            double x = this.getX() + r * Math.cos(theta);
            double z = this.getZ() + r * Math.sin(theta);
            this.level().addParticle(var12, x, this.getY(), z, 0.0, 0.05, 0.0);
         }
      } else {
         if (this.age++ >= this.duration) {
            this.discard();
            return;
         }

         if (this.age % 5 == 0) {
            AABB area = this.getBoundingBox().inflate(0.0, 2.0, 0.0);
            List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area);
            Entity owner = this.getOwner();

            for (LivingEntity target : targets) {
               if (target != owner && (owner == null || !target.isAlliedTo(owner)) && target.distanceToSqr(this) <= this.radius * this.radius) {
                  MobEffect effect = (MobEffect)ModMobEffects.BURNING.get();
                  if (this.isInfernalFire()) {
                     MobEffect infernal = (MobEffect)BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.parse("born_in_chaos_v1:infernal_flame"));
                     if (infernal != null) {
                        effect = infernal;
                     }
                  } else if (this.isSoulFire()) {
                     effect = (MobEffect)ModMobEffects.SOUL_FIRE.get();
                  }

                  target.addEffect(new MobEffectInstance(Holder.direct(effect), this.appliedEffectDuration, 0, true, true, true));
                  target.hurt(((PumpkinBombSpell)ModSpells.PUMPKIN_BOMB.get()).getDamageSource(this, (Entity)(owner != null ? owner : this)), this.damage);
               }
            }
         }
      }
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      this.age = compound.getInt("Age");
      this.setIsSoulFire(compound.getBoolean("IsSoulFire"));
      this.setIsInfernalFire(compound.getBoolean("IsInfernalFire"));
      if (compound.contains("Radius")) {
         this.setRadius(compound.getFloat("Radius"));
      }

      if (compound.contains("AppliedEffectDuration")) {
         this.appliedEffectDuration = compound.getInt("AppliedEffectDuration");
      }
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      compound.putInt("Age", this.age);
      compound.putBoolean("IsSoulFire", this.isSoulFire());
      compound.putBoolean("IsInfernalFire", this.isInfernalFire());
      compound.putFloat("Radius", this.radius);
      compound.putInt("AppliedEffectDuration", this.appliedEffectDuration);
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return super.getAddEntityPacket(serverEntity);
   }
}
