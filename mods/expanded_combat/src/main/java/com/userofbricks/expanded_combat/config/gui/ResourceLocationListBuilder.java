package com.userofbricks.expanded_combat.config.gui;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import me.shedaniel.clothconfig2.impl.builders.AbstractListBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ResourceLocationListBuilder extends AbstractListBuilder<ResourceLocation, ResourceLocationListListEntry, ResourceLocationListBuilder> {
   private Function<ResourceLocationListListEntry, ResourceLocationListListEntry.ResourceLocationListCell> createNewInstance;
   protected Supplier<List<ResourceLocation>> availableValues = null;

   public ResourceLocationListBuilder(Component resetButtonKey, Component fieldNameKey, List<ResourceLocation> value) {
      super(resetButtonKey, fieldNameKey);
      this.value = value;
   }

   public Function<ResourceLocation, Optional<Component>> getCellErrorSupplier() {
      return super.getCellErrorSupplier();
   }

   public ResourceLocationListBuilder setCellErrorSupplier(Function<ResourceLocation, Optional<Component>> cellErrorSupplier) {
      return (ResourceLocationListBuilder)super.setCellErrorSupplier(cellErrorSupplier);
   }

   public ResourceLocationListBuilder setErrorSupplier(Function<List<ResourceLocation>, Optional<Component>> errorSupplier) {
      return (ResourceLocationListBuilder)super.setErrorSupplier(errorSupplier);
   }

   public ResourceLocationListBuilder setDeleteButtonEnabled(boolean deleteButtonEnabled) {
      return (ResourceLocationListBuilder)super.setDeleteButtonEnabled(deleteButtonEnabled);
   }

   public ResourceLocationListBuilder setInsertInFront(boolean insertInFront) {
      return (ResourceLocationListBuilder)super.setInsertInFront(insertInFront);
   }

   public ResourceLocationListBuilder setAddButtonTooltip(Component addTooltip) {
      return (ResourceLocationListBuilder)super.setAddButtonTooltip(addTooltip);
   }

   public ResourceLocationListBuilder setRemoveButtonTooltip(Component removeTooltip) {
      return (ResourceLocationListBuilder)super.setRemoveButtonTooltip(removeTooltip);
   }

   public ResourceLocationListBuilder requireRestart() {
      return (ResourceLocationListBuilder)super.requireRestart();
   }

   public ResourceLocationListBuilder setCreateNewInstance(
      Function<ResourceLocationListListEntry, ResourceLocationListListEntry.ResourceLocationListCell> createNewInstance
   ) {
      this.createNewInstance = createNewInstance;
      return this;
   }

   public ResourceLocationListBuilder setExpanded(boolean expanded) {
      return (ResourceLocationListBuilder)super.setExpanded(expanded);
   }

   public ResourceLocationListBuilder setSaveConsumer(Consumer<List<ResourceLocation>> saveConsumer) {
      return (ResourceLocationListBuilder)super.setSaveConsumer(saveConsumer);
   }

   public ResourceLocationListBuilder setDefaultValue(Supplier<List<ResourceLocation>> defaultValue) {
      return (ResourceLocationListBuilder)super.setDefaultValue(defaultValue);
   }

   public ResourceLocationListBuilder setDefaultValue(List<ResourceLocation> defaultValue) {
      return (ResourceLocationListBuilder)super.setDefaultValue(defaultValue);
   }

   public ResourceLocationListBuilder setTooltipSupplier(Function<List<ResourceLocation>, Optional<Component[]>> tooltipSupplier) {
      return (ResourceLocationListBuilder)super.setTooltipSupplier(tooltipSupplier);
   }

   public ResourceLocationListBuilder setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
      return (ResourceLocationListBuilder)super.setTooltipSupplier(tooltipSupplier);
   }

   public ResourceLocationListBuilder setTooltip(Optional<Component[]> tooltip) {
      return (ResourceLocationListBuilder)super.setTooltip(tooltip);
   }

   public ResourceLocationListBuilder setTooltip(Component... tooltip) {
      return (ResourceLocationListBuilder)super.setTooltip(tooltip);
   }

   public ResourceLocationListBuilder setAvailableValues(Supplier<List<ResourceLocation>> availableValues) {
      this.availableValues = availableValues;
      return this;
   }

   public ResourceLocationListBuilder removeAvailableValues() {
      this.availableValues = null;
      return this;
   }

   @NotNull
   public ResourceLocationListListEntry build() {
      ResourceLocationListListEntry entry = new ResourceLocationListListEntry(
         this.getFieldNameKey(),
         (List<ResourceLocation>)this.value,
         this.isExpanded(),
         null,
         this.getSaveConsumer(),
         this.defaultValue,
         this.getResetButtonKey(),
         this.isRequireRestart(),
         this.isDeleteButtonEnabled(),
         this.isInsertInFront()
      );
      if (this.availableValues != null) {
         entry.setPossibleValues(this.availableValues);
      }

      if (this.createNewInstance != null) {
         entry.setCreateNewInstance(this.createNewInstance);
      }

      entry.setInsertButtonEnabled(this.isInsertButtonEnabled());
      entry.setCellErrorSupplier(this.cellErrorSupplier);
      entry.setTooltipSupplier(() -> (Optional)this.getTooltipSupplier().apply(entry.getValue()));
      entry.setAddTooltip(this.getAddTooltip());
      entry.setRemoveTooltip(this.getRemoveTooltip());
      if (this.errorSupplier != null) {
         entry.setErrorSupplier(() -> (Optional)this.errorSupplier.apply(entry.getValue()));
      }

      return (ResourceLocationListListEntry)this.finishBuilding(entry);
   }
}
