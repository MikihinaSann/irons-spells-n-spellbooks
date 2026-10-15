package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModParticles;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.blood.FuneralBloomSpell;
import io.redspace.ironsspellbooks.api.events.SpellHealEvent;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import java.util.UUID;
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
import net.neoforged.neoforge.common.NeoForge;

public class FuneralBloomBudEntity extends Projectile {
   private static final EntityDataAccessor<Optional<UUID>> DATA_CONTROLLER_UUID = SynchedEntityData.defineId(
      FuneralBloomBudEntity.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Optional<UUID>> DATA_TARGET_UUID = SynchedEntityData.defineId(
      FuneralBloomBudEntity.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Float> DATA_IMPACT_X = SynchedEntityData.defineId(FuneralBloomBudEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DATA_IMPACT_Y = SynchedEntityData.defineId(FuneralBloomBudEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DATA_IMPACT_Z = SynchedEntityData.defineId(FuneralBloomBudEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> DATA_TELEGRAPH_TICKS = SynchedEntityData.defineId(FuneralBloomBudEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_FALL_TICKS = SynchedEntityData.defineId(FuneralBloomBudEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DATA_RETREATING = SynchedEntityData.defineId(FuneralBloomBudEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> DATA_RETREAT_START = SynchedEntityData.defineId(FuneralBloomBudEntity.class, EntityDataSerializers.INT);
   public static final int DEFAULT_FALL_TICKS = 11;
   private static final float IMPACT_RADIUS = 1.75F;
   private static final float WITHERED_HEAL = 1.25F;
   private float impactDamage;
   private int witherDuration = 80;
   private int witherAmplifier;
   private Vec3 fallStart = Vec3.ZERO;
   private boolean impacted;

   public FuneralBloomBudEntity(EntityType<? extends Projectile> type, Level level) {
      super(type, level);
      this.noPhysics = true;
      this.setNoGravity(true);
   }

   public FuneralBloomBudEntity(
      Level level,
      FuneralBloomControllerEntity controller,
      LivingEntity target,
      Vec3 impact,
      int telegraphTicks,
      float impactDamage,
      int witherDuration,
      int witherAmplifier
   ) {
      this((EntityType<? extends Projectile>)ModEntityTypes.FUNERAL_BLOOM_BUD.get(), level);
      this.setOwner(controller.getOwner());
      this.setControllerId(controller.getUUID());
      this.setTargetUuid(target.getUUID());
      this.setImpactPosition(impact);
      this.setTelegraphTicks(telegraphTicks);
      this.setFallTicks(11);
      this.impactDamage = impactDamage;
      this.witherDuration = Math.max(30, witherDuration);
      this.witherAmplifier = Math.max(0, witherAmplifier);
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_CONTROLLER_UUID, Optional.empty());
      builder.define(DATA_TARGET_UUID, Optional.empty());
      builder.define(DATA_IMPACT_X, 0.0F);
      builder.define(DATA_IMPACT_Y, 0.0F);
      builder.define(DATA_IMPACT_Z, 0.0F);
      builder.define(DATA_TELEGRAPH_TICKS, 12);
      builder.define(DATA_FALL_TICKS, 11);
      builder.define(DATA_RETREATING, false);
      builder.define(DATA_RETREAT_START, -1);
   }

   public UUID getControllerId() {
      return ((Optional<UUID>)this.entityData.get(DATA_CONTROLLER_UUID)).orElse(new UUID(0L, 0L));
   }

   public void setControllerId(UUID uuid) {
      this.entityData.set(DATA_CONTROLLER_UUID, Optional.ofNullable(uuid));
   }

   public UUID getTargetUuid() {
      return ((Optional<UUID>)this.entityData.get(DATA_TARGET_UUID)).orElse(new UUID(0L, 0L));
   }

   public void setTargetUuid(UUID uuid) {
      this.entityData.set(DATA_TARGET_UUID, Optional.ofNullable(uuid));
   }

   public Vec3 getImpactPosition() {
      return new Vec3(
         ((Float)this.entityData.get(DATA_IMPACT_X)).floatValue(),
         ((Float)this.entityData.get(DATA_IMPACT_Y)).floatValue(),
         ((Float)this.entityData.get(DATA_IMPACT_Z)).floatValue()
      );
   }

   public void setImpactPosition(Vec3 position) {
      this.entityData.set(DATA_IMPACT_X, (float)position.x);
      this.entityData.set(DATA_IMPACT_Y, (float)position.y);
      this.entityData.set(DATA_IMPACT_Z, (float)position.z);
   }

   public int getTelegraphTicks() {
      return (Integer)this.entityData.get(DATA_TELEGRAPH_TICKS);
   }

   public void setTelegraphTicks(int ticks) {
      this.entityData.set(DATA_TELEGRAPH_TICKS, Math.max(8, ticks));
   }

   public int getFallTicks() {
      return (Integer)this.entityData.get(DATA_FALL_TICKS);
   }

   public void setFallTicks(int ticks) {
      this.entityData.set(DATA_FALL_TICKS, Math.max(6, ticks));
   }

   public boolean isRetreating() {
      return (Boolean)this.entityData.get(DATA_RETREATING);
   }

   public void beginRetreat() {
      if (!this.isRetreating()) {
         this.entityData.set(DATA_RETREATING, true);
         this.entityData.set(DATA_RETREAT_START, this.tickCount);
      }
   }

   public float getTelegraphProgress(float partialTick) {
      return smoothstep(Mth.clamp((this.tickCount + partialTick) / this.getTelegraphTicks(), 0.0F, 1.0F));
   }

   public float getFallProgress(float partialTick) {
      return smoothstep(Mth.clamp((this.tickCount + partialTick - this.getTelegraphTicks()) / this.getFallTicks(), 0.0F, 1.0F));
   }

   public float getRetreatProgress(float partialTick) {
      if (!this.isRetreating()) {
         return 0.0F;
      } else {
         int retreatStart = (Integer)this.entityData.get(DATA_RETREAT_START);
         return smoothstep(Mth.clamp((this.tickCount + partialTick - retreatStart) / 8.0F, 0.0F, 1.0F));
      }
   }

   private static float smoothstep(float value) {
      return value * value * (3.0F - 2.0F * value);
   }

   public void tick() {
      super.tick();
      if (this.level() instanceof ServerLevel level) {
         FuneralBloomControllerEntity controller = this.findController(level);
         if (controller == null || controller.isRemoved()) {
            this.beginRetreat();
         }

         if (this.isRetreating()) {
            this.setDeltaMovement(0.0, 0.22, 0.0);
            this.moveTo(this.position().add(this.getDeltaMovement()));
            if (this.tickCount - (Integer)this.entityData.get(DATA_RETREAT_START) >= 8) {
               this.discard();
            }
         } else if (this.tickCount < this.getTelegraphTicks()) {
            this.updatePrediction(level);
            this.setDeltaMovement(Vec3.ZERO);
         } else {
            if (this.tickCount == this.getTelegraphTicks()) {
               this.fallStart = this.position();
               level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITHER_SHOOT, SoundSource.PLAYERS, 0.72F, 1.35F);
            }

            if (this.fallStart == Vec3.ZERO) {
               this.fallStart = this.position();
            }

            float fall = this.getFallProgress(0.0F);
            Vec3 target = this.getImpactPosition();
            Vec3 curvedTarget = this.fallStart.lerp(target, fall);
            double arc = Math.sin(fall * Math.PI) * 0.55;
            this.setPos(curvedTarget.x, curvedTarget.y + arc, curvedTarget.z);
            this.setDeltaMovement(Vec3.ZERO);
            this.spawnFallingTrail(level, fall);
            if (fall >= 0.995F && !this.impacted) {
               this.impacted = true;
               this.impact(level);
               this.discard();
            }
         }
      }
   }

   private void updatePrediction(ServerLevel level) {
      LivingEntity target = level.getEntity(this.getTargetUuid()) instanceof LivingEntity living ? living : null;
      if (target != null && target.isAlive()) {
         int remaining = this.getTelegraphTicks() - this.tickCount;
         if (remaining > this.getTelegraphTicks() / 2) {
            Vec3 velocity = target.getDeltaMovement();
            Vec3 predicted = target.position().add(velocity.x * Math.min(4, remaining), 0.0, velocity.z * Math.min(4, remaining));
            this.setImpactPosition(FuneralBloomControllerEntity.groundPosition(level, predicted));
         }
      }
   }

   private FuneralBloomControllerEntity findController(ServerLevel level) {
      UUID controllerId = this.getControllerId();
      return level.getEntity(controllerId) instanceof FuneralBloomControllerEntity controller ? controller : null;
   }

   private void spawnFallingTrail(ServerLevel level, float fall) {
      int count = fall > 0.72F ? 4 : 2;
      level.sendParticles(
         (SimpleParticleType)ModParticles.WITHERED_ROSE_PETAL.get(), this.getX(), this.getY() + 0.25, this.getZ(), count, 0.16, 0.12, 0.16, 0.025
      );
   }

   private void impact(ServerLevel level) {
      Vec3 impact = this.getImpactPosition();
      Entity owner = this.getOwner();
      LivingEntity caster = owner instanceof LivingEntity living ? living : null;
      AABB area = new AABB(impact, impact).inflate(1.75, 1.35, 1.75);
      int witheredHits = 0;

      for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area)) {
         if (target.isAlive() && target != caster && (caster == null || !target.isAlliedTo(caster))) {
            double dx = target.getX() - impact.x;
            double dz = target.getZ() - impact.z;
            boolean verticallyAligned = target.getBoundingBox().maxY >= impact.y - 0.35 && target.getBoundingBox().minY <= impact.y + 2.35;
            if (verticallyAligned && !(dx * dx + dz * dz > 3.0625)) {
               boolean alreadyWithered = target.hasEffect(MobEffects.WITHER);
               if (DamageSources.applyDamage(
                  target, this.impactDamage, ((FuneralBloomSpell)ModSpells.FUNERAL_BLOOM.get()).getDamageSource(this, (Entity)(owner != null ? owner : this))
               )) {
                  target.addEffect(new MobEffectInstance(MobEffects.WITHER, this.witherDuration, this.witherAmplifier, false, true, true));
                  target.push(0.0, 0.34, 0.0);
                  target.hurtMarked = true;
                  if (alreadyWithered) {
                     witheredHits++;
                  }
               }
            }
         }
      }

      if (caster != null && witheredHits > 0) {
         float healing = Math.min(3.75F, 1.25F * witheredHits);
         NeoForge.EVENT_BUS.post(new SpellHealEvent(caster, caster, healing, ((FuneralBloomSpell)ModSpells.FUNERAL_BLOOM.get()).getSchoolType()));
         caster.heal(healing);
      }

      float bloomScale = witheredHits > 0 ? 2.45F : 1.85F;
      level.sendParticles(new BlastwaveParticleOptions(0.46F, 0.025F, 0.075F, bloomScale), impact.x, impact.y + 0.08, impact.z, 1, 0.0, 0.0, 0.0, 0.0);
      MagicManager.spawnParticles(level, ParticleHelper.BLOOD, impact.x, impact.y + 0.2, impact.z, witheredHits > 0 ? 12 : 7, 0.42, 0.5, 0.42, 0.08, true);
      level.sendParticles(
         (SimpleParticleType)ModParticles.WITHERED_ROSE_PETAL.get(), impact.x, impact.y + 0.15, impact.z, witheredHits > 0 ? 28 : 18, 0.75, 0.65, 0.75, 0.12
      );
      level.playSound(null, impact.x, impact.y, impact.z, SoundEvents.WITHER_HURT, SoundSource.PLAYERS, 0.78F, witheredHits > 0 ? 1.32F : 1.55F);
      FuneralBloomImpactEntity visual = new FuneralBloomImpactEntity(level, impact, witheredHits > 0 ? 1.28F : 1.0F);
      level.addFreshEntity(visual);
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
      if (tag.hasUUID("Controller")) {
         this.setControllerId(tag.getUUID("Controller"));
      }

      if (tag.hasUUID("Target")) {
         this.setTargetUuid(tag.getUUID("Target"));
      }

      this.setImpactPosition(new Vec3(tag.getDouble("ImpactX"), tag.getDouble("ImpactY"), tag.getDouble("ImpactZ")));
      this.setTelegraphTicks(tag.getInt("TelegraphTicks"));
      this.setFallTicks(tag.getInt("FallTicks"));
      this.entityData.set(DATA_RETREATING, tag.getBoolean("Retreating"));
      this.entityData.set(DATA_RETREAT_START, tag.getInt("RetreatStart"));
      this.impactDamage = tag.getFloat("ImpactDamage");
      this.witherDuration = Math.max(30, tag.getInt("WitherDuration"));
      this.witherAmplifier = Math.max(0, tag.getInt("WitherAmplifier"));
      this.fallStart = new Vec3(tag.getDouble("FallStartX"), tag.getDouble("FallStartY"), tag.getDouble("FallStartZ"));
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
      ((Optional<UUID>)this.entityData.get(DATA_CONTROLLER_UUID)).ifPresent(uuid -> tag.putUUID("Controller", uuid));
      ((Optional<UUID>)this.entityData.get(DATA_TARGET_UUID)).ifPresent(uuid -> tag.putUUID("Target", uuid));
      Vec3 impact = this.getImpactPosition();
      tag.putDouble("ImpactX", impact.x);
      tag.putDouble("ImpactY", impact.y);
      tag.putDouble("ImpactZ", impact.z);
      tag.putInt("TelegraphTicks", this.getTelegraphTicks());
      tag.putInt("FallTicks", this.getFallTicks());
      tag.putBoolean("Retreating", this.isRetreating());
      tag.putInt("RetreatStart", (Integer)this.entityData.get(DATA_RETREAT_START));
      tag.putFloat("ImpactDamage", this.impactDamage);
      tag.putInt("WitherDuration", this.witherDuration);
      tag.putInt("WitherAmplifier", this.witherAmplifier);
      tag.putDouble("FallStartX", this.fallStart.x);
      tag.putDouble("FallStartY", this.fallStart.y);
      tag.putDouble("FallStartZ", this.fallStart.z);
   }
}
