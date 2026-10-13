package com.grandsigil.client;

import com.grandsigil.GrandSigil;
import com.grandsigil.GsPresence;
import com.grandsigil.config.GsConfig;
import com.grandsigil.entity.PhoenixEntity;
import com.grandsigil.entity.Terrain;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
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

public final class PhoenixRenderer {
   private static final ResourceLocation FLARE = GrandSigil.id("textures/entity/flare.png");
   private static final float[] EMBER = new float[]{0.98F, 0.46F, 0.08F};
   private static final float[] CRIMSON = new float[]{0.92F, 0.24F, 0.04F};
   private static final float[] CHAR = new float[]{0.1F, 0.04F, 0.02F};
   private static final float[] WHITE_HOT = new float[]{1.0F, 0.92F, 0.62F};
   private static final float[] GOLDEN = new float[]{1.0F, 0.68F, 0.2F};
   private static final float[] FLAME = new float[]{1.0F, 0.34F, 0.05F};
   private static final double VIEW = 320.0;
   private static final float TAIL_SPAN = 9.0F;
   private static final int TAIL_STEPS = 22;
   private static final ByteBufferBuilder BUILDER = new ByteBufferBuilder(262144);
   private static final BufferSource BUFFERS = MultiBufferSource.immediate(BUILDER);
   private static final Map<PhoenixEntity, Terrain> GROUND = new WeakHashMap<>();
   private static final int CHUNK = 200;
   private static final List<PhoenixRenderer.Tongue> TONGUES = new ArrayList<>();

   private PhoenixRenderer() {
   }

