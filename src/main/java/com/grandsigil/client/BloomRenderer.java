package com.grandsigil.client;

import com.grandsigil.GrandSigil;
import com.grandsigil.GsPresence;
import com.grandsigil.config.GsConfig;
import com.grandsigil.entity.BloomEntity;
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
import net.minecraft.client.renderer.RenderType;
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

public final class BloomRenderer {
   private static final ResourceLocation PETAL = GrandSigil.id("textures/entity/bloom_petal.png");
   private static final ResourceLocation LEAF = GrandSigil.id("textures/entity/bloom_leaf.png");
   private static final ResourceLocation VINE = GrandSigil.id("textures/entity/vine_flow.png");
   private static final ResourceLocation FLARE = GrandSigil.id("textures/entity/flare.png");
   private static final float[] GREEN = new float[]{0.45F, 0.95F, 0.45F};
   private static final float[] GOLD = new float[]{1.0F, 0.88F, 0.45F};
   private static final float[] PINK = new float[]{1.0F, 0.65F, 0.85F};
   private static final float[] WHITE = new float[]{1.0F, 1.0F, 0.92F};
   private static final float[][] RINGS = new float[][]{
      {8.0F, 26.0F, 80.0F, 0.55F, 15.0F, 0.0F}, {8.0F, 20.0F, 58.0F, 0.35F, 12.0F, 0.3927F}, {6.0F, 13.0F, 32.0F, 0.2F, 8.5F, 0.2F}
   };
   private static final int VINES = 5;
   private static final float[] LEAVES = new float[]{0.24F, 0.36F, 0.48F, 0.6F, 0.7F};
   private static final int MOTES = 380;
   private static final double VIEW = 320.0;
   private static final ByteBufferBuilder BUILDER = new ByteBufferBuilder(524288);
   private static final BufferSource BUFFERS = MultiBufferSource.immediate(BUILDER);
   private static final Map<BloomEntity, Terrain> GROUND = new WeakHashMap<>();

   private BloomRenderer() {
   }

