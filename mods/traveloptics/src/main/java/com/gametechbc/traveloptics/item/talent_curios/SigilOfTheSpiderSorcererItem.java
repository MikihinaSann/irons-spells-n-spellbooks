package com.gametechbc.traveloptics.item.talent_curios;

import com.gametechbc.traveloptics.api.compat.Curios;
import io.redspace.ironsspellbooks.item.curios.SimpleDescriptiveCurio;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;

public class SigilOfTheSpiderSorcererItem extends SimpleDescriptiveCurio {
   public SigilOfTheSpiderSorcererItem() {
      super(new Properties().stacksTo(1).rarity(Rarity.EPIC), Curios.NECKLACE_SLOT);
   }

   public List<Component> getDescriptionLines(ItemStack stack) {
      return List.of(
         Component.translatable("item.traveloptics.sigil_of_the_spider_sorcerer.tooltip.desc1").withStyle(ChatFormatting.BLUE),
         Component.translatable("item.traveloptics.sigil_of_the_spider_sorcerer.tooltip.desc2").withStyle(ChatFormatting.BLUE)
      );
   }
}
