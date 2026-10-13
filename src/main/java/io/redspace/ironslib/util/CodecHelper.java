package io.redspace.ironslib.util;

import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

public class CodecHelper {
   public static <T> void replaceLegacy(ItemStack stack, Supplier<DataComponentType<T>> legacy, Supplier<DataComponentType<T>> dataComponentType) {
      if (stack.has(legacy)) {
         T data = (T)stack.get(legacy);
         stack.remove(legacy);
         stack.set(dataComponentType, data);
      }
   }
}
