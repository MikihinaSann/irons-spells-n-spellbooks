package com.github.L_Ender.lionfishapi.server.animation;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LegSolver {
   public final LegSolver.Leg[] legs;

   public LegSolver(LegSolver.Leg... legs) {
      this.legs = legs;
   }

   public final void update(LivingEntity entity, float scale) {
      this.update(entity, entity.yBodyRot, scale);
   }

   public final void update(LivingEntity entity, float yaw, float scale) {
      double sideTheta = yaw / (180.0 / Math.PI);
      double sideX = Math.cos(sideTheta) * scale;
      double sideZ = Math.sin(sideTheta) * scale;
      double forwardTheta = sideTheta + (Math.PI / 2);
      double forwardX = Math.cos(forwardTheta) * scale;
      double forwardZ = Math.sin(forwardTheta) * scale;

      for (LegSolver.Leg leg : this.legs) {
         leg.update(entity, sideX, sideZ, forwardX, forwardZ, scale);
      }
   }

   public static final class Leg {
      public final float forward;
      public final float side;
      private final float range;
      private float height;
      private float prevHeight;
      private boolean isWing;

      public Leg(float forward, float side, float range, boolean isWing) {
         this.forward = forward;
         this.side = side;
         this.range = range;
         this.isWing = isWing;
      }

      public float getHeight(float delta) {
         return this.prevHeight + (this.height - this.prevHeight) * delta;
      }

      public void update(LivingEntity entity, double sideX, double sideZ, double forwardX, double forwardZ, float scale) {
         this.prevHeight = this.height;
         double posY = entity.getY();
         float settledHeight = this.settle(
            entity, entity.getX() + sideX * this.side + forwardX * this.forward, posY, entity.getZ() + sideZ * this.side + forwardZ * this.forward, this.height
         );
         this.height = Mth.clamp(settledHeight, -this.range * scale, this.range * scale);
      }

      protected float settle(LivingEntity entity, double x, double y, double z, float height) {
         BlockPos pos = BlockPos.containing(x, y + 0.001, z);
         Vec3 vec3 = new Vec3(x, y, z);
         float dist = this.getDistance(entity.level(), pos, vec3);
         if (1.0F - dist < 0.001) {
            dist = this.getDistance(entity.level(), pos.below(), vec3) + (float)y % 1.0F;
         } else {
            dist = (float)(dist - (1.0 - y % 1.0));
         }

         if (entity.onGround() && height <= dist) {
            return height == dist ? height : Math.min(height + this.getFallSpeed(), dist);
         } else if (height > 0.0F) {
            return height == dist ? height : Math.max(height - this.getRiseSpeed(), dist);
         } else {
            return height;
         }
      }

      protected float getDistance(Level world, BlockPos pos, Vec3 position) {
         BlockState state = world.getBlockState(pos);
         VoxelShape shape = state.getCollisionShape(world, pos);
         if (shape.isEmpty()) {
            return 1.0F;
         }

         Optional<Vec3> closest = shape.closestPointTo(position);
         if (closest.isEmpty()) {
            return 1.0F;
         }

         float closestY = Math.min((float)closest.get().y, 1.0F);
         return position.y < 0.0 ? closestY : 1.0F - closestY;
      }

      protected float getFallSpeed() {
         return 0.25F;
      }

      protected float getRiseSpeed() {
         return 0.25F;
      }
   }
}
