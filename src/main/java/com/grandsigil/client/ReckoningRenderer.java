package com.grandsigil.client;

import com.grandsigil.GrandSigil;
import com.grandsigil.GsPresence;
import com.grandsigil.config.GsConfig;
import com.grandsigil.entity.ReckoningEntity;
import com.grandsigil.entity.Terrain;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.Map;
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

public final class ReckoningRenderer {
   private static final ResourceLocation BEAM = GrandSigil.id("textures/entity/beam.png");
   private static final ResourceLocation FLARE = GrandSigil.id("textures/entity/flare.png");
   private static final float[] CORE = new float[]{0.78F, 1.0F, 0.84F};
   private static final float[] HALO = new float[]{1.0F, 0.84F, 0.36F};
   private static final float[] FLOOD = new float[]{1.0F, 0.94F, 0.72F};
   private static final float[] WHITE = new float[]{1.0F, 1.0F, 1.0F};
   private static final float DRAWN = 260.0F;
   private static final int RAYS = 30;
   private static final float PINCH = 0.72F;
   private static final int CLIMBERS = 6;
   private static final float CLIMB = 1.35F;
   private static final int SPINES = 30;
   private static final float SPINE_LENGTH = 46.0F;
   private static final float SPINE_LIFE = 30.0F;
   private static final int HALOS = 3;
   private static final float STAR = 1.15F;
   private static final double VIEW = 512.0;
   private static final ByteBufferBuilder BUILDER = new ByteBufferBuilder(32768);
   private static final BufferSource BUFFERS = MultiBufferSource.immediate(BUILDER);
   private static final Map<Integer, Terrain> FLOORS = new HashMap<>();

   private ReckoningRenderer() {
   }

