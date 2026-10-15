package net.alshanex.alshanex_familiars.datafix;

import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class FixAttributeNames extends DataFixerElement {
   private static final Map<String, String> LEGACY_ATTRIBUTE_IDS = Map.of(
      "alshanex_familiars:sound_spell_power", "familiarslib:sound_spell_power", "alshanex_familiars:sound_magic_resist", "familiarslib:sound_magic_resist"
   );

   @Override
   public List<String> preScanValuesToMatch() {
      return List.of("alshanex_familiars:sound_spell_power", "alshanex_familiars:sound_magic_resist", "attribute_modifiers", "Attributes", "components");
   }

   @Override
   public boolean runFixer(CompoundTag tag) {
      boolean fixed = false;
      if (tag.contains("components") && this.fixItemComponents(tag.getCompound("components"))) {
         fixed = true;
      }

      if (tag.contains("AttributeModifiers") && this.fixAttributeModifiers(tag.getList("AttributeModifiers", 10))) {
         fixed = true;
      }

      if (tag.contains("attribute_modifiers") && this.fixNewAttributeModifiers(tag.getCompound("attribute_modifiers"))) {
         fixed = true;
      }

      if (tag.contains("Attributes") && this.fixEntityAttributes(tag.getList("Attributes", 10))) {
         fixed = true;
      }

      if (tag.contains("Inventory") && this.fixInventory(tag.getList("Inventory", 10))) {
         fixed = true;
      }

      if (tag.contains("HandItems") && this.fixItemsList(tag.getList("HandItems", 10))) {
         fixed = true;
      }

      if (tag.contains("ArmorItems") && this.fixItemsList(tag.getList("ArmorItems", 10))) {
         fixed = true;
      }

      return fixed;
   }

   private boolean fixItemComponents(CompoundTag components) {
      boolean fixed = false;
      if (components.contains("minecraft:attribute_modifiers")) {
         CompoundTag attributeModifiers = components.getCompound("minecraft:attribute_modifiers");
         if (attributeModifiers.contains("modifiers") && this.fixAttributeModifiers(attributeModifiers.getList("modifiers", 10))) {
            fixed = true;
         }
      }

      return fixed;
   }

   private boolean fixNewAttributeModifiers(CompoundTag attributeModifiersTag) {
      boolean fixed = false;
      if (attributeModifiersTag.contains("modifiers") && this.fixAttributeModifiers(attributeModifiersTag.getList("modifiers", 10))) {
         fixed = true;
      }

      return fixed;
   }

   private boolean fixAttributeModifiers(ListTag attributeModifiers) {
      boolean fixed = false;

      for (Tag t : attributeModifiers) {
         CompoundTag modifier = (CompoundTag)t;
         if (modifier.contains("type", 8)) {
            String attributeName = modifier.getString("type");
            String newName = LEGACY_ATTRIBUTE_IDS.get(attributeName);
            if (newName != null) {
               modifier.putString("type", newName);
               fixed = true;
            }
         }

         if (modifier.contains("AttributeName", 8)) {
            String attributeName = modifier.getString("AttributeName");
            String newName = LEGACY_ATTRIBUTE_IDS.get(attributeName);
            if (newName != null) {
               modifier.putString("AttributeName", newName);
               fixed = true;
            }
         }
      }

      return fixed;
   }

   private boolean fixEntityAttributes(ListTag attributes) {
      boolean fixed = false;

      for (Tag t : attributes) {
         CompoundTag attribute = (CompoundTag)t;
         if (attribute.contains("Name", 8)) {
            String attributeName = attribute.getString("Name");
            String newName = LEGACY_ATTRIBUTE_IDS.get(attributeName);
            if (newName != null) {
               attribute.putString("Name", newName);
               fixed = true;
            }
         }
      }

      return fixed;
   }

   private boolean fixInventory(ListTag inventory) {
      boolean fixed = false;

      for (Tag t : inventory) {
         CompoundTag item = (CompoundTag)t;
         if (this.runFixer(item)) {
            fixed = true;
         }
      }

      return fixed;
   }

   private boolean fixItemsList(ListTag itemsList) {
      boolean fixed = false;

      for (Tag t : itemsList) {
         CompoundTag item = (CompoundTag)t;
         if (this.runFixer(item)) {
            fixed = true;
         }
      }

      return fixed;
   }
}
