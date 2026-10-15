package com.somake.somakespells.registries;

import com.mojang.serialization.MapCodec;
import com.somake.somakespells.compat.BornInChaosCompat;
import com.somake.somakespells.particle.BloodSlashParticleOptions;
import com.somake.somakespells.particle.CustomStormParticleOptions;
import com.somake.somakespells.particle.ElementalFierySmokeParticleOptions;
import com.somake.somakespells.particle.FireVortexParticleOptions;
import com.somake.somakespells.particle.LightningCutParticleOptions;
import com.somake.somakespells.particle.OrbitingWaterParticleOptions;
import com.somake.somakespells.particle.WitheredRoseVortexParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModParticles {
   public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, "somakespells");
   public static final DeferredRegister<ParticleType<?>> BORN_IN_CHAOS_PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, "somakespells");
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SUBMERGED_HIT_SPLASH = PARTICLE_TYPES.register(
      "submerged_hit_splash", () -> new SimpleParticleType(true)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INFERNAL_FIRE_EXPLODE = BORN_IN_CHAOS_PARTICLE_TYPES.register(
      "infernal_fire_explode", () -> new SimpleParticleType(true)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FIRE_ORB_SMALL_EXPLODE = PARTICLE_TYPES.register(
      "fire_orb_small_explode", () -> new SimpleParticleType(true)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SOUL_FIRE_ORB_SMALL_EXPLODE = PARTICLE_TYPES.register(
      "soul_fire_orb_small_explode", () -> new SimpleParticleType(true)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INFERNAL_FIRE_ORB_SMALL_EXPLODE = BORN_IN_CHAOS_PARTICLE_TYPES.register(
      "infernal_fire_orb_small_explode", () -> new SimpleParticleType(true)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INFERNAL_FIRE_FLAME = BORN_IN_CHAOS_PARTICLE_TYPES.register(
      "infernal_fire_flame", () -> new SimpleParticleType(false)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SOUL_FIRE_FLAME_JET = PARTICLE_TYPES.register(
      "soul_fire_flame_jet", () -> new SimpleParticleType(false)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INFERNAL_FIRE_FLAME_JET = BORN_IN_CHAOS_PARTICLE_TYPES.register(
      "infernal_fire_flame_jet", () -> new SimpleParticleType(false)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SOUL_FIRE_LAVA = PARTICLE_TYPES.register(
      "soul_fire_lava", () -> new SimpleParticleType(false)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INFERNAL_FIRE_LAVA = BORN_IN_CHAOS_PARTICLE_TYPES.register(
      "infernal_fire_lava", () -> new SimpleParticleType(false)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FIRE = PARTICLE_TYPES.register("fire", () -> new SimpleParticleType(false));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SOUL_FIRE = PARTICLE_TYPES.register("soul_fire", () -> new SimpleParticleType(false));
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WITHERED_ROSE_PETAL = PARTICLE_TYPES.register(
      "withered_rose_petal", () -> new SimpleParticleType(false)
   );
   public static final DeferredHolder<ParticleType<?>, ParticleType<WitheredRoseVortexParticleOptions>> WITHERED_ROSE_VORTEX_PARTICLE = PARTICLE_TYPES.register(
      "withered_rose_vortex_particle", () -> new ParticleType<WitheredRoseVortexParticleOptions>(false) {
         public MapCodec<WitheredRoseVortexParticleOptions> codec() {
            return WitheredRoseVortexParticleOptions.MAP_CODEC;
         }

         public StreamCodec<? super RegistryFriendlyByteBuf, WitheredRoseVortexParticleOptions> streamCodec() {
            return WitheredRoseVortexParticleOptions.STREAM_CODEC;
         }
      }
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FLAME_ANIM_FIRE = PARTICLE_TYPES.register(
      "flame_anim_fire", () -> new SimpleParticleType(false)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FLAME_SURGE = PARTICLE_TYPES.register(
      "flame_surge", () -> new SimpleParticleType(false)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SOUL_FLAME_ANIM_FIRE = PARTICLE_TYPES.register(
      "soul_flame_anim_fire", () -> new SimpleParticleType(false)
   );
   public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SOUL_SURGE = PARTICLE_TYPES.register(
      "soul_surge", () -> new SimpleParticleType(false)
   );
   public static final DeferredHolder<ParticleType<?>, ParticleType<ElementalFierySmokeParticleOptions>> ELEMENTAL_FIERY_SMOKE = PARTICLE_TYPES.register(
      "elemental_fiery_smoke", () -> new ParticleType<ElementalFierySmokeParticleOptions>(true) {
         public MapCodec<ElementalFierySmokeParticleOptions> codec() {
            return ElementalFierySmokeParticleOptions.MAP_CODEC;
         }

         public StreamCodec<? super RegistryFriendlyByteBuf, ElementalFierySmokeParticleOptions> streamCodec() {
            return ElementalFierySmokeParticleOptions.STREAM_CODEC;
         }
      }
   );
   public static final DeferredHolder<ParticleType<?>, ParticleType<FireVortexParticleOptions>> FIRE_VORTEX_PARTICLE = PARTICLE_TYPES.register(
      "fire_vortex_particle", () -> new ParticleType<FireVortexParticleOptions>(false) {
         public MapCodec<FireVortexParticleOptions> codec() {
            return FireVortexParticleOptions.MAP_CODEC;
         }

         public StreamCodec<? super RegistryFriendlyByteBuf, FireVortexParticleOptions> streamCodec() {
            return FireVortexParticleOptions.STREAM_CODEC;
         }
      }
   );
   public static final DeferredHolder<ParticleType<?>, ParticleType<FireVortexParticleOptions>> SOUL_FIRE_VORTEX_PARTICLE = PARTICLE_TYPES.register(
      "soul_fire_vortex_particle", () -> new ParticleType<FireVortexParticleOptions>(false) {
         public MapCodec<FireVortexParticleOptions> codec() {
            return FireVortexParticleOptions.MAP_CODEC;
         }

         public StreamCodec<? super RegistryFriendlyByteBuf, FireVortexParticleOptions> streamCodec() {
            return FireVortexParticleOptions.STREAM_CODEC;
         }
      }
   );
   public static final DeferredHolder<ParticleType<?>, ParticleType<FireVortexParticleOptions>> INFERNAL_FIRE_VORTEX_PARTICLE = BORN_IN_CHAOS_PARTICLE_TYPES.register(
      "infernal_fire_vortex_particle", () -> new ParticleType<FireVortexParticleOptions>(false) {
         public MapCodec<FireVortexParticleOptions> codec() {
            return FireVortexParticleOptions.MAP_CODEC;
         }

         public StreamCodec<? super RegistryFriendlyByteBuf, FireVortexParticleOptions> streamCodec() {
            return FireVortexParticleOptions.STREAM_CODEC;
         }
      }
   );
   public static final DeferredHolder<ParticleType<?>, ParticleType<LightningCutParticleOptions>> LIGHTNING_CUT_PARTICLE = PARTICLE_TYPES.register(
      "lightning_cut_particle", () -> new ParticleType<LightningCutParticleOptions>(false) {
         public MapCodec<LightningCutParticleOptions> codec() {
            return LightningCutParticleOptions.MAP_CODEC;
         }

         public StreamCodec<? super RegistryFriendlyByteBuf, LightningCutParticleOptions> streamCodec() {
            return LightningCutParticleOptions.STREAM_CODEC;
         }
      }
   );
   public static final DeferredHolder<ParticleType<?>, ParticleType<BloodSlashParticleOptions>> BLOOD_SLASH_PARTICLE = PARTICLE_TYPES.register(
      "blood_slash_particle", () -> new ParticleType<BloodSlashParticleOptions>(false) {
         public MapCodec<BloodSlashParticleOptions> codec() {
            return BloodSlashParticleOptions.MAP_CODEC;
         }

         public StreamCodec<? super RegistryFriendlyByteBuf, BloodSlashParticleOptions> streamCodec() {
            return BloodSlashParticleOptions.STREAM_CODEC;
         }
      }
   );
   public static final DeferredHolder<ParticleType<?>, ParticleType<CustomStormParticleOptions>> CUSTOM_STORM_PARTICLE = PARTICLE_TYPES.register(
      "custom_storm_particle", () -> new ParticleType<CustomStormParticleOptions>(false) {
         public MapCodec<CustomStormParticleOptions> codec() {
            return CustomStormParticleOptions.MAP_CODEC;
         }

         public StreamCodec<? super RegistryFriendlyByteBuf, CustomStormParticleOptions> streamCodec() {
            return CustomStormParticleOptions.STREAM_CODEC;
         }
      }
   );
   public static final DeferredHolder<ParticleType<?>, ParticleType<OrbitingWaterParticleOptions>> ORBITING_WATER_PARTICLE = PARTICLE_TYPES.register(
      "orbiting_water_particle", () -> new ParticleType<OrbitingWaterParticleOptions>(false) {
         public MapCodec<OrbitingWaterParticleOptions> codec() {
            return OrbitingWaterParticleOptions.MAP_CODEC;
         }

         public StreamCodec<? super RegistryFriendlyByteBuf, OrbitingWaterParticleOptions> streamCodec() {
            return OrbitingWaterParticleOptions.STREAM_CODEC;
         }
      }
   );

   public static void register(IEventBus eventBus) {
      PARTICLE_TYPES.register(eventBus);
   }

   public static void registerBornInChaos(IEventBus eventBus) {
      BORN_IN_CHAOS_PARTICLE_TYPES.register(eventBus);
   }

   public static SimpleParticleType infernalFireExplodeOr(SimpleParticleType fallback) {
      return BornInChaosCompat.isLoaded() ? (SimpleParticleType)INFERNAL_FIRE_EXPLODE.get() : fallback;
   }

   public static SimpleParticleType infernalFireOrbSmallExplodeOr(SimpleParticleType fallback) {
      return BornInChaosCompat.isLoaded() ? (SimpleParticleType)INFERNAL_FIRE_ORB_SMALL_EXPLODE.get() : fallback;
   }

   public static SimpleParticleType infernalFireFlameOr(SimpleParticleType fallback) {
      return BornInChaosCompat.isLoaded() ? (SimpleParticleType)INFERNAL_FIRE_FLAME.get() : fallback;
   }

   public static SimpleParticleType infernalFireFlameJetOr(SimpleParticleType fallback) {
      return BornInChaosCompat.isLoaded() ? (SimpleParticleType)INFERNAL_FIRE_FLAME_JET.get() : fallback;
   }

   public static SimpleParticleType infernalFireLavaOr(SimpleParticleType fallback) {
      return BornInChaosCompat.isLoaded() ? (SimpleParticleType)INFERNAL_FIRE_LAVA.get() : fallback;
   }

   public static ParticleType<FireVortexParticleOptions> infernalFireVortexOr(ParticleType<FireVortexParticleOptions> fallback) {
      return BornInChaosCompat.isLoaded() ? (ParticleType)INFERNAL_FIRE_VORTEX_PARTICLE.get() : fallback;
   }
}
