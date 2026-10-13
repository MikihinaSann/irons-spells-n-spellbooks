package com.grandsigil.client;

import com.grandsigil.GsPresence;
import com.grandsigil.config.GsConfig;
import com.grandsigil.entity.BoltEntity;
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class SkybreakRenderer {
   private static final float DOME = 300.0F;
   private static final float WORLD_DOME = 120.0F;
   private static final int CRACKS = 8;
   private static final float[] CORE = new float[]{0.94F, 0.98F, 1.0F};
   private static final float[] ARC = new float[]{0.62F, 0.8F, 1.0F};
   private static final double VIEW = 400.0;
   private static final ByteBufferBuilder BUILDER = new ByteBufferBuilder(32768);
   private static final BufferSource BUFFERS = MultiBufferSource.immediate(BUILDER);
   private static final Map<Integer, SkybreakRenderer.Cached> CACHE = new HashMap<>();

   private static float dome() {
      return Shaders.packInUse() ? 120.0F : 300.0F;
   }

   private SkybreakRenderer() {
   }

   public static void render(RenderLevelStageEvent event) {
      Stage stage = event.getStage();
      boolean skyPass = stage == Stage.AFTER_SKY;
      boolean worldPass = stage == Stage.AFTER_TRANSLUCENT_BLOCKS;
      if (skyPass || worldPass) {
         if (GsPresence.any(2)) {
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
                  if (entity instanceof BoltEntity) {
                     BoltEntity bolt = (BoltEntity)entity;
                     if (!(bolt.distanceToSqr(eye.x, eye.y, eye.z) > 160000.0)) {
                        any = true;
                        SkybreakRenderer.Cached cached = CACHE.computeIfAbsent(bolt.getId(), id -> new SkybreakRenderer.Cached());
                        if (skyPass) {
                           if (!Shaders.packInUse()) {
                              cracks(skyPose, bolt, cached, partial, eye);
                           }
                        } else {
                           world(pose, bolt, cached, partial, eye, camera, level);
                           if (Shaders.packInUse()) {
                              cracks(pose, bolt, cached, partial, eye);
                           }
                        }
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

   private static void cracks(PoseStack pose, BoltEntity bolt, SkybreakRenderer.Cached cached, float partial, Vec3 eye) {
      if (cached.cracks == null) {
         build(bolt, cached);
      }

      float reveal = bolt.spread(partial);

      float glow = switch (bolt.stage()) {
         case 0 -> 0.3F + 0.35F * reveal;
         case 1 -> 0.75F;
         default -> {
            float age = (float)bolt.stageAge() + partial;
            yield age < 6.0F ? 1.0F : Math.max(0.0F, 1.0F - (age - 6.0F) / 60.0F) * 0.7F;
         }
      };
      if (!(glow <= 0.004F)) {
         Vector3f origin = new Vector3f((float)(bolt.getX() - eye.x), (float)(bolt.getY() + 210.0 - eye.y), (float)(bolt.getZ() - eye.z));
         if (origin.lengthSquared() < 1.0F) {
            origin.set(0.0F, 1.0F, 0.0F);
         }

         origin.normalize();
         pose.pushPose();
         Matrix4f m = pose.last().pose();
         VertexConsumer vc = BUFFERS.getBuffer(GsRenderTypes.sky());
         Vector3f viewer = new Vector3f(0.0F, 0.0F, 0.0F);

         for (Bolt.Arc arc : cached.cracks) {
            float reach = arc.from().distance(origin) / cached.crackReach;
            if (!(reach > reveal)) {
               float edge = Mth.clamp((reveal - reach) * 6.0F, 0.0F, 1.0F);
               Vector3f from = new Vector3f(arc.from()).mul(dome());
               Vector3f to = new Vector3f(arc.to()).mul(dome());
               Geo.strand(
                  m,
                  vc,
                  from,
                  to,
                  viewer,
                  arc.width() * dome() * 3.2F,
                  ARC[0],
                  ARC[1],
                  ARC[2],
                  arc.alpha() * glow * edge * 0.22F,
                  arc.alpha() * glow * edge * 0.22F
               );
               Geo.strand(m, vc, from, to, viewer, arc.width() * dome(), CORE[0], CORE[1], CORE[2], arc.alpha() * glow * edge, arc.alpha() * glow * edge);
            }
         }

         BUFFERS.endBatch(GsRenderTypes.sky());
         pose.popPose();
      }
   }

   private static void build(BoltEntity bolt, SkybreakRenderer.Cached cached) {
      RandomSource random = RandomSource.create((long)bolt.getId() * 31L + 17L);
      List<Bolt.Arc> arcs = new ArrayList<>();
      Vector3f centre = new Vector3f(0.0F, 0.0F, 0.0F);
      Vector3f origin = new Vector3f(0.0F, 1.0F, 0.0F);

      for (int i = 0; i < 8; i++) {
         float azimuth = (float)((Math.PI * 2) * (double)i / 8.0) + random.nextFloat() * 0.5F;
         float reach = 0.55F + random.nextFloat() * 0.85F;
         Vector3f end = new Vector3f(Mth.cos(azimuth) * Mth.sin(reach), Mth.cos(reach), Mth.sin(azimuth) * Mth.sin(reach));
         Bolt.onSphere(arcs, centre, 1.0F, origin, end, random, 5, 0.11F, 0.0016F, 1.0F, 2);
      }

      float furthest = 0.001F;

      for (Bolt.Arc arc : arcs) {
         furthest = Math.max(furthest, arc.from().distance(origin));
      }

      cached.cracks = arcs;
      cached.crackReach = furthest;
   }

   private static void world(PoseStack pose, BoltEntity bolt, SkybreakRenderer.Cached cached, float partial, Vec3 eye, Camera camera, ClientLevel level) {
      int detail = (Integer)GsConfig.CLIENT.detail.get();
      int stage = bolt.stage();
      float sa = (float)bolt.stageAge() + partial;
      float radius = bolt.radius();
      pose.pushPose();
      pose.translate(bolt.getX() - eye.x, bolt.getY() - eye.y, bolt.getZ() - eye.z);
      Matrix4f m = pose.last().pose();
      Vector3f viewer = new Vector3f((float)(eye.x - bolt.getX()), (float)(eye.y - bolt.getY()), (float)(eye.z - bolt.getZ()));
      VertexConsumer floor = BUFFERS.getBuffer(GsRenderTypes.floor());
      scorch(m, floor, radius, stage, sa);
      BUFFERS.endBatch(GsRenderTypes.floor());
      VertexConsumer glow = BUFFERS.getBuffer(GsRenderTypes.glow());
      if (stage == 0) {
         static_(m, glow, viewer, bolt, radius, detail);
      }

      if (stage == 2) {
         column(m, glow, viewer, bolt, cached, level, radius, sa, detail);
      }

      BUFFERS.endBatch(GsRenderTypes.glow());
      pose.popPose();
   }

   private static void column(
      Matrix4f m, VertexConsumer vc, Vector3f viewer, BoltEntity bolt, SkybreakRenderer.Cached cached, ClientLevel level, float radius, float sa, int detail
   ) {
      int tick = bolt.stageAge();
      if (cached.strikeTick != tick) {
         cached.strikeTick = tick;
         regenerate(bolt, cached, level, radius, detail);
      }

      float brightness = flash(sa);
      if (!(brightness <= 0.004F) && cached.strike != null) {
         for (Bolt.Arc arc : cached.strike) {
            Geo.strand(
               m,
               vc,
               arc.from(),
               arc.to(),
               viewer,
               arc.width() * 3.4F,
               ARC[0],
               ARC[1],
               ARC[2],
               arc.alpha() * brightness * 0.3F,
               arc.alpha() * brightness * 0.3F
            );
            Geo.strand(m, vc, arc.from(), arc.to(), viewer, arc.width(), CORE[0], CORE[1], CORE[2], arc.alpha() * brightness, arc.alpha() * brightness);
         }

         if (cached.branches != null) {
            for (Bolt.Arc arc : cached.branches) {
               Geo.strand(
                  m,
                  vc,
                  arc.from(),
                  arc.to(),
                  viewer,
                  arc.width() * 3.0F,
                  ARC[0],
                  ARC[1],
                  ARC[2],
                  arc.alpha() * brightness * 0.26F,
                  arc.alpha() * brightness * 0.26F
               );
               Geo.strand(m, vc, arc.from(), arc.to(), viewer, arc.width(), CORE[0], CORE[1], CORE[2], arc.alpha() * brightness, arc.alpha() * brightness);
            }
         }
      }
   }

   private static float flash(float sa) {
      if (sa < 5.0F) {
         return 1.0F;
      } else if (sa < 8.0F) {
         return 0.45F;
      } else if (sa < 10.0F) {
         return 0.8F;
      } else {
         return sa < 14.0F ? 0.3F : Math.max(0.0F, 1.0F - (sa - 14.0F) / 8.0F) * 0.35F;
      }
   }

   private static void regenerate(BoltEntity bolt, SkybreakRenderer.Cached cached, ClientLevel level, float radius, int detail) {
      RandomSource random = RandomSource.create((long)bolt.getId() * 8191L + (long)bolt.stageAge());
      int levels = detail >= 2 ? 6 : (detail == 1 ? 5 : 4);
      List<Bolt.Arc> strike = new ArrayList<>();
      Bolt.generate(strike, new Vector3f(0.0F, 210.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F), random, levels, 0.055F, 3.36F, 1.0F, detail >= 1 ? 3 : 1);
      cached.strike = strike;
      List<Bolt.Arc> branches = new ArrayList<>();
      AABB box = new AABB(
         bolt.getX() - (double)radius,
         bolt.getY() - 12.0,
         bolt.getZ() - (double)radius,
         bolt.getX() + (double)radius,
         bolt.getY() + 32.0,
         bolt.getZ() + (double)radius
      );
      Minecraft minecraft = Minecraft.getInstance();

      for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, box, SkybreakRenderer::hostile)) {
         if (target != minecraft.player) {
            Vector3f to = new Vector3f(
               (float)(target.getX() - bolt.getX()),
               (float)(target.getY() + (double)target.getBbHeight() * 0.6 - bolt.getY()),
               (float)(target.getZ() - bolt.getZ())
            );
            if (!(to.length() > radius)) {
               Bolt.generate(branches, new Vector3f(0.0F, 1.5F, 0.0F), to, random, detail >= 2 ? 4 : 3, 0.22F, 0.35F, 0.9F, 1);
            }
         }
      }

      cached.branches = branches;
   }

   private static boolean hostile(LivingEntity target) {
      return target.isAlive() && (target instanceof Enemy || target instanceof Player);
   }

   private static void static_(Matrix4f m, VertexConsumer vc, Vector3f viewer, BoltEntity bolt, float radius, int detail) {
      if (detail >= 1) {
         float progress = (float)bolt.stageAge() / 60.0F;
         int count = 1 + Mth.floor(progress * 7.0F);
         RandomSource random = RandomSource.create((long)bolt.getId() * 131L + (long)bolt.stageAge());
         List<Bolt.Arc> crawl = new ArrayList<>();

         for (int i = 0; i < count; i++) {
            float a0 = random.nextFloat() * (float) (Math.PI * 2);
            float r0 = Mth.sqrt(random.nextFloat()) * radius;
            float length = 2.0F + random.nextFloat() * 6.0F;
            float a1 = a0 + (random.nextFloat() - 0.5F) * 1.6F;
            Vector3f from = new Vector3f(Mth.cos(a0) * r0, 0.4F, Mth.sin(a0) * r0);
            Vector3f to = new Vector3f(Mth.cos(a1) * (r0 + length), 0.4F, Mth.sin(a1) * (r0 + length));
            Bolt.generate(crawl, from, to, random, 3, 0.35F, 0.06F, 1.0F, 1);
         }

         float alpha = 0.35F + 0.5F * progress;

         for (Bolt.Arc arc : crawl) {
            Geo.strand(m, vc, arc.from(), arc.to(), viewer, arc.width(), CORE[0], CORE[1], CORE[2], arc.alpha() * alpha, arc.alpha() * alpha);
         }
      }
   }

   private static void scorch(Matrix4f m, VertexConsumer vc, float radius, int stage, float sa) {
      if (stage == 2) {
         float fade = Math.max(0.0F, 1.0F - sa / 90.0F);
         if (!(fade <= 0.004F)) {
            Geo.band(m, vc, Terrain.FLAT, 0.04F, radius * 0.96F, radius, 128, 0.0F, 1.0F, ARC[0], ARC[1], ARC[2], fade * 0.55F, 0.0F);
            Geo.disc(m, vc, Terrain.FLAT, 0.03F, 8.0F, 48, CORE[0], CORE[1], CORE[2], fade * 0.7F, 0.0F);
            float travelled = radius * (0.2F + sa * 0.1F);
            float ring = Math.max(0.0F, 1.0F - sa / 30.0F);
            if (ring > 0.004F) {
               Geo.band(m, vc, Terrain.FLAT, 0.06F, travelled * 0.93F, travelled, 128, 0.0F, 1.0F, CORE[0], CORE[1], CORE[2], ring, 0.0F);
            }
         }
      }
   }

   private static final class Cached {
      List<Bolt.Arc> cracks;
      float crackReach;
      List<Bolt.Arc> strike;
      List<Bolt.Arc> branches;
      int strikeTick = Integer.MIN_VALUE;
   }
}
