package net.warphan.iss_magicfromtheeast.registries;

import java.util.Collection;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.warphan.iss_magicfromtheeast.block.MFTEDirectionalBlock;
import net.warphan.iss_magicfromtheeast.block.RiceWineVaseBlock;

public class MFTEBlockRegistries {
   private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, "iss_magicfromtheeast");
   public static final DeferredHolder<Block, Block> JADE_ORE = BLOCKS.register(
      "jade_ore", () -> new DropExperienceBlock(UniformInt.of(0, 3), Properties.ofFullCopy(Blocks.LAPIS_ORE))
   );
   public static final DeferredHolder<Block, Block> JADE_ORE_DEEPSLATE = BLOCKS.register(
      "deepslate_jade_ore", () -> new DropExperienceBlock(UniformInt.of(1, 3), Properties.ofFullCopy(Blocks.DEEPSLATE_LAPIS_ORE))
   );
   public static final DeferredHolder<Block, Block> JADE_BLOCK = BLOCKS.register(
      "jade_block",
      () -> new Block(
         Properties.of().mapColor(MapColor.COLOR_GREEN).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F)
      )
   );
   public static final DeferredHolder<Block, SlabBlock> JADE_SLAB = BLOCKS.register(
      "jade_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)JADE_BLOCK.get()))
   );
   public static final DeferredHolder<Block, StairBlock> JADE_STAIR = BLOCKS.register(
      "jade_stair", () -> new StairBlock(((Block)JADE_BLOCK.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)JADE_BLOCK.get()))
   );
   public static final DeferredHolder<Block, WallBlock> JADE_WALL = BLOCKS.register(
      "jade_wall", () -> new WallBlock(Properties.ofFullCopy((BlockBehaviour)JADE_BLOCK.get()))
   );
   public static final DeferredHolder<Block, Block> CHISELED_JADE = BLOCKS.register(
      "chiseled_jade", () -> new Block(Properties.ofFullCopy((BlockBehaviour)JADE_BLOCK.get()))
   );
   public static final DeferredHolder<Block, Block> JADE_LION_HEAD_BLOCK = BLOCKS.register(
      "jade_lion_head_block", () -> new MFTEDirectionalBlock(Properties.ofFullCopy((BlockBehaviour)JADE_BLOCK.get()))
   );
   public static final DeferredHolder<Block, Block> JADE_BRICK_BLOCK = BLOCKS.register(
      "jadestone_bricks",
      () -> new Block(
         Properties.of().mapColor(MapColor.COLOR_GREEN).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F)
      )
   );
   public static final DeferredHolder<Block, SlabBlock> JADE_BRICK_SLAB = BLOCKS.register(
      "jadestone_bricks_slab", () -> new SlabBlock(Properties.ofFullCopy((BlockBehaviour)JADE_BRICK_BLOCK.get()))
   );
   public static final DeferredHolder<Block, StairBlock> JADE_BRICK_STAIR = BLOCKS.register(
      "jadestone_bricks_stair",
      () -> new StairBlock(((Block)JADE_BRICK_BLOCK.get()).defaultBlockState(), Properties.ofFullCopy((BlockBehaviour)JADE_BRICK_BLOCK.get()))
   );
   public static final DeferredHolder<Block, WallBlock> JADE_BRICK_WALL = BLOCKS.register(
      "jadestone_bricks_wall", () -> new WallBlock(Properties.ofFullCopy((BlockBehaviour)JADE_BRICK_BLOCK.get()))
   );
   public static final DeferredHolder<Block, Block> CHISELED_JADE_BRICK = BLOCKS.register(
      "chiseled_jadestone_bricks", () -> new Block(Properties.ofFullCopy((BlockBehaviour)JADE_BRICK_BLOCK.get()))
   );
   public static final DeferredHolder<Block, Block> JADE_BRICK_PILLAR = BLOCKS.register(
      "jadestone_bricks_pillar", () -> new RotatedPillarBlock(Properties.ofFullCopy((BlockBehaviour)JADE_BRICK_BLOCK.get()))
   );
   public static final DeferredHolder<Block, Block> REFINED_JADE_BLOCK = BLOCKS.register(
      "refined_jade_block", () -> new Block(Properties.ofFullCopy(Blocks.EMERALD_BLOCK))
   );
   public static final DeferredHolder<Block, Block> RICE_WINE_VASE = BLOCKS.register("rice_wine_vase", RiceWineVaseBlock::new);

   public static void register(IEventBus eventBus) {
      BLOCKS.register(eventBus);
   }

   public static Collection<DeferredHolder<Block, ? extends Block>> blocks() {
      return BLOCKS.getEntries();
   }
}
