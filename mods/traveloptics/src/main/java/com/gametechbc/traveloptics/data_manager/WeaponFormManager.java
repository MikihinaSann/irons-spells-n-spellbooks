package com.gametechbc.traveloptics.data_manager;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class WeaponFormManager {
   private static final String FORM_NBT_TAG = "WeaponForm";

   public WeaponFormManager() {
      super();
   }

   public static int getForm(ItemStack stack) {
      return stack.has(DataComponents.CUSTOM_DATA) ? ((CustomData)stack.get(DataComponents.CUSTOM_DATA)).copyTag().getInt("WeaponForm") : 0;
   }

   public static void setForm(ItemStack stack, int form, int maxForms) {
      int cappedForm = Math.max(0, Math.min(form, maxForms - 1));
      CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt("WeaponForm", cappedForm));
   }

   public static void cycleForm(ItemStack stack, int maxForms) {
      int newForm = (getForm(stack) + 1) % maxForms;
      setForm(stack, newForm, maxForms);
   }
}
