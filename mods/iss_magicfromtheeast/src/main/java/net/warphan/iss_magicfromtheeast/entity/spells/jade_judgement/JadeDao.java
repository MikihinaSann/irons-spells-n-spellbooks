package net.warphan.iss_magicfromtheeast.entity.spells.jade_judgement;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.particle.SparkParticleOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.warphan.iss_magicfromtheeast.registries.MFTEEntityRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESoundRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESpellRegistries;
import net.warphan.iss_magicfromtheeast.util.MFTEParticleHelper;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class JadeDao extends AbstractMagicProjectile implements GeoEntity {
   private UUID targetUUID;
   private Entity cachedTarget;
   private List<Entity> targets;
   int airTime;
   private final RawAnimation prepare = RawAnimation.begin().thenPlay("prepare");
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public JadeDao(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.targets = new ArrayList<>();
      this.setNoGravity(true);
   }

   public JadeDao(Level pLevel, LivingEntity owner, LivingEntity target) {
      this((EntityType<? extends Projectile>)MFTEEntityRegistries.JADE_DAO.get(), pLevel);
      this.setOwner(owner);
      this.setTarget(target);
   }

   public void setAirTime(int airTimeInTicks) {
      this.airTime = airTimeInTicks;
   }

   public void setTarget(@Nullable Entity pOwner) {
      if (pOwner != null) {
         this.targetUUID = pOwner.getUUID();
         this.cachedTarget = pOwner;
      }
   }

   @Nullable
   public Entity getTarget() {
      if (this.cachedTarget != null && !this.cachedTarget.isRemoved()) {
         return this.cachedTarget;
      } else if (this.targetUUID != null && this.level instanceof ServerLevel) {
         this.cachedTarget = ((ServerLevel)this.level).getEntity(this.targetUUID);
         return this.cachedTarget;
      } else {
         return null;
      }
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      if (this.targetUUID != null) {
         tag.putUUID("Target", this.targetUUID);
      }

      if (this.airTime > 0) {
         tag.putInt("airTime", this.airTime);
      }
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      if (tag.hasUUID("Target")) {
         this.targetUUID = tag.getUUID("Target");
      }

      if (tag.contains("airTime")) {
         this.airTime = tag.getInt("airTime");
      }
   }

   public void trailParticles() {
      for (int i = 0; i < 1; i++) {
         Vec3 random = new Vec3(Utils.getRandomScaled(this.getBbWidth() * 0.5F), 0.0, Utils.getRandomScaled(this.getBbWidth() * 0.5F));
         this.level.addParticle(ParticleTypes.GLOW, this.getX() + random.x, this.getY(), this.getZ() + random.z, 0.0, -0.05, 0.0);
      }
   }

   private void doFallingDamage(Entity target) {
      if (!this.level.isClientSide) {
         if (this.canHitEntity(target) && !this.targets.contains(target)) {
            boolean flag = DamageSources.applyDamage(
               target, this.getDamage() * 0.25F, MFTESpellRegistries.JADE_JUDGEMENT_SPELL.get().getDamageSource(this, this.getOwner())
            );
            if (flag) {
               this.targets.add(target);
               target.invulnerableTime = 0;
            }
         }
      }
   }

   private void doImpactDamage() {
      float boomRadius = 5.0F;
      this.level.getEntities(this, this.getBoundingBox().inflate(boomRadius)).forEach(entity -> {
         if (this.canHitEntity(entity)) {
            double distance = entity.distanceToSqr(this.position());
            if (distance < boomRadius * boomRadius) {
               double p = 1.0 - Math.pow(Math.sqrt(distance) / boomRadius, 3.0);
               float damage = (float)(this.damage * p);
               DamageSources.applyDamage(entity, damage, MFTESpellRegistries.JADE_JUDGEMENT_SPELL.get().getDamageSource(this, this.getOwner()));
            }
         }
      });
   }

   public void tick() {
      this.firstTick = false;
      this.xo = this.getX();
      this.yo = this.getY();
      this.zo = this.getZ();
      this.xOld = this.getX();
      this.yOld = this.getY();
      this.zOld = this.getZ();
      this.yRotO = this.getYRot();
      this.xRotO = this.getXRot();
      if (!this.level.isClientSide) {
         if (this.airTime <= 0) {
            if (this.onGround()) {
               this.doImpactDamage();
               this.playSound((SoundEvent)MFTESoundRegistries.JADE_DAO_IMPACT.value(), 8.0F, 7.5F);
               this.impactParticles(this.getX(), this.getY(), this.getZ());
               this.discard();
            } else {
               this.level.getEntities(this, this.getBoundingBox().inflate(0.35)).forEach(this::doFallingDamage);
            }
         }

         if (this.airTime-- > 0) {
            boolean tooHigh = false;
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.95F, 0.75, 0.95F));
            if (this.getTarget() != null) {
               Entity target = this.getTarget();
               Vec3 diff = target.position().subtract(this.position());
               if (diff.horizontalDistanceSqr() > 1.0) {
                  this.setDeltaMovement(this.getDeltaMovement().add(diff.multiply(1.0, 0.0, 1.0).normalize().scale(0.025F)));
               }

               if (this.getY() - target.getY() > 18.0) {
                  tooHigh = true;
               }
            } else if (this.airTime % 3 == 0) {
               HitResult ground = Utils.raycastForBlock(this.level, this.position(), this.position().subtract(0.0, 3.5, 0.0), Fluid.ANY);
               if (ground.getType() == Type.MISS) {
                  tooHigh = true;
               } else if (Math.abs(this.position().y - ground.getLocation().y) < 4.0) {
               }
            }

            if (tooHigh) {
               this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
            } else {
               this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.01, 0.0));
            }

            if (this.airTime == 0) {
               this.setDeltaMovement(0.0, 0.5, 0.0);
            }
         } else {
            this.setDeltaMovement(0.0, this.getDeltaMovement().y - 0.15, 0.0);
         }
      } else {
         this.trailParticles();
      }

      this.move(MoverType.SELF, this.getDeltaMovement());
   }

   public void setYRot(float pYRot) {
   }

   public void setXRot(float pXRot) {
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   protected boolean canHitEntity(Entity pTarget) {
      return pTarget != this.getOwner() && super.canHitEntity(pTarget);
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(
         this.level, new BlastwaveParticleOptions(MFTESchoolRegistries.SYMMETRY.get().getTargetingColor(), 5.0F), x, y + 0.165F, z, 1, 0.0, 0.0, 0.0, 0.0, true
      );
      MagicManager.spawnParticles(
         this.level, new SparkParticleOptions(MFTESchoolRegistries.SYMMETRY.get().getTargetingColor()), x, y + 0.1, z, 30, 0.08, 1.0, 0.08, 0.3, false
      );
      MagicManager.spawnParticles(
         this.level, new DustParticleOptions(MFTESchoolRegistries.SYMMETRY.get().getTargetingColor(), 3.0F), x, y + 0.1, z, 60, 2.0, 1.0, 2.0, 0.5, false
      );
      MagicManager.spawnParticles(this.level, ParticleTypes.GLOW, x, y + 0.1, z, 40, 1.5, 0.3, 1.5, 0.6, false);
      MagicManager.spawnParticles(this.level, MFTEParticleHelper.JADE_SHATTER, x, y + 0.1, z, 25, 1.0, 0.1, 1.0, 0.4, false);
   }

   public float getSpeed() {
      return 0.0F;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.empty();
   }

   private PlayState predicate(AnimationState state) {
      state.getController().setAnimation(this.prepare);
      return PlayState.CONTINUE;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController(this, "controller", 0, this::predicate));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
