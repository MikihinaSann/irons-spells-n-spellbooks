package com.grandsigil.client;

import com.grandsigil.GsPresence;
import com.grandsigil.config.GsConfig;
import com.grandsigil.entity.GazeEntity;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class GazeRenderer {
   private static final float[] SCLERA = new float[]{0.74F, 0.71F, 0.64F};
   private static final float[] IRIS = new float[]{0.44F, 0.18F, 0.78F};
   private static final float[] IRIS_HOT = new float[]{0.86F, 0.62F, 1.0F};
   private static final float[] PUPIL = new float[]{0.02F, 0.01F, 0.04F};
   private static final float[] VEIN = new float[]{0.46F, 0.08F, 0.15F};
   private static final float[] TEAR = new float[]{0.72F, 0.36F, 1.0F};
   private static final float DOME = 300.0F;
   private static final float WORLD_DOME = 120.0F;
   private static final int RIM = 48;
   private static final float ASPECT = 0.52F;
   private static final float IRIS_R = 0.4F;
   private static final float PUPIL_R = 0.17F;
   private static final int TEARS = 22;
   private static final double VIEW = 480.0;
   private static final ByteBufferBuilder BUILDER = new ByteBufferBuilder(65536);
   private static final BufferSource BUFFERS = MultiBufferSource.immediate(BUILDER);
   private static final Map<Integer, GazeRenderer.Cached> CACHE = new HashMap<>();

   private static float dome() {
      return Shaders.packInUse() ? 120.0F : 300.0F;
   }

   private GazeRenderer() {
   }

   public static void render(RenderLevelStageEvent event) {
      if (event.getStage() == (Shaders.packInUse() ? Stage.AFTER_TRANSLUCENT_BLOCKS : Stage.AFTER_SKY)) {
         if (GsPresence.any(6)) {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            if (level != null) {
               Camera camera = event.getCamera();
               Vec3 eye = camera.getPosition();
               float partial = event.getPartialTick();
               PoseStack pose = event.getPoseStack();
               PoseStack skyPose = new PoseStack();
               skyPose.mulPose(event.getModelViewMatrix());
               boolean any = false;

               for (Entity entity : level.entitiesForRendering()) {
                  if (entity instanceof GazeEntity) {
                     GazeEntity gaze = (GazeEntity)entity;
                     if (!(gaze.distanceToSqr(eye.x, eye.y, eye.z) > 230400.0)) {
                        any = true;
                        GazeRenderer.Cached cached = CACHE.computeIfAbsent(gaze.getId(), id -> new GazeRenderer.Cached());
                        sky(Shaders.packInUse() ? pose : skyPose, gaze, cached, level, partial, eye);
                     }
                  }
               }

               if (any) {
                  BUFFERS.endBatch();
               } else if (!CACHE.isEmpty()) {
                  CACHE.clear();
               }
            }
         }
      }
   }

   private static void sky(PoseStack pose, GazeEntity gaze, GazeRenderer.Cached cached, ClientLevel level, float partial, Vec3 eye) {
      float open = gaze.open(partial);
      if (!(open <= 0.004F)) {
         int detail = (Integer)GsConfig.CLIENT.detail.get();
         float age = (float)gaze.tickCount + partial;
         Vector3f look = new Vector3f((float)(gaze.getX() - eye.x), (float)(gaze.getY() + 150.0 - eye.y), (float)(gaze.getZ() - eye.z));
         if (look.lengthSquared() < 1.0F) {
            look.set(0.0F, 1.0F, 0.0F);
         }

         look.normalize();
         Vector3f centre = new Vector3f(look).mul(dome());
         Vector3f right = across(look);
         Vector3f up = new Vector3f(look).cross(right).normalize();
         float half = dome() * 0.4F;
         float lidded = half * 0.52F * open;
         pose.pushPose();
         Matrix4f m = pose.last().pose();
         VertexConsumer glow = BUFFERS.getBuffer(GsRenderTypes.sky());
         tear(m, glow, cached, gaze, look, open, age, detail);
         BUFFERS.endBatch(GsRenderTypes.sky());
         VertexConsumer veil = BUFFERS.getBuffer(GsRenderTypes.veil());
         lens(m, veil, centre, right, up, half, lidded, detail, SCLERA[0], SCLERA[1], SCLERA[2], open * 0.94F, open * 0.55F);
         veins(m, veil, centre, right, up, half, lidded, age, open, detail);
         BUFFERS.endBatch(GsRenderTypes.veil());
         VertexConsumer iris = BUFFERS.getBuffer(GsRenderTypes.sky());
         Vector3f pupil = tracking(gaze, level, centre, right, up, half, eye, partial);
         iris(m, iris, pupil, right, up, half, age, open, detail);
         BUFFERS.endBatch(GsRenderTypes.sky());
         VertexConsumer dark = BUFFERS.getBuffer(GsRenderTypes.veil());
         Geo.discIn(
            m,
            dark,
            pupil,
            right,
            up,
            half * 0.17F * (0.86F + 0.14F * Mth.sin(age * 0.03F)),
            detail >= 2 ? 48 : 24,
            PUPIL[0],
            PUPIL[1],
            PUPIL[2],
            open,
            open * 0.9F
         );
         BUFFERS.endBatch(GsRenderTypes.veil());
         pose.popPose();
      }
   }

   private static void lens(
      Matrix4f m,
      VertexConsumer vc,
      Vector3f centre,
      Vector3f right,
      Vector3f up,
      float half,
      float lid,
      int detail,
      float r,
      float g,
      float b,
      float aCentre,
      float aEdge
   ) {
      int steps = detail >= 2 ? 48 : 24;

      for (int i = 0; i < steps; i++) {
         float x0 = -half + 2.0F * half * ((float)i / (float)steps);
         float x1 = -half + 2.0F * half * ((float)(i + 1) / (float)steps);
         float y0 = lid * profile(x0 / half);
         float y1 = lid * profile(x1 / half);
         quad(m, vc, centre, right, up, x0, 0.0F, x1, 0.0F, x1, y1, x0, y0, r, g, b, aCentre, aEdge);
         quad(m, vc, centre, right, up, x0, 0.0F, x1, 0.0F, x1, -y1, x0, -y0, r, g, b, aCentre, aEdge);
      }
   }

   private static float profile(float t) {
      return Math.max(0.0F, 1.0F - t * t);
   }

   private static void iris(Matrix4f m, VertexConsumer vc, Vector3f centre, Vector3f right, Vector3f up, float half, float age, float open, int detail) {
      int segments = detail >= 2 ? 96 : 48;
      float r = half * 0.4F * open;
      float inner = half * 0.17F * open;
      Geo.ringIn(m, vc, centre, right, up, inner, r, segments, age * 0.012F, 1.0F, IRIS[0], IRIS[1], IRIS[2], open * 0.85F, open * 0.35F);
      Geo.ringIn(m, vc, centre, right, up, r * 0.72F, r * 0.86F, segments, -age * 0.02F, 1.0F, IRIS_HOT[0], IRIS_HOT[1], IRIS_HOT[2], open * 0.7F, open * 0.5F);
      Geo.ringIn(m, vc, centre, right, up, r * 0.97F, r * 1.06F, segments, age * 0.007F, 1.0F, IRIS_HOT[0], IRIS_HOT[1], IRIS_HOT[2], open * 0.9F, open * 0.2F);
      Geo.spokesIn(
         m,
         vc,
         centre,
         right,
         up,
         inner * 1.05F,
         r * 0.95F,
         detail >= 2 ? 64 : 32,
         -age * 0.009F,
         half * 0.006F,
         0.35F,
         IRIS_HOT[0],
         IRIS_HOT[1],
         IRIS_HOT[2],
         open * 0.55F,
         open * 0.12F
      );
      Geo.ringIn(m, vc, centre, right, up, inner * 0.98F, inner * 1.12F, segments, 0.0F, 1.0F, IRIS_HOT[0], IRIS_HOT[1], IRIS_HOT[2], open, open * 0.6F);
   }

   private static void veins(
      Matrix4f m, VertexConsumer vc, Vector3f centre, Vector3f right, Vector3f up, float half, float lid, float age, float open, int detail
   ) {
      if (detail >= 1) {
         float beat = 0.55F + 0.28F * Mth.sin(age * 0.06F) + 0.1F * Mth.sin(age * 0.017F);
         Geo.spokesIn(
            m,
            vc,
            centre,
            right,
            up,
            half * 0.4F * 1.2F,
            half * 0.92F,
            detail >= 2 ? 26 : 14,
            age * 0.0015F,
            lid * 0.012F,
            0.55F,
            VEIN[0],
            VEIN[1],
            VEIN[2],
            open * beat * 0.5F,
            0.0F
         );
      }
   }

   private static Vector3f tracking(GazeEntity gaze, ClientLevel level, Vector3f centre, Vector3f right, Vector3f up, float half, Vec3 eye, float partial) {
      Entity target = GazeSwarm.watched(gaze, level);
      if (target == null) {
         return new Vector3f(centre);
      } else {
         Vector3f toward = new Vector3f(
            (float)(target.getX() - gaze.getX()), (float)(target.getY() - (gaze.getY() + 150.0)), (float)(target.getZ() - gaze.getZ())
         );
         if (toward.lengthSquared() < 1.0F) {
            return new Vector3f(centre);
         } else {
            toward.normalize();
            float reach = half * 0.23F * 1.6F;
            float ox = Mth.clamp(toward.dot(right) * 2.4F, -1.0F, 1.0F) * reach;
            float oy = Mth.clamp(toward.dot(up) * 2.4F, -1.0F, 1.0F) * reach;
            return new Vector3f(centre.x + right.x * ox + up.x * oy, centre.y + right.y * ox + up.y * oy, centre.z + right.z * ox + up.z * oy);
         }
      }
   }

   private static void tear(Matrix4f m, VertexConsumer vc, GazeRenderer.Cached cached, GazeEntity gaze, Vector3f look, float open, float age, int detail) {
      if (cached.cracks == null) {
         build(gaze, cached, detail);
      }

      float glow = gaze.stage() == 0 ? open : 0.32F * open;
      if (!(glow <= 0.004F)) {
         Vector3f viewer = new Vector3f(0.0F, 0.0F, 0.0F);

         for (Bolt.Arc arc : cached.cracks) {
            float reach = arc.from().distance(new Vector3f(0.0F, 1.0F, 0.0F)) / cached.crackReach;
            if (!(reach > open)) {
               float edge = Mth.clamp((open - reach) * 5.0F, 0.0F, 1.0F);
               Vector3f from = turned(arc.from(), look).mul(dome());
               Vector3f to = turned(arc.to(), look).mul(dome());
               float a = arc.alpha() * glow * edge;
               Geo.strand(m, vc, from, to, viewer, arc.width() * dome() * 2.8F, IRIS[0], IRIS[1], IRIS[2], a * 0.2F, a * 0.2F);
               Geo.strand(m, vc, from, to, viewer, arc.width() * dome(), TEAR[0], TEAR[1], TEAR[2], a, a);
            }
         }
      }
   }

   private static Vector3f turned(Vector3f unit, Vector3f look) {
      Vector3f up = new Vector3f(0.0F, 1.0F, 0.0F);
      Vector3f axis = new Vector3f(up).cross(look);
      float sin = axis.length();
      float cos = up.dot(look);
      Vector3f out = new Vector3f(unit);
      if (sin < 1.0E-5F) {
         return cos < 0.0F ? out.mul(-1.0F) : out;
      } else {
         axis.normalize();
         return out.rotateAxis((float)Math.atan2((double)sin, (double)cos), axis.x, axis.y, axis.z);
      }
   }

   private static void build(GazeEntity gaze, GazeRenderer.Cached cached, int detail) {
      RandomSource random = RandomSource.create((long)gaze.getId() * 4517L + 29L);
      List<Bolt.Arc> arcs = new ArrayList<>();
      Vector3f centre = new Vector3f(0.0F, 0.0F, 0.0F);
      Vector3f origin = new Vector3f(0.0F, 1.0F, 0.0F);
      int count = detail >= 2 ? 22 : 11;

      for (int i = 0; i < count; i++) {
         float azimuth = (float)((Math.PI * 2) * (double)i / (double)count) + random.nextFloat() * 0.4F;
         float from = 0.42F;
         float reach = from + 0.3F + random.nextFloat() * 0.75F;
         Bolt.onSphere(
            arcs,
            centre,
            1.0F,
            new Vector3f(Mth.cos(azimuth) * Mth.sin(from), Mth.cos(from), Mth.sin(azimuth) * Mth.sin(from)),
            new Vector3f(Mth.cos(azimuth) * Mth.sin(reach), Mth.cos(reach), Mth.sin(azimuth) * Mth.sin(reach)),
            random,
            detail >= 2 ? 5 : 4,
            0.13F,
            0.0014F,
            1.0F,
            2
         );
      }

      float furthest = 0.001F;

      for (Bolt.Arc arc : arcs) {
         furthest = Math.max(furthest, arc.from().distance(origin));
      }

      cached.cracks = arcs;
      cached.crackReach = furthest;
   }

   private static void quad(
      Matrix4f m,
      VertexConsumer vc,
      Vector3f centre,
      Vector3f right,
      Vector3f up,
      float x0,
      float y0,
      float x1,
      float y1,
      float x2,
      float y2,
      float x3,
      float y3,
      float r,
      float g,
      float b,
      float aIn,
      float aOut
   ) {
      at(m, vc, centre, right, up, x0, y0, r, g, b, aIn);
      at(m, vc, centre, right, up, x1, y1, r, g, b, aIn);
      at(m, vc, centre, right, up, x2, y2, r, g, b, aOut);
      at(m, vc, centre, right, up, x3, y3, r, g, b, aOut);
   }

   private static void at(Matrix4f m, VertexConsumer vc, Vector3f centre, Vector3f right, Vector3f up, float x, float y, float r, float g, float b, float a) {
      vc.addVertex(m, centre.x + right.x * x + up.x * y, centre.y + right.y * x + up.y * y, centre.z + right.z * x + up.z * y)
         .setColor(r, g, b, a)
         .setUv(0.5F, 0.5F);
   }

   private static Vector3f across(Vector3f look) {
      Vector3f axis = Math.abs(look.y) > 0.9F ? new Vector3f(1.0F, 0.0F, 0.0F) : new Vector3f(0.0F, 1.0F, 0.0F);
      return new Vector3f(look).cross(axis).normalize();
   }

   private static final class Cached {
      List<Bolt.Arc> cracks;
      float crackReach;
   }
}
