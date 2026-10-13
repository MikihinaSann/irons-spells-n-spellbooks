package io.redspace.ironslib.kinetic_weapon.network;

import io.redspace.ironslib.kinetic_weapon.client.KineticHitFeedback;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundKineticHitPacket(int attackerId, Optional<Holder<SoundEvent>> hitSound) implements CustomPacketPayload {
   public static final Type<ClientboundKineticHitPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("irons_lib", "kinetic_hit"));
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundKineticHitPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT,
      ClientboundKineticHitPacket::attackerId,
      SoundEvent.STREAM_CODEC.apply(ByteBufCodecs::optional),
      ClientboundKineticHitPacket::hitSound,
      ClientboundKineticHitPacket::new
   );

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handle(ClientboundKineticHitPacket payload, IPayloadContext context) {
      context.enqueueWork(() -> KineticHitFeedback.onKineticHit(payload.attackerId(), payload.hitSound()));
   }
}
