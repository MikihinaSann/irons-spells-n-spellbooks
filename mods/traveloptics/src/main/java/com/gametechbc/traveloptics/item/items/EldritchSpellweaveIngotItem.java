package com.gametechbc.traveloptics.item.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;

public class EldritchSpellweaveIngotItem extends Item {
   public EldritchSpellweaveIngotItem() {
      super(new Properties().stacksTo(64).fireResistant().rarity(Rarity.RARE));
   }
}
