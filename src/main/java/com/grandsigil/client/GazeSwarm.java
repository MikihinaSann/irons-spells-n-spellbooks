package com.grandsigil.client;

import com.grandsigil.GsPresence;
import com.grandsigil.config.GsConfig;
import com.grandsigil.entity.GazeEntity;
import com.grandsigil.entity.Terrain;
import com.grandsigil.registry.GsEffects;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class GazeSwarm {
   private static final float[] SCLERA = new float[]{0.8F, 0.77F, 0.7F};
   private static final float[] IRIS = new float[]{0.5F, 0.2F, 0.86F};
   private static final float[] IRIS_HOT = new float[]{0.88F, 0.66F, 1.0F};
   private static final float[] PUPIL = new float[]{0.02F, 0.01F, 0.04F};
   private static final float SHELL = 2.9F;
   private static final float INTIMATE = 1.85F;
   private static final float SIZE = 0.36F;
   private static final float ASPECT = 0.56F;
   private static final float IRIS_R = 0.3F;
   private static final float PUPIL_R = 0.12F;
   private static final float TRAVEL_X = 0.45F;
   private static final float TRAVEL_Y = 0.19F;
   private static final double VIEW = 72.0;
   private static final int CROWD = 12;
   private static final float GOLDEN = 2.3999631F;
   private static final float SQUASH = 0.62F;
   private static final float FADE = 25.0F;
   private static final ByteBufferBuilder BUILDER = new ByteBufferBuilder(65536);
   private static final BufferSource BUFFERS = MultiBufferSource.immediate(BUILDER);

   private GazeSwarm() {
   }

   public static void render(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_TRANSLUCENT_BLOCKS) {
         if (GsPresence.any(6)) {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            if (level != null) {
               Camera camera = event.getCamera();
               Vec3 eye = camera.getPosition();
               float partial = event.getPartialTick();
               List<LivingEntity> victims = gathered(level, eye);
               if (!victims.isEmpty()) {
                  Vector3f right = new Vector3f(camera.getLeftVector()).mul(-1.0F);
                  Vector3f up = new Vector3f(camera.getUpVector());
                  int detail = (Integer)GsConfig.CLIENT.detail.get();
                  float age = (float)(level.getGameTime() % 24000L) + partial;
                  List<GazeSwarm.Watcher> eyes = new ArrayList<>();

                  for (LivingEntity victim : victims) {
                     gather(eyes, victim, minecraft, age, partial, detail);
                  }

                  if (!eyes.isEmpty()) {
                     PoseStack pose = event.getPoseStack();
                     pose.pushPose();
                     pose.translate(-eye.x, -eye.y, -eye.z);
                     Matrix4f m = pose.last().pose();
                     VertexConsumer body = BUFFERS.getBuffer(GsRenderTypes.glass());

                     for (GazeSwarm.Watcher watcher : eyes) {
                        sclera(m, body, watcher, right, up, detail);
                        pupil(m, body, watcher, right, up, detail);
                     }

                     BUFFERS.endBatch(GsRenderTypes.glass());
                     VertexConsumer light = BUFFERS.getBuffer(GsRenderTypes.glow());

                     for (GazeSwarm.Watcher watcher : eyes) {
                        iris(m, light, watcher, right, up, age, detail);
                     }

                     BUFFERS.endBatch(GsRenderTypes.glow());
                     VertexConsumer ground = BUFFERS.getBuffer(GsRenderTypes.floor());

                     for (LivingEntity victim : victims) {
                        floor(m, ground, victim, age, partial);
                     }

                     BUFFERS.endBatch(GsRenderTypes.floor());
                     pose.popPose();
                  }
               }
            }
         }
      }
   }

   private static List<LivingEntity> gathered(ClientLevel level, Vec3 eye) {
      List<LivingEntity> found = new ArrayList<>();

      for (Entity entity : level.entitiesForRendering()) {
         if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;
            if (GsEffects.transfixed(living) && !(living.distanceToSqr(eye.x, eye.y, eye.z) > 5184.0)) {
               found.add(living);
            }
         }
      }

      if (found.size() > 12) {
         found.sort((a, b) -> Double.compare(a.distanceToSqr(eye.x, eye.y, eye.z), b.distanceToSqr(eye.x, eye.y, eye.z)));
         return found.subList(0, 12);
      } else {
         return found;
      }
   }

   @Nullable
   public static Entity watched(GazeEntity gaze, ClientLevel level) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player != null && GsEffects.transfixed(minecraft.player) && gaze.covers(minecraft.player)) {
         return minecraft.player;
      } else {
         LivingEntity best = null;
         double closest = Double.MAX_VALUE;

         for (Entity entity : level.entitiesForRendering()) {
            if (entity instanceof LivingEntity) {
               LivingEntity living = (LivingEntity)entity;
               if (GsEffects.transfixed(living) && gaze.covers(living)) {
                  double d = living.distanceToSqr(gaze.position());
                  if (d < closest) {
                     closest = d;
                     best = living;
                  }
               }
            }
         }

         return best;
      }
   }

   private static void gather(List<GazeSwarm.Watcher> into, LivingEntity victim, Minecraft minecraft, float age, float partial, int detail) {
      float remaining = (float)GsEffects.held(victim);
      float fade = Mth.clamp(remaining / 25.0F, 0.0F, 1.0F);
      if (!(fade <= 0.01F)) {
         boolean mine = victim == minecraft.player;

         int count = switch (detail) {
            case 0 -> 6;
            case 1 -> 10;
            default -> 15;
         } + (mine ? 5 : 0);
         float shell = mine ? 1.85F : 2.9F + victim.getBbWidth() * 0.5F;
         float size = 0.36F * (mine ? 0.8F : 1.0F + victim.getBbHeight() * 0.06F);
         Vec3 stood = victim.getPosition(partial);
         Vector3f centre = new Vector3f((float)stood.x, (float)stood.y + victim.getBbHeight() * 0.62F, (float)stood.z);
         int seed = victim.getId() * 7919 & 0xFF;
         float spin = age * 0.0075F;

         for (int i = 0; i < count; i++) {
            float polar = 1.0F - 2.0F * ((float)i + 0.5F) / (float)count;
            float band = Mth.sqrt(Math.max(0.0F, 1.0F - polar * polar));
            float around = (float)i * 2.3999631F + spin;
            float bob = 0.14F * Mth.sin(age * 0.045F + (float)i * 1.7F + (float)seed);
            float lid = blink(age, i * 37 + seed);
            if (!(lid <= 0.02F)) {
               float scale = size * (0.8F + 0.4F * frac((float)i * 0.6180339F + (float)seed * 0.0137F));
               into.add(
                  new GazeSwarm.Watcher(
                     new Vector3f(centre.x + Mth.cos(around) * band * shell, centre.y + polar * 0.62F * shell + bob, centre.z + Mth.sin(around) * band * shell),
                     centre,
                     scale,
                     lid,
                     fade
                  )
               );
            }
         }
      }
   }

   private static void sclera(Matrix4f m, VertexConsumer vc, GazeSwarm.Watcher eye, Vector3f right, Vector3f up, int detail) {
      float half = eye.half();
      float tall = half * 0.56F * eye.lid();
      float a = eye.alpha();
      int steps = detail >= 2 ? 10 : 6;

      for (int i = 0; i < steps; i++) {
         float x0 = -half + 2.0F * half * ((float)i / (float)steps);
         float x1 = -half + 2.0F * half * ((float)(i + 1) / (float)steps);
         float y0 = tall * profile(x0 / half);
         float y1 = tall * profile(x1 / half);
         quad(m, vc, eye.at(), right, up, x0, 0.0F, x1, 0.0F, x1, y1, x0, y0, SCLERA[0], SCLERA[1], SCLERA[2], a * 0.94F, a * 0.72F);
         quad(m, vc, eye.at(), right, up, x0, 0.0F, x1, 0.0F, x1, -y1, x0, -y0, SCLERA[0], SCLERA[1], SCLERA[2], a * 0.94F, a * 0.72F);
      }
   }

   private static void pupil(Matrix4f m, VertexConsumer vc, GazeSwarm.Watcher eye, Vector3f right, Vector3f up, int detail) {
      Geo.discIn(
         m,
         vc,
         aimed(eye, right, up),
         right,
         squashed(up, eye.lid()),
         eye.half() * 0.12F,
         detail >= 2 ? 16 : 8,
         PUPIL[0],
         PUPIL[1],
         PUPIL[2],
         eye.alpha(),
         eye.alpha() * 0.9F
      );
   }

   private static void iris(Matrix4f m, VertexConsumer vc, GazeSwarm.Watcher eye, Vector3f right, Vector3f up, float age, int detail) {
      Vector3f centre = aimed(eye, right, up);
      Vector3f squash = squashed(up, eye.lid());
      float half = eye.half();
      float a = eye.alpha();
      int segments = detail >= 2 ? 16 : 8;
      Geo.ringIn(m, vc, centre, right, squash, half * 0.12F * 1.05F, half * 0.3F, segments, age * 0.02F, 1.0F, IRIS[0], IRIS[1], IRIS[2], a * 0.85F, a * 0.2F);
      Geo.ringIn(
         m,
         vc,
         centre,
         right,
         squash,
         half * 0.3F * 0.94F,
         half * 0.3F * 1.1F,
         segments,
         -age * 0.03F,
         1.0F,
         IRIS_HOT[0],
         IRIS_HOT[1],
         IRIS_HOT[2],
         a * 0.9F,
         a * 0.25F
      );
   }

   private static Vector3f aimed(GazeSwarm.Watcher eye, Vector3f right, Vector3f up) {
      Vector3f toward = new Vector3f(eye.focus()).sub(eye.at());
      if (toward.lengthSquared() > 1.0E-6F) {
         toward.normalize();
      }

      float nx = Mth.clamp(toward.dot(right) * 1.8F, -1.0F, 1.0F);
      float ny = Mth.clamp(toward.dot(up) * 1.8F, -1.0F, 1.0F);
      float reach = Mth.sqrt(nx * nx + ny * ny);
      if (reach > 1.0F) {
         nx /= reach;
         ny /= reach;
      }

      float ox = nx * 0.45F * eye.half();
      float oy = ny * 0.19F * eye.half() * eye.lid();
      return new Vector3f(eye.at().x + right.x * ox + up.x * oy, eye.at().y + right.y * ox + up.y * oy, eye.at().z + right.z * ox + up.z * oy);
   }

   private static Vector3f squashed(Vector3f up, float lid) {
      return new Vector3f(up).mul(lid);
   }

   private static float profile(float t) {
      return Math.max(0.0F, 1.0F - t * t);
   }

   private static float blink(float age, int seed) {
      float period = 44.0F + (float)(seed & 31);
      float phase = frac((age + (float)(seed & 63)) / period);
      return phase > 0.1F ? 1.0F : Mth.abs(phase - 0.05F) / 0.05F;
   }

   private static float frac(float v) {
      return v - (float)Mth.floor(v);
   }

   private static void floor(Matrix4f m, VertexConsumer vc, LivingEntity victim, float age, float partial) {
      float alpha = Mth.clamp((float)GsEffects.held(victim) / 25.0F, 0.0F, 1.0F);
      if (!(alpha <= 0.01F)) {
         Vec3 stood = victim.getPosition(partial);
         Matrix4f under = new Matrix4f(m).translate((float)stood.x, (float)stood.y, (float)stood.z);
         float r = 0.75F + victim.getBbWidth() * 0.6F;
         Geo.band(under, vc, Terrain.FLAT, 0.05F, r, r * 1.16F, 32, age * 0.04F, 1.0F, IRIS_HOT[0], IRIS_HOT[1], IRIS_HOT[2], alpha * 0.55F, 0.0F);
         Geo.ticks(under, vc, Terrain.FLAT, 0.05F, r * 1.24F, r * 1.5F, r * 1.9F, 6, 1, -age * 0.03F, 0.16F, 1.0F, IRIS[0], IRIS[1], IRIS[2], alpha * 0.45F);
      }
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

   private static record Watcher(Vector3f at, Vector3f focus, float half, float lid, float alpha) {
   }
}
