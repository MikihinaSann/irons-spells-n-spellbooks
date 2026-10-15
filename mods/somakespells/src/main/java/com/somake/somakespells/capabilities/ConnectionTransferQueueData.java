package com.somake.somakespells.capabilities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class ConnectionTransferQueueData implements INBTSerializable<CompoundTag> {
   private final List<ConnectionTransferQueueData.PendingTransfer> pendingTransfers = new ArrayList<>();

   public void enqueue(UUID sourceUuid, UUID ownerUuid, float amount, long executeAt, ConnectionKind kind) {
      if (sourceUuid != null && kind != null && !(amount <= 0.0F)) {
         this.pendingTransfers.add(new ConnectionTransferQueueData.PendingTransfer(sourceUuid, ownerUuid, amount, executeAt, kind));
      }
   }

   public List<ConnectionTransferQueueData.PendingTransfer> getPendingTransfers() {
      return this.pendingTransfers;
   }

   public boolean hasPendingTransfers() {
      return !this.pendingTransfers.isEmpty();
   }

   public void clear(ConnectionKind kind) {
      if (kind != null) {
         this.pendingTransfers.removeIf(pendingTransfer -> pendingTransfer.getKind() == kind);
      }
   }

   public void clear() {
      this.pendingTransfers.clear();
   }

   public CompoundTag serializeNBT(Provider provider) {
      CompoundTag tag = new CompoundTag();
      ListTag listTag = new ListTag();

      for (ConnectionTransferQueueData.PendingTransfer pendingTransfer : this.pendingTransfers) {
         listTag.add(pendingTransfer.serialize());
      }

      tag.put("entries", listTag);
      return tag;
   }

   public void deserializeNBT(Provider provider, CompoundTag tag) {
      this.pendingTransfers.clear();
      ListTag listTag = tag.getList("entries", 10);

      for (int i = 0; i < listTag.size(); i++) {
         this.pendingTransfers.add(ConnectionTransferQueueData.PendingTransfer.deserialize(listTag.getCompound(i)));
      }
   }

   public static class PendingTransfer {
      private final UUID sourceUuid;
      private final UUID ownerUuid;
      private final float amount;
      private final long executeAt;
      private final ConnectionKind kind;

      public PendingTransfer(UUID sourceUuid, UUID ownerUuid, float amount, long executeAt, ConnectionKind kind) {
         this.sourceUuid = sourceUuid;
         this.ownerUuid = ownerUuid;
         this.amount = amount;
         this.executeAt = executeAt;
         this.kind = kind;
      }

      public UUID getSourceUuid() {
         return this.sourceUuid;
      }

      public UUID getOwnerUuid() {
         return this.ownerUuid;
      }

      public float getAmount() {
         return this.amount;
      }

      public long getExecuteAt() {
         return this.executeAt;
      }

      public ConnectionKind getKind() {
         return this.kind;
      }

      public boolean isReady(long gameTime) {
         return this.executeAt <= gameTime;
      }

      private CompoundTag serialize() {
         CompoundTag tag = new CompoundTag();
         tag.putUUID("source", this.sourceUuid);
         if (this.ownerUuid != null) {
            tag.putUUID("owner", this.ownerUuid);
         }

         tag.putFloat("amount", this.amount);
         tag.putLong("executeAt", this.executeAt);
         tag.putString("kind", this.kind.name());
         return tag;
      }

      private static ConnectionTransferQueueData.PendingTransfer deserialize(CompoundTag tag) {
         UUID sourceUuid = tag.getUUID("source");
         UUID ownerUuid = tag.hasUUID("owner") ? tag.getUUID("owner") : null;
         float amount = tag.getFloat("amount");
         long executeAt = tag.getLong("executeAt");
         ConnectionKind kind = ConnectionKind.valueOf(tag.getString("kind"));
         return new ConnectionTransferQueueData.PendingTransfer(sourceUuid, ownerUuid, amount, executeAt, kind);
      }
   }
}
