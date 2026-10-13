package com.grandsigil.client;

import com.grandsigil.GrandSigil;
import com.grandsigil.GsPresence;
import com.grandsigil.config.GsConfig;
import com.grandsigil.entity.SunEntity;
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

public final class SunRenderer {
   private static final ResourceLocation SUN = ResourceLocation.parse("textures/environment/sun.png");
   private static final ResourceLocation BEAM = GrandSigil.id("textures/entity/beam.png");
   private static final ResourceLocation MASK = GrandSigil.id("textures/entity/mask.png");
   private static final ResourceLocation FLARE = GrandSigil.id("textures/entity/flare.png");
   private static final float[] CORE = new float[]{1.0F, 0.98F, 0.9F};
   private static final float[] BODY = new float[]{1.0F, 0.76F, 0.3F};
   private static final float[] RIM = new float[]{1.0F, 0.44F, 0.12F};
   private static final float PATCH_SCALE = 1.7F;
   private static final double VIEW = 512.0;
   private static final ByteBufferBuilder BUILDER = new ByteBufferBuilder(8192);
   private static final BufferSource BUFFERS = MultiBufferSource.immediate(BUILDER);

   private SunRenderer() {
   }

   public static void render(RenderLevelStageEvent event) {
      Stage stage = event.getStage();
      boolean skyPass = stage == Stage.AFTER_SKY;
      boolean worldPass = stage == Stage.AFTER_TRANSLUCENT_BLOCKS;
      if (skyPass || worldPass) {
         if (GsPresence.any(1)) {
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
                  if (entity instanceof SunEntity) {
                     SunEntity sun = (SunEntity)entity;
                     if (!(sun.distanceToSqr(eye.x, eye.y, eye.z) > 262144.0)) {
                        any = true;
                        if (skyPass) {
                           if (!Shaders.packInUse()) {
                              sky(skyPose, sun, level, eye, partial);
                           }
                        } else {
                           world(pose, sun, partial, eye, camera);
                           if (Shaders.packInUse()) {
                              starInWorld(pose, sun, partial);
                           }
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

   private static void sky(PoseStack pose, SunEntity sun, ClientLevel level, Vec3 eye, float partial) {
      pose.pushPose();
      Matrix4f m = pose.last().pose();
      float realAngle = level.getSunAngle(partial);
      Vector3f real = SunEntity.sunDirection(realAngle);
      if (real.y > -0.25F && !Shaders.packInUse()) {
         Vec3 skyColour = level.getSkyColor(eye, partial);
         float half = 51.000004F;
         VertexConsumer patch = BUFFERS.getBuffer(GsRenderTypes.patch(MASK));
         Geo.billboard(
            m,
            patch,
            real.x * 100.0F,
            real.y * 100.0F,
            real.z * 100.0F,
            SunEntity.sunRight(realAngle),
            SunEntity.sunUp(realAngle),
            half,
            0.0F,
            0.0F,
            1.0F,
            1.0F,
            (float)skyColour.x,
            (float)skyColour.y,
            (float)skyColour.z,
            1.0F
         );
         BUFFERS.endBatch(GsRenderTypes.patch(MASK));
      }

      float movedAngle = sun.angle(partial);
      Vector3f moved = SunEntity.sunDirection(movedAngle);
      float radius = sun.sunRadius(partial);
      float x = moved.x * 100.0F;
      float y = moved.y * 100.0F;
      float z = moved.z * 100.0F;
      VertexConsumer disc = BUFFERS.getBuffer(GsRenderTypes.celestial(SUN));
      Geo.billboard(m, disc, x, y, z, SunEntity.sunRight(movedAngle), SunEntity.sunUp(movedAngle), radius, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F);
      BUFFERS.endBatch(GsRenderTypes.celestial(SUN));
      pose.popPose();
   }

   private static void starInWorld(PoseStack pose, SunEntity sun, float partial) {
      float angle = sun.angle(partial);
      Vector3f at = SunEntity.sunDirection(angle).mul(100.0F);
      float radius = sun.sunRadius(partial);
      pose.pushPose();
      Matrix4f m = pose.last().pose();
      VertexConsumer glow = BUFFERS.getBuffer(GsRenderTypes.glow(FLARE));
      Geo.billboard(
         m, glow, at.x, at.y, at.z, SunEntity.sunRight(angle), SunEntity.sunUp(angle), radius * 1.9F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.82F, 0.46F, 0.85F
      );
      Geo.billboard(
         m, glow, at.x, at.y, at.z, SunEntity.sunRight(angle), SunEntity.sunUp(angle), radius * 0.95F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.97F, 0.86F, 1.0F
      );
      BUFFERS.endBatch(GsRenderTypes.glow(FLARE));
      pose.popPose();
   }

   private static void world(PoseStack pose, SunEntity sun, float partial, Vec3 eye, Camera camera) {
      int detail = (Integer)GsConfig.CLIENT.detail.get();
      int stage = sun.stage();
      float sa = (float)sun.stageAge() + partial;
      float age = (float)sun.tickCount + partial;
      float radius = sun.radius();
      pose.pushPose();
      pose.translate(sun.getX() - eye.x, sun.getY() - eye.y, sun.getZ() - eye.z);
      Matrix4f m = pose.last().pose();
      VertexConsumer floor = BUFFERS.getBuffer(GsRenderTypes.floor());
      scorch(m, floor, radius, stage, sa, age, detail);
      BUFFERS.endBatch(GsRenderTypes.floor());
      if (stage == 2 || stage == 3 && sa < 30.0F) {
         float fired = stage == 2 ? sa : 16.0F + sa;
         VertexConsumer beam = BUFFERS.getBuffer(GsRenderTypes.glow(BEAM));
         columnBody(m, beam, radius, fired, age, detail);
         BUFFERS.endBatch(GsRenderTypes.glow(BEAM));
         VertexConsumer glow = BUFFERS.getBuffer(GsRenderTypes.glow());
         column(m, glow, radius, fired, age, detail);
         BUFFERS.endBatch(GsRenderTypes.glow());
      }

      pose.popPose();
   }

   private static void column(Matrix4f m, VertexConsumer vc, float radius, float sa, float age, int detail) {
      float alpha = strength(sa);
      if (!(alpha <= 0.004F)) {
         int segments = detail >= 2 ? 40 : (detail == 1 ? 20 : 12);
         float height = 200.0F;
         Geo.columnPlain(m, vc, 0.0F, 0.0F, 0.0F, height, radius * 0.34F, radius * 0.3F, segments, 0.0F, CORE[0], CORE[1], CORE[2], alpha, alpha * 0.2F);
         Geo.columnPlain(m, vc, 0.0F, 0.0F, 0.0F, height, radius * 0.7F, radius * 0.58F, segments, 0.0F, BODY[0], BODY[1], BODY[2], alpha * 0.7F, alpha * 0.1F);
         Geo.columnPlain(m, vc, 0.0F, 0.0F, 0.0F, height * 0.6F, radius * 1.1F, radius * 0.82F, segments, 0.0F, RIM[0], RIM[1], RIM[2], alpha * 0.32F, 0.0F);

         for (int i = 0; i < 5; i++) {
            float span = radius * 4.0F;
            float h = span - (age * 3.2F + (float)i * span / 5.0F) % span;
            float ring = alpha * (h / span) * 0.8F;
            Geo.band(m, vc, Terrain.FLAT, h, radius * 0.72F, radius * 1.05F, segments, 0.0F, 1.0F, CORE[0], CORE[1], CORE[2], ring, 0.0F);
         }

         Geo.disc(
            m, vc, Terrain.FLAT, 0.06F, radius * (1.1F + Math.min(sa, 14.0F) * 0.05F), detail >= 2 ? 64 : 32, CORE[0], CORE[1], CORE[2], alpha * 0.6F, 0.0F
         );
         float travelled = radius * (0.4F + sa * 0.075F);
         float shock = Math.max(0.0F, 1.0F - sa / 36.0F);
         if (shock > 0.004F) {
            Geo.band(m, vc, Terrain.FLAT, 0.07F, travelled * 0.92F, travelled, detail >= 2 ? 128 : 64, 0.0F, 1.0F, 1.0F, 0.94F, 0.76F, shock, 0.0F);
         }
      }
   }

   private static void columnBody(Matrix4f m, VertexConsumer vc, float radius, float sa, float age, int detail) {
      float alpha = strength(sa);
      if (!(alpha <= 0.004F)) {
         Geo.column(
            m,
            vc,
            0.0F,
            0.0F,
            0.0F,
            200.0F,
            radius * 0.52F,
            radius * 0.44F,
            detail >= 2 ? 40 : 20,
            0.0F,
            12.0F,
            age * 0.22F,
            1.0F,
            0.9F,
            0.66F,
            alpha * 0.9F,
            alpha * 0.15F
         );
      }
   }

   private static float strength(float sa) {
      return sa < 4.0F ? Mth.clamp(sa / 1.5F, 0.0F, 1.0F) : Math.max(0.0F, 1.0F - (sa - 4.0F) / 26.0F);
   }

   private static void scorch(Matrix4f m, VertexConsumer vc, float radius, int stage, float sa, float age, int detail) {
      int segments = detail >= 2 ? 128 : 64;

      float heat = switch (stage) {
         case 0 -> Mth.clamp(sa / 80.0F, 0.0F, 1.0F) * 0.3F;
         case 1 -> 0.3F + 0.7F * Mth.clamp(sa / 60.0F, 0.0F, 1.0F);
         case 2 -> 1.0F;
         default -> Math.max(0.0F, 1.0F - sa / 45.0F);
      };
      if (!(heat <= 0.004F)) {
         float pulse = 0.9F + 0.1F * Mth.sin(age * 0.18F);
         Geo.band(m, vc, Terrain.FLAT, 0.04F, radius * 0.96F, radius, segments, 0.0F, 1.0F, RIM[0], RIM[1], RIM[2], heat * pulse, heat * 0.25F);
         Geo.disc(m, vc, Terrain.FLAT, 0.03F, radius * 0.97F, segments, BODY[0], BODY[1] * 0.7F, BODY[2] * 0.4F, heat * 0.3F * pulse, 0.0F);
      }
   }
}
