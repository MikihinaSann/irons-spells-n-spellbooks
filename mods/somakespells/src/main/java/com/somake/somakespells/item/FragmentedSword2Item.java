package com.somake.somakespells.item;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Item.Properties;

public class FragmentedSword2Item extends AbstractFragmentedSwordItem {
   public FragmentedSword2Item(Properties properties) {
      super(
         Tiers.IRON,
         15,
         -3.0F,
         properties,
         AttributeRegistry.BLOOD_SPELL_POWER,
         ResourceLocation.fromNamespaceAndPath("somakespells", "fragmented_sword_2_modifier"),
         0.2,
         Operation.ADD_MULTIPLIED_BASE,
         3
      );
   }
}
