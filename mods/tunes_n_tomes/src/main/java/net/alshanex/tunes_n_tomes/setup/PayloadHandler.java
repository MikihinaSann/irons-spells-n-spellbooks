package net.alshanex.tunes_n_tomes.setup;

import net.alshanex.tunes_n_tomes.network.SyncSoundMarkerDataPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(
   bus = Bus.MOD,
   modid = "tunes_n_tomes"
)
public class PayloadHandler {
   @SubscribeEvent
   public static void register(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar payloadRegistrar = event.registrar("tunes_n_tomes").versioned("1.0.0").optional();
      payloadRegistrar.playToClient(SyncSoundMarkerDataPacket.TYPE, SyncSoundMarkerDataPacket.STREAM_CODEC, SyncSoundMarkerDataPacket::handle);
   }
}
