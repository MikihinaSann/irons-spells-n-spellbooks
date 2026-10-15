package net.warphan.iss_magicfromtheeast.entity.spells.soul_skull;

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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.warphan.iss_magicfromtheeast.registries.MFTEEffectRegistries;
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

public class SoulSkullProjectile extends AbstractMagicProjectile implements GeoEntity {
   private final RawAnimation skull_fly = RawAnimation.begin().thenPlay("skull_jaw");
   public AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public SoulSkullProjectile(EntityType<? extends Projectile> entityType, Level level) {
      super(entityType, level);
      this.setNoGravity(true);
   }

   public SoulSkullProjectile(Level level, LivingEntity caster) {
      this((EntityType<? extends Projectile>)MFTEEntityRegistries.SOUL_SKULL.get(), level);
      this.setOwner(caster);
   }

   public void shoot(Vec3 rotation, float inaccuracy) {
      double speed = rotation.length();
      Vec3 offset = Utils.getRandomVec3(1.0).normalize().scale(inaccuracy);
      Vec3 motion = rotation.normalize().add(offset).normalize().scale(speed);
      super.shoot(motion);
   }

   public void trailParticles() {
      Vec3 pos = this.getBoundingBox().getCenter().add(this.getDeltaMovement());
      pos = pos.add(this.getDeltaMovement());
      Vec3 random = new Vec3(Utils.getRandomScaled(0.05F), Utils.getRandomScaled(0.05F), Utils.getRandomScaled(0.05F));

      for (int i = 0; i < 3; i++) {
         this.level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, pos.x, pos.y, pos.z, random.x, random.y, random.z);
      }
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level, ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 8, 0.4, 0.4, 0.4, 0.2, true);
      MagicManager.spawnParticles(this.level, ParticleTypes.SCULK_SOUL, x, y, z, 5, 0.3, 0.3, 0.3, 0.1, true);
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(SoundEvents.SOUL_ESCAPE);
   }

   public float getSpeed() {
      return 1.75F;
   }

   protected void onHitBlock(BlockHitResult blockHitResult) {
      super.onHitBlock(blockHitResult);
      this.discard();
   }

   protected void onHitEntity(EntityHitResult entityHitResult) {
      super.onHitEntity(entityHitResult);
      Entity entity = entityHitResult.getEntity();
      DamageSources.applyDamage(entity, this.getDamage(), MFTESpellRegistries.SOUL_CATALYST_SPELL.get().getDamageSource(this, this.getOwner()));
      if (entity instanceof LivingEntity livingEntity) {
         livingEntity.addEffect(new MobEffectInstance(MFTEEffectRegistries.SOULBURN, 80, 0));
         livingEntity.invulnerableTime = 0;
      }

      this.discard();
   }

   private PlayState predicate(AnimationState event) {
      event.getController().setAnimation(this.skull_fly);
      return PlayState.CONTINUE;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController(this, "controller", 0, this::predicate));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
