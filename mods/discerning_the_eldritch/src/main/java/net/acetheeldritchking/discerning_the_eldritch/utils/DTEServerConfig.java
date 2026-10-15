package net.acetheeldritchking.discerning_the_eldritch.utils;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

@EventBusSubscriber(
   modid = "discerning_the_eldritch",
   bus = Bus.MOD
)
public class DTEServerConfig {
   private static final Builder BUILDER = new Builder();
   private static final BooleanValue MEND_FLESH_LIFESTEAL = BUILDER.comment("Defines whether or not mend flesh will heal on entity hit. Default is true")
      .define("Mend flesh lifesteal", true);
   private static final BooleanValue MEND_FLESH_EXP_GAIN = BUILDER.comment("Defines whether or not mend flesh will heal on XP gain. Default is true")
      .define("Mend flesh exp gain", true);
   private static final BooleanValue ELDRITCH_INSANITY_SYSTEM = BUILDER.comment(
         "Defines whether or not to enable the insanity system. Everytime an Eldritch spell is cast, it increases your insanity."
      )
      .comment("Default is false")
      .define("Enable insanity system", false);
   private static final ConfigValue<Integer> ELDRITCH_MAX_INSANITY = BUILDER.comment(
         "Defines the max value for insanity. At the max value, the insanity debuffs will be applied to the player."
      )
      .comment("Default is [15]")
      .define("Max value for insanity", 15);
   private static final BooleanValue ENABLE_ABRACADABRA_DAMAGE_CAP = BUILDER.comment("Defines whether or not to enable Abracadabra's damage cap abilities.")
      .comment("Default is true")
      .define("Enable Abracadabra's damage cap", true);
   private static final ConfigValue<Integer> ABRACADABRA_DAMAGE_CAP_VALUE = BUILDER.comment("Defines the base damage cap value for Abracadabra spell")
      .comment("Default is [80]")
      .define("Base damage cap", 80);
   private static final BooleanValue ENABLE_ABRACADABRA_HEX_PREVENTION = BUILDER.comment(
         "Defines whether or not to enable Abracadabra's hex prevention abilities. This prevents the user from gaining negative potion effects."
      )
      .comment("Default is true")
      .define("Enable Abracadabra's hex prevention", true);
   private static final ConfigValue<Integer> PYRIUM_SHEATH_COOLDOWN = BUILDER.comment("Defines the cooldown in seconds value for the Pyrium Sheath's ability")
      .comment("Default is [5]")
      .define("Pyrium Sheath CD", 5);
   private static final ConfigValue<Integer> FROSTBOURNE_SHEATH_COOLDOWN = BUILDER.comment(
         "Defines the cooldown in seconds value for the Frostbourne Sheath's ability"
      )
      .comment("Default is [5]")
      .define("Frostbourne Sheath CD", 5);
   private static final ConfigValue<Integer> RAZOR_SHEATH_COOLDOWN = BUILDER.comment(
         "Defines the cooldown in seconds value for the Sheath of The Razor's ability"
      )
      .comment("Default is [10]")
      .define("Sheath of The Razor CD", 10);
   private static final ConfigValue<Integer> RAZOR_SHEATH_MULTIPLIER = BUILDER.comment(
         "Defines the multiplier to be added when evaluating the damage from the razor blades"
      )
      .comment("Math is: 2 + (base attack damage * (config value / 100))")
      .comment("Dividing by 100 gets the percentage of what the config value, so if you input 10, it will result in 0.10 aka 10%")
      .comment("Default is [10]")
      .define("Sheath of The Razor Multiplier", 10);
   private static final ConfigValue<Float> MOURNING_STAR_MORTAL_DAMAGE_MULTIPLIER = BUILDER.comment(
         "Defines the multiplier for the Mourning Star's mortal damage decree"
      )
      .comment("Default is [5.5F]")
      .define("Mourning Star Damage Multiplier", 5.5F);
   private static final BooleanValue ENABLED_ASCENDED_ONE_DAMAGE_CAP = BUILDER.comment("Defines whether or not to enable Ascended One's damage cap.")
      .comment("Default is true")
      .define("Enable Ascended One's damage cap", true);
   private static final ConfigValue<Integer> ASCENDED_ONE_DAMAGE_CAP_VALUE = BUILDER.comment(
         "Defines the max damage the Ascended One can take for the damage cap"
      )
      .comment("Default is [100]")
      .define("Base Ascended One damage cap", 100);
   private static final BooleanValue BLOOD_CULTIST_AGGRESSION = BUILDER.comment("Defines whether or not to enable blood cultist aggression.")
      .comment("This means blood cultists will attack you on sight if your blood spell power is below 1.15x.")
      .comment("Default is false")
      .define("Enable Blood Cultist aggression", false);
   private static final BooleanValue ENABLE_GAOLER_SCREENSHAKE = BUILDER.comment("Defines whether or not to enable the Gaoler's walking screenshake.")
      .comment("Default is true")
      .define("Enable Gaoler's walking screenshake", true);
   public static final ModConfigSpec SPEC = BUILDER.build();
   public static boolean mendFleshLifesteal;
   public static boolean mendFleshEXPGain;
   public static boolean enableInsanitySystem;
   public static int maxInsanityValue;
   public static int abracadabraDamageCap;
   public static boolean enableDamageCap;
   public static boolean enableHexPrevention;
   public static boolean enableAscendedOneDamageCap;
   public static int ascendedOneDamageCap;
   public static int pyriumSheathCooldown;
   public static int frostbourneSheathCooldown;
   public static int razorSheathCooldown;
   public static int razorSheathMultiplier;
   public static boolean gaolerWalkingScreenshake;
   public static float mourningStarMortalDamageMultiplier;
   public static boolean enableBloodCultistAggression;

   @SubscribeEvent
   static void onLoad(ModConfigEvent event) {
      mendFleshLifesteal = (Boolean)MEND_FLESH_LIFESTEAL.get();
      mendFleshEXPGain = (Boolean)MEND_FLESH_EXP_GAIN.get();
      enableInsanitySystem = (Boolean)ELDRITCH_INSANITY_SYSTEM.get();
      maxInsanityValue = (Integer)ELDRITCH_MAX_INSANITY.get();
      enableDamageCap = (Boolean)ENABLE_ABRACADABRA_DAMAGE_CAP.get();
      abracadabraDamageCap = (Integer)ABRACADABRA_DAMAGE_CAP_VALUE.get();
      enableHexPrevention = (Boolean)ENABLE_ABRACADABRA_HEX_PREVENTION.get();
      pyriumSheathCooldown = (Integer)PYRIUM_SHEATH_COOLDOWN.get();
      frostbourneSheathCooldown = (Integer)FROSTBOURNE_SHEATH_COOLDOWN.get();
      razorSheathCooldown = (Integer)RAZOR_SHEATH_COOLDOWN.get();
      razorSheathMultiplier = (Integer)RAZOR_SHEATH_MULTIPLIER.get();
      mourningStarMortalDamageMultiplier = (Float)MOURNING_STAR_MORTAL_DAMAGE_MULTIPLIER.get();
      enableAscendedOneDamageCap = (Boolean)ENABLED_ASCENDED_ONE_DAMAGE_CAP.get();
      ascendedOneDamageCap = (Integer)ASCENDED_ONE_DAMAGE_CAP_VALUE.get();
      enableBloodCultistAggression = (Boolean)BLOOD_CULTIST_AGGRESSION.get();
      gaolerWalkingScreenshake = (Boolean)ENABLE_GAOLER_SCREENSHAKE.get();
   }
}
