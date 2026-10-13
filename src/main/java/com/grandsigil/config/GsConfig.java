package com.grandsigil.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import org.apache.commons.lang3.tuple.Pair;

public final class GsConfig {
   public static final GsConfig.Common COMMON;
   public static final ModConfigSpec COMMON_SPEC;
   public static final GsConfig.Client CLIENT;
   public static final ModConfigSpec CLIENT_SPEC;

   private GsConfig() {
   }

   static {
      Pair<GsConfig.Common, ModConfigSpec> common = new Builder().configure(GsConfig.Common::new);
      COMMON = (GsConfig.Common)common.getLeft();
      COMMON_SPEC = (ModConfigSpec)common.getRight();
      Pair<GsConfig.Client, ModConfigSpec> client = new Builder().configure(GsConfig.Client::new);
      CLIENT = (GsConfig.Client)client.getLeft();
      CLIENT_SPEC = (ModConfigSpec)client.getRight();
   }

   public static final class Client {
      public final BooleanValue screenDistortion;
      public final BooleanValue screenFlash;
      public final IntValue detail;

      Client(Builder builder) {
         builder.comment("What this machine draws").push("visuals");
         this.screenDistortion = builder.comment(
               "The radial smear across the screen when the circle fires.",
               "This one copies the framebuffer and runs a shader over it. If another",
               "mod owns the pipeline -- Optifine, Iris, a shaderpack -- turn it off."
            )
            .define("screenDistortion", true);
         this.screenFlash = builder.comment("The white wash over the screen at the moment of firing.").define("screenFlash", true);
         this.detail = builder.comment(
               "How finely the circles are tessellated.",
               "2 is what the spell was drawn for; 1 halves the segment counts;",
               "0 draws the rings and the beam and skips the ornament."
            )
            .defineInRange("detail", 2, 0, 2);
         builder.pop();
      }
   }

   public static final class Common {
      public final DoubleValue damageMultiplier;
      public final IntValue chargeSeconds;
      public final BooleanValue sigilBinds;
      public final BooleanValue scorchesGround;
      public final IntValue maxTargets;
      public final BooleanValue glaciationBreaksBlocks;
      public final BooleanValue glaciationFreezesGround;
      public final BooleanValue glaciationThaws;
      public final BooleanValue gazeHolds;
      public final BooleanValue fieldsPave;
      public final BooleanValue coffinHolds;

      Common(Builder builder) {
         builder.comment("Every spell in this mod").push("general");
         this.damageMultiplier = builder.comment("Scales everything these spells deal.").defineInRange("damageMultiplier", 1.0, 0.0, 10.0);
         this.maxTargets = builder.comment("Hard cap on how many things one casting may hit.").defineInRange("maxTargets", 64, 1, 512);
         builder.pop();
         builder.comment("Grand Sigil").push("sigil");
         this.chargeSeconds = builder.comment(
               "How long the ritual takes, from the circle being drawn to the",
               "column landing. The twelve runes light across the first five",
               "sevenths of it; the wind-up is the rest."
            )
            .defineInRange("chargeSeconds", 7, 3, 120);
         this.sigilBinds = builder.comment(
               "Whether the circle holds the creature it was drawn under.",
               "This is the spell: turn it off and the ritual still runs and the",
               "column still lands, but the target can walk out of the ring -- which",
               "ends it, because the circle only ever hits that one thing."
            )
            .define("binds", true);
         this.scorchesGround = builder.comment(
               "Whether the column of light leaves a ring of fire when it fires.", "Off by default: this is the only thing the spell does to the world."
            )
            .define("scorchesGround", false);
         builder.pop();
         builder.comment("Glaciation").push("glaciation");
         this.glaciationBreaksBlocks = builder.comment(
               "Whether the plates tear through the floor they come up out of.",
               "Each one clears the few blocks directly in its way -- nothing is",
               "dropped, it is destroyed. Turn off on a server where the spell is",
               "allowed but the terrain is not."
            )
            .define("breaksBlocks", true);
         this.glaciationFreezesGround = builder.comment(
               "Whether the wall is made of real ice blocks.",
               "This is what makes it a wall rather than a picture of one: turn",
               "it off and the plates and Iron's spikes still go up, but nothing",
               "is stopped by them."
            )
            .define("freezesGround", true);
         this.glaciationThaws = builder.comment(
               "Whether the wall goes away again when it comes down.",
               "On by default: a wall that falls is a spell, and one that does",
               "not is a permanent ninety-six block scar on the map every cast.",
               "Turn off if you want it to stay."
            )
            .define("thaws", true);
         builder.pop();
         builder.comment("The Gaze").push("gaze");
         this.gazeHolds = builder.comment(
               "Whether being watched stops you moving and casting.",
               "This is the spell: turn it off and the eye still opens and",
               "still burns everything under it, but nothing is held. Worth",
               "turning off on a PvP server that would rather not have a",
               "fourteen-second lockdown in it."
            )
            .define("holds", true);
         builder.pop();
         builder.comment("Black Coffin").push("coffin");
         this.coffinHolds = builder.comment(
               "Whether being shut in the box stops you walking out of it.",
               "This is the spell: the lances are four seconds long and they only",
               "land on something that is still there. Turn it off and the box",
               "still closes and still drives its lances, but anything quick",
               "enough can leave through the wall. Spells are never taken away --",
               "being sealed in a box with four seconds to answer it is a better",
               "fight than being sealed in a box."
            )
            .define("holds", true);
         builder.pop();
         builder.comment("The nine domains").push("fields");
         this.fieldsPave = builder.comment(
               "Whether a domain repaves the ground inside it.",
               "This is what the nine field spells are: the buff and the debuff are",
               "one piece of terrain read from two sides. Turn it off and they still",
               "hold and still favour their caster, but the floor does not change.",
               "Nothing is ever destroyed either way -- every block is written down",
               "with what was under it and put back when the domain ends."
            )
            .define("pave", true);
         builder.pop();
      }
   }
}
