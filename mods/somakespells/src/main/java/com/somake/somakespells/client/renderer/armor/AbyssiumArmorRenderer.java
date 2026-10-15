package com.somake.somakespells.client.renderer.armor;

import com.somake.somakespells.client.model.AbyssiumArmorModel;
import com.somake.somakespells.item.armor.AbyssiumArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class AbyssiumArmorRenderer extends GeoArmorRenderer<AbyssiumArmorItem> {
   public AbyssiumArmorRenderer() {
      super(new AbyssiumArmorModel());
   }
}
