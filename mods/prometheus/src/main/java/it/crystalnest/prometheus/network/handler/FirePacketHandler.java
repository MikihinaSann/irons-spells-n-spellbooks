package it.crystalnest.prometheus.network.handler;

import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.client.FireClientManager;
import it.crystalnest.prometheus.network.packet.RegisterFirePacket;
import it.crystalnest.prometheus.network.packet.UnregisterFirePacket;

public final class FirePacketHandler {
   private FirePacketHandler() {
   }

   public static void handle(RegisterFirePacket packet) {
      if (!FireManager.isRegisteredType(packet.fire().getFireType())) {
         FireManager.registerFire(packet.fire());
      }

      FireClientManager.registerFire(packet.fire());
   }

   public static void handle(UnregisterFirePacket packet) {
      FireManager.unregisterFire(packet.fireType());
      FireClientManager.unregisterFire(packet.fireType());
   }
}
