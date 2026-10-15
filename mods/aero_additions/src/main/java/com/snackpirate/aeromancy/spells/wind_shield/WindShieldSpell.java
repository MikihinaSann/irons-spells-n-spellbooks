package com.snackpirate.aeromancy.spells.wind_shield;

import com.snackpirate.aeromancy.Aeromancy;
import com.snackpirate.aeromancy.spells.AASpells;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@AutoSpellConfig
public class WindShieldSpell extends AbstractSpell {
   public WindShieldSpell() {
      this.manaCostPerLevel = 30;
      this.baseSpellPower = 30;
      this.spellPowerPerLevel = 8;
      this.castTime = 60;
      this.baseManaCost = 130;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getDurationSeconds(caster, spellLevel) * 20, 1)}),
         Component.translatable("spell.aero_additions.wind_shield.chance", new Object[]{Utils.stringTruncation(chanceToDeflect(spellLevel - 1) * 100.0F, 2)})
      );
   }

   public ResourceLocation getSpellResource() {
      return Aeromancy.id("wind_shield");
   }

   public DefaultConfig getDefaultConfig() {
      return new DefaultConfig()
         .setMinRarity(SpellRarity.EPIC)
         .setSchoolResource(AASpells.Schools.WIND_RESOURCE)
         .setMaxLevel(5)
         .setCooldownSeconds(140.0)
         .build();
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      entity.addEffect(
         new MobEffectInstance(AASpells.MobEffects.WIND_SHIELD, this.getDurationSeconds(entity, spellLevel) * 20, spellLevel - 1, false, false, true)
      );
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public static float chanceToDeflect(int amplifier) {
      return amplifier / 10.0F + 0.3F;
   }

   private int getDurationSeconds(LivingEntity entity, int spellLevel) {
      return 10 + 5 * spellLevel + (int)Math.sqrt(10.0F * this.getSpellPower(spellLevel, entity));
   }

   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.CAST_T_POSE;
   }
}
