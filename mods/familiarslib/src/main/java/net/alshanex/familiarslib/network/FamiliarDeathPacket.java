package net.alshanex.familiarslib.network;

import java.util.HashMap;
import java.util.Map;
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

public class FamiliarDeathPacket implements CustomPacketPayload {
   public static final Type<FamiliarDeathPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("familiarslib", "familiar_death"));
   public static final StreamCodec<RegistryFriendlyByteBuf, FamiliarDeathPacket> STREAM_CODEC = CustomPacketPayload.codec(
      FamiliarDeathPacket::write, FamiliarDeathPacket::new
   );
   private final UUID deadFamiliarId;
   private final Map<UUID, CompoundTag> remainingFamiliars;
   private final UUID newSelectedFamiliarId;
   private final UUID currentSummonedFamiliarId;
   private final CompoundTag familiarData;

   public FamiliarDeathPacket(
      UUID deadFamiliarId, Map<UUID, CompoundTag> remainingFamiliars, UUID newSelectedFamiliarId, UUID currentSummonedFamiliarId, CompoundTag familiarData
   ) {
      this.deadFamiliarId = deadFamiliarId;
      this.remainingFamiliars = remainingFamiliars;
      this.newSelectedFamiliarId = newSelectedFamiliarId;
      this.currentSummonedFamiliarId = currentSummonedFamiliarId;
      this.familiarData = familiarData;
   }

   public FamiliarDeathPacket(FriendlyByteBuf buf) {
      this.deadFamiliarId = buf.readUUID();
      int size = buf.readVarInt();
      this.remainingFamiliars = new HashMap<>();

      for (int i = 0; i < size; i++) {
         UUID id = buf.readUUID();
         CompoundTag nbt = buf.readNbt();
         this.remainingFamiliars.put(id, nbt);
      }

      this.newSelectedFamiliarId = buf.readBoolean() ? buf.readUUID() : null;
      this.currentSummonedFamiliarId = buf.readBoolean() ? buf.readUUID() : null;
      this.familiarData = buf.readNbt();
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeUUID(this.deadFamiliarId);
      buf.writeVarInt(this.remainingFamiliars.size());

      for (Entry<UUID, CompoundTag> entry : this.remainingFamiliars.entrySet()) {
         buf.writeUUID(entry.getKey());
         buf.writeNbt((Tag)entry.getValue());
      }

      buf.writeBoolean(this.newSelectedFamiliarId != null);
      if (this.newSelectedFamiliarId != null) {
         buf.writeUUID(this.newSelectedFamiliarId);
      }

      buf.writeBoolean(this.currentSummonedFamiliarId != null);
      if (this.currentSummonedFamiliarId != null) {
         buf.writeUUID(this.currentSummonedFamiliarId);
      }

      buf.writeNbt(this.familiarData);
   }

   public static void handle(FamiliarDeathPacket packet, IPayloadContext context) {
      context.enqueueWork(
         () -> FamiliarManager.handleFamiliarDeathPacket(
            packet.deadFamiliarId, packet.remainingFamiliars, packet.newSelectedFamiliarId, packet.currentSummonedFamiliarId, packet.familiarData
         )
      );
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
