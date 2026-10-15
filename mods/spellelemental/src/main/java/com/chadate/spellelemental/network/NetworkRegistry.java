package com.chadate.spellelemental.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(
   modid = "spellelemental"
)
public class NetworkRegistry {
   @SubscribeEvent
   public static void register(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("1");
      registrar.playToClient(ElementAttachmentSyncPacket.TYPE, ElementAttachmentSyncPacket.STREAM_CODEC, ElementAttachmentSyncPacket::handleClient);
      registrar.playToClient(ActionBarMessagePacket.TYPE, ActionBarMessagePacket.STREAM_CODEC, ActionBarMessagePacket::handleClient);
      registrar.playToClient(SwordOilConfigSyncPacket.TYPE, SwordOilConfigSyncPacket.STREAM_CODEC, SwordOilConfigSyncPacket::handleClient);
   }
}
