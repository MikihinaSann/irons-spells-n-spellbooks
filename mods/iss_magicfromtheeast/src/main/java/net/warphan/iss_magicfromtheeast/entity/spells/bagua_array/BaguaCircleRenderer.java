package net.warphan.iss_magicfromtheeast.entity.spells.bagua_array;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BaguaCircleRenderer extends GeoEntityRenderer<BaguaCircle> {
   public static final ResourceLocation textureLocation = new ResourceLocation("iss_magicfromtheeast", "textures/entity/bagua_circle.png");

   public BaguaCircleRenderer(Context renderManager) {
      super(renderManager, new BaguaCircleModel());
      this.shadowRadius = 0.0F;
   }

   public ResourceLocation getTextureLocation(BaguaCircle animatable) {
      return textureLocation;
   }

   public RenderType getRenderType(BaguaCircle animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
      return RenderType.energySwirl(texture, 0.0F, 0.0F);
   }
}
