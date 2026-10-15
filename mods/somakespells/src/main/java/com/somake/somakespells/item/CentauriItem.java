package com.somake.somakespells.item;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Item.Properties;

public class CentauriItem extends BaseAttributeSwordItem {
   public CentauriItem(Properties properties) {
      super(
         Tiers.IRON,
         11,
         -2.3F,
         properties,
         AttributeRegistry.BLOOD_SPELL_POWER,
         ResourceLocation.fromNamespaceAndPath("somakespells", "centauri_modifier"),
         0.15,
         Operation.ADD_MULTIPLIED_BASE
      );
   }
}
