package com.gametechbc.traveloptics.item.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;

public class CelestialSpellweaveIngotItem extends Item {
   public CelestialSpellweaveIngotItem() {
      super(new Properties().stacksTo(64).fireResistant().rarity(Rarity.RARE));
   }
}
