package com.somake.somakespells.compat;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.fml.ModList;

public final class LegendaryMonstersCompat {
   public static final String MODID = "legendary_monsters";
   public static final ResourceLocation WAND_OF_CLOUDS_ID = id("wand_of_clouds");
   public static final ResourceLocation VOID_ENTITY_WARPER_ID = id("void_entity_warper");
   public static final ResourceLocation ENTITY_WARPER_ID = id("entity_warper");
   public static final ResourceLocation SOUL_SWEEP_PARTICLE_ID = id("soul_sweep");
   public static final ResourceLocation SOUL_SWEEP_RED_PARTICLE_ID = id("soul_sweep_red");
   public static final ResourceLocation POSSESSED_PALADIN_SWING_SOUND_ID = id("pp_swing");
   public static final ResourceLocation SOUL_FLY_SOUND_ID = id("soul_fly");
   private static final String SOUL_SWEEP_DATA_CLASS = "net.miauczel.legendary_monsters.Particle.custom.SoulSweepParticle$SweepData";
   private static final String SOUL_SWEEP_RED_DATA_CLASS = "net.miauczel.legendary_monsters.Particle.custom.SoulSweepRedParticle$SweepData";
   private static final Set<ResourceLocation> COMPAT_STAFF_IDS = Set.of(WAND_OF_CLOUDS_ID, VOID_ENTITY_WARPER_ID, ENTITY_WARPER_ID);

   private LegendaryMonstersCompat() {
   }

   public static boolean isLoaded() {
      return ModList.get().isLoaded("legendary_monsters");
   }

   public static boolean isCompatStaff(ResourceLocation itemId) {
      return COMPAT_STAFF_IDS.contains(itemId);
   }

   public static Optional<SoundEvent> getPossessedPaladinSwingSound() {
      return BuiltInRegistries.SOUND_EVENT.getOptional(POSSESSED_PALADIN_SWING_SOUND_ID);
   }

   public static Optional<SoundEvent> getSoulFlySound() {
      return BuiltInRegistries.SOUND_EVENT.getOptional(SOUL_FLY_SOUND_ID);
   }

   public static Optional<ParticleOptions> createSoulSweepParticle(float scale, float yaw, float pitch, boolean red) {
      if (!isLoaded()) {
         return Optional.empty();
      } else {
         String className = red
            ? "net.miauczel.legendary_monsters.Particle.custom.SoulSweepRedParticle$SweepData"
            : "net.miauczel.legendary_monsters.Particle.custom.SoulSweepParticle$SweepData";

         try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getConstructor(float.class, float.class, float.class);
            if (constructor.newInstance(scale, yaw, pitch) instanceof ParticleOptions particleOptions) {
               return Optional.of(particleOptions);
            }
         } catch (ReflectiveOperationException var9) {
         }

         return Optional.empty();
      }
   }

   public static ResourceLocation id(String path) {
      return ResourceLocation.fromNamespaceAndPath("legendary_monsters", path);
   }
}
