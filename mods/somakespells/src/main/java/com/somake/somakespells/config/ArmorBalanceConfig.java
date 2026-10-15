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

public class ArmorBalanceConfig {
   private static final Builder BUILDER = new Builder();
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<String, ArmorBalanceConfig.ArmorSetOverrideConfig> ARMOR_SETS = new LinkedHashMap<>();
   private static final Map<String, ArmorBalanceConfig.DynamicArmorPieceEntry> CUSTOM_ARMOR_PIECES = new LinkedHashMap<>();
   public static final ArmorBalanceConfig.ArmorSetOverrideConfig IGNITIUM = registerArmorSet(
      "cataclysm:ignitium", "cataclysm", "ignitium", legacyIgnitiumDefaults()
   );
   public static final ArmorBalanceConfig.ArmorSetOverrideConfig CURSIUM = registerArmorSet(
      "cataclysm:cursium", "cataclysm", "cursium", legacyCursiumDefaults()
   );
   public static final ArmorBalanceConfig.ArmorSetOverrideConfig CERANIUM = registerArmorSet("somakespells:ceranium", "somake", "ceranium", ceraniumDefaults());
   public static final ArmorBalanceConfig.ArmorSetOverrideConfig ABYSSIUM = registerArmorSet("somakespells:abyssium", "somake", "abyssium", abyssiumDefaults());
   public static final ArmorBalanceConfig.ArmorSetOverrideConfig DARK_METAL_BATTLEMAGE = registerArmorSet(
      "somakespells:dark_metal_battlemage", "somake", "darkMetalBattlemage", darkMetalBattlemageDefaults()
   );
   public static final ArmorBalanceConfig.ArmorSetOverrideConfig DARK_METAL_ARMOR = registerArmorSet(
      "born_in_chaos_v1:dark_metal_armor", "bornInChaos", "darkMetalArmor", darkMetalArmorDefaults()
   );
   public static final ArmorBalanceConfig.ArmorSetOverrideConfig GEOMANCER = registerArmorSet(
      "mowziesmobs:geomancer", "mowziesmobs", "geomancer", darkMetalBattleMageDefaults(ArmorBalanceConfig.ElementSchool.GEO)
   );
   public static final ArmorBalanceConfig.ArmorSetOverrideConfig CULTIST = registerArmorSet(
      "irons_spellbooks:cultist", "ironsSpellbooks", "cultist", darkMetalBattleMageDefaults(ArmorBalanceConfig.ElementSchool.BLOOD)
   );
   public static final ArmorBalanceConfig.ArmorSetOverrideConfig CRYOMANCER = registerArmorSet(
      "irons_spellbooks:cryomancer", "ironsSpellbooks", "cryomancer", darkMetalBattleMageDefaults(ArmorBalanceConfig.ElementSchool.ICE)
   );
   public static final ArmorBalanceConfig.ArmorSetOverrideConfig ELECTROMANCER = registerArmorSet(
      "irons_spellbooks:electromancer", "ironsSpellbooks", "electromancer", darkMetalBattleMageDefaults(ArmorBalanceConfig.ElementSchool.LIGHTNING)
   );
   public static final ArmorBalanceConfig.ArmorSetOverrideConfig PYROMANCER = registerArmorSet(
      "irons_spellbooks:pyromancer", "ironsSpellbooks", "pyromancer", darkMetalBattleMageDefaults(ArmorBalanceConfig.ElementSchool.FIRE)
   );
   public static final ArmorBalanceConfig.ArmorSetOverrideConfig PRIEST = registerArmorSet(
      "irons_spellbooks:priest", "ironsSpellbooks", "priest", darkMetalBattleMageDefaults(ArmorBalanceConfig.ElementSchool.HOLY)
   );
   public static final ArmorBalanceConfig.ArmorSetOverrideConfig PLAGUED = registerArmorSet(
      "irons_spellbooks:plagued", "ironsSpellbooks", "plagued", darkMetalBattleMageDefaults(ArmorBalanceConfig.ElementSchool.NATURE)
   );
   public static final ArmorBalanceConfig.ArmorSetOverrideConfig ONMYOJI = registerArmorSet(
      "iss_magicfromtheeast:onmyoji", "magicFromTheEast", "onmyoji", darkMetalBattleMageDefaults(ArmorBalanceConfig.ElementSchool.SPIRIT)
   );
   public static final ArmorBalanceConfig.ArmorSetOverrideConfig TAOIST = registerArmorSet(
      "iss_magicfromtheeast:taoist", "magicFromTheEast", "taoist", darkMetalBattleMageDefaults(ArmorBalanceConfig.ElementSchool.SYMMETRY)
   );
   public static final ArmorBalanceConfig.ArmorSetOverrideConfig ARCHEVOKER = registerArmorSet(
      "irons_spellbooks:archevoker", "ironsSpellbooks", "archevoker", darkMetalBattleMageDefaults(ArmorBalanceConfig.ElementSchool.EVOCATION)
   );
   public static final ArmorBalanceConfig.ArmorSetOverrideConfig SHADOWWALKER = registerArmorSet(
      "irons_spellbooks:shadowwalker", "ironsSpellbooks", "shadowwalker", darkMetalBattleMageDefaults(ArmorBalanceConfig.ElementSchool.ENDER)
   );
   public static final ArmorBalanceConfig.ArmorSetOverrideConfig AQUAMANCER = registerArmorSet(
      "somakespells:aquamancer", "somake", "aquamancer", darkMetalBattleMageDefaults(ArmorBalanceConfig.ElementSchool.AQUA)
   );
   public static final ArmorBalanceConfig.ArmorSetOverrideConfig BARD = registerArmorSet(
      "tunes_n_tomes:bard", "familiars", "bard", darkMetalBattleMageDefaults(ArmorBalanceConfig.ElementSchool.SOUND)
   );
   public static final ModConfigSpec SPEC;

