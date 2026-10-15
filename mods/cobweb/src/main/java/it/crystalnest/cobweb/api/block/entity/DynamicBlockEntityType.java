package it.crystalnest.cobweb.api.block.entity;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class DynamicBlockEntityType<T extends BlockEntity> extends BlockEntityType<T> {
   private final Function<BlockState, Boolean> validator;

   public DynamicBlockEntityType(BlockEntitySupplier<? extends T> supplier, Function<BlockState, Boolean> validator) {
      super(supplier, Set.of(), null);
      this.validator = validator;
   }

   public static <E extends BlockEntity> DynamicBlockEntityType<E> of(
      BiFunction<BlockPos, BlockState, ? extends E> supplier, Function<BlockState, Boolean> validator
   ) {
      return new DynamicBlockEntityType<>(supplier::apply, validator);
   }

   public boolean isValid(@NotNull BlockState state) {
      return this.validator.apply(state);
   }
}
