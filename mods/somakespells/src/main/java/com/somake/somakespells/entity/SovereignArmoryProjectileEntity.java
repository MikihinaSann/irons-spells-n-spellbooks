package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.holy.SovereignArmorySpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SovereignArmoryProjectileEntity extends AbstractMagicProjectile implements GeoEntity {
   private static final EntityDataAccessor<Integer> LAUNCH_DELAY = SynchedEntityData.defineId(SovereignArmoryProjectileEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> LAUNCHED = SynchedEntityData.defineId(SovereignArmoryProjectileEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> AIM_X = SynchedEntityData.defineId(SovereignArmoryProjectileEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> AIM_Y = SynchedEntityData.defineId(SovereignArmoryProjectileEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> AIM_Z = SynchedEntityData.defineId(SovereignArmoryProjectileEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(SovereignArmoryProjectileEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(SovereignArmoryProjectileEntity.class, EntityDataSerializers.INT);
   private static final RawAnimation EMERGE_ANIMATION = RawAnimation.begin().thenPlayAndHold("emerge");
   private static final RawAnimation FLY_ANIMATION = RawAnimation.begin().thenLoop("fly");
   private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
   private boolean previousLaunchedState;
   private boolean animationInitialized;

   public SovereignArmoryProjectileEntity(EntityType<? extends AbstractMagicProjectile> entityType, Level level) {
      super(entityType, level);
      this.setNoGravity(true);
   }

   public SovereignArmoryProjectileEntity(
      Level level, LivingEntity owner, Vec3 spawnPosition, Vec3 aimPoint, @Nullable LivingEntity target, float damage, int launchDelay, int variant
   ) {
      this((EntityType<? extends AbstractMagicProjectile>)ModEntityTypes.SOVEREIGN_ARMORY_PROJECTILE.get(), level);
      this.setOwner(owner);
      this.setPos(spawnPosition.x, spawnPosition.y, spawnPosition.z);
      this.setAimPoint(aimPoint);
      this.setDamageValue(damage);
      this.setLaunchDelay(launchDelay);
      this.setVariant(variant);
      this.setDeltaMovement(Vec3.ZERO);
      if (target != null) {
         this.setHomingTarget(target);
      }

      this.facePoint(aimPoint);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(LAUNCH_DELAY, 12);
      builder.define(LAUNCHED, false);
      builder.define(AIM_X, 0.0F);
      builder.define(AIM_Y, 0.0F);
      builder.define(AIM_Z, 0.0F);
      builder.define(DAMAGE, 0.0F);
      builder.define(VARIANT, 0);
   }

   public float getSpeed() {
      return 2.25F;
   }

   public void tick() {
      if (!this.isLaunched()) {
         this.setDeltaMovement(Vec3.ZERO);
      }

      super.tick();
      if (!this.isRemoved()) {
         if (!this.isLaunched()) {
            this.setDeltaMovement(Vec3.ZERO);
            this.facePoint(this.getAimPoint());
            this.xo = this.getX();
            this.yo = this.getY();
            this.zo = this.getZ();
            this.xOld = this.getX();
            this.yOld = this.getY();
            this.zOld = this.getZ();
         }

         if (!this.level().isClientSide) {
            Entity owner = this.getOwner();
            if (owner != null && !owner.isRemoved()) {
               if (!this.isLaunched() && this.tickCount >= this.getLaunchDelay()) {
                  this.launchWeapon();
               } else if (this.isLaunched() && this.tickCount > this.getLaunchDelay() + 100) {
                  this.discard();
               }
            } else {
               this.discard();
            }
         }
      }
   }

   public void handleHitDetection() {
      if (this.isLaunched()) {
         super.handleHitDetection();
      }
   }

   public void travel() {
      if (this.isLaunched()) {
         super.travel();
      }
   }

   private void launchWeapon() {
      Entity owner = this.getOwner();
      if (owner == null) {
         this.discard();
      } else {
         Vec3 targetPoint = this.getAimPoint();
         if (this.getHomingTarget() instanceof LivingEntity livingTarget && livingTarget.isAlive() && !DamageSources.isFriendlyFireBetween(owner, livingTarget)
            )
          {
            targetPoint = livingTarget.getBoundingBox().getCenter();
         } else {
            this.stopEntityHoming();
         }

         Vec3 direction = targetPoint.subtract(this.position());
         if (direction.lengthSqr() < 1.0E-6) {
            direction = owner.getLookAngle();
         }

         this.entityData.set(LAUNCHED, true);
         this.shoot(direction.normalize());
         this.hasImpulse = true;
         this.facePoint(targetPoint);
         this.level()
            .playSound(
               null,
               this.getX(),
               this.getY(),
               this.getZ(),
               (SoundEvent)SoundEvents.TRIDENT_THROW.value(),
               SoundSource.PLAYERS,
               0.9F,
               1.25F + this.random.nextFloat() * 0.2F
            );
      }
   }

   protected boolean canHitEntity(@NotNull Entity target) {
      Entity owner = this.getOwner();
      return owner != null && target != owner && super.canHitEntity(target) && !DamageSources.isFriendlyFireBetween(owner, target);
   }

   protected void onHitEntity(@NotNull EntityHitResult result) {
      super.onHitEntity(result);
      if (!this.level().isClientSide && !this.isRemoved()) {
         Entity owner = this.getOwner();
         Entity target = result.getEntity();
         if (owner != null && !DamageSources.isFriendlyFireBetween(owner, target)) {
            DamageSources.applyDamage(target, this.getDamageValue(), ((SovereignArmorySpell)ModSpells.SOVEREIGN_ARMORY.get()).getDamageSource(this, owner));
            this.consumeEntityImpact(result, true);
         } else {
            this.discard();
         }
      }
   }

   protected void onHitBlock(BlockHitResult result) {
      super.onHitBlock(result);
      if (!this.level().isClientSide) {
         this.discard();
      }
   }

   public void trailParticles() {
      if (this.isLaunched()) {
         Vec3 motion = this.getDeltaMovement();
         this.level()
            .addParticle(
               ParticleHelper.CLEANSE_PARTICLE, this.getX() - motion.x * 0.2, this.getY() - motion.y * 0.2, this.getZ() - motion.z * 0.2, 0.0, 0.0, 0.0
            );
         this.level().addParticle(ParticleTypes.WAX_ON, this.getX(), this.getY(), this.getZ(), -motion.x * 0.015, -motion.y * 0.015, -motion.z * 0.015);
      }
   }

   public void impactParticles(double x, double y, double z) {
      if (this.level() instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles(ParticleHelper.CLEANSE_PARTICLE, x, y, z, 12, 0.25, 0.25, 0.25, 0.05);
         serverLevel.sendParticles(ParticleTypes.WAX_ON, x, y, z, 16, 0.3, 0.3, 0.3, 0.08);
         serverLevel.sendParticles(new BlastwaveParticleOptions(1.0F, 0.78F, 0.18F, 1.1F), x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
      }
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.empty();
   }

   public boolean isLaunched() {
      return (Boolean)this.entityData.get(LAUNCHED);
   }

   public int getVariant() {
      return (Integer)this.entityData.get(VARIANT);
   }

   private void setVariant(int variant) {
      this.entityData.set(VARIANT, Mth.clamp(variant, 0, 2));
   }

   private int getLaunchDelay() {
      return (Integer)this.entityData.get(LAUNCH_DELAY);
   }

   private void setLaunchDelay(int launchDelay) {
      this.entityData.set(LAUNCH_DELAY, Math.max(1, launchDelay));
   }

   private float getDamageValue() {
      return (Float)this.entityData.get(DAMAGE);
   }

   private void setDamageValue(float damage) {
      this.entityData.set(DAMAGE, Math.max(0.0F, damage));
   }

   private void setAimPoint(Vec3 aimPoint) {
      this.entityData.set(AIM_X, (float)aimPoint.x);
      this.entityData.set(AIM_Y, (float)aimPoint.y);
      this.entityData.set(AIM_Z, (float)aimPoint.z);
   }

   private Vec3 getAimPoint() {
      return new Vec3(
         ((Float)this.entityData.get(AIM_X)).floatValue(), ((Float)this.entityData.get(AIM_Y)).floatValue(), ((Float)this.entityData.get(AIM_Z)).floatValue()
      );
   }

   private void facePoint(Vec3 point) {
      Vec3 direction = point.subtract(this.position());
      if (!(direction.lengthSqr() < 1.0E-6)) {
         direction = direction.normalize();
         float yaw = (float)(Mth.atan2(direction.x, direction.z) * 180.0F / (float)Math.PI);
         float pitch = (float)(Mth.atan2(direction.y, direction.horizontalDistance()) * 180.0F / (float)Math.PI);
         this.setYRot(yaw);
         this.setXRot(pitch);
         this.yRotO = yaw;
         this.xRotO = pitch;
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "weapon", 0, state -> {
         boolean launched = this.isLaunched();
         if (!this.animationInitialized || launched != this.previousLaunchedState) {
            this.animationInitialized = true;
            this.previousLaunchedState = launched;
            state.getController().forceAnimationReset();
         }

         return state.setAndContinue(launched ? FLY_ANIMATION : EMERGE_ANIMATION);
      }));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.animationCache;
   }
}
