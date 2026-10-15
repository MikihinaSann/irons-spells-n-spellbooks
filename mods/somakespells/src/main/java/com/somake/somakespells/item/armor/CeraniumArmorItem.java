package com.somake.somakespells.item.armor;

import com.somake.somakespells.registries.ModAttributes;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;

public class CeraniumArmorItem extends BaseGeoArmorItem implements IPresetSpellContainer {
   public CeraniumArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
      super(material, type, properties);
   }

   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         if (this.getType() == Type.CHESTPLATE && !ISpellContainer.isSpellContainer(itemStack)) {
            ISpellContainer spellContainer = ISpellContainer.create(1, true, true);
            ISpellContainer.set(itemStack, spellContainer);
         }
      }
   }

   public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
      ItemAttributeModifiers base = super.getDefaultAttributeModifiers(stack);
      Builder builder = ItemAttributeModifiers.builder();

      for (Entry entry : base.modifiers()) {
         builder.add(entry.attribute(), entry.modifier(), entry.slot());
      }

      ResourceLocation id = ResourceLocation.fromNamespaceAndPath("somakespells", "ceranium_armor_modifier_" + ((ArmorItem)stack.getItem()).getType().getName());
      EquipmentSlotGroup slotGroup = EquipmentSlotGroup.bySlot(((ArmorItem)stack.getItem()).getType().getSlot());
      builder.add(AttributeRegistry.MAX_MANA, new AttributeModifier(id, 200.0, Operation.ADD_VALUE), slotGroup);
      builder.add(AttributeRegistry.LIGHTNING_SPELL_POWER, new AttributeModifier(id, 0.1, Operation.ADD_MULTIPLIED_BASE), slotGroup);
      builder.add(ModAttributes.AQUA_SPELL_POWER, new AttributeModifier(id, 0.1, Operation.ADD_MULTIPLIED_BASE), slotGroup);
      builder.add(AttributeRegistry.SPELL_POWER, new AttributeModifier(id, 0.05, Operation.ADD_MULTIPLIED_BASE), slotGroup);
      return builder.build();
   }
}
