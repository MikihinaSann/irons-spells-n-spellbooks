package com.gametechbc.traveloptics.spells.fire;

import com.gametechbc.traveloptics.api.particle.CylinderParticleManager;
import com.gametechbc.traveloptics.api.particle.ParticleDirection;
import com.gametechbc.traveloptics.api.spells.AbstractUniqueSpell;
import com.gametechbc.traveloptics.api.utils.SummonCheckHelper;
import com.gametechbc.traveloptics.config.SpellsConfig;
import com.gametechbc.traveloptics.entity.summons.SummonedIgnitedBerserker;
import com.gametechbc.traveloptics.entity.summons.SummonedIgnitedRevenant;
import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.util.SummonTypes;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@AutoSpellConfig
public class IgnitedOnslaughtSpell extends AbstractUniqueSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "ignited_onslaught");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(360.0)
      .build();

   public IgnitedOnslaughtSpell() {
      super();
      this.manaCostPerLevel = 75;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 60;
      this.baseManaCost = 50;
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
      return Optional.of((SoundEvent)ModSounds.REVENANT_IDLE.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)ModSounds.REVENANT_HURT.get());
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      if (entity instanceof Player player
         && (Boolean)SpellsConfig.limitMinibossSummons.get()
         && SummonCheckHelper.hasActiveSummons(player, 128.0, SummonTypes.getMinibossSummons())) {
         player.displayClientMessage(Component.translatable("spell.traveloptics.summon_miniboss.warning").withStyle(ChatFormatting.RED), true);
         return false;
      } else {
         return true;
      }
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      CylinderParticleManager.spawnParticles(level, entity, 2, ParticleHelper.FIRE, ParticleDirection.UPWARD, 2.0, 2.0, -1.0);
      CylinderParticleManager.spawnParticles(level, entity, 7, ParticleTypes.SMOKE, ParticleDirection.UPWARD, 2.0, 2.0, -1.0);
      super.onServerCastTick(level, spellLevel, entity, playerMagicData);
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      int summonTime = 12000;
      double radius = 4.5;
      double leftAngle = Math.PI / 2;
      double rightAngle = -Math.PI / 2;
      double berserkerXOffset = radius * Math.cos(leftAngle);
      double berserkerZOffset = radius * Math.sin(leftAngle);
      SummonedIgnitedBerserker ignitedBerserker = new SummonedIgnitedBerserker(world, entity);
      ignitedBerserker.setPos(entity.getX() + berserkerXOffset, entity.getY(), entity.getZ() + berserkerZOffset);
      ignitedBerserker.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(this.getBerserkerDamage(spellLevel, entity));
      ignitedBerserker.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(this.getBerserkerHealth(spellLevel));
      ignitedBerserker.setHealth(ignitedBerserker.getMaxHealth());
      world.addFreshEntity(ignitedBerserker);
      ignitedBerserker.addEffect(new MobEffectInstance(TOEffects.IGNITED_ONSLAUGHT_TIMER, summonTime, 0, false, false, false));
      MagicManager.spawnParticles(
         world, ParticleHelper.FIRE, entity.getX() + berserkerXOffset, entity.getY() + 2.0, entity.getZ() + berserkerZOffset, 20, 0.0, 0.0, 0.0, 0.03, false
      );
      MagicManager.spawnParticles(
         world, ParticleTypes.SMOKE, entity.getX() + berserkerXOffset, entity.getY() + 2.0, entity.getZ() + berserkerZOffset, 45, 0.0, 0.0, 0.0, 0.03, false
      );
      double revenantXOffset = radius * Math.cos(rightAngle);
      double revenantZOffset = radius * Math.sin(rightAngle);
      SummonedIgnitedRevenant ignitedRevenant = new SummonedIgnitedRevenant(world, entity);
      ignitedRevenant.setPos(entity.getX() + revenantXOffset, entity.getY(), entity.getZ() + revenantZOffset);
      ignitedRevenant.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(this.getRevenantDamage(spellLevel, entity));
      ignitedRevenant.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(this.getRevenantHealth(spellLevel));
      ignitedRevenant.setHealth(ignitedRevenant.getMaxHealth());
      world.addFreshEntity(ignitedRevenant);
      ignitedRevenant.addEffect(new MobEffectInstance(TOEffects.IGNITED_ONSLAUGHT_TIMER, summonTime, 0, false, false, false));
      MagicManager.spawnParticles(
         world, ParticleHelper.FIRE, entity.getX() + revenantXOffset, entity.getY() + 2.0, entity.getZ() + revenantZOffset, 20, 0.0, 0.0, 0.0, 0.03, false
      );
      MagicManager.spawnParticles(
         world, ParticleTypes.SMOKE, entity.getX() + revenantXOffset, entity.getY() + 2.0, entity.getZ() + revenantZOffset, 45, 0.0, 0.0, 0.0, 0.03, false
      );
      int effectAmplifier = 0;
      if (entity.hasEffect(TOEffects.IGNITED_ONSLAUGHT_TIMER)) {
         effectAmplifier += entity.getEffect(TOEffects.IGNITED_ONSLAUGHT_TIMER).getAmplifier() + 1;
      }

      entity.addEffect(new MobEffectInstance(TOEffects.IGNITED_ONSLAUGHT_TIMER, summonTime, effectAmplifier, false, false, true));
      CameraShakeManager.addCameraShake(new CameraShakeData(30, entity.position(), 25.0F));
      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   private float getBerserkerHealth(int spellLevel) {
      return 25.0F + spellLevel * 8.0F;
   }

   private float getBerserkerDamage(int spellLevel, LivingEntity caster) {
      float baseDamage = 2.0F + this.getSpellPower(spellLevel, caster) * 1.2F;
      double summonedDamageMultiplier = 1.0;
      if (caster.getAttributes().hasAttribute(AttributeRegistry.SUMMON_DAMAGE)) {
         summonedDamageMultiplier = caster.getAttributeValue(AttributeRegistry.SUMMON_DAMAGE);
      }

      return (float)(baseDamage * summonedDamageMultiplier);
   }

   private String getBerserkerDamageText(int spellLevel, LivingEntity caster) {
      if (caster != null) {
         float baseDamage = 2.0F + this.getSpellPower(spellLevel, caster) * 1.2F;
         double summonedDamageMultiplier = caster.getAttributes().hasAttribute(AttributeRegistry.SUMMON_DAMAGE)
            ? caster.getAttributeValue(AttributeRegistry.SUMMON_DAMAGE)
            : 1.0;
         float finalDamage = (float)(baseDamage * summonedDamageMultiplier);
         String baseDamageText = Utils.stringTruncation(baseDamage, 1);
         String finalDamageText = Utils.stringTruncation(finalDamage, 1);
         return summonedDamageMultiplier > 1.0 ? String.format("%s -> %s", baseDamageText, finalDamageText) : baseDamageText;
      } else {
         return this.getSpellPower(spellLevel, caster) + "";
      }
   }

   private float getRevenantHealth(int spellLevel) {
      return 30.0F + spellLevel * 10.0F;
   }

   private float getRevenantDamage(int spellLevel, LivingEntity caster) {
      float baseDamage = 2.0F + this.getSpellPower(spellLevel, caster);
      double summonedDamageMultiplier = 1.0;
      if (caster.getAttributes().hasAttribute(AttributeRegistry.SUMMON_DAMAGE)) {
         summonedDamageMultiplier = caster.getAttributeValue(AttributeRegistry.SUMMON_DAMAGE);
      }

      return (float)(baseDamage * summonedDamageMultiplier);
   }

   private String getRevenantDamageText(int spellLevel, LivingEntity caster) {
      if (caster != null) {
         float baseDamage = 2.0F + this.getSpellPower(spellLevel, caster);
         double summonedDamageMultiplier = caster.getAttributes().hasAttribute(AttributeRegistry.SUMMON_DAMAGE)
            ? caster.getAttributeValue(AttributeRegistry.SUMMON_DAMAGE)
            : 1.0;
         float finalDamage = (float)(baseDamage * summonedDamageMultiplier);
         String baseDamageText = Utils.stringTruncation(baseDamage, 1);
         String finalDamageText = Utils.stringTruncation(finalDamage, 1);
         return summonedDamageMultiplier > 1.0 ? String.format("%s -> %s", baseDamageText, finalDamageText) : baseDamageText;
      } else {
         return this.getSpellPower(spellLevel, caster) + "";
      }
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.traveloptics.summon_count", new Object[]{2}),
         Component.translatable("ui.traveloptics.berserker_hp", new Object[]{this.getBerserkerHealth(spellLevel)}),
         Component.translatable("ui.traveloptics.berserker_damage", new Object[]{this.getBerserkerDamageText(spellLevel, caster)}),
         Component.translatable("ui.traveloptics.revenant_hp", new Object[]{this.getRevenantHealth(spellLevel)}),
         Component.translatable("ui.traveloptics.revenant_damage", new Object[]{this.getRevenantDamageText(spellLevel, caster)}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }
}
