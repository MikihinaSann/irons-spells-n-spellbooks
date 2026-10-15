package net.hazen.hazennstuff.Entity.Spells.Lightning.EnergyBurst;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.particle.ZapParticleOption;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import net.hazen.hazennstuff.Registries.HnSEntityRegistry;
import net.hazen.hazennstuff.Registries.HnSSounds;
import net.hazen.hazennstuff.Spells.HnSSpellRegistries;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
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

public class EnergyBurst extends AbstractMagicProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private int delay = 0;
   private int age = 0;
   private Vec3 spawnPos;
   private final RawAnimation idle = RawAnimation.begin().thenLoop("animation.spark.idle");

   public void setSpawnPos(Vec3 pos) {
      this.spawnPos = pos;
      if (pos != null) {
         this.setPos(pos);
      }
   }

   public void setDelay(int delay) {
      this.delay = delay;
   }

   public EnergyBurst(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
   }

   public EnergyBurst(Level level, LivingEntity shooter) {
      this((EntityType<? extends Projectile>)HnSEntityRegistry.ENERGY_BURST.get(), level);
      this.setOwner(shooter);
   }

   public void tick() {
      this.age++;
      if (this.age < this.delay) {
         if (this.spawnPos != null) {
            this.setPos(this.spawnPos);
         }

         if (this.level.isClientSide) {
            this.level
               .addParticle(
                  ParticleHelper.ELECTRIC_SPARKS,
                  this.getX(),
                  this.getY() + this.getBbHeight() * 0.5,
                  this.getZ(),
                  (this.random.nextDouble() - 0.5) * 0.1,
                  (this.random.nextDouble() - 0.5) * 0.1,
                  (this.random.nextDouble() - 0.5) * 0.1
               );
         }
      } else {
         super.tick();
      }
   }

   public float getSpeed() {
      return 1.4F;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(HnSSounds.ELECTRIC_IMPACT);
   }

   protected void doImpactSound(Holder<SoundEvent> sound) {
      this.level.playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.NEUTRAL, 1.5F, 1.0F);
   }

   public void trailParticles() {
      Vec3 pos = this.getBoundingBox().getCenter().add(this.getDeltaMovement());
      Vec3 random = Utils.getRandomVec3(0.25).add(pos);
      pos = pos.add(this.getDeltaMovement());
      this.level.addParticle(new ZapParticleOption(random), pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level, ParticleHelper.ELECTRIC_SPARKS, x, y, z, 12, 0.08, 0.08, 0.08, 0.3, false);
   }

   protected void onHitBlock(BlockHitResult blockHitResult) {
      super.onHitBlock(blockHitResult);
      this.discard();
   }

   protected void onHitEntity(EntityHitResult entityHitResult) {
      super.onHitEntity(entityHitResult);
      DamageSources.applyDamage(entityHitResult.getEntity(), this.damage, HnSSpellRegistries.ENERGY_BURST.get().getDamageSource(this, this.getOwner()));
      this.discard();
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
