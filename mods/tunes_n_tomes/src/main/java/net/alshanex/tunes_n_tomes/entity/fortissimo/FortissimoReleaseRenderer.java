package net.alshanex.tunes_n_tomes.entity.fortissimo;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class FortissimoReleaseRenderer extends GeoEntityRenderer<FortissimoReleaseEntity> {
   public FortissimoReleaseRenderer(Context renderManager) {
      super(renderManager, new FortissimoReleaseModel());
      this.shadowRadius = 0.0F;
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }
}
