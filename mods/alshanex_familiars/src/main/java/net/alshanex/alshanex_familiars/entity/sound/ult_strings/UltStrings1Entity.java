package net.alshanex.alshanex_familiars.entity.sound.ult_strings;

import java.util.UUID;
import javax.annotation.Nullable;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class UltStrings1Entity extends Entity implements GeoEntity {
   @Nullable
   private LivingEntity owner;
   @Nullable
   private UUID ownerUUID;
   private int age;
   private final RawAnimation idle = RawAnimation.begin().thenLoop("spin");
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public UltStrings1Entity(EntityType<?> entityType, Level level) {
      super(entityType, level);
   }

   public UltStrings1Entity(Level level, LivingEntity owner) {
      this((EntityType<?>)EntityRegistry.ULT_STRINGS_1.get(), level);
      this.setOwner(owner);
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public boolean isOnFire() {
      return false;
   }

   public boolean isPushable() {
      return false;
   }

   public boolean isPickable() {
      return false;
   }

   public void tick() {
      super.tick();
      if (!this.level.isClientSide && this.age > 300) {
         this.discard();
      }

      this.age++;
   }

   protected void defineSynchedData(Builder builder) {
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      this.age = pCompound.getInt("Age");
      if (pCompound.hasUUID("Owner")) {
         this.ownerUUID = pCompound.getUUID("Owner");
      }
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      pCompound.putInt("Age", this.age);
      if (this.ownerUUID != null) {
         pCompound.putUUID("Owner", this.ownerUUID);
      }
   }

   public void setOwner(@Nullable LivingEntity pOwner) {
      this.owner = pOwner;
      this.ownerUUID = pOwner == null ? null : pOwner.getUUID();
   }

   @Nullable
   public LivingEntity getOwner() {
      if (this.owner == null && this.ownerUUID != null && this.level instanceof ServerLevel) {
         Entity entity = ((ServerLevel)this.level).getEntity(this.ownerUUID);
         if (entity instanceof LivingEntity) {
            this.owner = (LivingEntity)entity;
         }
      }

      return this.owner;
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
