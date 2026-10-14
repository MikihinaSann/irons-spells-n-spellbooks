package com.gametechbc.traveloptics.spells.fire;

import com.gametechbc.traveloptics.api.particle.ParticleDirection;
import com.gametechbc.traveloptics.api.particle.SphereParticleManager;
import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.spells.TOSpellAnimations;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@AutoSpellConfig
public class MeteorStormSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "meteor_storm");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(6)
      .setCooldownSeconds(90.0)
      .build();

   public MeteorStormSpell() {
      super();
      this.manaCostPerLevel = 30;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 15;
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

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      int duration = this.getDuration(spellLevel);
      int damage = this.getDamage(entity, spellLevel);
      if (!entity.hasEffect(TOEffects.METEOR_STORM_EFFECT)) {
         entity.addEffect(new MobEffectInstance(TOEffects.METEOR_STORM_EFFECT, duration, damage));
      }

      MagicManager.spawnParticles(
         level,
         new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.FIRE.get()).getTargetingColor(), 25.0F),
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
      SphereParticleManager.spawnParticles(level, entity, 60, ParticleHelper.FIRE, ParticleDirection.OUTWARD, 3.5);
      ScreenShake_Entity.ScreenShake(level, entity.position(), 8.0F, 0.03F, 10, 20);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public int getDuration(int spellLevel) {
      return 160 + spellLevel * 60;
   }

   public int getDamage(LivingEntity caster, int spellLevel) {
      return (int)(4.0 + this.getSpellPower(spellLevel, caster) * 2.0F);
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)ModSounds.FLAME_BURST.get());
   }

   public AnimationHolder getCastStartAnimation() {
      return TOSpellAnimations.METEOR_STORM_CAST;
   }

   public AnimationHolder getCastFinishAnimation() {
      return AnimationHolder.pass();
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      double radius = 25.0;
      int damage = this.getDamage(caster, spellLevel);
      int duration = this.getDuration(spellLevel);
      return List.of(
         Component.translatable("ui.traveloptics.damage", new Object[]{Utils.stringTruncation(damage, 2)}),
         Component.translatable("ui.traveloptics.radius", new Object[]{Utils.stringTruncation(radius, 2)}),
         Component.translatable("ui.traveloptics.meteor_storm_duration", new Object[]{Utils.timeFromTicks(duration, 2)}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }
}
