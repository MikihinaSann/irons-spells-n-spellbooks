package net.hazen.hazennstuff.Entity.Spells.Fire.ImmolatingObliterator;

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
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import net.hazen.hazennstuff.Registries.HnSEntityRegistry;
import net.hazen.hazennstuff.Spells.HnSSpellRegistries;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
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

public class ScorchingSlash extends AbstractMagicProjectile implements GeoEntity {
   public final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   public int despawnTimer = 20;
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

   public ScorchingSlash(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
   }

   public ScorchingSlash(Level level, LivingEntity shooter) {
      this((EntityType<? extends Projectile>)HnSEntityRegistry.SCORCHING_SLASH.get(), level);
      this.setOwner(shooter);
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level, ParticleHelper.FIERY_SMOKE, x, y, z, 12, 0.08, 0.08, 0.08, 0.3, false);
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

   public float getSpeed() {
      return 1.6F;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(SoundRegistry.FIERY_EXPLOSION);
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

         DamageSources.applyDamage(target, this.damage, HnSSpellRegistries.SCORCHING_SLASH.get().getDamageSource(this, owner));
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

   public void setDamage(float damage) {
      this.damage = damage;
   }
}
