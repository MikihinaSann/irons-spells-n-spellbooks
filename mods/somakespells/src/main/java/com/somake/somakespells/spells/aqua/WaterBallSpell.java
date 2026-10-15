package com.somake.somakespells.spells.aqua;

import com.somake.somakespells.entity.WaterBallEntity;
import com.somake.somakespells.registries.ModSchools;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class WaterBallSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "water_ball");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(ModSchools.AQUA.getId())
      .setMaxLevel(5)
      .setCooldownSeconds(10.0)
      .build();

   public WaterBallSpell() {
      this.baseManaCost = 50;
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
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
      return Optional.of(SoundEvents.WATER_AMBIENT);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_ANIMATION;
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity caster, MagicData playerMagicData) {
      if (!level.isClientSide) {
         WaterBallEntity ball = this.findWaterBall(level, caster);
         if (ball == null) {
            ball = new WaterBallEntity(level, caster, this.getDamage(spellLevel, caster), this.getRadius(spellLevel, caster));
            level.addFreshEntity(ball);
         } else {
            ball.setDamage(this.getDamage(spellLevel, caster));
            ball.setRadius(this.getRadius(spellLevel, caster));
         }
      }
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (!level.isClientSide) {
         WaterBallEntity ball = this.findWaterBall(level, caster);
         if (ball != null) {
            ball.setDamage(this.getDamage(spellLevel, caster));
            ball.setRadius(this.getRadius(spellLevel, caster));
            ball.launch(caster);
            level.playSound(null, caster.getX(), caster.getY(), caster.getZ(), SoundEvents.PLAYER_SPLASH_HIGH_SPEED, SoundSource.PLAYERS, 1.0F, 1.0F);
         } else {
            ball = new WaterBallEntity(level, caster, this.getDamage(spellLevel, caster), this.getRadius(spellLevel, caster));
            ball.launch(caster);
            level.addFreshEntity(ball);
         }
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private WaterBallEntity findWaterBall(Level level, LivingEntity caster) {
      List<WaterBallEntity> balls = level.getEntitiesOfClass(WaterBallEntity.class, caster.getBoundingBox().inflate(5.0));
      Iterator var4 = balls.iterator();
      return var4.hasNext() ? (WaterBallEntity)var4.next() : null;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(spellLevel, caster), 1)})
      );
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return 5.0F + 5.0F * this.getSpellPower(spellLevel, caster);
   }

   private float getRadius(int spellLevel, LivingEntity caster) {
      return 2.0F + this.getSpellPower(spellLevel, caster);
   }
}
