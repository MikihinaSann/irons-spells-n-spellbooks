package com.somake.somakespells.util;

import com.somake.somakespells.config.WeaponBalanceConfig;
import com.somake.somakespells.network.WeaponBalanceClientState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab.Output;

public final class WeaponVisibilityHelper {
   private WeaponVisibilityHelper() {
   }

   public static boolean shouldShow(Item item) {
      ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
      if (itemId == null) {
         return true;
      } else {
         WeaponBalanceConfig.WeaponOverrideValues serverValues = WeaponBalanceClientState.getWeaponForItem(itemId);
         if (serverValues != null) {
            return serverValues.showInListings();
         } else {
            WeaponBalanceConfig.WeaponOverrideValues localValues = WeaponBalanceConfig.getLocalWeaponOverride(itemId);
            return localValues == null || localValues.showInListings();
         }
      }
   }

   public static void acceptIfVisible(Output output, Item item) {
      if (shouldShow(item)) {
         output.accept(item);
      }
   }
}
