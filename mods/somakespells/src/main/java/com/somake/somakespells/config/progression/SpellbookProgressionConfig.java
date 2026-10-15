package com.somake.somakespells.config.progression;

import com.mojang.logging.LogUtils;
import com.somake.somakespells.compat.BornInChaosCompat;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import org.slf4j.Logger;

public class SpellbookProgressionConfig {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Builder BUILDER = new Builder();
   private static final Map<String, SpellbookProgressionConfig.FamilyProgressionConfig> FAMILIES = new LinkedHashMap<>();
   public static final SpellbookProgressionConfig.FamilyProgressionConfig BURNING_MAGMA = registerFamily(
      "burning_magma",
      "Burning Magma",
      new SpellbookProgressionConfig.TierProgressionDefaults(120, 50, "minecraft:blaze", 25, "irons_spellbooks:pyromancer", 1),
      new SpellbookProgressionConfig.TierProgressionDefaults(280, 100, "cataclysm:ignited_berserker", 20, "cataclysm:ignis", 1)
   );
   public static final SpellbookProgressionConfig.FamilyProgressionConfig TIDEBOUND = registerFamily(
      "tidebound",
      "Tidebound",
      new SpellbookProgressionConfig.TierProgressionDefaults(120, 50, "minecraft:guardian", 25, "minecraft:elder_guardian", 1),
      new SpellbookProgressionConfig.TierProgressionDefaults(280, 100, "cataclysm:hippocamtus", 20, "cataclysm:scylla", 1)
   );
   public static final SpellbookProgressionConfig.FamilyProgressionConfig STARLESS_GRIMOIRE = registerFamily(
      "starless_grimoire",
      "Starless Grimoire",
      new SpellbookProgressionConfig.TierProgressionDefaults(120, 25, "minecraft:enderman", 5, "minecraft:shulker", 1),
      new SpellbookProgressionConfig.TierProgressionDefaults(280, 60, "minecraft:shulker", 8, "minecraft:warden", 1)
   );
   public static final SpellbookProgressionConfig.FamilyProgressionConfig VOIDBOUND_CODEX = registerFamily(
      "voidbound_codex",
      "Voidbound Codex",
      new SpellbookProgressionConfig.TierProgressionDefaults(120, 50, "minecraft:enderman", 25, "born_in_chaos_v1:phantom_creeper", 1),
      new SpellbookProgressionConfig.TierProgressionDefaults(280, 100, "cataclysm:endermaptera", 20, "cataclysm:ender_guardian", 1)
   );
   public static final SpellbookProgressionConfig.FamilyProgressionConfig INVOKER_CANON = registerFamily(
      "invoker_canon",
      "Invoker Canon",
      new SpellbookProgressionConfig.TierProgressionDefaults(120, 50, "minecraft:pillager", 25, "born_in_chaos_v1:missioner", 5),
      new SpellbookProgressionConfig.TierProgressionDefaults(280, 100, "minecraft:evoker", 20, "born_in_chaos_v1:lifestealer", 5)
   );
   public static final SpellbookProgressionConfig.FamilyProgressionConfig DAWNLIT_GOSPEL = registerFamily(
      "dawnlit_gospel",
      "Dawnlit Gospel",
      new SpellbookProgressionConfig.TierProgressionDefaults(120, 50, "minecraft:zombie", 25, "irons_spellbooks:priest", 1),
      new SpellbookProgressionConfig.TierProgressionDefaults(280, 100, "minecraft:zombie", 50, "irons_spellbooks:priest", 5)
   );
   public static final SpellbookProgressionConfig.FamilyProgressionConfig FROSTVEIN_CODEX = registerFamily(
      "frostvein_codex",
      "Frostvein Codex",
      new SpellbookProgressionConfig.TierProgressionDefaults(120, 20, "minecraft:stray", 5, "minecraft:polar_bear", 1),
      new SpellbookProgressionConfig.TierProgressionDefaults(280, 50, "minecraft:stray", 12, "minecraft:polar_bear", 5)
   );
   public static final SpellbookProgressionConfig.FamilyProgressionConfig STORMCALLER_LEXICON = registerFamily(
      "stormcaller_lexicon",
      "Stormcaller Lexicon",
      new SpellbookProgressionConfig.TierProgressionDefaults(120, 50, "minecraft:creeper", 25, "minecraft:breeze", 5),
      new SpellbookProgressionConfig.TierProgressionDefaults(280, 100, "minecraft:creeper", 50, "cataclysm:scylla", 1)
   );
   public static final SpellbookProgressionConfig.FamilyProgressionConfig VERDANT_TESTAMENT = registerFamily(
      "verdant_testament",
      "Verdant Testament",
      new SpellbookProgressionConfig.TierProgressionDefaults(120, 50, "minecraft:cave_spider", 25, "minecraft:bogged", 1),
      new SpellbookProgressionConfig.TierProgressionDefaults(280, 100, "minecraft:cave_spider", 50, "cataclysm:amethyst_crab", 1)
   );
   public static final SpellbookProgressionConfig.FamilyProgressionConfig SANGUINE_TESTAMENT = registerFamily(
      "sanguine_testament",
      "Sanguine Testament",
      new SpellbookProgressionConfig.TierProgressionDefaults(120, 50, "minecraft:bat", 25, "born_in_chaos_v1:bloody_gadfly", 5),
      new SpellbookProgressionConfig.TierProgressionDefaults(280, 100, "born_in_chaos_v1:bloody_gadfly", 20, "irons_spellbooks:dead_king", 1)
   );
   public static final SpellbookProgressionConfig.FamilyProgressionConfig SOULLIT_TESTAMENT = registerFamily(
      "soullit_testament",
      "Soullit Testament",
      new SpellbookProgressionConfig.TierProgressionDefaults(120, 20, "minecraft:phantom", 5, "minecraft:vex", 1),
      new SpellbookProgressionConfig.TierProgressionDefaults(280, 50, "minecraft:ghast", 8, "minecraft:wither_skeleton", 10)
   );
   public static final SpellbookProgressionConfig.FamilyProgressionConfig AXIOMATIC_CODEX = registerFamily(
      "axiomatic_codex",
      "Axiomatic Codex",
      new SpellbookProgressionConfig.TierProgressionDefaults(120, 25, "minecraft:phantom", 5, "minecraft:enderman", 1),
      new SpellbookProgressionConfig.TierProgressionDefaults(280, 60, "minecraft:enderman", 8, "minecraft:warden", 1)
   );
   public static final SpellbookProgressionConfig.FamilyProgressionConfig STONEHEART_CODEX = registerFamily(
      "stoneheart_codex",
      "Stoneheart Codex",
      new SpellbookProgressionConfig.TierProgressionDefaults(120, 25, "minecraft:iron_golem", 3, "minecraft:ravager", 1),
      new SpellbookProgressionConfig.TierProgressionDefaults(280, 60, "minecraft:iron_golem", 8, "minecraft:ravager", 2)
   );
   public static final SpellbookProgressionConfig.FamilyProgressionConfig RESONANCE_CANTICLE = registerFamily(
      "resonance_canticle",
      "Resonance Canticle",
      new SpellbookProgressionConfig.TierProgressionDefaults(120, 25, "minecraft:bat", 12, "minecraft:warden", 1),
      new SpellbookProgressionConfig.TierProgressionDefaults(280, 60, "minecraft:bat", 32, "minecraft:warden", 1)
   );
   public static final ModConfigSpec SPEC = BUILDER.build();

