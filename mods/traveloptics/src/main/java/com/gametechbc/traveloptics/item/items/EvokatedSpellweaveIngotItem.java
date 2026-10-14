package com.gametechbc.traveloptics.item.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;

public class EvokatedSpellweaveIngotItem extends Item {
   public EvokatedSpellweaveIngotItem() {
      super(new Properties().stacksTo(64).rarity(Rarity.RARE));
   }
}
