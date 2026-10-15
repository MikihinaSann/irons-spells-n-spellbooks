package net.alshanex.tunes_n_tomes.registry;

import net.alshanex.tunes_n_tomes.util.armor_sets.ArmorSetDefinition;
import net.alshanex.tunes_n_tomes.util.armor_sets.ArmorSetTracker;
import net.minecraft.network.chat.Component;

public class TArmorSets {
   public static final ArmorSetDefinition BARD_SET = new ArmorSetDefinition(
      "bard",
      TItemRegistry.BARD_HELMET,
      TItemRegistry.BARD_CHESTPLATE,
      TItemRegistry.BARD_LEGGINGS,
      TItemRegistry.BARD_BOOTS,
      Component.translatable("armor_set.tunes_n_tomes.bard_set")
   );
   public static final ArmorSetDefinition MELODIC_MASTERY_SET = new ArmorSetDefinition(
      "melodic_mastery",
      TItemRegistry.MELODIC_MASTERY_HELMET,
      TItemRegistry.MELODIC_MASTERY_CHESTPLATE,
      TItemRegistry.MELODIC_MASTERY_LEGGINGS,
      TItemRegistry.MELODIC_MASTERY_BOOTS,
      Component.translatable("armor_set.tunes_n_tomes.bard_set")
   );

   public static void register() {
      ArmorSetTracker.register(BARD_SET);
   }
}
