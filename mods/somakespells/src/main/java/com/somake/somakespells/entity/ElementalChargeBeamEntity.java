package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ElementalChargeBeamEntity extends Entity {
   public static final int WARMUP_TICKS = 15;
   private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(ElementalChargeBeamEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(ElementalChargeBeamEntity.class, EntityDataSerializers.INT);
   @Nullable
   private LivingEntity activationTarget;
   @Nullable
   private Holder<MobEffect> activationEffect;
   private int effectDuration;
   private int effectAmplifier;
   private boolean activated;

   public ElementalChargeBeamEntity(EntityType<?> entityType, Level level) {
      super(entityType, level);
      this.noPhysics = true;
      this.setNoGravity(true);
   }

   public ElementalChargeBeamEntity(ServerLevel level, LivingEntity target, Holder<MobEffect> effect, int duration, int amplifier, int color) {
      this((EntityType<?>)ModEntityTypes.ELEMENTAL_CHARGE_BEAM.get(), level);
      this.activationTarget = target;
      this.activationEffect = effect;
      this.effectDuration = duration;
      this.effectAmplifier = amplifier;
      this.entityData.set(TARGET_ID, target.getId());
      this.entityData.set(COLOR, color & 16777215);
      this.moveTo(target.position());
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(COLOR, 16777215);
      builder.define(TARGET_ID, -1);
   }

   public void tick() {
      super.tick();
      LivingEntity target = this.resolveTarget();
      if (target != null) {
         this.setPos(target.getX(), target.getY(), target.getZ());
      }

      if (this.level().isClientSide) {
         if (this.tickCount > 16) {
            this.discard();
         }
      } else if (target != null && !target.isRemoved() && target.isAlive()) {
         if (!this.activated && this.tickCount >= 15) {
            this.activated = true;
            this.activateCharge((ServerLevel)this.level(), target);
         }

         if (this.tickCount > 15) {
            this.discard();
         }
      } else {
         this.discard();
      }
   }

   @Nullable
   private LivingEntity resolveTarget() {
      if (!this.level().isClientSide && this.activationTarget != null) {
         return this.activationTarget;
      } else {
         return this.level().getEntity((Integer)this.entityData.get(TARGET_ID)) instanceof LivingEntity living ? living : null;
      }
   }

   private void activateCharge(ServerLevel level, LivingEntity target) {
      if (this.activationEffect != null) {
         target.addEffect(new MobEffectInstance(this.activationEffect, this.effectDuration, this.effectAmplifier, false, false, true));
         int color = this.getColor();
         float red = (color >> 16 & 0xFF) / 255.0F;
         float green = (color >> 8 & 0xFF) / 255.0F;
         float blue = (color & 0xFF) / 255.0F;
         MagicManager.spawnParticles(
            level, new BlastwaveParticleOptions(red, green, blue, 2.5F), target.getX(), target.getY() + 0.08, target.getZ(), 1, 0.0, 0.0, 0.0, 0.0, true
         );
         level.playSound(null, target.blockPosition(), (SoundEvent)SoundRegistry.SUNBEAM_IMPACT.get(), SoundSource.PLAYERS, 1.4F, 1.1F);
      }
   }

   public int getColor() {
      return (Integer)this.entityData.get(COLOR) & 16777215;
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
   }
}
