package net.alshanex.familiarslib.network;

import java.util.UUID;
import net.alshanex.familiarslib.util.familiars.FamiliarManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ReleaseFamiliarPacket implements CustomPacketPayload {
   public static final Type<ReleaseFamiliarPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("familiarslib", "release_familiar"));
   public static final StreamCodec<RegistryFriendlyByteBuf, ReleaseFamiliarPacket> STREAM_CODEC = CustomPacketPayload.codec(
      ReleaseFamiliarPacket::write, ReleaseFamiliarPacket::new
   );
   private final UUID familiarId;

   public ReleaseFamiliarPacket(UUID familiarId) {
      this.familiarId = familiarId;
   }

   public ReleaseFamiliarPacket(FriendlyByteBuf buf) {
      this.familiarId = buf.readUUID();
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeUUID(this.familiarId);
   }

   public static void handle(ReleaseFamiliarPacket packet, IPayloadContext context) {
      context.enqueueWork(() -> {
         if (context.player() instanceof ServerPlayer serverPlayer) {
            FamiliarManager.handleReleaseFamiliar(serverPlayer, packet.familiarId);
         }
      });
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
