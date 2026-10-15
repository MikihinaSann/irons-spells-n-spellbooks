package com.somake.somakespells.item.spellbooks;

import com.somake.somakespells.registries.ModAttributes;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class TideboundSpellBook extends AbstractAffinitySpellBook {
   public TideboundSpellBook() {
      super(
         8,
         ResourceLocation.fromNamespaceAndPath("somakespells", "aqua"),
         "tidebound",
         1,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.WET,
         SpellbookProgressHelper.aquaTheme(),
         new AttributeContainer(ModAttributes.AQUA_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE),
         mana(100)
      );
   }
}
