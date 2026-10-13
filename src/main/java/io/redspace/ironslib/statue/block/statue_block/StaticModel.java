package io.redspace.ironslib.statue.block.statue_block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironslib.statue.PlayerStatuePose;
import io.redspace.ironslib.statue.StatueData;
import io.redspace.ironslib.statue.block.statue_block.player.PlayerStatueBlockEntity;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.util.TriConsumer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.keyframe.BoneAnimation;
import software.bernie.geckolib.animation.keyframe.Keyframe;
import software.bernie.geckolib.animation.keyframe.KeyframeStack;
import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtil;

public class StaticModel extends GeoModel<StaticModel.Instance> {
   private final ResourceLocation model;
   private final ResourceLocation texture;
   private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath("irons_lib", "animations/statue_poses.animation.json");

   public StaticModel(String modid, String name) {
      this(
         ResourceLocation.fromNamespaceAndPath(modid, String.format("geo/statue/%s.geo.json", name)),
         ResourceLocation.fromNamespaceAndPath(modid, String.format("textures/entity/statue/%s.png", name))
      );
   }

   public StaticModel(ResourceLocation model, ResourceLocation texture) {
      this.model = model;
      this.texture = texture;
   }

   public void render(
      PoseStack poseStack, Function<ResourceLocation, RenderType> renderType, MultiBufferSource bufferSource, int packedLight, int packedOverlay
   ) {
      this.render(poseStack, renderType.apply(this.texture), bufferSource, packedLight, packedOverlay);
   }

   public void render(PoseStack poseStack, RenderType renderType, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
      VertexConsumer buffer = bufferSource.getBuffer(renderType);
      this.render(poseStack, buffer, packedLight, packedOverlay);
   }

