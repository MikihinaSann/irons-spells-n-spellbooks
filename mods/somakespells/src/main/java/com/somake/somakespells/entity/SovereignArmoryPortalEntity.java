package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SovereignArmoryPortalEntity extends Entity implements GeoEntity {
   private static final int CLOSE_ANIMATION_TICKS = 8;
   private static final int PHASE_OPEN = 0;
   private static final int PHASE_CLOSING = 1;
   private static final EntityDataAccessor<Integer> CLOSE_DELAY = SynchedEntityData.defineId(SovereignArmoryPortalEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(SovereignArmoryPortalEntity.class, EntityDataSerializers.INT);
   private static final RawAnimation OPEN_ANIMATION = RawAnimation.begin().thenPlay("open").thenLoop("idle");
   private static final RawAnimation CLOSE_ANIMATION = RawAnimation.begin().thenPlayAndHold("close");
   private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
   private int previousPhase = -1;
   @Nullable
   private LivingEntity secondShotOwner;
   @Nullable
   private LivingEntity secondShotTarget;
   private Vec3 secondShotAimPoint = Vec3.ZERO;
   private float secondShotDamage;
   private int secondShotTick = -1;
   private int secondProjectileLaunchDelay = 1;
   private int secondShotVariant;
   private boolean secondShotFired;

   public SovereignArmoryPortalEntity(EntityType<?> entityType, Level level) {
      super(entityType, level);
      this.noPhysics = true;
   }

   public SovereignArmoryPortalEntity(Level level, Vec3 position, float yaw, float pitch, int closeDelay) {
      this((EntityType<?>)ModEntityTypes.SOVEREIGN_ARMORY_PORTAL.get(), level);
      this.moveTo(position.x, position.y, position.z, yaw, pitch);
      this.setCloseDelay(closeDelay);
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(CLOSE_DELAY, 20);
      builder.define(PHASE, 0);
   }

   public void tick() {
      super.tick();
      this.setDeltaMovement(Vec3.ZERO);
      if (this.level() instanceof ServerLevel serverLevel) {
         if (this.tickCount == 1) {
            serverLevel.playSound(
               null,
               this.getX(),
               this.getY(),
               this.getZ(),
               SoundEvents.AMETHYST_BLOCK_CHIME,
               SoundSource.PLAYERS,
               0.7F,
               1.45F + this.random.nextFloat() * 0.15F
            );
            MagicManager.spawnParticles(
               serverLevel, new BlastwaveParticleOptions(1.0F, 0.78F, 0.18F, 1.25F), this.getX(), this.getY(), this.getZ(), 1, 0.0, 0.0, 0.0, 0.0, true
            );
            MagicManager.spawnParticles(serverLevel, ParticleHelper.CLEANSE_PARTICLE, this.getX(), this.getY(), this.getZ(), 5, 0.45, 0.45, 0.45, 0.04, false);
         }

         if (!this.secondShotFired && this.secondShotTick >= 0 && this.tickCount >= this.secondShotTick) {
            this.fireSecondShot(serverLevel);
         }

         if (!this.isClosing() && this.tickCount >= this.getCloseDelay()) {
            this.entityData.set(PHASE, 1);
         }

         if (this.tickCount >= this.getCloseDelay() + 8) {
            this.discard();
         }
      }
   }

   public int getCloseDelay() {
      return (Integer)this.entityData.get(CLOSE_DELAY);
   }

   public void setCloseDelay(int closeDelay) {
      this.entityData.set(CLOSE_DELAY, Math.max(8, closeDelay));
   }

   public boolean isClosing() {
      return (Integer)this.entityData.get(PHASE) == 1;
   }

   public void configureSecondShot(
      LivingEntity owner, @Nullable LivingEntity target, Vec3 aimPoint, float damage, int shotTick, int projectileLaunchDelay, int variant
   ) {
      this.secondShotOwner = owner;
      this.secondShotTarget = target;
      this.secondShotAimPoint = aimPoint;
      this.secondShotDamage = damage;
      this.secondShotTick = Math.max(1, shotTick);
      this.secondProjectileLaunchDelay = Math.max(1, projectileLaunchDelay);
      this.secondShotVariant = variant;
   }

   private void fireSecondShot(ServerLevel serverLevel) {
      this.secondShotFired = true;
      LivingEntity owner = this.secondShotOwner;
      if (owner != null && !owner.isRemoved() && owner.isAlive()) {
         LivingEntity target = this.secondShotTarget;
         if (target != null && (!target.isAlive() || target.isRemoved())) {
            target = null;
         }

         SovereignArmoryProjectileEntity projectile = new SovereignArmoryProjectileEntity(
            serverLevel,
            owner,
            this.position(),
            this.secondShotAimPoint,
            target,
            this.secondShotDamage,
            this.secondProjectileLaunchDelay,
            this.secondShotVariant
         );
         serverLevel.addFreshEntity(projectile);
         MagicManager.spawnParticles(serverLevel, ParticleHelper.CLEANSE_PARTICLE, this.getX(), this.getY(), this.getZ(), 8, 0.3, 0.3, 0.3, 0.05, false);
         serverLevel.playSound(
            null, this.getX(), this.getY(), this.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.55F, 1.7F + this.random.nextFloat() * 0.15F
         );
      }
   }

   public AABB getBoundingBoxForCulling() {
      return this.getBoundingBox().inflate(3.0);
   }

   public boolean shouldRenderAtSqrDistance(double distance) {
      return distance < 16384.0;
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "portal", 0, this::animationPredicate));
   }

   private PlayState animationPredicate(AnimationState<SovereignArmoryPortalEntity> state) {
      int phase = (Integer)this.entityData.get(PHASE);
      if (phase != this.previousPhase) {
         this.previousPhase = phase;
         state.getController().forceAnimationReset();
      }

      return state.setAndContinue(phase == 1 ? CLOSE_ANIMATION : OPEN_ANIMATION);
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.animationCache;
   }
}
