package com.somake.somakespells.entity;

import com.somake.somakespells.compat.TunesCompat;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.sound.JingleBellSpell;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.UUID;
import net.minecraft.core.particles.ParticleOptions;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BellEntity extends Entity implements GeoEntity {
   private static final int STARTUP_TICKS = 15;
   private static final int DEFAULT_LIFETIME = 180;
   private static final int DEFAULT_PULSE_INTERVAL = 28;
   private static final int RING_ANIMATION_TICKS = 16;
   private static final int FINALE_ANIMATION_TICKS = 10;
   private static final EntityDataAccessor<Integer> DATA_RING_TICKS = SynchedEntityData.defineId(BellEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_RING_ID = SynchedEntityData.defineId(BellEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_FINALE_TICKS = SynchedEntityData.defineId(BellEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_FINALE_ID = SynchedEntityData.defineId(BellEntity.class, EntityDataSerializers.INT);
   private static final RawAnimation START_ANIMATION = RawAnimation.begin().thenPlay("start");
   private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");
   private static final RawAnimation RING_ANIMATION = RawAnimation.begin().thenPlay("ring");
   private static final RawAnimation FINALE_ANIMATION = RawAnimation.begin().thenPlay("finale");
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private UUID ownerUuid;
   private float radius = 4.0F;
   private float damagePerPulse = 4.0F;
   private int lifetime = 180;
   private int pulseInterval = 28;
   private int lastSeenRingId = -1;
   private int lastSeenFinaleId = -1;
   private boolean finaleTriggered;

   public BellEntity(EntityType<?> entityType, Level level) {
      super(entityType, level);
      this.noPhysics = true;
   }

   public BellEntity(Level level, LivingEntity owner, double x, double y, double z, float radius, float damagePerPulse, int lifetime, int pulseInterval) {
      this((EntityType<?>)ModEntityTypes.BELL.get(), level);
      this.ownerUuid = owner != null ? owner.getUUID() : null;
      this.radius = radius;
      this.damagePerPulse = damagePerPulse;
      this.lifetime = Math.max(25, lifetime);
      this.pulseInterval = Math.max(8, pulseInterval);
      this.setPos(x, y, z);
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_RING_TICKS, 0);
      builder.define(DATA_RING_ID, 0);
      builder.define(DATA_FINALE_TICKS, 0);
      builder.define(DATA_FINALE_ID, 0);
   }

   public void tick() {
      super.tick();
      if (this.level() instanceof ServerLevel serverLevel) {
         if ((Integer)this.entityData.get(DATA_RING_TICKS) > 0) {
            this.entityData.set(DATA_RING_TICKS, (Integer)this.entityData.get(DATA_RING_TICKS) - 1);
         }

         if ((Integer)this.entityData.get(DATA_FINALE_TICKS) > 0) {
            this.entityData.set(DATA_FINALE_TICKS, (Integer)this.entityData.get(DATA_FINALE_TICKS) - 1);
         }

         if (this.tickCount == 1) {
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 1.8F, 0.9F);
            this.spawnStartupParticles(serverLevel);
         }

         if (!this.finaleTriggered && this.tickCount >= this.lifetime - 10) {
            this.triggerFinale(serverLevel);
         }

         if (!this.finaleTriggered && this.tickCount > 15 && (this.tickCount - 15) % this.pulseInterval == 0) {
            this.ring(serverLevel);
         }

         if (this.tickCount >= this.lifetime) {
            this.discard();
         }
      }
   }

   private void spawnStartupParticles(ServerLevel serverLevel) {
      MagicManager.spawnParticles(
         serverLevel,
         new BlastwaveParticleOptions(0.94F, 0.84F, 0.36F, this.radius * 0.5F),
         this.getX(),
         this.getY() + 0.15,
         this.getZ(),
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      MagicManager.spawnParticles(serverLevel, ParticleHelper.CLEANSE_PARTICLE, this.getX(), this.getY(), this.getZ(), 5, 0.2, 1.25, 0.2, 0.08, false);
   }

   private void ring(ServerLevel serverLevel) {
      this.entityData.set(DATA_RING_TICKS, 16);
      this.entityData.set(DATA_RING_ID, (Integer)this.entityData.get(DATA_RING_ID) + 1);
      serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 2.2F, 1.0F);
      MagicManager.spawnParticles(
         serverLevel, new BlastwaveParticleOptions(0.98F, 0.92F, 0.48F, this.radius), this.getX(), this.getY() + 0.12, this.getZ(), 1, 0.0, 0.0, 0.0, 0.0, true
      );
      int points = Math.max(18, (int)(this.radius * 10.0F));

      for (int i = 0; i < points; i++) {
         double angle = (Math.PI * 2) * i / points;
         double x = this.getX() + Math.cos(angle) * this.radius;
         double z = this.getZ() + Math.sin(angle) * this.radius;
         serverLevel.sendParticles(ParticleHelper.CLEANSE_PARTICLE, x, this.getY() + 0.35, z, 1, 0.04, 0.1, 0.04, 0.0);
      }

      MagicManager.spawnParticles(serverLevel, ParticleHelper.CLEANSE_PARTICLE, this.getX(), this.getY(), this.getZ(), 5, 0.2, 1.25, 0.2, 0.08, false);
      this.pulseDamage(serverLevel);
   }

   private void triggerFinale(ServerLevel serverLevel) {
      this.finaleTriggered = true;
      this.entityData.set(DATA_FINALE_TICKS, 10);
      this.entityData.set(DATA_FINALE_ID, (Integer)this.entityData.get(DATA_FINALE_ID) + 1);
      this.entityData.set(DATA_RING_TICKS, 0);
      TunesCompat.getHopeBurstSound()
         .ifPresent(soundEvent -> serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), soundEvent, SoundSource.PLAYERS, 1.7F, 1.0F));
      ParticleOptions finaleParticle = TunesCompat.getHarpExplosionParticle();
      serverLevel.sendParticles(finaleParticle, this.getX(), this.getY() + 6.0, this.getZ(), 1, 0.1, 0.0, 0.1, 1.0E-4);
      MagicManager.spawnParticles(serverLevel, ParticleHelper.CLEANSE_PARTICLE, this.getX(), this.getY() + 0.2, this.getZ(), 12, 0.25, 0.55, 0.25, 0.08, false);
   }

   private void pulseDamage(ServerLevel serverLevel) {
      LivingEntity owner = this.getOwnerEntity(serverLevel);
      AABB area = this.getBoundingBox().inflate(this.radius, 2.5, this.radius);

      for (LivingEntity target : serverLevel.getEntitiesOfClass(
         LivingEntity.class, area, entity -> entity.isAlive() && entity != owner && (owner == null || !entity.isAlliedTo(owner))
      )) {
         double dx = target.getX() - this.getX();
         double dz = target.getZ() - this.getZ();
         if (!(dx * dx + dz * dz > this.radius * this.radius)) {
            boolean damaged = DamageSources.applyDamage(
               target, this.damagePerPulse, ((JingleBellSpell)ModSpells.JINGLE_BELL.get()).getDamageSource(this, (Entity)(owner != null ? owner : this))
            );
            if (damaged) {
               Vec3 knockback = target.position().subtract(this.position());
               if (knockback.lengthSqr() > 1.0E-4) {
                  knockback = knockback.normalize().scale(0.35);
                  target.push(knockback.x, 0.12, knockback.z);
                  target.hurtMarked = true;
               }
            }
         }
      }
   }

   private LivingEntity getOwnerEntity(ServerLevel serverLevel) {
      if (this.ownerUuid == null) {
         return null;
      } else {
         return serverLevel.getEntity(this.ownerUuid) instanceof LivingEntity livingEntity ? livingEntity : null;
      }
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      this.ownerUuid = compound.hasUUID("Owner") ? compound.getUUID("Owner") : null;
      this.radius = compound.getFloat("Radius");
      this.damagePerPulse = compound.getFloat("DamagePerPulse");
      this.lifetime = compound.getInt("Lifetime");
      this.pulseInterval = compound.getInt("PulseInterval");
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      if (this.ownerUuid != null) {
         compound.putUUID("Owner", this.ownerUuid);
      }

      compound.putFloat("Radius", this.radius);
      compound.putFloat("DamagePerPulse", this.damagePerPulse);
      compound.putInt("Lifetime", this.lifetime);
      compound.putInt("PulseInterval", this.pulseInterval);
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "controller", 0, this::predicate));
   }

   private PlayState predicate(AnimationState<BellEntity> event) {
      if (this.tickCount < 15) {
         return event.setAndContinue(START_ANIMATION);
      } else {
         int finaleId = (Integer)this.entityData.get(DATA_FINALE_ID);
         if (finaleId != this.lastSeenFinaleId) {
            this.lastSeenFinaleId = finaleId;
            if (finaleId > 0) {
               event.getController().forceAnimationReset();
               return event.setAndContinue(FINALE_ANIMATION);
            }
         }

         if ((Integer)this.entityData.get(DATA_FINALE_TICKS) > 0) {
            return event.setAndContinue(FINALE_ANIMATION);
         } else {
            int ringId = (Integer)this.entityData.get(DATA_RING_ID);
            if (ringId != this.lastSeenRingId) {
               this.lastSeenRingId = ringId;
               if (ringId > 0) {
                  event.getController().forceAnimationReset();
                  return event.setAndContinue(RING_ANIMATION);
               }
            }

            return this.entityData.get(DATA_RING_TICKS) > 0 ? event.setAndContinue(RING_ANIMATION) : event.setAndContinue(IDLE_ANIMATION);
         }
      }
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return new ClientboundAddEntityPacket(this, serverEntity);
   }
}
