package com.somake.somakespells.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.somake.somakespells.entity.SoulBastionShieldEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class SoulBastionShieldModel<T extends SoulBastionShieldEntity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("somakespells", "soul_bastion_shield"), "main"
   );
   private final ModelPart shield;

   public SoulBastionShieldModel(ModelPart root) {
      this.shield = root.getChild("shield");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition shield = partdefinition.addOrReplaceChild(
         "shield", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.0928F, 10.2885F, 0.1982F, 0.0F, -0.7854F, -3.1416F)
      );
      PartDefinition bone = shield.addOrReplaceChild(
         "bone",
         CubeListBuilder.create().texOffs(36, 19).addBox(-1.75F, -14.0F, -3.25F, 9.0F, 28.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      bone.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(10, 49).addBox(-6.0F, -1.0F, -4.0F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(1.0F, 0.0258F, 1.6803F, -0.7854F, 0.0F, -1.5708F)
      );
      bone.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create().texOffs(10, 19).addBox(-5.0F, -27.0F, -1.0F, 11.0F, 28.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-2.75F, 13.0F, 2.75F, 0.0F, 1.5708F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
      this.shield.render(poseStack, buffer, packedLight, packedOverlay, color);
   }
}
