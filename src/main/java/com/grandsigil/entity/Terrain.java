package com.grandsigil.entity;

import com.grandsigil.GsWatchdog;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class Terrain {
   public static final Terrain FLAT = new Terrain(null, 1.0F);
   private static final float SPAN = 1.35F;
   private static final float FADE = 0.45F;
   private static final int ANGLES = 72;
   private static final int RINGS = 20;
   private static final int REACH_UP = 2;
   private static final int REACH_DOWN = 6;
   private final float[] heights;
   private final float reach;
   private float weight = 1.0F;

   private Terrain(float[] heights, float reach) {
      this.heights = heights;
      this.reach = reach;
   }

   public static Terrain sample(BlockGetter level, Vec3 centre, float radius) {
      long started = GsWatchdog.start();

      Terrain var5;
      try {
         var5 = read(level, centre, radius);
      } finally {
         GsWatchdog.end("Terrain.sample", started);
      }

      return var5;
   }

   private static Terrain read(BlockGetter level, Vec3 centre, float radius) {
      float reach = radius * 1.35F;
      float[] heights = new float[1440];
      MutableBlockPos cursor = new MutableBlockPos();

      for (int ring = 0; ring < 20; ring++) {
         float r = reach * (float)ring / 19.0F;

         for (int angle = 0; angle < 72; angle++) {
            double a = (Math.PI * 2) * (double)angle / 72.0;
            double x = centre.x + Math.cos(a) * (double)r;
            double z = centre.z + Math.sin(a) * (double)r;
            heights[ring * 72 + angle] = (float)(surfaceAt(level, cursor, x, z, centre.y) - centre.y);
         }
      }

      return new Terrain(heights, reach);
   }

   private static double surfaceAt(BlockGetter level, MutableBlockPos cursor, double x, double z, double baseY) {
      int col = Mth.floor(x);
      int row = Mth.floor(z);
      int top = Mth.floor(baseY) + 2;
      int bottom = Mth.floor(baseY) - 6;

      for (int y = top; y >= bottom; y--) {
         cursor.set(col, y, row);
         BlockState state = level.getBlockState(cursor);
         if (!state.isAir()) {
            VoxelShape shape = state.getCollisionShape(level, cursor);
            if (!shape.isEmpty()) {
               return (double)y + shape.max(Axis.Y);
            }
         }
      }

      return baseY;
   }

   public void weight(float weight) {
      this.weight = weight;
   }

   public float lift(float x, float z) {
      if (this.heights != null && !(this.weight <= 0.0F)) {
         float distance = Mth.sqrt(x * x + z * z);
         float out = distance / this.reach;
         if (out > 1.45F) {
            return 0.0F;
         } else {
            float edge = out <= 1.0F ? 1.0F : 1.0F - (out - 1.0F) / 0.45F;
            float ringF = Math.min(out, 1.0F) * 19.0F;
            int ring0 = (int)ringF;
            int ring1 = Math.min(ring0 + 1, 19);
            float ringT = ringF - (float)ring0;
            float angleF = (float)(Math.atan2((double)z, (double)x) / (Math.PI * 2)) * 72.0F;
            if (angleF < 0.0F) {
               angleF += 72.0F;
            }

            int angle0 = (int)angleF % 72;
            int angle1 = (angle0 + 1) % 72;
            float angleT = angleF - (float)((int)angleF);
            float inner = Mth.lerp(angleT, this.heights[ring0 * 72 + angle0], this.heights[ring0 * 72 + angle1]);
            float outer = Mth.lerp(angleT, this.heights[ring1 * 72 + angle0], this.heights[ring1 * 72 + angle1]);
            return Mth.lerp(ringT, inner, outer) * this.weight * edge * edge;
         }
      } else {
         return 0.0F;
      }
   }
}
