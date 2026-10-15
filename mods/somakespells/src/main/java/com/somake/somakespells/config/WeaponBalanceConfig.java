package com.somake.somakespells.config;

import com.mojang.logging.LogUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import org.slf4j.Logger;

public class WeaponBalanceConfig {
   private static final Builder BUILDER = new Builder();
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<ResourceLocation, WeaponBalanceConfig.WeaponOverrideConfig> WEAPONS = new LinkedHashMap<>();
   private static final Map<String, WeaponBalanceConfig.DynamicWeaponEntry> CUSTOM_WEAPONS = new LinkedHashMap<>();
   public static final WeaponBalanceConfig.WeaponOverrideConfig SHARPENED_DARK_METAL_SWORD = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "sharpened_dark_metal_sword"),
      "sword",
      "sharpenedDarkMetalSword",
      9.0,
      1.5,
      3.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig BOREAL_BLADE = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "boreal_blade"),
      "sword",
      "borealBlade",
      13.0,
      1.5,
      3.0,
      false,
      0.0,
      true,
      0.15,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig FLUIX_SWORD = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("ae2", "fluix_sword"),
      "sword",
      "fluixSword",
      13.0,
      1.5,
      3.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig SOUL_BREAKER = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "soul_breaker"),
      "sword",
      "soulBreaker",
      13.0,
      1.5,
      3.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15,
      false,
      0.0,
      false,
      0.0,
      true,
      -0.1
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig SPIDER_BITE_SWORD = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "spider_bite_sword"),
      "sword",
      "spiderBiteSword",
      13.0,
      1.5,
      3.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig GREAT_REAPER_AXE = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "great_reaper_axe"),
      "axe",
      "greatReaperAxe",
      11.0,
      1.0,
      3.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig SKULLBREAKER_HAMMER = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "skullbreaker_hammer"),
      "hammer",
      "skullbreakerHammer",
      11.0,
      0.5,
      3.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig FORBIDDEN_HAMMER = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("somakespells", "forbidden_hammer"),
      "hammer",
      "forbiddenHammer",
      15.0,
      0.5,
      3.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig DECREPIT_SCYTHE = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "decrepit_scythe"),
      "foices",
      "decrepitScythe",
      9.0,
      1.0,
      3.5,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig NIGHTMARE_SCYTHE = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "nightmare_scythe"),
      "foices",
      "nightmareScythe",
      10.0,
      1.0,
      3.5,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig HELLRAZOR = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "hellrazor"),
      "foices",
      "hellrazor",
      12.0,
      1.0,
      3.5,
      true,
      0.15,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig TRIDENT_HAYFORK = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "trident_hayfork"),
      "spears",
      "tridentHayfork",
      9.0,
      1.1,
      4.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig JADE_GUANDAO = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "jade_guandao"),
      "spears",
      "jadeGuandao",
      9.0,
      1.1,
      4.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig CORAL_BARDICHE = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("cataclysm", "coral_bardiche"),
      "spears",
      "coralBardiche",
      9.0,
      1.1,
      4.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig MOWZIE_SPEAR = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("mowziesmobs", "spear"),
      "spears",
      "mowzieSpear",
      9.0,
      1.1,
      4.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig TWILIGHT_GALE = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "twilight_gale"),
      "spears",
      "twilightGale",
      9.0,
      1.1,
      4.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig KEEPER_FLAMBERGE = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "keeper_flamberge"),
      "soul_sabers",
      "keeperFlamberge",
      6.0,
      1.7,
      3.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig SOUL_CUTLASS = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "soul_cutlass"),
      "soul_sabers",
      "soulCutlass",
      8.0,
      1.7,
      3.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig SOULBANE = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "soulbane"),
      "soul_sabers",
      "soulbane",
      11.0,
      1.7,
      3.0,
      true,
      0.25,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig LEGIONNAIRE_FLAMBERGE = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "legionnaire_flamberge"),
      "soul_sabers",
      "legionnaireFlamberge",
      12.0,
      1.7,
      3.0,
      true,
      0.15,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig SPELLBREAKER = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "spellbreaker"),
      "soul_sabers",
      "spellbreaker",
      12.0,
      1.7,
      3.0,
      false,
      0.0,
      true,
      0.15,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig SOUL_KATANA = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "soul_katana"),
      "soul_sabers",
      "soulKatana",
      12.0,
      1.7,
      3.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.15,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig MURAMASA = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "muramasa"),
      "soul_sabers",
      "muramasa",
      12.0,
      1.7,
      3.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.15,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig AMETHYST_RAPIER = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "amethyst_rapier"),
      "soul_sabers",
      "amethystRapier",
      12.0,
      1.7,
      3.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig INTOXICATING_DAGGER = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "intoxicating_dagger"),
      "dagger",
      "intoxicatingDagger",
      5.0,
      2.5,
      2.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig DARK_RITUAL_DAGGER = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "dark_ritual_dagger"),
      "dagger",
      "darkRitualDagger",
      8.0,
      2.5,
      2.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig NAGA_FANG = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("mowziesmobs", "naga_fang_dagger"),
      "dagger",
      "nagaFang",
      8.0,
      2.5,
      2.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig GLACIUM_GREATAXE = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("somakespells", "glacium_greataxe"),
      "axe",
      "glaciumGreataxe",
      18.0,
      1.5,
      3.0,
      false,
      0.0,
      true,
      0.25,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig CORE_SPLITTER = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("somakespells", "core_splitter"),
      "axe",
      "coreSplitter",
      20.0,
      1.7,
      3.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.25,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig HALLOW_SWORD = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("somakespells", "hallow_sword"),
      "sword",
      "hallowSword",
      13.0,
      1.5,
      3.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig CENTAURI = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("somakespells", "centauri"),
      "sword",
      "centauri",
      12.0,
      1.7,
      3.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig LAST_MOURNING = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("somakespells", "last_mourning"),
      "sword",
      "lastMourning",
      12.0,
      1.0,
      3.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig MIRRORED_EDGE = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("somakespells", "mirrored_edge"),
      "sword",
      "mirroredEdge",
      15.0,
      1.0,
      3.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig MONOLITH_SWORD = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("somakespells", "monolith_sword"),
      "sword",
      "monolithSword",
      13.0,
      1.5,
      3.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig RADIANT_JUDGMENT = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("somakespells", "radiant_judgment"),
      "sword",
      "radiantJudgment",
      15.0,
      1.0,
      3.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig SCORN = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("somakespells", "scorn"),
      "sword",
      "scorn",
      12.0,
      1.0,
      3.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig SCULKBORNE_AXE = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("somakespells", "sculkborne_axe"),
      "axe",
      "sculkborneAxe",
      15.0,
      1.0,
      3.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig TIDE_PIERCER = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("somakespells", "tide_piercer"),
      "spears",
      "tidePiercer",
      9.0,
      1.1,
      4.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig TOTEM_DAGGER = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("somakespells", "totem_dagger"),
      "dagger",
      "totemDagger",
      8.0,
      2.5,
      2.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig TRUMPET_AXE = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("somakespells", "trumpet_axe"),
      "axe",
      "trumpetAxe",
      15.0,
      1.0,
      3.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig WITHERITE_GLAIVE = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("somakespells", "witherite_glaive"),
      "spears",
      "witheriteGlaive",
      18.0,
      1.5,
      3.5,
      false,
      0.0,
      false,
      0.0,
      true,
      0.25,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig RUINED_BLADE = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("somakespells", "ruined_blade"),
      "sword",
      "ruinedBlade",
      12.0,
      1.7,
      3.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig BOLTCUTTER_DAGGER = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("somakespells", "boltcutter_dagger"),
      "dagger",
      "boltcutterDagger",
      9.0,
      2.5,
      2.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig CLEF_SWORD = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("somakespells", "clef_sword"),
      "sword",
      "clefSword",
      13.0,
      1.5,
      3.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15,
      false,
      0.0,
      false,
      0.0
   );
   public static final WeaponBalanceConfig.WeaponOverrideConfig ROCK_SWORD = registerWeapon(
      ResourceLocation.fromNamespaceAndPath("somakespells", "rock_sword"),
      "sword",
      "rockSword",
      15.0,
      0.5,
      3.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      false,
      0.0,
      true,
      0.15,
      false,
      0.0
   );
   public static final ModConfigSpec SPEC;

   private static void registerCustomWeaponSlot(int index) {
      BUILDER.push("weaponBalance");
      BUILDER.push("custom");
      BUILDER.push("entry" + index);
      ConfigValue<String> itemId = BUILDER.comment("Weapon item ID to override, for example 'othermod:forgotten_sword'. Leave empty to disable this slot.")
         .define("itemId", "");
      WeaponBalanceConfig.WeaponOverrideConfig config = registerWeapon(
         ResourceLocation.fromNamespaceAndPath("somakespells", "dynamic_weapon_placeholder_" + index),
         "custom",
         "entry" + index,
         0.0,
         0.0,
         0.0,
         false,
         0.0,
         false,
         0.0,
         false,
         0.0,
         false,
         0.0,
         false,
         0.0,
         false,
         0.0,
         false,
         0.0,
         false,
         0.0,
         false,
         0.0,
         false,
         0.0
      );
      CUSTOM_WEAPONS.put("entry" + index, new WeaponBalanceConfig.DynamicWeaponEntry(itemId, config));
      BUILDER.pop();
      BUILDER.pop();
      BUILDER.pop();
   }

   private static WeaponBalanceConfig.WeaponOverrideConfig registerWeapon(
      ResourceLocation itemId,
      String category,
      String key,
      double attackDamage,
      double attackSpeed,
      double entityInteractionRange,
      boolean overrideFireSpellPower,
      double fireSpellPower,
      boolean overrideIceSpellPower,
      double iceSpellPower,
      boolean overrideLightningSpellPower,
      double lightningSpellPower,
      boolean overrideEnderSpellPower,
      double enderSpellPower,
      boolean overrideBloodSpellPower,
      double bloodSpellPower,
      boolean overrideNatureSpellPower,
      double natureSpellPower,
      boolean overrideSpiritSpellPower,
      double spiritSpellPower,
      boolean overrideSymmetrySpellPower,
      double symmetrySpellPower,
      boolean overrideAquaSpellPower,
      double aquaSpellPower,
      boolean overrideSpellResist,
      double spellResist
   ) {
      return registerWeapon(
         itemId,
         category,
         key,
         attackDamage,
         attackSpeed,
         entityInteractionRange,
         overrideFireSpellPower,
         fireSpellPower,
         overrideIceSpellPower,
         iceSpellPower,
         overrideLightningSpellPower,
         lightningSpellPower,
         overrideEnderSpellPower,
         enderSpellPower,
         overrideBloodSpellPower,
         bloodSpellPower,
         overrideNatureSpellPower,
         natureSpellPower,
         overrideSpiritSpellPower,
         spiritSpellPower,
         overrideSymmetrySpellPower,
         symmetrySpellPower,
         overrideAquaSpellPower,
         aquaSpellPower,
         overrideSpellResist,
         spellResist,
         false,
         0.0,
         false,
         0.0,
         false,
         0.0,
         false,
         0.0,
         false,
         0.0
      );
   }

   private static WeaponBalanceConfig.WeaponOverrideConfig registerWeapon(
      ResourceLocation itemId,
      String category,
      String key,
      double attackDamage,
      double attackSpeed,
      double entityInteractionRange,
      boolean overrideFireSpellPower,
      double fireSpellPower,
      boolean overrideIceSpellPower,
      double iceSpellPower,
      boolean overrideLightningSpellPower,
      double lightningSpellPower,
      boolean overrideEnderSpellPower,
      double enderSpellPower,
      boolean overrideBloodSpellPower,
      double bloodSpellPower,
      boolean overrideNatureSpellPower,
      double natureSpellPower,
      boolean overrideSpiritSpellPower,
      double spiritSpellPower,
      boolean overrideSymmetrySpellPower,
      double symmetrySpellPower,
      boolean overrideAquaSpellPower,
      double aquaSpellPower,
      boolean overrideSpellResist,
      double spellResist,
      boolean overrideHolySpellPower,
      double holySpellPower,
      boolean overrideEldritchSpellPower,
      double eldritchSpellPower,
      boolean overrideSoundSpellPower,
      double soundSpellPower,
      boolean overrideGeoSpellPower,
      double geoSpellPower,
      boolean overrideEvocationSpellPower,
      double evocationSpellPower
   ) {
      return registerWeapon(
         itemId,
         category,
         key,
         attackDamage,
         attackSpeed,
         entityInteractionRange,
         false,
         0.0,
         overrideFireSpellPower,
         fireSpellPower,
         overrideIceSpellPower,
         iceSpellPower,
         overrideLightningSpellPower,
         lightningSpellPower,
         overrideEnderSpellPower,
         enderSpellPower,
         overrideBloodSpellPower,
         bloodSpellPower,
         overrideNatureSpellPower,
         natureSpellPower,
         overrideSpiritSpellPower,
         spiritSpellPower,
         overrideSymmetrySpellPower,
         symmetrySpellPower,
         overrideAquaSpellPower,
         aquaSpellPower,
         overrideSpellResist,
         spellResist,
         overrideHolySpellPower,
         holySpellPower,
         overrideEldritchSpellPower,
         eldritchSpellPower,
         overrideSoundSpellPower,
         soundSpellPower,
         overrideGeoSpellPower,
         geoSpellPower,
         overrideEvocationSpellPower,
         evocationSpellPower
      );
   }

   private static WeaponBalanceConfig.WeaponOverrideConfig registerWeapon(
      ResourceLocation itemId,
      String category,
      String key,
      double attackDamage,
      double attackSpeed,
      double entityInteractionRange,
      boolean overrideInfernalFireSpellPower,
      double infernalFireSpellPower,
      boolean overrideFireSpellPower,
      double fireSpellPower,
      boolean overrideIceSpellPower,
      double iceSpellPower,
      boolean overrideLightningSpellPower,
      double lightningSpellPower,
      boolean overrideEnderSpellPower,
      double enderSpellPower,
      boolean overrideBloodSpellPower,
      double bloodSpellPower,
      boolean overrideNatureSpellPower,
      double natureSpellPower,
      boolean overrideSpiritSpellPower,
      double spiritSpellPower,
      boolean overrideSymmetrySpellPower,
      double symmetrySpellPower,
      boolean overrideAquaSpellPower,
      double aquaSpellPower,
      boolean overrideSpellResist,
      double spellResist,
      boolean overrideHolySpellPower,
      double holySpellPower,
      boolean overrideEldritchSpellPower,
      double eldritchSpellPower,
      boolean overrideSoundSpellPower,
      double soundSpellPower,
      boolean overrideGeoSpellPower,
      double geoSpellPower,
      boolean overrideEvocationSpellPower,
      double evocationSpellPower
   ) {
      BUILDER.push("weaponBalance");
      BUILDER.push(category);
      BUILDER.push(key);
      BooleanValue enabled = BUILDER.comment("If true, overrides this weapon attributes using values below. If false, keeps original mod defaults.")
         .define("enabled", false);
      BooleanValue showInListings = shouldExposeListingToggle(itemId, category)
         ? BUILDER.comment("If true, shows this weapon in creative tab listings. If false, the weapon stays registered but is hidden from listings.")
            .define("showInListings", true)
         : null;
      DoubleValue cfgAttackDamage = BUILDER.comment("Final attack damage value shown in-game for this weapon.")
         .defineInRange("attackDamage", attackDamage, -1024.0, 1024.0);
      DoubleValue cfgAttackSpeed = BUILDER.comment("Final attack speed value shown in-game for this weapon.")
         .defineInRange("attackSpeed", attackSpeed, -1024.0, 1024.0);
      DoubleValue cfgEntityInteractionRange = BUILDER.comment("Final entity interaction range value for this weapon.")
         .defineInRange("entityInteractionRange", entityInteractionRange, 0.0, 1024.0);
      BooleanValue cfgOverrideInfernalFireSpellPower = BUILDER.define("overrideInfernalFireSpellPower", overrideInfernalFireSpellPower);
      DoubleValue cfgInfernalFireSpellPower = BUILDER.defineInRange("infernalFireSpellPower", infernalFireSpellPower, -100.0, 100.0);
      BooleanValue cfgOverrideFireSpellPower = BUILDER.define("overrideFireSpellPower", overrideFireSpellPower);
      DoubleValue cfgFireSpellPower = BUILDER.defineInRange("fireSpellPower", fireSpellPower, -100.0, 100.0);
      BooleanValue cfgOverrideIceSpellPower = BUILDER.define("overrideIceSpellPower", overrideIceSpellPower);
      DoubleValue cfgIceSpellPower = BUILDER.defineInRange("iceSpellPower", iceSpellPower, -100.0, 100.0);
      BooleanValue cfgOverrideLightningSpellPower = BUILDER.define("overrideLightningSpellPower", overrideLightningSpellPower);
      DoubleValue cfgLightningSpellPower = BUILDER.defineInRange("lightningSpellPower", lightningSpellPower, -100.0, 100.0);
      BooleanValue cfgOverrideEnderSpellPower = BUILDER.define("overrideEnderSpellPower", overrideEnderSpellPower);
      DoubleValue cfgEnderSpellPower = BUILDER.defineInRange("enderSpellPower", enderSpellPower, -100.0, 100.0);
      BooleanValue cfgOverrideBloodSpellPower = BUILDER.define("overrideBloodSpellPower", overrideBloodSpellPower);
      DoubleValue cfgBloodSpellPower = BUILDER.defineInRange("bloodSpellPower", bloodSpellPower, -100.0, 100.0);
      BooleanValue cfgOverrideNatureSpellPower = BUILDER.define("overrideNatureSpellPower", overrideNatureSpellPower);
      DoubleValue cfgNatureSpellPower = BUILDER.defineInRange("natureSpellPower", natureSpellPower, -100.0, 100.0);
      BooleanValue cfgOverrideSpiritSpellPower = BUILDER.define("overrideSpiritSpellPower", overrideSpiritSpellPower);
      DoubleValue cfgSpiritSpellPower = BUILDER.defineInRange("spiritSpellPower", spiritSpellPower, -100.0, 100.0);
      BooleanValue cfgOverrideSymmetrySpellPower = BUILDER.define("overrideSymmetrySpellPower", overrideSymmetrySpellPower);
      DoubleValue cfgSymmetrySpellPower = BUILDER.defineInRange("symmetrySpellPower", symmetrySpellPower, -100.0, 100.0);
      BooleanValue cfgOverrideAquaSpellPower = BUILDER.define("overrideAquaSpellPower", overrideAquaSpellPower);
      DoubleValue cfgAquaSpellPower = BUILDER.defineInRange("aquaSpellPower", aquaSpellPower, -100.0, 100.0);
      BooleanValue cfgOverrideSpellResist = BUILDER.define("overrideSpellResist", overrideSpellResist);
      DoubleValue cfgSpellResist = BUILDER.defineInRange("spellResist", spellResist, -100.0, 100.0);
      BooleanValue cfgOverrideHolySpellPower = BUILDER.define("overrideHolySpellPower", overrideHolySpellPower);
      DoubleValue cfgHolySpellPower = BUILDER.defineInRange("holySpellPower", holySpellPower, -100.0, 100.0);
      BooleanValue cfgOverrideEldritchSpellPower = BUILDER.define("overrideEldritchSpellPower", overrideEldritchSpellPower);
      DoubleValue cfgEldritchSpellPower = BUILDER.defineInRange("eldritchSpellPower", eldritchSpellPower, -100.0, 100.0);
      BooleanValue cfgOverrideSoundSpellPower = BUILDER.define("overrideSoundSpellPower", overrideSoundSpellPower);
      DoubleValue cfgSoundSpellPower = BUILDER.defineInRange("soundSpellPower", soundSpellPower, -100.0, 100.0);
      BooleanValue cfgOverrideGeoSpellPower = BUILDER.define("overrideGeoSpellPower", overrideGeoSpellPower);
      DoubleValue cfgGeoSpellPower = BUILDER.defineInRange("geoSpellPower", geoSpellPower, -100.0, 100.0);
      BooleanValue cfgOverrideEvocationSpellPower = BUILDER.define("overrideEvocationSpellPower", overrideEvocationSpellPower);
      DoubleValue cfgEvocationSpellPower = BUILDER.defineInRange("evocationSpellPower", evocationSpellPower, -100.0, 100.0);
      BUILDER.pop();
      BUILDER.pop();
      BUILDER.pop();
      WeaponBalanceConfig.WeaponOverrideConfig cfg = new WeaponBalanceConfig.WeaponOverrideConfig(
         enabled,
         showInListings,
         cfgAttackDamage,
         cfgAttackSpeed,
         cfgEntityInteractionRange,
         cfgOverrideInfernalFireSpellPower,
         cfgInfernalFireSpellPower,
         cfgOverrideFireSpellPower,
         cfgFireSpellPower,
         cfgOverrideIceSpellPower,
         cfgIceSpellPower,
         cfgOverrideLightningSpellPower,
         cfgLightningSpellPower,
         cfgOverrideEnderSpellPower,
         cfgEnderSpellPower,
         cfgOverrideBloodSpellPower,
         cfgBloodSpellPower,
         cfgOverrideNatureSpellPower,
         cfgNatureSpellPower,
         cfgOverrideSpiritSpellPower,
         cfgSpiritSpellPower,
         cfgOverrideSymmetrySpellPower,
         cfgSymmetrySpellPower,
         cfgOverrideAquaSpellPower,
         cfgAquaSpellPower,
         cfgOverrideSpellResist,
         cfgSpellResist,
         cfgOverrideHolySpellPower,
         cfgHolySpellPower,
         cfgOverrideEldritchSpellPower,
         cfgEldritchSpellPower,
         cfgOverrideSoundSpellPower,
         cfgSoundSpellPower,
         cfgOverrideGeoSpellPower,
         cfgGeoSpellPower,
         cfgOverrideEvocationSpellPower,
         cfgEvocationSpellPower
      );
      WEAPONS.put(itemId, cfg);
      return cfg;
   }

   public static WeaponBalanceConfig.WeaponOverrideValues getLocalWeaponOverride(ResourceLocation itemId) {
      WeaponBalanceConfig.WeaponOverrideConfig cfg = WEAPONS.get(itemId);
      if (cfg != null) {
         return cfg.read();
      } else {
         for (WeaponBalanceConfig.DynamicWeaponEntry entry : CUSTOM_WEAPONS.values()) {
            ResourceLocation configuredId = entry.parseItemId();
            if (configuredId != null && configuredId.equals(itemId)) {
               return entry.config().read();
            }
         }

         return null;
      }
   }

   public static Map<String, WeaponBalanceConfig.WeaponOverrideValues> getLocalWeaponOverrideSnapshot() {
      Map<String, WeaponBalanceConfig.WeaponOverrideValues> snapshot = new LinkedHashMap<>();
      WEAPONS.forEach((itemId, cfg) -> snapshot.put(itemId.toString(), cfg.read()));
      CUSTOM_WEAPONS.forEach((entryKey, entry) -> {
         ResourceLocation itemId = entry.parseItemId();
         if (itemId != null) {
            if (snapshot.containsKey(itemId.toString())) {
               LOGGER.warn("Custom weapon config '{}' duplicates existing weapon id '{}'; keeping custom values for sync snapshot.", entryKey, itemId);
            }

            snapshot.put(itemId.toString(), entry.config().read());
         }
      });
      return snapshot;
   }

   private static boolean shouldExposeListingToggle(ResourceLocation itemId, String category) {
      return "somakespells".equals(itemId.getNamespace()) && "sword".equals(category);
   }

   static {
      for (int i = 1; i <= 24; i++) {
         registerCustomWeaponSlot(i);
      }

      SPEC = BUILDER.build();
   }

   private record DynamicWeaponEntry(ConfigValue<String> itemId, WeaponBalanceConfig.WeaponOverrideConfig config) {
      private ResourceLocation parseItemId() {
         String raw = ((String)this.itemId.get()).trim();
         if (raw.isEmpty()) {
            return null;
         } else {
            try {
               return ResourceLocation.parse(raw);
            } catch (IllegalArgumentException var3) {
               return null;
            }
         }
      }
   }

   public static class WeaponOverrideConfig {
      public final BooleanValue enabled;
      public final BooleanValue showInListings;
      public final DoubleValue attackDamage;
      public final DoubleValue attackSpeed;
      public final DoubleValue entityInteractionRange;
      public final BooleanValue overrideInfernalFireSpellPower;
      public final DoubleValue infernalFireSpellPower;
      public final BooleanValue overrideFireSpellPower;
      public final DoubleValue fireSpellPower;
      public final BooleanValue overrideIceSpellPower;
      public final DoubleValue iceSpellPower;
      public final BooleanValue overrideLightningSpellPower;
      public final DoubleValue lightningSpellPower;
      public final BooleanValue overrideEnderSpellPower;
      public final DoubleValue enderSpellPower;
      public final BooleanValue overrideBloodSpellPower;
      public final DoubleValue bloodSpellPower;
      public final BooleanValue overrideNatureSpellPower;
      public final DoubleValue natureSpellPower;
      public final BooleanValue overrideSpiritSpellPower;
      public final DoubleValue spiritSpellPower;
      public final BooleanValue overrideSymmetrySpellPower;
      public final DoubleValue symmetrySpellPower;
      public final BooleanValue overrideAquaSpellPower;
      public final DoubleValue aquaSpellPower;
      public final BooleanValue overrideSpellResist;
      public final DoubleValue spellResist;
      public final BooleanValue overrideHolySpellPower;
      public final DoubleValue holySpellPower;
      public final BooleanValue overrideEldritchSpellPower;
      public final DoubleValue eldritchSpellPower;
      public final BooleanValue overrideSoundSpellPower;
      public final DoubleValue soundSpellPower;
      public final BooleanValue overrideGeoSpellPower;
      public final DoubleValue geoSpellPower;
      public final BooleanValue overrideEvocationSpellPower;
      public final DoubleValue evocationSpellPower;

      public WeaponOverrideConfig(
         BooleanValue enabled,
         BooleanValue showInListings,
         DoubleValue attackDamage,
         DoubleValue attackSpeed,
         DoubleValue entityInteractionRange,
         BooleanValue overrideInfernalFireSpellPower,
         DoubleValue infernalFireSpellPower,
         BooleanValue overrideFireSpellPower,
         DoubleValue fireSpellPower,
         BooleanValue overrideIceSpellPower,
         DoubleValue iceSpellPower,
         BooleanValue overrideLightningSpellPower,
         DoubleValue lightningSpellPower,
         BooleanValue overrideEnderSpellPower,
         DoubleValue enderSpellPower,
         BooleanValue overrideBloodSpellPower,
         DoubleValue bloodSpellPower,
         BooleanValue overrideNatureSpellPower,
         DoubleValue natureSpellPower,
         BooleanValue overrideSpiritSpellPower,
         DoubleValue spiritSpellPower,
         BooleanValue overrideSymmetrySpellPower,
         DoubleValue symmetrySpellPower,
         BooleanValue overrideAquaSpellPower,
         DoubleValue aquaSpellPower,
         BooleanValue overrideSpellResist,
         DoubleValue spellResist,
         BooleanValue overrideHolySpellPower,
         DoubleValue holySpellPower,
         BooleanValue overrideEldritchSpellPower,
         DoubleValue eldritchSpellPower,
         BooleanValue overrideSoundSpellPower,
         DoubleValue soundSpellPower,
         BooleanValue overrideGeoSpellPower,
         DoubleValue geoSpellPower,
         BooleanValue overrideEvocationSpellPower,
         DoubleValue evocationSpellPower
      ) {
         this.enabled = enabled;
         this.showInListings = showInListings;
         this.attackDamage = attackDamage;
         this.attackSpeed = attackSpeed;
         this.entityInteractionRange = entityInteractionRange;
         this.overrideInfernalFireSpellPower = overrideInfernalFireSpellPower;
         this.infernalFireSpellPower = infernalFireSpellPower;
         this.overrideFireSpellPower = overrideFireSpellPower;
         this.fireSpellPower = fireSpellPower;
         this.overrideIceSpellPower = overrideIceSpellPower;
         this.iceSpellPower = iceSpellPower;
         this.overrideLightningSpellPower = overrideLightningSpellPower;
         this.lightningSpellPower = lightningSpellPower;
         this.overrideEnderSpellPower = overrideEnderSpellPower;
         this.enderSpellPower = enderSpellPower;
         this.overrideBloodSpellPower = overrideBloodSpellPower;
         this.bloodSpellPower = bloodSpellPower;
         this.overrideNatureSpellPower = overrideNatureSpellPower;
         this.natureSpellPower = natureSpellPower;
         this.overrideSpiritSpellPower = overrideSpiritSpellPower;
         this.spiritSpellPower = spiritSpellPower;
         this.overrideSymmetrySpellPower = overrideSymmetrySpellPower;
         this.symmetrySpellPower = symmetrySpellPower;
         this.overrideAquaSpellPower = overrideAquaSpellPower;
         this.aquaSpellPower = aquaSpellPower;
         this.overrideSpellResist = overrideSpellResist;
         this.spellResist = spellResist;
         this.overrideHolySpellPower = overrideHolySpellPower;
         this.holySpellPower = holySpellPower;
         this.overrideEldritchSpellPower = overrideEldritchSpellPower;
         this.eldritchSpellPower = eldritchSpellPower;
         this.overrideSoundSpellPower = overrideSoundSpellPower;
         this.soundSpellPower = soundSpellPower;
         this.overrideGeoSpellPower = overrideGeoSpellPower;
         this.geoSpellPower = geoSpellPower;
         this.overrideEvocationSpellPower = overrideEvocationSpellPower;
         this.evocationSpellPower = evocationSpellPower;
      }

      public WeaponBalanceConfig.WeaponOverrideValues read() {
         return new WeaponBalanceConfig.WeaponOverrideValues(
            (Boolean)this.enabled.get(),
            this.showInListings != null ? (Boolean)this.showInListings.get() : true,
            (Double)this.attackDamage.get(),
            (Double)this.attackSpeed.get(),
            (Double)this.entityInteractionRange.get(),
            (Boolean)this.overrideInfernalFireSpellPower.get(),
            (Double)this.infernalFireSpellPower.get(),
            (Boolean)this.overrideFireSpellPower.get(),
            (Double)this.fireSpellPower.get(),
            (Boolean)this.overrideIceSpellPower.get(),
            (Double)this.iceSpellPower.get(),
            (Boolean)this.overrideLightningSpellPower.get(),
            (Double)this.lightningSpellPower.get(),
            (Boolean)this.overrideEnderSpellPower.get(),
            (Double)this.enderSpellPower.get(),
            (Boolean)this.overrideBloodSpellPower.get(),
            (Double)this.bloodSpellPower.get(),
            (Boolean)this.overrideNatureSpellPower.get(),
            (Double)this.natureSpellPower.get(),
            (Boolean)this.overrideSpiritSpellPower.get(),
            (Double)this.spiritSpellPower.get(),
            (Boolean)this.overrideSymmetrySpellPower.get(),
            (Double)this.symmetrySpellPower.get(),
            (Boolean)this.overrideAquaSpellPower.get(),
            (Double)this.aquaSpellPower.get(),
            (Boolean)this.overrideSpellResist.get(),
            (Double)this.spellResist.get(),
            (Boolean)this.overrideHolySpellPower.get(),
            (Double)this.holySpellPower.get(),
            (Boolean)this.overrideEldritchSpellPower.get(),
            (Double)this.eldritchSpellPower.get(),
            (Boolean)this.overrideSoundSpellPower.get(),
            (Double)this.soundSpellPower.get(),
            (Boolean)this.overrideGeoSpellPower.get(),
            (Double)this.geoSpellPower.get(),
            (Boolean)this.overrideEvocationSpellPower.get(),
            (Double)this.evocationSpellPower.get()
         );
      }
   }

   public record WeaponOverrideValues(
      boolean enabled,
      boolean showInListings,
      double attackDamage,
      double attackSpeed,
      double entityInteractionRange,
      boolean overrideInfernalFireSpellPower,
      double infernalFireSpellPower,
      boolean overrideFireSpellPower,
      double fireSpellPower,
      boolean overrideIceSpellPower,
      double iceSpellPower,
      boolean overrideLightningSpellPower,
      double lightningSpellPower,
      boolean overrideEnderSpellPower,
      double enderSpellPower,
      boolean overrideBloodSpellPower,
      double bloodSpellPower,
      boolean overrideNatureSpellPower,
      double natureSpellPower,
      boolean overrideSpiritSpellPower,
      double spiritSpellPower,
      boolean overrideSymmetrySpellPower,
      double symmetrySpellPower,
      boolean overrideAquaSpellPower,
      double aquaSpellPower,
      boolean overrideSpellResist,
      double spellResist,
      boolean overrideHolySpellPower,
      double holySpellPower,
      boolean overrideEldritchSpellPower,
      double eldritchSpellPower,
      boolean overrideSoundSpellPower,
      double soundSpellPower,
      boolean overrideGeoSpellPower,
      double geoSpellPower,
      boolean overrideEvocationSpellPower,
      double evocationSpellPower
   ) {
   }
}
