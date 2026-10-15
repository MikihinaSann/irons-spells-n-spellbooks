package com.somake.somakespells.item;

import com.somake.somakespells.util.StaffHandCompat;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;

public class DormantGrimoireItem extends AbstractAnimatedGrimoireItem {
   public DormantGrimoireItem(Properties properties) {
      super(properties);
   }

   @Override
   public ResourceLocation getGrimoireTexture() {
      return ResourceLocation.fromNamespaceAndPath("somakespells", "textures/item/staffs/dormant_grimoire.png");
   }

   public Component getName(ItemStack stack) {
      return super.getName(stack).copy().withStyle(Style.EMPTY.withColor(DormantGrimoireProgressHelper.dormantTitleColor()));
   }

   public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath("somakespells", "dormant_grimoire_modifier");
      Builder builder = StaffHandCompat.createStaffAttributeBuilder(super.getDefaultAttributeModifiers(stack), id);
      EquipmentSlotGroup slotGroup = StaffHandCompat.getConfiguredStaffSlot();
      builder.add(AttributeRegistry.CAST_TIME_REDUCTION, new AttributeModifier(id, 0.1, Operation.ADD_MULTIPLIED_BASE), slotGroup);
      builder.add(AttributeRegistry.COOLDOWN_REDUCTION, new AttributeModifier(id, 0.1, Operation.ADD_MULTIPLIED_BASE), slotGroup);
      builder.add(AttributeRegistry.SPELL_POWER, new AttributeModifier(id, 0.05, Operation.ADD_MULTIPLIED_BASE), slotGroup);
      return builder.build();
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      DormantGrimoireProgressHelper.appendTooltip(stack, tooltipComponents);
   }
}
