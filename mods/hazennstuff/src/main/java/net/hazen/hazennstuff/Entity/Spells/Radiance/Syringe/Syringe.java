package net.hazen.hazennstuff.Entity.Spells.Radiance.Syringe;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import java.util.Optional;
import net.hazen.hazennstuff.Registries.HnSEntityRegistry;
import net.hazen.hazennstuff.Registries.HnSSounds;
import net.hazen.hazennstuff.Registries.Effects.InfestedEffect;
import net.hazen.hazennstuff.Spells.HnSSpellRegistries;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
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

public class Syringe extends AbstractMagicProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private int delay = 0;
   private int age = 0;
   private Vec3 spawnPos;
   private final RawAnimation idle = RawAnimation.begin().thenLoop("animation.syringe.idle");
   private float damage = 2.0F;

   public void setSpawnPos(Vec3 pos) {
      this.spawnPos = pos;
      if (pos != null) {
         this.setPos(pos);
      }
   }

   public void setDelay(int delay) {
      this.delay = delay;
   }

   public Syringe(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public Syringe(Level level, LivingEntity shooter) {
      this((EntityType<? extends Projectile>)HnSEntityRegistry.SYRINGE.get(), level);
      this.setOwner(shooter);
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.empty();
   }

   public void tick() {
      this.age++;
      if (this.age < this.delay) {
         if (this.spawnPos != null) {
            this.setPos(this.spawnPos);
         }
      } else {
         super.tick();
      }
   }

   public void trailParticles() {
   }

   public void impactParticles(double x, double y, double z) {
   }

   public float getSpeed() {
      return 1.4F;
   }

   protected void doImpactSound(Holder<SoundEvent> sound) {
      this.level.playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.NEUTRAL, 1.5F, 1.0F);
   }

   protected void onHitBlock(BlockHitResult pResult) {
      super.onHitBlock(pResult);
      this.level.playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)HnSSounds.SYRINGE_BARRAGE_MISS.get(), SoundSource.NEUTRAL, 2.0F, 1.0F);
      this.discard();
   }

   protected void onHitEntity(EntityHitResult pResult) {
      Entity target = pResult.getEntity();
      DamageSources.applyDamage(target, this.damage, HnSSpellRegistries.SYRINGE_BARRAGE.get().getDamageSource(this, this.getOwner()));
      if (target instanceof LivingEntity livingTarget) {
         InfestedEffect.addInfestedStack(livingTarget, this.getOwner());
      }

      this.level.playSound(null, this.getX(), this.getY(), this.getZ(), HnSSounds.SYRINGE_BARRAGE_HIT, SoundSource.NEUTRAL, 2.0F, 1.0F);
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
