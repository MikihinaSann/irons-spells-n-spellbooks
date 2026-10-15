package net.acetheeldritchking.aces_spell_utils.utils;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;

public class AcesSpellUtilsConfig {
   private static final Builder BUILDER = new Builder();
   private static final BooleanValue MANA_STEAL_DRAINS_MANA = BUILDER.comment(
         "Defines whether or not mana steal should drain the mana of the target entity. Default is true"
      )
      .define("Mana Steal drains mana", true);
   public static final ModConfigSpec SPEC = BUILDER.build();
   public static boolean manaStealDrain;

   @SubscribeEvent
   static void onLoad(ModConfigEvent event) {
      manaStealDrain = (Boolean)MANA_STEAL_DRAINS_MANA.get();
   }
}
