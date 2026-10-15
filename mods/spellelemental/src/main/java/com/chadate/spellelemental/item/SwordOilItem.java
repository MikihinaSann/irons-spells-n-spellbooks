package com.chadate.spellelemental.item;

import com.chadate.spellelemental.integration.jei.data.SwordOilConfigLoader;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class SwordOilItem extends Item {
   public SwordOilItem(Properties properties) {
      super(properties);
   }

   public String getElementType() {
      SwordOilConfigLoader.SwordOilConfig config = SwordOilConfigLoader.getSwordOilConfig(this);
      return config != null ? config.getElement() : "";
   }

   public int getElementAmount() {
      SwordOilConfigLoader.SwordOilConfig config = SwordOilConfigLoader.getSwordOilConfig(this);
      return config != null ? config.getAmount() : 0;
   }

   public void appendHoverText(
      @Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag
   ) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
   }

   public boolean isFoil(@Nonnull ItemStack stack) {
      return true;
   }
}