   public static void render(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_TRANSLUCENT_BLOCKS && GsPresence.any(14)) {
         Minecraft minecraft = Minecraft.getInstance();
         ClientLevel level = minecraft.level;
         if (level != null) {
            Camera camera = event.getCamera();
            Vec3 eye = camera.getPosition();
            float partial = event.getPartialTick();
            int detail = (Integer)GsConfig.CLIENT.detail.get();
            List<PhoenixEntity> birds = new ArrayList<>();

            for (Entity entity : level.entitiesForRendering()) {
               if (entity instanceof PhoenixEntity) {
                  PhoenixEntity phoenix = (PhoenixEntity)entity;
                  if (phoenix.distanceToSqr(eye.x, eye.y, eye.z) < 102400.0) {
                     birds.add(phoenix);
                  }
               }
            }

            if (!birds.isEmpty()) {
               PoseStack pose = event.getPoseStack();
               pose.pushPose();
               Matrix4f m = pose.last().pose();
               boolean pack = Shaders.packInUse();
               float dim = pack ? 0.7F : 1.0F;
               List<PhoenixRenderer.Frame> frames = new ArrayList<>();

               for (PhoenixEntity phoenix : birds) {
                  frames.add(new PhoenixRenderer.Frame(phoenix, partial, eye, level));
               }

               List<PhoenixSculpt.Bird> sculpted = new ArrayList<>();

               for (PhoenixRenderer.Frame f : frames) {
                  if (f.bird != null && f.bird.alpha > 0.01F) {
                     sculpted.add(new PhoenixSculpt.Bird(f.bird, f.base, f.t, f.bird.alpha, f.phoenix, false));
                  }

                  if (f.ghost != null && f.ghost.alpha > 0.01F) {
                     sculpted.add(new PhoenixSculpt.Bird(f.ghost, f.base, f.t, f.ghost.alpha, f.phoenix, true));
                  }
               }

               if (!sculpted.isEmpty()) {
                  VertexConsumer vc = BUFFERS.getBuffer(GsRenderTypes.matter(PhoenixSculpt.FIRE_FLOW));

                  for (PhoenixSculpt.Bird b : sculpted) {
                     PhoenixSculpt.energyBody(m, vc, b, 1.45F, 1.0F, 0.8F, 0.6F, 0.2F * b.alpha(), 0.0F, 0.8F, -b.t() * 0.03F, 0.3F);
                     PhoenixSculpt.energyBody(m, vc, b, 1.0F, 1.0F, 1.0F, 1.0F, 0.8F * b.alpha(), 0.05F * b.alpha(), 0.8F, -b.t() * 0.05F, 0.18F);
                  }

                  BUFFERS.endBatch(GsRenderTypes.matter(PhoenixSculpt.FIRE_FLOW));
                  vc = BUFFERS.getBuffer(GsRenderTypes.matter(PhoenixSculpt.FLAME_FEATHER));

                  for (PhoenixSculpt.Bird b : sculpted) {
                     PhoenixSculpt.feathers(m, vc, b, false, 1.0F, 1.0F, 1.0F, 0.82F * b.alpha());
                     PhoenixSculpt.plumes(m, vc, b);
                  }

                  BUFFERS.endBatch(GsRenderTypes.matter(PhoenixSculpt.FLAME_FEATHER));
                  vc = BUFFERS.getBuffer(GsRenderTypes.glow(PhoenixSculpt.FLAME_FEATHER));

                  for (PhoenixSculpt.Bird b : sculpted) {
                     PhoenixSculpt.feathers(m, vc, b, false, 1.0F, 0.55F, 0.2F, 0.3F * dim * b.alpha());
                  }

                  BUFFERS.endBatch(GsRenderTypes.glow(PhoenixSculpt.FLAME_FEATHER));
                  vc = BUFFERS.getBuffer(GsRenderTypes.glow(PhoenixSculpt.FIRE_FLOW));

                  for (PhoenixSculpt.Bird b : sculpted) {
                     PhoenixSculpt.energyBody(m, vc, b, 0.5F, 1.0F, 0.85F, 0.5F, 0.65F * dim * b.alpha(), 0.0F, 1.0F, -b.t() * 0.11F, 0.25F);
                  }

                  BUFFERS.endBatch(GsRenderTypes.glow(PhoenixSculpt.FIRE_FLOW));
               }

               TONGUES.clear();
               VertexConsumer glass = BUFFERS.getBuffer(GsRenderTypes.glass());

               for (PhoenixRenderer.Frame f : frames) {
                  trail(m, glass, f, false, detail, 1.0F);
                  pillar(m, glass, f, false);
                  burst(m, glass, f, false, 1.0F);
               }

               BUFFERS.endBatch(GsRenderTypes.glass());
               VertexConsumer flames = BUFFERS.getBuffer(GsRenderTypes.matter(PhoenixSculpt.FLAME));

               for (int i = 0; i < TONGUES.size(); i++) {
                  TONGUES.get(i).draw(m, flames, false, 1.0F);
                  if (i % 200 == 199) {
                     BUFFERS.endBatch(GsRenderTypes.matter(PhoenixSculpt.FLAME));
                     flames = BUFFERS.getBuffer(GsRenderTypes.matter(PhoenixSculpt.FLAME));
                  }
               }

               for (PhoenixSculpt.Bird b : sculpted) {
                  PhoenixSculpt.fire(m, flames, b, false, 1.0F);
               }

               BUFFERS.endBatch(GsRenderTypes.matter(PhoenixSculpt.FLAME));
               VertexConsumer heat = BUFFERS.getBuffer(GsRenderTypes.glow(PhoenixSculpt.FLAME));

               for (int ix = 0; ix < TONGUES.size(); ix++) {
                  TONGUES.get(ix).draw(m, heat, true, pack ? 0.45F : 1.0F);
                  if (ix % 200 == 199) {
                     BUFFERS.endBatch(GsRenderTypes.glow(PhoenixSculpt.FLAME));
                     heat = BUFFERS.getBuffer(GsRenderTypes.glow(PhoenixSculpt.FLAME));
                  }
               }

               for (PhoenixSculpt.Bird b : sculpted) {
                  PhoenixSculpt.fire(m, heat, b, true, dim);
               }

               BUFFERS.endBatch(GsRenderTypes.glow(PhoenixSculpt.FLAME));
               VertexConsumer glow = BUFFERS.getBuffer(GsRenderTypes.glow());

               for (PhoenixRenderer.Frame f : frames) {
                  trail(m, glow, f, true, detail, pack ? 0.42F : 1.0F);
                  pillar(m, glow, f, true);
                  burst(m, glow, f, true, dim);
               }

               for (PhoenixSculpt.Bird b : sculpted) {
                  PhoenixSculpt.glints(m, glow, b, dim);
               }

               BUFFERS.endBatch(GsRenderTypes.glow());
               VertexConsumer floor = BUFFERS.getBuffer(GsRenderTypes.glow());

               for (PhoenixRenderer.Frame f : frames) {
                  pose.pushPose();
                  pose.translate(f.base.x, f.base.y, f.base.z);
                  shock(pose.last().pose(), floor, f, dim);
                  pose.popPose();
               }

               BUFFERS.endBatch(GsRenderTypes.glow());
               if (!pack) {
                  Vector3f right = new Vector3f(camera.getLeftVector()).mul(-1.0F);
                  Vector3f up = new Vector3f(camera.getUpVector());
                  VertexConsumer halo = BUFFERS.getBuffer(GsRenderTypes.glow(FLARE));

                  for (PhoenixRenderer.Frame f : frames) {
                     halos(m, halo, f, right, up);
                  }

                  BUFFERS.endBatch(GsRenderTypes.glow(FLARE));
               }

               pose.popPose();
               BUFFERS.endBatch();
            }
         }
      }
   }

   private static void quad(
      Matrix4f m, VertexConsumer vc, Vector3f l0, Vector3f r0, Vector3f r1, Vector3f l1, float[] c0, float[] c1, float k0, float k1, float a0, float a1
   ) {
      float ra = Mth.lerp(k0, c0[0], c1[0]);
      float ga = Mth.lerp(k0, c0[1], c1[1]);
      float ba = Mth.lerp(k0, c0[2], c1[2]);
      float rb = Mth.lerp(k1, c0[0], c1[0]);
      float gb = Mth.lerp(k1, c0[1], c1[1]);
      float bb = Mth.lerp(k1, c0[2], c1[2]);
      float aa = Mth.lerp(k0, a0, a1);
      float ab = Mth.lerp(k1, a0, a1);
      vertex(m, vc, l0, ra, ga, ba, aa);
      vertex(m, vc, r0, ra, ga, ba, aa);
      vertex(m, vc, r1, rb, gb, bb, ab);
      vertex(m, vc, l1, rb, gb, bb, ab);
   }

   private static void trail(Matrix4f m, VertexConsumer vc, PhoenixRenderer.Frame f, boolean light, int detail, float dim) {
      if (!(f.t < 62.0F)) {
         float[] floor = f.phoenix.floor();
         PhoenixEntity phoenix = f.phoenix;
         Vector3f s = phoenix.start();
         float e = phoenix.entry();
         float turn = phoenix.turn();
         int n = 576;
         Vector3f[] p = new Vector3f[n];

         for (int i = 0; i < n; i++) {
            Vector3f q = PhoenixEntity.at(PhoenixEntity.trailTime(i), s, e, turn);
            p[i] = new Vector3f(q.x, floor[i] + 0.06F, q.z);
         }

         float lingering = 1.0F - Mth.clamp((f.t - 292.0F - 20.0F) / 20.0F, 0.0F, 1.0F);

         for (int i = 0; i + 1 < n; i++) {
            float b0 = PhoenixEntity.burning(i, f.t);
            float b1 = PhoenixEntity.burning(i + 1, f.t);
            boolean lit0 = f.t > PhoenixEntity.trailTime(i) + 8.0F;
            boolean lit1 = f.t > PhoenixEntity.trailTime(i + 1) + 8.0F;
            if (lit0 && lit1 && !(Math.abs(floor[i] - floor[i + 1]) > 1.5F)) {
               Vector3f run = new Vector3f(p[i + 1]).sub(p[i]);
               Vector3f across = new Vector3f(-run.z, 0.0F, run.x);
               if (!(across.lengthSquared() < 1.0E-6F)) {
                  across.normalize();
                  float w = 3.91F;
                  Vector3f l0 = f.world(new Vector3f(p[i]).sub(new Vector3f(across).mul(w)));
                  Vector3f r0 = f.world(new Vector3f(p[i]).add(new Vector3f(across).mul(w)));
                  Vector3f l1 = f.world(new Vector3f(p[i + 1]).sub(new Vector3f(across).mul(w)));
                  Vector3f r1 = f.world(new Vector3f(p[i + 1]).add(new Vector3f(across).mul(w)));
                  Vector3f c0 = f.world(p[i]);
                  Vector3f c1 = f.world(p[i + 1]);
                  if (light) {
                     float g0 = b0 * dim * (0.55F + 0.25F * Mth.sin(f.t * 0.4F + (float)i * 0.9F));
                     float g1 = b1 * dim * (0.55F + 0.25F * Mth.sin(f.t * 0.4F + (float)(i + 1) * 0.9F));
                     band(m, vc, l0, c0, c1, l1, FLAME, g0, g1);
                     band(m, vc, r0, c0, c1, r1, FLAME, g0, g1);
                  } else {
                     band(m, vc, l0, c0, c1, l1, CHAR, 0.6F * lingering, 0.6F * lingering);
                     band(m, vc, r0, c0, c1, r1, CHAR, 0.6F * lingering, 0.6F * lingering);
                  }
               }
            }
         }

         int step = detail >= 2 ? 1 : (detail == 1 ? 2 : 3);
         Vector3f toEye = new Vector3f();

         for (int ix = 0; ix < n; ix += step) {
            float b = PhoenixEntity.burning(ix, f.t);
            if (!(b <= 0.02F)) {
               float lit = f.t - PhoenixEntity.trailTime(ix) - 8.0F;
               float catchFlare = 1.0F + 1.3F * Math.max(0.0F, 1.0F - lit / 14.0F);
               Vector3f run = new Vector3f(p[Math.min(n - 1, ix + 1)]).sub(p[Math.max(0, ix - 1)]);
               Vector3f across = new Vector3f(-run.z, 0.0F, run.x);
               if (!(across.lengthSquared() < 1.0E-6F)) {
                  across.normalize();

                  for (int side = -1; side <= 1; side += 2) {
                     float seed = (float)ix * 0.913F + (float)side * 3.1F;
                     float jitter = 0.35F * Mth.sin(seed * 7.3F);
                     Vector3f root = new Vector3f(p[ix]).add(new Vector3f(across).mul((float)side * (1.4F + 2.0F * jitter)));
                     float h = b * catchFlare * (3.2F + 3.4F * noise(f.t * 0.23F + seed));
                     float w = 1.25F * b;
                     Vector3f world = f.world(root);
                     toEye.set(world).negate();
                     tongue(m, vc, world, toEye, h, w, f.t, seed, light, light ? dim : 1.0F);
                  }
               }
            }
         }
      }
   }

   private static void band(Matrix4f m, VertexConsumer vc, Vector3f a, Vector3f b, Vector3f c, Vector3f d, float[] col, float aIn, float aOut) {
      vertex(m, vc, a, col[0], col[1], col[2], 0.0F);
      vertex(m, vc, b, col[0], col[1], col[2], aIn);
      vertex(m, vc, c, col[0], col[1], col[2], aOut);
      vertex(m, vc, d, col[0], col[1], col[2], 0.0F);
   }

   private static void tongue(Matrix4f m, VertexConsumer vc, Vector3f root, Vector3f toEye, float h, float w, float t, float seed, boolean light, float dim) {
      if (!light) {
         TONGUES.add(new PhoenixRenderer.Tongue(new Vector3f(root), h, w, t, seed));
      }
   }

   private static void tongueFlat(Matrix4f m, VertexConsumer vc, Vector3f root, Vector3f toEye, float h, float w, float t, float seed, boolean light, float dim) {
      Vector3f side = new Vector3f(toEye.z, 0.0F, -toEye.x);
      if (side.lengthSquared() < 1.0E-6F) {
         side.set(1.0F, 0.0F, 0.0F);
      }

      side.normalize();
      float sway = 0.28F * Mth.sin(t * 0.17F + seed * 2.3F) + 0.12F * Mth.sin(t * 0.43F + seed);
      if (light) {
         h *= 0.72F;
         w *= 0.5F;
      }

      float[] at = new float[]{0.0F, 0.35F, 0.7F, 1.0F};
      float[] wide = new float[]{1.0F, 0.85F, 0.45F, 0.0F};
      float[] c0 = light ? WHITE_HOT : EMBER;
      float[] c1 = light ? FLAME : CRIMSON;
      float a0 = light ? 0.9F * dim : 0.85F;
      Vector3f pl = null;
      Vector3f pr = null;
      float pk = 0.0F;

      for (int i = 0; i < at.length; i++) {
         float k = at[i];
         Vector3f c = new Vector3f(root).add(new Vector3f(side).mul(sway * k * k * h * 0.4F)).add(0.0F, h * k, 0.0F);
         Vector3f half = new Vector3f(side).mul(w * wide[i]);
         Vector3f l = new Vector3f(c).sub(half);
         Vector3f r = new Vector3f(c).add(half);
         if (pl != null) {
            quad(m, vc, pl, pr, r, l, c0, c1, pk, k, a0, 0.0F);
         }

         pl = l;
         pr = r;
         pk = k;
      }
   }

   private static float noise(float x) {
      return 0.5F + 0.3F * Mth.sin(x * 1.7F) + 0.2F * Mth.sin(x * 3.13F + 1.1F);
   }

   private static void pillar(Matrix4f m, VertexConsumer vc, PhoenixRenderer.Frame f, boolean light) {
      float t = f.t;
      if (!(t > 42.0F)) {
         float grow = PhoenixEntity.smooth(Mth.clamp(t / 8.0F, 0.0F, 1.0F));
         float die = 1.0F - PhoenixEntity.smooth(Mth.clamp((t - 24.0F) / 18.0F, 0.0F, 1.0F));
         float k = grow * die;
         if (!(k <= 0.01F)) {
            Vector3f base = f.world(new Vector3f(f.phoenix.start()).add(0.0F, -1.0F, 0.0F));
            float height = 32.0F * grow;
            if (light) {
               Geo.columnPlain(
                  m, vc, base.x, base.z, base.y, base.y + height, 1.8F * k, 0.5F * k, 16, t * 0.1F, GOLDEN[0], GOLDEN[1], GOLDEN[2], 0.55F * k, 0.0F
               );
               Geo.columnPlain(
                  m, vc, base.x, base.z, base.y, base.y + height * 0.8F, 3.2F * k, 1.2F * k, 16, -t * 0.08F, FLAME[0], FLAME[1], FLAME[2], 0.6F * k, 0.0F
               );
            } else {
               Geo.columnPlain(
                  m, vc, base.x, base.z, base.y, base.y + height * 0.9F, 4.0F * k, 1.4F * k, 16, t * 0.05F, EMBER[0], EMBER[1], EMBER[2], 0.65F * k, 0.0F
               );
            }

            Vector3f toEye = new Vector3f();

            for (int i = 0; i < 18; i++) {
               float ang = (float)i * (float) (Math.PI * 2) / 18.0F + t * 0.02F;
               Vector3f root = new Vector3f(base).add(Mth.cos(ang) * 4.0F * k, 0.0F, Mth.sin(ang) * 4.0F * k);
               toEye.set(root).negate();
               tongue(m, vc, root, toEye, (5.5F + 4.0F * noise(t * 0.3F + (float)i)) * k, 1.5F * k, t, (float)i * 1.37F, light, 1.0F);
            }
         }
      }
   }

   private static void burst(Matrix4f m, VertexConsumer vc, PhoenixRenderer.Frame f, boolean light, float dim) {
      float since = f.t - 236.0F;
      if (!(since < 0.0F)) {
         float blast = 16.0F;
         float r = blast * (1.0F - (float)Math.exp((double)(-since / 4.0F)));
         Vector3f c = f.world(new Vector3f(0.0F, 0.6F + Math.max(0.0F, since - 4.0F) * 0.45F, 0.0F));
         Vector3f foot = f.world(new Vector3f(0.0F, 0.4F, 0.0F));
         if (light) {
            float core = (float)Math.exp((double)(-since / 6.0F)) * dim;
            if (core > 0.01F) {
               sphere(m, vc, c, r * 0.6F, WHITE_HOT, core * 0.6F, 0.0F);
               sphere(m, vc, c, r, GOLDEN, core * 0.35F, 0.0F);
            }

            float col = (float)Math.exp((double)(-since / 7.0F)) * dim;
            if (col > 0.01F) {
               float h = 72.0F * (1.0F - (float)Math.exp((double)(-since / 3.0F)));
               float thin = 1.0F - 0.5F * Math.min(1.0F, since / 40.0F);
               Geo.columnPlain(
                  m, vc, foot.x, foot.z, foot.y, foot.y + h, 4.8F * thin, 1.0F * thin, 20, since * 0.05F, GOLDEN[0], GOLDEN[1], GOLDEN[2], col * 0.5F, 0.0F
               );
               Geo.columnPlain(
                  m,
                  vc,
                  foot.x,
                  foot.z,
                  foot.y,
                  foot.y + h * 0.8F,
                  8.0F * thin,
                  3.0F * thin,
                  20,
                  -since * 0.04F,
                  FLAME[0],
                  FLAME[1],
                  FLAME[2],
                  col * 0.35F,
                  0.0F
               );
            }
         } else {
            float body = 0.8F * (float)Math.exp((double)(-since / 14.0F));
            if (body > 0.01F) {
               float[] tone = new float[]{
                  Mth.lerp(Math.min(1.0F, since / 30.0F), EMBER[0], CRIMSON[0]),
                  Mth.lerp(Math.min(1.0F, since / 30.0F), EMBER[1], CRIMSON[1]),
                  Mth.lerp(Math.min(1.0F, since / 30.0F), EMBER[2], CRIMSON[2])
               };
               sphere(m, vc, c, r, tone, body, body * 0.15F);
            }

            float col = 0.7F * (float)Math.exp((double)(-since / 7.0F));
            if (col > 0.01F) {
               float h = 68.0F * (1.0F - (float)Math.exp((double)(-since / 3.0F)));
               float thin = 1.0F - 0.5F * Math.min(1.0F, since / 40.0F);
               Geo.columnPlain(
                  m, vc, foot.x, foot.z, foot.y, foot.y + h, 9.0F * thin, 3.2F * thin, 20, since * 0.03F, EMBER[0], EMBER[1], EMBER[2], col * 0.7F, 0.0F
               );
            }

            float fade = 1.0F - Mth.clamp((f.t - 332.0F + 20.0F) / 20.0F, 0.0F, 1.0F);
            Vector3f floorC = f.world(new Vector3f(0.0F, 0.07F, 0.0F));
            discFlat(m, vc, floorC, blast * 1.25F * Math.min(1.0F, since / 3.0F), CHAR, 0.7F * fade, 0.0F);
         }

         float twist = (float)Math.exp((double)(-since / 9.0F));
         if (twist > 0.03F) {
            Vector3f toEye = new Vector3f();
            float reach = 72.0F * (1.0F - (float)Math.exp((double)(-since / 3.0F)));

            for (int tier = 0; tier < 2; tier++) {
               float y0 = (float)tier * 9.0F;
               if (!(y0 > reach * 0.55F)) {
                  float rad = (7.5F - (float)tier * 1.8F) * (1.0F - 0.4F * Math.min(1.0F, since / 40.0F));

                  for (int i = 0; i < 14; i++) {
                     float ang = (float)i * (float) (Math.PI * 2) / 14.0F + since * (0.09F + (float)tier * 0.03F) + (float)tier * 0.7F;
                     Vector3f root = new Vector3f(foot).add(Mth.cos(ang) * rad, y0, Mth.sin(ang) * rad);
                     toEye.set(root).negate();
                     float h = twist
                        * (1.0F - 0.35F * (float)tier)
                        * (9.0F + 6.0F * noise(f.t * 0.25F + (float)i + (float)tier * 5.0F))
                        * Math.min(1.0F, since / 3.0F);
                     tongue(m, vc, root, toEye, h, 2.3F * twist, f.t, (float)i * 1.3F + (float)tier, light, light ? dim : 1.0F);
                  }
               }
            }
         }

         float burn = Math.min(1.0F, since / 4.0F) * (1.0F - Mth.clamp((f.t - 292.0F) / 40.0F, 0.0F, 1.0F));
         if (burn > 0.02F) {
            Vector3f toEye = new Vector3f();
            int count = 84;

            for (int i = 0; i < count; i++) {
               float ang = (float)i * (float) (Math.PI * 2) / (float)count;
               float rr = blast * (0.55F + 0.35F * noise((float)i * 2.1F));
               Vector3f local = new Vector3f(Mth.cos(ang) * rr, 0.0F, Mth.sin(ang) * rr);
               local.y = f.ground.lift(local.x, local.z) + 0.05F;
               Vector3f root = f.world(local);
               toEye.set(root).negate();
               float h = burn * (5.0F + 5.0F * noise(f.t * 0.2F + (float)i * 1.3F)) * (1.0F + 1.5F * (float)Math.exp((double)(-since / 8.0F)));
               tongue(m, vc, root, toEye, h, 1.8F * burn, f.t, (float)i * 1.7F, light, light ? dim : 1.0F);
            }
         }
      }
   }

   private static void shock(Matrix4f m, VertexConsumer vc, PhoenixRenderer.Frame f, float dim) {
      float since = f.t - 236.0F;
      if (!(since < 0.0F) && !(since > 30.0F)) {
         float r = 4.0F + 50.0F * (1.0F - (float)Math.exp((double)(-since / 7.0F)));
         float a = (1.0F - since / 30.0F) * dim;
         Geo.band(m, vc, f.ground, 0.1F, r - 3.2F, r, 96, 0.0F, 1.0F, FLAME[0], FLAME[1], FLAME[2], a * 0.2F, a);
         Geo.band(m, vc, f.ground, 0.08F, r * 0.7F - 1.2F, r * 0.7F, 96, 0.0F, 1.0F, WHITE_HOT[0], WHITE_HOT[1], WHITE_HOT[2], 0.0F, a * 0.6F);
      }
   }

   private static void halos(Matrix4f m, VertexConsumer vc, PhoenixRenderer.Frame f, Vector3f right, Vector3f up) {
      float since = f.t - 236.0F;
      if (since >= 0.0F && since < 30.0F) {
         Vector3f c = f.world(new Vector3f(0.0F, 2.0F, 0.0F));
         float k = (float)Math.exp((double)(-since / 6.0F));
         Geo.billboard(m, vc, c.x, c.y, c.z, right, up, 26.0F, 0.0F, 0.0F, 1.0F, 1.0F, GOLDEN[0], GOLDEN[1], GOLDEN[2], 0.4F * k);
      }

      if (f.ghost != null && f.ghost.alpha > 0.01F) {
         Vector3f c = f.world(f.ghost.at);
         Geo.billboard(m, vc, c.x, c.y, c.z, right, up, 15.0F, 0.0F, 0.0F, 1.0F, 1.0F, FLAME[0], FLAME[1], FLAME[2], 0.14F * f.ghost.alpha);
      }
   }

   private static void sphere(Matrix4f m, VertexConsumer vc, Vector3f c, float r, float[] col, float aFacing, float aRim) {
      if (!(r <= 0.05F)) {
         int rings = 12;
         int segs = 24;
         Vector3f toEye = new Vector3f(c).negate().normalize();

         for (int i = 0; i < rings; i++) {
            float p0 = (float) Math.PI * (float)i / (float)rings;
            float p1 = (float) Math.PI * (float)(i + 1) / (float)rings;

            for (int j = 0; j < segs; j++) {
               float q0 = (float) (Math.PI * 2) * (float)j / (float)segs;
               float q1 = (float) (Math.PI * 2) * (float)(j + 1) / (float)segs;
               Vector3f n00 = unit(p0, q0);
               Vector3f n01 = unit(p0, q1);
               Vector3f n11 = unit(p1, q1);
               Vector3f n10 = unit(p1, q0);
               sv(m, vc, c, r, n00, col, toEye, aFacing, aRim);
               sv(m, vc, c, r, n01, col, toEye, aFacing, aRim);
               sv(m, vc, c, r, n11, col, toEye, aFacing, aRim);
               sv(m, vc, c, r, n10, col, toEye, aFacing, aRim);
            }
         }
      }
   }

   private static Vector3f unit(float phi, float theta) {
      return new Vector3f(Mth.sin(phi) * Mth.cos(theta), Mth.cos(phi), Mth.sin(phi) * Mth.sin(theta));
   }

   private static void sv(Matrix4f m, VertexConsumer vc, Vector3f c, float r, Vector3f n, float[] col, Vector3f toEye, float aFacing, float aRim) {
      float facing = Math.abs(n.dot(toEye));
      float a = Mth.lerp(facing, aRim, aFacing);
      vertex(m, vc, new Vector3f(n).mul(r).add(c), col[0], col[1], col[2], a);
   }

   private static void discFlat(Matrix4f m, VertexConsumer vc, Vector3f c, float r, float[] col, float aIn, float aOut) {
      int segs = 40;

      for (int i = 0; i < segs; i++) {
         float a0 = (float) (Math.PI * 2) * (float)i / (float)segs;
         float a1 = (float) (Math.PI * 2) * (float)(i + 1) / (float)segs;
         vertex(m, vc, c, col[0], col[1], col[2], aIn);
         vertex(m, vc, c, col[0], col[1], col[2], aIn);
         vertex(m, vc, new Vector3f(c).add(Mth.cos(a1) * r, 0.0F, Mth.sin(a1) * r), col[0], col[1], col[2], aOut);
         vertex(m, vc, new Vector3f(c).add(Mth.cos(a0) * r, 0.0F, Mth.sin(a0) * r), col[0], col[1], col[2], aOut);
      }
   }

   private static void vertex(Matrix4f m, VertexConsumer vc, Vector3f p, float r, float g, float b, float a) {
      vc.addVertex(m, p.x, p.y, p.z).setColor(r, g, b, a).setUv(0.5F, 0.5F);
   }

   private static final class Frame {
      final PhoenixEntity phoenix;
      final float t;
      final Vector3f base;
      final Vector3f viewer;
      final PhoenixPose bird;
      final PhoenixPose ghost;
      final Terrain ground;

      Frame(PhoenixEntity phoenix, float partial, Vec3 eye, ClientLevel level) {
         this.phoenix = phoenix;
         this.t = phoenix.age(partial);
         this.base = new Vector3f((float)(phoenix.getX() - eye.x), (float)(phoenix.getY() - eye.y), (float)(phoenix.getZ() - eye.z));
         this.viewer = new Vector3f(this.base).negate();
         this.bird = this.t < 236.0F ? PhoenixPose.flight(phoenix, this.t) : null;
         float since = this.t - 236.0F - 10.0F;
         if (since > 0.0F && since < 44.0F) {
            Vector3f back = new Vector3f(-this.viewer.x, 0.0F, -this.viewer.z);
            if (back.lengthSquared() < 1.0E-4F) {
               back.set(1.0F, 0.0F, 0.0F);
            }

            this.ghost = PhoenixPose.ghost(since, back);
         } else {
            this.ghost = null;
         }

         this.ground = PhoenixRenderer.GROUND.computeIfAbsent(phoenix, p -> Terrain.sample(level, p.position(), 60.0F));
      }

      Vector3f world(Vector3f local) {
         return new Vector3f(local).add(this.base);
      }
   }

   private static record Tongue(Vector3f root, float h, float w, float t, float seed) {
      void draw(Matrix4f m, VertexConsumer vc, boolean hot, float dim) {
         float sway = 0.18F * Mth.sin(this.t * 0.17F + this.seed * 2.3F) + 0.08F * Mth.sin(this.t * 0.43F + this.seed);
         Vector3f up = new Vector3f(sway, 1.0F, 0.6F * sway);
         int frame = (int)(this.t * 0.4F + this.seed * 3.1F);
         if (hot) {
            Sculpt.flame(m, vc, this.root, up, this.h * 0.95F, this.w * 1.5F, frame + 1, 1.0F, 1.0F, 1.0F, 0.65F * dim);
         } else {
            Sculpt.flame(m, vc, this.root, up, this.h * 1.35F, this.w * 2.6F, frame, 1.0F, 1.0F, 1.0F, 0.9F);
         }
      }
   }
}
