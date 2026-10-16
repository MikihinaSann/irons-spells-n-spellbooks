package com.userofbricks.expanded_combat.item;

import com.userofbricks.expanded_combat.api.material.Material;
import com.userofbricks.expanded_combat.datagen.LangStrings;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.jetbrains.annotations.NotNull;

public interface IMaterialItem extends IMendingBonusItem, IItemExtension {
   @Override
   default float getMendingBonus() {
      return this.getMaterial().enchanting().mendingBonus();
   }

   default float getXpRepairRatio(@NotNull ItemStack stack) {
      return 2.0F + this.getMendingBonus();
   }

   default int getEnchantmentValue(@NotNull ItemStack stack) {
      return this.getMaterial().enchanting().offenseEnchantability();
   }

   default boolean isValidRepairItem(@NotNull ItemStack toRepair, @NotNull ItemStack repair) {
      return (this.getMaterial().repairItem() != null ? this.getMaterial().repairItem().get() : Ingredient.EMPTY).test(repair);
   }

   default boolean isRepairable(@NotNull ItemStack stack) {
      return stack.getItem().isDamageable(stack);
   }

   Material getMaterial();

   default boolean makesPiglinsNeutral(@NotNull ItemStack stack, @NotNull LivingEntity wearer) {
      IMaterialItem materialItem = (IMaterialItem)stack.getItem();
      return materialItem.getMaterial().defense().makesPiglinsNeutral();
   }

   @OnlyIn(Dist.CLIENT)
   default void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag tooltipFlag) {
      if (this.getMendingBonus() != 0.0F && stack.getItem().isDamageable(stack)) {
         if (this.getMendingBonus() > 0.0F) {
            list.add(
               1,
               Component.translatable(LangStrings.GOLD_MENDING_TOOLTIP)
                  .withStyle(ChatFormatting.BLUE)
                  .append(Component.literal(ChatFormatting.BLUE + " " + ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(this.getMendingBonus())))
            );
         } else if (this.getMendingBonus() < 0.0F) {
            list.add(
               1,
               Component.translatable(LangStrings.GOLD_MENDING_TOOLTIP)
                  .withStyle(ChatFormatting.RED)
                  .append(Component.literal(ChatFormatting.RED + " " + ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(this.getMendingBonus())))
            );
         }
      }
   }
}
