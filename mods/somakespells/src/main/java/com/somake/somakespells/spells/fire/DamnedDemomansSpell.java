package com.somake.somakespells.spells.fire;

import com.somake.somakespells.registries.ModMobEffects;
import com.somake.somakespells.util.FireElementContext;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class DamnedDemomansSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "damned_demomans");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(60.0)
      .build();

   public DamnedDemomansSpell() {
      this.manaCostPerLevel = 20;
      this.baseManaCost = 50;
      this.baseSpellPower = 5;
      this.spellPowerPerLevel = 5;
      this.castTime = 0;
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
      return Optional.of(SoundEvents.TNT_PRIMED);
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getDuration(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)})
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      int duration = this.getDuration(spellLevel, caster);
      int amplifier = spellLevel - 1;
      caster.addEffect(new MobEffectInstance(ModMobEffects.DAMNED_DEMOMANS, duration, amplifier, false, true, true));
      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private int getDuration(int spellLevel, LivingEntity caster) {
      return 200 + spellLevel * 20;
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      float power = this.getSpellPower(spellLevel, caster);
      return caster == null ? power : FireElementContext.resolveForSpell(caster, this).applySupportedDamage(power, caster);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.SELF_CAST_ANIMATION;
   }
}
