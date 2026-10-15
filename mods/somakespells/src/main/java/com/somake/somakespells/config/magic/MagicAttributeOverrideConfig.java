package com.somake.somakespells.config.magic;

import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;

public class MagicAttributeOverrideConfig {
   public final MagicItemProfile profile;
   public final BooleanValue enabled;
   public final BooleanValue overrideMaxMana;
   public final DoubleValue maxMana;
   public final BooleanValue overrideManaRegen;
   public final DoubleValue manaRegen;
   public final BooleanValue overrideSpellPower;
   public final DoubleValue spellPower;
   public final BooleanValue overrideCastTimeReduction;
   public final DoubleValue castTimeReduction;
   public final BooleanValue overrideCooldownReduction;
   public final DoubleValue cooldownReduction;
   public final BooleanValue overrideSpellResist;
   public final DoubleValue spellResist;
   public final BooleanValue overrideSummonDamage;
   public final DoubleValue summonDamage;
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
   public final BooleanValue overrideHolySpellPower;
   public final DoubleValue holySpellPower;
   public final BooleanValue overrideEvocationSpellPower;
   public final DoubleValue evocationSpellPower;
   public final BooleanValue overrideEldritchSpellPower;
   public final DoubleValue eldritchSpellPower;
   public final BooleanValue overrideAquaSpellPower;
   public final DoubleValue aquaSpellPower;
   public final BooleanValue overrideSpiritSpellPower;
   public final DoubleValue spiritSpellPower;
   public final BooleanValue overrideSymmetrySpellPower;
   public final DoubleValue symmetrySpellPower;
   public final BooleanValue overrideSoundSpellPower;
   public final DoubleValue soundSpellPower;
   public final BooleanValue overrideGeoSpellPower;
   public final DoubleValue geoSpellPower;
   public final BooleanValue overrideAttackDamage;
   public final DoubleValue attackDamage;
   public final BooleanValue overrideAttackSpeed;
   public final DoubleValue attackSpeed;
   public final BooleanValue overrideEntityInteractionRange;
   public final DoubleValue entityInteractionRange;

   public MagicAttributeOverrideConfig(
      MagicItemProfile profile,
      BooleanValue enabled,
      BooleanValue overrideMaxMana,
      DoubleValue maxMana,
      BooleanValue overrideManaRegen,
      DoubleValue manaRegen,
      BooleanValue overrideSpellPower,
      DoubleValue spellPower,
      BooleanValue overrideCastTimeReduction,
      DoubleValue castTimeReduction,
      BooleanValue overrideCooldownReduction,
      DoubleValue cooldownReduction,
      BooleanValue overrideSpellResist,
      DoubleValue spellResist,
      BooleanValue overrideSummonDamage,
      DoubleValue summonDamage,
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
      BooleanValue overrideHolySpellPower,
      DoubleValue holySpellPower,
      BooleanValue overrideEvocationSpellPower,
      DoubleValue evocationSpellPower,
      BooleanValue overrideEldritchSpellPower,
      DoubleValue eldritchSpellPower,
      BooleanValue overrideAquaSpellPower,
      DoubleValue aquaSpellPower,
      BooleanValue overrideSpiritSpellPower,
      DoubleValue spiritSpellPower,
      BooleanValue overrideSymmetrySpellPower,
      DoubleValue symmetrySpellPower,
      BooleanValue overrideSoundSpellPower,
      DoubleValue soundSpellPower,
      BooleanValue overrideGeoSpellPower,
      DoubleValue geoSpellPower,
      BooleanValue overrideAttackDamage,
      DoubleValue attackDamage,
      BooleanValue overrideAttackSpeed,
      DoubleValue attackSpeed,
      BooleanValue overrideEntityInteractionRange,
      DoubleValue entityInteractionRange
   ) {
      this.profile = profile;
      this.enabled = enabled;
      this.overrideMaxMana = overrideMaxMana;
      this.maxMana = maxMana;
      this.overrideManaRegen = overrideManaRegen;
      this.manaRegen = manaRegen;
      this.overrideSpellPower = overrideSpellPower;
      this.spellPower = spellPower;
      this.overrideCastTimeReduction = overrideCastTimeReduction;
      this.castTimeReduction = castTimeReduction;
      this.overrideCooldownReduction = overrideCooldownReduction;
      this.cooldownReduction = cooldownReduction;
      this.overrideSpellResist = overrideSpellResist;
      this.spellResist = spellResist;
      this.overrideSummonDamage = overrideSummonDamage;
      this.summonDamage = summonDamage;
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
      this.overrideHolySpellPower = overrideHolySpellPower;
      this.holySpellPower = holySpellPower;
      this.overrideEvocationSpellPower = overrideEvocationSpellPower;
      this.evocationSpellPower = evocationSpellPower;
      this.overrideEldritchSpellPower = overrideEldritchSpellPower;
      this.eldritchSpellPower = eldritchSpellPower;
      this.overrideAquaSpellPower = overrideAquaSpellPower;
      this.aquaSpellPower = aquaSpellPower;
      this.overrideSpiritSpellPower = overrideSpiritSpellPower;
      this.spiritSpellPower = spiritSpellPower;
      this.overrideSymmetrySpellPower = overrideSymmetrySpellPower;
      this.symmetrySpellPower = symmetrySpellPower;
      this.overrideSoundSpellPower = overrideSoundSpellPower;
      this.soundSpellPower = soundSpellPower;
      this.overrideGeoSpellPower = overrideGeoSpellPower;
      this.geoSpellPower = geoSpellPower;
      this.overrideAttackDamage = overrideAttackDamage;
      this.attackDamage = attackDamage;
      this.overrideAttackSpeed = overrideAttackSpeed;
      this.attackSpeed = attackSpeed;
      this.overrideEntityInteractionRange = overrideEntityInteractionRange;
      this.entityInteractionRange = entityInteractionRange;
   }

