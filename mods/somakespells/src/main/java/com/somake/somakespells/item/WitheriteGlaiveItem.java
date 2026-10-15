package com.somake.somakespells.item;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;

public class WitheriteGlaiveItem extends SwordItem {
   public WitheriteGlaiveItem(Properties properties) {
      super(Tiers.NETHERITE, properties);
   }

   public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
      Builder builder = ItemAttributeModifiers.builder();
      ResourceLocation damageId = ResourceLocation.withDefaultNamespace("base_attack_damage");
      ResourceLocation speedId = ResourceLocation.withDefaultNamespace("base_attack_speed");
      ResourceLocation modifierId = ResourceLocation.fromNamespaceAndPath("somakespells", "witherite_glaive_modifier");
      builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(damageId, 15.0, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
      builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(speedId, -2.5, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
      builder.add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(modifierId, 0.5, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
      builder.add(AttributeRegistry.LIGHTNING_SPELL_POWER, new AttributeModifier(modifierId, 0.25, Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.MAINHAND);
      return builder.build();
   }
}
