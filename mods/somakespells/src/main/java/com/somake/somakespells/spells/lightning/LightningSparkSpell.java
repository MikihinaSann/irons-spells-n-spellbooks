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

public class LightningSparkSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "lightning_spark");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(1.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}));
   }

   public LightningSparkSpell() {
      this.manaCostPerLevel = 2;
      this.baseSpellPower = 4;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 10;
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
      CustomSparkEntity spark = new CustomSparkEntity(entity, level);
      spark.setSpell(this);
      spark.setPos(entity.getX(), entity.getEyeY() - 0.1, entity.getZ());
      spark.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0.0F, 3.0F, 1.0F);
      float damage = this.getDamage(spellLevel, entity);
      spark.setDamage(damage);
      spark.setAreaDamage(damage);
      spark.setAreaRadius(3.0F);
      level.addFreshEntity(spark);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_INSTANT_CAST;
   }
}
