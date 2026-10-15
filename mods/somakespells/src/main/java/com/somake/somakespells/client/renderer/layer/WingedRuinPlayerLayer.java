package com.somake.somakespells.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.somake.somakespells.client.RedSoulWingTransform;
import com.somake.somakespells.client.model.WingedRuinWingsModel;
import com.somake.somakespells.registries.ModMobEffects;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.effect.MobEffectInstance;

public class WingedRuinPlayerLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "legendary_monsters", "textures/entity/posessed_paladin/layer/possessed_paladin_wings_red_layer.png"
   );
   private static final Map<UUID, Integer> RED_SOUL_START_TICKS = new HashMap<>();
   private final WingedRuinWingsModel model;

   public WingedRuinPlayerLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent, EntityModelSet modelSet) {
      super(parent);
      this.model = new WingedRuinWingsModel(modelSet.bakeLayer(WingedRuinWingsModel.LAYER_LOCATION));
   }

   public void render(
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      AbstractClientPlayer player,
      float limbSwing,
      float limbSwingAmount,
      float partialTick,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      MobEffectInstance effect = player.getEffect(ModMobEffects.WINGED_RUIN);
      boolean redSoul = player.hasEffect(ModMobEffects.RED_SOUL);
      if (!redSoul) {
         RED_SOUL_START_TICKS.remove(player.getUUID());
      }

      if (effect != null || redSoul) {
         if (effect != null) {
            float elapsed = 73 - effect.getDuration() + partialTick;
            this.model.setupAnimation(elapsed);
         } else {
            int startTick = RED_SOUL_START_TICKS.computeIfAbsent(player.getUUID(), ignored -> player.tickCount);
            float activationElapsed = player.tickCount - startTick + partialTick;
            if (activationElapsed < 9.0F) {
               return;
            }

            float activationProgress = Math.min(1.0F, (activationElapsed - 9.0F) / 5.0F);
            boolean casting = MagicData.getPlayerMagicData(player).isCasting();
            RedSoulWingTransform.WingState state = resolveState(player, limbSwingAmount, casting);
            this.model.setupRedSoulAnimation(ageInTicks + partialTick, state, activationProgress);
         }

         poseStack.pushPose();
         ((PlayerModel)this.getParentModel()).body.translateAndRotate(poseStack);
         poseStack.translate(RedSoulWingTransform.x(), RedSoulWingTransform.y(), RedSoulWingTransform.z());
         poseStack.mulPose(Axis.XP.rotationDegrees(RedSoulWingTransform.rotX()));
         poseStack.mulPose(Axis.YP.rotationDegrees(RedSoulWingTransform.rotY()));
         poseStack.mulPose(Axis.ZP.rotationDegrees(RedSoulWingTransform.rotZ()));
         float scale = RedSoulWingTransform.scale();
         poseStack.scale(scale, scale, scale);
         VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE));
         this.model.renderToBuffer(poseStack, consumer, 15728880, OverlayTexture.NO_OVERLAY, ARGB32.colorFromFloat(0.5F, 1.0F, 1.0F, 1.0F));
         poseStack.popPose();
      }
   }

   private static RedSoulWingTransform.WingState resolveState(AbstractClientPlayer player, float limbSwingAmount, boolean casting) {
      RedSoulWingTransform.WingState preview = RedSoulWingTransform.previewState();
      if (preview != RedSoulWingTransform.WingState.AUTO) {
         return preview;
      } else if (casting) {
         return RedSoulWingTransform.WingState.CAST;
      } else if (player.isCrouching()) {
         return RedSoulWingTransform.WingState.CROUCH;
      } else if (!player.onGround()) {
         return RedSoulWingTransform.WingState.AIR;
      } else if (player.isSprinting()) {
         return RedSoulWingTransform.WingState.SPRINT;
      } else {
         return limbSwingAmount > 0.08F ? RedSoulWingTransform.WingState.WALK : RedSoulWingTransform.WingState.IDLE;
      }
   }
}
