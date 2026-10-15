package com.somake.somakespells.client.renderer.armor;

import com.somake.somakespells.client.model.CeraniumArmorModel;
import com.somake.somakespells.item.armor.CeraniumArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class CeraniumArmorRenderer extends GeoArmorRenderer<CeraniumArmorItem> {
   public CeraniumArmorRenderer() {
      super(new CeraniumArmorModel());
   }
}
