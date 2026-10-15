package com.somake.somakespells.spells.fire;

import com.somake.somakespells.compat.BornInChaosCompat;
import com.somake.somakespells.registries.ModMobEffects;
import com.somake.somakespells.util.ElementalChargeActivation;
import com.somake.somakespells.util.FireElementContext;
import dev.shadowsoffire.apothic_attributes.api.ALObjects.Attributes;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.Level;

public class CombustionSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "combustion");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(40.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      FireElementContext context = FireElementContext.resolveForSpell(caster, this);
      String attributeKey = "attribute.irons_spellbooks.fire_spell_power";
      if (context.isSoulFire()) {
         attributeKey = "attribute.somakespells.soul_fire_spell_power";
      } else if (context.isInfernalFire()) {
         attributeKey = "attribute.somakespells.infernal_fire_spell_power";
      }

      return List.of(
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getSpellPower(spellLevel, caster) * 20.0F, 1)}),
         Component.translatable(
            "attribute.modifier.plus.1",
            new Object[]{Utils.stringTruncation(this.getPercentSpellPower(spellLevel, caster), 0), Component.translatable(attributeKey)}
         ),
         Component.translatable(
            "attribute.modifier.plus.0",
            new Object[]{
               Utils.stringTruncation(this.getFireDamage(spellLevel, caster), 0),
               Component.translatable(((Attribute)Attributes.FIRE_DAMAGE.value()).getDescriptionId())
            }
         )
      );
   }

   public CombustionSpell() {
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

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      FireElementContext context = FireElementContext.resolveForSpell(entity, this);
      if (context.isSoulFire()) {
         ElementalChargeActivation.activate(level, this, spellLevel, entity, ModMobEffects.SOUL_COMBUSTIONED, 2462666);
      } else if (context.isInfernalFire() && BornInChaosCompat.isLoaded()) {
         ElementalChargeActivation.activate(level, this, spellLevel, entity, ModMobEffects.INFERNAL_COMBUSTIONED, 3524643);
      } else {
         ElementalChargeActivation.activate(level, this, spellLevel, entity, ModMobEffects.COMBUSTIONED, 12546335);
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getPercentSpellPower(int spellLevel, LivingEntity entity) {
      return spellLevel * 0.05F * 100.0F;
   }

   private float getFireDamage(int spellLevel, LivingEntity entity) {
      return spellLevel * 5.0F;
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.SELF_CAST_ANIMATION;
   }
}
