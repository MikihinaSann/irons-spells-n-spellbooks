package com.somake.somakespells.render;

import com.somake.somakespells.entity.GeoGroundEntity;
import com.somake.somakespells.entity.model.GeoGroundModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GeoGroundRenderer extends GeoEntityRenderer<GeoGroundEntity> {
   public GeoGroundRenderer(Context renderManager) {
      super(renderManager, new GeoGroundModel());
   }
}
