package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.lightning.LightningDanceSpell;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.event.EventHooks;

public class LightningLanceDanceProjectile extends AbstractMagicProjectile {
   private static final EntityDataAccessor<Float> FORCED_YAW = SynchedEntityData.defineId(LightningLanceDanceProjectile.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> FORCED_PITCH = SynchedEntityData.defineId(LightningLanceDanceProjectile.class, EntityDataSerializers.FLOAT);
   private int hitPerTicks;
   private int waitTimer = -1;
   private LivingEntity owner;
   BlockPos lastHitBlock;

   public LightningLanceDanceProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
   }

   public LightningLanceDanceProjectile(Level level, LivingEntity owner) {
      this((EntityType<? extends Projectile>)ModEntityTypes.LIGHTNING_LANCE_DANCE_PROJECTILE.get(), level);
      this.setOwner(owner);
      this.owner = owner;
   }

   public void trailParticles() {
      Vec3 vec3 = this.position().subtract(this.getDeltaMovement());
      this.level().addParticle(ParticleTypes.ELECTRIC_SPARK, vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level(), ParticleHelper.ELECTRICITY, x, y, z, 5, 0.6, 0.6, 0.6, 0.2, true);
      MagicManager.spawnParticles(this.level(), ParticleHelper.ELECTRIC_SPARKS, x, y, z, 3, 0.4, 0.4, 0.4, 0.3, true);
   }

   public float getSpeed() {
      return 1.4F;
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FORCED_YAW, 0.0F);
      builder.define(FORCED_PITCH, 0.0F);
   }

   public void setForcedRotation(float yaw, float pitch) {
      this.entityData.set(FORCED_YAW, yaw);
      this.entityData.set(FORCED_PITCH, pitch);
   }

   public void tick() {
      super.tick();
      if (this.getDeltaMovement().lengthSqr() < 1.0E-4) {
         this.setYRot((Float)this.entityData.get(FORCED_YAW));
         this.setXRot((Float)this.entityData.get(FORCED_PITCH));
      }

      this.hitPerTicks = 0;
      if (this.waitTimer > 0) {
         this.waitTimer--;
         if (this.waitTimer <= 110) {
            Vec3 stop = new Vec3(0.0, 0.0, 0.0);
            this.setDeltaMovement(stop);
            if (this.waitTimer <= 100 && this.waitTimer > 10) {
               LivingEntity target = this.owner.getLastHurtMob();
               if (target != null && !target.isDeadOrDying()) {
                  Vec3 attack = target.getBoundingBox().getCenter().subtract(this.position());
                  this.setDeltaMovement(this.getDeltaMovement().add(attack.multiply(1.0, 1.0, 1.0).normalize().scale(2.0)));
               }
            } else if (this.waitTimer == 5) {
               this.doBreaking();
            } else if (this.waitTimer == 0) {
               this.discard();
            }
         }
      }
   }

   public void setWaitTimer(int timerDeath) {
      this.waitTimer = timerDeath;
   }

   protected void onHitBlock(BlockHitResult bResult) {
      super.onHitBlock(bResult);
   }

   private void doBreaking() {
      Vec3 center = this.position();
      MagicManager.spawnParticles(this.level(), ParticleHelper.ELECTRICITY, center.x, center.y, center.z, 10, 0.8, 0.8, 0.8, 0.2, true);
      this.level().playSound(null, BlockPos.containing(this.position()), SoundEvents.ITEM_BREAK, SoundSource.NEUTRAL, 2.0F, 0.5F);
   }

   public void onHitEntity(EntityHitResult entityHitResult) {
      Entity entity = entityHitResult.getEntity();
      DamageSources.applyDamage(entity, this.getDamage(), ((LightningDanceSpell)ModSpells.LIGHTNING_DANCE.get()).getDamageSource(this, this.getOwner()));
      if (this.hitPerTicks++ < 5) {
         HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, x$0 -> this.canHitEntity(x$0));
         if (hitResult.getType() != Type.MISS && !EventHooks.onProjectileImpact(this, hitResult)) {
            this.onHit(hitResult);
         }
      }

      if (this.tickCount > 10) {
         entity.invulnerableTime = 0;
      }
   }

   public void onHit(HitResult result) {
      if (!this.level().isClientSide) {
         BlockPos blockPos = BlockPos.containing(result.getLocation());
         if (result.getType() == Type.BLOCK && !blockPos.equals(this.lastHitBlock)) {
            this.lastHitBlock = blockPos;
            this.discard();
         } else if (result.getType() == Type.ENTITY) {
            this.level()
               .playSound(null, BlockPos.containing(this.position()), (SoundEvent)SoundEvents.TRIDENT_THUNDER.value(), SoundSource.NEUTRAL, 0.5F, 2.0F);
            if (this.tickCount > 10 && result.getType() == Type.ENTITY) {
               this.discard();
            }
         }
      }

      super.onHit(result);
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.empty();
   }

   public void onAntiMagic(MagicData pMagicData) {
      this.discard();
   }

   public boolean shouldPierceShields() {
      return false;
   }
}
