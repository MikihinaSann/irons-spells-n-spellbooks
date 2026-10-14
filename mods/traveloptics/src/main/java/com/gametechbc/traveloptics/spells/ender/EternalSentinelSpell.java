package com.gametechbc.traveloptics.spells.ender;

import com.gametechbc.traveloptics.api.particle.CylinderParticleManager;
import com.gametechbc.traveloptics.api.particle.ParticleDirection;
import com.gametechbc.traveloptics.api.spells.AbstractUniqueSpell;
import com.gametechbc.traveloptics.api.utils.SummonCheckHelper;
import com.gametechbc.traveloptics.config.SpellsConfig;
import com.gametechbc.traveloptics.entity.summons.SummonedEnderGolem;
import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.util.SummonTypes;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.events.SpellSummonEvent;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;

@AutoSpellConfig
public class EternalSentinelSpell extends AbstractUniqueSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "eternal_sentinel");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(330.0)
      .build();

   public EternalSentinelSpell() {
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
      return Optional.of(SoundEvents.END_PORTAL_SPAWN);
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
      CylinderParticleManager.spawnParticles(level, entity, 3, ParticleHelper.UNSTABLE_ENDER, ParticleDirection.UPWARD, 2.0, 2.0, -1.0);
      super.onServerCastTick(level, spellLevel, entity, playerMagicData);
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      int summonTime = 12000;
      double offset = 3.0;
      double angle = Math.toRadians(-entity.getYRot());
      double xOffset = offset * Math.cos(angle);
      double zOffset = offset * Math.sin(angle);
      SummonedEnderGolem enderGolem = new SummonedEnderGolem(world, entity);
      enderGolem.setPos(entity.getX() + xOffset, entity.getY(), entity.getZ() + zOffset);
      enderGolem.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(this.getGolemDamage(spellLevel, entity));
      enderGolem.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(this.getGolemHealth(spellLevel));
      enderGolem.setHealth(enderGolem.getMaxHealth());
      enderGolem.finalizeSpawn((ServerLevel)world, world.getCurrentDifficultyAt(enderGolem.getOnPos()), MobSpawnType.MOB_SUMMONED, null);
      SpellSummonEvent<SummonedEnderGolem> event = (SpellSummonEvent<SummonedEnderGolem>)NeoForge.EVENT_BUS
         .post(new SpellSummonEvent(entity, enderGolem, this.spellId, spellLevel));
      world.addFreshEntity(event.getCreature());
      enderGolem.addEffect(new MobEffectInstance(TOEffects.ENDER_GOLEM_TIMER, summonTime, 0, false, false, false));
      MagicManager.spawnParticles(
         world, ParticleTypes.REVERSE_PORTAL, entity.getX() + xOffset, entity.getY() + 2.0, entity.getZ() + zOffset, 50, 0.0, 0.0, 0.0, 0.03, false
      );
      int effectAmplifier = 0;
      if (entity.hasEffect(TOEffects.ENDER_GOLEM_TIMER)) {
         effectAmplifier += entity.getEffect(TOEffects.ENDER_GOLEM_TIMER).getAmplifier() + 1;
      }

      entity.addEffect(new MobEffectInstance(TOEffects.ENDER_GOLEM_TIMER, summonTime, effectAmplifier, false, false, true));
      CameraShakeManager.addCameraShake(new CameraShakeData(30, entity.position(), 25.0F));
      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   private float getGolemHealth(int spellLevel) {
      return 45.0F + spellLevel * 45.0F;
   }

   private float getGolemDamage(int spellLevel, LivingEntity caster) {
      float baseDamage = 8.0F + this.getSpellPower(spellLevel, caster) * 2.0F;
      double summonedDamageMultiplier = 1.0;
      if (caster.getAttributes().hasAttribute(AttributeRegistry.SUMMON_DAMAGE)) {
         summonedDamageMultiplier = caster.getAttributeValue(AttributeRegistry.SUMMON_DAMAGE);
      }

      return (float)(baseDamage * summonedDamageMultiplier);
   }

   private String getGolemDamageText(int spellLevel, LivingEntity caster) {
      if (caster != null) {
         float baseDamage = 8.0F + this.getSpellPower(spellLevel, caster) * 2.0F;
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
         Component.translatable("ui.traveloptics.hp", new Object[]{this.getGolemHealth(spellLevel)}),
         Component.translatable("ui.traveloptics.damage", new Object[]{this.getGolemDamageText(spellLevel, caster)}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }
}
