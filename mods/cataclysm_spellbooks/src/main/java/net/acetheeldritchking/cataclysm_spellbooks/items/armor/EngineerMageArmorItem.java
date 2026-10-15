package net.acetheeldritchking.cataclysm_spellbooks.items.armor;

import net.acetheeldritchking.cataclysm_spellbooks.registries.CSAttributeRegistry;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;

public class EngineerMageArmorItem extends ImbuableCSArmorItem {
   public EngineerMageArmorItem(Type slot, Properties settings) {
      super(CSArmorMaterialRegistry.ENGINEER_ARMOR, slot, settings, schoolAttributes(CSAttributeRegistry.TECHNOMANCY_MAGIC_POWER, 125, 0.1F, 0.05F));
   }
}