   public static void render(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_TRANSLUCENT_BLOCKS && GsPresence.any(16)) {
         Minecraft minecraft = Minecraft.getInstance();
         ClientLevel level = minecraft.level;
         if (level != null) {
            Camera camera = event.getCamera();
            Vec3 eye = camera.getPosition();
            float partial = event.getPartialTick();
            int detail = (Integer)GsConfig.CLIENT.detail.get();
            List<BloomRenderer.View> views = new ArrayList<>();

            for (Entity entity : level.entitiesForRendering()) {
               if (entity instanceof BloomEntity) {
                  BloomEntity bloom = (BloomEntity)entity;
                  if (bloom.distanceToSqr(eye.x, eye.y, eye.z) < 102400.0) {
                     views.add(new BloomRenderer.View(bloom, partial, eye, level));
                  }
               }
            }

            if (!views.isEmpty()) {
               PoseStack pose = event.getPoseStack();
               pose.pushPose();
               Matrix4f m = pose.last().pose();
               boolean pack = Shaders.packInUse();
               float dim = pack ? 0.6F : 1.0F;
               batch(GsRenderTypes.matter(VINE), vc -> views.forEach(v -> vines(m, vc, v, false, 1.0F)));
               batch(GsRenderTypes.matter(LEAF), vc -> views.forEach(v -> leaves(m, vc, v, 0.85F)));
               batch(GsRenderTypes.matter(PETAL), vc -> views.forEach(v -> petals(m, vc, v, 0.92F)));
               batch(GsRenderTypes.glow(VINE), vc -> views.forEach(v -> vines(m, vc, v, true, dim)));
               batch(GsRenderTypes.glow(LEAF), vc -> views.forEach(v -> leaves(m, vc, v, 0.2F * dim)));
               batch(GsRenderTypes.glow(PETAL), vc -> views.forEach(v -> petals(m, vc, v, 0.12F * dim)));
               batch(GsRenderTypes.glow(), vc -> views.forEach(v -> {
                     stamens(m, vc, v, dim);
                     burst(m, vc, v, dim);
                     if (pack) {
                        motes(m, vc, v, camera, dim, false);
                     }
                  }));
               batch(GsRenderTypes.glow(), vc -> views.forEach(v -> {
                     pose.pushPose();
                     pose.translate(v.base.x, v.base.y, v.base.z);
                     circle(pose.last().pose(), vc, v, detail, dim);
                     pose.popPose();
                  }));
               if (!pack) {
                  batch(GsRenderTypes.glow(FLARE), vc -> views.forEach(v -> motes(m, vc, v, camera, dim, true)));
               }

               pose.popPose();
               BUFFERS.endBatch();
            }
         }
      }
   }

   private static void batch(RenderType type, BloomRenderer.Pass pass) {
      VertexConsumer vc = BUFFERS.getBuffer(type);
      pass.draw(vc);
      BUFFERS.endBatch(type);
   }

   private static Vector3f vine(BloomRenderer.View v, int j, float h) {
      float y = 46.0F * v.growth * h;
      float a = (float)j * (float) (Math.PI * 2) / 5.0F + y * 0.11F + v.time * 0.006F;
      float r = 2.6F * (1.0F - 0.45F * h) + 0.7F * Mth.sin(y * 0.25F + (float)j) + 6.0F * (float)Math.exp((double)(-y / 2.5F));
      return new Vector3f(v.base).add(Mth.cos(a) * r, y, Mth.sin(a) * r);
   }

   private static void vines(Matrix4f m, VertexConsumer vc, BloomRenderer.View v, boolean light, float dim) {
      if (!(v.growth <= 0.01F)) {
         int n = 28;

         for (int j = 0; j < 5; j++) {
            Sculpt.Ring[] rings = new Sculpt.Ring[n + 1];

            for (int k = 0; k <= n; k++) {
               float h = (float)k / (float)n;
               Vector3f p = vine(v, j, h);
               Vector3f q = vine(v, j, Math.min(1.0F, h + 0.02F)).sub(vine(v, j, Math.max(0.0F, h - 0.02F)));
               if (q.lengthSquared() < 1.0E-8F) {
                  q.set(0.0F, 1.0F, 0.0F);
               }

               q.normalize();
               Vector3f side = new Vector3f(q).cross(0.0F, 0.0F, 1.0F);
               if (side.lengthSquared() < 1.0E-6F) {
                  side.set(1.0F, 0.0F, 0.0F);
               }

               side.normalize();
               Vector3f up = new Vector3f(side).cross(q).normalize();
               float r = 1.15F * (1.0F - 0.55F * h) + 0.25F;
               rings[k] = new Sculpt.Ring(p, side, up, r, r, h * 6.0F, v.stemAlpha(h));
            }

            if (light) {
               Sculpt.energy(m, vc, rings, 10, 1.0F, -v.time * 0.06F, 0.6F, 0.8F, 1.0F, 0.7F, 0.6F * dim, 0.0F, 1.0F, 0.15F, v.time);
            } else {
               Sculpt.energy(m, vc, rings, 12, 1.0F, -v.time * 0.04F, 1.0F, 1.0F, 1.0F, 1.0F, 0.85F, 0.0F, 0.8F, 0.12F, v.time);
            }
         }
      }
   }

   private static void blade(
      Matrix4f m,
      VertexConsumer vc,
      Vector3f base,
      Vector3f dir,
      Vector3f bend,
      Vector3f side,
      float length,
      float width,
      float curl,
      float cup,
      float r,
      float g,
      float b,
      float a
   ) {
      if (!(a <= 0.01F) && !(length <= 0.05F)) {
         int nu = 10;
         int nv = 6;
         Vector3f d = new Vector3f(dir).normalize();
         Vector3f perp = new Vector3f(bend).sub(new Vector3f(d).mul(d.dot(bend)));
         if (perp.lengthSquared() < 1.0E-6F) {
            perp = new Vector3f(side).cross(d);
         }

         perp.normalize();
         Vector3f s = new Vector3f(side).normalize();
         Vector3f[] centre = new Vector3f[nu + 1];
         Vector3f[] normal = new Vector3f[nu + 1];
         Vector3f at = new Vector3f(base);

         for (int i = 0; i <= nu; i++) {
            float u = (float)i / (float)nu;
            float ang = curl * u;
            Vector3f heading = new Vector3f(d).mul(Mth.cos(ang)).add(new Vector3f(perp).mul(Mth.sin(ang)));
            if (i > 0) {
               at.add(new Vector3f(heading).mul(length / (float)nu));
            }

            centre[i] = new Vector3f(at);
            normal[i] = new Vector3f(heading).cross(s).normalize();
         }

         for (int i = 0; i < nu; i++) {
            for (int k = 0; k < nv; k++) {
               float v0 = -1.0F + 2.0F * (float)k / (float)nv;
               float v1 = -1.0F + 2.0F * (float)(k + 1) / (float)nv;
               Vector3f p00 = sheet(centre[i], s, normal[i], width, cup, v0);
               Vector3f p01 = sheet(centre[i], s, normal[i], width, cup, v1);
               Vector3f p11 = sheet(centre[i + 1], s, normal[i + 1], width, cup, v1);
               Vector3f p10 = sheet(centre[i + 1], s, normal[i + 1], width, cup, v0);
               float t0 = 1.0F - (float)i / (float)nu;
               float t1 = 1.0F - (float)(i + 1) / (float)nu;
               float u0 = (v0 + 1.0F) * 0.5F;
               float u1 = (v1 + 1.0F) * 0.5F;
               Sculpt.vertex(m, vc, p00, r, g, b, a, u0, t0);
               Sculpt.vertex(m, vc, p01, r, g, b, a, u1, t0);
               Sculpt.vertex(m, vc, p11, r, g, b, a, u1, t1);
               Sculpt.vertex(m, vc, p10, r, g, b, a, u0, t1);
            }
         }
      }
   }

   private static Vector3f sheet(Vector3f c, Vector3f side, Vector3f normal, float width, float cup, float v) {
      return new Vector3f(c).add(new Vector3f(side).mul(v * width * 0.5F)).add(new Vector3f(normal).mul(cup * width * v * v));
   }

   private static void leaves(Matrix4f m, VertexConsumer vc, BloomRenderer.View v, float a) {
      for (int i = 0; i < LEAVES.length; i++) {
         float h = LEAVES[i];
         float unfurl = BloomEntity.smooth((v.growth - h) / 0.15F);
         float alpha = a * unfurl * v.stemAlpha(h);
         if (!(alpha <= 0.01F)) {
            float az = (float)i * 2.4F + 0.5F;
            Vector3f radial = new Vector3f(Mth.cos(az), 0.0F, Mth.sin(az));
            Vector3f root = new Vector3f(v.base).add(new Vector3f(radial).mul(2.0F)).add(0.0F, 46.0F * v.growth * h, 0.0F);
            float sway = 0.08F * Mth.sin(v.time * 0.05F + (float)i);
            Vector3f dir = new Vector3f(radial).mul(0.85F).add(0.0F, 0.5F + sway, 0.0F);
            Vector3f side = new Vector3f(radial.z, 0.0F, -radial.x);
            float len = (16.0F - (float)i * 1.5F) * unfurl;
            blade(m, vc, root, dir, new Vector3f(0.0F, -1.0F, 0.0F), side, len, len * 0.55F, 0.9F * unfurl, 0.18F, 1.0F, 1.0F, 1.0F, alpha);
         }
      }
   }

   private static void petals(Matrix4f m, VertexConsumer vc, BloomRenderer.View v, float a) {
      if (!(v.growth < 0.5F)) {
         Vector3f crown = v.crown();
         float budIn = BloomEntity.smooth((v.growth - 0.6F) / 0.4F);

         for (int ring = 0; ring < RINGS.length; ring++) {
            float[] q = RINGS[ring];
            int n = (int)q[0];
            float opened = (float)Math.toRadians((double)Mth.lerp(Math.min(1.2F, v.open), 6.0F, q[2]));

            for (int i = 0; i < n; i++) {
               float az = q[5] + (float) (Math.PI * 2) * (float)i / (float)n + v.time * 0.003F;
               float leave = v.t - (274.0F + (float)(ring * n + i) * 1.4F);
               float drift = Math.max(0.0F, leave);
               float alpha = a * budIn * Mth.clamp(1.0F - drift / 22.0F, 0.0F, 1.0F);
               if (!(alpha <= 0.01F)) {
                  az += drift * 0.03F;
                  Vector3f radial = new Vector3f(Mth.cos(az), 0.0F, Mth.sin(az));
                  Vector3f side = new Vector3f(-radial.z, 0.0F, radial.x);
                  float flutter = 0.03F * Mth.sin(v.time * 0.12F + (float)i * 1.7F + (float)ring);
                  float tilt = opened + flutter + drift * 0.04F;
                  Vector3f dir = new Vector3f(0.0F, Mth.cos(tilt), 0.0F).add(new Vector3f(radial).mul(Mth.sin(tilt)));
                  Vector3f root = new Vector3f(crown)
                     .add(new Vector3f(radial).mul(1.5F + (float)ring * 0.3F))
                     .add(new Vector3f(radial).mul(drift * 0.9F))
                     .add(0.0F, drift * 0.5F - (float)ring * 0.4F, 0.0F);
                  float len = q[1] * (0.35F + 0.65F * budIn);
                  blade(m, vc, root, dir, radial, side, len, q[4], q[3] * (0.3F + 0.7F * Math.min(1.0F, v.open)), 0.22F, 1.0F, 1.0F, 1.0F, alpha);
               }
            }
         }
      }
   }

   private static void stamens(Matrix4f m, VertexConsumer vc, BloomRenderer.View v, float dim) {
      if (!(v.open <= 0.01F)) {
         Vector3f c = v.crown();
         float k = Math.min(1.0F, v.open) * (1.0F - v.fade) * dim;
         RandomSource random = RandomSource.create((long)v.bloom.getId() * 97L);
         Vector3f eye = new Vector3f();

         for (int i = 0; i < 28; i++) {
            float az = random.nextFloat() * (float) (Math.PI * 2) + v.time * 0.004F;
            float tilt = 0.2F + 0.5F * random.nextFloat();
            float len = 5.0F + 4.0F * random.nextFloat();
            Vector3f tip = new Vector3f(c).add(Mth.cos(az) * Mth.sin(tilt) * len, Mth.cos(tilt) * len, Mth.sin(az) * Mth.sin(tilt) * len);
            Geo.strand(m, vc, c, tip, eye, 0.08F, GOLD[0], GOLD[1], GOLD[2], 0.8F * k, 0.8F * k);
            Vector3f toEye = new Vector3f(tip).negate().normalize();
            Vector3f a1 = new Vector3f(toEye).cross(0.0F, 1.0F, 0.0F).normalize();
            Vector3f a2 = new Vector3f(toEye).cross(a1).normalize();
            Geo.discIn(m, vc, tip, a1, a2, 0.45F, 10, WHITE[0], WHITE[1], WHITE[2], k, 0.0F);
         }

         Vector3f toEye = new Vector3f(c).negate().normalize();
         Vector3f a1 = new Vector3f(toEye).cross(0.0F, 1.0F, 0.0F).normalize();
         Vector3f a2 = new Vector3f(toEye).cross(a1).normalize();
         Geo.discIn(m, vc, new Vector3f(c).add(0.0F, 1.5F, 0.0F), a1, a2, 4.5F, 24, GOLD[0], GOLD[1], GOLD[2], 0.7F * k, 0.0F);
      }
   }

   private static void burst(Matrix4f m, VertexConsumer vc, BloomRenderer.View v, float dim) {
      float since = v.t - 270.0F;
      if (!(since < 0.0F) && !(since > 30.0F)) {
         Vector3f c = v.crown();
         float k = (float)Math.exp((double)(-since / 5.0F)) * dim;
         Vector3f ex = new Vector3f(1.0F, 0.0F, 0.0F);
         Vector3f ez = new Vector3f(0.0F, 0.0F, 1.0F);
         float r = 4.0F + 50.0F * (1.0F - (float)Math.exp((double)(-since / 6.0F)));
         Geo.ringIn(m, vc, c, ex, ez, Math.max(0.0F, r - 9.0F), r, 96, 0.0F, 1.0F, GOLD[0], GOLD[1], GOLD[2], 0.0F, 0.45F * (1.0F - since / 30.0F) * dim);
         Vector3f toEye = new Vector3f(c).negate().normalize();
         Vector3f a1 = new Vector3f(toEye).cross(0.0F, 1.0F, 0.0F).normalize();
         Vector3f a2 = new Vector3f(toEye).cross(a1).normalize();
         Geo.discIn(m, vc, c, a1, a2, 6.0F + since * 1.2F, 32, WHITE[0], WHITE[1], WHITE[2], k, 0.0F);
      }
   }

   private static void circle(Matrix4f m, VertexConsumer vc, BloomRenderer.View v, int detail, float dim) {
      float k = Math.min(1.0F, v.field * dim * 1.6F);
      if (!(k <= 0.01F)) {
         float R = 48.0F * Mth.clamp(v.t / 20.0F, 0.0F, 1.0F);
         float spin = v.time * 0.004F;
         int segs = detail >= 2 ? 128 : 72;
         Geo.band(m, vc, v.ground, 0.08F, R - 1.2F, R, segs, spin, 1.0F, GREEN[0], GREEN[1], GREEN[2], 0.2F * k, 0.8F * k);
         Geo.band(m, vc, v.ground, 0.08F, R * 0.9F - 0.5F, R * 0.9F, segs, -spin, 1.0F, GOLD[0], GOLD[1], GOLD[2], 0.5F * k, 0.5F * k);
         Geo.ticks(m, vc, v.ground, 0.08F, R * 0.78F, R * 0.86F, R * 0.89F, 48, 4, spin * 2.0F, 0.12F, 1.0F, GREEN[0], GREEN[1], GREEN[2], 0.7F * k);
         Geo.band(m, vc, v.ground, 0.08F, 6.0F, 8.0F, segs, 0.0F, 1.0F, GOLD[0], GOLD[1], GOLD[2], 0.6F * k, 0.6F * k);
         Geo.disc(m, vc, v.ground, 0.06F, R, segs, GREEN[0], GREEN[1], GREEN[2], 0.22F * k, 0.0F);
         if (v.t >= 90.0F && v.t < 270.0F) {
            float q = (v.t - 90.0F) % 40.0F / 22.0F;
            if (q < 1.0F) {
               float r = 6.0F + 42.0F * q;
               float a = (1.0F - q) * dim;
               Geo.band(m, vc, v.ground, 0.12F, Math.max(0.0F, r - 3.0F), r, segs, 0.0F, 1.0F, GOLD[0], GOLD[1], GOLD[2], 0.0F, a);
            }
         }
      }
   }

   private static void motes(Matrix4f m, VertexConsumer vc, BloomRenderer.View v, Camera camera, float dim, boolean soft) {
      float k = v.field * dim;
      if (!(k <= 0.01F) && !(v.open <= 0.05F)) {
         Vector3f right = new Vector3f(camera.getLeftVector()).mul(-1.0F);
         Vector3f up = new Vector3f(camera.getUpVector());
         Vector3f crown = v.crown();
         RandomSource random = RandomSource.create((long)v.bloom.getId() * 131L);
         float wave = v.t >= 90.0F ? (v.t - 90.0F) % 40.0F / 22.0F : 2.0F;

         for (int j = 0; j < 380; j++) {
            float speed = 0.004F + 0.006F * random.nextFloat();
            float phase = random.nextFloat();
            float a0 = random.nextFloat() * (float) (Math.PI * 2);
            float size = 0.18F + 0.35F * random.nextFloat();
            int hue = random.nextInt(3);
            float q = (v.time * speed + phase) % 1.0F;
            float r = 3.0F + q * 45.0F;
            if (wave < 1.0F && Math.abs(q - wave) < 0.08F) {
               r += 3.0F;
               size *= 1.6F;
            }

            float y = (crown.y - v.base.y) * (float)Math.pow((double)(1.0F - q), 0.8) + 1.0F;
            float ang = a0 + q * 5.0F + v.time * 0.02F;
            float x = v.base.x + Mth.cos(ang) * r;
            float z = v.base.z + Mth.sin(ang) * r;
            float alpha = k * Mth.sin((float) Math.PI * q) * 0.6F;
            float[] c = hue == 0 ? GOLD : (hue == 1 ? PINK : GREEN);
            if (soft) {
               Geo.billboard(m, vc, x, v.base.y + y, z, right, up, size * 1.6F, 0.0F, 0.0F, 1.0F, 1.0F, c[0], c[1], c[2], alpha);
            } else {
               Vector3f p = new Vector3f(x, v.base.y + y, z);
               Vector3f rr = new Vector3f(right).mul(size * 0.4F);
               Vector3f uu = new Vector3f(up).mul(size * 0.4F);
               Sculpt.vertex(m, vc, new Vector3f(p).sub(rr), c[0], c[1], c[2], alpha, 0.5F, 0.5F);
               Sculpt.vertex(m, vc, new Vector3f(p).sub(uu), c[0], c[1], c[2], alpha, 0.5F, 0.5F);
               Sculpt.vertex(m, vc, new Vector3f(p).add(rr), c[0], c[1], c[2], alpha, 0.5F, 0.5F);
               Sculpt.vertex(m, vc, new Vector3f(p).add(uu), c[0], c[1], c[2], alpha, 0.5F, 0.5F);
            }
         }
      }
   }

   private interface Pass {
      void draw(VertexConsumer var1);
   }

   private static final class View {
      final BloomEntity bloom;
      final float t;
      final float time;
      final Vector3f base;
      final float growth;
      final float open;
      final float fade;
      final float field;
      final Terrain ground;

      View(BloomEntity bloom, float partial, Vec3 eye, ClientLevel level) {
         this.bloom = bloom;
         this.t = bloom.age(partial);
         this.time = (float)bloom.tickCount + partial;
         this.base = new Vector3f((float)(bloom.getX() - eye.x), (float)(bloom.getY() - eye.y), (float)(bloom.getZ() - eye.z));
         this.growth = BloomEntity.growth(this.t);
         this.open = BloomEntity.open(this.t);
         this.fade = BloomEntity.fade(this.t);
         this.field = BloomEntity.field(this.t);
         this.ground = BloomRenderer.GROUND.computeIfAbsent(bloom, b -> Terrain.sample(level, b.position(), 52.0F));
      }

      Vector3f crown() {
         return new Vector3f(this.base).add(0.0F, 46.0F * this.growth, 0.0F);
      }

      float stemAlpha(float h) {
         float cut = 1.0F - this.fade * 1.2F;
         return Mth.clamp((cut - h) * 5.0F, 0.0F, 1.0F);
      }
   }
}
