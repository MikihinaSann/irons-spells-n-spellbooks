package com.somake.somakespells.spells.sound;

import com.somake.somakespells.compat.TunesCompat;
import com.somake.somakespells.entity.spells.ResonantPulseProjectile;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ResonantPulseSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "resonant_pulse");
   private final DefaultConfig defaultConfig;

   public ResonantPulseSpell() {
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 12;
      this.spellPowerPerLevel = 3;
      this.castTime = 0;
      this.baseManaCost = 20;
      this.defaultConfig = new DefaultConfig()
         .setMinRarity(SpellRarity.UNCOMMON)
         .setSchoolResource(TunesCompat.schoolOrEvocation())
         .setMaxLevel(5)
         .setCooldownSeconds(2.0)
         .build();
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getSpellPower(spellLevel, caster), 1)}));
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.empty();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return TunesCompat.getHarpWaveSound();
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         ParticleOptions particle = this.getPulseParticle();
         Vec3 direction = entity.getLookAngle().normalize();
         Vec3 reference = Math.abs(direction.y) > 0.9 ? new Vec3(1.0, 0.0, 0.0) : new Vec3(0.0, 1.0, 0.0);
         Vec3 side = direction.cross(reference);
         if (side.lengthSqr() < 1.0E-4) {
            side = direction.cross(new Vec3(0.0, 0.0, 1.0));
         }

         side = side.normalize();
         Vec3 up = side.cross(direction).normalize();
         Vec3 origin = entity.position().add(0.0, entity.getEyeHeight() - 0.2, 0.0).add(direction.scale(0.55));

         for (int i = 0; i < 10; i++) {
            double angle = (Math.PI * 2) * i / 10.0;
            Vec3 offset = side.scale(Math.cos(angle) * 0.24).add(up.scale(Math.sin(angle) * 0.24));
            Vec3 velocity = direction.scale(0.08).add(offset.scale(0.1));
            serverLevel.sendParticles(particle, origin.x + offset.x, origin.y + offset.y, origin.z + offset.z, 0, velocity.x, velocity.y, velocity.z, 0.02);
         }

         serverLevel.sendParticles(
            particle, origin.x + direction.x * 0.24, origin.y + direction.y * 0.24, origin.z + direction.z * 0.24, 6, 0.08, 0.08, 0.08, 0.01
         );
      }

      ResonantPulseProjectile projectile = new ResonantPulseProjectile(level, entity);
      projectile.setPos(entity.position().add(0.0, entity.getEyeHeight() - 0.3, 0.0));
      projectile.shoot(entity.getLookAngle());
      projectile.setDamage(this.getSpellPower(spellLevel, entity));
      level.addFreshEntity(projectile);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ONE_HANDED_HORIZONTAL_SWING_ANIMATION;
   }

   private ParticleOptions getPulseParticle() {
      return TunesCompat.getBlackOrMusicalParticle();
   }
}
