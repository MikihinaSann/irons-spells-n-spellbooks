package com.somake.somakespells.spells.ender;

import com.somake.somakespells.entity.AbyssalOrbEntity;
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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class AbyssalOrbsSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "abyssal_orbs");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
      .setMaxLevel(8)
      .setCooldownSeconds(10.0)
      .build();

   public AbyssalOrbsSpell() {
      this.manaCostPerLevel = 5;
      this.baseManaCost = 30;
      this.baseSpellPower = 6;
      this.spellPowerPerLevel = 2;
      this.castTime = 40;
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
      return Optional.of(SoundEvents.ENDERMAN_TELEPORT);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_CHARGED_CAST;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.projectile_count", new Object[]{5})
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      int projectiles = 5;
      float damage = this.getDamage(spellLevel, caster);
      Vec3 start = caster.getEyePosition();
      Vec3 look = caster.getLookAngle();
      Vec3 end = start.add(look.scale(32.0));
      HitResult hitResult = level.clip(new ClipContext(start, end, Block.COLLIDER, Fluid.NONE, caster));
      Vec3 targetPos = hitResult.getLocation();
      double radius = 3.0;
      double height = 15.0;

      for (int i = 0; i < projectiles; i++) {
         double angle = (Math.PI * 2) * i / projectiles;
         double xOffset = radius * Math.cos(angle);
         double zOffset = radius * Math.sin(angle);
         Vec3 spawnPos = targetPos.add(xOffset, height, zOffset);
         AbyssalOrbEntity orb = new AbyssalOrbEntity(level, caster);
         orb.setDamage(damage);
         orb.setPos(spawnPos);
         orb.shoot(new Vec3(0.0, -1.0, 0.0));
         level.addFreshEntity(orb);
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 0.75F;
   }
}
