package net.hazen.hazennstuff.Entity.Spells.Nature.DeathSentence;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.Optional;
import net.hazen.hazennstuff.Registries.HnSEntityRegistry;
import net.hazen.hazennstuff.Registries.HnSParticleHelper;
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

public class DeathSentence extends AbstractMagicProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   public int despawnTimer = 30;
   private final RawAnimation idle = RawAnimation.begin().thenLoop("animation.spectral_axe.idle");
   private float damage = 2.0F;

   public void tick() {
      super.tick();
      if (this.despawnTimer > 0) {
         this.despawnTimer--;
         if (this.despawnTimer == 0) {
            this.discard();
         }
      }
   }

   public void startDespawning() {
      this.despawnTimer = 15;
   }

   public boolean isDespawning() {
      return this.despawnTimer > 0;
   }

   public float getDespawnPercent(float partialTicks) {
      return (this.despawnTimer - partialTicks) / 10.0F;
   }

   public DeathSentence(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
   }

   public DeathSentence(Level level, LivingEntity shooter) {
      this((EntityType<? extends Projectile>)HnSEntityRegistry.DEATH_SENTENCE.get(), level);
      this.setOwner(shooter);
   }

   public void trailParticles() {
      Vec3 pos = this.getBoundingBox().getCenter().add(this.getDeltaMovement());
      Vec3 random = Utils.getRandomVec3(0.0);
      pos = pos.add(this.getDeltaMovement());
      this.level.addParticle(HnSParticleHelper.BLADE_PARTICLE, pos.x, pos.y, pos.z, random.x, random.y, random.z);
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level, HnSParticleHelper.BLADE_PARTICLE, x, y, z, 12, 0.08, 0.08, 0.08, 0.3, false);
   }

   public float getSpeed() {
      return 1.2F;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(SoundRegistry.ECHOING_STRIKE);
   }

   protected void doImpactSound(Holder<SoundEvent> sound) {
      this.level.playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.NEUTRAL, 1.5F, 1.0F);
   }

   protected void onHitBlock(BlockHitResult blockHitResult) {
      super.onHitBlock(blockHitResult);
      this.discard();
   }

   protected void onHitEntity(EntityHitResult entityHitResult) {
      super.onHitEntity(entityHitResult);
      DamageSources.applyDamage(entityHitResult.getEntity(), this.damage, HnSSpellRegistries.DEATH_SENTENCE.get().getDamageSource(this, this.getOwner()));
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
