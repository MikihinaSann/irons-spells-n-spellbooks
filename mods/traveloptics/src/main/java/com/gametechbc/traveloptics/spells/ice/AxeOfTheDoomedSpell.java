package com.gametechbc.traveloptics.spells.ice;

import com.gametechbc.traveloptics.api.particle.CylinderParticleManager;
import com.gametechbc.traveloptics.api.particle.ParticleDirection;
import com.gametechbc.traveloptics.api.spells.AbstractUniqueSpell;
import com.gametechbc.traveloptics.api.utils.SummonCheckHelper;
import com.gametechbc.traveloptics.config.SpellsConfig;
import com.gametechbc.traveloptics.entity.summons.SummonedAptrgangr;
import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.util.SummonTypes;
import com.github.L_Ender.cataclysm.init.ModParticle;
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
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@AutoSpellConfig
public class AxeOfTheDoomedSpell extends AbstractUniqueSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "axe_of_the_doomed");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(360.0)
      .build();

   public AxeOfTheDoomedSpell() {
      super();
      this.manaCostPerLevel = 100;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 80;
      this.baseManaCost = 120;
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
      return Optional.of((SoundEvent)ModSounds.APTRGANGR_IDLE.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)ModSounds.APTRGANGR_HURT.get());
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
      CylinderParticleManager.spawnParticles(level, entity, 3, (ParticleOptions)ModParticle.CURSED_FLAME.get(), ParticleDirection.UPWARD, 2.0, 2.0, -1.0);
      super.onServerCastTick(level, spellLevel, entity, playerMagicData);
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      int summonTime = 12000;
      double offset = 3.0;
      double angle = Math.toRadians(-entity.getYRot());
      double xOffset = offset * Math.cos(angle);
      double zOffset = offset * Math.sin(angle);
      SummonedAptrgangr aptrgangr = new SummonedAptrgangr(world, entity);
      aptrgangr.setPos(entity.getX() + xOffset, entity.getY(), entity.getZ() + zOffset);
      aptrgangr.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(this.getAptrgangrDamage(spellLevel, entity));
      aptrgangr.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(this.getAptrgangrHealth(spellLevel));
      aptrgangr.setHealth(aptrgangr.getMaxHealth());
      aptrgangr.finalizeSpawn((ServerLevel)world, world.getCurrentDifficultyAt(aptrgangr.getOnPos()), MobSpawnType.MOB_SUMMONED, null);
      world.addFreshEntity(aptrgangr);
      aptrgangr.addEffect(new MobEffectInstance(TOEffects.APTRGANGR_TIMER, summonTime, 0, false, false, false));
      MagicManager.spawnParticles(
         world,
         (ParticleOptions)ModParticle.CURSED_FLAME.get(),
         entity.getX() + xOffset,
         entity.getY() + 2.5,
         entity.getZ() + zOffset,
         45,
         0.0,
         0.0,
         0.0,
         0.03,
         false
      );
      int effectAmplifier = 0;
      if (entity.hasEffect(TOEffects.APTRGANGR_TIMER)) {
         effectAmplifier += entity.getEffect(TOEffects.APTRGANGR_TIMER).getAmplifier() + 1;
      }

      entity.addEffect(new MobEffectInstance(TOEffects.APTRGANGR_TIMER, summonTime, effectAmplifier, false, false, true));
      CameraShakeManager.addCameraShake(new CameraShakeData(35, entity.position(), 25.0F));
      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   private float getAptrgangrHealth(int spellLevel) {
      return 25.0F + spellLevel * 45.0F;
   }

   private float getAptrgangrDamage(int spellLevel, LivingEntity caster) {
      float baseDamage = 12.0F + this.getSpellPower(spellLevel, caster) * 2.0F;
      double summonedDamageMultiplier = 1.0;
      if (caster.getAttributes().hasAttribute(AttributeRegistry.SUMMON_DAMAGE)) {
         summonedDamageMultiplier = caster.getAttributeValue(AttributeRegistry.SUMMON_DAMAGE);
      }

      return (float)(baseDamage * summonedDamageMultiplier);
   }

   private String getAptrgangrDamageText(int spellLevel, LivingEntity caster) {
      if (caster != null) {
         float baseDamage = 12.0F + this.getSpellPower(spellLevel, caster) * 2.0F;
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
         Component.translatable("ui.traveloptics.hp", new Object[]{this.getAptrgangrHealth(spellLevel)}),
         Component.translatable("ui.traveloptics.damage", new Object[]{this.getAptrgangrDamageText(spellLevel, caster)}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }
}
