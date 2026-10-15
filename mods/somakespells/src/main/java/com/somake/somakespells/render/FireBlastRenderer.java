package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.somake.somakespells.entity.FireBlastVisualEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class FireBlastRenderer extends EntityRenderer<FireBlastVisualEntity> {
   public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("somakespells", "fire_blast_model"), "main"
   );
   private static final ResourceLocation FIRE_TEXTURE_CORE = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "textures/entity/projectile/fire_blast/core.png"
   );
   private static final ResourceLocation FIRE_TEXTURE_OVERLAY = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "textures/entity/projectile/fire_blast/overlay.png"
   );
   private static final ResourceLocation SOUL_TEXTURE_CORE = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "textures/entity/projectile/soul_fire_blast/core.png"
   );
   private static final ResourceLocation SOUL_TEXTURE_OVERLAY = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "textures/entity/projectile/soul_fire_blast/overlay.png"
   );
   private static final ResourceLocation INFERNAL_TEXTURE_CORE = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "textures/entity/projectile/infernal_fire_blast/core.png"
   );
   private static final ResourceLocation INFERNAL_TEXTURE_OVERLAY = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "textures/entity/projectile/infernal_fire_blast/overlay.png"
   );
   private final ModelPart body;

   public FireBlastRenderer(Context context) {
      super(context);
      ModelPart modelpart = context.bakeLayer(MODEL_LAYER_LOCATION);
      this.body = modelpart.getChild("body");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 32.0F, 16.0F), PartPose.ZERO);
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void render(FireBlastVisualEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      poseStack.pushPose();
      float lifetime = 8.0F;
      float scalar = 0.25F;
      float length = 32.0F * scalar * scalar;
      float f = entity.tickCount + partialTicks;
      poseStack.translate(0.0F, 0.0F, 0.0F);
      poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot() - 180.0F));
      poseStack.mulPose(Axis.XP.rotationDegrees(-entity.getXRot() - 90.0F));
      poseStack.scale(scalar, scalar, scalar);
      float alpha = Mth.clamp(1.0F - f / lifetime, 0.0F, 1.0F);

      for (float i = 0.0F; i < entity.distance * 4.0F; i += length) {
         poseStack.translate(0.0F, length, 0.0F);
         VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(this.getOverlayTextureLocation(entity)));
         poseStack.pushPose();
         float expansion = Mth.clampedLerp(1.2F, 0.0F, f / lifetime);
         poseStack.mulPose(Axis.YP.rotationDegrees(f * 5.0F));
         poseStack.scale(expansion, 1.0F, expansion);
         poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
         this.body.render(poseStack, consumer, 15728880, OverlayTexture.NO_OVERLAY, (int)(alpha * 255.0F) << 24 | 16777215);
         poseStack.popPose();
         consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity)));
         poseStack.pushPose();
         expansion = Mth.clampedLerp(1.0F, 0.0F, f / (lifetime - 5.0F));
         poseStack.scale(expansion, 1.0F, expansion);
         poseStack.mulPose(Axis.YP.rotationDegrees(f * -10.0F));
         this.body.render(poseStack, consumer, 15728880, OverlayTexture.NO_OVERLAY, -1);
         poseStack.popPose();
      }

      poseStack.popPose();
      super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   public ResourceLocation getTextureLocation(FireBlastVisualEntity entity) {
      if (entity.isSoulFire()) {
         return SOUL_TEXTURE_CORE;
      } else {
         return entity.isInfernalFire() ? INFERNAL_TEXTURE_CORE : FIRE_TEXTURE_CORE;
      }
   }

   public ResourceLocation getOverlayTextureLocation(FireBlastVisualEntity entity) {
      if (entity.isSoulFire()) {
         return SOUL_TEXTURE_OVERLAY;
      } else {
         return entity.isInfernalFire() ? INFERNAL_TEXTURE_OVERLAY : FIRE_TEXTURE_OVERLAY;
      }
   }
}
