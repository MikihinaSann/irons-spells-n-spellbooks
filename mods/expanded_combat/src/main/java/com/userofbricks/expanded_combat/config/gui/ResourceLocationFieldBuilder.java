package com.userofbricks.expanded_combat.config.gui;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ResourceLocationFieldBuilder extends AbstractFieldBuilder<ResourceLocation, ResourceLocationListEntry, ResourceLocationFieldBuilder> {
   private Supplier<List<ResourceLocation>> availableValues = null;

   public ResourceLocationFieldBuilder(Component resetButtonKey, Component fieldNameKey, ResourceLocation value) {
      super(resetButtonKey, fieldNameKey);
      Objects.requireNonNull(value);
      this.value = value;
   }

   public ResourceLocationFieldBuilder setErrorSupplier(Function<ResourceLocation, Optional<Component>> errorSupplier) {
      return (ResourceLocationFieldBuilder)super.setErrorSupplier(errorSupplier);
   }

   public ResourceLocationFieldBuilder requireRestart() {
      return (ResourceLocationFieldBuilder)super.requireRestart();
   }

   public ResourceLocationFieldBuilder setSaveConsumer(Consumer<ResourceLocation> saveConsumer) {
      return (ResourceLocationFieldBuilder)super.setSaveConsumer(saveConsumer);
   }

   public ResourceLocationFieldBuilder setDefaultValue(Supplier<ResourceLocation> defaultValue) {
      return (ResourceLocationFieldBuilder)super.setDefaultValue(defaultValue);
   }

   public ResourceLocationFieldBuilder setDefaultValue(ResourceLocation defaultValue) {
      return (ResourceLocationFieldBuilder)super.setDefaultValue(defaultValue);
   }

   public ResourceLocationFieldBuilder setTooltipSupplier(Function<ResourceLocation, Optional<Component[]>> tooltipSupplier) {
      return (ResourceLocationFieldBuilder)super.setTooltipSupplier(tooltipSupplier);
   }

   public ResourceLocationFieldBuilder setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
      return (ResourceLocationFieldBuilder)super.setTooltipSupplier(tooltipSupplier);
   }

   public ResourceLocationFieldBuilder setTooltip(Optional<Component[]> tooltip) {
      return (ResourceLocationFieldBuilder)super.setTooltip(tooltip);
   }

   public ResourceLocationFieldBuilder setTooltip(Component... tooltip) {
      return (ResourceLocationFieldBuilder)super.setTooltip(tooltip);
   }

   public ResourceLocationFieldBuilder setAvailableValues(Supplier<List<ResourceLocation>> availableValues) {
      this.availableValues = availableValues;
      return this;
   }

   public ResourceLocationFieldBuilder removeAvailableValues() {
      this.availableValues = null;
      return this;
   }

   @NotNull
   public ResourceLocationListEntry build() {
      ResourceLocationListEntry entry = new ResourceLocationListEntry(
         this.getFieldNameKey(),
         (ResourceLocation)this.value,
         this.getResetButtonKey(),
         this.defaultValue,
         this.getSaveConsumer(),
         null,
         this.isRequireRestart()
      );
      if (this.availableValues != null) {
         entry.setAvailableValues(this.availableValues);
      }

      entry.setTooltipSupplier(() -> (Optional)this.getTooltipSupplier().apply(entry.getValue()));
      if (this.errorSupplier != null) {
         entry.setErrorSupplier(() -> (Optional)this.errorSupplier.apply(entry.getValue()));
      }

      return (ResourceLocationListEntry)this.finishBuilding(entry);
   }
}
