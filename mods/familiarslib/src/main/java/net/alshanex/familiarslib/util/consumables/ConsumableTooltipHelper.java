package net.alshanex.familiarslib.util.consumables;

import java.util.List;
import net.alshanex.familiarslib.registry.ComponentRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class ConsumableTooltipHelper {
   public static void addConsumableTooltip(ItemStack stack, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      FamiliarConsumableComponent consumableComponent = (FamiliarConsumableComponent)stack.get(ComponentRegistry.FAMILIAR_CONSUMABLE.get());
      if (consumableComponent != null) {
         String typeKey = getTypeTranslationKey(consumableComponent.type());
         int bonus = consumableComponent.getBonus();
         int limit = consumableComponent.getLimit();
         String unit = getUnitSuffix(consumableComponent.type());
         tooltipComponents.add(
            Component.translatable("item.familiarslib.consumable.tooltip.type", new Object[]{Component.translatable(typeKey)}).withStyle(ChatFormatting.GOLD)
         );
         tooltipComponents.add(Component.translatable("item.familiarslib.consumable.tooltip.bonus", new Object[]{bonus + unit}).withStyle(ChatFormatting.GREEN));
         tooltipComponents.add(Component.translatable("item.familiarslib.consumable.tooltip.limit", new Object[]{limit + unit}).withStyle(ChatFormatting.BLUE));
         tooltipComponents.add(
            Component.translatable("item.familiarslib.consumable.tooltip.tier", new Object[]{consumableComponent.tier()}).withStyle(ChatFormatting.YELLOW)
         );
      }

      FamiliarFoodComponent foodComponent = (FamiliarFoodComponent)stack.get(ComponentRegistry.FAMILIAR_FOOD.get());
      if (foodComponent != null) {
         int healingValue = foodComponent.healing();
         tooltipComponents.add(
            Component.translatable("item.familiarslib.consumable.tooltip.food", new Object[]{healingValue + " hearts"}).withStyle(ChatFormatting.RED)
         );
      }
   }

   private static String getTypeTranslationKey(FamiliarConsumableSystem.ConsumableType type) {
      return switch (type) {
         case ARMOR -> "consumable.type.armor";
         case HEALTH -> "consumable.type.health";
         case SPELL_POWER -> "consumable.type.spell_power";
         case SPELL_RESIST -> "consumable.type.spell_resist";
         case ENRAGED -> "consumable.type.enraged";
         case BLOCKING -> "consumable.type.blocking";
      };
   }

   private static String getUnitSuffix(FamiliarConsumableSystem.ConsumableType type) {
      return switch (type) {
         case ARMOR -> "";
         case HEALTH -> "%";
         case SPELL_POWER -> "%";
         case SPELL_RESIST -> "%";
         case ENRAGED -> " stacks";
         case BLOCKING -> "";
      };
   }
}
