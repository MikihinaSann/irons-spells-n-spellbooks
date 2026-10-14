package com.gametechbc.traveloptics.spells.eldritch;

import com.gametechbc.traveloptics.api.particle.CylinderParticleManager;
import com.gametechbc.traveloptics.api.particle.ParticleDirection;
import com.gametechbc.traveloptics.api.spells.AbstractUniqueSpell;
import com.gametechbc.traveloptics.entity.spells.BlackoutAntiMagicField;
import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.init.TOSounds;
import com.gametechbc.traveloptics.spells.TOSpellAnimations;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.particle.ShockwaveParticleOptions;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

@AutoSpellConfig
public class BlackoutSpell extends AbstractUniqueSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "blackout");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(120.0)
      .build();

   public BlackoutSpell() {
      super();
      this.manaCostPerLevel = 50;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 39;
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
      return Optional.of((SoundEvent)TOSounds.BANISH_CHARGE.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)TOSounds.BANISH_CAST.get());
   }

   public boolean stopSoundOnCancel() {
      return true;
   }

   public AnimationHolder getCastStartAnimation() {
      return TOSpellAnimations.BANISH_CHARGE;
   }

   public AnimationHolder getCastFinishAnimation() {
      return TOSpellAnimations.BANISH_FINISH;
   }

   public int getEffectiveCastTime(int spellLevel, @Nullable LivingEntity entity) {
      return this.getCastTime(spellLevel);
   }

   public void onServerPreCast(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      MagicManager.spawnParticles(
         level,
         new ShockwaveParticleOptions(new Vector3f(0.1F, 0.1F, 0.1F), -3.0F, true),
         entity.getX(),
         entity.getY(),
         entity.getZ(),
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      super.onServerPreCast(level, spellLevel, entity, playerMagicData);
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      float radius = this.getRadius(spellLevel);
      CylinderParticleManager.spawnParticles(level, entity, 8 * spellLevel, ParticleTypes.SMOKE, ParticleDirection.UPWARD, radius, 3 * spellLevel, -1.0);
      CameraShakeManager.addCameraShake(new CameraShakeData(15, entity.position(), radius));
      entity.addEffect(new MobEffectInstance(TOEffects.THIRD_PERSON_SWITCH, 5, 0, false, false, false));
      super.onServerCastTick(level, spellLevel, entity, playerMagicData);
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      float radius = this.getRadius(spellLevel);
      int effectDuration = (int)this.getEffectDuration(spellLevel);
      int antiMagicZoneDuration = (int)this.getAntiMagicZoneDuration(spellLevel);
      level.getEntities(
            entity,
            entity.getBoundingBox().inflate(radius, 4.0, radius),
            target -> !DamageSources.isFriendlyFireBetween(target, entity) && Utils.hasLineOfSight(level, entity, target, true)
         )
         .forEach(target -> {
            if (target instanceof LivingEntity livingEntity && livingEntity.distanceToSqr(entity) < radius * radius) {
               livingEntity.addEffect(new MobEffectInstance(TOEffects.BLACKOUT, effectDuration, 0));
            }
         });
      BlackoutAntiMagicField antiMagicField = new BlackoutAntiMagicField(level);
      antiMagicField.setOwner(entity);
      antiMagicField.setMovementDamage(3.0F);
      antiMagicField.setDuration(antiMagicZoneDuration);
      antiMagicField.setRadius(radius);
      antiMagicField.setCircular();
      antiMagicField.moveTo(entity.position());
      level.addFreshEntity(antiMagicField);
      MagicManager.spawnParticles(
         level,
         new BlastwaveParticleOptions(new Vector3f(0.1F, 0.1F, 0.1F), radius),
         entity.getX(),
         entity.getY() + 0.165F,
         entity.getZ(),
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public float getRadius(int spellLevel) {
      return 6 + spellLevel * 2;
   }

   public float getEffectDuration(int spellLevel) {
      return 30.0F;
   }

   public float getAntiMagicZoneDuration(int spellLevel) {
      return 120 + spellLevel * 40;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.traveloptics.radius", new Object[]{Utils.stringTruncation(this.getRadius(spellLevel), 2)}),
         Component.translatable("ui.traveloptics.effect_length", new Object[]{Utils.timeFromTicks(this.getEffectDuration(spellLevel), 2)}),
         Component.translatable("ui.traveloptics.anti_magic_zone_duration", new Object[]{Utils.timeFromTicks(this.getAntiMagicZoneDuration(spellLevel), 2)}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }
}
