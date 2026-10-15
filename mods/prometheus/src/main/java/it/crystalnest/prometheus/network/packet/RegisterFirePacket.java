package it.crystalnest.prometheus.network.packet;

import it.crystalnest.prometheus.api.Fire;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record RegisterFirePacket(Fire fire) implements CustomPacketPayload {
   public static final Type<RegisterFirePacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("prometheus", "register_fire"));
   public static final StreamCodec<FriendlyByteBuf, RegisterFirePacket> CODEC = StreamCodec.composite(
      Fire.STREAM_CODEC, RegisterFirePacket::fire, RegisterFirePacket::new
   );

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
