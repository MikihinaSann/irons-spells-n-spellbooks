package com.somake.somakespells.item;

import com.somake.somakespells.util.StaffHandCompat;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;

public class IgnitiumStaffItem extends StaffItem {
   public IgnitiumStaffItem(Properties properties) {
      super(properties);
   }

   public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath("somakespells", "ignitium_staff_modifier");
      Builder builder = StaffHandCompat.createStaffAttributeBuilder(super.getDefaultAttributeModifiers(stack), id);
      EquipmentSlotGroup slotGroup = StaffHandCompat.getConfiguredStaffSlot();
      builder.add(AttributeRegistry.FIRE_SPELL_POWER, new AttributeModifier(id, 0.15, Operation.ADD_MULTIPLIED_BASE), slotGroup);
      builder.add(AttributeRegistry.COOLDOWN_REDUCTION, new AttributeModifier(id, 0.1, Operation.ADD_MULTIPLIED_BASE), slotGroup);
      builder.add(AttributeRegistry.SPELL_POWER, new AttributeModifier(id, 0.05, Operation.ADD_MULTIPLIED_BASE), slotGroup);
      return builder.build();
   }
}
