package com.somake.somakespells.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.somake.somakespells.registries.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record CustomStormParticleOptions(float r, float g, float b, float width, float height, int entityid, float expansion) implements ParticleOptions {
   public static final MapCodec<CustomStormParticleOptions> MAP_CODEC = RecordCodecBuilder.mapCodec(
      object -> object.group(
            Codec.FLOAT.fieldOf("r").forGetter(CustomStormParticleOptions::r),
            Codec.FLOAT.fieldOf("g").forGetter(CustomStormParticleOptions::g),
            Codec.FLOAT.fieldOf("b").forGetter(CustomStormParticleOptions::b),
            Codec.FLOAT.fieldOf("width").forGetter(CustomStormParticleOptions::width),
            Codec.FLOAT.fieldOf("height").forGetter(CustomStormParticleOptions::height),
            Codec.INT.fieldOf("entityid").forGetter(CustomStormParticleOptions::entityid),
            Codec.FLOAT.fieldOf("expansion").forGetter(CustomStormParticleOptions::expansion)
         )
         .apply(object, CustomStormParticleOptions::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, CustomStormParticleOptions> STREAM_CODEC = StreamCodec.of((buf, option) -> {
      buf.writeFloat(option.r);
      buf.writeFloat(option.g);
      buf.writeFloat(option.b);
      buf.writeFloat(option.width);
      buf.writeFloat(option.height);
      buf.writeInt(option.entityid);
      buf.writeFloat(option.expansion);
   }, buf -> new CustomStormParticleOptions(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readInt(), buf.readFloat()));

   public ParticleType<?> getType() {
      return (ParticleType<?>)ModParticles.CUSTOM_STORM_PARTICLE.get();
   }
}
