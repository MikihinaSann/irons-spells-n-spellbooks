package net.hazen.hazennstuff.Item.Weapons.Generic.TrueNightsEdge;

import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class TrueNightsEdgeRenderer extends GeoItemRenderer<TrueNightsEdgeItem> {
   public TrueNightsEdgeRenderer() {
      super(new TrueNightsEdgeModel());
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }
}
