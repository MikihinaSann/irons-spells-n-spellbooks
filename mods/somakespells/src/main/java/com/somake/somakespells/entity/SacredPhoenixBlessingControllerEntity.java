package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModMobEffects;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.holy.SacredPhoenixBlessingSpell;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.UUID;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SacredPhoenixBlessingControllerEntity extends Entity {
   private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(
      SacredPhoenixBlessingControllerEntity.class, EntityDataSerializers.INT
   );
   private static final EntityDataAccessor<Float> RADIUS = SynchedEntityData.defineId(SacredPhoenixBlessingControllerEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> DURATION = SynchedEntityData.defineId(
      SacredPhoenixBlessingControllerEntity.class, EntityDataSerializers.INT
   );
   private static final EntityDataAccessor<Integer> PULSE_SEQUENCE = SynchedEntityData.defineId(
      SacredPhoenixBlessingControllerEntity.class, EntityDataSerializers.INT
   );
   private static final double HOVER_CLEARANCE = 0.65;
   private static final int COLLISION_GRACE_TICKS = 5;
   private LivingEntity caster;
   private UUID casterId;
   private LivingEntity target;
   private UUID targetId;
   private int spellLevel;
   private int duration;
   private int stunDuration;
   private float damage;
   private float radius;
   private int blockedTicks;
   private boolean cleanupComplete;

   public SacredPhoenixBlessingControllerEntity(EntityType<?> entityType, Level level) {
      super(entityType, level);
      this.noPhysics = true;
      this.setNoGravity(true);
   }

   public SacredPhoenixBlessingControllerEntity(
      ServerLevel level, LivingEntity caster, LivingEntity target, int spellLevel, int duration, int stunDuration, float damage, float radius
   ) {
      this((EntityType<?>)ModEntityTypes.SACRED_PHOENIX_BLESSING_CONTROLLER.get(), level);
      this.caster = caster;
      this.casterId = caster.getUUID();
      this.target = target;
      this.targetId = target.getUUID();
      this.spellLevel = spellLevel;
      this.duration = duration;
      this.stunDuration = stunDuration;
      this.damage = damage;
      this.radius = radius;
      this.entityData.set(TARGET_ID, target.getId());
      this.entityData.set(RADIUS, radius);
      this.entityData.set(DURATION, duration);
      this.setPos(target.position());
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(TARGET_ID, -1);
      builder.define(RADIUS, 4.0F);
      builder.define(DURATION, 160);
      builder.define(PULSE_SEQUENCE, 0);
   }

   public void tick() {
      super.tick();
      LivingEntity visualTarget = this.resolveVisualTarget();
      if (visualTarget != null) {
         this.setPos(visualTarget.getX(), visualTarget.getY() + 0.03, visualTarget.getZ());
      }

      if (!this.level().isClientSide) {
         this.resolveEntities();
         if (this.level() instanceof ServerLevel serverLevel && this.isLinkValid()) {
            this.entityData.set(TARGET_ID, this.target.getId());
            this.setPos(this.target.getX(), this.target.getY() + 0.03, this.target.getZ());
            this.holdCasterAboveTarget();
            if (!this.cleanupComplete) {
               this.spawnAmbientParticles(serverLevel);
               if (this.tickCount == 1) {
                  this.spawnActivation(serverLevel);
               }

               int pulseInterval = ((SacredPhoenixBlessingSpell)ModSpells.SACRED_PHOENIX_BLESSING.get()).getPulseInterval();
               if (this.tickCount >= 12 && (this.tickCount - 12) % pulseInterval == 0) {
                  this.pulse(serverLevel);
               }

               if (this.tickCount >= this.duration) {
                  this.finish(true);
               }
            }
         } else {
            this.finish(true);
         }
      }
   }

   private void holdCasterAboveTarget() {
      Vec3 desired = new Vec3(this.target.getX(), this.target.getBoundingBox().maxY + 0.65, this.target.getZ());
      AABB desiredBox = this.caster.getBoundingBox().move(desired.subtract(this.caster.position()));
      if (!this.level().noCollision(this.caster, desiredBox)) {
         if (++this.blockedTicks >= 5) {
            this.finish(true);
         }
      } else {
         this.blockedTicks = 0;
         if (this.caster.getVehicle() != this && !this.caster.startRiding(this, true)) {
            this.finish(true);
         } else {
            this.caster.stopUsingItem();
            this.caster.setSprinting(false);
            this.caster.setShiftKeyDown(false);
            this.caster.setJumping(false);
            this.caster.setDeltaMovement(Vec3.ZERO);
            this.caster.hurtMarked = true;
            this.caster.resetFallDistance();
            if (this.caster instanceof ServerPlayer player) {
               MagicData magicData = MagicData.getPlayerMagicData(player);
               if (magicData.isCasting()) {
                  Utils.serverSideCancelCast(player, false);
               }
            }
         }
      }
   }

   protected void positionRider(Entity passenger, MoveFunction callback) {
      if (this.hasPassenger(passenger)) {
         LivingEntity linkedTarget = this.resolveVisualTarget();
         double passengerY = linkedTarget != null ? linkedTarget.getBoundingBox().maxY + 0.65 : this.getY() + 0.65;
         callback.accept(passenger, this.getX(), passengerY, this.getZ());
      }
   }

   public Vec3 getPassengerRidingPosition(Entity passenger) {
      LivingEntity linkedTarget = this.resolveVisualTarget();
      double passengerY = linkedTarget != null ? linkedTarget.getBoundingBox().maxY + 0.65 : this.getY() + 0.65;
      return new Vec3(this.getX(), passengerY, this.getZ());
   }

   public boolean shouldRiderSit() {
      return false;
   }

   public boolean canRiderInteract() {
      return false;
   }

   public boolean dismountsUnderwater() {
      return false;
   }

   private void pulse(ServerLevel level) {
      int sequence = (Integer)this.entityData.get(PULSE_SEQUENCE) + 1;
      this.entityData.set(PULSE_SEQUENCE, sequence);
      Vec3 center = this.target.position();
      AABB area = this.target.getBoundingBox().inflate(this.radius, 2.5, this.radius);

      for (LivingEntity enemy : level.getEntitiesOfClass(
         LivingEntity.class,
         area,
         entity -> entity.isAlive()
            && entity != this.caster
            && entity != this.target
            && !DamageSources.isFriendlyFireBetween(this.caster, entity)
            && horizontalDistanceSqr(entity.position(), center) <= this.radius * this.radius
      )) {
         if (DamageSources.applyDamage(
            enemy, this.damage, ((SacredPhoenixBlessingSpell)ModSpells.SACRED_PHOENIX_BLESSING.get()).getDamageSource(this, this.caster)
         )) {
            level.sendParticles(ParticleHelper.BLOOD, enemy.getX(), enemy.getY() + enemy.getBbHeight() * 0.5, enemy.getZ(), 14, 0.3, 0.45, 0.3, 0.05);
         }
      }

      MagicManager.spawnParticles(
         level, new BlastwaveParticleOptions(0.92F, 0.04F, 0.08F, this.radius), center.x, center.y + 0.08, center.z, 1, 0.0, 0.0, 0.0, 0.0, true
      );
      MagicManager.spawnParticles(
         level, new BlastwaveParticleOptions(1.0F, 0.55F, 0.58F, this.radius * 0.58F), center.x, center.y + 0.12, center.z, 1, 0.0, 0.0, 0.0, 0.0, true
      );
      level.sendParticles(ParticleHelper.BLOOD, center.x, center.y + 1.0, center.z, 42, this.radius * 0.55, 1.6, this.radius * 0.55, 0.06);
      level.playSound(null, this.target.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 0.6F);
   }

   private void spawnActivation(ServerLevel level) {
      MagicManager.spawnParticles(
         level, new BlastwaveParticleOptions(0.9F, 0.02F, 0.07F, this.radius), this.getX(), this.getY() + 0.08, this.getZ(), 1, 0.0, 0.0, 0.0, 0.0, true
      );
      level.sendParticles(ParticleHelper.BLOOD, this.getX(), this.getY() + 0.8, this.getZ(), 36, this.radius * 0.35, 1.0, this.radius * 0.35, 0.05);
      level.playSound(null, this.target.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.1F, 0.65F);
   }

   private void spawnAmbientParticles(ServerLevel level) {
      if (this.tickCount % 3 == 0) {
         double angle = this.tickCount * 0.25;

         for (int i = 0; i < 4; i++) {
            double current = angle + i * Math.PI * 0.5;
            double x = this.target.getX() + Math.cos(current) * this.radius * 0.82;
            double z = this.target.getZ() + Math.sin(current) * this.radius * 0.82;
            level.sendParticles(ParticleHelper.BLOOD, x, this.target.getY() + 0.18, z, 1, 0.02, 0.04, 0.02, 0.0);
         }
      }
   }

   private boolean isLinkValid() {
      return this.caster != null
         && this.target != null
         && this.caster.isAlive()
         && this.target.isAlive()
         && !this.caster.isRemoved()
         && !this.target.isRemoved()
         && !this.caster.isSpectator()
         && !this.target.isSpectator()
         && this.caster.level() == this.level()
         && this.target.level() == this.level();
   }

   private void resolveEntities() {
      if (this.level() instanceof ServerLevel serverLevel) {
         if (this.caster == null && this.casterId != null && serverLevel.getEntity(this.casterId) instanceof LivingEntity living) {
            this.caster = living;
         }

         if (this.target == null && this.targetId != null && serverLevel.getEntity(this.targetId) instanceof LivingEntity living) {
            this.target = living;
         }
      }
   }

   private LivingEntity resolveVisualTarget() {
      if (this.target != null) {
         return this.target;
      } else {
         return this.level().getEntity((Integer)this.entityData.get(TARGET_ID)) instanceof LivingEntity living ? living : null;
      }
   }

   private static double horizontalDistanceSqr(Vec3 first, Vec3 second) {
      double dx = first.x - second.x;
      double dz = first.z - second.z;
      return dx * dx + dz * dz;
   }

   private void finish(boolean applyStun) {
      if (!this.cleanupComplete) {
         this.cleanupComplete = true;
         if (this.caster != null && this.caster.isAlive()) {
            this.caster.stopRiding();
            this.caster.setDeltaMovement(Vec3.ZERO);
            this.caster.hurtMarked = true;
            this.caster.resetFallDistance();
            if (applyStun && this.stunDuration > 0) {
               this.caster.addEffect(new MobEffectInstance(ModMobEffects.SACRED_PHOENIX_EXHAUSTION, this.stunDuration, 0, false, true, true));
            }
         }

         this.discard();
      }
   }

   public UUID getCasterId() {
      return this.casterId;
   }

   public static boolean hasActiveLink(ServerLevel level, LivingEntity caster) {
      for (Entity entity : level.getAllEntities()) {
         if (entity instanceof SacredPhoenixBlessingControllerEntity controller && caster.getUUID().equals(controller.getCasterId())) {
            return true;
         }
      }

      return false;
   }

   public float getRadius() {
      return (Float)this.entityData.get(RADIUS);
   }

   public int getPulseSequence() {
      return (Integer)this.entityData.get(PULSE_SEQUENCE);
   }

   public AABB getBoundingBoxForCulling() {
      double range = ((Float)this.entityData.get(RADIUS)).floatValue() + 2.0;
      return this.getBoundingBox().inflate(range, 12.0, range);
   }

   public boolean shouldRenderAtSqrDistance(double distance) {
      return distance < 16384.0;
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
      if (tag.hasUUID("Caster")) {
         this.casterId = tag.getUUID("Caster");
      }

      if (tag.hasUUID("Target")) {
         this.targetId = tag.getUUID("Target");
      }

      this.spellLevel = tag.getInt("SpellLevel");
      this.duration = tag.getInt("Duration");
      this.stunDuration = tag.getInt("StunDuration");
      this.damage = tag.getFloat("Damage");
      this.radius = tag.getFloat("Radius");
      this.entityData.set(RADIUS, this.radius);
      this.entityData.set(DURATION, this.duration);
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
      if (this.casterId != null) {
         tag.putUUID("Caster", this.casterId);
      }

      if (this.targetId != null) {
         tag.putUUID("Target", this.targetId);
      }

      tag.putInt("SpellLevel", this.spellLevel);
      tag.putInt("Duration", this.duration);
      tag.putInt("StunDuration", this.stunDuration);
      tag.putFloat("Damage", this.damage);
      tag.putFloat("Radius", this.radius);
   }

   public void remove(RemovalReason reason) {
      if (!this.level().isClientSide && !this.cleanupComplete) {
         this.finish(true);
      } else {
         super.remove(reason);
      }
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return new ClientboundAddEntityPacket(this, serverEntity);
   }
}
