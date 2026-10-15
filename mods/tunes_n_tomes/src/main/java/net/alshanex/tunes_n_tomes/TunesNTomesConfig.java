package net.alshanex.tunes_n_tomes;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class TunesNTomesConfig {
   private static final Builder BUILDER = new Builder();
   public static final IntValue MAX_SOUND_MARKERS = BUILDER.comment("Maximum number of active sound markers a player can have at once")
      .defineInRange("maxSoundMarkers", 4, 1, 16);
   public static final IntValue MARKER_DURATION_SECONDS = BUILDER.comment("How long sound markers last in seconds before expiring")
      .defineInRange("markerDurationSeconds", 60, 10, 600);
   public static final BooleanValue MARKER_WITH_SHIFT = BUILDER.comment(
         "If true the marker will be set when casting a sound spell while shifting, if false it will be set when casting without shifting"
      )
      .define("markerWhenShift", false);
   public static final IntValue RESONANCE_TICKS_PER_5_BLOCKS = BUILDER.comment(
         "How many ticks of delay per 5 blocks of distance before a marker resonates. 20 ticks = 1 second. Set to 0 for instant resonance regardless of distance."
      )
      .defineInRange("resonanceTicksPer5Blocks", 10, 0, 200);
   public static final BooleanValue ENABLE_POSTGAME_ARMORSETS = BUILDER.comment(
         "Enable postgame armor set for compatibility with other mods which add postgame ISS content"
      )
      .define("postgameArmor", false);
   public static final ModConfigSpec SPEC = BUILDER.build();
}
