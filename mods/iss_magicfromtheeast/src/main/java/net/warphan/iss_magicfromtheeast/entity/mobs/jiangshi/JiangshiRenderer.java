package net.warphan.iss_magicfromtheeast.entity.mobs.jiangshi;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class JiangshiRenderer extends GeoEntityRenderer<JiangshiEntity> {
   public static final ResourceLocation textureLocation = new ResourceLocation("iss_magicfromtheeast", "textures/entity/jiangshi.png");

   public JiangshiRenderer(Context renderManager) {
      super(renderManager, new JiangshiModel());
      this.shadowRadius = 0.5F;
   }

   public ResourceLocation getTextureLocation(JiangshiEntity animatable) {
      return textureLocation;
   }
}
