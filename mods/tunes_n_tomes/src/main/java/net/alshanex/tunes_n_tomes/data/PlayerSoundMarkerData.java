package net.alshanex.tunes_n_tomes.data;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class PlayerSoundMarkerData implements INBTSerializable<CompoundTag> {
   private final LinkedList<UUID> markerQueue = new LinkedList<>();

   public UUID addMarker(UUID markerId, int maxMarkers) {
      UUID removed = null;
      if (this.markerQueue.size() >= maxMarkers) {
         removed = this.markerQueue.removeFirst();
      }

      this.markerQueue.addLast(markerId);
      return removed;
   }

   public boolean removeMarker(UUID markerId) {
      return this.markerQueue.remove(markerId);
   }

   public List<UUID> getMarkerIds() {
      return Collections.unmodifiableList(this.markerQueue);
   }

   public int getMarkerCount() {
      return this.markerQueue.size();
   }

   public void clear() {
      this.markerQueue.clear();
   }

   public CompoundTag serializeNBT(Provider provider) {
      CompoundTag tag = new CompoundTag();
      ListTag list = new ListTag();

      for (UUID id : this.markerQueue) {
         CompoundTag entry = new CompoundTag();
         entry.putUUID("id", id);
         list.add(entry);
      }

      tag.put("markers", list);
      return tag;
   }

   public void deserializeNBT(Provider provider, CompoundTag tag) {
      this.markerQueue.clear();
      if (tag.contains("markers", 9)) {
         ListTag list = tag.getList("markers", 10);

         for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            if (entry.hasUUID("id")) {
               this.markerQueue.addLast(entry.getUUID("id"));
            }
         }
      }
   }
}
