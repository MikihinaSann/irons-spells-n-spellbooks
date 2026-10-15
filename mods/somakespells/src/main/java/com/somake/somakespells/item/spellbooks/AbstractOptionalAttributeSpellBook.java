package com.somake.somakespells.item.spellbooks;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public abstract class AbstractOptionalAttributeSpellBook extends AbstractElementalProgressSpellBook {
   protected AbstractOptionalAttributeSpellBook(
      int maxSpellSlots,
      ResourceLocation trackedSchoolId,
      String progressionFamilyId,
      int progressionTier,
      AbstractElementalProgressSpellBook.ConditionalObjectiveKind conditionalObjectiveKind,
      SpellbookProgressHelper.ProgressTheme progressTheme,
      ResourceLocation optionalAttributeId,
      double spellPowerBonus,
      int maxManaBonus
   ) {
      super(maxSpellSlots, trackedSchoolId, progressionFamilyId, progressionTier, conditionalObjectiveKind, progressTheme);
      Optional<Reference<Attribute>> optionalAttribute = BuiltInRegistries.ATTRIBUTE.getHolder(optionalAttributeId);
      if (optionalAttribute.isPresent()) {
         this.withSpellbookAttributes(
            new AttributeContainer[]{
               new AttributeContainer((Holder)optionalAttribute.get(), spellPowerBonus, Operation.ADD_MULTIPLIED_BASE),
               new AttributeContainer(AttributeRegistry.MAX_MANA, maxManaBonus, Operation.ADD_VALUE)
            }
         );
      } else {
         this.withSpellbookAttributes(new AttributeContainer[]{new AttributeContainer(AttributeRegistry.MAX_MANA, maxManaBonus, Operation.ADD_VALUE)});
      }
   }
}
