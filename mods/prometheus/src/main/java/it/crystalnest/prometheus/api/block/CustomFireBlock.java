package it.crystalnest.prometheus.api.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.crystalnest.prometheus.api.Fire;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.type.FireTyped;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;

public class CustomFireBlock extends BaseFireBlock implements FireTyped {
   public static final int MAX_AGE = 15;
   public static final IntegerProperty AGE = FireBlock.AGE;
   public static final MapCodec<CustomFireBlock> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("fire_type").forGetter(block -> block.fireType),
            TagKey.codec(Registries.BLOCK).fieldOf("base").forGetter(block -> block.base),
            propertiesCodec()
         )
         .apply(instance, CustomFireBlock::new)
   );
   private final ResourceLocation fireType;
   private final TagKey<Block> base;

   public CustomFireBlock(ResourceLocation fireType, TagKey<Block> base, Properties properties) {
      this(fireType, base, true, properties);
   }

   public CustomFireBlock(ResourceLocation fireType, TagKey<Block> base, boolean addDefaultProperties, Properties properties) {
      super(
         (addDefaultProperties ? addDefaultProperties(properties) : properties).lightLevel(FireManager.lightLevel(fireType)),
         FireManager.getProperty(fireType, Fire::getDamage)
      );
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
      this.fireType = fireType;
      this.base = base;
   }

   private static Properties addDefaultProperties(Properties properties) {
      return properties.replaceable().noCollission().instabreak().sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{AGE});
   }

   @NotNull
   public BlockState updateShape(
      @NotNull BlockState state,
      @NotNull Direction direction,
      @NotNull BlockState state2,
      @NotNull LevelAccessor level,
      @NotNull BlockPos pos,
      @NotNull BlockPos pos2
   ) {
      return this.canSurvive(state, level, pos) ? this.getStateWithAge(level, pos, (Integer)state.getValue(AGE)) : Blocks.AIR.defaultBlockState();
   }

   public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
      BlockPos below = pos.below();
      return this.canSurvive(level.getBlockState(below)) && level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
   }

   public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource rand) {
      super.tick(state, level, pos, rand);
      this.scheduleTick(level, pos);
      if (level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
         int age = (Integer)state.getValue(AGE);
         if (state.canSurvive(level, pos)
            && (
               !FireManager.getProperty(this.getFireType(), Fire::canRainDouse)
                  || level.getBlockState(pos.below()).is(level.dimensionType().infiniburn())
                  || !level.isRaining()
                  || !level.isRainingAt(pos)
                  || !(rand.nextFloat() < 0.2 + age * 0.03)
            )) {
            if (age < 15 && rand.nextInt(2) == 0) {
               level.setBlock(pos, (BlockState)state.setValue(AGE, age + 1), 4);
            }
         } else {
            level.removeBlock(pos, false);
         }
      }
   }

   @NotNull
   public MapCodec<? extends BaseFireBlock> codec() {
      return CODEC;
   }

   protected boolean canBurn(@NotNull BlockState state) {
      return true;
   }

   public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState initial, boolean updateFlags) {
      super.onPlace(state, level, pos, initial, updateFlags);
      this.scheduleTick(level, pos);
   }

   @Override
   public ResourceLocation getFireType() {
      return this.fireType;
   }

   public boolean canSurvive(BlockState state) {
      return state.is(this.base);
   }

   protected BlockState getStateWithAge(LevelAccessor level, BlockPos pos, int age) {
      BlockState state = getState(level, pos);
      return state.hasProperty(AGE) ? (BlockState)state.setValue(AGE, age) : state;
   }

   protected void scheduleTick(Level level, BlockPos pos) {
      level.scheduleTick(pos, this, 30 + level.random.nextInt(10));
   }
}
