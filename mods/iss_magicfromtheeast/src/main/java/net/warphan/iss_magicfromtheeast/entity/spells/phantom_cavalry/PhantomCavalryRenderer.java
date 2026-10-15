package net.warphan.iss_magicfromtheeast.entity.spells.phantom_cavalry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PhantomCavalryRenderer extends GeoEntityRenderer<PhantomCavalryVisualEntity> {
   public static final ResourceLocation textureLocation = new ResourceLocation("iss_magicfromtheeast", "textures/entity/phantom_cavalry.png");

   public PhantomCavalryRenderer(Context renderManager) {
      super(renderManager, new PhantomCavalryModel());
      this.shadowRadius = 0.0F;
   }

   public ResourceLocation getTextureLocation(PhantomCavalryVisualEntity animatable) {
      return textureLocation;
   }

   public void preRender(
      PoseStack poseStack,
      PhantomCavalryVisualEntity animatable,
      BakedGeoModel model,
      @Nullable MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      Vec3 motion = animatable.getDeltaMovement();
      float xRot = -((float)(Mth.atan2(motion.horizontalDistance(), motion.y) * 180.0F / (float)Math.PI) - 90.0F);
      float yRot = -((float)(Mth.atan2(motion.z, motion.x) * 180.0F / (float)Math.PI) - 90.0F);
      poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
      poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
      super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
   }
}
