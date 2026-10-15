package com.somake.somakespells.client.renderer.armor;

import com.somake.somakespells.client.model.AquamancerArmorModel;
import com.somake.somakespells.item.armor.AquamancerArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class AquamancerArmorRenderer extends GeoArmorRenderer<AquamancerArmorItem> {
   public AquamancerArmorRenderer() {
      super(new AquamancerArmorModel());
   }
}
