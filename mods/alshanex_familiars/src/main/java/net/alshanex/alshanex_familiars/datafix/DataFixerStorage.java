package net.alshanex.alshanex_familiars.datafix;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

public class DataFixerStorage extends SavedData {
   public static DataFixerStorage INSTANCE;
   private DimensionDataStorage overworldDataStorage;
   private int dataVersion;
   private static final String DATA_NAME = "familiars_data_fixer";

   public static void init(DimensionDataStorage dataStorage) {
      INSTANCE = (DataFixerStorage)dataStorage.computeIfAbsent(new Factory<>(DataFixerStorage::new, DataFixerStorage::load, null), "familiars_data_fixer");
      INSTANCE.overworldDataStorage = dataStorage;
   }

   public DataFixerStorage() {
      this.dataVersion = 0;
   }

   public DataFixerStorage(int dataVersion) {
      this.dataVersion = dataVersion;
   }

   public int getDataVersion() {
      return this.dataVersion;
   }

   public void setDataVersion(int dataVersion) {
      this.dataVersion = dataVersion;
      this.setDirty();
      if (this.overworldDataStorage != null) {
         this.overworldDataStorage.save();
      }
   }

   public boolean needsUpgrade() {
      return this.dataVersion < 1;
   }

   @NotNull
   public CompoundTag save(@NotNull CompoundTag pCompoundTag, @NotNull Provider pRegistries) {
      pCompoundTag.putInt("dataVersion", this.dataVersion);
      return pCompoundTag;
   }

   public static DataFixerStorage load(CompoundTag tag, Provider provider) {
      int dataVersion = tag.getInt("dataVersion");
      return new DataFixerStorage(dataVersion);
   }
}
