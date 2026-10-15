package com.somake.somakespells.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.somake.somakespells.registries.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record FireVortexParticleOptions(
   float radius, float height, int entityid, float angularVelocity, float ascent, float scale, float jitter, float lifeScale, int elementMode
) implements ParticleOptions {
   public static final MapCodec<FireVortexParticleOptions> MAP_CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Codec.FLOAT.fieldOf("radius").forGetter(FireVortexParticleOptions::radius),
            Codec.FLOAT.fieldOf("height").forGetter(FireVortexParticleOptions::height),
            Codec.INT.fieldOf("entityid").forGetter(FireVortexParticleOptions::entityid),
            Codec.FLOAT.fieldOf("angular_velocity").forGetter(FireVortexParticleOptions::angularVelocity),
            Codec.FLOAT.fieldOf("ascent").forGetter(FireVortexParticleOptions::ascent),
            Codec.FLOAT.fieldOf("scale").forGetter(FireVortexParticleOptions::scale),
            Codec.FLOAT.fieldOf("jitter").forGetter(FireVortexParticleOptions::jitter),
            Codec.FLOAT.fieldOf("life_scale").forGetter(FireVortexParticleOptions::lifeScale),
            Codec.INT.optionalFieldOf("element_mode", 0).forGetter(FireVortexParticleOptions::elementMode)
         )
         .apply(instance, FireVortexParticleOptions::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, FireVortexParticleOptions> STREAM_CODEC = StreamCodec.of(
      (buf, option) -> {
         buf.writeFloat(option.radius);
         buf.writeFloat(option.height);
         buf.writeInt(option.entityid);
         buf.writeFloat(option.angularVelocity);
         buf.writeFloat(option.ascent);
         buf.writeFloat(option.scale);
         buf.writeFloat(option.jitter);
         buf.writeFloat(option.lifeScale);
         buf.writeInt(option.elementMode);
      },
      buf -> new FireVortexParticleOptions(
         buf.readFloat(), buf.readFloat(), buf.readInt(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readInt()
      )
   );

   public ParticleType<?> getType() {
      if (this.elementMode == 1) {
         return (ParticleType<?>)ModParticles.SOUL_FIRE_VORTEX_PARTICLE.get();
      } else {
         return this.elementMode == 2
            ? ModParticles.infernalFireVortexOr((ParticleType<FireVortexParticleOptions>)ModParticles.FIRE_VORTEX_PARTICLE.get())
            : (ParticleType)ModParticles.FIRE_VORTEX_PARTICLE.get();
      }
   }
}
