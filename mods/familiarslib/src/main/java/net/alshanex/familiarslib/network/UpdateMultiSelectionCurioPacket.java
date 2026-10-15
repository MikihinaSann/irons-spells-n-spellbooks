package net.alshanex.familiarslib.network;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.alshanex.familiarslib.util.CurioUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class UpdateMultiSelectionCurioPacket implements CustomPacketPayload {
   public static final Type<UpdateMultiSelectionCurioPacket> TYPE = new Type(
      ResourceLocation.fromNamespaceAndPath("familiarslib", "update_multi_selection_curio")
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, UpdateMultiSelectionCurioPacket> STREAM_CODEC = CustomPacketPayload.codec(
      UpdateMultiSelectionCurioPacket::write, UpdateMultiSelectionCurioPacket::new
   );
   private final Set<UUID> selectedFamiliars;

   public UpdateMultiSelectionCurioPacket(Set<UUID> selectedFamiliars) {
      this.selectedFamiliars = selectedFamiliars;
   }

   public UpdateMultiSelectionCurioPacket(FriendlyByteBuf buf) {
      int size = buf.readVarInt();
      this.selectedFamiliars = new HashSet<>();

      for (int i = 0; i < size; i++) {
         this.selectedFamiliars.add(buf.readUUID());
      }
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeVarInt(this.selectedFamiliars.size());

      for (UUID uuid : this.selectedFamiliars) {
         buf.writeUUID(uuid);
      }
   }

   public static void handle(UpdateMultiSelectionCurioPacket packet, IPayloadContext context) {
      context.enqueueWork(() -> {
         if (context.player() instanceof ServerPlayer serverPlayer) {
            CurioUtils.updateMultiSelectionCurio(serverPlayer, packet.selectedFamiliars);
         }
      });
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
