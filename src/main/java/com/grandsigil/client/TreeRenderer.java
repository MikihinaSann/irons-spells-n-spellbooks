package com.grandsigil.client;

import com.grandsigil.GrandSigil;
import com.grandsigil.GsPresence;
import com.grandsigil.config.GsConfig;
import com.grandsigil.entity.Terrain;
import com.grandsigil.entity.TreeEntity;
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

public final class TreeRenderer {
   private static final ResourceLocation LEAF = GrandSigil.id("textures/entity/leaf.png");
   private static final float[] LIMB = new float[]{0.26F, 0.82F, 0.4F};
   private static final float[] HEART = new float[]{0.74F, 1.0F, 0.66F};
   private static final float[] ROOT = new float[]{0.16F, 0.52F, 0.28F};
   private static final float[] GREEN = new float[]{0.4F, 1.0F, 0.36F};
   private static final float[] GOLD = new float[]{1.0F, 0.86F, 0.32F};
   private static final float GROWTH_TICKS = 30.0F;
   private static final float FRONT = 5.0F;
   private static final float TIP_WIDTH = 0.34F;
   private static final double VIEW = 320.0;
   private static final ByteBufferBuilder BUILDER = new ByteBufferBuilder(32768);
   private static final BufferSource BUFFERS = MultiBufferSource.immediate(BUILDER);
   private static final Map<Integer, TreeRenderer.Cached> CACHE = new HashMap<>();

   private TreeRenderer() {
   }

