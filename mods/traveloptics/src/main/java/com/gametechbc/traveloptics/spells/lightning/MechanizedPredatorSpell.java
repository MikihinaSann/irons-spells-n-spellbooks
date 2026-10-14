package com.gametechbc.traveloptics.spells.lightning;

import com.gametechbc.traveloptics.api.spells.AbstractUniqueSpell;
import com.gametechbc.traveloptics.api.utils.SummonCheckHelper;
import com.gametechbc.traveloptics.config.SpellsConfig;
import com.gametechbc.traveloptics.entity.summons.SummonedTheProwler;
import com.gametechbc.traveloptics.entity.summons.SummonedTheWatcher;
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
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
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
public class MechanizedPredatorSpell extends AbstractUniqueSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "mechanized_predator");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(540.0)
      .build();

   public MechanizedPredatorSpell() {
      super();
      this.manaCostPerLevel = 100;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 80;
      this.baseManaCost = 100;
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
      return Optional.of((SoundEvent)ModSounds.HARBINGER_CHARGE_PREPARE.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)ModSounds.WATCHER_DEATH.get());
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      if (entity instanceof Player player
         && (Boolean)SpellsConfig.limitGroupSummons.get()
         && SummonCheckHelper.hasActiveSummons(player, 128.0, SummonTypes.getGroupSummons())) {
         player.displayClientMessage(Component.translatable("spell.traveloptics.summon_group.warning").withStyle(ChatFormatting.RED), true);
         return false;
      } else {
         return true;
      }
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      super.onServerCastTick(level, spellLevel, entity, playerMagicData);
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      int summonTime = 12000;
      int koboletonCount = (int)this.getWatcherCount(spellLevel);
      double radius = 3.5;
      double angleIncrement = (Math.PI * 2) / koboletonCount;

      for (int i = 0; i < koboletonCount; i++) {
         double angle = i * angleIncrement;
         double xOffset = radius * Math.cos(angle);
         double zOffset = radius * Math.sin(angle);
         SummonedTheWatcher watcher = new SummonedTheWatcher(world, entity);
         watcher.setPos(entity.getX() + xOffset, entity.getY(), entity.getZ() + zOffset);
         watcher.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(this.getWatcherDamage(spellLevel, entity));
         watcher.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(this.getWatcherHealth(spellLevel));
         watcher.setHealth(watcher.getMaxHealth());
         world.addFreshEntity(watcher);
         watcher.addEffect(new MobEffectInstance(TOEffects.MECHANIZED_PREDATOR_TIMER, summonTime, 0, false, false, false));
      }

      SummonedTheProwler prowler = new SummonedTheProwler(world, entity);
      prowler.setPos(entity.position());
      prowler.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(this.getProwlerDamage(spellLevel, entity));
      prowler.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(this.getProwlerHealth(spellLevel));
      prowler.setHealth(prowler.getMaxHealth());
      world.addFreshEntity(prowler);
      prowler.addEffect(new MobEffectInstance(TOEffects.MECHANIZED_PREDATOR_TIMER, summonTime, 0, false, false, false));
      int effectAmplifier = 0;
      if (entity.hasEffect(TOEffects.MECHANIZED_PREDATOR_TIMER)) {
         effectAmplifier += entity.getEffect(TOEffects.MECHANIZED_PREDATOR_TIMER).getAmplifier() + 1;
      }

      entity.addEffect(new MobEffectInstance(TOEffects.MECHANIZED_PREDATOR_TIMER, summonTime, effectAmplifier, false, false, true));
      CameraShakeManager.addCameraShake(new CameraShakeData(35, entity.position(), 25.0F));
      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   private float getWatcherCount(int spellLevel) {
      return spellLevel;
   }

   private float getWatcherHealth(int spellLevel) {
      return spellLevel * 5.0F;
   }

   private float getWatcherDamage(int spellLevel, LivingEntity caster) {
      float baseDamage = this.getSpellPower(spellLevel, caster);
      double summonedDamageMultiplier = 1.0;
      if (caster.getAttributes().hasAttribute(AttributeRegistry.SUMMON_DAMAGE)) {
         summonedDamageMultiplier = caster.getAttributeValue(AttributeRegistry.SUMMON_DAMAGE);
      }

      return (float)(baseDamage * summonedDamageMultiplier);
   }

   private String getWatcherDamageText(int spellLevel, LivingEntity caster) {
      if (caster != null) {
         float baseDamage = this.getSpellPower(spellLevel, caster);
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

   private float getProwlerHealth(int spellLevel) {
      return 60.0F + spellLevel * 20.0F;
   }

   private float getProwlerDamage(int spellLevel, LivingEntity caster) {
      float baseDamage = 5.0F + this.getSpellPower(spellLevel, caster) * 2.0F;
      double summonedDamageMultiplier = 1.0;
      if (caster.getAttributes().hasAttribute(AttributeRegistry.SUMMON_DAMAGE)) {
         summonedDamageMultiplier = caster.getAttributeValue(AttributeRegistry.SUMMON_DAMAGE);
      }

      return (float)(baseDamage * summonedDamageMultiplier);
   }

   private String getProwlerDamageText(int spellLevel, LivingEntity caster) {
      if (caster != null) {
         float baseDamage = 5.0F + this.getSpellPower(spellLevel, caster) * 2.0F;
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
         Component.translatable("ui.traveloptics.summon_count", new Object[]{this.getWatcherCount(spellLevel) + 1.0F}),
         Component.translatable("ui.traveloptics.watcher_hp", new Object[]{this.getWatcherHealth(spellLevel)}),
         Component.translatable("ui.traveloptics.watcher_damage", new Object[]{this.getWatcherDamageText(spellLevel, caster)}),
         Component.translatable("ui.traveloptics.prowler_hp", new Object[]{this.getProwlerHealth(spellLevel)}),
         Component.translatable("ui.traveloptics.prowler_damage", new Object[]{this.getProwlerDamageText(spellLevel, caster)}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }
}
