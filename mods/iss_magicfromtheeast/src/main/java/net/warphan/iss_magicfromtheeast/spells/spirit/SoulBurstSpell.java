package net.warphan.iss_magicfromtheeast.spells.spirit;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.warphan.iss_magicfromtheeast.registries.MFTEEffectRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;

@AutoSpellConfig
public class SoulBurstSpell extends AbstractSpell {
   private final ResourceLocation spellID = new ResourceLocation("iss_magicfromtheeast", "soul_burst");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(MFTESchoolRegistries.SPIRIT_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(30.0)
      .build();

   public SoulBurstSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 2;
      this.spellPowerPerLevel = 1;
      this.castTime = 20;
      this.baseManaCost = 40;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getSpellPower(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getDuration(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(8.0, 2)})
      );
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellID;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of(SoundEvents.WARDEN_SONIC_CHARGE);
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(SoundEvents.SCULK_SHRIEKER_SHRIEK);
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      float radius = this.getRadius(spellLevel, entity);
      MagicManager.spawnParticles(
         level,
         new BlastwaveParticleOptions(MFTESchoolRegistries.SPIRIT.get().getTargetingColor(), radius),
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
      MagicManager.spawnParticles(
         level, ParticleTypes.SOUL_FIRE_FLAME, entity.getX(), entity.getY() + 1.0, entity.getZ(), 100, 0.25, 0.25, 0.25, 0.7F + radius * 0.1F, false
      );
      MagicManager.spawnParticles(
         level, ParticleTypes.SCULK_SOUL, entity.getX(), entity.getY() + 1.0, entity.getZ(), 40, 0.15, 0.15, 0.15, 0.7F + radius * 0.1F, false
      );
      CameraShakeManager.addCameraShake(new CameraShakeData(10, entity.position(), radius * 2.0F));
      float damage = this.getDamage(spellLevel, entity);
      int duration = this.getDuration(spellLevel, entity);
      level.getEntities(
            entity,
            entity.getBoundingBox().inflate(radius, 4.0, radius),
            target -> !DamageSources.isFriendlyFireBetween(target, entity) && Utils.hasLineOfSight(level, entity, target, true)
         )
         .forEach(
            target -> {
               if (target instanceof LivingEntity livingEntity && livingEntity.distanceToSqr(entity) < radius * radius) {
                  DamageSources.applyDamage(target, damage, this.getDamageSource(entity));
                  livingEntity.addEffect(new MobEffectInstance(MFTEEffectRegistries.SOULBURN, duration, 0));
                  MagicManager.spawnParticles(
                     level,
                     ParticleTypes.SOUL_FIRE_FLAME,
                     livingEntity.getX(),
                     livingEntity.getY() + livingEntity.getBbHeight() / 2.0F,
                     livingEntity.getZ(),
                     10,
                     livingEntity.getBbWidth() / 3.0F,
                     livingEntity.getBbHeight() / 3.0F,
                     livingEntity.getBbWidth() / 3.0F,
                     0.1,
                     false
                  );
               }
            }
         );
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public float getRadius(int spellLevel, LivingEntity caster) {
      return 8.0F;
   }

   public int getDuration(int spellLevel, LivingEntity caster) {
      return 40 + spellLevel * 20;
   }

   public float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.PREPARE_CROSS_ARMS;
   }

   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.CAST_T_POSE;
   }
}
