package net.warphan.iss_magicfromtheeast.entity.spells.jade_drape;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class JadeDrapesRenderer extends GeoEntityRenderer<JadeDrapesEntity> {
   public static final ResourceLocation textureLocation = new ResourceLocation("iss_magicfromtheeast", "textures/entity/jade_drape.png");

   public JadeDrapesRenderer(Context renderManager) {
      super(renderManager, new JadeDrapesModel());
      this.shadowRadius = 0.0F;
   }

   public ResourceLocation getTextureLocation(JadeDrapesEntity animatable) {
      return textureLocation;
   }

   public void preRender(
      PoseStack poseStack,
      JadeDrapesEntity animatable,
      BakedGeoModel model,
      @Nullable MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      Vec3 motion = animatable.getForward();
      float xRot = -((float)(Mth.atan2(motion.horizontalDistance(), motion.y) * 180.0F / (float)Math.PI) - 90.0F);
      float yRot = -((float)(Mth.atan2(motion.z, motion.x) * 180.0F / (float)Math.PI) + 90.0F);
      poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
      poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
      super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
   }

   public RenderType getRenderType(JadeDrapesEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
      return RenderType.energySwirl(texture, 0.0F, 0.0F);
   }
}
