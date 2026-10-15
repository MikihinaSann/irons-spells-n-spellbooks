package com.somake.somakespells.spells.spirit;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.entity.SoulfallJudgmentControllerEntity;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class SoulfallJudgmentSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "soulfall_judgment");
   private final ResourceLocation spiritSchool = MagicFromTheEastCompat.spiritSchoolOrEvocation();
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(this.spiritSchool)
      .setMaxLevel(8)
      .setCooldownSeconds(24.0)
      .build();

   public SoulfallJudgmentSpell() {
      this.baseManaCost = 52;
      this.manaCostPerLevel = 7;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 2;
      this.castTime = 16;
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

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_RAISED_HAND;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getImpactDamage(spellLevel, caster), 1)}));
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel && caster.onGround() && !caster.isPassenger() && !caster.isFallFlying()) {
         RedSoulVisuals.spawnSpiritCastPulse(serverLevel, caster);
         SoulfallJudgmentControllerEntity controller = new SoulfallJudgmentControllerEntity(
            (EntityType<?>)ModEntityTypes.SOULFALL_JUDGMENT_CONTROLLER.get(),
            serverLevel,
            caster,
            this.getImpactDamage(spellLevel, caster),
            this.getBladeDamage(spellLevel, caster),
            RedSoulHelper.isActive(caster)
         );
         serverLevel.addFreshEntity(controller);
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private float getImpactDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 1.05F + 4.0F;
   }

   private float getBladeDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 0.32F;
   }
}
