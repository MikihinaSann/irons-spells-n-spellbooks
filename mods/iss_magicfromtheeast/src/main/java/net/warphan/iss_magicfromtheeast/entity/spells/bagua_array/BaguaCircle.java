package net.warphan.iss_magicfromtheeast.entity.spells.bagua_array;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.warphan.iss_magicfromtheeast.registries.MFTEEffectRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTEEntityRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESpellRegistries;
import net.warphan.iss_magicfromtheeast.util.MFTEParticleHelper;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BaguaCircle extends AoeEntity implements GeoEntity, AntiMagicSusceptible {
   public int healingAmplifier;
   private final RawAnimation baguaspin = RawAnimation.begin().thenPlay("animation.bagua_circle.spin");
   public AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public BaguaCircle(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public BaguaCircle(Level level) {
      this((EntityType<? extends Projectile>)MFTEEntityRegistries.BAGUA_CIRCLE.get(), level);
   }

   protected boolean canHitEntity(Entity pTarget) {
      return !pTarget.isSpectator() && pTarget.isAlive() && pTarget.isPickable();
   }

   public void applyEffect(LivingEntity target) {
      if (target != this.getOwner() && target.getType().is(EntityTypeTags.UNDEAD)) {
         DamageSources.applyDamage(target, this.getDamage(), MFTESpellRegistries.BAGUA_ARRAY_CIRCLE_SPELL.get().getDamageSource(this, this.getOwner()));
         DamageSources.ignoreNextKnockback(target);
      } else if (target == this.getOwner()) {
         target.addEffect(new MobEffectInstance(MFTEEffectRegistries.REVERSAL_HEALING, 40, this.healingAmplifier));
      }
   }

   public void setAmplifier(int amplifier) {
      this.healingAmplifier = amplifier;
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
      return 0.125F;
   }

   public Optional<ParticleOptions> getParticle() {
      return Optional.of(MFTEParticleHelper.JADE_SHATTER);
   }

   public void onAntiMagic(MagicData magicData) {
      this.discard();
   }

   private PlayState predicate(AnimationState event) {
      event.getController().setAnimation(this.baguaspin);
      return PlayState.CONTINUE;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController(this, "controller", 0, this::predicate));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
