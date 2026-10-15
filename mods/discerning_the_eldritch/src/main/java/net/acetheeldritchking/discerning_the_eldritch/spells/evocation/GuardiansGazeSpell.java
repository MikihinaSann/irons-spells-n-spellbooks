package net.acetheeldritchking.discerning_the_eldritch.spells.evocation;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.ray_of_frost.RayOfFrostVisualEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.Optional;
import net.acetheeldritchking.discerning_the_eldritch.registries.SpellRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;

public class GuardiansGazeSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "guardians_gaze");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
      .setMaxLevel(1)
      .setCooldownSeconds(10.0)
      .build();

   public boolean canBeCraftedBy(Player player) {
      return false;
   }

   public boolean allowLooting() {
      return false;
   }

   public GuardiansGazeSpell() {
      this.manaCostPerLevel = 0;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 50;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.RAY_OF_FROST.get());
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      HitResult hitResult = Utils.raycastForEntity(level, entity, 30.0F, true, 0.15F);
      RayOfFrostVisualEntity ray = new RayOfFrostVisualEntity(level, entity.getEyePosition(), hitResult.getLocation(), entity);
      level.addFreshEntity(ray);
      if (hitResult.getType() == Type.ENTITY) {
         Entity target = ((EntityHitResult)hitResult).getEntity();
         DamageSources.applyDamage(target, this.getDamage(spellLevel, entity), SpellRegistries.GUARDIANS_GAZE.get().getDamageSource(entity));
         if (target instanceof LivingEntity livingTarget) {
            livingTarget.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 1, true, true, true));
            MagicManager.spawnParticles(
               level, ParticleTypes.BUBBLE, hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z, 10, 0.0, 0.0, 0.0, 0.1, true
            );
         } else if (hitResult.getType() == Type.BLOCK) {
            MagicManager.spawnParticles(
               level, ParticleTypes.BUBBLE, hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z, 10, 0.0, 0.0, 0.0, 0.1, false
            );
         }

         MagicManager.spawnParticles(
            level, ParticleTypes.BUBBLE, hitResult.getLocation().x, hitResult.getLocation().y, hitResult.getLocation().z, 10, 0.0, 0.0, 0.0, 0.1, false
         );
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 2.0F;
   }
}
