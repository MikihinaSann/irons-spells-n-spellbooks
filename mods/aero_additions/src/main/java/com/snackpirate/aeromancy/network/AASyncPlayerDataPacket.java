package com.snackpirate.aeromancy.network;

import com.snackpirate.aeromancy.Aeromancy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class AASyncPlayerDataPacket implements CustomPacketPayload {
   AeromancySpellData data;
   public static final Type<AASyncPlayerDataPacket> TYPE = new Type(Aeromancy.id("sync_magic_data"));
   public static final StreamCodec<RegistryFriendlyByteBuf, AASyncPlayerDataPacket> STREAM_CODEC = CustomPacketPayload.codec(
      AASyncPlayerDataPacket::write, AASyncPlayerDataPacket::new
   );

   public AASyncPlayerDataPacket(FriendlyByteBuf buf) {
      this.data = AeromancySpellData.read(buf);
   }

   public AASyncPlayerDataPacket(AeromancySpellData data) {
      this.data = data;
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public void write(FriendlyByteBuf buf) {
      AeromancySpellData.write(buf, this.data);
   }

   public static void handle(AASyncPlayerDataPacket packet, IPayloadContext context) {
      context.enqueueWork(() -> AAClientData.handlePlayerSyncedData(packet.data));
   }
}
