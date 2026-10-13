package com.grandsigil.client;

import com.grandsigil.GsPresence;
import com.grandsigil.config.GsConfig;
import com.grandsigil.entity.GripEntity;
import com.grandsigil.entity.Terrain;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class GripRenderer {
   private static final float[] FORCE = new float[]{0.62F, 0.24F, 0.94F};
   private static final float[] HOT = new float[]{0.92F, 0.62F, 1.0F};
   private static final float[] MARK = new float[]{0.86F, 0.3F, 0.98F};
   private static final int RINGS = 3;
   private static final float CAGE = 1.9F;
   private static final int RUBBLE = 40;
   private static final double VIEW = 400.0;
   private static final ByteBufferBuilder BUILDER = new ByteBufferBuilder(262144);
   private static final BufferSource BUFFERS = MultiBufferSource.immediate(BUILDER);
   private static final Map<Integer, GripRenderer.Cached> CACHE = new HashMap<>();

   private GripRenderer() {
   }

   public static void render(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_TRANSLUCENT_BLOCKS) {
         if (GsPresence.any(8)) {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            if (level != null) {
               Camera camera = event.getCamera();
               Vec3 eye = camera.getPosition();
               float partial = event.getPartialTick();
               PoseStack pose = event.getPoseStack();
               boolean any = false;

               for (Entity entity : level.entitiesForRendering()) {
                  if (entity instanceof GripEntity) {
                     GripEntity grip = (GripEntity)entity;
                     if (!(grip.distanceToSqr(eye.x, eye.y, eye.z) > 160000.0)) {
                        any = true;
                        draw(pose, grip, level, partial, eye, camera);
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

   private static void draw(PoseStack pose, GripEntity grip, ClientLevel level, float partial, Vec3 eye, Camera camera) {
      int detail = (Integer)GsConfig.CLIENT.detail.get();
      float here = grip.seized(partial);
      if (!(here <= 0.004F)) {
         float radius = grip.radius();
         float age = (float)grip.tickCount + partial;
         float risen = grip.risen(partial);
         float slam = grip.slam(partial);
         GripRenderer.Cached cached = CACHE.computeIfAbsent(grip.getId(), id -> build(grip, level, radius, detail));
         pose.pushPose();
         pose.translate(grip.getX() - eye.x, grip.getY() - eye.y, grip.getZ() - eye.z);
         Matrix4f m = pose.last().pose();
         Vector3f viewer = new Vector3f((float)(eye.x - grip.getX()), (float)(eye.y - grip.getY()), (float)(eye.z - grip.getZ()));
         Vector3f right = new Vector3f(camera.getLeftVector()).mul(-1.0F);
         Vector3f up = new Vector3f(camera.getUpVector());
         BufferSource world = Minecraft.getInstance().renderBuffers().bufferSource();
         isles(pose, world, cached, here, age, detail);
         rubble(pose, world, cached, risen, slam, age, detail);
         world.endBatch();
         VertexConsumer glow = BUFFERS.getBuffer(GsRenderTypes.glow());
         List<Vec3> caught = caught(grip, level);
         cages(m, glow, caught, right, up, viewer, age, slam);
         tethers(m, glow, cached, caught, here, age);
         marks(m, glow, cached, caught, radius, age, slam, detail);
         BUFFERS.endBatch(GsRenderTypes.glow());
         VertexConsumer floor = BUFFERS.getBuffer(GsRenderTypes.floor());
         ground(m, floor, cached, radius, here, age, slam, detail);
         BUFFERS.endBatch(GsRenderTypes.floor());
         pose.popPose();
      }
   }

   private static void isles(PoseStack pose, MultiBufferSource buffers, GripRenderer.Cached cached, float here, float age, int detail) {
      if (detail >= 1) {
         BlockRenderDispatcher blocks = Minecraft.getInstance().getBlockRenderer();
         int light = 15728880;

         for (GripRenderer.Isle isle : cached.isles) {
            float bob = Mth.sin(age * 0.012F + isle.phase()) * 1.4F;
            float arrive = 1.0F - here;

            for (GripRenderer.Slab slab : isle.slabs()) {
               pose.pushPose();
               pose.translate(isle.x(), isle.y() * here + bob + arrive * 40.0F, isle.z());
               pose.mulPose(Axis.YP.rotation(isle.turn() + age * isle.spin()));
               pose.translate(slab.x(), slab.y(), slab.z());
               pose.scale(slab.size(), slab.size(), slab.size());
               pose.translate(-0.5F, -0.5F, -0.5F);
               blocks.renderSingleBlock(slab.state(), pose, buffers, light, OverlayTexture.NO_OVERLAY);
               pose.popPose();
            }
         }
      }
   }

   private static void rubble(PoseStack pose, MultiBufferSource buffers, GripRenderer.Cached cached, float risen, float slam, float age, int detail) {
      if (detail >= 1 && !(risen <= 0.01F)) {
         BlockRenderDispatcher blocks = Minecraft.getInstance().getBlockRenderer();
         int light = 15728880;

         for (GripRenderer.Stone stone : cached.rubble) {
            float y = cached.floor.lift(stone.x(), stone.z()) + stone.lift() * risen;
            pose.pushPose();
            pose.translate(stone.x(), y, stone.z());
            pose.mulPose(Axis.YP.rotation(age * stone.spin() + stone.phase()));
            pose.mulPose(Axis.XP.rotation(age * stone.spin() * 0.7F));
            pose.scale(stone.size(), stone.size(), stone.size());
            pose.translate(-0.5F, -0.5F, -0.5F);
            blocks.renderSingleBlock(stone.state(), pose, buffers, light, OverlayTexture.NO_OVERLAY);
            pose.popPose();
         }
      }
   }

   private static void cages(Matrix4f m, VertexConsumer vc, List<Vec3> caught, Vector3f right, Vector3f up, Vector3f viewer, float age, float slam) {
      float heat = 1.0F + slam * 2.5F;

      for (Vec3 at : caught) {
         Vector3f centre = new Vector3f((float)at.x, (float)at.y, (float)at.z);

         for (int i = 0; i < 3; i++) {
            float turn = age * (0.06F + (float)i * 0.035F) * (i % 2 == 0 ? 1.0F : -1.0F);

            Vector3f axis = switch (i) {
               case 0 -> new Vector3f(0.0F, 1.0F, 0.0F);
               case 1 -> new Vector3f(Mth.cos(turn * 0.3F), 0.25F, Mth.sin(turn * 0.3F)).normalize();
               default -> new Vector3f(Mth.sin(turn * 0.2F), 0.7F, Mth.cos(turn * 0.2F)).normalize();
            };
            Vector3f a = perpendicular(axis);
            Vector3f b = new Vector3f(axis).cross(a).normalize();
            float r = 1.9F * (0.82F + 0.18F * Mth.sin(age * 0.09F + (float)i));
            Geo.ringIn(m, vc, centre, a, b, r * 0.94F, r, 28, turn, 1.0F, FORCE[0], FORCE[1], FORCE[2], 0.85F * heat, 0.2F * heat);
         }

         for (int i = 0; i < 4; i++) {
            float a = age * 0.05F + (float)((Math.PI * 2) * (double)i / 4.0);
            Vector3f from = new Vector3f(centre).add(Mth.cos(a) * 1.9F * 1.5F, 0.0F, Mth.sin(a) * 1.9F * 1.5F);
            Vector3f to = new Vector3f(centre).add(Mth.cos(a) * 1.9F * 0.6F, 0.0F, Mth.sin(a) * 1.9F * 0.6F);
            Geo.taper(m, vc, from, to, viewer, 0.02F, 0.16F, HOT[0], HOT[1], HOT[2], 0.0F, 0.9F * heat);
         }
      }
   }

   private static void tethers(Matrix4f m, VertexConsumer vc, GripRenderer.Cached cached, List<Vec3> caught, float here, float age) {
      if (!cached.isles.isEmpty()) {
         Vector3f viewer = new Vector3f(0.0F, 40.0F, 0.0F);
         int i = 0;

         for (Vec3 at : caught) {
            GripRenderer.Isle isle = cached.isles.get(i % cached.isles.size());
            i++;
            Vector3f from = new Vector3f(isle.x(), isle.y() * here, isle.z());
            Vector3f to = new Vector3f((float)at.x, (float)at.y + 1.9F, (float)at.z);
            float travel = (age * 0.06F + (float)i * 0.37F) % 1.0F;
            Vector3f mid = new Vector3f(from).lerp(to, travel);
            Geo.taper(m, vc, from, to, viewer, 0.06F, 0.06F, FORCE[0], FORCE[1], FORCE[2], 0.3F, 0.3F);
            Geo.taper(m, vc, new Vector3f(mid).lerp(from, 0.06F), new Vector3f(mid).lerp(to, 0.06F), viewer, 0.2F, 0.2F, HOT[0], HOT[1], HOT[2], 0.85F, 0.85F);
         }
      }
   }

   private static void marks(Matrix4f m, VertexConsumer vc, GripRenderer.Cached cached, List<Vec3> caught, float radius, float age, float slam, int detail) {
      if (detail >= 1) {
         Vector3f viewer = new Vector3f(0.0F, 40.0F, 0.0F);

         for (Vec3 at : caught) {
            float floor = cached.floor.lift((float)at.x, (float)at.z);
            float r = 3.2F * (1.0F - slam * 0.55F);
            float alpha = 0.35F + 0.5F * slam;

            for (int i = 0; i < 8; i++) {
               float a0 = age * 0.03F + (float)((Math.PI * 2) * (double)i / 8.0);
               float a1 = a0 + 0.42F;
               Geo.taper(
                  m,
                  vc,
                  new Vector3f((float)at.x + Mth.cos(a0) * r, floor + 0.08F, (float)at.z + Mth.sin(a0) * r),
                  new Vector3f((float)at.x + Mth.cos(a1) * r, floor + 0.08F, (float)at.z + Mth.sin(a1) * r),
                  viewer,
                  0.16F,
                  0.16F,
                  MARK[0],
                  MARK[1],
                  MARK[2],
                  alpha,
                  alpha
               );
            }
         }
      }
   }

   private static void ground(Matrix4f m, VertexConsumer vc, GripRenderer.Cached cached, float radius, float here, float age, float slam, int detail) {
      int segments = detail >= 2 ? 96 : 48;
      float r = radius * here;
      float alpha = 0.5F * here * (1.0F + slam * 1.4F);
      Geo.band(m, vc, cached.floor, 0.05F, r * 0.985F, r, segments, age * 0.004F, 1.0F, FORCE[0], FORCE[1], FORCE[2], alpha, 0.0F);
      Geo.ticks(m, vc, cached.floor, 0.05F, r * 0.9F, r * 0.96F, r * 1.02F, 24, 4, -age * 0.006F, 0.22F, 1.0F, MARK[0], MARK[1], MARK[2], alpha * 0.8F);
      if (slam > 0.2F) {
         float wave = (slam - 0.2F) / 0.8F;
         Geo.band(m, vc, cached.floor, 0.06F, radius * wave * 0.9F, radius * wave, segments, 0.0F, 1.0F, HOT[0], HOT[1], HOT[2], (1.0F - wave) * 0.8F, 0.0F);
      }
   }

   private static List<Vec3> caught(GripEntity grip, ClientLevel level) {
      float r = grip.radius();
      AABB box = new AABB(
         grip.getX() - (double)r, grip.getY() - 8.0, grip.getZ() - (double)r, grip.getX() + (double)r, grip.getY() + 30.0 + 12.0, grip.getZ() + (double)r
      );
      List<Vec3> out = new ArrayList<>();

      for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, box, t -> t.isAlive() && grip.grips(t))) {
         out.add(new Vec3(target.getX() - grip.getX(), target.getY() + (double)target.getBbHeight() * 0.5 - grip.getY(), target.getZ() - grip.getZ()));
      }

      return out;
   }

   private static Vector3f perpendicular(Vector3f axis) {
      Vector3f other = Math.abs(axis.y) > 0.9F ? new Vector3f(1.0F, 0.0F, 0.0F) : new Vector3f(0.0F, 1.0F, 0.0F);
      return new Vector3f(axis).cross(other).normalize();
   }

   private static GripRenderer.Cached build(GripEntity grip, ClientLevel level, float radius, int detail) {
      GripRenderer.Cached cached = new GripRenderer.Cached();
      RandomSource random = RandomSource.create((long)grip.getId() * 9173L + 31L);
      int count = detail >= 2 ? 7 : 4;
      List<GripRenderer.Isle> isles = new ArrayList<>(count);

      for (int i = 0; i < count; i++) {
         List<GripRenderer.Slab> slabs = new ArrayList<>();
         float span = 3.0F + random.nextFloat() * 5.0F;
         int layers = 2 + random.nextInt(3);

         for (int layer = 0; layer < layers; layer++) {
            float shrink = 1.0F - (float)layer / ((float)layers + 0.6F);
            int wide = Math.max(1, Mth.floor(span * shrink * 0.45F));

            for (int a = -wide; a <= wide; a++) {
               for (int o = -wide; o <= wide; o++) {
                  if (a * a + o * o <= wide * wide + 1) {
                     float size = 2.4F + random.nextFloat() * 1.2F;
                     slabs.add(new GripRenderer.Slab((float)a * size, (float)(-layer) * size, (float)o * size, size, Blocks.END_STONE.defaultBlockState()));
                  }
               }
            }
         }

         if (span > 5.5F) {
            int tall = 2 + random.nextInt(3);

            for (int h = 1; h <= tall; h++) {
               slabs.add(new GripRenderer.Slab(0.0F, (float)h * 2.8F, 0.0F, 2.8F, Blocks.OBSIDIAN.defaultBlockState()));
            }
         }

         float a = (float)((Math.PI * 2) * (double)i / (double)count) + random.nextFloat() * 0.5F;
         float rr = radius * (0.35F + random.nextFloat() * 0.75F);
         isles.add(
            new GripRenderer.Isle(
               Mth.cos(a) * rr,
               26.0F + random.nextFloat() * 36.0F,
               Mth.sin(a) * rr,
               random.nextFloat() * (float) (Math.PI * 2),
               (random.nextFloat() - 0.5F) * 0.004F,
               random.nextFloat() * (float) (Math.PI * 2),
               slabs
            )
         );
      }

      cached.isles = isles;
      List<GripRenderer.Stone> rubble = new ArrayList<>();
      int stones = detail >= 2 ? 40 : 20;
      BlockState[] kinds = new BlockState[]{
         Blocks.END_STONE.defaultBlockState(), Blocks.OBSIDIAN.defaultBlockState(), Blocks.END_STONE_BRICKS.defaultBlockState()
      };

      for (int i = 0; i < stones; i++) {
         float t = ((float)i + 0.5F) / (float)stones;
         float rr = Mth.sqrt(t) * radius * 0.92F;
         float a = (float)i * 2.3999631F;
         rubble.add(
            new GripRenderer.Stone(
               Mth.cos(a) * rr,
               Mth.sin(a) * rr,
               2.0F + random.nextFloat() * 25.5F,
               0.6F + random.nextFloat() * 1.5F,
               (random.nextFloat() - 0.5F) * 0.05F,
               random.nextFloat() * (float) (Math.PI * 2),
               kinds[random.nextInt(kinds.length)]
            )
         );
      }

      cached.rubble = rubble;
      cached.floor = Terrain.sample(level, grip.position(), radius);
      return cached;
   }

   private static final class Cached {
      List<GripRenderer.Isle> isles;
      List<GripRenderer.Stone> rubble;
      Terrain floor;
   }

   private static record Isle(float x, float y, float z, float turn, float spin, float phase, List<GripRenderer.Slab> slabs) {
   }

   private static record Slab(float x, float y, float z, float size, BlockState state) {
   }

   private static record Stone(float x, float z, float lift, float size, float spin, float phase, BlockState state) {
   }
}
