package net.acetheeldritchking.aces_spell_utils.items.example;

import io.redspace.ironsspellbooks.render.CinderousRarity;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.acetheeldritchking.aces_spell_utils.items.custom.LootBagItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;

public class ExampleLootBagItem extends LootBagItem {
   static ResourceLocation lootTable = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "entities/fire_boss");

   public ExampleLootBagItem() {
      super(ItemPropertiesHelper.equipment(8).fireResistant().rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue()), lootTable);
   }
}
