package net.alshanex.familiarslib.network;

import net.alshanex.familiarslib.util.familiars.FamiliarManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class UpdateStorageSettingsPacket implements CustomPacketPayload {
   public static final Type<UpdateStorageSettingsPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("familiarslib", "update_storage_settings"));
   public static final StreamCodec<RegistryFriendlyByteBuf, UpdateStorageSettingsPacket> STREAM_CODEC = CustomPacketPayload.codec(
      UpdateStorageSettingsPacket::write, UpdateStorageSettingsPacket::new
   );
   private final BlockPos blockPos;
   private final boolean canFamiliarsUseGoals;
   private final int maxDistance;

   public UpdateStorageSettingsPacket(BlockPos blockPos, boolean canFamiliarsUseGoals, int maxDistance) {
      this.blockPos = blockPos;
      this.canFamiliarsUseGoals = canFamiliarsUseGoals;
      this.maxDistance = maxDistance;
   }

   public UpdateStorageSettingsPacket(FriendlyByteBuf buf) {
      this.blockPos = buf.readBlockPos();
      this.canFamiliarsUseGoals = buf.readBoolean();
      this.maxDistance = buf.readVarInt();
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeBlockPos(this.blockPos);
      buf.writeBoolean(this.canFamiliarsUseGoals);
      buf.writeVarInt(this.maxDistance);
   }

   public static void handle(UpdateStorageSettingsPacket packet, IPayloadContext context) {
      context.enqueueWork(() -> {
         if (context.player() instanceof ServerPlayer serverPlayer) {
            FamiliarManager.handleUpdateStorageSettings(serverPlayer, packet.blockPos, packet.canFamiliarsUseGoals, packet.maxDistance);
         }
      });
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
