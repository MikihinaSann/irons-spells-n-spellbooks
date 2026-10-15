package com.somake.somakespells.item;

import com.somake.somakespells.compat.TunesCompat;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
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

public class ClefSwordItem extends SwordItem {
   public ClefSwordItem(Properties properties) {
      super(Tiers.IRON, properties);
   }

   public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
      Builder builder = ItemAttributeModifiers.builder();
      ResourceLocation damageId = ResourceLocation.withDefaultNamespace("base_attack_damage");
      ResourceLocation speedId = ResourceLocation.withDefaultNamespace("base_attack_speed");
      ResourceLocation modifierId = ResourceLocation.fromNamespaceAndPath("somakespells", "clef_sword_modifier");
      builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(damageId, 12.0, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
      builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(speedId, -2.5, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
      Optional<Reference<Attribute>> soundAttribute = TunesCompat.getMelodySpellPowerAttribute();
      if (soundAttribute.isPresent()) {
         builder.add((Holder)soundAttribute.get(), new AttributeModifier(modifierId, 0.15, Operation.ADD_MULTIPLIED_BASE), EquipmentSlotGroup.MAINHAND);
      }

      return builder.build();
   }
}