   private static void registerCustomArmorPieceSlot(int index) {
      ArmorBalanceConfig.ArmorDefaults defaults = emptyDefaults();
      BUILDER.push("armorBalance");
      BUILDER.push("customPieces");
      BUILDER.push("entry" + index);
      ConfigValue<String> itemId = BUILDER.comment("Armor piece item ID to override, for example 'othermod:mystic_helmet'. Leave empty to disable this slot.")
         .define("itemId", "");
      BooleanValue enabled = BUILDER.comment("If true, overrides this armor piece attributes. If false, keeps original mod defaults.")
         .define("enabled", defaults.enabled);
      BooleanValue overrideArmor = BUILDER.define("overrideArmor", defaults.overrideArmor);
      DoubleValue armorValue = BUILDER.defineInRange("armorValue", defaults.armorHelmet, -1024.0, 1024.0);
      BooleanValue overrideArmorToughness = BUILDER.define("overrideArmorToughness", defaults.overrideArmorToughness);
      DoubleValue armorToughness = BUILDER.defineInRange("armorToughness", defaults.armorToughness, -1024.0, 1024.0);
      BooleanValue overrideMaxMana = BUILDER.define("overrideMaxMana", defaults.overrideMaxMana);
      DoubleValue maxMana = BUILDER.defineInRange("maxMana", defaults.maxMana, -100000.0, 100000.0);
      BooleanValue overrideSpellPower = BUILDER.define("overrideSpellPower", defaults.overrideSpellPower);
      DoubleValue spellPower = BUILDER.defineInRange("spellPower", defaults.spellPower, -100.0, 100.0);
      BooleanValue overrideFireSpellPower = BUILDER.define("overrideFireSpellPower", defaults.overrideFireSpellPower);
      DoubleValue fireSpellPower = BUILDER.defineInRange("fireSpellPower", defaults.fireSpellPower, -100.0, 100.0);
      BooleanValue overrideIceSpellPower = BUILDER.define("overrideIceSpellPower", defaults.overrideIceSpellPower);
      DoubleValue iceSpellPower = BUILDER.defineInRange("iceSpellPower", defaults.iceSpellPower, -100.0, 100.0);
      BooleanValue overrideLightningSpellPower = BUILDER.define("overrideLightningSpellPower", defaults.overrideLightningSpellPower);
      DoubleValue lightningSpellPower = BUILDER.defineInRange("lightningSpellPower", defaults.lightningSpellPower, -100.0, 100.0);
      BooleanValue overrideEnderSpellPower = BUILDER.define("overrideEnderSpellPower", defaults.overrideEnderSpellPower);
      DoubleValue enderSpellPower = BUILDER.defineInRange("enderSpellPower", defaults.enderSpellPower, -100.0, 100.0);
      BooleanValue overrideBloodSpellPower = BUILDER.define("overrideBloodSpellPower", defaults.overrideBloodSpellPower);
      DoubleValue bloodSpellPower = BUILDER.defineInRange("bloodSpellPower", defaults.bloodSpellPower, -100.0, 100.0);
      BooleanValue overrideNatureSpellPower = BUILDER.define("overrideNatureSpellPower", defaults.overrideNatureSpellPower);
      DoubleValue natureSpellPower = BUILDER.defineInRange("natureSpellPower", defaults.natureSpellPower, -100.0, 100.0);
      BooleanValue overrideSpiritSpellPower = BUILDER.define("overrideSpiritSpellPower", defaults.overrideSpiritSpellPower);
      DoubleValue spiritSpellPower = BUILDER.defineInRange("spiritSpellPower", defaults.spiritSpellPower, -100.0, 100.0);
      BooleanValue overrideSymmetrySpellPower = BUILDER.define("overrideSymmetrySpellPower", defaults.overrideSymmetrySpellPower);
      DoubleValue symmetrySpellPower = BUILDER.defineInRange("symmetrySpellPower", defaults.symmetrySpellPower, -100.0, 100.0);
      BooleanValue overrideAquaSpellPower = BUILDER.define("overrideAquaSpellPower", defaults.overrideAquaSpellPower);
      DoubleValue aquaSpellPower = BUILDER.defineInRange("aquaSpellPower", defaults.aquaSpellPower, -100.0, 100.0);
      BooleanValue overrideHolySpellPower = BUILDER.define("overrideHolySpellPower", defaults.overrideHolySpellPower);
      DoubleValue holySpellPower = BUILDER.defineInRange("holySpellPower", defaults.holySpellPower, -100.0, 100.0);
      BooleanValue overrideEvocationSpellPower = BUILDER.define("overrideEvocationSpellPower", defaults.overrideEvocationSpellPower);
      DoubleValue evocationSpellPower = BUILDER.defineInRange("evocationSpellPower", defaults.evocationSpellPower, -100.0, 100.0);
      BooleanValue overrideSoundSpellPower = BUILDER.define("overrideSoundSpellPower", defaults.overrideSoundSpellPower);
      DoubleValue soundSpellPower = BUILDER.defineInRange("soundSpellPower", defaults.soundSpellPower, -100.0, 100.0);
      BooleanValue overrideGeoSpellPower = BUILDER.define("overrideGeoSpellPower", defaults.overrideGeoSpellPower);
      DoubleValue geoSpellPower = BUILDER.defineInRange("geoSpellPower", defaults.geoSpellPower, -100.0, 100.0);
      CUSTOM_ARMOR_PIECES.put(
         "entry" + index,
         new ArmorBalanceConfig.DynamicArmorPieceEntry(
            itemId,
            enabled,
            overrideArmor,
            armorValue,
            overrideArmorToughness,
            armorToughness,
            overrideMaxMana,
            maxMana,
            overrideSpellPower,
            spellPower,
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
            overrideHolySpellPower,
            holySpellPower,
            overrideEvocationSpellPower,
            evocationSpellPower,
            overrideSoundSpellPower,
            soundSpellPower,
            overrideGeoSpellPower,
            geoSpellPower
         )
      );
      BUILDER.pop();
      BUILDER.pop();
      BUILDER.pop();
   }

