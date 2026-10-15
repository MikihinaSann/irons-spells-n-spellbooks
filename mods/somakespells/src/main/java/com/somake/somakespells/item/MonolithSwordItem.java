package com.somake.somakespells.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Item.Properties;

public class MonolithSwordItem extends CompatAttributeSwordItem {
   public MonolithSwordItem(Properties properties) {
      super(
         Tiers.IRON,
         12,
         -2.5F,
         properties,
         ResourceLocation.fromNamespaceAndPath("gtbcs_geomancy_plus", "geo_spell_power"),
         ResourceLocation.fromNamespaceAndPath("somakespells", "monolith_sword_modifier"),
         0.15,
         Operation.ADD_MULTIPLIED_BASE
      );
   }
}
