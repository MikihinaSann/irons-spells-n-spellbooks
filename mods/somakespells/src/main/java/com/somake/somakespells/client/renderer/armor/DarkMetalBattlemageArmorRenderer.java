package com.somake.somakespells.client.renderer.armor;

import com.somake.somakespells.client.model.DarkMetalBattlemageArmorModel;
import com.somake.somakespells.item.armor.DarkMetalBattlemageArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class DarkMetalBattlemageArmorRenderer extends GeoArmorRenderer<DarkMetalBattlemageArmorItem> {
   public DarkMetalBattlemageArmorRenderer() {
      super(new DarkMetalBattlemageArmorModel());
   }
}
