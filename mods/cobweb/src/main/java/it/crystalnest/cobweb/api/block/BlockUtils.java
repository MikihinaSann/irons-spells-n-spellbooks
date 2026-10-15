package it.crystalnest.cobweb.api.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public final class BlockUtils {
   private BlockUtils() {
   }

   public static ResourceLocation getKey(Block block) {
      return BuiltInRegistries.BLOCK.getKey(block);
   }

   public static String getStringKey(Block block) {
      return getKey(block).toString();
   }
}