   private static ArmorBalanceConfig.ArmorSetOverrideConfig registerArmorSet(
      String armorKey, String category, String key, ArmorBalanceConfig.ArmorDefaults defaults
   ) {
      BUILDER.push("armorBalance");
      BUILDER.push(category);
      BUILDER.push(key);
      BooleanValue enabled = BUILDER.comment("If true, overrides this armor set attributes. If false, keeps original mod defaults.")
         .define("enabled", defaults.enabled);
      BooleanValue overrideArmor = BUILDER.comment("If true, overrides base armor points for each armor piece.")
         .define("overrideArmor", defaults.overrideArmor);
      DoubleValue armorHelmet = BUILDER.comment("Final armor value for helmet piece.").defineInRange("armorHelmet", defaults.armorHelmet, -1024.0, 1024.0);
      DoubleValue armorChestplate = BUILDER.comment("Final armor value for chestplate piece.")
         .defineInRange("armorChestplate", defaults.armorChestplate, -1024.0, 1024.0);
      DoubleValue armorLeggings = BUILDER.comment("Final armor value for leggings piece.")
         .defineInRange("armorLeggings", defaults.armorLeggings, -1024.0, 1024.0);
      DoubleValue armorBoots = BUILDER.comment("Final armor value for boots piece.").defineInRange("armorBoots", defaults.armorBoots, -1024.0, 1024.0);
      BooleanValue overrideArmorToughness = BUILDER.comment("If true, overrides armor toughness.")
         .define("overrideArmorToughness", defaults.overrideArmorToughness);
      DoubleValue armorToughness = BUILDER.comment("Final armor toughness value for all pieces.")
         .defineInRange("armorToughness", defaults.armorToughness, -1024.0, 1024.0);
      BooleanValue overrideMaxMana = BUILDER.comment("If true, overrides max mana bonus.").define("overrideMaxMana", defaults.overrideMaxMana);
      DoubleValue maxMana = BUILDER.comment("Max mana bonus per armor piece.").defineInRange("maxMana", defaults.maxMana, -100000.0, 100000.0);
      BooleanValue overrideSpellPower = BUILDER.comment("If true, overrides generic spell power bonus.")
         .define("overrideSpellPower", defaults.overrideSpellPower);
      DoubleValue spellPower = BUILDER.comment("Generic Spell Power bonus as decimal (0.05 = 5%).")
         .defineInRange("spellPower", defaults.spellPower, -100.0, 100.0);
      BooleanValue overrideFireSpellPower = BUILDER.define("overrideFireSpellPower", defaults.overrideFireSpellPower);
      DoubleValue cfgFireSpellPower = BUILDER.comment("Fire Spell Power bonus as decimal (0.20 = 20%).")
         .defineInRange("fireSpellPower", defaults.fireSpellPower, -100.0, 100.0);
      BooleanValue overrideIceSpellPower = BUILDER.define("overrideIceSpellPower", defaults.overrideIceSpellPower);
      DoubleValue cfgIceSpellPower = BUILDER.comment("Ice Spell Power bonus as decimal (0.10 = 10%).")
         .defineInRange("iceSpellPower", defaults.iceSpellPower, -100.0, 100.0);
      BooleanValue overrideLightningSpellPower = BUILDER.define("overrideLightningSpellPower", defaults.overrideLightningSpellPower);
      DoubleValue cfgLightningSpellPower = BUILDER.comment("Lightning Spell Power bonus as decimal (0.10 = 10%).")
         .defineInRange("lightningSpellPower", defaults.lightningSpellPower, -100.0, 100.0);
      BooleanValue overrideEnderSpellPower = BUILDER.define("overrideEnderSpellPower", defaults.overrideEnderSpellPower);
      DoubleValue cfgEnderSpellPower = BUILDER.comment("Ender Spell Power bonus as decimal (0.10 = 10%).")
         .defineInRange("enderSpellPower", defaults.enderSpellPower, -100.0, 100.0);
      BooleanValue overrideBloodSpellPower = BUILDER.define("overrideBloodSpellPower", defaults.overrideBloodSpellPower);
      DoubleValue cfgBloodSpellPower = BUILDER.comment("Blood Spell Power bonus as decimal (0.20 = 20%).")
         .defineInRange("bloodSpellPower", defaults.bloodSpellPower, -100.0, 100.0);
      BooleanValue overrideNatureSpellPower = BUILDER.define("overrideNatureSpellPower", defaults.overrideNatureSpellPower);
      DoubleValue cfgNatureSpellPower = BUILDER.comment("Nature Spell Power bonus as decimal (0.10 = 10%).")
         .defineInRange("natureSpellPower", defaults.natureSpellPower, -100.0, 100.0);
      BooleanValue overrideSpiritSpellPower = BUILDER.define("overrideSpiritSpellPower", defaults.overrideSpiritSpellPower);
      DoubleValue cfgSpiritSpellPower = BUILDER.comment("Spirit Spell Power bonus as decimal (0.10 = 10%).")
         .defineInRange("spiritSpellPower", defaults.spiritSpellPower, -100.0, 100.0);
      BooleanValue overrideSymmetrySpellPower = BUILDER.define("overrideSymmetrySpellPower", defaults.overrideSymmetrySpellPower);
      DoubleValue cfgSymmetrySpellPower = BUILDER.comment("Symmetry Spell Power bonus as decimal (0.10 = 10%).")
         .defineInRange("symmetrySpellPower", defaults.symmetrySpellPower, -100.0, 100.0);
      BooleanValue overrideAquaSpellPower = BUILDER.define("overrideAquaSpellPower", defaults.overrideAquaSpellPower);
      DoubleValue cfgAquaSpellPower = BUILDER.comment("Aqua Spell Power bonus as decimal (0.10 = 10%).")
         .defineInRange("aquaSpellPower", defaults.aquaSpellPower, -100.0, 100.0);
      BooleanValue overrideHolySpellPower = BUILDER.define("overrideHolySpellPower", defaults.overrideHolySpellPower);
      DoubleValue cfgHolySpellPower = BUILDER.comment("Holy Spell Power bonus as decimal (0.10 = 10%).")
         .defineInRange("holySpellPower", defaults.holySpellPower, -100.0, 100.0);
      BooleanValue overrideEvocationSpellPower = BUILDER.define("overrideEvocationSpellPower", defaults.overrideEvocationSpellPower);
      DoubleValue cfgEvocationSpellPower = BUILDER.comment("Evocation Spell Power bonus as decimal (0.10 = 10%).")
         .defineInRange("evocationSpellPower", defaults.evocationSpellPower, -100.0, 100.0);
      BooleanValue overrideSoundSpellPower = BUILDER.define("overrideSoundSpellPower", defaults.overrideSoundSpellPower);
      DoubleValue cfgSoundSpellPower = BUILDER.comment("Sound Spell Power bonus as decimal (0.10 = 10%).")
         .defineInRange("soundSpellPower", defaults.soundSpellPower, -100.0, 100.0);
      BooleanValue overrideGeoSpellPower = BUILDER.define("overrideGeoSpellPower", defaults.overrideGeoSpellPower);
      DoubleValue cfgGeoSpellPower = BUILDER.comment("Geo Spell Power bonus as decimal (0.10 = 10%).")
         .defineInRange("geoSpellPower", defaults.geoSpellPower, -100.0, 100.0);
      BUILDER.pop();
      BUILDER.pop();
      BUILDER.pop();
      ArmorBalanceConfig.ArmorSetOverrideConfig config = new ArmorBalanceConfig.ArmorSetOverrideConfig(
         enabled,
         overrideArmor,
         armorHelmet,
         armorChestplate,
         armorLeggings,
         armorBoots,
         overrideArmorToughness,
         armorToughness,
         overrideMaxMana,
         maxMana,
         overrideSpellPower,
         spellPower,
         overrideFireSpellPower,
         cfgFireSpellPower,
         overrideIceSpellPower,
         cfgIceSpellPower,
         overrideLightningSpellPower,
         cfgLightningSpellPower,
         overrideEnderSpellPower,
         cfgEnderSpellPower,
         overrideBloodSpellPower,
         cfgBloodSpellPower,
         overrideNatureSpellPower,
         cfgNatureSpellPower,
         overrideSpiritSpellPower,
         cfgSpiritSpellPower,
         overrideSymmetrySpellPower,
         cfgSymmetrySpellPower,
         overrideAquaSpellPower,
         cfgAquaSpellPower,
         overrideHolySpellPower,
         cfgHolySpellPower,
         overrideEvocationSpellPower,
         cfgEvocationSpellPower,
         overrideSoundSpellPower,
         cfgSoundSpellPower,
         overrideGeoSpellPower,
         cfgGeoSpellPower
      );
      ARMOR_SETS.put(armorKey, config);
      return config;
   }

