package com.somake.somakespells.item;

import com.somake.somakespells.util.StaffHandCompat;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;

public class SanctumGrimoireItem extends AbstractAnimatedGrimoireItem {
   public SanctumGrimoireItem(Properties properties) {
      super(properties);
   }

   @Override
   public ResourceLocation getGrimoireTexture() {
      return ResourceLocation.fromNamespaceAndPath("somakespells", "textures/item/staffs/sanctum_grimoire.png");
   }

   public Component getName(ItemStack stack) {
      return super.getName(stack).copy().withStyle(Style.EMPTY.withColor(DormantGrimoireProgressHelper.holyTitleColor()));
   }

   public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath("somakespells", "sanctum_grimoire_modifier");
      Builder builder = StaffHandCompat.createStaffAttributeBuilder(super.getDefaultAttributeModifiers(stack), id);
      EquipmentSlotGroup slotGroup = StaffHandCompat.getConfiguredStaffSlot();
      builder.add(AttributeRegistry.HOLY_SPELL_POWER, new AttributeModifier(id, 0.15, Operation.ADD_MULTIPLIED_BASE), slotGroup);
      builder.add(AttributeRegistry.COOLDOWN_REDUCTION, new AttributeModifier(id, 0.1, Operation.ADD_MULTIPLIED_BASE), slotGroup);
      builder.add(AttributeRegistry.SPELL_POWER, new AttributeModifier(id, 0.05, Operation.ADD_MULTIPLIED_BASE), slotGroup);
      return builder.build();
   }
}
