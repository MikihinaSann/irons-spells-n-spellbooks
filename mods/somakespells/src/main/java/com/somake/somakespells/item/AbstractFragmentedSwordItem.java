package com.somake.somakespells.item;

import com.somake.somakespells.registries.ModSpells;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainerMutable;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;

public abstract class AbstractFragmentedSwordItem extends BaseAttributeSwordItem implements IPresetSpellContainer {
   private final int presetSpellLevel;

   protected AbstractFragmentedSwordItem(
      Tier tier,
      int attackDamageBonus,
      float attackSpeedModifier,
      Properties properties,
      Holder<Attribute> extraAttribute,
      ResourceLocation modifierId,
      double extraAmount,
      Operation extraOperation,
      int presetSpellLevel
   ) {
      super(tier, attackDamageBonus, attackSpeedModifier, properties, extraAttribute, modifierId, extraAmount, extraOperation);
      this.presetSpellLevel = presetSpellLevel;
   }

   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null && !ISpellContainer.isSpellContainer(itemStack)) {
         ISpellContainerMutable spellContainer = ISpellContainer.create(1, true, false).mutableCopy();
         spellContainer.addSpellAtIndex((AbstractSpell)ModSpells.FRAGMENTED_REQUIEM.get(), this.presetSpellLevel, 0, true);
         ISpellContainer.set(itemStack, spellContainer.toImmutable());
      }
   }
}
