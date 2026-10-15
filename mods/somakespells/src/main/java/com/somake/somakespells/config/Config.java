package com.somake.somakespells.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class Config {
   private static final Builder BUILDER = new Builder();
   public static final BooleanValue REMOVE_CHARGE_SPEED_BOOST = BUILDER.comment(
         "If true, removes the Movement Speed bonus from Iron's Spells 'n Spellbooks Charge effect while keeping its other bonuses intact."
      )
      .define("removeChargeSpeedBoost", false);
   public static final BooleanValue ENABLE_SPELL_LOCK_SYSTEM = BUILDER.comment(
         "If true, players must learn spells (via /somake learn or other means) before using them. If false, all spells are unlocked by default."
      )
      .define("enableSpellLockSystem", false);
   public static final BooleanValue ENABLE_SACRED_PHOENIX_BLESSING = BUILDER.comment(
         "Startup-only toggle for the Sacred Phoenix Blessing spell. When false, the spell and its support registries do not exist and it is absent from commands such as /cast. Restart the game after changing this value."
      )
      .define("enableSacredPhoenixBlessing", false);
   public static final IntValue FAMILIAR_LIMIT = BUILDER.comment(
         "The maximum number of familiars a player can tame from the Alshanex Familiars mod (if installed). Default is 10."
      )
      .defineInRange("familiarLimit", 10, 1, Integer.MAX_VALUE);
   public static final BooleanValue DISABLE_KRAMPUS_ITEM_STEAL = BUILDER.comment(
         "If true, Krampus and Krampus Henchman from Born in Chaos can no longer disarm players (item steal/drop is blocked)."
      )
      .define("disableKrampusItemSteal", true);
   public static final BooleanValue ENABLE_MISSIONARY_RAIN = BUILDER.comment(
         "If true, Missionary and Missionary Raider from Born in Chaos can force rain again. If false, only their forced rain is blocked and natural weather still works normally."
      )
      .define("enableMissionaryRain", false);
   public static final IntValue SOUL_FIRE_NECKLACE_MAX_CHARGES = BUILDER.comment("Maximum charges for the Soul Fire Necklace.")
      .defineInRange("soulFireNecklaceMaxCharges", 100, 1, Integer.MAX_VALUE);
   public static final IntValue INFERNAL_FIRE_NECKLACE_MAX_CHARGES = BUILDER.comment("Maximum charges for the Infernal Fire Necklace.")
      .defineInRange("infernalFireNecklaceMaxCharges", 100, 1, Integer.MAX_VALUE);
   public static final DoubleValue SOUL_FIRE_BASE_DAMAGE_BONUS_PERCENT = BUILDER.comment(
         "Base bonus damage percent applied to supported Fire spells while Soul Fire mode is active."
      )
      .defineInRange("soulFireBaseDamageBonusPercent", 20.0, 0.0, 1000.0);
   public static final DoubleValue INFERNAL_FIRE_BASE_DAMAGE_BONUS_PERCENT = BUILDER.comment(
         "Base bonus damage percent applied to supported Fire spells while Infernal Fire mode is active."
      )
      .defineInRange("infernalFireBaseDamageBonusPercent", 30.0, 0.0, 1000.0);
   public static final BooleanValue STAFFS_USE_LEGACY_OFFHAND = BUILDER.comment(
         "If true, all Iron's Spellbooks staffs apply their held attributes in the off hand like the legacy behavior. If false, staffs keep the current main hand behavior."
      )
      .define("staffsUseLegacyOffhand", false);
   public static final BooleanValue DISABLE_SOUL_BREAKER_BONUS_SOUL_DAMAGE = BUILDER.comment(
         "If true, Soul Breaker from ISS: Magic From The East no longer deals its bonus soul damage on hit, including Inner Impact scaling."
      )
      .define("disableSoulBreakerBonusSoulDamage", false);
   public static final BooleanValue DISABLE_CUSTODIA_CAELI_CRAFTING_AND_LOOTING = BUILDER.comment(
         "If true, Custodia Caeli can no longer be crafted and will no longer appear in loot sources such as chests."
      )
      .define("disableCustodiaCaeliCraftingAndLooting", false);
   public static final BooleanValue DISABLE_JINGLE_BELL_CRAFTING_AND_LOOTING = BUILDER.comment(
         "If true, Jingle Bell can no longer be crafted and will no longer appear in loot sources such as chests."
      )
      .define("disableJingleBellCraftingAndLooting", false);
   public static final BooleanValue DISABLE_WITHERED_ROSE_SPELLS_CRAFTING_AND_LOOTING = BUILDER.comment(
         "If true, Withered Rose Vortex and Funeral Bloom can no longer be crafted and will no longer appear in loot sources such as chests. The Rose's Secret always remains exclusive to the Withered Rose Staff."
      )
      .define("disableWitheredRoseSpellsCraftingAndLooting", false);
   public static final BooleanValue HIDE_CENTAURI_FROM_CREATIVE_TAB = BUILDER.comment("If true, Centauri is hidden from the Somake Spells creative tab.")
      .define("hideCentauriFromCreativeTab", true);
   public static final BooleanValue HIDE_SCORN_FROM_CREATIVE_TAB = BUILDER.comment("If true, Scorn is hidden from the Somake Spells creative tab.")
      .define("hideScornFromCreativeTab", true);
   public static final ModConfigSpec SPEC = BUILDER.build();
}
