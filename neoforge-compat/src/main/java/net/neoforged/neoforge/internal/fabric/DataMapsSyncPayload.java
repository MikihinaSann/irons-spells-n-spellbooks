package net.neoforged.neoforge.internal.fabric;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * S2C snapshot of all network-synced data maps — the NeoForge equivalent of
 * its data-map sync step during {@code OnDatapackSyncEvent}. The body is one
 * JSON document: {@code {typeId: {entryKeyOr#tag: valueJson}}} produced by
 * {@link DataMapManager#encodeSyncJson()} and consumed by
 * {@link DataMapManager#applySyncJson(String)}.
 */
public record DataMapsSyncPayload(byte[] json) implements CustomPacketPayload {
    public static final Type<DataMapsSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("neoforge", "sync_data_maps"));

    public static final StreamCodec<FriendlyByteBuf, DataMapsSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE_ARRAY, DataMapsSyncPayload::json, DataMapsSyncPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
