package com.somake.somakespells.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class RedSoulLanternItem extends Item {
   public RedSoulLanternItem(Properties properties) {
      super(properties);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      List<String> spells = RedSoulLanternProgressHelper.getStoredSpells(stack);
      tooltipComponents.add(Component.empty());
      tooltipComponents.add(
         Component.translatable("tooltip.somakespells.red_soul_lantern.progress", new Object[]{spells.size(), 3})
            .withStyle(spells.size() >= 3 ? ChatFormatting.DARK_RED : ChatFormatting.AQUA)
      );
      if (Screen.hasShiftDown()) {
         tooltipComponents.add(Component.translatable("tooltip.somakespells.red_soul_lantern.instruction").withStyle(ChatFormatting.GRAY));

         for (String spellId : spells) {
            tooltipComponents.add(
               Component.literal("- ")
                  .append(Component.translatable(RedSoulLanternProgressHelper.spellTranslationKey(spellId)))
                  .withStyle(ChatFormatting.DARK_AQUA)
            );
         }
      } else {
         tooltipComponents.add(Component.translatable("tooltip.somakespells.hold_shift"));
      }
   }

   public boolean isBarVisible(ItemStack stack) {
      return true;
   }

   public int getBarWidth(ItemStack stack) {
      float remaining = 1.0F - (float)stack.getDamageValue() / stack.getMaxDamage();
      return Math.max(1, Math.round(13.0F * remaining));
   }

   public int getBarColor(ItemStack stack) {
      return 12785694;
   }
}
