package com.grandsigil.client;

import com.grandsigil.GrandSigil;
import com.grandsigil.GsPresence;
import com.grandsigil.config.GsConfig;
import com.grandsigil.entity.ArenaEntity;
import com.grandsigil.entity.Terrain;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class ArenaRenderer {
   private static final ResourceLocation BLOOD = GrandSigil.id("textures/entity/blood.png");
   private static final ResourceLocation SCRIPT = GrandSigil.id("textures/entity/script.png");
   private static final float[] MASS = new float[]{0.2F, 0.014F, 0.022F};
   private static final float[] BONE = new float[]{0.88F, 0.85F, 0.76F};
   private static final float[] SHADE = new float[]{0.52F, 0.49F, 0.43F};
   private static final float[] DEEP = new float[]{0.46F, 0.02F, 0.03F};
   private static final float[] BLEED = new float[]{0.92F, 0.055F, 0.075F};
   private static final float[] HOT = new float[]{1.0F, 0.22F, 0.22F};
   private static final float THICK = 2.6F;
   private static final float STEP = 0.9F;
   private static final float COLUMN = 0.62F;
   private static final float PIER = 0.3F;
   private static final int TEETH = 48;
   private static final int FALL_STEPS = 10;
   private static final float LANCE = 5.6F;
   private static final float GIRTH = 0.26F;
   private static final float HEAD = 0.26F;
   private static final float BURIED = 1.4F;
   private static final float LEAN = 0.3F;
   private static final double VIEW = 400.0;
   private static final ByteBufferBuilder BUILDER = new ByteBufferBuilder(524288);
   private static final BufferSource BUFFERS = MultiBufferSource.immediate(BUILDER);
   private static final Map<Integer, ArenaRenderer.Cached> CACHE = new HashMap<>();

   private ArenaRenderer() {
   }

   public static void render(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_TRANSLUCENT_BLOCKS) {
         if (GsPresence.any(7)) {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            if (level != null) {
               Camera camera = event.getCamera();
               Vec3 eye = camera.getPosition();
               float partial = event.getPartialTick();
               PoseStack pose = event.getPoseStack();
               boolean any = false;

               for (Entity entity : level.entitiesForRendering()) {
                  if (entity instanceof ArenaEntity) {
                     ArenaEntity arena = (ArenaEntity)entity;
                     if (!(arena.distanceToSqr(eye.x, eye.y, eye.z) > 160000.0)) {
                        any = true;
                        draw(pose, arena, level, partial, eye);
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

   private static void draw(PoseStack pose, ArenaEntity arena, ClientLevel level, float partial, Vec3 eye) {
      int detail = (Integer)GsConfig.CLIENT.detail.get();
      float built = arena.built(partial);
      float fall = arena.falling(partial);
      if (!(built <= 0.004F) && !(fall >= 1.0F)) {
         float radius = arena.radius();
         float age = (float)arena.tickCount + partial;
         float tall = arena.height() * built;
         ArenaRenderer.Cached cached = CACHE.computeIfAbsent(arena.getId(), id -> build(arena, level, radius));
         pose.pushPose();
         pose.translate(arena.getX() - eye.x, arena.getY() - eye.y, arena.getZ() - eye.z);
         Matrix4f m = pose.last().pose();
         VertexConsumer stone = BUFFERS.getBuffer(GsRenderTypes.stone());
         backing(m, stone, cached, radius, tall, fall, detail);
         wall(m, stone, cached, radius, tall, fall, age, detail);
         BUFFERS.endBatch(GsRenderTypes.stone());
         VertexConsumer grain = BUFFERS.getBuffer(GsRenderTypes.matter(BLOOD));
         grain(m, grain, cached, radius, tall, fall, age, detail);
         cascades(m, grain, cached, radius, tall, age, fall, arena.fed(), detail);
         BUFFERS.endBatch(GsRenderTypes.matter(BLOOD));
         Vector3f viewer = new Vector3f((float)(eye.x - arena.getX()), (float)(eye.y - arena.getY()), (float)(eye.z - arena.getZ()));
         VertexConsumer shafts = BUFFERS.getBuffer(GsRenderTypes.glass());
         rain(m, shafts, cached, arena, viewer, partial, false);
         BUFFERS.endBatch(GsRenderTypes.glass());
         VertexConsumer glow = BUFFERS.getBuffer(GsRenderTypes.glow(SCRIPT));
         band(m, glow, radius, tall, age, fall, built);
         BUFFERS.endBatch(GsRenderTypes.glow(SCRIPT));
         VertexConsumer floor = BUFFERS.getBuffer(GsRenderTypes.floor());
         ground(m, floor, cached, radius, built, age, fall, detail);
         BUFFERS.endBatch(GsRenderTypes.floor());
         VertexConsumer cores = BUFFERS.getBuffer(GsRenderTypes.glow());
         rain(m, cores, cached, arena, viewer, partial, true);
         BUFFERS.endBatch(GsRenderTypes.glow());
         pose.popPose();
      }
   }

   private static void backing(Matrix4f m, VertexConsumer vc, ArenaRenderer.Cached cached, float radius, float tall, float fall, int detail) {
      int bays = detail >= 2 ? 48 : 24;
      int steps = detail >= 2 ? 8 : 4;
      float r = radius + 0.6F;

      for (int b = 0; b < bays; b++) {
         float a0 = (float)((Math.PI * 2) * (double)b / (double)bays);
         float a1 = (float)((Math.PI * 2) * (double)(b + 1) / (double)bays);
         float shake = cached.jitter[b / 2 % cached.jitter.length];
         float dropped = fall > 0.0F ? sag(fall, shake) : 0.0F;
         if (!(dropped >= 1.0F)) {
            float top = tall * (1.0F - dropped);
            float base = -tall * dropped;

            for (int i = 0; i < steps; i++) {
               float y0 = base + (top - base) * (float)i / (float)steps;
               float y1 = base + (top - base) * (float)(i + 1) / (float)steps;
               float k0 = 0.55F + 0.45F * ((float)i / (float)steps);
               float k1 = 0.55F + 0.45F * ((float)(i + 1) / (float)steps);
               panel(m, vc, a0, a1, r, y0, y1, k0, k1);
            }
         }
      }
   }

   private static void panel(Matrix4f m, VertexConsumer vc, float a0, float a1, float r, float y0, float y1, float k0, float k1) {
      float c0 = Mth.cos(a0);
      float s0 = Mth.sin(a0);
      float c1 = Mth.cos(a1);
      float s1 = Mth.sin(a1);
      vc.addVertex(m, c0 * r, y0, s0 * r).setColor(MASS[0] * k0, MASS[1] * k0, MASS[2] * k0, 1.0F).setUv(0.5F, 0.5F);
      vc.addVertex(m, c1 * r, y0, s1 * r).setColor(MASS[0] * k0, MASS[1] * k0, MASS[2] * k0, 1.0F).setUv(0.5F, 0.5F);
      vc.addVertex(m, c1 * r, y1, s1 * r).setColor(MASS[0] * k1, MASS[1] * k1, MASS[2] * k1, 1.0F).setUv(0.5F, 0.5F);
      vc.addVertex(m, c0 * r, y1, s0 * r).setColor(MASS[0] * k1, MASS[1] * k1, MASS[2] * k1, 1.0F).setUv(0.5F, 0.5F);
   }

   private static void grain(Matrix4f m, VertexConsumer vc, ArenaRenderer.Cached cached, float radius, float tall, float fall, float age, int detail) {
      int bays = detail >= 2 ? 24 : 12;
      int steps = detail >= 2 ? 6 : 3;
      float slide = age * 6.0E-4F;
      float r = radius + 0.5F;

      for (int b = 0; b < bays; b++) {
         float a0 = (float)((Math.PI * 2) * (double)b / (double)bays);
         float a1 = (float)((Math.PI * 2) * (double)(b + 1) / (double)bays);
         float shake = cached.jitter[b % cached.jitter.length];
         float dropped = fall > 0.0F ? sag(fall, shake) : 0.0F;
         if (!(dropped >= 1.0F)) {
            float top = tall * (1.0F - dropped);
            float base = -tall * dropped;

            for (int i = 0; i < steps; i++) {
               float y0 = base + (top - base) * (float)i / (float)steps;
               float y1 = base + (top - base) * (float)(i + 1) / (float)steps;
               skin(m, vc, a0, a1, r, y0, y1, a0 * 2.2F + slide, y0 * 0.045F, y1 * 0.045F);
            }
         }
      }
   }

   private static void skin(Matrix4f m, VertexConsumer vc, float a0, float a1, float r, float y0, float y1, float u, float v0, float v1) {
      float c0 = Mth.cos(a0);
      float s0 = Mth.sin(a0);
      float c1 = Mth.cos(a1);
      float s1 = Mth.sin(a1);
      float du = 0.5F;
      vc.addVertex(m, c0 * r, y0, s0 * r).setColor(DEEP[0], DEEP[1], DEEP[2], 0.5F).setUv(u, v0);
      vc.addVertex(m, c1 * r, y0, s1 * r).setColor(DEEP[0], DEEP[1], DEEP[2], 0.5F).setUv(u + du, v0);
      vc.addVertex(m, c1 * r, y1, s1 * r).setColor(DEEP[0], DEEP[1], DEEP[2], 0.5F).setUv(u + du, v1);
      vc.addVertex(m, c0 * r, y1, s0 * r).setColor(DEEP[0], DEEP[1], DEEP[2], 0.5F).setUv(u, v1);
   }

   private static void wall(Matrix4f m, VertexConsumer vc, ArenaRenderer.Cached cached, float radius, float tall, float fall, float age, int detail) {
      int bays = detail >= 2 ? 24 : 12;
      float step = (float)((Math.PI * 2) / (double)bays);
      float tier = tall / 3.0F;

      for (int b = 0; b < bays; b++) {
         float a0 = (float)b * step;
         float shake = cached.jitter[b % cached.jitter.length];
         float dropped = fall > 0.0F ? sag(fall, shake) : 0.0F;
         if (!(dropped >= 1.0F)) {
            float lean = fall * (0.6F + shake) * 6.0F;

            for (int t = 0; t < 3; t++) {
               float y0 = tier * (float)t - tall * dropped;
               float y1 = y0 + tier;
               float r = radius + shake * 0.5F - (float)t * 0.9F - lean * (float)(t + 1) * 0.2F;
               float shadeTop = 1.0F - (float)t * 0.06F;
               pier(m, vc, a0 - step * 0.3F * 0.5F, r, y0, y1, tier, shadeTop);
               pier(m, vc, a0 + step * 0.3F * 0.5F, r, y0, y1, tier, shadeTop);
               arch(m, vc, a0, step, r, y0, tier, shadeTop, detail);
               slab(m, vc, a0, step, r, y0, shadeTop);
            }

            float rimY = tall * (1.0F - dropped);
            float rimR = radius + shake * 0.5F - 2.6999998F - lean * 0.8F;
            teeth(m, vc, a0, step, rimR, rimY, bays);
         }
      }
   }

   private static void pier(Matrix4f m, VertexConsumer vc, float a, float r, float y0, float y1, float tier, float shade) {
      float c = Mth.cos(a);
      float s = Mth.sin(a);
      float half = tier * 0.62F * 0.14F;
      Vector3f base = new Vector3f(c * r, y0, s * r);
      Vector3f axis = new Vector3f(0.0F, 1.0F, 0.0F);
      float body = (y1 - y0) * 0.86F;
      Geo.prism(m, vc, base, axis, body, half, 0.99F, 6, 0.12F, BONE[0] * shade, BONE[1] * shade, BONE[2] * shade, 1.0F, 0.92F);
      Geo.prism(
         m,
         vc,
         new Vector3f(c * r, y0 + body, s * r),
         axis,
         (y1 - y0) * 0.14F,
         half * 1.5F,
         0.99F,
         6,
         0.0F,
         BONE[0] * shade,
         BONE[1] * shade,
         BONE[2] * shade,
         1.0F,
         1.0F
      );
   }

   private static void arch(Matrix4f m, VertexConsumer vc, float a, float step, float r, float y0, float tier, float shade, int detail) {
      int spans = detail >= 2 ? 9 : 5;
      float rise = tier * 0.58F;
      float wide = step * 0.34F;
      float top = y0 + tier * 0.86F;

      for (int i = 0; i < spans; i++) {
         float t0 = (float)i / (float)spans;
         float t1 = (float)(i + 1) / (float)spans;
         float o0 = Mth.cos((float) Math.PI * t0) * wide;
         float o1 = Mth.cos((float) Math.PI * t1) * wide;
         float h0 = top - rise * Mth.sin((float) Math.PI * t0);
         float h1 = top - rise * Mth.sin((float) Math.PI * t1);
         block(m, vc, a + o0, a + o1, r, r, h0, h1, 1.3F, tier * 0.1F, BONE[0] * shade, BONE[1] * shade, BONE[2] * shade);
      }
   }

   private static void slab(Matrix4f m, VertexConsumer vc, float a, float step, float r, float y, float shade) {
      block(m, vc, a - step * 0.5F, a + step * 0.5F, r, r, y, y, 2.6F, 0.55F, SHADE[0] * shade, SHADE[1] * shade, SHADE[2] * shade);
   }

   private static void teeth(Matrix4f m, VertexConsumer vc, float a, float step, float r, float y, int bays) {
      int per = Math.max(1, 48 / bays);

      for (int i = 0; i < per; i++) {
         float t = a - step * 0.5F + step * ((float)i + 0.5F) / (float)per;
         block(m, vc, t - step * 0.14F, t + step * 0.14F, r, r, y, y, 2.08F, 2.2F, BONE[0], BONE[1], BONE[2]);
      }
   }

   private static void cascades(
      Matrix4f m, VertexConsumer vc, ArenaRenderer.Cached cached, float radius, float tall, float age, float fall, int fed, int detail
   ) {
      int bays = detail >= 2 ? 24 : 12;
      float step = (float)((Math.PI * 2) / (double)bays);
      float slide = age * 0.035F;
      float gorged = Math.min(1.0F, (float)fed * 0.1F);
      float alpha = (1.0F - fall) * (0.9F + 0.25F * gorged);
      float wider = 1.0F + 0.55F * gorged;

      for (int b = 0; b < bays; b++) {
         float a = (float)b * step;
         float shake = cached.jitter[b % cached.jitter.length];
         float flow = 0.7F + 0.5F * Mth.abs(Mth.sin((float)b * 2.4F));
         float r = radius + 0.45F + shake * 0.1F;

         for (int i = 0; i < 10; i++) {
            float t0 = (float)i / 10.0F;
            float t1 = (float)(i + 1) / 10.0F;
            float y0 = tall * (1.0F - t0);
            float y1 = tall * (1.0F - t1);
            float w0 = step * (0.1F + 0.16F * t0) * wider;
            float w1 = step * (0.1F + 0.16F * t1) * wider;
            float a0 = alpha * fadeIn(t0);
            float a1 = alpha * fadeIn(t1);
            sheet(m, vc, a - w0, a + w0, a - w1, a + w1, r, y0, y1, t0 * 2.2F - slide * flow, t1 * 2.2F - slide * flow, a0, a1);
         }
      }
   }

   private static void band(Matrix4f m, VertexConsumer vc, float radius, float tall, float age, float fall, float built) {
      float r = radius - 2.6999998F;
      float pulse = 0.7F + 0.3F * Mth.sin(age * 0.05F);
      Geo.script(
         m,
         vc,
         Terrain.FLAT,
         tall * 0.99F,
         r - 1.2F,
         r + 1.2F,
         96,
         age * 0.002F,
         built,
         24.0F,
         -age * 0.003F,
         HOT[0],
         HOT[1],
         HOT[2],
         pulse * (1.0F - fall) * 0.9F
      );
   }

   private static void ground(Matrix4f m, VertexConsumer vc, ArenaRenderer.Cached cached, float radius, float built, float age, float fall, int detail) {
      int segments = detail >= 2 ? 96 : 48;
      float alpha = 0.55F * built * (1.0F + fall);
      Geo.band(m, vc, cached.floor, 0.05F, radius * 0.975F, radius, segments, age * 0.003F, 1.0F, HOT[0], HOT[1], HOT[2], alpha, 0.0F);
      Geo.ticks(
         m, vc, cached.floor, 0.05F, radius * 0.9F, radius * 0.95F, radius * 1.0F, 24, 4, -age * 0.004F, 0.3F, 1.0F, BLEED[0], BLEED[1], BLEED[2], alpha * 0.8F
      );
   }

   private static void rain(Matrix4f m, VertexConsumer vc, ArenaRenderer.Cached cached, ArenaEntity arena, Vector3f viewer, float partial, boolean lit) {
      if (cached.sky != null) {
         float clock = arena.loosed(partial);
         int planted = Math.min(arena.planted(partial), cached.ground.size());
         float age = (float)arena.tickCount + partial;
         float fall = arena.falling(partial);
         float alpha = 1.0F - fall;

         for (int i = 0; i < cached.sky.size(); i++) {
            ArenaRenderer.Spear spear = cached.sky.get(i);
            float since = (clock - (float)i) / 0.95F;
            if (!(since > 16.0F)) {
               float y;
               float speed;
               if (since <= 0.0F) {
                  if (since < -60.0F) {
                     continue;
                  }

                  y = spear.y() + Mth.sin(age * 0.05F + spear.phase()) * 0.7F;
                  speed = 0.0F;
               } else {
                  float t = since / 16.0F;
                  float drop = t * t * t;
                  y = spear.y() * (1.0F - drop) + cached.floor.lift(spear.x(), spear.z()) * drop;
                  speed = t;
               }

               if (!(y < 0.2F)) {
                  one(
                     m,
                     vc,
                     viewer,
                     spear.x(),
                     y,
                     spear.z(),
                     spear.length() * (1.0F + speed * 1.7F),
                     spear.girth(),
                     spear.lean() * (1.0F - speed),
                     spear.roll(),
                     1.0F + speed * 2.2F,
                     alpha,
                     lit
                  );
               }
            }
         }

         for (int ix = 0; ix < planted; ix++) {
            ArenaRenderer.Spear spear = cached.ground.get(ix);
            float floor = cached.floor.lift(spear.x(), spear.z());
            float fresh = Mth.clamp(1.0F - (float)(planted - ix) / 26.0F, 0.0F, 1.0F);
            one(
               m,
               vc,
               viewer,
               spear.x(),
               floor + spear.length() * 0.5F - 1.4F,
               spear.z(),
               spear.length(),
               spear.girth(),
               spear.lean(),
               spear.roll(),
               0.3F + 0.9F * fresh,
               alpha,
               lit
            );
         }
      }
   }

   private static void one(
      Matrix4f m,
      VertexConsumer vc,
      Vector3f viewer,
      float x,
      float y,
      float z,
      float length,
      float girth,
      float lean,
      float roll,
      float heat,
      float alpha,
      boolean lit
   ) {
      float tipX = x + Mth.cos(roll) * lean * length * 0.5F;
      float tipZ = z + Mth.sin(roll) * lean * length * 0.5F;
      Vector3f tip = new Vector3f(tipX, y - length * 0.5F, tipZ);
      Vector3f butt = new Vector3f(x - Mth.cos(roll) * lean * length * 0.5F, y + length * 0.5F, z - Mth.sin(roll) * lean * length * 0.5F);
      Vector3f neck = new Vector3f(tip).lerp(butt, 0.26F);
      if (!lit) {
         Geo.taper(m, vc, neck, butt, viewer, girth, girth * 0.35F, BONE[0], BONE[1], BONE[2], alpha * 0.95F, alpha * 0.8F);
         Geo.taper(m, vc, neck, tip, viewer, girth * 1.35F, 0.01F, SHADE[0], SHADE[1], SHADE[2], alpha, alpha);
      } else {
         Geo.taper(m, vc, butt, tip, viewer, girth * 0.34F, 0.01F, BLEED[0], BLEED[1], BLEED[2], alpha * 0.5F * heat, alpha * 0.95F * heat);
      }
   }

   private static void block(
      Matrix4f m, VertexConsumer vc, float a0, float a1, float r0, float r1, float y0, float y1, float thick, float rise, float r, float g, float b
   ) {
      float c0 = Mth.cos(a0);
      float s0 = Mth.sin(a0);
      float c1 = Mth.cos(a1);
      float s1 = Mth.sin(a1);
      float in0 = r0 - thick;
      float in1 = r1 - thick;
      face(m, vc, c0 * r0, y0, s0 * r0, c1 * r1, y1, s1 * r1, rise, r, g, b, 1.0F);
      face(m, vc, c0 * in0, y0, s0 * in0, c1 * in1, y1, s1 * in1, rise, r, g, b, 0.72F);
      cap(m, vc, c0, s0, c1, s1, r0, r1, in0, in1, y0 + rise, y1 + rise, r, g, b, 1.14F);
      cap(m, vc, c0, s0, c1, s1, r0, r1, in0, in1, y0, y1, r, g, b, 0.5F);
   }

   private static void face(
      Matrix4f m, VertexConsumer vc, float x0, float y0, float z0, float x1, float y1, float z1, float rise, float r, float g, float b, float shade
   ) {
      vc.addVertex(m, x0, y0, z0).setColor(r * shade, g * shade, b * shade, 1.0F).setUv(0.5F, 0.5F);
      vc.addVertex(m, x1, y1, z1).setColor(r * shade, g * shade, b * shade, 1.0F).setUv(0.5F, 0.5F);
      vc.addVertex(m, x1, y1 + rise, z1).setColor(r * shade, g * shade, b * shade, 1.0F).setUv(0.5F, 0.5F);
      vc.addVertex(m, x0, y0 + rise, z0).setColor(r * shade, g * shade, b * shade, 1.0F).setUv(0.5F, 0.5F);
   }

   private static void cap(
      Matrix4f m,
      VertexConsumer vc,
      float c0,
      float s0,
      float c1,
      float s1,
      float r0,
      float r1,
      float in0,
      float in1,
      float y0,
      float y1,
      float r,
      float g,
      float b,
      float shade
   ) {
      float cr = Math.min(1.0F, r * shade);
      float cg = Math.min(1.0F, g * shade);
      float cb = Math.min(1.0F, b * shade);
      vc.addVertex(m, c0 * r0, y0, s0 * r0).setColor(cr, cg, cb, 1.0F).setUv(0.5F, 0.5F);
      vc.addVertex(m, c1 * r1, y1, s1 * r1).setColor(cr, cg, cb, 1.0F).setUv(0.5F, 0.5F);
      vc.addVertex(m, c1 * in1, y1, s1 * in1).setColor(cr, cg, cb, 1.0F).setUv(0.5F, 0.5F);
      vc.addVertex(m, c0 * in0, y0, s0 * in0).setColor(cr, cg, cb, 1.0F).setUv(0.5F, 0.5F);
   }

   private static void sheet(
      Matrix4f m, VertexConsumer vc, float a0, float a1, float b0, float b1, float r, float y0, float y1, float v0, float v1, float alpha0, float alpha1
   ) {
      vc.addVertex(m, Mth.cos(a0) * r, y0, Mth.sin(a0) * r).setColor(BLEED[0], BLEED[1], BLEED[2], alpha0).setUv(0.0F, v0);
      vc.addVertex(m, Mth.cos(a1) * r, y0, Mth.sin(a1) * r).setColor(BLEED[0], BLEED[1], BLEED[2], alpha0).setUv(1.0F, v0);
      vc.addVertex(m, Mth.cos(b1) * r, y1, Mth.sin(b1) * r).setColor(DEEP[0], DEEP[1], DEEP[2], alpha1).setUv(1.0F, v1);
      vc.addVertex(m, Mth.cos(b0) * r, y1, Mth.sin(b0) * r).setColor(DEEP[0], DEEP[1], DEEP[2], alpha1).setUv(0.0F, v1);
   }

   private static float fadeIn(float t) {
      return Mth.clamp(t * 3.2F, 0.0F, 1.0F) * Mth.clamp((1.0F - t) * 2.6F, 0.0F, 1.0F);
   }

   private static float sag(float fall, float shake) {
      float own = Mth.clamp((fall - shake * 0.35F) / 0.65F, 0.0F, 1.0F);
      return own * own;
   }

   private static ArenaRenderer.Cached build(ArenaEntity arena, ClientLevel level, float radius) {
      ArenaRenderer.Cached cached = new ArenaRenderer.Cached();
      RandomSource random = RandomSource.create((long)arena.getId() * 5471L + 37L);
      cached.jitter = new float[24];

      for (int i = 0; i < cached.jitter.length; i++) {
         cached.jitter[i] = random.nextFloat();
      }

      float inset = radius * 0.86F;
      List<ArenaRenderer.Spear> sky = new ArrayList<>(300);
      List<ArenaRenderer.Spear> ground = new ArrayList<>(300);

      for (int i = 0; i < 300; i++) {
         float t = ((float)i + 0.5F) / 300.0F;
         float rr = Mth.sqrt(t) * inset;
         float a = (float)i * 2.3999631F;
         sky.add(
            new ArenaRenderer.Spear(
               Mth.cos(a) * rr + (random.nextFloat() - 0.5F) * 2.0F,
               Mth.sin(a) * rr + (random.nextFloat() - 0.5F) * 2.0F,
               50.0F + random.nextFloat() * 46.0F,
               5.6F * (0.78F + random.nextFloat() * 0.5F),
               0.26F * (0.82F + random.nextFloat() * 0.45F),
               0.3F * (random.nextFloat() - 0.5F) * 0.6F,
               random.nextFloat() * (float) (Math.PI * 2),
               random.nextFloat() * (float) (Math.PI * 2)
            )
         );
         float ga = random.nextFloat() * (float) (Math.PI * 2);
         float gr = Mth.sqrt(random.nextFloat()) * inset;
         ground.add(
            new ArenaRenderer.Spear(
               Mth.cos(ga) * gr,
               Mth.sin(ga) * gr,
               0.0F,
               5.6F * (0.78F + random.nextFloat() * 0.5F),
               0.26F * (0.82F + random.nextFloat() * 0.45F),
               0.3F * (0.4F + random.nextFloat() * 1.5F) * (random.nextBoolean() ? 1.0F : -1.0F),
               random.nextFloat() * (float) (Math.PI * 2),
               0.0F
            )
         );
      }

      for (int i = sky.size() - 1; i > 0; i--) {
         int j = random.nextInt(i + 1);
         ArenaRenderer.Spear swap = sky.get(i);
         sky.set(i, sky.get(j));
         sky.set(j, swap);
      }

      cached.sky = sky;
      cached.ground = ground;
      cached.floor = Terrain.sample(level, arena.position(), radius);
      return cached;
   }

   private static final class Cached {
      float[] jitter;
      List<ArenaRenderer.Spear> sky;
      List<ArenaRenderer.Spear> ground;
      Terrain floor;
   }

   private static record Spear(float x, float z, float y, float length, float girth, float lean, float roll, float phase) {
   }
}
