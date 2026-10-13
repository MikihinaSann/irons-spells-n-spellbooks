package com.grandsigil.client;

import com.grandsigil.GrandSigil;
import com.grandsigil.GsPresence;
import com.grandsigil.config.GsConfig;
import com.grandsigil.entity.GateEntity;
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

public final class GateRenderer {
   private static final ResourceLocation BEAM = GrandSigil.id("textures/entity/beam.png");
   private static final ResourceLocation SCRIPT = GrandSigil.id("textures/entity/script.png");
   private static final ResourceLocation RUNES = GrandSigil.id("textures/entity/runes.png");
   private static final float[] WHITE = new float[]{1.0F, 1.0F, 1.0F};
   private static final float[] COLD = new float[]{0.78F, 0.93F, 1.0F};
   private static final float[] GOLD = new float[]{1.0F, 0.88F, 0.58F};
   private static final int RAYS = 26;
   private static final int RING_GLYPHS = 8;
   private static final float ORBIT = 1.45F;
   private static final float LOW_RING = 7.0F;
   private static final float HIGH_RING = 19.0F;
   private static final float ORBIT_SPEED = 0.03F;
   private static final float GLYPH = 2.1F;
   private static final float[] INK = new float[]{0.24F, 0.17F, 0.05F};
   private static final float[] EMBER = new float[]{1.0F, 0.88F, 0.58F};
   private static final double VIEW = 400.0;
   private static final ByteBufferBuilder BUILDER = new ByteBufferBuilder(32768);
   private static final BufferSource BUFFERS = MultiBufferSource.immediate(BUILDER);

   private GateRenderer() {
   }

