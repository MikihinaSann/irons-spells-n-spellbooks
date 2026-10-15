package net.alshanex.alshanex_familiars.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.alshanex.alshanex_familiars.util.rituals.FlyingItem;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class RitualAutomatorRenderer implements BlockEntityRenderer<RitualAutomatorBlockEntity> {
   private final ItemRenderer itemRenderer;

   public RitualAutomatorRenderer(Context context) {
      this.itemRenderer = context.getItemRenderer();
   }

   public void render(RitualAutomatorBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
      for (FlyingItem item : be.getFlyingItems()) {
         this.renderFlyingItem(item, be.getBlockPos(), partialTick, poseStack, buffer, combinedOverlay, be.getLevel());
      }
   }

   private void renderFlyingItem(
      FlyingItem item, BlockPos originPos, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int overlay, Level level
   ) {
      poseStack.pushPose();
      float ticksPassed = item.getTotalTicks() - item.getTicksRemaining() + partialTick;
      float progress = Math.min(ticksPassed / item.getTotalTicks(), 1.0F);
      Vec3 start = Vec3.atCenterOf(item.getSourcePos());
      Vec3 end = Vec3.atCenterOf(item.getTargetPos()).add(0.0, 1.0, 0.0);
      Vec3 currentPos = start.lerp(end, progress);
      Vec3 localPos = currentPos.subtract(Vec3.atLowerCornerOf(originPos));
      poseStack.translate(localPos.x, localPos.y, localPos.z);
      double heightOffset = Math.sin(progress * Math.PI) * 0.5;
      poseStack.translate(0.0, heightOffset, 0.0);
      float spin = ticksPassed * 20.0F;
      poseStack.mulPose(Axis.YP.rotationDegrees(spin));
      poseStack.scale(0.5F, 0.5F, 0.5F);
      this.itemRenderer
         .renderStatic(
            item.getStack(),
            ItemDisplayContext.FIXED,
            LevelRenderer.getLightColor(level, BlockPos.containing(currentPos)),
            overlay,
            poseStack,
            buffer,
            level,
            0
         );
      poseStack.popPose();
   }

   public boolean shouldRenderOffScreen(RitualAutomatorBlockEntity be) {
      return true;
   }
}
