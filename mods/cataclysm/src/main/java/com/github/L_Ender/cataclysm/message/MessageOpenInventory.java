package com.github.L_Ender.cataclysm.message;

import com.github.L_Ender.cataclysm.Cataclysm;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record MessageOpenInventory(int id, int size, int entityId) implements CustomPacketPayload {
    public static final Type<MessageOpenInventory> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Cataclysm.MODID, "open_inventory"));

    public static final StreamCodec<FriendlyByteBuf, MessageOpenInventory> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, MessageOpenInventory::id,
            ByteBufCodecs.INT, MessageOpenInventory::size,
            ByteBufCodecs.INT, MessageOpenInventory::entityId,
            MessageOpenInventory::new
    );

    public static void handle(MessageOpenInventory payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientPacketHandlers.openGuardInventory(payload);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