   private static SpellbookProgressionConfig.FamilyProgressionConfig registerFamily(
      String key,
      String displayName,
      SpellbookProgressionConfig.TierProgressionDefaults tier1Defaults,
      SpellbookProgressionConfig.TierProgressionDefaults tier2Defaults
   ) {
      BUILDER.push(key);
      BUILDER.comment(displayName + " progression");
      SpellbookProgressionConfig.TierProgressionConfig tier1 = registerTier("tier1", "Tier 1 progression", tier1Defaults);
      SpellbookProgressionConfig.TierProgressionConfig tier2 = registerTier("tier2", "Tier 2 progression", tier2Defaults);
      BUILDER.pop();
      SpellbookProgressionConfig.FamilyProgressionConfig config = new SpellbookProgressionConfig.FamilyProgressionConfig(key, tier1, tier2);
      FAMILIES.put(key, config);
      return config;
   }

   private static SpellbookProgressionConfig.TierProgressionConfig registerTier(
      String key, String comment, SpellbookProgressionConfig.TierProgressionDefaults defaults
   ) {
      BUILDER.push(key);
      BUILDER.comment(comment);
      IntValue castsRequired = BUILDER.comment("How many spell casts are required in this tier.")
         .defineInRange("castsRequired", defaults.castsRequired(), 1, Integer.MAX_VALUE);
      IntValue conditionalKillsRequired = BUILDER.comment("How many conditional kills are required in this tier.")
         .defineInRange("conditionalKillsRequired", defaults.conditionalKillsRequired(), 0, Integer.MAX_VALUE);
      ConfigValue<String> targetMobId = BUILDER.comment("Entity ID for the third objective target mob.").define("targetMobId", defaults.targetMobId());
      IntValue targetMobKillsRequired = BUILDER.comment("How many kills are required for the third objective target mob.")
         .defineInRange("targetMobKillsRequired", defaults.targetMobKillsRequired(), 0, Integer.MAX_VALUE);
      ConfigValue<String> finalMobId = BUILDER.comment("Entity ID for the final objective target mob.").define("finalMobId", defaults.finalMobId());
      IntValue finalMobKillsRequired = BUILDER.comment("How many kills are required for the final objective target mob.")
         .defineInRange("finalMobKillsRequired", defaults.finalMobKillsRequired(), 0, Integer.MAX_VALUE);
      BUILDER.pop();
      return new SpellbookProgressionConfig.TierProgressionConfig(
         castsRequired, conditionalKillsRequired, targetMobId, targetMobKillsRequired, finalMobId, finalMobKillsRequired
      );
   }

