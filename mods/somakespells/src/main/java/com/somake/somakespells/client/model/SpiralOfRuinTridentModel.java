package com.somake.somakespells.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class SpiralOfRuinTridentModel extends Model {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("somakespells", "spiral_of_ruin_trident"), "main"
   );
   private final ModelPart trident;

   public SpiralOfRuinTridentModel(ModelPart root) {
      super(RenderType::entityTranslucentEmissive);
      this.trident = root.getChild("trident");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      PartDefinition trident = root.addOrReplaceChild(
         "trident",
         CubeListBuilder.create()
            .texOffs(71, 124)
            .addBox(-1.0F, -1.0F, -16.1429F, 2.0F, 2.0F, 37.0F, new CubeDeformation(0.0F))
            .texOffs(72, 126)
            .addBox(0.0F, -14.5F, -53.1429F, 0.0F, 29.0F, 37.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(1.0F, 18.0F, 1.1429F, 3.1416F, 0.0F, 0.0F)
      );
      trident.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(112, 149).addBox(-1.0F, -3.0F, -3.0F, 2.0F, 6.0F, 6.0F, new CubeDeformation(0.01F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, -17.1429F, 0.7854F, 0.0F, 0.0F)
      );
      trident.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create().texOffs(122, 180).addBox(0.0F, -4.0F, -6.0F, 0.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, -3.1429F, 3.1416F, 0.0F, 1.5708F)
      );
      trident.addOrReplaceChild(
         "cube_r3",
         CubeListBuilder.create().texOffs(122, 180).addBox(0.0F, -4.0F, -6.0F, 0.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, 31.8571F, 3.1416F, 0.0F, 1.5708F)
      );
      trident.addOrReplaceChild(
         "cube_r4",
         CubeListBuilder.create().texOffs(122, 180).addBox(0.0F, -4.0F, -6.0F, 0.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, -3.1429F, 3.1416F, 0.0F, 0.0F)
      );
      trident.addOrReplaceChild(
         "cube_r5",
         CubeListBuilder.create().texOffs(122, 180).addBox(0.0F, -4.0F, -6.0F, 0.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, 23.8571F, -3.1416F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(mesh, 256, 256);
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
      this.trident.render(poseStack, buffer, packedLight, packedOverlay, color);
   }
}
