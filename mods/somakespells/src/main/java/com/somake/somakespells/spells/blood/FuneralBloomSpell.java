package com.somake.somakespells.spells.blood;

import com.somake.somakespells.config.Config;
import com.somake.somakespells.entity.FuneralBloomControllerEntity;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FuneralBloomSpell extends AbstractSpell {
   private static final double RANGE = 32.0;
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "funeral_bloom");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(42.0)
      .build();

   public FuneralBloomSpell() {
      this.baseManaCost = 110;
      this.manaCostPerLevel = 24;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 2;
      this.castTime = 42;
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

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_CHARGED_CAST;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)SoundRegistry.HEARTSTOP_CAST.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(SoundEvents.WITHER_SPAWN);
   }

   public boolean allowLooting() {
      return !(Boolean)Config.DISABLE_WITHERED_ROSE_SPELLS_CRAFTING_AND_LOOTING.get();
   }

   public boolean allowCrafting() {
      return !(Boolean)Config.DISABLE_WITHERED_ROSE_SPELLS_CRAFTING_AND_LOOTING.get();
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getImpactDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.duration", new Object[]{Utils.stringTruncation(this.getDuration(spellLevel) / 20.0F, 1)}),
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getWitherDuration(spellLevel), 1)})
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      HitResult hit = Utils.raycastForEntity(
         level, caster, caster.getEyePosition(), caster.getEyePosition().add(caster.getLookAngle().scale(32.0)), true, 0.0F, entity -> false
      );
      Vec3 impact = FuneralBloomControllerEntity.groundPosition(level, hit.getLocation());
      FuneralBloomControllerEntity storm = new FuneralBloomControllerEntity(
         level,
         caster,
         this.getRadius(spellLevel, caster),
         this.getDuration(spellLevel),
         this.getWaveInterval(spellLevel),
         this.getTelegraphTicks(spellLevel),
         this.getMaxTargets(spellLevel),
         this.getImpactDamage(spellLevel, caster),
         this.getWitherDuration(spellLevel),
         this.getWitherAmplifier(spellLevel)
      );
      storm.setPos(impact);
      level.addFreshEntity(storm);
      level.playSound(null, impact.x, impact.y, impact.z, SoundEvents.WITHER_AMBIENT, SoundSource.PLAYERS, 1.05F, 0.72F);
      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private float getRadius(int spellLevel, LivingEntity caster) {
      float bloodPower = caster == null ? 1.0F : (float)this.getSchoolType().getPowerFor(caster);
      return Mth.clamp(9.5F + spellLevel * 0.82F + Math.max(0.0F, bloodPower - 1.0F) * 0.65F, 10.0F, 15.0F);
   }

   private int getDuration(int spellLevel) {
      return 240 + (Mth.clamp(spellLevel, 1, 5) - 1) * 20;
   }

   private int getWaveInterval(int spellLevel) {
      return 18 - Mth.clamp(spellLevel, 1, 5);
   }

   private int getTelegraphTicks(int spellLevel) {
      return Math.max(9, 13 - spellLevel / 2);
   }

   private int getMaxTargets(int spellLevel) {
      return 4 + Mth.clamp(spellLevel, 1, 5);
   }

   private float getImpactDamage(int spellLevel, LivingEntity caster) {
      return 1.5F + this.getSpellPower(spellLevel, caster) * 0.75F;
   }

   private int getWitherDuration(int spellLevel) {
      return 60 + Mth.clamp(spellLevel, 1, 5) * 12;
   }

   private int getWitherAmplifier(int spellLevel) {
      return spellLevel >= 5 ? 1 : 0;
   }
}
