package com.somake.somakespells.spells.fire;

import com.somake.somakespells.entity.PumpkinBombEntity;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class PumpkinBombSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "pumpkin_bomb");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(5.0)
      .build();

   public PumpkinBombSpell() {
      this.manaCostPerLevel = 10;
      this.baseManaCost = 20;
      this.baseSpellPower = 5;
      this.spellPowerPerLevel = 1;
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
      return Optional.empty();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(SoundEvents.SNOWBALL_THROW);
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getInitialDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.aoe_damage", new Object[]{Utils.stringTruncation(this.getAoEDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(caster), 1)})
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      FireElementContext context = FireElementContext.resolveForSpell(caster, this);
      int effectDuration = 200 + spellLevel * 60;
      PumpkinBombEntity projectile = new PumpkinBombEntity(level, caster);
      projectile.setDamage(this.getInitialDamage(spellLevel, caster), this.getAoEDamage(spellLevel, caster));
      projectile.setRadius(this.getRadius(caster));
      projectile.setEffectDuration(effectDuration);
      projectile.setIsSoulFire(context.isSoulFire());
      projectile.setIsInfernalFire(context.isInfernalFire());
      projectile.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0F, 1.5F, 1.0F);
      level.addFreshEntity(projectile);
      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private float getInitialDamage(int spellLevel, LivingEntity caster) {
      float power = this.getSpellPower(spellLevel, caster);
      return caster == null ? power : FireElementContext.resolveForSpell(caster, this).applySupportedDamage(power, caster);
   }

   private float getAoEDamage(int spellLevel, LivingEntity caster) {
      float power = this.getSpellPower(spellLevel, caster);
      return caster == null ? power * 0.2F : FireElementContext.resolveForSpell(caster, this).applySupportedDamage(power, caster) * 0.2F;
   }

   private float getRadius(LivingEntity caster) {
      if (caster == null) {
         return 3.0F;
      } else {
         float schoolPower = (float)this.getSchoolType().getPowerFor(caster);
         float bonus = FireElementContext.resolveForSpell(caster, this).getSpellPowerBonus(caster);
         return 3.0F * schoolPower + bonus / 5.0F;
      }
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_CHARGED_CAST;
   }
}
