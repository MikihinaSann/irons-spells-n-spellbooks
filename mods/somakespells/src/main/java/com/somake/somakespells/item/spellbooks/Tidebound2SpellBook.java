package com.somake.somakespells.item.spellbooks;

import com.somake.somakespells.registries.ModAttributes;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class Tidebound2SpellBook extends AbstractAffinitySpellBook {
   public Tidebound2SpellBook() {
      super(
         10,
         ResourceLocation.fromNamespaceAndPath("somakespells", "aqua"),
         "tidebound",
         2,
         AbstractElementalProgressSpellBook.ConditionalObjectiveKind.WET,
         SpellbookProgressHelper.aquaTheme(),
         new AttributeContainer(ModAttributes.AQUA_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
         mana(200)
      );
   }
}
