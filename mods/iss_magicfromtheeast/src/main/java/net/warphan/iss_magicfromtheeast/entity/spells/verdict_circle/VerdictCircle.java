package net.warphan.iss_magicfromtheeast.entity.spells.verdict_circle;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.warphan.iss_magicfromtheeast.configs.MFTEServerConfigs;
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

public class VerdictCircle extends AoeEntity implements GeoEntity, AntiMagicSusceptible {
   public float percentBonus;
   public static int onWorldTick = 80;
   @Nullable
   LivingEntity targets;
   private final RawAnimation action = RawAnimation.begin().thenPlay("animation.verdict_circle.action");
   public AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public VerdictCircle(EntityType<? extends Projectile> entityType, Level level) {
      super(entityType, level);
   }

   public VerdictCircle(Level level) {
      this((EntityType<? extends Projectile>)MFTEEntityRegistries.VERDICT_CIRCLE.get(), level);
   }

   protected boolean canHitEntity(Entity pTarget) {
      return !pTarget.isSpectator() && pTarget.isAlive() && pTarget.isPickable();
   }

   public void tick() {
      if (this.tickCount > onWorldTick) {
         this.discard();
      }

      super.tick();
   }

   public void applyEffect(LivingEntity target) {
      if (target != this.getOwner()) {
         target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 4));
         target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 1));
         target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20, 1));
         float targetLostHealth = target.getMaxHealth() - target.getHealth();
         float damageAmount = this.getDamage() + targetLostHealth * this.percentBonus;
         double limitDamage = target.getMaxHealth() * (Double)MFTEServerConfigs.IMPERMANENCE_LIMIT_PERCENT.get();
         if (damageAmount >= limitDamage) {
            damageAmount = (float)limitDamage;
            DamageSources.applyDamage(target, damageAmount, MFTESpellRegistries.UNDERWORLD_AID_SPELL.get().getDamageSource(this, this.getOwner()));
         } else {
            DamageSources.applyDamage(target, damageAmount, MFTESpellRegistries.UNDERWORLD_AID_SPELL.get().getDamageSource(this, this.getOwner()));
         }
      }
   }

   protected Vec3 getInflation() {
      return new Vec3(0.0, 1.0, 0.0);
   }

   public boolean shouldBeSaved() {
      return false;
   }

   public void refreshDimensions() {
   }

   public float getParticleCount() {
      return 0.35F;
   }

   public Optional<ParticleOptions> getParticle() {
      return Optional.of(ParticleTypes.SOUL.getType());
   }

   public void onAntiMagic(MagicData magicData) {
      this.discard();
   }

   private PlayState predicate(AnimationState event) {
      event.getController().setAnimation(this.action);
      return PlayState.CONTINUE;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController(this, "controller", 0, this::predicate));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
