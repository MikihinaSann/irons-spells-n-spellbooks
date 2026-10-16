package com.userofbricks.expanded_combat.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.userofbricks.expanded_combat.ExpandedCombat;
import com.userofbricks.expanded_combat.api.client.IGauntletRenderer;
import com.userofbricks.expanded_combat.client.model.MaulersModel;
import com.userofbricks.expanded_combat.init.ECLayerDefinitions;
import com.userofbricks.expanded_combat.item.GauntletItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class MaulersRenderer implements IGauntletRenderer {
   private final ResourceLocation GAUNTLET_TEXTURE = ExpandedCombat.modLoc("textures/model/gauntlet/maulers.png");
   private final MaulersModel model = new MaulersModel(Minecraft.getInstance().getEntityModels().bakeLayer(ECLayerDefinitions.MAULERS));

   public <T extends LivingEntity, M extends EntityModel<T>> void render(
      ItemStack stack,
      SlotContext slotContext,
      PoseStack poseStack,
      RenderLayerParent<T, M> renderLayerParent,
      MultiBufferSource multiBufferSource,
      int light,
      float limbSwing,
      float limbSwingAmount,
      float partialTicks,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      if (stack.getItem() instanceof GauntletItem) {
         LivingEntity entity = slotContext.entity();
         this.model.setAllVisible(false);
         this.model.leftArm.visible = true;
         this.model.rightArm.visible = true;
         this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
         this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
         ICurioRenderer.followBodyRotations(entity, new HumanoidModel[]{this.model});
         this.renderModel(poseStack, multiBufferSource, light, this.model, this.GAUNTLET_TEXTURE);
         if (stack.hasFoil()) {
            this.model.renderToBuffer(poseStack, multiBufferSource.getBuffer(RenderType.armorEntityGlint()), light, OverlayTexture.NO_OVERLAY);
         }
      }
   }

   @Override
   public void renderFirstPersonArm(
      ItemStack stack, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, AbstractClientPlayer player, HumanoidArm arm, boolean hasFoil
   ) {
      if (!player.isSpectator()) {
         ModelPart modelPart = arm == HumanoidArm.LEFT ? this.model.leftArm : this.model.rightArm;
         this.model.setAllVisible(false);
         modelPart.visible = true;
         this.model.crouching = false;
         this.model.attackTime = this.model.swimAmount = 0.0F;
         this.model.setupAnim(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
         modelPart.xRot = 0.0F;
         if (stack.getItem() instanceof GauntletItem) {
            RenderType renderType = RenderType.armorCutoutNoCull(this.GAUNTLET_TEXTURE);
            VertexConsumer builder = multiBufferSource.getBuffer(renderType);
            modelPart.render(poseStack, builder, light, OverlayTexture.NO_OVERLAY);
            if (stack.hasFoil()) {
               modelPart.render(poseStack, multiBufferSource.getBuffer(RenderType.armorEntityGlint()), light, OverlayTexture.NO_OVERLAY);
            }
         }
      }
   }

   private void renderModel(PoseStack poseStack, MultiBufferSource multibuffersource, int light, Model model, ResourceLocation armorResource) {
      VertexConsumer vertexconsumer = multibuffersource.getBuffer(RenderType.armorCutoutNoCull(armorResource));
      model.renderToBuffer(poseStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY);
   }
}
