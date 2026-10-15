package com.somake.somakespells.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class BulwarkOfTheFlameModel<T extends Entity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("somakespells", "bulwark_of_the_flame"), "main"
   );
   private final ModelPart shield_root;
   private final ModelPart shield;
   private final ModelPart left;
   private final ModelPart right;

   public BulwarkOfTheFlameModel(ModelPart layerRoot) {
      this.shield_root = layerRoot.getChild("shield_root");
      this.shield = this.shield_root.getChild("shield");
      this.left = this.shield.getChild("left");
      this.right = this.shield.getChild("right");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition shield_root = partdefinition.addOrReplaceChild(
         "shield_root",
         CubeListBuilder.create().texOffs(112, 153).addBox(0.0F, -4.0F, -4.0F, 4.0F, 8.0F, 8.0F),
         PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, -1.5708F, -1.5708F, 0.0F)
      );
      PartDefinition shield = shield_root.addOrReplaceChild(
         "shield",
         CubeListBuilder.create()
            .texOffs(0, 57)
            .addBox(1.0F, -7.0F, -15.0F, 2.0F, 14.0F, 42.0F)
            .texOffs(90, 72)
            .addBox(0.0F, -8.0F, -26.0F, 4.0F, 16.0F, 11.0F)
            .texOffs(0, 57)
            .addBox(0.0F, -8.0F, 25.0F, 4.0F, 16.0F, 11.0F),
         PartPose.offset(0.0F, 0.0F, -3.0F)
      );
      PartDefinition right = shield.addOrReplaceChild(
         "right",
         CubeListBuilder.create()
            .texOffs(27, 151)
            .addBox(-2.0F, -6.0F, -29.5F, 4.0F, 14.0F, 8.0F)
            .texOffs(27, 151)
            .addBox(-2.0F, -6.0F, 20.5F, 4.0F, 14.0F, 8.0F)
            .texOffs(0, 0)
            .addBox(-1.0F, -7.0F, -21.5F, 2.0F, 14.0F, 42.0F),
         PartPose.offset(0.0F, 14.0F, 6.5F)
      );
      PartDefinition left = shield.addOrReplaceChild(
         "left",
         CubeListBuilder.create()
            .texOffs(27, 151)
            .addBox(-2.0F, -8.0F, -29.5F, 4.0F, 14.0F, 8.0F)
            .texOffs(27, 151)
            .addBox(-2.0F, -8.0F, 20.5F, 4.0F, 14.0F, 8.0F)
            .texOffs(47, 15)
            .addBox(-1.0F, -7.0F, -21.5F, 2.0F, 14.0F, 42.0F),
         PartPose.offset(0.0F, -14.0F, 6.5F)
      );
      return LayerDefinition.create(meshdefinition, 256, 256);
   }

   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
      this.shield_root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
   }
}
