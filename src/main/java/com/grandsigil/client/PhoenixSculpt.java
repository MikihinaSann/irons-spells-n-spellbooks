package com.grandsigil.client;

import com.grandsigil.GrandSigil;
import com.grandsigil.entity.PhoenixEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class PhoenixSculpt {
   public static final ResourceLocation BODY = GrandSigil.id("textures/entity/phoenix_body.png");
   public static final ResourceLocation FEATHER = GrandSigil.id("textures/entity/phoenix_feather.png");
   public static final ResourceLocation PLUME = GrandSigil.id("textures/entity/phoenix_plume.png");
   public static final ResourceLocation FLAME = GrandSigil.id("textures/entity/flame.png");
   public static final ResourceLocation WHITE = GrandSigil.id("textures/entity/white.png");
   public static final ResourceLocation FIRE_FLOW = GrandSigil.id("textures/entity/fire_flow.png");
   public static final ResourceLocation FLAME_FEATHER = GrandSigil.id("textures/entity/flame_feather.png");
   private static final float[][] SPINE = new float[][]{
      {-2.6F, 0.05F, 0.22F, 0.2F},
      {-1.9F, 0.12F, 0.46F, 0.42F},
      {-1.2F, 0.12F, 0.72F, 0.66F},
      {-0.4F, 0.04F, 0.88F, 0.84F},
      {0.4F, 0.05F, 0.92F, 0.92F},
      {1.0F, 0.2F, 0.8F, 0.84F},
      {1.45F, 0.55F, 0.56F, 0.6F},
      {1.8F, 0.95F, 0.42F, 0.46F},
      {2.1F, 1.3F, 0.44F, 0.46F},
      {2.45F, 1.5F, 0.5F, 0.48F},
      {2.85F, 1.55F, 0.42F, 0.4F},
      {3.1F, 1.52F, 0.28F, 0.26F},
      {3.2F, 1.5F, 0.14F, 0.13F}
   };

   private PhoenixSculpt() {
   }

   private static Sculpt.Ring[] spine(PhoenixSculpt.Bird b, float[][] stations, int sub, float swell) {
      List<Vector3f> c = new ArrayList<>();
      List<float[]> r = new ArrayList<>();

      for (int i = 0; i + 1 < stations.length; i++) {
         float[] s0 = stations[Math.max(0, i - 1)];
         float[] s1 = stations[i];
         float[] s2 = stations[i + 1];
         float[] s3 = stations[Math.min(stations.length - 1, i + 2)];

         for (int k = 0; k < sub; k++) {
            float t = (float)k / (float)sub;
            float[] q = new float[4];

            for (int j = 0; j < 4; j++) {
               q[j] = catmull(s0[j], s1[j], s2[j], s3[j], t);
            }

            c.add(b.at(q[0], q[1], 0.0F));
            r.add(q);
         }
      }

      float[] last = stations[stations.length - 1];
      c.add(b.at(last[0], last[1], 0.0F));
      r.add(last);
      Sculpt.Ring[] rings = new Sculpt.Ring[c.size()];
      float sc = b.p.scale;
      float v = 0.0F;

      for (int i = 0; i < rings.length; i++) {
         Vector3f t = new Vector3f((Vector3fc)c.get(Math.min(rings.length - 1, i + 1))).sub((Vector3fc)c.get(Math.max(0, i - 1)));
         if (t.lengthSquared() < 1.0E-8F) {
            t.set(b.p.fwd);
         }

         t.normalize();
         Vector3f side = new Vector3f(b.p.right).sub(new Vector3f(t).mul(t.dot(b.p.right)));
         if (side.lengthSquared() < 1.0E-6F) {
            side.set(b.p.right);
         }

         side.normalize();
         Vector3f up = new Vector3f(side).cross(t).normalize();
         if (i > 0) {
            v += c.get(i).distance((Vector3fc)c.get(i - 1)) / (1.1F * sc);
         }

         float[] q = r.get(i);
         rings[i] = new Sculpt.Ring(c.get(i), side, up, q[2] * sc * swell, q[3] * sc * swell, v, b.alpha);
      }

      return rings;
   }

   private static float catmull(float p0, float p1, float p2, float p3, float t) {
      float t2 = t * t;
      float t3 = t2 * t;
      return 0.5F * (2.0F * p1 + (-p0 + p2) * t + (2.0F * p0 - 5.0F * p1 + 4.0F * p2 - p3) * t2 + (-p0 + 3.0F * p1 - 3.0F * p2 + p3) * t3);
   }

   public static void body(Matrix4f m, VertexConsumer vc, PhoenixSculpt.Bird b) {
      Sculpt.loft(m, vc, spine(b, SPINE, 3, 1.0F), 18, 3.0F, 1.0F, 1.0F, 1.0F, 0.5F);
      float sc = b.p.scale;

      for (int w = 0; w < 2; w++) {
         Vector3f[] pts = new Vector3f[]{b.along(w, 0.0F), b.along(w, 0.5F), b.along(w, 1.0F)};
         float[] rad = new float[]{0.2F * sc, 0.13F * sc, 0.05F * sc};
         Vector3f n = wingNormal(b, w);
         Sculpt.Ring[] rings = new Sculpt.Ring[3];
         float v = 0.0F;

         for (int i = 0; i < 3; i++) {
            Vector3f t = new Vector3f(pts[Math.min(2, i + 1)]).sub(pts[Math.max(0, i - 1)]).normalize();
            Vector3f up = new Vector3f(n).sub(new Vector3f(t).mul(t.dot(n))).normalize();
            Vector3f side = new Vector3f(t).cross(up).normalize();
            if (i > 0) {
               v += pts[i].distance(pts[i - 1]) / (1.1F * sc);
            }

            rings[i] = new Sculpt.Ring(pts[i], side, up, rad[i], rad[i] * 0.7F, v, b.alpha);
         }

         Sculpt.loft(m, vc, rings, 10, 1.0F, 1.0F, 1.0F, 1.0F, 0.5F);
      }
   }

   public static void energyBody(
      Matrix4f m,
      VertexConsumer vc,
      PhoenixSculpt.Bird b,
      float swell,
      float r,
      float g,
      float bl,
      float aFacing,
      float aEdge,
      float power,
      float scroll,
      float wobble
   ) {
      Sculpt.energy(m, vc, spine(b, SPINE, 3, 1.0F), 18, 2.0F, scroll, swell, r, g, bl, aFacing, aEdge, power, wobble, b.t);
      float[][] upper = new float[][]{{3.05F, 1.56F, 0.25F, 0.2F}, {3.45F, 1.46F, 0.18F, 0.14F}, {3.8F, 1.28F, 0.1F, 0.08F}, {4.02F, 1.02F, 0.03F, 0.03F}};
      Sculpt.energy(m, vc, spine(b, upper, 3, 1.0F), 12, 1.0F, scroll, swell, r, g, bl, aFacing, aEdge, power, wobble, b.t);
   }

   public static void beak(Matrix4f m, VertexConsumer vc, PhoenixSculpt.Bird b) {
      float[][] upper = new float[][]{
         {3.05F, 1.56F, 0.25F, 0.2F}, {3.45F, 1.46F, 0.18F, 0.14F}, {3.8F, 1.28F, 0.1F, 0.08F}, {4.02F, 1.02F, 0.03F, 0.03F}, {4.0F, 0.92F, 0.005F, 0.005F}
      };
      float[][] lower = new float[][]{{3.0F, 1.38F, 0.2F, 0.11F}, {3.4F, 1.3F, 0.13F, 0.07F}, {3.7F, 1.24F, 0.03F, 0.02F}};
      Sculpt.loft(m, vc, spine(b, upper, 3, 1.0F), 12, 1.0F, 1.0F, 0.8F, 0.32F, 0.25F);
      Sculpt.loft(m, vc, spine(b, lower, 3, 1.0F), 10, 1.0F, 0.85F, 0.62F, 0.22F, 0.2F);
   }

   private static Vector3f wingNormal(PhoenixSculpt.Bird b, int w) {
      Vector3f n = new Vector3f(b.p.normal[w]);
      if (n.dot(b.p.up) < 0.0F) {
         n.negate();
      }

      return n;
   }

   private static float flicker(float t, int j, int w, float depth) {
      return 1.0F + depth * Mth.sin(t * 0.5F + (float)j * 1.91F + (float)w * 2.7F) + depth * 0.5F * Mth.sin(t * 1.3F + (float)j * 0.77F);
   }

   public static void feathers(Matrix4f m, VertexConsumer vc, PhoenixSculpt.Bird b, boolean halo, float r, float g, float bl, float a) {
      PhoenixPose p = b.p;
      float sc = p.scale;
      float grow = halo ? 1.1F : 1.0F;
      Vector3f back = new Vector3f(p.fwd).mul(-1.0F);

      for (int w = 0; w < 2; w++) {
         int wing = w;
         Vector3f n = wingNormal(b, w);
         Vector3f arm = p.arm[w];
         Vector3f hand = p.hand[w];
         rank(
            16,
            (j, k) -> {
               Vector3f dir = new Vector3f(back).add(new Vector3f(arm).mul(0.1F * k));
               float len = (3.3F + 0.35F * Mth.sin((float)j * 0.9F)) * sc * flicker(b.t, j, wing, 0.05F) * grow;
               Sculpt.feather(
                  m, vc, b.along(wing, 0.07F + 0.43F * k), waver(dir, n, b.t, j, wing), n, back, len, 0.9F * sc * grow, 0.07F, 0.05F, 0.0F, r, g, bl, a, 0.9F
               );
            }
         );
         rank(
            12,
            (j, k) -> {
               Vector3f dir = new Vector3f(back).lerp(hand, 0.18F + 0.82F * (float)Math.pow((double)k, 1.4));
               float len = (3.5F + 2.6F * k) * sc * flicker(b.t, j + 20, wing, 0.07F) * grow;
               Sculpt.feather(
                  m,
                  vc,
                  b.along(wing, 0.5F + 0.48F * k),
                  waver(dir, n, b.t, j + 20, wing),
                  n,
                  back,
                  len,
                  0.82F * sc * grow,
                  0.1F,
                  0.06F,
                  0.01F * sc,
                  r,
                  g,
                  bl,
                  a,
                  0.9F
               );
            }
         );
         if (!halo) {
            rank(5, (j, k) -> {
               Vector3f dir = new Vector3f(back).sub(new Vector3f(arm).mul(0.28F));
               Sculpt.feather(m, vc, b.along(wing, 0.01F + 0.07F * k), dir, n, back, 2.8F * sc, 0.85F * sc, 0.05F, 0.03F, 0.02F * sc, r, g, bl, a, 0.9F);
            });
            rank(
               24,
               (j, k) -> {
                  Vector3f dir = k < 0.52F
                     ? new Vector3f(back).add(new Vector3f(arm).mul(0.08F))
                     : new Vector3f(back).lerp(hand, 0.18F + 0.6F * (k - 0.52F) / 0.48F);
                  float len = (1.9F + 0.5F * k) * sc;
                  Sculpt.feather(
                     m,
                     vc,
                     b.along(wing, 0.04F + 0.9F * k),
                     waver(dir, n, b.t, j + 40, wing),
                     n,
                     back,
                     len,
                     0.78F * sc,
                     0.05F,
                     0.03F,
                     0.05F * sc,
                     r,
                     g,
                     bl,
                     a,
                     0.9F
                  );
               }
            );
            rank(28, (j, k) -> {
               Vector3f rootx = b.along(wing, 0.02F + 0.86F * k).add(new Vector3f(back).mul(0.1F * sc));
               Vector3f dir = new Vector3f(back).add(new Vector3f(arm).mul(0.05F));
               Sculpt.feather(m, vc, rootx, dir, n, back, 1.25F * sc, 0.66F * sc, 0.04F, 0.02F, 0.1F * sc, r, g, bl, a, 0.9F);
            });
            rank(3, (j, k) -> {
               Vector3f dir = new Vector3f(hand).mul(0.7F).add(new Vector3f(back).mul(0.3F + 0.2F * k));
               Sculpt.feather(m, vc, b.along(wing, 0.5F), dir, n, back, (1.3F + 0.3F * k) * sc, 0.5F * sc, 0.05F, 0.02F, 0.12F * sc, r, g, bl, a, 0.9F);
            });
         }
      }

      Vector3f root = b.at(-2.4F, 0.1F, 0.0F);
      Vector3f up = new Vector3f(p.up);
      rank(11, (j, k) -> {
         float ang = (k - 0.5F) * 1.25F;
         Vector3f dir = new Vector3f(back).mul(Mth.cos(ang)).add(new Vector3f(p.right).mul(Mth.sin(ang))).add(new Vector3f(up).mul(-0.12F));
         float len = (4.4F + 1.4F * (1.0F - Math.abs(2.0F * k - 1.0F))) * sc * flicker(b.t, j + 50, 0, 0.06F) * grow;
         Sculpt.feather(m, vc, root, dir, up, back, len, 0.95F * sc * grow, 0.06F, 0.05F, 0.002F * (float)j * sc, r, g, bl, a, 0.3F);
      });
   }

   private static Vector3f waver(Vector3f dir, Vector3f n, float t, int j, int w) {
      float a = 0.16F * Mth.sin(t * 0.55F + (float)j * 1.37F + (float)w * 2.1F) + 0.08F * Mth.sin(t * 1.3F + (float)j * 0.61F);
      return new Vector3f(dir).normalize().add(new Vector3f(n).mul(a));
   }

   private static void rank(int count, PhoenixSculpt.Rank rank) {
      for (int j = 0; j < count; j++) {
         rank.each(j, count == 1 ? 0.0F : (float)j / ((float)count - 1.0F));
      }
   }

   public static void plumes(Matrix4f m, VertexConsumer vc, PhoenixSculpt.Bird b) {
      PhoenixPose p = b.p;
      float sc = p.scale;

      for (int i = -1; i <= 1; i++) {
         Vector3f start = b.at(2.35F, 1.85F, 0.18F * (float)i);
         Vector3f dir = new Vector3f(p.fwd).mul(-0.72F).add(new Vector3f(p.up).mul(0.72F)).add(new Vector3f(p.right).mul(0.28F * (float)i)).normalize();
         float len = (i == 0 ? 4.6F : 3.6F) * sc * flicker(b.t, 60 + i, 0, 0.06F);
         int n = 7;
         Vector3f[] pts = new Vector3f[n];
         float[] half = new float[n];
         Vector3f at = new Vector3f(start);
         Vector3f h = new Vector3f(dir);

         for (int k = 0; k < n; k++) {
            pts[k] = new Vector3f(at);
            float u = (float)k / ((float)n - 1.0F);
            half[k] = (0.16F + 0.34F * (float)Math.sin(Math.PI * (double)Math.min(1.0F, u * 1.1F)) + (u > 0.75F ? 0.18F : 0.0F)) * sc;
            h.add(new Vector3f(p.fwd).mul(-0.08F)).add(new Vector3f(p.up).mul(-0.03F)).normalize();
            at.add(new Vector3f(h).mul(len / (float)(n - 1)));
         }

         Sculpt.ribbon(m, vc, pts, half, 1.0F, 1.0F, 1.0F, b.alpha, b.alpha);
      }

      if (!b.ghost && b.phoenix != null) {
         Vector3f s = b.phoenix.start();
         float e = b.phoenix.entry();
         float turn = b.phoenix.turn();
         int steps = 20;
         float span = 9.0F;
         float[] lanes = new float[]{-0.7F, 0.0F, 0.7F};

         for (int lane = 0; lane < lanes.length; lane++) {
            float o = lanes[lane];
            Vector3f[] pts = new Vector3f[steps + 1];
            float[] half = new float[steps + 1];
            Vector3f right = new Vector3f(p.right);

            for (int k = 0; k <= steps; k++) {
               float tk = Math.max(0.0F, b.t - span * (float)k / (float)steps);
               Vector3f here = k == 0 ? new Vector3f(p.at) : PhoenixEntity.at(tk, s, e, turn);
               Vector3f d = PhoenixEntity.at(tk + 0.5F, s, e, turn).sub(PhoenixEntity.at(Math.max(0.0F, tk - 0.5F), s, e, turn));
               if (d.lengthSquared() < 1.0E-6F) {
                  d.set(p.fwd);
               }

               d.normalize();
               right.sub(new Vector3f(d).mul(d.dot(right)));
               if (right.lengthSquared() < 1.0E-6F) {
                  right.set(p.right);
               }

               right.normalize();
               float u = (float)k / (float)steps;
               Vector3f q = new Vector3f(here)
                  .add(new Vector3f(d).mul(-2.6F * sc))
                  .add(new Vector3f(right).mul(o * sc * (0.4F + 2.2F * u) + 0.5F * sc * u * Mth.sin(b.t * 0.33F - (float)k * 0.45F + (float)lane * 1.3F)))
                  .add(0.0F, -0.35F * sc * u * u, 0.0F);
               pts[k] = q.add(b.base);
               half[k] = (0.26F + 0.2F * u) * sc * (lane == 1 ? 1.15F : 1.0F);
            }

            Sculpt.ribbon(m, vc, pts, half, 1.0F, 1.0F, 1.0F, b.alpha, b.alpha);
         }
      }
   }

   public static void rims(Matrix4f m, VertexConsumer vc, PhoenixSculpt.Bird b, float dim) {
      Sculpt.rim(m, vc, spine(b, SPINE, 3, 1.0F), 18, 1.02F, 1.0F, 0.72F, 0.25F, 0.45F * dim * b.alpha, 3.2F);
   }

   public static void glints(Matrix4f m, VertexConsumer vc, PhoenixSculpt.Bird b, float dim) {
      float sc = b.p.scale;

      for (int s = -1; s <= 1; s += 2) {
         Vector3f c = b.at(2.78F, 1.64F, 0.4F * (float)s);
         Vector3f f = new Vector3f(b.p.fwd).mul(0.16F * sc);
         Vector3f u = new Vector3f(b.p.up).mul(0.07F * sc);
         Vector3f a = new Vector3f(c).sub(f);
         Vector3f bb = new Vector3f(c).add(u);
         Vector3f cc = new Vector3f(c).add(f).add(new Vector3f(u).mul(0.3F));
         Vector3f d = new Vector3f(c).sub(u);
         float al = b.alpha * dim;
         Sculpt.vertex(m, vc, a, 1.0F, 0.95F, 0.7F, al, 0.5F, 0.5F);
         Sculpt.vertex(m, vc, bb, 1.0F, 0.95F, 0.7F, al, 0.5F, 0.5F);
         Sculpt.vertex(m, vc, cc, 1.0F, 1.0F, 0.9F, al, 0.5F, 0.5F);
         Sculpt.vertex(m, vc, d, 1.0F, 0.95F, 0.7F, al, 0.5F, 0.5F);
      }
   }

   public static void fire(Matrix4f m, VertexConsumer vc, PhoenixSculpt.Bird b, boolean hot, float dim) {
      PhoenixPose p = b.p;
      float sc = p.scale;
      Vector3f back = new Vector3f(p.fwd).mul(-1.0F);
      Vector3f stream = new Vector3f(back).mul(0.85F).add(new Vector3f(p.up).mul(0.45F)).normalize();
      float k = hot ? 0.6F : 1.0F;
      float al = (hot ? 0.75F * dim : 0.9F) * b.alpha;
      int seed = 0;

      for (int w = 0; w < 2; w++) {
         for (int j = 0; j < 14; j++) {
            float u = (float)j / 13.0F;
            Vector3f root;
            if (u < 0.5F) {
               root = b.along(w, 0.07F + 0.43F * (u / 0.5F)).add(new Vector3f(back).mul(3.0F * sc));
            } else {
               float kk = (u - 0.5F) / 0.5F;
               Vector3f dir = new Vector3f(back).lerp(p.hand[w], 0.18F + 0.82F * (float)Math.pow((double)kk, 1.4)).normalize();
               root = b.along(w, 0.5F + 0.48F * kk).add(dir.mul((3.2F + 2.4F * kk) * sc));
            }

            flame(m, vc, root, stream, 2.4F * sc * k, 1.5F * sc * k, b.t, seed++, al);
         }
      }

      for (int w = 0; w < 2; w++) {
         for (int j = 0; j < 12; j++) {
            float kk = (float)j / 11.0F;

            for (int d = 1; d <= 2; d++) {
               Vector3f root = b.along(w, 0.05F + 0.9F * kk).add(new Vector3f(back).mul(1.1F * (float)d * sc));
               flame(m, vc, root, stream, 2.2F * sc * k, 1.9F * sc * k, b.t, seed++, al * 0.8F);
            }
         }
      }

      for (int j = 0; j < 7; j++) {
         float f = 1.0F - (float)j * 0.55F;
         flame(m, vc, b.at(f, 0.7F, 0.0F), stream, 2.0F * sc * k, 1.6F * sc * k, b.t, seed++, al);
      }

      for (int j = 0; j < 9; j++) {
         float ang = ((float)j / 8.0F - 0.5F) * 1.25F;
         Vector3f dir = new Vector3f(back).mul(Mth.cos(ang)).add(new Vector3f(p.right).mul(Mth.sin(ang)));
         Vector3f root = b.at(-2.4F, 0.1F, 0.0F).add(dir.mul(5.2F * sc));
         flame(m, vc, root, stream, 2.6F * sc * k, 1.6F * sc * k, b.t, seed++, al);
      }
   }

   private static void flame(Matrix4f m, VertexConsumer vc, Vector3f root, Vector3f up, float h, float w, float t, int seed, float a) {
      float breathe = 0.8F + 0.35F * Mth.sin(t * 0.7F + (float)seed * 2.1F);
      int frame = (int)(t * 0.45F + (float)seed * 1.7F);
      Vector3f u = new Vector3f(up).add(0.15F * Mth.sin(t * 0.3F + (float)seed), 0.0F, 0.15F * Mth.cos(t * 0.27F + (float)seed)).normalize();
      Sculpt.flame(m, vc, root, u, h * breathe, w, frame, 1.0F, 1.0F, 1.0F, a);
   }

   public static record Bird(PhoenixPose p, Vector3f base, float t, float alpha, PhoenixEntity phoenix, boolean ghost) {
      Vector3f at(float f, float u, float s) {
         float sc = this.p.scale;
         return new Vector3f(this.p.at)
            .add(this.base)
            .add(new Vector3f(this.p.fwd).mul(f * sc))
            .add(new Vector3f(this.p.up).mul(u * sc))
            .add(new Vector3f(this.p.right).mul(s * sc));
      }

      Vector3f along(int w, float k) {
         return this.p.along(w, k).add(this.base);
      }
   }

   private interface Rank {
      void each(int var1, float var2);
   }
}
