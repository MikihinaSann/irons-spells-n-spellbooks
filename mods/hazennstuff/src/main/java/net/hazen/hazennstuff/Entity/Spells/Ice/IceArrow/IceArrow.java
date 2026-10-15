package net.hazen.hazennstuff.Entity.Spells.Ice.IceArrow;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import net.hazen.hazennstuff.Entity.Spells.Ice.IceCloud.IceCloud;
import net.hazen.hazennstuff.Registries.HnSEntityRegistry;
import net.hazen.hazennstuff.Spells.HnSSpellRegistries;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class IceArrow extends AbstractMagicProjectile {
   private static final EntityDataAccessor<Boolean> IN_GROUND = SynchedEntityData.defineId(IceArrow.class, EntityDataSerializers.BOOLEAN);
   public int shakeTime;
   protected boolean hasEmittedPoison;
   protected boolean inGround;
   protected float aoeDamage;

   public IceArrow(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public IceArrow(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends Projectile>)HnSEntityRegistry.ICE_ARROW.get(), levelIn);
      this.setOwner(shooter);
   }

   public void tick() {
      if (this.shakeTime > 0) {
         this.shakeTime--;
      }

      if (!this.inGround) {
         super.tick();
      } else {
         if (this.tickCount > 300) {
            this.discard();
            return;
         }

         if (this.shouldFall()) {
            this.inGround = false;
            this.setDeltaMovement(this.getDeltaMovement().normalize().scale(0.05F));
         }
      }
   }

   protected void defineSynchedData(Builder pBuilder) {
      super.defineSynchedData(pBuilder);
      pBuilder.define(IN_GROUND, false);
   }

   public void setAoeDamage(float damage) {
      this.aoeDamage = damage;
   }

   public float getAoeDamage() {
      return this.aoeDamage;
   }

   private boolean shouldFall() {
      return this.inGround && this.level.noCollision(new AABB(this.position(), this.position()).inflate(0.06));
   }

   protected void onHitBlock(BlockHitResult pResult) {
      super.onHitBlock(pResult);
      Vec3 vec3 = pResult.getLocation().subtract(this.getX(), this.getY(), this.getZ());
      this.setDeltaMovement(vec3);
      Vec3 vec31 = vec3.normalize().scale(0.05F);
      this.setPosRaw(this.getX() - vec31.x, this.getY() - vec31.y, this.getZ() - vec31.z);
      this.playSound(SoundEvents.ARROW_HIT, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
      this.inGround = true;
      this.shakeTime = 7;
      if (!this.level.isClientSide && !this.hasEmittedPoison) {
         this.createIceCloud(pResult.getLocation());
      }
   }

   protected void onHitEntity(EntityHitResult entityHitResult) {
      if (!this.level.isClientSide) {
         Entity entity = entityHitResult.getEntity();
         boolean hit = DamageSources.applyDamage(entity, this.getDamage(), HnSSpellRegistries.ICE_ARROW.get().getDamageSource(this, this.getOwner()));
         boolean ignore = entity.getType() == EntityType.ENDERMAN;
         if (hit) {
            if (!ignore) {
               if (!this.level.isClientSide && !this.hasEmittedPoison) {
                  this.createIceCloud(entity.position());
               }

               if (entity instanceof LivingEntity livingEntity) {
                  livingEntity.setArrowCount(livingEntity.getArrowCount() + 1);
               }
            }

            this.discard();
         } else {
            this.setDeltaMovement(this.getDeltaMovement().scale(-0.1));
            this.setYRot(this.getYRot() + 180.0F);
            this.yRotO += 180.0F;
         }
      }
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putBoolean("inGround", this.inGround);
      tag.putBoolean("hasEmittedFreezing", this.hasEmittedPoison);
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
         this.hasEmittedPoison = true;
      }
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.inGround = tag.getBoolean("inGround");
      this.hasEmittedPoison = tag.getBoolean("hasEmittedFreezing");
      this.aoeDamage = tag.getFloat("aoeDamage");
   }

   public void trailParticles() {
      Vec3 vec3 = this.position().subtract(this.getDeltaMovement().scale(2.0));
      this.level.addParticle(ParticleHelper.SNOW_DUST, vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level, ParticleHelper.SNOWFLAKE, x, y, z, 15, 0.03, 0.03, 0.03, 0.2, true);
   }

   public float getSpeed() {
      return 2.5F;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.empty();
   }
}
