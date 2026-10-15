package com.somake.somakespells.capabilities;

import com.somake.somakespells.config.Config;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class PlayerSpellMastery implements INBTSerializable<CompoundTag> {
   private final Map<String, Integer> unlockedSpells = new HashMap<>();

   public int getUnlockedLevel(String spellId) {
      return !Config.ENABLE_SPELL_LOCK_SYSTEM.get() ? 100 : this.unlockedSpells.getOrDefault(spellId, 0);
   }

   public void setUnlockedLevel(String spellId, int level) {
      if (level <= 0) {
         this.unlockedSpells.remove(spellId);
      } else {
         this.unlockedSpells.put(spellId, level);
      }
   }

   public void clear() {
      this.unlockedSpells.clear();
   }

   public Map<String, Integer> getAllUnlockedSpells() {
      return new HashMap<>(this.unlockedSpells);
   }

   public void setAllUnlockedSpells(Map<String, Integer> spells) {
      this.unlockedSpells.clear();
      this.unlockedSpells.putAll(spells);
   }

   public CompoundTag serializeNBT(Provider provider) {
      CompoundTag tag = new CompoundTag();
      ListTag list = new ListTag();
      this.unlockedSpells.forEach((spellId, level) -> {
         CompoundTag entry = new CompoundTag();
         entry.putString("id", spellId);
         entry.putInt("lvl", level);
         list.add(entry);
      });
      tag.put("spells", list);
      return tag;
   }

   public void deserializeNBT(Provider provider, CompoundTag tag) {
      this.unlockedSpells.clear();
      if (tag.contains("spells", 9)) {
         for (Tag t : tag.getList("spells", 10)) {
            CompoundTag entry = (CompoundTag)t;
            this.unlockedSpells.put(entry.getString("id"), entry.getInt("lvl"));
         }
      }
   }
}
