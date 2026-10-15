package net.alshanex.alshanex_familiars.setup;

import net.alshanex.alshanex_familiars.network.ServerboundSetAutomatorRecipePacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(
   bus = Bus.MOD,
   modid = "alshanex_familiars"
)
public class PayloadHandler {
   @SubscribeEvent
   public static void register(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar payloadRegistrar = event.registrar("alshanex_familiars").versioned("1.1.0").optional();
      payloadRegistrar.playToServer(
         ServerboundSetAutomatorRecipePacket.TYPE, ServerboundSetAutomatorRecipePacket.STREAM_CODEC, ServerboundSetAutomatorRecipePacket::handle
      );
   }
}
