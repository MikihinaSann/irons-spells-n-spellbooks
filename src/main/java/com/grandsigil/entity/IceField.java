package com.grandsigil.entity;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

public final class IceField {
   private static final int REACH_UP = 5;
   private static final int REACH_DOWN = 12;
   private static final float GOLDEN = 2.3999631F;
   public static final float STAGGER = 0.55F;
   private static final float LEAN = 0.22F;

   private IceField() {
   }

   public static float profile(float r, float angle, float radius) {
      float t = Mth.clamp(r / radius, 0.0F, 1.0F);
      float cap = 0.56F + 0.44F * Mth.sqrt(Mth.clamp(1.0F - t * t, 0.0F, 1.0F));
      float ridges = 1.0F + 0.13F * Mth.sin(angle * 5.0F + t * 3.1F) + 0.07F * Mth.sin(angle * 11.0F - t * 1.7F);
      float stepped = cap * ridges;
      float bench = 0.14F;
      stepped = Mth.lerp(0.55F, stepped, (float)Mth.floor(stepped / bench) * bench + bench * 0.5F);
      float t6 = t * t * t;
      float cliff = Mth.clamp(1.0F - t6 * t6, 0.0F, 1.0F);
      return stepped * (float)Math.pow((double)cliff, 0.32);
   }

   public static List<IceField.Serac> layout(int seed, float radius) {
      RandomSource random = RandomSource.create((long)seed * 6079L + 17L);
      List<IceField.Serac> seracs = new ArrayList<>(260);
      float spread = radius / Mth.sqrt(260.0F) * 1.75F;

      for (int i = 0; i < 260; i++) {
         float angle = (float)i * 2.3999631F;
         float rr = radius * Mth.sqrt(((float)i + 0.5F) / 260.0F);
         float x = Mth.cos(angle) * rr;
         float z = Mth.sin(angle) * rr;
         float shape = profile(rr, angle, radius);
         float top = 42.0F * shape * (0.86F + 0.28F * random.nextFloat());
         float keel = 14.0F * (0.35F + 0.65F * shape) * (0.6F + 0.8F * random.nextFloat());
         float roll = random.nextFloat();
         float bulk = 0.55F + 2.1F * roll * roll * roll;
         Vector3f axis = new Vector3f((random.nextFloat() - 0.5F) * 0.22F, 1.0F, (random.nextFloat() - 0.5F) * 0.22F).normalize();
         seracs.add(
            new IceField.Serac(
               x,
               z,
               top,
               keel,
               axis,
               spread * bulk,
               5 + random.nextInt(4),
               (0.1F + 0.34F * (1.0F - Mth.clamp(bulk / 2.6F, 0.0F, 1.0F))) * spread * bulk,
               random.nextLong(),
               0.55F * Mth.clamp(1.0F - rr / radius, 0.0F, 1.0F) * (0.72F + 0.28F * random.nextFloat())
            )
         );
      }

      return seracs;
   }

   public static float broken(IceField.Serac serac, float breaking) {
      return Mth.clamp((breaking - serac.delay()) / 0.45F, 0.0F, 1.0F);
   }

   public static float floorAt(BlockGetter level, MutableBlockPos cursor, double x, double z, double baseY) {
      int col = Mth.floor(x);
      int row = Mth.floor(z);
      int top = Mth.floor(baseY) + 5;
      int bottom = Mth.floor(baseY) - 12;

      for (int y = top; y >= bottom; y--) {
         cursor.set(col, y, row);
         BlockState state = level.getBlockState(cursor);
         if (!state.isAir()) {
            VoxelShape shape = state.getCollisionShape(level, cursor);
            if (!shape.isEmpty()) {
               return (float)((double)y + shape.max(Axis.Y) - baseY);
            }
         }
      }

      return 0.0F;
   }

   public static record Serac(float x, float z, float top, float keel, Vector3f axis, float radius, int sides, float tilt, long seed, float delay) {
   }
}
