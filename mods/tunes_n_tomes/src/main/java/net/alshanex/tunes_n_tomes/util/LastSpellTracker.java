package net.alshanex.tunes_n_tomes.util;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;

public class LastSpellTracker {
   private static final ConcurrentHashMap<UUID, LastSpellTracker.SpellRecord> lastSpells = new ConcurrentHashMap<>();

   public static void track(ServerPlayer player, String spellId, int spellLevel) {
      lastSpells.put(player.getUUID(), new LastSpellTracker.SpellRecord(spellId, spellLevel));
   }

   @Nullable
   public static LastSpellTracker.SpellRecord get(ServerPlayer player) {
      return lastSpells.get(player.getUUID());
   }

   public static void clear(UUID playerUUID) {
      lastSpells.remove(playerUUID);
   }

   public record SpellRecord(String spellId, int spellLevel) {
   }
}
