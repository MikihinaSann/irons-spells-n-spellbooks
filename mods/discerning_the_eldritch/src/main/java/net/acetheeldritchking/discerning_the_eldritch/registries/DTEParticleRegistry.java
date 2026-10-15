package net.acetheeldritchking.discerning_the_eldritch.registries;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.acetheeldritchking.discerning_the_eldritch.particle.GlacialShadowParticleOptions;
import net.acetheeldritchking.discerning_the_eldritch.particle.SoulFireSlashParticleOptions;
import net.acetheeldritchking.discerning_the_eldritch.particle.VeinRipperVerticalParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DTEParticleRegistry {
   public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, "discerning_the_eldritch");
   public static final Supplier<ParticleType<GlacialShadowParticleOptions>> GLACIAL_SHADOW_PARTICLE = PARTICLE_TYPES.register(
      "glacial_shadow", () -> new ParticleType<GlacialShadowParticleOptions>(true) {
         public MapCodec<GlacialShadowParticleOptions> codec() {
            return GlacialShadowParticleOptions.MAP_CODEC;
         }

         public StreamCodec<? super RegistryFriendlyByteBuf, GlacialShadowParticleOptions> streamCodec() {
            return GlacialShadowParticleOptions.STREAM_CODEC;
         }
      }
   );
   public static final Supplier<SimpleParticleType> ESOTERIC_SPARKS_PARTICLE = PARTICLE_TYPES.register("esoteric_sparks", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> RIFT_SLICE_PARTICLE = PARTICLE_TYPES.register("rift_slice", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> MALIGNANT_SOUL = PARTICLE_TYPES.register("malignant_soul", () -> new SimpleParticleType(false));
   public static final Supplier<SimpleParticleType> MALIGNANT_FLAME = PARTICLE_TYPES.register("malignant_flame", () -> new SimpleParticleType(false));
   public static final Supplier<ParticleType<SoulFireSlashParticleOptions>> SOUL_FIRE_SLASH_PARTICLE = PARTICLE_TYPES.register(
      "soul_fire_slash", () -> new ParticleType<SoulFireSlashParticleOptions>(true) {
         public MapCodec<SoulFireSlashParticleOptions> codec() {
            return SoulFireSlashParticleOptions.MAP_CODEC;
         }

         public StreamCodec<? super RegistryFriendlyByteBuf, SoulFireSlashParticleOptions> streamCodec() {
            return SoulFireSlashParticleOptions.STREAM_CODEC;
         }
      }
   );
   public static final Supplier<ParticleType<VeinRipperVerticalParticleOptions>> VEIN_RIPPER_VERTICAL_PARTICLE = PARTICLE_TYPES.register(
      "vein_ripper", () -> new ParticleType<VeinRipperVerticalParticleOptions>(true) {
         public MapCodec<VeinRipperVerticalParticleOptions> codec() {
            return VeinRipperVerticalParticleOptions.MAP_CODEC;
         }

         public StreamCodec<? super RegistryFriendlyByteBuf, VeinRipperVerticalParticleOptions> streamCodec() {
            return VeinRipperVerticalParticleOptions.STREAM_CODEC;
         }
      }
   );

   public static void register(IEventBus eventBus) {
      PARTICLE_TYPES.register(eventBus);
   }
}
