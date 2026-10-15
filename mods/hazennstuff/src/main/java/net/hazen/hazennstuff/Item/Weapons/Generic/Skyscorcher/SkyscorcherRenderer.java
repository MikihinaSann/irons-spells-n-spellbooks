package net.hazen.hazennstuff.Item.Weapons.Generic.Skyscorcher;

import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class SkyscorcherRenderer extends GeoItemRenderer<SkyscorcherItem> {
   public SkyscorcherRenderer() {
      super(new SkyscorcherModel());
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }
}
