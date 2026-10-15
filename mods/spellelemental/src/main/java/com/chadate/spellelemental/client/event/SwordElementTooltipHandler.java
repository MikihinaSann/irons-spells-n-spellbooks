package com.chadate.spellelemental.client.event;

import com.chadate.spellelemental.config.ServerConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber({Dist.CLIENT})
public class SwordElementTooltipHandler {
   @SubscribeEvent
   public static void onItemTooltip(ItemTooltipEvent event) {
      ItemStack stack = event.getItemStack();
      if (stack.getItem() instanceof SwordItem) {
         CompoundTag customData = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
         CompoundTag elementData = customData.getCompound("ElementAttachment");
         if (elementData.contains("element") && elementData.contains("amount")) {
            String elementType = elementData.getString("element");
            int elementAmount = elementData.getInt("amount");
            int maxElementAmount = ServerConfig.getWeaponMaxElementAmount();
            event.getToolTip().add(Component.literal(""));
            Component elementName = Component.translatable("element.spellelemental." + elementType).withStyle(style -> style.withColor(16766720));
            event.getToolTip()
               .add(
                  Component.translatable("tooltip.spellelemental.sword.element_attachment", new Object[]{elementName})
                     .withStyle(style -> style.withColor(16777215))
               );
            Component amountValue = Component.literal(elementAmount + "/" + maxElementAmount).withStyle(style -> style.withColor(16777045));
            event.getToolTip()
               .add(
                  Component.translatable("tooltip.spellelemental.sword.element_amount", new Object[]{amountValue}).withStyle(style -> style.withColor(8947848))
               );
         }
      }
   }
}
