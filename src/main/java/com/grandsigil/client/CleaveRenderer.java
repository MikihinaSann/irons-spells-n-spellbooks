package com.grandsigil.client;

import com.grandsigil.GrandSigil;
import com.grandsigil.GsPresence;
import com.grandsigil.entity.CleaveEntity;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
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

public final class CleaveRenderer {
   private static final ResourceLocation VOID = GrandSigil.id("textures/entity/void_rift.png");
   private static final ResourceLocation EDGE = GrandSigil.id("textures/entity/rift_edge.png");
   private static final ResourceLocation FLOW = GrandSigil.id("textures/entity/ender_flow.png");
   private static final ResourceLocation STREAK = GrandSigil.id("textures/entity/cut_streak.png");
   private static final float[] VIOLET = new float[]{0.72F, 0.45F, 1.0F};
   private static final float[] WHITE = new float[]{1.0F, 0.96F, 1.0F};
   private static final int STATIONS = 56;
   private static final int HAIRLINES = 36;
   private static final int WISPS = 44;
   private static final double VIEW = 340.0;
   private static final ByteBufferBuilder BUILDER = new ByteBufferBuilder(524288);
   private static final BufferSource BUFFERS = MultiBufferSource.immediate(BUILDER);
   private static final Vector3f NONE = new Vector3f();
   private static final float[] ZENITH = new float[]{0.04F, 0.02F, 0.1F};
   private static final float[] HORIZON = new float[]{0.16F, 0.08F, 0.26F};

   private CleaveRenderer() {
   }

