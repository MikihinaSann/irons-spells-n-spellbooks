package com.somake.somakespells.spells.spirit;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.entity.SpectralRondoControllerEntity;
import com.somake.somakespells.util.RedSoulHelper;
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

public class SpectralRondoSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "spectral_rondo");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(MagicFromTheEastCompat.spiritSchoolOrEvocation())
      .setMaxLevel(8)
      .setCooldownSeconds(20.0)
      .build();

   public SpectralRondoSpell() {
      this.baseManaCost = 46;
      this.manaCostPerLevel = 6;
      this.baseSpellPower = 9;
      this.spellPowerPerLevel = 2;
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
      return MagicFromTheEastCompat.getSpiritCastSound();
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_INSTANT_CAST;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.somakespells.damage_per_hit", new Object[]{Utils.stringTruncation(this.getHitDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.somakespells.hits", new Object[]{Utils.stringTruncation(2.0, 0)})
      );
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity caster, MagicData playerMagicData) {
      return caster.onGround() && !caster.isPassenger() && !caster.isFallFlying();
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         RedSoulVisuals.spawnSpiritCastPulse(serverLevel, caster);
         serverLevel.addFreshEntity(
            new SpectralRondoControllerEntity(serverLevel, caster, this.getHitDamage(spellLevel, caster), RedSoulHelper.isActive(caster))
         );
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private float getHitDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 0.42F + 1.5F;
   }
}
