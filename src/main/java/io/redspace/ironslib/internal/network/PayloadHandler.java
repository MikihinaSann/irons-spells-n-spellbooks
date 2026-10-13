package io.redspace.ironslib.internal.network;

import io.redspace.ironslib.attribute.network.SyncAttributeRemapsPacket;
import io.redspace.ironslib.internal.network.packet.CritParticlesAreaPacket;
import io.redspace.ironslib.internal.network.packet.OpenStatuePoseScreenPacket;
import io.redspace.ironslib.internal.network.packet.SelectStatuePosePacket;
import io.redspace.ironslib.internal.network.packet.SyncPatreonDataPacket;
import io.redspace.ironslib.kinetic_weapon.network.ClientboundKineticHitPacket;
import io.redspace.ironslib.soulfire.network.ClientboundSoulFirePacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(
   modid = "irons_lib"
)
public class PayloadHandler {
   @SubscribeEvent
   public static void register(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar payloadRegistrar = event.registrar("irons_lib").versioned("1.0.0").optional();
      payloadRegistrar.playToServer(SelectStatuePosePacket.TYPE, SelectStatuePosePacket.STREAM_CODEC, SelectStatuePosePacket::handle);
      payloadRegistrar.playToClient(OpenStatuePoseScreenPacket.TYPE, OpenStatuePoseScreenPacket.STREAM_CODEC, OpenStatuePoseScreenPacket::handle);
      payloadRegistrar.playToClient(CritParticlesAreaPacket.TYPE, CritParticlesAreaPacket.STREAM_CODEC, CritParticlesAreaPacket::handle);
      payloadRegistrar.playToClient(SyncPatreonDataPacket.TYPE, SyncPatreonDataPacket.STREAM_CODEC, SyncPatreonDataPacket::handle);
      payloadRegistrar.playToClient(SyncAttributeRemapsPacket.TYPE, SyncAttributeRemapsPacket.STREAM_CODEC, SyncAttributeRemapsPacket::handle);
      payloadRegistrar.playToClient(ClientboundKineticHitPacket.TYPE, ClientboundKineticHitPacket.STREAM_CODEC, ClientboundKineticHitPacket::handle);
      payloadRegistrar.playToClient(ClientboundSoulFirePacket.TYPE, ClientboundSoulFirePacket.STREAM_CODEC, ClientboundSoulFirePacket::handle);
   }
}
