package com.somake.somakespells.spells.lightning;

import com.somake.somakespells.entity.CustomSparkEntity;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class LightningSwarmSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "lightning_swarm");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(20.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.projectile_count", new Object[]{this.getCount(spellLevel)})
      );
   }

   public LightningSwarmSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 5;
      this.spellPowerPerLevel = 0;
      this.castTime = 0;
      this.baseManaCost = 60;
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.empty();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.LIGHTNING_CAST.get());
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      int count = this.getCount(spellLevel);
      float damage = this.getDamage(spellLevel, entity);
      int offset = 360 / count;
      Vec3 center = entity.getEyePosition().add(0.0, -0.75, 0.0);

      for (int i = 0; i < count; i++) {
         Vec3 motion = new Vec3(0.0, 0.0, 1.0);
         motion = motion.xRot(-0.2F);
         motion = motion.yRot(offset * i * (float) (Math.PI / 180.0));
         motion = motion.normalize().scale(0.8);
         CustomSparkEntity spark = new CustomSparkEntity(entity, level);
         spark.setSpell(this);
         Vec3 spawnPos = center.add(new Vec3(0.0, 0.0, 1.5).yRot(offset * i * (float) (Math.PI / 180.0)));
         spark.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
         spark.setDeltaMovement(motion);
         spark.shoot(motion.x, motion.y, motion.z, 0.8F, 0.0F);
         spark.setDamage(damage);
         spark.setAreaDamage(damage);
         spark.setAreaRadius(3.0F);
         spark.setShouldChain(false);
         level.addFreshEntity(spark);
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity);
   }

   private int getCount(int spellLevel) {
      return 5 + spellLevel;
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.TOUCH_GROUND_ANIMATION;
   }
}
