package net.kayn.better_spellcasting.network;

import net.kayn.better_spellcasting.BetterSpellcasting;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(
   modid = "better_spellcasting"
)
public class NetworkHandler {
   @SubscribeEvent
   public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
      BetterSpellcasting.LOGGER.info("Registering payload handlers for {}", "better_spellcasting");
      PayloadRegistrar registrar = event.registrar("better_spellcasting").versioned("1.0.0").optional();
      registrar.playToServer(SwingMessage.TYPE, SwingMessage.CODEC, SwingMessage::handle);
      BetterSpellcasting.LOGGER.info("Registered SwingMessage payload");
   }
}
