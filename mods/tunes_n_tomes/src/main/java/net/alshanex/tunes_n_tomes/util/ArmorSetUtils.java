package net.alshanex.tunes_n_tomes.util;

import net.alshanex.tunes_n_tomes.registry.TArmorSets;
import net.minecraft.world.entity.player.Player;

public class ArmorSetUtils {
   public static boolean hasCompleteSoundSet(Player player) {
      return TArmorSets.BARD_SET.isFullSetEquipped(player) || TArmorSets.MELODIC_MASTERY_SET.isFullSetEquipped(player);
   }
}
