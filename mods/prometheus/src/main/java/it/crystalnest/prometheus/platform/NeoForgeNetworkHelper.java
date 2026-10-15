package it.crystalnest.prometheus.platform;

import it.crystalnest.prometheus.ModLoader;
import it.crystalnest.prometheus.api.Fire;
import it.crystalnest.prometheus.network.handler.FirePacketHandler;
import it.crystalnest.prometheus.network.packet.RegisterFirePacket;
import it.crystalnest.prometheus.network.packet.UnregisterFirePacket;
import it.crystalnest.prometheus.platform.services.NetworkHelper;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.Nullable;

public final class NeoForgeNetworkHelper implements NetworkHelper {
   private static void registerPackets(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("1");
      registrar.playToClient(RegisterFirePacket.TYPE, RegisterFirePacket.CODEC, (packet, context) -> FirePacketHandler.handle(packet));
      registrar.playToClient(UnregisterFirePacket.TYPE, UnregisterFirePacket.CODEC, (packet, context) -> FirePacketHandler.handle(packet));
   }

   @Override
   public void sendToClient(@Nullable ServerPlayer player, Fire fire) {
      if (player == null) {
         PacketDistributor.sendToAllPlayers(new RegisterFirePacket(fire), new CustomPacketPayload[0]);
      } else {
         PacketDistributor.sendToPlayer(player, new RegisterFirePacket(fire), new CustomPacketPayload[0]);
      }
   }

   @Override
   public void sendToClient(@Nullable ServerPlayer player, ResourceLocation fireType) {
      if (player == null) {
         PacketDistributor.sendToAllPlayers(new UnregisterFirePacket(fireType), new CustomPacketPayload[0]);
      } else {
         PacketDistributor.sendToPlayer(player, new UnregisterFirePacket(fireType), new CustomPacketPayload[0]);
      }
   }

   @Override
   public void register() {
      ModLoader.getBus().addListener(NeoForgeNetworkHelper::registerPackets);
   }
}
