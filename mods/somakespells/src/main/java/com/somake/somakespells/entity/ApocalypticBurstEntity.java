package com.somake.somakespells.entity;

import com.github.L_Ender.cataclysm.init.ModParticle;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModParticles;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.fire.ApocalypticBurstSpell;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
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
import net.minecraft.world.phys.AABB;

public class ApocalypticBurstEntity extends Projectile implements AntiMagicSusceptible {
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(ApocalypticBurstEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DATA_MAX_RADIUS = SynchedEntityData.defineId(ApocalypticBurstEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> DATA_IS_SOUL_FIRE = SynchedEntityData.defineId(ApocalypticBurstEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_IS_INFERNAL_FIRE = SynchedEntityData.defineId(
      ApocalypticBurstEntity.class, EntityDataSerializers.BOOLEAN
   );
   private float maxRadius = 5.0F;
   private int expandDuration = 5;
   private int age;
   private float damage;

   public void setMaxRadius(float maxRadius) {
      this.maxRadius = maxRadius;
      this.entityData.set(DATA_MAX_RADIUS, maxRadius);
   }

   public float getMaxRadius() {
      return (Float)this.entityData.get(DATA_MAX_RADIUS);
   }

   public ApocalypticBurstEntity(EntityType<? extends Projectile> entityType, Level level) {
      super(entityType, level);
   }

   public ApocalypticBurstEntity(Level level, LivingEntity owner, float damage) {
      this((EntityType<? extends Projectile>)ModEntityTypes.APOCALYPTIC_BURST.get(), level);
      this.setOwner(owner);
      this.damage = damage;
      this.setPos(owner.position());
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_RADIUS, 0.0F);
      builder.define(DATA_MAX_RADIUS, 5.0F);
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
      float progress = smoothAge / this.expandDuration;
      if (progress > 1.0F) {
         progress = 1.0F;
      }

      return this.maxRadius * progress;
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide) {
         this.maxRadius = this.getMaxRadius();
      }

      if (this.age++ >= this.expandDuration) {
         this.discard();
      } else {
         float progress = (float)this.age / this.expandDuration;
         float currentRadius = this.maxRadius * progress;
         if (this.level().isClientSide) {
            int particleCount = (int)(currentRadius * 10.0F);

            for (int i = 0; i < particleCount; i++) {
               double angle = this.random.nextDouble() * Math.PI * 2.0;
               double r = currentRadius * (0.8 + 0.2 * this.random.nextDouble());
               double x = this.getX() + Math.cos(angle) * r;
               double z = this.getZ() + Math.sin(angle) * r;
               if (this.isSoulFire()) {
                  this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, this.getY(), z, 0.0, 0.1, 0.0);
               } else if (this.isInfernalFire()) {
                  this.level().addParticle(ModParticles.infernalFireFlameOr((SimpleParticleType)ModParticles.FIRE.get()), x, this.getY(), z, 0.0, 0.1, 0.0);
               } else {
                  this.level().addParticle(ParticleTypes.FLAME, x, this.getY(), z, 0.0, 0.1, 0.0);
               }

               if (this.random.nextFloat() < 0.2F) {
                  if (this.isSoulFire()) {
                     this.level().addParticle((ParticleOptions)ModParticle.IGNIS_SOUL_EXPLODE.get(), x, this.getY(), z, 0.0, 0.1, 0.0);
                  } else if (this.isInfernalFire()) {
                     this.level()
                        .addParticle(ModParticles.infernalFireExplodeOr((SimpleParticleType)ModParticle.IGNIS_EXPLODE.get()), x, this.getY(), z, 0.0, 0.1, 0.0);
                  } else {
                     this.level().addParticle((ParticleOptions)ModParticle.IGNIS_EXPLODE.get(), x, this.getY(), z, 0.0, 0.1, 0.0);
                  }
               }
            }

            if (this.age == 1) {
               if (this.isSoulFire()) {
                  this.level().addParticle((ParticleOptions)ModParticle.IGNIS_SOUL_EXPLODE.get(), this.getX(), this.getY(), this.getZ(), 1.0, 0.0, 0.0);
               } else if (this.isInfernalFire()) {
                  this.level()
                     .addParticle(
                        ModParticles.infernalFireExplodeOr((SimpleParticleType)ModParticle.IGNIS_EXPLODE.get()),
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        1.0,
                        0.0,
                        0.0
                     );
               } else {
                  this.level().addParticle((ParticleOptions)ModParticle.IGNIS_EXPLODE.get(), this.getX(), this.getY(), this.getZ(), 1.0, 0.0, 0.0);
               }
            }
         } else {
            if (this.age == 1) {
               if (this.isSoulFire()) {
                  MagicManager.spawnParticles(
                     this.level(),
                     new BlastwaveParticleOptions(0.0F, 0.8F, 1.0F, this.maxRadius),
                     this.getX(),
                     this.getY(),
                     this.getZ(),
                     1,
                     0.0,
                     0.0,
                     0.0,
                     0.0,
                     true
                  );
               } else if (this.isInfernalFire()) {
                  MagicManager.spawnParticles(
                     this.level(),
                     new BlastwaveParticleOptions(0.47F, 0.7F, 0.14F, this.maxRadius),
                     this.getX(),
                     this.getY(),
                     this.getZ(),
                     1,
                     0.0,
                     0.0,
                     0.0,
                     0.0,
                     true
                  );
               } else {
                  MagicManager.spawnParticles(
                     this.level(),
                     new BlastwaveParticleOptions(1.0F, 0.4F, 0.0F, this.maxRadius),
                     this.getX(),
                     this.getY(),
                     this.getZ(),
                     1,
                     0.0,
                     0.0,
                     0.0,
                     0.0,
                     true
                  );
               }
            }

            float prevProgress = (float)(this.age - 1) / this.expandDuration;
            if (prevProgress < 0.0F) {
               prevProgress = 0.0F;
            }

            float prevRadius = this.maxRadius * prevProgress;
            AABB area = this.getBoundingBox().inflate(currentRadius);
            List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area);
            Entity owner = this.getOwner();

            for (LivingEntity target : targets) {
               if (target != owner && (owner == null || !target.isAlliedTo(owner))) {
                  double distSqr = target.distanceToSqr(this);
                  if (distSqr < currentRadius * currentRadius && distSqr >= prevRadius * prevRadius) {
                     target.hurt(
                        ((ApocalypticBurstSpell)ModSpells.APOCALYPTIC_BURST.get()).getDamageSource(this, (Entity)(owner != null ? owner : this)), this.damage
                     );
                     double dx = target.getX() - this.getX();
                     double dz = target.getZ() - this.getZ();
                     double dist = Math.sqrt(dx * dx + dz * dz);
                     if (dist > 0.0) {
                        target.push(dx / dist * 2.0, 0.8, dz / dist * 2.0);
                     }
                  }
               }
            }
         }

         this.setRadius(currentRadius);
      }
   }

   public void onAntiMagic(MagicData playerMagicData) {
      this.discard();
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      this.age = compound.getInt("Age");
      this.damage = compound.getFloat("Damage");
      this.maxRadius = compound.getFloat("MaxRadius");
      if (this.maxRadius <= 0.0F) {
         this.maxRadius = 5.0F;
      }

      this.setIsSoulFire(compound.getBoolean("IsSoulFire"));
      this.setIsInfernalFire(compound.getBoolean("IsInfernalFire"));
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      compound.putInt("Age", this.age);
      compound.putFloat("Damage", this.damage);
      compound.putFloat("MaxRadius", this.maxRadius);
      compound.putBoolean("IsSoulFire", this.isSoulFire());
      compound.putBoolean("IsInfernalFire", this.isInfernalFire());
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return super.getAddEntityPacket(serverEntity);
   }
}
