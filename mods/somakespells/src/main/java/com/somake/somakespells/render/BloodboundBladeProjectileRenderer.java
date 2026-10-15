package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.somake.somakespells.entity.BloodboundBladeProjectileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class BloodboundBladeProjectileRenderer extends EntityRenderer<BloodboundBladeProjectileEntity> {
   private static final ResourceLocation GLOW_TEXTURE = ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/bloodbound_blade_glow.png");

   public BloodboundBladeProjectileRenderer(Context context) {
      super(context);
      this.shadowRadius = 0.0F;
   }

   public void render(
      BloodboundBladeProjectileEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight
   ) {
      ItemStack item = entity.getThrownItem();
      if (item.isEmpty()) {
         item = Items.IRON_SWORD.getDefaultInstance();
      }

      poseStack.pushPose();
      Vec3 motion = entity.deltaMovementOld.add(entity.getDeltaMovement().subtract(entity.deltaMovementOld).scale(partialTick));
      float xRot = -((float)(Mth.atan2(motion.horizontalDistance(), motion.y) * 180.0F / (float)Math.PI) - 90.0F);
      float yRot = -((float)(Mth.atan2(motion.z, motion.x) * 180.0F / (float)Math.PI) + 90.0F);
      poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
      poseStack.mulPose(Axis.XP.rotationDegrees(xRot - (entity.tickCount + partialTick) * 30.0F));
      float scale = entity.getScale();
      poseStack.scale(scale, scale, scale);
      Minecraft.getInstance()
         .getItemRenderer()
         .renderStatic(
            item, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId()
         );
      float pulse = 0.85F + Mth.sin((entity.tickCount + partialTick) * 0.45F) * 0.12F;
      VertexConsumer glow = buffer.getBuffer(RenderType.entityTranslucentEmissive(GLOW_TEXTURE));
      Matrix4f matrix = poseStack.last().pose();
      int color = ARGB32.colorFromFloat(0.7F, 1.0F, 0.12F, 0.16F);
      addVertex(glow, matrix, -0.72F * pulse, -0.72F * pulse, 0.0F, 0.0F, 1.0F, color);
      addVertex(glow, matrix, 0.72F * pulse, -0.72F * pulse, 0.0F, 1.0F, 1.0F, color);
      addVertex(glow, matrix, 0.72F * pulse, 0.72F * pulse, 0.0F, 1.0F, 0.0F, color);
      addVertex(glow, matrix, -0.72F * pulse, 0.72F * pulse, 0.0F, 0.0F, 0.0F, color);
      poseStack.popPose();
      super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
   }

   private static void addVertex(VertexConsumer consumer, Matrix4f pose, float x, float y, float z, float u, float v, int color) {
      consumer.addVertex(pose, x, y, z).setColor(color).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0.0F, 0.0F, 1.0F);
   }

   public ResourceLocation getTextureLocation(BloodboundBladeProjectileEntity entity) {
      return GLOW_TEXTURE;
   }
}
