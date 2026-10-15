package com.somake.somakespells.entity;

import com.somake.somakespells.particle.WitheredRoseVortexParticleOptions;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModParticles;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.blood.WitheredRoseVortexSpell;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WitheredRoseVortexEntity extends Projectile implements AntiMagicSusceptible {
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(WitheredRoseVortexEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> DATA_TOTAL_LIFETIME = SynchedEntityData.defineId(WitheredRoseVortexEntity.class, EntityDataSerializers.INT);
   public static final int WARMUP_TICKS = 14;
   public static final int PULSE_INTERVAL = 10;
   public static final int COLLAPSE_TICKS = 14;
   public static final int PETAL_RETREAT_TICKS = 16;
   public static final float VORTEX_HEIGHT = 6.4F;
   private static final int MIN_LIFETIME = 54;
   private float damagePerPulse;
   private int witherDuration = 60;
   private int witherAmplifier;

   public WitheredRoseVortexEntity(EntityType<? extends Projectile> entityType, Level level) {
      super(entityType, level);
      this.noPhysics = true;
      this.setNoGravity(true);
   }

   public WitheredRoseVortexEntity(
      Level level, LivingEntity owner, float damagePerPulse, float radius, int totalLifetime, int witherDuration, int witherAmplifier
   ) {
      this((EntityType<? extends Projectile>)ModEntityTypes.WITHERED_ROSE_VORTEX.get(), level);
      this.setOwner(owner);
      this.damagePerPulse = damagePerPulse;
      this.setRadius(radius);
      this.setTotalLifetime(totalLifetime);
      this.witherDuration = Math.max(20, witherDuration);
      this.witherAmplifier = Math.max(0, witherAmplifier);
      this.setPos(owner.getX(), owner.getY(), owner.getZ());
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_RADIUS, 4.0F);
      builder.define(DATA_TOTAL_LIFETIME, 160);
   }

   public float getRadius() {
      return (Float)this.entityData.get(DATA_RADIUS);
   }

   public void setRadius(float radius) {
      this.entityData.set(DATA_RADIUS, Mth.clamp(radius, 2.5F, 8.0F));
   }

   public int getTotalLifetime() {
      return (Integer)this.entityData.get(DATA_TOTAL_LIFETIME);
   }

   public void setTotalLifetime(int totalLifetime) {
      this.entityData.set(DATA_TOTAL_LIFETIME, Math.max(54, totalLifetime));
   }

   public float getWarmupProgress(float partialTick) {
      return smoothstep(Mth.clamp((this.tickCount + partialTick) / 14.0F, 0.0F, 1.0F));
   }

   public float getCollapseProgress(float partialTick) {
      float collapseStart = this.getTotalLifetime() - 14;
      return smoothstep(Mth.clamp((this.tickCount + partialTick - collapseStart) / 14.0F, 0.0F, 1.0F));
   }

   public float getPetalRetreatProgress(float partialTick) {
      return smoothstep(Mth.clamp((this.tickCount + partialTick - this.getTotalLifetime()) / 16.0F, 0.0F, 1.0F));
   }

   public float getPulseStrength(float partialTick) {
      float activeAge = this.tickCount + partialTick - 14.0F;
      if (!(activeAge < 10.0F) && !(this.getCollapseProgress(partialTick) > 0.0F)) {
         float phase = Mth.positiveModulo(activeAge, 10.0F) / 10.0F;
         float impulse = 1.0F - Mth.clamp(phase / 0.62F, 0.0F, 1.0F);
         return impulse * impulse;
      } else {
         return 0.0F;
      }
   }

   private static float smoothstep(float value) {
      return value * value * (3.0F - 2.0F * value);
   }

   public void tick() {
      super.tick();
      if (this.level() instanceof ServerLevel serverLevel) {
         if (this.tickCount == 1) {
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITHER_AMBIENT, SoundSource.PLAYERS, 0.85F, 1.35F);
            serverLevel.sendParticles(
               new BlastwaveParticleOptions(0.45F, 0.015F, 0.06F, this.getRadius()), this.getX(), this.getY() + 0.08, this.getZ(), 1, 0.0, 0.0, 0.0, 0.0
            );
         }

         if (this.tickCount <= 14) {
            this.spawnTelegraph(serverLevel);
         } else if (this.tickCount > 15 && this.tickCount < this.getTotalLifetime()) {
            this.spawnActiveVortex(serverLevel);
            if (this.tickCount < this.getTotalLifetime() - 14 && (this.tickCount - 14) % 10 == 0) {
               this.pulseDamage(serverLevel);
            }
         }

         if (this.tickCount >= this.getTotalLifetime() + 16) {
            this.spawnDissipation(serverLevel);
            this.discard();
         }
      }
   }

   private void spawnTelegraph(ServerLevel level) {
      float radius = this.getRadius();
      int points = Math.max(18, Mth.ceil(radius * 10.0F));
      double spin = this.tickCount * 0.13;

      for (int i = 0; i < points; i++) {
         double angle = (float) (Math.PI * 2) * i / points + spin;
         double x = this.getX() + Math.cos(angle) * radius;
         double z = this.getZ() + Math.sin(angle) * radius;
         level.sendParticles((SimpleParticleType)ModParticles.WITHERED_ROSE_PETAL.get(), x, this.getY() + 0.1, z, 1, 0.01, 0.035, 0.01, 0.0);
         if ((i + this.tickCount) % 3 == 0) {
            this.emitPetal(level, radius * 0.92F, 0.1F, 13.0F, 0.045F, 0.12F, 0.35F, 0.65F, i % 3, true, 1);
         }
      }
   }

   private void spawnActiveVortex(ServerLevel level) {
      float radius = this.getRadius();
      int activeAge = this.tickCount - 14;
      float bloom = Mth.clamp(activeAge / 12.0F, 0.0F, 1.0F) * Mth.clamp((this.getTotalLifetime() - this.tickCount) / 14.0F, 0.0F, 1.0F);
      this.emitHelixBand(level, radius * 1.08F, 0.12F, 17.0F, 0.065F, 0.17F, 3, 1.2F, bloom);
      this.emitHelixBand(level, radius * 0.93F, 0.72F, -15.0F, 0.07F, 0.16F, 3, 1.15F, bloom);
      this.emitHelixBand(level, radius * 0.76F, 1.55F, 20.0F, 0.078F, 0.15F, 3, 1.1F, bloom);
      this.emitHelixBand(level, radius * 0.56F, 2.55F, -19.0F, 0.085F, 0.14F, 3, 1.05F, bloom);
      this.emitHelixBand(level, radius * 0.36F, 3.75F, 24.0F, 0.092F, 0.13F, 3, 1.0F, bloom);
      this.emitHelixBand(level, radius * 0.24F, 4.85F, -26.0F, 0.098F, 0.12F, 3, 0.96F, bloom);
      this.spawnPetalBase(level, radius, activeAge);
      this.spawnCrimsonCore(level, radius, activeAge);
      this.spawnRoseCrown(level, radius, activeAge);
   }

   private void emitHelixBand(
      ServerLevel level, float radius, float height, float angularVelocity, float ascent, float scale, int count, float lifeScale, float bloom
   ) {
      if (!(bloom <= 0.02F)) {
         int scaledCount = Math.max(1, Mth.ceil(count * bloom));

         for (int strand = 0; strand < 3; strand++) {
            float bloomedRadius = radius * (0.35F + 0.65F * bloom);
            float bloomedHeight = height * (0.25F + 0.75F * bloom);
            float bloomedScale = scale * (0.72F + 0.28F * bloom);
            this.emitPetal(level, bloomedRadius, bloomedHeight, angularVelocity, ascent, bloomedScale, 0.72F, lifeScale, strand, false, scaledCount);
            this.emitPetal(
               level,
               bloomedRadius * 0.94F,
               bloomedHeight + 0.12F,
               -angularVelocity * 1.08F,
               ascent * 1.05F,
               bloomedScale * 0.86F,
               0.64F,
               lifeScale * 0.9F,
               (strand + 1) % 3,
               true,
               Math.max(1, scaledCount / 2)
            );
         }
      }
   }

   private void emitPetal(
      ServerLevel level,
      float radius,
      float height,
      float angularVelocity,
      float ascent,
      float scale,
      float jitter,
      float lifeScale,
      int strand,
      boolean crimson,
      int count
   ) {
      level.sendParticles(
         new WitheredRoseVortexParticleOptions(radius, height, this.getId(), angularVelocity, ascent, scale, jitter, lifeScale, strand, crimson),
         this.getX(),
         this.getY(),
         this.getZ(),
         count,
         0.0,
         0.0,
         0.0,
         0.0
      );
   }

   private void spawnPetalBase(ServerLevel level, float radius, int activeAge) {
      if (activeAge % 2 == 0) {
         for (int strand = 0; strand < 3; strand++) {
            this.emitPetal(level, radius * 0.86F, 0.06F, 10.0F, 0.038F, 0.13F, 0.8F, 0.72F, strand, false, 2);
            this.emitPetal(level, radius * 0.68F, 0.18F, -12.0F, 0.042F, 0.11F, 0.7F, 0.68F, strand, true, 1);
         }
      }
   }

   private void spawnCrimsonCore(ServerLevel level, float radius, int activeAge) {
      if (activeAge % 2 == 0) {
         this.emitPetal(level, radius * 0.24F, 0.65F, 29.0F, 0.11F, 0.095F, 0.45F, 0.82F, activeAge % 3, false, 2);
         this.emitPetal(level, radius * 0.17F, 1.15F, -34.0F, 0.105F, 0.085F, 0.38F, 0.78F, (activeAge + 1) % 3, true, 1);
      }

      if (activeAge % 4 == 0) {
         MagicManager.spawnParticles(
            level, ParticleHelper.BLOOD, this.getX(), this.getY() + 1.2, this.getZ(), 3, radius * 0.07, 0.65, radius * 0.07, 0.025, true
         );
      }
   }

   private void spawnRoseCrown(ServerLevel level, float radius, int activeAge) {
      if (activeAge % 2 == 0) {
         int points = 10;
         double phase = -activeAge * 0.13;
         double crownRadius = Math.max(0.8, radius * 0.34);

         for (int i = 0; i < points; i++) {
            double angle = (float) (Math.PI * 2) * i / points + phase;
            double wave = Math.sin(activeAge * 0.18 + i) * 0.18;
            level.sendParticles(
               (SimpleParticleType)ModParticles.WITHERED_ROSE_PETAL.get(),
               this.getX() + Math.cos(angle) * crownRadius,
               this.getY() + 6.4F + wave,
               this.getZ() + Math.sin(angle) * crownRadius,
               1,
               Math.cos(angle) * 0.035,
               0.025,
               Math.sin(angle) * 0.035,
               0.0
            );
         }
      }
   }

   private void pulseDamage(ServerLevel level) {
      Entity owner = this.getOwner();
      float radius = this.getRadius();
      AABB area = this.getBoundingBox().inflate(radius, 4.6080003F, radius);

      for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area)) {
         if (target != owner && (owner == null || !target.isAlliedTo(owner))) {
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            if (!(dx * dx + dz * dz > radius * radius * 1.12F)) {
               if (DamageSources.applyDamage(
                  target,
                  this.damagePerPulse,
                  ((WitheredRoseVortexSpell)ModSpells.WITHERED_ROSE_VORTEX.get()).getDamageSource(this, (Entity)(owner != null ? owner : this))
               )) {
                  target.addEffect(new MobEffectInstance(MobEffects.WITHER, this.witherDuration, this.witherAmplifier, false, true, true));
                  level.sendParticles(
                     (SimpleParticleType)ModParticles.WITHERED_ROSE_PETAL.get(),
                     target.getX(),
                     target.getY() + target.getBbHeight() * 0.55,
                     target.getZ(),
                     5,
                     target.getBbWidth() * 0.35,
                     0.35,
                     target.getBbWidth() * 0.35,
                     0.025
                  );
               }

               Vec3 pull = new Vec3(this.getX() - target.getX(), 0.0, this.getZ() - target.getZ());
               if (pull.lengthSqr() > 1.0E-4) {
                  double distance = Math.sqrt(pull.lengthSqr());
                  double strength = Mth.clamp(0.14 + distance * 0.018, 0.14, 0.29);
                  pull = pull.normalize().scale(strength);
                  target.push(pull.x, 0.2 + Math.min(0.16, distance * 0.016), pull.z);
                  target.hurtMarked = true;
               }
            }
         }
      }
   }

   private void spawnDissipation(ServerLevel level) {
      level.sendParticles(
         new BlastwaveParticleOptions(0.22F, 0.01F, 0.04F, this.getRadius() * 0.8F), this.getX(), this.getY() + 0.16, this.getZ(), 1, 0.0, 0.0, 0.0, 0.0
      );
      level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITHER_HURT, SoundSource.PLAYERS, 0.7F, 1.45F);
   }

   public void onAntiMagic(MagicData playerMagicData) {
      this.discard();
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
      this.damagePerPulse = tag.getFloat("DamagePerPulse");
      this.setTotalLifetime(tag.getInt("TotalLifetime"));
      this.witherDuration = Math.max(20, tag.getInt("WitherDuration"));
      this.witherAmplifier = Math.max(0, tag.getInt("WitherAmplifier"));
      if (tag.contains("Radius")) {
         this.setRadius(tag.getFloat("Radius"));
      }
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
      tag.putFloat("DamagePerPulse", this.damagePerPulse);
      tag.putInt("TotalLifetime", this.getTotalLifetime());
      tag.putInt("WitherDuration", this.witherDuration);
      tag.putInt("WitherAmplifier", this.witherAmplifier);
      tag.putFloat("Radius", this.getRadius());
   }
}
