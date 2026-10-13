package com.grandsigil.client;

import com.grandsigil.GrandSigil;
import com.grandsigil.GsPresence;
import com.grandsigil.config.GsConfig;
import com.grandsigil.entity.IceAgeEntity;
import com.grandsigil.entity.Terrain;
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
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import net.neoforged.neoforge.client.event.ViewportEvent.ComputeFogColor;
import net.neoforged.neoforge.client.event.ViewportEvent.RenderFog;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class IceAgeRenderer {
   private static final ResourceLocation AURORA = GrandSigil.id("textures/entity/aurora.png");
   private static final ResourceLocation BLIZZARD = GrandSigil.id("textures/entity/blizzard.png");
   private static final ResourceLocation FROST_FLOW = GrandSigil.id("textures/entity/frost_flow.png");
   private static final ResourceLocation STREAK = GrandSigil.id("textures/entity/frost_streak.png");
   private static final ResourceLocation FROST_GROUND = GrandSigil.id("textures/entity/frost_ground.png");
   private static final ResourceLocation FLARE = GrandSigil.id("textures/entity/flare.png");
   private static final float[] ICE = new float[]{0.55F, 0.82F, 1.0F};
   private static final float[] PALE = new float[]{0.84F, 0.94F, 1.0F};
   private static final float[] DEEP = new float[]{0.3F, 0.55F, 1.0F};
   private static final float[] WHITE = new float[]{1.0F, 1.0F, 1.0F};
   private static final float[][] CURTAINS = new float[][]{
      {0.3F, 190.0F, 1.2F, 120.0F, 70.0F, 0.0F},
      {2.4F, 210.0F, 1.0F, 135.0F, 64.0F, 1.3F},
      {4.3F, 185.0F, 1.1F, 115.0F, 74.0F, 2.1F},
      {1.4F, 170.0F, 0.9F, 140.0F, 58.0F, 0.7F},
      {3.4F, 225.0F, 1.3F, 145.0F, 66.0F, 3.1F},
      {5.4F, 175.0F, 0.8F, 130.0F, 60.0F, 4.4F}
   };
   private static final float[][] SHELLS = new float[][]{
      {1.3F, 1.6F, 0.035F, 0.1F, 0.45F}, {1.0F, 2.2F, 0.05F, 0.3F, 0.92F}, {0.78F, 2.8F, 0.075F, 0.3F, 0.85F}, {0.55F, 3.4F, 0.11F, 0.32F, 0.8F}
   };
   private static final float[][] SHELL_TINT = new float[][]{{0.66F, 0.74F, 0.88F}, {0.66F, 0.78F, 0.95F}, {0.78F, 0.88F, 1.0F}, {0.9F, 0.96F, 1.0F}};
   private static final int FLAKES = 600;
   private static final int CHUNK = 200;
   private static final int FRAGMENTS = 70;
   private static final double VIEW = 360.0;
   private static final ByteBufferBuilder BUILDER = new ByteBufferBuilder(524288);
   private static final BufferSource BUFFERS = MultiBufferSource.immediate(BUILDER);
   private static final Map<IceAgeEntity, Terrain> GROUND = new WeakHashMap<>();
   private static final Map<IceAgeEntity, float[]> LANDINGS = new WeakHashMap<>();
   private static final float[] ZENITH = new float[]{0.02F, 0.05F, 0.13F};
   private static final float[] HORIZON = new float[]{0.1F, 0.17F, 0.3F};

   private IceAgeRenderer() {
   }

   public static void render(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_TRANSLUCENT_BLOCKS && GsPresence.any(17)) {
         Minecraft minecraft = Minecraft.getInstance();
         ClientLevel level = minecraft.level;
         if (level != null) {
            Camera camera = event.getCamera();
            Vec3 eye = camera.getPosition();
            float partial = event.getPartialTick();
            int detail = (Integer)GsConfig.CLIENT.detail.get();
            List<IceAgeRenderer.View> views = new ArrayList<>();

            for (Entity entity : level.entitiesForRendering()) {
               if (entity instanceof IceAgeEntity) {
                  IceAgeEntity storm = (IceAgeEntity)entity;
                  if (storm.distanceToSqr(eye.x, eye.y, eye.z) < 129600.0) {
                     views.add(new IceAgeRenderer.View(storm, partial, eye, level));
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
               float night = night(level, eye, partial);
               if (night > 0.01F) {
                  float far = Mth.clamp(minecraft.gameRenderer.getRenderDistance() * 0.9F, 96.0F, 300.0F);
                  batch(GsRenderTypes.glass(), vc -> nightDome(m, vc, far, night));
                  batch(GsRenderTypes.veil(), vc -> shade(m, vc, night));
               }

               batch(GsRenderTypes.matter(FROST_GROUND), vc -> views.forEach(vx -> {
                     pose.pushPose();
                     pose.translate(vx.base.x, vx.base.y, vx.base.z);
                     frostGround(pose.last().pose(), vc, vx, detail);
                     pose.popPose();
                  }));
               batch(GsRenderTypes.matter(AURORA), vc -> views.forEach(vx -> aurora(m, vc, vx, pack ? 0.28F : 0.5F)));
               batch(GsRenderTypes.matter(BLIZZARD), vc -> views.forEach(vx -> vortex(m, vc, vx, false, 1.0F)));
               batch(GsRenderTypes.matter(FROST_FLOW), vc -> views.forEach(vx -> dome(m, vc, vx, false, 1.0F)));
               batch(GsRenderTypes.matter(STREAK), vc -> views.forEach(vx -> {
                     shards(m, vc, vx, 0.9F);
                     radials(m, vc, vx, 0.8F);
                     fragments(m, vc, vx, 0.9F);
                  }));
               batch(GsRenderTypes.glow(AURORA), vc -> views.forEach(vx -> aurora(m, vc, vx, 0.14F * dim * (1.0F + 0.8F * vx.flare))));
               batch(GsRenderTypes.glow(BLIZZARD), vc -> views.forEach(vx -> vortex(m, vc, vx, true, dim)));
               batch(GsRenderTypes.glow(FROST_FLOW), vc -> views.forEach(vx -> dome(m, vc, vx, true, dim)));
               batch(GsRenderTypes.glow(STREAK), vc -> views.forEach(vx -> {
                     shards(m, vc, vx, 0.35F * dim);
                     radials(m, vc, vx, 0.3F * dim);
                     fragments(m, vc, vx, 0.35F * dim);
                  }));
               batch(GsRenderTypes.glow(), vc -> views.forEach(vx -> {
                     flashes(m, vc, vx, right, up, dim);
                     pose.pushPose();
                     pose.translate(vx.base.x, vx.base.y, vx.base.z);
                     rings(pose.last().pose(), vc, vx, detail, dim);
                     pose.popPose();
                  }));

               for (IceAgeRenderer.View v : views) {
                  for (int from = 0; from < 600; from += 200) {
                     int start = from;
                     if (pack) {
                        batch(GsRenderTypes.glow(), vc -> flakes(m, vc, v, right, up, dim, false, start));
                     } else {
                        batch(GsRenderTypes.glow(FLARE), vc -> flakes(m, vc, v, right, up, dim, true, start));
                     }
                  }
               }

               pose.popPose();
               BUFFERS.endBatch();
            }
         }
      }
   }

   public static float night(ClientLevel level, Vec3 eye, float partial) {
      if (!GsPresence.any(17)) {
         return 0.0F;
      } else {
         float best = 0.0F;

         for (Entity entity : level.entitiesForRendering()) {
            if (entity instanceof IceAgeEntity storm) {
               double dx = storm.getX() - eye.x;
               double dz = storm.getZ() - eye.z;
               float near = Mth.clamp(1.0F - (float)(Math.sqrt(dx * dx + dz * dz) - 100.0) / 150.0F, 0.0F, 1.0F);
               best = Math.max(best, IceAgeEntity.sky(storm.age(partial)) * near);
            }
         }

         return best;
      }
   }

   private static void nightDome(Matrix4f m, VertexConsumer vc, float radius, float k) {
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
      float up = Mth.clamp(elevation / (float) (Math.PI / 2), 0.0F, 1.0F);
      float q = (float)Math.sqrt((double)up);
      float r = Mth.lerp(q, HORIZON[0], ZENITH[0]);
      float g = Mth.lerp(q, HORIZON[1], ZENITH[1]);
      float b = Mth.lerp(q, HORIZON[2], ZENITH[2]);
      float a = k * Mth.lerp(q, 0.72F, 0.86F);
      float c = Mth.cos(elevation);
      Sculpt.vertex(m, vc, new Vector3f(Mth.cos(azimuth) * c * radius, Mth.sin(elevation) * radius, Mth.sin(azimuth) * c * radius), r, g, b, a, 0.5F, 0.5F);
   }

   private static void shade(Matrix4f m, VertexConsumer vc, float k) {
      float a = 0.4F * k;
      int bands = 8;
      int segs = 16;
      float radius = 1.5F;

      for (int i = 0; i < bands; i++) {
         float e0 = (float) (-Math.PI / 2) + (float) Math.PI * (float)i / (float)bands;
         float e1 = (float) (-Math.PI / 2) + (float) Math.PI * (float)(i + 1) / (float)bands;

         for (int j = 0; j < segs; j++) {
            float a0 = (float) (Math.PI * 2) * (float)j / (float)segs;
            float a1 = (float) (Math.PI * 2) * (float)(j + 1) / (float)segs;
            shadeVertex(m, vc, radius, e0, a0, a);
            shadeVertex(m, vc, radius, e1, a0, a);
            shadeVertex(m, vc, radius, e1, a1, a);
            shadeVertex(m, vc, radius, e0, a1, a);
         }
      }
   }

   private static void shadeVertex(Matrix4f m, VertexConsumer vc, float radius, float elevation, float azimuth, float a) {
      float c = Mth.cos(elevation);
      Sculpt.vertex(
         m, vc, new Vector3f(Mth.cos(azimuth) * c * radius, Mth.sin(elevation) * radius, Mth.sin(azimuth) * c * radius), 0.03F, 0.06F, 0.15F, a, 0.5F, 0.5F
      );
   }

   public static void fogColour(ComputeFogColor event) {
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null) {
         float k = night(level, event.getCamera().getPosition(), (float)event.getPartialTick());
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
         float k = night(level, event.getCamera().getPosition(), (float)event.getPartialTick());
         if (!(k <= 0.01F)) {
            event.setFarPlaneDistance(Mth.lerp(k, event.getFarPlaneDistance(), event.getFarPlaneDistance() * 0.6F));
            event.setNearPlaneDistance(Mth.lerp(k, event.getNearPlaneDistance(), 0.0F));
            event.setCanceled(true);
         }
      }
   }

   private static void batch(RenderType type, IceAgeRenderer.Pass pass) {
      VertexConsumer vc = BUFFERS.getBuffer(type);
      pass.draw(vc);
      BUFFERS.endBatch(type);
   }

   private static float[] landings(ClientLevel level, IceAgeEntity storm) {
      float[] out = new float[28];

      for (int i = 0; i < out.length; i++) {
         Vec3 off = IceAgeEntity.shardTarget(storm.seed(), i);
         int y = level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(storm.getX() + off.x), Mth.floor(storm.getZ() + off.z));
         float d = (float)((double)y - storm.getY());
         out[i] = Math.abs(d) > 24.0F ? 0.0F : d;
      }

      return out;
   }

   private static void frostGround(Matrix4f m, VertexConsumer vc, IceAgeRenderer.View v, int detail) {
      float R = 56.0F * v.frost;
      float a = 0.5F * v.frostAlpha * (1.0F + 0.3F * v.zero);
      if (!(R < 0.5F) && !(a <= 0.01F)) {
         int rings = detail >= 2 ? 28 : 18;
         int segs = detail >= 2 ? 96 : 64;
         float spin = v.time * 0.0015F;

         for (int i = 0; i < rings; i++) {
            float r0 = R * (float)i / (float)rings;
            float r1 = R * (float)(i + 1) / (float)rings;
            float a0 = a * frostEdge(r0, R);
            float a1 = a * frostEdge(r1, R);

            for (int j = 0; j < segs; j++) {
               float t0 = (float) (Math.PI * 2) * (float)j / (float)segs;
               float t1 = (float) (Math.PI * 2) * (float)(j + 1) / (float)segs;
               frostVertex(m, vc, v, r0, t0, spin, a0);
               frostVertex(m, vc, v, r1, t0, spin, a1);
               frostVertex(m, vc, v, r1, t1, spin, a1);
               frostVertex(m, vc, v, r0, t1, spin, a0);
            }
         }
      }
   }

   private static float frostEdge(float r, float R) {
      return Mth.clamp((R - r) / 7.0F, 0.0F, 1.0F);
   }

   private static void frostVertex(Matrix4f m, VertexConsumer vc, IceAgeRenderer.View v, float r, float t, float spin, float a) {
      float x = Mth.cos(t) * r;
      float z = Mth.sin(t) * r;
      float y = v.ground.lift(x, z) + 0.07F;
      float cs = Mth.cos(spin);
      float sn = Mth.sin(spin);
      float u = (x * cs - z * sn) / 18.0F;
      float w = (x * sn + z * cs) / 18.0F;
      Sculpt.vertex(m, vc, new Vector3f(x, y, z), 0.78F, 0.9F, 1.0F, a, u, w);
   }

   private static void rings(Matrix4f m, VertexConsumer vc, IceAgeRenderer.View v, int detail, float dim) {
      int segs = detail >= 2 ? 128 : 72;
      float R = 56.0F * v.frost;
      if (v.frost < 0.999F && R > 1.0F) {
         Geo.band(m, vc, v.ground, 0.12F, Math.max(0.0F, R - 4.0F), R, segs, 0.0F, 1.0F, ICE[0], ICE[1], ICE[2], 0.0F, 0.3F * dim);
      }

      float sheen = v.frostAlpha * Math.min(1.0F, v.frost * 2.0F) * (0.06F + 0.18F * v.zero) * dim;
      if (sheen > 0.01F) {
         Geo.disc(m, vc, v.ground, 0.1F, R, segs, ICE[0], ICE[1], ICE[2], sheen, 0.0F);
      }

      float s = v.sinceShatter();
      if (s >= 0.0F && s < 40.0F) {
         float r = 67.200005F * (1.0F - (float)Math.exp((double)(-s / 6.0F)));
         float a = (1.0F - s / 40.0F) * dim;
         Geo.band(m, vc, v.ground, 0.2F, Math.max(0.0F, r - 10.0F), r, segs, 0.0F, 1.0F, ICE[0], ICE[1], ICE[2], 0.0F, 0.12F * a);
         Geo.band(m, vc, v.ground, 0.18F, Math.max(0.0F, r * 0.7F - 6.0F), r * 0.7F, segs, 0.0F, 1.0F, DEEP[0], DEEP[1], DEEP[2], 0.0F, 0.12F * a);
      }
   }

   private static void aurora(Matrix4f m, VertexConsumer vc, IceAgeRenderer.View v, float a) {
      float k = v.sky * a;
      if (!(k <= 0.01F)) {
         int n = 64;
         boolean pack = Shaders.packInUse();
         float reach = pack ? 0.5F : 1.0F;
         float lift = pack ? 0.6F : 1.0F;

         for (float[] c0 : CURTAINS) {
            float[] c = new float[]{c0[0], c0[1] * reach, c0[2], c0[3] * lift, c0[4] * lift, c0[5]};
            float unroll = v.sky;
            Vector3f[] hem = new Vector3f[n + 1];
            Vector3f[] top = new Vector3f[n + 1];
            float[] alpha = new float[n + 1];
            float[] u = new float[n + 1];

            for (int i = 0; i <= n; i++) {
               float s = (float)i / (float)n;
               float az = c[0] + (s - 0.5F) * c[2] + v.time * 8.0E-4F;
               float r = c[1] + 18.0F * Mth.sin(s * 6.0F + v.time * 0.018F + c[5]) + 6.0F * Mth.sin(s * 15.0F - v.time * 0.03F + c[5]);
               float h = c[3] + 5.0F * Mth.sin(s * 5.0F + v.time * 0.015F + c[5]);
               float lean = 10.0F;
               hem[i] = new Vector3f(v.base).add(Mth.cos(az) * r, h, Mth.sin(az) * r);
               top[i] = new Vector3f(v.base).add(Mth.cos(az) * (r + lean), h + c[4], Mth.sin(az) * (r + lean));
               float open = Mth.clamp((unroll * 0.5F - Math.abs(s - 0.5F)) / 0.08F, 0.0F, 1.0F);
               alpha[i] = k * open * (float)Math.pow((double)Mth.sin((float) Math.PI * s), 0.6) * (0.75F + 0.25F * Mth.sin(s * 11.0F + v.time * 0.05F + c[5]));
               u[i] = s * c[2] * c[1] / 90.0F + v.time * 0.0012F;
            }

            for (int i = 0; i < n; i++) {
               curtainQuad(m, vc, hem[i], top[i], hem[i + 1], top[i + 1], u[i], u[i + 1], alpha[i], alpha[i + 1]);
            }
         }
      }
   }

   private static void curtainQuad(Matrix4f m, VertexConsumer vc, Vector3f h0, Vector3f t0, Vector3f h1, Vector3f t1, float u0, float u1, float a0, float a1) {
      if (!(a0 <= 0.005F) || !(a1 <= 0.005F)) {
         float below = 0.25F;
         Vector3f b0 = new Vector3f(h0).sub(new Vector3f(t0).sub(h0).mul(below));
         Vector3f b1 = new Vector3f(h1).sub(new Vector3f(t1).sub(h1).mul(below));
         Sculpt.vertex(m, vc, b0, 1.0F, 1.0F, 1.0F, a0, u0, 1.0F);
         Sculpt.vertex(m, vc, b1, 1.0F, 1.0F, 1.0F, a1, u1, 1.0F);
         Sculpt.vertex(m, vc, t1, 1.0F, 1.0F, 1.0F, a1, u1, 0.0F);
         Sculpt.vertex(m, vc, t0, 1.0F, 1.0F, 1.0F, a0, u0, 0.0F);
      }
   }

   private static float funnel(float h) {
      return 5.0F + 31.0F * (float)Math.pow((double)h, 2.3);
   }

   private static Vector3f axis(IceAgeRenderer.View v, float h) {
      float loose = (1.0F - h) * (1.0F - h);
      return new Vector3f(v.base).add(4.0F * Mth.sin(v.time * 0.031F) * loose, 70.0F * h, 4.0F * Mth.cos(v.time * 0.023F) * loose);
   }

   private static void vortex(Matrix4f m, VertexConsumer vc, IceAgeRenderer.View v, boolean light, float dim) {
      if (!(v.vortex <= 0.01F)) {
         float squeeze = 1.0F - 0.9F * v.zero;
         float foot = 1.0F - v.descent;
         int n = 26;
         int crown = 8;

         for (int si = 0; si < SHELLS.length; si++) {
            float[] shell = SHELLS[si];
            float[] tint = SHELL_TINT[si];
            Sculpt.Ring[] rings = new Sculpt.Ring[n + 1 + crown];

            for (int k = 0; k <= n; k++) {
               float h = foot + (1.0F - foot) * (float)k / (float)n;
               float r = funnel(h) * shell[0] * squeeze;
               float spin = v.time * shell[2] * (1.0F + 2.5F * v.zero) + h * shell[1] * (float) Math.PI;
               float tip = Mth.clamp((float)k / (float)n / 0.18F, 0.0F, 1.0F);
               rings[k] = ring(axis(v, h), r, spin, h * 3.0F, tip);
            }

            float top = funnel(1.0F) * shell[0];

            for (int k = 1; k <= crown; k++) {
               float q = (float)k / (float)crown;
               float r = Mth.lerp(q, top, 58.0F * (0.7F + 0.3F * shell[0])) * squeeze;
               float spin = v.time * shell[2] * (1.0F + 2.5F * v.zero) + shell[1] * (float) Math.PI + q * 1.2F;
               Vector3f c = new Vector3f(v.base).add(0.0F, 70.0F + 7.0F * q * (2.0F - q), 0.0F);
               rings[n + k] = ring(c, r, spin, 3.0F + q * 2.0F, 1.0F - q * q);
            }

            float alpha = v.vortex;
            if (light) {
               Sculpt.energy(
                  m,
                  vc,
                  rings,
                  24,
                  3.0F,
                  -v.time * 0.012F,
                  1.02F,
                  DEEP[0],
                  DEEP[1],
                  DEEP[2],
                  0.0F,
                  shell[4] * 0.18F * dim * alpha * (1.0F + 2.5F * v.zero),
                  1.5F,
                  0.06F,
                  v.time
               );
            } else {
               Sculpt.energy(m, vc, rings, 28, 3.0F, -v.time * 0.01F, 1.0F, tint[0], tint[1], tint[2], shell[3] * alpha, shell[4] * alpha, 1.2F, 0.08F, v.time);
            }
         }
      }
   }

   private static Sculpt.Ring ring(Vector3f centre, float r, float spin, float vCoord, float alpha) {
      Vector3f side = new Vector3f(Mth.cos(spin), 0.0F, Mth.sin(spin));
      Vector3f up = new Vector3f(-Mth.sin(spin), 0.0F, Mth.cos(spin));
      return new Sculpt.Ring(centre, side, up, r, r, vCoord, alpha);
   }

   private static void flakes(Matrix4f m, VertexConsumer vc, IceAgeRenderer.View v, Vector3f right, Vector3f up, float dim, boolean soft, int from) {
      if (v.sinceShatter() >= 0.0F) {
         nova(m, vc, v, right, up, dim, soft, from);
      } else {
         float k = v.vortex * dim;
         if (!(k <= 0.01F)) {
            float squeeze = 1.0F - 0.9F * v.zero;
            float foot = 1.0F - v.descent;
            RandomSource random = RandomSource.create((long)v.seed * 131L + (long)from);

            for (int j = from; j < Math.min(600, from + 200); j++) {
               float h0 = random.nextFloat();
               float climb = 0.0015F + 0.002F * random.nextFloat();
               float spread = 0.55F + 0.75F * random.nextFloat();
               float a0 = random.nextFloat() * (float) (Math.PI * 2);
               float size = 0.22F + 0.4F * random.nextFloat();
               boolean blue = random.nextInt(3) == 0;
               float h = (h0 + v.time * climb) % 1.0F;
               if (!(h < foot)) {
                  float r = funnel(h) * spread * squeeze;
                  float ang = a0 + v.time * 1.3F / Math.max(3.0F, r) * (1.0F + 2.0F * v.zero);
                  Vector3f c = axis(v, h);
                  float x = c.x + Mth.cos(ang) * r;
                  float z = c.z + Mth.sin(ang) * r;
                  float alpha = k * 0.75F * Mth.clamp((h - foot) / 0.1F, 0.0F, 1.0F) * Mth.clamp((1.0F - h) / 0.08F, 0.0F, 1.0F);
                  float[] col = blue ? ICE : WHITE;
                  if (soft) {
                     Geo.billboard(m, vc, x, c.y, z, right, up, size * 1.6F, 0.0F, 0.0F, 1.0F, 1.0F, col[0], col[1], col[2], alpha);
                  } else {
                     Vector3f p = new Vector3f(x, c.y, z);
                     Vector3f rr = new Vector3f(right).mul(size * 0.4F);
                     Vector3f uu = new Vector3f(up).mul(size * 0.4F);
                     Sculpt.vertex(m, vc, new Vector3f(p).sub(rr), col[0], col[1], col[2], alpha, 0.5F, 0.5F);
                     Sculpt.vertex(m, vc, new Vector3f(p).sub(uu), col[0], col[1], col[2], alpha, 0.5F, 0.5F);
                     Sculpt.vertex(m, vc, new Vector3f(p).add(rr), col[0], col[1], col[2], alpha, 0.5F, 0.5F);
                     Sculpt.vertex(m, vc, new Vector3f(p).add(uu), col[0], col[1], col[2], alpha, 0.5F, 0.5F);
                  }
               }
            }
         }
      }
   }

   private static void nova(Matrix4f m, VertexConsumer vc, IceAgeRenderer.View v, Vector3f right, Vector3f up, float dim, boolean soft, int from) {
      float s = v.sinceShatter();
      float life = 56.0F;
      if (!(s > life)) {
         RandomSource random = RandomSource.create((long)v.seed * 389L + (long)from);
         float fade = Mth.clamp((life - s) / 20.0F, 0.0F, 1.0F) * dim;

         for (int j = from; j < Math.min(600, from + 200); j++) {
            float az = random.nextFloat() * (float) (Math.PI * 2);
            float el = 0.05F + random.nextFloat() * 1.2F;
            float speed = 2.5F + random.nextFloat() * 5.5F;
            float size = 0.25F + 0.5F * random.nextFloat();
            boolean blue = random.nextInt(3) == 0;
            float tau = 7.0F;
            float d = speed * tau * (1.0F - (float)Math.exp((double)(-s / tau)));
            float drop = 0.012F * s * s * (0.5F + random.nextFloat());
            float x = v.base.x + Mth.cos(az) * Mth.cos(el) * d;
            float y = v.base.y + 3.0F + Mth.sin(el) * d - drop;
            float z = v.base.z + Mth.sin(az) * Mth.cos(el) * d;
            float alpha = 0.8F * fade * Mth.clamp(s / 2.0F, 0.0F, 1.0F);
            float[] col = blue ? ICE : WHITE;
            if (soft) {
               Geo.billboard(m, vc, x, y, z, right, up, size * 1.8F, 0.0F, 0.0F, 1.0F, 1.0F, col[0], col[1], col[2], alpha);
            } else {
               Vector3f p = new Vector3f(x, y, z);
               Vector3f rr = new Vector3f(right).mul(size * 0.45F);
               Vector3f uu = new Vector3f(up).mul(size * 0.45F);
               Sculpt.vertex(m, vc, new Vector3f(p).sub(rr), col[0], col[1], col[2], alpha, 0.5F, 0.5F);
               Sculpt.vertex(m, vc, new Vector3f(p).sub(uu), col[0], col[1], col[2], alpha, 0.5F, 0.5F);
               Sculpt.vertex(m, vc, new Vector3f(p).add(rr), col[0], col[1], col[2], alpha, 0.5F, 0.5F);
               Sculpt.vertex(m, vc, new Vector3f(p).add(uu), col[0], col[1], col[2], alpha, 0.5F, 0.5F);
            }
         }
      }
   }

   private static void shards(Matrix4f m, VertexConsumer vc, IceAgeRenderer.View v, float a) {
      for (int i = 0; i < 28; i++) {
         float u = (v.t - (float)IceAgeEntity.shardStart(i)) / 10.0F;
         if (!(u < 0.0F) && !(u > 1.0F)) {
            Vector3f from = v.origin(i);
            Vector3f to = v.landing(i);
            int n = 8;
            Vector3f[] pts = new Vector3f[n];
            float[] half = new float[n];

            for (int k = 0; k < n; k++) {
               float q = (float)k / (float)(n - 1);
               float uu = Math.max(0.0F, u - 0.4F * (1.0F - q));
               pts[k] = new Vector3f(from).lerp(to, uu * uu * (0.4F + 0.6F * uu));
               half[k] = 0.5F + 2.4F * q * q;
            }

            Sculpt.ribbon(m, vc, pts, half, ICE[0], ICE[1], ICE[2], a, a);
         }
      }
   }

   private static void radials(Matrix4f m, VertexConsumer vc, IceAgeRenderer.View v, float a) {
      for (int i = 0; i < 28; i++) {
         float since = v.t - (float)(IceAgeEntity.shardStart(i) + 10);
         if (!(since < 0.0F) && !(since > 12.0F)) {
            Vector3f at = v.landing(i);
            float q = 1.0F - (float)Math.exp((double)(-since / 3.0F));
            float alpha = a * (1.0F - since / 12.0F);

            for (int k = 0; k < 7; k++) {
               float az = (float)k * (float) (Math.PI * 2) / 7.0F + (float)i;
               Vector3f dir = new Vector3f(Mth.cos(az), 0.7F + 0.3F * Mth.sin((float)k * 2.3F + (float)i), Mth.sin(az));
               streak(m, vc, at, dir, 2.0F + 6.0F * q, 2.2F, alpha);
            }
         }
      }

      float s = v.sinceShatter();
      if (s >= 0.0F && s < 30.0F) {
         float q = 1.0F - (float)Math.exp((double)(-s / 6.0F));
         float alpha = a * (1.0F - s / 30.0F);

         for (int k = 0; k < 30; k++) {
            float az = (float)k * (float) (Math.PI * 2) / 30.0F + 0.2F * Mth.sin((float)k * 1.7F);
            float r = 61.600002F * q;
            Vector3f dir = new Vector3f(Mth.cos(az), 0.12F + 0.08F * Mth.sin((float)k * 2.9F), Mth.sin(az));
            Vector3f from = new Vector3f(v.base).add(dir.x * r * 0.4F, 1.5F + 2.0F * Mth.abs(Mth.sin((float)k * 0.9F)), dir.z * r * 0.4F);
            streak(m, vc, from, dir, r * 0.6F, 5.5F, alpha * 0.7F);
         }
      }
   }

   private static void streak(Matrix4f m, VertexConsumer vc, Vector3f from, Vector3f dir, float length, float width, float a) {
      if (!(a <= 0.01F) && !(length <= 0.1F)) {
         Vector3f d = new Vector3f(dir).normalize();
         Vector3f[] pts = new Vector3f[]{
            new Vector3f(from), new Vector3f(from).add(new Vector3f(d).mul(length * 0.5F)), new Vector3f(from).add(new Vector3f(d).mul(length))
         };
         float[] half = new float[]{width * 0.2F, width * 0.6F, width};
         Sculpt.ribbon(m, vc, pts, half, ICE[0], ICE[1], ICE[2], a, a);
      }
   }

   private static void dome(Matrix4f m, VertexConsumer vc, IceAgeRenderer.View v, boolean light, float dim) {
      float s = v.sinceShatter();
      if (!(s < 0.0F) && !(s > 34.0F)) {
         float R = 64.4F * (1.0F - (float)Math.exp((double)(-s / 6.0F))) + 2.0F;
         float fade = 1.0F - s / 34.0F;
         fade *= fade;
         int n = 14;
         Sculpt.Ring[] rings = new Sculpt.Ring[n + 1];

         for (int k = 0; k <= n; k++) {
            float phi = (float) (Math.PI / 2) * (float)k / (float)n;
            float r = Math.max(0.05F, R * Mth.cos(phi));
            Vector3f c = new Vector3f(v.base).add(0.0F, R * 0.5F * Mth.sin(phi), 0.0F);
            rings[k] = ring(
               c, r, v.time * 0.02F, (float)k / (float)n * 2.0F, fade * Mth.clamp((float)(n - k) / 3.0F, 0.0F, 1.0F) * Mth.clamp((float)k / 3.0F, 0.0F, 1.0F)
            );
         }

         if (light) {
            Sculpt.energy(m, vc, rings, 48, 4.0F, -s * 0.05F, 1.01F, DEEP[0], DEEP[1], DEEP[2], 0.0F, 0.25F * dim, 1.0F, 0.05F, v.time);
            pillar(m, vc, v, true, dim);
         } else {
            Sculpt.energy(m, vc, rings, 48, 4.0F, -s * 0.04F, 1.0F, ICE[0], ICE[1], ICE[2], 0.06F, 0.45F, 0.9F, 0.06F, v.time);
            pillar(m, vc, v, false, 1.0F);
         }
      }
   }

   private static void pillar(Matrix4f m, VertexConsumer vc, IceAgeRenderer.View v, boolean light, float dim) {
      float s = v.sinceShatter();
      if (!(s < 0.0F) && !(s > 30.0F)) {
         float height = 125.0F * (1.0F - (float)Math.exp((double)(-s / 4.0F))) + 4.0F;
         float width = 4.0F + 12.0F * (1.0F - (float)Math.exp((double)(-s / 5.0F)));
         float fade = 1.0F - s / 30.0F;
         fade *= fade;
         int n = 18;
         Sculpt.Ring[] rings = new Sculpt.Ring[n + 1];

         for (int k = 0; k <= n; k++) {
            float h = (float)k / (float)n;
            float r = width * (0.7F + 0.9F * h) * (1.0F + 0.15F * Mth.sin(h * 9.0F - s * 0.6F));
            float a = fade * Mth.clamp(h / 0.08F, 0.0F, 1.0F) * Mth.clamp((1.0F - h) / 0.3F, 0.0F, 1.0F);
            rings[k] = ring(new Vector3f(v.base).add(0.0F, height * h, 0.0F), r, s * 0.15F + h * 2.0F, h * 4.0F, a);
         }

         if (light) {
            Sculpt.energy(m, vc, rings, 32, 3.0F, -s * 0.12F, 1.02F, DEEP[0], DEEP[1], DEEP[2], 0.05F * dim, 0.4F * dim, 1.2F, 0.1F, v.time);
         } else {
            Sculpt.energy(m, vc, rings, 32, 3.0F, -s * 0.1F, 1.0F, PALE[0], PALE[1], PALE[2], 0.25F, 0.8F, 1.2F, 0.1F, v.time);
         }
      }
   }

   private static void fragments(Matrix4f m, VertexConsumer vc, IceAgeRenderer.View v, float a) {
      float s = v.sinceShatter();
      if (!(s < 0.0F) && !(s > 36.0F)) {
         RandomSource random = RandomSource.create((long)v.seed * 977L);
         float alpha = a * Mth.clamp((36.0F - s) / 14.0F, 0.0F, 1.0F);

         for (int j = 0; j < 70; j++) {
            float az = random.nextFloat() * (float) (Math.PI * 2);
            float r0 = 39.2F * (float)Math.sqrt((double)random.nextFloat());
            float out = 0.5F + random.nextFloat() * 0.9F;
            float rise = 1.4F + random.nextFloat() * 1.8F;
            float delay = r0 / 39.2F * 6.0F;
            float tt = s - delay;
            if (!(tt <= 0.0F)) {
               Vector3f start = new Vector3f(v.base).add(Mth.cos(az) * r0, 0.5F, Mth.sin(az) * r0);
               int n = 6;
               Vector3f[] pts = new Vector3f[n];
               float[] half = new float[n];

               for (int k = 0; k < n; k++) {
                  float q = (float)k / (float)(n - 1);
                  float ts = Math.max(0.0F, tt - 5.0F * (1.0F - q));
                  float d = out * ts;
                  float y = rise * ts - 0.045F * ts * ts;
                  pts[k] = new Vector3f(start).add(Mth.cos(az) * d, Math.max(0.0F, y), Mth.sin(az) * d);
                  half[k] = 0.4F + 1.6F * q;
               }

               Sculpt.ribbon(m, vc, pts, half, ICE[0], ICE[1], ICE[2], alpha * 0.55F, alpha * 0.55F);
            }
         }
      }
   }

   private static void flashes(Matrix4f m, VertexConsumer vc, IceAgeRenderer.View v, Vector3f right, Vector3f up, float dim) {
      for (int i = 0; i < 28; i++) {
         float u = (v.t - (float)IceAgeEntity.shardStart(i)) / 10.0F;
         if (!(u < -0.6F) && !(u > 1.0F)) {
            if (u < 0.0F) {
               float k = (1.0F - u / -0.6F) * dim;
               Geo.discIn(m, vc, v.origin(i), right, up, 4.0F * k + 0.5F, 20, ICE[0], ICE[1], ICE[2], 0.35F * k, 0.0F);
            }

            float since = v.t - (float)(IceAgeEntity.shardStart(i) + 10);
            if (since >= 0.0F && since < 16.0F) {
               float q = 1.0F - (float)Math.exp((double)(-since / 3.5F));
               float r = 1.0F + 9.099999F * q;
               float a = (1.0F - since / 16.0F) * dim;
               Vector3f at = new Vector3f(v.landing(i)).add(0.0F, 0.25F, 0.0F);
               Vector3f ex = new Vector3f(1.0F, 0.0F, 0.0F);
               Vector3f ez = new Vector3f(0.0F, 0.0F, 1.0F);
               Geo.ringIn(m, vc, at, ex, ez, Math.max(0.0F, r - 3.0F), r, 40, 0.0F, 1.0F, ICE[0], ICE[1], ICE[2], 0.0F, 0.45F * a);
               Geo.discIn(m, vc, at, ex, ez, r * 0.8F, 32, ICE[0], ICE[1], ICE[2], 0.25F * a, 0.0F);
            }

            if (since >= 0.0F && since < 10.0F) {
               float k = (float)Math.exp((double)(-since / 2.5F)) * dim;
               Geo.discIn(
                  m, vc, new Vector3f(v.landing(i)).add(0.0F, 1.2F, 0.0F), right, up, 4.0F + since * 1.2F, 24, PALE[0], PALE[1], PALE[2], 0.5F * k, 0.0F
               );
            }
         }
      }

      if (v.zero > 0.01F && v.t < 244.0F) {
         Vector3f c = new Vector3f(v.base).add(0.0F, 3.0F, 0.0F);
         float k = v.zero * dim;
         Geo.discIn(m, vc, c, right, up, 2.0F + 9.0F * v.zero, 32, PALE[0], PALE[1], PALE[2], 0.9F * k, 0.0F);
         Geo.discIn(m, vc, c, right, up, 1.0F + 3.0F * v.zero, 24, WHITE[0], WHITE[1], WHITE[2], k, 0.0F);
      }

      float s = v.sinceShatter();
      if (s >= 0.0F && s < 24.0F) {
         Vector3f c = new Vector3f(v.base).add(0.0F, 4.0F, 0.0F);
         float k = (float)Math.exp((double)(-s / 4.0F)) * dim;
         Geo.discIn(m, vc, c, right, up, 14.0F + s * 3.0F, 40, ICE[0], ICE[1], ICE[2], 0.6F * k, 0.0F);
         Geo.discIn(m, vc, c, right, up, 6.0F + s, 32, PALE[0], PALE[1], PALE[2], 0.7F * k, 0.0F);
      }
   }

   private interface Pass {
      void draw(VertexConsumer var1);
   }

   private static final class View {
      final IceAgeEntity storm;
      final float t;
      final float time;
      final Vector3f base;
      final int seed;
      final float sky;
      final float vortex;
      final float descent;
      final float zero;
      final float frost;
      final float frostAlpha;
      final float flare;
      final Terrain ground;
      final float[] landings;

      View(IceAgeEntity storm, float partial, Vec3 eye, ClientLevel level) {
         this.storm = storm;
         this.t = storm.age(partial);
         this.time = (float)storm.tickCount + partial;
         this.base = new Vector3f((float)(storm.getX() - eye.x), (float)(storm.getY() - eye.y), (float)(storm.getZ() - eye.z));
         this.seed = storm.seed();
         this.sky = IceAgeEntity.sky(this.t);
         this.vortex = IceAgeEntity.vortex(this.t);
         this.descent = IceAgeEntity.descent(this.t);
         this.zero = IceAgeEntity.zero(this.t);
         this.frost = IceAgeEntity.frost(this.t);
         this.frostAlpha = IceAgeEntity.frostAlpha(this.t);
         float since = this.t - 244.0F;
         this.flare = since < 0.0F ? this.zero : (float)Math.exp((double)(-since / 14.0F));
         this.ground = IceAgeRenderer.GROUND.computeIfAbsent(storm, s -> Terrain.sample(level, s.position(), 60.0F));
         this.landings = IceAgeRenderer.LANDINGS.computeIfAbsent(storm, s -> IceAgeRenderer.landings(level, s));
      }

      Vector3f landing(int i) {
         Vec3 off = IceAgeEntity.shardTarget(this.seed, i);
         return new Vector3f(this.base).add((float)off.x, this.landings[i], (float)off.z);
      }

      Vector3f origin(int i) {
         Vec3 off = IceAgeEntity.shardOrigin(this.seed, i);
         return new Vector3f(this.base).add((float)off.x, (float)off.y, (float)off.z);
      }

      float sinceShatter() {
         return this.t - 244.0F;
      }
   }
}
