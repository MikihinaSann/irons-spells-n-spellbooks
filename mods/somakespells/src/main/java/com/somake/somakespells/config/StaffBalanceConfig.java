package com.somake.somakespells.config;

import com.mojang.logging.LogUtils;
import com.somake.somakespells.compat.BornInChaosCompat;
import com.somake.somakespells.compat.LegendaryMonstersCompat;
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

public class StaffBalanceConfig {
   private static final Builder BUILDER = new Builder();
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<ResourceLocation, MagicAttributeOverrideConfig> STAFFS = new LinkedHashMap<>();
   private static final Map<String, StaffBalanceConfig.DynamicStaffEntry> CUSTOM_STAFFS = new LinkedHashMap<>();
   public static final ModConfigSpec SPEC;

   private static MagicAttributeDefaults staffDefaults() {
      return MagicAttributeDefaults.empty();
   }

   private static MagicAttributeDefaults compatStaffDefaults(boolean infernalFire, boolean blood, boolean evocation) {
      MagicAttributeDefaults defaults = MagicAttributeDefaults.empty();
      defaults.enabled = true;
      if (infernalFire) {
         defaults.overrideFireSpellPower = false;
      }

      if (blood) {
         defaults.overrideBloodSpellPower = true;
         defaults.bloodSpellPower = 0.15;
      }

      if (evocation) {
         defaults.overrideEvocationSpellPower = true;
         defaults.evocationSpellPower = 0.15;
      }

      return defaults;
   }

   private static MagicAttributeDefaults wandOfCloudsDefaults() {
      MagicAttributeDefaults defaults = MagicAttributeDefaults.empty();
      defaults.enabled = true;
      defaults.overrideAquaSpellPower = true;
      defaults.aquaSpellPower = 0.1;
      defaults.overrideLightningSpellPower = true;
      defaults.lightningSpellPower = 0.1;
      return defaults;
   }

   private static MagicAttributeDefaults singleSchoolStaffDefaults(StaffBalanceConfig.SchoolDefaults school, double amount) {
      MagicAttributeDefaults defaults = MagicAttributeDefaults.empty();
      defaults.enabled = true;
      school.apply(defaults, amount);
      return defaults;
   }

   private static void registerIronsStaff(String path, String key, MagicAttributeDefaults defaults) {
      registerStaff(ResourceLocation.fromNamespaceAndPath("irons_spellbooks", path), "ironsSpellbooks", key, defaults);
   }

   private static void registerSomakeStaff(String path, String key, MagicAttributeDefaults defaults) {
      registerStaff(ResourceLocation.fromNamespaceAndPath("somakespells", path), "somake", key, defaults);
   }

   private static void registerStaff(ResourceLocation itemId, String category, String key, MagicAttributeDefaults defaults) {
      STAFFS.put(itemId, buildEntry(category, key, defaults));
   }

   private static MagicAttributeOverrideConfig buildEntry(String category, String key, MagicAttributeDefaults defaults) {
      BUILDER.push("staffBalance");
      BUILDER.push(category);
      BUILDER.push(key);
      MagicAttributeOverrideConfig config = ConfigBuilders.buildMagicAttributeConfig(BUILDER, MagicItemProfile.STAFF, defaults);
      BUILDER.pop();
      BUILDER.pop();
      BUILDER.pop();
      return config;
   }

   private static void registerCustomStaffSlot(int index) {
      BUILDER.push("staffBalance");
      BUILDER.push("custom");
      BUILDER.push("entry" + index);
      ConfigValue<String> itemId = BUILDER.comment("Staff item ID to override, for example 'othermod:my_staff'. Leave empty to disable this slot.")
         .define("itemId", "");
      MagicAttributeOverrideConfig config = ConfigBuilders.buildMagicAttributeConfig(BUILDER, MagicItemProfile.STAFF, staffDefaults());
      CUSTOM_STAFFS.put("entry" + index, new StaffBalanceConfig.DynamicStaffEntry(itemId, config));
      BUILDER.pop();
      BUILDER.pop();
      BUILDER.pop();
   }

   public static MagicAttributeOverrideValues getLocalStaffOverride(ResourceLocation itemId) {
      MagicAttributeOverrideConfig config = STAFFS.get(itemId);
      if (config != null) {
         return config.read();
      } else {
         for (StaffBalanceConfig.DynamicStaffEntry entry : CUSTOM_STAFFS.values()) {
            ResourceLocation configuredId = entry.parseItemId();
            if (configuredId != null && configuredId.equals(itemId)) {
               return entry.config().read();
            }
         }

         return null;
      }
   }

