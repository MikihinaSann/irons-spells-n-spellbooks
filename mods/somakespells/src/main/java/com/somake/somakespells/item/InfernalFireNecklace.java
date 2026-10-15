package com.somake.somakespells.item;

import com.somake.somakespells.config.Config;

public class InfernalFireNecklace extends ElementalChargeNecklaceItem {
   @Override
   public int getMaxCharges() {
      return (Integer)Config.INFERNAL_FIRE_NECKLACE_MAX_CHARGES.get();
   }

   @Override
   protected int getBarColorValue() {
      return 3721567;
   }
}
