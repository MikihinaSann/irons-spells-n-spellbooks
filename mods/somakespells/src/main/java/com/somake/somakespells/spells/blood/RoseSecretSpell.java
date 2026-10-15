package com.somake.somakespells.spells.blood;

import com.somake.somakespells.registries.ModMobEffects;
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
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class RoseSecretSpell extends AbstractSpell {
   private static final int BASE_DURATION_SECONDS = 6;
   private static final int DURATION_PER_LEVEL_SECONDS = 1;
   private static final int MAX_DURATION_TICKS = 500;
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "rose_secret");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(240.0)
      .build();

   public RoseSecretSpell() {
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 6;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 120;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getEffectDuration(spellLevel, caster), 1)})
      );
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

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.SELF_CAST_ANIMATION;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.empty();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.HEARTSTOP_CAST.get());
   }

   public boolean allowLooting() {
      return false;
   }

   public boolean allowCrafting() {
      return false;
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      entity.addEffect(new MobEffectInstance(ModMobEffects.ROSE_SECRET, this.getEffectDuration(spellLevel, entity), spellLevel - 1, false, false, true));
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private int getEffectDuration(int spellLevel, LivingEntity caster) {
      return Mth.clamp(Math.round(this.getSpellPower(spellLevel, caster) * 20.0F), 1, 500);
   }
}
