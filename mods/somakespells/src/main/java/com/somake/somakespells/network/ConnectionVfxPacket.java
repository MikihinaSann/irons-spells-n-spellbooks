package com.somake.somakespells.network;

import com.somake.somakespells.capabilities.ConnectionKind;
import com.somake.somakespells.compat.mowzie.MowzieConnectionVfx;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ConnectionVfxPacket(
   double startX, double startY, double startZ, double endX, double endY, double endZ, ConnectionKind kind, boolean transferPulse, int travelTicks
) implements CustomPacketPayload {
   public static final Type<ConnectionVfxPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("somakespells", "connection_vfx"));
   public static final StreamCodec<RegistryFriendlyByteBuf, ConnectionVfxPacket> STREAM_CODEC = CustomPacketPayload.codec(
      ConnectionVfxPacket::write, ConnectionVfxPacket::new
   );

   public ConnectionVfxPacket(RegistryFriendlyByteBuf buffer) {
      this(
         buffer.readDouble(),
         buffer.readDouble(),
         buffer.readDouble(),
         buffer.readDouble(),
         buffer.readDouble(),
         buffer.readDouble(),
         ConnectionKind.valueOf(buffer.readUtf()),
         buffer.readBoolean(),
         buffer.readInt()
      );
   }

   private void write(RegistryFriendlyByteBuf buffer) {
      buffer.writeDouble(this.startX);
      buffer.writeDouble(this.startY);
      buffer.writeDouble(this.startZ);
      buffer.writeDouble(this.endX);
      buffer.writeDouble(this.endY);
      buffer.writeDouble(this.endZ);
      buffer.writeUtf(this.kind.name());
      buffer.writeBoolean(this.transferPulse);
      buffer.writeInt(this.travelTicks);
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handle(ConnectionVfxPacket packet, IPayloadContext context) {
      context.enqueueWork(
         () -> {
            if (ModList.get().isLoaded("mowziesmobs") && !packet.kind().isRestraining()) {
               Player player = context.player();
               if (player != null && player.level() != null) {
                  if (packet.transferPulse()) {
                     MowzieConnectionVfx.spawnTransferPulse(
                        player.level(),
                        new Vec3(packet.startX, packet.startY, packet.startZ),
                        new Vec3(packet.endX, packet.endY, packet.endZ),
                        packet.kind(),
                        packet.travelTicks()
                     );
                  } else {
                     MowzieConnectionVfx.spawnBeamOnly(
                        player.level(), new Vec3(packet.startX, packet.startY, packet.startZ), new Vec3(packet.endX, packet.endY, packet.endZ), packet.kind()
                     );
                  }
               }
            }
         }
      );
   }
}
