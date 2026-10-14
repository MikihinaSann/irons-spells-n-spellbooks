package com.gametechbc.traveloptics.spells.lightning;

import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.init.ModEffect;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@AutoSpellConfig
public class EmPulse extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "em_pulse");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(45.0)
      .build();

   public EmPulse() {
      super();
      this.manaCostPerLevel = 50;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 20;
      this.baseManaCost = 0;
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
      return Optional.of((SoundEvent)ModSounds.HARBINGER_PREPARE.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)ModSounds.EMP_ACTIVATED.get());
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_RAISED_HAND;
   }

   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.TOUCH_GROUND_ANIMATION;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.traveloptics.stun_length", new Object[]{Utils.timeFromTicks(this.getDuration(spellLevel, caster), 2)}),
         Component.translatable("ui.traveloptics.radius", new Object[]{Utils.stringTruncation(this.getRadius(spellLevel, caster), 2)}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      float radius = this.getRadius(spellLevel, entity);
      level.getEntities(entity, entity.getBoundingBox().inflate(radius, 4.0, radius), target -> !DamageSources.isFriendlyFireBetween(target, entity))
         .forEach(target -> {
            if (target instanceof LivingEntity livingEntity && livingEntity.distanceToSqr(entity) < radius * radius) {
               livingEntity.addEffect(new MobEffectInstance(ModEffect.EFFECTSTUN, this.getDuration(spellLevel, entity)));
            }
         });
      ScreenShake_Entity.ScreenShake(level, entity.position(), radius, 0.03F, 10, 20);
      MagicManager.spawnParticles(level, (ParticleOptions)ModParticle.EM_PULSE.get(), entity.getX(), entity.getY(), entity.getZ(), 1, 0.0, 0.0, 0.0, 0.0, true);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public int getDuration(int spellLevel, LivingEntity caster) {
      return (int)(this.getSpellPower(spellLevel, caster) * 20.0F);
   }

   public float getRadius(int spellLevel, LivingEntity caster) {
      return 2 + spellLevel * 2;
   }
}
