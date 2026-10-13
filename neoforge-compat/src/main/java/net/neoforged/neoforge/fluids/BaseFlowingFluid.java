package net.neoforged.neoforge.fluids;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

/**
 * NeoForge's BaseFlowingFluid — a vanilla {@link FlowingFluid} carrying a
 * {@link FluidType}.
 */
public abstract class BaseFlowingFluid extends FlowingFluid implements FluidTypeCarrier {
    private final Supplier<? extends FluidType> fluidType;
    private final Supplier<? extends Fluid> still;
    private final Supplier<? extends Fluid> flowing;
    private final Supplier<? extends Item> bucket;
    private final Supplier<? extends Block> block;
    private final int slopeFindDistance;
    private final int levelDecreasePerBlock;
    private final float explosionResistance;
    private final int tickRate;

    public BaseFlowingFluid(Properties properties) {
        this.fluidType = properties.fluidType;
        this.still = properties.still;
        this.flowing = properties.flowing;
        this.bucket = properties.bucket;
        this.block = properties.block;
        this.slopeFindDistance = properties.slopeFindDistance;
        this.levelDecreasePerBlock = properties.levelDecreasePerBlock;
        this.explosionResistance = properties.explosionResistance;
        this.tickRate = properties.tickRate;
    }

    @Override
    public FluidType getFluidType() {
        return fluidType.get();
    }

    @Override
    public Item getBucket() {
        return bucket.get();
    }

    @Override
    protected float getExplosionResistance() {
        return explosionResistance;
    }

    @Override
    public int getSlopeFindDistance(net.minecraft.world.level.LevelReader level) {
        return slopeFindDistance;
    }

    @Override
    public int getDropOff(net.minecraft.world.level.LevelReader level) {
        return levelDecreasePerBlock;
    }

    @Override
    public int getTickDelay(net.minecraft.world.level.LevelReader level) {
        return tickRate;
    }

    @Override
    protected void beforeDestroyingBlock(net.minecraft.world.level.LevelAccessor level, net.minecraft.core.BlockPos pos,
                                         net.minecraft.world.level.block.state.BlockState state) {
        net.minecraft.world.level.block.entity.BlockEntity blockEntity =
                state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        Block.dropResources(state, level, pos, blockEntity);
    }

    @Override
    protected boolean canConvertToSource(net.minecraft.world.level.Level level) {
        return false;
    }

    @Override
    protected net.minecraft.world.level.block.state.BlockState createLegacyBlock(net.minecraft.world.level.material.FluidState state) {
        Block liquidBlock = block.get();
        if (liquidBlock instanceof LiquidBlock) {
            return liquidBlock.defaultBlockState().setValue(LiquidBlock.LEVEL, state.getValue(LEVEL));
        }
        return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
    }

    @Override
    protected boolean canBeReplacedWith(net.minecraft.world.level.material.FluidState state, net.minecraft.world.level.BlockGetter level, net.minecraft.core.BlockPos pos, net.minecraft.world.level.material.Fluid fluid, net.minecraft.core.Direction direction) {
        return false;
    }

    protected Supplier<? extends Fluid> still() {
        return still;
    }

    protected Supplier<? extends Fluid> flowing() {
        return flowing;
    }

    public static class Source extends BaseFlowingFluid {
        public Source(Properties properties) {
            super(properties);
        }

        @Override
        public boolean isSource(net.minecraft.world.level.material.FluidState state) {
            return true;
        }

        @Override
        public int getAmount(net.minecraft.world.level.material.FluidState state) {
            return 8;
        }

        @Override
        public Fluid getSource() {
            return this;
        }

        @Override
        public Fluid getFlowing() {
            return this.flowing().get();
        }
    }

    public static class Flowing extends BaseFlowingFluid {
        public Flowing(Properties properties) {
            super(properties);
            registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        }

        @Override
        protected void createFluidStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Fluid, net.minecraft.world.level.material.FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public boolean isSource(net.minecraft.world.level.material.FluidState state) {
            return false;
        }

        @Override
        public int getAmount(net.minecraft.world.level.material.FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public Fluid getSource() {
            return this.still().get();
        }

        @Override
        public Fluid getFlowing() {
            return this;
        }
    }

    public static class Properties {
        private final Supplier<? extends FluidType> fluidType;
        private final Supplier<? extends Fluid> still;
        private final Supplier<? extends Fluid> flowing;
        private Supplier<? extends Item> bucket = () -> null;
        private Supplier<? extends Block> block = () -> null;
        private int slopeFindDistance = 4;
        private int levelDecreasePerBlock = 1;
        private float explosionResistance = 1.0F;
        private int tickRate = 5;

        public Properties(Supplier<? extends FluidType> fluidType, Supplier<? extends Fluid> still,
                          Supplier<? extends Fluid> flowing) {
            this.fluidType = fluidType;
            this.still = still;
            this.flowing = flowing;
        }

        public Properties bucket(Supplier<? extends Item> bucket) {
            this.bucket = bucket;
            return this;
        }

        public Properties block(Supplier<? extends LiquidBlock> block) {
            this.block = block;
            return this;
        }

        public Properties slopeFindDistance(int slopeFindDistance) {
            this.slopeFindDistance = slopeFindDistance;
            return this;
        }

        public Properties levelDecreasePerBlock(int levelDecreasePerBlock) {
            this.levelDecreasePerBlock = levelDecreasePerBlock;
            return this;
        }

        public Properties explosionResistance(float explosionResistance) {
            this.explosionResistance = explosionResistance;
            return this;
        }

        public Properties tickRate(int tickRate) {
            this.tickRate = tickRate;
            return this;
        }
    }
}
