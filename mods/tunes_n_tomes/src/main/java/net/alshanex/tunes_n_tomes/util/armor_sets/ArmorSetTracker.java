package net.alshanex.tunes_n_tomes.util.armor_sets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;

@EventBusSubscriber(
   modid = "tunes_n_tomes",
   bus = Bus.GAME
)
public class ArmorSetTracker {
   private static final List<ArmorSetDefinition> REGISTERED_SETS = new ArrayList<>();
   private static final Map<UUID, Set<Integer>> activeSets = new HashMap<>();
   private static final int CHECK_INTERVAL = 10;

   public static void register(ArmorSetDefinition definition) {
      REGISTERED_SETS.add(definition);
   }

   public static boolean isWearingSet(ServerPlayer player, ArmorSetDefinition setDef) {
      Set<Integer> active = activeSets.get(player.getUUID());
      if (active == null) {
         return false;
      } else {
         int index = REGISTERED_SETS.indexOf(setDef);
         return index >= 0 && active.contains(index);
      }
   }

   public static List<String> getActiveSetNames(ServerPlayer player) {
      Set<Integer> active = activeSets.get(player.getUUID());
      if (active != null && !active.isEmpty()) {
         List<String> names = new ArrayList<>();

         for (int i : active) {
            if (i < REGISTERED_SETS.size()) {
               names.add(REGISTERED_SETS.get(i).getName());
            }
         }

         return names;
      } else {
         return Collections.emptyList();
      }
   }

   public static ArmorSetDefinition getSetForItem(Item item) {
      for (ArmorSetDefinition def : REGISTERED_SETS) {
         if (def.isPartOfSet(item)) {
            return def;
         }
      }

      return null;
   }

   public static List<ArmorSetDefinition> getRegisteredSets() {
      return Collections.unmodifiableList(REGISTERED_SETS);
   }

   @SubscribeEvent
   public static void onPlayerTick(Post event) {
      if (event.getEntity() instanceof ServerPlayer serverPlayer) {
         if (serverPlayer.tickCount % 10 == 0) {
            if (!REGISTERED_SETS.isEmpty()) {
               Set<Integer> current = activeSets.computeIfAbsent(serverPlayer.getUUID(), k -> new HashSet<>());

               for (int i = 0; i < REGISTERED_SETS.size(); i++) {
                  boolean equipped = REGISTERED_SETS.get(i).isFullSetEquipped(serverPlayer);
                  if (equipped) {
                     current.add(i);
                  } else {
                     current.remove(i);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerLogout(PlayerLoggedOutEvent event) {
      activeSets.remove(event.getEntity().getUUID());
   }

   @SubscribeEvent
   public static void onPlayerClone(Clone event) {
      activeSets.remove(event.getOriginal().getUUID());
   }
}
