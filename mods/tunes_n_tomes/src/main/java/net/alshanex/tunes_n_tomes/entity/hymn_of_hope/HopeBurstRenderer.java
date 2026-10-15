package net.alshanex.tunes_n_tomes.entity.hymn_of_hope;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class HopeBurstRenderer extends GeoEntityRenderer<HopeBurstEntity> {
   public HopeBurstRenderer(Context renderManager) {
      super(renderManager, new HopeBurstModel());
      this.shadowRadius = 0.0F;
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }
}