   public static Map<String, MagicAttributeOverrideValues> getLocalStaffOverrideSnapshot() {
      Map<String, MagicAttributeOverrideValues> snapshot = new LinkedHashMap<>();
      STAFFS.forEach((itemId, config) -> snapshot.put(itemId.toString(), config.read()));
      CUSTOM_STAFFS.forEach((entryKey, entry) -> {
         ResourceLocation itemId = entry.parseItemId();
         if (itemId != null) {
            if (snapshot.containsKey(itemId.toString())) {
               LOGGER.warn("Custom staff config '{}' duplicates existing staff id '{}'; keeping custom values for sync snapshot.", entryKey, itemId);
            }

            snapshot.put(itemId.toString(), entry.config().read());
         }
      });
      return snapshot;
   }

   static {
      registerIronsStaff("graybeard_staff", "graybeardStaff", staffDefaults());
      registerIronsStaff("pyrium_staff", "pyriumStaff", staffDefaults());
      registerIronsStaff("artificer_cane", "artificerCane", staffDefaults());
      registerIronsStaff("ice_staff", "iceStaff", staffDefaults());
      registerIronsStaff("lightning_rod", "lightningRodStaff", staffDefaults());
      registerIronsStaff("blood_staff", "bloodStaff", staffDefaults());
      registerSomakeStaff("aquamancer_staff", "aquamancerStaff", staffDefaults());
      registerSomakeStaff("ignitium_staff", "ignitiumStaff", staffDefaults());
      registerSomakeStaff("soul_fire_ignitium_staff", "soulFireIgnitiumStaff", staffDefaults());
      registerSomakeStaff("infernal_fire_ignitium_staff", "infernalFireIgnitiumStaff", staffDefaults());
      registerSomakeStaff("spirit_staff", "spiritStaff", staffDefaults());
      registerSomakeStaff("symmetry_staff", "symmetryStaff", staffDefaults());
      registerSomakeStaff("archangels_staff", "archangelsStaff", staffDefaults());
      registerSomakeStaff("withered_rose_staff", "witheredRoseStaff", staffDefaults());
      registerStaff(BornInChaosCompat.PUMPKIN_STAFF_ID, "bornInChaos", "pumpkinStaff", compatStaffDefaults(true, false, false));
      registerStaff(BornInChaosCompat.BONESCALLER_STAFF_ID, "bornInChaos", "bonescallerStaff", compatStaffDefaults(false, true, false));
      registerStaff(BornInChaosCompat.STAFF_OF_MAGIC_ARROWS_ID, "bornInChaos", "staffOfMagicArrows", compatStaffDefaults(false, false, true));
      registerStaff(LegendaryMonstersCompat.WAND_OF_CLOUDS_ID, "legendaryMonsters", "wandOfClouds", wandOfCloudsDefaults());
      registerStaff(
         LegendaryMonstersCompat.VOID_ENTITY_WARPER_ID,
         "legendaryMonsters",
         "voidEntityWarper",
         singleSchoolStaffDefaults(StaffBalanceConfig.SchoolDefaults.ENDER, 0.15)
      );
      registerStaff(
         LegendaryMonstersCompat.ENTITY_WARPER_ID,
         "legendaryMonsters",
         "entityWarper",
         singleSchoolStaffDefaults(StaffBalanceConfig.SchoolDefaults.NATURE, 0.15)
      );

      for (int i = 1; i <= 24; i++) {
         registerCustomStaffSlot(i);
      }

      SPEC = BUILDER.build();
   }

   private record DynamicStaffEntry(ConfigValue<String> itemId, MagicAttributeOverrideConfig config) {
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

   private static enum SchoolDefaults {
      ENDER {
         @Override
         void apply(MagicAttributeDefaults defaults, double amount) {
            defaults.overrideEnderSpellPower = true;
            defaults.enderSpellPower = amount;
         }
      },
      NATURE {
         @Override
         void apply(MagicAttributeDefaults defaults, double amount) {
            defaults.overrideNatureSpellPower = true;
            defaults.natureSpellPower = amount;
         }
      };

      abstract void apply(MagicAttributeDefaults var1, double var2);
   }
}
