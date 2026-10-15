package com.somake.somakespells.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.somake.somakespells.client.RedSoulWingTransform;
import com.somake.somakespells.client.model.WingedRuinWingsModel;
import com.somake.somakespells.entity.RedEchoEntity;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.render.SpellRenderingHelper;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin.Model;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor.ARGB32;

public class RedEchoRenderer extends MobRenderer<RedEchoEntity, PlayerModel<RedEchoEntity>> {
   private static final ResourceLocation WING_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "legendary_monsters", "textures/entity/posessed_paladin/layer/possessed_paladin_wings_red_layer.png"
   );
   private final PlayerModel<RedEchoEntity> classicModel = (PlayerModel<RedEchoEntity>)this.model;
   private final PlayerModel<RedEchoEntity> slimModel;

   public RedEchoRenderer(Context context) {
      super(context, new PlayerModel(context.bakeLayer(ModelLayers.PLAYER), false), 0.0F);
      this.slimModel = new PlayerModel(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
      this.addLayer(new RedEchoRenderer.EchoBodyLayer(this));
      this.addLayer(new RedEchoRenderer.EchoWingLayer(this, context));
   }

   protected boolean isBodyVisible(RedEchoEntity entity) {
      return false;
   }

   public ResourceLocation getTextureLocation(RedEchoEntity entity) {
      UUID ownerId = entity.getOwnerId().orElse(entity.getUUID());
      return Minecraft.getInstance().level != null && Minecraft.getInstance().level.getPlayerByUUID(ownerId) instanceof AbstractClientPlayer clientPlayer
         ? clientPlayer.getSkin().texture()
         : DefaultPlayerSkin.get(ownerId).texture();
   }

   public void render(RedEchoEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
      this.model = this.isSlim(entity) ? this.slimModel : this.classicModel;
      super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
      SpellRenderingHelper.renderSpellHelper(ClientMagicData.getSyncedSpellData(entity), entity, poseStack, buffer, partialTick);
   }

   private boolean isSlim(RedEchoEntity entity) {
      UUID ownerId = entity.getOwnerId().orElse(entity.getUUID());
      return Minecraft.getInstance().level != null && Minecraft.getInstance().level.getPlayerByUUID(ownerId) instanceof AbstractClientPlayer clientPlayer
         ? clientPlayer.getSkin().model() == Model.SLIM
         : entity.usesSlimModel();
   }

   private static final class EchoBodyLayer extends RenderLayer<RedEchoEntity, PlayerModel<RedEchoEntity>> {
      private final RedEchoRenderer renderer;

      private EchoBodyLayer(RedEchoRenderer parent) {
         super(parent);
         this.renderer = parent;
      }

      public void render(
         PoseStack poseStack,
         MultiBufferSource buffer,
         int packedLight,
         RedEchoEntity entity,
         float limbSwing,
         float limbSwingAmount,
         float partialTick,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         ResourceLocation texture = this.renderer.getTextureLocation(entity);
         VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(texture));
         int color = ARGB32.colorFromFloat(0.72F, 1.0F, 0.08F, 0.1F);
         ((PlayerModel)this.getParentModel()).renderToBuffer(poseStack, consumer, 15728880, OverlayTexture.NO_OVERLAY, color);
      }
   }

   private static final class EchoWingLayer extends RenderLayer<RedEchoEntity, PlayerModel<RedEchoEntity>> {
      private final WingedRuinWingsModel wings;

      private EchoWingLayer(RenderLayerParent<RedEchoEntity, PlayerModel<RedEchoEntity>> parent, Context context) {
         super(parent);
         this.wings = new WingedRuinWingsModel(context.bakeLayer(WingedRuinWingsModel.LAYER_LOCATION));
      }

      public void render(
         PoseStack poseStack,
         MultiBufferSource buffer,
         int packedLight,
         RedEchoEntity entity,
         float limbSwing,
         float limbSwingAmount,
         float partialTick,
         float ageInTicks,
         float netHeadYaw,
         float headPitch
      ) {
         this.wings.setupRedSoulAnimation(ageInTicks, RedSoulWingTransform.WingState.CAST, 1.0F);
         poseStack.pushPose();
         ((PlayerModel)this.getParentModel()).body.translateAndRotate(poseStack);
         poseStack.translate(RedSoulWingTransform.x(), RedSoulWingTransform.y(), RedSoulWingTransform.z());
         poseStack.mulPose(Axis.XP.rotationDegrees(RedSoulWingTransform.rotX()));
         poseStack.mulPose(Axis.YP.rotationDegrees(RedSoulWingTransform.rotY()));
         poseStack.mulPose(Axis.ZP.rotationDegrees(RedSoulWingTransform.rotZ()));
         float scale = RedSoulWingTransform.scale();
         poseStack.scale(scale, scale, scale);
         VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(RedEchoRenderer.WING_TEXTURE));
         this.wings.renderToBuffer(poseStack, consumer, 15728880, OverlayTexture.NO_OVERLAY, ARGB32.colorFromFloat(0.72F, 1.0F, 0.18F, 0.16F));
         poseStack.popPose();
      }
   }
}
