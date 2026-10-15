package net.warphan.iss_magicfromtheeast.registries;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.warphan.iss_magicfromtheeast.particle.JadeSlashParticleOptions;

public class MFTEParticleRegistries {
   public static final DeferredRegister<ParticleType<?>> MFTE_PARTICLE_TYPE = DeferredRegister.create(Registries.PARTICLE_TYPE, "iss_magicfromtheeast");
   public static final Supplier<SimpleParticleType> JADE_SHATTER_PARTICLE = MFTE_PARTICLE_TYPE.register("jade_shatter", () -> new SimpleParticleType(false));
   public static final Supplier<ParticleType<JadeSlashParticleOptions>> JADE_SLASH_PARTICLE = MFTE_PARTICLE_TYPE.register(
      "jade_slash", () -> new ParticleType<JadeSlashParticleOptions>(true) {
         public MapCodec<JadeSlashParticleOptions> codec() {
            return JadeSlashParticleOptions.MAP_CODEC;
         }

         public StreamCodec<? super RegistryFriendlyByteBuf, JadeSlashParticleOptions> streamCodec() {
            return JadeSlashParticleOptions.STREAM_CODEC;
         }
      }
   );

   public static void register(IEventBus eventBus) {
      MFTE_PARTICLE_TYPE.register(eventBus);
   }
}
