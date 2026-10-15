package com.chadate.spellelemental.network;

import com.chadate.spellelemental.SpellElemental;
import com.chadate.spellelemental.integration.jei.data.SwordOilConfigLoader;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SwordOilConfigSyncPacket(List<SwordOilConfigSyncPacket.SwordOilConfigData> configs) implements CustomPacketPayload {
   public static final Type<SwordOilConfigSyncPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("spellelemental", "sword_oil_config_sync"));
   public static final StreamCodec<RegistryFriendlyByteBuf, SwordOilConfigSyncPacket> STREAM_CODEC = StreamCodec.composite(StreamCodec.of((buf, configs) -> {
      buf.writeInt(configs.size());

      for (SwordOilConfigSyncPacket.SwordOilConfigData config : configs) {
         buf.writeUtf(config.itemId());
         buf.writeUtf(config.element());
         buf.writeInt(config.amount());
         buf.writeUtf(config.displayName());
      }
   }, buf -> {
      int size = buf.readInt();
      List<SwordOilConfigSyncPacket.SwordOilConfigData> configs = new ArrayList<>();

      for (int i = 0; i < size; i++) {
         configs.add(new SwordOilConfigSyncPacket.SwordOilConfigData(buf.readUtf(), buf.readUtf(), buf.readInt(), buf.readUtf()));
      }

      return configs;
   }), SwordOilConfigSyncPacket::configs, SwordOilConfigSyncPacket::new);

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static SwordOilConfigSyncPacket create() {
      List<SwordOilConfigSyncPacket.SwordOilConfigData> configData = new ArrayList<>();

      for (SwordOilConfigLoader.SwordOilConfig config : SwordOilConfigLoader.getSwordOilConfigs()) {
         configData.add(new SwordOilConfigSyncPacket.SwordOilConfigData(config.getItemId(), config.getElement(), config.getAmount(), config.getDisplayName()));
      }

      return new SwordOilConfigSyncPacket(configData);
   }

   public static void handleClient(SwordOilConfigSyncPacket packet, IPayloadContext context) {
      context.enqueueWork(
         () -> {
            SwordOilConfigLoader.clearConfigs();

            for (SwordOilConfigSyncPacket.SwordOilConfigData configData : packet.configs) {
               SwordOilConfigLoader.addConfig(
                  new SwordOilConfigLoader.SwordOilConfig(configData.itemId(), configData.element(), configData.amount(), configData.displayName())
               );
            }

            SpellElemental.LOGGER.info("已同步 {} 个精油配置到客户端", packet.configs.size());
         }
      );
   }

   public record SwordOilConfigData(String itemId, String element, int amount, String displayName) {
   }
}
