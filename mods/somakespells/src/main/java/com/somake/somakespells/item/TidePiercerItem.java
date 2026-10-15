package com.somake.somakespells.item;

import com.somake.somakespells.registries.ModAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Item.Properties;

public class TidePiercerItem extends BaseAttributeSwordItem {
   public TidePiercerItem(Properties properties) {
      super(
         Tiers.IRON,
         8,
         -2.9F,
         properties,
         4.0,
         ModAttributes.AQUA_SPELL_POWER,
         ResourceLocation.fromNamespaceAndPath("somakespells", "tide_piercer_modifier"),
         0.15,
         Operation.ADD_MULTIPLIED_BASE
      );
   }
}
