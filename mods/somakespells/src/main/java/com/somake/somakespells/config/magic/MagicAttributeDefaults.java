package com.somake.somakespells.config.magic;

public class MagicAttributeDefaults {
   public boolean enabled;
   public boolean overrideMaxMana;
   public double maxMana;
   public boolean overrideManaRegen;
   public double manaRegen;
   public boolean overrideSpellPower;
   public double spellPower;
   public boolean overrideCastTimeReduction;
   public double castTimeReduction;
   public boolean overrideCooldownReduction;
   public double cooldownReduction;
   public boolean overrideSpellResist;
   public double spellResist;
   public boolean overrideSummonDamage;
   public double summonDamage;
   public boolean overrideFireSpellPower;
   public double fireSpellPower;
   public boolean overrideIceSpellPower;
   public double iceSpellPower;
   public boolean overrideLightningSpellPower;
   public double lightningSpellPower;
   public boolean overrideEnderSpellPower;
   public double enderSpellPower;
   public boolean overrideBloodSpellPower;
   public double bloodSpellPower;
   public boolean overrideNatureSpellPower;
   public double natureSpellPower;
   public boolean overrideHolySpellPower;
   public double holySpellPower;
   public boolean overrideEvocationSpellPower;
   public double evocationSpellPower;
   public boolean overrideEldritchSpellPower;
   public double eldritchSpellPower;
   public boolean overrideAquaSpellPower;
   public double aquaSpellPower;
   public boolean overrideSpiritSpellPower;
   public double spiritSpellPower;
   public boolean overrideSymmetrySpellPower;
   public double symmetrySpellPower;
   public boolean overrideSoundSpellPower;
   public double soundSpellPower;
   public boolean overrideGeoSpellPower;
   public double geoSpellPower;
   public boolean overrideAttackDamage;
   public double attackDamage;
   public boolean overrideAttackSpeed;
   public double attackSpeed;
   public boolean overrideEntityInteractionRange;
   public double entityInteractionRange;

   public static MagicAttributeDefaults empty() {
      return new MagicAttributeDefaults();
   }
}
