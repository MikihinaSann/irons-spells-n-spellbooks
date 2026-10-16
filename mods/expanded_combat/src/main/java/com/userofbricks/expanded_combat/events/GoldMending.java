package com.userofbricks.expanded_combat.events;

import com.userofbricks.expanded_combat.datagen.LangStrings;
import com.userofbricks.expanded_combat.init.ECTags;
import com.userofbricks.expanded_combat.item.IMendingBonusItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent.PickupXp;

@EventBusSubscriber(
   modid = "expanded_combat",
   bus = Bus.GAME
)
public class GoldMending {
   @SubscribeEvent
   public static void MendingBonus(PickupXp event) {
      Player player = event.getEntity();

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         ItemStack stack = player.getItemBySlot(slot);
         if (!stack.isEmpty()
            && stack.getEnchantmentLevel(
                  event.getEntity().level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.MENDING)
               )
               > 0
            && stack.isDamaged()
            && doesGoldMendingContainItem(stack)) {
            event.setCanceled(true);
            ExperienceOrb orb = event.getOrb();
            player.takeXpDelay = 2;
            player.take(orb, 1);
            int toRepair = Math.min(orb.value * 4, stack.getDamageValue());
            orb.value -= toRepair / 4;
            stack.setDamageValue(stack.getDamageValue() - toRepair);
            if (orb.value > 0) {
               player.giveExperiencePoints(orb.value);
            }

            orb.remove(RemovalReason.KILLED);
         }
      }
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public static void handleToolTip(ItemTooltipEvent event) {
      ItemStack itemStack = event.getItemStack();
      List<Component> list = event.getToolTip();
      if (doesGoldMendingContainItem(itemStack)) {
         list.add(
            1,
            Component.translatable(LangStrings.GOLD_MENDING_TOOLTIP)
               .withStyle(ChatFormatting.BLUE)
               .append(Component.literal(ChatFormatting.BLUE + " +" + ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(2L)))
         );
      }

      if (itemStack.getItem() instanceof IMendingBonusItem simpleMaterialItem && simpleMaterialItem.getMendingBonus() != 0.0F) {
         if (simpleMaterialItem.getMendingBonus() > 0.0F) {
            list.add(
               1,
               Component.translatable(LangStrings.GOLD_MENDING_TOOLTIP)
                  .withStyle(ChatFormatting.BLUE)
                  .append(
                     Component.literal(
                        ChatFormatting.BLUE + " +" + ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(simpleMaterialItem.getMendingBonus())
                     )
                  )
            );
         } else if (simpleMaterialItem.getMendingBonus() < 0.0F) {
            list.add(
               1,
               Component.translatable(LangStrings.GOLD_MENDING_TOOLTIP)
                  .withStyle(ChatFormatting.RED)
                  .append(
                     Component.literal(ChatFormatting.RED + " " + ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(simpleMaterialItem.getMendingBonus()))
                  )
            );
         }
      }
   }

   public static boolean doesGoldMendingContainItem(ItemStack itemStack) {
      return itemStack.is(ECTags.NON_EC_MENDABLE_GOLD);
   }
}
