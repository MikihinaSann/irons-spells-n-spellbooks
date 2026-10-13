package io.redspace.ironslib.transmog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import io.redspace.ironslib.registry.IronsLibRegistries;
import io.redspace.ironslib.util.CodecHelper;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record TransmogItemData(TransmogHolder transmog, int dyeColor) {
   public static final Codec<TransmogItemData> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
               TransmogHolder.CODEC.fieldOf("transmog").forGetter(TransmogItemData::transmog),
               Codec.INT.optionalFieldOf("dye_color", -1).forGetter(TransmogItemData::dyeColor)
            )
            .apply(builder, TransmogItemData::new)
   );
   public static final StreamCodec<ByteBuf, TransmogItemData> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

   public TransmogItemData(TransmogHolder transmog) {
      this(transmog, transmog.dyeConfig().defaultColor());
   }

   public static boolean has(ItemStack stack) {
      CodecHelper.replaceLegacy(stack, IronsLibRegistries.Legacy.ComponentRegistry.TRANSMOG, IronsLibRegistries.ComponentRegistry.TRANSMOG);
      return stack.has(IronsLibRegistries.ComponentRegistry.TRANSMOG);
   }

   public static void set(ItemStack stack, TransmogItemData holder) {
      CodecHelper.replaceLegacy(stack, IronsLibRegistries.Legacy.ComponentRegistry.TRANSMOG, IronsLibRegistries.ComponentRegistry.TRANSMOG);
      stack.set(IronsLibRegistries.ComponentRegistry.TRANSMOG, holder);
   }

   @Nullable
   public static TransmogItemData get(ItemStack stack) {
      CodecHelper.replaceLegacy(stack, IronsLibRegistries.Legacy.ComponentRegistry.TRANSMOG, IronsLibRegistries.ComponentRegistry.TRANSMOG);
      return (TransmogItemData)stack.get(IronsLibRegistries.ComponentRegistry.TRANSMOG);
   }

   public static void remove(ItemStack stack) {
      CodecHelper.replaceLegacy(stack, IronsLibRegistries.Legacy.ComponentRegistry.TRANSMOG, IronsLibRegistries.ComponentRegistry.TRANSMOG);
      stack.remove(IronsLibRegistries.ComponentRegistry.TRANSMOG);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else {
         if (obj instanceof TransmogItemData data && this.transmog.equals(data.transmog) && this.dyeColor == data.dyeColor) {
            return true;
         }

         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.transmog.hashCode() + this.dyeColor * 31;
   }
}
