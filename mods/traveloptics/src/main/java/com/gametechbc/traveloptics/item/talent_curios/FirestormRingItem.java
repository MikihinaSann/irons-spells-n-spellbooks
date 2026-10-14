package com.gametechbc.traveloptics.item.talent_curios;

import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.item.curios.SimpleDescriptiveCurio;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;

public class FirestormRingItem extends SimpleDescriptiveCurio {
   public FirestormRingItem() {
      super(new Properties().stacksTo(1).rarity(Rarity.EPIC), Curios.RING_SLOT);
   }

   public List<Component> getDescriptionLines(ItemStack stack) {
      return List.of(this.getDescription(stack));
   }

   public Component getDescription(ItemStack stack) {
      return Component.translatable("item.traveloptics.firestorm_ring.tooltip.desc").withStyle(ChatFormatting.BLUE);
   }
}
