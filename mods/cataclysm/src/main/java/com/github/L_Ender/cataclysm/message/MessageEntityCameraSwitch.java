package com.github.L_Ender.cataclysm.message;

import com.github.L_Ender.cataclysm.Cataclysm;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Packets to help sync the server's Aether boss bars with the client's.
 */
public abstract class MessageEntityCameraSwitch implements CustomPacketPayload {
    protected final int entityId;

    public MessageEntityCameraSwitch(int entityId) {
        this.entityId = entityId;
    }

    /**
     * Adds a boss bar for the client.
     */
    public static class FirstPerson extends MessageEntityCameraSwitch {
        public static final Type<FirstPerson> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Cataclysm.MODID, "first_person_camera"));

        public static final StreamCodec<RegistryFriendlyByteBuf, FirstPerson> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
                FirstPerson::getEntityId,
                FirstPerson::new);

        public FirstPerson(int entityId) {
            super(entityId);
        }

        @Override
        public Type<FirstPerson> type() {
            return TYPE;
        }

        public static void execute(FirstPerson payload, IPayloadContext context) {
            context.enqueueWork(() -> {
                ClientPacketHandlers.firstPersonCameraSwitch(payload, context);
            });
        }
    }

    /**
     * Removes a boss bar for the client.
     */
    public static class ThridPerson extends MessageEntityCameraSwitch {
        public static final Type<ThridPerson> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Cataclysm.MODID, "third_person_camera"));

        public static final StreamCodec<RegistryFriendlyByteBuf, ThridPerson> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
                ThridPerson::getEntityId,
                ThridPerson::new);

        public ThridPerson(int entityId) {
            super(entityId);
        }

        @Override
        public Type<ThridPerson> type() {
            return TYPE;
        }

        public static void execute(ThridPerson payload, IPayloadContext context) {
            context.enqueueWork(() -> {
                ClientPacketHandlers.thirdPersonCameraSwitch(payload, context);
            });
        }
    }


    public int getEntityId() {
        return this.entityId;
    }
}
