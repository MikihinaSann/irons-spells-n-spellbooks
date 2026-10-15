package com.somake.somakespells.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.somake.somakespells.block.entity.PedestalOfFireBlockEntity;
import com.somake.somakespells.client.model.PedestalOfFireModel;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PedestalOfFireRenderer extends GeoBlockRenderer<PedestalOfFireBlockEntity> {
   private static final float ITEM_ORBIT_RADIUS = 0.55F;
   private static final float ITEM_HEIGHT = 1.15F;
   private static final float ITEM_SCALE = 0.45F;
   private static final float ITEM_SELF_SPIN_SPEED = 3.0F;
   private static final float ITEM_ORBIT_SPEED = 2.5F;
   private final ItemRenderer itemRenderer;

   public PedestalOfFireRenderer(Context context) {
      super(new PedestalOfFireModel());
      this.itemRenderer = context.getItemRenderer();
   }

   public void render(
      PedestalOfFireBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay
   ) {
      super.render(blockEntity, partialTick, poseStack, buffer, packedLight, packedOverlay);
      this.renderOrbitingItems(blockEntity, partialTick, poseStack, buffer, packedLight, packedOverlay);
   }

   private void renderOrbitingItems(
      PedestalOfFireBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay
   ) {
      List<ItemStack> stacks = new ArrayList<>();

      for (ItemStack stack : blockEntity.getItems()) {
         if (!stack.isEmpty()) {
            stacks.add(stack);
         }
      }

      if (!stacks.isEmpty()) {
         float time = blockEntity.tickCount + partialTick;
         float orbitRotation = time * 2.5F;
         float ritualProgress = blockEntity.getRitualProgress(partialTick);
         boolean ritualActive = blockEntity.isRitualActive();
         boolean previewActive = blockEntity.isPreviewActive();
         float previewPulse = previewActive ? (Mth.sin(time * 0.35F) + 1.0F) * 0.5F : 0.0F;
         float contractionProgress = ritualActive ? easeInOut(Mth.clamp(ritualProgress / 0.35F, 0.0F, 1.0F)) : 0.0F;
         float chargeProgress = ritualActive ? Mth.clamp((ritualProgress - 0.35F) / 0.35F, 0.0F, 1.0F) : 0.0F;
         float descentProgress = ritualActive ? easeIn(Mth.clamp((ritualProgress - 0.7F) / 0.2F, 0.0F, 1.0F)) : 0.0F;
         float fadeProgress = ritualActive ? Mth.clamp((ritualProgress - 0.9F) / 0.1F, 0.0F, 1.0F) : 0.0F;
         float orbitRadius = 0.55F;
         float itemHeight = 1.15F;
         float itemScale = 0.45F;
         if (previewActive) {
            orbitRadius = Mth.lerp(previewPulse, 0.55F, 0.495F);
            itemScale = Mth.lerp(previewPulse, 0.45F, 0.46799996F);
            itemHeight += previewPulse * 0.03F;
         }

         if (ritualActive) {
            orbitRadius = Mth.lerp(contractionProgress, orbitRadius, 0.02F);
            itemHeight = Mth.lerp(contractionProgress, itemHeight, 1.2099999F);
            itemScale = Mth.lerp(contractionProgress, itemScale, 0.486F);
            if (chargeProgress > 0.0F) {
               float chargePulse = (Mth.sin(time * 0.7F) + 1.0F) * 0.5F * chargeProgress;
               orbitRadius = Mth.lerp(chargeProgress, orbitRadius, 0.0F);
               itemScale = Mth.lerp(chargePulse, itemScale, 0.51299995F);
               itemHeight += chargePulse * 0.03F;
            }

            itemHeight = Mth.lerp(descentProgress, itemHeight, 0.22F);
            itemScale = Mth.lerp(descentProgress, itemScale, 0.081F);
         }

         if (!ritualActive || !(fadeProgress >= 0.95F)) {
            for (int i = 0; i < stacks.size(); i++) {
               float angleDeg = orbitRotation + 360.0F / stacks.size() * i;
               float angleRad = angleDeg * (float) (Math.PI / 180.0);
               float x = Mth.cos(angleRad) * orbitRadius;
               float z = Mth.sin(angleRad) * orbitRadius;
               poseStack.pushPose();
               poseStack.translate(0.5F + x, itemHeight, 0.5F + z);
               poseStack.mulPose(Axis.YP.rotationDegrees(-angleDeg + time * 3.0F));
               poseStack.scale(itemScale, itemScale, itemScale);
               this.itemRenderer
                  .render(
                     stacks.get(i),
                     ItemDisplayContext.GROUND,
                     false,
                     poseStack,
                     buffer,
                     packedLight,
                     OverlayTexture.NO_OVERLAY,
                     Minecraft.getInstance().getItemRenderer().getModel(stacks.get(i), blockEntity.getLevel(), (LivingEntity)null, 0)
                  );
               poseStack.popPose();
            }
         }
      }
   }

   private static float easeInOut(float value) {
      return value * value * (3.0F - 2.0F * value);
   }

   private static float easeIn(float value) {
      return value * value;
   }
}
