package com.grandsigil.client;

import com.grandsigil.GrandSigil;
import com.grandsigil.GsPresence;
import com.grandsigil.config.GsConfig;
import com.grandsigil.entity.ThunderDragonEntity;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class DragonRenderer {
   private static final ResourceLocation FLARE = GrandSigil.id("textures/entity/flare.png");
   private static final ResourceLocation PLASMA = GrandSigil.id("textures/entity/plasma_body.png");
   private static final ResourceLocation CLOUD = GrandSigil.id("textures/entity/storm_cloud.png");
   private static final float[] CYAN = new float[]{0.55F, 0.85F, 1.0F};
   private static final float[] WHITE_BLUE = new float[]{0.88F, 0.96F, 1.0F};
   private static final float[] VIOLET = new float[]{0.7F, 0.55F, 1.0F};
   private static final float SIZE = 2.6F;
   private static final float SMALL = 1.1F;
   private static final float STEP = 1.25F;
   private static final double VIEW = 400.0;
   private static final float FALL_TICKS = 6.0F;
   private static final float STRIKE_LIFE = 16.0F;
   private static final ByteBufferBuilder BUILDER = new ByteBufferBuilder(1048576);
   private static final BufferSource BUFFERS = MultiBufferSource.immediate(BUILDER);
   private static final Map<ThunderDragonEntity, DragonRenderer.Memory> MEMORY = new WeakHashMap<>();
   private static final float[][] SKULL = new float[][]{
      {-1.6F, 0.0F, 1.25F, 1.25F},
      {-0.6F, 0.15F, 1.75F, 1.6F},
      {0.4F, 0.3F, 1.75F, 1.45F},
      {1.4F, 0.2F, 1.45F, 1.05F},
      {2.4F, 0.05F, 1.15F, 0.8F},
      {3.3F, -0.05F, 0.95F, 0.62F},
      {4.0F, -0.1F, 0.78F, 0.52F},
      {4.6F, -0.18F, 0.2F, 0.15F}
   };

   private DragonRenderer() {
   }

   public static void render(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_TRANSLUCENT_BLOCKS && GsPresence.any(15)) {
         Minecraft minecraft = Minecraft.getInstance();
         ClientLevel level = minecraft.level;
         if (level != null) {
            Camera camera = event.getCamera();
            Vec3 eye = camera.getPosition();
            float partial = event.getPartialTick();
            int detail = (Integer)GsConfig.CLIENT.detail.get();
            List<DragonRenderer.View> views = new ArrayList<>();

            for (Entity entity : level.entitiesForRendering()) {
               if (entity instanceof ThunderDragonEntity) {
                  ThunderDragonEntity dragon = (ThunderDragonEntity)entity;
                  if (dragon.distanceToSqr(eye.x, eye.y, eye.z) < 160000.0) {
                     views.add(new DragonRenderer.View(dragon, partial, eye));
                  }
               }
            }

            if (!views.isEmpty()) {
               PoseStack pose = event.getPoseStack();
               pose.pushPose();
               Matrix4f m = pose.last().pose();
               boolean pack = Shaders.packInUse();
               float dim = pack ? 0.65F : 1.0F;
               batch(GsRenderTypes.matter(CLOUD), vc -> views.forEach(v -> storm(m, vc, v, pack)));
               batch(GsRenderTypes.matter(PLASMA), vc -> views.forEach(v -> {
                     if (v.body != null) {
                        haze(m, vc, v.body, v.time, 1.0F);
                     }

                     for (DragonRenderer.Falling f : v.falling) {
                        haze(m, vc, f.body, v.time, f.alpha);
                     }
                  }));
               batch(
                  GsRenderTypes.glow(),
                  vc -> views.forEach(
                        v -> {
                           eyeOfStorm(m, vc, v, detail, dim);
                           if (v.body != null) {
                              DragonRenderer.Shape main = new DragonRenderer.Shape(
                                 v.body, v.headOut, 2.6F, v.flight > 250.0F ? 0.7F : 0.2F, (long)v.dragon.getId() * 7919L, v.time, detail, dim
                              );
                              lightning(m, vc, main);
                           }

                           for (DragonRenderer.Falling f : v.falling) {
                              lightning(m, vc, new DragonRenderer.Shape(f.body, f.headOut, 1.1F, 0.5F, (long)f.mark.n * 104729L, v.time, detail, dim * f.alpha));
                              landing(m, vc, v, f, dim);
                           }

                           meteor(m, vc, v, dim);
                           impact(m, vc, v, dim);
                           pour(m, vc, v, dim);
                        }
                     )
               );
               if (!pack) {
                  Vector3f right = new Vector3f(camera.getLeftVector()).mul(-1.0F);
                  Vector3f up = new Vector3f(camera.getUpVector());
                  batch(GsRenderTypes.glow(FLARE), vc -> views.forEach(v -> halos(m, vc, v, right, up)));
               }

               pose.popPose();
               BUFFERS.endBatch();
            }
         }
      }
   }

   private static void batch(RenderType type, DragonRenderer.Pass pass) {
      VertexConsumer vc = BUFFERS.getBuffer(type);
      pass.draw(vc);
      BUFFERS.endBatch(type);
   }

   private static DragonRenderer.Station[] lay(List<Vector3f> pts, float size, float length) {
      if (pts.size() < 3) {
         return null;
      } else {
         DragonRenderer.Station[] out = new DragonRenderer.Station[pts.size()];
         Vector3f carried = new Vector3f(0.0F, 1.0F, 0.0F);
         float s = 0.0F;

         for (int i = 0; i < pts.size(); i++) {
            Vector3f p = pts.get(i);
            if (i > 0) {
               s += p.distance((Vector3fc)pts.get(i - 1));
            }

            Vector3f fwd = i == 0 ? new Vector3f((Vector3fc)pts.get(0)).sub((Vector3fc)pts.get(1)) : new Vector3f((Vector3fc)pts.get(i - 1)).sub(p);
            if (fwd.lengthSquared() < 1.0E-8F) {
               fwd.set(0.0F, -1.0F, 0.0F);
            }

            fwd.normalize();
            Vector3f up = new Vector3f(carried).add(0.0F, 0.12F, 0.0F);
            up.sub(new Vector3f(fwd).mul(fwd.dot(up)));
            if (up.lengthSquared() < 1.0E-6F) {
               up = new Vector3f(1.0F, 0.0F, 0.0F).sub(new Vector3f(fwd).mul(fwd.x));
            }

            up.normalize();
            carried = up;
            Vector3f side = new Vector3f(fwd).cross(up).normalize();
            float u = Math.min(1.0F, s / Math.max(1.0F, length));
            out[i] = new DragonRenderer.Station(new Vector3f(p), fwd, up, side, s, u, size * shape(s / size, u));
         }

         return out;
      }
   }

   private static float shape(float s, float u) {
      if (s < 2.5F) {
         return 1.25F;
      } else {
         float neck = 1.15F + 0.15F * Mth.clamp((s - 2.5F) / 6.0F, 0.0F, 1.0F);
         float body = 2.0F * (float)Math.pow((double)Math.max(0.0F, 1.0F - u), 0.7) + 0.12F;
         float k = Mth.clamp((s - 6.0F) / 14.0F, 0.0F, 1.0F);
         return Mth.lerp(k, neck, Math.min(body, 2.0F));
      }
   }

   private static void haze(Matrix4f m, VertexConsumer vc, DragonRenderer.Station[] b, float time, float alpha) {
      Sculpt.Ring[] rings = new Sculpt.Ring[b.length];

      for (int i = 0; i < b.length; i++) {
         DragonRenderer.Station st = b[i];
         rings[i] = new Sculpt.Ring(st.at, st.up, st.side, st.r * 0.9F, st.r, st.s / 20.0F, alpha);
      }

      Sculpt.energy(m, vc, rings, 14, 1.0F, -time * 0.02F, 1.15F, 0.5F, 0.6F, 1.0F, 0.16F, 0.0F, 0.8F, 0.25F, time);
   }

   private static void lightning(Matrix4f m, VertexConsumer vc, DragonRenderer.Shape sh) {
      if (!(sh.dim <= 0.01F)) {
         spine(m, vc, sh);
         outline(m, vc, sh);
         forks(m, vc, sh);
         legs(m, vc, sh);
         if (sh.headOut) {
            head(m, vc, sh);
         }
      }
   }

   private static void spine(Matrix4f m, VertexConsumer vc, DragonRenderer.Shape sh) {
      DragonRenderer.Station[] b = sh.b;
      int chunk = 6;

      for (int strand = 0; strand < 3; strand++) {
         RandomSource random = sh.random(10 + strand);

         for (int i = 0; i + 1 < b.length; i += chunk) {
            DragonRenderer.Station a = b[i];
            DragonRenderer.Station z = b[Math.min(b.length - 1, i + chunk)];
            float ta = (float)strand * (float) (Math.PI * 2) / 3.0F + sh.time * 0.2F + a.s * 0.05F;
            float tz = (float)strand * (float) (Math.PI * 2) / 3.0F + sh.time * 0.2F + z.s * 0.05F;
            float w = sh.size * 0.12F * (1.2F - a.u);
            bolt(m, vc, a.round(ta, 0.28F), z.round(tz, 0.28F), random, 3, 0.16F, w, sh.dim, 0, WHITE_BLUE);
         }
      }
   }

   private static void outline(Matrix4f m, VertexConsumer vc, DragonRenderer.Shape sh) {
      DragonRenderer.Station[] b = sh.b;
      int chunk = 4;
      float[] lines = new float[]{0.0F, (float) Math.PI, (float) (Math.PI / 2), (float) (-Math.PI / 2)};

      for (int l = 0; l < lines.length; l++) {
         RandomSource random = sh.random(20 + l);

         for (int i = 0; i + 1 < b.length; i += chunk) {
            DragonRenderer.Station a = b[i];
            DragonRenderer.Station z = b[Math.min(b.length - 1, i + chunk)];
            float w = sh.size * 0.055F;
            bolt(m, vc, a.round(lines[l], 0.95F), z.round(lines[l], 0.95F), random, 2, 0.3F, w, sh.dim * 0.85F, 0, l == 0 ? WHITE_BLUE : CYAN);
         }
      }

      RandomSource random = sh.random(30);
      int every = sh.detail >= 2 ? 5 : 8;

      for (int i = 3; i < b.length; i += every) {
         DragonRenderer.Station st = b[i];
         int n = 7;
         float spin = sh.time * 0.3F + (float)i;
         Vector3f prev = st.round(spin, 0.98F);

         for (int k = 1; k <= n; k++) {
            Vector3f next = st.round(spin + (float) (Math.PI * 2) * (float)k / (float)n, 0.92F + 0.12F * random.nextFloat());
            bolt(m, vc, prev, next, random, 1, 0.35F, sh.size * 0.04F, sh.dim * 0.7F, 0, CYAN);
            prev = next;
         }
      }
   }

   private static void forks(Matrix4f m, VertexConsumer vc, DragonRenderer.Shape sh) {
      DragonRenderer.Station[] b = sh.b;
      RandomSource random = sh.random(40);
      int count = (sh.detail >= 2 ? 28 : 16) * b.length / 120 + 4;

      for (int i = 0; i < count; i++) {
         DragonRenderer.Station st = b[random.nextInt(b.length)];
         float t = random.nextFloat() * (float) (Math.PI * 2);
         Vector3f from = st.round(t, 1.0F);
         Vector3f out = new Vector3f(from).sub(st.at).normalize();
         Vector3f to = new Vector3f(from).add(out.mul(st.r * (0.8F + 1.6F * random.nextFloat()))).add(new Vector3f(st.fwd).mul(-st.r * random.nextFloat()));
         bolt(m, vc, from, to, random, 3, 0.35F, sh.size * 0.04F, sh.dim * 0.8F, 1, random.nextInt(4) == 0 ? VIOLET : WHITE_BLUE);
      }
   }

   private static void legs(Matrix4f m, VertexConsumer vc, DragonRenderer.Shape sh) {
      DragonRenderer.Station[] b = sh.b;
      RandomSource random = sh.random(50);
      float k = sh.size;

      for (float at : new float[]{0.18F, 0.5F}) {
         DragonRenderer.Station st = b[Math.min(b.length - 1, Math.round(at * (float)(b.length - 1)))];
         if (!(st.u > 0.85F) && b.length >= 12) {
            for (int s = -1; s <= 1; s += 2) {
               float swing = 0.4F * Mth.sin(sh.time * 0.12F + at * 9.0F + (s > 0 ? 0.0F : (float) Math.PI));
               Vector3f hip = st.off(0.0F, -st.r * 0.25F, (float)s * st.r * 0.8F);
               Vector3f knee = new Vector3f(hip)
                  .add(new Vector3f(st.side).mul((float)s * 2.2F * k))
                  .add(new Vector3f(st.up).mul(-1.4F * k))
                  .add(new Vector3f(st.fwd).mul((swing * 2.2F - 0.6F) * k));
               Vector3f ankle = new Vector3f(knee).add(new Vector3f(st.up).mul(-2.0F * k)).add(new Vector3f(st.fwd).mul((1.0F + swing) * k));
               Vector3f foot = new Vector3f(ankle).add(new Vector3f(st.fwd).mul(0.9F * k)).add(new Vector3f(st.up).mul(-0.4F * k));
               bolt(m, vc, hip, knee, random, 3, 0.2F, 0.09F * k, sh.dim, 0, WHITE_BLUE);
               bolt(m, vc, knee, ankle, random, 3, 0.2F, 0.08F * k, sh.dim, 0, WHITE_BLUE);
               bolt(m, vc, ankle, foot, random, 2, 0.2F, 0.07F * k, sh.dim, 0, WHITE_BLUE);

               for (int c = -1; c <= 1; c++) {
                  Vector3f claw = new Vector3f(foot)
                     .add(new Vector3f(st.fwd).mul(1.2F * k))
                     .add(new Vector3f(st.side).mul((float)c * 0.6F * k))
                     .add(new Vector3f(st.up).mul(-0.7F * k));
                  bolt(m, vc, foot, claw, random, 2, 0.3F, 0.05F * k, sh.dim, 0, CYAN);
               }
            }
         }
      }
   }

   private static void head(Matrix4f m, VertexConsumer vc, DragonRenderer.Shape sh) {
      DragonRenderer.Station h = sh.b[0];
      float H = sh.size * 1.35F;
      Vector3f F = h.fwd;
      Vector3f U = h.up;
      Vector3f S = h.side;
      Vector3f K = new Vector3f(h.at).add(new Vector3f(F).mul(-0.5F * H));
      RandomSource random = sh.random(60);
      float open = 0.25F + sh.open + 0.06F * Mth.sin(sh.time * 0.19F);
      Vector3f jawF = new Vector3f(F).mul(Mth.cos(open)).sub(new Vector3f(U).mul(Mth.sin(open))).normalize();
      Vector3f hinge = new Vector3f(K).add(new Vector3f(F).mul(-0.2F * H)).add(new Vector3f(U).mul(-0.75F * H));

      for (int i = 1; i < SKULL.length - 1; i += 2) {
         float[] q = SKULL[i];
         Vector3f c = new Vector3f(K).add(new Vector3f(F).mul(q[0] * H)).add(new Vector3f(U).mul(q[1] * H));
         int n = 7;
         Vector3f prev = null;

         for (int k = 0; k <= n; k++) {
            float t = (float) (Math.PI * 2) * (float)k / (float)n;
            Vector3f p = new Vector3f(c).add(new Vector3f(U).mul(Mth.cos(t) * q[3] * H)).add(new Vector3f(S).mul(Mth.sin(t) * q[2] * H));
            if (prev != null) {
               bolt(m, vc, prev, p, random, 1, 0.3F, 0.05F * H, sh.dim * 0.8F, 0, CYAN);
            }

            prev = p;
         }
      }

      float[][] lines = new float[][]{{0.0F, 1.0F, 0.0F}, {0.7F, 0.2F, 1.0F}, {0.7F, 0.2F, -1.0F}};

      for (float[] line : lines) {
         Vector3f prev = null;

         for (float[] q : SKULL) {
            Vector3f p = new Vector3f(K)
               .add(new Vector3f(F).mul(q[0] * H))
               .add(new Vector3f(U).mul((q[1] + line[1] * q[3]) * H))
               .add(new Vector3f(S).mul(line[2] * q[2] * line[0] * H));
            if (prev != null) {
               bolt(m, vc, prev, p, random, 2, 0.25F, 0.07F * H, sh.dim, 0, WHITE_BLUE);
            }

            prev = p;
         }
      }

      for (int s = -1; s <= 1; s += 2) {
         Vector3f a = new Vector3f(hinge).add(new Vector3f(S).mul((float)s * 1.1F * H));
         Vector3f b = new Vector3f(hinge).add(new Vector3f(jawF).mul(2.6F * H)).add(new Vector3f(S).mul((float)s * 0.75F * H));
         Vector3f c = new Vector3f(hinge).add(new Vector3f(jawF).mul(4.0F * H));
         bolt(m, vc, a, b, random, 2, 0.25F, 0.07F * H, sh.dim, 0, WHITE_BLUE);
         bolt(m, vc, b, c, random, 2, 0.25F, 0.06F * H, sh.dim, 0, WHITE_BLUE);
      }

      for (int s = -1; s <= 1; s += 2) {
         Vector3f p = new Vector3f(K).add(new Vector3f(U).mul(1.2F * H)).add(new Vector3f(S).mul((float)s * 0.75F * H)).add(new Vector3f(F).mul(-0.4F * H));
         Vector3f dir = new Vector3f(F).mul(-0.85F).add(new Vector3f(U).mul(0.5F)).add(new Vector3f(S).mul((float)s * 0.25F)).normalize();

         for (int seg = 0; seg < 4; seg++) {
            Vector3f q = new Vector3f(p).add(new Vector3f(dir).mul(1.4F * H));
            bolt(m, vc, p, q, random, 2, 0.2F, 0.08F * H * (1.0F - (float)seg * 0.2F), sh.dim, 0, WHITE_BLUE);
            if (seg == 1) {
               bolt(
                  m, vc, p, new Vector3f(p).add(new Vector3f(U).mul(1.6F * H)).add(new Vector3f(F).mul(0.3F * H)), random, 2, 0.25F, 0.05F * H, sh.dim, 0, CYAN
               );
            }

            dir.add(new Vector3f(U).mul(0.15F)).normalize();
            p = q;
         }
      }

      for (int s = -1; s <= 1; s += 2) {
         Vector3f p = new Vector3f(K).add(new Vector3f(F).mul(3.8F * H)).add(new Vector3f(S).mul((float)s * 0.9F * H));

         for (int seg = 0; seg < 10; seg++) {
            Vector3f q = new Vector3f(p)
               .add(new Vector3f(F).mul(-1.3F * H))
               .add(new Vector3f(S).mul((float)s * 0.4F * H))
               .add(new Vector3f(U).mul(0.8F * H * Mth.sin(sh.time * 0.3F - (float)seg * 0.6F + (float)s)));
            bolt(m, vc, p, q, random, 1, 0.3F, 0.05F * H * (1.0F - (float)seg / 11.0F), sh.dim * (1.0F - (float)seg / 12.0F), 0, CYAN);
            p = q;
         }
      }

      for (int s = -1; s <= 1; s += 2) {
         Vector3f c = new Vector3f(K).add(new Vector3f(F).mul(1.0F * H)).add(new Vector3f(U).mul(0.85F * H)).add(new Vector3f(S).mul((float)s * 1.3F * H));
         Vector3f toEye = new Vector3f(c).negate().normalize();
         Vector3f a1 = new Vector3f(toEye).cross(U).normalize();
         Vector3f a2 = new Vector3f(toEye).cross(a1).normalize();
         Geo.discIn(m, vc, c, a1, a2, 0.55F * H, 12, 1.0F, 1.0F, 1.0F, sh.dim, 0.0F);
      }
   }

   private static void landing(Matrix4f m, VertexConsumer vc, DragonRenderer.View v, DragonRenderer.Falling f, float dim) {
      float since = v.time - f.mark.time - 6.0F;
      if (!(since < 0.0F)) {
         float k = Mth.clamp(1.0F - since / 10.0F, 0.0F, 1.0F);
         Vector3f ex = new Vector3f(1.0F, 0.0F, 0.0F);
         Vector3f ez = new Vector3f(0.0F, 0.0F, 1.0F);
         Vector3f g = new Vector3f(f.ground).add(0.0F, 0.2F, 0.0F);
         float r = 3.0F + since * (1.8F + 0.12F * (float)f.mark.n);
         Geo.ringIn(m, vc, g, ex, ez, r * 0.85F, r, 64, 0.0F, 1.0F, CYAN[0], CYAN[1], CYAN[2], 0.0F, k * 0.9F * dim);
         Geo.discIn(m, vc, g, ex, ez, 6.0F + 0.5F * (float)f.mark.n, 40, WHITE_BLUE[0], WHITE_BLUE[1], WHITE_BLUE[2], k * k * dim, 0.0F);
         RandomSource random = RandomSource.create((long)f.mark.n * 271L + (long)(v.time / 2.0F));

         for (int i = 0; i < 5; i++) {
            float a = random.nextFloat() * (float) (Math.PI * 2);
            Vector3f to = new Vector3f(g).add(Mth.cos(a) * (3.0F + 6.0F * random.nextFloat()), 0.0F, Mth.sin(a) * (3.0F + 6.0F * random.nextFloat()));
            drawBolt(m, vc, new Vector3f(g).add(0.0F, 2.0F, 0.0F), to, random, 3, 0.3F, 0.12F, 0.8F * k * dim, 1);
         }
      }
   }

   private static void storm(Matrix4f m, VertexConsumer vc, DragonRenderer.View v, boolean pack) {
      float k = v.storm;
      if (!(k <= 0.01F)) {
         float R = 64.0F * (0.4F + 0.6F * k);
         float tone = pack ? 0.55F : 1.0F;
         float[][] layers = new float[][]{{-5.0F, 1.0F, 0.004F, 0.9F}, {-2.0F, 0.9F, -0.006F, 0.8F}, {1.5F, 0.78F, 0.008F, 0.75F}, {4.5F, 0.6F, -0.011F, 0.7F}};

         for (float[] layer : layers) {
            Vector3f c = new Vector3f(v.cloud).add(0.0F, layer[0], 0.0F);
            cloudDisc(m, vc, c, R * layer[1], v.time * layer[2], tone, layer[3] * k * (pack ? 1.2F : 1.0F));
         }
      }
   }

   private static void cloudDisc(Matrix4f m, VertexConsumer vc, Vector3f c, float R, float spin, float tone, float a) {
      int rings = 7;
      int segs = 40;

      for (int i = 0; i < rings; i++) {
         float r0 = R * (float)i / (float)rings;
         float r1 = R * (float)(i + 1) / (float)rings;
         float a0 = a * falloff((float)i / (float)rings);
         float a1 = a * falloff((float)(i + 1) / (float)rings);

         for (int j = 0; j < segs; j++) {
            float t0 = (float) (Math.PI * 2) * (float)j / (float)segs;
            float t1 = (float) (Math.PI * 2) * (float)(j + 1) / (float)segs;
            cloudVertex(m, vc, c, r0, t0, R, spin, tone, a0);
            cloudVertex(m, vc, c, r1, t0, R, spin, tone, a1);
            cloudVertex(m, vc, c, r1, t1, R, spin, tone, a1);
            cloudVertex(m, vc, c, r0, t1, R, spin, tone, a0);
         }
      }
   }

   private static float falloff(float t) {
      float hole = Mth.clamp((t - 0.06F) / 0.1F, 0.0F, 1.0F);
      float edge = 1.0F - (float)Math.pow((double)Math.max(0.0F, (t - 0.55F) / 0.45F), 2.0);
      return hole * edge;
   }

   private static void cloudVertex(Matrix4f m, VertexConsumer vc, Vector3f c, float r, float t, float R, float spin, float tone, float a) {
      float x = Mth.cos(t) * r;
      float z = Mth.sin(t) * r;
      float cs = Mth.cos(spin);
      float sn = Mth.sin(spin);
      float u = 0.5F + (x * cs - z * sn) / (R * 1.4F);
      float w = 0.5F + (x * sn + z * cs) / (R * 1.4F);
      Sculpt.vertex(m, vc, new Vector3f(c).add(x, -0.05F * r * r / R, z), tone, tone, tone, a, u, w);
   }

   private static void eyeOfStorm(Matrix4f m, VertexConsumer vc, DragonRenderer.View v, int detail, float dim) {
      float k = v.storm;
      if (!(k <= 0.01F)) {
         float R = 64.0F * (0.4F + 0.6F * k);
         Vector3f ex = new Vector3f(1.0F, 0.0F, 0.0F);
         Vector3f ez = new Vector3f(0.0F, 0.0F, 1.0F);
         Vector3f eyeC = new Vector3f(v.cloud).add(0.0F, -5.5F, 0.0F);
         float spin = v.time * 0.02F;
         float emerge = v.flight < 0.0F ? k : Math.max(0.4F, 1.0F - v.flight / 30.0F);
         Geo.ringIn(m, vc, eyeC, ex, ez, 7.0F, 9.0F + 3.0F * emerge, 64, spin * 3.0F, 1.0F, CYAN[0], CYAN[1], CYAN[2], 0.9F * k * dim, 0.0F);
         Geo.discIn(m, vc, eyeC, ex, ez, 7.5F, 48, WHITE_BLUE[0], WHITE_BLUE[1], WHITE_BLUE[2], 0.45F * emerge * k * dim * dim, 0.0F);

         for (int i = 0; i < 4; i++) {
            float rr = R * (0.25F + 0.17F * (float)i);
            Geo.ringIn(
               m,
               vc,
               new Vector3f(v.cloud).add(0.0F, -5.0F, 0.0F),
               ex,
               ez,
               rr,
               rr + 0.5F,
               96,
               -spin * (float)(2 + i),
               0.3F,
               CYAN[0],
               CYAN[1],
               CYAN[2],
               0.3F * k * dim,
               0.3F * k * dim
            );
         }

         RandomSource random = RandomSource.create((long)v.dragon.getId() * 7919L + (long)(v.time / 3.0F));
         int flashes = detail >= 2 ? 7 : 4;

         for (int i = 0; i < flashes; i++) {
            float a = random.nextFloat() * (float) (Math.PI * 2);
            float rr = R * (0.12F + 0.75F * random.nextFloat());
            float phase = v.time / 3.0F % 1.0F;
            float flash = (1.0F - phase) * (1.0F - phase) * (random.nextFloat() < 0.6F ? 1.0F : 0.35F);
            Vector3f at = new Vector3f(v.cloud).add(Mth.cos(a) * rr, -4.0F, Mth.sin(a) * rr);
            Geo.discIn(m, vc, at, ex, ez, R * 0.18F, 28, CYAN[0], CYAN[1], CYAN[2], 0.22F * flash * k * dim * dim, 0.0F);
            float b = a + (random.nextFloat() - 0.5F) * 1.2F;
            float rb = R * (0.12F + 0.75F * random.nextFloat());
            Vector3f to = new Vector3f(v.cloud).add(Mth.cos(b) * rb, -5.5F, Mth.sin(b) * rb);
            drawBolt(m, vc, new Vector3f(at).add(0.0F, -1.5F, 0.0F), to, random, 5, 0.25F, 0.1F, 0.8F * flash * k * dim, 1);
         }
      }
   }

   private static void pour(Matrix4f m, VertexConsumer vc, DragonRenderer.View v, float dim) {
      float since = v.flight - 275.0F;
      if (!(since < 0.0F) && !(since > (float)ThunderDragonEntity.POUR + 30.0F)) {
         float pouring = since < (float)ThunderDragonEntity.POUR ? 1.0F : 1.0F - (since - (float)ThunderDragonEntity.POUR) / 30.0F;
         Vector3f c = v.last;
         Vector3f ex = new Vector3f(1.0F, 0.0F, 0.0F);
         Vector3f ez = new Vector3f(0.0F, 0.0F, 1.0F);
         float beat = 0.6F + 0.4F * Mth.sin(since * 1.3F);
         Geo.discIn(
            m, vc, new Vector3f(c).add(0.0F, 0.3F, 0.0F), ex, ez, 14.0F * beat, 48, WHITE_BLUE[0], WHITE_BLUE[1], WHITE_BLUE[2], 0.6F * pouring * dim, 0.0F
         );

         for (int i = 0; i < 3; i++) {
            float rr = (since * 2.2F + (float)i * 12.0F) % 36.0F + 2.0F;
            float aa = (1.0F - rr / 38.0F) * pouring * dim;
            Geo.ringIn(m, vc, new Vector3f(c).add(0.0F, 0.3F, 0.0F), ex, ez, rr - 1.6F, rr, 72, 0.0F, 1.0F, CYAN[0], CYAN[1], CYAN[2], 0.0F, aa);
         }

         RandomSource random = RandomSource.create((long)v.dragon.getId() * 31L + (long)(v.time / 2.0F));

         for (int i = 0; i < 6; i++) {
            float a = random.nextFloat() * (float) (Math.PI * 2);
            Vector3f to = new Vector3f(c).add(Mth.cos(a) * (6.0F + 12.0F * random.nextFloat()), 0.2F, Mth.sin(a) * (6.0F + 12.0F * random.nextFloat()));
            drawBolt(m, vc, new Vector3f(c).add(0.0F, 4.0F, 0.0F), to, random, 4, 0.3F, 0.35F, 0.9F * pouring * dim, 1);
         }

         float end = since - (float)ThunderDragonEntity.POUR;
         if (end >= 0.0F && end < 30.0F) {
            float k = 1.0F - end / 30.0F;
            drawBolt(m, vc, new Vector3f(v.cloud).add(0.0F, -5.0F, 0.0F), c, RandomSource.create(9991L + (long)(end / 2.0F)), 7, 0.12F, 1.6F, k * dim, 3);
            float rr = 4.0F + 60.0F * (1.0F - (float)Math.exp((double)(-end / 7.0F)));
            Geo.ringIn(m, vc, new Vector3f(c).add(0.0F, 0.3F, 0.0F), ex, ez, rr - 4.0F, rr, 96, 0.0F, 1.0F, CYAN[0], CYAN[1], CYAN[2], 0.0F, k * dim);
         }
      }
   }

   private static void meteor(Matrix4f m, VertexConsumer vc, DragonRenderer.View v, float dim) {
      float u = (v.flight - 262.0F) / 13.0F;
      if (!(u < 0.0F) && !(u > 1.4F) && (v.body != null || !(u <= 1.0F))) {
         Vector3f top = v.at(261.99F);
         Vector3f head = u <= 1.0F ? v.at(v.flight) : new Vector3f(v.last);
         float k = u <= 1.0F ? u : 1.0F - (u - 1.0F) / 0.4F;
         Vector3f eye = new Vector3f();
         Geo.taper(m, vc, top, head, eye, 1.5F, 9.0F * k, CYAN[0], CYAN[1], CYAN[2], 0.05F * dim, 0.45F * k * dim);
         Geo.taper(m, vc, top, head, eye, 0.4F, 3.0F * k, WHITE_BLUE[0], WHITE_BLUE[1], WHITE_BLUE[2], 0.1F * dim, 0.8F * k * dim);
      }
   }

   private static void impact(Matrix4f m, VertexConsumer vc, DragonRenderer.View v, float dim) {
      float since = v.flight - 275.0F;
      if (!(since < 0.0F) && !(since > 70.0F)) {
         Vector3f c = v.last;
         Vector3f ex = new Vector3f(1.0F, 0.0F, 0.0F);
         Vector3f ez = new Vector3f(0.0F, 0.0F, 1.0F);
         Vector3f g = new Vector3f(c).add(0.0F, 0.3F, 0.0F);
         float flash = (float)Math.exp((double)(-since / 5.0F)) * dim;
         if (flash > 0.01F) {
            float r = 45.0F * (1.0F - (float)Math.exp((double)(-since / 2.5F))) + 2.0F;
            sphere(m, vc, new Vector3f(c).add(0.0F, -r * 0.2F, 0.0F), r, WHITE_BLUE, flash * 0.9F, flash * 0.15F);
            sphere(m, vc, new Vector3f(c).add(0.0F, -r * 0.2F, 0.0F), r * 0.55F, new float[]{1.0F, 1.0F, 1.0F}, flash, flash * 0.4F);
         }

         if (since < 16.0F) {
            float k = 1.0F - since / 16.0F;
            Vector3f sky = new Vector3f(c).add(0.0F, 120.0F, 0.0F);
            bolt(m, vc, sky, c, RandomSource.create(4242L + (long)(since / 2.0F)), 7, 0.1F, 2.6F, k * dim, 3, WHITE_BLUE);
            Geo.columnPlain(
               m, vc, c.x, c.z, c.y, c.y + 130.0F, 9.0F * (0.4F + 0.6F * k), 3.0F, 32, since * 0.1F, CYAN[0], CYAN[1], CYAN[2], 0.55F * k * dim, 0.0F
            );
         }

         for (int w = 0; w < 2; w++) {
            float reach = w == 0 ? 140.0F : 90.0F;
            float speed = w == 0 ? 9.0F : 14.0F;
            float r = reach * (1.0F - (float)Math.exp((double)(-since / speed))) + 3.0F;
            float a = Mth.clamp(1.0F - since / (w == 0 ? 45.0F : 60.0F), 0.0F, 1.0F) * dim;
            float width = w == 0 ? 7.0F : 4.0F;
            Geo.ringIn(m, vc, g, ex, ez, Math.max(0.0F, r - width), r, 128, 0.0F, 1.0F, CYAN[0], CYAN[1], CYAN[2], 0.0F, a);
            Geo.ringIn(m, vc, g, ex, ez, Math.max(0.0F, r - width * 0.3F), r, 128, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, a * 0.8F);
         }

         if (since < 28.0F) {
            float k = 1.0F - since / 28.0F;
            RandomSource random = RandomSource.create((long)v.dragon.getId() * 977L + (long)(since / 2.0F));
            float r = 120.0F * (1.0F - (float)Math.exp((double)(-since / 9.0F))) + 6.0F;

            for (int i = 0; i < 36; i++) {
               float a = random.nextFloat() * (float) (Math.PI * 2);
               float len = r * (0.5F + 0.5F * random.nextFloat());
               Vector3f to = new Vector3f(g).add(Mth.cos(a) * len, 0.5F, Mth.sin(a) * len);
               bolt(
                  m,
                  vc,
                  new Vector3f(g).add(Mth.cos(a) * 4.0F, 1.0F, Mth.sin(a) * 4.0F),
                  to,
                  random,
                  5,
                  0.22F,
                  0.35F,
                  0.9F * k * dim,
                  2,
                  random.nextInt(4) == 0 ? VIOLET : WHITE_BLUE
               );
            }
         }

         float crater = Mth.clamp(1.0F - since / 70.0F, 0.0F, 1.0F) * dim;
         Geo.discIn(m, vc, g, ex, ez, 28.0F, 64, CYAN[0], CYAN[1], CYAN[2], 0.55F * crater, 0.0F);
      }
   }

   private static void sphere(Matrix4f m, VertexConsumer vc, Vector3f c, float r, float[] col, float aFacing, float aRim) {
      int rings = 14;
      int segs = 28;
      Vector3f toEye = new Vector3f(c).negate().normalize();

      for (int i = 0; i < rings; i++) {
         float p0 = (float) Math.PI * (float)i / (float)rings;
         float p1 = (float) Math.PI * (float)(i + 1) / (float)rings;

         for (int j = 0; j < segs; j++) {
            float q0 = (float) (Math.PI * 2) * (float)j / (float)segs;
            float q1 = (float) (Math.PI * 2) * (float)(j + 1) / (float)segs;
            float[][] corners = new float[][]{{p0, q0}, {p1, q0}, {p1, q1}, {p0, q1}};

            for (float[] k : corners) {
               Vector3f n = new Vector3f(Mth.sin(k[0]) * Mth.cos(k[1]), Mth.cos(k[0]), Mth.sin(k[0]) * Mth.sin(k[1]));
               float a = Mth.lerp(Math.abs(n.dot(toEye)), aRim, aFacing);
               Sculpt.vertex(m, vc, new Vector3f(n).mul(r).add(c), col[0], col[1], col[2], a, 0.5F, 0.5F);
            }
         }
      }
   }

   private static void bolt(
      Matrix4f m,
      VertexConsumer vc,
      Vector3f from,
      Vector3f to,
      RandomSource random,
      int levels,
      float chaos,
      float width,
      float alpha,
      int branches,
      float[] col
   ) {
      if (!(alpha <= 0.01F)) {
         List<Bolt.Arc> out = new ArrayList<>();
         Bolt.generate(out, from, to, random, levels, chaos, width, alpha, branches);
         Vector3f eye = new Vector3f();

         for (Bolt.Arc arc : out) {
            Geo.strand(m, vc, arc.from(), arc.to(), eye, arc.width() * 3.5F, col[0] * 0.45F, col[1] * 0.6F, col[2], arc.alpha() * 0.2F, arc.alpha() * 0.2F);
            Geo.strand(m, vc, arc.from(), arc.to(), eye, arc.width(), col[0], col[1], col[2], arc.alpha(), arc.alpha());
         }
      }
   }

   private static void drawBolt(
      Matrix4f m, VertexConsumer vc, Vector3f from, Vector3f to, RandomSource random, int levels, float chaos, float width, float alpha, int branches
   ) {
      bolt(m, vc, from, to, random, levels, chaos, width, alpha, branches, WHITE_BLUE);
   }

   private static void halos(Matrix4f m, VertexConsumer vc, DragonRenderer.View v, Vector3f right, Vector3f up) {
      float afterImpact = v.flight - 275.0F;
      if (afterImpact >= 0.0F && afterImpact < 20.0F) {
         float k = (float)Math.exp((double)(-afterImpact / 4.0F));
         Geo.billboard(
            m, vc, v.last.x, v.last.y + 8.0F, v.last.z, right, up, 70.0F, 0.0F, 0.0F, 1.0F, 1.0F, WHITE_BLUE[0], WHITE_BLUE[1], WHITE_BLUE[2], 0.7F * k
         );
      }

      for (DragonRenderer.Falling f : v.falling) {
         float since = v.time - f.mark.time - 6.0F;
         if (!(since < 0.0F)) {
            float k = Mth.clamp(1.0F - since / 8.0F, 0.0F, 1.0F);
            Geo.billboard(
               m,
               vc,
               f.ground.x,
               f.ground.y + 2.0F,
               f.ground.z,
               right,
               up,
               12.0F + (float)f.mark.n,
               0.0F,
               0.0F,
               1.0F,
               1.0F,
               WHITE_BLUE[0],
               WHITE_BLUE[1],
               WHITE_BLUE[2],
               0.3F * k * k
            );
         }
      }
   }

   private static record Falling(DragonRenderer.StrikeMark mark, DragonRenderer.Station[] body, boolean headOut, float alpha, Vector3f ground) {
      static DragonRenderer.Falling of(DragonRenderer.View v, DragonRenderer.StrikeMark mark) {
         float since = v.time - mark.time;
         if (!(since < 0.0F) && !(since > 16.0F)) {
            Vector3f from = new Vector3f(mark.from).add(v.base);
            Vector3f to = new Vector3f(mark.at).add(v.base);
            Vector3f run = new Vector3f(to).sub(from);
            Vector3f side = new Vector3f(-run.z, 0.0F, run.x);
            if (side.lengthSquared() < 1.0E-6F) {
               side.set(1.0F, 0.0F, 0.0F);
            }

            side.normalize();
            float head = since / 6.0F;
            float span = 0.85F;
            List<Vector3f> pts = new ArrayList<>();
            int n = 46;

            for (int k = 0; k <= n; k++) {
               float u = head - span * (float)k / (float)n;
               if (u < 0.0F) {
                  break;
               }

               if (!(u > 1.0F)) {
                  Vector3f p = new Vector3f(from)
                     .lerp(to, u * u * (1.6F - 0.6F * u))
                     .add(new Vector3f(side).mul(mark.swing * Mth.sin((float) Math.PI * u)))
                     .add(new Vector3f(side).mul(mark.swing * 0.35F * Mth.sin((float) (Math.PI * 2) * u)));
                  pts.add(p);
               }
            }

            DragonRenderer.Station[] body = DragonRenderer.lay(pts, 1.1F, run.length() * span);
            if (body == null) {
               return null;
            } else {
               float alpha = since < 11.0F ? 1.0F : 1.0F - (since - 6.0F - 5.0F) / 5.0F;
               return new DragonRenderer.Falling(mark, body, head < 1.0F, Mth.clamp(alpha, 0.0F, 1.0F), to);
            }
         } else {
            return null;
         }
      }
   }

   private static final class Memory {
      int strikes;
      final List<DragonRenderer.StrikeMark> marks = new ArrayList<>();
   }

   private interface Pass {
      void draw(VertexConsumer var1);
   }

   private static record Shape(DragonRenderer.Station[] b, boolean headOut, float size, float open, long seed, float time, int detail, float dim) {
      RandomSource random(int salt) {
         return RandomSource.create(this.seed + (long)salt * 31L + (long)(this.time / 2.0F) * 1009L);
      }
   }

   private static final class Station {
      final Vector3f at;
      final Vector3f fwd;
      final Vector3f up;
      final Vector3f side;
      final float s;
      final float u;
      final float r;

      Station(Vector3f at, Vector3f fwd, Vector3f up, Vector3f side, float s, float u, float r) {
         this.at = at;
         this.fwd = fwd;
         this.up = up;
         this.side = side;
         this.s = s;
         this.u = u;
         this.r = r;
      }

      Vector3f off(float f, float u, float sd) {
         return new Vector3f(this.at).add(new Vector3f(this.fwd).mul(f)).add(new Vector3f(this.up).mul(u)).add(new Vector3f(this.side).mul(sd));
      }

      Vector3f round(float t, float k) {
         return this.off(0.0F, Mth.cos(t) * this.r * k, Mth.sin(t) * this.r * k);
      }
   }

   private static record StrikeMark(Vector3f at, Vector3f from, float swing, float time, int n) {
   }

   private static final class View {
      final ThunderDragonEntity dragon;
      final DragonRenderer.Memory memory;
      final float time;
      final float flight;
      final Vector3f base;
      final Vector3f cloud;
      final float storm;
      final DragonRenderer.Station[] body;
      final boolean headOut;
      final Vector3f last;
      final List<DragonRenderer.Falling> falling = new ArrayList<>();

      View(ThunderDragonEntity dragon, float partial, Vec3 eye) {
         this.dragon = dragon;
         this.time = (float)dragon.tickCount + partial;
         this.flight = dragon.flight(partial);
         this.memory = DragonRenderer.MEMORY.computeIfAbsent(dragon, d -> new DragonRenderer.Memory());
         this.base = new Vector3f((float)(dragon.getX() - eye.x), (float)(dragon.getY() - eye.y), (float)(dragon.getZ() - eye.z));
         this.cloud = new Vector3f(this.base).add(0.0F, 48.0F, 0.0F);
         this.storm = dragon.storm(partial);
         this.last = new Vector3f(dragon.finalPoint()).add(this.base);
         this.headOut = this.flight > 0.0F && this.flight < 275.0F;
         this.body = this.flight > 0.0F ? this.layMain() : null;
         this.remember();

         for (DragonRenderer.StrikeMark mark : this.memory.marks) {
            DragonRenderer.Falling f = DragonRenderer.Falling.of(this, mark);
            if (f != null) {
               this.falling.add(f);
            }
         }
      }

      Vector3f at(float t) {
         return ThunderDragonEntity.at(t, this.dragon.entry(), this.dragon.turn(), this.dragon.finalPoint()).add(this.base);
      }

      private void remember() {
         int seen = this.dragon.strikes();
         if (seen > this.memory.strikes) {
            this.memory.strikes = seen;
            Vec3 hit = this.dragon.strikeAt().subtract(this.dragon.position());
            Vector3f at = new Vector3f((float)hit.x, (float)hit.y, (float)hit.z);
            Vector3f from = new Vector3f(at).mul(0.55F).add(0.0F, 44.0F, 0.0F);
            float swing = (seen % 2 == 0 ? 1.0F : -1.0F) * (8.0F + 3.0F * (float)(seen % 3));
            this.memory.marks.add(new DragonRenderer.StrikeMark(at, from, swing, this.time, seen));
         }

         Iterator<DragonRenderer.StrikeMark> it = this.memory.marks.iterator();

         while (it.hasNext()) {
            if (this.time - it.next().time > 16.0F) {
               it.remove();
            }
         }
      }

      private DragonRenderer.Station[] layMain() {
         List<Vector3f> pts = new ArrayList<>();
         int n = Mth.ceil(120.0F);
         float clock = this.flight <= 275.0F ? this.flight : 275.0F + (this.flight - 275.0F) * 4.0F;

         for (int k = 0; k <= n; k++) {
            float ts = clock - (float)k * 1.25F / 3.2F;
            if (ts <= 0.4F) {
               break;
            }

            if (!(ts > 275.0F)) {
               pts.add(this.at(ts));
            }
         }

         return DragonRenderer.lay(pts, 2.6F, 150.0F);
      }
   }
}
