package net.acetheeldritchking.discerning_the_eldritch.entity.spells.razor_blade;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import java.util.Collections;
import java.util.UUID;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEDamageTypes;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEEntityRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTESoundRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController.State;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RazorBlade extends LivingEntity implements GeoEntity, AntiMagicSusceptible, GeoAnimatable {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   @Nullable
   private LivingEntity owner;
   @Nullable
   private UUID ownerUUID;
   private float damage;
   private int age;
   private DamageSource damageSource;
   private final RawAnimation RISE_ANIM = RawAnimation.begin().thenPlay("rise");
   private final RawAnimation FALL_ANIM = RawAnimation.begin().thenPlay("fall");
   private final RawAnimation IDLE_ANIM = RawAnimation.begin().thenPlay("idle");
   private final AnimationController<RazorBlade> animationController = new AnimationController(this, "controller", 0, this::predicate);
   private final AnimationController<RazorBlade> riseAnimationController = new AnimationController(this, "rise_controller", 0, this::risePredicate);

   public RazorBlade(EntityType<? extends LivingEntity> entityType, Level level) {
      super(entityType, level);
   }

   public RazorBlade(Level level, LivingEntity owner, float damage) {
      this((EntityType<? extends LivingEntity>)DTEEntityRegistry.RAZOR_BLADE.get(), level);
      this.setOwner(owner);
      this.setDamage(damage);
   }

   public void onAntiMagic(MagicData playerMagicData) {
   }

   public void setDamage(float damage) {
      this.damage = damage;
   }

   public boolean isPushedByFluid(FluidType type) {
      return false;
   }

   public boolean isOnFire() {
      return false;
   }

   public void tick() {
      if (!this.level().isClientSide) {
         if (this.age > 150) {
            this.discard();
         } else {
            if (this.age < 1) {
               this.playSound((SoundEvent)DTESoundRegistry.RAZOR_UNSHEATHED.get(), 1.0F, 1.0F);
            }

            if (this.age < 130 && this.age % 20 == 0) {
               this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.1)).forEach(this::dealDamage);
            }
         }

         if (this.age == 100 && Utils.random.nextFloat() < 0.4F) {
            this.playSound((SoundEvent)DTESoundRegistry.RAZOR_SHEATHED.get(), 1.0F, 1.0F);
         }
      }

      this.age++;
   }

   public boolean dealDamage(LivingEntity target) {
      if (this.damageSource == null) {
         this.damageSource = new DamageSource(DamageSources.getHolderFromResource(target, DTEDamageTypes.RAZOR_DAMAGE), this, this.getOwner());
      }

      if (target != this.getOwner() && DamageSources.applyDamage(target, this.damage, this.damageSource)) {
         target.invulnerableTime = 0;
         return true;
      } else {
         return false;
      }
   }

   public void setOwner(@Nullable LivingEntity owner) {
      this.owner = owner;
      this.ownerUUID = owner == null ? null : owner.getUUID();
   }

   public boolean isPushable() {
      return false;
   }

   public boolean isPickable() {
      return false;
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      return !pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) ? false : super.hurt(pSource, pAmount);
   }

   @Nullable
   public LivingEntity getOwner() {
      if (this.owner == null
         && this.ownerUUID != null
         && this.level() instanceof ServerLevel serverLevel
         && serverLevel.getEntity(this.ownerUUID) instanceof LivingEntity livingEntity) {
         this.owner = livingEntity;
      }

      return this.owner;
   }

   public Iterable<ItemStack> getArmorSlots() {
      return Collections.singleton(ItemStack.EMPTY);
   }

   public ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
      return ItemStack.EMPTY;
   }

   public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {
   }

   public HumanoidArm getMainArm() {
      return HumanoidArm.RIGHT;
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.age = compound.getInt("Age");
      if (compound.hasUUID("Owner")) {
         this.ownerUUID = compound.getUUID("Owner");
      }
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("Age", this.age);
      if (this.ownerUUID != null) {
         compound.putUUID("Owner", this.ownerUUID);
      }
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(this.animationController);
      controllerRegistrar.add(this.riseAnimationController);
   }

   private PlayState predicate(AnimationState<RazorBlade> event) {
      AnimationController<RazorBlade> controller = event.getController();
      if (this.age > 100 && Utils.random.nextFloat() < 0.05F) {
         controller.setAnimation(this.FALL_ANIM);
      } else if (controller.getAnimationState() == State.STOPPED) {
         controller.setAnimation(this.IDLE_ANIM);
      }

      return PlayState.CONTINUE;
   }

   private PlayState risePredicate(AnimationState<RazorBlade> event) {
      AnimationController<RazorBlade> controller = event.getController();
      if (this.age < 10) {
         controller.setAnimation(this.RISE_ANIM);
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
