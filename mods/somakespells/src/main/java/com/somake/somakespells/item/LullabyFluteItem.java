package com.somake.somakespells.item;

import com.somake.somakespells.compat.TunesCompat;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.util.StaffHandCompat;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainerMutable;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;

public class LullabyFluteItem extends StaffItem implements IPresetSpellContainer {
   public LullabyFluteItem(Properties properties) {
      super(properties);
   }

   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null && !ISpellContainer.isSpellContainer(itemStack)) {
         ISpellContainerMutable spellContainer = ISpellContainer.create(1, true, false).mutableCopy();
         spellContainer.addSpellAtIndex((AbstractSpell)ModSpells.COMFORTING_LULLABY.get(), 4, 0, true);
         ISpellContainer.set(itemStack, spellContainer.toImmutable());
      }
   }

   public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
      ResourceLocation modifierId = ResourceLocation.fromNamespaceAndPath("somakespells", "lullaby_flute_modifier");
      Builder builder = StaffHandCompat.createStaffAttributeBuilder(super.getDefaultAttributeModifiers(stack), modifierId, 4.0, -2.4, 0.0);
      EquipmentSlotGroup slotGroup = StaffHandCompat.getConfiguredStaffSlot();
      TunesCompat.getMelodySpellPowerAttribute()
         .ifPresent(attribute -> builder.add(attribute, new AttributeModifier(modifierId, 0.15, Operation.ADD_MULTIPLIED_BASE), slotGroup));
      builder.add(AttributeRegistry.MANA_REGEN, new AttributeModifier(modifierId, 0.1, Operation.ADD_MULTIPLIED_BASE), slotGroup);
      return builder.build();
   }
}
