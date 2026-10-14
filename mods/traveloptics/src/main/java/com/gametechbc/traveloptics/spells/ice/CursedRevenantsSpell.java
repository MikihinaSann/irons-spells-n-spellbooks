package com.gametechbc.traveloptics.spells.ice;

import com.gametechbc.traveloptics.api.particle.CylinderParticleManager;
import com.gametechbc.traveloptics.api.particle.ParticleDirection;
import com.gametechbc.traveloptics.entity.summons.SummonedDraugr;
import com.gametechbc.traveloptics.entity.summons.SummonedEliteDraugr;
import com.gametechbc.traveloptics.entity.summons.SummonedRoyalDraugr;
import com.gametechbc.traveloptics.init.TOEffects;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
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
import net.minecraft.world.level.Level;

@AutoSpellConfig
public class CursedRevenantsSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "cursed_revenants");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(150.0)
      .build();

   public CursedRevenantsSpell() {
      super();
      this.manaCostPerLevel = 30;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 45;
      this.baseManaCost = 40;
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
      return Optional.of((SoundEvent)ModSounds.DRAUGR_IDLE.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)ModSounds.DRAUGR_HURT.get());
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      CylinderParticleManager.spawnParticles(level, entity, 3, (ParticleOptions)ModParticle.CURSED_FLAME.get(), ParticleDirection.UPWARD, 2.0, 2.0, -1.0);
      super.onServerCastTick(level, spellLevel, entity, playerMagicData);
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      int summonTime = 12000;
      int draugrCount = (int)this.getDraugrCount(spellLevel);
      double radius = 3.5;
      double angleIncrement = (Math.PI * 2) / draugrCount;

      for (int i = 0; i < draugrCount; i++) {
         double angle = i * angleIncrement;
         double xOffset = radius * Math.cos(angle);
         double zOffset = radius * Math.sin(angle);
         int randomDraugrType = (int)(Math.random() * 3.0);
         switch (randomDraugrType) {
            case 0:
               SummonedDraugr draugr = new SummonedDraugr(world, entity);
               draugr.setPos(entity.getX() + xOffset, entity.getY(), entity.getZ() + zOffset);
               draugr.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(this.getDraugrDamage(spellLevel, entity));
               draugr.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(this.getDraugrHealth(spellLevel));
               draugr.setHealth(draugr.getMaxHealth());
               draugr.finalizeSpawn((ServerLevel)world, world.getCurrentDifficultyAt(draugr.getOnPos()), MobSpawnType.MOB_SUMMONED, null);
               world.addFreshEntity(draugr);
               draugr.addEffect(new MobEffectInstance(TOEffects.CURSED_REVENANTS_TIMER, summonTime, 0, false, false, false));
               MagicManager.spawnParticles(
                  world,
                  (ParticleOptions)ModParticle.CURSED_FLAME.get(),
                  entity.getX() + xOffset,
                  entity.getY(),
                  entity.getZ() + zOffset,
                  20,
                  0.0,
                  0.0,
                  0.0,
                  0.03,
                  false
               );
               break;
            case 1:
               SummonedEliteDraugr eliteDraugr = new SummonedEliteDraugr(world, entity);
               eliteDraugr.setPos(entity.getX() + xOffset, entity.getY(), entity.getZ() + zOffset);
               eliteDraugr.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(this.getDraugrDamage(spellLevel, entity) + 1.0F);
               eliteDraugr.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(this.getDraugrHealth(spellLevel) + 4.0F);
               eliteDraugr.setHealth(eliteDraugr.getMaxHealth());
               eliteDraugr.finalizeSpawn((ServerLevel)world, world.getCurrentDifficultyAt(eliteDraugr.getOnPos()), MobSpawnType.MOB_SUMMONED, null);
               world.addFreshEntity(eliteDraugr);
               eliteDraugr.addEffect(new MobEffectInstance(TOEffects.CURSED_REVENANTS_TIMER, summonTime, 0, false, false, false));
               MagicManager.spawnParticles(
                  world,
                  (ParticleOptions)ModParticle.CURSED_FLAME.get(),
                  entity.getX() + xOffset,
                  entity.getY(),
                  entity.getZ() + zOffset,
                  20,
                  0.0,
                  0.0,
                  0.0,
                  0.03,
                  false
               );
               break;
            case 2:
               SummonedRoyalDraugr royalDraugr = new SummonedRoyalDraugr(world, entity);
               royalDraugr.setPos(entity.getX() + xOffset, entity.getY(), entity.getZ() + zOffset);
               royalDraugr.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(this.getDraugrDamage(spellLevel, entity) + 1.0F);
               royalDraugr.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(this.getDraugrHealth(spellLevel) + 2.0F);
               royalDraugr.setHealth(royalDraugr.getMaxHealth());
               royalDraugr.finalizeSpawn((ServerLevel)world, world.getCurrentDifficultyAt(royalDraugr.getOnPos()), MobSpawnType.MOB_SUMMONED, null);
               world.addFreshEntity(royalDraugr);
               royalDraugr.addEffect(new MobEffectInstance(TOEffects.CURSED_REVENANTS_TIMER, summonTime, 0, false, false, false));
               MagicManager.spawnParticles(
                  world,
                  (ParticleOptions)ModParticle.CURSED_FLAME.get(),
                  entity.getX() + xOffset,
                  entity.getY(),
                  entity.getZ() + zOffset,
                  20,
                  0.0,
                  0.0,
                  0.0,
                  0.03,
                  false
               );
               break;
            default:
               throw new IllegalStateException("Unexpected value: " + randomDraugrType);
         }
      }

      int effectAmplifier = 0;
      if (entity.hasEffect(TOEffects.CURSED_REVENANTS_TIMER)) {
         effectAmplifier += entity.getEffect(TOEffects.CURSED_REVENANTS_TIMER).getAmplifier() + 1;
      }

      entity.addEffect(new MobEffectInstance(TOEffects.CURSED_REVENANTS_TIMER, summonTime, effectAmplifier, false, false, true));
      CameraShakeManager.addCameraShake(new CameraShakeData(30, entity.position(), 25.0F));
      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDraugrCount(int spellLevel) {
      return spellLevel;
   }

   private float getDraugrHealth(int spellLevel) {
      return 3.0F + spellLevel * 5.0F;
   }

   private float getDraugrDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 0.8F;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.traveloptics.summon_count", new Object[]{this.getDraugrCount(spellLevel)}),
         Component.translatable("ui.traveloptics.hp", new Object[]{this.getDraugrHealth(spellLevel)}),
         Component.translatable("ui.traveloptics.damage", new Object[]{this.getDraugrDamage(spellLevel, caster)}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }
}
