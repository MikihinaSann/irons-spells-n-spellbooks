package com.somake.somakespells.client.renderer.item;

import com.somake.somakespells.client.model.BookStaffGeoModel;
import com.somake.somakespells.item.AbstractAnimatedGrimoireItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class BookStaffRenderer extends GeoItemRenderer<AbstractAnimatedGrimoireItem> {
   public BookStaffRenderer() {
      super(new BookStaffGeoModel());
      this.useAlternateGuiLighting();
   }
}
