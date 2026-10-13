package io.redspace.ironslib.soulfire.network;

import io.redspace.ironslib.soulfire.client.SoulFireClientHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundSoulFirePacket(int entityId, boolean soul) implements CustomPacketPayload {
   public static final Type<ClientboundSoulFirePacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("irons_lib", "soul_fire"));
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSoulFirePacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT, ClientboundSoulFirePacket::entityId, ByteBufCodecs.BOOL, ClientboundSoulFirePacket::soul, ClientboundSoulFirePacket::new
   );

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handle(ClientboundSoulFirePacket payload, IPayloadContext context) {
      context.enqueueWork(() -> SoulFireClientHandler.onSoulFire(payload.entityId(), payload.soul()));
   }
}
