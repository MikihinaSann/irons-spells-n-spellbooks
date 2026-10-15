package com.somake.somakespells.item;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Item.Properties;

public class TotemDaggerItem extends BaseAttributeSwordItem {
   public TotemDaggerItem(Properties properties) {
      super(
         Tiers.IRON,
         7,
         -1.5F,
         properties,
         2.0,
         AttributeRegistry.EVOCATION_SPELL_POWER,
         ResourceLocation.fromNamespaceAndPath("somakespells", "totem_dagger_modifier"),
         0.15,
         Operation.ADD_MULTIPLIED_BASE
      );
   }
}
