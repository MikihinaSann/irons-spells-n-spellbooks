package com.somake.somakespells.util;

import com.somake.somakespells.compat.BornInChaosCompat;
import com.somake.somakespells.compat.LegendaryMonstersCompat;
import com.somake.somakespells.config.Config;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public final class StaffHandCompat {
   private static final EquipmentSlotGroup DEFAULT_STAFF_SLOT = EquipmentSlotGroup.MAINHAND;
   private static final EquipmentSlotGroup LEGACY_STAFF_SLOT = EquipmentSlotGroup.OFFHAND;
   private static final double DEFAULT_STAFF_ATTACK_DAMAGE = 5.0;
   private static final double DEFAULT_STAFF_ATTACK_SPEED = -2.4;
   private static final double DEFAULT_STAFF_ENTITY_INTERACTION_RANGE = 0.0;
   public static final ResourceLocation BASE_ATTACK_DAMAGE_ID = ResourceLocation.withDefaultNamespace("base_attack_damage");
   public static final ResourceLocation BASE_ATTACK_SPEED_ID = ResourceLocation.withDefaultNamespace("base_attack_speed");

   private StaffHandCompat() {
   }

   public static EquipmentSlotGroup getConfiguredStaffSlot() {
      return Config.STAFFS_USE_LEGACY_OFFHAND.get() ? LEGACY_STAFF_SLOT : DEFAULT_STAFF_SLOT;
   }

   public static EquipmentSlotGroup getPreferredStaffSlot(ItemStack stack) {
      return getConfiguredStaffSlot();
   }

   public static EquipmentSlotGroup getBaseCombatSlot() {
      return DEFAULT_STAFF_SLOT;
   }

   public static boolean isStaff(ItemStack stack) {
      return stack.getItem() instanceof StaffItem
         || BornInChaosCompat.isLoaded() && BornInChaosCompat.isCompatStaff(BuiltInRegistries.ITEM.getKey(stack.getItem()))
         || LegendaryMonstersCompat.isLoaded() && LegendaryMonstersCompat.isCompatStaff(BuiltInRegistries.ITEM.getKey(stack.getItem()));
   }

   public static Builder createStaffAttributeBuilder(ItemAttributeModifiers baseModifiers, ResourceLocation rangeModifierId) {
      return createStaffAttributeBuilder(baseModifiers, rangeModifierId, 5.0, -2.4, 0.0);
   }

   public static Builder createStaffAttributeBuilder(
      ItemAttributeModifiers baseModifiers, ResourceLocation rangeModifierId, double attackDamage, double attackSpeed, double entityInteractionRange
   ) {
      Builder builder = ItemAttributeModifiers.builder();
      boolean hasAttackDamage = false;
      boolean hasAttackSpeed = false;
      boolean hasRange = false;

      for (Entry entry : baseModifiers.modifiers()) {
         builder.add(entry.attribute(), entry.modifier(), entry.slot());
         if (entry.slot() == DEFAULT_STAFF_SLOT) {
            if (entry.attribute().equals(Attributes.ATTACK_DAMAGE)) {
               hasAttackDamage = true;
            } else if (entry.attribute().equals(Attributes.ATTACK_SPEED)) {
               hasAttackSpeed = true;
            } else if (entry.attribute().equals(Attributes.ENTITY_INTERACTION_RANGE)) {
               hasRange = true;
            }
         }
      }

      if (!hasAttackDamage) {
         builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage, Operation.ADD_VALUE), DEFAULT_STAFF_SLOT);
      }

      if (!hasAttackSpeed) {
         builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, Operation.ADD_VALUE), DEFAULT_STAFF_SLOT);
      }

      if (!hasRange) {
         builder.add(
            Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(rangeModifierId, entityInteractionRange, Operation.ADD_VALUE), DEFAULT_STAFF_SLOT
         );
      }

      return builder;
   }

   public static void remapStaffModifiers(ItemAttributeModifierEvent event) {
      if (isStaff(event.getItemStack())) {
         EquipmentSlotGroup targetSlot = getPreferredStaffSlot(event.getItemStack());
         EquipmentSlotGroup sourceSlot = targetSlot == DEFAULT_STAFF_SLOT ? LEGACY_STAFF_SLOT : DEFAULT_STAFF_SLOT;
         boolean restoreToMainHand = targetSlot == DEFAULT_STAFF_SLOT;
         List<Entry> entriesToMove = new ArrayList<>();

         for (Entry entry : event.getModifiers()) {
            if (entry.slot() == sourceSlot && (restoreToMainHand || !shouldStayInMainHand(entry.attribute()))) {
               entriesToMove.add(entry);
            }
         }

         for (Entry entryx : entriesToMove) {
            Holder<Attribute> attribute = entryx.attribute();
            AttributeModifier modifier = entryx.modifier();
            event.removeModifier(attribute, modifier.id());
            event.addModifier(attribute, modifier, targetSlot);
         }
      }
   }

   private static boolean shouldStayInMainHand(Holder<Attribute> attribute) {
      return attribute.equals(Attributes.ATTACK_DAMAGE) || attribute.equals(Attributes.ATTACK_SPEED) || attribute.equals(Attributes.ENTITY_INTERACTION_RANGE);
   }
}
