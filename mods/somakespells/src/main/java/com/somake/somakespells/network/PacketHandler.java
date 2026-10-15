package com.somake.somakespells.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(
   modid = "somakespells",
   bus = Bus.MOD
)
public class PacketHandler {
   @SubscribeEvent
   public static void register(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("somakespells").versioned("1.0.0");
      registrar.playToClient(SyncSpellMasteryPacket.TYPE, SyncSpellMasteryPacket.STREAM_CODEC, SyncSpellMasteryPacket::handle);
      registrar.playToClient(SyncWeaponBalancePacket.TYPE, SyncWeaponBalancePacket.STREAM_CODEC, SyncWeaponBalancePacket::handle);
      registrar.playToClient(ConnectionVfxPacket.TYPE, ConnectionVfxPacket.STREAM_CODEC, ConnectionVfxPacket::handle);
      registrar.playToClient(ElementalFieryExplosionPacket.TYPE, ElementalFieryExplosionPacket.STREAM_CODEC, ElementalFieryExplosionPacket::handle);
      registrar.playToClient(WaterBallExplosionVfxPacket.TYPE, WaterBallExplosionVfxPacket.STREAM_CODEC, WaterBallExplosionVfxPacket::handle);
      registrar.playToServer(FireOrbsAttackPacket.TYPE, FireOrbsAttackPacket.STREAM_CODEC, FireOrbsAttackPacket::handle);
   }
}
