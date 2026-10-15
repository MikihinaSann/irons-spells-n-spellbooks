package net.hazen.hazennstuff.Entity.Spells.Ice.Hailstorm.IceChunkB;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import net.hazen.hazennstuff.Entity.Spells.Ice.IceCloud.IceCloud;
import net.hazen.hazennstuff.Registries.HnSEntityRegistry;
import net.hazen.hazennstuff.Spells.HnSSpellRegistries;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class IceChunkB extends AbstractMagicProjectile implements GeoEntity {
   public int shakeTime;
   protected boolean hasEmittedFreezing;
   protected float aoeDamage;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private final RawAnimation idle = RawAnimation.begin().thenLoop("animation.ice_chunk.spinning_a");

   public IceChunkB(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
   }

   public IceChunkB(Level pLevel, LivingEntity pShooter) {
      this((EntityType<? extends Projectile>)HnSEntityRegistry.ICE_CHUNK_B.get(), pLevel);
      this.setOwner(pShooter);
   }

   public void shoot(Vec3 rotation, float innaccuracy) {
      Vec3 offset = Utils.getRandomVec3(1.0).normalize().scale(innaccuracy);
      super.shoot(rotation.add(offset));
   }

   public void trailParticles() {
      Vec3 vec = this.getDeltaMovement();
      double length = vec.length();
      int count = (int)Math.min(20L, Math.round(length) * 4L) + 1;
      float f = (float)length / count;

      for (int i = 0; i < count; i++) {
         Vec3 random = Utils.getRandomVec3(0.04);
         Vec3 p = vec.scale(f * i);
         this.level
            .addParticle(
               ParticleHelper.SNOW_DUST, this.getX() + random.x + p.x, this.getY() + random.y + p.y, this.getZ() + random.z + p.z, random.x, random.y, random.z
            );
      }
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(
         this.level,
         new BlastwaveParticleOptions(HnSSpellRegistries.HAILSTORM.get().getSchoolType().getTargetingColor(), 1.25F),
         x,
         y,
         z,
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      MagicManager.spawnParticles(this.level, ParticleHelper.SNOWFLAKE, x, y, z, 15, 0.03, 0.03, 0.03, 0.2, true);
   }

   public float getSpeed() {
      return 1.85F;
   }

   protected void doImpactSound(Holder<SoundEvent> sound) {
      this.level.playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.NEUTRAL, 0.8F, 1.35F + Utils.random.nextFloat() * 0.3F);
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(SoundRegistry.ICE_IMPACT);
   }

   protected void onHit(HitResult hitResult) {
      if (!this.level.isClientSide) {
         this.impactParticles(this.xOld, this.yOld, this.zOld);
         this.getImpactSound().ifPresent(this::doImpactSound);
         float explosionRadius = this.getExplosionRadius();

         for (Entity entity : this.level.getEntities(this, this.getBoundingBox().inflate(explosionRadius))) {
            double distance = entity.distanceToSqr(hitResult.getLocation());
            if (distance < explosionRadius * explosionRadius && this.canHitEntity(entity)) {
               DamageSources.applyDamage(entity, this.damage, HnSSpellRegistries.HAILSTORM.get().getDamageSource(this, this.getOwner()));
            }
         }

         this.discardHelper(hitResult);
      }
   }

   protected void onHitEntity(EntityHitResult entityHitResult) {
      if (!this.level.isClientSide) {
         Entity entity = entityHitResult.getEntity();
         boolean hit = DamageSources.applyDamage(entity, this.getDamage(), HnSSpellRegistries.HAILSTORM.get().getDamageSource(this, this.getOwner()));
         boolean ignore = entity.getType() == EntityType.ENDERMAN;
         if (hit) {
            if (!ignore && !this.level.isClientSide && !this.hasEmittedFreezing) {
               this.createIceCloud(entity.position());
            }

            this.discard();
         }
      }
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putBoolean("hasEmittedFreezing", this.hasEmittedFreezing);
      tag.putFloat("aoeDamage", this.aoeDamage);
   }

   public void createIceCloud(Vec3 location) {
      if (!this.level.isClientSide) {
         IceCloud cloud = new IceCloud(this.level);
         cloud.setOwner(this.getOwner());
         cloud.setDuration(200);
         cloud.setDamage(this.aoeDamage);
         cloud.moveTo(location);
         this.level.addFreshEntity(cloud);
         this.hasEmittedFreezing = true;
      }
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.hasEmittedFreezing = tag.getBoolean("hasEmittedFreezing");
      this.aoeDamage = tag.getFloat("aoeDamage");
   }

   private PlayState predicate(AnimationState event) {
      event.getController().setAnimation(this.idle);
      return PlayState.CONTINUE;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController(this, "controller", 0, this::predicate));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
