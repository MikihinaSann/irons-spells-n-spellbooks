package com.gametechbc.traveloptics.data_manager;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class CooldownManager {
   private static final String COOLDOWN_TAG = "TOCooldowns";

   public CooldownManager() {
      super();
   }

   public static int getCooldown(ItemStack stack, String abilityKey) {
      if (stack.has(DataComponents.CUSTOM_DATA)) {
         CompoundTag tag = ((CustomData)stack.get(DataComponents.CUSTOM_DATA)).copyTag();
         if (tag.contains("TOCooldowns")) {
            return tag.getCompound("TOCooldowns").getInt(abilityKey);
         }
      }

      return 0;
   }

   public static void setCooldown(ItemStack stack, String abilityKey, int cooldown, int maxCooldown) {
      CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
         CompoundTag cooldowns = tag.getCompound("TOCooldowns");
         cooldowns.putInt(abilityKey, Math.min(cooldown, maxCooldown));
         tag.put("TOCooldowns", cooldowns);
      });
   }

   public static void addCooldown(ItemStack stack, String abilityKey, int amount, int maxCooldown) {
      int newCooldown = Math.min(getCooldown(stack, abilityKey) + amount, maxCooldown);
      setCooldown(stack, abilityKey, newCooldown, maxCooldown);
   }

   public static void reduceCooldown(ItemStack stack, String abilityKey, int amount) {
      int newCooldown = Math.max(getCooldown(stack, abilityKey) - amount, 0);
      setCooldown(stack, abilityKey, newCooldown, Integer.MAX_VALUE);
   }

   public static boolean isCooldownActive(ItemStack stack, String abilityKey) {
      return getCooldown(stack, abilityKey) > 0;
   }

   public static void tickCooldown(ItemStack stack) {
      CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
         if (tag.contains("TOCooldowns")) {
            CompoundTag cooldowns = tag.getCompound("TOCooldowns");

            for (String key : cooldowns.getAllKeys()) {
               int newCooldown = Math.max(cooldowns.getInt(key) - 1, 0);
               cooldowns.putInt(key, newCooldown);
            }

            tag.put("TOCooldowns", cooldowns);
         }
      });
   }
}
