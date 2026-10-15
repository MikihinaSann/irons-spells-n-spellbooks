package com.somake.somakespells.render;

import com.github.L_Ender.cataclysm.client.render.entity.Scylla_Ceraunus_Renderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.somake.somakespells.entity.ConnectionChainVisualEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class ConnectionChainRenderer extends EntityRenderer<ConnectionChainVisualEntity> {
   private static final ResourceLocation CHAIN_TEXTURE = ResourceLocation.fromNamespaceAndPath("cataclysm", "textures/entity/scylla/scylla_chain.png");

   public ConnectionChainRenderer(Context context) {
      super(context);
   }

   public void render(
      ConnectionChainVisualEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight
   ) {
      LivingEntity firstTarget = entity.getFirstTarget();
      LivingEntity secondTarget = entity.getSecondTarget();
      if (firstTarget != null && secondTarget != null) {
         Vec3 firstCenter = firstTarget.getBoundingBox().getCenter();
         Vec3 secondCenter = secondTarget.getBoundingBox().getCenter();
         Vec3 entityPos = entity.position();
         Vec3 chainStart = firstCenter.subtract(entityPos);
         Vec3 chainVector = secondCenter.subtract(firstCenter);
         poseStack.pushPose();
         poseStack.translate(chainStart.x, chainStart.y, chainStart.z);
         VertexConsumer chainBuffer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(CHAIN_TEXTURE));
         Scylla_Ceraunus_Renderer.renderChainCube(chainVector, poseStack, chainBuffer, 15728880, OverlayTexture.NO_OVERLAY);
         poseStack.popPose();
      }
   }

   public boolean shouldRender(ConnectionChainVisualEntity entity, Frustum camera, double camX, double camY, double camZ) {
      return true;
   }

   public ResourceLocation getTextureLocation(ConnectionChainVisualEntity entity) {
      return CHAIN_TEXTURE;
   }
}
