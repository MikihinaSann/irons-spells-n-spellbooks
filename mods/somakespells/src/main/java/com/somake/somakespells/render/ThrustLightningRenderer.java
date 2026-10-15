package com.somake.somakespells.render;

import com.somake.somakespells.entity.ThrustLightningEntity;
import com.somake.somakespells.entity.model.ThrustLightningModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ThrustLightningRenderer extends GeoEntityRenderer<ThrustLightningEntity> {
   public ThrustLightningRenderer(Context renderManager) {
      super(renderManager, new ThrustLightningModel());
      this.shadowRadius = 0.0F;
   }
}
