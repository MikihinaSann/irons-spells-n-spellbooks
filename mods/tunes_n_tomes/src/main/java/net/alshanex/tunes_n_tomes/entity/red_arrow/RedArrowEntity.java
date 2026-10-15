package net.alshanex.tunes_n_tomes.entity.red_arrow;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import java.util.Optional;
import net.alshanex.tunes_n_tomes.registry.TEffectRegistry;
import net.alshanex.tunes_n_tomes.registry.TEntityRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
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
import software.bernie.geckolib.util.GeckoLibUtil;

public class RedArrowEntity extends AoeEntity implements GeoEntity, AntiMagicSusceptible {
   private DamageSource damageSource;
   private int duration = 20;
   private CameraShakeData cameraShakeData;
   private int age;
   int waveAnim = -1;
   private final RawAnimation idle = RawAnimation.begin().thenLoop("arrow_red");
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public RedArrowEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setCircular();
   }

   public RedArrowEntity(Level level) {
      this((EntityType<? extends Projectile>)TEntityRegistry.RED_ARROW.get(), level);
   }

   public void applyEffect(LivingEntity target) {
      if (this.damageSource == null) {
         this.damageSource = new DamageSource(DamageSources.getHolderFromResource(target, ISSDamageTypes.FIRE_FIELD), this, this.getOwner());
      }

      if (!DamageSources.isFriendlyFireBetween(this.getOwner(), target)) {
         DamageSources.ignoreNextKnockback(target);
         if (target.hurt(this.damageSource, this.getDamage())) {
            target.addEffect(new MobEffectInstance(TEffectRegistry.DEAFENED, 60, 0));
            target.setDeltaMovement(target.getDeltaMovement().add(0.0, 0.5, 0.0));
            target.hurtMarked = true;
         }
      }
   }

   public float getParticleCount() {
      return 0.0F;
   }

   protected float particleYOffset() {
      return 0.25F;
   }

   protected float getParticleSpeedModifier() {
      return 1.4F;
   }

   public void ambientParticles() {
   }

   public Optional<ParticleOptions> getParticle() {
      return Optional.empty();
   }

   public void tick() {
      super.tick();
      if (this.tickCount == 1) {
         this.createScreenShake();
      }

      if (this.tickCount % this.reapplicationDelay == 1) {
         this.waveAnim = 0;
      }

      if (!this.level().isClientSide && this.age > this.duration) {
         this.discard();
      }

      this.age++;
   }

   protected boolean canHitTargetForGroundContext(LivingEntity target) {
      return true;
   }

   protected Vec3 getInflation() {
      return new Vec3(0.0, 5.0, 0.0);
   }

   protected void createScreenShake() {
      if (!this.level().isClientSide && !this.isRemoved()) {
         this.cameraShakeData = new CameraShakeData(this.level(), this.duration - this.tickCount, this.position(), 15.0F);
         CameraShakeManager.addCameraShake(this.cameraShakeData);
      }
   }

   public void remove(RemovalReason pReason) {
      super.remove(pReason);
      if (!this.level().isClientSide) {
         CameraShakeManager.removeCameraShake(this.cameraShakeData);
      }
   }

   public void onAntiMagic(MagicData magicData) {
      this.discard();
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.age = pCompound.getInt("Age");
      this.duration = pCompound.getInt("Duration");
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putInt("Age", this.age);
      pCompound.putInt("Duration", this.duration);
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "idle", 0, this::idlePredicate));
   }

   private PlayState idlePredicate(AnimationState event) {
      event.getController().setAnimation(this.idle);
      return PlayState.CONTINUE;
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
