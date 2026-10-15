package com.somake.somakespells.entity;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.registries.ModEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MirrorStrikeVisualEntity extends Projectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private static final EntityDataAccessor<Float> RADIUS = SynchedEntityData.defineId(MirrorStrikeVisualEntity.class, EntityDataSerializers.FLOAT);

   public MirrorStrikeVisualEntity(EntityType<? extends Projectile> entityType, Level level) {
      super(entityType, level);
      this.setNoGravity(true);
   }

   public MirrorStrikeVisualEntity(Level level, double x, double y, double z, float radius) {
      this(resolveEntityType(), level);
      this.setPos(x, y, z);
      this.setRadius(radius);
   }

   private static EntityType<? extends Projectile> resolveEntityType() {
      if (MagicFromTheEastCompat.isLoaded() && ModEntityTypes.MIRROR_STRIKE_VISUAL != null) {
         return (EntityType<? extends Projectile>)ModEntityTypes.MIRROR_STRIKE_VISUAL.get();
      } else {
         throw new IllegalStateException("Mirror Strike visual entity requires ISS compatibility");
      }
   }

   public void setRadius(float radius) {
      this.entityData.set(RADIUS, radius);
   }

   public float getRadius() {
      return (Float)this.entityData.get(RADIUS);
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(RADIUS, 1.0F);
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      if (compound.contains("Radius")) {
         this.setRadius(compound.getFloat("Radius"));
      }
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      compound.putFloat("Radius", this.getRadius());
   }

   public void tick() {
      super.tick();
      if (this.tickCount > 6) {
         this.discard();
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController(this, "controller", 0, event -> event.setAndContinue(RawAnimation.begin().thenPlay("animation.mirror_strike.cast")))
            .setAnimationSpeed(2.0)
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
