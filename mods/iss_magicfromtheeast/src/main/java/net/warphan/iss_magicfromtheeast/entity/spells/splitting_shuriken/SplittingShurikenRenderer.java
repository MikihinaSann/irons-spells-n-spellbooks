package net.warphan.iss_magicfromtheeast.entity.spells.splitting_shuriken;

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
import net.warphan.iss_magicfromtheeast.ISS_MagicFromTheEast;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SplittingShurikenRenderer extends GeoEntityRenderer<SplittingShurikenProjectile> {
   private static final ResourceLocation TEXTURE = ISS_MagicFromTheEast.id("textures/entity/spirit_shuriken.png");

   public SplittingShurikenRenderer(Context context) {
      super(context, new SplittingShurikenModel());
      this.shadowRadius = 0.0F;
   }

   public void preRender(
      PoseStack poseStack,
      SplittingShurikenProjectile animatable,
      BakedGeoModel model,
      @Nullable MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      if (animatable.isPrimary()) {
         poseStack.scale(0.5F, 0.5F, 0.5F);
      } else {
         poseStack.scale(0.3F, 0.3F, 0.3F);
      }

      Vec3 motion = animatable.getDeltaMovement();
      float xRot = -((float)(Mth.atan2(motion.horizontalDistance(), motion.y) * 180.0F / (float)Math.PI) - 90.0F);
      float yRot = -((float)(Mth.atan2(motion.z, motion.x) * 180.0F / (float)Math.PI) - 90.0F);
      poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
      poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
      super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
   }

   public RenderType getRenderType(
      SplittingShurikenProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick
   ) {
      return RenderType.energySwirl(texture, 0.0F, 0.0F);
   }

   public ResourceLocation getTextureLocation(SplittingShurikenProjectile entity) {
      return TEXTURE;
   }
}
