package io.redspace.ironslib.statue.block.statue_block.decorative;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironslib.statue.block.statue_block.AbstractStatueBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DecorativeStatueBlock extends AbstractStatueBlock {
   protected final Holder<BlockEntityType<?>> type;
   public static final Codec<Holder<BlockEntityType<?>>> TYPE_CODEC = BuiltInRegistries.BLOCK_ENTITY_TYPE.holderByNameCodec();
   public static final MapCodec<DecorativeStatueBlock> CODEC = RecordCodecBuilder.mapCodec(
      builder -> builder.group(
               Codec.INT.fieldOf("xSize").forGetter(block -> block.xSize),
               Codec.INT.fieldOf("ySize").forGetter(block -> block.ySize),
               Codec.INT.fieldOf("zSize").forGetter(block -> block.zSize),
               TYPE_CODEC.fieldOf("type").forGetter(block -> block.type)
            )
            .apply(builder, DecorativeStatueBlock::new)
   );

   public DecorativeStatueBlock(int xSize, int ySize, int zSize, Holder<BlockEntityType<? extends BlockEntity>> type) {
      super(xSize, ySize, zSize);
      this.type = type;
   }

   @NotNull
   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   @Nullable
   public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
      return new DecorativeStatueBlockEntity((BlockEntityType<?>)(Object) this.type.value(), pos, state);
   }
}
