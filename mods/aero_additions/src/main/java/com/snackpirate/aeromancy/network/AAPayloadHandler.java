package com.snackpirate.aeromancy.network;

import com.snackpirate.aeromancy.spells.airstep.AirstepPacket;
import com.snackpirate.aeromancy.spells.dash.DashParticlesPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(
   modid = "aero_additions"
)
public class AAPayloadHandler {
   @SubscribeEvent
   public static void register(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar payloadRegistrar = event.registrar("aero_additions").versioned("1.0.0").optional();
      payloadRegistrar.playToClient(DashParticlesPacket.TYPE, DashParticlesPacket.STREAM_CODEC, DashParticlesPacket::handle);
      payloadRegistrar.playToServer(AirstepPacket.TYPE, AirstepPacket.STREAM_CODEC, AirstepPacket::handle);
      payloadRegistrar.playToClient(AASyncPlayerDataPacket.TYPE, AASyncPlayerDataPacket.STREAM_CODEC, AASyncPlayerDataPacket::handle);
   }
}
