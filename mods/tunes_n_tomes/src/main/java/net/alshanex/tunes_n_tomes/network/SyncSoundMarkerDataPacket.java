package net.alshanex.tunes_n_tomes.network;

import net.alshanex.tunes_n_tomes.data.ClientSoundMarkerData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SyncSoundMarkerDataPacket implements CustomPacketPayload {
   public static final Type<SyncSoundMarkerDataPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "sync_sound_marker_data"));
   public static final StreamCodec<RegistryFriendlyByteBuf, SyncSoundMarkerDataPacket> STREAM_CODEC = CustomPacketPayload.codec(
      SyncSoundMarkerDataPacket::write, SyncSoundMarkerDataPacket::new
   );
   private final int currentMarkers;
   private final int maxMarkers;

   public SyncSoundMarkerDataPacket(int currentMarkers, int maxMarkers) {
      this.currentMarkers = currentMarkers;
      this.maxMarkers = maxMarkers;
   }

   public SyncSoundMarkerDataPacket(FriendlyByteBuf buf) {
      this.currentMarkers = buf.readVarInt();
      this.maxMarkers = buf.readVarInt();
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeVarInt(this.currentMarkers);
      buf.writeVarInt(this.maxMarkers);
   }

   public static void handle(SyncSoundMarkerDataPacket packet, IPayloadContext context) {
      context.enqueueWork(() -> ClientSoundMarkerData.setMarkerInfo(packet.currentMarkers, packet.maxMarkers));
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
