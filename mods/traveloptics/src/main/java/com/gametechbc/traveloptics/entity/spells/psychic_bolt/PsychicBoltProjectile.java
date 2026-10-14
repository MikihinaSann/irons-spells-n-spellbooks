package com.gametechbc.traveloptics.entity.spells.psychic_bolt;

import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.init.TOEntities;
import com.gametechbc.traveloptics.util.TOParticleHelper;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.joml.Vector3f;

public class PsychicBoltProjectile extends AbstractMagicProjectile implements IEntityWithComplexSpawn {
   private int age;
   private static final int EXPIRE_TIME = 40;
   private int effectDuration = 200;
   @Nullable
   Entity cachedHomingTarget;
   @Nullable
   UUID homingTargetUUID;

   public PsychicBoltProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
   }

   public PsychicBoltProjectile(Level pLevel, LivingEntity pShooter) {
      this((EntityType<? extends Projectile>)TOEntities.PSYCHIC_BOLT_PROJECTILE.get(), pLevel);
      this.setOwner(pShooter);
   }

   public void shoot(Vec3 rotation, float inaccuracy) {
      double speed = rotation.length();
      Vec3 offset = Utils.getRandomVec3(1.0).normalize().scale(inaccuracy);
      Vec3 motion = rotation.normalize().add(offset).normalize().scale(speed);
      super.shoot(motion);
   }

   @Nullable
   public Entity getHomingTarget() {
      if (this.cachedHomingTarget != null && !this.cachedHomingTarget.isRemoved()) {
         return this.cachedHomingTarget;
      } else if (this.homingTargetUUID != null && this.level() instanceof ServerLevel) {
         this.cachedHomingTarget = ((ServerLevel)this.level()).getEntity(this.homingTargetUUID);
         return this.cachedHomingTarget;
      } else {
         return null;
      }
   }

   public void setHomingTarget(LivingEntity entity) {
      this.homingTargetUUID = entity.getUUID();
      this.cachedHomingTarget = entity;
   }

   public int getEffectDuration() {
      return this.effectDuration;
   }

   public void setEffectDuration(int duration) {
      this.effectDuration = duration;
   }

   public void tick() {
      super.tick();
      if (++this.age > 40) {
         this.discard();
      } else {
         Entity homingTarget = this.getHomingTarget();
         if (homingTarget != null && !this.doHomingTowards(homingTarget)) {
            this.homingTargetUUID = null;
            this.cachedHomingTarget = null;
         }
      }
   }

   private boolean doHomingTowards(Entity entity) {
      if (entity.isRemoved()) {
         return false;
      } else {
         Vec3 motion = this.getDeltaMovement();
         double speed = this.getDeltaMovement().length();
         Vec3 delta = entity.getBoundingBox().getCenter().subtract(this.position()).add(entity.getDeltaMovement());
         float f = 0.08F;
         Vec3 newMotion = new Vec3(Mth.lerp(f, motion.x, delta.x), Mth.lerp(f, motion.y, delta.y), Mth.lerp(f, motion.z, delta.z)).normalize().scale(speed);
         this.setDeltaMovement(newMotion);
         return this.tickCount <= 10 || !(newMotion.dot(delta) < 0.0);
      }
   }

   protected void onHitEntity(EntityHitResult pResult) {
      if (!this.level().isClientSide) {
         Entity target = pResult.getEntity();
         Entity owner = this.getOwner();
         double radius = 2.0;
         if (target instanceof LivingEntity livingTarget) {
            livingTarget.addEffect(new MobEffectInstance(TOEffects.PSYCHIC_CONTROL, this.getEffectDuration(), 0, false, false, false));
         }

         AABB aoeRegion = new AABB(
            this.getX() - radius, this.getY() - radius, this.getZ() - radius, this.getX() + radius, this.getY() + radius, this.getZ() + radius
         );

         for (Entity entity : this.level().getEntities(this, aoeRegion, entityx -> entityx instanceof LivingEntity)) {
            if (entity != target
               && entity != owner
               && entity instanceof LivingEntity livingEntity
               && !this.isAlly((LivingEntity)owner, livingEntity)
               && !this.isTamed(livingEntity)) {
               livingEntity.addEffect(new MobEffectInstance(TOEffects.PSYCHIC_CONTROL, this.getEffectDuration(), 0, false, false, false));
            }
         }

         MagicManager.spawnParticles(
            this.level(),
            new BlastwaveParticleOptions(new Vector3f(0.1608F, 0.8745F, 0.9216F), 2.0F),
            this.getX(),
            this.getY() + 0.165F,
            this.getZ(),
            1,
            0.0,
            0.0,
            0.0,
            0.0,
            true
         );
      }
   }

   protected void onHitBlock(BlockHitResult pResult) {
      super.onHitBlock(pResult);
   }

   protected void onHit(HitResult pResult) {
      super.onHit(pResult);
   }

   private boolean isAlly(LivingEntity owner, LivingEntity target) {
      return owner.getTeam() != null && owner.getTeam().isAlliedTo(target.getTeam());
   }

   private boolean isTamed(LivingEntity target) {
      return target instanceof TamableAnimal tamableAnimal ? tamableAnimal.isTame() : false;
   }

   public void trailParticles() {
      Vec3 vec3 = this.getDeltaMovement();
      double d0 = this.getX() - vec3.x;
      double d1 = this.getY() - vec3.y;
      double d2 = this.getZ() - vec3.z;
      int count = Mth.clamp((int)(vec3.lengthSqr() * 4.0), 1, 5);

      for (int i = 0; i < count; i++) {
         Vec3 random = Utils.getRandomVec3(0.1);
         float f = (float)i / count;
         double x = Mth.lerp(f, d0, this.getX());
         double y = Mth.lerp(f, d1, this.getY());
         double z = Mth.lerp(f, d2, this.getZ());
         this.level().addParticle(ParticleTypes.SCULK_SOUL, x - random.x, y + 0.5 - random.y, z - random.z, random.x * 0.5, random.y * 0.5, random.z * 0.5);
      }
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level(), TOParticleHelper.PSYCHIC_SPARKS, x, y, z, 15, 0.1, 0.1, 0.1, 0.25, false);
   }

   public float getSpeed() {
      return 1.85F;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.empty();
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      if (this.homingTargetUUID != null) {
         tag.putUUID("homingTarget", this.homingTargetUUID);
      }

      tag.putInt("EffectDuration", this.effectDuration);
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      if (tag.contains("homingTarget", 11)) {
         this.homingTargetUUID = tag.getUUID("homingTarget");
      }

      if (tag.contains("EffectDuration")) {
         this.effectDuration = tag.getInt("EffectDuration");
      }
   }

   public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
      Entity owner = this.getOwner();
      buffer.writeInt(owner == null ? 0 : owner.getId());
      Entity homingTarget = this.getHomingTarget();
      buffer.writeInt(homingTarget == null ? 0 : homingTarget.getId());
   }

   public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
      Entity owner = this.level().getEntity(additionalData.readInt());
      if (owner != null) {
         this.setOwner(owner);
      }

      Entity homingTarget = this.level().getEntity(additionalData.readInt());
      if (homingTarget != null) {
         this.cachedHomingTarget = homingTarget;
         this.homingTargetUUID = homingTarget.getUUID();
      }
   }
}
