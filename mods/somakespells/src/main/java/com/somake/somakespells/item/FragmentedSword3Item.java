package com.somake.somakespells.item;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Item.Properties;

public class FragmentedSword3Item extends AbstractFragmentedSwordItem {
   public FragmentedSword3Item(Properties properties) {
      super(
         Tiers.IRON,
         17,
         -3.0F,
         properties,
         AttributeRegistry.BLOOD_SPELL_POWER,
         ResourceLocation.fromNamespaceAndPath("somakespells", "fragmented_sword_3_modifier"),
         0.25,
         Operation.ADD_MULTIPLIED_BASE,
         5
      );
   }
}
