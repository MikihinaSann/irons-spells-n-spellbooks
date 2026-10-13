package com.grandsigil.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class Sculpt {
   private static float WOBBLE;
   private static float TIME;
   private static final Vector3f KEY = new Vector3f(0.35F, 1.0F, 0.25F).normalize();

   private Sculpt() {
   }

   public static float shade(Vector3f n, float emissive) {
      float key = Math.max(0.0F, n.dot(KEY));
      float fill = Math.max(0.0F, -n.y) * 0.18F;
      return Math.min(1.15F, emissive + (1.0F - emissive) * (0.42F + 0.62F * key + fill));
   }

   public static float shade2(Vector3f n, float emissive) {
      float key = Math.abs(n.dot(KEY));
      return Math.min(1.15F, emissive + (1.0F - emissive) * (0.5F + 0.55F * key));
   }

   public static void loft(Matrix4f m, VertexConsumer vc, Sculpt.Ring[] rings, int sides, float uWrap, float r, float g, float b, float emissive) {
      for (int i = 0; i + 1 < rings.length; i++) {
         Sculpt.Ring p = rings[i];
         Sculpt.Ring q = rings[i + 1];

         for (int s = 0; s < sides; s++) {
            float t0 = (float) (Math.PI * 2) * (float)s / (float)sides;
            float t1 = (float) (Math.PI * 2) * (float)(s + 1) / (float)sides;
            float u0 = uWrap * (float)s / (float)sides;
            float u1 = uWrap * (float)(s + 1) / (float)sides;
            corner(m, vc, p, t0, u0, r, g, b, emissive);
            corner(m, vc, q, t0, u0, r, g, b, emissive);
            corner(m, vc, q, t1, u1, r, g, b, emissive);
            corner(m, vc, p, t1, u1, r, g, b, emissive);
         }
      }
   }

   private static void corner(Matrix4f m, VertexConsumer vc, Sculpt.Ring ring, float t, float u, float r, float g, float b, float emissive) {
      float c = Mth.cos(t);
      float s = Mth.sin(t);
      Vector3f p = new Vector3f(ring.centre).add(new Vector3f(ring.side).mul(ring.a * c)).add(new Vector3f(ring.up).mul(ring.b * s));
      Vector3f n = new Vector3f(ring.side).mul(c / Math.max(0.001F, ring.a)).add(new Vector3f(ring.up).mul(s / Math.max(0.001F, ring.b)));
      if (n.lengthSquared() < 1.0E-8F) {
         n.set(ring.up);
      }

      n.normalize();
      float k = shade(n, emissive);
      vertex(m, vc, p, r * k, g * k, b * k, ring.alpha, u, ring.v);
   }

   public static void energy(
      Matrix4f m,
      VertexConsumer vc,
      Sculpt.Ring[] rings,
      int sides,
      float uWrap,
      float vScroll,
      float swell,
      float r,
      float g,
      float b,
      float aFacing,
      float aEdge,
      float power
   ) {
      energy(m, vc, rings, sides, uWrap, vScroll, swell, r, g, b, aFacing, aEdge, power, 0.0F, 0.0F);
   }

   public static void energy(
      Matrix4f m,
      VertexConsumer vc,
      Sculpt.Ring[] rings,
      int sides,
      float uWrap,
      float vScroll,
      float swell,
      float r,
      float g,
      float b,
      float aFacing,
      float aEdge,
      float power,
      float wobble,
      float time
   ) {
      WOBBLE = wobble;
      TIME = time;

      for (int i = 0; i + 1 < rings.length; i++) {
         Sculpt.Ring p = rings[i];
         Sculpt.Ring q = rings[i + 1];

         for (int s = 0; s < sides; s++) {
            float t0 = (float) (Math.PI * 2) * (float)s / (float)sides;
            float t1 = (float) (Math.PI * 2) * (float)(s + 1) / (float)sides;
            float u0 = uWrap * (float)s / (float)sides;
            float u1 = uWrap * (float)(s + 1) / (float)sides;
            energyCorner(m, vc, p, t0, u0, vScroll, swell, r, g, b, aFacing, aEdge, power);
            energyCorner(m, vc, q, t0, u0, vScroll, swell, r, g, b, aFacing, aEdge, power);
            energyCorner(m, vc, q, t1, u1, vScroll, swell, r, g, b, aFacing, aEdge, power);
            energyCorner(m, vc, p, t1, u1, vScroll, swell, r, g, b, aFacing, aEdge, power);
         }
      }
   }

   private static void energyCorner(
      Matrix4f m,
      VertexConsumer vc,
      Sculpt.Ring ring,
      float t,
      float u,
      float vScroll,
      float swell,
      float r,
      float g,
      float b,
      float aFacing,
      float aEdge,
      float power
   ) {
      float c = Mth.cos(t);
      float s = Mth.sin(t);
      float heave = 1.0F;
      if (WOBBLE > 0.0F) {
         heave += WOBBLE * (0.6F * Mth.sin(t * 3.0F + ring.v * 4.0F + TIME * 0.25F) + 0.4F * Mth.sin(t * 5.0F - ring.v * 7.0F + TIME * 0.41F));
      }

      Vector3f p = new Vector3f(ring.centre)
         .add(new Vector3f(ring.side).mul(ring.a * swell * heave * c))
         .add(new Vector3f(ring.up).mul(ring.b * swell * heave * s));
      Vector3f n = new Vector3f(ring.side).mul(c).add(new Vector3f(ring.up).mul(s)).normalize();
      Vector3f toEye = new Vector3f(p).negate();
      if (toEye.lengthSquared() > 1.0E-6F) {
         toEye.normalize();
      }

      float edge = (float)Math.pow((double)(1.0F - Math.abs(n.dot(toEye))), (double)power);
      float a = Mth.lerp(edge, aFacing, aEdge) * ring.alpha;
      vertex(m, vc, p, r, g, b, a, u, ring.v + vScroll);
   }

   public static void rim(Matrix4f m, VertexConsumer vc, Sculpt.Ring[] rings, int sides, float swell, float r, float g, float b, float strength, float power) {
      for (int i = 0; i + 1 < rings.length; i++) {
         Sculpt.Ring p = rings[i];
         Sculpt.Ring q = rings[i + 1];

         for (int s = 0; s < sides; s++) {
            float t0 = (float) (Math.PI * 2) * (float)s / (float)sides;
            float t1 = (float) (Math.PI * 2) * (float)(s + 1) / (float)sides;
            rimCorner(m, vc, p, t0, swell, r, g, b, strength, power);
            rimCorner(m, vc, q, t0, swell, r, g, b, strength, power);
            rimCorner(m, vc, q, t1, swell, r, g, b, strength, power);
            rimCorner(m, vc, p, t1, swell, r, g, b, strength, power);
         }
      }
   }

   private static void rimCorner(Matrix4f m, VertexConsumer vc, Sculpt.Ring ring, float t, float swell, float r, float g, float b, float strength, float power) {
      float c = Mth.cos(t);
      float s = Mth.sin(t);
      Vector3f p = new Vector3f(ring.centre).add(new Vector3f(ring.side).mul(ring.a * swell * c)).add(new Vector3f(ring.up).mul(ring.b * swell * s));
      Vector3f n = new Vector3f(ring.side).mul(c).add(new Vector3f(ring.up).mul(s)).normalize();
      Vector3f toEye = new Vector3f(p).negate();
      if (toEye.lengthSquared() > 1.0E-6F) {
         toEye.normalize();
      }

      float edge = (float)Math.pow((double)(1.0F - Math.abs(n.dot(toEye))), (double)power);
      vertex(m, vc, p, r, g, b, strength * edge * ring.alpha, 0.5F, 0.5F);
   }

   public static void feather(
      Matrix4f m,
      VertexConsumer vc,
      Vector3f root,
      Vector3f dir,
      Vector3f normal,
      Vector3f curlDir,
      float length,
      float width,
      float curl,
      float droop,
      float lift,
      float r,
      float g,
      float b,
      float alpha,
      float emissive
   ) {
      Vector3f d = new Vector3f(dir);
      if (!(d.lengthSquared() < 1.0E-8F)) {
         d.normalize();
         Vector3f n = new Vector3f(normal).sub(new Vector3f(d).mul(d.dot(normal)));
         if (!(n.lengthSquared() < 1.0E-8F)) {
            n.normalize();
            Vector3f side = new Vector3f(d).cross(n).normalize();
            float k = shade2(n, emissive);
            float rr = r * k;
            float gg = g * k;
            float bb = b * k;
            Vector3f base = new Vector3f(root).add(new Vector3f(n).mul(lift));
            Vector3f prevL = null;
            Vector3f prevR = null;
            float prevV = 0.0F;
            int segs = 3;
            Vector3f at = new Vector3f(base);
            Vector3f heading = new Vector3f(d);

            for (int i = 0; i <= segs; i++) {
               float v = (float)i / (float)segs;
               if (i > 0) {
                  heading.add(new Vector3f(curlDir).mul(curl * v)).add(new Vector3f(n).mul(-droop * v)).normalize();
                  at.add(new Vector3f(heading).mul(length / (float)segs));
               }

               Vector3f half = new Vector3f(side).mul(width * 0.5F);
               Vector3f l = new Vector3f(at).sub(half);
               Vector3f rt = new Vector3f(at).add(half);
               if (prevL != null) {
                  vertex(m, vc, prevL, rr, gg, bb, alpha, 0.0F, 1.0F - prevV);
                  vertex(m, vc, prevR, rr, gg, bb, alpha, 1.0F, 1.0F - prevV);
                  vertex(m, vc, rt, rr, gg, bb, alpha, 1.0F, 1.0F - v);
                  vertex(m, vc, l, rr, gg, bb, alpha, 0.0F, 1.0F - v);
               }

               prevL = l;
               prevR = rt;
               prevV = v;
            }
         }
      }
   }

   public static void ribbon(Matrix4f m, VertexConsumer vc, Vector3f[] pts, float[] half, float r, float g, float b, float a0, float a1) {
      int n = pts.length;
      if (n >= 2) {
         Vector3f[] side = new Vector3f[n];

         for (int k = 0; k < n; k++) {
            Vector3f run = new Vector3f(pts[Math.min(n - 1, k + 1)]).sub(pts[Math.max(0, k - 1)]);
            Vector3f s = run.cross(new Vector3f(pts[k]).negate());
            if (s.lengthSquared() < 1.0E-8F) {
               s.set(0.0F, 1.0F, 0.0F);
            }

            side[k] = s.normalize().mul(half[k]);
         }

         for (int k = 0; k + 1 < n; k++) {
            float v0 = (float)k / (float)(n - 1);
            float v1 = (float)(k + 1) / (float)(n - 1);
            float aa = Mth.lerp(v0, a0, a1);
            float ab = Mth.lerp(v1, a0, a1);
            vertex(m, vc, new Vector3f(pts[k]).sub(side[k]), r, g, b, aa, 0.0F, 1.0F - v0);
            vertex(m, vc, new Vector3f(pts[k]).add(side[k]), r, g, b, aa, 1.0F, 1.0F - v0);
            vertex(m, vc, new Vector3f(pts[k + 1]).add(side[k + 1]), r, g, b, ab, 1.0F, 1.0F - v1);
            vertex(m, vc, new Vector3f(pts[k + 1]).sub(side[k + 1]), r, g, b, ab, 0.0F, 1.0F - v1);
         }
      }
   }

   public static void flame(Matrix4f m, VertexConsumer vc, Vector3f root, Vector3f up, float height, float width, int frame, float r, float g, float b, float a) {
      if (!(a <= 0.01F) && !(height <= 0.01F)) {
         Vector3f u = new Vector3f(up).normalize();
         Vector3f toEye = new Vector3f(root).negate();
         Vector3f side = new Vector3f(u).cross(toEye);
         if (side.lengthSquared() < 1.0E-8F) {
            side.set(1.0F, 0.0F, 0.0F);
         }

         side.normalize().mul(width * 0.5F);
         Vector3f top = new Vector3f(root).add(new Vector3f(u).mul(height));
         float u0 = (float)Math.floorMod(frame, 4) * 0.25F;
         float u1 = u0 + 0.25F;
         vertex(m, vc, new Vector3f(root).sub(side), r, g, b, a, u0, 1.0F);
         vertex(m, vc, new Vector3f(root).add(side), r, g, b, a, u1, 1.0F);
         vertex(m, vc, new Vector3f(top).add(side), r, g, b, a, u1, 0.0F);
         vertex(m, vc, new Vector3f(top).sub(side), r, g, b, a, u0, 0.0F);
      }
   }

   public static void quad(
      Matrix4f m,
      VertexConsumer vc,
      Vector3f a,
      Vector3f b,
      Vector3f c,
      Vector3f d,
      float u0,
      float v0,
      float u1,
      float v1,
      float r,
      float g,
      float bl,
      float alpha,
      float emissive
   ) {
      Vector3f n = new Vector3f(b).sub(a).cross(new Vector3f(d).sub(a));
      if (n.lengthSquared() > 1.0E-10F) {
         n.normalize();
      }

      float k = shade2(n, emissive);
      vertex(m, vc, a, r * k, g * k, bl * k, alpha, u0, v1);
      vertex(m, vc, b, r * k, g * k, bl * k, alpha, u1, v1);
      vertex(m, vc, c, r * k, g * k, bl * k, alpha, u1, v0);
      vertex(m, vc, d, r * k, g * k, bl * k, alpha, u0, v0);
   }

   public static void vertex(Matrix4f m, VertexConsumer vc, Vector3f p, float r, float g, float b, float a, float u, float v) {
      vc.addVertex(m, p.x, p.y, p.z).setColor(Math.min(1.0F, r), Math.min(1.0F, g), Math.min(1.0F, b), a).setUv(u, v);
   }

   public static record Ring(Vector3f centre, Vector3f side, Vector3f up, float a, float b, float v, float alpha) {
   }
}
