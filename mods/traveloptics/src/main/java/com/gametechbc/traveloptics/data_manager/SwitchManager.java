package com.gametechbc.traveloptics.data_manager;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class SwitchManager {
   private static final String SWITCHES_TAG = "TOSwitchStates";

   public SwitchManager() {
      super();
   }

   public static boolean isEnabled(ItemStack stack, String switchKey) {
      if (stack.has(DataComponents.CUSTOM_DATA)) {
         CompoundTag switches = ((CustomData)stack.get(DataComponents.CUSTOM_DATA)).copyTag().getCompound("TOSwitchStates");
         return switches.getBoolean(switchKey);
      } else {
         return false;
      }
   }

   public static void setSwitch(ItemStack stack, String switchKey, boolean state) {
      CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
         CompoundTag switches = tag.getCompound("TOSwitchStates");
         switches.putBoolean(switchKey, state);
         tag.put("TOSwitchStates", switches);
      });
   }

   public static void toggleSwitch(ItemStack stack, String switchKey) {
      setSwitch(stack, switchKey, !isEnabled(stack, switchKey));
   }
}
