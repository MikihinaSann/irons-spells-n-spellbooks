package com.userofbricks.expanded_combat.item;

import com.userofbricks.expanded_combat.datagen.LangStrings;
import com.userofbricks.expanded_combat.init.DataAttachments;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SolidPureFoodItem extends Item {
   public SolidPureFoodItem(Properties p_41383_) {
      super(p_41383_);
   }

   public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
      ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
      FoodProperties foodproperties = itemstack.getFoodProperties(pPlayer);
      if (foodproperties == null || (Integer)pPlayer.getData(DataAttachments.STOLEN_HEALTH) <= 2) {
         return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
      } else if (pPlayer.canEat(foodproperties.canAlwaysEat())) {
         pPlayer.startUsingItem(pUsedHand);
         return InteractionResultHolder.consume(itemstack);
      } else {
         return InteractionResultHolder.fail(itemstack);
      }
   }

   public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
      if ((Integer)livingEntity.getData(DataAttachments.STOLEN_HEALTH) > 2) {
         livingEntity.setData(DataAttachments.STOLEN_HEALTH, (Integer)livingEntity.getData(DataAttachments.STOLEN_HEALTH) - 2);
         livingEntity.setData(DataAttachments.ADDED_HEALTH, (Integer)livingEntity.getData(DataAttachments.ADDED_HEALTH) + 1);
      }

      return super.finishUsingItem(stack, level, livingEntity);
   }

   public void appendHoverText(ItemStack stack, TooltipContext pContext, List<Component> componentList, TooltipFlag tooltipFlag) {
      componentList.add(Component.translatable(LangStrings.EDIBLE).withStyle(ChatFormatting.GRAY));
      if (tooltipFlag.isAdvanced()) {
         componentList.add(Component.translatable(LangStrings.CONSUMES_CURSES_LANG).withStyle(ChatFormatting.AQUA));
      }

      super.appendHoverText(stack, pContext, componentList, tooltipFlag);
   }
}
