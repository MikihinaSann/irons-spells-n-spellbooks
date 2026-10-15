package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ThrustLightningEntity extends Projectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public ThrustLightningEntity(EntityType<? extends Projectile> entityType, Level level) {
      super(entityType, level);
      this.setNoGravity(true);
   }

   public ThrustLightningEntity(Level level, double x, double y, double z) {
      this((EntityType<? extends Projectile>)ModEntityTypes.THRUST_LIGHTNING_ENTITY.get(), level);
      this.setPos(x, y, z);
   }

   protected void defineSynchedData(Builder builder) {
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
   }

   public void tick() {
      super.tick();
      if (this.tickCount > 9) {
         this.discard();
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
