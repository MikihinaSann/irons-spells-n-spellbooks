package net.acetheeldritchking.cataclysm_spellbooks.util;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class CSConfig {
   private static final Builder BUILDER = new Builder();
   public static ConfigValue<Boolean> BOSSES_RESISTANCES;
   public static final ModConfigSpec BUILDING;

   static {
      BUILDER.push("Cataclysm Spellbooks");
      BUILDER.comment("Makes Cataclysm bosses weak/resistant to certain schools");
      BOSSES_RESISTANCES = BUILDER.worldRestart().define("Add resistances to bosses", true);
      BUILDER.pop();
      BUILDING = BUILDER.build();
   }
}