   public static SpellbookProgressionConfig.FamilyProgressionConfig getFamily(String familyId) {
      return FAMILIES.get(familyId);
   }

   public static void migrateLegacyMobIds() {
      FAMILIES.values().forEach(family -> {
         migrateTier(family.tier1);
         migrateTier(family.tier2);
      });
   }

   public static void validateMobIds() {
      FAMILIES.values().forEach(family -> {
         validateTier(family.key + ".tier1", family.tier1.read());
         validateTier(family.key + ".tier2", family.tier2.read());
      });
   }

   private static void validateTier(String path, SpellbookProgressionConfig.TierProgressionValues values) {
      validateMobId(path + ".targetMobId", values.targetMobId());
      validateMobId(path + ".finalMobId", values.finalMobId());
   }

   private static void validateMobId(String path, String entityId) {
      ResourceLocation id = ResourceLocation.tryParse(entityId);
      if (id == null || !BuiltInRegistries.ENTITY_TYPE.containsKey(id)) {
         LOGGER.error("Invalid spellbook progression mob at {}: {}", path, entityId);
      }
   }

   private static void migrateTier(SpellbookProgressionConfig.TierProgressionConfig tier) {
      String targetMob = (String)tier.targetMobId.get();
      String normalizedTarget = normalizeMobId(targetMob);
      if (!normalizedTarget.equals(targetMob)) {
         tier.targetMobId.set(normalizedTarget);
      }

      String finalMob = (String)tier.finalMobId.get();
      String normalizedFinal = normalizeMobId(finalMob);
      if (!normalizedFinal.equals(finalMob)) {
         tier.finalMobId.set(normalizedFinal);
      }
   }

