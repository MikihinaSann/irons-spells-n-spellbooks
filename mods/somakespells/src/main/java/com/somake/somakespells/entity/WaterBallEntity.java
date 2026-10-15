package com.somake.somakespells.entity;

import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.somake.somakespells.network.WaterBallExplosionVfxPacket;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModMobEffects;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.aqua.WaterBallSpell;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
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
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.network.PacketDistributor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WaterBallEntity extends Projectile implements GeoEntity {
   private static final int IMPACT_VISUAL_TICKS = 16;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private static final EntityDataAccessor<Boolean> LAUNCHED = SynchedEntityData.defineId(WaterBallEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(WaterBallEntity.class, EntityDataSerializers.OPTIONAL_UUID);
   private float damage;
   private float radius;
   private LivingEntity cachedOwner;
   private boolean impacted;
   private int impactTickCount;

   public WaterBallEntity(EntityType<? extends Projectile> type, Level level) {
      super(type, level);
      this.setNoGravity(true);
   }

   public WaterBallEntity(Level level, LivingEntity owner, float damage, float radius) {
      this((EntityType<? extends Projectile>)ModEntityTypes.WATER_BALL.get(), level);
      this.setOwner(owner);
      this.damage = damage;
      this.radius = radius;
      this.entityData.set(OWNER_UUID, Optional.of(owner.getUUID()));
      this.setPos(owner.getX(), owner.getY() + 2.0, owner.getZ());
   }

   public void setDamage(float damage) {
      this.damage = damage;
   }

   public void setRadius(float radius) {
      this.radius = radius;
   }

   public void tick() {
      if (this.impacted) {
         if (!this.level().isClientSide) {
            this.impactTickCount++;
            if (this.impactTickCount >= 16) {
               this.discard();
            }
         }
      } else {
         super.tick();
         if (this.level().isClientSide) {
            for (int i = 0; i < 3; i++) {
               this.level()
                  .addParticle(
                     ParticleTypes.FALLING_WATER,
                     this.getX() + (this.random.nextDouble() - 0.5) * 1.0,
                     this.getY() + (this.random.nextDouble() - 0.5) * 1.0,
                     this.getZ() + (this.random.nextDouble() - 0.5) * 1.0,
                     0.0,
                     0.0,
                     0.0
                  );
            }
         }

         if (!(Boolean)this.entityData.get(LAUNCHED)) {
            LivingEntity owner = this.getOwnerEntity();
            if (owner != null) {
               if (!this.level().isClientSide) {
                  MagicData magicData = MagicData.getPlayerMagicData(owner);
                  if (!magicData.isCasting()
                     || magicData.getCastingSpell().getSpell().getSpellResource() != ((WaterBallSpell)ModSpells.WATER_BALL.get()).getSpellResource()) {
                     this.discard();
                     return;
                  }
               }

               if (this.level().isClientSide) {
                  float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
                  double x = Mth.lerp(partialTick, owner.xo, owner.getX());
                  double y = Mth.lerp(partialTick, owner.yo, owner.getY()) + 2.5;
                  double z = Mth.lerp(partialTick, owner.zo, owner.getZ());
                  float yRot = Mth.lerp(partialTick, owner.yRotO, owner.getYRot());
                  float xRot = Mth.lerp(partialTick, owner.xRotO, owner.getXRot());
                  this.setPos(owner.getX(), owner.getY() + 2.5, owner.getZ());
                  this.setYRot(owner.getYRot());
                  this.setXRot(owner.getXRot());
                  this.xo = this.getX();
                  this.yo = this.getY();
                  this.zo = this.getZ();
                  this.xOld = this.getX();
                  this.yOld = this.getY();
                  this.zOld = this.getZ();
                  this.yRotO = this.getYRot();
                  this.xRotO = this.getXRot();
               } else {
                  this.setPos(owner.getX(), owner.getY() + 2.5, owner.getZ());
                  this.setYRot(owner.getYRot());
                  this.setXRot(owner.getXRot());
               }

               this.setDeltaMovement(Vec3.ZERO);
            } else if (!this.level().isClientSide) {
               this.discard();
            }
         } else {
            if (!this.level().isClientSide) {
               HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, x$0 -> this.canHitEntity(x$0));
               if (hitresult.getType() != Type.MISS) {
                  this.onHit(hitresult);
               }
            }

            this.setPos(this.getX() + this.getDeltaMovement().x, this.getY() + this.getDeltaMovement().y, this.getZ() + this.getDeltaMovement().z);
            if (!this.level().isClientSide && this.tickCount > 200) {
               this.discard();
            }
         }
      }
   }

   public void launch(LivingEntity owner) {
      this.entityData.set(LAUNCHED, true);
      Vec3 look = owner.getLookAngle();
      this.setDeltaMovement(look.scale(2.0));
   }

   private LivingEntity getOwnerEntity() {
      if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
         return this.cachedOwner;
      } else {
         Optional<UUID> uuid = (Optional<UUID>)this.entityData.get(OWNER_UUID);
         if (uuid.isPresent()) {
            if (this.level() instanceof ServerLevel serverLevel) {
               Entity entity = serverLevel.getEntity(uuid.get());
               if (entity instanceof LivingEntity) {
                  this.cachedOwner = (LivingEntity)entity;
                  return this.cachedOwner;
               }
            } else if (this.level().isClientSide) {
               Entity entity = this.level().getPlayerByUUID(uuid.get());
               if (entity instanceof LivingEntity) {
                  this.cachedOwner = (LivingEntity)entity;
                  return this.cachedOwner;
               }
            }
         }

         return null;
      }
   }

   protected void onHit(HitResult result) {
      if (!this.impacted) {
         super.onHit(result);
         if (!this.level().isClientSide) {
            Vec3 impactLocation = result.getLocation();
            this.impacted = true;
            this.setPos(impactLocation.x, impactLocation.y, impactLocation.z);
            this.setDeltaMovement(Vec3.ZERO);
            this.setInvisible(true);
            if (this.level() instanceof ServerLevel serverLevel) {
               ScreenShake_Entity.ScreenShake(serverLevel, this.position(), 20.0F, 0.2F, 0, 20);
               serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_SPLASH_HIGH_SPEED, SoundSource.PLAYERS, 2.0F, 0.8F);
               WaterSplashEntity splash = new WaterSplashEntity((EntityType<?>)ModEntityTypes.WATER_SPLASH.get(), this.level());
               splash.moveTo(this.getX(), this.getY(), this.getZ());
               splash.setAnimationState(2);
               this.level().addFreshEntity(splash);
               this.spawnImpactVisuals(serverLevel, impactLocation);
            }

            float radius = this.radius;
            float radiusSqr = radius * radius;
            List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(radius));
            LivingEntity owner = this.getOwnerEntity();

            for (LivingEntity target : targets) {
               if (target != owner && (owner == null || !target.isAlliedTo(owner))) {
                  double distanceSqr = target.distanceToSqr(impactLocation);
                  if (!(distanceSqr >= radiusSqr)) {
                     float scaledDamage = (float)(this.damage * (1.0 - distanceSqr / radiusSqr));
                     if (!(scaledDamage <= 0.0F)
                        && DamageSources.applyDamage(
                           target, scaledDamage, ((WaterBallSpell)ModSpells.WATER_BALL.get()).getDamageSource(this, (Entity)(owner != null ? owner : this))
                        )) {
                        target.addEffect(new MobEffectInstance(ModMobEffects.WET, 100, 0, false, false, true));
                     }
                  }
               }
            }
         }
      }
   }

   private void spawnImpactVisuals(ServerLevel serverLevel, Vec3 impactLocation) {
      float blastwaveRadius = Math.max(1.35F, this.radius * 0.82F);
      float innerBlastwaveRadius = Math.max(0.8F, this.radius * 0.5F);
      serverLevel.sendParticles(
         new BlastwaveParticleOptions(0.18F, 0.68F, 1.0F, blastwaveRadius), impactLocation.x, impactLocation.y + 0.08, impactLocation.z, 1, 0.0, 0.0, 0.0, 0.0
      );
      serverLevel.sendParticles(
         new BlastwaveParticleOptions(0.62F, 0.92F, 1.0F, innerBlastwaveRadius),
         impactLocation.x,
         impactLocation.y + 0.12,
         impactLocation.z,
         1,
         0.0,
         0.0,
         0.0,
         0.0
      );
      PacketDistributor.sendToPlayersTrackingEntity(this, new WaterBallExplosionVfxPacket(impactLocation, this.radius), new CustomPacketPayload[0]);
      serverLevel.sendParticles(
         ParticleTypes.SPLASH, impactLocation.x, impactLocation.y + 0.15, impactLocation.z, 36, this.radius * 0.2, 0.12, this.radius * 0.2, 0.12
      );
      serverLevel.sendParticles(
         ParticleTypes.BUBBLE_POP, impactLocation.x, impactLocation.y + 0.18, impactLocation.z, 20, this.radius * 0.18, 0.1, this.radius * 0.18, 0.05
      );
      int spokes = Math.max(36, (int)(6.28F * this.radius) * 4);

      for (int i = 0; i < spokes; i++) {
         double angle = (Math.PI * 2) * i / spokes;
         Vec3 radial = new Vec3(Math.cos(angle), 0.0, Math.sin(angle));
         Vec3 lowStart = impactLocation.add(radial.scale(0.2));
         Vec3 lowVelocity = radial.scale(0.42 + this.radius * 0.08).add(0.0, 0.1 + this.random.nextDouble() * 0.05, 0.0);
         serverLevel.sendParticles(ParticleTypes.SPLASH, lowStart.x, lowStart.y + 0.08, lowStart.z, 0, lowVelocity.x, lowVelocity.y, lowVelocity.z, 1.0);
         Vec3 sheetStart = impactLocation.add(radial.scale(0.35));
         Vec3 sheetVelocity = radial.scale(0.26 + this.radius * 0.06).add(0.0, 0.18 + this.random.nextDouble() * 0.08, 0.0);
         serverLevel.sendParticles(
            ParticleTypes.SPLASH, sheetStart.x, sheetStart.y + 0.28, sheetStart.z, 0, sheetVelocity.x, sheetVelocity.y, sheetVelocity.z, 1.0
         );
         if ((i & 1) == 0) {
            Vec3 crestStart = impactLocation.add(radial.scale(0.45));
            Vec3 crestVelocity = radial.scale(0.18 + this.radius * 0.04).add(0.0, 0.28 + this.random.nextDouble() * 0.08, 0.0);
            serverLevel.sendParticles(
               ParticleTypes.SPLASH, crestStart.x, crestStart.y + 0.55, crestStart.z, 0, crestVelocity.x, crestVelocity.y, crestVelocity.z, 1.0
            );
         }
      }

      int groundRingPoints = 36;
      double groundRingDistance = this.radius * 0.62 + 0.3;

      for (int ix = 0; ix < groundRingPoints; ix++) {
         double angle = (Math.PI * 2) * ix / groundRingPoints;
         Vec3 radial = new Vec3(Math.cos(angle), 0.0, Math.sin(angle));
         Vec3 ringPoint = impactLocation.add(radial.scale(groundRingDistance));
         Vec3 ringVelocity = radial.scale(0.16 + this.radius * 0.04);
         serverLevel.sendParticles(ParticleTypes.SPLASH, ringPoint.x, ringPoint.y + 0.03, ringPoint.z, 0, ringVelocity.x, 0.02, ringVelocity.z, 1.0);
      }
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(LAUNCHED, false);
      builder.define(OWNER_UUID, Optional.empty());
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      if (compound.hasUUID("Owner")) {
         this.entityData.set(OWNER_UUID, Optional.of(compound.getUUID("Owner")));
      }

      this.entityData.set(LAUNCHED, compound.getBoolean("Launched"));
      this.damage = compound.getFloat("Damage");
      this.radius = compound.getFloat("Radius");
      this.impacted = compound.getBoolean("Impacted");
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      ((Optional<UUID>)this.entityData.get(OWNER_UUID)).ifPresent(uuid -> compound.putUUID("Owner", uuid));
      compound.putBoolean("Launched", (Boolean)this.entityData.get(LAUNCHED));
      compound.putFloat("Damage", this.damage);
      compound.putFloat("Radius", this.radius);
      compound.putBoolean("Impacted", this.impacted);
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController(this, "controller", 0, this::predicate));
   }

   private PlayState predicate(AnimationState<WaterBallEntity> event) {
      if ((Boolean)this.entityData.get(LAUNCHED)) {
         return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
      } else {
         return this.tickCount < 20
            ? event.setAndContinue(RawAnimation.begin().thenPlay("start"))
            : event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
      }
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
      return new ClientboundAddEntityPacket(this, entity);
   }
}
