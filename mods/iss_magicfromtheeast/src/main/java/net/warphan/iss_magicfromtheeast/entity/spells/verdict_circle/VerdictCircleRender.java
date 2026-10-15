package net.warphan.iss_magicfromtheeast.entity.spells.verdict_circle;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class VerdictCircleRender extends GeoEntityRenderer<VerdictCircle> {
   public static final ResourceLocation textureLocation = new ResourceLocation("iss_magicfromtheeast", "textures/entity/verdict_circle.png");

   public VerdictCircleRender(Context renderManager) {
      super(renderManager, new VerdictCircleModel());
      this.shadowRadius = 0.0F;
   }

   public ResourceLocation getTextureLocation(VerdictCircle animatable) {
      return textureLocation;
   }

   public RenderType getRenderType(VerdictCircle animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
      return RenderType.entityTranslucent(texture);
   }
}
