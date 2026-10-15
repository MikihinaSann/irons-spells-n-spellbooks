package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModMobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BloodCrystalRootVisualEntity extends Entity implements GeoEntity {
   private static final RawAnimation ROOT_ANIMATION = RawAnimation.begin().thenPlay("form").thenLoop("hold");
   private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(BloodCrystalRootVisualEntity.class, EntityDataSerializers.INT);
   private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

   public BloodCrystalRootVisualEntity(EntityType<? extends BloodCrystalRootVisualEntity> entityType, Level level) {
      super(entityType, level);
      this.noPhysics = true;
      this.noCulling = true;
      this.setNoGravity(true);
   }

   public BloodCrystalRootVisualEntity(Level level, LivingEntity target) {
      this((EntityType<? extends BloodCrystalRootVisualEntity>)ModEntityTypes.BLOOD_CRYSTAL_ROOT_VISUAL.get(), level);
      this.setTarget(target);
      this.followTarget(target);
   }

   public static void attach(ServerLevel level, LivingEntity target) {
      boolean alreadyAttached = level.getEntitiesOfClass(
            BloodCrystalRootVisualEntity.class, target.getBoundingBox().inflate(3.0), visual -> visual.getTargetId() == target.getId() && visual.isAlive()
         )
         .stream()
         .findFirst()
         .isPresent();
      if (!alreadyAttached) {
         level.addFreshEntity(new BloodCrystalRootVisualEntity(level, target));
      }
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(TARGET_ID, -1);
   }

   public void tick() {
      super.tick();
      LivingEntity target = this.getTarget();
      if (target != null && target.isAlive()) {
         this.followTarget(target);
      }

      if (!this.level().isClientSide) {
         if (target == null || !target.isAlive() || !target.hasEffect(ModMobEffects.CRIMSON_ROOT)) {
            this.discard();
         }
      }
   }

   private void followTarget(LivingEntity target) {
      this.setPos(target.getX(), target.getY(), target.getZ());
      this.setYRot(target.yBodyRot);
      this.yRotO = target.yBodyRotO;
      this.setDeltaMovement(Vec3.ZERO);
   }

   private void setTarget(LivingEntity target) {
      this.entityData.set(TARGET_ID, target.getId());
   }

   public int getTargetId() {
      return (Integer)this.entityData.get(TARGET_ID);
   }

   @Nullable
   public LivingEntity getTarget() {
      return this.level().getEntity(this.getTargetId()) instanceof LivingEntity living ? living : null;
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
   }

   public boolean isPickable() {
      return false;
   }

   public boolean isAttackable() {
      return false;
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "crystal_root", 0, state -> state.setAndContinue(ROOT_ANIMATION)));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.animationCache;
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return new ClientboundAddEntityPacket(this, serverEntity);
   }
}
