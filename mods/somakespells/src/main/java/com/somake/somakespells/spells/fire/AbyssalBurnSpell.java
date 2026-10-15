package com.somake.somakespells.spells.fire;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.entity.AbyssalBurnEntity;
import com.somake.somakespells.util.FireElementContext;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class AbyssalBurnSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "abyssal_burn");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(20.0)
      .build();

   public AbyssalBurnSpell() {
      this.manaCostPerLevel = 15;
      this.baseManaCost = 50;
      this.baseSpellPower = 7;
      this.spellPowerPerLevel = 2;
      this.castTime = 30;
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of(SoundEvents.FIRECHARGE_USE);
   }

   public AnimationHolder getCastStartAnimation() {
      return MagicFromTheEastCompat.getKatanaStrikeAnimationOrFallback();
   }

   public AnimationHolder getCastFinishAnimation() {
      return AnimationHolder.pass();
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}));
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      FireElementContext context = FireElementContext.resolveForSpell(caster, this);
      float damage = this.getDamage(spellLevel, caster);
      AbyssalBurnEntity fissure = new AbyssalBurnEntity(level, caster, damage);
      fissure.setIsSoulFire(context.isSoulFire());
      fissure.setIsInfernalFire(context.isInfernalFire());
      level.addFreshEntity(fissure);
      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      float power = this.getSpellPower(spellLevel, caster);
      return caster == null ? power : FireElementContext.resolveForSpell(caster, this).applySupportedDamage(power, caster);
   }
}
