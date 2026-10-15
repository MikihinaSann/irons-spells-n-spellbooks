package net.alshanex.alshanex_familiars.datafix;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.nbt.CompoundTag;

public class DataFixerHelpers {
   public static final List<DataFixerElement> DATA_FIXER_ELEMENTS = List.of(new FixAttributeNames());

   public static boolean doFixUps(CompoundTag tag) {
      AtomicInteger runningCount = new AtomicInteger();
      DATA_FIXER_ELEMENTS.forEach(fixerElement -> {
         if (fixerElement.runFixer(tag)) {
            runningCount.incrementAndGet();
         }
      });
      return runningCount.get() > 0;
   }
}
