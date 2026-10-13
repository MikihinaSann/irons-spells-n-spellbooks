package com.grandsigil.client;

import com.grandsigil.GrandSigil;
import com.grandsigil.GsPresence;
import com.grandsigil.config.GsConfig;
import com.grandsigil.entity.ArsenalEntity;
import com.grandsigil.entity.BladeEntity;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

public final class ArsenalRenderer {
   private static final ResourceLocation FLARE = GrandSigil.id("textures/entity/flare.png");
   private static final float[] GOLD = new float[]{1.0F, 0.8F, 0.32F};
   private static final float[] PALE = new float[]{1.0F, 0.94F, 0.72F};
   private static final float[] VIOLET = new float[]{0.66F, 0.44F, 1.0F};
   private static final float[] SURFACE_DARK = new float[]{0.09F, 0.045F, 0.015F};
   private static final float[] SURFACE_LIGHT = new float[]{1.0F, 0.84F, 0.42F};
   private static final float[] STEEL = new float[]{0.86F, 0.85F, 0.8F};
   private static final float[] HILT = new float[]{0.92F, 0.72F, 0.28F};
   private static final float[] GRIP = new float[]{0.22F, 0.13F, 0.07F};
   private static final float RIPPLE = 30.0F;
   private static final double VIEW = 256.0;
   private static final double BLADE_VIEW = 192.0;
   private static final ByteBufferBuilder BUILDER = new ByteBufferBuilder(65536);
   private static Vec3 EYE = Vec3.ZERO;
   private static final BufferSource BUFFERS = MultiBufferSource.immediate(BUILDER);

   private ArsenalRenderer() {
   }

   private static float width(int style) {
      return switch (style) {
         case 0 -> 0.19F;
         case 1 -> 0.22F;
         default -> 0.17F;
      };
   }

   public static float length(int index) {
      return switch (Math.floorMod(index, 3)) {
         case 0 -> 3.0F;
         case 1 -> 3.8F;
         default -> 4.5F;
      };
   }

