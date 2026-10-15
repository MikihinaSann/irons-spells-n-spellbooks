package net.acetheeldritchking.aces_spell_utils.items.example;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.acetheeldritchking.aces_spell_utils.items.weapons.ActiveAndPassiveAbilityMagicSwordItem;
import net.acetheeldritchking.aces_spell_utils.utils.ASRarities;
import net.minecraft.world.item.Rarity;

public class ExampleAPMagicSwordItem extends ActiveAndPassiveAbilityMagicSwordItem {
   public ExampleAPMagicSwordItem() {
      super(
         ASWeaponTiers.EXAMPLE_AP_MAGIC_SWORD,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)ASRarities.SCULK_RARITY_PROXY.getValue())
            .attributes(ExtendedSwordItem.createAttributes(ASWeaponTiers.EXAMPLE_AP_MAGIC_SWORD)),
         SpellDataRegistryHolder.of(
            new SpellDataRegistryHolder[]{
               new SpellDataRegistryHolder(SpellRegistry.TELEKINESIS_SPELL, 10), new SpellDataRegistryHolder(SpellRegistry.ABYSSAL_SHROUD_SPELL, 10)
            }
         )
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
