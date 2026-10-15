package net.warphan.iss_magicfromtheeast.entity.spells.phantom_cavalry;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.warphan.iss_magicfromtheeast.registries.MFTEEntityRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESpellRegistries;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PhantomCavalryVisualEntity extends AbstractMagicProjectile implements GeoEntity {
   private final RawAnimation charge = RawAnimation.begin().thenPlay("phantom_charging");
   public AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public PhantomCavalryVisualEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.noPhysics = true;
      super.tick();
      this.noPhysics = false;
      this.setNoGravity(true);
   }

   public PhantomCavalryVisualEntity(Level level, LivingEntity caster) {
      this((EntityType<? extends Projectile>)MFTEEntityRegistries.PHANTOM_CAVALRY.get(), level);
      this.setOwner(caster);
   }

   public void trailParticles() {
      Vec3 pos = this.getBoundingBox().getBottomCenter().add(this.getDeltaMovement());
      pos = pos.add(this.getDeltaMovement());
      Vec3 random = new Vec3(Utils.getRandomScaled(0.1F), Utils.getRandomScaled(0.05F), Utils.getRandomScaled(0.1F));
      this.level.addParticle(ParticleTypes.SCULK_SOUL, pos.x, pos.y, pos.z, random.x, random.y, random.z);
   }

   public void impactParticles(double x, double y, double z) {
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.empty();
   }

   public float getSpeed() {
      return 1.0F;
   }

   public void tick() {
      super.tick();
      if (this.tickCount < 35 && this.tickCount % 10 == 0 && !this.level.isClientSide) {
         this.playSound(SoundEvents.HORSE_GALLOP);
      }

      if (this.tickCount > 40) {
         this.discard();
         if (!this.level.isClientSide) {
            MagicManager.spawnParticles(this.level(), ParticleTypes.SCULK_SOUL, this.getX(), this.getY(), this.getZ(), 25, 1.5, 2.5, 1.5, 0.08, false);
         }
      }
   }

   public boolean canHitEntity(Entity pTarget) {
      return super.canHitEntity(pTarget);
   }

   public void handleHitDetection() {
      Vec3 vec3 = this.getDeltaMovement();
      Vec3 pos = this.position();
      Vec3 vec32 = pos.add(vec3);
      HitResult hitResult = this.level.clip(new ClipContext(pos, vec32, Block.COLLIDER, Fluid.NONE, this));
      if (hitResult.getType() != Type.MISS) {
         this.onHit(hitResult);
      } else {
         for (Entity entity : this.level.getEntities(this, this.getBoundingBox().inflate(0.25), this::canHitEntity)) {
            this.onHit(new EntityHitResult(entity, this.getBoundingBox().getCenter().add(entity.getBoundingBox().getCenter()).scale(0.5)));
         }
      }
   }

   protected void onHitEntity(EntityHitResult entityHitResult) {
      super.onHitEntity(entityHitResult);
      if (entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
         DamageSources.ignoreNextKnockback(livingEntity);
         livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80));
         livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80));
      }

      DamageSources.applyDamage(entityHitResult.getEntity(), this.damage, MFTESpellRegistries.PHANTOM_CHARGE_SPELL.get().getDamageSource(this, this.getOwner()));
   }

   private PlayState predicate(AnimationState event) {
      event.getController().setAnimationSpeed(2.0).setAnimation(this.charge);
      return PlayState.CONTINUE;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController(this, "controller", 0, this::predicate));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
