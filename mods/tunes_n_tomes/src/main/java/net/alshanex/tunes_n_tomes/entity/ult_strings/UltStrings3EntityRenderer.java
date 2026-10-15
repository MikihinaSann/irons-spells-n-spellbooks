package net.alshanex.tunes_n_tomes.entity.ult_strings;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class UltStrings3EntityRenderer extends GeoEntityRenderer<UltStrings3Entity> {
   public UltStrings3EntityRenderer(Context renderManager) {
      super(renderManager, new UltStrings3EntityModel());
      this.shadowRadius = 0.0F;
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }
}
