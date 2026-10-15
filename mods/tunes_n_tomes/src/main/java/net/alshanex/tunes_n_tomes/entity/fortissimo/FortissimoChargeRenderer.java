package net.alshanex.tunes_n_tomes.entity.fortissimo;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class FortissimoChargeRenderer extends GeoEntityRenderer<FortissimoChargeEntity> {
   public FortissimoChargeRenderer(Context renderManager) {
      super(renderManager, new FortissimoChargeModel());
      this.shadowRadius = 0.0F;
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }
}
