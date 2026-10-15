package com.somake.somakespells.spells.fire;

import com.somake.somakespells.entity.ApocalypticBurstChargeEntity;
import com.somake.somakespells.entity.ApocalypticBurstEntity;
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
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ApocalypticBurstSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "apocalyptic_burst");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(60.0)
      .build();

   public ApocalypticBurstSpell() {
      this.manaCostPerLevel = 20;
      this.baseManaCost = 100;
      this.baseSpellPower = 13;
      this.spellPowerPerLevel = 3;
      this.castTime = 60;
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
      return Optional.of(SoundEvents.BEACON_ACTIVATE);
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundEvents.GENERIC_EXPLODE.value());
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_CHARGED_CAST;
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      if (playerMagicData != null && playerMagicData.getCastDurationRemaining() + 1 >= this.getCastTime(spellLevel)) {
         ApocalypticBurstChargeEntity charge = new ApocalypticBurstChargeEntity(level, entity);
         FireElementContext context = FireElementContext.resolveForSpell(entity, this);
         charge.setIsSoulFire(context.isSoulFire());
         charge.setIsInfernalFire(context.isInfernalFire());
         level.addFreshEntity(charge);
      }

      super.onServerCastTick(level, spellLevel, entity, playerMagicData);
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(spellLevel), 1)})
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      FireElementContext context = FireElementContext.resolveForSpell(caster, this);
      float damage = this.getDamage(spellLevel, caster);
      ApocalypticBurstEntity burst = new ApocalypticBurstEntity(level, caster, damage);
      burst.setIsSoulFire(context.isSoulFire());
      burst.setIsInfernalFire(context.isInfernalFire());
      burst.setRadius(this.getRadius(spellLevel));
      level.addFreshEntity(burst);
      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      float power = this.getSpellPower(spellLevel, caster);
      return caster == null ? power : FireElementContext.resolveForSpell(caster, this).applySupportedDamage(power, caster);
   }

   private float getRadius(int spellLevel) {
      return 5.0F + spellLevel * 1.0F;
   }
}
