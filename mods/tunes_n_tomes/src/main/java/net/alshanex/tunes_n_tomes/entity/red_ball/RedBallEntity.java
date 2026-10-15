package net.alshanex.tunes_n_tomes.entity.red_ball;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import java.util.Optional;
import net.alshanex.tunes_n_tomes.registry.TEffectRegistry;
import net.alshanex.tunes_n_tomes.registry.TEntityRegistry;
import net.alshanex.tunes_n_tomes.registry.TSoundRegistry;
import net.alshanex.tunes_n_tomes.registry.TSpellRegistry;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RedBallEntity extends AbstractMagicProjectile implements GeoEntity {
   private final RawAnimation ANIMATION_LOOP = RawAnimation.begin().thenLoop("ball_red");
   private final AnimationController controller = new AnimationController(this, "blue_ball_controller", 0, this::animationPredicate);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public RedBallEntity(EntityType<? extends AbstractMagicProjectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
   }

   public RedBallEntity(EntityType<? extends AbstractMagicProjectile> entityType, Level levelIn, LivingEntity shooter) {
      this(entityType, levelIn);
      this.setOwner(shooter);
   }

   public RedBallEntity(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends AbstractMagicProjectile>)TEntityRegistry.RED_BALL.get(), levelIn, shooter);
   }

   public void trailParticles() {
   }

   public void impactParticles(double x, double y, double z) {
   }

   public float getSpeed() {
      return 1.2F;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(TSoundRegistry.CLEF);
   }

   protected void doImpactSound(Holder<SoundEvent> sound) {
      this.level().playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.NEUTRAL, 2.0F, 0.9F + Utils.random.nextFloat() * 0.4F);
   }

   protected void onHitBlock(BlockHitResult blockHitResult) {
      super.onHitBlock(blockHitResult);
      if (!this.level().isClientSide) {
         RedBallHitEntity hit = new RedBallHitEntity(this.level());
         hit.moveTo(blockHitResult.getLocation());
         this.level().addFreshEntity(hit);
      }

      this.discard();
   }

   protected void onHitEntity(EntityHitResult entityHitResult) {
      super.onHitEntity(entityHitResult);
      if (DamageSources.applyDamage(entityHitResult.getEntity(), this.damage, TSpellRegistry.CLAMOR_NOTE_SPELL.get().getDamageSource(this, this.getOwner()))
         && entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
         livingEntity.addEffect(new MobEffectInstance(TEffectRegistry.DEAFENED, 100));
      }

      if (!this.level().isClientSide) {
         RedBallHitEntity hit = new RedBallHitEntity(this.level());
         hit.moveTo(entityHitResult.getEntity().position());
         this.level().addFreshEntity(hit);
      }

      this.discard();
   }

   public void setRotation(float x, float y) {
      this.setXRot(x);
      this.xRotO = x;
      this.setYRot(y);
      this.yRotO = y;
   }

   private PlayState animationPredicate(AnimationState event) {
      AnimationController controller = event.getController();
      controller.setAnimation(this.ANIMATION_LOOP);
      return PlayState.CONTINUE;
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(this.controller);
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
