package com.userofbricks.expanded_combat.client.model;

import com.google.common.collect.ImmutableList;
import javax.annotation.Nonnull;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class QuiverModel extends AgeableListModel<LivingEntity> {
   public ModelPart quiver;
   public ModelPart strap;

   public QuiverModel(ModelPart part) {
      this.quiver = part.getChild("quiver");
      this.strap = part.getChild("quiver_strap");
   }

   public static LayerDefinition createLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition part = mesh.getRoot();
      part.addOrReplaceChild(
         "quiver",
         CubeListBuilder.create().texOffs(20, 0).addBox(4.5F, -23.0F, 4.0F, 8.0F, 24.0F, 6.0F, new CubeDeformation(0.05F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.6981F)
      );
      part.addOrReplaceChild(
         "quiver_strap",
         CubeListBuilder.create().texOffs(0, 0).addBox(7.5F, -23.75F, -4.0F, 2.0F, 26.0F, 8.0F, new CubeDeformation(0.2F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.6981F)
      );
      return LayerDefinition.create(mesh, 64, 64);
   }

   @Nonnull
   protected Iterable<ModelPart> headParts() {
      return ImmutableList.of();
   }

   @Nonnull
   protected Iterable<ModelPart> bodyParts() {
      return ImmutableList.of(this.quiver, this.strap);
   }

   public void setupAnim(@NotNull LivingEntity livingEntity, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {
   }
}