   public static void render(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_TRANSLUCENT_BLOCKS) {
         if (GsPresence.any(3)) {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            if (level != null) {
               Camera camera = event.getCamera();
               Vec3 eye = camera.getPosition();
               float partial = event.getPartialTick();
               PoseStack pose = event.getPoseStack();
               boolean any = false;

               for (Entity entity : level.entitiesForRendering()) {
                  if (entity instanceof TreeEntity) {
                     TreeEntity tree = (TreeEntity)entity;
                     if (!(tree.distanceToSqr(eye.x, eye.y, eye.z) > 102400.0)) {
                        any = true;
                        draw(pose, tree, partial, eye, camera);
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

   private static void draw(PoseStack pose, TreeEntity tree, float partial, Vec3 eye, Camera camera) {
      int detail = (Integer)GsConfig.CLIENT.detail.get();
      int generation = tree.generation();
      float radius = tree.radius();
      float risen = tree.risen(partial);
      float age = (float)tree.tickCount + partial;
      TreeRenderer.Cached cached = CACHE.computeIfAbsent(tree.getId(), id -> new TreeRenderer.Cached());
      grow(tree, cached, generation, radius, age, detail);
      float alpha = tree.stage() == 2 ? Math.max(0.0F, 1.0F - ((float)tree.stageAge() + partial) / 50.0F) : 1.0F;
      if (!(alpha <= 0.004F)) {
         float settle = ease(unfolding(cached, generation, age));
         float top = Mth.lerp(settle, trunkFor(Math.max(0, generation - 1)), trunkFor(generation)) * risen;
         pose.pushPose();
         pose.translate(tree.getX() - eye.x, tree.getY() - eye.y, tree.getZ() - eye.z);
         Matrix4f m = pose.last().pose();
         Vector3f viewer = new Vector3f((float)(eye.x - tree.getX()), (float)(eye.y - tree.getY()), (float)(eye.z - tree.getZ()));
         float reach = tree.rootReach(partial) * risen;
         VertexConsumer floor = BUFFERS.getBuffer(GsRenderTypes.floor());
         Geo.band(
            m,
            floor,
            Terrain.FLAT,
            0.04F,
            radius * reach * 0.94F,
            radius * reach,
            detail >= 2 ? 128 : 64,
            0.0F,
            1.0F,
            GREEN[0],
            GREEN[1],
            GREEN[2],
            alpha * 0.45F,
            0.0F
         );
         BUFFERS.endBatch(GsRenderTypes.floor());
         VertexConsumer glow = BUFFERS.getBuffer(GsRenderTypes.glow());
         trunk(m, glow, top, alpha, detail);
         roots(m, glow, viewer, cached, radius * reach, alpha);

         for (int g = 0; g <= generation && g < cached.batches.size(); g++) {
            limbs(m, glow, viewer, cached, g, unfolding(cached, g, age), alpha);
         }

         BUFFERS.endBatch(GsRenderTypes.glow());
         leaves(m, cached, camera, generation, age, alpha, detail);
         pose.popPose();
      }
   }

   private static float unfolding(TreeRenderer.Cached cached, int generation, float age) {
      return generation >= cached.arrivedAt.length ? 1.0F : Mth.clamp((age - cached.arrivedAt[generation]) / 30.0F, 0.0F, 1.0F);
   }

   private static float ease(float t) {
      return t * t * (3.0F - 2.0F * t);
   }

   private static float trunkFor(int generation) {
      return (34.0F + 5.0F * (float)generation) * 0.42F;
   }

   private static void limbs(Matrix4f m, VertexConsumer vc, Vector3f viewer, TreeRenderer.Cached cached, int generation, float unfolded, float alpha) {
      List<Bolt.Arc> batch = cached.batches.get(generation);
      float span = cached.batchReach[generation];
      float front = ease(unfolded) * span;

      for (Bolt.Arc arc : batch) {
         float distance = arc.from().length();
         if (!(distance > front)) {
            float edge = Mth.clamp((front - distance) / (span / 5.0F), 0.0F, 1.0F);
            float a = alpha * arc.alpha() * edge;
            Geo.taper(m, vc, arc.from(), arc.to(), viewer, arc.width() * edge, arc.width() * 0.72F * edge, LIMB[0], LIMB[1], LIMB[2], a, a * 0.75F);
         }
      }
   }

   private static void roots(Matrix4f m, VertexConsumer vc, Vector3f viewer, TreeRenderer.Cached cached, float front, float alpha) {
      for (Bolt.Arc arc : cached.roots) {
         float distance = arc.from().length();
         if (!(distance > front)) {
            float edge = Mth.clamp((front - distance) / 4.0F, 0.0F, 1.0F);
            float a = alpha * arc.alpha() * edge;
            Geo.taper(m, vc, arc.from(), arc.to(), viewer, arc.width() * edge, arc.width() * 0.7F * edge, ROOT[0], ROOT[1], ROOT[2], a * 0.85F, a * 0.5F);
         }
      }
   }

   private static void trunk(Matrix4f m, VertexConsumer vc, float top, float alpha, int detail) {
      int segments = detail >= 2 ? 20 : 10;
      if (!(top <= 0.1F)) {
         Geo.columnPlain(m, vc, 0.0F, 0.0F, 0.0F, top, 2.1F, 0.95F, segments, 0.0F, LIMB[0], LIMB[1], LIMB[2], alpha * 0.85F, alpha * 0.7F);
         Geo.columnPlain(m, vc, 0.0F, 0.0F, 0.0F, top, 1.1F, 0.45F, segments, 0.0F, HEART[0], HEART[1], HEART[2], alpha * 0.55F, alpha * 0.45F);
      }
   }

   private static void leaves(Matrix4f m, TreeRenderer.Cached cached, Camera camera, int generation, float age, float alpha, int detail) {
      if (detail >= 1) {
         Vector3f right = new Vector3f(camera.getLeftVector()).mul(-1.0F);
         Vector3f up = new Vector3f(camera.getUpVector());
         float golden = (float)generation / 6.0F;
         float cr = Mth.lerp(golden, GREEN[0], GOLD[0]);
         float cg = Mth.lerp(golden, GREEN[1], GOLD[1]);
         float cb = Mth.lerp(golden, GREEN[2], GOLD[2]);
         VertexConsumer vc = BUFFERS.getBuffer(GsRenderTypes.glow(LEAF));
         int index = 0;

         for (int g = 0; g <= generation && g < cached.batches.size(); g++) {
            float front = ease(unfolding(cached, g, age)) * cached.batchReach[g];

            for (Bolt.Arc arc : cached.batches.get(g)) {
               if (!(arc.width() > 0.34F)) {
                  float distance = arc.to().length();
                  if (!(distance > front)) {
                     index++;
                     float edge = Mth.clamp((front - distance) / 4.0F, 0.0F, 1.0F);
                     float breath = 0.85F + 0.15F * Mth.sin(age * 0.06F + (float)index * 0.7F);
                     Vector3f at = arc.to();
                     Geo.billboard(m, vc, at.x, at.y, at.z, right, up, 1.5F * breath * edge, 0.0F, 0.0F, 1.0F, 1.0F, cr, cg, cb, alpha * 0.85F * edge);
                  }
               }
            }
         }

         BUFFERS.endBatch(GsRenderTypes.glow(LEAF));
      }
   }

   private static void grow(TreeEntity tree, TreeRenderer.Cached cached, int generation, float radius, float age, int detail) {
      if (cached.roots == null) {
         buildRoots(tree, cached, radius);
      }

      while (cached.built < generation) {
         int g = cached.built + 1;
         cached.batches.add(buildBatch(tree, g, radius, detail, cached));
         cached.arrivedAt[g] = age;
         cached.built = g;
      }
   }

   private static List<Bolt.Arc> buildBatch(TreeEntity tree, int generation, float radius, int detail, TreeRenderer.Cached cached) {
      RandomSource random = RandomSource.create((long)tree.getId() * 7919L + (long)generation * 31L);
      int levels = (detail >= 2 ? 4 : 3) + generation / 3;
      int branches = 2 + generation / 2;
      float top = trunkFor(generation);
      int count = generation == 0 ? 5 : 3;
      float base = generation == 0 ? top * 0.72F : trunkFor(generation - 1) * 0.92F;
      List<Bolt.Arc> batch = new ArrayList<>();

      for (int i = 0; i < count; i++) {
         float azimuth = (float)((Math.PI * 2) * (double)i / (double)count) + (float)generation * 0.7F + random.nextFloat() * 0.5F;
         float lean = 0.45F + random.nextFloat() * 0.45F;
         float out = radius * (0.3F + 0.05F * (float)generation) * (0.7F + random.nextFloat() * 0.6F);
         Vector3f from = new Vector3f(0.0F, base + random.nextFloat() * top * 0.2F, 0.0F);
         Vector3f to = new Vector3f(
            Mth.cos(azimuth) * out * lean, top + (34.0F + 5.0F * (float)generation) * (0.26F + random.nextFloat() * 0.26F), Mth.sin(azimuth) * out * lean
         );
         Bolt.generate(batch, from, to, random, levels, 0.16F, 0.95F, 1.0F, branches);
      }

      float furthest = 0.001F;

      for (Bolt.Arc arc : batch) {
         furthest = Math.max(furthest, arc.from().length());
      }

      cached.batchReach[generation] = furthest;
      return batch;
   }

   private static void buildRoots(TreeEntity tree, TreeRenderer.Cached cached, float radius) {
      RandomSource random = RandomSource.create((long)tree.getId() * 4409L);
      List<Bolt.Arc> roots = new ArrayList<>();
      int count = 20;

      for (int i = 0; i < count; i++) {
         float azimuth = (float)((Math.PI * 2) * (double)i / (double)count) + random.nextFloat() * 0.4F;
         float out = radius * (0.6F + random.nextFloat() * 0.4F);
         Vector3f from = new Vector3f(0.0F, 0.35F, 0.0F);
         Vector3f to = new Vector3f(Mth.cos(azimuth) * out, 0.3F, Mth.sin(azimuth) * out);
         Bolt.generate(roots, from, to, random, 4, 0.26F, 0.6F, 1.0F, 2);
      }

      cached.roots = roots;
      cached.rootSpan = radius;
   }

   private static final class Cached {
      int built = -1;
      final List<List<Bolt.Arc>> batches = new ArrayList<>();
      final float[] batchReach = new float[7];
      final float[] arrivedAt = new float[7];
      List<Bolt.Arc> roots;
      float rootSpan = 1.0F;
   }
}
