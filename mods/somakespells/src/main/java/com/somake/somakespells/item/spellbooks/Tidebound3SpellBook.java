package com.somake.somakespells.item.spellbooks;

import com.somake.somakespells.registries.ModAttributes;
import com.somake.somakespells.registries.ModSpells;
import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;

public class Tidebound3SpellBook extends AbstractAffinitySpellBook {
   public Tidebound3SpellBook() {
      super(
         12,
         ResourceLocation.fromNamespaceAndPath("somakespells", "aqua"),
         "tidebound",
         3,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.WET,
         SpellbookProgressHelper.aquaTheme(),
         new AttributeContainer(ModAttributes.AQUA_SPELL_POWER, 0.25, Operation.ADD_MULTIPLIED_BASE),
         mana(300)
      );
   }

   @Override
   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         super.initializeSpellContainer(itemStack);
         AffinityData.setAffinityData(itemStack, (AbstractSpell)ModSpells.THUNDER_CLOUD.get(), 1);
      }
   }
}