   public static ArmorBalanceConfig.ArmorSetOverrideValues getIgnitium() {
      return IGNITIUM.read();
   }

   public static ArmorBalanceConfig.ArmorSetOverrideValues getCursium() {
      return CURSIUM.read();
   }

   public static ArmorBalanceConfig.ArmorSetOverrideValues getLocalArmorOverride(String armorKey) {
      ArmorBalanceConfig.ArmorSetOverrideConfig config = ARMOR_SETS.get(armorKey);
      return config == null ? null : config.read();
   }

   public static Map<String, ArmorBalanceConfig.ArmorSetOverrideValues> getLocalArmorOverrideSnapshot() {
      Map<String, ArmorBalanceConfig.ArmorSetOverrideValues> snapshot = new LinkedHashMap<>();
      ARMOR_SETS.forEach((armorKey, config) -> snapshot.put(armorKey, config.read()));
      return snapshot;
   }

   public static Map<String, ArmorBalanceConfig.DynamicArmorPieceValues> getLocalCustomArmorPieceSnapshot() {
      Map<String, ArmorBalanceConfig.DynamicArmorPieceValues> snapshot = new LinkedHashMap<>();
      CUSTOM_ARMOR_PIECES.forEach((entryKey, entry) -> {
         ResourceLocation itemId = entry.parseItemId();
         if (itemId != null) {
            if (snapshot.containsKey(itemId.toString())) {
               LOGGER.warn("Custom armor config '{}' duplicates existing custom armor id '{}'; keeping latest values for sync snapshot.", entryKey, itemId);
            }

            snapshot.put(itemId.toString(), entry.read());
         }
      });
      return snapshot;
   }

