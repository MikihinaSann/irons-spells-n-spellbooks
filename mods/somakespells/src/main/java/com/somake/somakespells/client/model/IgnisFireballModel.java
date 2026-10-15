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

public class IgnisFireballModel<T extends Entity> extends EntityModel<T> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("somakespells", "ignis_fireball"), "main"
   );
   private final ModelPart root;
   private final ModelPart core;
   private final ModelPart out_line;

   public IgnisFireballModel(ModelPart root) {
      this.root = root.getChild("root");
      this.core = this.root.getChild("core");
      this.out_line = this.root.getChild("out_line");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.ZERO);
      PartDefinition core = root.addOrReplaceChild(
         "core",
         CubeListBuilder.create()
            .texOffs(20, 37)
            .addBox(-4.0F, -4.0F, -1.0F, 8.0F, 8.0F, 2.0F)
            .texOffs(0, 31)
            .addBox(-1.0F, -4.0F, -4.0F, 2.0F, 8.0F, 8.0F)
            .texOffs(0, 21)
            .addBox(-4.0F, -1.0F, -4.0F, 8.0F, 2.0F, 8.0F),
         PartPose.ZERO
      );
      PartDefinition out_line = root.addOrReplaceChild(
         "out_line", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F), PartPose.ZERO
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.root.yRot = netHeadYaw * (float) (Math.PI / 180.0);
      this.root.xRot = headPitch * (float) (Math.PI / 180.0);
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
      this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
   }
}
