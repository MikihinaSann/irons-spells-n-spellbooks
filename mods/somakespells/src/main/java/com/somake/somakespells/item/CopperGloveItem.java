package com.somake.somakespells.item;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Item.Properties;

public class CopperGloveItem extends BaseAttributeSwordItem {
   public CopperGloveItem(Properties properties) {
      super(
         Tiers.IRON,
         11,
         -2.6F,
         properties,
         AttributeRegistry.FIRE_SPELL_POWER,
         ResourceLocation.fromNamespaceAndPath("somakespells", "copper_glove_modifier"),
         0.15,
         Operation.ADD_MULTIPLIED_BASE
      );
   }
}