   public static ArmorBalanceConfig.DynamicArmorPieceValues getLocalCustomArmorPieceOverride(ResourceLocation itemId) {
      for (ArmorBalanceConfig.DynamicArmorPieceEntry entry : CUSTOM_ARMOR_PIECES.values()) {
         ResourceLocation configuredId = entry.parseItemId();
         if (configuredId != null && configuredId.equals(itemId)) {
            return entry.read();
         }
      }

      return null;
   }

   private static ArmorBalanceConfig.ArmorDefaults legacyIgnitiumDefaults() {
      ArmorBalanceConfig.ArmorDefaults defaults = emptyDefaults();
      defaults.enabled = true;
      defaults.overrideMaxMana = true;
      defaults.maxMana = 200.0;
      defaults.overrideSpellPower = true;
      defaults.spellPower = 0.05;
      defaults.overrideFireSpellPower = true;
      defaults.fireSpellPower = 0.2;
      return defaults;
   }

   private static ArmorBalanceConfig.ArmorDefaults legacyCursiumDefaults() {
      ArmorBalanceConfig.ArmorDefaults defaults = emptyDefaults();
      defaults.enabled = true;
      defaults.overrideMaxMana = true;
      defaults.maxMana = 200.0;
      defaults.overrideSpellPower = true;
      defaults.spellPower = 0.05;
      defaults.overrideIceSpellPower = true;
      defaults.iceSpellPower = 0.1;
      defaults.overrideSymmetrySpellPower = true;
      defaults.symmetrySpellPower = 0.1;
      return defaults;
   }

   private static ArmorBalanceConfig.ArmorDefaults legacyCrystallizedBloodDefaults() {
      ArmorBalanceConfig.ArmorDefaults defaults = emptyDefaults();
      defaults.enabled = true;
      defaults.overrideMaxMana = true;
      defaults.maxMana = 200.0;
      defaults.overrideSpellPower = true;
      defaults.spellPower = 0.05;
      defaults.overrideBloodSpellPower = true;
      defaults.bloodSpellPower = 0.2;
      return defaults;
   }

