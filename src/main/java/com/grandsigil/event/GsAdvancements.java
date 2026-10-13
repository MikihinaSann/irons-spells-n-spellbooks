package com.grandsigil.event;

import com.grandsigil.GrandSigil;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class GsAdvancements {
   private static final int SCORCHED = 8;
   private static final int STORM_CROWD = 10;
   private static final int CONGREGATION = 60;
   private static final float LOADED = 60.0F;
   private static final int ROOM = 12;
   private static final int PACKED = 10;
   private static final int ROOMFUL = 8;

   private GsAdvancements() {
   }

   public static void fired(ServerPlayer caster, boolean killed) {
      award(caster, "root");
      award(caster, "full_circle");
      if (killed) {
         award(caster, "congregation");
      }
   }

   public static void sunfell(ServerPlayer caster, int caught) {
      award(caster, "sunfall");
      if (caught >= 8) {
         award(caster, "solar_eclipse");
      }
   }

   public static void skybroke(ServerPlayer caster, int caught, int sheltered) {
      award(caster, "skybreak");
      if (caught >= 10) {
         award(caster, "one_strike");
      }

      if (caught > 0 && sheltered == 0) {
         award(caster, "nowhere_to_hide");
      }
   }

   public static void treeBloomed(ServerPlayer caster, int generation, int maxGeneration) {
      award(caster, "world_tree");
      if (generation >= maxGeneration) {
         award(caster, "well_fed");
      }
   }

   public static void gateHeld(ServerPlayer caster, int judged) {
      award(caster, "heaven_gate");
      if (judged >= 60) {
         award(caster, "standing_judgement");
      }
   }

   public static void glaciated(ServerPlayer caster, int sealed, float biggest) {
      award(caster, "glaciation");
      if (biggest >= 60.0F) {
         award(caster, "cold_storage");
      }
   }

   public static void gazed(ServerPlayer caster, int held, int looked) {
      award(caster, "the_gaze");
      if (held >= 12) {
         award(caster, "no_ones_side");
      }
   }

   public static void penned(ServerPlayer caster, int killed) {
      award(caster, "colosseum");
      if (killed >= 10) {
         award(caster, "full_house");
      }
   }

   public static void gripped(ServerPlayer caster, int slammed) {
      award(caster, "voidgrip");
      if (slammed >= 8) {
         award(caster, "off_your_feet");
      }
   }

   public static void reckoned(ServerPlayer caster, int struck) {
      award(caster, "reckoning");
      if (struck >= 8) {
         award(caster, "nothing_left");
      }
   }

   public static void coffined(ServerPlayer caster, int caught) {
      award(caster, "black_coffin");
      if (caught >= 5) {
         award(caster, "closed_casket");
      }
   }

   public static void armed(ServerPlayer caster, int landed, int launched) {
      award(caster, "thousand_blades");
      if (launched > 0 && landed >= launched) {
         award(caster, "not_one_missed");
      }
   }

   public static void reborn(ServerPlayer caster, int kills) {
      award(caster, "phoenix");
      if (kills >= 10) {
         award(caster, "from_the_ashes");
      }
   }

   public static void unleashed(ServerPlayer caster, int strikes, int kills) {
      award(caster, "thunder_dragon");
      if (kills >= 8) {
         award(caster, "eye_of_the_storm");
      }
   }

   public static void bloomed(ServerPlayer caster, int kills) {
      award(caster, "full_bloom");
      if (kills >= 10) {
         award(caster, "in_full_flower");
      }
   }

   public static void froze(ServerPlayer caster, int kills) {
      award(caster, "ice_age");
      if (kills >= 10) {
         award(caster, "absolute_zero");
      }
   }

   public static void bledDry(ServerPlayer caster, int kills) {
      award(caster, "crimson_threads");
      if (kills >= 10) {
         award(caster, "bled_dry");
      }
   }

   public static void cleaved(ServerPlayer caster, int kills) {
      award(caster, "dimension_cut");
      if (kills >= 10) {
         award(caster, "world_cleaver");
      }
   }

   public static void award(ServerPlayer player, String path) {
      ResourceLocation id = GrandSigil.id(path);
      AdvancementHolder advancement = player.server.getAdvancements().get(id);
      if (advancement != null) {
         AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
         if (!progress.isDone()) {
            for (String criterion : progress.getRemainingCriteria()) {
               player.getAdvancements().award(advancement, criterion);
            }
         }
      }
   }
}
