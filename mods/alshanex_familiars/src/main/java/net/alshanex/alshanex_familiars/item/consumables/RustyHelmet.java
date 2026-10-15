package net.alshanex.alshanex_familiars.item.consumables;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class RustyHelmet extends Item {
   public RustyHelmet() {
      super(new Properties().stacksTo(16));
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      tooltipComponents.add(Component.literal("Unusable").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC));
   }
}
