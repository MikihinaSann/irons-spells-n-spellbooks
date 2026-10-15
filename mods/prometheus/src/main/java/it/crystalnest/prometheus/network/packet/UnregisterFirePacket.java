package it.crystalnest.prometheus.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record UnregisterFirePacket(ResourceLocation fireType) implements CustomPacketPayload {
   public static final Type<UnregisterFirePacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("prometheus", "unregister_fire"));
   public static final StreamCodec<FriendlyByteBuf, UnregisterFirePacket> CODEC = StreamCodec.composite(
      ResourceLocation.STREAM_CODEC, UnregisterFirePacket::fireType, UnregisterFirePacket::new
   );

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
