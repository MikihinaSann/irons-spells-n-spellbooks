package com.somake.somakespells.spells.holy;

import com.somake.somakespells.capabilities.ConnectionKind;
import com.somake.somakespells.event.ConnectionEventHandler;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastData;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class GuardianConnectionSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "guardian_connection");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.HOLY_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(45.0)
      .build();

   public GuardianConnectionSpell() {
      this.baseManaCost = 60;
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 0;
      this.spellPowerPerLevel = 0;
      this.castTime = 20;
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

   public int getRecastCount(int spellLevel, LivingEntity entity) {
      return 0;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getDuration(spellLevel), 1)}),
         Component.translatable(
            "ui.somakespells.connection_max_distance", new Object[]{Utils.stringTruncation(ConnectionEventHandler.getConnectionMaxDistance(spellLevel), 0)}
         )
      );
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_ANIMATION;
   }

   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.ANIMATION_INSTANT_CAST;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.empty();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.empty();
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity caster, MagicData playerMagicData) {
      return Utils.preCastTargetHelper(level, caster, playerMagicData, this, 24, 0.35F);
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      ICastData iCastData = playerMagicData.getAdditionalCastData();
      if (level instanceof ServerLevel serverLevel && iCastData instanceof TargetEntityCastData targetData) {
         LivingEntity targetEntity = targetData.getTarget(serverLevel);
         if (targetEntity != null && targetEntity != caster && targetEntity.isAlive()) {
            ConnectionEventHandler.createLink(serverLevel, caster, caster, targetEntity, spellLevel, this.getDuration(spellLevel), ConnectionKind.GUARDIAN);
            super.onCast(level, spellLevel, caster, castSource, playerMagicData);
         } else {
            super.onCast(level, spellLevel, caster, castSource, playerMagicData);
         }
      } else {
         super.onCast(level, spellLevel, caster, castSource, playerMagicData);
      }
   }

   public int getDuration(int spellLevel) {
      return 160 + spellLevel * 40;
   }
}
