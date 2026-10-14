package com.gametechbc.traveloptics.entity.extended;

import com.gametechbc.traveloptics.init.TOEntities;
import com.github.L_Ender.cataclysm.entity.projectile.Flame_Jet_Entity;
import com.github.L_Ender.cataclysm.entity.projectile.Flare_Bomb_Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ExtendedFlareBombEntity extends Flare_Bomb_Entity {
   private float flameJetDamage = 7.0F;

   public ExtendedFlareBombEntity(EntityType<? extends Flare_Bomb_Entity> type, Level world) {
      super(type, world);
   }

   public ExtendedFlareBombEntity(Level level, LivingEntity owner) {
      this((EntityType<? extends Flare_Bomb_Entity>)TOEntities.EXTENDED_FLARE_BOMB.get(), level);
      this.setOwner(owner);
   }

   public float getFlameJetDamage() {
      return this.flameJetDamage;
   }

   public void setFlameJetDamage(float flameJetDamage) {
      this.flameJetDamage = flameJetDamage;
   }

   protected void onHitEntity(EntityHitResult result) {
      super.onHitEntity(result);
   }

   protected void onHit(HitResult p_37628_) {
      if (!this.level().isClientSide) {
         this.playSound(SoundEvents.GENERIC_BURN, 1.5F, 0.75F);
         this.level().broadcastEntityEvent(this, (byte)4);
         if (this.random.nextBoolean()) {
            this.extendedXStrikeRune(10, 2.0);
         } else {
            this.extendedPlusStrikeRune(10, 2.0);
         }

         this.discard();
      }
   }

   public void tick() {
      super.tick();
   }

   private void extendedPlusStrikeRune(int rune, double time) {
      for (int i = 0; i < 4; i++) {
         float yawRadians = (float)Math.toRadians(90.0F + this.getYRot());
         float throwAngle = yawRadians + i * (float) Math.PI / 2.0F;

         for (int k = 0; k < rune; k++) {
            double distance = 0.8 * (k + 1);
            int delay = (int)(time * (k + 1));
            this.extendedSpawnFangs(
               this.getX() + Mth.cos(throwAngle) * 1.25 * distance,
               this.getZ() + Mth.sin(throwAngle) * 1.25 * distance,
               this.getY() - 2.0,
               this.getY() + 2.0,
               throwAngle,
               delay
            );
         }
      }
   }

   private void extendedXStrikeRune(int rune, double time) {
      for (int i = 0; i < 4; i++) {
         float yawRadians = (float)Math.toRadians(45.0F + this.getYRot());
         float throwAngle = yawRadians + i * (float) Math.PI / 2.0F;

         for (int k = 0; k < rune; k++) {
            double distance = 0.8 * (k + 1);
            int delay = (int)(time * (k + 1));
            this.extendedSpawnFangs(
               this.getX() + Mth.cos(throwAngle) * 1.25 * distance,
               this.getZ() + Mth.sin(throwAngle) * 1.25 * distance,
               this.getY() - 2.0,
               this.getY() + 2.0,
               throwAngle,
               delay
            );
         }
      }
   }

   private void extendedSpawnFangs(double x, double z, double minY, double maxY, float rotation, int delay) {
      float jetDamage = this.getFlameJetDamage();
      BlockPos blockpos = BlockPos.containing(x, maxY, z);
      boolean flag = false;
      double d0 = 0.0;

      do {
         BlockPos blockposBelow = blockpos.below();
         BlockState blockstate = this.level().getBlockState(blockposBelow);
         if (blockstate.isFaceSturdy(this.level(), blockposBelow, Direction.UP)) {
            if (!this.level().isEmptyBlock(blockpos)) {
               BlockState blockstate1 = this.level().getBlockState(blockpos);
               VoxelShape voxelShape = blockstate1.getCollisionShape(this.level(), blockpos);
               if (!voxelShape.isEmpty()) {
                  d0 = voxelShape.max(Axis.Y);
               }
            }

            flag = true;
            break;
         }

         blockpos = blockpos.below();
      } while (blockpos.getY() >= Mth.floor(minY) - 1);

      if (flag) {
         LivingEntity ownerEntity = this.getOwner() instanceof LivingEntity ? (LivingEntity)this.getOwner() : null;
         this.level().addFreshEntity(new Flame_Jet_Entity(this.level(), x, blockpos.getY() + d0, z, rotation, delay, jetDamage, ownerEntity));
      }
   }
}
