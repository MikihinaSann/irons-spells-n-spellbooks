package com.somake.somakespells.spells.spirit;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.entity.ProcessionOfSoulsControllerEntity;
import com.somake.somakespells.registries.ModEntityTypes;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ProcessionOfSoulsSpell extends AbstractSpell {
   private static final float SEGMENT_SPACING = 1.75F;
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "procession_of_souls");
   private final ResourceLocation spiritSchool = MagicFromTheEastCompat.spiritSchoolOrEvocation();
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(this.spiritSchool)
      .setMaxLevel(8)
      .setCooldownSeconds(14.0)
      .build();

   public ProcessionOfSoulsSpell() {
      this.manaCostPerLevel = 6;
      this.baseManaCost = 34;
      this.baseSpellPower = 7;
      this.spellPowerPerLevel = 1;
      this.castTime = 18;
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
      return MagicFromTheEastCompat.getSpiritCastSound();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return MagicFromTheEastCompat.getSoundEvent("cast.generic.soul_cast");
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.TOUCH_GROUND_ANIMATION;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getStrikeDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.projectile_count", new Object[]{Utils.stringTruncation(this.getSegmentCount(spellLevel), 0)})
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         RedSoulVisuals.spawnSpiritCastPulse(serverLevel, caster);
         ProcessionOfSoulsControllerEntity controller = new ProcessionOfSoulsControllerEntity(
            (EntityType<?>)ModEntityTypes.PROCESSION_OF_SOULS_CONTROLLER.get(),
            serverLevel,
            caster,
            this.getStrikeDamage(spellLevel, caster),
            this.getSegmentCount(spellLevel),
            1.75F,
            RedSoulHelper.isActive(caster)
         );
         serverLevel.addFreshEntity(controller);
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private int getSegmentCount(int spellLevel) {
      return Mth.clamp(6 + spellLevel / 2, 6, 10);
   }

   private float getStrikeDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 0.95F + 2.0F;
   }
}
