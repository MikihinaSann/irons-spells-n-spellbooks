package com.chadate.spellelemental.event;

import com.chadate.spellelemental.integration.jei.data.SwordOilConfigLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber({Dist.CLIENT})
public class SwordOilTooltipHandler {
   @SubscribeEvent
   public static void onItemTooltip(ItemTooltipEvent event) {
      ItemStack stack = event.getItemStack();
      SwordOilConfigLoader.SwordOilConfig config = SwordOilConfigLoader.getSwordOilConfig(stack.getItem());
      if (config != null) {
         event.getToolTip().add(Component.empty());
         event.getToolTip()
            .add(
               Component.literal("◆ ")
                  .withStyle(ChatFormatting.GOLD)
                  .append(
                     Component.translatable("tooltip.spellelemental.sword_oil.title").withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD})
                  )
            );
         Component elementIcon = Component.literal(getElementIcon(config.getElement()) + " ").withStyle(ChatFormatting.WHITE);
         Component elementName = Component.translatable("element.spellelemental." + config.getElement())
            .withStyle(new ChatFormatting[]{ChatFormatting.YELLOW, ChatFormatting.BOLD});
         event.getToolTip()
            .add(
               Component.literal("  ")
                  .append(elementIcon)
                  .append(Component.translatable("tooltip.spellelemental.sword_oil.element", new Object[]{elementName}).withStyle(ChatFormatting.GRAY))
            );
         Component amountIcon = Component.literal("◆ ").withStyle(ChatFormatting.WHITE);
         Component amountValue = Component.literal(String.valueOf(config.getAmount()))
            .withStyle(new ChatFormatting[]{ChatFormatting.YELLOW, ChatFormatting.BOLD});
         event.getToolTip()
            .add(
               Component.literal("  ")
                  .append(amountIcon)
                  .append(Component.translatable("tooltip.spellelemental.sword_oil.amount", new Object[]{amountValue}).withStyle(ChatFormatting.GRAY))
            );
      }
   }

   private static String getElementIcon(String element) {
      byte var2 = -1;
      element.hashCode();
      switch (var2) {
         default:
            return "◆";
      }
   }
}
