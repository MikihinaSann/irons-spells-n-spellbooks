package net.acetheeldritchking.cataclysm_spellbooks.registries;

import io.redspace.ironsspellbooks.item.armor.UpgradeOrbType;
import io.redspace.ironsspellbooks.registries.UpgradeOrbTypeRegistry;
import net.acetheeldritchking.cataclysm_spellbooks.CataclysmSpellbooks;
import net.minecraft.resources.ResourceKey;

public class CSUpgradeOrbTypeRegistry {
   public static ResourceKey<UpgradeOrbType> ABYSSAL_SPELL_POWER = ResourceKey.create(
      UpgradeOrbTypeRegistry.UPGRADE_ORB_REGISTRY_KEY, CataclysmSpellbooks.id("abyssal_power")
   );
   public static ResourceKey<UpgradeOrbType> TECHNOMANCY_SPELL_POWER = ResourceKey.create(
      UpgradeOrbTypeRegistry.UPGRADE_ORB_REGISTRY_KEY, CataclysmSpellbooks.id("technomancy_power")
   );
}
