package com.somake.somakespells.spells.holy;

import com.somake.somakespells.entity.SacredPhoenixBlessingControllerEntity;
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
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SacredPhoenixBlessingSpell extends AbstractSpell {
   private static final int TARGET_RANGE = 24;
   private static final int BASE_DURATION_TICKS = 120;
   private static final int DURATION_PER_LEVEL_TICKS = 20;
   private static final int BASE_STUN_TICKS = 60;
   private static final int STUN_REDUCTION_PER_LEVEL_TICKS = 4;
   private static final int PULSE_INTERVAL_TICKS = 24;
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "sacred_phoenix_blessing");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.HOLY_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(55.0)
      .build();

   public SacredPhoenixBlessingSpell() {
      this.baseManaCost = 90;
      this.manaCostPerLevel = 20;
      this.baseSpellPower = 8;
      this.spellPowerPerLevel = 1;
      this.castTime = 30;
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
      return Optional.of(SoundEvents.BEACON_POWER_SELECT);
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(SoundEvents.TOTEM_USE);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_RAISED_HAND;
   }

   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.ANIMATION_INSTANT_CAST;
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity caster, MagicData playerMagicData) {
      return level instanceof ServerLevel serverLevel && SacredPhoenixBlessingControllerEntity.hasActiveLink(serverLevel, caster)
         ? false
         : Utils.preCastTargetHelper(level, caster, playerMagicData, this, 24, 0.35F, true, target -> isValidAlly(caster, target));
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getPulseDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getDuration(spellLevel), 1)}),
         Component.translatable("ui.somakespells.pulse_interval", new Object[]{Utils.timeFromTicks(24.0F, 1)}),
         Component.translatable("ui.somakespells.stun_duration", new Object[]{Utils.timeFromTicks(this.getStunDuration(spellLevel), 1)})
      );
   }

   public SpellDamageSource getDamageSource(Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker).setIFrames(0);
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      ICastData castData = playerMagicData.getAdditionalCastData();
      if (level instanceof ServerLevel serverLevel && castData instanceof TargetEntityCastData targetData) {
         LivingEntity target = targetData.getTarget(serverLevel);
         if (isValidAlly(caster, target)) {
            SacredPhoenixBlessingControllerEntity controller = new SacredPhoenixBlessingControllerEntity(
               serverLevel,
               caster,
               target,
               spellLevel,
               this.getDuration(spellLevel),
               this.getStunDuration(spellLevel),
               this.getPulseDamage(spellLevel, caster),
               this.getRadius(spellLevel)
            );
            if (serverLevel.addFreshEntity(controller)) {
               caster.startRiding(controller, true);
            }
         }
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private static boolean isValidAlly(LivingEntity caster, LivingEntity target) {
      if (target == null || target == caster || !target.isAlive() || target.isSpectator()) {
         return false;
      } else {
         return caster.getTeam() == null && target.getTeam() == null ? caster instanceof Player && target instanceof Player : caster.isAlliedTo(target);
      }
   }

   public int getDuration(int spellLevel) {
      return 120 + (spellLevel - 1) * 20;
   }

   public int getStunDuration(int spellLevel) {
      return Math.max(40, 60 - (spellLevel - 1) * 4);
   }

   public int getPulseInterval() {
      return 24;
   }

   public float getPulseDamage(int spellLevel, LivingEntity caster) {
      return 2.0F + this.getSpellPower(spellLevel, caster) * 0.7F;
   }

   public float getRadius(int spellLevel) {
      return 4.0F + (spellLevel - 1) * 0.5F;
   }
}
