package net.acetheeldritchking.discerning_the_eldritch.spells.ritual;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Optional;
import net.acetheeldritchking.aces_spell_utils.spells.ASSpellAnimations;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEPotionEffectRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTESchoolRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class RavenousRevenantSpell extends AbstractRitualSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "ravenous_revenant");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(DTESchoolRegistry.RITUAL_RESOURCE)
      .setMaxLevel(1)
      .setCooldownSeconds(35.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getSpellPower(spellLevel, caster) * 20.0F, 1)})
      );
   }

   public RavenousRevenantSpell() {
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 35;
      this.spellPowerPerLevel = 2;
      this.castTime = 20;
      this.baseManaCost = 100;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   @Override
   public boolean isComplex() {
      return true;
   }

   @Override
   public boolean isSuperComplex() {
      return false;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)SoundRegistry.BLIGHT_BEGIN.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(SoundEvents.WARDEN_ROAR);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_SPIT_ANIMATION;
   }

   public AnimationHolder getCastFinishAnimation() {
      return ASSpellAnimations.ANIMATION_WRATH_ROAR;
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      double radius = 15.0;

      for (LivingEntity targets : level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(radius))) {
         if (targets != entity) {
            targets.addEffect(new MobEffectInstance(DTEPotionEffectRegistry.PREY_POTION_EFFECT, this.getDuration(spellLevel, entity), 0, false, false, true));
         }
      }

      MagicManager.spawnParticles(
         level,
         new BlastwaveParticleOptions(DTESchoolRegistry.RITUAL.get().getTargetingColor(), (float)radius),
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
      CameraShakeManager.addCameraShake(new CameraShakeData(level, 20, entity.position(), (float)radius));
      entity.addEffect(new MobEffectInstance(DTEPotionEffectRegistry.PREDATOR_POTION_EFFECT, this.getDuration(spellLevel, entity), 0, false, false, true));
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker).setIFrames(0);
   }

   private int getDuration(int spellLevel, LivingEntity caster) {
      return (int)(20.0F * this.getSpellPower(spellLevel, caster));
   }
}
