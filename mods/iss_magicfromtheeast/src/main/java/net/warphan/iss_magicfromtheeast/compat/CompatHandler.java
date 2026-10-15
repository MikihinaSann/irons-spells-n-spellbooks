package net.warphan.iss_magicfromtheeast.compat;

import java.util.Map;
import net.neoforged.fml.ModList;

public class CompatHandler {
   private static final Map<String, Runnable> MOD_MAP = Map.of();

   public static void init() {
      MOD_MAP.forEach((modid, supplier) -> {
         if (ModList.get().isLoaded(modid)) {
            supplier.run();
         }
      });
   }
}
