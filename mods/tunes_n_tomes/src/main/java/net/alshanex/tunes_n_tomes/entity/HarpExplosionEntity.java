package net.alshanex.tunes_n_tomes.entity;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.entity.spells.EarthquakeAoe;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.alshanex.tunes_n_tomes.registry.TEntityRegistry;
import net.alshanex.tunes_n_tomes.registry.TParticleRegistry;
import net.alshanex.tunes_n_tomes.registry.TSoundRegistry;
import net.alshanex.tunes_n_tomes.registry.TSpellRegistry;
import net.alshanex.tunes_n_tomes.util.CylinderParticleManager;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ExplosionEvent.Start;

public class HarpExplosionEntity extends AoeEntity {
   @Nullable
   private LivingEntity owner;
   @Nullable
   private UUID ownerUUID;
   private int age;
   private float damage;

   public HarpExplosionEntity(EntityType<? extends Projectile> entityType, Level level) {
      super(entityType, level);
   }

   public HarpExplosionEntity(Level level, LivingEntity owner, float damage) {
      this((EntityType<? extends Projectile>)TEntityRegistry.HARP_EXPLOSION.get(), level);
      this.setOwner(owner);
      this.setDamage(damage);
   }

   public void setDamage(float damage) {
      this.damage = damage;
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide) {
         if (this.age == 1) {
            this.level()
               .addParticle((ParticleOptions)TParticleRegistry.DESTRUCTION_SYMPHONY_PARTICLE.get(), this.getX(), this.getY() + 3.0, this.getZ(), 0.0, 0.0, 0.0);
         }

         if (this.age == 26) {
            this.level()
               .addParticle((ParticleOptions)TParticleRegistry.HARP_EXPLOSION_PARTICLE.get(), this.getX(), this.getY() + 5.0, this.getZ(), 0.0, 0.0, 0.0);
         }
      } else {
         if (this.age > 60) {
            this.discard();
         }

         if (this.age <= 20) {
            CylinderParticleManager.spawnParticlesAtBlockPos(
               this.level(),
               this.position(),
               50,
               (ParticleOptions)TParticleRegistry.MUSICAL_NOTE_PARTICLE.get(),
               CylinderParticleManager.ParticleDirection.UPWARD,
               this.getRadius(),
               0.0,
               0.0
            );
         }

         if (this.age == 26) {
            float explosionRadius = this.getRadius();
            float explosionRadiusSqr = explosionRadius * explosionRadius;
            List<Entity> entities = this.level()
               .getEntities(
                  this,
                  this.getBoundingBox().inflate(explosionRadius),
                  entityx -> this.getOwner() != null && !entityx.is(this.getOwner()) && !this.getOwner().isAlliedTo(entityx)
               );
            Vec3 losPoint = Utils.raycastForBlock(this.level(), this.position(), this.position().add(0.0, 2.0, 0.0), Fluid.NONE).getLocation();

            for (Entity entity : entities) {
               double distanceSqr = entity.distanceToSqr(this.position());
               if (distanceSqr < explosionRadiusSqr && Utils.hasLineOfSight(this.level(), losPoint, entity.getBoundingBox().getCenter(), true)) {
                  double p = 1.0 - distanceSqr / explosionRadiusSqr;
                  float damage = (float)(this.damage * p);
                  DamageSources.applyDamage(entity, damage, TSpellRegistry.GRAND_FINALE_SPELL.get().getDamageSource(this, this.getOwner()));
                  if (entity instanceof LivingEntity target) {
                     target.addDeltaMovement(new Vec3(0.0, 1.0, 0.0));
                     target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 50, 3, false, false));
                     target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 50, 3, false, false));
                     target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 50, 3, false, false));
                  }
               }
            }

            if ((Boolean)ServerConfigs.SPELL_GREIFING.get()) {
               Explosion explosion = new Explosion(
                  this.level(),
                  null,
                  TSpellRegistry.GRAND_FINALE_SPELL.get().getDamageSource(this, this.getOwner()),
                  null,
                  this.getX(),
                  this.getY(),
                  this.getZ(),
                  explosionRadius,
                  true,
                  BlockInteraction.DESTROY,
                  ParticleTypes.EXPLOSION,
                  ParticleTypes.EXPLOSION_EMITTER,
                  SoundEvents.GENERIC_EXPLODE
               );
               if (!((Start)NeoForge.EVENT_BUS.post(new Start(this.level(), explosion))).isCanceled()) {
                  explosion.explode();
                  explosion.finalizeExplosion(false);
               }
            }

            EarthquakeAoe aoeEntity = new EarthquakeAoe(this.level());
            aoeEntity.moveTo(this.position());
            aoeEntity.setOwner(this.getOwner());
            aoeEntity.setCircular();
            aoeEntity.setRadius(explosionRadius);
            aoeEntity.setDuration(30);
            aoeEntity.setDamage(0.0F);
            aoeEntity.setSlownessAmplifier(0);
            this.level().addFreshEntity(aoeEntity);
            this.playSound(
               (SoundEvent)TSoundRegistry.HARP_EXPLOSION.get(),
               4.0F,
               (1.0F + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2F) * 0.7F
            );
         }
      }

      this.age++;
   }

   public void applyEffect(LivingEntity target) {
      if (!target.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) && this.owner != null && !this.owner.isAlliedTo(target)) {
         int duration = 40;
         int amplifier = 2;
         target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, amplifier, false, false));
      }
   }

   public float getParticleCount() {
      return 0.0F;
   }

   public Optional<ParticleOptions> getParticle() {
      return Optional.empty();
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.age = pCompound.getInt("Age");
      if (pCompound.hasUUID("Owner")) {
         this.ownerUUID = pCompound.getUUID("Owner");
      }
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putInt("Age", this.age);
      if (this.ownerUUID != null) {
         pCompound.putUUID("Owner", this.ownerUUID);
      }
   }

   public void setOwner(@Nullable LivingEntity pOwner) {
      this.owner = pOwner;
      this.ownerUUID = pOwner == null ? null : pOwner.getUUID();
   }

   @Nullable
   public LivingEntity getOwner() {
      if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel) {
         Entity entity = ((ServerLevel)this.level()).getEntity(this.ownerUUID);
         if (entity instanceof LivingEntity) {
            this.owner = (LivingEntity)entity;
         }
      }

      return this.owner;
   }
}
