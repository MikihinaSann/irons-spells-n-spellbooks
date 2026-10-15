package net.hazen.hazennstuff.Entity.Spells.Eldritch.SoulSeeker;

import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.events.CounterSpellEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import mod.azure.azurelib.core.utils.MathHelper;
import net.hazen.hazennstuff.Registries.HnSEntityRegistry;
import net.hazen.hazennstuff.Registries.HnSParticleHelper;
import net.hazen.hazennstuff.Registries.HnSSounds;
import net.hazen.hazennstuff.Spells.HnSSpellRegistries;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SoulSeeker extends AbstractMagicProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private LivingEntity lockedTarget = null;
   private final RawAnimation idle = RawAnimation.begin().thenLoop("animation.spark.idle");
   private float damage = 2.0F;

   public SoulSeeker(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
   }

   public SoulSeeker(Level level, LivingEntity shooter) {
      this((EntityType<? extends Projectile>)HnSEntityRegistry.SOUL_SEEKER.get(), level);
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
      Vec3 pos = this.getBoundingBox().getCenter().add(this.getDeltaMovement());
      Vec3 random = Utils.getRandomVec3(0.0);
      pos = pos.add(this.getDeltaMovement());
      this.level.addParticle(HnSParticleHelper.BLADE_PARTICLE, pos.x, pos.y, pos.z, random.x, random.y, random.z);
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level, HnSParticleHelper.BLADE_PARTICLE, x, y, z, 12, 0.08, 0.08, 0.08, 0.3, false);
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

   protected void onHitBlock(BlockHitResult blockHitResult) {
      super.onHitBlock(blockHitResult);
      this.discard();
   }

   private LivingEntity findNearestTarget() {
      List<LivingEntity> entities = this.level
         .getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(12.0), e -> e != this.getOwner() && e.isAlive() && !e.isInvisible());
      return entities.stream().min(Comparator.comparingDouble(e -> e.distanceToSqr(this))).orElse(null);
   }

   private float getYawFromVec(Vec3 vec) {
      return (float)(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 180.0);
   }

   private float getPitchFromVec(Vec3 vec) {
      return (float)(-Math.toDegrees(Math.atan2(vec.y, vec.horizontalDistance())));
   }

   private float rotLerp(float lerpSpeed, float current, float target) {
      float delta = MathHelper.wrapDegrees(target - current);
      return current + delta * Math.min(lerpSpeed * 0.05F, 1.0F);
   }

   public void tick() {
      super.tick();
      if (!this.level.isClientSide && this.tickCount > 10) {
         if (this.lockedTarget == null || !this.lockedTarget.isAlive()) {
            this.lockedTarget = this.findNearestTarget();
         }

         if (this.lockedTarget != null) {
            Vec3 toTarget = this.lockedTarget.getEyePosition().subtract(this.position());
            Vec3 currentMotion = this.getDeltaMovement();
            double homingStrength = 0.05;
            double speed = currentMotion.length();
            Vec3 desiredDirection = toTarget.normalize();
            Vec3 newDirection = currentMotion.normalize().scale(1.0 - homingStrength).add(desiredDirection.scale(homingStrength)).normalize();
            Vec3 newMotion = newDirection.scale(speed);
            this.setDeltaMovement(newMotion);
         }
      }
   }

   protected void onHitEntity(EntityHitResult entityHitResult) {
      super.onHitEntity(entityHitResult);
      Entity target = entityHitResult.getEntity();
      LivingEntity owner = (LivingEntity)this.getOwner();
      CounterSpellEvent event = new CounterSpellEvent(owner, target);
      NeoForge.EVENT_BUS.post(event);
      if (!event.isCanceled() && target instanceof LivingEntity livingTarget) {
         MagicData targetMagicData = MagicData.getPlayerMagicData(livingTarget);
         if (target instanceof AntiMagicSusceptible antiMagicTarget) {
            if (antiMagicTarget instanceof IMagicSummon summon) {
               if (summon.getSummoner() == owner) {
                  if (summon instanceof Mob mob && mob.getTarget() == null) {
                     antiMagicTarget.onAntiMagic(targetMagicData);
                  }
               } else {
                  antiMagicTarget.onAntiMagic(targetMagicData);
               }
            } else {
               antiMagicTarget.onAntiMagic(targetMagicData);
            }
         } else if (target instanceof ServerPlayer serverPlayer) {
            Utils.serverSideCancelCast(serverPlayer, true);
            MagicData.getPlayerMagicData(serverPlayer).getPlayerRecasts().removeAll(RecastResult.COUNTERSPELL);
         } else if (target instanceof IMagicEntity magicEntity) {
            magicEntity.cancelCast();
         }

         for (Holder<MobEffect> mobEffect : livingTarget.getActiveEffectsMap().keySet().stream().toList()) {
            if (mobEffect.value() instanceof MagicMobEffect) {
               livingTarget.removeEffect(mobEffect);
            }
         }

         try {
            DamageSources.applyDamage(target, this.damage, HnSSpellRegistries.SOUL_SEEKERS.get().getDamageSource(this, owner));
         } catch (Exception var12) {
         }
      }

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
