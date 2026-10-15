package net.warphan.iss_magicfromtheeast.item;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.warphan.iss_magicfromtheeast.registries.MFTEDataComponentRegistries;
import org.apache.commons.lang3.math.Fraction;

public final class LoadableWeaponContents implements TooltipComponent {
   public static final LoadableWeaponContents EMPTY = new LoadableWeaponContents(List.of());
   public static final Codec<LoadableWeaponContents> CODEC = ItemStack.CODEC.listOf().xmap(LoadableWeaponContents::new, component -> component.items);
   public static final StreamCodec<RegistryFriendlyByteBuf, LoadableWeaponContents> STREAM_CODEC = ItemStack.STREAM_CODEC
      .apply(ByteBufCodecs.list())
      .map(LoadableWeaponContents::new, function -> function.items);
   private static final Fraction PROJECTILE_IN_STORAGE_WEIGHT = Fraction.getFraction(1, 16);
   final List<ItemStack> items;
   final Fraction weight;

   LoadableWeaponContents(List<ItemStack> items, Fraction weight) {
      this.items = items;
      this.weight = weight;
   }

   public static LoadableWeaponContents of(List<ItemStack> stacks) {
      return new LoadableWeaponContents(List.copyOf(Lists.transform(stacks, ItemStack::copy)));
   }

   public LoadableWeaponContents(List<ItemStack> stacks) {
      this(stacks, computeContentWeight(stacks));
   }

   private static Fraction computeContentWeight(List<ItemStack> stacks) {
      Fraction fraction = Fraction.ZERO;

      for (ItemStack itemStack : stacks) {
         fraction = fraction.add(getWeight(itemStack).multiplyBy(Fraction.getFraction(itemStack.getCount(), 1)));
      }

      return fraction;
   }

   static Fraction getWeight(ItemStack itemStack) {
      LoadableWeaponContents contents = (LoadableWeaponContents)itemStack.get(MFTEDataComponentRegistries.LOADABLE_WEAPON_CONTENTS);
      return contents != null ? PROJECTILE_IN_STORAGE_WEIGHT.add(contents.weight) : Fraction.ONE;
   }

   public ItemStack getItemUnsafe(int p_330802_) {
      return this.items.get(p_330802_);
   }

   public Stream<ItemStack> itemCopyStream() {
      return this.items.stream().map(ItemStack::copy);
   }

   public Iterable<ItemStack> items() {
      return this.items;
   }

   public Iterable<ItemStack> itemsCopy() {
      return Lists.transform(this.items, ItemStack::copy);
   }

   public int size() {
      return this.items.size();
   }

   public Fraction weight() {
      return this.weight;
   }

   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         boolean var10000;
         if (object instanceof LoadableWeaponContents loadableWeaponContents) {
            var10000 = this.weight.equals(loadableWeaponContents.weight) && ItemStack.listMatches(this.items, loadableWeaponContents.items);
         } else {
            var10000 = false;
         }

         return var10000;
      }
   }

   @Override
   public int hashCode() {
      return ItemStack.hashStackList(this.items);
   }

   @Override
   public String toString() {
      return "LoadableWeaponContents" + this.items;
   }

   public static class Mutable {
      private final List<ItemStack> items;
      private Fraction weight;

      public Mutable(LoadableWeaponContents contents) {
         this.items = new ArrayList<>(contents.items);
         this.weight = contents.weight;
      }

      public List<ItemStack> getItems() {
         return Lists.transform(this.items, ItemStack::copy);
      }

      public int tryInsert(ItemStack itemStack) {
         if (!itemStack.isEmpty() && itemStack.getItem().canFitInsideContainerItems()) {
            int i = 1;
            this.weight = this.weight.add(LoadableWeaponContents.getWeight(itemStack).multiplyBy(Fraction.getFraction(i, 1)));
            this.items.add(0, itemStack.split(i));
            return i;
         } else {
            return 0;
         }
      }

      @Nullable
      public ItemStack removeOne() {
         if (this.items.isEmpty()) {
            return null;
         } else {
            ItemStack itemstack = this.items.remove(0).copy();
            this.weight = this.weight.subtract(LoadableWeaponContents.getWeight(itemstack).multiplyBy(Fraction.getFraction(itemstack.getCount(), 1)));
            return itemstack;
         }
      }

      public Fraction weight() {
         return this.weight;
      }

      public LoadableWeaponContents toImmutable() {
         return new LoadableWeaponContents(List.copyOf(this.items), this.weight);
      }
   }
}
