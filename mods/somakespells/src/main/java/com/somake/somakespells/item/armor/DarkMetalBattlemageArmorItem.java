package com.somake.somakespells.item.armor;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import org.jetbrains.annotations.NotNull;

public class DarkMetalBattlemageArmorItem extends BaseGeoArmorItem implements IPresetSpellContainer {
   public DarkMetalBattlemageArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
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

      ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
         "somakespells", "dark_metal_armor_modifier_" + ((ArmorItem)stack.getItem()).getType().getName()
      );
      EquipmentSlotGroup slotGroup = EquipmentSlotGroup.bySlot(((ArmorItem)stack.getItem()).getType().getSlot());
      builder.add(AttributeRegistry.MAX_MANA, new AttributeModifier(id, 125.0, Operation.ADD_VALUE), slotGroup);
      return builder.build();
   }

   public void appendHoverText(@NotNull ItemStack itemStack, TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      super.appendHoverText(itemStack, context, lines, flag);
      if (Screen.hasShiftDown()) {
         lines.add(Component.translatable("item.somakespells.dark_metal_battlemage.description_0"));
         lines.add(Component.translatable("item.somakespells.dark_metal_battlemage.description_1"));
         lines.add(Component.translatable("item.somakespells.dark_metal_battlemage.description_2"));
      } else {
         lines.add(Component.translatable("tooltip.somakespells.hold_shift"));
      }
   }
}
