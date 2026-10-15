package net.acetheeldritchking.discerning_the_eldritch.networking;

import net.acetheeldritchking.discerning_the_eldritch.networking.devour.GetSyncDevourStacksPacket;
import net.acetheeldritchking.discerning_the_eldritch.networking.devour.ResetSyncDevourStacksPacket;
import net.acetheeldritchking.discerning_the_eldritch.networking.devour.SetSyncDevourStacksPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(
   bus = Bus.MOD,
   modid = "discerning_the_eldritch"
)
public class DTEPayloadHandler {
   @SubscribeEvent
   public static void register(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar payloadRegistrar = event.registrar("discerning_the_eldritch").versioned("1.0.0").optional();
      payloadRegistrar.playToClient(SetSyncDevourStacksPacket.TYPE, SetSyncDevourStacksPacket.STREAM_CODEC, SetSyncDevourStacksPacket::handle);
      payloadRegistrar.playToClient(ResetSyncDevourStacksPacket.TYPE, ResetSyncDevourStacksPacket.STREAM_CODEC, ResetSyncDevourStacksPacket::handle);
      payloadRegistrar.playToClient(GetSyncDevourStacksPacket.TYPE, GetSyncDevourStacksPacket.STREAM_CODEC, GetSyncDevourStacksPacket::handle);
   }
}
