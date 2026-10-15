package net.alshanex.tunes_n_tomes.entity.fortissimo;

import io.redspace.ironsspellbooks.api.util.Utils;
import net.alshanex.tunes_n_tomes.registry.TEntityRegistry;
import net.alshanex.tunes_n_tomes.registry.TSoundRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FortissimoChargeEntity extends Entity implements GeoEntity {
   private int duration = 44;
   private int age;
   private final RawAnimation idle = RawAnimation.begin().thenPlayAndHold("charge");
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public FortissimoChargeEntity(EntityType<?> entityType, Level level) {
      super(entityType, level);
   }

   public FortissimoChargeEntity(Level level) {
      this((EntityType<?>)TEntityRegistry.FORTISSIMO_CHARGE.get(), level);
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public boolean isOnFire() {
      return false;
   }

   public boolean canCollideWith(Entity entity) {
      return false;
   }

   public boolean canBeHitByProjectile() {
      return false;
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      return false;
   }

   public boolean isInvulnerable() {
      return true;
   }

   public boolean isPushable() {
      return false;
   }

   public void push(Entity pEntity) {
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean canChangeDimensions(Level oldLevel, Level newLevel) {
      return false;
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide && this.age > this.duration) {
         this.discard();
      }

      this.age++;
   }

   public void onAddedToLevel() {
      if (!this.level().isClientSide) {
         this.level()
            .playSound(
               null, this.getX(), this.getY(), this.getZ(), TSoundRegistry.FORTISSIMO_CHARGE, SoundSource.NEUTRAL, 2.0F, 0.9F + Utils.random.nextFloat() * 0.4F
            );
      }

      super.onAddedToLevel();
   }

   protected void defineSynchedData(Builder builder) {
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      this.age = pCompound.getInt("Age");
      this.duration = pCompound.getInt("Duration");
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
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
