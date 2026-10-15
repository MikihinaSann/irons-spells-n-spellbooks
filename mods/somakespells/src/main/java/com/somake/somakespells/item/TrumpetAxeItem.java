package com.somake.somakespells.item;

import com.somake.somakespells.compat.TunesCompat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Item.Properties;

public class TrumpetAxeItem extends CompatAttributeSwordItem {
   public TrumpetAxeItem(Properties properties) {
      super(
         Tiers.IRON,
         14,
         -3.0F,
         properties,
         TunesCompat.MELODY_SPELL_POWER_ID,
         ResourceLocation.fromNamespaceAndPath("somakespells", "trumpet_axe_modifier"),
         0.15,
         Operation.ADD_MULTIPLIED_BASE
      );
   }
}
