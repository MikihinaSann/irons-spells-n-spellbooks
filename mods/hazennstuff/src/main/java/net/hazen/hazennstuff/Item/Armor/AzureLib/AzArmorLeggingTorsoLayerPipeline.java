package net.hazen.hazennstuff.Item.Armor.AzureLib;

import java.util.UUID;
import mod.azure.azurelib.common.model.AzBakedModel;
import mod.azure.azurelib.common.model.AzBone;
import mod.azure.azurelib.common.render.AzRendererPipeline;
import mod.azure.azurelib.common.render.armor.AzArmorRendererPipelineContext;
import mod.azure.azurelib.common.render.armor.bone.AzArmorBoneContext;
import mod.azure.azurelib.common.render.armor.bone.AzArmorBoneProvider;
import mod.azure.azurelib.common.util.client.RenderUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class AzArmorLeggingTorsoLayerPipeline extends AzArmorRendererPipelineContext {
   public AzArmorLeggingTorsoLayerPipeline(AzRendererPipeline<UUID, ItemStack> rendererPipeline) {
      super(rendererPipeline);
   }

   public AzArmorBoneContext boneContext() {
      return new AzArmorBoneContext() {
         protected AzBone armorLeggingTorsoBone;

         public AzBone getArmorLeggingTorsoBone(AzBakedModel model) {
            return (AzBone)model.getBone("armorLeggingTorsoLayer").orElse(null);
         }

         public void grabRelevantBones(AzBakedModel model, AzArmorBoneProvider boneProvider) {
            super.grabRelevantBones(model, boneProvider);
            this.armorLeggingTorsoBone = this.getArmorLeggingTorsoBone(model);
         }

         public void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
            this.setAllVisible(false);
            this.setBoneVisible(this.armorLeggingTorsoBone, false);
            switch (currentSlot) {
               case HEAD:
                  this.setBoneVisible(this.head, true);
                  break;
               case CHEST:
                  this.setBoneVisible(this.body, true);
                  this.setBoneVisible(this.rightArm, true);
                  this.setBoneVisible(this.leftArm, true);
                  break;
               case LEGS:
                  this.setBoneVisible(this.armorLeggingTorsoBone, true);
                  this.setBoneVisible(this.rightLeg, true);
                  this.setBoneVisible(this.leftLeg, true);
                  break;
               case FEET:
                  this.setBoneVisible(this.rightBoot, true);
                  this.setBoneVisible(this.leftBoot, true);
               case MAINHAND:
               case OFFHAND:
            }
         }

         public void applyBaseTransformations(HumanoidModel<?> baseModel) {
            super.applyBaseTransformations(baseModel);
            if (this.armorLeggingTorsoBone != null) {
               ModelPart modelPart = baseModel.body;
               RenderUtils.matchModelPartRot(modelPart, this.armorLeggingTorsoBone);
               this.armorLeggingTorsoBone.updatePosition(modelPart.x, -modelPart.y, modelPart.z);
            }
         }
      };
   }
}
