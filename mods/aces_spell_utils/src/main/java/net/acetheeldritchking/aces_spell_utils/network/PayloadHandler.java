package net.acetheeldritchking.aces_spell_utils.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(
   bus = Bus.MOD,
   modid = "aces_spell_utils"
)
public class PayloadHandler {
   @SubscribeEvent
   public static void register(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar payloadRegistrar = event.registrar("aces_spell_utils").versioned("1.0.0").optional();
      payloadRegistrar.playToClient(AddShaderEffectPacket.TYPE, AddShaderEffectPacket.STREAM_CODEC, AddShaderEffectPacket::handle);
      payloadRegistrar.playToClient(RemoveShaderEffectPacket.TYPE, RemoveShaderEffectPacket.STREAM_CODEC, RemoveShaderEffectPacket::handle);
   }

   public static void handleClientBoundShaderEffect(String modid, String location) {
      Minecraft mc = Minecraft.getInstance();
      GameRenderer render = mc.gameRenderer;
      LocalPlayer clientPlayer = mc.player;
      if (clientPlayer != null) {
         render.loadEffect(ResourceLocation.fromNamespaceAndPath(modid, location));
      }
   }

   public static void removeClientBoundShaderEffect() {
      Minecraft mc = Minecraft.getInstance();
      GameRenderer render = mc.gameRenderer;
      LocalPlayer clientPlayer = mc.player;
      if (clientPlayer != null) {
         render.shutdownEffect();
      }
   }
}
