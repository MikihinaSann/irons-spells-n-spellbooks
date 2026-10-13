package com.grandsigil.client;

import com.grandsigil.GrandSigil;
import com.grandsigil.GsPresence;
import com.grandsigil.config.GsConfig;
import com.grandsigil.entity.GlacierEntity;
import com.grandsigil.entity.IceField;
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

public final class GlacierRenderer {
   private static final ResourceLocation RUNES = GrandSigil.id("textures/entity/runes.png");
   private static final ResourceLocation SCRIPT = GrandSigil.id("textures/entity/script.png");
   private static final float[] CORE = new float[]{0.13F, 0.34F, 0.47F};
   private static final float[] FACET = new float[]{0.62F, 0.92F, 1.0F};
   private static final float[] RIME = new float[]{0.84F, 0.95F, 1.0F};
   private static final float FALL = 5.5F;
   private static final float FROST_REACH = 0.96F;
   private static final double VIEW = 320.0;
   private static final ByteBufferBuilder BUILDER = new ByteBufferBuilder(65536);
   private static final BufferSource BUFFERS = MultiBufferSource.immediate(BUILDER);
   private static final Map<Integer, GlacierRenderer.Cached> CACHE = new HashMap<>();

   private GlacierRenderer() {
   }

   public static void render(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_TRANSLUCENT_BLOCKS) {
         if (GsPresence.any(5)) {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            if (level != null) {
               Camera camera = event.getCamera();
               Vec3 eye = camera.getPosition();
               float partial = event.getPartialTick();
               PoseStack pose = event.getPoseStack();
               boolean any = false;

               for (Entity entity : level.entitiesForRendering()) {
                  if (entity instanceof GlacierEntity) {
                     GlacierEntity glacier = (GlacierEntity)entity;
                     if (!(glacier.distanceToSqr(eye.x, eye.y, eye.z) > 102400.0)) {
                        any = true;
                        draw(pose, glacier, level, partial, eye);
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

   private static void draw(PoseStack pose, GlacierEntity glacier, ClientLevel level, float partial, Vec3 eye) {
      int detail = (Integer)GsConfig.CLIENT.detail.get();
      float broken = glacier.breaking(partial);
      if (!(broken >= 1.0F)) {
         float radius = glacier.radius();
         float age = (float)glacier.tickCount + partial;
         float altitude = glacier.altitude(partial);
         GlacierRenderer.Cached cached = CACHE.computeIfAbsent(glacier.getId(), id -> new GlacierRenderer.Cached());
         if (cached.seracs == null) {
            build(glacier, cached, level, radius, detail);
         }

         pose.pushPose();
         pose.translate(glacier.getX() - eye.x, glacier.getY() - eye.y, glacier.getZ() - eye.z);
         Matrix4f m = pose.last().pose();
         Vector3f viewer = new Vector3f((float)(eye.x - glacier.getX()), (float)(eye.y - glacier.getY()), (float)(eye.z - glacier.getZ()));
         VertexConsumer floor = BUFFERS.getBuffer(GsRenderTypes.floor());
         frost(m, floor, cached, radius, glacier.sitting(partial), broken, age);
         if (altitude > 0.5F) {
            float near = 1.0F - Mth.clamp(altitude / 170.0F, 0.0F, 1.0F);
            float dark = (0.14F + 0.62F * near) * (0.35F + 0.65F * near);
            Geo.discIn(
               m,
               floor,
               new Vector3f(0.0F, 0.06F, 0.0F),
               new Vector3f(1.0F, 0.0F, 0.0F),
               new Vector3f(0.0F, 0.0F, 1.0F),
               glacier.mass() * (1.42F - 0.36F * near),
               44,
               0.0F,
               0.01F,
               0.04F,
               dark,
               0.0F
            );
         }

         BUFFERS.endBatch(GsRenderTypes.floor());
         VertexConsumer mark = BUFFERS.getBuffer(GsRenderTypes.floor());
         sigil(m, mark, cached.ground, glacier, partial, age, detail);
         BUFFERS.endBatch(GsRenderTypes.floor());
         VertexConsumer marked = BUFFERS.getBuffer(GsRenderTypes.floor(SCRIPT));
         sigilScript(m, marked, cached.ground, glacier, partial, age, detail);
         BUFFERS.endBatch(GsRenderTypes.floor(SCRIPT));
         VertexConsumer lit = BUFFERS.getBuffer(GsRenderTypes.glow());
         sigilGlow(m, lit, cached.ground, glacier, partial, age, detail);
         BUFFERS.endBatch(GsRenderTypes.glow());
         VertexConsumer glyphs = BUFFERS.getBuffer(GsRenderTypes.glow(RUNES));
         sigilRunes(m, glyphs, cached.ground, glacier, partial, age);
         BUFFERS.endBatch(GsRenderTypes.glow(RUNES));
         pose.pushPose();
         pose.translate(0.0F, altitude, 0.0F);
         Matrix4f lifted = pose.last().pose();
         Vector3f raised = new Vector3f(viewer.x, viewer.y - altitude, viewer.z);
         VertexConsumer solid = BUFFERS.getBuffer(GsRenderTypes.stone());
         bodies(lifted, solid, cached, broken, age);
         BUFFERS.endBatch(GsRenderTypes.stone());
         VertexConsumer glow = BUFFERS.getBuffer(GsRenderTypes.glow());
         edges(lifted, glow, cached, raised, broken, age);
         BUFFERS.endBatch(GsRenderTypes.glow());
         pose.popPose();
         pose.popPose();
      }
   }

   private static int seg(int base, int detail) {
      return detail >= 2 ? base : (detail == 1 ? Math.max(8, base / 2) : Math.max(6, base / 3));
   }

   private static void sigil(Matrix4f m, VertexConsumer vc, Terrain ground, GlacierEntity glacier, float partial, float age, int detail) {
      float drawn = drawn(glacier, partial);
      if (!(drawn <= 0.004F)) {
         float r = glacier.mass();
         float a = alpha(glacier, partial);
         float spin = age * 0.0042F;
         float w = r * 0.005F;
         float y = 0.09F;
         Geo.band(m, vc, ground, y, r * 0.972F, r, seg(120, detail), spin, drawn, RIME[0], RIME[1], RIME[2], a, a * 0.3F);
         Geo.band(m, vc, ground, y, r * 0.928F, r * 0.944F, seg(120, detail), -spin * 1.6F, drawn, RIME[0], RIME[1], RIME[2], a * 0.85F, a * 0.85F);
         Geo.band(m, vc, ground, y, r * 0.472F, r * 0.492F, seg(96, detail), spin * 2.3F, drawn, RIME[0], RIME[1], RIME[2], a * 0.72F, a * 0.72F);
         if (detail >= 1) {
            Geo.ticks(m, vc, ground, y, r * 0.946F, r * 0.962F, r * 0.984F, detail >= 2 ? 144 : 72, 6, spin, w, drawn, RIME[0], RIME[1], RIME[2], a * 0.62F);
            Geo.star(m, vc, ground, y, r * 0.8F, 6, 2, spin, w * 2.6F, drawn, RIME[0], RIME[1], RIME[2], a * 0.7F);
            Geo.star(m, vc, ground, y, r * 0.8F, 6, 2, -spin + 0.5236F, w * 2.6F, drawn, RIME[0], RIME[1], RIME[2], a * 0.44F);
            Geo.star(m, vc, ground, y, r * 0.44F, 12, 5, spin * 1.8F, w * 1.5F, drawn, RIME[0], RIME[1], RIME[2], a * 0.38F);
         }

         Geo.disc(m, vc, ground, y, r * 0.12F, seg(36, detail), RIME[0], RIME[1], RIME[2], a * 0.8F, 0.0F);
      }
   }

   private static void sigilScript(Matrix4f m, VertexConsumer vc, Terrain ground, GlacierEntity glacier, float partial, float age, int detail) {
      float drawn = drawn(glacier, partial);
      if (!(drawn <= 0.004F)) {
         float r = glacier.mass();
         float a = alpha(glacier, partial);
         float spin = age * 0.0042F;
         Geo.script(m, vc, ground, 0.092F, r * 0.856F, r * 0.918F, seg(120, detail), spin, drawn, 18.0F, -spin * 0.55F, RIME[0], RIME[1], RIME[2], a * 0.92F);
      }
   }

   private static void sigilGlow(Matrix4f m, VertexConsumer vc, Terrain ground, GlacierEntity glacier, float partial, float age, int detail) {
      float drawn = drawn(glacier, partial);
      if (!(drawn <= 0.004F)) {
         float r = glacier.mass();
         float a = alpha(glacier, partial);
         float spin = age * 0.0042F;
         float y = 0.11F;

         for (int i = 0; i < 6; i++) {
            float t = spin + (float)((Math.PI * 2) * (double)i / 6.0);
            float inner = r * 0.135F;
            float outer = r * (0.135F + 0.79F * drawn);
            Geo.stroke(
               m,
               vc,
               ground,
               y,
               Mth.cos(t) * inner,
               Mth.sin(t) * inner,
               Mth.cos(t) * outer,
               Mth.sin(t) * outer,
               r * 0.0075F,
               RIME[0],
               RIME[1],
               RIME[2],
               a * 0.85F
            );
            float barbAt = r * (0.135F + 0.79F * drawn * 0.58F);

            for (int side = -1; side <= 1; side += 2) {
               float b = t + (float)side * 0.55F;
               Geo.stroke(
                  m,
                  vc,
                  ground,
                  y,
                  Mth.cos(t) * barbAt,
                  Mth.sin(t) * barbAt,
                  Mth.cos(t) * barbAt + Mth.cos(b) * r * 0.16F * drawn,
                  Mth.sin(t) * barbAt + Mth.sin(b) * r * 0.16F * drawn,
                  r * 0.005F,
                  RIME[0],
                  RIME[1],
                  RIME[2],
                  a * 0.6F
               );
            }
         }

         if (detail >= 1) {
            Geo.band(m, vc, ground, y, r * 0.134F, r * 0.146F, seg(48, detail), -spin * 3.0F, drawn, RIME[0], RIME[1], RIME[2], a * 0.9F, a * 0.9F);
         }
      }
   }

   private static void sigilRunes(Matrix4f m, VertexConsumer vc, Terrain ground, GlacierEntity glacier, float partial, float age) {
      float drawn = drawn(glacier, partial);
      if (!(drawn <= 0.004F)) {
         float r = glacier.mass();
         float a = alpha(glacier, partial);
         float spin = age * 0.0042F;

         for (int i = 0; i < 6; i++) {
            float lit = Mth.clamp(drawn * 6.0F - (float)i, 0.0F, 1.0F);
            if (!(lit <= 0.01F)) {
               float t = spin + (float)((Math.PI * 2) * (double)i / 6.0);
               float at = r * 0.8F;
               int cell = (i * 5 + 3) % 16;
               float u0 = (float)(cell % 4) * 0.25F;
               float v0 = (float)(cell / 4) * 0.25F;
               float pulse = 0.72F + 0.28F * Mth.sin(age * 0.06F + (float)i * 1.9F);
               Geo.seat(
                  m,
                  vc,
                  ground,
                  0.115F,
                  Mth.cos(t) * at,
                  Mth.sin(t) * at,
                  r * 0.062F,
                  t + (float) (Math.PI / 2),
                  u0,
                  v0,
                  u0 + 0.25F,
                  v0 + 0.25F,
                  RIME[0],
                  RIME[1],
                  RIME[2],
                  a * lit * pulse
               );
            }
         }
      }
   }

   private static float drawn(GlacierEntity glacier, float partial) {
      return glacier.stage() != 0 ? 1.0F : ease(Mth.clamp(((float)glacier.stageAge() + partial) / 45.0F, 0.0F, 1.0F));
   }

   private static float alpha(GlacierEntity glacier, float partial) {
      float age = (float)glacier.stageAge() + partial;

      return switch (glacier.stage()) {
         case 0, 1 -> 0.9F;
         case 2 -> age < 10.0F ? 1.6F * (1.0F - age / 10.0F) : 0.0F;
         default -> 0.0F;
      };
   }

   private static void bodies(Matrix4f m, VertexConsumer vc, GlacierRenderer.Cached cached, float broken, float age) {
      for (IceField.Serac serac : cached.seracs) {
         float height = serac.top() + serac.keel();
         float shimmer = 1.0F + 0.02F * Mth.sin(age * 0.04F + serac.delay() * 9.0F);
         float gone = broken <= 0.0F ? 0.0F : IceField.broken(serac, broken);
         if (!(gone >= 0.995F)) {
            float sink = serac.keel() + (serac.top() + serac.keel()) * gone * 0.75F;
            Geo.berg(
               m,
               vc,
               new Vector3f(serac.x(), -sink, serac.z()),
               serac.axis(),
               height * (1.0F - gone),
               serac.radius() * shimmer * (1.0F - gone * 0.45F),
               serac.sides(),
               serac.tilt() * (1.0F - gone * 0.6F),
               serac.seed(),
               CORE[0],
               CORE[1],
               CORE[2],
               1.0F,
               1.0F
            );
         }
      }
   }

   private static void edges(Matrix4f m, VertexConsumer vc, GlacierRenderer.Cached cached, Vector3f viewer, float broken, float age) {
      for (IceField.Serac serac : cached.seracs) {
         float height = serac.top() + serac.keel();
         float gone = broken <= 0.0F ? 0.0F : IceField.broken(serac, broken);
         if (!(gone >= 0.995F)) {
            float glow = (0.3F + 0.34F * Mth.clamp(2.2F / Math.max(0.6F, serac.radius()), 0.0F, 1.0F)) * (1.0F - gone * gone);
            float sink = serac.keel() + (serac.top() + serac.keel()) * gone * 0.75F;
            Geo.bergRim(
               m,
               vc,
               new Vector3f(serac.x(), -sink, serac.z()),
               serac.axis(),
               height * (1.0F - gone),
               serac.radius() * (1.0F - gone * 0.45F),
               serac.sides(),
               serac.tilt() * (1.0F - gone * 0.6F),
               serac.seed(),
               viewer,
               0.16F,
               FACET[0],
               FACET[1],
               FACET[2],
               glow
            );
         }
      }
   }

   private static float hash(float seed) {
      return Mth.frac(Mth.sin(seed) * 43758.547F);
   }

   private static void frost(Matrix4f m, VertexConsumer vc, GlacierRenderer.Cached cached, float radius, float risen, float broken, float age) {
      if (cached.frost != null) {
         float front = radius * 0.96F * ease(risen);
         float fade = 1.0F - broken * 0.7F;
         Vector3f viewer = new Vector3f(0.0F, 40.0F, 0.0F);

         for (Bolt.Arc arc : cached.frost) {
            float distance = arc.to().length();
            if (!(distance > front)) {
               float edge = Mth.clamp((front - distance) / 5.0F, 0.0F, 1.0F);
               float alpha = arc.alpha() * 0.42F * edge * fade;
               if (!(alpha <= 0.004F)) {
                  Geo.strand(m, vc, arc.from(), arc.to(), viewer, arc.width(), RIME[0], RIME[1], RIME[2], alpha, alpha * 0.7F);
               }
            }
         }

         float ring = radius * 0.96F;
         Geo.band(m, vc, Terrain.FLAT, 0.05F, ring * 0.985F, ring, 96, age * 0.002F, ease(risen), RIME[0], RIME[1], RIME[2], 0.5F * fade, 0.0F);
      }
   }

   private static float ease(float t) {
      return t * t * (3.0F - 2.0F * t);
   }

   private static void build(GlacierEntity glacier, GlacierRenderer.Cached cached, ClientLevel level, float radius, int detail) {
      cached.seracs = IceField.layout(glacier.getId(), glacier.mass());
      cached.ground = Terrain.sample(level, glacier.position(), glacier.mass());
      List<Bolt.Arc> frost = new ArrayList<>();
      RandomSource crack = RandomSource.create((long)glacier.getId() * 3163L + 5L);
      int runs = detail >= 2 ? 14 : 8;

      for (int i = 0; i < runs; i++) {
         float a = (float)((Math.PI * 2) * (double)i / (double)runs) + crack.nextFloat() * 0.4F;
         float far = radius * 0.96F * (0.65F + 0.35F * crack.nextFloat());
         Bolt.generate(
            frost, new Vector3f(0.0F, 0.06F, 0.0F), new Vector3f(Mth.cos(a) * far, 0.06F, Mth.sin(a) * far), crack, detail >= 2 ? 5 : 4, 0.3F, 0.3F, 1.0F, 2
         );
      }

      List<Bolt.Arc> flat = new ArrayList<>(frost.size());

      for (Bolt.Arc arc : frost) {
         flat.add(new Bolt.Arc(new Vector3f(arc.from().x, 0.06F, arc.from().z), new Vector3f(arc.to().x, 0.06F, arc.to().z), arc.width(), arc.alpha()));
      }

      cached.frost = flat;
   }

   private static final class Cached {
      List<IceField.Serac> seracs;
      List<Bolt.Arc> frost;
      Terrain ground;
   }
}
