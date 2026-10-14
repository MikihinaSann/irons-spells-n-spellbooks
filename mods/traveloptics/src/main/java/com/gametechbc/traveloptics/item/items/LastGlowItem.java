package com.gametechbc.traveloptics.item.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;

public class LastGlowItem extends Item {
   public LastGlowItem() {
      super(new Properties().stacksTo(64).rarity(Rarity.EPIC));
   }
}
