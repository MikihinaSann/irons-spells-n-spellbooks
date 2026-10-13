package com.grandsigil.client;

import com.grandsigil.GrandSigil;
import com.grandsigil.GsPresence;
import com.grandsigil.config.GsConfig;
import com.grandsigil.entity.Terrain;
import com.grandsigil.entity.ThreadsEntity;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
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
import net.neoforged.neoforge.client.event.ViewportEvent.ComputeFogColor;
import net.neoforged.neoforge.client.event.ViewportEvent.RenderFog;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class ThreadsRenderer {
   private static final ResourceLocation FLOW = GrandSigil.id("textures/entity/blood_flow.png");
   private static final ResourceLocation THREAD = GrandSigil.id("textures/entity/blood_thread.png");
   private static final ResourceLocation POOL = GrandSigil.id("textures/entity/blood_pool.png");
   private static final float[] RED = new float[]{0.9F, 0.06F, 0.08F};
   private static final float[] HOT = new float[]{1.0F, 0.32F, 0.18F};
   private static final float[] DARK = new float[]{0.45F, 0.01F, 0.04F};
   private static final float POOL_RADIUS = 20.0F;
   private static final int STREAMS = 11;
   private static final int ORBITS = 7;
   private static final double VIEW = 320.0;
   private static final ByteBufferBuilder BUILDER = new ByteBufferBuilder(524288);
   private static final BufferSource BUFFERS = MultiBufferSource.immediate(BUILDER);
   private static final Map<ThreadsEntity, Terrain> GROUND = new WeakHashMap<>();
   private static final float[] ZENITH = new float[]{0.1F, 0.0F, 0.02F};
   private static final float[] HORIZON = new float[]{0.3F, 0.04F, 0.05F};

   private ThreadsRenderer() {
   }

   public static void render(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_TRANSLUCENT_BLOCKS && GsPresence.any(18)) {
         Minecraft minecraft = Minecraft.getInstance();
         ClientLevel level = minecraft.level;
         if (level != null) {
            Camera camera = event.getCamera();
            Vec3 eye = camera.getPosition();
            float partial = event.getPartialTick();
            int detail = (Integer)GsConfig.CLIENT.detail.get();
            List<ThreadsRenderer.View> views = new ArrayList<>();

            for (Entity entity : level.entitiesForRendering()) {
               if (entity instanceof ThreadsEntity) {
                  ThreadsEntity threads = (ThreadsEntity)entity;
                  if (threads.distanceToSqr(eye.x, eye.y, eye.z) < 102400.0) {
                     views.add(new ThreadsRenderer.View(threads, partial, eye, level));
                  }
               }
            }

            if (!views.isEmpty()) {
               PoseStack pose = event.getPoseStack();
               pose.pushPose();
               Matrix4f m = pose.last().pose();
               boolean pack = Shaders.packInUse();
               float dim = pack ? 0.6F : 1.0F;
               Vector3f right = new Vector3f(camera.getLeftVector()).mul(-1.0F);
               Vector3f up = new Vector3f(camera.getUpVector());
               float dusk = dusk(level, eye, partial);
               if (dusk > 0.01F && !pack) {
                  float far = Mth.clamp(minecraft.gameRenderer.getRenderDistance() * 0.9F, 96.0F, 300.0F);
                  batch(GsRenderTypes.glass(), vc -> duskDome(m, vc, far, dusk));
                  batch(GsRenderTypes.veil(), vc -> shade(m, vc, dusk));
               }

               batch(GsRenderTypes.matter(POOL), vc -> views.forEach(v -> {
                     pose.pushPose();
                     pose.translate(v.base.x, v.base.y, v.base.z);
                     pool(pose.last().pose(), vc, v, detail);
                     pose.popPose();
                  }));
               batch(GsRenderTypes.matter(FLOW), vc -> views.forEach(v -> {
                     heart(m, vc, v, false, 1.0F);
                     stream(m, vc, v, false, 1.0F);
                     aura(m, vc, v, false, 1.0F);
                     nova(m, vc, v, false, 1.0F);
                  }));
               batch(GsRenderTypes.matter(THREAD), vc -> views.forEach(v -> {
                     rising(m, vc, v, 0.95F, 1.6F);
                     threads(m, vc, v, 0.95F, 0.38F);
                     orbits(m, vc, v, 0.8F);
                  }));
               batch(GsRenderTypes.glow(FLOW), vc -> views.forEach(v -> {
                     heart(m, vc, v, true, dim);
                     stream(m, vc, v, true, dim);
                     aura(m, vc, v, true, dim);
                     nova(m, vc, v, true, dim);
                  }));
               batch(GsRenderTypes.glow(THREAD), vc -> views.forEach(v -> {
                     rising(m, vc, v, 0.12F * dim, 3.2F);
                     threads(m, vc, v, 0.12F * dim, 0.8F);
                     orbits(m, vc, v, 0.12F * dim);
                  }));
               batch(GsRenderTypes.glow(), vc -> views.forEach(v -> {
                     beads(m, vc, v, right, up, dim);
                     flashes(m, vc, v, right, up, dim);
                     pose.pushPose();
                     pose.translate(v.base.x, v.base.y, v.base.z);
                     rings(pose.last().pose(), vc, v, detail, dim);
                     pose.popPose();
                  }));
               pose.popPose();
               BUFFERS.endBatch();
            }
         }
      }
   }

   private static void batch(RenderType type, ThreadsRenderer.Pass pass) {
      VertexConsumer vc = BUFFERS.getBuffer(type);
      pass.draw(vc);
      BUFFERS.endBatch(type);
   }

   private static Sculpt.Ring[] tube(Vector3f[] pts, float[] radius, float[] alpha, float spin, float vSpan) {
      int n = pts.length;
      Sculpt.Ring[] rings = new Sculpt.Ring[n];

      for (int k = 0; k < n; k++) {
         Vector3f q = new Vector3f(pts[Math.min(n - 1, k + 1)]).sub(pts[Math.max(0, k - 1)]);
         if (q.lengthSquared() < 1.0E-8F) {
            q.set(0.0F, 1.0F, 0.0F);
         }

         q.normalize();
         Vector3f side = new Vector3f(q).cross(0.0F, 1.0F, 0.0F);
         if (side.lengthSquared() < 1.0E-6F) {
            side.set(1.0F, 0.0F, 0.0F);
         }

         side.normalize();
         Vector3f up = new Vector3f(side).cross(q).normalize();
         Vector3f s2 = new Vector3f(side).mul(Mth.cos(spin)).add(new Vector3f(up).mul(Mth.sin(spin)));
         Vector3f u2 = new Vector3f(up).mul(Mth.cos(spin)).sub(new Vector3f(side).mul(Mth.sin(spin)));
         rings[k] = new Sculpt.Ring(pts[k], s2, u2, radius[k], radius[k], (float)k / (float)(n - 1) * vSpan, alpha[k]);
      }

      return rings;
   }

   private static void heart(Matrix4f m, VertexConsumer vc, ThreadsRenderer.View v, boolean light, float dim) {
      float R = v.radius;
      if (!(R <= 0.2F) && !(v.t > 232.0F)) {
         float spin = v.time * 0.02F;
         Sculpt.Ring[] rings = sphere(v.heart, R, spin);
         Sculpt.Ring[] core = sphere(v.heart, R * (Shaders.packInUse() ? 0.62F : 0.5F) * (1.0F + 0.15F * v.beat), -spin * 1.5F);
         boolean pack = Shaders.packInUse();
         if (light && pack) {
            Sculpt.energy(m, vc, rings, 36, 2.0F, -v.time * 0.02F, 1.0F, RED[0], RED[1], RED[2], 0.35F + 0.2F * v.beat, 0.7F, 1.1F, 0.12F, v.time);
            Sculpt.energy(m, vc, core, 28, 2.0F, -v.time * 0.04F, 1.4F, RED[0], RED[1], RED[2], 0.35F + 0.2F * v.beat, 0.05F, 1.0F, 0.2F, v.time + 20.0F);
            Sculpt.energy(m, vc, core, 28, 2.0F, -v.time * 0.03F, 1.0F, HOT[0], HOT[1], HOT[2], 0.6F + 0.35F * v.beat, 0.1F, 0.8F, 0.2F, v.time);
         } else if (!light && pack) {
            Sculpt.energy(m, vc, rings, 36, 2.0F, -v.time * 0.02F, 1.0F, 1.0F, 0.5F, 0.5F, 0.62F, 0.98F, 1.1F, 0.12F, v.time);
         } else {
            if (light) {
               float glow = 0.16F * v.beat;
               Sculpt.energy(m, vc, core, 28, 2.0F, -v.time * 0.03F, 1.0F, HOT[0], HOT[1], HOT[2], glow * dim, 0.0F, 1.0F, 0.15F, v.time);
               Sculpt.energy(m, vc, rings, 32, 2.0F, -v.time * 0.02F, 1.03F, RED[0], RED[1], RED[2], 0.0F, 0.1F * dim, 2.5F, 0.12F, v.time);
            } else {
               Sculpt.energy(m, vc, rings, 32, 2.0F, -v.time * 0.012F, 1.3F, 0.8F, 0.6F, 0.6F, 0.06F, 0.28F, 1.2F, 0.18F, v.time + 40.0F);
               Sculpt.energy(m, vc, rings, 36, 2.0F, -v.time * 0.02F, 1.0F, 0.9F, 0.85F, 0.85F, 0.78F, 1.0F, 1.1F, 0.12F, v.time);
            }
         }
      }
   }

   private static Sculpt.Ring[] sphere(Vector3f centre, float R, float spin) {
      int n = 18;
      Sculpt.Ring[] rings = new Sculpt.Ring[n + 1];
      Vector3f side = new Vector3f(Mth.cos(spin), 0.0F, Mth.sin(spin));
      Vector3f up = new Vector3f(-Mth.sin(spin), 0.0F, Mth.cos(spin));

      for (int k = 0; k <= n; k++) {
         float phi = (float) (-Math.PI / 2) + (float) Math.PI * (float)k / (float)n;
         float r = Math.max(0.02F, R * Mth.cos(phi));
         Vector3f c = new Vector3f(centre).add(0.0F, R * 1.05F * Mth.sin(phi), 0.0F);
         rings[k] = new Sculpt.Ring(c, side, up, r, r, (float)k / (float)n * 2.0F, 1.0F);
      }

      return rings;
   }

   private static void orbits(Matrix4f m, VertexConsumer vc, ThreadsRenderer.View v, float a) {
      float R = v.radius;
      if (!(R <= 0.5F) && !(v.t > 230.0F)) {
         float k = a * Math.min(1.0F, R / 4.0F);
         RandomSource random = RandomSource.create((long)v.threads.getId() * 61L);

         for (int j = 0; j < 7; j++) {
            float tilt = 0.3F + random.nextFloat() * 1.0F;
            float yaw = random.nextFloat() * (float) (Math.PI * 2);
            float speed = 0.05F + random.nextFloat() * 0.05F;
            float reach = 1.35F + random.nextFloat() * 0.4F;
            int n = 14;
            Vector3f[] pts = new Vector3f[n];
            float[] half = new float[n];

            for (int i = 0; i < n; i++) {
               float q = (float)i / (float)(n - 1);
               float ang = v.time * speed + (float)j * 2.1F - (1.0F - q) * 1.6F;
               float x = Mth.cos(ang) * R * reach;
               float y = Mth.sin(ang) * R * reach;
               Vector3f p = new Vector3f(x, y * Mth.sin(tilt), y * Mth.cos(tilt));
               p.rotateY(yaw);
               pts[i] = p.add(v.heart);
               half[i] = 0.2F + 0.9F * q;
            }

            Sculpt.ribbon(m, vc, pts, half, 1.0F, 0.35F, 0.35F, k * 0.2F, k);
         }
      }
   }

   private static void rising(Matrix4f m, VertexConsumer vc, ThreadsRenderer.View v, float a, float width) {
      if (!(v.t > 56.0F)) {
         float reach = Mth.clamp(v.t / 24.0F, 0.0F, 1.0F);
         float fade = 1.0F - Mth.clamp((v.t - 30.0F) / 26.0F, 0.0F, 1.0F);
         if (!(fade <= 0.01F)) {
            int n = 24;
            RandomSource random = RandomSource.create((long)v.threads.getId() * 17L);

            for (int j = 0; j < 11; j++) {
               float a0 = random.nextFloat() * (float) (Math.PI * 2);
               float r0 = 7.0F + random.nextFloat() * 14.0F;
               float turns = 0.55F + random.nextFloat() * 0.6F;
               float delay = random.nextFloat() * 0.35F;
               float w = width * (0.6F + random.nextFloat() * 0.8F);
               float own = Mth.clamp((reach - delay) / (1.0F - delay), 0.0F, 1.0F);
               int shown = (int)((float)n * own);
               if (shown >= 2) {
                  Vector3f[] pts = new Vector3f[shown];
                  float[] half = new float[shown];

                  for (int i = 0; i < shown; i++) {
                     float q = (float)i / (float)(n - 1);
                     float ang = a0 + q * turns * (float) Math.PI + 0.4F * Mth.sin(q * 7.0F + v.time * 0.08F + (float)j);
                     float r = Mth.lerp((float)Math.pow((double)q, 1.6), r0, 1.5F);
                     float h = 32.0F * q;
                     pts[i] = new Vector3f(v.base).add(Mth.cos(ang) * r, h, Mth.sin(ang) * r);
                     half[i] = w * (0.3F + 0.7F * Mth.sin((float) Math.PI * Math.min(1.0F, q * 1.05F)));
                  }

                  Sculpt.ribbon(m, vc, pts, half, 1.0F, 0.3F, 0.3F, a * fade, a * fade);
               }
            }
         }
      }
   }

   private static Vector3f along(ThreadsRenderer.View v, ThreadsRenderer.Bound b, float s, float shake) {
      Vector3f from = new Vector3f(b.at).sub(v.heart);
      float len = from.length();
      Vector3f dir = len > 1.0E-4F ? new Vector3f(from).div(len) : new Vector3f(0.0F, -1.0F, 0.0F);
      Vector3f start = new Vector3f(v.heart).add(new Vector3f(dir).mul(v.radius * 0.85F));
      Vector3f p = new Vector3f(start).lerp(b.at, s);
      float bell = Mth.sin((float) Math.PI * s);
      p.y -= len * 0.06F * bell;
      Vector3f side = new Vector3f(dir).cross(0.0F, 1.0F, 0.0F);
      if (side.lengthSquared() < 1.0E-6F) {
         side.set(1.0F, 0.0F, 0.0F);
      }

      side.normalize();
      float sway = (0.5F + shake) * bell * Mth.sin(s * (float) Math.PI * 3.0F + v.time * 0.18F + (float)b.index * 1.7F);
      p.add(side.mul(sway));
      p.y = p.y + 0.4F * bell * Mth.sin(s * (float) Math.PI * 2.0F - v.time * 0.13F + (float)b.index);
      return p;
   }

   private static void threads(Matrix4f m, VertexConsumer vc, ThreadsRenderer.View v, float a, float width) {
      float snap = v.t - 206.0F;
      if (!(snap > 12.0F) && (!(v.radius <= 0.2F) || !(snap < 0.0F))) {
         int n = 18;

         for (ThreadsRenderer.Bound b : v.bound) {
            float shot = ThreadsEntity.smooth(b.since / 7.0F);
            if (!(shot <= 0.01F)) {
               if (snap < 0.0F) {
                  float swallow = 1.0F + 0.35F * v.beat;
                  piece(m, vc, v, b, 0.0F, shot, n, width * swallow, a, 0.0F);
               } else {
                  float q = Mth.clamp(snap / 9.0F, 0.0F, 1.0F);
                  float alpha = a * (1.0F - q) * (snap < 3.0F ? 1.6F : 1.0F);
                  piece(m, vc, v, b, 0.0F, 0.5F * (1.0F - q), n / 2, width, alpha, 2.5F * q + 0.8F);
                  piece(m, vc, v, b, 1.0F - 0.5F * (1.0F - q), 1.0F, n / 2, width, alpha, 2.5F * q + 0.8F);
               }
            }
         }
      }
   }

   private static void piece(
      Matrix4f m, VertexConsumer vc, ThreadsRenderer.View v, ThreadsRenderer.Bound b, float s0, float s1, int n, float width, float a, float shake
   ) {
      if (!(s1 - s0 < 0.01F) && !(a <= 0.01F)) {
         Vector3f[] pts = new Vector3f[n];
         float[] half = new float[n];

         for (int i = 0; i < n; i++) {
            float s = Mth.lerp((float)i / (float)(n - 1), s0, s1);
            pts[i] = along(v, b, s, shake);
            half[i] = width * (0.7F + 0.3F * Mth.sin((float) Math.PI * s));
         }

         Sculpt.ribbon(m, vc, pts, half, 1.0F, 0.3F, 0.3F, a, a);
      }
   }

   private static void beads(Matrix4f m, VertexConsumer vc, ThreadsRenderer.View v, Vector3f right, Vector3f up, float dim) {
      if (!(v.t < 46.0F) && !(v.t >= 206.0F)) {
         for (ThreadsRenderer.Bound b : v.bound) {
            if (!(b.since < 7.0F)) {
               for (int k = 0; k < 3; k++) {
                  float s = 1.0F - (v.time * 0.035F + (float)b.index * 0.37F + (float)k / 3.0F) % 1.0F;
                  Vector3f p = along(v, b, s, 0.0F);
                  float size = 0.45F + 0.35F * v.beat;
                  Geo.discIn(m, vc, p, right, up, size, 12, HOT[0], HOT[1], HOT[2], 0.85F * dim, 0.0F);
               }

               Geo.discIn(m, vc, b.at, right, up, 0.9F + 0.6F * v.beat, 16, RED[0], RED[1], RED[2], 0.6F * dim, 0.0F);
            }
         }
      }
   }

   private static void stream(Matrix4f m, VertexConsumer vc, ThreadsRenderer.View v, boolean light, float dim) {
      float k = ThreadsEntity.smooth((v.t - 46.0F + 4.0F) / 10.0F) * (1.0F - ThreadsEntity.smooth((v.t - 206.0F) / 6.0F));
      if (!(k <= 0.01F)) {
         Vector3f from = new Vector3f(v.heart).add(0.0F, -v.radius * 0.9F, 0.0F);
         Vector3f to = v.caster;
         int n = 20;
         Vector3f[] pts = new Vector3f[n];
         float[] radius = new float[n];
         float[] alpha = new float[n];
         Vector3f across = new Vector3f(to).sub(from).cross(0.0F, 1.0F, 0.0F);
         if (across.lengthSquared() < 1.0E-6F) {
            across.set(1.0F, 0.0F, 0.0F);
         }

         across.normalize();

         for (int i = 0; i < n; i++) {
            float s = (float)i / (float)(n - 1);
            float bell = Mth.sin((float) Math.PI * s);
            pts[i] = new Vector3f(from).lerp(to, s).add(new Vector3f(across).mul(1.5F * bell * Mth.sin(s * 5.0F + v.time * 0.1F)));
            radius[i] = (1.1F - 0.6F * s) * (1.0F + 0.25F * v.beat);
            alpha[i] = k * Mth.clamp((1.0F - s) / 0.05F, 0.0F, 1.0F);
         }

         Sculpt.Ring[] rings = tube(pts, radius, alpha, v.time * 0.05F, 4.0F);
         if (light) {
            Sculpt.energy(m, vc, rings, 14, 1.0F, v.time * 0.08F, 0.8F, HOT[0], HOT[1], HOT[2], 0.12F * dim, 0.02F * dim, 1.0F, 0.15F, v.time);
         } else {
            Sculpt.energy(m, vc, rings, 16, 1.0F, v.time * 0.06F, 1.0F, 1.0F, 1.0F, 1.0F, 0.5F, 0.95F, 1.2F, 0.15F, v.time);
         }
      }
   }

   private static void nova(Matrix4f m, VertexConsumer vc, ThreadsRenderer.View v, boolean light, float dim) {
      float s = v.t - 230.0F;
      if (!(s < 0.0F) && !(s > 26.0F)) {
         float R = 2.0F + 26.0F * (1.0F - (float)Math.exp((double)(-s / 5.0F)));
         float fade = 1.0F - s / 26.0F;
         fade *= fade;
         int n = 12;
         Sculpt.Ring[] rings = new Sculpt.Ring[n + 1];
         Vector3f foot = new Vector3f(v.caster).add(0.0F, -1.1F, 0.0F);

         for (int k = 0; k <= n; k++) {
            float phi = (float) (Math.PI / 2) * (float)k / (float)n;
            float r = Math.max(0.05F, R * Mth.cos(phi));
            Vector3f c = new Vector3f(foot).add(0.0F, R * 0.55F * Mth.sin(phi), 0.0F);
            float spin = v.time * 0.03F;
            Vector3f side = new Vector3f(Mth.cos(spin), 0.0F, Mth.sin(spin));
            Vector3f up = new Vector3f(-Mth.sin(spin), 0.0F, Mth.cos(spin));
            float a = fade * Mth.clamp((float)k / 2.0F, 0.0F, 1.0F) * Mth.clamp((float)(n - k) / 3.0F, 0.0F, 1.0F);
            rings[k] = new Sculpt.Ring(c, side, up, r, r, (float)k / (float)n * 2.0F, a);
         }

         if (light) {
            Sculpt.energy(m, vc, rings, 40, 3.0F, -s * 0.05F, 1.01F, RED[0], RED[1], RED[2], 0.0F, 0.25F * dim, 1.2F, 0.08F, v.time);
         } else {
            Sculpt.energy(m, vc, rings, 40, 3.0F, -s * 0.04F, 1.0F, 1.0F, 1.0F, 1.0F, 0.1F, 0.75F, 1.0F, 0.08F, v.time);
         }
      }
   }

   private static void aura(Matrix4f m, VertexConsumer vc, ThreadsRenderer.View v, boolean light, float dim) {
      float s = v.t - 230.0F;
      if (!(s < 0.0F) && !(s > 36.0F)) {
         float k = 1.0F - s / 36.0F;
         k *= k;
         float lift = 1.0F + s * 0.12F;
         int n = 10;
         Vector3f[] pts = new Vector3f[n];
         float[] radius = new float[n];
         float[] alpha = new float[n];

         for (int i = 0; i < n; i++) {
            float q = (float)i / (float)(n - 1);
            pts[i] = new Vector3f(v.caster).add(0.0F, -1.1F + q * 12.0F * lift, 0.0F);
            radius[i] = (2.2F + 1.6F * q) * (1.0F + 0.4F * (float)Math.exp((double)(-s / 4.0F)));
            alpha[i] = k * Mth.clamp(q / 0.1F, 0.0F, 1.0F) * Mth.clamp((1.0F - q) / 0.5F, 0.0F, 1.0F);
         }

         Sculpt.Ring[] rings = tube(pts, radius, alpha, v.time * 0.1F, 2.0F);
         if (light) {
            Sculpt.energy(m, vc, rings, 18, 2.0F, -v.time * 0.08F, 1.02F, RED[0], RED[1], RED[2], 0.1F * dim, 0.5F * dim, 1.3F, 0.2F, v.time);
         } else {
            Sculpt.energy(m, vc, rings, 18, 2.0F, -v.time * 0.06F, 1.0F, 1.0F, 1.0F, 1.0F, 0.15F, 0.8F, 1.3F, 0.2F, v.time);
         }
      }
   }

   private static void flashes(Matrix4f m, VertexConsumer vc, ThreadsRenderer.View v, Vector3f right, Vector3f up, float dim) {
      float snap = v.t - 206.0F;
      if (snap >= 0.0F && snap < 10.0F) {
         float k = (float)Math.exp((double)(-snap / 2.5F)) * dim;

         for (ThreadsRenderer.Bound b : v.bound) {
            Geo.discIn(m, vc, b.at, right, up, 1.5F + snap * 0.4F, 16, HOT[0], HOT[1], HOT[2], k, 0.0F);
            Vector3f mid = along(v, b, 0.5F, 0.0F);
            Geo.discIn(m, vc, mid, right, up, 1.2F, 12, HOT[0], HOT[1], HOT[2], 0.8F * k, 0.0F);
         }

         Geo.discIn(m, vc, v.heart, right, up, v.radius * 2.4F, 32, RED[0], RED[1], RED[2], 0.15F * k, 0.0F);
      }

      float s = v.t - 230.0F;
      if (s >= 0.0F && s < 20.0F) {
         float k = (float)Math.exp((double)(-s / 3.5F)) * dim;
         Geo.discIn(m, vc, v.caster, right, up, 5.0F + s * 1.5F, 32, RED[0], RED[1], RED[2], 0.8F * k, 0.0F);
         Geo.discIn(m, vc, v.caster, right, up, 2.0F + s * 0.5F, 24, HOT[0], HOT[1], HOT[2], k, 0.0F);
      }
   }

   private static void pool(Matrix4f m, VertexConsumer vc, ThreadsRenderer.View v, int detail) {
      float k = v.presence * 0.9F;
      float R = 20.0F * ThreadsEntity.smooth(v.t / 24.0F);
      if (!(k <= 0.01F) && !(R < 0.5F)) {
         int rings = detail >= 2 ? 16 : 10;
         int segs = detail >= 2 ? 72 : 48;
         float spin = v.time * 0.003F;

         for (int i = 0; i < rings; i++) {
            float r0 = R * (float)i / (float)rings;
            float r1 = R * (float)(i + 1) / (float)rings;
            float a0 = k * edge(r0, R);
            float a1 = k * edge(r1, R);

            for (int j = 0; j < segs; j++) {
               float t0 = (float) (Math.PI * 2) * (float)j / (float)segs;
               float t1 = (float) (Math.PI * 2) * (float)(j + 1) / (float)segs;
               poolVertex(m, vc, v, r0, t0, R, spin, a0);
               poolVertex(m, vc, v, r1, t0, R, spin, a1);
               poolVertex(m, vc, v, r1, t1, R, spin, a1);
               poolVertex(m, vc, v, r0, t1, R, spin, a0);
            }
         }
      }
   }

   private static float edge(float r, float R) {
      return Mth.clamp((R - r) / 4.0F, 0.0F, 1.0F);
   }

   private static void poolVertex(Matrix4f m, VertexConsumer vc, ThreadsRenderer.View v, float r, float t, float R, float spin, float a) {
      float rr = r * (1.0F + 0.12F * Mth.sin(t * 5.0F + 1.3F) * Mth.sin(t * 3.0F) * (r / Math.max(1.0F, R)));
      float x = Mth.cos(t) * rr;
      float z = Mth.sin(t) * rr;
      float y = v.ground.lift(x, z) + 0.06F;
      float cs = Mth.cos(spin);
      float sn = Mth.sin(spin);
      Sculpt.vertex(m, vc, new Vector3f(x, y, z), 1.0F, 1.0F, 1.0F, a, (x * cs - z * sn) / 12.0F, (x * sn + z * cs) / 12.0F);
   }

   private static void rings(Matrix4f m, VertexConsumer vc, ThreadsRenderer.View v, int detail, float dim) {
      int segs = detail >= 2 ? 96 : 64;
      float k = v.presence * dim;
      if (!(k <= 0.01F)) {
         if (v.t < 206.0F) {
            float ph = v.t % 20.0F / 20.0F;
            float r = 2.0F + ph * 26.0F;
            Geo.band(m, vc, v.ground, 0.1F, Math.max(0.0F, r - 1.5F), r, segs, 0.0F, 1.0F, RED[0], RED[1], RED[2], 0.0F, 0.45F * (1.0F - ph) * k);
            Geo.band(m, vc, v.ground, 0.08F, 38.5F, 40.0F, segs, 0.0F, 1.0F, DARK[0], DARK[1], DARK[2], 0.0F, 0.25F * k);
         }

         float s = v.t - 230.0F;
         if (s >= 0.0F && s < 24.0F) {
            float r = 2.0F + 22.0F * (1.0F - (float)Math.exp((double)(-s / 5.0F)));
            float a = (1.0F - s / 24.0F) * dim;
            Vector3f c = new Vector3f(v.caster).sub(v.base).add(0.0F, -1.0F, 0.0F);
            Vector3f ex = new Vector3f(1.0F, 0.0F, 0.0F);
            Vector3f ez = new Vector3f(0.0F, 0.0F, 1.0F);
            Geo.ringIn(m, vc, c, ex, ez, Math.max(0.0F, r - 4.0F), r, segs, 0.0F, 1.0F, RED[0], RED[1], RED[2], 0.0F, 0.7F * a);
         }
      }
   }

   public static float dusk(ClientLevel level, Vec3 eye, float partial) {
      if (!GsPresence.any(18)) {
         return 0.0F;
      } else {
         float best = 0.0F;

         for (Entity entity : level.entitiesForRendering()) {
            if (entity instanceof ThreadsEntity threads) {
               double dx = threads.getX() - eye.x;
               double dz = threads.getZ() - eye.z;
               float near = Mth.clamp(1.0F - (float)(Math.sqrt(dx * dx + dz * dz) - 80.0) / 120.0F, 0.0F, 1.0F);
               best = Math.max(best, 0.85F * ThreadsEntity.presence(threads.age(partial)) * near);
            }
         }

         return best;
      }
   }

   private static void duskDome(Matrix4f m, VertexConsumer vc, float radius, float k) {
      int bands = 12;
      int segs = 48;

      for (int i = 0; i < bands; i++) {
         float e0 = -0.35F + 1.9207964F * (float)i / (float)bands;
         float e1 = -0.35F + 1.9207964F * (float)(i + 1) / (float)bands;

         for (int j = 0; j < segs; j++) {
            float a0 = (float) (Math.PI * 2) * (float)j / (float)segs;
            float a1 = (float) (Math.PI * 2) * (float)(j + 1) / (float)segs;
            domeVertex(m, vc, radius, e0, a0, k);
            domeVertex(m, vc, radius, e1, a0, k);
            domeVertex(m, vc, radius, e1, a1, k);
            domeVertex(m, vc, radius, e0, a1, k);
         }
      }
   }

   private static void domeVertex(Matrix4f m, VertexConsumer vc, float radius, float elevation, float azimuth, float k) {
      float q = (float)Math.sqrt((double)Mth.clamp(elevation / (float) (Math.PI / 2), 0.0F, 1.0F));
      float c = Mth.cos(elevation);
      Sculpt.vertex(
         m,
         vc,
         new Vector3f(Mth.cos(azimuth) * c * radius, Mth.sin(elevation) * radius, Mth.sin(azimuth) * c * radius),
         Mth.lerp(q, HORIZON[0], ZENITH[0]),
         Mth.lerp(q, HORIZON[1], ZENITH[1]),
         Mth.lerp(q, HORIZON[2], ZENITH[2]),
         k * Mth.lerp(q, 0.7F, 0.85F),
         0.5F,
         0.5F
      );
   }

   private static void shade(Matrix4f m, VertexConsumer vc, float k) {
      float a = 0.32F * k;
      int bands = 8;
      int segs = 16;

      for (int i = 0; i < bands; i++) {
         float e0 = (float) (-Math.PI / 2) + (float) Math.PI * (float)i / (float)bands;
         float e1 = (float) (-Math.PI / 2) + (float) Math.PI * (float)(i + 1) / (float)bands;

         for (int j = 0; j < segs; j++) {
            float a0 = (float) (Math.PI * 2) * (float)j / (float)segs;
            float a1 = (float) (Math.PI * 2) * (float)(j + 1) / (float)segs;
            shadeVertex(m, vc, e0, a0, a);
            shadeVertex(m, vc, e1, a0, a);
            shadeVertex(m, vc, e1, a1, a);
            shadeVertex(m, vc, e0, a1, a);
         }
      }
   }

   private static void shadeVertex(Matrix4f m, VertexConsumer vc, float elevation, float azimuth, float a) {
      float c = Mth.cos(elevation);
      Sculpt.vertex(m, vc, new Vector3f(Mth.cos(azimuth) * c * 1.5F, Mth.sin(elevation) * 1.5F, Mth.sin(azimuth) * c * 1.5F), 0.16F, 0.0F, 0.02F, a, 0.5F, 0.5F);
   }

   public static void fogColour(ComputeFogColor event) {
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null) {
         float k = dusk(level, event.getCamera().getPosition(), (float)event.getPartialTick());
         if (!(k <= 0.01F)) {
            event.setRed(Mth.lerp(k, event.getRed(), HORIZON[0]));
            event.setGreen(Mth.lerp(k, event.getGreen(), HORIZON[1]));
            event.setBlue(Mth.lerp(k, event.getBlue(), HORIZON[2]));
         }
      }
   }

   public static void fog(RenderFog event) {
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null) {
         float k = dusk(level, event.getCamera().getPosition(), (float)event.getPartialTick());
         if (!(k <= 0.01F)) {
            event.setFarPlaneDistance(Mth.lerp(k, event.getFarPlaneDistance(), event.getFarPlaneDistance() * 0.65F));
            event.setNearPlaneDistance(Mth.lerp(k, event.getNearPlaneDistance(), 0.0F));
            event.setCanceled(true);
         }
      }
   }

   private static final class Bound {
      final Vector3f at;
      final float since;
      final int index;

      Bound(Vector3f at, float since, int index) {
         this.at = at;
         this.since = since;
         this.index = index;
      }
   }

   private interface Pass {
      void draw(VertexConsumer var1);
   }

   private static final class View {
      final ThreadsEntity threads;
      final float t;
      final float time;
      final Vector3f base;
      final float radius;
      final float beat;
      final float presence;
      final Vector3f heart;
      final Vector3f caster;
      final List<ThreadsRenderer.Bound> bound = new ArrayList<>();
      final Terrain ground;

      View(ThreadsEntity threads, float partial, Vec3 eye, ClientLevel level) {
         this.threads = threads;
         this.t = threads.age(partial);
         this.time = (float)threads.tickCount + partial;
         this.base = new Vector3f((float)(threads.getX() - eye.x), (float)(threads.getY() - eye.y), (float)(threads.getZ() - eye.z));
         this.beat = ThreadsEntity.beat(this.t);
         this.radius = ThreadsEntity.heart(this.t) * (1.0F + 0.1F * this.beat);
         this.presence = ThreadsEntity.presence(this.t);
         Entity who = level.getEntity(threads.casterEntityId());
         if (who != null) {
            Vec3 p = who.getPosition(partial);
            this.caster = new Vector3f((float)(p.x - eye.x), (float)(p.y + (double)who.getBbHeight() * 0.55 - eye.y), (float)(p.z - eye.z));
         } else {
            this.caster = new Vector3f(this.base).add(0.0F, 1.0F, 0.0F);
         }

         Vector3f hang = new Vector3f(this.base).add(0.0F, 34.0F, 0.0F);
         this.heart = hang.lerp(this.caster, ThreadsEntity.descent(this.t));
         int[] ids = threads.boundIds();
         int[] at = threads.boundTicks();

         for (int i = 0; i < ids.length && i < at.length; i++) {
            Entity e = level.getEntity(ids[i]);
            if (e != null && (e.isAlive() || !(this.t < 206.0F))) {
               Vec3 p = e.getPosition(partial);
               this.bound
                  .add(
                     new ThreadsRenderer.Bound(
                        new Vector3f((float)(p.x - eye.x), (float)(p.y + (double)e.getBbHeight() * 0.6 - eye.y), (float)(p.z - eye.z)),
                        this.t - (float)at[i],
                        i
                     )
                  );
            }
         }

         this.ground = ThreadsRenderer.GROUND.computeIfAbsent(threads, s -> Terrain.sample(level, s.position(), 24.0F));
      }
   }
}
