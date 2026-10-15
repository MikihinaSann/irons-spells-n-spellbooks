package net.alshanex.alshanex_familiars.item.curios;

import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;

public class DragonWarriorTrinket extends CurioBaseItem {
   public DragonWarriorTrinket() {
      super(ItemPropertiesHelper.equipment().stacksTo(1).rarity(Rarity.EPIC));
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      tooltipComponents.add(Component.translatable("tooltip.alshanex_familiars.dragon_warrior_trinket").withStyle(ChatFormatting.WHITE));
   }
}
