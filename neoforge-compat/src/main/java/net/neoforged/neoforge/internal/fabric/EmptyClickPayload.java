package net.neoforged.neoforge.internal.fabric;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Client-to-server notification that the player left-clicked empty air —
 * mirrors NeoForge's EmptyClickPayload which triggers the server-side
 * {@link net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty}.
 */
public record EmptyClickPayload() implements CustomPacketPayload {
    public static final Type<EmptyClickPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("neoforge", "left_click_empty"));
    public static final StreamCodec<net.minecraft.network.FriendlyByteBuf, EmptyClickPayload> STREAM_CODEC =
            StreamCodec.unit(new EmptyClickPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
