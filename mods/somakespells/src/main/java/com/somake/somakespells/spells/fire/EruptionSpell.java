package com.somake.somakespells.spells.fire;

import com.somake.somakespells.entity.EruptionEntity;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class EruptionSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "eruption");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(15.0)
      .build();

   public EruptionSpell() {
      this.manaCostPerLevel = 20;
      this.baseManaCost = 60;
      this.baseSpellPower = 15;
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
      return Optional.of(SoundEvents.FIRECHARGE_USE);
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(caster), 1)})
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      float damage = this.getDamage(spellLevel, caster);
      float radius = this.getRadius(caster);
      FireElementContext context = FireElementContext.resolveForSpell(caster, this);
      HitResult hitResult = Utils.raycastForEntity(
         level, caster, caster.getEyePosition(), caster.getEyePosition().add(caster.getLookAngle().scale(32.0)), true, 0.0F, e -> false
      );
      Vec3 targetPos = hitResult.getLocation();
      EruptionEntity eruption = new EruptionEntity(level, caster, damage);
      eruption.setRadius(radius);
      eruption.setPos(targetPos.x, targetPos.y, targetPos.z);
      eruption.setIsSoulFire(context.isSoulFire());
      eruption.setIsInfernalFire(context.isInfernalFire());
      level.addFreshEntity(eruption);
      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private float getRadius(LivingEntity caster) {
      if (caster == null) {
         return 3.0F;
      } else {
         float schoolPower = (float)this.getSchoolType().getPowerFor(caster);
         float bonus = FireElementContext.resolveForSpell(caster, this).getSpellPowerBonus(caster);
         return 3.0F * schoolPower + bonus / 5.0F;
      }
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      float power = this.getSpellPower(spellLevel, caster);
      return caster == null ? power : FireElementContext.resolveForSpell(caster, this).applySupportedDamage(power, caster);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_CHARGED_CAST;
   }
}
