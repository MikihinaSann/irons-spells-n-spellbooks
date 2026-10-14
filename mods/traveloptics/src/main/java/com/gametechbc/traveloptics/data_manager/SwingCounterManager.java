package com.gametechbc.traveloptics.data_manager;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class SwingCounterManager {
   private static final String SWING_COUNTER_TAG = "TOSwingCounter";

   public SwingCounterManager() {
      super();
   }

   public static int getSwingCount(ItemStack stack) {
      return stack.has(DataComponents.CUSTOM_DATA) ? ((CustomData)stack.get(DataComponents.CUSTOM_DATA)).copyTag().getInt("TOSwingCounter") : 1;
   }

   public static void setSwingCount(ItemStack stack, int count) {
      CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt("TOSwingCounter", count));
   }

   public static void incrementSwingCount(ItemStack stack, int maxSwings) {
      int current = getSwingCount(stack);
      setSwingCount(stack, current >= maxSwings ? 1 : current + 1);
   }
}
