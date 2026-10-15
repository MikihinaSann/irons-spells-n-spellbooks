package com.somake.somakespells.item;

import com.somake.somakespells.registries.ModItems;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public final class RedSoulLanternProgressHelper {
   public static final int REQUIRED_SPELLS = 3;
   private static final String ROOT_KEY = "somake_red_soul_lantern";
   private static final String SPELLS_KEY = "spells";

   private RedSoulLanternProgressHelper() {
   }

   public static boolean recordSpiritSpell(ServerPlayer player, ResourceLocation spellId) {
      Optional<RedSoulLanternProgressHelper.LanternSlot> selected = findChargeableLantern(player, spellId);
      if (selected.isEmpty()) {
         return false;
      } else {
         RedSoulLanternProgressHelper.LanternSlot lantern = selected.get();
         List<String> progress = getStoredSpells(lantern.stack());
         progress.add(spellId.toString());
         setStoredSpells(lantern.stack(), progress);
         syncInventory(player);
         int count = progress.size();
         String message = count >= 3 ? "message.somakespells.red_soul_lantern.ready" : "message.somakespells.red_soul_lantern.absorbed";
         player.displayClientMessage(
            Component.translatable(message, new Object[]{count, 3}).withStyle(count >= 3 ? ChatFormatting.DARK_RED : ChatFormatting.AQUA), true
         );
         return true;
      }
   }

   public static boolean hasChargedLantern(ServerPlayer player) {
      return findChargedLantern(player).isPresent();
   }

   public static Optional<RedSoulLanternProgressHelper.LanternSlot> findChargedLantern(ServerPlayer player) {
      RedSoulLanternProgressHelper.LanternSlot selected = null;

      for (int slot = 0; slot < 36; slot++) {
         ItemStack stack = player.getInventory().getItem(slot);
         if (isCharged(stack)) {
            selected = choose(selected, new RedSoulLanternProgressHelper.LanternSlot(slot, stack, priority(player, slot)));
         }
      }

      ItemStack offhand = player.getInventory().getItem(40);
      if (isCharged(offhand)) {
         selected = choose(selected, new RedSoulLanternProgressHelper.LanternSlot(40, offhand, 1));
      }

      return Optional.ofNullable(selected);
   }

   public static boolean consumeCharge(ServerPlayer player) {
      Optional<RedSoulLanternProgressHelper.LanternSlot> selected = findChargedLantern(player);
      if (selected.isEmpty()) {
         return false;
      } else {
         ItemStack lantern = selected.get().stack();
         clearStoredSpells(lantern);
         int nextDamage = lantern.getDamageValue() + 1;
         if (nextDamage >= lantern.getMaxDamage()) {
            lantern.shrink(1);
         } else {
            lantern.setDamageValue(nextDamage);
         }

         syncInventory(player);
         return true;
      }
   }

   public static List<String> getStoredSpells(ItemStack stack) {
      if (!isLantern(stack)) {
         return new ArrayList<>();
      } else {
         CompoundTag root = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
         if (!(root.get("somake_red_soul_lantern") instanceof CompoundTag progressTag)) {
            return new ArrayList<>();
         } else {
            LinkedHashSet var8 = new LinkedHashSet();

            for (Tag tag : progressTag.getList("spells", 8)) {
               if (tag instanceof StringTag stringTag && ResourceLocation.tryParse(stringTag.getAsString()) != null) {
                  var8.add(stringTag.getAsString());
               }

               if (var8.size() >= 3) {
                  break;
               }
            }

            return new ArrayList<>(var8);
         }
      }
   }

   public static String spellTranslationKey(String spellId) {
      ResourceLocation id = ResourceLocation.tryParse(spellId);
      return id == null ? spellId : "spell." + id.getNamespace() + "." + id.getPath();
   }

   private static Optional<RedSoulLanternProgressHelper.LanternSlot> findChargeableLantern(ServerPlayer player, ResourceLocation spellId) {
      RedSoulLanternProgressHelper.LanternSlot selected = null;
      int selectedProgress = -1;

      for (int slot = 0; slot < 36; slot++) {
         ItemStack stack = player.getInventory().getItem(slot);
         int progress = chargeableProgress(stack, spellId);
         if (progress >= 0
            && (progress > selectedProgress || progress == selectedProgress && (selected == null || priority(player, slot) < selected.priority()))) {
            selected = new RedSoulLanternProgressHelper.LanternSlot(slot, stack, priority(player, slot));
            selectedProgress = progress;
         }
      }

      ItemStack offhand = player.getInventory().getItem(40);
      int offhandProgress = chargeableProgress(offhand, spellId);
      if (offhandProgress >= 0 && (offhandProgress > selectedProgress || offhandProgress == selectedProgress && (selected == null || 1 < selected.priority()))) {
         selected = new RedSoulLanternProgressHelper.LanternSlot(40, offhand, 1);
      }

      return Optional.ofNullable(selected);
   }

   private static int chargeableProgress(ItemStack stack, ResourceLocation spellId) {
      if (!isLantern(stack)) {
         return -1;
      } else {
         List<String> progress = getStoredSpells(stack);
         return progress.size() < 3 && !progress.contains(spellId.toString()) ? progress.size() : -1;
      }
   }

   private static void setStoredSpells(ItemStack stack, List<String> spellIds) {
      CustomData.update(DataComponents.CUSTOM_DATA, stack, root -> {
         CompoundTag progressTag = new CompoundTag();
         ListTag spells = new ListTag();
         spellIds.stream().distinct().limit(3L).forEach(id -> spells.add(StringTag.valueOf(id)));
         progressTag.put("spells", spells);
         root.put("somake_red_soul_lantern", progressTag);
      });
   }

   private static void clearStoredSpells(ItemStack stack) {
      CompoundTag root = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      root.remove("somake_red_soul_lantern");
      if (root.isEmpty()) {
         stack.remove(DataComponents.CUSTOM_DATA);
      } else {
         stack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
      }
   }

   private static boolean isCharged(ItemStack stack) {
      return isLantern(stack) && getStoredSpells(stack).size() >= 3;
   }

   private static boolean isLantern(ItemStack stack) {
      return ModItems.RED_SOUL_LANTERN != null && stack != null && !stack.isEmpty() && stack.getItem() == ModItems.RED_SOUL_LANTERN.get();
   }

   private static RedSoulLanternProgressHelper.LanternSlot choose(
      RedSoulLanternProgressHelper.LanternSlot current, RedSoulLanternProgressHelper.LanternSlot candidate
   ) {
      return current != null && candidate.priority() >= current.priority() ? current : candidate;
   }

   private static int priority(ServerPlayer player, int slot) {
      return slot == player.getInventory().selected ? 0 : 2 + slot;
   }

   private static void syncInventory(ServerPlayer player) {
      player.getInventory().setChanged();
      player.inventoryMenu.broadcastChanges();
   }

   public record LanternSlot(int slot, ItemStack stack, int priority) {
   }
}
