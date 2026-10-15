package it.crystalnest.cobweb.api.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class ItemUtils {
   private ItemUtils() {
   }

   public static ResourceLocation getKey(Item item) {
      return BuiltInRegistries.ITEM.getKey(item);
   }

   public static String getStringKey(Item item) {
      return getKey(item).toString();
   }
}
