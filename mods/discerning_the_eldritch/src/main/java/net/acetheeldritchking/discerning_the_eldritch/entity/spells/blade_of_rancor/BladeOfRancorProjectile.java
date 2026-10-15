package net.acetheeldritchking.discerning_the_eldritch.entity.spells.blade_of_rancor;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.Optional;
import net.acetheeldritchking.discerning_the_eldritch.particle.DTEParticleHelper;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEEntityRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEPotionEffectRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTESchoolRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.SpellRegistries;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BladeOfRancorProjectile extends AbstractMagicProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public BladeOfRancorProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
   }

   public BladeOfRancorProjectile(Level pLevel, LivingEntity pShooter) {
      super((EntityType)DTEEntityRegistry.BLADE_OF_RANCOR.get(), pLevel);
      this.setOwner(pShooter);
   }

   public void shoot(Vec3 rotation, float inaccuracy) {
      double speed = rotation.length();
      Vec3 offset = Utils.getRandomVec3(1.0).normalize().scale(inaccuracy);
      Vec3 motion = rotation.normalize().add(offset).normalize().scale(speed);
      super.shoot(motion);
   }

   protected void setRot(float y, float x) {
      this.setXRot(x);
      this.xRotO = x;
      this.setYRot(y);
      this.yRotO = y;
   }

   public void tick() {
      Vec3 deltaMovement = this.getDeltaMovement();
      double distance = deltaMovement.horizontalDistance();
      double x = deltaMovement.x;
      double y = deltaMovement.y;
      double z = deltaMovement.z;
      this.setYRot((float)(Mth.atan2(x, z) * (180.0 / Math.PI)));
      this.setXRot((float)(Mth.atan2(y, distance) * (180.0 / Math.PI)));
      this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
      this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
      super.tick();
   }

   public void trailParticles() {
      for (int i = 0; i < 2; i++) {
         double speed = 0.05;
         double x = Utils.random.nextDouble() * 2.0 * speed - speed;
         double y = Utils.random.nextDouble() * 2.0 * speed - speed;
         double z = Utils.random.nextDouble() * 2.0 * speed - speed;
         this.level().addParticle(DTEParticleHelper.MALIGNANT_SOUL, this.getX(), this.getY(), this.getZ(), x, y, z);
      }
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(
         this.level(),
         new BlastwaveParticleOptions(DTESchoolRegistry.RITUAL.get().getTargetingColor(), 1.5F),
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

   public float getSpeed() {
      return 1.0F;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.empty();
   }

   protected void onHitEntity(EntityHitResult pResult) {
      if (!this.level().isClientSide) {
         Entity target = pResult.getEntity();
         Entity owner = this.getOwner();
         DamageSources.applyDamage(target, this.damage, SpellRegistries.BLADES_OF_RANCOR.get().getDamageSource(this, owner));
         if (target instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(DTEPotionEffectRegistry.MALIGNANT_BURN_EFFECT, 100, 0, true, true, true));
         }
      }
   }

   protected void onHitBlock(BlockHitResult result) {
      super.onHitBlock(result);
   }

   protected void onHit(HitResult hitresult) {
      super.onHit(hitresult);
      this.discardHelper(hitresult);
   }

   public void registerControllers(ControllerRegistrar controllers) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
