package com.somake.somakespells.spells.fire;

import com.somake.somakespells.entity.FirestormVortexEntity;
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
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FirestormVortexSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "firestorm_vortex");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(18.0)
      .build();

   public FirestormVortexSpell() {
      this.manaCostPerLevel = 18;
      this.baseManaCost = 55;
      this.baseSpellPower = 12;
      this.spellPowerPerLevel = 3;
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

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of(SoundEvents.BLAZE_SHOOT);
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamagePerPulse(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(caster), 1)}),
         Component.translatable("ui.irons_spellbooks.duration", new Object[]{Utils.stringTruncation(this.getLifetimeTicks(spellLevel) / 20.0F, 1)})
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      FireElementContext context = FireElementContext.resolveForSpell(caster, this);
      float damagePerPulse = this.getDamagePerPulse(spellLevel, caster);
      float radius = this.getRadius(caster);
      int lifetimeTicks = this.getLifetimeTicks(spellLevel);
      HitResult hitResult = Utils.raycastForEntity(
         level, caster, caster.getEyePosition(), caster.getEyePosition().add(caster.getLookAngle().scale(28.0)), true, 0.0F, entity -> false
      );
      Vec3 targetPos = hitResult.getLocation();
      FirestormVortexEntity vortex = new FirestormVortexEntity(level, caster, damagePerPulse, radius, lifetimeTicks);
      vortex.setVariant(1);
      vortex.setIsSoulFire(context.isSoulFire());
      vortex.setIsInfernalFire(context.isInfernalFire());
      vortex.setPos(targetPos.x, targetPos.y, targetPos.z);
      level.addFreshEntity(vortex);
      level.playSound(null, targetPos.x, targetPos.y, targetPos.z, SoundEvents.BLAZE_AMBIENT, SoundSource.PLAYERS, 1.0F, 0.8F);
      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private float getRadius(LivingEntity caster) {
      if (caster == null) {
         return 3.5F;
      } else {
         float schoolPower = (float)this.getSchoolType().getPowerFor(caster);
         float bonus = FireElementContext.resolveForSpell(caster, this).getSpellPowerBonus(caster);
         return Mth.clamp(2.8F + schoolPower * 1.2F + bonus * 0.12F, 3.0F, 7.5F);
      }
   }

   private float getDamagePerPulse(int spellLevel, LivingEntity caster) {
      float spellPower = this.getSpellPower(spellLevel, caster);
      if (caster != null) {
         spellPower = FireElementContext.resolveForSpell(caster, this).applySupportedDamage(spellPower, caster);
      }

      return 2.5F + spellPower * 0.45F;
   }

   private int getLifetimeTicks(int spellLevel) {
      int clampedLevel = Mth.clamp(spellLevel, 1, 5);
      return 140 + (clampedLevel - 1) * 30;
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_CHARGED_CAST;
   }
}
