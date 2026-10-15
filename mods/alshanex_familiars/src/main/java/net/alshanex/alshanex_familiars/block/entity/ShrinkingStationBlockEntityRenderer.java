package net.alshanex.alshanex_familiars.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import net.alshanex.alshanex_familiars.block.ShrinkingStationBlock;
import net.alshanex.familiarslib.util.consumables.FamiliarConsumableIntegration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ShrinkingStationBlockEntityRenderer implements BlockEntityRenderer<ShrinkingStationBlockEntity> {
   ItemRenderer itemRenderer;
   private static final Vec3 INPUT_POS = new Vec3(0.5, 1.4, 0.5);
   private static final Vec3 OUTPUT_POS = new Vec3(0.5, 1.4, 0.5);
   private static final Vec3 ANTENNA_NORTH_OFFSET = new Vec3(0.0, 1.5, -0.55);
   private static final Vec3 ANTENNA_SOUTH_OFFSET = new Vec3(0.0, 1.5, 0.55);

   public ShrinkingStationBlockEntityRenderer(Context context) {
      this.itemRenderer = context.getItemRenderer();
   }

   public void render(
      ShrinkingStationBlockEntity shrinkingStation, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay
   ) {
      ItemStack inputItem = shrinkingStation.getInputItem();
      ItemStack outputItem = shrinkingStation.getOutputItem();
      Player player = Minecraft.getInstance().player;
      float rotation = player.tickCount * 2 + partialTick;
      if (!inputItem.isEmpty()) {
         float scale = this.calculateItemScale(shrinkingStation, partialTick);
         this.renderItem(inputItem, INPUT_POS, rotation, scale, shrinkingStation, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
         if (shrinkingStation.isShrinkable()) {
            BlockState state = shrinkingStation.getBlockState();
            if (state.hasProperty(ShrinkingStationBlock.FACING)) {
               Direction facing = (Direction)state.getValue(ShrinkingStationBlock.FACING);
               float yRot = this.getRotationForFacing(facing);
               Vec3 centerPos = new Vec3(0.5, 0.0, 0.5);
               Vec3 northAntennaPos = centerPos.add(ANTENNA_NORTH_OFFSET.yRot((float)Math.toRadians(-yRot)));
               Vec3 southAntennaPos = centerPos.add(ANTENNA_SOUTH_OFFSET.yRot((float)Math.toRadians(-yRot)));
               this.spawnElectricParticles(northAntennaPos, INPUT_POS, shrinkingStation, 1);
               this.spawnElectricParticles(southAntennaPos, INPUT_POS, shrinkingStation, 1);
               this.spawnParticleAt(northAntennaPos, shrinkingStation);
               this.spawnParticleAt(southAntennaPos, shrinkingStation);
            }
         }
      }

      if (!outputItem.isEmpty()) {
         float outputScale = 0.325F;
         this.renderItem(
            outputItem, OUTPUT_POS, rotation * 0.5F, outputScale, shrinkingStation, partialTick, poseStack, bufferSource, packedLight, packedOverlay
         );
      }
   }

   private float calculateItemScale(ShrinkingStationBlockEntity shrinkingStation, float partialTick) {
      ItemStack inputItem = shrinkingStation.getInputItem();
      if (FamiliarConsumableIntegration.isConsumableItem(inputItem)) {
         return 0.325F;
      } else if (!shrinkingStation.isShrinkable()) {
         return 0.65F;
      } else {
         int counter = shrinkingStation.getCounter();
         float progress = (counter + partialTick) / 100.0F;
         progress = Math.min(progress, 1.0F);
         float baseScale = 1.0F - progress * 0.5F;
         return baseScale * 0.65F;
      }
   }

   private void renderItem(
      ItemStack itemStack,
      Vec3 offset,
      float yRot,
      float scale,
      ShrinkingStationBlockEntity shrinkingStation,
      float partialTick,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int packedLight,
      int packedOverlay
   ) {
      poseStack.pushPose();
      int renderId = (int)shrinkingStation.getBlockPos().asLong();
      poseStack.translate(offset.x, offset.y, offset.z);
      poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
      if (itemStack.getItem() instanceof SwordItem || itemStack.getItem() instanceof DiggerItem) {
         poseStack.mulPose(Axis.ZP.rotationDegrees(-45.0F));
      }

      poseStack.scale(scale, scale, scale);
      int brightLight = 15728880;
      this.itemRenderer
         .renderStatic(itemStack, ItemDisplayContext.FIXED, brightLight, packedOverlay, poseStack, bufferSource, shrinkingStation.getLevel(), renderId);
      poseStack.popPose();
   }

   private float getRotationForFacing(Direction facing) {
      return switch (facing) {
         case SOUTH -> 270.0F;
         case WEST -> 0.0F;
         case EAST -> 180.0F;
         default -> 90.0F;
      };
   }

   private void spawnElectricParticles(Vec3 startLocal, Vec3 endLocal, ShrinkingStationBlockEntity tile, int frequency) {
      if (tile.getLevel() != null) {
         Vec3 start = startLocal.add(tile.getBlockPos().getX(), tile.getBlockPos().getY(), tile.getBlockPos().getZ());
         Vec3 end = endLocal.add(tile.getBlockPos().getX(), tile.getBlockPos().getY(), tile.getBlockPos().getZ());

         for (int i = 0; i < frequency; i++) {
            float t = tile.getLevel().random.nextFloat();
            double x = start.x + (end.x - start.x) * t;
            double y = start.y + (end.y - start.y) * t;
            double z = start.z + (end.z - start.z) * t;
            double jitter = 0.05;
            double offsetX = (tile.getLevel().random.nextDouble() - 0.5) * jitter;
            double offsetY = (tile.getLevel().random.nextDouble() - 0.5) * jitter;
            double offsetZ = (tile.getLevel().random.nextDouble() - 0.5) * jitter;
            tile.getLevel().addParticle(ParticleTypes.ELECTRIC_SPARK, x + offsetX, y + offsetY, z + offsetZ, 0.0, 0.0, 0.0);
         }
      }
   }

   private void spawnParticleAt(Vec3 localPos, ShrinkingStationBlockEntity tile) {
      if (tile.getLevel() != null) {
         double x = tile.getBlockPos().getX() + localPos.x;
         double y = tile.getBlockPos().getY() + localPos.y;
         double z = tile.getBlockPos().getZ() + localPos.z;
         tile.getLevel().addParticle((ParticleOptions)ParticleRegistry.ELECTRICITY_PARTICLE.get(), x, y, z, 0.0, 0.0, 0.0);
      }
   }
}