   public MagicAttributeOverrideValues read() {
      return new MagicAttributeOverrideValues(
         this.profile,
         (Boolean)this.enabled.get(),
         (Boolean)this.overrideMaxMana.get(),
         (Double)this.maxMana.get(),
         (Boolean)this.overrideManaRegen.get(),
         (Double)this.manaRegen.get(),
         (Boolean)this.overrideSpellPower.get(),
         (Double)this.spellPower.get(),
         (Boolean)this.overrideCastTimeReduction.get(),
         (Double)this.castTimeReduction.get(),
         (Boolean)this.overrideCooldownReduction.get(),
         (Double)this.cooldownReduction.get(),
         (Boolean)this.overrideSpellResist.get(),
         (Double)this.spellResist.get(),
         (Boolean)this.overrideSummonDamage.get(),
         (Double)this.summonDamage.get(),
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
         (Boolean)this.overrideHolySpellPower.get(),
         (Double)this.holySpellPower.get(),
         (Boolean)this.overrideEvocationSpellPower.get(),
         (Double)this.evocationSpellPower.get(),
         (Boolean)this.overrideEldritchSpellPower.get(),
         (Double)this.eldritchSpellPower.get(),
         (Boolean)this.overrideAquaSpellPower.get(),
         (Double)this.aquaSpellPower.get(),
         (Boolean)this.overrideSpiritSpellPower.get(),
         (Double)this.spiritSpellPower.get(),
         (Boolean)this.overrideSymmetrySpellPower.get(),
         (Double)this.symmetrySpellPower.get(),
         (Boolean)this.overrideSoundSpellPower.get(),
         (Double)this.soundSpellPower.get(),
         (Boolean)this.overrideGeoSpellPower.get(),
         (Double)this.geoSpellPower.get(),
         (Boolean)this.overrideAttackDamage.get(),
         (Double)this.attackDamage.get(),
         (Boolean)this.overrideAttackSpeed.get(),
         (Double)this.attackSpeed.get(),
         (Boolean)this.overrideEntityInteractionRange.get(),
         (Double)this.entityInteractionRange.get()
      );
   }
}
