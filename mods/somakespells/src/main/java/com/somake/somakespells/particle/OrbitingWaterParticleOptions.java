package com.somake.somakespells.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.somake.somakespells.registries.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record OrbitingWaterParticleOptions(float r, float g, float b, float width, float height, int entityid, float expansion) implements ParticleOptions {
   public static final MapCodec<OrbitingWaterParticleOptions> MAP_CODEC = RecordCodecBuilder.mapCodec(
      object -> object.group(
            Codec.FLOAT.fieldOf("r").forGetter(OrbitingWaterParticleOptions::r),
            Codec.FLOAT.fieldOf("g").forGetter(OrbitingWaterParticleOptions::g),
            Codec.FLOAT.fieldOf("b").forGetter(OrbitingWaterParticleOptions::b),
            Codec.FLOAT.fieldOf("width").forGetter(OrbitingWaterParticleOptions::width),
            Codec.FLOAT.fieldOf("height").forGetter(OrbitingWaterParticleOptions::height),
            Codec.INT.fieldOf("entityid").forGetter(OrbitingWaterParticleOptions::entityid),
            Codec.FLOAT.fieldOf("expansion").forGetter(OrbitingWaterParticleOptions::expansion)
         )
         .apply(object, OrbitingWaterParticleOptions::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, OrbitingWaterParticleOptions> STREAM_CODEC = StreamCodec.of(
      (buf, option) -> {
         buf.writeFloat(option.r);
         buf.writeFloat(option.g);
         buf.writeFloat(option.b);
         buf.writeFloat(option.width);
         buf.writeFloat(option.height);
         buf.writeInt(option.entityid);
         buf.writeFloat(option.expansion);
      },
      buf -> new OrbitingWaterParticleOptions(
         buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readInt(), buf.readFloat()
      )
   );

   public ParticleType<?> getType() {
      return (ParticleType<?>)ModParticles.ORBITING_WATER_PARTICLE.get();
   }
}
