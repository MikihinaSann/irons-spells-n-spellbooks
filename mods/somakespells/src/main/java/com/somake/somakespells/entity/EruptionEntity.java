package com.somake.somakespells.entity;

import com.github.L_Ender.cataclysm.init.ModParticle;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModParticles;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.fire.EruptionSpell;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.List;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class EruptionEntity extends Projectile implements AntiMagicSusceptible {
   private static final EntityDataAccessor<Boolean> DATA_IS_SOUL_FIRE = SynchedEntityData.defineId(EruptionEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_IS_INFERNAL_FIRE = SynchedEntityData.defineId(EruptionEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(EruptionEntity.class, EntityDataSerializers.FLOAT);
   private float damage;
   private int age;
   private int delay = 10;

   public EruptionEntity(EntityType<? extends Projectile> entityType, Level level) {
      super(entityType, level);
   }

   public EruptionEntity(Level level, LivingEntity owner, float damage) {
      super((EntityType)ModEntityTypes.ERUPTION.get(), level);
      this.setOwner(owner);
      this.damage = damage;
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_IS_SOUL_FIRE, false);
      builder.define(DATA_IS_INFERNAL_FIRE, false);
      builder.define(DATA_RADIUS, 3.0F);
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

   public void setDamage(float damage) {
      this.damage = damage;
   }

   public void setRadius(float radius) {
      this.entityData.set(DATA_RADIUS, radius);
   }

   public float getRadius() {
      return (Float)this.entityData.get(DATA_RADIUS);
   }

   public void tick() {
      super.tick();
      if (this.age < this.delay) {
         if (this.level().isClientSide) {
            float radius = this.getRadius();
            int particles = (int)(radius * 2.0F);

            for (int i = 0; i < particles; i++) {
               double angle = this.random.nextDouble() * Math.PI * 2.0;
               double r = radius * Math.sqrt(this.random.nextDouble());
               double x = this.getX() + r * Math.cos(angle);
               double z = this.getZ() + r * Math.sin(angle);
               ParticleOptions p = ParticleTypes.SMOKE;
               if (this.isSoulFire()) {
                  p = ParticleTypes.SOUL_FIRE_FLAME;
               } else if (this.isInfernalFire()) {
                  p = ModParticles.infernalFireFlameOr((SimpleParticleType)ModParticles.FIRE.get());
               } else {
                  p = ParticleTypes.FLAME;
               }

               this.level().addParticle(p, x, this.getY() + 0.1, z, 0.0, 0.05, 0.0);
            }
         }
      } else if (this.age == this.delay) {
         this.explode();
      } else if (this.age > this.delay + 20) {
         this.discard();
      }

      this.age++;
   }

   private void explode() {
      float radius = this.getRadius();
      if (!this.level().isClientSide) {
         AABB area = this.getBoundingBox().inflate(radius, 4.0, radius);
         List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area);
         Entity owner = this.getOwner();

         for (LivingEntity target : targets) {
            if (target != owner && (owner == null || !target.isAlliedTo(owner)) && target.distanceToSqr(this) <= radius * radius) {
               target.hurt(((EruptionSpell)ModSpells.ERUPTION.get()).getDamageSource(this, (Entity)(owner != null ? owner : this)), this.damage);
               target.push(0.0, 1.2, 0.0);
               target.hurtMarked = true;
            }
         }

         if (this.isSoulFire()) {
            MagicManager.spawnParticles(
               this.level(), new BlastwaveParticleOptions(0.0F, 0.8F, 1.0F, radius), this.getX(), this.getY(), this.getZ(), 1, 0.0, 0.0, 0.0, 0.0, true
            );
         } else if (this.isInfernalFire()) {
            MagicManager.spawnParticles(
               this.level(), new BlastwaveParticleOptions(0.47F, 0.7F, 0.14F, radius), this.getX(), this.getY(), this.getZ(), 1, 0.0, 0.0, 0.0, 0.0, true
            );
         } else {
            MagicManager.spawnParticles(
               this.level(), new BlastwaveParticleOptions(1.0F, 0.4F, 0.0F, radius), this.getX(), this.getY(), this.getZ(), 1, 0.0, 0.0, 0.0, 0.0, true
            );
         }

         this.level()
            .playSound(
               null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 2.0F, 0.5F + this.random.nextFloat() * 0.2F
            );
      }

      if (this.level().isClientSide) {
         ParticleOptions explosionParticle = (ParticleOptions)ModParticle.IGNIS_EXPLODE.get();
         if (this.isInfernalFire()) {
            explosionParticle = ModParticles.infernalFireExplodeOr((SimpleParticleType)ModParticle.IGNIS_EXPLODE.get());
         }

         if (this.isSoulFire()) {
            explosionParticle = (ParticleOptions)ModParticle.IGNIS_SOUL_EXPLODE.get();
         }

         this.level().addParticle(explosionParticle, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
         ParticleOptions jetParticle = (ParticleOptions)BuiltInRegistries.PARTICLE_TYPE.get(ResourceLocation.parse("cataclysm:flame_jet"));
         if (jetParticle == null) {
            jetParticle = ParticleTypes.FLAME;
         }

         if (this.isSoulFire()) {
            jetParticle = (ParticleOptions)ModParticles.SOUL_FIRE_FLAME_JET.get();
         }

         if (this.isInfernalFire()) {
            jetParticle = ModParticles.infernalFireFlameJetOr((SimpleParticleType)ModParticles.SOUL_FIRE_FLAME_JET.get());
         }

         int jetCount = (int)(radius * 2.0F) + 3;

         for (int i = 0; i < jetCount; i++) {
            double ox = (this.random.nextDouble() - 0.5) * radius;
            double oz = (this.random.nextDouble() - 0.5) * radius;
            this.level().addParticle(jetParticle, this.getX() + ox, this.getY() + 2.0, this.getZ() + oz, 0.0, 0.5 + this.random.nextDouble() * 0.5, 0.0);
         }

         ParticleOptions lavaParticle = ParticleTypes.LAVA;
         if (this.isSoulFire()) {
            lavaParticle = (ParticleOptions)ModParticles.SOUL_FIRE_LAVA.get();
         } else if (this.isInfernalFire()) {
            lavaParticle = ModParticles.infernalFireLavaOr((SimpleParticleType)ModParticles.SOUL_FIRE_LAVA.get());
         }

         int lavaCount = (int)(radius * 4.0F);

         for (int i = 0; i < lavaCount; i++) {
            double r = radius * Math.sqrt(this.random.nextDouble());
            double theta = this.random.nextDouble() * 2.0 * Math.PI;
            double ox = r * Math.cos(theta);
            double oz = r * Math.sin(theta);
            this.level()
               .addParticle(
                  lavaParticle,
                  this.getX() + ox,
                  this.getY() + 1.0,
                  this.getZ() + oz,
                  (this.random.nextDouble() - 0.5) * 0.5,
                  0.5,
                  (this.random.nextDouble() - 0.5) * 0.5
               );
         }
      }
   }

   public void onAntiMagic(MagicData playerMagicData) {
      this.discard();
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      this.damage = compound.getFloat("Damage");
      this.age = compound.getInt("Age");
      this.setIsSoulFire(compound.getBoolean("IsSoulFire"));
      this.setIsInfernalFire(compound.getBoolean("IsInfernalFire"));
      if (compound.contains("Radius")) {
         this.setRadius(compound.getFloat("Radius"));
      }
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      compound.putFloat("Damage", this.damage);
      compound.putInt("Age", this.age);
      compound.putBoolean("IsSoulFire", this.isSoulFire());
      compound.putBoolean("IsInfernalFire", this.isInfernalFire());
      compound.putFloat("Radius", this.getRadius());
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return super.getAddEntityPacket(serverEntity);
   }
}
