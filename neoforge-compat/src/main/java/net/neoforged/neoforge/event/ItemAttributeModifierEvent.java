package net.neoforged.neoforge.event;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class ItemAttributeModifierEvent extends Event {
   private final ItemStack stack;
   private final ItemAttributeModifiers defaultModifiers;
   private ItemAttributeModifierEvent.ItemAttributeModifiersBuilder builder;

   @Internal
   public ItemAttributeModifierEvent(ItemStack stack, ItemAttributeModifiers defaultModifiers) {
      this.stack = stack;
      this.defaultModifiers = defaultModifiers;
   }

   public ItemStack getItemStack() {
      return this.stack;
   }

   public ItemAttributeModifiers getDefaultModifiers() {
      return this.defaultModifiers;
   }

   public List<Entry> getModifiers() {
      return this.builder == null ? this.defaultModifiers.modifiers() : this.builder.getEntryView();
   }

   public boolean addModifier(Holder<Attribute> attribute, AttributeModifier modifier, EquipmentSlotGroup slot) {
      return this.getBuilder().addModifier(attribute, modifier, slot);
   }

   public boolean removeModifier(Holder<Attribute> attribute, ResourceLocation id) {
      return this.getBuilder().removeModifier(attribute, id);
   }

   public void replaceModifier(Holder<Attribute> attribute, AttributeModifier modifier, EquipmentSlotGroup slot) {
      this.getBuilder().replaceModifier(attribute, modifier, slot);
   }

   public boolean removeIf(Predicate<Entry> condition) {
      return this.getBuilder().removeIf(condition);
   }

   public boolean removeAllModifiersFor(Holder<Attribute> attribute) {
      return this.getBuilder().removeIf(entry -> entry.attribute().equals(attribute));
   }

   public void clearModifiers() {
      this.getBuilder().clear();
   }

   public ItemAttributeModifiers build() {
      return this.builder == null ? this.defaultModifiers : this.builder.build(this.defaultModifiers.showInTooltip());
   }

   private ItemAttributeModifierEvent.ItemAttributeModifiersBuilder getBuilder() {
      if (this.builder == null) {
         this.builder = new ItemAttributeModifierEvent.ItemAttributeModifiersBuilder(this.defaultModifiers);
      }

      return this.builder;
   }

   private static class ItemAttributeModifiersBuilder {
      private List<Entry> entries = new LinkedList<>();
      private Map<ItemAttributeModifierEvent.ItemAttributeModifiersBuilder.Key, Entry> entriesByKey;

      ItemAttributeModifiersBuilder(ItemAttributeModifiers defaultModifiers) {
         this.entriesByKey = new HashMap<>(defaultModifiers.modifiers().size());

         for (Entry entry : defaultModifiers.modifiers()) {
            this.entries.add(entry);
            this.entriesByKey.put(new ItemAttributeModifierEvent.ItemAttributeModifiersBuilder.Key(entry.attribute(), entry.modifier().id()), entry);
         }
      }

      List<Entry> getEntryView() {
         return Collections.unmodifiableList(this.entries);
      }

      boolean addModifier(Holder<Attribute> attribute, AttributeModifier modifier, EquipmentSlotGroup slot) {
         ItemAttributeModifierEvent.ItemAttributeModifiersBuilder.Key key = new ItemAttributeModifierEvent.ItemAttributeModifiersBuilder.Key(
            attribute, modifier.id()
         );
         if (this.entriesByKey.containsKey(key)) {
            return false;
         }

         Entry entry = new Entry(attribute, modifier, slot);
         this.entries.add(entry);
         this.entriesByKey.put(key, entry);
         return true;
      }

      boolean removeModifier(Holder<Attribute> attribute, ResourceLocation id) {
         Entry entry = this.entriesByKey.remove(new ItemAttributeModifierEvent.ItemAttributeModifiersBuilder.Key(attribute, id));
         if (entry != null) {
            this.entries.remove(entry);
            return true;
         } else {
            return false;
         }
      }

      @Nullable
      Entry replaceModifier(Holder<Attribute> attribute, AttributeModifier modifier, EquipmentSlotGroup slot) {
         ItemAttributeModifierEvent.ItemAttributeModifiersBuilder.Key key = new ItemAttributeModifierEvent.ItemAttributeModifiersBuilder.Key(
            attribute, modifier.id()
         );
         Entry entry = new Entry(attribute, modifier, slot);
         if (this.entriesByKey.containsKey(key)) {
            Entry previousEntry = this.entriesByKey.get(key);
            int index = this.entries.indexOf(previousEntry);
            if (index != -1) {
               this.entries.set(index, entry);
            } else {
               this.entries.add(entry);
            }

            this.entriesByKey.put(key, entry);
            return previousEntry;
         } else {
            this.entries.add(entry);
            this.entriesByKey.put(key, entry);
            return null;
         }
      }

      boolean removeIf(Predicate<Entry> condition) {
         this.entries.removeIf(condition);
         return this.entriesByKey.values().removeIf(condition);
      }

      void clear() {
         this.entries.clear();
         this.entriesByKey.clear();
      }

      public ItemAttributeModifiers build(boolean showInTooltip) {
         return new ItemAttributeModifiers(ImmutableList.copyOf(this.entries), showInTooltip);
      }

      private record Key(Holder<Attribute> attr, ResourceLocation id) {
      }
   }
}
