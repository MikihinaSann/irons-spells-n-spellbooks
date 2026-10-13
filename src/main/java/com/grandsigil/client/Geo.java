package com.grandsigil.client;

import com.grandsigil.entity.Terrain;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class Geo {
   private static final float STROKE_STEP = 1.5F;
   private static final Vector3f LIGHT = new Vector3f(0.42F, 0.8F, 0.28F).normalize();
   private static final float AMBIENT = 0.38F;
   private static final float DIFFUSE = 0.72F;

   private Geo() {
   }

   public static void band(
      Matrix4f m,
      VertexConsumer vc,
      Terrain ground,
      float y,
      float rIn,
      float rOut,
      int segments,
      float angle0,
      float fraction,
      float r,
      float g,
      float b,
      float aIn,
      float aOut
   ) {
      int steps = Math.max(1, Mth.ceil((float)segments * Mth.clamp(fraction, 0.0F, 1.0F)));
      float span = (float) (Math.PI * 2) * Mth.clamp(fraction, 0.0F, 1.0F);

      for (int i = 0; i < steps; i++) {
         float a0 = angle0 + span * ((float)i / (float)steps);
         float a1 = angle0 + span * ((float)(i + 1) / (float)steps);
         float c0 = Mth.cos(a0);
         float s0 = Mth.sin(a0);
         float c1 = Mth.cos(a1);
         float s1 = Mth.sin(a1);
         ground(m, vc, ground, y, c0 * rIn, s0 * rIn, r, g, b, aIn);
         ground(m, vc, ground, y, c1 * rIn, s1 * rIn, r, g, b, aIn);
         ground(m, vc, ground, y, c1 * rOut, s1 * rOut, r, g, b, aOut);
         ground(m, vc, ground, y, c0 * rOut, s0 * rOut, r, g, b, aOut);
      }
   }

   public static void disc(
      Matrix4f m, VertexConsumer vc, Terrain ground, float y, float radius, int segments, float r, float g, float b, float aCentre, float aEdge
   ) {
      float centreY = y + ground.lift(0.0F, 0.0F);

      for (int i = 0; i < segments; i++) {
         float a0 = (float)((Math.PI * 2) * (double)i / (double)segments);
         float a1 = (float)((Math.PI * 2) * (double)(i + 1) / (double)segments);
         vertex(m, vc, 0.0F, centreY, 0.0F, r, g, b, aCentre);
         vertex(m, vc, 0.0F, centreY, 0.0F, r, g, b, aCentre);
         ground(m, vc, ground, y, Mth.cos(a1) * radius, Mth.sin(a1) * radius, r, g, b, aEdge);
         ground(m, vc, ground, y, Mth.cos(a0) * radius, Mth.sin(a0) * radius, r, g, b, aEdge);
      }
   }

   public static void ticks(
      Matrix4f m,
      VertexConsumer vc,
      Terrain ground,
      float y,
      float rIn,
      float rOut,
      float rOutLong,
      int count,
      int emphasis,
      float angle0,
      float halfWidth,
      float fraction,
      float r,
      float g,
      float b,
      float a
   ) {
      int drawn = Math.max(0, Mth.ceil((float)count * Mth.clamp(fraction, 0.0F, 1.0F)));

      for (int i = 0; i < drawn; i++) {
         float ang = angle0 + (float)((Math.PI * 2) * (double)i / (double)count);
         float dx = Mth.cos(ang);
         float dz = Mth.sin(ang);
         float px = -dz * halfWidth;
         float pz = dx * halfWidth;
         float far = emphasis > 0 && i % emphasis == 0 ? rOutLong : rOut;
         ground(m, vc, ground, y, dx * rIn - px, dz * rIn - pz, r, g, b, a);
         ground(m, vc, ground, y, dx * far - px, dz * far - pz, r, g, b, a);
         ground(m, vc, ground, y, dx * far + px, dz * far + pz, r, g, b, a);
         ground(m, vc, ground, y, dx * rIn + px, dz * rIn + pz, r, g, b, a);
      }
   }

   public static void stroke(
      Matrix4f m, VertexConsumer vc, Terrain ground, float y, float x1, float z1, float x2, float z2, float halfWidth, float r, float g, float b, float a
   ) {
      float dx = x2 - x1;
      float dz = z2 - z1;
      float len = Mth.sqrt(dx * dx + dz * dz);
      if (!(len < 1.0E-4F)) {
         float px = -dz / len * halfWidth;
         float pz = dx / len * halfWidth;
         int steps = Math.max(1, Mth.ceil(len / 1.5F));
         float ax = x1;
         float az = z1;
         float ay = y + ground.lift(x1, z1);

         for (int i = 1; i <= steps; i++) {
            float t = (float)i / (float)steps;
            float bx = x1 + dx * t;
            float bz = z1 + dz * t;
            float by = y + ground.lift(bx, bz);
            vertex(m, vc, ax - px, ay, az - pz, r, g, b, a);
            vertex(m, vc, bx - px, by, bz - pz, r, g, b, a);
            vertex(m, vc, bx + px, by, bz + pz, r, g, b, a);
            vertex(m, vc, ax + px, ay, az + pz, r, g, b, a);
            ax = bx;
            az = bz;
            ay = by;
         }
      }
   }

   public static void star(
      Matrix4f m,
      VertexConsumer vc,
      Terrain ground,
      float y,
      float radius,
      int points,
      int step,
      float angle0,
      float halfWidth,
      float fraction,
      float r,
      float g,
      float b,
      float a
   ) {
      int drawn = Math.max(0, Mth.ceil((float)points * Mth.clamp(fraction, 0.0F, 1.0F)));

      for (int i = 0; i < drawn; i++) {
         float a0 = angle0 + (float)((Math.PI * 2) * (double)i / (double)points);
         float a1 = angle0 + (float)((Math.PI * 2) * (double)((i + step) % points) / (double)points);
         stroke(m, vc, ground, y, Mth.cos(a0) * radius, Mth.sin(a0) * radius, Mth.cos(a1) * radius, Mth.sin(a1) * radius, halfWidth, r, g, b, a);
      }
   }

   public static void seat(
      Matrix4f m,
      VertexConsumer vc,
      Terrain ground,
      float y,
      float cx,
      float cz,
      float half,
      float facing,
      float u0,
      float v0,
      float u1,
      float v1,
      float r,
      float g,
      float b,
      float a
   ) {
      float fx = Mth.cos(facing);
      float fz = Mth.sin(facing);
      float sx = -fz;
      float sy = y + ground.lift(cx, cz);
      vertex(m, vc, cx - fx * half - sx * half, sy, cz - fz * half - fx * half, r, g, b, a, u0, v1);
      vertex(m, vc, cx + fx * half - sx * half, sy, cz + fz * half - fx * half, r, g, b, a, u0, v0);
      vertex(m, vc, cx + fx * half + sx * half, sy, cz + fz * half + fx * half, r, g, b, a, u1, v0);
      vertex(m, vc, cx - fx * half + sx * half, sy, cz - fz * half + fx * half, r, g, b, a, u1, v1);
   }

   public static void script(
      Matrix4f m,
      VertexConsumer vc,
      Terrain ground,
      float y,
      float rIn,
      float rOut,
      int segments,
      float angle0,
      float fraction,
      float repeats,
      float scroll,
      float r,
      float g,
      float b,
      float a
   ) {
      int steps = Math.max(1, Mth.ceil((float)segments * Mth.clamp(fraction, 0.0F, 1.0F)));
      float span = (float) (Math.PI * 2) * Mth.clamp(fraction, 0.0F, 1.0F);

      for (int i = 0; i < steps; i++) {
         float f0 = (float)i / (float)steps;
         float f1 = (float)(i + 1) / (float)steps;
         float a0 = angle0 + span * f0;
         float a1 = angle0 + span * f1;
         float c0 = Mth.cos(a0);
         float s0 = Mth.sin(a0);
         float c1 = Mth.cos(a1);
         float s1 = Mth.sin(a1);
         float u0 = f0 * repeats + scroll;
         float u1 = f1 * repeats + scroll;
         ground(m, vc, ground, y, c0 * rIn, s0 * rIn, r, g, b, a, u0, 1.0F);
         ground(m, vc, ground, y, c1 * rIn, s1 * rIn, r, g, b, a, u1, 1.0F);
         ground(m, vc, ground, y, c1 * rOut, s1 * rOut, r, g, b, a, u1, 0.0F);
         ground(m, vc, ground, y, c0 * rOut, s0 * rOut, r, g, b, a, u0, 0.0F);
      }
   }

   public static void billboard(
      Matrix4f m,
      VertexConsumer vc,
      float cx,
      float cy,
      float cz,
      Vector3f right,
      Vector3f up,
      float half,
      float u0,
      float v0,
      float u1,
      float v1,
      float r,
      float g,
      float b,
      float a
   ) {
      float rx = right.x * half;
      float ry = right.y * half;
      float rz = right.z * half;
      float ux = up.x * half;
      float uy = up.y * half;
      float uz = up.z * half;
      vertex(m, vc, cx - rx - ux, cy - ry - uy, cz - rz - uz, r, g, b, a, u0, v1);
      vertex(m, vc, cx + rx - ux, cy + ry - uy, cz + rz - uz, r, g, b, a, u1, v1);
      vertex(m, vc, cx + rx + ux, cy + ry + uy, cz + rz + uz, r, g, b, a, u1, v0);
      vertex(m, vc, cx - rx + ux, cy - ry + uy, cz - rz + uz, r, g, b, a, u0, v0);
   }

   public static void column(
      Matrix4f m,
      VertexConsumer vc,
      float x,
      float z,
      float y0,
      float y1,
      float r0,
      float r1,
      int segments,
      float angle0,
      float vSpan,
      float vScroll,
      float r,
      float g,
      float b,
      float aBottom,
      float aTop
   ) {
      for (int i = 0; i < segments; i++) {
         float a0 = angle0 + (float)((Math.PI * 2) * (double)i / (double)segments);
         float a1 = angle0 + (float)((Math.PI * 2) * (double)(i + 1) / (double)segments);
         float c0 = Mth.cos(a0);
         float s0 = Mth.sin(a0);
         float c1 = Mth.cos(a1);
         float s1 = Mth.sin(a1);
         float u0 = (float)i / (float)segments;
         float u1 = (float)(i + 1) / (float)segments;
         vertex(m, vc, x + c0 * r0, y0, z + s0 * r0, r, g, b, aBottom, u0, vScroll + vSpan);
         vertex(m, vc, x + c1 * r0, y0, z + s1 * r0, r, g, b, aBottom, u1, vScroll + vSpan);
         vertex(m, vc, x + c1 * r1, y1, z + s1 * r1, r, g, b, aTop, u1, vScroll);
         vertex(m, vc, x + c0 * r1, y1, z + s0 * r1, r, g, b, aTop, u0, vScroll);
      }
   }

   public static void columnPlain(
      Matrix4f m,
      VertexConsumer vc,
      float x,
      float z,
      float y0,
      float y1,
      float r0,
      float r1,
      int segments,
      float angle0,
      float r,
      float g,
      float b,
      float aBottom,
      float aTop
   ) {
      for (int i = 0; i < segments; i++) {
         float a0 = angle0 + (float)((Math.PI * 2) * (double)i / (double)segments);
         float a1 = angle0 + (float)((Math.PI * 2) * (double)(i + 1) / (double)segments);
         float c0 = Mth.cos(a0);
         float s0 = Mth.sin(a0);
         float c1 = Mth.cos(a1);
         float s1 = Mth.sin(a1);
         vertex(m, vc, x + c0 * r0, y0, z + s0 * r0, r, g, b, aBottom);
         vertex(m, vc, x + c1 * r0, y0, z + s1 * r0, r, g, b, aBottom);
         vertex(m, vc, x + c1 * r1, y1, z + s1 * r1, r, g, b, aTop);
         vertex(m, vc, x + c0 * r1, y1, z + s0 * r1, r, g, b, aTop);
      }
   }

   public static void strand(
      Matrix4f m, VertexConsumer vc, Vector3f from, Vector3f to, Vector3f viewer, float halfWidth, float r, float g, float b, float aFrom, float aTo
   ) {
      Vector3f run = new Vector3f(to).sub(from);
      float len = run.length();
      if (!(len < 1.0E-4F)) {
         Vector3f mid = new Vector3f(from).add(to).mul(0.5F);
         Vector3f toViewer = new Vector3f(viewer).sub(mid);
         Vector3f side = new Vector3f(run).cross(toViewer);
         if (side.lengthSquared() < 1.0E-8F) {
            side = new Vector3f(run).cross(new Vector3f(0.0F, 1.0F, 0.0F));
         }

         side.normalize().mul(halfWidth);
         vertex(m, vc, from.x - side.x, from.y - side.y, from.z - side.z, r, g, b, aFrom);
         vertex(m, vc, to.x - side.x, to.y - side.y, to.z - side.z, r, g, b, aTo);
         vertex(m, vc, to.x + side.x, to.y + side.y, to.z + side.z, r, g, b, aTo);
         vertex(m, vc, from.x + side.x, from.y + side.y, from.z + side.z, r, g, b, aFrom);
      }
   }

   public static void sphere(
      Matrix4f m,
      VertexConsumer vc,
      float cx,
      float cy,
      float cz,
      float radius,
      int segments,
      int rings,
      float uRepeat,
      float uScroll,
      float vScroll,
      float r,
      float g,
      float b,
      float a,
      Geo.Shade shade
   ) {
      for (int ring = 0; ring < rings; ring++) {
         float v0 = (float)ring / (float)rings;
         float v1 = (float)(ring + 1) / (float)rings;
         float phi0 = (float)(Math.PI * (double)v0);
         float phi1 = (float)(Math.PI * (double)v1);
         float sp0 = Mth.sin(phi0);
         float cp0 = Mth.cos(phi0);
         float sp1 = Mth.sin(phi1);
         float cp1 = Mth.cos(phi1);

         for (int seg = 0; seg < segments; seg++) {
            float u0 = (float)seg / (float)segments;
            float u1 = (float)(seg + 1) / (float)segments;
            float th0 = (float)((Math.PI * 2) * (double)u0);
            float th1 = (float)((Math.PI * 2) * (double)u1);
            float st0 = Mth.sin(th0);
            float ct0 = Mth.cos(th0);
            float st1 = Mth.sin(th1);
            float ct1 = Mth.cos(th1);
            shaded(m, vc, cx, cy, cz, radius, ct0 * sp0, cp0, st0 * sp0, u0 * uRepeat + uScroll, v0 + vScroll, r, g, b, a, shade);
            shaded(m, vc, cx, cy, cz, radius, ct1 * sp0, cp0, st1 * sp0, u1 * uRepeat + uScroll, v0 + vScroll, r, g, b, a, shade);
            shaded(m, vc, cx, cy, cz, radius, ct1 * sp1, cp1, st1 * sp1, u1 * uRepeat + uScroll, v1 + vScroll, r, g, b, a, shade);
            shaded(m, vc, cx, cy, cz, radius, ct0 * sp1, cp1, st0 * sp1, u0 * uRepeat + uScroll, v1 + vScroll, r, g, b, a, shade);
         }
      }
   }

   private static void shaded(
      Matrix4f m,
      VertexConsumer vc,
      float cx,
      float cy,
      float cz,
      float radius,
      float nx,
      float ny,
      float nz,
      float u,
      float v,
      float r,
      float g,
      float b,
      float a,
      Geo.Shade shade
   ) {
      float k = shade.at(nx, ny, nz, u, v);
      vertex(m, vc, cx + nx * radius, cy + ny * radius, cz + nz * radius, r, g, b, a * k, u, v);
   }

   public static void arc(
      Matrix4f m,
      VertexConsumer vc,
      Vector3f from,
      Vector3f control,
      Vector3f to,
      Vector3f viewer,
      int steps,
      float halfWidth,
      float taper,
      float r,
      float g,
      float b,
      float aFrom,
      float aTo
   ) {
      Vector3f previous = new Vector3f(from);

      for (int i = 1; i <= steps; i++) {
         float t = (float)i / (float)steps;
         float s = 1.0F - t;
         Vector3f point = new Vector3f(
            s * s * from.x + 2.0F * s * t * control.x + t * t * to.x,
            s * s * from.y + 2.0F * s * t * control.y + t * t * to.y,
            s * s * from.z + 2.0F * s * t * control.z + t * t * to.z
         );
         float mid = 1.0F - Math.abs(t - 0.5F) * 2.0F;
         float width = halfWidth * (1.0F - taper + taper * mid);
         float alpha = Mth.lerp(t, aFrom, aTo);
         strand(m, vc, previous, point, viewer, width, r, g, b, alpha, alpha);
         previous = point;
      }
   }

   public static void taper(
      Matrix4f m,
      VertexConsumer vc,
      Vector3f from,
      Vector3f to,
      Vector3f viewer,
      float halfFrom,
      float halfTo,
      float r,
      float g,
      float b,
      float aFrom,
      float aTo
   ) {
      Vector3f run = new Vector3f(to).sub(from);
      if (!(run.lengthSquared() < 1.0E-6F)) {
         Vector3f mid = new Vector3f(from).add(to).mul(0.5F);
         Vector3f side = new Vector3f(run).cross(new Vector3f(viewer).sub(mid));
         if (side.lengthSquared() < 1.0E-8F) {
            side = new Vector3f(run).cross(new Vector3f(0.0F, 1.0F, 0.0F));
         }

         side.normalize();
         Vector3f a = new Vector3f(side).mul(halfFrom);
         Vector3f c = new Vector3f(side).mul(halfTo);
         vertex(m, vc, from.x - a.x, from.y - a.y, from.z - a.z, r, g, b, aFrom);
         vertex(m, vc, to.x - c.x, to.y - c.y, to.z - c.z, r, g, b, aTo);
         vertex(m, vc, to.x + c.x, to.y + c.y, to.z + c.z, r, g, b, aTo);
         vertex(m, vc, from.x + a.x, from.y + a.y, from.z + a.z, r, g, b, aFrom);
      }
   }

   public static void blob(
      Matrix4f m,
      VertexConsumer vc,
      float cx,
      float cy,
      float cz,
      float rx,
      float ry,
      float rz,
      float leanX,
      float leanZ,
      float lean,
      int segments,
      int rings,
      float uRepeat,
      float uScroll,
      float vScroll,
      float r,
      float g,
      float b,
      float a,
      Geo.Shade shade
   ) {
      for (int ring = 0; ring < rings; ring++) {
         float v0 = (float)ring / (float)rings;
         float v1 = (float)(ring + 1) / (float)rings;
         float phi0 = (float)(Math.PI * (double)v0);
         float phi1 = (float)(Math.PI * (double)v1);
         float sp0 = Mth.sin(phi0);
         float cp0 = Mth.cos(phi0);
         float sp1 = Mth.sin(phi1);
         float cp1 = Mth.cos(phi1);

         for (int seg = 0; seg < segments; seg++) {
            float u0 = (float)seg / (float)segments;
            float u1 = (float)(seg + 1) / (float)segments;
            float th0 = (float)((Math.PI * 2) * (double)u0);
            float th1 = (float)((Math.PI * 2) * (double)u1);
            float st0 = Mth.sin(th0);
            float ct0 = Mth.cos(th0);
            float st1 = Mth.sin(th1);
            float ct1 = Mth.cos(th1);
            leaned(m, vc, cx, cy, cz, rx, ry, rz, leanX, leanZ, lean, ct0 * sp0, cp0, st0 * sp0, u0 * uRepeat + uScroll, v0 + vScroll, r, g, b, a, shade);
            leaned(m, vc, cx, cy, cz, rx, ry, rz, leanX, leanZ, lean, ct1 * sp0, cp0, st1 * sp0, u1 * uRepeat + uScroll, v0 + vScroll, r, g, b, a, shade);
            leaned(m, vc, cx, cy, cz, rx, ry, rz, leanX, leanZ, lean, ct1 * sp1, cp1, st1 * sp1, u1 * uRepeat + uScroll, v1 + vScroll, r, g, b, a, shade);
            leaned(m, vc, cx, cy, cz, rx, ry, rz, leanX, leanZ, lean, ct0 * sp1, cp1, st0 * sp1, u0 * uRepeat + uScroll, v1 + vScroll, r, g, b, a, shade);
         }
      }
   }

   private static void leaned(
      Matrix4f m,
      VertexConsumer vc,
      float cx,
      float cy,
      float cz,
      float rx,
      float ry,
      float rz,
      float leanX,
      float leanZ,
      float lean,
      float nx,
      float ny,
      float nz,
      float u,
      float v,
      float r,
      float g,
      float b,
      float a,
      Geo.Shade shade
   ) {
      float k = shade.at(nx, ny, nz, u, v);
      float slide = (ny * 0.5F + 0.5F) * lean * ry;
      vertex(m, vc, cx + nx * rx + leanX * slide, cy + ny * ry, cz + nz * rz + leanZ * slide, r, g, b, a * k, u, v);
   }

   public static void tube(
      Matrix4f m,
      VertexConsumer vc,
      Vector3f[] path,
      float[] radius,
      float[] bright,
      int sides,
      float uScale,
      float uScroll,
      float r,
      float g,
      float b,
      float a
   ) {
      if (path.length >= 2) {
         Vector3f[] side = new Vector3f[path.length];
         Vector3f[] out = new Vector3f[path.length];
         Vector3f carried = null;

         for (int i = 0; i < path.length; i++) {
            Vector3f ahead = i + 1 < path.length ? new Vector3f(path[i + 1]).sub(path[i]) : new Vector3f(path[i]).sub(path[i - 1]);
            if (ahead.lengthSquared() < 1.0E-8F) {
               ahead.set(0.0F, 1.0F, 0.0F);
            }

            ahead.normalize();
            if (carried == null) {
               Vector3f axis = Math.abs(ahead.y) > 0.9F ? new Vector3f(1.0F, 0.0F, 0.0F) : new Vector3f(0.0F, 1.0F, 0.0F);
               carried = new Vector3f(ahead).cross(axis).normalize();
            } else {
               Vector3f fixed = new Vector3f(carried).sub(new Vector3f(ahead).mul(carried.dot(ahead)));
               if (fixed.lengthSquared() < 1.0E-6F) {
                  Vector3f axis = Math.abs(ahead.y) > 0.9F ? new Vector3f(1.0F, 0.0F, 0.0F) : new Vector3f(0.0F, 1.0F, 0.0F);
                  fixed = new Vector3f(ahead).cross(axis);
               }

               carried = fixed.normalize();
            }

            side[i] = new Vector3f(carried);
            out[i] = new Vector3f(ahead).cross(carried).normalize();
         }

         for (int i = 0; i < path.length - 1; i++) {
            float v0 = (float)i * uScale + uScroll;
            float v1 = (float)(i + 1) * uScale + uScroll;

            for (int f = 0; f < sides; f++) {
               float t0 = (float)((Math.PI * 2) * (double)f / (double)sides);
               float t1 = (float)((Math.PI * 2) * (double)(f + 1) / (double)sides);
               float u0 = (float)f / (float)sides;
               float u1 = (float)(f + 1) / (float)sides;
               ring(m, vc, path[i], side[i], out[i], t0, radius[i], u0, v0, r, g, b, a * bright[i]);
               ring(m, vc, path[i + 1], side[i + 1], out[i + 1], t0, radius[i + 1], u0, v1, r, g, b, a * bright[i + 1]);
               ring(m, vc, path[i + 1], side[i + 1], out[i + 1], t1, radius[i + 1], u1, v1, r, g, b, a * bright[i + 1]);
               ring(m, vc, path[i], side[i], out[i], t1, radius[i], u1, v0, r, g, b, a * bright[i]);
            }
         }
      }
   }

   private static void ring(
      Matrix4f m, VertexConsumer vc, Vector3f at, Vector3f side, Vector3f out, float angle, float radius, float u, float v, float r, float g, float b, float a
   ) {
      float c = Mth.cos(angle) * radius;
      float s = Mth.sin(angle) * radius;
      vertex(m, vc, at.x + side.x * c + out.x * s, at.y + side.y * c + out.y * s, at.z + side.z * c + out.z * s, r, g, b, a, u, v);
   }

   public static Vector3f[] curve(Vector3f from, Vector3f to, float sag, int stations) {
      Vector3f control = new Vector3f(from).add(to).mul(0.5F).add(0.0F, -sag, 0.0F);
      Vector3f[] path = new Vector3f[stations + 1];

      for (int i = 0; i <= stations; i++) {
         float t = (float)i / (float)stations;
         float k = 1.0F - t;
         path[i] = new Vector3f(
            k * k * from.x + 2.0F * k * t * control.x + t * t * to.x,
            k * k * from.y + 2.0F * k * t * control.y + t * t * to.y,
            k * k * from.z + 2.0F * k * t * control.z + t * t * to.z
         );
      }

      return path;
   }

   public static void prism(
      Matrix4f m,
      VertexConsumer vc,
      Vector3f base,
      Vector3f axis,
      float height,
      float radius,
      float shoulder,
      int sides,
      float twist,
      float r,
      float g,
      float b,
      float aBase,
      float aTip
   ) {
      Vector3f up = new Vector3f(axis);
      if (!(up.lengthSquared() < 1.0E-8F)) {
         up.normalize();
         Vector3f u = sideways(up);
         Vector3f v = new Vector3f(up).cross(u).normalize();
         float waist = radius * 0.74F;
         float lift = height * shoulder;
         Vector3f tip = new Vector3f(base).add(new Vector3f(up).mul(height));
         float aMid = Mth.lerp(shoulder, aBase, aTip);
         boolean pointed = shoulder < 0.985F;

         for (int i = 0; i < sides; i++) {
            float t0 = (float)((Math.PI * 2) * (double)i / (double)sides);
            float t1 = (float)((Math.PI * 2) * (double)(i + 1) / (double)sides);
            Vector3f b0 = ring(base, u, v, t0, radius, up, 0.0F);
            Vector3f b1 = ring(base, u, v, t1, radius, up, 0.0F);
            Vector3f s0 = ring(base, u, v, t0 + twist, waist, up, lift);
            Vector3f s1 = ring(base, u, v, t1 + twist, waist, up, lift);
            vertex(m, vc, b0.x, b0.y, b0.z, r, g, b, aBase);
            vertex(m, vc, b1.x, b1.y, b1.z, r, g, b, aBase);
            vertex(m, vc, s1.x, s1.y, s1.z, r, g, b, aMid);
            vertex(m, vc, s0.x, s0.y, s0.z, r, g, b, aMid);
            if (pointed) {
               vertex(m, vc, s0.x, s0.y, s0.z, r, g, b, aMid);
               vertex(m, vc, s1.x, s1.y, s1.z, r, g, b, aMid);
               vertex(m, vc, tip.x, tip.y, tip.z, r, g, b, aTip);
               vertex(m, vc, tip.x, tip.y, tip.z, r, g, b, aTip);
            }
         }
      }
   }

   public static void prismEdges(
      Matrix4f m,
      VertexConsumer vc,
      Vector3f base,
      Vector3f axis,
      float height,
      float radius,
      float shoulder,
      int sides,
      float twist,
      Vector3f viewer,
      float halfWidth,
      float r,
      float g,
      float b,
      float aBase,
      float aTip
   ) {
      Vector3f up = new Vector3f(axis);
      if (!(up.lengthSquared() < 1.0E-8F)) {
         up.normalize();
         Vector3f u = sideways(up);
         Vector3f v = new Vector3f(up).cross(u).normalize();
         float waist = radius * 0.74F;
         float lift = height * shoulder;
         float step = (float)((Math.PI * 2) / (double)sides);
         Vector3f tip = new Vector3f(base).add(new Vector3f(up).mul(height));
         float aMid = Mth.lerp(shoulder, aBase, aTip);

         for (int i = 0; i < sides; i++) {
            float t = (float)((Math.PI * 2) * (double)i / (double)sides);
            Vector3f b0 = ring(base, u, v, t, radius, up, 0.0F);
            Vector3f s0 = ring(base, u, v, t + twist, waist, up, lift);
            Vector3f s1 = ring(base, u, v, t + twist + step, waist, up, lift);
            taper(m, vc, b0, s0, viewer, halfWidth, halfWidth * 0.8F, r, g, b, aBase, aMid);
            taper(m, vc, s0, s1, viewer, halfWidth * 0.8F, halfWidth * 0.8F, r, g, b, aMid, aMid);
            taper(m, vc, s0, tip, viewer, halfWidth * 0.8F, halfWidth * 0.25F, r, g, b, aMid, aTip);
         }
      }
   }

   public static void berg(
      Matrix4f m,
      VertexConsumer vc,
      Vector3f base,
      Vector3f axis,
      float height,
      float radius,
      int sides,
      float tilt,
      long seed,
      float r,
      float g,
      float b,
      float alpha,
      float shade
   ) {
      Vector3f up = new Vector3f(axis);
      if (!(up.lengthSquared() < 1.0E-8F) && !(height <= 0.0F) && !(radius <= 0.0F)) {
         up.normalize();
         Vector3f u = sideways(up);
         Vector3f v = new Vector3f(up).cross(u).normalize();
         float[] rim = new float[sides];
         float[] lip = new float[sides];

         for (int i = 0; i < sides; i++) {
            rim[i] = radius * (0.72F + 0.56F * wobble(seed, i * 2));
            lip[i] = rim[i] * (0.62F + 0.3F * wobble(seed, i * 2 + 1));
         }

         Vector3f[] foot = new Vector3f[sides];
         Vector3f[] head = new Vector3f[sides];

         for (int i = 0; i < sides; i++) {
            float t = (float)((Math.PI * 2) * (double)i / (double)sides);
            foot[i] = ring(base, u, v, t, rim[i], up, 0.0F);
            float lean = tilt * (Mth.cos(t) * (wobble(seed, 90) - 0.5F) * 2.0F + Mth.sin(t) * (wobble(seed, 91) - 0.5F) * 2.0F);
            head[i] = ring(base, u, v, t, lip[i], up, height + lean);
         }

         for (int i = 0; i < sides; i++) {
            int j = (i + 1) % sides;
            Vector3f b0 = foot[i];
            Vector3f b1 = foot[j];
            Vector3f h1 = head[j];
            Vector3f h0 = head[i];
            float lit = light(b0, b1, h1, shade);
            float deep = lit * 0.66F;
            quad(
               m,
               vc,
               b0,
               r * deep,
               g * deep,
               b * (deep + 0.1F),
               alpha,
               b1,
               r * deep,
               g * deep,
               b * (deep + 0.1F),
               alpha,
               h1,
               r * lit,
               g * lit,
               b * lit,
               alpha,
               h0,
               r * lit,
               g * lit,
               b * lit,
               alpha
            );
         }

         Vector3f crown = new Vector3f();

         for (Vector3f point : head) {
            crown.add(point);
         }

         crown.mul(1.0F / (float)sides);

         for (int i = 0; i < sides; i++) {
            int j = (i + 1) % sides;
            float lit = light(crown, head[i], head[j], shade);
            quad(
               m,
               vc,
               crown,
               r * lit,
               g * lit,
               b * lit,
               alpha,
               head[i],
               r * lit,
               g * lit,
               b * lit,
               alpha,
               head[j],
               r * lit,
               g * lit,
               b * lit,
               alpha,
               crown,
               r * lit,
               g * lit,
               b * lit,
               alpha
            );
         }
      }
   }

   private static float light(Vector3f a, Vector3f b, Vector3f c, float shade) {
      Vector3f normal = new Vector3f(b).sub(a).cross(new Vector3f(c).sub(a));
      if (normal.lengthSquared() < 1.0E-9F) {
         return 0.38F;
      } else {
         float lambert = Math.abs(normal.normalize().dot(LIGHT));
         return Mth.lerp(shade, 1.0F, 0.38F + 0.72F * lambert);
      }
   }

   private static float wobble(long seed, int index) {
      long h = seed * 6364136223846793005L + (long)index * 1442695040888963407L;
      h ^= h >>> 33;
      h *= -49064778989728563L;
      h ^= h >>> 33;
      return (float)(h >>> 40 & 65535L) / 65535.0F;
   }

   private static void quad(
      Matrix4f m,
      VertexConsumer vc,
      Vector3f p0,
      float r0,
      float g0,
      float b0,
      float a0,
      Vector3f p1,
      float r1,
      float g1,
      float b1,
      float a1,
      Vector3f p2,
      float r2,
      float g2,
      float b2,
      float a2,
      Vector3f p3,
      float r3,
      float g3,
      float b3,
      float a3
   ) {
      vertex(m, vc, p0.x, p0.y, p0.z, r0, g0, b0, a0);
      vertex(m, vc, p1.x, p1.y, p1.z, r1, g1, b1, a1);
      vertex(m, vc, p2.x, p2.y, p2.z, r2, g2, b2, a2);
      vertex(m, vc, p3.x, p3.y, p3.z, r3, g3, b3, a3);
   }

   public static void bergRim(
      Matrix4f m,
      VertexConsumer vc,
      Vector3f base,
      Vector3f axis,
      float height,
      float radius,
      int sides,
      float tilt,
      long seed,
      Vector3f viewer,
      float weight,
      float r,
      float g,
      float b,
      float alpha
   ) {
      Vector3f up = new Vector3f(axis);
      if (!(up.lengthSquared() < 1.0E-8F) && !(alpha <= 0.002F)) {
         up.normalize();
         Vector3f u = sideways(up);
         Vector3f v = new Vector3f(up).cross(u).normalize();
         Vector3f[] head = new Vector3f[sides];

         for (int i = 0; i < sides; i++) {
            float t = (float)((Math.PI * 2) * (double)i / (double)sides);
            float lip = radius * (0.72F + 0.56F * wobble(seed, i * 2)) * (0.62F + 0.3F * wobble(seed, i * 2 + 1));
            float lean = tilt * (Mth.cos(t) * (wobble(seed, 90) - 0.5F) * 2.0F + Mth.sin(t) * (wobble(seed, 91) - 0.5F) * 2.0F);
            head[i] = ring(base, u, v, t, lip, up, height + lean);
         }

         for (int i = 0; i < sides; i++) {
            Vector3f a = head[i];
            Vector3f c = head[(i + 1) % sides];
            Vector3f along = new Vector3f(c).sub(a);
            if (!(along.lengthSquared() < 1.0E-8F)) {
               Vector3f out = new Vector3f(along).cross(new Vector3f(viewer).sub(a)).normalize().mul(weight);
               quad(
                  m,
                  vc,
                  new Vector3f(a).sub(out),
                  r,
                  g,
                  b,
                  alpha,
                  new Vector3f(c).sub(out),
                  r,
                  g,
                  b,
                  alpha,
                  new Vector3f(c).add(out),
                  r,
                  g,
                  b,
                  alpha,
                  new Vector3f(a).add(out),
                  r,
                  g,
                  b,
                  alpha
               );
            }
         }
      }
   }

   private static Vector3f ring(Vector3f base, Vector3f u, Vector3f v, float angle, float radius, Vector3f up, float lift) {
      float c = Mth.cos(angle) * radius;
      float s = Mth.sin(angle) * radius;
      return new Vector3f(base.x + u.x * c + v.x * s + up.x * lift, base.y + u.y * c + v.y * s + up.y * lift, base.z + u.z * c + v.z * s + up.z * lift);
   }

   private static Vector3f sideways(Vector3f up) {
      Vector3f axis = Math.abs(up.y) > 0.9F ? new Vector3f(1.0F, 0.0F, 0.0F) : new Vector3f(0.0F, 1.0F, 0.0F);
      return new Vector3f(up).cross(axis).normalize();
   }

   public static void ringIn(
      Matrix4f m,
      VertexConsumer vc,
      Vector3f centre,
      Vector3f right,
      Vector3f up,
      float rIn,
      float rOut,
      int segments,
      float angle0,
      float fraction,
      float r,
      float g,
      float b,
      float aIn,
      float aOut
   ) {
      int steps = Math.max(1, Mth.ceil((float)segments * Mth.clamp(fraction, 0.0F, 1.0F)));
      float span = (float) (Math.PI * 2) * Mth.clamp(fraction, 0.0F, 1.0F);

      for (int i = 0; i < steps; i++) {
         float a0 = angle0 + span * ((float)i / (float)steps);
         float a1 = angle0 + span * ((float)(i + 1) / (float)steps);
         planar(m, vc, centre, right, up, a0, rIn, r, g, b, aIn);
         planar(m, vc, centre, right, up, a1, rIn, r, g, b, aIn);
         planar(m, vc, centre, right, up, a1, rOut, r, g, b, aOut);
         planar(m, vc, centre, right, up, a0, rOut, r, g, b, aOut);
      }
   }

   public static void discIn(
      Matrix4f m,
      VertexConsumer vc,
      Vector3f centre,
      Vector3f right,
      Vector3f up,
      float radius,
      int segments,
      float r,
      float g,
      float b,
      float aCentre,
      float aEdge
   ) {
      for (int i = 0; i < segments; i++) {
         float a0 = (float)((Math.PI * 2) * (double)i / (double)segments);
         float a1 = (float)((Math.PI * 2) * (double)(i + 1) / (double)segments);
         vertex(m, vc, centre.x, centre.y, centre.z, r, g, b, aCentre);
         vertex(m, vc, centre.x, centre.y, centre.z, r, g, b, aCentre);
         planar(m, vc, centre, right, up, a1, radius, r, g, b, aEdge);
         planar(m, vc, centre, right, up, a0, radius, r, g, b, aEdge);
      }
   }

   public static void spokesIn(
      Matrix4f m,
      VertexConsumer vc,
      Vector3f centre,
      Vector3f right,
      Vector3f up,
      float rIn,
      float rOut,
      int count,
      float angle0,
      float halfWidth,
      float jitter,
      float r,
      float g,
      float b,
      float aIn,
      float aOut
   ) {
      for (int i = 0; i < count; i++) {
         float a = angle0 + (float)((Math.PI * 2) * (double)i / (double)count);
         float far = rOut * (1.0F - jitter + jitter * Mth.abs(Mth.sin((float)i * 2.399963F)));
         float w = halfWidth * (0.6F + 0.8F * Mth.abs(Mth.cos((float)i * 1.7F)));
         float pa = a + (float) (Math.PI / 2);
         float px = Mth.cos(pa) * w;
         float py = Mth.sin(pa) * w;
         offset(m, vc, centre, right, up, a, rIn, px, py, r, g, b, aIn);
         offset(m, vc, centre, right, up, a, far, px, py, r, g, b, aOut);
         offset(m, vc, centre, right, up, a, far, -px, -py, r, g, b, aOut);
         offset(m, vc, centre, right, up, a, rIn, -px, -py, r, g, b, aIn);
      }
   }

   private static void planar(
      Matrix4f m, VertexConsumer vc, Vector3f centre, Vector3f right, Vector3f up, float angle, float radius, float r, float g, float b, float a
   ) {
      float c = Mth.cos(angle) * radius;
      float s = Mth.sin(angle) * radius;
      vertex(m, vc, centre.x + right.x * c + up.x * s, centre.y + right.y * c + up.y * s, centre.z + right.z * c + up.z * s, r, g, b, a);
   }

   private static void offset(
      Matrix4f m,
      VertexConsumer vc,
      Vector3f centre,
      Vector3f right,
      Vector3f up,
      float angle,
      float radius,
      float ox,
      float oy,
      float r,
      float g,
      float b,
      float a
   ) {
      float c = Mth.cos(angle) * radius + ox;
      float s = Mth.sin(angle) * radius + oy;
      vertex(m, vc, centre.x + right.x * c + up.x * s, centre.y + right.y * c + up.y * s, centre.z + right.z * c + up.z * s, r, g, b, a);
   }

   private static void ground(Matrix4f m, VertexConsumer vc, Terrain terrain, float y, float x, float z, float r, float g, float b, float a) {
      vertex(m, vc, x, y + terrain.lift(x, z), z, r, g, b, a);
   }

   private static void ground(Matrix4f m, VertexConsumer vc, Terrain terrain, float y, float x, float z, float r, float g, float b, float a, float u, float v) {
      vertex(m, vc, x, y + terrain.lift(x, z), z, r, g, b, a, u, v);
   }

   private static void vertex(Matrix4f m, VertexConsumer vc, float x, float y, float z, float r, float g, float b, float a) {
      vc.addVertex(m, x, y, z).setColor(r, g, b, a).setUv(0.5F, 0.5F);
   }

   private static void vertex(Matrix4f m, VertexConsumer vc, float x, float y, float z, float r, float g, float b, float a, float u, float v) {
      vc.addVertex(m, x, y, z).setColor(r, g, b, a).setUv(u, v);
   }

   public interface Shade {
      float at(float var1, float var2, float var3, float var4, float var5);
   }
}
