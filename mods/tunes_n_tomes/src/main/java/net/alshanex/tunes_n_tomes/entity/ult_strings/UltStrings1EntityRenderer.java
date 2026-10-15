package net.alshanex.tunes_n_tomes.entity.ult_strings;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class UltStrings1EntityRenderer extends GeoEntityRenderer<UltStrings1Entity> {
   public UltStrings1EntityRenderer(Context renderManager) {
      super(renderManager, new UltStrings1EntityModel());
      this.shadowRadius = 0.0F;
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }
}
