package net.hazen.hazennstuff.Entity.Spells.Blood.LifestealKnife;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import net.hazen.hazennstuff.Registries.HnSEntityRegistry;
import net.hazen.hazennstuff.Registries.HnSSounds;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class VampireKnife extends AbstractMagicProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private final RawAnimation idle = RawAnimation.begin().thenLoop("animation.vampire_knife_projectile.idle");

   public VampireKnife(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public VampireKnife(Level level, LivingEntity shooter) {
      this((EntityType<? extends Projectile>)HnSEntityRegistry.LIFESTEAL_KNIFE.get(), level);
      this.setOwner(shooter);
   }

   public void travel() {
      this.setPos(this.position().add(this.getDeltaMovement()));
      if (!this.isNoGravity()) {
         Vec3 vec3 = this.getDeltaMovement();
         this.setDeltaMovement(vec3.x, vec3.y - 0.05F, vec3.z);
      }
   }

   public void setRotation(float x, float y) {
      this.setXRot(x);
      this.xRotO = x;
      this.setYRot(y);
      this.yRotO = y;
   }

   public void trailParticles() {
      for (int i = 0; i < 1; i++) {
         double speed = 0.05;
         double dx = Utils.random.nextDouble() * 2.0 * speed - speed;
         double dy = Utils.random.nextDouble() * 2.0 * speed - speed;
         double dz = Utils.random.nextDouble() * 2.0 * speed - speed;
         this.level.addParticle(ParticleHelper.BLOOD, this.getX() + dx, this.getY() + dy, this.getZ() + dz, dx, dy, dz);
      }
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level, ParticleHelper.BLOOD, x, y, z, 5, 0.1, 0.1, 0.1, 0.25, true);
   }

   public float getSpeed() {
      return 1.4F;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(HnSSounds.SYRINGE_BARRAGE_HIT);
   }

   protected void doImpactSound(Holder<SoundEvent> sound) {
      this.level.playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.NEUTRAL, 1.5F, 1.0F);
   }

   protected void onHitBlock(BlockHitResult blockHitResult) {
      super.onHitBlock(blockHitResult);
      this.discard();
   }

   protected void onHitEntity(EntityHitResult result) {
      super.onHitEntity(result);
      if (!this.level.isClientSide()) {
         Entity target = result.getEntity();
         if (this.getOwner() instanceof LivingEntity caster) {
            double bloodPower = caster.getAttributeValue(AttributeRegistry.BLOOD_SPELL_POWER);
            double spellPower = caster.getAttributeValue(AttributeRegistry.SPELL_POWER);
            float damage = (float)(2.0 + 2.0 * (bloodPower + spellPower));
            DamageSource source = this.level.damageSources().indirectMagic(this, caster);
            target.hurt(source, damage);
            caster.heal(damage * 0.075F);
         }

         this.level.playSound(null, this.getX(), this.getY(), this.getZ(), HnSSounds.VAMPIRE_KNIFE_HIT, SoundSource.NEUTRAL, 2.0F, 1.0F);
         this.discard();
      }
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
