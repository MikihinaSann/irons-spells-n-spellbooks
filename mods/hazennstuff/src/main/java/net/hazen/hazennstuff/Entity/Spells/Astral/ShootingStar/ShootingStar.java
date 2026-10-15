package net.hazen.hazennstuff.Entity.Spells.Astral.ShootingStar;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import java.util.Optional;
import net.hazen.hazennstuff.Registries.HnSEntityRegistry;
import net.hazen.hazennstuff.Registries.HnSParticleHelper;
import net.hazen.hazennstuff.Registries.HnSSounds;
import net.hazen.hazennstuff.Spells.HnSSpellRegistries;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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

public class ShootingStar extends AbstractMagicProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private final RawAnimation idle = RawAnimation.begin().thenLoop("animation.shooting_star.idle");
   private float damage = 2.0F;

   public ShootingStar(EntityType<? extends ShootingStar> entityType, Level level) {
      super(entityType, level);
      this.setNoGravity(true);
      this.setPierceLevel(1);
   }

   public ShootingStar(EntityType<? extends ShootingStar> entityType, Level levelIn, LivingEntity shooter) {
      this(entityType, levelIn);
      this.setOwner(shooter);
   }

   public ShootingStar(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends ShootingStar>)HnSEntityRegistry.SHOOTING_STAR.get(), levelIn, shooter);
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
         this.level.addParticle(HnSParticleHelper.COSMIC_EMBER_PARTICLE, x, y, z, jitter.x, jitter.y, jitter.z);
      }
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level(), HnSParticleHelper.STAR_IMPACT_PARTICLE, x, y, z, 25, 0.0, 0.0, 0.0, 0.18, true);
   }

   public float getSpeed() {
      return 2.5F;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(HnSSounds.TERRARIA_IMPACT);
   }

   protected void onHitEntity(EntityHitResult entityHitResult) {
      super.onHitEntity(entityHitResult);
      DamageSources.applyDamage(entityHitResult.getEntity(), this.damage, HnSSpellRegistries.SHOOTING_STAR.get().getDamageSource(this, this.getOwner()));
      this.discard();
   }

   protected void onHitBlock(BlockHitResult blockHitResult) {
      super.onHitBlock(blockHitResult);
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

   public void setDamage(float damage) {
      this.damage = damage;
   }
}
