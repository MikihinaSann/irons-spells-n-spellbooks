package com.userofbricks.expanded_combat.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.userofbricks.expanded_combat.api.client.IGauntletRenderer;
import com.userofbricks.expanded_combat.client.model.GauntletModel;
import com.userofbricks.expanded_combat.init.ECLayerDefinitions;
import com.userofbricks.expanded_combat.item.GauntletItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.component.DyedItemColor;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class GauntletRenderer implements IGauntletRenderer {
   private final GauntletModel model = new GauntletModel(Minecraft.getInstance().getEntityModels().bakeLayer(ECLayerDefinitions.GAUNTLET));

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
      if (stack.getItem() instanceof GauntletItem gauntletItem) {
         LivingEntity entity = slotContext.entity();
         this.model.setAllVisible(false);
         this.model.leftArm.visible = true;
         this.model.rightArm.visible = true;
         this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
         this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
         ICurioRenderer.followBodyRotations(entity, new HumanoidModel[]{this.model});
         ResourceLocation material = gauntletItem.material.id();

         for (GauntletItem.Layer layer : gauntletItem.GAUNTLET_TEXTURE_LAYERS) {
            VertexConsumer vertexconsumer = multiBufferSource.getBuffer(RenderType.armorCutoutNoCull(layer.texture(gauntletItem.material.id(), stack)));
            if (layer.dyeable()) {
               int i = ARGB32.opaque(DyedItemColor.getOrDefault(stack, -6265536));
               this.model.renderToBuffer(poseStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, i);
            } else {
               this.model.renderToBuffer(poseStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY);
            }
         }

         ArmorTrim armortrim = (ArmorTrim)stack.get(DataComponents.TRIM);
         if (armortrim != null) {
            this.renderTrim(material, poseStack, multiBufferSource, light, armortrim);
         }

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
         if (stack.getItem() instanceof GauntletItem gauntletItem) {
            ResourceLocation material = gauntletItem.material.id();

            for (GauntletItem.Layer layer : gauntletItem.GAUNTLET_TEXTURE_LAYERS) {
               VertexConsumer builder = multiBufferSource.getBuffer(RenderType.armorCutoutNoCull(layer.texture(gauntletItem.material.id(), stack)));
               if (layer.dyeable()) {
                  int i = ARGB32.opaque(DyedItemColor.getOrDefault(stack, -6265536));
                  modelPart.render(poseStack, builder, light, OverlayTexture.NO_OVERLAY, i);
               } else {
                  modelPart.render(poseStack, builder, light, OverlayTexture.NO_OVERLAY);
               }
            }

            ArmorTrim armortrim = (ArmorTrim)stack.get(DataComponents.TRIM);
            if (armortrim != null) {
               VertexConsumer vertexConsumer = this.getTrimVertexConsumer(material, multiBufferSource, armortrim);
               modelPart.render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
            }

            if (stack.hasFoil()) {
               modelPart.render(poseStack, multiBufferSource.getBuffer(RenderType.armorEntityGlint()), light, OverlayTexture.NO_OVERLAY);
            }
         }
      }
   }

   private void renderTrim(ResourceLocation material, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, ArmorTrim armorTrim) {
      VertexConsumer vertexconsumer = this.getTrimVertexConsumer(material, multiBufferSource, armorTrim);
      this.model.renderToBuffer(poseStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY);
   }

   private VertexConsumer getTrimVertexConsumer(ResourceLocation material, MultiBufferSource multiBufferSource, ArmorTrim armorTrim) {
      String materialSuffix = ((TrimMaterial)armorTrim.material().value()).assetName();
      if (materialSuffix.equals(material.getPath())) {
         materialSuffix = materialSuffix + "_darker";
      }

      ResourceLocation trimTexture = ResourceLocation.fromNamespaceAndPath(
         "expanded_combat", "trims/models/gauntlets/" + ((TrimPattern)armorTrim.pattern().value()).assetId().getPath() + "_" + materialSuffix
      );
      TextureAtlasSprite textureatlassprite = Minecraft.getInstance().getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET).getSprite(trimTexture);
      return textureatlassprite.wrap(multiBufferSource.getBuffer(Sheets.armorTrimsSheet(((TrimPattern)armorTrim.pattern().value()).decal())));
   }
}
