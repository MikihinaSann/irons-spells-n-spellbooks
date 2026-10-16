package com.userofbricks.expanded_combat.client.model;

import com.google.common.collect.ImmutableList;
import javax.annotation.Nonnull;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

public class GauntletModel extends HumanoidModel<LivingEntity> {
   public GauntletModel(ModelPart part) {
      super(part);
   }

   public static LayerDefinition createLayer() {
      CubeDeformation deformation = new CubeDeformation(0.5F);
      MeshDefinition mesh = HumanoidModel.createMesh(deformation, 0.0F);
      PartDefinition part = mesh.getRoot();
      part.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformation),
         PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -5.0F, 2.0F, 0.0F)
      );
      part.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().mirror().texOffs(16, 0).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformation),
         PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 5.0F, 2.0F, 0.0F)
      );
      return LayerDefinition.create(mesh, 32, 16);
   }

   @Nonnull
   protected Iterable<ModelPart> headParts() {
      return ImmutableList.of();
   }

   @Nonnull
   protected Iterable<ModelPart> bodyParts() {
      return ImmutableList.of(this.rightArm, this.leftArm);
   }
}