   private static String resolveMobId(String entityId) {
      String normalizedId = normalizeMobId(entityId);
      if (BornInChaosCompat.isLoaded()) {
         return normalizedId;
      } else {
         return switch (normalizedId) {
            case "born_in_chaos_v1:phantom_creeper_copy" -> "minecraft:phantom";
            case "born_in_chaos_v1:missionary_raider" -> "minecraft:vindicator";
            case "born_in_chaos_v1:lifestealer_true_form" -> "minecraft:ravager";
            case "born_in_chaos_v1:bloody_gadfly" -> "minecraft:cave_spider";
            default -> normalizedId;
         };
      }
   }

   private static String normalizeMobId(String entityId) {
      return switch (entityId) {
         case "born_in_chaos_v1:phantom_creeper" -> "born_in_chaos_v1:phantom_creeper_copy";
         case "born_in_chaos_v1:missioner" -> "born_in_chaos_v1:missionary_raider";
         case "born_in_chaos_v1:lifestealer" -> "born_in_chaos_v1:lifestealer_true_form";
         default -> entityId;
      };
   }

   public static class FamilyProgressionConfig {
      public final String key;
      public final SpellbookProgressionConfig.TierProgressionConfig tier1;
      public final SpellbookProgressionConfig.TierProgressionConfig tier2;

      public FamilyProgressionConfig(String key, SpellbookProgressionConfig.TierProgressionConfig tier1, SpellbookProgressionConfig.TierProgressionConfig tier2) {
         this.key = key;
         this.tier1 = tier1;
         this.tier2 = tier2;
      }

      public SpellbookProgressionConfig.TierProgressionValues tier(int tier) {
         return switch (tier) {
            case 1 -> this.tier1.read();
            case 2 -> this.tier2.read();
            default -> this.tier2.read();
         };
      }
   }

   public static class TierProgressionConfig {
      public final IntValue castsRequired;
      public final IntValue conditionalKillsRequired;
      public final ConfigValue<String> targetMobId;
      public final IntValue targetMobKillsRequired;
      public final ConfigValue<String> finalMobId;
      public final IntValue finalMobKillsRequired;

      public TierProgressionConfig(
         IntValue castsRequired,
         IntValue conditionalKillsRequired,
         ConfigValue<String> targetMobId,
         IntValue targetMobKillsRequired,
         ConfigValue<String> finalMobId,
         IntValue finalMobKillsRequired
      ) {
         this.castsRequired = castsRequired;
         this.conditionalKillsRequired = conditionalKillsRequired;
         this.targetMobId = targetMobId;
         this.targetMobKillsRequired = targetMobKillsRequired;
         this.finalMobId = finalMobId;
         this.finalMobKillsRequired = finalMobKillsRequired;
      }

      public SpellbookProgressionConfig.TierProgressionValues read() {
         return new SpellbookProgressionConfig.TierProgressionValues(
            (Integer)this.castsRequired.get(),
            (Integer)this.conditionalKillsRequired.get(),
            SpellbookProgressionConfig.resolveMobId((String)this.targetMobId.get()),
            (Integer)this.targetMobKillsRequired.get(),
            SpellbookProgressionConfig.resolveMobId((String)this.finalMobId.get()),
            (Integer)this.finalMobKillsRequired.get()
         );
      }
   }

   public record TierProgressionDefaults(
      int castsRequired, int conditionalKillsRequired, String targetMobId, int targetMobKillsRequired, String finalMobId, int finalMobKillsRequired
   ) {
   }

   public record TierProgressionValues(
      int castsRequired, int conditionalKillsRequired, String targetMobId, int targetMobKillsRequired, String finalMobId, int finalMobKillsRequired
   ) {
   }
}
