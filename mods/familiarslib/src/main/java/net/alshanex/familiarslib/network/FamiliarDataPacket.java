package net.alshanex.familiarslib.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.alshanex.familiarslib.util.familiars.FamiliarManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class FamiliarDataPacket implements CustomPacketPayload {
   public static final Type<FamiliarDataPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("familiarslib", "familiar_data"));
   public static final StreamCodec<RegistryFriendlyByteBuf, FamiliarDataPacket> STREAM_CODEC = CustomPacketPayload.codec(
      FamiliarDataPacket::write, FamiliarDataPacket::new
   );
   private final Map<UUID, CompoundTag> familiars;
   private final UUID selectedFamiliarId;
   private final UUID currentSummonedFamiliarId;
   private final Set<UUID> summonedFamiliarIds;

   public FamiliarDataPacket(Map<UUID, CompoundTag> familiars, UUID selectedFamiliarId, UUID currentSummonedFamiliarId, Set<UUID> summonedFamiliarIds) {
      this.familiars = familiars;
      this.selectedFamiliarId = selectedFamiliarId;
      this.currentSummonedFamiliarId = currentSummonedFamiliarId;
      this.summonedFamiliarIds = (Set<UUID>)(summonedFamiliarIds != null ? summonedFamiliarIds : new HashSet<>());
   }

   public FamiliarDataPacket(Map<UUID, CompoundTag> familiars, UUID selectedFamiliarId, UUID currentSummonedFamiliarId) {
      this(familiars, selectedFamiliarId, currentSummonedFamiliarId, new HashSet<>());
   }

   public FamiliarDataPacket(FriendlyByteBuf buf) {
      int size = buf.readVarInt();
      this.familiars = new HashMap<>();

      for (int i = 0; i < size; i++) {
         UUID id = buf.readUUID();
         CompoundTag nbt = buf.readNbt();
         this.familiars.put(id, nbt);
      }

      this.selectedFamiliarId = buf.readBoolean() ? buf.readUUID() : null;
      this.currentSummonedFamiliarId = buf.readBoolean() ? buf.readUUID() : null;
      int summonedSize = buf.readVarInt();
      this.summonedFamiliarIds = new HashSet<>();

      for (int i = 0; i < summonedSize; i++) {
         this.summonedFamiliarIds.add(buf.readUUID());
      }
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeVarInt(this.familiars.size());

      for (Entry<UUID, CompoundTag> entry : this.familiars.entrySet()) {
         buf.writeUUID(entry.getKey());
         buf.writeNbt((Tag)entry.getValue());
      }

      buf.writeBoolean(this.selectedFamiliarId != null);
      if (this.selectedFamiliarId != null) {
         buf.writeUUID(this.selectedFamiliarId);
      }

      buf.writeBoolean(this.currentSummonedFamiliarId != null);
      if (this.currentSummonedFamiliarId != null) {
         buf.writeUUID(this.currentSummonedFamiliarId);
      }

      buf.writeVarInt(this.summonedFamiliarIds.size());

      for (UUID summonedId : this.summonedFamiliarIds) {
         buf.writeUUID(summonedId);
      }
   }

   public static void handle(FamiliarDataPacket packet, IPayloadContext context) {
      context.enqueueWork(
         () -> FamiliarManager.handleFamiliarDataPacket(
            packet.familiars, packet.selectedFamiliarId, packet.currentSummonedFamiliarId, packet.summonedFamiliarIds
         )
      );
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
