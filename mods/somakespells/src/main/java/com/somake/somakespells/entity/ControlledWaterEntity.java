package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.aqua.WaterControlSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ControlledWaterEntity extends Projectile implements GeoEntity {
   private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(
      ControlledWaterEntity.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Integer> LIFETIME = SynchedEntityData.defineId(ControlledWaterEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> LAUNCHED = SynchedEntityData.defineId(ControlledWaterEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(ControlledWaterEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> TARGET_X = SynchedEntityData.defineId(ControlledWaterEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> TARGET_Y = SynchedEntityData.defineId(ControlledWaterEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> TARGET_Z = SynchedEntityData.defineId(ControlledWaterEntity.class, EntityDataSerializers.FLOAT);
   private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");
   private static final double BLOCK_PUSHBACK = 0.6;
   private static final double FOLLOW_SMOOTHING = 0.18;
   private static final double MAX_FOLLOW_STEP = 0.35;
   private static final double CLIENT_FOLLOW_SMOOTHING = 0.42;
   private static final double CLIENT_MAX_FOLLOW_STEP = 0.45;
   private static final double LAUNCH_SPEED = 1.2;
   private static final double IMPACT_RADIUS = 1.6;
   private static final int CONTROL_DAMAGE_INTERVAL = 8;
   private static final float CONTROL_DAMAGE_MULTIPLIER = 0.45F;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private final Set<UUID> hitEntities = new HashSet<>();
   private final Map<UUID, Integer> recentControlHits = new HashMap<>();
   private LivingEntity cachedOwner;

   public ControlledWaterEntity(EntityType<? extends Projectile> entityType, Level level) {
      super(entityType, level);
      this.setNoGravity(true);
      this.noPhysics = true;
   }

   public ControlledWaterEntity(Level level, LivingEntity owner) {
      this((EntityType<? extends Projectile>)ModEntityTypes.CONTROLLED_WATER.get(), level);
      this.setOwner(owner);
      this.entityData.set(OWNER_UUID, Optional.of(owner.getUUID()));
      this.entityData.set(LIFETIME, 120);
      this.moveTo(owner.getEyePosition());
   }

   public void setLifetime(int lifetime) {
      this.entityData.set(LIFETIME, Math.max(20, lifetime));
   }

   public int getLifetime() {
      return (Integer)this.entityData.get(LIFETIME);
   }

   public void setDamage(float damage) {
      this.entityData.set(DAMAGE, damage);
   }

   public float getDamage() {
      return (Float)this.entityData.get(DAMAGE);
   }

   public boolean isLaunched() {
      return (Boolean)this.entityData.get(LAUNCHED);
   }

   public void followTarget(Vec3 targetPosition, LivingEntity owner) {
      this.setOwner(owner);
      this.entityData.set(OWNER_UUID, Optional.of(owner.getUUID()));
      Vec3 desiredPosition = new Vec3(targetPosition.x, targetPosition.y - 0.35, targetPosition.z);
      this.entityData.set(TARGET_X, (float)desiredPosition.x);
      this.entityData.set(TARGET_Y, (float)desiredPosition.y);
      this.entityData.set(TARGET_Z, (float)desiredPosition.z);
      Vec3 currentPosition = this.position();
      Vec3 toTarget = desiredPosition.subtract(currentPosition);
      Vec3 step = toTarget.scale(0.18);
      if (step.length() > 0.35) {
         step = step.normalize().scale(0.35);
      }

      Vec3 nextPosition = currentPosition.add(step);
      this.setDeltaMovement(step);
      this.setPos(nextPosition.x, nextPosition.y, nextPosition.z);
      this.setYRot(owner.getYRot());
      this.setXRot(owner.getXRot());
   }

   public boolean isOwnedBy(LivingEntity entity) {
      return entity != null && ((Optional<UUID>)this.entityData.get(OWNER_UUID)).<Boolean>map(uuid -> uuid.equals(entity.getUUID())).orElse(false);
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide) {
         if (this.isLaunched()) {
            this.setPos(this.getX() + this.getDeltaMovement().x, this.getY() + this.getDeltaMovement().y, this.getZ() + this.getDeltaMovement().z);
         } else {
            this.smoothClientMotion();
         }

         this.spawnClientParticles();
      }

      LivingEntity owner = this.getOwnerEntity();
      if (!this.level().isClientSide) {
         if (owner == null || owner.isRemoved() || !owner.isAlive()) {
            this.discard();
            return;
         }

         if (!this.isLaunched()) {
            this.setDeltaMovement(Vec3.ZERO);
            Vec3 targetPosition = this.computeControlPosition(owner);
            this.followTarget(targetPosition, owner);
            this.applyControlledContactDamage(owner);
         } else {
            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitResult.getType() != Type.MISS) {
               this.onHit(hitResult);
               if (!this.isRemoved()) {
                  this.setPos(this.getX() + this.getDeltaMovement().x, this.getY() + this.getDeltaMovement().y, this.getZ() + this.getDeltaMovement().z);
               }
            } else {
               this.setPos(this.getX() + this.getDeltaMovement().x, this.getY() + this.getDeltaMovement().y, this.getZ() + this.getDeltaMovement().z);
            }
         }

         this.spawnParticles((ServerLevel)this.level());
         if (this.tickCount >= this.getLifetime()) {
            this.discard();
         }
      }
   }

   private void applyControlledContactDamage(LivingEntity owner) {
      this.recentControlHits.entrySet().removeIf(entry -> this.tickCount - entry.getValue() >= 8);

      for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.35))) {
         if (target != owner && !target.isAlliedTo(owner) && !this.recentControlHits.containsKey(target.getUUID())) {
            float damage = this.getDamage() * 0.45F;
            if (DamageSources.applyDamage(target, damage, ((WaterControlSpell)ModSpells.WATER_CONTROL.get()).getDamageSource(this, owner))) {
               this.recentControlHits.put(target.getUUID(), this.tickCount);
               target.invulnerableTime = 0;
               Vec3 push = target.position().subtract(this.position()).normalize().scale(0.18);
               if (push.lengthSqr() > 0.0) {
                  target.push(push.x, 0.04, push.z);
               }
            }
         }
      }
   }

   private void spawnParticles(ServerLevel serverLevel) {
      double baseX = this.getX();
      double baseY = this.getY() + 0.2;
      double baseZ = this.getZ();
      double orbitScale = this.isLaunched() ? 0.32 : 0.45;

      for (int i = 0; i < 3; i++) {
         double angle = this.tickCount * 0.25 + (Math.PI * 2.0 / 3.0) * i;
         double orbitX = Math.cos(angle) * orbitScale;
         double orbitZ = Math.sin(angle) * orbitScale;
         double particleX = baseX + orbitX;
         double particleZ = baseZ + orbitZ;
         double particleY = baseY + (i % 2 == 0 ? 0.15 : -0.05);
         serverLevel.sendParticles(ParticleTypes.SPLASH, particleX, particleY, particleZ, 1, 0.02, 0.02, 0.02, 0.01);
         serverLevel.sendParticles(ParticleTypes.FALLING_WATER, particleX, particleY + 0.1, particleZ, 2, 0.03, 0.03, 0.03, 0.0);
      }

      serverLevel.sendParticles(ParticleTypes.SPLASH, baseX, baseY, baseZ, 2, 0.15, 0.15, 0.15, 0.01);
      serverLevel.sendParticles(ParticleTypes.FALLING_WATER, baseX, baseY + 0.15, baseZ, 4, 0.18, 0.18, 0.18, 0.0);
   }

   private void spawnClientParticles() {
      double radiusScale = this.isLaunched() ? 0.42 : 0.55;

      for (int i = 0; i < 5; i++) {
         double angle = this.tickCount * 0.35 + (Math.PI * 2.0 / 5.0) * i;
         double radius = radiusScale + i % 2 * 0.06;
         double particleX = this.getX() + Math.cos(angle) * radius;
         double particleY = this.getY() + 0.18 + (i % 2 == 0 ? 0.1 : -0.02);
         double particleZ = this.getZ() + Math.sin(angle) * radius;
         this.level().addParticle(ParticleTypes.SPLASH, particleX, particleY, particleZ, 0.0, 0.015, 0.0);
         this.level().addParticle(ParticleTypes.FALLING_WATER, particleX, particleY + 0.08, particleZ, 0.0, -0.01, 0.0);
      }

      this.level().addParticle(ParticleTypes.SPLASH, this.getX(), this.getY() + 0.2, this.getZ(), 0.0, 0.02, 0.0);
   }

   private void smoothClientMotion() {
      Vec3 desiredPosition = new Vec3(
         ((Float)this.entityData.get(TARGET_X)).floatValue(),
         ((Float)this.entityData.get(TARGET_Y)).floatValue(),
         ((Float)this.entityData.get(TARGET_Z)).floatValue()
      );
      if (!(desiredPosition.lengthSqr() < 1.0E-4)) {
         Vec3 currentPosition = this.position();
         Vec3 toTarget = desiredPosition.subtract(currentPosition);
         if (toTarget.lengthSqr() < 4.0E-4) {
            this.setPos(desiredPosition.x, desiredPosition.y, desiredPosition.z);
         } else {
            Vec3 step = toTarget.scale(0.42);
            if (step.length() > 0.45) {
               step = step.normalize().scale(0.45);
            }

            Vec3 nextPosition = currentPosition.add(step);
            this.setPos(nextPosition.x, nextPosition.y, nextPosition.z);
         }
      }
   }

   private Vec3 computeControlPosition(LivingEntity owner) {
      Vec3 eyePosition = owner.getEyePosition();
      Vec3 look = owner.getLookAngle().normalize();
      Vec3 maxTarget = eyePosition.add(look.scale(4.5));
      HitResult hitResult = Utils.raycastForBlock(this.level(), eyePosition, maxTarget, Fluid.NONE);
      return hitResult.getType() == Type.BLOCK && hitResult.getLocation().distanceToSqr(eyePosition) < 20.25
         ? hitResult.getLocation().subtract(look.scale(0.6))
         : maxTarget;
   }

   public void launch(LivingEntity owner) {
      if (!this.isLaunched()) {
         Vec3 look = owner.getLookAngle().normalize();
         this.entityData.set(LAUNCHED, true);
         this.setPos(this.getX() + look.x * 0.4, this.getY() + 0.05, this.getZ() + look.z * 0.4);
         this.setDeltaMovement(look.scale(1.2));
         this.setYRot(owner.getYRot());
         this.setXRot(owner.getXRot());
         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_SPLASH_HIGH_SPEED, SoundSource.PLAYERS, 1.0F, 1.0F);
      }
   }

   public boolean isPickable() {
      return !this.isRemoved();
   }

   public boolean canBeCollidedWith() {
      return !this.isRemoved();
   }

   public boolean skipAttackInteraction(Entity entity) {
      LivingEntity owner = this.getOwnerEntity();
      if (!this.level().isClientSide && !this.isLaunched() && owner != null && entity == owner) {
         this.launch(owner);
         return true;
      } else {
         return super.skipAttackInteraction(entity);
      }
   }

   public boolean hurt(DamageSource source, float amount) {
      if (!this.level().isClientSide && !this.isLaunched()) {
         LivingEntity owner = this.getOwnerEntity();
         Entity attacker = source.getEntity();
         Entity directEntity = source.getDirectEntity();
         if (owner == null || attacker != owner && directEntity != owner) {
            return false;
         } else {
            this.launch(owner);
            return true;
         }
      } else {
         return false;
      }
   }

   private LivingEntity getOwnerEntity() {
      if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
         return this.cachedOwner;
      } else {
         Optional<UUID> ownerUuid = (Optional<UUID>)this.entityData.get(OWNER_UUID);
         if (ownerUuid.isPresent()) {
            if (this.level() instanceof ServerLevel serverLevel) {
               if (serverLevel.getEntity(ownerUuid.get()) instanceof LivingEntity livingEntity) {
                  this.cachedOwner = livingEntity;
                  return livingEntity;
               }
            } else {
               Entity entity = this.level().getPlayerByUUID(ownerUuid.get());
               if (entity instanceof LivingEntity livingEntity) {
                  this.cachedOwner = livingEntity;
                  return livingEntity;
               }
            }
         }

         return null;
      }
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(OWNER_UUID, Optional.empty());
      builder.define(LIFETIME, 120);
      builder.define(LAUNCHED, false);
      builder.define(DAMAGE, 6.0F);
      builder.define(TARGET_X, 0.0F);
      builder.define(TARGET_Y, 0.0F);
      builder.define(TARGET_Z, 0.0F);
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      if (compound.hasUUID("Owner")) {
         this.entityData.set(OWNER_UUID, Optional.of(compound.getUUID("Owner")));
      }

      if (compound.contains("Lifetime")) {
         this.entityData.set(LIFETIME, compound.getInt("Lifetime"));
      }

      if (compound.contains("Launched")) {
         this.entityData.set(LAUNCHED, compound.getBoolean("Launched"));
      }

      if (compound.contains("Damage")) {
         this.entityData.set(DAMAGE, compound.getFloat("Damage"));
      }

      if (compound.contains("TargetX")) {
         this.entityData.set(TARGET_X, compound.getFloat("TargetX"));
      }

      if (compound.contains("TargetY")) {
         this.entityData.set(TARGET_Y, compound.getFloat("TargetY"));
      }

      if (compound.contains("TargetZ")) {
         this.entityData.set(TARGET_Z, compound.getFloat("TargetZ"));
      }
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      ((Optional<UUID>)this.entityData.get(OWNER_UUID)).ifPresent(uuid -> compound.putUUID("Owner", uuid));
      compound.putInt("Lifetime", this.getLifetime());
      compound.putBoolean("Launched", this.isLaunched());
      compound.putFloat("Damage", this.getDamage());
      compound.putFloat("TargetX", (Float)this.entityData.get(TARGET_X));
      compound.putFloat("TargetY", (Float)this.entityData.get(TARGET_Y));
      compound.putFloat("TargetZ", (Float)this.entityData.get(TARGET_Z));
   }

   protected boolean canHitEntity(Entity target) {
      LivingEntity owner = this.getOwnerEntity();
      return this.isLaunched()
         && target instanceof LivingEntity livingTarget
         && livingTarget != owner
         && (owner == null || !livingTarget.isAlliedTo(owner))
         && target.isPickable();
   }

   protected void onHit(HitResult result) {
      if (!this.level().isClientSide) {
         LivingEntity owner = this.getOwnerEntity();
         LivingEntity primaryTarget = null;
         if (result instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity livingTarget) {
            primaryTarget = livingTarget;
         }

         for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.6))) {
            if (target != owner && (owner == null || !target.isAlliedTo(owner)) && !this.hitEntities.contains(target.getUUID())) {
               float finalDamage = target == primaryTarget ? this.getDamage() : this.getDamage() * 0.6F;
               if (DamageSources.applyDamage(target, finalDamage, ((WaterControlSpell)ModSpells.WATER_CONTROL.get()).getDamageSource(this, owner))) {
                  this.hitEntities.add(target.getUUID());
                  target.invulnerableTime = 0;
               }
            }
         }

         if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SPLASH, this.getX(), this.getY(), this.getZ(), 18, 0.35, 0.25, 0.35, 0.04);
            serverLevel.sendParticles(ParticleTypes.FALLING_WATER, this.getX(), this.getY() + 0.1, this.getZ(), 22, 0.4, 0.3, 0.4, 0.0);
            serverLevel.sendParticles(ParticleTypes.BUBBLE, this.getX(), this.getY() + 0.05, this.getZ(), 10, 0.25, 0.2, 0.25, 0.02);
         }

         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_SPLASH, SoundSource.PLAYERS, 1.0F, 0.9F);
         this.discard();
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "controller", 0, this::animationPredicate));
   }

   private PlayState animationPredicate(AnimationState<ControlledWaterEntity> state) {
      return state.setAndContinue(IDLE_ANIMATION);
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
      return new ClientboundAddEntityPacket(this, entity);
   }
}
