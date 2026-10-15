package com.snackpirate.aeromancy.spells.asphyxiate;

import com.snackpirate.aeromancy.Aeromancy;
import com.snackpirate.aeromancy.data.AAEntityTypeTags;
import com.snackpirate.aeromancy.spells.AASpells;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

@AutoSpellConfig
public class AsphyxiateSpell extends AbstractSpell {
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(AASpells.Schools.WIND_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(20.0)
      .build();

   public ResourceLocation getSpellResource() {
      return Aeromancy.id("asphyxiate");
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.CONTINUOUS;
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      if (playerMagicData != null
         && playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData
         && playerMagicData.getCastDurationRemaining() % 10 == 0) {
         LivingEntity target = ((TargetEntityCastData)playerMagicData.getAdditionalCastData()).getTarget((ServerLevel)level);
         if (target != null) {
            target.addEffect(new MobEffectInstance(AASpells.MobEffects.BREATHLESS, 10, spellLevel - 1));
         }
      }

      super.onServerCastTick(level, spellLevel, entity, playerMagicData);
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of(SoundEvents.BREEZE_IDLE_GROUND);
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(SoundEvents.BREEZE_IDLE_GROUND);
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      if (Utils.preCastTargetHelper(level, entity, playerMagicData, this, 15, 0.15F)) {
         LivingEntity target = ((TargetEntityCastData)playerMagicData.getAdditionalCastData()).getTarget((ServerLevel)level);
         if (target != null && !target.getType().is(AAEntityTypeTags.ASPHYXIATION_IMMUNE)) {
            playerMagicData.setAdditionalCastData(new TargetEntityCastData(target));
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_CONTINUOUS_OVERHEAD;
   }

   public AsphyxiateSpell() {
      this.manaCostPerLevel = 3;
      this.baseSpellPower = 3;
      this.spellPowerPerLevel = 2;
      this.castTime = 100;
      this.baseManaCost = 9;
   }

   public int getCastTime(int spellLevel) {
      return this.castTime + 100 * spellLevel;
   }
}
