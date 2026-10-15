package com.somake.somakespells.entity;

import com.somake.somakespells.particle.FireVortexParticleOptions;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModParticles;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.fire.FirestormVortexSpell;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FirestormVortexEntity extends Projectile implements AntiMagicSusceptible {
   private static final EntityDataAccessor<Boolean> DATA_IS_SOUL_FIRE = SynchedEntityData.defineId(FirestormVortexEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_IS_INFERNAL_FIRE = SynchedEntityData.defineId(
      FirestormVortexEntity.class, EntityDataSerializers.BOOLEAN
   );
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(FirestormVortexEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(FirestormVortexEntity.class, EntityDataSerializers.INT);
   private static final int WARMUP_TICKS = 12;
   private static final int DEFAULT_TOTAL_LIFETIME = 140;
   private static final int MIN_TOTAL_LIFETIME = 32;
   public static final int VARIANT_VOLUME = 0;
   public static final int VARIANT_SILHOUETTE = 1;
   public static final int VARIANT_AGGRESSIVE = 2;
   public static final int VARIANT_PREMIUM = 3;
   private float damagePerPulse;
   private int totalLifetime = 140;

   public FirestormVortexEntity(EntityType<? extends Projectile> entityType, Level level) {
      super(entityType, level);
      this.noPhysics = true;
   }

   public FirestormVortexEntity(Level level, LivingEntity owner, float damagePerPulse, float radius, int totalLifetime) {
      this((EntityType<? extends Projectile>)ModEntityTypes.FIRESTORM_VORTEX.get(), level);
      this.setOwner(owner);
      this.damagePerPulse = damagePerPulse;
      this.setRadius(radius);
      this.totalLifetime = Math.max(32, totalLifetime);
      this.setPos(owner.getX(), owner.getY(), owner.getZ());
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_IS_SOUL_FIRE, false);
      builder.define(DATA_IS_INFERNAL_FIRE, false);
      builder.define(DATA_RADIUS, 3.5F);
      builder.define(DATA_VARIANT, 1);
   }

   public void setIsSoulFire(boolean isSoulFire) {
      this.entityData.set(DATA_IS_SOUL_FIRE, isSoulFire);
      if (isSoulFire) {
         this.entityData.set(DATA_IS_INFERNAL_FIRE, false);
      }
   }

   public boolean isSoulFire() {
      return (Boolean)this.entityData.get(DATA_IS_SOUL_FIRE);
   }

   public void setIsInfernalFire(boolean isInfernalFire) {
      this.entityData.set(DATA_IS_INFERNAL_FIRE, isInfernalFire);
      if (isInfernalFire) {
         this.entityData.set(DATA_IS_SOUL_FIRE, false);
      }
   }

   public boolean isInfernalFire() {
      return (Boolean)this.entityData.get(DATA_IS_INFERNAL_FIRE);
   }

   public void setRadius(float radius) {
      this.entityData.set(DATA_RADIUS, radius);
   }

   public float getRadius() {
      return (Float)this.entityData.get(DATA_RADIUS);
   }

   public void setVariant(int variant) {
      this.entityData.set(DATA_VARIANT, Mth.clamp(variant, 0, 3));
   }

   public int getVariant() {
      return (Integer)this.entityData.get(DATA_VARIANT);
   }

   public void tick() {
      super.tick();
      if (this.level() instanceof ServerLevel serverLevel) {
         if (this.tickCount == 1) {
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.15F, 0.7F);
         }

         if (this.tickCount <= 12) {
            this.spawnTelegraphParticles(serverLevel);
         } else {
            this.spawnActiveParticles(serverLevel);
            if ((this.tickCount - 12) % 8 == 0) {
               this.pulseDamage();
            }
         }

         if (this.tickCount >= this.totalLifetime) {
            this.discard();
         }
      }
   }

   private void spawnTelegraphParticles(ServerLevel serverLevel) {
      switch (this.getVariant()) {
         case 0:
            this.spawnVolumeTelegraph(serverLevel);
            break;
         case 1:
         default:
            this.spawnSilhouetteTelegraph(serverLevel);
            break;
         case 2:
            this.spawnAggressiveTelegraph(serverLevel);
            break;
         case 3:
            this.spawnPremiumTelegraph(serverLevel);
      }
   }

   private void spawnSilhouetteTelegraph(ServerLevel serverLevel) {
      float radius = this.getRadius();
      int ringPoints = Math.max(14, Mth.ceil(radius * 12.0F));
      double spin = this.tickCount * 0.11;
      ParticleOptions animParticle = this.getAnimatedFlameParticle();
      ParticleOptions sparkParticle = this.getSparkParticle();

      for (int i = 0; i < ringPoints; i++) {
         double angle = (Math.PI * 2) * i / ringPoints + spin;
         double x = this.getX() + Math.cos(angle) * radius;
         double z = this.getZ() + Math.sin(angle) * radius;
         serverLevel.sendParticles(animParticle, x, this.getY() + 0.08, z, 1, 0.01, 0.02, 0.01, 0.0);
         if ((i + this.tickCount) % 3 == 0) {
            serverLevel.sendParticles(sparkParticle, x, this.getY() + 0.1, z, 1, 0.015, 0.025, 0.015, 0.0);
         }
      }

      serverLevel.sendParticles(this.getSurgeParticle(), this.getX(), this.getY() + 0.12, this.getZ(), 3, radius * 0.18, 0.03, radius * 0.18, 0.0);
   }

   private void spawnVolumeTelegraph(ServerLevel serverLevel) {
      float radius = this.getRadius();
      int ringPoints = Math.max(16, Mth.ceil(radius * 13.0F));
      ParticleOptions animParticle = this.getAnimatedFlameParticle();
      ParticleOptions sparkParticle = this.getSparkParticle();

      for (int i = 0; i < ringPoints; i++) {
         double angle = (Math.PI * 2) * i / ringPoints + this.tickCount * 0.09;
         double x = this.getX() + Math.cos(angle) * radius * 0.95F;
         double z = this.getZ() + Math.sin(angle) * radius * 0.95F;
         serverLevel.sendParticles(animParticle, x, this.getY() + 0.08, z, 1, 0.015, 0.02, 0.015, 0.0);
      }

      serverLevel.sendParticles(this.getSurgeParticle(), this.getX(), this.getY() + 0.14, this.getZ(), 5, radius * 0.2, 0.08, radius * 0.2, 0.0);
      serverLevel.sendParticles(sparkParticle, this.getX(), this.getY() + 0.18, this.getZ(), 6, radius * 0.16, 0.05, radius * 0.16, 0.0);
   }

   private void spawnAggressiveTelegraph(ServerLevel serverLevel) {
      float radius = this.getRadius();
      int ringPoints = Math.max(18, Mth.ceil(radius * 14.0F));
      ParticleOptions sparkParticle = this.getSparkParticle();
      ParticleOptions surgeParticle = this.getSurgeParticle();

      for (int i = 0; i < ringPoints; i++) {
         double angle = (Math.PI * 2) * i / ringPoints + this.tickCount * 0.18;
         double x = this.getX() + Math.cos(angle) * radius;
         double z = this.getZ() + Math.sin(angle) * radius;
         serverLevel.sendParticles(sparkParticle, x, this.getY() + 0.1, z, 1, 0.02, 0.03, 0.02, 0.0);
         if ((i + this.tickCount) % 2 == 0) {
            serverLevel.sendParticles(surgeParticle, x, this.getY() + 0.08, z, 1, 0.02, 0.04, 0.02, 0.0);
         }
      }

      serverLevel.sendParticles(surgeParticle, this.getX(), this.getY() + 0.18, this.getZ(), 7, radius * 0.24, 0.1, radius * 0.24, 0.01);
   }

   private void spawnPremiumTelegraph(ServerLevel serverLevel) {
      float radius = this.getRadius();
      int outerPoints = Math.max(20, Mth.ceil(radius * 16.0F));
      int innerPoints = Math.max(14, Mth.ceil(radius * 11.0F));
      ParticleOptions animParticle = this.getAnimatedFlameParticle();
      ParticleOptions sparkParticle = this.getSparkParticle();

      for (int i = 0; i < outerPoints; i++) {
         double angle = (Math.PI * 2) * i / outerPoints + this.tickCount * 0.1;
         double x = this.getX() + Math.cos(angle) * radius * 1.05F;
         double z = this.getZ() + Math.sin(angle) * radius * 1.05F;
         serverLevel.sendParticles(animParticle, x, this.getY() + 0.08, z, 1, 0.01, 0.02, 0.01, 0.0);
      }

      for (int i = 0; i < innerPoints; i++) {
         double angle = (Math.PI * 2) * i / innerPoints - this.tickCount * 0.08;
         double x = this.getX() + Math.cos(angle) * radius * 0.58F;
         double z = this.getZ() + Math.sin(angle) * radius * 0.58F;
         serverLevel.sendParticles(sparkParticle, x, this.getY() + 0.14, z, 1, 0.01, 0.02, 0.01, 0.0);
      }

      serverLevel.sendParticles(this.getSurgeParticle(), this.getX(), this.getY() + 0.16, this.getZ(), 6, radius * 0.16, 0.06, radius * 0.16, 0.0);
   }

   private void spawnActiveParticles(ServerLevel serverLevel) {
      switch (this.getVariant()) {
         case 0:
            this.spawnVolumeActive(serverLevel);
            break;
         case 1:
         default:
            this.spawnSilhouetteActive(serverLevel);
            break;
         case 2:
            this.spawnAggressiveActive(serverLevel);
            break;
         case 3:
            this.spawnPremiumActive(serverLevel);
      }
   }

   private void spawnSilhouetteActive(ServerLevel serverLevel) {
      float radius = this.getRadius();
      int activeAge = Math.max(0, this.tickCount - 12);
      float baseProgress = Mth.clamp((activeAge + 1) / 4.0F, 0.0F, 1.0F);
      float lowerProgress = Mth.clamp((activeAge - 2) / 5.0F, 0.0F, 1.0F);
      float neckStartProgress = Mth.clamp((activeAge - 7) / 5.0F, 0.0F, 1.0F);
      float neckMidProgress = Mth.clamp((activeAge - 10) / 5.0F, 0.0F, 1.0F);
      float upperProgress = Mth.clamp((activeAge - 13) / 5.0F, 0.0F, 1.0F);
      float topProgress = Mth.clamp((activeAge - 16) / 5.0F, 0.0F, 1.0F);
      ParticleOptions lavaParticle = this.getLavaParticle();
      this.emitProgressiveVortexBand(serverLevel, radius * 1.22F, 0.1F, 12.0F, 0.058F, 0.88F, 4, 0.55F, 1.12F, baseProgress);
      this.emitProgressiveVortexBand(serverLevel, radius * 0.9F, 0.62F, -10.0F, 0.054F, 0.78F, 4, 0.42F, 1.1F, lowerProgress);
      this.emitProgressiveVortexBand(serverLevel, radius * 0.58F, 1.55F, 15.0F, 0.07F, 0.7F, 5, 0.28F, 1.08F, neckStartProgress);
      this.emitProgressiveVortexBand(serverLevel, radius * 0.48F, 2.85F, -13.0F, 0.078F, 0.64F, 5, 0.24F, 1.08F, neckMidProgress);
      this.emitProgressiveVortexBand(serverLevel, radius * 0.54F, 3.9F, 14.0F, 0.084F, 0.6F, 4, 0.26F, 1.04F, upperProgress);
      this.emitProgressiveVortexBand(serverLevel, radius * 0.84F, 5.1F, -11.0F, 0.08F, 0.55F, 5, 0.34F, 1.0F, topProgress);
      this.emitProgressiveVortexBand(serverLevel, radius * 1.06F, 6.1F, 10.0F, 0.074F, 0.5F, 4, 0.4F, 0.96F, topProgress * 0.9F);
      float coreProgress = Mth.clamp((activeAge + 1) / 8.0F, 0.0F, 1.0F);
      serverLevel.sendParticles(
         this.getSurgeParticle(),
         this.getX(),
         this.getY() + 0.6 + coreProgress * 1.15F,
         this.getZ(),
         Math.max(1, Mth.ceil(3.0F * coreProgress)),
         radius * (0.03 + coreProgress * 0.06),
         0.24 + coreProgress * 0.3,
         radius * (0.03 + coreProgress * 0.06),
         0.0
      );
      serverLevel.sendParticles(lavaParticle, this.getX(), this.getY() + 0.28, this.getZ(), 1, radius * 0.12, 0.05, radius * 0.12, 0.0);
   }

   private void spawnVolumeActive(ServerLevel serverLevel) {
      float radius = this.getRadius();
      ParticleOptions animParticle = this.getAnimatedFlameParticle();
      ParticleOptions sparkParticle = this.getSparkParticle();
      ParticleOptions surgeParticle = this.getSurgeParticle();
      ParticleOptions lavaParticle = this.getLavaParticle();
      this.emitVortexBand(serverLevel, radius * 1.0F, 0.18F, 12.0F, 0.07F, 1.06F, 5, 0.75F, 1.05F);
      this.emitVortexBand(serverLevel, radius * 0.84F, 0.78F, -10.0F, 0.066F, 0.94F, 5, 0.7F, 1.0F);
      this.emitVortexBand(serverLevel, radius * 0.68F, 1.45F, 16.0F, 0.08F, 0.86F, 6, 0.62F, 1.0F);
      this.emitVortexBand(serverLevel, radius * 0.48F, 2.08F, -13.5F, 0.09F, 0.76F, 4, 0.58F, 0.95F);
      serverLevel.sendParticles(animParticle, this.getX(), this.getY() + 1.15, this.getZ(), 10, radius * 0.22, 0.9, radius * 0.22, 0.0);
      serverLevel.sendParticles(sparkParticle, this.getX(), this.getY() + 1.25, this.getZ(), 7, radius * 0.18, 0.75, radius * 0.18, 0.0);
      serverLevel.sendParticles(surgeParticle, this.getX(), this.getY() + 1.55, this.getZ(), 4, radius * 0.12, 0.55, radius * 0.12, 0.0);
      serverLevel.sendParticles(lavaParticle, this.getX(), this.getY() + 0.35, this.getZ(), 2, radius * 0.2, 0.08, radius * 0.2, 0.0);
   }

   private void spawnAggressiveActive(ServerLevel serverLevel) {
      float radius = this.getRadius();
      ParticleOptions sparkParticle = this.getSparkParticle();
      ParticleOptions animParticle = this.getAnimatedFlameParticle();
      ParticleOptions surgeParticle = this.getSurgeParticle();
      ParticleOptions lavaParticle = this.getLavaParticle();
      this.emitVortexBand(serverLevel, radius * 1.04F, 0.2F, 24.0F, 0.078F, 1.0F, 5, 1.15F, 0.9F);
      this.emitVortexBand(serverLevel, radius * 0.86F, 0.82F, -19.0F, 0.072F, 0.92F, 5, 1.1F, 0.9F);
      this.emitVortexBand(serverLevel, radius * 0.66F, 1.45F, 30.0F, 0.095F, 0.84F, 6, 1.2F, 0.88F);
      this.emitVortexBand(serverLevel, radius * 0.46F, 2.1F, -26.0F, 0.108F, 0.72F, 4, 1.25F, 0.85F);
      serverLevel.sendParticles(sparkParticle, this.getX(), this.getY() + 1.2, this.getZ(), 12, radius * 0.36, 1.05, radius * 0.36, 0.05);
      serverLevel.sendParticles(animParticle, this.getX(), this.getY() + 1.0, this.getZ(), 10, radius * 0.24, 0.85, radius * 0.24, 0.02);
      serverLevel.sendParticles(surgeParticle, this.getX(), this.getY() + 1.45, this.getZ(), 6, radius * 0.18, 0.65, radius * 0.18, 0.03);
      serverLevel.sendParticles(lavaParticle, this.getX(), this.getY() + 0.45, this.getZ(), 6, radius * 0.4, 0.14, radius * 0.4, 0.04);
   }

   private void spawnPremiumActive(ServerLevel serverLevel) {
      float radius = this.getRadius();
      ParticleOptions surgeParticle = this.getSurgeParticle();
      ParticleOptions animParticle = this.getAnimatedFlameParticle();
      ParticleOptions lavaParticle = this.getLavaParticle();
      this.emitVortexBand(serverLevel, radius * 1.12F, 0.12F, 11.0F, 0.062F, 1.18F, 7, 0.28F, 1.35F);
      this.emitVortexBand(serverLevel, radius * 0.99F, 0.46F, -11.0F, 0.064F, 1.08F, 7, 0.26F, 1.35F);
      this.emitVortexBand(serverLevel, radius * 0.87F, 0.9F, 13.0F, 0.068F, 0.96F, 7, 0.24F, 1.32F);
      this.emitVortexBand(serverLevel, radius * 0.76F, 1.38F, -13.0F, 0.072F, 0.88F, 7, 0.24F, 1.3F);
      this.emitVortexBand(serverLevel, radius * 0.65F, 1.92F, 15.0F, 0.078F, 0.8F, 7, 0.22F, 1.26F);
      this.emitVortexBand(serverLevel, radius * 0.54F, 2.5F, -15.0F, 0.083F, 0.72F, 6, 0.22F, 1.22F);
      serverLevel.sendParticles(surgeParticle, this.getX(), this.getY() + 1.25, this.getZ(), 4, radius * 0.1, 0.55, radius * 0.1, 0.0);
      serverLevel.sendParticles(animParticle, this.getX(), this.getY() + 1.55, this.getZ(), 5, radius * 0.08, 0.45, radius * 0.08, 0.0);
      serverLevel.sendParticles(lavaParticle, this.getX(), this.getY() + 0.35, this.getZ(), 1, radius * 0.12, 0.06, radius * 0.12, 0.0);
   }

   private void emitVortexBand(
      ServerLevel serverLevel, float radius, float height, float angularVelocity, float ascent, float scale, int count, float jitter, float lifeScale
   ) {
      serverLevel.sendParticles(
         new FireVortexParticleOptions(radius, height, this.getId(), angularVelocity, ascent, scale, jitter, lifeScale, this.getElementalVisualMode()),
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

   private void emitProgressiveVortexBand(
      ServerLevel serverLevel,
      float radius,
      float height,
      float angularVelocity,
      float ascent,
      float scale,
      int count,
      float jitter,
      float lifeScale,
      float progress
   ) {
      progress = Mth.clamp(progress, 0.0F, 1.0F);
      if (!(progress <= 0.03F)) {
         int scaledCount = Math.max(1, Mth.ceil(count * progress));
         float scaledRadius = radius * (0.35F + 0.65F * progress);
         float scaledHeight = height * (0.22F + 0.78F * progress);
         float scaledAscent = ascent * (0.72F + 0.28F * progress);
         float scaledScale = scale * (0.58F + 0.42F * progress);
         this.emitVortexBand(serverLevel, scaledRadius, scaledHeight, angularVelocity, scaledAscent, scaledScale, scaledCount, jitter, lifeScale);
      }
   }

   private void pulseDamage() {
      Entity owner = this.getOwner();
      float radius = this.getRadius();
      AABB area = this.getBoundingBox().inflate(radius, 3.0, radius);

      for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, area)) {
         if (target != owner && (owner == null || !target.isAlliedTo(owner))) {
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            if (!(dx * dx + dz * dz > radius * radius * 1.15F)) {
               target.hurt(
                  ((FirestormVortexSpell)ModSpells.FIRESTORM_VORTEX.get()).getDamageSource(this, (Entity)(owner != null ? owner : this)), this.damagePerPulse
               );
               target.setRemainingFireTicks(Math.max(target.getRemainingFireTicks(), 80));
               Vec3 pull = new Vec3(this.getX() - target.getX(), 0.0, this.getZ() - target.getZ());
               if (pull.lengthSqr() > 1.0E-4) {
                  pull = pull.normalize().scale(0.18);
                  target.push(pull.x, 0.28, pull.z);
                  target.hurtMarked = true;
               }
            }
         }
      }
   }

   public void onAntiMagic(MagicData playerMagicData) {
      this.discard();
   }

   private ParticleOptions getAnimatedFlameParticle() {
      if (this.isSoulFire()) {
         return (ParticleOptions)ModParticles.SOUL_FLAME_ANIM_FIRE.get();
      } else {
         return this.isInfernalFire()
            ? this.getExternalSimpleParticle("born_in_chaos_v1:anim_fire", (ParticleOptions)ModParticles.FLAME_ANIM_FIRE.get())
            : (ParticleOptions)ModParticles.FLAME_ANIM_FIRE.get();
      }
   }

   private ParticleOptions getSparkParticle() {
      if (this.isSoulFire()) {
         return (ParticleOptions)ModParticles.SOUL_FIRE.get();
      } else {
         return this.isInfernalFire()
            ? this.getExternalSimpleParticle("born_in_chaos_v1:soul_fire", (ParticleOptions)ModParticles.SOUL_FIRE.get())
            : (ParticleOptions)ModParticles.FIRE.get();
      }
   }

   private ParticleOptions getSurgeParticle() {
      if (this.isSoulFire()) {
         return (ParticleOptions)ModParticles.SOUL_SURGE.get();
      } else {
         return this.isInfernalFire()
            ? this.getExternalSimpleParticle("born_in_chaos_v1:infernal_surge", (ParticleOptions)ModParticles.FLAME_SURGE.get())
            : (ParticleOptions)ModParticles.FLAME_SURGE.get();
      }
   }

   private ParticleOptions getLavaParticle() {
      if (this.isSoulFire()) {
         return (ParticleOptions)ModParticles.SOUL_FIRE_LAVA.get();
      } else {
         return this.isInfernalFire() ? ModParticles.infernalFireLavaOr((SimpleParticleType)ModParticles.SOUL_FIRE_LAVA.get()) : ParticleTypes.LAVA;
      }
   }

   private int getElementalVisualMode() {
      if (this.isSoulFire()) {
         return 1;
      } else {
         return this.isInfernalFire() ? 2 : 0;
      }
   }

   private ParticleOptions getExternalSimpleParticle(String id, ParticleOptions fallback) {
      ParticleType<?> type = (ParticleType<?>)BuiltInRegistries.PARTICLE_TYPE.get(ResourceLocation.parse(id));
      return type instanceof ParticleOptions options ? options : fallback;
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      this.damagePerPulse = compound.getFloat("DamagePerPulse");
      this.setIsSoulFire(compound.getBoolean("IsSoulFire"));
      this.setIsInfernalFire(compound.getBoolean("IsInfernalFire"));
      if (compound.contains("TotalLifetime")) {
         this.totalLifetime = Math.max(32, compound.getInt("TotalLifetime"));
      }

      if (compound.contains("Radius")) {
         this.setRadius(compound.getFloat("Radius"));
      }

      if (compound.contains("Variant")) {
         this.setVariant(compound.getInt("Variant"));
      }
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      compound.putFloat("DamagePerPulse", this.damagePerPulse);
      compound.putBoolean("IsSoulFire", this.isSoulFire());
      compound.putBoolean("IsInfernalFire", this.isInfernalFire());
      compound.putInt("TotalLifetime", this.totalLifetime);
      compound.putFloat("Radius", this.getRadius());
      compound.putInt("Variant", this.getVariant());
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return super.getAddEntityPacket(serverEntity);
   }
}
