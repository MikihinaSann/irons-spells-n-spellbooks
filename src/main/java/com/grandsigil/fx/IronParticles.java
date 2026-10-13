package com.grandsigil.fx;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

public final class IronParticles {
   public static final String FIREFLY = "irons_spellbooks:firefly";
   public static final String WISP = "irons_spellbooks:wisp";
   public static final String CLEANSE = "irons_spellbooks:cleanse";
   public static final String FALLING_SPARKLE = "irons_spellbooks:falling_sparkle";
   public static final String ACID = "irons_spellbooks:acid";
   public static final String SNOWFLAKE = "irons_spellbooks:snowflake";
   public static final String SNOW_DUST = "irons_spellbooks:snow_dust";
   public static final String FOG = "irons_spellbooks:fog";
   public static final String BLOOD = "irons_spellbooks:blood";
   public static final String BLOOD_GROUND = "irons_spellbooks:blood_ground";
   public static final String UNSTABLE_ENDER = "irons_spellbooks:unstable_ender";
   public static final String TRIAL_OMEN = "irons_spellbooks:trial_omen";
   public static final String RING_SMOKE = "irons_spellbooks:ring_smoke";
   private static final Map<String, ParticleOptions> CACHE = new HashMap<>();

   private IronParticles() {
   }

   public static ParticleOptions of(String id) {
      if (CACHE.containsKey(id)) {
         return CACHE.get(id);
      } else {
         ParticleType<?> type = BuiltInRegistries.PARTICLE_TYPE.get(ResourceLocation.parse(id));
         ParticleOptions resolved = type instanceof SimpleParticleType simple ? simple : null;
         CACHE.put(id, resolved);
         return resolved;
      }
   }

   public static ParticleOptions or(String id, ParticleOptions fallback) {
      ParticleOptions found = of(id);
      return found != null ? found : fallback;
   }

   public static ParticleOptions ringSmoke() {
      return of("irons_spellbooks:ring_smoke");
   }

   public static void flatRing(ServerLevel level, double x, double y, double z) {
      ParticleOptions ring = ringSmoke();
      if (ring != null) {
         level.sendParticles(ring, x, y, z, 0, 0.0, 1.0, 0.0, 0.001);
      }
   }
}
