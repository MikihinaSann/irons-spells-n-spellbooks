package net.alshanex.alshanex_familiars.compat;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;

public class ItemMultiSet {
   private final Map<Item, Integer> itemCounts = new HashMap<>();
   public static final Codec<ItemMultiSet> CODEC = Codec.unboundedMap(BuiltInRegistries.ITEM.byNameCodec(), Codec.INT)
      .xmap(ItemMultiSet::fromMap, ItemMultiSet::getItemCounts);
   public static final StreamCodec<RegistryFriendlyByteBuf, ItemMultiSet> STREAM_CODEC = ByteBufCodecs.map(
         HashMap::new, ByteBufCodecs.registry(Registries.ITEM), ByteBufCodecs.VAR_INT
      )
      .map(ItemMultiSet::fromMap, ItemMultiSet::getItemCounts);

   public ItemMultiSet() {
   }

   public ItemMultiSet(Iterable<Item> items) {
      for (Item item : items) {
         this.add(item);
      }
   }

   public void add(Item item) {
      this.itemCounts.put(item, this.itemCounts.getOrDefault(item, 0) + 1);
   }

   public int getCount(Item item) {
      return this.itemCounts.getOrDefault(item, 0);
   }

   public boolean containsAll(ItemMultiSet other) {
      for (Entry<Item, Integer> entry : other.itemCounts.entrySet()) {
         if (this.getCount(entry.getKey()) < entry.getValue()) {
            return false;
         }
      }

      return true;
   }

   public Iterable<Item> getUniqueItems() {
      return this.itemCounts.keySet();
   }

   public int size() {
      return this.itemCounts.values().stream().mapToInt(Integer::intValue).sum();
   }

   public boolean isEmpty() {
      return this.itemCounts.isEmpty();
   }

   public HashMap<Item, Integer> getItemCounts() {
      return new HashMap<>(this.itemCounts);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ItemMultiSet that = (ItemMultiSet)o;
         if (this.itemCounts.size() != that.itemCounts.size()) {
            return false;
         } else {
            for (Entry<Item, Integer> entry : this.itemCounts.entrySet()) {
               Item item = entry.getKey();
               Integer count = entry.getValue();
               if (!count.equals(that.itemCounts.getOrDefault(item, 0))) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int hash = 0;

      for (Entry<Item, Integer> entry : this.itemCounts.entrySet()) {
         hash += Objects.hash(entry.getKey()) * entry.getValue();
      }

      return hash;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder("ItemMultiSet{");

      for (Entry<Item, Integer> entry : this.itemCounts.entrySet()) {
         sb.append(entry.getKey()).append("×").append(entry.getValue()).append(", ");
      }

      if (!this.itemCounts.isEmpty()) {
         sb.setLength(sb.length() - 2);
      }

      sb.append("}");
      return sb.toString();
   }

   private static ItemMultiSet fromMap(Map<Item, Integer> map) {
      ItemMultiSet multiSet = new ItemMultiSet();

      for (Entry<Item, Integer> entry : map.entrySet()) {
         for (int i = 0; i < entry.getValue(); i++) {
            multiSet.add(entry.getKey());
         }
      }

      return multiSet;
   }
}
