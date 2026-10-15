package net.neoforged.neoforge.internal.fabric;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Server-to-client spawn data for {@code IEntityWithComplexSpawn} entities —
 * NeoForge would fold this into the entity-spawning packet; here the vanilla
 * add-entity packet goes first, then this payload applies
 * {@code readSpawnData} once the entity exists client-side.
 */
public record SpawnDataPayload(int entityId, byte[] data) implements CustomPacketPayload {
    public static final Type<SpawnDataPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("neoforge", "spawn_data"));
    public static final StreamCodec<FriendlyByteBuf, SpawnDataPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SpawnDataPayload::entityId,
            ByteBufCodecs.BYTE_ARRAY, SpawnDataPayload::data,
            SpawnDataPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
