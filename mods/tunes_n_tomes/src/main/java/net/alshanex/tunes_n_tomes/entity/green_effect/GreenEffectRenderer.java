package net.alshanex.tunes_n_tomes.entity.green_effect;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class GreenEffectRenderer extends GeoEntityRenderer<GreenEffectEntity> {
   public GreenEffectRenderer(Context renderManager) {
      super(renderManager, new GreenEffectModel());
      this.shadowRadius = 0.0F;
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }
}
