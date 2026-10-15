package com.somake.somakespells.spells.spirit;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.registries.ModMobEffects;
import com.somake.somakespells.util.ElementalChargeActivation;
import com.somake.somakespells.util.RedSoulVisuals;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class SpiritEmpowermentSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "spirit_empowerment");
   private final ResourceLocation spiritSchool = MagicFromTheEastCompat.spiritSchoolOrEvocation();
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(this.spiritSchool)
      .setMaxLevel(3)
      .setCooldownSeconds(40.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getSpellPower(spellLevel, caster) * 20.0F, 1)}),
         Component.translatable(
            "attribute.modifier.plus.1",
            new Object[]{
               Utils.stringTruncation(this.getPercentSpellPower(spellLevel, caster), 0),
               Component.translatable(
                  MagicFromTheEastCompat.isLoaded() ? "attribute.iss_magicfromtheeast.spirit_spell_power" : "attribute.irons_spellbooks.evocation_spell_power"
               )
            }
         )
      );
   }

   public SpiritEmpowermentSpell() {
      this.manaCostPerLevel = 25;
      this.baseSpellPower = 30;
      this.spellPowerPerLevel = 8;
      this.castTime = 0;
      this.baseManaCost = 50;
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return MagicFromTheEastCompat.getSpiritCastSound();
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         RedSoulVisuals.spawnSpiritCastPulse(serverLevel, entity);
      }

      ElementalChargeActivation.activate(level, this, spellLevel, entity, ModMobEffects.SPIRIT_EMPOWERMENT, 6002608);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getPercentSpellPower(int spellLevel, LivingEntity entity) {
      return spellLevel * 0.05F * 100.0F;
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.SELF_CAST_ANIMATION;
   }
}