   public static void render(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_TRANSLUCENT_BLOCKS && GsPresence.any(19)) {
         Minecraft minecraft = Minecraft.getInstance();
         ClientLevel level = minecraft.level;
         if (level != null) {
            Camera camera = event.getCamera();
            Vec3 eye = camera.getPosition();
            float partial = event.getPartialTick();
            List<CleaveRenderer.View> views = new ArrayList<>();

            for (Entity entity : level.entitiesForRendering()) {
               if (entity instanceof CleaveEntity) {
                  CleaveEntity cleave = (CleaveEntity)entity;
                  if (cleave.distanceToSqr(eye.x, eye.y, eye.z) < 115600.0) {
                     views.add(new CleaveRenderer.View(cleave, partial, eye));
                  }
               }
            }

            if (!views.isEmpty()) {
               PoseStack pose = event.getPoseStack();
               pose.pushPose();
               Matrix4f m = pose.last().pose();
               boolean pack = Shaders.packInUse();
               float dim = pack ? 0.7F : 1.0F;
               Vector3f right = new Vector3f(camera.getLeftVector()).mul(-1.0F);
               Vector3f up = new Vector3f(camera.getUpVector());
               float dusk = dusk(level, eye, partial);
               if (dusk > 0.01F && !pack) {
                  float far = Mth.clamp(minecraft.gameRenderer.getRenderDistance() * 0.9F, 96.0F, 300.0F);
                  batch(GsRenderTypes.glass(), vc -> duskDome(m, vc, far, dusk));
                  batch(GsRenderTypes.veil(), vc -> shade(m, vc, dusk));
               }

               batch(GsRenderTypes.matter(VOID), vc -> views.forEach(v -> tear(m, vc, v)));
               batch(GsRenderTypes.matter(FLOW), vc -> views.forEach(v -> wisps(m, vc, v, 0.9F)));
               batch(GsRenderTypes.glow(FLOW), vc -> views.forEach(v -> wisps(m, vc, v, 0.55F * dim)));
               batch(GsRenderTypes.glow(EDGE), vc -> views.forEach(v -> lips(m, vc, v, dim)));
               batch(GsRenderTypes.glow(STREAK), vc -> views.forEach(v -> {
                     trace(m, vc, v, dim);
                     stroke(m, vc, v, dim);
                     hairlines(m, vc, v, dim);
                     snap(m, vc, v, dim);
                  }));
               batch(GsRenderTypes.glow(), vc -> views.forEach(v -> flashes(m, vc, v, right, up, dim)));
               pose.popPose();
               BUFFERS.endBatch();
            }
         }
      }
   }

   private static void batch(RenderType type, CleaveRenderer.Pass pass) {
      VertexConsumer vc = BUFFERS.getBuffer(type);
      pass.draw(vc);
      BUFFERS.endBatch(type);
   }

   private static void strip(
      Matrix4f m,
      VertexConsumer vc,
      CleaveRenderer.View v,
      float s0,
      float s1,
      CleaveRenderer.Width half,
      Vector3f offset,
      float r,
      float g,
      float b,
      float a,
      float vScale,
      float vScroll
   ) {
      if (!(s1 - s0 < 0.002F) && !(a <= 0.01F)) {
         int n = Math.max(2, (int)(56.0F * (s1 - s0) / 2.0F) + 2);
         Vector3f[] pts = new Vector3f[n];
         float[] hw = new float[n];

         for (int k = 0; k < n; k++) {
            float s = Mth.lerp((float)k / (float)(n - 1), s0, s1);
            pts[k] = v.at(s).add(offset);
            hw[k] = half.at(s);
         }

         Vector3f[] side = new Vector3f[n];

         for (int k = 0; k < n; k++) {
            Vector3f run = new Vector3f(pts[Math.min(n - 1, k + 1)]).sub(pts[Math.max(0, k - 1)]);
            Vector3f sd = run.cross(new Vector3f(pts[k]).negate());
            if (sd.lengthSquared() < 1.0E-8F) {
               sd.set(0.0F, 1.0F, 0.0F);
            }

            side[k] = sd.normalize().mul(hw[k]);
         }

         float len = 100.0F * (s1 - s0) / 2.0F;

         for (int k = 0; k + 1 < n; k++) {
            float v0 = vScroll + vScale * len * (float)k / (float)(n - 1);
            float v1 = vScroll + vScale * len * (float)(k + 1) / (float)(n - 1);
            Sculpt.vertex(m, vc, new Vector3f(pts[k]).sub(side[k]), r, g, b, a, 0.0F, v0);
            Sculpt.vertex(m, vc, new Vector3f(pts[k]).add(side[k]), r, g, b, a, 1.0F, v0);
            Sculpt.vertex(m, vc, new Vector3f(pts[k + 1]).add(side[k + 1]), r, g, b, a, 1.0F, v1);
            Sculpt.vertex(m, vc, new Vector3f(pts[k + 1]).sub(side[k + 1]), r, g, b, a, 0.0F, v1);
         }
      }
   }

   private static float mouth(CleaveRenderer.View v, float s) {
      float ripple = 1.0F + 0.08F * Mth.sin(s * 9.0F + v.time * 0.2F) + 0.05F * Mth.sin(s * 23.0F - v.time * 0.31F);
      return 8.0F * CleaveEntity.profile(s) * v.open * ripple;
   }

   private static void tear(Matrix4f m, VertexConsumer vc, CleaveRenderer.View v) {
      if (!(v.open <= 0.005F)) {
         strip(m, vc, v, -1.0F, 1.0F, s -> mouth(v, s), NONE, 1.0F, 1.0F, 1.0F, 1.0F, 0.05F, v.time * 0.004F);
      }
   }

   private static void lips(Matrix4f m, VertexConsumer vc, CleaveRenderer.View v, float dim) {
      if (!(v.open <= 0.005F)) {
         float flicker = 0.85F + 0.15F * Mth.sin(v.time * 0.9F) * Mth.sin(v.time * 0.37F);
         strip(m, vc, v, -1.0F, 1.0F, s -> mouth(v, s) * 1.3F + 0.6F, NONE, 1.0F, 1.0F, 1.0F, 0.95F * dim * flicker, 0.03F, -v.time * 0.03F);
         strip(m, vc, v, -1.0F, 1.0F, s -> mouth(v, s) * 1.6F + 1.5F, NONE, VIOLET[0], VIOLET[1], VIOLET[2], 0.35F * dim, 0.02F, v.time * 0.02F);
      }
   }

   private static void trace(Matrix4f m, VertexConsumer vc, CleaveRenderer.View v, float dim) {
      if (!(v.t >= 21.0F)) {
         float e = CleaveEntity.smooth(v.t / 15.0F);
         float flicker = 0.6F + 0.4F * Mth.sin(v.time * 1.7F);
         line(m, vc, v.at(-e), v.at(e), 1.4F, 1.0F, 1.0F, 1.0F, 0.8F * flicker * dim);
      }
   }

   private static void stroke(Matrix4f m, VertexConsumer vc, CleaveRenderer.View v, float dim) {
      float since = v.t - 20.0F;
      if (!(since < 0.0F) && !(since > 14.0F)) {
         float head = Math.min(1.0F, since / 3.0F) * 2.0F - 1.0F;
         float k = since < 3.0F ? 1.0F : (float)Math.exp((double)(-(since - 3.0F) / 3.0F));
         line(m, vc, v.at(-1.0F), v.at(head), 2.4F, 1.0F, 1.0F, 1.0F, k * dim);
         line(m, vc, v.at(-1.0F), v.at(head), 9.0F, VIOLET[0], VIOLET[1], VIOLET[2], 0.5F * k * dim);
      }
   }

   private static void hairlines(Matrix4f m, VertexConsumer vc, CleaveRenderer.View v, float dim) {
      flurry(m, vc, v, v.t - 20.0F, (long)v.seed * 173L, 36, 1.0F, dim);
      flurry(m, vc, v, v.t - 91.0F + 1.0F, (long)v.seed * 331L, 48, 1.4F, dim);
   }

   private static void flurry(Matrix4f m, VertexConsumer vc, CleaveRenderer.View v, float since, long seed, int count, float spread, float dim) {
      if (!(since < 0.0F) && !(since > 16.0F)) {
         RandomSource random = RandomSource.create(seed);

         for (int i = 0; i < count; i++) {
            float s = random.nextFloat() * 1.8F - 0.9F;
            float at = 1.0F + random.nextFloat() * 11.0F;
            float len = (8.0F + random.nextFloat() * 18.0F) * spread;
            float ang = (random.nextFloat() - 0.5F) * 2.4F;
            float off = (random.nextFloat() - 0.5F) * 16.0F * spread;
            float lean = (random.nextFloat() - 0.5F) * 0.8F;
            float age = since - at;
            if (!(age < 0.0F) && !(age > 3.5F)) {
               float k = (1.0F - age / 3.5F) * dim;
               Vector3f c = v.at(s).add(v.across().mul(off * 0.4F)).add(0.0F, off * 0.5F, 0.0F);
               Vector3f along = new Vector3f(Mth.cos(v.heading), 0.0F, Mth.sin(v.heading));
               Vector3f dir = new Vector3f(along).mul(Mth.cos(ang)).add(0.0F, Mth.sin(ang), 0.0F).add(v.across().mul(lean)).normalize();
               Vector3f a = new Vector3f(c).sub(new Vector3f(dir).mul(len * 0.5F));
               Vector3f b = new Vector3f(c).add(new Vector3f(dir).mul(len * 0.5F));
               float grow = Math.min(1.0F, age / 0.8F);
               Vector3f bb = new Vector3f(a).lerp(b, grow);
               line(m, vc, a, bb, 1.4F + 0.6F * age, 1.0F, 1.0F, 1.0F, k);
            }
         }
      }
   }

   private static void line(Matrix4f m, VertexConsumer vc, Vector3f a, Vector3f b, float half, float r, float g, float bl, float alpha) {
      int n = 12;
      Vector3f run = new Vector3f(b).sub(a);

      for (int k = 0; k < n; k++) {
         float q0 = (float)k / (float)n;
         float q1 = (float)(k + 1) / (float)n;
         Vector3f p0 = new Vector3f(a).lerp(b, q0);
         Vector3f p1 = new Vector3f(a).lerp(b, q1);
         Vector3f sd = new Vector3f(run).cross(new Vector3f(p0).negate());
         if (sd.lengthSquared() < 1.0E-8F) {
            sd.set(0.0F, 1.0F, 0.0F);
         }

         float taper0 = 1.0F;
         float taper1 = 1.0F;
         Vector3f s0 = new Vector3f(sd).normalize().mul(half * taper0);
         Vector3f s1 = new Vector3f(sd).normalize().mul(half * taper1);
         Sculpt.vertex(m, vc, new Vector3f(p0).sub(s0), r, g, bl, alpha, 0.0F, q0);
         Sculpt.vertex(m, vc, new Vector3f(p0).add(s0), r, g, bl, alpha, 1.0F, q0);
         Sculpt.vertex(m, vc, new Vector3f(p1).add(s1), r, g, bl, alpha, 1.0F, q1);
         Sculpt.vertex(m, vc, new Vector3f(p1).sub(s1), r, g, bl, alpha, 0.0F, q1);
      }
   }

   private static void wisps(Matrix4f m, VertexConsumer vc, CleaveRenderer.View v, float a) {
      if (!(v.open <= 0.05F)) {
         RandomSource random = RandomSource.create((long)v.seed * 911L);

         for (int j = 0; j < 44; j++) {
            float s = random.nextFloat() * 1.7F - 0.85F;
            float side = random.nextBoolean() ? 1.0F : -1.0F;
            float dist = 8.0F + random.nextFloat() * 14.0F;
            float lift = (random.nextFloat() - 0.5F) * 16.0F;
            float speed = 0.012F + random.nextFloat() * 0.015F;
            float phase = random.nextFloat();
            float swirl = (random.nextFloat() - 0.5F) * 10.0F;
            Vector3f mouthAt = v.at(s);
            Vector3f from = new Vector3f(mouthAt).add(v.across().mul(side * dist)).add(0.0F, lift, 0.0F);
            Vector3f ctrl = new Vector3f(mouthAt)
               .lerp(from, 0.5F)
               .add(new Vector3f(Mth.cos(v.heading), 0.0F, Mth.sin(v.heading)).mul(swirl))
               .add(0.0F, lift * 0.3F, 0.0F);
            float q = (v.time * speed + phase) % 1.0F;
            int n = 10;
            Vector3f[] pts = new Vector3f[n];
            float[] half = new float[n];

            for (int i = 0; i < n; i++) {
               float u = Mth.clamp(q - 0.4F + 0.4F * (float)i / (float)(n - 1), 0.0F, 1.0F);
               float w = u * u * (3.0F - 2.0F * u);
               Vector3f p0 = new Vector3f(from).lerp(ctrl, w);
               Vector3f p1 = new Vector3f(ctrl).lerp(mouthAt, w);
               pts[i] = p0.lerp(p1, w);
               half[i] = (0.3F + 1.6F * (float)i / (float)(n - 1)) * (1.0F - 0.5F * u);
            }

            float fade = a * v.open * Mth.sin((float) Math.PI * q);
            Sculpt.ribbon(m, vc, pts, half, 1.0F, 1.0F, 1.0F, fade * 0.2F, fade);
         }
      }
   }

   private static void snap(Matrix4f m, VertexConsumer vc, CleaveRenderer.View v, float dim) {
      float s = v.t - 91.0F;
      if (!(s < -2.0F) && !(s > 24.0F)) {
         float k = s < 0.0F ? 1.0F : (float)Math.exp((double)(-s / 4.0F));
         line(m, vc, v.at(-1.0F), v.at(1.0F), 3.0F, 1.0F, 1.0F, 1.0F, k * dim);
         if (s >= 0.0F) {
            float scar = (1.0F - Mth.clamp(s / 30.0F, 0.0F, 1.0F)) * 0.5F * dim;
            line(m, vc, v.at(-1.0F), v.at(1.0F), 1.2F, VIOLET[0], VIOLET[1], VIOLET[2], scar);
         }
      }
   }

   private static void flashes(Matrix4f m, VertexConsumer vc, CleaveRenderer.View v, Vector3f right, Vector3f up, float dim) {
      float since = v.t - 20.0F;
      float s = v.t - 91.0F;
      if (s >= 0.0F && s < 12.0F && !Shaders.packInUse()) {
         float k = (float)Math.exp((double)(-s / 3.0F)) * dim;
         Geo.discIn(m, vc, v.at(0.0F), right, up, 22.0F + s * 3.0F, 32, VIOLET[0], VIOLET[1], VIOLET[2], 0.22F * k, 0.0F);
      }
   }

   public static float dusk(ClientLevel level, Vec3 eye, float partial) {
      if (!GsPresence.any(19)) {
         return 0.0F;
      } else {
         float best = 0.0F;

         for (Entity entity : level.entitiesForRendering()) {
            if (entity instanceof CleaveEntity cleave) {
               double dx = cleave.getX() - eye.x;
               double dz = cleave.getZ() - eye.z;
               float near = Mth.clamp(1.0F - (float)(Math.sqrt(dx * dx + dz * dz) - 90.0) / 120.0F, 0.0F, 1.0F);
               best = Math.max(best, 0.8F * CleaveEntity.presence(cleave.age(partial)) * near);
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
         k * Mth.lerp(q, 0.72F, 0.88F),
         0.5F,
         0.5F
      );
   }

   private static void shade(Matrix4f m, VertexConsumer vc, float k) {
      float a = 0.38F * k;
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
      Sculpt.vertex(
         m, vc, new Vector3f(Mth.cos(azimuth) * c * 1.5F, Mth.sin(elevation) * 1.5F, Mth.sin(azimuth) * c * 1.5F), 0.05F, 0.02F, 0.12F, a, 0.5F, 0.5F
      );
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
            event.setFarPlaneDistance(Mth.lerp(k, event.getFarPlaneDistance(), event.getFarPlaneDistance() * 0.6F));
            event.setNearPlaneDistance(Mth.lerp(k, event.getNearPlaneDistance(), 0.0F));
            event.setCanceled(true);
         }
      }
   }

   private interface Pass {
      void draw(VertexConsumer var1);
   }

   private static final class View {
      final CleaveEntity cleave;
      final float t;
      final float time;
      final Vector3f base;
      final float heading;
      final float open;
      final int seed;

      View(CleaveEntity cleave, float partial, Vec3 eye) {
         this.cleave = cleave;
         this.t = cleave.age(partial);
         this.time = (float)cleave.tickCount + partial;
         this.base = new Vector3f((float)(cleave.getX() - eye.x), (float)(cleave.getY() - eye.y), (float)(cleave.getZ() - eye.z));
         this.heading = cleave.heading();
         this.open = CleaveEntity.open(this.t);
         this.seed = cleave.seed();
      }

      Vector3f at(float s) {
         Vec3 p = CleaveEntity.along(this.heading, s);
         return new Vector3f(this.base).add((float)p.x, (float)p.y, (float)p.z);
      }

      Vector3f across() {
         return new Vector3f(-Mth.sin(this.heading), 0.0F, Mth.cos(this.heading));
      }
   }

   private interface Width {
      float at(float var1);
   }
}
