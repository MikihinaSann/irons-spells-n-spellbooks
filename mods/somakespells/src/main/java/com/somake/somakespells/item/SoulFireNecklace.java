package com.somake.somakespells.item;

import com.somake.somakespells.config.Config;

public class SoulFireNecklace extends ElementalChargeNecklaceItem {
   @Override
   public int getMaxCharges() {
      return (Integer)Config.SOUL_FIRE_NECKLACE_MAX_CHARGES.get();
   }

   @Override
   protected int getBarColorValue() {
      return 4160511;
   }
}
