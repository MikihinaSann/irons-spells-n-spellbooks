package net.acetheeldritchking.discerning_the_eldritch.entity.spells.ravenous_jaw;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.Optional;
import net.acetheeldritchking.discerning_the_eldritch.particle.DTEParticleHelper;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEEntityRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEPotionEffectRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.SpellRegistries;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController.State;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RavenousJawEntity extends AoeEntity implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   LivingEntity target;
   private final RawAnimation RISE_ANIM = RawAnimation.begin().thenPlay("rise");
   private final RawAnimation FALL_ANIM = RawAnimation.begin().thenPlay("fall");
   private final RawAnimation IDLE_ANIM = RawAnimation.begin().thenPlay("idle");
   private int riseAnimTime = 10;
   private int waitAnimTime = this.riseAnimTime + 5;
   private int dueToFallAnimTime = this.waitAnimTime + 30;
   private final AnimationController<RavenousJawEntity> animationController = new AnimationController(this, "controller", 0, this::predicate);
   private final AnimationController<RavenousJawEntity> riseAnimationController = new AnimationController(this, "rise_controller", 0, this::risePredicate);

   public RavenousJawEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public RavenousJawEntity(Level level, LivingEntity owner, LivingEntity target) {
      this((EntityType<? extends Projectile>)DTEEntityRegistry.RAVENOUS_JAW.get(), level);
      this.setOwner(owner);
      this.target = target;
   }

   public void applyEffect(LivingEntity target) {
      if (target == this.target
         && DamageSources.applyDamage(target, this.getDamage(), SpellRegistries.RAVENOUS_REVENANT.get().getDamageSource(this, this.getOwner()))
         && this.getOwner() instanceof LivingEntity livingOwner) {
         target.setDeltaMovement(target.getDeltaMovement().add(0.0, 0.5, 0.0));
         target.hurtMarked = true;
         target.addEffect(new MobEffectInstance(DTEPotionEffectRegistry.ACCURSED_EFFECT, 1200, 0, false, false, true));
      }
   }

   public float getParticleCount() {
      return 0.0F;
   }

   public Optional<ParticleOptions> getParticle() {
      return Optional.empty();
   }

   protected Vec3 getInflation() {
      return new Vec3(2.5, 2.5, 2.5);
   }

   public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
      super.recreateFromPacket(pPacket);
      this.xRotO = this.getXRot();
      this.yRotO = this.getYRot();
   }

   public void tick() {
      if (this.tickCount < this.riseAnimTime) {
         if (this.target != null) {
            this.setPos(this.target.position());
         }
      } else if (this.tickCount == this.riseAnimTime) {
         this.playSound((SoundEvent)SoundRegistry.DEVOUR_BITE.get(), 2.0F, 0.5F);
      } else if (this.tickCount == this.waitAnimTime) {
         if (this.level().isClientSide()) {
            float y = this.getYRot();
            int countsPerSide = 25;

            for (int i = -countsPerSide; i < countsPerSide; i++) {
               Vec3 motion = new Vec3(0.0, Math.abs(countsPerSide) - i, countsPerSide * 0.5F).yRot(y).normalize().multiply(0.4F, 0.8F, 0.4F);
               this.level().addParticle(DTEParticleHelper.MALIGNANT_FLAME, this.getX(), this.getY() + 0.5, this.getZ(), motion.x, motion.y, motion.z);
            }
         } else {
            this.checkHits();
         }
      } else if (this.tickCount > this.dueToFallAnimTime) {
         this.discard();
      }
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(this.animationController);
      controllerRegistrar.add(this.riseAnimationController);
   }

   private PlayState predicate(AnimationState<RavenousJawEntity> event) {
      AnimationController<RavenousJawEntity> controller = event.getController();
      if (this.tickCount >= this.dueToFallAnimTime - 12) {
         controller.setAnimation(this.FALL_ANIM);
      } else if (controller.getAnimationState() == State.STOPPED) {
         controller.setAnimation(this.IDLE_ANIM);
      }

      return PlayState.CONTINUE;
   }

   private PlayState risePredicate(AnimationState<RavenousJawEntity> event) {
      AnimationController<RavenousJawEntity> controller = event.getController();
      if (this.tickCount < this.riseAnimTime) {
         controller.setAnimation(this.RISE_ANIM);
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
