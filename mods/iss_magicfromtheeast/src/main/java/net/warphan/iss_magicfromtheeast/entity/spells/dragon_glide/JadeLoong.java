package net.warphan.iss_magicfromtheeast.entity.spells.dragon_glide;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
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
import net.warphan.iss_magicfromtheeast.util.MFTEParticleHelper;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class JadeLoong extends AbstractMagicProjectile implements GeoEntity {
   private final RawAnimation loongglide = RawAnimation.begin().thenPlay("glide");
   public AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public JadeLoong(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.noPhysics = true;
      super.tick();
      this.noPhysics = false;
      this.setNoGravity(true);
   }

   public JadeLoong(Level level, LivingEntity caster) {
      this((EntityType<? extends Projectile>)MFTEEntityRegistries.JADE_LOONG.get(), level);
      this.setOwner(caster);
   }

   public void trailParticles() {
      float yHeading = -((float)(Mth.atan2(this.getDeltaMovement().z, this.getDeltaMovement().x) * 180.0F / (float)Math.PI) + 90.0F);
      float radius = 0.25F;
      int steps = 2;
      Vec3 vec = this.getDeltaMovement();
      double x2 = this.getX();
      double x1 = x2 - vec.x;
      double y2 = this.getY();
      double y1 = y2 - vec.y;
      double z2 = this.getZ();
      double z1 = z2 - vec.z;

      for (int j = 0; j < steps; j++) {
         float offset = 1.0F / steps * j;
         double radians = (this.tickCount + offset) / 7.5F * 360.0F * (float) (Math.PI / 180.0);
         Vec3 swirl = new Vec3(Math.cos(radians) * radius, Math.sin(radians) * radius, 0.0).yRot(yHeading * (float) (Math.PI / 180.0));
         double x = Mth.lerp(offset, x1, x2) + swirl.x;
         double y = Mth.lerp(offset, y1, y2) + swirl.y + this.getBbHeight() / 2.0F;
         double z = Mth.lerp(offset, z1, z2) + swirl.z;
         Vec3 jitter = Vec3.ZERO;
         this.level.addParticle(ParticleTypes.GLOW, x, y, z, jitter.x, jitter.y, jitter.z);
      }
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level, ParticleTypes.SCRAPE, x, y, z, 5, 0.8, 0.8, 0.8, 1.0, false);
      MagicManager.spawnParticles(this.level, MFTEParticleHelper.JADE_SHATTER, x, y, z, 2, 0.2, 0.2, 0.2, 0.2, false);
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.empty();
   }

   public float getSpeed() {
      return 0.6F;
   }

   public void tick() {
      super.tick();
      if (this.tickCount > 80) {
         this.discard();
         if (!this.level.isClientSide) {
            this.impactParticles(this.getX(), this.getBoundingBox().getCenter().y, this.getZ());
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
      }

      DamageSources.applyDamage(entityHitResult.getEntity(), this.damage, MFTESpellRegistries.DRAGON_GLIDE_SPELL.get().getDamageSource(this, this.getOwner()));
   }

   private PlayState predicate(AnimationState event) {
      event.getController().setAnimation(this.loongglide);
      return PlayState.CONTINUE;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController(this, "controller", 0, this::predicate));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
