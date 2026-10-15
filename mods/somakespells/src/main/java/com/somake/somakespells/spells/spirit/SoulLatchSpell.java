package com.somake.somakespells.spells.spirit;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.entity.SoulLatchControllerEntity;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.util.RedSoulHelper;
import com.somake.somakespells.util.RedSoulVisuals;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class SoulLatchSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "soul_latch");
   private final ResourceLocation spiritSchool = MagicFromTheEastCompat.spiritSchoolOrEvocation();
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(this.spiritSchool)
      .setMaxLevel(8)
      .setCooldownSeconds(22.0)
      .build();

   public SoulLatchSpell() {
      this.manaCostPerLevel = 7;
      this.baseManaCost = 46;
      this.baseSpellPower = 6;
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

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_ANIMATION;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getBurstDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getLatchDuration(), 1)})
      );
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity caster, MagicData playerMagicData) {
      return Utils.preCastTargetHelper(level, caster, playerMagicData, this, 24, 0.35F);
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      ICastData iCastData = playerMagicData.getAdditionalCastData();
      if (level instanceof ServerLevel serverLevel && iCastData instanceof TargetEntityCastData targetData) {
         RedSoulVisuals.spawnSpiritCastPulse(serverLevel, caster);
         LivingEntity targetEntity = targetData.getTarget(serverLevel);
         if (targetEntity != null && targetEntity != caster && targetEntity.isAlive()) {
            SoulLatchControllerEntity controller = new SoulLatchControllerEntity(
               (EntityType<?>)ModEntityTypes.SOUL_LATCH_CONTROLLER.get(),
               serverLevel,
               caster,
               targetEntity,
               this.getBurstDamage(spellLevel, caster),
               this.getLatchDuration(),
               RedSoulHelper.isActive(caster)
            );
            serverLevel.addFreshEntity(controller);
         }
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private int getLatchDuration() {
      return 54;
   }

   private float getBurstDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }
}
