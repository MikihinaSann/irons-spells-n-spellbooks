package com.somake.somakespells.compat;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.lang.reflect.Constructor;
import java.util.Optional;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;

public final class TunesCompat {
   public static final String MODID = "tunes_n_tomes";
   public static final ResourceLocation MELODY_SCHOOL_ID = id("melody");
   public static final ResourceLocation MELODY_SPELL_POWER_ID = id("melody_spell_power");
   public static final ResourceLocation SLEEPY_EFFECT_ID = id("sleepy");
   public static final ResourceLocation HARP_WAVE_SOUND_ID = id("harp_wave");
   public static final ResourceLocation HOPE_BURST_SOUND_ID = id("hope_burst");
   public static final ResourceLocation HARP_EXPLOSION_PARTICLE_ID = id("harp_explosion");
   public static final ResourceLocation MUSICAL_NOTE_PARTICLE_ID = id("musical_note");
   public static final ResourceLocation BLACK_NOTE_PARTICLE_ID = id("black_note");
   public static final ResourceLocation GRAND_FINALE_SPELL_ID = id("grand_finale");

   private TunesCompat() {
   }

   public static boolean isLoaded() {
      return ModList.get().isLoaded("tunes_n_tomes");
   }

   public static ResourceLocation schoolOrEvocation() {
      return isLoaded() ? MELODY_SCHOOL_ID : SchoolRegistry.EVOCATION_RESOURCE;
   }

   public static Optional<SoundEvent> getHarpWaveSound() {
      return BuiltInRegistries.SOUND_EVENT.getOptional(HARP_WAVE_SOUND_ID);
   }

   public static Optional<SoundEvent> getHopeBurstSound() {
      return BuiltInRegistries.SOUND_EVENT.getOptional(HOPE_BURST_SOUND_ID);
   }

   public static Optional<Reference<Attribute>> getMelodySpellPowerAttribute() {
      return BuiltInRegistries.ATTRIBUTE.getHolder(MELODY_SPELL_POWER_ID);
   }

   public static ParticleOptions getMusicalParticle() {
      ParticleType<?> musicalNote = (ParticleType<?>)BuiltInRegistries.PARTICLE_TYPE.get(MUSICAL_NOTE_PARTICLE_ID);
      return (ParticleOptions)(musicalNote != null ? (ParticleOptions)musicalNote : ParticleTypes.NOTE);
   }

   public static ParticleOptions getBlackOrMusicalParticle() {
      ParticleType<?> blackNote = (ParticleType<?>)BuiltInRegistries.PARTICLE_TYPE.get(BLACK_NOTE_PARTICLE_ID);
      return blackNote != null ? (ParticleOptions)blackNote : getMusicalParticle();
   }

   public static ParticleOptions getHarpExplosionParticle() {
      ParticleType<?> harpExplosion = (ParticleType<?>)BuiltInRegistries.PARTICLE_TYPE.get(HARP_EXPLOSION_PARTICLE_ID);
      return harpExplosion != null ? (ParticleOptions)harpExplosion : ParticleHelper.CLEANSE_PARTICLE;
   }

   public static boolean spawnCelestialHealingCircle(Level level, LivingEntity owner, int amplifier, int duration, float radius, Vec3 position) {
      if (isLoaded() && !level.isClientSide) {
         try {
            Class<?> healingCircleClass = Class.forName("net.alshanex.tunes_n_tomes.entity.celestial_chant.HealingCircleEntity");
            Constructor<?> constructor = healingCircleClass.getConstructor(Level.class, int.class, LivingEntity.class);
            Object instance = constructor.newInstance(level, amplifier, null);
            if (instance instanceof Entity entity) {
               healingCircleClass.getMethod("setOwner", LivingEntity.class).invoke(instance, null);
               healingCircleClass.getMethod("setDuration", int.class).invoke(instance, duration);
               healingCircleClass.getMethod("setRadius", float.class).invoke(instance, radius);
               healingCircleClass.getMethod("setCircular").invoke(instance);
               entity.moveTo(position);
               return level.addFreshEntity(entity);
            } else {
               return false;
            }
         } catch (LinkageError | ReflectiveOperationException var10) {
            return false;
         }
      } else {
         return false;
      }
   }

   private static ResourceLocation id(String path) {
      return ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", path);
   }
}
