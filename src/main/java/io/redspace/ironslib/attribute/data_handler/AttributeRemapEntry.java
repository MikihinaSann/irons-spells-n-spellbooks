package io.redspace.ironslib.attribute.data_handler;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record AttributeRemapEntry(ResourceLocation from, ResourceLocation to) {
   public static final Codec<AttributeRemapEntry> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
               ResourceLocation.CODEC.fieldOf("from").forGetter(AttributeRemapEntry::from),
               ResourceLocation.CODEC.fieldOf("to").forGetter(AttributeRemapEntry::to)
            )
            .apply(builder, AttributeRemapEntry::new)
   );
}
