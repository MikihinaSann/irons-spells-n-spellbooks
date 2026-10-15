package net.hazen.hazennstuff.Item.Weapons.Ascended.DraconicSplitter;

import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class DraconicSplitterRenderer extends GeoItemRenderer<DraconicSplitterItem> {
   public DraconicSplitterRenderer() {
      super(new DraconicSplitterModel());
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }
}
