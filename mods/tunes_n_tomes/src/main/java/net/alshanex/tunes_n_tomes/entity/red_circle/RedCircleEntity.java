package net.alshanex.tunes_n_tomes.entity.red_circle;

import java.util.UUID;
import javax.annotation.Nullable;
import net.alshanex.tunes_n_tomes.registry.TEntityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
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

public class RedCircleEntity extends Entity implements GeoEntity {
   @Nullable
   private LivingEntity owner;
   @Nullable
   private UUID ownerUUID;
   private int duration = 300;
   private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(RedCircleEntity.class, EntityDataSerializers.INT);
   private int age;
   private final RawAnimation idle = RawAnimation.begin().thenLoop("chord_cycle_red_idle");
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public RedCircleEntity(EntityType<?> entityType, Level level) {
      super(entityType, level);
   }

   public RedCircleEntity(Level level, LivingEntity owner) {
      this((EntityType<?>)TEntityRegistry.RED_CIRCLE.get(), level);
      this.setOwner(owner);
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
      if (this.getOwner() != null) {
         this.setPos(this.getOwner().position());
      }

      if (!this.level().isClientSide && this.age > this.duration) {
         this.discard();
      }

      this.age++;
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(OWNER_ID, -1);
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      this.age = pCompound.getInt("Age");
      this.duration = pCompound.getInt("Duration");
      if (pCompound.hasUUID("Owner")) {
         this.ownerUUID = pCompound.getUUID("Owner");
      }
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      pCompound.putInt("Age", this.age);
      pCompound.putInt("Duration", this.duration);
      if (this.ownerUUID != null) {
         pCompound.putUUID("Owner", this.ownerUUID);
      }
   }

   public void setOwner(@Nullable LivingEntity pOwner) {
      this.owner = pOwner;
      this.ownerUUID = pOwner == null ? null : pOwner.getUUID();
      this.entityData.set(OWNER_ID, pOwner == null ? -1 : pOwner.getId());
   }

   @Nullable
   public LivingEntity getOwner() {
      if (this.owner == null) {
         if (this.level() instanceof ServerLevel && this.ownerUUID != null) {
            if (((ServerLevel)this.level()).getEntity(this.ownerUUID) instanceof LivingEntity le) {
               this.owner = le;
            }
         } else if (this.level().isClientSide) {
            int id = (Integer)this.entityData.get(OWNER_ID);
            if (id != -1 && this.level().getEntity(id) instanceof LivingEntity le) {
               this.owner = le;
            }
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
