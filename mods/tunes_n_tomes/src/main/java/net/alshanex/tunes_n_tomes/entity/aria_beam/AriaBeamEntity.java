package net.alshanex.tunes_n_tomes.entity.aria_beam;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import java.util.UUID;
import javax.annotation.Nullable;
import net.alshanex.tunes_n_tomes.registry.TEntityRegistry;
import net.alshanex.tunes_n_tomes.util.ClampedAnimationController;
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
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AriaBeamEntity extends Entity implements GeoEntity {
   @Nullable
   private LivingEntity owner;
   @Nullable
   private UUID ownerUUID;
   private int duration = 200;
   private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(AriaBeamEntity.class, EntityDataSerializers.INT);
   private int age;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public AriaBeamEntity(EntityType<?> entityType, Level level) {
      super(entityType, level);
   }

   public AriaBeamEntity(Level level, LivingEntity owner) {
      this((EntityType<?>)TEntityRegistry.ARIA_BEAM.get(), level);
      this.setOwner(owner);
   }

   public AriaBeamEntity(Level level, LivingEntity owner, int duration) {
      this((EntityType<?>)TEntityRegistry.ARIA_BEAM.get(), level);
      this.setOwner(owner);
      this.duration = duration;
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
      LivingEntity currentOwner = this.getOwner();
      if (currentOwner != null) {
         this.setPos(currentOwner.getX(), currentOwner.getY(), currentOwner.getZ());
         this.setYRot(currentOwner.getYRot());
         this.yRotO = currentOwner.yRotO;
         this.setXRot(currentOwner.getXRot());
         this.xRotO = currentOwner.xRotO;
      }

      if (!this.level().isClientSide) {
         if (currentOwner == null || !currentOwner.isAlive()) {
            this.discard();
            return;
         }

         if (currentOwner instanceof LivingEntity) {
            MagicData magicData = MagicData.getPlayerMagicData(currentOwner);
            if (!magicData.isCasting()) {
               this.discard();
               return;
            }
         }

         if (this.age > this.duration) {
            this.discard();
         }
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

   public int getDuration() {
      return this.duration;
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new ClampedAnimationController(this, "beam_controller", 0, 0.15, 2.9, state -> {
         state.getController().setAnimation(RawAnimation.begin().thenLoop("beam"));
         return PlayState.CONTINUE;
      }));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
