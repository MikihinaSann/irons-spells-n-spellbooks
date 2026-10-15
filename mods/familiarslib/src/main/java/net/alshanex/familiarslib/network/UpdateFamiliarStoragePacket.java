package net.alshanex.familiarslib.network;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.alshanex.familiarslib.util.familiars.FamiliarManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class UpdateFamiliarStoragePacket implements CustomPacketPayload {
   public static final Type<UpdateFamiliarStoragePacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("familiarslib", "update_familiar_storage"));
   public static final StreamCodec<RegistryFriendlyByteBuf, UpdateFamiliarStoragePacket> STREAM_CODEC = CustomPacketPayload.codec(
      UpdateFamiliarStoragePacket::write, UpdateFamiliarStoragePacket::new
   );
   private final BlockPos blockPos;
   private final Map<UUID, CompoundTag> storedFamiliars;
   private final boolean storeMode;
   private final boolean canFamiliarsUseGoals;
   private final int maxDistance;

   public UpdateFamiliarStoragePacket(
      BlockPos blockPos, Map<UUID, CompoundTag> storedFamiliars, boolean storeMode, boolean canFamiliarsUseGoals, int maxDistance
   ) {
      this.blockPos = blockPos;
      this.storedFamiliars = storedFamiliars;
      this.storeMode = storeMode;
      this.canFamiliarsUseGoals = canFamiliarsUseGoals;
      this.maxDistance = maxDistance;
   }

   public UpdateFamiliarStoragePacket(BlockPos blockPos, Map<UUID, CompoundTag> storedFamiliars, boolean storeMode) {
      this(blockPos, storedFamiliars, storeMode, true, 25);
   }

   public UpdateFamiliarStoragePacket(FriendlyByteBuf buf) {
      this.blockPos = buf.readBlockPos();
      int size = buf.readVarInt();
      this.storedFamiliars = new HashMap<>();

      for (int i = 0; i < size; i++) {
         UUID id = buf.readUUID();
         CompoundTag nbt = buf.readNbt();
         this.storedFamiliars.put(id, nbt);
      }

      this.storeMode = buf.readBoolean();
      this.canFamiliarsUseGoals = buf.readBoolean();
      this.maxDistance = buf.readVarInt();
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeBlockPos(this.blockPos);
      buf.writeVarInt(this.storedFamiliars.size());

      for (Entry<UUID, CompoundTag> entry : this.storedFamiliars.entrySet()) {
         buf.writeUUID(entry.getKey());
         buf.writeNbt((Tag)entry.getValue());
      }

      buf.writeBoolean(this.storeMode);
      buf.writeBoolean(this.canFamiliarsUseGoals);
      buf.writeVarInt(this.maxDistance);
   }

   public static void handle(UpdateFamiliarStoragePacket packet, IPayloadContext context) {
      context.enqueueWork(
         () -> FamiliarManager.handleStorageUpdate(packet.blockPos, packet.storedFamiliars, packet.storeMode, packet.canFamiliarsUseGoals, packet.maxDistance)
      );
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