   public static void render(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_TRANSLUCENT_BLOCKS) {
         if (GsPresence.any(4)) {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            if (level != null) {
               Camera camera = event.getCamera();
               Vec3 eye = camera.getPosition();
               float partial = event.getPartialTick();
               PoseStack pose = event.getPoseStack();
               boolean any = false;

               for (Entity entity : level.entitiesForRendering()) {
                  if (entity instanceof GateEntity) {
                     GateEntity gate = (GateEntity)entity;
                     if (!(gate.distanceToSqr(eye.x, eye.y, eye.z) > 160000.0)) {
                        any = true;
                        draw(pose, gate, partial, eye, camera);
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

   private static void draw(PoseStack pose, GateEntity gate, float partial, Vec3 eye, Camera camera) {
      int detail = (Integer)GsConfig.CLIENT.detail.get();
      float open = gate.opening(partial);
      if (!(open <= 0.004F)) {
         float radius = gate.radius();
         float age = (float)gate.tickCount + partial;
         pose.pushPose();
         pose.translate(gate.getX() - eye.x, gate.getY() - eye.y, gate.getZ() - eye.z);
         Matrix4f m = pose.last().pose();
         Vector3f viewer = new Vector3f((float)(eye.x - gate.getX()), (float)(eye.y - gate.getY()), (float)(eye.z - gate.getZ()));
         VertexConsumer floor = BUFFERS.getBuffer(GsRenderTypes.floor());
         ground(m, floor, radius, open, age, detail);
         BUFFERS.endBatch(GsRenderTypes.floor());
         VertexConsumer glow = BUFFERS.getBuffer(GsRenderTypes.glow());
         aperture(m, glow, open, age, detail);
         ribs(m, glow, viewer, open, age);
         corridor(m, glow, open, age, detail);
         BUFFERS.endBatch(GsRenderTypes.glow());
         VertexConsumer script = BUFFERS.getBuffer(GsRenderTypes.glow(SCRIPT));
         Geo.script(
            m,
            script,
            Terrain.FLAT,
            119.6F,
            13.799999F,
            18.599998F,
            detail >= 2 ? 72 : 36,
            age * 0.006F,
            open,
            10.0F,
            -age * 0.004F,
            COLD[0],
            COLD[1],
            COLD[2],
            open * 0.8F
         );
         BUFFERS.endBatch(GsRenderTypes.glow(SCRIPT));
         VertexConsumer beam = BUFFERS.getBuffer(GsRenderTypes.glow(BEAM));
         shaft(m, beam, open, age, detail);
         BUFFERS.endBatch(GsRenderTypes.glow(BEAM));
         VertexConsumer ink = BUFFERS.getBuffer(GsRenderTypes.ink(RUNES));
         seated(m, ink, open, age, detail, true);
         orbiting(m, ink, camera, open, age, detail, true);
         BUFFERS.endBatch(GsRenderTypes.ink(RUNES));
         VertexConsumer runes = BUFFERS.getBuffer(GsRenderTypes.glow(RUNES));
         seated(m, runes, open, age, detail, false);
         orbiting(m, runes, camera, open, age, detail, false);
         BUFFERS.endBatch(GsRenderTypes.glow(RUNES));
         pose.popPose();
      }
   }

   private static void aperture(Matrix4f m, VertexConsumer vc, float open, float age, int detail) {
      int segments = detail >= 2 ? 96 : 48;
      float y = 120.0F;
      float r = 12.0F * (0.6F + 1.0F * open);
      float spin = age * 0.004F;
      Geo.band(m, vc, Terrain.FLAT, y, r * 0.98F, r * 1.06F, segments, spin, 1.0F, WHITE[0], WHITE[1], WHITE[2], open, open * 0.3F);
      Geo.band(m, vc, Terrain.FLAT, y, r * 1.55F, r * 1.62F, segments, -spin, open, COLD[0], COLD[1], COLD[2], open * 0.8F, open * 0.8F);
      Geo.disc(m, vc, Terrain.FLAT, y - 0.2F, r * 0.96F, segments, WHITE[0], WHITE[1], WHITE[2], open * 0.65F, 0.0F);
      if (detail >= 1) {
         Geo.ticks(
            m, vc, Terrain.FLAT, y, r * 1.08F, r * 1.26F, r * 1.44F, detail >= 2 ? 48 : 24, 6, spin, r * 0.012F, open, COLD[0], COLD[1], COLD[2], open * 0.7F
         );
         Geo.star(m, vc, Terrain.FLAT, y, r * 0.9F, 8, 3, -spin, r * 0.014F, open, WHITE[0], WHITE[1], WHITE[2], open * 0.5F);
      }
   }

   private static void ribs(Matrix4f m, VertexConsumer vc, Vector3f viewer, float open, float age) {
      float y = 120.0F;
      float r = 12.0F * (0.6F + 1.0F * open) * 1.6F;
      float peak = y + 22.0F * open;

      for (int i = 0; i < 8; i++) {
         float a = (float)((Math.PI * 2) * (double)i / 8.0) + age * 0.002F;
         Vector3f from = new Vector3f(Mth.cos(a) * r, y, Mth.sin(a) * r);
         Vector3f control = new Vector3f(Mth.cos(a) * r * 0.75F, peak, Mth.sin(a) * r * 0.75F);
         Vector3f to = new Vector3f(0.0F, peak + 4.0F, 0.0F);
         Geo.arc(m, vc, from, control, to, viewer, 10, 0.55F, 0.5F, GOLD[0], GOLD[1], GOLD[2], open * 0.85F, open * 0.15F);
      }
   }

   private static void corridor(Matrix4f m, VertexConsumer vc, float open, float age, int detail) {
      int segments = detail >= 2 ? 40 : 20;
      float top = 120.0F;
      float r = 12.0F;
      Geo.columnPlain(m, vc, 0.0F, 0.0F, 0.2F, top, r * 1.08F, r * 1.02F, segments, 0.0F, COLD[0], COLD[1], COLD[2], open * 0.18F, open * 0.05F);
      Geo.columnPlain(m, vc, 0.0F, 0.0F, 0.2F, top, r * 0.55F, r * 0.5F, segments, 0.0F, WHITE[0], WHITE[1], WHITE[2], open * 0.13F, open * 0.04F);
      if (detail >= 1) {
         for (int i = 0; i < 26; i++) {
            float seed = (float)i * 2.399963F;
            float a = seed + age * (0.0015F + (float)(i % 4) * 6.0E-4F);
            float rr = r * (0.15F + 0.85F * Mth.abs(Mth.sin(seed * 3.1F)));
            float x = Mth.cos(a) * rr;
            float z = Mth.sin(a) * rr;
            float fade = 0.35F + 0.4F * Mth.sin(age * 0.03F + seed);
            Geo.columnPlain(
               m, vc, x, z, 0.3F, top * (0.75F + 0.25F * Mth.abs(Mth.cos(seed))), 0.42F, 0.1F, 5, 0.0F, WHITE[0], WHITE[1], WHITE[2], open * fade * 0.55F, 0.0F
            );
         }
      }
   }

   private static void seated(Matrix4f m, VertexConsumer vc, float open, float age, int detail, boolean asInk) {
      if (detail >= 1) {
         float y = 120.0F - (asInk ? 0.7F : 0.6F);
         float r = 12.0F * (0.6F + 1.0F * open) * 1.34F;
         float spin = age * 0.004F;
         int count = 12;
         float[] colour = asInk ? INK : EMBER;

         for (int i = 0; i < count; i++) {
            float a = spin + (float)((Math.PI * 2) * (double)i / (double)count);
            int cell = (i * 5 + 3) % 16;
            float pulse = 0.78F + 0.22F * Mth.sin(age * 0.05F + (float)i * 0.9F);
            Geo.seat(
               m,
               vc,
               Terrain.FLAT,
               y,
               Mth.cos(a) * r,
               Mth.sin(a) * r,
               2.4F,
               a,
               (float)(cell % 4) * 0.25F,
               (float)(cell / 4) * 0.25F,
               (float)(cell % 4) * 0.25F + 0.25F,
               (float)(cell / 4) * 0.25F + 0.25F,
               colour[0],
               colour[1],
               colour[2],
               open * pulse * (asInk ? 0.9F : 0.75F)
            );
         }
      }
   }

   private static void orbiting(Matrix4f m, VertexConsumer vc, Camera camera, float open, float age, int detail, boolean asInk) {
      if (!(open <= 0.01F)) {
         int count = detail >= 2 ? 8 : 4;
         float[] colour = asInk ? INK : EMBER;
         float radius = 17.400002F;
         Vector3f right = new Vector3f(camera.getLeftVector()).mul(-1.0F);
         Vector3f up = new Vector3f(camera.getUpVector());

         for (int ring = 0; ring < 2; ring++) {
            float height = ring == 0 ? 7.0F : 19.0F;
            float turn = age * 0.03F * (ring == 0 ? 1.0F : -0.78F);
            float r = radius * (ring == 0 ? 1.0F : 1.18F);

            for (int i = 0; i < count; i++) {
               float seed = (float)ring * 3.7F + (float)i * 2.399963F;
               float a = turn + (float)((Math.PI * 2) * (double)i / (double)count);
               float bob = Mth.sin(age * 0.05F + seed) * 1.6F;
               float x = Mth.cos(a) * r;
               float z = Mth.sin(a) * r;
               float y = height * open + bob;
               float alpha = open * (0.85F + 0.15F * Mth.sin(age * 0.04F + seed));
               int cell = (ring * 5 + i * 7 + 1) % 16;
               float half = asInk ? 2.1F : 2.4779997F;
               Geo.billboard(
                  m,
                  vc,
                  x,
                  y,
                  z,
                  right,
                  up,
                  half,
                  (float)(cell % 4) * 0.25F,
                  (float)(cell / 4) * 0.25F,
                  (float)(cell % 4) * 0.25F + 0.25F,
                  (float)(cell / 4) * 0.25F + 0.25F,
                  colour[0],
                  colour[1],
                  colour[2],
                  alpha * (asInk ? 1.0F : 0.7F)
               );
            }
         }
      }
   }

   private static void shaft(Matrix4f m, VertexConsumer vc, float open, float age, int detail) {
      Geo.column(
         m,
         vc,
         0.0F,
         0.0F,
         0.2F,
         120.0F,
         9.84F,
         9.36F,
         detail >= 2 ? 40 : 20,
         0.0F,
         14.0F,
         age * 0.01F,
         WHITE[0],
         WHITE[1],
         WHITE[2],
         open * 0.26F,
         open * 0.05F
      );
   }

   private static void ground(Matrix4f m, VertexConsumer vc, float radius, float open, float age, int detail) {
      int segments = detail >= 2 ? 128 : 64;
      float pulse = 0.9F + 0.1F * Mth.sin(age * 0.08F);
      Geo.disc(m, vc, Terrain.FLAT, 0.03F, 13.200001F, segments, WHITE[0], WHITE[1], WHITE[2], open * 0.6F * pulse, 0.0F);
      Geo.band(m, vc, Terrain.FLAT, 0.04F, radius * 0.96F, radius, segments, age * 0.003F, open, COLD[0], COLD[1], COLD[2], open * 0.5F, 0.0F);
      if (detail >= 1) {
         Geo.ticks(
            m,
            vc,
            Terrain.FLAT,
            0.04F,
            radius * 0.9F,
            radius * 0.95F,
            radius * 0.99F,
            detail >= 2 ? 72 : 36,
            6,
            -age * 0.003F,
            radius * 0.006F,
            open,
            COLD[0],
            COLD[1],
            COLD[2],
            open * 0.4F
         );
      }
   }
}
