package com.somake.somakespells.item;

import com.somake.somakespells.registries.ModAttributes;
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

public class InfernalFireIgnitiumStaffItem extends StaffItem {
   public InfernalFireIgnitiumStaffItem(Properties properties) {
      super(properties);
   }

   public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath("somakespells", "infernal_fire_ignitium_staff_modifier");
      Builder builder = StaffHandCompat.createStaffAttributeBuilder(super.getDefaultAttributeModifiers(stack), id, 10.0, -2.4, 0.0);
      EquipmentSlotGroup slotGroup = StaffHandCompat.getConfiguredStaffSlot();
      builder.add(ModAttributes.INFERNAL_FIRE_SPELL_POWER, new AttributeModifier(id, 0.25, Operation.ADD_MULTIPLIED_BASE), slotGroup);
      builder.add(AttributeRegistry.COOLDOWN_REDUCTION, new AttributeModifier(id, 0.1, Operation.ADD_MULTIPLIED_BASE), slotGroup);
      builder.add(AttributeRegistry.SPELL_POWER, new AttributeModifier(id, 0.05, Operation.ADD_MULTIPLIED_BASE), slotGroup);
      return builder.build();
   }
}
