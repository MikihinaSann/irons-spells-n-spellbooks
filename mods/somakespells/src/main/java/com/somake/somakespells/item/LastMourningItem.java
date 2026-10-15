package com.somake.somakespells.item;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Item.Properties;

public class LastMourningItem extends BaseAttributeSwordItem {
   public LastMourningItem(Properties properties) {
      super(
         Tiers.IRON,
         11,
         -3.0F,
         properties,
         AttributeRegistry.EVOCATION_SPELL_POWER,
         ResourceLocation.fromNamespaceAndPath("somakespells", "last_mourning_modifier"),
         0.15,
         Operation.ADD_MULTIPLIED_BASE
      );
   }
}
