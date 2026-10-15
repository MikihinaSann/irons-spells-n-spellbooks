package com.snackpirate.aeromancy.spells.thunderclap;

import com.snackpirate.aeromancy.AASounds;
import com.snackpirate.aeromancy.Aeromancy;
import com.snackpirate.aeromancy.spells.AASpells;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

@AutoSpellConfig
public class ThunderclapSpell extends AbstractSpell {
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(AASpells.Schools.WIND_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(15.0)
      .build();

   public ThunderclapSpell() {
      this.manaCostPerLevel = 20;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 1;
      this.castTime = 30;
      this.baseManaCost = 100;
   }

   public ResourceLocation getSpellResource() {
      return Aeromancy.id("thunderclap");
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return java.util.Optional.of(AASounds.WIND_CAST_REVERSED.get());
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_ANIMATION;
   }

   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.FINISH_ANIMATION;
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return java.util.Optional.of(AASounds.THUNDERCLAP_CAST.get());
   }
}
