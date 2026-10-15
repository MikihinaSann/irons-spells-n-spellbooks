package com.somake.somakespells.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.somake.somakespells.registries.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ElementalFierySmokeParticleOptions(float r, float g, float b, float scale) implements ParticleOptions {
   public static final MapCodec<ElementalFierySmokeParticleOptions> MAP_CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Codec.FLOAT.fieldOf("r").forGetter(ElementalFierySmokeParticleOptions::r),
            Codec.FLOAT.fieldOf("g").forGetter(ElementalFierySmokeParticleOptions::g),
            Codec.FLOAT.fieldOf("b").forGetter(ElementalFierySmokeParticleOptions::b),
            Codec.FLOAT.optionalFieldOf("scale", 1.0F).forGetter(ElementalFierySmokeParticleOptions::scale)
         )
         .apply(instance, ElementalFierySmokeParticleOptions::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, ElementalFierySmokeParticleOptions> STREAM_CODEC = StreamCodec.of((buf, option) -> {
      buf.writeFloat(option.r);
      buf.writeFloat(option.g);
      buf.writeFloat(option.b);
      buf.writeFloat(option.scale);
   }, buf -> new ElementalFierySmokeParticleOptions(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat()));

   public ParticleType<?> getType() {
      return (ParticleType<?>)ModParticles.ELEMENTAL_FIERY_SMOKE.get();
   }
}
