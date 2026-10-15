package net.acetheeldritchking.discerning_the_eldritch.entity.render.armor;

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

public class AzArmorLowerCloakLayerPipeline extends AzArmorRendererPipelineContext {
   public AzArmorLowerCloakLayerPipeline(AzRendererPipeline<UUID, ItemStack> rendererPipeline) {
      super(rendererPipeline);
   }

   public AzArmorBoneContext boneContext() {
      return new AzArmorBoneContext() {
         protected AzBone armorLowerCloakLeftBone;
         protected AzBone armorLowerCloakRightBone;
         protected AzBone armorLeggingTorsoBone;

         public AzBone getArmorLeggingTorsoBone(AzBakedModel model) {
            return (AzBone)model.getBone("armorLeggingTorsoLayer").orElse(null);
         }

         public AzBone getArmorLowerCloakLeftBoneBone(AzBakedModel model) {
            return (AzBone)model.getBone("armorLeftLowerCloakLayer").orElse(null);
         }

         public AzBone getArmorLowerCloakRightBone(AzBakedModel model) {
            return (AzBone)model.getBone("armorRightLowerCloakLayer").orElse(null);
         }

         public void grabRelevantBones(AzBakedModel model, AzArmorBoneProvider boneProvider) {
            super.grabRelevantBones(model, boneProvider);
            this.armorLowerCloakLeftBone = this.getArmorLowerCloakLeftBoneBone(model);
            this.armorLowerCloakRightBone = this.getArmorLowerCloakRightBone(model);
            this.armorLeggingTorsoBone = this.getArmorLeggingTorsoBone(model);
         }

         public void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
            this.setAllVisible(false);
            this.setBoneVisible(this.armorLowerCloakLeftBone, false);
            this.setBoneVisible(this.armorLowerCloakRightBone, false);
            this.setBoneVisible(this.armorLeggingTorsoBone, false);
            switch (currentSlot) {
               case HEAD:
                  this.setBoneVisible(this.head, true);
                  break;
               case CHEST:
                  this.setBoneVisible(this.body, true);
                  this.setBoneVisible(this.rightArm, true);
                  this.setBoneVisible(this.leftArm, true);
                  this.setBoneVisible(this.armorLowerCloakLeftBone, true);
                  this.setBoneVisible(this.armorLowerCloakRightBone, true);
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

            if (this.armorLowerCloakLeftBone != null) {
               ModelPart modelPart = baseModel.leftLeg;
               RenderUtils.matchModelPartRot(modelPart, this.armorLowerCloakLeftBone);
               this.armorLowerCloakLeftBone.updatePosition(0.0F, 0.0F, modelPart.z);
            }

            if (this.armorLowerCloakRightBone != null) {
               ModelPart modelPart = baseModel.rightLeg;
               RenderUtils.matchModelPartRot(modelPart, this.armorLowerCloakRightBone);
               this.armorLowerCloakRightBone.updatePosition(0.0F, 0.0F, modelPart.z);
            }
         }
      };
   }
}