   private static ArmorBalanceConfig.ArmorDefaults ceraniumDefaults() {
      ArmorBalanceConfig.ArmorDefaults defaults = emptyDefaults();
      defaults.enabled = true;
      defaults.overrideMaxMana = true;
      defaults.maxMana = 200.0;
      defaults.overrideSpellPower = true;
      defaults.spellPower = 0.05;
      defaults.overrideLightningSpellPower = true;
      defaults.lightningSpellPower = 0.1;
      defaults.overrideAquaSpellPower = true;
      defaults.aquaSpellPower = 0.1;
      return defaults;
   }

   private static ArmorBalanceConfig.ArmorDefaults abyssiumDefaults() {
      ArmorBalanceConfig.ArmorDefaults defaults = emptyDefaults();
      defaults.enabled = true;
      defaults.overrideMaxMana = true;
      defaults.maxMana = 200.0;
      defaults.overrideSpellPower = true;
      defaults.spellPower = 0.05;
      defaults.overrideEnderSpellPower = true;
      defaults.enderSpellPower = 0.1;
      defaults.overrideAquaSpellPower = true;
      defaults.aquaSpellPower = 0.1;
      return defaults;
   }

   private static ArmorBalanceConfig.ArmorDefaults darkMetalBattlemageDefaults() {
      ArmorBalanceConfig.ArmorDefaults defaults = emptyDefaults();
      defaults.enabled = true;
      defaults.overrideMaxMana = true;
      defaults.maxMana = 125.0;
      return defaults;
   }

   private static ArmorBalanceConfig.ArmorDefaults darkMetalArmorDefaults() {
      ArmorBalanceConfig.ArmorDefaults defaults = emptyDefaults();
      defaults.enabled = true;
      defaults.overrideArmor = true;
      defaults.armorHelmet = 4.0;
      defaults.armorChestplate = 10.0;
      defaults.armorLeggings = 7.0;
      defaults.armorBoots = 3.0;
      defaults.overrideArmorToughness = true;
      defaults.armorToughness = 5.0;
      return defaults;
   }

   private static ArmorBalanceConfig.ArmorDefaults darkMetalBattleMageDefaults(ArmorBalanceConfig.ElementSchool elementSchool) {
      ArmorBalanceConfig.ArmorDefaults defaults = emptyDefaults();
      defaults.enabled = true;
      defaults.overrideArmor = true;
      defaults.armorHelmet = 4.0;
      defaults.armorChestplate = 10.0;
      defaults.armorLeggings = 7.0;
      defaults.armorBoots = 3.0;
      defaults.overrideArmorToughness = true;
      defaults.armorToughness = 5.0;
      defaults.overrideMaxMana = true;
      defaults.maxMana = 125.0;
      defaults.overrideSpellPower = true;
      defaults.spellPower = 0.05;
      applyElementDefault(defaults, elementSchool, 0.1);
      return defaults;
   }

   private static void applyElementDefault(ArmorBalanceConfig.ArmorDefaults defaults, ArmorBalanceConfig.ElementSchool elementSchool, double value) {
      switch (elementSchool) {
         case FIRE:
            defaults.overrideFireSpellPower = true;
            defaults.fireSpellPower = value;
            break;
         case ICE:
            defaults.overrideIceSpellPower = true;
            defaults.iceSpellPower = value;
            break;
         case LIGHTNING:
            defaults.overrideLightningSpellPower = true;
            defaults.lightningSpellPower = value;
            break;
         case ENDER:
            defaults.overrideEnderSpellPower = true;
            defaults.enderSpellPower = value;
            break;
         case BLOOD:
            defaults.overrideBloodSpellPower = true;
            defaults.bloodSpellPower = value;
            break;
         case NATURE:
            defaults.overrideNatureSpellPower = true;
            defaults.natureSpellPower = value;
            break;
         case SPIRIT:
            defaults.overrideSpiritSpellPower = true;
            defaults.spiritSpellPower = value;
            break;
         case SYMMETRY:
            defaults.overrideSymmetrySpellPower = true;
            defaults.symmetrySpellPower = value;
            break;
         case AQUA:
            defaults.overrideAquaSpellPower = true;
            defaults.aquaSpellPower = value;
            break;
         case HOLY:
            defaults.overrideHolySpellPower = true;
            defaults.holySpellPower = value;
            break;
         case EVOCATION:
            defaults.overrideEvocationSpellPower = true;
            defaults.evocationSpellPower = value;
            break;
         case SOUND:
            defaults.overrideSoundSpellPower = true;
            defaults.soundSpellPower = value;
            break;
         case GEO:
            defaults.overrideGeoSpellPower = true;
            defaults.geoSpellPower = value;
      }
   }

