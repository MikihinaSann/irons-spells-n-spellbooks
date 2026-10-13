package com.grandsigil.client;

import com.grandsigil.GsPresence;
import com.grandsigil.config.GsConfig;
import com.grandsigil.entity.CalderaEntity;
import com.grandsigil.entity.FieldEntity;
import com.grandsigil.entity.Terrain;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import org.joml.Matrix4f;

public final class FieldRenderer {
   private static final double VIEW = 160.0;
   private static final ByteBufferBuilder BUILDER = new ByteBufferBuilder(4096);
   private static final BufferSource BUFFERS = MultiBufferSource.immediate(BUILDER);

   private FieldRenderer() {
   }

   public static void render(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_TRANSLUCENT_BLOCKS) {
         if (GsPresence.any(9)) {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            if (level != null) {
               Camera camera = event.getCamera();
               Vec3 eye = camera.getPosition();
               float partial = event.getPartialTick();
               PoseStack pose = event.getPoseStack();
               boolean any = false;

               for (Entity entity : level.entitiesForRendering()) {
                  if (entity instanceof FieldEntity) {
                     FieldEntity field = (FieldEntity)entity;
                     if (!(field.distanceToSqr(eye.x, eye.y, eye.z) > 25600.0)) {
                        any = true;
                        rim(pose, field, partial, eye);
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

   private static void rim(PoseStack pose, FieldEntity field, float partial, Vec3 eye) {
      float spread = field.spread(partial);
      if (!(spread <= 0.01F)) {
         int detail = (Integer)GsConfig.CLIENT.detail.get();
         float r = field.radius() * spread;
         float age = (float)field.tickCount + partial;
         float[] tint = colour(field);
         float alpha = (field.muted() ? 0.35F : 0.75F) * (0.82F + 0.18F * Mth.sin(age * 0.12F));
         pose.pushPose();
         pose.translate(field.getX() - eye.x, field.getY() - eye.y, field.getZ() - eye.z);
         Matrix4f m = pose.last().pose();
         VertexConsumer floor = BUFFERS.getBuffer(GsRenderTypes.floor());
         int segments = detail >= 2 ? 160 : (detail == 1 ? 96 : 64);
         Geo.band(m, floor, Terrain.FLAT, 0.06F, r * 0.985F, r, segments, age * 0.004F, 1.0F, tint[0], tint[1], tint[2], alpha, alpha * 0.15F);
         Geo.band(m, floor, Terrain.FLAT, 0.06F, r * 0.94F, r * 0.95F, segments, -age * 0.003F, 1.0F, tint[0], tint[1], tint[2], alpha * 0.5F, alpha * 0.5F);
         if (detail >= 1) {
            Geo.ticks(
               m,
               floor,
               Terrain.FLAT,
               0.06F,
               r * 0.955F,
               r * 0.972F,
               r * 0.995F,
               detail >= 2 ? 96 : 48,
               8,
               age * 0.004F,
               r * 0.004F,
               1.0F,
               tint[0],
               tint[1],
               tint[2],
               alpha * 0.55F
            );
         }

         BUFFERS.endBatch(GsRenderTypes.floor());
         pose.popPose();
      }
   }

   private static float[] colour(FieldEntity field) {
      return field instanceof CalderaEntity ? new float[]{1.0F, 0.42F, 0.1F} : new float[]{1.0F, 0.9F, 0.7F};
   }
}
