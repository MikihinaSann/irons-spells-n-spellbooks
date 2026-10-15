package com.somake.somakespells.render;

import com.somake.somakespells.entity.WaterSplashEntity;
import com.somake.somakespells.entity.model.WaterSplashModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WaterSplashRenderer extends GeoEntityRenderer<WaterSplashEntity> {
   public WaterSplashRenderer(Context renderManager) {
      super(renderManager, new WaterSplashModel());
   }
}
