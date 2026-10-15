package net.hazen.hazennstuff.Entity.Spells.Shadow.ShadowReaver;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import net.hazen.hazennstuff.Registries.HnSEffects;
import net.hazen.hazennstuff.Registries.HnSEntityRegistry;
import net.hazen.hazennstuff.Registries.HnSParticleHelper;
import net.hazen.hazennstuff.Registries.HnSSounds;
import net.hazen.hazennstuff.Spells.HnSSpellRegistries;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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

public class ShadowReaver extends AbstractMagicProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   HashMap<UUID, Integer> victims;
   private final RawAnimation idle = RawAnimation.begin().thenLoop("idle");

   public ShadowReaver(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.victims = new HashMap<>();
      this.setNoGravity(true);
      this.setPierceLevel(-1);
   }

   public ShadowReaver(Level level, LivingEntity shooter) {
      this((EntityType<? extends Projectile>)HnSEntityRegistry.SHADOW_REAVER.get(), level);
      this.setOwner(shooter);
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
         this.level.addParticle(HnSParticleHelper.NIGHTS_EDGE_PARTICLE, x, y, z, jitter.x, jitter.y, jitter.z);
      }
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level(), HnSParticleHelper.NIGHTS_EDGE_PARTICLE, x, y, z, 25, 0.0, 0.0, 0.0, 0.18, true);
   }

   public float getSpeed() {
      return 2.0F;
   }

   protected void onHitEntity(EntityHitResult pResult) {
      Entity target = pResult.getEntity();
      DamageSources.applyDamage(target, this.damage, HnSSpellRegistries.SHADOW_REAVER.get().getDamageSource(this, this.getOwner()));
      if (target instanceof LivingEntity livingTarget) {
         livingTarget.addEffect(new MobEffectInstance(HnSEffects.HEXED, 100, 0, false, true, true));
      }

      this.discard();
   }

   protected void doImpactSound(Holder<SoundEvent> sound) {
      this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.NEUTRAL, 1.0F, 1.0F);
      if (this.getOwner() instanceof Player player) {
         player.playSound((SoundEvent)sound.value(), 0.75F, 1.0F);
      }
   }

   protected void onHitBlock(BlockHitResult blockHitResult) {
      super.onHitBlock(blockHitResult);
      this.discard();
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(HnSSounds.BRIMSTONE_HELLBLAST_IMPACT);
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