   public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay) {
      BakedGeoModel model = this.getBakedModel(this.model);

      for (GeoBone group : model.topLevelBones()) {
         this.renderRecursively(poseStack, group, vertexConsumer, packedLight, packedOverlay);
      }
   }

   public void setupPose(PlayerStatueBlockEntity statueBlock) {
      StatueData data = statueBlock.getStatueData();
      if (data != null) {
         PlayerStatuePose pose = data.pose();
         boolean flipped = data.flipped();
         Animation animation = this.getAnimation(StaticModel.Instance.INSTANCE, pose.getSerializedName());
         if (animation != null) {
            Map<String, BoneAnimation> mappedPose = Arrays.stream(animation.boneAnimations())
               .collect(Collectors.toMap(BoneAnimation::boneName, Function.identity()));
            if (flipped) {
               BoneAnimation tmp = mappedPose.get("arm_l");
               mappedPose.put("arm_l", mappedPose.get("arm_r"));
               mappedPose.put("arm_r", tmp);
               tmp = mappedPose.get("leg_l");
               mappedPose.put("leg_l", mappedPose.get("leg_r"));
               mappedPose.put("leg_r", tmp);
            }

            mappedPose.forEach((bone, anim) -> this.getBone(bone).ifPresent(b -> this.applyAnimation(b, anim, flipped)));
         }
      }
   }

   public void flushPose() {
      for (GeoBone bone : this.getAnimationProcessor().getRegisteredBones()) {
         BoneSnapshot initial = bone.getInitialSnapshot();
         bone.updateRotation(initial.getRotX(), initial.getRotY(), initial.getRotZ());
         bone.updatePosition(initial.getOffsetX(), initial.getOffsetY(), initial.getOffsetZ());
      }
   }

   private void applyAnimation(GeoBone bone, BoneAnimation boneAnimation, boolean mirror) {
      if (boneAnimation != null) {
         KeyframeStack<Keyframe<MathValue>> rotationKeyFrames = boneAnimation.rotationKeyFrames();
         KeyframeStack<Keyframe<MathValue>> positionKeyFrames = boneAnimation.positionKeyFrames();
         this.applyValue(rotationKeyFrames, bone::updateRotation, false, mirror);
         this.applyValue(positionKeyFrames, bone::updatePosition, true, mirror);
      }
   }

   private void applyValue(KeyframeStack<Keyframe<MathValue>> keyframe, TriConsumer<Float, Float, Float> function, boolean position, boolean flipped) {
      float x = keyframe.xKeyframes().isEmpty() ? 0.0F : (float)((Keyframe)keyframe.xKeyframes().get(0)).startValue().get();
      float y = keyframe.yKeyframes().isEmpty() ? 0.0F : (float)((Keyframe)keyframe.yKeyframes().get(0)).startValue().get();
      float z = keyframe.zKeyframes().isEmpty() ? 0.0F : (float)((Keyframe)keyframe.zKeyframes().get(0)).startValue().get();
      if (flipped) {
         if (position) {
            x = -x;
         } else {
            y = -y;
            z = -z;
         }
      }

      function.accept(x, y, z);
   }

   protected void renderRecursively(PoseStack poseStack, GeoBone bone, VertexConsumer buffer, int packedLight, int packedOverlay) {
      poseStack.pushPose();
      RenderUtil.prepMatrixForBone(poseStack, bone);
      if (!bone.isHidden()) {
         this.renderBone(poseStack, bone, buffer, packedLight, packedOverlay);
      }

      if (!bone.isHidingChildren()) {
         for (GeoBone childBone : bone.getChildBones()) {
            this.renderRecursively(poseStack, childBone, buffer, packedLight, packedOverlay);
         }
      }

      poseStack.popPose();
   }

   protected void renderBone(PoseStack poseStack, GeoBone bone, VertexConsumer buffer, int packedLight, int packedOverlay) {
      for (GeoCube cube : bone.getCubes()) {
         poseStack.pushPose();
         this.renderCube(poseStack, cube, buffer, packedLight, packedOverlay);
         poseStack.popPose();
      }
   }

   protected void renderCube(PoseStack poseStack, GeoCube cube, VertexConsumer buffer, int packedLight, int packedOverlay) {
      RenderUtil.translateToPivotPoint(poseStack, cube);
      RenderUtil.rotateMatrixAroundCube(poseStack, cube);
      RenderUtil.translateAwayFromPivotPoint(poseStack, cube);
      Matrix3f normalisedPoseState = poseStack.last().normal();
      Matrix4f poseState = new Matrix4f(poseStack.last().pose());

      for (GeoQuad quad : cube.quads()) {
         if (quad != null) {
            Vector3f normal = normalisedPoseState.transform(new Vector3f(quad.normal()));
            RenderUtil.fixInvertedFlatCube(cube, normal);
            this.createVerticesOfQuad(quad, poseState, normal, buffer, packedLight, packedOverlay);
         }
      }
   }

   protected void createVerticesOfQuad(GeoQuad quad, Matrix4f poseState, Vector3f normal, VertexConsumer buffer, int packedLight, int packedOverlay) {
      for (GeoVertex vertex : quad.vertices()) {
         Vector3f position = vertex.position();
         Vector4f vector4f = poseState.transform(new Vector4f(position.x(), position.y(), position.z(), 1.0F));
         buffer.addVertex(
            vector4f.x(), vector4f.y(), vector4f.z(), -1, vertex.texU(), vertex.texV(), packedOverlay, packedLight, normal.x(), normal.y(), normal.z()
         );
      }
   }

   public ResourceLocation getModelResource() {
      return this.model;
   }

   public ResourceLocation getTextureResource() {
      return this.texture;
   }

   public ResourceLocation getModelResource(StaticModel.Instance animatable) {
      return this.model;
   }

   public ResourceLocation getTextureResource(StaticModel.Instance animatable) {
      return this.texture;
   }

   public ResourceLocation getAnimationResource(StaticModel.Instance animatable) {
      return ANIMATION;
   }

   public static class Instance implements GeoAnimatable {
      public static final StaticModel.Instance INSTANCE = new StaticModel.Instance();
      AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

      public void registerControllers(ControllerRegistrar controllers) {
      }

      public AnimatableInstanceCache getAnimatableInstanceCache() {
         return this.cache;
      }

      public double getTick(Object object) {
         return 0.0;
      }
   }
}
