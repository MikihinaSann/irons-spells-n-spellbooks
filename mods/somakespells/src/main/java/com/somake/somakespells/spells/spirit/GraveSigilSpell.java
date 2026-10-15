package com.somake.somakespells.spells.spirit;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.entity.GraveSigilControllerEntity;
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
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class GraveSigilSpell extends AbstractSpell {
   private static final int TOTAL_SIGILS = 9;
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "grave_sigil");
   private final ResourceLocation spiritSchool = MagicFromTheEastCompat.spiritSchoolOrEvocation();
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(this.spiritSchool)
      .setMaxLevel(8)
      .setCooldownSeconds(18.0)
      .build();

   public GraveSigilSpell() {
      this.manaCostPerLevel = 7;
      this.baseManaCost = 42;
      this.baseSpellPower = 6;
      this.spellPowerPerLevel = 1;
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
         Component.translatable("ui.irons_spellbooks.projectile_count", new Object[]{Utils.stringTruncation(9.0, 0)})
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         RedSoulVisuals.spawnSpiritCastPulse(serverLevel, caster);
         Vec3 targetPoint = this.getTargetPoint(level, caster);
         Vec3 look = caster.getLookAngle();
         GraveSigilControllerEntity controller = new GraveSigilControllerEntity(
            (EntityType<?>)ModEntityTypes.GRAVE_SIGIL_CONTROLLER.get(),
            serverLevel,
            caster,
            targetPoint,
            look,
            this.getStrikeDamage(spellLevel, caster),
            RedSoulHelper.isActive(caster)
         );
         serverLevel.addFreshEntity(controller);
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private Vec3 getTargetPoint(Level level, LivingEntity caster) {
      HitResult hitResult = Utils.raycastForEntity(
         level, caster, caster.getEyePosition(), caster.getEyePosition().add(caster.getLookAngle().scale(24.0)), true, 0.0F, entity -> false
      );
      return hitResult.getLocation();
   }

   private float getStrikeDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) + 2.0F;
   }
}
