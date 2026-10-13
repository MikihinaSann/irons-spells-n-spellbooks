package com.grandsigil.client;

import com.grandsigil.GsPresence;
import com.grandsigil.config.GsConfig;
import com.grandsigil.entity.CoffinEntity;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class CoffinRenderer {
   private static final float[] SLAB = new float[]{0.055F, 0.048F, 0.078F};
   private static final float[] EDGE = new float[]{0.62F, 0.24F, 0.95F};
   private static final float[] CORE = new float[]{0.96F, 0.36F, 1.0F};
   private static final float[] PALE = new float[]{1.0F, 0.86F, 1.0F};
   private static final int SLABS = 9;
   private static final float RAGGED = 0.16F;
   private static final float DEPTH = 0.34F;
   private static final float STAGGER = 0.45F;
   private static final float HOLDS = 0.12F;
   private static final float HANDOVER = 0.92F;
   private static final float LANCE_OUT = -2.2F;
   private static final float LANCE_IN = 0.35F;
   private static final float LANCE_LENGTH = 1.9F;
   private static final double VIEW = 320.0;
   private static final ByteBufferBuilder BUILDER = new ByteBufferBuilder(32768);
   private static final BufferSource BUFFERS = MultiBufferSource.immediate(BUILDER);

   private static float ghost(float gone) {
      return gone <= 0.12F ? 1.0F : 0.92F * (1.0F - (gone - 0.12F) / 0.88F);
   }

   private CoffinRenderer() {
   }

   public static void render(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_TRANSLUCENT_BLOCKS) {
         if (GsPresence.any(11)) {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            if (level != null) {
               Camera camera = event.getCamera();
               Vec3 eye = camera.getPosition();
               float partial = event.getPartialTick();
               PoseStack pose = event.getPoseStack();
               boolean any = false;

               for (Entity entity : level.entitiesForRendering()) {
                  if (entity instanceof CoffinEntity) {
                     CoffinEntity coffin = (CoffinEntity)entity;
                     if (!(coffin.distanceToSqr(eye.x, eye.y, eye.z) > 102400.0)) {
                        any = true;
                        draw(pose, coffin, partial, eye);
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

   private static void draw(PoseStack pose, CoffinEntity coffin, float partial, Vec3 eye) {
      float up = coffin.sealed(partial);
      float gone = coffin.breaking(partial);
      if (!(up <= 0.004F) && !(gone >= 1.0F)) {
         int detail = (Integer)GsConfig.CLIENT.detail.get();
         float half = coffin.half();
         float tall = coffin.height();
         float age = (float)coffin.tickCount + partial;
         pose.pushPose();
         pose.translate(coffin.getX() - eye.x, coffin.getY() - eye.y, coffin.getZ() - eye.z);
         Matrix4f m = pose.last().pose();
         Vector3f viewer = new Vector3f((float)(eye.x - coffin.getX()), (float)(eye.y - coffin.getY()), (float)(eye.z - coffin.getZ()));
         float ghost = ghost(gone);
         boolean solid = ghost >= 1.0F;
         RenderType mass = solid ? GsRenderTypes.stone() : GsRenderTypes.glass();
         VertexConsumer stone = BUFFERS.getBuffer(mass);
         walls(m, stone, coffin, half, tall, up, ghost, detail);
         lances(m, stone, coffin, half, tall, gone, partial, false, viewer, ghost);
         BUFFERS.endBatch(mass);
         VertexConsumer glow = BUFFERS.getBuffer(GsRenderTypes.glow());
         seams(m, glow, half, tall, up, gone, age, detail);
         spill(m, glow, coffin, half, tall, up, gone, age);
         lances(m, glow, coffin, half, tall, gone, partial, true, viewer, ghost);
         BUFFERS.endBatch(GsRenderTypes.glow());
         VertexConsumer floor = BUFFERS.getBuffer(GsRenderTypes.floor());
         footprint(m, floor, half, up, gone, age);
         BUFFERS.endBatch(GsRenderTypes.floor());
         pose.popPose();
      }
   }

   private static void walls(Matrix4f m, VertexConsumer vc, CoffinEntity coffin, float half, float tall, float up, float ghost, int detail) {
      int count = slabs(half, detail);
      int id = coffin.getId();

      for (int side = 0; side < 4; side++) {
         for (int i = 0; i < count; i++) {
            int seed = hash(id, side * 97 + i);
            float lag = (float)(seed & 0xFF) / 255.0F * 0.45F;
            float own = Mth.clamp((up - lag) / Math.max(0.05F, 1.0F - lag), 0.0F, 1.0F);
            if (!(own <= 0.002F)) {
               float crest = tall * (0.84000003F + 0.16F * ((float)(seed >>> 8 & 0xFF) / 255.0F));
               float grown = crest * own;
               float depth = 0.34F * (0.7F + 0.6F * ((float)(seed >>> 16 & 0xFF) / 255.0F));
               float span = half / (float)count;
               float along = -half + span * (float)(2 * i + 1);
               float shade = 0.75F + 0.45F * ((float)(seed >>> 24 & 0xFF) / 255.0F);
               float cx;
               float cz;
               float hx;
               float hz;
               if (side < 2) {
                  cx = along;
                  cz = (float)(side == 0 ? -1 : 1) * (half + depth * 0.5F);
                  hx = span;
                  hz = depth * 0.5F;
               } else {
                  cx = (float)(side == 2 ? -1 : 1) * (half + depth * 0.5F);
                  cz = along;
                  hx = depth * 0.5F;
                  hz = span;
               }

               box(m, vc, cx, (grown - 0.6F) * 0.5F, cz, hx, (grown + 0.6F) * 0.5F, hz, SLAB[0] * shade, SLAB[1] * shade, SLAB[2] * shade, ghost);
            }
         }
      }
   }

   private static void seams(Matrix4f m, VertexConsumer vc, float half, float tall, float up, float gone, float age, int detail) {
      int count = slabs(half, detail);
      float fade = (1.0F - gone) * (1.0F - gone);
      if (!(fade <= 0.01F)) {
         float lit = tall * up;
         float span = half / (float)count;

         for (int side = 0; side < 4; side++) {
            for (int i = 1; i < count; i++) {
               float along = -half + span * 2.0F * (float)i;
               float pulse = 0.55F + 0.45F * Mth.sin(age * 0.09F + (float)(side * 31 + i) * 1.7F);
               float a = fade * pulse * 0.8F;
               float w = 0.09F;
               if (side < 2) {
                  float z = (float)(side == 0 ? -1 : 1) * (half + 0.2108F);
                  box(m, vc, along, lit * 0.5F - 0.2F, z, w, lit * 0.5F + 0.2F, 0.02F, EDGE[0], EDGE[1], EDGE[2], a);
               } else {
                  float x = (float)(side == 2 ? -1 : 1) * (half + 0.2108F);
                  box(m, vc, x, lit * 0.5F - 0.2F, along, 0.02F, lit * 0.5F + 0.2F, w, EDGE[0], EDGE[1], EDGE[2], a);
               }
            }
         }

         float rim = tall * up * 0.92F;
         float crown = fade * (0.5F + 0.5F * Mth.sin(age * 0.06F));

         for (int side = 0; side < 4; side++) {
            float e = half + 0.136F;
            if (side < 2) {
               box(m, vc, 0.0F, rim, side == 0 ? -e : e, half, 0.06F, 0.05F, CORE[0], CORE[1], CORE[2], crown * 0.75F);
            } else {
               box(m, vc, side == 2 ? -e : e, rim, 0.0F, 0.05F, 0.06F, half, CORE[0], CORE[1], CORE[2], crown * 0.75F);
            }
         }
      }
   }

   private static void spill(Matrix4f m, VertexConsumer vc, CoffinEntity coffin, float half, float tall, float up, float gone, float age) {
      if (coffin.stage() == 0) {
         float lit = tall * up;

         for (int side = 0; side < 4; side++) {
            float e = half * 0.97F;
            if (side < 2) {
               face(m, vc, -half, 0.1F, side == 0 ? -e : e, half, lit, side == 0 ? -e : e, CORE[0], CORE[1], CORE[2], 0.1F, 0.75F);
            } else {
               faceZ(m, vc, side == 2 ? -e : e, 0.1F, -half, side == 2 ? -e : e, lit, half, CORE[0], CORE[1], CORE[2], 0.1F, 0.75F);
            }
         }

         float boil = 0.55F + 0.35F * Mth.sin(age * 0.22F);
         flat(m, vc, half * 0.95F, lit + 0.15F, PALE[0], PALE[1], PALE[2], boil * up);
      } else {
         if (gone > 0.0F) {
            float burst = Mth.clamp(1.0F - gone * 1.6F, 0.0F, 1.0F);
            flat(m, vc, half * (1.0F + gone * 2.2F), tall * (0.4F + gone * 0.9F), CORE[0], CORE[1], CORE[2], burst * 0.85F);
         }
      }
   }

   private static void lances(
      Matrix4f m, VertexConsumer vc, CoffinEntity coffin, float half, float tall, float gone, float partial, boolean lit, Vector3f viewer, float ghost
   ) {
      if (coffin.stage() != 0) {
         float clock = coffin.stage() == 1 ? (float)coffin.stageAge() + partial : (float)(80 + coffin.stageAge()) + partial;
         float alpha = Mth.clamp(1.0F - gone * 1.4F, 0.0F, 1.0F);
         if (lit ? !(alpha <= 0.01F) : !(ghost <= 0.01F)) {
            int id = coffin.getId();

            for (int i = 0; i < 24; i++) {
               float since = clock - (float)CoffinEntity.born(i);
               if (!(since <= 0.0F)) {
                  float driven = Mth.clamp(since / 7.0F, 0.0F, 1.0F);
                  float bite = 1.0F - (1.0F - driven) * (1.0F - driven);
                  int seed = hash(id, i * 131 + 7);
                  int side = seed & 3;
                  float along = ((float)(seed >>> 4 & 0xFF) / 255.0F * 1.5F - 0.75F) * half;
                  float lift = ((float)(seed >>> 12 & 0xFF) / 255.0F * 0.75F + 0.12F) * coffin.height();
                  float skew = ((float)(seed >>> 20 & 0xFF) / 255.0F - 0.5F) * 0.5F;

                  Vector3f axis = switch (side) {
                     case 0 -> new Vector3f(skew, -0.12F, 1.0F);
                     case 1 -> new Vector3f(skew, -0.12F, -1.0F);
                     case 2 -> new Vector3f(1.0F, -0.12F, skew);
                     default -> new Vector3f(-1.0F, -0.12F, skew);
                  };
                  axis.normalize();

                  Vector3f seat = switch (side) {
                     case 0, 1 -> new Vector3f(along, lift, 0.0F);
                     default -> new Vector3f(0.0F, lift, along);
                  };
                  float length = half * 1.9F;
                  float girth = half * 0.055F;
                  float point = Mth.lerp(bite, -2.2F, 0.35F) * half;
                  Vector3f base = new Vector3f(axis).mul(point - length).add(seat);
                  if (lit) {
                     Geo.prismEdges(
                        m, vc, base, axis, length, girth, 0.68F, 6, 0.5F, viewer, girth * 0.22F, EDGE[0], EDGE[1], EDGE[2], alpha * 0.3F, alpha * 0.95F
                     );
                  } else {
                     Geo.prism(m, vc, base, axis, length, girth, 0.68F, 6, 0.5F, SLAB[0] * 1.6F, SLAB[1] * 1.6F, SLAB[2] * 1.9F, ghost, ghost);
                  }
               }
            }
         }
      }
   }

   private static void footprint(Matrix4f m, VertexConsumer vc, float half, float up, float gone, float age) {
      float a = (1.0F - gone) * (0.55F + 0.35F * Mth.sin(age * 0.1F)) * up;
      if (!(a <= 0.01F)) {
         float w = 0.16F;
         float e = half + 0.238F;

         for (int side = 0; side < 4; side++) {
            if (side < 2) {
               box(m, vc, 0.0F, 0.06F, side == 0 ? -e : e, e, 0.004F, w, EDGE[0], EDGE[1], EDGE[2], a);
            } else {
               box(m, vc, side == 2 ? -e : e, 0.06F, 0.0F, w, 0.004F, e, EDGE[0], EDGE[1], EDGE[2], a);
            }
         }
      }
   }

   private static int slabs(float half, int detail) {
      int base = 9 + Mth.floor(half - 4.5F);
      return detail >= 2 ? base : (detail == 1 ? Math.max(5, base * 2 / 3) : Math.max(4, base / 2));
   }

   private static void box(Matrix4f m, VertexConsumer vc, float cx, float cy, float cz, float hx, float hy, float hz, float r, float g, float b, float a) {
      float x0 = cx - hx;
      float x1 = cx + hx;
      float y0 = cy - hy;
      float y1 = cy + hy;
      float z0 = cz - hz;
      float z1 = cz + hz;
      quad(m, vc, x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1, r, g, b, a);
      quad(m, vc, x0, y1, z1, x1, y1, z1, x1, y1, z0, x0, y1, z0, r, g, b, a);
      float k = 0.82F;
      quad(m, vc, x0, y0, z0, x0, y1, z0, x1, y1, z0, x1, y0, z0, r, g, b, a);
      quad(m, vc, x1, y0, z1, x1, y1, z1, x0, y1, z1, x0, y0, z1, r, g, b, a);
      quad(m, vc, x0, y0, z1, x0, y1, z1, x0, y1, z0, x0, y0, z0, r * k, g * k, b * k, a);
      quad(m, vc, x1, y0, z0, x1, y1, z0, x1, y1, z1, x1, y0, z1, r * k, g * k, b * k, a);
   }

   private static void face(
      Matrix4f m, VertexConsumer vc, float x0, float y0, float z, float x1, float y1, float z1, float r, float g, float b, float aLow, float aHigh
   ) {
      quad(m, vc, x0, y0, z, x1, y0, z1, x1, y1, z1, x0, y1, z, r, g, b, aLow, aLow, aHigh, aHigh);
   }

   private static void faceZ(
      Matrix4f m, VertexConsumer vc, float x, float y0, float z0, float x1, float y1, float z1, float r, float g, float b, float aLow, float aHigh
   ) {
      quad(m, vc, x, y0, z0, x1, y0, z1, x1, y1, z1, x, y1, z0, r, g, b, aLow, aLow, aHigh, aHigh);
   }

   private static void flat(Matrix4f m, VertexConsumer vc, float half, float y, float r, float g, float b, float a) {
      if (!(a <= 0.004F)) {
         quad(m, vc, -half, y, -half, half, y, -half, half, y, half, -half, y, half, r, g, b, a);
      }
   }

   private static void quad(
      Matrix4f m,
      VertexConsumer vc,
      float x0,
      float y0,
      float z0,
      float x1,
      float y1,
      float z1,
      float x2,
      float y2,
      float z2,
      float x3,
      float y3,
      float z3,
      float r,
      float g,
      float b,
      float a
   ) {
      quad(m, vc, x0, y0, z0, x1, y1, z1, x2, y2, z2, x3, y3, z3, r, g, b, a, a, a, a);
   }

   private static void quad(
      Matrix4f m,
      VertexConsumer vc,
      float x0,
      float y0,
      float z0,
      float x1,
      float y1,
      float z1,
      float x2,
      float y2,
      float z2,
      float x3,
      float y3,
      float z3,
      float r,
      float g,
      float b,
      float a0,
      float a1,
      float a2,
      float a3
   ) {
      vc.addVertex(m, x0, y0, z0).setColor(r, g, b, a0).setUv(0.5F, 0.5F);
      vc.addVertex(m, x1, y1, z1).setColor(r, g, b, a1).setUv(0.5F, 0.5F);
      vc.addVertex(m, x2, y2, z2).setColor(r, g, b, a2).setUv(0.5F, 0.5F);
      vc.addVertex(m, x3, y3, z3).setColor(r, g, b, a3).setUv(0.5F, 0.5F);
   }

   private static int hash(int a, int b) {
      int h = a * 73856093 ^ b * 19349663;
      h ^= h >>> 13;
      h *= 1540483477;
      return h ^ h >>> 15;
   }
}
