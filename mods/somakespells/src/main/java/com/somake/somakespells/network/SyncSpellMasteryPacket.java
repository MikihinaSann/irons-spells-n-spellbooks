package com.somake.somakespells.network;

import com.somake.somakespells.capabilities.PlayerSpellMastery;
import com.somake.somakespells.registries.ModAttachments;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncSpellMasteryPacket(Map<String, Integer> unlockedSpells) implements CustomPacketPayload {
   public static final Type<SyncSpellMasteryPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("somakespells", "sync_spell_mastery"));
   public static final StreamCodec<RegistryFriendlyByteBuf, SyncSpellMasteryPacket> STREAM_CODEC = CustomPacketPayload.codec(
      SyncSpellMasteryPacket::write, SyncSpellMasteryPacket::new
   );

   public SyncSpellMasteryPacket(RegistryFriendlyByteBuf buffer) {
      this(readMap(buffer));
   }

   private static Map<String, Integer> readMap(RegistryFriendlyByteBuf buffer) {
      Map<String, Integer> map = new HashMap<>();
      int size = buffer.readInt();

      for (int i = 0; i < size; i++) {
         map.put(buffer.readUtf(), buffer.readInt());
      }

      return map;
   }

   public void write(RegistryFriendlyByteBuf buffer) {
      buffer.writeInt(this.unlockedSpells.size());
      this.unlockedSpells.forEach((id, level) -> {
         buffer.writeUtf(id);
         buffer.writeInt(level);
      });
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handle(SyncSpellMasteryPacket packet, IPayloadContext context) {
      context.enqueueWork(() -> {
         Player player = context.player();
         if (player != null) {
            PlayerSpellMastery mastery = (PlayerSpellMastery)player.getData(ModAttachments.PLAYER_SPELL_MASTERY);
            mastery.setAllUnlockedSpells(packet.unlockedSpells());
         }
      });
   }
}
