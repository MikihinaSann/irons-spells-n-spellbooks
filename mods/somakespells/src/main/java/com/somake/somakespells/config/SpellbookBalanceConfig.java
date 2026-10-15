package com.somake.somakespells.config;

import com.mojang.logging.LogUtils;
import com.somake.somakespells.config.magic.MagicAttributeDefaults;
import com.somake.somakespells.config.magic.MagicAttributeOverrideConfig;
import com.somake.somakespells.config.magic.MagicAttributeOverrideValues;
import com.somake.somakespells.config.magic.MagicItemProfile;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import org.slf4j.Logger;

public class SpellbookBalanceConfig {
   private static final Builder BUILDER = new Builder();
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<ResourceLocation, MagicAttributeOverrideConfig> SPELLBOOKS = new LinkedHashMap<>();
   private static final Map<String, SpellbookBalanceConfig.DynamicSpellbookEntry> CUSTOM_SPELLBOOKS = new LinkedHashMap<>();
   public static final ModConfigSpec SPEC;

   private static MagicAttributeDefaults defaults() {
      return MagicAttributeDefaults.empty();
   }

   private static void registerIronsSpellbook(String path, String key, MagicAttributeDefaults defaults) {
      registerSpellbook(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", path), "ironsSpellbooks", key, defaults);
   }

   private static void registerSomakeSpellbook(String path, String key, MagicAttributeDefaults defaults) {
      registerSpellbook(ResourceLocation.fromNamespaceAndPath("somakespells", path), "somake", key, defaults);
   }

   private static void registerSpellbook(ResourceLocation itemId, String category, String key, MagicAttributeDefaults defaults) {
      SPELLBOOKS.put(itemId, buildEntry(itemId, category, key, defaults));
   }

   private static MagicAttributeOverrideConfig buildEntry(ResourceLocation itemId, String category, String key, MagicAttributeDefaults defaults) {
      BUILDER.push("spellbookBalance");
      BUILDER.push(category);
      BUILDER.push(key);
      MagicAttributeOverrideConfig config = ConfigBuilders.buildMagicAttributeConfig(BUILDER, MagicItemProfile.SPELLBOOK, defaults);
      BUILDER.pop();
      BUILDER.pop();
      BUILDER.pop();
      return config;
   }

   private static void registerCustomSpellbookSlot(int index) {
      BUILDER.push("spellbookBalance");
      BUILDER.push("custom");
      BUILDER.push("entry" + index);
      ConfigValue<String> itemId = BUILDER.comment("Spellbook item ID to override, for example 'othermod:my_spellbook'. Leave empty to disable this slot.")
         .define("itemId", "");
      MagicAttributeOverrideConfig config = ConfigBuilders.buildMagicAttributeConfig(BUILDER, MagicItemProfile.SPELLBOOK, defaults());
      CUSTOM_SPELLBOOKS.put("entry" + index, new SpellbookBalanceConfig.DynamicSpellbookEntry(itemId, config));
      BUILDER.pop();
      BUILDER.pop();
      BUILDER.pop();
   }

   public static MagicAttributeOverrideValues getLocalSpellbookOverride(ResourceLocation itemId) {
      MagicAttributeOverrideConfig config = SPELLBOOKS.get(itemId);
      if (config != null) {
         return config.read();
      } else {
         for (SpellbookBalanceConfig.DynamicSpellbookEntry entry : CUSTOM_SPELLBOOKS.values()) {
            ResourceLocation configuredId = entry.parseItemId();
            if (configuredId != null && configuredId.equals(itemId)) {
               return entry.config().read();
            }
         }

         return null;
      }
   }

   public static Map<String, MagicAttributeOverrideValues> getLocalSpellbookOverrideSnapshot() {
      Map<String, MagicAttributeOverrideValues> snapshot = new LinkedHashMap<>();
      SPELLBOOKS.forEach((itemId, config) -> snapshot.put(itemId.toString(), config.read()));
      CUSTOM_SPELLBOOKS.forEach((entryKey, entry) -> {
         ResourceLocation itemId = entry.parseItemId();
         if (itemId != null) {
            if (snapshot.containsKey(itemId.toString())) {
               LOGGER.warn("Custom spellbook config '{}' duplicates existing spellbook id '{}'; keeping custom values for sync snapshot.", entryKey, itemId);
            }

            snapshot.put(itemId.toString(), entry.config().read());
         }
      });
      return snapshot;
   }

   static {
      registerIronsSpellbook("wimpy_spell_book", "wimpySpellBook", defaults());
      registerIronsSpellbook("legendary_spell_book", "legendarySpellBook", defaults());
      registerIronsSpellbook("netherite_spell_book", "netheriteSpellBook", defaults());
      registerIronsSpellbook("diamond_spell_book", "diamondSpellBook", defaults());
      registerIronsSpellbook("gold_spell_book", "goldSpellBook", defaults());
      registerIronsSpellbook("iron_spell_book", "ironSpellBook", defaults());
      registerIronsSpellbook("copper_spell_book", "copperSpellBook", defaults());
      registerIronsSpellbook("rotten_spell_book", "rottenSpellBook", defaults());
      registerIronsSpellbook("blaze_spell_book", "blazeSpellBook", defaults());
      registerIronsSpellbook("dragonskin_spell_book", "dragonskinSpellBook", defaults());
      registerIronsSpellbook("druidic_spell_book", "druidicSpellBook", defaults());
      registerIronsSpellbook("villager_spell_book", "villagerSpellBook", defaults());
      registerIronsSpellbook("ice_spell_book", "iceSpellBook", defaults());
      registerIronsSpellbook("evoker_spell_book", "evokerSpellBook", defaults());
      registerIronsSpellbook("necronomicon_spell_book", "necronomiconSpellBook", defaults());
      registerIronsSpellbook("cursed_doll_spell_book", "cursedDollSpellBook", defaults());
      registerSomakeSpellbook("tidebound_spell_book", "tideboundSpellBook", defaults());
      registerSomakeSpellbook("tidebound_2_spell_book", "tidebound2SpellBook", defaults());
      registerSomakeSpellbook("tidebound_3_spell_book", "tidebound3SpellBook", defaults());
      registerSomakeSpellbook("aquamancer_spell_book", "aquamancerSpellBook", defaults());
      registerSomakeSpellbook("storm_scriptures_spell_book", "stormScripturesSpellBook", defaults());
      registerSomakeSpellbook("starless_grimoire_spell_book", "starlessGrimoireSpellBook", defaults());
      registerSomakeSpellbook("starless_grimoire_2_spell_book", "starlessGrimoire2SpellBook", defaults());
      registerSomakeSpellbook("starless_grimoire_3_spell_book", "starlessGrimoire3SpellBook", defaults());
      registerSomakeSpellbook("voidbound_codex_spell_book", "voidboundCodexSpellBook", defaults());
      registerSomakeSpellbook("voidbound_codex_2_spell_book", "voidboundCodex2SpellBook", defaults());
      registerSomakeSpellbook("voidbound_codex_3_spell_book", "voidboundCodex3SpellBook", defaults());
      registerSomakeSpellbook("invoker_canon_spell_book", "invokerCanonSpellBook", defaults());
      registerSomakeSpellbook("invoker_canon_2_spell_book", "invokerCanon2SpellBook", defaults());
      registerSomakeSpellbook("invoker_canon_3_spell_book", "invokerCanon3SpellBook", defaults());
      registerSomakeSpellbook("burning_magma_spell_book", "burningMagmaSpellBook", defaults());
      registerSomakeSpellbook("burning_magma_2_spell_book", "burningMagma2SpellBook", defaults());
      registerSomakeSpellbook("burning_magma_3_spell_book", "burningMagma3SpellBook", defaults());
      registerSomakeSpellbook("dawnlit_gospel_spell_book", "dawnlitGospelSpellBook", defaults());
      registerSomakeSpellbook("dawnlit_gospel_2_spell_book", "dawnlitGospel2SpellBook", defaults());
      registerSomakeSpellbook("dawnlit_gospel_3_spell_book", "dawnlitGospel3SpellBook", defaults());
      registerSomakeSpellbook("frostvein_codex_spell_book", "frostveinCodexSpellBook", defaults());
      registerSomakeSpellbook("frostvein_codex_2_spell_book", "frostveinCodex2SpellBook", defaults());
      registerSomakeSpellbook("frostvein_codex_3_spell_book", "frostveinCodex3SpellBook", defaults());
      registerSomakeSpellbook("stormcaller_lexicon_spell_book", "stormcallerLexiconSpellBook", defaults());
      registerSomakeSpellbook("stormcaller_lexicon_2_spell_book", "stormcallerLexicon2SpellBook", defaults());
      registerSomakeSpellbook("stormcaller_lexicon_3_spell_book", "stormcallerLexicon3SpellBook", defaults());
      registerSomakeSpellbook("verdant_testament_spell_book", "verdantTestamentSpellBook", defaults());
      registerSomakeSpellbook("verdant_testament_2_spell_book", "verdantTestament2SpellBook", defaults());
      registerSomakeSpellbook("verdant_testament_3_spell_book", "verdantTestament3SpellBook", defaults());
      registerSomakeSpellbook("sanguine_testament_spell_book", "sanguineTestamentSpellBook", defaults());
      registerSomakeSpellbook("sanguine_testament_2_spell_book", "sanguineTestament2SpellBook", defaults());
      registerSomakeSpellbook("sanguine_testament_3_spell_book", "sanguineTestament3SpellBook", defaults());
      registerSomakeSpellbook("soullit_testament_spell_book", "soullitTestamentSpellBook", defaults());
      registerSomakeSpellbook("soullit_testament_2_spell_book", "soullitTestament2SpellBook", defaults());
      registerSomakeSpellbook("soullit_testament_3_spell_book", "soullitTestament3SpellBook", defaults());
      registerSomakeSpellbook("axiomatic_codex_spell_book", "axiomaticCodexSpellBook", defaults());
      registerSomakeSpellbook("axiomatic_codex_2_spell_book", "axiomaticCodex2SpellBook", defaults());
      registerSomakeSpellbook("axiomatic_codex_3_spell_book", "axiomaticCodex3SpellBook", defaults());
      registerSomakeSpellbook("stoneheart_codex_spell_book", "stoneheartCodexSpellBook", defaults());
      registerSomakeSpellbook("stoneheart_codex_2_spell_book", "stoneheartCodex2SpellBook", defaults());
      registerSomakeSpellbook("stoneheart_codex_3_spell_book", "stoneheartCodex3SpellBook", defaults());
      registerSomakeSpellbook("resonance_canticle_spell_book", "resonanceCanticleSpellBook", defaults());
      registerSomakeSpellbook("resonance_canticle_2_spell_book", "resonanceCanticle2SpellBook", defaults());
      registerSomakeSpellbook("resonance_canticle_3_spell_book", "resonanceCanticle3SpellBook", defaults());

      for (int i = 1; i <= 24; i++) {
         registerCustomSpellbookSlot(i);
      }

      SPEC = BUILDER.build();
   }

   private record DynamicSpellbookEntry(ConfigValue<String> itemId, MagicAttributeOverrideConfig config) {
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
}
