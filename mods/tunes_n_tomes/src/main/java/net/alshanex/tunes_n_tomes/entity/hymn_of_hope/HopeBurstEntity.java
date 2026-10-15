package net.alshanex.tunes_n_tomes.entity.hymn_of_hope;

import net.alshanex.tunes_n_tomes.registry.TEntityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
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

public class HopeBurstEntity extends Entity implements GeoEntity {
   private int duration = 26;
   private int age;
   private final RawAnimation idle = RawAnimation.begin().thenPlayAndHold("shield_active");
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public HopeBurstEntity(EntityType<?> entityType, Level level) {
      super(entityType, level);
   }

   public HopeBurstEntity(Level level) {
      this((EntityType<?>)TEntityRegistry.HOPE_BURST.get(), level);
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
