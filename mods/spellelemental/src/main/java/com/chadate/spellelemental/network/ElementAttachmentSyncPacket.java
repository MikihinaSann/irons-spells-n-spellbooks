package com.chadate.spellelemental.network;

import com.chadate.spellelemental.SpellElemental;
import com.chadate.spellelemental.element.attachment.data.UnifiedElementAttachmentAssets;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ElementAttachmentSyncPacket(Map<String, String> iconMap, Map<String, String> particleMap, Map<String, String> schoolMap)
   implements CustomPacketPayload {
   public static final Type<ElementAttachmentSyncPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("spellelemental", "element_attachment_sync"));
   public static final StreamCodec<RegistryFriendlyByteBuf, ElementAttachmentSyncPacket> STREAM_CODEC = StreamCodec.composite(StreamCodec.of((buf, map) -> {
      buf.writeInt(map.size());
      map.forEach((key, value) -> {
         buf.writeUtf(key);
         buf.writeUtf(value);
      });
   }, buf -> {
      int size = buf.readInt();
      Map<String, String> map = new HashMap<>();

      for (int i = 0; i < size; i++) {
         map.put(buf.readUtf(), buf.readUtf());
      }

      return map;
   }), ElementAttachmentSyncPacket::iconMap, StreamCodec.of((buf, map) -> {
      buf.writeInt(map.size());
      map.forEach((key, value) -> {
         buf.writeUtf(key);
         buf.writeUtf(value);
      });
   }, buf -> {
      int size = buf.readInt();
      Map<String, String> map = new HashMap<>();

      for (int i = 0; i < size; i++) {
         map.put(buf.readUtf(), buf.readUtf());
      }

      return map;
   }), ElementAttachmentSyncPacket::particleMap, StreamCodec.of((buf, map) -> {
      buf.writeInt(map.size());
      map.forEach((key, value) -> {
         buf.writeUtf(key);
         buf.writeUtf(value);
      });
   }, buf -> {
      int size = buf.readInt();
      Map<String, String> map = new HashMap<>();

      for (int i = 0; i < size; i++) {
         map.put(buf.readUtf(), buf.readUtf());
      }

      return map;
   }), ElementAttachmentSyncPacket::schoolMap, ElementAttachmentSyncPacket::new);

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static ElementAttachmentSyncPacket create() {
      return new ElementAttachmentSyncPacket(
         UnifiedElementAttachmentAssets.getAllIcons(), UnifiedElementAttachmentAssets.getAllParticleEffects(), UnifiedElementAttachmentAssets.getAllSchools()
      );
   }

   public static void handleClient(ElementAttachmentSyncPacket packet, IPayloadContext context) {
      context.enqueueWork(
         () -> {
            UnifiedElementAttachmentAssets.clear();
            packet.iconMap.forEach(UnifiedElementAttachmentAssets::setIcon);
            packet.particleMap.forEach(UnifiedElementAttachmentAssets::setParticleEffect);
            packet.schoolMap.forEach(UnifiedElementAttachmentAssets::setSchool);
            SpellElemental.LOGGER
               .info("已同步 {} 个元素图标, {} 个粒子效果, {} 个学派配置", new Object[]{packet.iconMap.size(), packet.particleMap.size(), packet.schoolMap.size()});
         }
      );
   }
}
