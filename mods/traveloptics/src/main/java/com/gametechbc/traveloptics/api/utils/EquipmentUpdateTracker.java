package com.gametechbc.traveloptics.api.utils;

import java.util.WeakHashMap;
import net.minecraft.world.entity.player.Player;

public class EquipmentUpdateTracker {
   private static final WeakHashMap<Player, Boolean> updatingPlayers = new WeakHashMap<>();

   public EquipmentUpdateTracker() {
      super();
   }

   public static void markPlayer(Player player) {
      updatingPlayers.put(player, true);
   }

   public static boolean isMarked(Player player) {
      return updatingPlayers.getOrDefault(player, false);
   }

   public static void unmarkPlayer(Player player) {
      updatingPlayers.remove(player);
   }
}