   private static ArmorBalanceConfig.ArmorDefaults emptyDefaults() {
      ArmorBalanceConfig.ArmorDefaults defaults = new ArmorBalanceConfig.ArmorDefaults();
      defaults.armorHelmet = 0.0;
      defaults.armorChestplate = 0.0;
      defaults.armorLeggings = 0.0;
      defaults.armorBoots = 0.0;
      defaults.armorToughness = 0.0;
      defaults.maxMana = 0.0;
      defaults.spellPower = 0.0;
      defaults.fireSpellPower = 0.0;
      defaults.iceSpellPower = 0.0;
      defaults.lightningSpellPower = 0.0;
      defaults.enderSpellPower = 0.0;
      defaults.bloodSpellPower = 0.0;
      defaults.natureSpellPower = 0.0;
      defaults.spiritSpellPower = 0.0;
      defaults.symmetrySpellPower = 0.0;
      defaults.aquaSpellPower = 0.0;
      defaults.holySpellPower = 0.0;
      defaults.evocationSpellPower = 0.0;
      defaults.soundSpellPower = 0.0;
      defaults.geoSpellPower = 0.0;
      return defaults;
   }

   static {
      for (int i = 1; i <= 24; i++) {
         registerCustomArmorPieceSlot(i);
      }

      SPEC = BUILDER.build();
   }

   private static class ArmorDefaults {
      boolean enabled;
      boolean overrideArmor;
      double armorHelmet;
      double armorChestplate;
      double armorLeggings;
      double armorBoots;
      boolean overrideArmorToughness;
      double armorToughness;
      boolean overrideMaxMana;
      double maxMana;
      boolean overrideSpellPower;
      double spellPower;
      boolean overrideFireSpellPower;
      double fireSpellPower;
      boolean overrideIceSpellPower;
      double iceSpellPower;
      boolean overrideLightningSpellPower;
      double lightningSpellPower;
      boolean overrideEnderSpellPower;
      double enderSpellPower;
      boolean overrideBloodSpellPower;
      double bloodSpellPower;
      boolean overrideNatureSpellPower;
      double natureSpellPower;
      boolean overrideSpiritSpellPower;
      double spiritSpellPower;
      boolean overrideSymmetrySpellPower;
      double symmetrySpellPower;
      boolean overrideAquaSpellPower;
      double aquaSpellPower;
      boolean overrideHolySpellPower;
      double holySpellPower;
      boolean overrideEvocationSpellPower;
      double evocationSpellPower;
      boolean overrideSoundSpellPower;
      double soundSpellPower;
      boolean overrideGeoSpellPower;
      double geoSpellPower;
   }

   public static class ArmorSetOverrideConfig {
      public final BooleanValue enabled;
      public final BooleanValue overrideArmor;
      public final DoubleValue armorHelmet;
      public final DoubleValue armorChestplate;
      public final DoubleValue armorLeggings;
      public final DoubleValue armorBoots;
      public final BooleanValue overrideArmorToughness;
      public final DoubleValue armorToughness;
      public final BooleanValue overrideMaxMana;
      public final DoubleValue maxMana;
      public final BooleanValue overrideSpellPower;
      public final DoubleValue spellPower;
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
      public final BooleanValue overrideHolySpellPower;
      public final DoubleValue holySpellPower;
      public final BooleanValue overrideEvocationSpellPower;
      public final DoubleValue evocationSpellPower;
      public final BooleanValue overrideSoundSpellPower;
      public final DoubleValue soundSpellPower;
      public final BooleanValue overrideGeoSpellPower;
      public final DoubleValue geoSpellPower;

      public ArmorSetOverrideConfig(
         BooleanValue enabled,
         BooleanValue overrideArmor,
         DoubleValue armorHelmet,
         DoubleValue armorChestplate,
         DoubleValue armorLeggings,
         DoubleValue armorBoots,
         BooleanValue overrideArmorToughness,
         DoubleValue armorToughness,
         BooleanValue overrideMaxMana,
         DoubleValue maxMana,
         BooleanValue overrideSpellPower,
         DoubleValue spellPower,
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
         BooleanValue overrideHolySpellPower,
         DoubleValue holySpellPower,
         BooleanValue overrideEvocationSpellPower,
         DoubleValue evocationSpellPower,
         BooleanValue overrideSoundSpellPower,
         DoubleValue soundSpellPower,
         BooleanValue overrideGeoSpellPower,
         DoubleValue geoSpellPower
      ) {
         this.enabled = enabled;
         this.overrideArmor = overrideArmor;
         this.armorHelmet = armorHelmet;
         this.armorChestplate = armorChestplate;
         this.armorLeggings = armorLeggings;
         this.armorBoots = armorBoots;
         this.overrideArmorToughness = overrideArmorToughness;
         this.armorToughness = armorToughness;
         this.overrideMaxMana = overrideMaxMana;
         this.maxMana = maxMana;
         this.overrideSpellPower = overrideSpellPower;
         this.spellPower = spellPower;
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
         this.overrideHolySpellPower = overrideHolySpellPower;
         this.holySpellPower = holySpellPower;
         this.overrideEvocationSpellPower = overrideEvocationSpellPower;
         this.evocationSpellPower = evocationSpellPower;
         this.overrideSoundSpellPower = overrideSoundSpellPower;
         this.soundSpellPower = soundSpellPower;
         this.overrideGeoSpellPower = overrideGeoSpellPower;
         this.geoSpellPower = geoSpellPower;
      }

