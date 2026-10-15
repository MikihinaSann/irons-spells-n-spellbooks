package net.alshanex.familiarslib.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class PlayerFamiliarData implements INBTSerializable<CompoundTag> {
   public static final int MAX_FAMILIAR_LIMIT = 10;
   private final Map<UUID, CompoundTag> tamedFamiliars = new HashMap<>();
   private UUID selectedFamiliarId = null;
   private UUID currentSummonedFamiliarId = null;
   private final Set<UUID> summonedFamiliarIds = new HashSet<>();

   public void addTamedFamiliar(UUID familiarId, CompoundTag familiarData) {
      this.tamedFamiliars.put(familiarId, familiarData);
   }

   public boolean canTameMoreFamiliars() {
      return this.tamedFamiliars.size() < 10;
   }

   public boolean tryAddTamedFamiliar(UUID familiarId, CompoundTag familiarData) {
      if (!this.canTameMoreFamiliars()) {
         return false;
      } else {
         this.addTamedFamiliar(familiarId, familiarData);
         return true;
      }
   }

   public int getRemainingFamiliarSlots() {
      return 10 - this.tamedFamiliars.size();
   }

   public boolean isAtMaxCapacity() {
      return this.tamedFamiliars.size() >= 10;
   }

   public void removeTamedFamiliar(UUID familiarId) {
      this.tamedFamiliars.remove(familiarId);
      if (Objects.equals(this.selectedFamiliarId, familiarId)) {
         this.selectedFamiliarId = null;
      }

      if (Objects.equals(this.currentSummonedFamiliarId, familiarId)) {
         this.currentSummonedFamiliarId = null;
      }

      this.summonedFamiliarIds.remove(familiarId);
   }

   public CompoundTag getFamiliarData(UUID familiarId) {
      return this.tamedFamiliars.get(familiarId);
   }

   public Map<UUID, CompoundTag> getAllFamiliars() {
      return new HashMap<>(this.tamedFamiliars);
   }

   public boolean hasFamiliar(UUID familiarId) {
      return this.tamedFamiliars.containsKey(familiarId);
   }

   public UUID getSelectedFamiliarId() {
      return this.selectedFamiliarId;
   }

   public void setSelectedFamiliarId(UUID familiarId) {
      this.selectedFamiliarId = familiarId;
   }

   public UUID getCurrentSummonedFamiliarId() {
      return this.currentSummonedFamiliarId;
   }

   public void setCurrentSummonedFamiliarId(UUID familiarId) {
      this.currentSummonedFamiliarId = familiarId;
   }

   public void addSummonedFamiliar(UUID familiarId) {
      this.summonedFamiliarIds.add(familiarId);
   }

   public void removeSummonedFamiliar(UUID familiarId) {
      this.summonedFamiliarIds.remove(familiarId);
      if (Objects.equals(this.currentSummonedFamiliarId, familiarId)) {
         this.currentSummonedFamiliarId = null;
      }
   }

   public boolean isFamiliarSummoned(UUID familiarId) {
      return this.summonedFamiliarIds.contains(familiarId);
   }

   public Set<UUID> getSummonedFamiliarIds() {
      return new HashSet<>(this.summonedFamiliarIds);
   }

   public int getSummonedFamiliarCount() {
      return this.summonedFamiliarIds.size();
   }

   public void clearAllSummoned() {
      this.summonedFamiliarIds.clear();
      this.currentSummonedFamiliarId = null;
   }

   public boolean isEmpty() {
      return this.tamedFamiliars.isEmpty();
   }

   public int getFamiliarCount() {
      return this.tamedFamiliars.size();
   }

   public CompoundTag serializeNBT(Provider provider) {
      CompoundTag nbt = new CompoundTag();
      ListTag familiarsList = new ListTag();

      for (Entry<UUID, CompoundTag> entry : this.tamedFamiliars.entrySet()) {
         CompoundTag familiarEntry = new CompoundTag();
         familiarEntry.putUUID("id", entry.getKey());
         familiarEntry.put("data", (Tag)entry.getValue());
         familiarsList.add(familiarEntry);
      }

      nbt.put("familiars", familiarsList);
      if (this.selectedFamiliarId != null) {
         nbt.putUUID("selectedFamiliar", this.selectedFamiliarId);
      }

      if (this.currentSummonedFamiliarId != null) {
         nbt.putUUID("currentSummoned", this.currentSummonedFamiliarId);
      }

      if (!this.summonedFamiliarIds.isEmpty()) {
         ListTag summonedList = new ListTag();

         for (UUID summonedId : this.summonedFamiliarIds) {
            CompoundTag summonedEntry = new CompoundTag();
            summonedEntry.putUUID("summonedId", summonedId);
            summonedList.add(summonedEntry);
         }

         nbt.put("summonedFamiliars", summonedList);
      }

      nbt.putInt("familiarCount", this.tamedFamiliars.size());
      nbt.putLong("saveTime", System.currentTimeMillis());
      return nbt;
   }

   public void deserializeNBT(Provider provider, CompoundTag nbt) {
      this.tamedFamiliars.clear();
      this.selectedFamiliarId = null;
      this.currentSummonedFamiliarId = null;
      this.summonedFamiliarIds.clear();
      if (nbt.contains("familiars", 9)) {
         ListTag familiarsList = nbt.getList("familiars", 10);

         for (int i = 0; i < familiarsList.size(); i++) {
            CompoundTag familiarEntry = familiarsList.getCompound(i);
            if (familiarEntry.hasUUID("id")) {
               UUID id = familiarEntry.getUUID("id");
               CompoundTag data = familiarEntry.getCompound("data");
               this.tamedFamiliars.put(id, data);
            }
         }
      }

      if (nbt.hasUUID("selectedFamiliar")) {
         this.selectedFamiliarId = nbt.getUUID("selectedFamiliar");
      }

      if (nbt.hasUUID("currentSummoned")) {
         this.currentSummonedFamiliarId = nbt.getUUID("currentSummoned");
      }

      if (nbt.contains("summonedFamiliars", 9)) {
         ListTag summonedList = nbt.getList("summonedFamiliars", 10);

         for (int ix = 0; ix < summonedList.size(); ix++) {
            CompoundTag summonedEntry = summonedList.getCompound(ix);
            if (summonedEntry.hasUUID("summonedId")) {
               UUID summonedId = summonedEntry.getUUID("summonedId");
               this.summonedFamiliarIds.add(summonedId);
            }
         }
      }
   }
}
