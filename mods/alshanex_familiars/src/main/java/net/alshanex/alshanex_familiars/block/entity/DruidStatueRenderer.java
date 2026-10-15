package net.alshanex.alshanex_familiars.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.phys.Vec3;

public class DruidStatueRenderer implements BlockEntityRenderer<DruidStatueEntity> {
   ItemRenderer itemRenderer;
   private static final Vec3 ITEM_POS = new Vec3(0.5, 2.0, 0.5);

   public DruidStatueRenderer(Context context) {
      this.itemRenderer = context.getItemRenderer();
   }

   public void render(DruidStatueEntity druidStatue, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
      ItemStack heldItem = druidStatue.getHeldItem();
      if (!heldItem.isEmpty()) {
         Player player = Minecraft.getInstance().player;
         float rotation = player.tickCount * 2 + partialTick;
         this.renderItem(heldItem, ITEM_POS, rotation, druidStatue, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
      }
   }

   private void renderItem(
      ItemStack itemStack,
      Vec3 offset,
      float yRot,
      DruidStatueEntity druidStatue,
      float partialTick,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int packedLight,
      int packedOverlay
   ) {
      poseStack.pushPose();
      int renderId = (int)druidStatue.getBlockPos().asLong();
      poseStack.translate(offset.x, offset.y, offset.z);
      poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
      if (itemStack.getItem() instanceof SwordItem || itemStack.getItem() instanceof DiggerItem) {
         poseStack.mulPose(Axis.ZP.rotationDegrees(-45.0F));
      }

      poseStack.scale(0.65F, 0.65F, 0.65F);
      this.itemRenderer
         .renderStatic(
            itemStack,
            ItemDisplayContext.FIXED,
            LevelRenderer.getLightColor(druidStatue.getLevel(), druidStatue.getBlockPos()),
            packedOverlay,
            poseStack,
            bufferSource,
            druidStatue.getLevel(),
            renderId
         );
      poseStack.popPose();
   }
}
