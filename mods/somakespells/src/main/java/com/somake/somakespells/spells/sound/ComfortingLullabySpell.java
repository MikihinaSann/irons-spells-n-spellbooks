package com.somake.somakespells.spells.sound;

import com.somake.somakespells.compat.TunesCompat;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.events.SpellHealEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class ComfortingLullabySpell extends AbstractSpell {
   private static final double TWO_PI = Math.PI * 2;
   private static final int SLEEPY_DURATION_TICKS = 60;
   private static final float BASE_RADIUS = 5.5F;
   private static final float RADIUS_PER_LEVEL = 0.5F;
   private static final float HEALING_MULTIPLIER = 0.25F;
   private static final DustParticleOptions MELODY_DUST = new DustParticleOptions(new Vector3f(0.25F, 0.92F, 0.82F), 0.9F);
   private static final DustParticleOptions HEALING_DUST = new DustParticleOptions(new Vector3f(1.0F, 0.78F, 0.28F), 0.85F);
   private static final DustParticleOptions COMFORT_DUST = new DustParticleOptions(new Vector3f(0.52F, 0.78F, 1.0F), 0.75F);
   private static final float[] MELODY_PITCHES = new float[]{0.89F, 1.0F, 1.12F, 1.0F, 1.19F, 1.12F, 1.0F, 0.94F};
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "comforting_lullaby");
   private final DefaultConfig defaultConfig;

   public ComfortingLullabySpell() {
      this.baseManaCost = 3;
      this.manaCostPerLevel = 1;
      this.baseSpellPower = 3;
      this.spellPowerPerLevel = 1;
      this.castTime = 120;
      this.defaultConfig = new DefaultConfig()
         .setMinRarity(SpellRarity.EPIC)
         .setSchoolResource(TunesCompat.schoolOrEvocation())
         .setMaxLevel(4)
         .setCooldownSeconds(26.0)
         .build();
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.healing", new Object[]{Utils.stringTruncation(this.getHealing(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(spellLevel), 1)}),
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(60.0F, 1)})
      );
   }

   public CastType getCastType() {
      return CastType.CONTINUOUS;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return TunesCompat.getHarpWaveSound();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.empty();
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_CONTINUOUS_CAST;
   }

   public boolean allowLooting() {
      return false;
   }

   public boolean allowCrafting() {
      return false;
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         int elapsedTicks = Math.max(0, playerMagicData.getCastDuration() - playerMagicData.getCastDurationRemaining());
         this.applyPulse(serverLevel, spellLevel, caster, elapsedTicks / 10);
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity caster, @Nullable MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel && playerMagicData != null) {
         int elapsedTicks = Math.max(0, playerMagicData.getCastDuration() - playerMagicData.getCastDurationRemaining());
         this.spawnAuraParticles(serverLevel, caster, this.getRadius(spellLevel), elapsedTicks);
      }
   }

   private void applyPulse(ServerLevel level, int spellLevel, LivingEntity caster, int pulseIndex) {
      float radius = this.getRadius(spellLevel);
      double radiusSqr = radius * radius;
      float healing = this.getHealing(spellLevel, caster);
      Optional<Reference<MobEffect>> sleepyEffect = BuiltInRegistries.MOB_EFFECT.getHolder(TunesCompat.SLEEPY_EFFECT_ID);
      List<Player> players = level.getEntitiesOfClass(
         Player.class, caster.getBoundingBox().inflate(radius), playerx -> this.canReceiveLullaby(playerx) && playerx.distanceToSqr(caster) <= radiusSqr
      );
      if (caster instanceof Player casterPlayer && this.canReceiveLullaby(casterPlayer) && !players.contains(casterPlayer)) {
         players.add(casterPlayer);
      }

      this.spawnPulseWave(level, caster, radius, pulseIndex);

      for (Player player : players) {
         NeoForge.EVENT_BUS.post(new SpellHealEvent(caster, player, healing, this.getSchoolType()));
         player.heal(healing);
         sleepyEffect.ifPresent(effect -> player.addEffect(new MobEffectInstance(effect, 60, 0, false, true, true)));
         this.spawnPlayerPulse(level, caster, player, pulseIndex);
      }

      float pitch = MELODY_PITCHES[Math.floorMod(pulseIndex, MELODY_PITCHES.length)];
      level.playSound(null, caster.getX(), caster.getY(), caster.getZ(), (SoundEvent)SoundEvents.NOTE_BLOCK_FLUTE.value(), SoundSource.PLAYERS, 0.9F, pitch);
   }

   private boolean canReceiveLullaby(Player player) {
      return player.isAlive() && !player.isSpectator() && player.getHealth() < player.getMaxHealth();
   }

   private void spawnAuraParticles(ServerLevel level, LivingEntity caster, float radius, int elapsedTicks) {
      if (elapsedTicks % 4 == 0) {
         double phase = elapsedTicks * 0.11;
         double breathing = 1.0 + Math.sin(elapsedTicks * 0.22) * 0.018;
         this.spawnOrbit(level, caster, radius * 0.96 * breathing, phase, 12, MELODY_DUST, 0.08);
         this.spawnOrbit(level, caster, radius * 0.67, -phase * 1.35, 8, COMFORT_DUST, 0.13);

         for (int i = 0; i < 2; i++) {
            double angle = phase * 1.8 + i * Math.PI;
            double orbitRadius = 0.8 + i * 0.18;
            double x = caster.getX() + Math.cos(angle) * orbitRadius;
            double y = caster.getY() + 0.65 + i * 0.75;
            double z = caster.getZ() + Math.sin(angle) * orbitRadius;
            level.sendParticles(TunesCompat.getMusicalParticle(), x, y, z, 1, 0.0, 0.025, 0.0, 0.0);
         }

         if (elapsedTicks % 8 == 0) {
            level.sendParticles(ParticleTypes.END_ROD, caster.getX(), caster.getY() + 0.15, caster.getZ(), 2, 0.28, 0.04, 0.28, 0.015);
         }
      }
   }

   private void spawnOrbit(ServerLevel level, LivingEntity caster, double radius, double phase, int points, ParticleOptions particle, double height) {
      for (int i = 0; i < points; i++) {
         double angle = phase + (Math.PI * 2) * i / points;
         double y = caster.getY() + height + Math.sin(angle * 3.0 + phase) * 0.025;
         level.sendParticles(particle, caster.getX() + Math.cos(angle) * radius, y, caster.getZ() + Math.sin(angle) * radius, 1, 0.0, 0.0, 0.0, 0.0);
      }
   }

   private void spawnPulseWave(ServerLevel level, LivingEntity caster, float radius, int pulseIndex) {
      boolean healingBeat = (pulseIndex & 1) == 0;
      float red = healingBeat ? 0.98F : 0.28F;
      float green = healingBeat ? 0.78F : 0.94F;
      float blue = healingBeat ? 0.3F : 0.84F;
      level.sendParticles(
         new BlastwaveParticleOptions(red, green, blue, radius * 0.95F), caster.getX(), caster.getY() + 0.07, caster.getZ(), 1, 0.0, 0.0, 0.0, 0.0
      );
      ParticleOptions edgeParticle = healingBeat ? HEALING_DUST : MELODY_DUST;
      double phase = pulseIndex * 0.37;

      for (int i = 0; i < 16; i++) {
         double angle = phase + (Math.PI * 2) * i / 16.0;
         double x = caster.getX() + Math.cos(angle) * radius;
         double z = caster.getZ() + Math.sin(angle) * radius;
         level.sendParticles(edgeParticle, x, caster.getY() + 0.12, z, 1, 0.0, 0.025, 0.0, 0.0);
         if ((i & 3) == 0) {
            double noteRadius = radius * 0.72;
            level.sendParticles(
               TunesCompat.getMusicalParticle(),
               caster.getX() + Math.cos(angle) * noteRadius,
               caster.getY() + 0.28,
               caster.getZ() + Math.sin(angle) * noteRadius,
               1,
               Math.cos(angle) * 0.025,
               0.045,
               Math.sin(angle) * 0.025,
               0.0
            );
         }
      }
   }

   private void spawnPlayerPulse(ServerLevel level, LivingEntity caster, Player player, int pulseIndex) {
      double phase = pulseIndex * 0.65 + player.getId() * 0.31;
      double spiralRadius = Math.max(0.36, player.getBbWidth() * 0.72);

      for (int i = 0; i < 8; i++) {
         double progress = i / 7.0;
         double angle = phase + progress * (Math.PI * 2) * 1.35;
         ParticleOptions particle = (i & 1) == 0 ? HEALING_DUST : COMFORT_DUST;
         level.sendParticles(
            particle,
            player.getX() + Math.cos(angle) * spiralRadius,
            player.getY() + 0.18 + progress * player.getBbHeight(),
            player.getZ() + Math.sin(angle) * spiralRadius,
            1,
            0.0,
            0.025,
            0.0,
            0.0
         );
      }

      level.sendParticles(ParticleTypes.HEART, player.getX(), player.getY() + player.getBbHeight() * 0.82, player.getZ(), 2, 0.28, 0.18, 0.28, 0.025);
      level.sendParticles(TunesCompat.getMusicalParticle(), player.getX(), player.getY() + player.getEyeHeight(), player.getZ(), 2, 0.34, 0.18, 0.34, 0.025);
      level.sendParticles(ParticleTypes.WHITE_ASH, player.getX(), player.getY() + player.getEyeHeight() + 0.18, player.getZ(), 3, 0.3, 0.12, 0.3, 0.008);
      if (player != caster) {
         this.spawnHealingRibbon(level, caster, player, pulseIndex);
      }
   }

   private void spawnHealingRibbon(ServerLevel level, LivingEntity caster, Player player, int pulseIndex) {
      Vec3 start = caster.position().add(0.0, caster.getBbHeight() * 0.62, 0.0);
      Vec3 end = player.position().add(0.0, player.getBbHeight() * 0.58, 0.0);
      double distance = start.distanceTo(end);
      if (!(distance < 1.25)) {
         int points = Math.min(8, Math.max(4, (int)Math.ceil(distance)));

         for (int i = 1; i < points; i++) {
            double progress = (double)i / points;
            Vec3 point = start.lerp(end, progress).add(0.0, Math.sin(Math.PI * progress) * 0.72, 0.0);
            ParticleOptions particle = (i + pulseIndex & 1) == 0 ? MELODY_DUST : HEALING_DUST;
            level.sendParticles(particle, point.x, point.y, point.z, 1, 0.0, 0.0, 0.0, 0.0);
         }
      }
   }

   private float getHealing(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 0.25F;
   }

   private float getRadius(int spellLevel) {
      return 5.5F + Math.max(0, spellLevel - 1) * 0.5F;
   }
}
