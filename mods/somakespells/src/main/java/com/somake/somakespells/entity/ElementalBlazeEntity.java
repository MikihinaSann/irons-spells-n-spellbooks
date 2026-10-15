package com.somake.somakespells.entity;

import com.somake.somakespells.compat.BornInChaosCompat;
import com.somake.somakespells.particle.ElementalFierySmokeParticleOptions;
import com.somake.somakespells.registries.ModCompatEntityTypes;
import com.somake.somakespells.registries.ModEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.level.Level;

public abstract class ElementalBlazeEntity extends Blaze {
   protected ElementalBlazeEntity(EntityType<? extends Blaze> entityType, Level level) {
      super(entityType, level);
   }

   public void aiStep() {
      if (!this.onGround() && this.getDeltaMovement().y < 0.0) {
         this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.6, 1.0));
      }

      if (this.level().isClientSide) {
         if (this.random.nextInt(24) == 0 && !this.isSilent()) {
            this.level()
               .playLocalSound(
                  this.getX() + 0.5,
                  this.getY() + 0.5,
                  this.getZ() + 0.5,
                  SoundEvents.BLAZE_BURN,
                  this.getSoundSource(),
                  1.0F + this.random.nextFloat(),
                  this.random.nextFloat() * 0.7F + 0.3F,
                  false
               );
         }

         ElementalFierySmokeParticleOptions particle = this.getSmokeParticle();

         for (int i = 0; i < 2; i++) {
            this.level().addParticle(particle, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0.0, 0.0, 0.0);
         }
      }

      super.aiStep();
   }

   protected abstract ElementalFierySmokeParticleOptions getSmokeParticle();

   public static ElementalBlazeEntity.SoulBlazeEntity transformToSoulBlaze(ServerLevel level, Blaze source) {
      ElementalBlazeEntity.SoulBlazeEntity target = (ElementalBlazeEntity.SoulBlazeEntity)((EntityType)ModEntityTypes.SOUL_BLAZE.get()).create(level);
      return copyState(source, target);
   }

   public static ElementalBlazeEntity.InfernalBlazeEntity transformToInfernalBlaze(ServerLevel level, ElementalBlazeEntity.SoulBlazeEntity source) {
      if (!BornInChaosCompat.isLoaded()) {
         return null;
      } else {
         ElementalBlazeEntity.InfernalBlazeEntity target = (ElementalBlazeEntity.InfernalBlazeEntity)((EntityType)ModCompatEntityTypes.INFERNAL_BLAZE.get())
            .create(level);
         return copyState(source, target);
      }
   }

   private static <T extends Blaze> T copyState(Blaze source, T target) {
      if (target == null) {
         return null;
      } else {
         CompoundTag tag = source.saveWithoutId(new CompoundTag());
         tag.remove("UUID");
         tag.remove("Passengers");
         target.load(tag);
         target.moveTo(source.getX(), source.getY(), source.getZ(), source.getYRot(), source.getXRot());
         target.setYBodyRot(source.yBodyRot);
         target.setYHeadRot(source.getYHeadRot());
         target.setDeltaMovement(source.getDeltaMovement());
         if (source.getTarget() != null) {
            target.setTarget(source.getTarget());
         }

         if (source.getLastHurtByMob() != null) {
            target.setLastHurtByMob(source.getLastHurtByMob());
         }

         source.level().addFreshEntity(target);
         source.discard();
         return target;
      }
   }

   public static class InfernalBlazeEntity extends ElementalBlazeEntity {
      private static final ElementalFierySmokeParticleOptions SMOKE = new ElementalFierySmokeParticleOptions(0.32F, 0.42F, 1.0F, 0.35F);

      public InfernalBlazeEntity(EntityType<? extends Blaze> entityType, Level level) {
         super(entityType, level);
      }

      @Override
      protected ElementalFierySmokeParticleOptions getSmokeParticle() {
         return SMOKE;
      }
   }

   public static class SoulBlazeEntity extends ElementalBlazeEntity {
      private static final ElementalFierySmokeParticleOptions SMOKE = new ElementalFierySmokeParticleOptions(0.1F, 0.1F, 0.85F, 0.35F);

      public SoulBlazeEntity(EntityType<? extends Blaze> entityType, Level level) {
         super(entityType, level);
      }

      @Override
      protected ElementalFierySmokeParticleOptions getSmokeParticle() {
         return SMOKE;
      }
   }
}
