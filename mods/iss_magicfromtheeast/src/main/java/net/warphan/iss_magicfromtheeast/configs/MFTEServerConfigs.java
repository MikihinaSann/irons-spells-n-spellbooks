package net.warphan.iss_magicfromtheeast.configs;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class MFTEServerConfigs {
   private static final Builder BUILDER = new Builder();
   public static final ConfigValue<Double> SOULBURN_MAX_DAMAGE;
   public static final ConfigValue<Integer> SOULBURN_DAMAGE_SCALING;
   public static final ConfigValue<Double> IMPERMANENCE_LIMIT_PERCENT;
   public static final ConfigValue<Boolean> PASS_CHALLENGING;
   public static final ConfigValue<Boolean> ALLOW_SPIRIT_CRUSHER_DAMAGE_CAP;
   public static final ConfigValue<Double> SPIRIT_CRUSHER_DAMAGE_CAP;
   public static final ModConfigSpec SPEC;

   public static void onConfigReload() {
   }

   static {
      BUILDER.comment("Serverside Configurations");
      BUILDER.push("Spells");
      BUILDER.comment("The limit of damage Impermanence's Verdict spell can reach based on target's max health. Default: 0.2 (20% target's max health)");
      IMPERMANENCE_LIMIT_PERCENT = BUILDER.worldRestart().define("impermanencePercentLimit", 0.2);
      BUILDER.comment("Allow player to use counterspell on extracted soul to skip the challenging. Default: false");
      PASS_CHALLENGING = BUILDER.worldRestart().define("passChallenging", false);
      BUILDER.pop();
      BUILDER.push("Effects");
      BUILDER.comment("The maximum amount of damage soulburn effect can reach to. Default: 10.0");
      SOULBURN_MAX_DAMAGE = BUILDER.define("maxSoulburnDamage", 10.0);
      BUILDER.comment("The percent amount of damage soulburn effect dealt based on target's max health. Default: 5 (5% of target's max health)");
      SOULBURN_DAMAGE_SCALING = BUILDER.define("soulburnDamageScaling", 5);
      BUILDER.pop();
      BUILDER.push("Weapons");
      BUILDER.comment("Allow damage cap working on Spirit Crusher. Default: false");
      ALLOW_SPIRIT_CRUSHER_DAMAGE_CAP = BUILDER.worldRestart().define("allowSpiritCrusherDamageCap", false);
      BUILDER.comment("The amount of the damage cap. Default: 30.0");
      SPIRIT_CRUSHER_DAMAGE_CAP = BUILDER.worldRestart().define("spiritCrusherDamageCapAmount", 30.0);
      BUILDER.pop();
      SPEC = BUILDER.build();
   }
}
