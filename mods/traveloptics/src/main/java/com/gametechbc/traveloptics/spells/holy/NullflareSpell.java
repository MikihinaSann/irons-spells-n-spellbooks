package com.gametechbc.traveloptics.spells.holy;

import com.gametechbc.traveloptics.api.particle.AdvancedCylinderParticleManager;
import com.gametechbc.traveloptics.api.particle.ParticleDirection;
import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.init.TOSounds;
import com.gametechbc.traveloptics.spells.TOSpellAnimations;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@AutoSpellConfig
public class NullflareSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "nullflare");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.HOLY_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(40.0)
      .build();

   public NullflareSpell() {
      super();
      this.manaCostPerLevel = 30;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 16;
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

   public AnimationHolder getCastStartAnimation() {
      return TOSpellAnimations.NULLFLARE_START;
   }

   public AnimationHolder getCastFinishAnimation() {
      return TOSpellAnimations.NULLFLARE_FINISH;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)TOSounds.NULLFLARE_TWO.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.empty();
   }

   public int getRecastCount(int spellLevel, LivingEntity caster) {
      return 5;
   }

   public boolean stopSoundOnCancel() {
      return true;
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      float radius = this.getRange(spellLevel);
      int duration = (int)this.getEffectDuration();
      CameraShakeManager.addCameraShake(new CameraShakeData(15, caster.position(), radius));
      if (!playerMagicData.getPlayerRecasts().hasRecastForSpell(this.getSpellId())) {
         playerMagicData.getPlayerRecasts()
            .addRecast(new RecastInstance(this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, caster), 300, castSource, null), playerMagicData);
      }

      boolean isCrouching = caster.isCrouching();
      if (isCrouching) {
         MagicManager.spawnParticles(
            level,
            new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.FIRE.get()).getTargetingColor(), radius),
            caster.getX(),
            caster.getY() + 0.165F,
            caster.getZ(),
            1,
            0.0,
            0.0,
            0.0,
            0.0,
            true
         );
      } else {
         MagicManager.spawnParticles(
            level,
            new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.ICE.get()).getTargetingColor(), radius),
            caster.getX(),
            caster.getY() + 0.165F,
            caster.getZ(),
            1,
            0.0,
            0.0,
            0.0,
            0.0,
            true
         );
      }

      level.getEntities(caster, caster.getBoundingBox().inflate(radius, 4.0, radius), target -> !DamageSources.isFriendlyFireBetween(target, caster))
         .forEach(
            target -> {
               if (target instanceof LivingEntity livingEntity && livingEntity.distanceToSqr(caster) < radius * radius) {
                  float baseDamage = this.getDamage(spellLevel, caster);
                  if (isCrouching) {
                     MobEffectInstance iceEffect = livingEntity.getEffect(TOEffects.NULLFLARE_ICE);
                     if (iceEffect != null) {
                        AdvancedCylinderParticleManager.spawnParticles(
                           level, caster.position(), 15, ParticleTypes.CAMPFIRE_COSY_SMOKE, ParticleDirection.OUTWARD, 2.0, 2.0, 0.0, 0.0, 0.0, 1.5, false
                        );
                        MagicManager.spawnParticles(
                           level, ParticleHelper.EMBERS, livingEntity.getX(), livingEntity.getY() + 1.0, livingEntity.getZ(), 30, 0.0, 0.0, 0.0, 0.3, false
                        );
                        livingEntity.removeEffect(TOEffects.NULLFLARE_ICE);
                        int amplifier = iceEffect.getAmplifier();
                        double fireSpellPower = caster.getAttributeValue(AttributeRegistry.FIRE_SPELL_POWER);
                        double scaledDamage = baseDamage * fireSpellPower * (1.0 + 0.85 * amplifier);
                        DamageSources.applyDamage(livingEntity, (float)scaledDamage, this.getDamageSource(caster));
                     }

                     MobEffectInstance fireEffect = livingEntity.getEffect(TOEffects.NULLFLARE_FIRE);
                     if (fireEffect != null) {
                        livingEntity.addEffect(new MobEffectInstance(TOEffects.NULLFLARE_FIRE, duration, fireEffect.getAmplifier() + 1));
                     } else {
                        livingEntity.addEffect(new MobEffectInstance(TOEffects.NULLFLARE_FIRE, duration, 0));
                     }
                  } else {
                     MobEffectInstance fireEffect = livingEntity.getEffect(TOEffects.NULLFLARE_FIRE);
                     if (fireEffect != null) {
                        AdvancedCylinderParticleManager.spawnParticles(
                           level, caster.position(), 15, ParticleTypes.CAMPFIRE_COSY_SMOKE, ParticleDirection.OUTWARD, 2.0, 2.0, 0.0, 0.0, 0.0, 1.5, false
                        );
                        MagicManager.spawnParticles(
                           level, ParticleHelper.EMBERS, livingEntity.getX(), livingEntity.getY() + 1.0, livingEntity.getZ(), 30, 0.0, 0.0, 0.0, 0.3, false
                        );
                        livingEntity.removeEffect(TOEffects.NULLFLARE_FIRE);
                        int amplifier = fireEffect.getAmplifier();
                        double iceSpellPower = caster.getAttributeValue(AttributeRegistry.ICE_SPELL_POWER);
                        double scaledDamage = baseDamage * iceSpellPower * (1.0 + 0.85 * amplifier);
                        DamageSources.applyDamage(livingEntity, (float)scaledDamage, this.getDamageSource(caster));
                     }

                     MobEffectInstance iceEffectx = livingEntity.getEffect(TOEffects.NULLFLARE_ICE);
                     if (iceEffectx != null) {
                        livingEntity.addEffect(new MobEffectInstance(TOEffects.NULLFLARE_ICE, duration, iceEffectx.getAmplifier() + 1));
                     } else {
                        livingEntity.addEffect(new MobEffectInstance(TOEffects.NULLFLARE_ICE, duration, 0));
                     }
                  }
               }
            }
         );
      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      float spellPower = this.getSpellPower(spellLevel, caster);
      return 10.0F + spellPower * 5.0F;
   }

   private String getFireDamageText(int spellLevel, LivingEntity caster) {
      if (caster != null) {
         float baseDamage = this.getDamage(spellLevel, caster);
         double fireSpellPower = caster.getAttributes().hasAttribute(AttributeRegistry.FIRE_SPELL_POWER)
            ? caster.getAttributeValue(AttributeRegistry.FIRE_SPELL_POWER)
            : 1.0;
         float finalDamage = (float)(baseDamage * fireSpellPower);
         String baseDamageText = Utils.stringTruncation(baseDamage, 1);
         String finalDamageText = Utils.stringTruncation(finalDamage, 1);
         return fireSpellPower > 1.0 ? String.format("%s -> %s", baseDamageText, finalDamageText) : baseDamageText;
      } else {
         return Utils.stringTruncation(this.getDamage(spellLevel, caster), 1);
      }
   }

   private String getIceDamageText(int spellLevel, LivingEntity caster) {
      if (caster != null) {
         float baseDamage = this.getDamage(spellLevel, caster);
         double iceSpellPower = caster.getAttributes().hasAttribute(AttributeRegistry.ICE_SPELL_POWER)
            ? caster.getAttributeValue(AttributeRegistry.ICE_SPELL_POWER)
            : 1.0;
         float finalDamage = (float)(baseDamage * iceSpellPower);
         String baseDamageText = Utils.stringTruncation(baseDamage, 1);
         String finalDamageText = Utils.stringTruncation(finalDamage, 1);
         return iceSpellPower > 1.0 ? String.format("%s -> %s", baseDamageText, finalDamageText) : baseDamageText;
      } else {
         return Utils.stringTruncation(this.getDamage(spellLevel, caster), 1);
      }
   }

   private float getRange(int spellLevel) {
      return 2.0F + spellLevel * 2.0F;
   }

   private float getEffectDuration() {
      return 200.0F;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      float range = this.getRange(spellLevel);
      return List.of(
         Component.translatable("ui.traveloptics.nullflare_blast", new Object[]{this.getFireDamageText(spellLevel, caster)}),
         Component.translatable("ui.traveloptics.nullflare_blast_fire_damage", new Object[]{this.getFireDamageText(spellLevel, caster)}),
         Component.translatable("ui.traveloptics.nullflare_blast_ice_damage", new Object[]{this.getIceDamageText(spellLevel, caster)}),
         Component.translatable("ui.traveloptics.radius", new Object[]{Utils.stringTruncation(range, 2)}),
         Component.translatable("ui.traveloptics.nullflare_blast.stacks"),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }
}
