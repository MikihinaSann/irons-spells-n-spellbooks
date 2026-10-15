package com.chadate.spellelemental.event;

import com.chadate.spellelemental.SpellElemental;
import com.chadate.spellelemental.network.ElementAttachmentSyncPacket;
import com.chadate.spellelemental.network.SwordOilConfigSyncPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(
   modid = "spellelemental"
)
public class ElementAttachmentSyncHandler {
   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof ServerPlayer serverPlayer) {
         ElementAttachmentSyncPacket attachmentSyncPacket = ElementAttachmentSyncPacket.create();
         PacketDistributor.sendToPlayer(serverPlayer, attachmentSyncPacket, new CustomPacketPayload[0]);
         SwordOilConfigSyncPacket oilConfigSyncPacket = SwordOilConfigSyncPacket.create();
         PacketDistributor.sendToPlayer(serverPlayer, oilConfigSyncPacket, new CustomPacketPayload[0]);
         SpellElemental.LOGGER.debug("已向玩家 {} 发送元素附着资源和精油配置同步数据包", serverPlayer.getName().getString());
      }
   }

   @SubscribeEvent
   public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
      if (event.getEntity() instanceof ServerPlayer serverPlayer) {
         ElementAttachmentSyncPacket attachmentSyncPacket = ElementAttachmentSyncPacket.create();
         PacketDistributor.sendToPlayer(serverPlayer, attachmentSyncPacket, new CustomPacketPayload[0]);
         SwordOilConfigSyncPacket oilConfigSyncPacket = SwordOilConfigSyncPacket.create();
         PacketDistributor.sendToPlayer(serverPlayer, oilConfigSyncPacket, new CustomPacketPayload[0]);
         SpellElemental.LOGGER.debug("已向切换维度的玩家 {} 发送元素附着资源和精油配置同步数据包", serverPlayer.getName().getString());
      }
   }
}
