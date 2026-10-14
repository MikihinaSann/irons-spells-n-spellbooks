package com.gametechbc.traveloptics.effects;

import com.gametechbc.traveloptics.api.particle.ParticleDirection;
import com.gametechbc.traveloptics.api.particle.SphereParticleManager;
import com.gametechbc.traveloptics.init.TOEffects;
import com.github.L_Ender.cataclysm.entity.projectile.Flame_Jet_Entity;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FlareVacuumEffect extends MobEffect {
   public FlareVacuumEffect() {
      super(MobEffectCategory.HARMFUL, 0);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      MobEffectInstance effectInstance = entity.getEffect(TOEffects.FLARE_VACUUM_EFFECT);
      if (effectInstance != null) {
         int duration = effectInstance.getDuration();
         if (duration > 2 && !entity.level().isClientSide) {
            SphereParticleManager.spawnParticles(entity.level(), entity, 2, ParticleHelper.EMBERS, ParticleDirection.INWARD, 2.0);
         }

         if (duration == 2) {
            if (Math.random() < 0.5) {
               this.xStrikeRune(entity, 4, 2.0, amplifier);
            } else {
               this.plusStrikeRune(entity, 4, 2.0, amplifier);
            }
         }

         if (duration < 4 && !entity.level().isClientSide) {
            SphereParticleManager.spawnParticles(entity.level(), entity, 30, ParticleHelper.EMBERS, ParticleDirection.OUTWARD, 6.0);
         }
      }

      for (LivingEntity target : entity.level()
         .getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(3.0), e -> e != entity && e.hasEffect(TOEffects.FLARE_VACUUM_EFFECT))) {
         this.pullEntityTowards(entity, target);
      }

      return true;
   }

   private void pullEntityTowards(LivingEntity source, LivingEntity target) {
      Vec3 sourcePos = source.position();
      Vec3 targetPos = target.position();
      Vec3 direction = sourcePos.subtract(targetPos).normalize();
      double strength = 0.012;
      Vec3 pullForce = direction.scale(strength);
      target.setDeltaMovement(target.getDeltaMovement().add(pullForce));
      target.hurtMarked = true;
   }

   private void plusStrikeRune(LivingEntity entity, int rune, double time, int amplifier) {
      for (int i = 0; i < 4; i++) {
         float yawRadians = (float)Math.toRadians(90.0F + entity.getYRot());
         float throwAngle = yawRadians + i * (float) Math.PI / 2.0F;

         for (int k = 0; k < rune; k++) {
            double distance = 0.8 * (k + 1);
            int delay = (int)(time * (k + 1));
            this.spawnFangs(
               entity,
               entity.getX() + Mth.cos(throwAngle) * 1.25 * distance,
               entity.getZ() + Mth.sin(throwAngle) * 1.25 * distance,
               entity.getY() - 2.0,
               entity.getY() + 2.0,
               throwAngle,
               delay,
               amplifier
            );
         }
      }
   }

   private void xStrikeRune(LivingEntity entity, int rune, double time, int amplifier) {
      for (int i = 0; i < 4; i++) {
         float yawRadians = (float)Math.toRadians(45.0F + entity.getYRot());
         float throwAngle = yawRadians + i * (float) Math.PI / 2.0F;

         for (int k = 0; k < rune; k++) {
            double distance = 0.8 * (k + 1);
            int delay = (int)(time * (k + 1));
            this.spawnFangs(
               entity,
               entity.getX() + Mth.cos(throwAngle) * 1.25 * distance,
               entity.getZ() + Mth.sin(throwAngle) * 1.25 * distance,
               entity.getY() - 2.0,
               entity.getY() + 2.0,
               throwAngle,
               delay,
               amplifier
            );
         }
      }
   }

   private void spawnFangs(LivingEntity entity, double x, double z, double minY, double maxY, float rotation, int delay, int amplifier) {
      BlockPos blockPos = BlockPos.containing(x, maxY, z);
      boolean flag = false;
      double d0 = 0.0;

      do {
         BlockPos blockPosBelow = blockPos.below();
         BlockState blockState = entity.level().getBlockState(blockPosBelow);
         if (blockState.isFaceSturdy(entity.level(), blockPosBelow, Direction.UP)) {
            if (!entity.level().isEmptyBlock(blockPos)) {
               BlockState blockState1 = entity.level().getBlockState(blockPos);
               VoxelShape voxelShape = blockState1.getCollisionShape(entity.level(), blockPos);
               if (!voxelShape.isEmpty()) {
                  d0 = voxelShape.max(Axis.Y);
               }
            }

            flag = true;
            break;
         }

         blockPos = blockPos.below();
      } while (blockPos.getY() >= Mth.floor(minY) - 1);

      if (flag) {
         entity.level()
            .addFreshEntity(new Flame_Jet_Entity(entity.level(), x, blockPos.getY() + d0, z, rotation, delay, this.getFlameJetDamage(amplifier), null));
      }
   }

   private float getFlameJetDamage(int amplifier) {
      return amplifier;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }
}
