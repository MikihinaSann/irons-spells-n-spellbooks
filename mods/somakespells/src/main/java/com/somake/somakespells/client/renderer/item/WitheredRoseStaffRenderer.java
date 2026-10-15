package com.somake.somakespells.client.renderer.item;

import com.somake.somakespells.client.model.WitheredRoseStaffGeoModel;
import com.somake.somakespells.item.WitheredRoseStaffItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class WitheredRoseStaffRenderer extends GeoItemRenderer<WitheredRoseStaffItem> {
   public WitheredRoseStaffRenderer() {
      super(new WitheredRoseStaffGeoModel());
      this.useAlternateGuiLighting();
   }
}
