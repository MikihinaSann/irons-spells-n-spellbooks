package net.alshanex.tunes_n_tomes.registry;

import io.redspace.ironsspellbooks.item.armor.UpgradeOrbType;
import io.redspace.ironsspellbooks.registries.UpgradeOrbTypeRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class TUpgradeOrbRegistry {
   public static ResourceKey<UpgradeOrbType> MELODY_SPELL_POWER = ResourceKey.create(
      UpgradeOrbTypeRegistry.UPGRADE_ORB_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "melody_power")
   );
}
