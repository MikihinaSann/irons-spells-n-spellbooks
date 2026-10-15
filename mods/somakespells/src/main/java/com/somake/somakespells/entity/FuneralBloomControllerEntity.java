package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModParticles;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FuneralBloomControllerEntity extends Projectile implements AntiMagicSusceptible {
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(FuneralBloomControllerEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> DATA_DURATION = SynchedEntityData.defineId(FuneralBloomControllerEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_WAVE_INTERVAL = SynchedEntityData.defineId(
      FuneralBloomControllerEntity.class, EntityDataSerializers.INT
   );
   public static final int WARMUP_TICKS = 24;
   public static final int COLLAPSE_TICKS = 22;
   public static final float SKY_HEIGHT = 10.5F;
   private float impactDamage;
   private int witherDuration = 80;
   private int witherAmplifier;
   private int telegraphTicks = 12;
   private int maxTargets = 8;
   private int waveIndex;
   private final Set<UUID> activeTargetIds = new HashSet<>();

   public FuneralBloomControllerEntity(EntityType<? extends Projectile> type, Level level) {
      super(type, level);
      this.noPhysics = true;
      this.setNoGravity(true);
   }

   public FuneralBloomControllerEntity(
      Level level,
      LivingEntity owner,
      float radius,
      int duration,
      int waveInterval,
      int telegraphTicks,
      int maxTargets,
      float impactDamage,
      int witherDuration,
      int witherAmplifier
   ) {
      this((EntityType<? extends Projectile>)ModEntityTypes.FUNERAL_BLOOM_CONTROLLER.get(), level);
      this.setOwner(owner);
      this.setRadius(radius);
      this.setDuration(duration);
      this.setWaveInterval(waveInterval);
      this.telegraphTicks = Math.max(8, telegraphTicks);
      this.maxTargets = Math.max(1, maxTargets);
      this.impactDamage = impactDamage;
      this.witherDuration = Math.max(30, witherDuration);
      this.witherAmplifier = Math.max(0, witherAmplifier);
      this.setPos(owner.getX(), owner.getY(), owner.getZ());
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_RADIUS, 10.0F);
      builder.define(DATA_DURATION, 200);
      builder.define(DATA_WAVE_INTERVAL, 16);
   }

   public float getRadius() {
      return (Float)this.entityData.get(DATA_RADIUS);
   }

   public void setRadius(float radius) {
      this.entityData.set(DATA_RADIUS, Mth.clamp(radius, 6.0F, 18.0F));
   }

   public int getDuration() {
      return (Integer)this.entityData.get(DATA_DURATION);
   }

   public void setDuration(int duration) {
      this.entityData.set(DATA_DURATION, Math.max(66, duration));
   }

   public int getWaveInterval() {
      return (Integer)this.entityData.get(DATA_WAVE_INTERVAL);
   }

   public void setWaveInterval(int interval) {
      this.entityData.set(DATA_WAVE_INTERVAL, Math.max(10, interval));
   }

   public float getWarmupProgress(float partialTick) {
      return smoothstep(Mth.clamp((this.tickCount + partialTick) / 24.0F, 0.0F, 1.0F));
   }

   public float getCollapseProgress(float partialTick) {
      float collapseStart = this.getDuration() - 22;
      return smoothstep(Mth.clamp((this.tickCount + partialTick - collapseStart) / 22.0F, 0.0F, 1.0F));
   }

   public float getWavePulse(float partialTick) {
      float activeAge = this.tickCount + partialTick - 24.0F;
      if (!(activeAge < 0.0F) && !(this.getCollapseProgress(partialTick) > 0.0F)) {
         float phase = Mth.positiveModulo(activeAge, this.getWaveInterval()) / this.getWaveInterval();
         float impulse = 1.0F - Mth.clamp(phase / 0.55F, 0.0F, 1.0F);
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
         this.pruneTargets(serverLevel);
         if (this.tickCount == 1) {
            serverLevel.playSound(null, this.getX(), this.getY() + 10.5, this.getZ(), SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 0.85F, 1.55F);
            serverLevel.sendParticles(
               new BlastwaveParticleOptions(0.38F, 0.025F, 0.07F, this.getRadius() * 0.82F),
               this.getX(),
               this.getY() + 10.5,
               this.getZ(),
               1,
               0.0,
               0.0,
               0.0,
               0.0
            );
         }

         if (this.tickCount <= 24) {
            this.spawnFormationParticles(serverLevel);
         } else if (this.tickCount < this.getDuration() - 22) {
            int activeAge = this.tickCount - 24;
            int lastLaunchTick = this.getDuration() - 22 - this.telegraphTicks - 4 - 11;
            if (this.tickCount <= lastLaunchTick && (activeAge - 1) % this.getWaveInterval() == 0) {
               this.launchWave(serverLevel);
            }

            if ((activeAge & 1) == 0) {
               this.spawnAmbientPetals(serverLevel);
            }
         } else if (this.tickCount < this.getDuration()) {
            this.spawnRetreatParticles(serverLevel);
         } else {
            this.cancelOwnedBuds(serverLevel);
            serverLevel.sendParticles(
               new BlastwaveParticleOptions(0.16F, 0.01F, 0.035F, this.getRadius() * 0.52F),
               this.getX(),
               this.getY() + 6.51F,
               this.getZ(),
               1,
               0.0,
               0.0,
               0.0,
               0.0
            );
            this.discard();
         }
      }
   }

   private void launchWave(ServerLevel level) {
      LivingEntity owner = this.getOwner() instanceof LivingEntity living ? living : null;
      AABB area = this.getBoundingBox().inflate(this.getRadius(), 10.5, this.getRadius());
      List<LivingEntity> targets = new ArrayList<>(
         level.getEntitiesOfClass(LivingEntity.class, area, targetx -> this.isValidTarget(owner, targetx) && !this.activeTargetIds.contains(targetx.getUUID()))
      );
      targets.sort(Comparator.comparingDouble(targetx -> targetx.distanceToSqr(this)));
      if (targets.size() > this.maxTargets) {
         targets = targets.subList(0, this.maxTargets);
      }

      int launched = 0;

      for (LivingEntity target : targets) {
         Vec3 impact = this.predictedImpact(level, target);
         float delay = this.telegraphTicks + launched % 3 * 2.0F;
         FuneralBloomBudEntity bud = new FuneralBloomBudEntity(
            level, this, target, impact, Mth.ceil(delay), this.impactDamage, this.witherDuration, this.witherAmplifier
         );
         double phase = (float) (Math.PI * 2) * (launched + this.waveIndex * 0.62) / Math.max(1, targets.size());
         double spawnRadius = Math.min(this.getRadius() * 0.55, 2.8 + launched * 0.34);
         bud.setPos(
            this.getX() + Math.cos(phase) * spawnRadius, this.getY() + 10.5 + 0.35 + Math.sin(phase * 2.0) * 0.35, this.getZ() + Math.sin(phase) * spawnRadius
         );
         level.addFreshEntity(bud);
         this.activeTargetIds.add(target.getUUID());
         launched++;
      }

      this.waveIndex++;
      if (launched > 0) {
         level.playSound(
            null,
            this.getX(),
            this.getY() + 10.5,
            this.getZ(),
            SoundEvents.CHORUS_FLOWER_GROW,
            SoundSource.PLAYERS,
            0.75F,
            0.62F + level.random.nextFloat() * 0.08F
         );
      }
   }

   private boolean isValidTarget(LivingEntity owner, LivingEntity target) {
      if (!target.isAlive() || target.isSpectator() || target == owner) {
         return false;
      } else if (owner != null && target.isAlliedTo(owner)) {
         return false;
      } else {
         double horizontal = target.distanceToSqr(this.getX(), target.getY(), this.getZ());
         return horizontal <= this.getRadius() * this.getRadius();
      }
   }

   private Vec3 predictedImpact(ServerLevel level, LivingEntity target) {
      Vec3 velocity = target.getDeltaMovement();
      Vec3 predicted = target.position().add(velocity.x * 5.0, 0.0, velocity.z * 5.0);
      return groundPosition(level, predicted);
   }

   public static Vec3 groundPosition(Level level, Vec3 position) {
      int x = Mth.floor(position.x);
      int z = Mth.floor(position.z);
      int startY = Mth.floor(position.y) + 3;
      int minY = Math.max(level.getMinBuildHeight() + 1, startY - 14);

      for (int y = startY; y >= minY; y--) {
         BlockPos blockPos = new BlockPos(x, y, z);
         if (!level.getBlockState(blockPos).getCollisionShape(level, blockPos).isEmpty()) {
            return new Vec3(position.x, y + 1.02, position.z);
         }
      }

      return position;
   }

   private void spawnFormationParticles(ServerLevel level) {
      float progress = this.getWarmupProgress(0.0F);
      int points = Math.max(8, Mth.ceil(22.0F * progress));

      for (int i = 0; i < points; i++) {
         double angle = (float) (Math.PI * 2) * i / points + this.tickCount * 0.12;
         double radius = this.getRadius() * (0.25 + progress * 0.52);
         double x = this.getX() + Math.cos(angle) * radius;
         double z = this.getZ() + Math.sin(angle) * radius;
         double y = this.getY() + progress * 10.5F;
         level.sendParticles(
            (SimpleParticleType)ModParticles.WITHERED_ROSE_PETAL.get(), x, y, z, 1, -Math.cos(angle) * 0.025, 0.065, -Math.sin(angle) * 0.025, 0.0
         );
      }
   }

   private void spawnAmbientPetals(ServerLevel level) {
      int count = Math.max(3, Mth.ceil(this.getRadius() * 0.42F));

      for (int i = 0; i < count; i++) {
         double angle = level.random.nextDouble() * (float) (Math.PI * 2);
         double radius = Math.sqrt(level.random.nextDouble()) * this.getRadius() * 0.78;
         level.sendParticles(
            (SimpleParticleType)ModParticles.WITHERED_ROSE_PETAL.get(),
            this.getX() + Math.cos(angle) * radius,
            this.getY() + 10.5 - 0.2 + level.random.nextDouble() * 0.6,
            this.getZ() + Math.sin(angle) * radius,
            1,
            Math.cos(angle) * 0.02,
            -0.055,
            Math.sin(angle) * 0.02,
            0.0
         );
      }
   }

   private void spawnRetreatParticles(ServerLevel level) {
      float collapse = this.getCollapseProgress(0.0F);
      int count = Math.max(1, Mth.ceil((1.0F - collapse) * 7.0F));

      for (int i = 0; i < count; i++) {
         double angle = level.random.nextDouble() * (float) (Math.PI * 2) + this.tickCount * 0.18;
         double radius = this.getRadius() * (1.0 - collapse) * (0.25 + level.random.nextDouble() * 0.45);
         level.sendParticles(
            (SimpleParticleType)ModParticles.WITHERED_ROSE_PETAL.get(),
            this.getX() + Math.cos(angle) * radius,
            this.getY() + 10.5 * (1.0 - collapse),
            this.getZ() + Math.sin(angle) * radius,
            1,
            -Math.cos(angle) * 0.03,
            0.025,
            -Math.sin(angle) * 0.03,
            0.0
         );
      }
   }

   private void pruneTargets(ServerLevel level) {
      Set<UUID> targetsWithActiveBuds = new HashSet<>();

      for (FuneralBloomBudEntity bud : level.getEntities(
         (EntityTypeTest<Entity, FuneralBloomBudEntity>)ModEntityTypes.FUNERAL_BLOOM_BUD.get(),
         this.getBoundingBox().inflate(this.getRadius() + 8.0, 18.5, this.getRadius() + 8.0),
         candidate -> candidate.getControllerId().equals(this.getUUID()) && !candidate.isRemoved()
      )) {
         targetsWithActiveBuds.add(bud.getTargetUuid());
      }

      this.activeTargetIds.retainAll(targetsWithActiveBuds);
   }

   private void cancelOwnedBuds(ServerLevel level) {
      for (FuneralBloomBudEntity bud : level.getEntities(
         (EntityTypeTest<Entity, FuneralBloomBudEntity>)ModEntityTypes.FUNERAL_BLOOM_BUD.get(),
         this.getBoundingBox().inflate(this.getRadius() + 10.0, 20.5, this.getRadius() + 10.0),
         candidate -> candidate.getControllerId().equals(this.getUUID())
      )) {
         bud.beginRetreat();
      }
   }

   public void onAntiMagic(MagicData magicData) {
      if (this.level() instanceof ServerLevel level) {
         this.cancelOwnedBuds(level);
      }

      this.discard();
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
      this.setRadius(tag.getFloat("Radius"));
      this.setDuration(tag.getInt("Duration"));
      this.setWaveInterval(tag.getInt("WaveInterval"));
      this.telegraphTicks = Math.max(8, tag.getInt("TelegraphTicks"));
      this.maxTargets = Math.max(1, tag.getInt("MaxTargets"));
      this.impactDamage = tag.getFloat("ImpactDamage");
      this.witherDuration = Math.max(30, tag.getInt("WitherDuration"));
      this.witherAmplifier = Math.max(0, tag.getInt("WitherAmplifier"));
      this.waveIndex = Math.max(0, tag.getInt("WaveIndex"));
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
      tag.putFloat("Radius", this.getRadius());
      tag.putInt("Duration", this.getDuration());
      tag.putInt("WaveInterval", this.getWaveInterval());
      tag.putInt("TelegraphTicks", this.telegraphTicks);
      tag.putInt("MaxTargets", this.maxTargets);
      tag.putFloat("ImpactDamage", this.impactDamage);
      tag.putInt("WitherDuration", this.witherDuration);
      tag.putInt("WitherAmplifier", this.witherAmplifier);
      tag.putInt("WaveIndex", this.waveIndex);
   }
}
