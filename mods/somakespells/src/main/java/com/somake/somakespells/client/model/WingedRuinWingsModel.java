package com.somake.somakespells.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.somake.somakespells.client.RedSoulWingTransform;
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
import net.minecraft.util.Mth;

public class WingedRuinWingsModel extends Model {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("somakespells", "winged_ruin_wings"), "main"
   );
   private final ModelPart rightWing;
   private final ModelPart rightEdge;
   private final ModelPart leftWing;
   private final ModelPart leftEdge;

   public WingedRuinWingsModel(ModelPart root) {
      super(RenderType::entityTranslucentEmissive);
      this.rightWing = root.getChild("RightWing");
      this.rightEdge = this.rightWing.getChild("RightWingEdge");
      this.leftWing = root.getChild("LeftWing");
      this.leftEdge = this.leftWing.getChild("LeftWingEdge");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      PartDefinition right = root.addOrReplaceChild(
         "RightWing",
         CubeListBuilder.create().texOffs(0, 96).addBox(0.0F, -14.5F, 0.0F, 0.0F, 33.0F, 25.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -0.1744F, 2.7874F, 0.0F, -1.3963F, 0.0F)
      );
      right.addOrReplaceChild(
         "RightWingEdge",
         CubeListBuilder.create().texOffs(0, 127).addBox(0.0F, -14.5F, 0.0F, 0.0F, 33.0F, 33.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 25.0F)
      );
      PartDefinition left = root.addOrReplaceChild(
         "LeftWing",
         CubeListBuilder.create().texOffs(0, 96).mirror().addBox(0.0F, -14.5F, 0.0F, 0.0F, 33.0F, 25.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offsetAndRotation(0.0F, -0.1744F, 2.7874F, 0.0F, 1.3963F, 0.0F)
      );
      left.addOrReplaceChild(
         "LeftWingEdge",
         CubeListBuilder.create().texOffs(0, 127).mirror().addBox(0.0F, -14.5F, 0.0F, 0.0F, 33.0F, 33.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offset(0.0F, 0.0F, 25.0F)
      );
      return LayerDefinition.create(mesh, 256, 256);
   }

   public void setupAnimation(float elapsed) {
      this.rightWing.resetPose();
      this.rightEdge.resetPose();
      this.leftWing.resetPose();
      this.leftEdge.resetPose();
      if (elapsed < 12.0F) {
         float open = smooth(elapsed / 8.0F);
         float flap = Mth.sin(elapsed * 0.72F) * 0.24F;
         this.applyMirrored(-1.3963F - open * 0.95F + flap, 1.3963F + open * 0.95F - flap, -open * 1.95F, open * 1.95F, -open * 0.42F, open * 0.42F);
         this.rightEdge.yRot = -open * 1.96F;
         this.leftEdge.yRot = open * 1.96F;
      } else if (elapsed < 26.0F) {
         float flap = Mth.sin((elapsed - 12.0F) * 0.55F) * 0.28F;
         this.applyMirrored(-2.15F + flap, 2.15F - flap, -0.35F, 0.35F, -0.18F, 0.18F);
         this.rightEdge.yRot = -1.35F - flap * 0.5F;
         this.leftEdge.yRot = 1.35F + flap * 0.5F;
      } else if (elapsed < 42.0F) {
         float fold = smooth((elapsed - 26.0F) / 8.0F);
         this.rightWing.xRot = Mth.lerp(fold, 0.0F, -2.1523F);
         this.leftWing.xRot = Mth.lerp(fold, 0.0F, -2.1523F);
         this.rightWing.yRot = Mth.lerp(fold, -2.15F, -0.1654F);
         this.leftWing.yRot = Mth.lerp(fold, 2.15F, 0.1654F);
         this.rightWing.zRot = Mth.lerp(fold, -0.18F, 1.9715F);
         this.leftWing.zRot = Mth.lerp(fold, 0.18F, -1.9715F);
         this.rightEdge.yRot = -1.309F;
         this.leftEdge.yRot = 1.309F;
      } else if (elapsed < 58.0F) {
         float burst = 1.0F - smooth((elapsed - 42.0F) / 16.0F);
         this.applyMirrored(-1.3963F - burst * 1.05F, 1.3963F + burst * 1.05F, -burst * 1.2F, burst * 1.2F, -burst * 0.35F, burst * 0.35F);
         this.rightEdge.yRot = -burst * 1.57F;
         this.leftEdge.yRot = burst * 1.57F;
      }
   }

   public void setupRedSoulAnimation(float ageInTicks, RedSoulWingTransform.WingState state, float activationProgress) {
      this.rightWing.resetPose();
      this.rightEdge.resetPose();
      this.leftWing.resetPose();
      this.leftEdge.resetPose();
      float open = smooth(activationProgress);
      RedSoulWingTransform.WingPose pose = RedSoulWingTransform.pose(state);
      boolean fast = state == RedSoulWingTransform.WingState.SPRINT || state == RedSoulWingTransform.WingState.AIR;
      boolean casting = state == RedSoulWingTransform.WingState.CAST;
      float flapSpeed = fast ? 0.34F : (casting ? 0.28F : 0.16F);
      float flapAmount = fast ? 0.24F : (casting ? 0.2F : 0.11F);
      float flap = Mth.sin(ageInTicks * flapSpeed) * flapAmount;
      float yOpen = (pose.y() + flap) * open;
      float zOpen = pose.z() * open;
      float xOpen = pose.x() * open;
      this.applyMirrored(yOpen, -yOpen, zOpen, -zOpen, xOpen, -xOpen);
      this.rightEdge.yRot = (pose.edgeY() - flap * 0.3F) * open;
      this.leftEdge.yRot = -this.rightEdge.yRot;
   }

   private void applyMirrored(float rightY, float leftY, float rightZ, float leftZ, float rightX, float leftX) {
      this.rightWing.yRot = rightY;
      this.leftWing.yRot = leftY;
      this.rightWing.zRot = rightZ;
      this.leftWing.zRot = leftZ;
      this.rightWing.xRot = rightX;
      this.leftWing.xRot = leftX;
   }

   private static float smooth(float value) {
      float clamped = Mth.clamp(value, 0.0F, 1.0F);
      return clamped * clamped * (3.0F - 2.0F * clamped);
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
      this.rightWing.render(poseStack, buffer, packedLight, packedOverlay, color);
      this.leftWing.render(poseStack, buffer, packedLight, packedOverlay, color);
   }
}
