package net.alshanex.familiarslib.event;

import net.alshanex.familiarslib.network.RequestFamiliarSelectionPacket;
import net.alshanex.familiarslib.network.SummonPetPackage;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(
   modid = "familiarslib",
   bus = Bus.GAME,
   value = {Dist.CLIENT}
)
public class ClientEvents {
   @SubscribeEvent
   public static void onClientTick(Post event) {
      handleKeybinds();
   }

   private static void handleKeybinds() {
      while (KeyMappings.SUMMONING_KEYMAP.consume()) {
         PacketDistributor.sendToServer(new SummonPetPackage(), new CustomPacketPayload[0]);
      }

      while (KeyMappings.SCREEN_KEYMAP.consume()) {
         PacketDistributor.sendToServer(new RequestFamiliarSelectionPacket(), new CustomPacketPayload[0]);
      }
   }
}