   public static void render(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_TRANSLUCENT_BLOCKS) {
         if (!GsPresence.any(10)) {
            if (!FLOORS.isEmpty()) {
               FLOORS.clear();
            }
         } else {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            if (level != null) {
               Camera camera = event.getCamera();
               Vec3 eye = camera.getPosition();
               float partial = event.getPartialTick();
               PoseStack pose = event.getPoseStack();
               boolean any = false;

               for (Entity entity : level.entitiesForRendering()) {
                  if (entity instanceof ReckoningEntity) {
                     ReckoningEntity star = (ReckoningEntity)entity;
                     if (!(star.distanceToSqr(eye.x, eye.y, eye.z) > 262144.0)) {
                        any = true;
                        if (star.stage() <= 1) {
                           held(pose, star, partial, eye, camera, level);
                        } else {
                           column(pose, star, partial, eye, level);
                        }
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

   private static Vector3f where(ReckoningEntity star, ClientLevel level, float partial) {
      Entity caster = level.getEntity(star.casterId());
      Vec3 seat = caster == null
         ? star.position().add(0.0, 1.3, 0.0)
         : caster.getEyePosition(partial).add(caster.getViewVector(partial).scale(0.55)).add(0.0, -0.42, 0.0);
      if (star.stage() != 1) {
         return new Vector3f((float)seat.x, (float)seat.y, (float)seat.z);
      } else {
         float t = Mth.clamp(((float)star.stageAge() + partial) / 8.0F, 0.0F, 1.0F);
         float ease = t * t;
         Vec3 to = star.position();
         return new Vector3f(
            (float)Mth.lerp((double)ease, seat.x, to.x), (float)Mth.lerp((double)ease, seat.y, to.y + 1.0), (float)Mth.lerp((double)ease, seat.z, to.z)
         );
      }
   }

   private static void held(PoseStack pose, ReckoningEntity star, float partial, Vec3 eye, Camera camera, ClientLevel level) {
      int detail = (Integer)GsConfig.CLIENT.detail.get();
      boolean thrown = star.stage() == 1;
      float fill = thrown ? 1.0F : Mth.clamp(((float)star.stageAge() + partial) / Math.max(1.0F, (float)star.gather()), 0.0F, 1.0F);
      float age = (float)star.tickCount + partial;
      Vector3f at = where(star, level, partial);
      float scale = thrown ? 1.0F - 0.45F * Mth.clamp(((float)star.stageAge() + partial) / 8.0F, 0.0F, 1.0F) : 1.0F;
      pose.pushPose();
      pose.translate((double)at.x - eye.x, (double)at.y - eye.y, (double)at.z - eye.z);
      Matrix4f m = pose.last().pose();
      Vector3f right = new Vector3f(camera.getLeftVector()).mul(-1.0F);
      Vector3f up = new Vector3f(camera.getUpVector());
      Vector3f viewer = new Vector3f((float)(eye.x - (double)at.x), (float)(eye.y - (double)at.y), (float)(eye.z - (double)at.z));
      VertexConsumer glow = BUFFERS.getBuffer(GsRenderTypes.glow());
      hoops(m, glow, fill, age, scale, detail);
      if (fill > 0.86F) {
         leak(m, glow, viewer, fill, age, scale);
      }

      BUFFERS.endBatch(GsRenderTypes.glow());
      VertexConsumer flare = BUFFERS.getBuffer(GsRenderTypes.glow(FLARE));
      seed(m, flare, right, up, fill, age, scale);
      BUFFERS.endBatch(GsRenderTypes.glow(FLARE));
      pose.popPose();
   }

   private static void seed(Matrix4f m, VertexConsumer vc, Vector3f right, Vector3f up, float fill, float age, float scale) {
      float core = 1.15F * scale * (0.06F + 0.3F * fill);
      float toWhite = Mth.clamp((fill - 0.45F) / 0.4F, 0.0F, 1.0F);
      float g = Mth.lerp(toWhite, CORE[1], WHITE[1]);
      float b = Mth.lerp(toWhite, CORE[2], WHITE[2]);
      float breathe = 0.9F + 0.1F * Mth.sin(age * 0.4F);
      Geo.billboard(m, vc, 0.0F, 0.0F, 0.0F, right, up, core * breathe, 0.0F, 0.0F, 1.0F, 1.0F, CORE[0], g, b, Math.min(1.0F, 0.55F + fill * 0.6F));
      if (!(fill <= 0.52F)) {
         float t = (fill - 0.52F) / 0.48F;
         float size = 1.15F * scale * (0.35F + 2.3F * t * t * t);
         float white = Mth.clamp((t - 0.55F) / 0.45F, 0.0F, 1.0F);
         Geo.billboard(
            m,
            vc,
            0.0F,
            0.0F,
            0.0F,
            right,
            up,
            size,
            0.0F,
            0.0F,
            1.0F,
            1.0F,
            1.0F,
            Mth.lerp(white, HALO[1], 1.0F),
            Mth.lerp(white, HALO[2], 0.92F),
            Math.min(1.0F, t * 0.85F)
         );
      }
   }

   private static void hoops(Matrix4f m, VertexConsumer vc, float fill, float age, float scale, int detail) {
      int segments = detail >= 2 ? 48 : (detail == 1 ? 28 : 16);
      float swallowed = 1.0F - 0.78F * Mth.clamp((fill - 0.62F) / 0.3F, 0.0F, 1.0F);
      if (!(swallowed <= 0.02F)) {
         if (fill > 0.08F && fill < 0.36F) {
            float t = Mth.clamp((fill - 0.08F) / 0.28F, 0.0F, 1.0F);
            float gap = 1.15F * scale * 0.62F * (1.0F - t);
            float rr = 1.15F * scale * (0.2F + 0.24F * t);
            Vector3f flatRight = new Vector3f(1.0F, 0.0F, 0.0F);
            Vector3f flatUp = new Vector3f(0.0F, 0.0F, 1.0F);

            for (int s = -1; s <= 1; s += 2) {
               Geo.ringIn(
                  m,
                  vc,
                  new Vector3f(0.0F, gap * (float)s, 0.0F),
                  flatRight,
                  flatUp,
                  rr * 0.8F,
                  rr,
                  segments,
                  age * 0.06F * (float)s,
                  1.0F,
                  HALO[0],
                  HALO[1],
                  HALO[2],
                  0.0F,
                  t * 0.95F
               );
            }
         }

         for (int i = 0; i < 3; i++) {
            float born = 0.3F + (float)i * 0.16F;
            if (!(fill <= born)) {
               float t = Mth.clamp((fill - born) / 0.18F, 0.0F, 1.0F);
               float rr = 1.15F * scale * (0.3F + 0.32F * fill) * (1.0F - 0.1F * (float)i);
               float alpha = t * swallowed;
               float spin = age * (0.045F + (float)i * 0.013F) + (float)i * 2.1F;
               float lean = 0.55F + (float)i * 0.85F;
               Vector3f n = new Vector3f(Mth.cos(spin) * Mth.sin(lean), Mth.cos(lean), Mth.sin(spin) * Mth.sin(lean));
               Vector3f right = across(n);
               Vector3f up = new Vector3f(n).cross(right).normalize();
               Geo.ringIn(m, vc, new Vector3f(), right, up, rr * 0.86F, rr, segments, age * 0.05F, 1.0F, HALO[0], HALO[1], HALO[2], alpha * 0.25F, alpha);
               if (i == 0 && fill > 0.4F && detail >= 1) {
                  float fibre = Mth.clamp((fill - 0.4F) / 0.22F, 0.0F, 1.0F);
                  Geo.spokesIn(
                     m,
                     vc,
                     new Vector3f(),
                     right,
                     up,
                     rr * 0.22F,
                     rr * 0.92F,
                     detail >= 2 ? 16 : 10,
                     age * 0.05F,
                     rr * 0.03F,
                     0.35F,
                     WHITE[0],
                     WHITE[1],
                     WHITE[2],
                     fibre * swallowed * 0.7F,
                     0.0F
                  );
               }
            }
         }
      }
   }

   private static void leak(Matrix4f m, VertexConsumer vc, Vector3f viewer, float fill, float age, float scale) {
      float t = (fill - 0.86F) / 0.14F;

      for (int i = 0; i < 12; i++) {
         float a = (float)i * 2.399963F + age * 0.02F;
         float lean = Mth.sin((float)i * 1.7F);
         Vector3f dir = new Vector3f(Mth.cos(a), lean * 0.8F, Mth.sin(a)).normalize();
         float len = 1.15F * scale * (1.6F + 3.4F * t) * (0.6F + 0.5F * Mth.abs(Mth.cos((float)i * 2.3F)));
         Geo.taper(m, vc, new Vector3f(), new Vector3f(dir).mul(len), viewer, 1.15F * scale * 0.1F, 0.0F, 1.0F, HALO[1], HALO[2], t * 0.8F, 0.0F);
      }
   }

   private static void column(PoseStack pose, ReckoningEntity star, float partial, Vec3 eye, ClientLevel level) {
      float up = star.standing(partial);
      if (!(up <= 0.004F)) {
         int detail = (Integer)GsConfig.CLIENT.detail.get();
         float r = star.radius();
         float age = (float)star.tickCount + partial;
         float bulge = 1.0F + star.flare(partial) * 0.85F;
         float close = star.closing(partial);
         float tall = 1.0F - close;
         float pinch = 1.0F - 0.72F * close;
         Terrain floor = FLOORS.computeIfAbsent(star.getId(), id -> Terrain.sample(level, star.position(), r * 1.8F));
         pose.pushPose();
         pose.translate(star.getX() - eye.x, star.getY() - eye.y, star.getZ() - eye.z);
         Matrix4f m = pose.last().pose();
         Vector3f viewer = new Vector3f((float)(eye.x - star.getX()), (float)(eye.y - star.getY()), (float)(eye.z - star.getZ()));
         VertexConsumer ground = BUFFERS.getBuffer(GsRenderTypes.floor());
         pool(m, ground, floor, r * pinch, up, age, star, partial, detail);
         BUFFERS.endBatch(GsRenderTypes.floor());
         VertexConsumer glow = BUFFERS.getBuffer(GsRenderTypes.glow());
         body(m, glow, r * pinch, up, bulge, tall, age, detail);
         climbers(m, glow, r * pinch, up, tall, age, detail);
         spines(m, glow, star, viewer, partial, r);
         BUFFERS.endBatch(GsRenderTypes.glow());
         VertexConsumer beam = BUFFERS.getBuffer(GsRenderTypes.glow(BEAM));
         heart(m, beam, r * pinch, up, bulge, tall, age, detail);
         BUFFERS.endBatch(GsRenderTypes.glow(BEAM));
         pose.popPose();
      }
   }

   private static void body(Matrix4f m, VertexConsumer vc, float r, float up, float bulge, float tall, float age, int detail) {
      int segments = detail >= 2 ? 44 : (detail == 1 ? 24 : 14);
      float top = 260.0F * tall;
      float foot = 0.1F;
      Geo.columnPlain(m, vc, 0.0F, 0.0F, foot, top, r * 2.15F * bulge, r * 1.3F, segments, 0.0F, HALO[0], HALO[1], HALO[2], up * 0.15F, 0.0F);
      Geo.columnPlain(m, vc, 0.0F, 0.0F, foot, top * 0.72F, r * 1.42F * bulge, r * 0.98F, segments, 0.0F, FLOOD[0], FLOOD[1], FLOOD[2], up * 0.3F, up * 0.02F);
      Geo.columnPlain(m, vc, 0.0F, 0.0F, foot, top, r * 0.95F * bulge, r * 0.7F, segments, 0.0F, WHITE[0], WHITE[1], WHITE[2], up * 0.34F, up * 0.05F);
      if (detail >= 1) {
         for (int i = 0; i < 30; i++) {
            float seed = (float)i * 2.399963F;
            float a = seed + age * (0.004F + (float)(i % 5) * 0.0011F);
            float rr = r * (0.1F + 0.82F * Mth.abs(Mth.sin(seed * 3.1F)));
            float x = Mth.cos(a) * rr;
            float z = Mth.sin(a) * rr;
            float fade = 0.4F + 0.45F * Mth.sin(age * 0.09F + seed);
            float reach = top * (0.42F + 0.58F * Mth.abs(Mth.cos(seed)));
            Geo.columnPlain(m, vc, x, z, 0.2F, reach, r * 0.07F, r * 0.012F, 4, 0.0F, WHITE[0], WHITE[1], WHITE[2], up * fade * 0.55F, 0.0F);
         }
      }
   }

   private static void heart(Matrix4f m, VertexConsumer vc, float r, float up, float bulge, float tall, float age, int detail) {
      int segments = detail >= 2 ? 32 : (detail == 1 ? 18 : 10);
      float top = 260.0F * tall;
      float scroll = -age * 0.045F;
      Geo.column(m, vc, 0.0F, 0.0F, 0.1F, top, r * 0.52F * bulge, r * 0.4F, segments, 0.0F, 8.0F, scroll, WHITE[0], WHITE[1], WHITE[2], up * 0.85F, up * 0.1F);
      Geo.column(
         m, vc, 0.0F, 0.0F, 0.1F, top * 0.55F, r * 0.2F, r * 0.14F, segments, 0.4F, 5.0F, scroll * 1.6F, WHITE[0], WHITE[1], WHITE[2], up * 0.95F, up * 0.15F
      );
   }

   private static void climbers(Matrix4f m, VertexConsumer vc, float r, float up, float tall, float age, int detail) {
      if (detail >= 1) {
         int segments = detail >= 2 ? 44 : 22;

         for (int i = 0; i < 6; i++) {
            float t = (age * 1.35F * 0.02F + (float)i / 6.0F) % 1.0F;
            float y = 0.4F + t * 62.0F * tall;
            float alpha = up * (1.0F - t) * (1.0F - t) * 0.55F;
            if (!(alpha <= 0.004F)) {
               float rr = r * (1.05F + t * 0.55F);
               Geo.band(m, vc, Terrain.FLAT, y, rr * 0.94F, rr, segments, (float)i * 0.7F, 1.0F, FLOOD[0], FLOOD[1], FLOOD[2], 0.0F, alpha);
            }
         }
      }
   }

   private static void spines(Matrix4f m, VertexConsumer vc, ReckoningEntity star, Vector3f viewer, float partial, float r) {
      float age = switch (star.stage()) {
         case 2 -> (float)star.stageAge() + partial;
         case 3 -> (float)(14 + star.stageAge()) + partial;
         default -> 31.0F;
      };
      if (!(age >= 30.0F)) {
         float t = age / 30.0F;
         float alpha = (1.0F - t) * (1.0F - t);
         float reach = 46.0F * (1.0F - (1.0F - t) * (1.0F - t) * (1.0F - t));
         Vector3f from = new Vector3f(0.0F, r * 0.2F, 0.0F);

         for (int i = 0; i < 30; i++) {
            float u = ((float)i + 0.5F) / 30.0F;
            float y = Mth.lerp(u, -0.35F, 1.0F);
            float ring = Mth.sqrt(Math.max(0.0F, 1.0F - y * y));
            float a = (float)i * 2.399963F;
            Vector3f dir = new Vector3f(Mth.cos(a) * ring, y, Mth.sin(a) * ring);
            float len = reach * (0.55F + 0.55F * Mth.abs(Mth.sin((float)i * 1.9F)));
            Geo.taper(
               m,
               vc,
               from,
               new Vector3f(dir).mul(len).add(from),
               viewer,
               r * 0.11F,
               0.0F,
               1.0F,
               Mth.lerp(t, 1.0F, HALO[1]),
               Mth.lerp(t, 0.95F, HALO[2]),
               alpha * 0.75F,
               0.0F
            );
         }
      }
   }

   private static void pool(Matrix4f m, VertexConsumer vc, Terrain floor, float r, float up, float age, ReckoningEntity star, float partial, int detail) {
      int segments = detail >= 2 ? 96 : (detail == 1 ? 56 : 32);
      if (up > 0.004F) {
         Geo.disc(m, vc, floor, 0.07F, r * 1.5F, segments, WHITE[0], WHITE[1], WHITE[2], up * 0.55F, 0.0F);
         Geo.band(m, vc, floor, 0.08F, r * 0.975F, r, segments, age * 0.003F, 1.0F, FLOOD[0], FLOOD[1], FLOOD[2], up * 0.85F, up * 0.2F);
         if (detail >= 1) {
            Geo.ticks(
               m,
               vc,
               floor,
               0.08F,
               r * 1.01F,
               r * 1.06F,
               r * 1.14F,
               detail >= 2 ? 64 : 32,
               8,
               -age * 0.003F,
               r * 0.006F,
               1.0F,
               HALO[0],
               HALO[1],
               HALO[2],
               up * 0.6F
            );
         }
      }
      float shock = switch (star.stage()) {
         case 2 -> (float)star.stageAge() + partial;
         case 3 -> (float)(14 + star.stageAge()) + partial;
         default -> 61.0F;
      };
      if (!(shock >= 60.0F)) {
         float travelled = r * (0.8F + shock * 0.1F);
         float fade = (1.0F - shock / 60.0F) * (1.0F - shock / 60.0F);
         Geo.band(m, vc, floor, 0.1F, travelled * 0.94F, travelled, segments, 0.0F, 1.0F, WHITE[0], WHITE[1], WHITE[2], 0.0F, fade * 0.7F);
      }
   }

   private static Vector3f across(Vector3f normal) {
      Vector3f axis = Math.abs(normal.y) > 0.9F ? new Vector3f(1.0F, 0.0F, 0.0F) : new Vector3f(0.0F, 1.0F, 0.0F);
      return new Vector3f(normal).cross(axis).normalize();
   }
}
