package com.somake.somakespells.item;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;

public class RockSwordItem extends SwordItem {
   public RockSwordItem(Properties properties) {
      super(Tiers.IRON, properties);
   }

   public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
      Builder builder = ItemAttributeModifiers.builder();
      ResourceLocation damageId = ResourceLocation.withDefaultNamespace("base_attack_damage");
      ResourceLocation speedId = ResourceLocation.withDefaultNamespace("base_attack_speed");
      ResourceLocation modifierId = ResourceLocation.fromNamespaceAndPath("somakespells", "rock_sword_modifier");
      builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(damageId, 14.0, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
      builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(speedId, -3.5, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
      Optional<Reference<Attribute>> geoAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.parse("gtbcs_geomancy_plus:geo_spell_power"));
      if (geoAttribute.isPresent()) {
         builder.add((Holder)geoAttribute.get(), new AttributeModifier(modifierId, 0.15, Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.MAINHAND);
      }

      return builder.build();
   }
}
