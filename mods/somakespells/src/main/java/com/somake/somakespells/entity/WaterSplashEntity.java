package com.somake.somakespells.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
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

public class WaterSplashEntity extends Entity implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private static final EntityDataAccessor<Integer> ANIMATION_STATE = SynchedEntityData.defineId(WaterSplashEntity.class, EntityDataSerializers.INT);

   public WaterSplashEntity(EntityType<?> type, Level level) {
      super(type, level);
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide && this.tickCount > 10) {
         this.discard();
      }
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(ANIMATION_STATE, 0);
   }

   public void setAnimationState(int state) {
      this.entityData.set(ANIMATION_STATE, state);
   }

   public int getAnimationState() {
      return (Integer)this.entityData.get(ANIMATION_STATE);
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController(this, "controller", 0, this::predicate));
   }

   private PlayState predicate(AnimationState<WaterSplashEntity> event) {
      if (this.getAnimationState() == 1) {
         return event.setAndContinue(RawAnimation.begin().thenPlay("bigSplash"));
      } else {
         return this.getAnimationState() == 2
            ? event.setAndContinue(RawAnimation.begin().thenPlay("bigBigSplash"))
            : event.setAndContinue(RawAnimation.begin().thenPlay("skill"));
      }
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
