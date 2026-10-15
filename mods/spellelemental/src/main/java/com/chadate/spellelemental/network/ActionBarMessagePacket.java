package com.chadate.spellelemental.network;

import com.chadate.spellelemental.client.render.ActionBarMessageRenderer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ActionBarMessagePacket(Component message) implements CustomPacketPayload {
   public static final Type<ActionBarMessagePacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("spellelemental", "actionbar_message"));
   public static final StreamCodec<RegistryFriendlyByteBuf, ActionBarMessagePacket> STREAM_CODEC = StreamCodec.composite(
      ComponentSerialization.STREAM_CODEC, ActionBarMessagePacket::message, ActionBarMessagePacket::new
   );

   public Type<ActionBarMessagePacket> type() {
      return TYPE;
   }

   public static void handleClient(ActionBarMessagePacket packet, IPayloadContext context) {
      context.enqueueWork(() -> ActionBarMessageRenderer.setMessage(packet.message()));
   }
}
