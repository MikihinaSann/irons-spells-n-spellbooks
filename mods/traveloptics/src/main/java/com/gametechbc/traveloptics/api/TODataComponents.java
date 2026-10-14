package com.gametechbc.traveloptics.api;

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

public class TODataComponents {
   public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "traveloptics");
   public static final Supplier<DataComponentType<CompoundTag>> ECHO_CURIO_COMPONENT = register(
      "echo_curio_component", CompoundTag::new, op -> op.persistent(CompoundTag.CODEC)
   );
   public static final Supplier<DataComponentType<CompoundTag>> PHANTOM_RAGE_COMPONENT = register(
      "phantom_rage_component", CompoundTag::new, op -> op.persistent(CompoundTag.CODEC)
   );
   public static final Supplier<DataComponentType<CompoundTag>> SPIRIT_POINTS_COMPONENT = register(
      "spirit_points_component", CompoundTag::new, op -> op.persistent(CompoundTag.CODEC)
   );
   public static final Supplier<DataComponentType<CompoundTag>> PLASMA_CORE_COMPONENT = register(
      "plasma_core_component", CompoundTag::new, op -> op.persistent(CompoundTag.CODEC)
   );
   public static final Supplier<DataComponentType<CompoundTag>> WILD_ATTRIBUTE_COMPONENT = register(
      "wild_attribute_component", CompoundTag::new, op -> op.persistent(CompoundTag.CODEC)
   );
   public static final Supplier<DataComponentType<CompoundTag>> WILD_ATTRIBUTE_CHECK_COMPONENT = register(
      "wild_attribute_check_component", CompoundTag::new, op -> op.persistent(CompoundTag.CODEC)
   );
   public static final Supplier<DataComponentType<CompoundTag>> WEAPON_SWING_COUNTER = register(
      "weapon_swing_counter_component", CompoundTag::new, op -> op.persistent(CompoundTag.CODEC)
   );
   public static final Supplier<DataComponentType<CompoundTag>> GECKOLIB_ANIMATION_COMPONENT = register(
      "geckolib_animation_component", CompoundTag::new, op -> op.persistent(CompoundTag.CODEC)
   );

   public TODataComponents() {
      super();
   }

   private static <T> TODataComponents.ComponentSupplier<T> register(String name, Supplier<T> defaultVal, UnaryOperator<Builder<T>> op) {
      DeferredHolder<DataComponentType<?>, DataComponentType<T>> registered = DATA_COMPONENT_TYPES.register(
         name, () -> op.apply(DataComponentType.builder()).build()
      );
      return new TODataComponents.ComponentSupplier<>(registered, defaultVal);
   }

   public static void register(IEventBus eventBus) {
      DATA_COMPONENT_TYPES.register(eventBus);
   }

   public static class ComponentSupplier<T> implements Supplier<DataComponentType<T>> {
      private final Supplier<DataComponentType<T>> type;
      private final Supplier<T> defaultSupplier;

      public ComponentSupplier(Supplier<DataComponentType<T>> type, Supplier<T> defaultSupplier) {
         super();
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
