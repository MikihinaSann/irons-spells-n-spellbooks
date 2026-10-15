package net.hazen.hazennstuff.Entity.Spells.Fire.BrimstoneHellblast;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.network.particles.FieryExplosionParticlesPacket;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import net.hazen.hazennstuff.Registries.HnSEntityRegistry;
import net.hazen.hazennstuff.Registries.HnSSounds;
import net.hazen.hazennstuff.Spells.HnSSpellRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ExplosionEvent.Start;
import net.neoforged.neoforge.network.PacketDistributor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BrimstoneHellblast extends AbstractMagicProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private final RawAnimation idle = RawAnimation.begin().thenLoop("animation.brimstone_hellblast.idle");

   public BrimstoneHellblast(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
   }

   public BrimstoneHellblast(Level pLevel, LivingEntity pShooter) {
      this((EntityType<? extends Projectile>)HnSEntityRegistry.BRIMSTONE_HELLBLAST.get(), pLevel);
      this.setOwner(pShooter);
   }

   public void setRotation(float x, float y) {
      this.setXRot(x);
      this.xRotO = x;
      this.setYRot(y);
      this.yRotO = y;
   }

   public void trailParticles() {
      Vec3 vec3 = this.getDeltaMovement();
      double d0 = this.getX() - vec3.x;
      double d1 = this.getY() - vec3.y;
      double d2 = this.getZ() - vec3.z;
      int count = Mth.clamp((int)(vec3.lengthSqr() * 2.0), 1, 4);

      for (int i = 0; i < count; i++) {
         Vec3 random = Utils.getRandomVec3(this.getBbHeight() * 0.2F);
         float f = (float)i / count;
         double x = Mth.lerp(f, d0, this.getX() + vec3.x);
         double y = Mth.lerp(f, d1, this.getY() + vec3.y);
         double z = Mth.lerp(f, d2, this.getZ() + vec3.z);
         this.level.addParticle(ParticleHelper.FIERY_SMOKE, true, x - random.x, y + this.getBbHeight() * 0.5F - random.y, z - random.z, 0.0, 0.0, 0.0);
      }
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level, ParticleHelper.FIERY_SMOKE, x, y, z, 5, 0.1, 0.1, 0.1, 0.25, true);
   }

   public float getSpeed() {
      return 1.4F;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(HnSSounds.BRIMSTONE_HELLBLAST_IMPACT);
   }

   protected void doImpactSound(Holder<SoundEvent> sound) {
      this.level.playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.NEUTRAL, 1.5F, 1.0F);
   }

   protected void onHit(HitResult hitResult) {
      if (!this.level.isClientSide) {
         this.impactParticles(this.xOld, this.yOld, this.zOld);
         float explosionRadius = this.getExplosionRadius();
         float explosionRadiusSqr = explosionRadius * explosionRadius;
         List<Entity> entities = this.level.getEntities(this, this.getBoundingBox().inflate(explosionRadius));
         Vec3 losPoint = Utils.raycastForBlock(this.level, this.position(), this.position().add(0.0, 2.0, 0.0), Fluid.NONE).getLocation();

         for (Entity entity : entities) {
            double distanceSqr = entity.distanceToSqr(hitResult.getLocation());
            if (distanceSqr < explosionRadiusSqr
               && this.canHitEntity(entity)
               && Utils.hasLineOfSight(this.level, losPoint, entity.getBoundingBox().getCenter(), true)) {
               double p = 1.0 - distanceSqr / explosionRadiusSqr;
               float damage = (float)(this.damage * p);
               DamageSources.applyDamage(entity, damage, HnSSpellRegistries.BRIMSTONE_HELLBLAST.get().getDamageSource(this, this.getOwner()));
            }
         }

         if ((Boolean)ServerConfigs.SPELL_GREIFING.get()) {
            Explosion explosion = new Explosion(
               this.level,
               (Entity)null,
               HnSSpellRegistries.BRIMSTONE_HELLBLAST.get().getDamageSource(this, this.getOwner()),
               (ExplosionDamageCalculator)null,
               this.getX(),
               this.getY(),
               this.getZ(),
               this.getExplosionRadius() / 2.0F,
               true,
               BlockInteraction.DESTROY,
               ParticleTypes.EXPLOSION,
               ParticleTypes.EXPLOSION_EMITTER,
               HnSSounds.BRIMSTONE_HELLBLAST_IMPACT
            );
            if (!((Start)NeoForge.EVENT_BUS.post(new Start(this.level, explosion))).isCanceled()) {
               explosion.explode();
               explosion.finalizeExplosion(false);
            }
         }

         PacketDistributor.sendToPlayersTrackingEntity(
            this,
            new FieryExplosionParticlesPacket(hitResult.getLocation().subtract(this.getDeltaMovement().scale(0.5)), this.getExplosionRadius()),
            new CustomPacketPayload[0]
         );
         this.playSound(
            (SoundEvent)HnSSounds.BRIMSTONE_HELLBLAST_IMPACT.value(),
            4.0F,
            (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F
         );
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
