package com.grandsigil.client;

import com.grandsigil.GrandSigil;
import com.grandsigil.GsPresence;
import com.grandsigil.config.GsConfig;
import com.grandsigil.entity.SigilEntity;
import com.grandsigil.entity.Terrain;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class SigilRenderer {
   private static final ResourceLocation RUNES = GrandSigil.id("textures/entity/runes.png");
   private static final ResourceLocation SCRIPT = GrandSigil.id("textures/entity/script.png");
   private static final ResourceLocation FLARE = GrandSigil.id("textures/entity/flare.png");
   private static final double VIEW = 192.0;
   private static final float RISE = 0.36F;
   private static final float BASE = 0.55F;
   private static final float FOCUS = 0.12F;
   private static final ByteBufferBuilder BUILDER = new ByteBufferBuilder(8192);
   private static final BufferSource BUFFERS = MultiBufferSource.immediate(BUILDER);

   private SigilRenderer() {
   }

   public static void render(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_TRANSLUCENT_BLOCKS) {
         if (GsPresence.any(0)) {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            if (level != null) {
               Camera camera = event.getCamera();
               Vec3 eye = camera.getPosition();
               float partial = event.getPartialTick();
               PoseStack pose = event.getPoseStack();
               boolean any = false;

               for (Entity entity : level.entitiesForRendering()) {
                  if (entity instanceof SigilEntity) {
                     SigilEntity sigil = (SigilEntity)entity;
                     if (!(sigil.distanceToSqr(eye.x, eye.y, eye.z) > 36864.0)) {
                        any = true;
                        draw(pose, sigil, partial, eye, camera);
                     }
                  }
               }

               if (any) {
                  BUFFERS.endBatch();
               }
            }
         }
      }
   }

   private static void draw(PoseStack pose, SigilEntity sigil, float partial, Vec3 eye, Camera camera) {
      int detail = (Integer)GsConfig.CLIENT.detail.get();
      int stage = sigil.stage();
      float sa = (float)sigil.stageAge() + partial;
      float age = (float)sigil.tickCount + partial;
      int lit = sigil.runes();
      int window = sigil.window();
      boolean charging = stage == 0;
      float fill = (float)lit / 12.0F;
      float r = sigil.radius();
      float drawn = charging ? easeOut(Mth.clamp(sa / 8.0F, 0.0F, 1.0F)) : 1.0F;

      float wind = switch (stage) {
         case 0 -> 0.0F;
         case 1 -> Mth.clamp(sa / 40.0F, 0.0F, 1.0F);
         default -> 1.0F;
      };
      float pull = wind * wind * wind;
      float rig = stage == 2 ? Math.max(0.0F, 1.0F - sa / 6.0F) : 1.0F;
      float spin = Mth.lerp(partial, sigil.spinOld, sigil.spin);
      float pulse = 0.86F + 0.14F * Mth.sin(age * 0.22F);
      float toWhite = Math.min(1.0F, fill * 0.55F + wind * 0.8F);
      float cr = 1.0F;
      float cg = Mth.lerp(toWhite, 0.74F, 0.97F);
      float cb = Mth.lerp(toWhite, 0.26F, 0.9F);
      float bright = Mth.clamp((0.55F + 0.3F * fill) * pulse * (1.0F + 1.6F * wind), 0.0F, 1.0F);
      Terrain ground = sigil.ground;
      ground.weight(1.0F);
      float focusY = r * 0.12F + 1.2F;
      pose.pushPose();
      pose.translate(sigil.getX() - eye.x, sigil.getY() - eye.y, sigil.getZ() - eye.z);
      Matrix4f m = pose.last().pose();
      Vector3f right = new Vector3f(camera.getLeftVector()).mul(-1.0F);
      Vector3f up = new Vector3f(camera.getUpVector());
      Vector3f viewer = new Vector3f((float)(eye.x - sigil.getX()), (float)(eye.y - sigil.getY()), (float)(eye.z - sigil.getZ()));
      VertexConsumer floor = BUFFERS.getBuffer(GsRenderTypes.floor());
      ward(m, floor, ground, r, spin, drawn, fill, bright, cr, cg, cb, detail);
      seal(m, floor, ground, r, spin, drawn, bright, cr, cg, cb, detail);
      BUFFERS.endBatch(GsRenderTypes.floor());
      VertexConsumer script = BUFFERS.getBuffer(GsRenderTypes.floor(SCRIPT));
      Geo.script(m, script, ground, 0.031F, r * 0.845F, r * 0.918F, seg(96, detail), spin, drawn, 12.0F, -spin * 0.5F, cr, cg, cb, bright * 0.95F);
      BUFFERS.endBatch(GsRenderTypes.floor(SCRIPT));
      VertexConsumer glow = BUFFERS.getBuffer(GsRenderTypes.glow());
      if (rig > 0.01F) {
         float crown = stack(m, glow, viewer, r, focusY, spin, sa, age, lit, window, charging, pull, bright * rig, cr, cg, cb, detail);
         rails(m, glow, ground, viewer, r, crown, spin, drawn, pull, bright * rig, cr, cg, cb, detail);
      }

      if (stage == 2) {
         burst(m, glow, ground, r, focusY, sa, sigil.perfect(), detail);
      }

      BUFFERS.endBatch(GsRenderTypes.glow());
      VertexConsumer runes = BUFFERS.getBuffer(GsRenderTypes.glow(RUNES));
      seats(m, runes, ground, r, spin, drawn, lit, bright * rig, cr, cg, cb);
      risen(m, runes, ground, right, up, r, spin, age, lit, bright * rig, cr, cg, cb);
      BUFFERS.endBatch(GsRenderTypes.glow(RUNES));
      VertexConsumer flare = BUFFERS.getBuffer(GsRenderTypes.glow(FLARE));
      core(m, flare, right, up, r, focusY, wind, stage, sa, sigil.perfect());
      BUFFERS.endBatch(GsRenderTypes.glow(FLARE));
      VertexConsumer late = BUFFERS.getBuffer(GsRenderTypes.floor());
      if (stage == 1) {
         funnel(m, late, ground, r, wind, cr, cg, cb);
      }

      if (stage == 2) {
         shock(m, late, ground, r, sa, sigil.perfect());
      }

      BUFFERS.endBatch(GsRenderTypes.floor());
      pose.popPose();
   }

   private static void ward(
      Matrix4f m, VertexConsumer vc, Terrain ground, float r, float spin, float drawn, float fill, float a, float cr, float cg, float cb, int detail
   ) {
      float rr = r * Mth.lerp(fill, 1.42F, 1.12F);
      int segments = seg(96, detail);
      Geo.band(m, vc, ground, 0.02F, rr * 0.992F, rr, segments, -spin * 0.4F, drawn, cr, cg, cb, a * 0.45F, a * 0.12F);
      if (detail >= 1) {
         Geo.ticks(m, vc, ground, 0.02F, rr * 0.955F, rr * 0.978F, rr * 0.995F, detail >= 2 ? 72 : 36, 6, -spin * 0.4F, r * 0.005F, drawn, cr, cg, cb, a * 0.3F);
      }
   }

   private static void seal(Matrix4f m, VertexConsumer vc, Terrain ground, float r, float spin, float drawn, float a, float cr, float cg, float cb, int detail) {
      int segments = seg(96, detail);
      float y = 0.03F;
      float w = r * 0.006F;
      Geo.band(m, vc, ground, y, r * 0.965F, r, segments, spin, drawn, cr, cg, cb, a, a * 0.35F);
      Geo.band(m, vc, ground, y, r * 0.924F, r * 0.936F, segments, spin, drawn, cr, cg, cb, a * 0.8F, a * 0.8F);
      Geo.band(m, vc, ground, y, r * 0.5F, r * 0.516F, segments, -spin, drawn, cr, cg, cb, a * 0.7F, a * 0.7F);
      if (detail >= 1) {
         Geo.ticks(m, vc, ground, y, r * 0.94F, r * 0.958F, r * 0.978F, detail >= 2 ? 144 : 72, 12, spin, w, drawn, cr, cg, cb, a * 0.65F);
         Geo.star(m, vc, ground, y, r * 0.78F, 12, 5, spin, w * 2.4F, drawn, cr, cg, cb, a * 0.55F);
         Geo.star(m, vc, ground, y, r * 0.5F, 12, 4, -spin, w * 1.6F, drawn, cr, cg, cb, a * 0.4F);
      }

      Geo.disc(m, vc, ground, y, r * 0.155F, seg(32, detail), cr, cg, cb, a * 0.85F, 0.0F);
   }

   private static float stack(
      Matrix4f m,
      VertexConsumer vc,
      Vector3f viewer,
      float r,
      float focusY,
      float spin,
      float sa,
      float age,
      int lit,
      int window,
      boolean charging,
      float pull,
      float a,
      float cr,
      float cg,
      float cb,
      int detail
   ) {
      if (lit <= 0) {
         return r * 0.55F;
      } else {
         int segments = seg(56, detail);
         float crown = r * 0.55F;
         float wire = Math.max(0.03F, r * 0.004F);

         for (int n = 0; n < lit; n++) {
            float open = 1.0F;
            if (charging) {
               float arrived = (float)Mth.ceil((float)((n + 1) * window) / 12.0F);
               open = Mth.clamp((sa - arrived) / 9.0F, 0.0F, 1.0F);
               if (open <= 0.01F) {
                  continue;
               }
            }

            float ease = open * open * (3.0F - 2.0F * open);
            float restY = r * 0.55F + r * 0.36F * (float)n - (1.0F - ease) * r * 0.36F * 0.4F + r * 0.012F * Mth.sin(age * 0.055F + (float)n * 0.8F);
            float restR = r * (0.88F - 0.03F * (float)n) * (1.0F + 0.16F * Mth.sin((float)n * 2.1F)) * ease;
            float y = Mth.lerp(pull, restY, focusY);
            float rr = Mth.lerp(pull, restR, r * 0.04F);
            if (!(rr < 0.05F)) {
               crown = Math.max(crown, y);
               float turn = spin * (1.8F + 0.4F * (float)(n % 4)) * ((n & 1) == 0 ? -1.0F : 1.0F);
               float alpha = a * ease * (0.55F + 0.45F * ease);
               float seat = spin + (float)((Math.PI * 2) * (double)n / 12.0);
               Geo.strand(
                  m,
                  vc,
                  new Vector3f(Mth.cos(seat) * r * 0.72F, 0.1F, Mth.sin(seat) * r * 0.72F),
                  new Vector3f(Mth.cos(seat + 0.3F) * rr, y, Mth.sin(seat + 0.3F) * rr),
                  viewer,
                  wire,
                  cr,
                  cg,
                  cb,
                  alpha * 0.5F * (1.0F - pull),
                  alpha * 0.18F
               );
               Matrix4f at = new Matrix4f(m).translate(0.0F, y, 0.0F);
               Geo.band(at, vc, Terrain.FLAT, 0.0F, rr * 0.93F, rr, segments, turn, 1.0F, cr, cg, cb, alpha, alpha * 0.28F);
               Geo.band(at, vc, Terrain.FLAT, 0.0F, rr * 0.52F, rr * 0.58F, segments, -turn, 1.0F, cr, cg, cb, alpha * 0.7F, alpha * 0.7F);
               if (detail >= 1) {
                  Geo.ticks(
                     at,
                     vc,
                     Terrain.FLAT,
                     0.0F,
                     rr * 0.64F,
                     rr * 0.82F,
                     rr * 0.9F,
                     detail >= 2 ? 24 : 12,
                     4,
                     turn,
                     rr * 0.022F,
                     1.0F,
                     cr,
                     cg,
                     cb,
                     alpha * 0.55F
                  );
                  int points = 3 + n % 6;
                  Geo.star(at, vc, Terrain.FLAT, 0.0F, rr * 0.48F, points, points >= 5 ? 2 : 1, -turn, rr * 0.03F, 1.0F, cr, cg, cb, alpha * 0.7F);
               }
            }
         }

         return crown;
      }
   }

   private static void rails(
      Matrix4f m,
      VertexConsumer vc,
      Terrain ground,
      Vector3f viewer,
      float r,
      float crown,
      float spin,
      float drawn,
      float pull,
      float a,
      float cr,
      float cg,
      float cb,
      int detail
   ) {
      int count = detail >= 2 ? 6 : 4;
      float width = Math.max(0.04F, r * 0.01F);

      for (int i = 0; i < count; i++) {
         float ang = spin * 0.35F + (float)((Math.PI * 2) * (double)i / (double)count);
         float fx = Mth.cos(ang) * r * 0.965F;
         float fz = Mth.sin(ang) * r * 0.965F;
         float top = ang + 0.42F;
         float lean = Mth.lerp(pull, r * 0.55F, r * 0.06F);
         Vector3f from = new Vector3f(fx, 0.05F + ground.lift(fx, fz), fz);
         Vector3f to = new Vector3f(Mth.cos(top) * lean, crown * drawn, Mth.sin(top) * lean);
         Geo.strand(m, vc, from, to, viewer, width, cr, cg, cb, a * 0.3F, a * 0.45F);
      }
   }

   private static void seats(Matrix4f m, VertexConsumer vc, Terrain ground, float r, float spin, float drawn, int lit, float a, float cr, float cg, float cb) {
      int shown = Mth.ceil(12.0F * drawn);

      for (int i = 0; i < shown; i++) {
         float ang = spin + (float)((Math.PI * 2) * (double)i / 12.0);
         boolean on = i < lit;
         float rr = on ? cr : 0.55F;
         float gg = on ? cg : 0.38F;
         float bb = on ? cb : 1.0F;
         float alpha = a * (on ? 1.0F : 0.28F);
         uv(m, vc, ground, 0.032F, Mth.cos(ang) * r * 0.72F, Mth.sin(ang) * r * 0.72F, r * 0.1F, ang, i, rr, gg, bb, alpha);
      }
   }

   private static void risen(
      Matrix4f m,
      VertexConsumer vc,
      Terrain ground,
      Vector3f right,
      Vector3f up,
      float r,
      float spin,
      float age,
      int lit,
      float a,
      float cr,
      float cg,
      float cb
   ) {
      for (int i = 0; i < lit; i++) {
         float ang = spin + (float)((Math.PI * 2) * (double)i / 12.0);
         float bob = 0.9F + 0.22F * Mth.sin(age * 0.09F + (float)i * 0.9F);
         float half = r * 0.085F;
         int cell = (i + 4) % 16;
         float gx = Mth.cos(ang) * r * 0.72F;
         float gz = Mth.sin(ang) * r * 0.72F;
         Geo.billboard(
            m,
            vc,
            gx,
            bob + ground.lift(gx, gz),
            gz,
            right,
            up,
            half,
            (float)(cell % 4) * 0.25F,
            (float)(cell / 4) * 0.25F,
            (float)(cell % 4) * 0.25F + 0.25F,
            (float)(cell / 4) * 0.25F + 0.25F,
            cr,
            cg,
            cb,
            a
         );
      }
   }

   private static void uv(
      Matrix4f m, VertexConsumer vc, Terrain ground, float y, float x, float z, float half, float facing, int index, float r, float g, float b, float a
   ) {
      int cell = index % 16;
      float u0 = (float)(cell % 4) * 0.25F;
      float v0 = (float)(cell / 4) * 0.25F;
      Geo.seat(m, vc, ground, y, x, z, half, facing, u0, v0, u0 + 0.25F, v0 + 0.25F, r, g, b, a);
   }

   private static void funnel(Matrix4f m, VertexConsumer vc, Terrain ground, float radius, float wind, float cr, float cg, float cb) {
      for (int i = 0; i < 3; i++) {
         float phase = (wind * 1.6F + (float)i / 3.0F) % 1.0F;
         float rr = radius * (1.15F - phase * 1.05F);
         if (!(rr < 0.2F)) {
            float alpha = Mth.sin(phase * (float) Math.PI) * 0.55F;
            Geo.band(m, vc, ground, 0.04F, rr * 0.94F, rr, 64, 0.0F, 1.0F, cr, cg, cb, alpha, alpha * 0.2F);
         }
      }
   }

   private static void core(Matrix4f m, VertexConsumer vc, Vector3f right, Vector3f up, float r, float focusY, float wind, int stage, float sa, boolean perfect) {
      float size;
      float alpha;
      if (stage == 2) {
         size = r * (0.6F + sa * 0.55F);
         alpha = Math.max(0.0F, 1.0F - sa / 20.0F);
      } else {
         size = r * (0.05F + wind * wind * wind * 0.7F);
         alpha = wind * 0.95F;
      }

      if (!(alpha <= 0.002F)) {
         float g = perfect ? 0.99F : 0.92F;
         float b = perfect ? 1.0F : 0.72F;
         Geo.billboard(m, vc, 0.0F, focusY, 0.0F, right, up, size, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, g, b, alpha);
      }
   }

   private static void burst(Matrix4f m, VertexConsumer vc, Terrain ground, float r, float focusY, float sa, boolean perfect, int detail) {
      float life = Mth.clamp(sa / 26.0F, 0.0F, 1.0F);
      float alpha = (1.0F - life) * (1.0F - life);
      if (!(alpha <= 0.004F)) {
         float g = perfect ? 0.98F : 0.88F;
         float b = perfect ? 1.0F : 0.62F;
         int segments = seg(48, detail);
         Vector3f centre = new Vector3f(0.0F, focusY, 0.0F);
         Vector3f tilt = new Vector3f(0.0F, 1.0F, 0.0F);
         int planes = detail >= 2 ? 7 : 4;

         for (int i = 0; i < planes; i++) {
            float lean = (float)(Math.PI * (double)i / (double)planes);
            Vector3f axis = new Vector3f(Mth.cos(lean), 0.0F, Mth.sin(lean));
            float rr = r * (0.25F + sa * 0.42F) * (1.0F - 0.06F * (float)i);
            Geo.ringIn(m, vc, centre, axis, tilt, rr * 0.86F, rr, segments, sa * 0.05F, 1.0F, 1.0F, g, b, alpha * 0.55F, 0.0F);
         }

         for (int n = 0; n < 12; n++) {
            float lag = (float)n * 0.6F;
            if (!(sa < lag)) {
               float travelled = (sa - lag) * 0.55F;
               float rr = r * (0.15F + travelled * 0.3F);
               float ringAlpha = alpha * Math.max(0.0F, 1.0F - travelled / 9.0F) * 0.5F;
               if (!(ringAlpha <= 0.004F)) {
                  float y = focusY + ((float)n - 5.5F) * r * 0.035F * travelled;
                  Geo.band(m, vc, Terrain.FLAT, y, rr * 0.92F, rr, segments, (float)n * 0.5F, 1.0F, 1.0F, g, b, ringAlpha, 0.0F);
               }
            }
         }

         Geo.disc(m, vc, ground, 0.06F, r * (0.8F + Math.min(sa, 10.0F) * 0.09F), seg(48, detail), 1.0F, g, b, alpha * 0.5F, 0.0F);
      }
   }

   private static void shock(Matrix4f m, VertexConsumer vc, Terrain ground, float radius, float sa, boolean perfect) {
      float travelled = radius * (0.3F + sa * 0.14F);
      float alpha = Math.max(0.0F, 1.0F - sa / 26.0F) * 0.85F;
      if (!(alpha <= 0.002F)) {
         float g = perfect ? 0.98F : 0.86F;
         Geo.band(m, vc, ground, 0.05F, travelled * 0.9F, travelled, 96, 0.0F, 1.0F, 1.0F, g, perfect ? 1.0F : 0.55F, alpha, 0.0F);
         Geo.band(m, vc, ground, 0.05F, travelled, travelled * 1.06F, 96, 0.0F, 1.0F, 1.0F, g, perfect ? 1.0F : 0.55F, alpha * 0.5F, 0.0F);
      }
   }

   private static int seg(int base, int detail) {
      return detail >= 2 ? base : (detail == 1 ? Math.max(8, base / 2) : Math.max(6, base / 3));
   }

   private static float easeOut(float t) {
      float u = 1.0F - t;
      return 1.0F - u * u * u;
   }
}
