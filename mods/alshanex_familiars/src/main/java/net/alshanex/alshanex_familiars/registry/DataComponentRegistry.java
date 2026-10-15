package net.alshanex.alshanex_familiars.registry;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentType.Builder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DataComponentRegistry {
   public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(
      Registries.DATA_COMPONENT_TYPE, "alshanex_familiars"
   );
   public static final Supplier<DataComponentType<CompoundTag>> PET_BED_COLOR = register(
      "pet_bed_color", CompoundTag::new, op -> op.persistent(CompoundTag.CODEC)
   );

   private static <T> DataComponentRegistry.ComponentSupplier<T> register(String name, Supplier<T> defaultVal, UnaryOperator<Builder<T>> op) {
      DeferredHolder<DataComponentType<?>, DataComponentType<T>> registered = DATA_COMPONENT_TYPES.register(
         name, () -> op.apply(DataComponentType.builder()).build()
      );
      return new DataComponentRegistry.ComponentSupplier<>(registered, defaultVal);
   }

   public static void register(IEventBus eventBus) {
      DATA_COMPONENT_TYPES.register(eventBus);
   }

   public static class ComponentSupplier<T> implements Supplier<DataComponentType<T>> {
      private final Supplier<DataComponentType<T>> type;
      private final Supplier<T> defaultSupplier;

      public ComponentSupplier(Supplier<DataComponentType<T>> type, Supplier<T> defaultSupplier) {
         this.type = type;
         this.defaultSupplier = Suppliers.memoize(defaultSupplier::get);
      }

      public T get(ItemStack stack) {
         return (T)stack.getOrDefault(this.type, this.defaultSupplier.get());
      }

      public DataComponentType<T> get() {
         return this.type.get();
      }
   }
}
