package com.userofbricks.expanded_combat.network.server;

import java.util.Objects;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

public record PacketIntAttachment(int id, AttachmentType<Integer> attachmentType, int data) implements CustomPacketPayload {
   public static final Type<PacketIntAttachment> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("expanded_combat", "int_attachment"));
   public static final StreamCodec<RegistryFriendlyByteBuf, PacketIntAttachment> STREAM_CODEC = StreamCodec.of(
      (pBuffer, pValue) -> {
         ByteBufCodecs.INT.encode(pBuffer, pValue.id());
         ByteBufCodecs.STRING_UTF8.encode(pBuffer, Objects.requireNonNull(NeoForgeRegistries.ATTACHMENT_TYPES.getKey(pValue.attachmentType)).toString());
         ByteBufCodecs.INT.encode(pBuffer, pValue.data());
      },
      pBuffer -> {
         int id = (Integer)ByteBufCodecs.INT.decode(pBuffer);
         AttachmentType<Integer> attachmentType = (AttachmentType<Integer>)NeoForgeRegistries.ATTACHMENT_TYPES
            .get(ResourceLocation.parse((String)ByteBufCodecs.STRING_UTF8.decode(pBuffer)));
         int data = (Integer)ByteBufCodecs.INT.decode(pBuffer);
         return new PacketIntAttachment(id, attachmentType, data);
      }
   );

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