   public static void render(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_TRANSLUCENT_BLOCKS) {
         boolean gates = GsPresence.any(12);
         boolean blades = GsPresence.any(13);
         if (gates || blades) {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            if (level != null) {
               Camera camera = event.getCamera();
               Vec3 eye = camera.getPosition();
               float partial = event.getPartialTick();
               PoseStack pose = event.getPoseStack();
               int detail = (Integer)GsConfig.CLIENT.detail.get();
               List<ArsenalEntity> armouries = new ArrayList<>();
               List<BladeEntity> drawn = new ArrayList<>();

               for (Entity entity : level.entitiesForRendering()) {
                  if (entity instanceof ArsenalEntity armoury && armoury.distanceToSqr(eye.x, eye.y, eye.z) < 65536.0) {
                     armouries.add(armoury);
                     continue;
                  }

                  if (entity instanceof BladeEntity blade && blade.distanceToSqr(eye.x, eye.y, eye.z) < 36864.0) {
                     drawn.add(blade);
                  }
               }

               if (!armouries.isEmpty() || !drawn.isEmpty()) {
                  pose.pushPose();
                  Matrix4f m = pose.last().pose();
                  Vector3f viewer = new Vector3f();
                  EYE = eye;
                  VertexConsumer steel = BUFFERS.getBuffer(GsRenderTypes.stone());

                  for (BladeEntity blade : drawn) {
                     if (blade.state() != 3) {
                        blade(m, steel, blade, partial, 1.0F, false);
                     }
                  }

                  BUFFERS.endBatch(GsRenderTypes.stone());
                  VertexConsumer glass = BUFFERS.getBuffer(GsRenderTypes.glass());

                  for (ArsenalEntity armoury : armouries) {
                     surfaces(m, glass, armoury, partial, detail);
                  }

                  BUFFERS.endBatch(GsRenderTypes.glass());
                  VertexConsumer glow = BUFFERS.getBuffer(GsRenderTypes.glow());

                  for (ArsenalEntity armoury : armouries) {
                     gates(m, glow, armoury, partial, detail);
                  }

                  for (BladeEntity bladex : drawn) {
                     if (bladex.state() == 3) {
                        blade(m, glow, bladex, partial, bladex.solid(partial), true);
                     }

                     edges(m, glow, bladex, partial, viewer);
                  }

                  BUFFERS.endBatch(GsRenderTypes.glow());
                  if (!Shaders.packInUse()) {
                     VertexConsumer halo = BUFFERS.getBuffer(GsRenderTypes.glow(FLARE));

                     for (ArsenalEntity armoury : armouries) {
                        halos(m, halo, armoury, partial, camera);
                     }

                     BUFFERS.endBatch(GsRenderTypes.glow(FLARE));
                  }

                  pose.popPose();
                  BUFFERS.endBatch();
               }
            }
         }
      }
   }

   private static float bloom(ArsenalEntity armoury, int i, float partial) {
      float open = armoury.open(partial);
      float lag = frac((float)i * 0.37F) * 0.55F;
      float own = Mth.clamp((open - lag) / (1.0F - lag), 0.0F, 1.0F);
      if (armoury.stage() == 2) {
         return own * own;
      } else {
         float c = 1.9F;
         float t = own - 1.0F;
         return 1.0F + (c + 1.0F) * t * t * t + c * t * t;
      }
   }

   private static float flash(int i, float volley) {
      if (volley < 0.0F) {
         return 0.0F;
      } else {
         float best = 0.0F;

         for (int k = 0; k < 120; k++) {
            if (ArsenalEntity.gateOf(k) == i) {
               float since = volley - (float)ArsenalEntity.launchTick(k);
               if (since >= 0.0F && since < 16.0F) {
                  best = Math.max(best, (float)Math.exp((double)(-since / 3.5F)));
               }
            }
         }

         return best;
      }
   }

   private static void gates(Matrix4f m, VertexConsumer vc, ArsenalEntity armoury, float partial, int detail) {
      Vec3 anchor = interpolated(armoury, partial);
      float yaw = armoury.yaw();
      float age = (float)armoury.tickCount + partial;
      float volley = armoury.volleyAge(partial);
      int segments = detail >= 2 ? 48 : (detail == 1 ? 32 : 20);
      float dim = Shaders.packInUse() ? 0.6F : 1.0F;

      for (int i = 0; i < 64; i++) {
         float open = bloom(armoury, i, partial);
         if (!(open <= 0.01F)) {
            float r = ArsenalEntity.size(i) * open;
            float fade = Mth.clamp(open, 0.0F, 1.0F) * dim;
            float lit = flash(i, volley);
            Vec3 at = ArsenalEntity.gate(anchor, yaw, i);
            Vec3 face = ArsenalEntity.facing(yaw, i);
            Vector3f c = new Vector3f((float)at.x, (float)at.y, (float)at.z);
            Vector3f n = new Vector3f((float)face.x, (float)face.y, (float)face.z);
            rings(m, vc, c, n, r, fade, lit, age, i, segments, detail);
         }
      }

      float g = armoury.grandOpen(partial);
      if (g > 0.01F) {
         Vector3f c = grandCentre(armoury);
         float lit = grandFlash(volley);
         rings(m, vc, c, grandFacing(armoury), 9.0F * g, Mth.clamp(g, 0.0F, 1.0F) * dim, lit, age, 7, segments * 2, detail);
      }
   }

   private static void rings(Matrix4f m, VertexConsumer vc, Vector3f c, Vector3f n, float r, float fade, float lit, float age, int i, int segments, int detail) {
      Vector3f right = across(n);
      Vector3f up = new Vector3f(n).cross(right).normalize();
      float turn = age * (i % 2 == 0 ? 0.03F : -0.026F);
      if (lit > 0.02F) {
         Geo.discIn(m, vc, c, right, up, r * (0.35F + 0.45F * lit), segments, 1.0F, 0.97F, 0.86F, lit * fade * 0.75F, 0.0F);
      }

      Geo.ringIn(m, vc, c, right, up, r * 0.9F, r, segments, turn, 1.0F, GOLD[0], GOLD[1], GOLD[2], fade * 0.55F, fade);
      Geo.ringIn(m, vc, c, right, up, r * 1.06F, r * 1.09F, segments, -turn, 1.0F, GOLD[0], GOLD[1], GOLD[2], fade * 0.35F, fade * 0.35F);

      for (int a = 0; a < 3; a++) {
         Geo.ringIn(
            m,
            vc,
            c,
            right,
            up,
            r * 0.62F,
            r * 0.68F,
            segments / 2,
            -turn * 1.6F + (float)a * 2.0944F,
            0.24F,
            PALE[0],
            PALE[1],
            PALE[2],
            fade * 0.85F,
            fade * 0.85F
         );
      }

      if (detail >= 1) {
         Geo.spokesIn(m, vc, c, right, up, r * 0.72F, r * 0.86F, 18, turn * 0.5F, r * 0.018F, 0.35F, VIOLET[0], VIOLET[1], VIOLET[2], fade * 0.8F, fade * 0.3F);
      }

      for (int w = 0; w < 2; w++) {
         float t = frac((age + (float)i * 7.3F) / 30.0F + (float)w * 0.5F);
         float rr = r * (0.25F + 0.95F * t);
         float a = fade * (1.0F - t) * (1.0F - t) * 0.6F;
         Geo.ringIn(m, vc, c, right, up, rr * 0.94F, rr, segments, 0.0F, 1.0F, GOLD[0], GOLD[1], GOLD[2], a * 0.4F, a);
      }

      if (lit > 0.05F) {
         float t = 1.0F - lit;
         float rr = r * (0.4F + 1.4F * t);
         Geo.ringIn(m, vc, c, right, up, rr * 0.9F, rr, segments, 0.0F, 1.0F, 1.0F, 0.96F, 0.84F, lit * 0.3F, lit * 0.9F);
      }
   }

   private static void surfaces(Matrix4f m, VertexConsumer vc, ArsenalEntity armoury, float partial, int detail) {
      Vec3 anchor = interpolated(armoury, partial);
      float yaw = armoury.yaw();
      float age = (float)armoury.tickCount + partial;
      int segments = detail >= 2 ? 48 : (detail == 1 ? 32 : 20);
      int bands = detail >= 2 ? 22 : (detail == 1 ? 14 : 8);
      Integer[] order = new Integer[64];
      double[] far = new double[64];

      for (int i = 0; i < order.length; i++) {
         order[i] = i;
         Vec3 g = ArsenalEntity.gate(anchor, yaw, i);
         far[i] = g.x * g.x + g.y * g.y + g.z * g.z;
      }

      Arrays.sort(order, (x, y) -> Double.compare(far[y], far[x]));
      Integer[] var23 = order;
      int var25 = order.length;

      for (int var14 = 0; var14 < var25; var14++) {
         int i = var23[var14];
         float open = bloom(armoury, i, partial);
         if (!(open <= 0.01F)) {
            float r = ArsenalEntity.size(i) * open * 0.94F;
            float fade = Mth.clamp(open, 0.0F, 1.0F);
            Vec3 at = ArsenalEntity.gate(anchor, yaw, i);
            Vec3 face = ArsenalEntity.facing(yaw, i);
            Vector3f c = new Vector3f((float)at.x, (float)at.y, (float)at.z);
            Vector3f n = new Vector3f((float)face.x, (float)face.y, (float)face.z);
            face(m, vc, c, n, r, fade, age, i, segments, bands);
         }
      }

      float g = armoury.grandOpen(partial);
      if (g > 0.01F) {
         face(m, vc, grandCentre(armoury), grandFacing(armoury), 9.0F * g * 0.94F, Mth.clamp(g, 0.0F, 1.0F), age, 7, segments * 2, bands * 2);
      }
   }

   private static void face(Matrix4f m, VertexConsumer vc, Vector3f c, Vector3f n, float r, float fade, float age, int i, int segments, int bands) {
      Vector3f right = across(n);
      Vector3f up = new Vector3f(n).cross(right).normalize();
      float phase = age * 0.32F + (float)i * 1.7F;

      for (int j = 0; j < bands; j++) {
         float t0 = (float)j / (float)bands;
         float t1 = (float)(j + 1) / (float)bands;
         float tm = (t0 + t1) * 0.5F;
         float wave = 0.5F + 0.5F * Mth.sin(tm * 13.0F - phase);
         float k = Mth.clamp((float)Math.pow((double)tm, 1.15) * 0.8F + wave * 0.32F * (0.3F + tm), 0.0F, 1.0F);
         float cr = Mth.lerp(k, SURFACE_DARK[0], SURFACE_LIGHT[0]);
         float cg = Mth.lerp(k, SURFACE_DARK[1], SURFACE_LIGHT[1]);
         float cb = Mth.lerp(k, SURFACE_DARK[2], SURFACE_LIGHT[2]);
         float a0 = fade * alpha(t0);
         float a1 = fade * alpha(t1);
         if (j == 0) {
            Geo.discIn(m, vc, c, right, up, r * t1, segments, cr, cg, cb, a0, a1);
         } else {
            Geo.ringIn(m, vc, c, right, up, r * t0, r * t1, segments, 0.0F, 1.0F, cr, cg, cb, a0, a1);
         }
      }
   }

   private static Vector3f grandFacing(ArsenalEntity armoury) {
      Vec3 d = armoury.grandFacing();
      return new Vector3f((float)d.x, (float)d.y, (float)d.z);
   }

   private static Vector3f grandCentre(ArsenalEntity armoury) {
      Vec3 g = armoury.grand().subtract(EYE);
      return new Vector3f((float)g.x, (float)g.y, (float)g.z);
   }

   private static float grandFlash(float volley) {
      float since = volley - 130.0F - 26.0F;
      if (since < 0.0F) {
         float into = volley - 130.0F;
         return into < 0.0F ? 0.0F : 0.5F * Mth.clamp(into / 26.0F, 0.0F, 1.0F);
      } else {
         return (float)Math.exp((double)(-since / 5.0F));
      }
   }

   private static float alpha(float t) {
      float core = Shaders.packInUse() ? 1.0F : 0.86F;
      if (t < 0.72F) {
         return core;
      } else {
         float e = (t - 0.72F) / 0.28F;
         return core * (1.0F - e * e);
      }
   }

   private static void halos(Matrix4f m, VertexConsumer vc, ArsenalEntity armoury, float partial, Camera camera) {
      Vec3 anchor = interpolated(armoury, partial);
      float volley = armoury.volleyAge(partial);
      Vector3f right = new Vector3f(camera.getLeftVector()).mul(-1.0F);
      Vector3f up = new Vector3f(camera.getUpVector());

      for (int i = 0; i < 64; i++) {
         float open = Mth.clamp(bloom(armoury, i, partial), 0.0F, 1.2F);
         if (!(open <= 0.01F)) {
            Vec3 at = ArsenalEntity.gate(anchor, armoury.yaw(), i);
            float lit = flash(i, volley);
            float r = ArsenalEntity.size(i) * (1.25F + 0.9F * lit);
            Geo.billboard(
               m,
               vc,
               (float)at.x,
               (float)at.y,
               (float)at.z,
               right,
               up,
               r * open,
               0.0F,
               0.0F,
               1.0F,
               1.0F,
               GOLD[0],
               GOLD[1],
               GOLD[2],
               Math.min(1.0F, open) * (0.08F + 0.42F * lit)
            );
         }
      }

      float g = armoury.grandOpen(partial);
      if (g > 0.01F) {
         Vector3f c = grandCentre(armoury);
         float lit = grandFlash(volley);
         Geo.billboard(
            m,
            vc,
            c.x,
            c.y,
            c.z,
            right,
            up,
            9.0F * g * (1.3F + 0.6F * lit),
            0.0F,
            0.0F,
            1.0F,
            1.0F,
            GOLD[0],
            GOLD[1],
            GOLD[2],
            Math.min(1.0F, g) * (0.1F + 0.45F * lit)
         );
      }
   }

   private static void blade(Matrix4f m, VertexConsumer vc, BladeEntity blade, float partial, float strength, boolean ghost) {
      if (!(strength <= 0.01F)) {
         ArsenalRenderer.Frame f = frame(blade, partial);
         int style = Math.floorMod(blade.index(), 3);
         float length = length(blade.index());
         float shown = length * (float)Math.pow((double)blade.drawn(partial), 0.8);

         float bladeLen = switch (style) {
            case 0 -> 2.15F;
            case 1 -> 2.85F;
            default -> 1.1F;
         };
         float width = width(style) * f.k;

         float guard = f.k * switch (style) {
            case 0 -> 0.46F;
            case 1 -> 0.6F;
            default -> 0.22F;
         };
         float[] body = ghost ? PALE : STEEL;
         float a = ghost ? strength * 0.9F : 1.0F;
         float bl = Math.min(bladeLen, shown);
         float thick = width * 0.3F;
         float shoulder = Math.min(bl, bladeLen * 0.2F);
         Vector3f tip = f.at;
         Vector3f sh = f.back(shoulder);
         Vector3f base = f.back(bl);
         float wTip = bl < shoulder ? width * (bl / shoulder) : width;
         diamond(m, vc, base, sh, f, wTip, thick, body, a, true);
         diamond(m, vc, sh, tip, f, wTip, thick, body, a, false);
         if (!(shown <= bladeLen)) {
            Vector3f g = f.back(bladeLen + 0.03F);
            box(m, vc, g, new Vector3f(f.side).mul(guard), new Vector3f(f.flat).mul(0.07F * f.k), new Vector3f(f.dir).mul(0.06F * f.k), ghost ? PALE : HILT, a);
            if (style == 2) {
               float shaft = Math.min(length - bladeLen - 0.08F, shown - bladeLen - 0.08F);
               if (shaft > 0.0F) {
                  Vector3f mid = f.back(bladeLen + 0.08F + shaft * 0.5F);
                  box(
                     m,
                     vc,
                     mid,
                     new Vector3f(f.side).mul(0.05F * f.k),
                     new Vector3f(f.flat).mul(0.05F * f.k),
                     new Vector3f(f.dir).mul(shaft * 0.5F * f.k),
                     ghost ? PALE : GRIP,
                     a
                  );
               }
            } else {
               float gripLen = length - bladeLen - 0.18F;
               float gs = Math.min(gripLen, shown - bladeLen - 0.08F);
               if (gs > 0.0F) {
                  Vector3f mid = f.back(bladeLen + 0.08F + gs * 0.5F);
                  box(
                     m,
                     vc,
                     mid,
                     new Vector3f(f.side).mul(0.06F * f.k),
                     new Vector3f(f.flat).mul(0.06F * f.k),
                     new Vector3f(f.dir).mul(gs * 0.5F * f.k),
                     ghost ? PALE : GRIP,
                     a
                  );
               }

               if (shown >= length - 0.06F) {
                  Vector3f p = f.back(length - 0.05F);
                  box(
                     m,
                     vc,
                     p,
                     new Vector3f(f.side).mul(0.1F * f.k),
                     new Vector3f(f.flat).mul(0.1F * f.k),
                     new Vector3f(f.dir).mul(0.06F * f.k),
                     ghost ? PALE : HILT,
                     a
                  );
               }
            }
         }
      }
   }

   private static void edges(Matrix4f m, VertexConsumer vc, BladeEntity blade, float partial, Vector3f viewer) {
      float strength = blade.solid(partial);
      if (!(strength <= 0.01F)) {
         ArsenalRenderer.Frame f = frame(blade, partial);
         int style = Math.floorMod(blade.index(), 3);
         float length = length(blade.index());
         float shown = length * (float)Math.pow((double)blade.drawn(partial), 0.8);
         float bladeLen = style == 0 ? 2.15F : (style == 1 ? 2.85F : 1.1F);
         float width = width(style) * f.k;
         float bl = Math.min(bladeLen, shown);
         float[] edge = Math.floorMod(blade.index(), 4) == 0 ? VIOLET : GOLD;
         Vector3f tip = f.at;
         Vector3f sh = f.back(Math.min(bl, bladeLen * 0.2F));
         Vector3f base = f.back(bl);
         Vector3f left = new Vector3f(base).add(new Vector3f(f.side).mul(-width));
         Vector3f rightB = new Vector3f(base).add(new Vector3f(f.side).mul(width));
         Vector3f leftS = new Vector3f(sh).add(new Vector3f(f.side).mul(-width));
         Vector3f rightS = new Vector3f(sh).add(new Vector3f(f.side).mul(width));
         float hw = 0.036F * (float)Math.sqrt((double)f.k) * 1.4F;
         Geo.strand(m, vc, left, leftS, viewer, hw, edge[0], edge[1], edge[2], strength * 0.8F, strength);
         Geo.strand(m, vc, leftS, tip, viewer, hw, edge[0], edge[1], edge[2], strength, strength);
         Geo.strand(m, vc, rightB, rightS, viewer, hw, edge[0], edge[1], edge[2], strength * 0.8F, strength);
         Geo.strand(m, vc, rightS, tip, viewer, hw, edge[0], edge[1], edge[2], strength, strength);
         Vector3f ridge = new Vector3f(f.flat).mul(width * 0.31F);
         Geo.strand(
            m, vc, new Vector3f(base).add(ridge), new Vector3f(sh).add(ridge), viewer, hw * 0.8F, PALE[0], PALE[1], PALE[2], strength * 0.9F, strength * 0.5F
         );
         Geo.strand(
            m, vc, new Vector3f(base).sub(ridge), new Vector3f(sh).sub(ridge), viewer, hw * 0.8F, PALE[0], PALE[1], PALE[2], strength * 0.9F, strength * 0.5F
         );
         if (blade.state() == 1) {
            double moved = Math.sqrt(blade.distanceToSqr(blade.xo, blade.yo, blade.zo));
            float trail = (float)Math.min(16.0, moved * 3.6) / f.k;
            if (trail > 0.2F) {
               Vector3f tail = f.back(length);
               Vector3f end = f.back(length + trail);
               Geo.taper(m, vc, tail, end, viewer, width * 0.9F, 0.0F, GOLD[0], GOLD[1], GOLD[2], 0.55F * strength, 0.0F);
               Geo.taper(m, vc, f.back(bl * 0.3F), f.back(length + trail * 0.6F), viewer, width * 0.35F, 0.0F, PALE[0], PALE[1], PALE[2], 0.5F * strength, 0.0F);
            }
         }
      }
   }

   private static ArsenalRenderer.Frame frame(BladeEntity blade, float partial) {
      Vec3 p = interpolated(blade, partial);
      Vector3f d = new Vector3f(blade.dir());
      if (d.lengthSquared() < 1.0E-6F) {
         d.set(0.0F, 0.0F, 1.0F);
      }

      return new ArsenalRenderer.Frame(new Vector3f((float)p.x, (float)p.y, (float)p.z), d.normalize(), blade.reach() / length(blade.index()));
   }

   private static void diamond(
      Matrix4f m, VertexConsumer vc, Vector3f a, Vector3f b, ArsenalRenderer.Frame f, float width, float thick, float[] c, float alpha, boolean full
   ) {
      Vector3f[] ring = new Vector3f[]{
         new Vector3f(f.side).mul(-width), new Vector3f(f.flat).mul(thick), new Vector3f(f.side).mul(width), new Vector3f(f.flat).mul(-thick)
      };
      float[] shade = new float[]{0.78F, 1.0F, 0.86F, 0.62F};

      for (int i = 0; i < 4; i++) {
         Vector3f p0 = ring[i];
         Vector3f p1 = ring[(i + 1) % 4];
         float k = shade[i];
         Vector3f a0 = new Vector3f(a).add(p0);
         Vector3f a1 = new Vector3f(a).add(p1);
         Vector3f b0 = full ? new Vector3f(b).add(p0) : new Vector3f(b);
         Vector3f b1 = full ? new Vector3f(b).add(p1) : new Vector3f(b);
         quad(m, vc, a0, a1, b1, b0, c[0] * k, c[1] * k, c[2] * k, alpha);
      }
   }

   private static void box(Matrix4f m, VertexConsumer vc, Vector3f c, Vector3f x, Vector3f y, Vector3f z, float[] col, float alpha) {
      Vector3f[] corner = new Vector3f[8];

      for (int i = 0; i < 8; i++) {
         corner[i] = new Vector3f(c)
            .add(new Vector3f(x).mul((i & 1) == 0 ? -1.0F : 1.0F))
            .add(new Vector3f(y).mul((i & 2) == 0 ? -1.0F : 1.0F))
            .add(new Vector3f(z).mul((i & 4) == 0 ? -1.0F : 1.0F));
      }

      int[][] faces = new int[][]{{0, 1, 3, 2}, {4, 6, 7, 5}, {0, 4, 5, 1}, {2, 3, 7, 6}, {0, 2, 6, 4}, {1, 5, 7, 3}};
      float[] shade = new float[]{0.7F, 0.85F, 0.75F, 1.0F, 0.8F, 0.9F};

      for (int i = 0; i < 6; i++) {
         int[] q = faces[i];
         float k = shade[i];
         quad(m, vc, corner[q[0]], corner[q[1]], corner[q[2]], corner[q[3]], col[0] * k, col[1] * k, col[2] * k, alpha);
      }
   }

   private static void quad(Matrix4f m, VertexConsumer vc, Vector3f a, Vector3f b, Vector3f c, Vector3f d, float r, float g, float bl, float alpha) {
      vertex(m, vc, a, r, g, bl, alpha);
      vertex(m, vc, b, r, g, bl, alpha);
      vertex(m, vc, c, r, g, bl, alpha);
      vertex(m, vc, d, r, g, bl, alpha);
   }

   private static void vertex(Matrix4f m, VertexConsumer vc, Vector3f p, float r, float g, float b, float a) {
      vc.addVertex(m, p.x, p.y, p.z).setColor(r, g, b, a).setUv(0.5F, 0.5F);
   }

   private static Vec3 interpolated(Entity e, float partial) {
      return new Vec3(
         Mth.lerp((double)partial, e.xo, e.getX()) - EYE.x,
         Mth.lerp((double)partial, e.yo, e.getY()) - EYE.y,
         Mth.lerp((double)partial, e.zo, e.getZ()) - EYE.z
      );
   }

   private static Vector3f across(Vector3f n) {
      Vector3f axis = Math.abs(n.y) > 0.9F ? new Vector3f(1.0F, 0.0F, 0.0F) : new Vector3f(0.0F, 1.0F, 0.0F);
      return new Vector3f(n).cross(axis).normalize();
   }

   private static float frac(float v) {
      return v - (float)Mth.floor(v);
   }

   private static final class Frame {
      final Vector3f at;
      final Vector3f dir;
      final Vector3f side;
      final Vector3f flat;
      final float k;

      Frame(Vector3f at, Vector3f dir, float k) {
         this.at = at;
         this.dir = dir;
         this.k = k;
         Vector3f s = new Vector3f(dir).cross(0.0F, 1.0F, 0.0F);
         if (s.lengthSquared() < 1.0E-4F) {
            s = new Vector3f(dir).cross(1.0F, 0.0F, 0.0F);
         }

         this.side = s.normalize();
         this.flat = new Vector3f(this.side).cross(dir).normalize();
      }

      Vector3f back(float d) {
         return new Vector3f(this.dir).mul(-d * this.k).add(this.at);
      }
   }
}