      public ArmorBalanceConfig.ArmorSetOverrideValues read() {
         return new ArmorBalanceConfig.ArmorSetOverrideValues(
            (Boolean)this.enabled.get(),
            (Boolean)this.overrideArmor.get(),
            (Double)this.armorHelmet.get(),
            (Double)this.armorChestplate.get(),
            (Double)this.armorLeggings.get(),
            (Double)this.armorBoots.get(),
            (Boolean)this.overrideArmorToughness.get(),
            (Double)this.armorToughness.get(),
            (Boolean)this.overrideMaxMana.get(),
            (Double)this.maxMana.get(),
            (Boolean)this.overrideSpellPower.get(),
            (Double)this.spellPower.get(),
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
            (Boolean)this.overrideHolySpellPower.get(),
            (Double)this.holySpellPower.get(),
            (Boolean)this.overrideEvocationSpellPower.get(),
            (Double)this.evocationSpellPower.get(),
            (Boolean)this.overrideSoundSpellPower.get(),
            (Double)this.soundSpellPower.get(),
            (Boolean)this.overrideGeoSpellPower.get(),
            (Double)this.geoSpellPower.get()
         );
      }
   }

   public record ArmorSetOverrideValues(
      boolean enabled,
      boolean overrideArmor,
      double armorHelmet,
      double armorChestplate,
      double armorLeggings,
      double armorBoots,
      boolean overrideArmorToughness,
      double armorToughness,
      boolean overrideMaxMana,
      double maxMana,
      boolean overrideSpellPower,
      double spellPower,
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
      boolean overrideHolySpellPower,
      double holySpellPower,
      boolean overrideEvocationSpellPower,
      double evocationSpellPower,
      boolean overrideSoundSpellPower,
      double soundSpellPower,
      boolean overrideGeoSpellPower,
      double geoSpellPower
   ) {
   }

   private record DynamicArmorPieceEntry(
      ConfigValue<String> itemId,
      BooleanValue enabled,
      BooleanValue overrideArmor,
      DoubleValue armorValue,
      BooleanValue overrideArmorToughness,
      DoubleValue armorToughness,
      BooleanValue overrideMaxMana,
      DoubleValue maxMana,
      BooleanValue overrideSpellPower,
      DoubleValue spellPower,
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
      BooleanValue overrideHolySpellPower,
      DoubleValue holySpellPower,
      BooleanValue overrideEvocationSpellPower,
      DoubleValue evocationSpellPower,
      BooleanValue overrideSoundSpellPower,
      DoubleValue soundSpellPower,
      BooleanValue overrideGeoSpellPower,
      DoubleValue geoSpellPower
   ) {
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

      private ArmorBalanceConfig.DynamicArmorPieceValues read() {
         return new ArmorBalanceConfig.DynamicArmorPieceValues(
            (Boolean)this.enabled.get(),
            (Boolean)this.overrideArmor.get(),
            (Double)this.armorValue.get(),
            (Boolean)this.overrideArmorToughness.get(),
            (Double)this.armorToughness.get(),
            (Boolean)this.overrideMaxMana.get(),
            (Double)this.maxMana.get(),
            (Boolean)this.overrideSpellPower.get(),
            (Double)this.spellPower.get(),
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
            (Boolean)this.overrideHolySpellPower.get(),
            (Double)this.holySpellPower.get(),
            (Boolean)this.overrideEvocationSpellPower.get(),
            (Double)this.evocationSpellPower.get(),
            (Boolean)this.overrideSoundSpellPower.get(),
            (Double)this.soundSpellPower.get(),
            (Boolean)this.overrideGeoSpellPower.get(),
            (Double)this.geoSpellPower.get()
         );
      }
   }

   public record DynamicArmorPieceValues(
      boolean enabled,
      boolean overrideArmor,
      double armorValue,
      boolean overrideArmorToughness,
      double armorToughness,
      boolean overrideMaxMana,
      double maxMana,
      boolean overrideSpellPower,
      double spellPower,
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
      boolean overrideHolySpellPower,
      double holySpellPower,
      boolean overrideEvocationSpellPower,
      double evocationSpellPower,
      boolean overrideSoundSpellPower,
      double soundSpellPower,
      boolean overrideGeoSpellPower,
      double geoSpellPower
   ) {
   }

   private static enum ElementSchool {
      FIRE,
      ICE,
      LIGHTNING,
      ENDER,
      BLOOD,
      NATURE,
      SPIRIT,
      SYMMETRY,
      AQUA,
      HOLY,
      EVOCATION,
      SOUND,
      GEO;
   }
}
