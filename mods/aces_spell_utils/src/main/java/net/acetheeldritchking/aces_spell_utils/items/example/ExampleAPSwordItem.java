package net.acetheeldritchking.aces_spell_utils.items.example;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.acetheeldritchking.aces_spell_utils.items.weapons.ActiveAndPassiveAbilitySwordItem;
import net.acetheeldritchking.aces_spell_utils.utils.ASRarities;
import net.minecraft.world.item.Rarity;

public class ExampleAPSwordItem extends ActiveAndPassiveAbilitySwordItem {
   public ExampleAPSwordItem() {
      super(
         ASWeaponTiers.EXAMPLE_AP_SWORD,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)ASRarities.COSMIC_RARITY_PROXY.getValue())
            .attributes(ExtendedSwordItem.createAttributes(ASWeaponTiers.EXAMPLE_AP_SWORD))
      );
   }

   @Override
   protected int getPassiveCooldownTicks() {
      return 200;
   }

   @Override
   protected int getActiveCooldownTicks() {
      return 200;
   }
}
