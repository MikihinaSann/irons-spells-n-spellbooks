package com.somake.somakespells.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.somake.somakespells.registries.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record WitheredRoseVortexParticleOptions(
   float radius, float height, int entityId, float angularVelocity, float ascent, float scale, float jitter, float lifeScale, int strand, boolean crimson
) implements ParticleOptions {
   public static final MapCodec<WitheredRoseVortexParticleOptions> MAP_CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Codec.FLOAT.fieldOf("radius").forGetter(WitheredRoseVortexParticleOptions::radius),
            Codec.FLOAT.fieldOf("height").forGetter(WitheredRoseVortexParticleOptions::height),
            Codec.INT.fieldOf("entity_id").forGetter(WitheredRoseVortexParticleOptions::entityId),
            Codec.FLOAT.fieldOf("angular_velocity").forGetter(WitheredRoseVortexParticleOptions::angularVelocity),
            Codec.FLOAT.fieldOf("ascent").forGetter(WitheredRoseVortexParticleOptions::ascent),
            Codec.FLOAT.fieldOf("scale").forGetter(WitheredRoseVortexParticleOptions::scale),
            Codec.FLOAT.fieldOf("jitter").forGetter(WitheredRoseVortexParticleOptions::jitter),
            Codec.FLOAT.fieldOf("life_scale").forGetter(WitheredRoseVortexParticleOptions::lifeScale),
            Codec.INT.optionalFieldOf("strand", 0).forGetter(WitheredRoseVortexParticleOptions::strand),
            Codec.BOOL.optionalFieldOf("crimson", false).forGetter(WitheredRoseVortexParticleOptions::crimson)
         )
         .apply(instance, WitheredRoseVortexParticleOptions::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, WitheredRoseVortexParticleOptions> STREAM_CODEC = StreamCodec.of(
      (buffer, option) -> {
         buffer.writeFloat(option.radius);
         buffer.writeFloat(option.height);
         buffer.writeInt(option.entityId);
         buffer.writeFloat(option.angularVelocity);
         buffer.writeFloat(option.ascent);
         buffer.writeFloat(option.scale);
         buffer.writeFloat(option.jitter);
         buffer.writeFloat(option.lifeScale);
         buffer.writeVarInt(option.strand);
         buffer.writeBoolean(option.crimson);
      },
      buffer -> new WitheredRoseVortexParticleOptions(
         buffer.readFloat(),
         buffer.readFloat(),
         buffer.readInt(),
         buffer.readFloat(),
         buffer.readFloat(),
         buffer.readFloat(),
         buffer.readFloat(),
         buffer.readFloat(),
         buffer.readVarInt(),
         buffer.readBoolean()
      )
   );

   public ParticleType<?> getType() {
      return (ParticleType<?>)ModParticles.WITHERED_ROSE_VORTEX_PARTICLE.get();
   }
}
