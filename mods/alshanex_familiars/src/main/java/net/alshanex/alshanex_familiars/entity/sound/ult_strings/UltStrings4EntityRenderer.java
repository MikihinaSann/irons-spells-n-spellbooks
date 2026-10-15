package net.alshanex.alshanex_familiars.entity.sound.ult_strings;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class UltStrings4EntityRenderer extends GeoEntityRenderer<UltStrings4Entity> {
   public UltStrings4EntityRenderer(Context renderManager) {
      super(renderManager, new UltStrings4EntityModel());
      this.shadowRadius = 0.0F;
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }
}
