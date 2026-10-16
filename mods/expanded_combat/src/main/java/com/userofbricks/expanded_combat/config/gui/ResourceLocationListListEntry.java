package com.userofbricks.expanded_combat.config.gui;

import com.userofbricks.expanded_combat.datagen.LangStrings;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import me.shedaniel.clothconfig2.gui.entries.AbstractTextFieldListListEntry;
import me.shedaniel.clothconfig2.gui.entries.AbstractTextFieldListListEntry.AbstractTextFieldListCell;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class ResourceLocationListListEntry
   extends AbstractTextFieldListListEntry<ResourceLocation, ResourceLocationListListEntry.ResourceLocationListCell, ResourceLocationListListEntry> {
   private Supplier<List<ResourceLocation>> availableValues = null;

   public ResourceLocationListListEntry(
      Component fieldName,
      List<ResourceLocation> value,
      boolean defaultExpanded,
      Supplier<Optional<Component[]>> tooltipSupplier,
      Consumer<List<ResourceLocation>> saveConsumer,
      Supplier<List<ResourceLocation>> defaultValue,
      Component resetButtonKey,
      boolean requiresRestart,
      boolean deleteButtonEnabled,
      boolean insertInFront
   ) {
      super(
         fieldName,
         value,
         defaultExpanded,
         tooltipSupplier,
         saveConsumer,
         defaultValue,
         resetButtonKey,
         requiresRestart,
         deleteButtonEnabled,
         insertInFront,
         ResourceLocationListListEntry.ResourceLocationListCell::new
      );
   }

   public ResourceLocationListListEntry setPossibleValues(Supplier<List<ResourceLocation>> availableValues) {
      this.availableValues = availableValues;
      return this;
   }

   public ResourceLocationListListEntry self() {
      return this;
   }

   public static class ResourceLocationListCell
      extends AbstractTextFieldListCell<ResourceLocation, ResourceLocationListListEntry.ResourceLocationListCell, ResourceLocationListListEntry> {
      public ResourceLocationListCell(@Nullable ResourceLocation value, ResourceLocationListListEntry listListEntry) {
         super(value, listListEntry);
      }

      @Nullable
      protected ResourceLocation substituteDefault(@Nullable ResourceLocation value) {
         return value == null ? ResourceLocation.parse("nothing") : value;
      }

      protected boolean isValidText(@NotNull String s) {
         List<String> parts = Arrays.asList(s.split(":"));
         return parts.size() <= 2 && ResourceLocation.isValidNamespace(parts.getFirst()) && ResourceLocation.isValidPath(parts.get(1));
      }

      public ResourceLocation getValue() {
         return ResourceLocation.parse(this.widget.getValue());
      }

      public Optional<Component> getError() {
         List<String> parts = Arrays.asList(this.widget.getValue().split(":"));
         if (parts.size() <= 2 && ResourceLocation.isValidNamespace(parts.getFirst()) && ResourceLocation.isValidPath(parts.get(1))) {
            return ((ResourceLocationListListEntry)this.listListEntry).availableValues != null
                  && !((ResourceLocationListListEntry)this.listListEntry).availableValues.get().contains(ResourceLocation.parse(this.widget.getValue()))
               ? Optional.of(Component.translatable(LangStrings.RESOURCE_LOCATION_NOT_WITHIN_AVAILABLE_VALUES))
               : Optional.empty();
         } else {
            return Optional.of(Component.translatable(LangStrings.NOT_VALID_RESOURCE_LOCATION_CONFIG_ERROR));
         }
      }
   }
}
