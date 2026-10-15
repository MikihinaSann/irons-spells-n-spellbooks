package com.somake.somakespells.item.spellbooks;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public abstract class AbstractAffinitySpellBook extends AbstractElementalProgressSpellBook {
   protected AbstractAffinitySpellBook(
      int maxSpellSlots,
      ResourceLocation trackedSchoolId,
      String progressionFamilyId,
      int progressionTier,
      AbstractElementalProgressSpellBook.ConditionalObjectiveKind conditionalObjectiveKind,
      SpellbookProgressHelper.ProgressTheme progressTheme,
      AttributeContainer... attributes
   ) {
      super(maxSpellSlots, trackedSchoolId, progressionFamilyId, progressionTier, conditionalObjectiveKind, progressTheme);
      this.withSpellbookAttributes(attributes);
   }

   protected static AttributeContainer mana(int amount) {
      return new AttributeContainer(AttributeRegistry.MAX_MANA, amount, Operation.ADD_VALUE);
   }

   protected static AttributeContainer[] affinityAttributes(Optional<? extends Holder<Attribute>> optionalAttribute, double amount, int manaAmount) {
      return optionalAttribute.isPresent()
         ? new AttributeContainer[]{new AttributeContainer(optionalAttribute.get(), amount, Operation.ADD_MULTIPLIED_BASE), mana(manaAmount)}
         : new AttributeContainer[]{mana(manaAmount)};
   }
}
