package com.somake.somakespells.spells.sound;

import com.somake.somakespells.compat.TunesCompat;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class SlumberMelodySpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "slumber_melody");
   private final DefaultConfig defaultConfig;

   public SlumberMelodySpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 2;
      this.castTime = 40;
      this.baseManaCost = 25;
      this.defaultConfig = new DefaultConfig()
         .setMinRarity(SpellRarity.COMMON)
         .setSchoolResource(TunesCompat.schoolOrEvocation())
         .setMaxLevel(5)
         .setCooldownSeconds(15.0)
         .build();
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getDuration(spellLevel, caster), 1)}));
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
      return TunesCompat.getHarpWaveSound();
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 32, 0.35F);
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetData) {
         LivingEntity targetEntity = targetData.getTarget((ServerLevel)level);
         if (targetEntity != null) {
            Optional<Reference<MobEffect>> effect = BuiltInRegistries.MOB_EFFECT.getHolder(TunesCompat.SLEEPY_EFFECT_ID);
            if (effect.isPresent()) {
               targetEntity.addEffect(new MobEffectInstance((Holder)effect.get(), this.getDuration(spellLevel, entity), 0, false, true, true));
            }

            if (level instanceof ServerLevel serverLevel) {
               serverLevel.sendParticles(
                  TunesCompat.getMusicalParticle(),
                  targetEntity.getX(),
                  targetEntity.getY() + targetEntity.getEyeHeight(),
                  targetEntity.getZ(),
                  10,
                  0.5,
                  0.5,
                  0.5,
                  0.1
               );
            }
         }
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private int getDuration(int spellLevel, LivingEntity entity) {
      return 100 + spellLevel * 40;
   }
}
