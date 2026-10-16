package com.userofbricks.expanded_combat.world;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import org.jetbrains.annotations.NotNull;

public class ECWorldSaveData extends SavedData {
   public static final String DATA_NAME = "expanded_combat_world_save_data";
   public static final String heartstealerCountNBTName = "heartStealerCount";
   protected int heartstealerCount = 0;

   public static ECWorldSaveData load(CompoundTag tag, @NotNull Provider pRegistries) {
      int heartstealerCount = tag.getInt("heartStealerCount");
      ECWorldSaveData data = new ECWorldSaveData();
      data.heartstealerCount = heartstealerCount;
      return data;
   }

   @NotNull
   public CompoundTag save(@NotNull CompoundTag pTag, @NotNull Provider pRegistries) {
      pTag.putInt("heartStealerCount", this.heartstealerCount);
      return pTag;
   }

   public static ECWorldSaveData get(ServerLevel world) {
      return (ECWorldSaveData)world.getDataStorage()
         .computeIfAbsent(new Factory<>(ECWorldSaveData::new, ECWorldSaveData::load, null), "expanded_combat_world_save_data");
   }

   public static double getHeartStealerCount(ServerLevel world) {
      return get(world).heartstealerCount;
   }

   public static void increaseHeartStealerCount(ServerLevel world) {
      get(world).heartstealerCount++;
   }
}
