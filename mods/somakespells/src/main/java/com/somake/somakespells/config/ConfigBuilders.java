package com.somake.somakespells.config;

import com.somake.somakespells.config.magic.MagicAttributeDefaults;
import com.somake.somakespells.config.magic.MagicAttributeOverrideConfig;
import com.somake.somakespells.config.magic.MagicItemProfile;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;

final class ConfigBuilders {
   static final int DEFAULT_DYNAMIC_CUSTOM_ENTRY_COUNT = 24;

   private ConfigBuilders() {
   }

   static MagicAttributeOverrideConfig buildMagicAttributeConfig(Builder builder, MagicItemProfile profile, MagicAttributeDefaults defaults) {
      BooleanValue enabled = builder.comment("If true, overrides this item's default attributes. If false, keeps original mod defaults.")
         .define("enabled", defaults.enabled);
      BooleanValue overrideMaxMana = builder.define("overrideMaxMana", defaults.overrideMaxMana);
      DoubleValue maxMana = builder.defineInRange("maxMana", defaults.maxMana, -100000.0, 100000.0);
      BooleanValue overrideManaRegen = builder.define("overrideManaRegen", defaults.overrideManaRegen);
      DoubleValue manaRegen = builder.defineInRange("manaRegen", defaults.manaRegen, -100.0, 100.0);
      BooleanValue overrideSpellPower = builder.define("overrideSpellPower", defaults.overrideSpellPower);
      DoubleValue spellPower = builder.defineInRange("spellPower", defaults.spellPower, -100.0, 100.0);
      BooleanValue overrideCastTimeReduction = builder.define("overrideCastTimeReduction", defaults.overrideCastTimeReduction);
      DoubleValue castTimeReduction = builder.defineInRange("castTimeReduction", defaults.castTimeReduction, -100.0, 100.0);
      BooleanValue overrideCooldownReduction = builder.define("overrideCooldownReduction", defaults.overrideCooldownReduction);
      DoubleValue cooldownReduction = builder.defineInRange("cooldownReduction", defaults.cooldownReduction, -100.0, 100.0);
      BooleanValue overrideSpellResist = builder.define("overrideSpellResist", defaults.overrideSpellResist);
      DoubleValue spellResist = builder.defineInRange("spellResist", defaults.spellResist, -100.0, 100.0);
      BooleanValue overrideSummonDamage = builder.define("overrideSummonDamage", defaults.overrideSummonDamage);
      DoubleValue summonDamage = builder.defineInRange("summonDamage", defaults.summonDamage, -100.0, 100.0);
      BooleanValue overrideFireSpellPower = builder.define("overrideFireSpellPower", defaults.overrideFireSpellPower);
      DoubleValue fireSpellPower = builder.defineInRange("fireSpellPower", defaults.fireSpellPower, -100.0, 100.0);
      BooleanValue overrideIceSpellPower = builder.define("overrideIceSpellPower", defaults.overrideIceSpellPower);
      DoubleValue iceSpellPower = builder.defineInRange("iceSpellPower", defaults.iceSpellPower, -100.0, 100.0);
      BooleanValue overrideLightningSpellPower = builder.define("overrideLightningSpellPower", defaults.overrideLightningSpellPower);
      DoubleValue lightningSpellPower = builder.defineInRange("lightningSpellPower", defaults.lightningSpellPower, -100.0, 100.0);
      BooleanValue overrideEnderSpellPower = builder.define("overrideEnderSpellPower", defaults.overrideEnderSpellPower);
      DoubleValue enderSpellPower = builder.defineInRange("enderSpellPower", defaults.enderSpellPower, -100.0, 100.0);
      BooleanValue overrideBloodSpellPower = builder.define("overrideBloodSpellPower", defaults.overrideBloodSpellPower);
      DoubleValue bloodSpellPower = builder.defineInRange("bloodSpellPower", defaults.bloodSpellPower, -100.0, 100.0);
      BooleanValue overrideNatureSpellPower = builder.define("overrideNatureSpellPower", defaults.overrideNatureSpellPower);
      DoubleValue natureSpellPower = builder.defineInRange("natureSpellPower", defaults.natureSpellPower, -100.0, 100.0);
      BooleanValue overrideHolySpellPower = builder.define("overrideHolySpellPower", defaults.overrideHolySpellPower);
      DoubleValue holySpellPower = builder.defineInRange("holySpellPower", defaults.holySpellPower, -100.0, 100.0);
      BooleanValue overrideEvocationSpellPower = builder.define("overrideEvocationSpellPower", defaults.overrideEvocationSpellPower);
      DoubleValue evocationSpellPower = builder.defineInRange("evocationSpellPower", defaults.evocationSpellPower, -100.0, 100.0);
      BooleanValue overrideEldritchSpellPower = builder.define("overrideEldritchSpellPower", defaults.overrideEldritchSpellPower);
      DoubleValue eldritchSpellPower = builder.defineInRange("eldritchSpellPower", defaults.eldritchSpellPower, -100.0, 100.0);
      BooleanValue overrideAquaSpellPower = builder.define("overrideAquaSpellPower", defaults.overrideAquaSpellPower);
      DoubleValue aquaSpellPower = builder.defineInRange("aquaSpellPower", defaults.aquaSpellPower, -100.0, 100.0);
      BooleanValue overrideSpiritSpellPower = builder.define("overrideSpiritSpellPower", defaults.overrideSpiritSpellPower);
      DoubleValue spiritSpellPower = builder.defineInRange("spiritSpellPower", defaults.spiritSpellPower, -100.0, 100.0);
      BooleanValue overrideSymmetrySpellPower = builder.define("overrideSymmetrySpellPower", defaults.overrideSymmetrySpellPower);
      DoubleValue symmetrySpellPower = builder.defineInRange("symmetrySpellPower", defaults.symmetrySpellPower, -100.0, 100.0);
      BooleanValue overrideSoundSpellPower = builder.define("overrideSoundSpellPower", defaults.overrideSoundSpellPower);
      DoubleValue soundSpellPower = builder.defineInRange("soundSpellPower", defaults.soundSpellPower, -100.0, 100.0);
      BooleanValue overrideGeoSpellPower = builder.define("overrideGeoSpellPower", defaults.overrideGeoSpellPower);
      DoubleValue geoSpellPower = builder.defineInRange("geoSpellPower", defaults.geoSpellPower, -100.0, 100.0);
      BooleanValue overrideAttackDamage = builder.define("overrideAttackDamage", defaults.overrideAttackDamage);
      DoubleValue attackDamage = builder.defineInRange("attackDamage", defaults.attackDamage, -1024.0, 1024.0);
      BooleanValue overrideAttackSpeed = builder.define("overrideAttackSpeed", defaults.overrideAttackSpeed);
      DoubleValue attackSpeed = builder.defineInRange("attackSpeed", defaults.attackSpeed, -1024.0, 1024.0);
      BooleanValue overrideEntityInteractionRange = builder.define("overrideEntityInteractionRange", defaults.overrideEntityInteractionRange);
      DoubleValue entityInteractionRange = builder.defineInRange("entityInteractionRange", defaults.entityInteractionRange, -1024.0, 1024.0);
      return new MagicAttributeOverrideConfig(
         profile,
         enabled,
         overrideMaxMana,
         maxMana,
         overrideManaRegen,
         manaRegen,
         overrideSpellPower,
         spellPower,
         overrideCastTimeReduction,
         castTimeReduction,
         overrideCooldownReduction,
         cooldownReduction,
         overrideSpellResist,
         spellResist,
         overrideSummonDamage,
         summonDamage,
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
         overrideHolySpellPower,
         holySpellPower,
         overrideEvocationSpellPower,
         evocationSpellPower,
         overrideEldritchSpellPower,
         eldritchSpellPower,
         overrideAquaSpellPower,
         aquaSpellPower,
         overrideSpiritSpellPower,
         spiritSpellPower,
         overrideSymmetrySpellPower,
         symmetrySpellPower,
         overrideSoundSpellPower,
         soundSpellPower,
         overrideGeoSpellPower,
         geoSpellPower,
         overrideAttackDamage,
         attackDamage,
         overrideAttackSpeed,
         attackSpeed,
         overrideEntityInteractionRange,
         entityInteractionRange
      );
   }
}
