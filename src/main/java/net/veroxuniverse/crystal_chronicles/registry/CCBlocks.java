package net.veroxuniverse.crystal_chronicles.registry;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;
import net.veroxuniverse.crystal_chronicles.block.ArteeBaseBlock;
import net.veroxuniverse.crystal_chronicles.block.AxonBlock;
import net.veroxuniverse.crystal_chronicles.block.BismuthCrystalBlock;
import net.veroxuniverse.crystal_chronicles.block.BloodFlowerBlock;
import net.veroxuniverse.crystal_chronicles.block.BronchusBlock;
import net.veroxuniverse.crystal_chronicles.block.CCSaplingBlock;
import net.veroxuniverse.crystal_chronicles.block.CapillaryBlock;
import net.veroxuniverse.crystal_chronicles.block.CleverLightBlock;
import net.veroxuniverse.crystal_chronicles.block.CloudBlock;
import net.veroxuniverse.crystal_chronicles.block.DirectionalClusterBlock;
import net.veroxuniverse.crystal_chronicles.block.FacingBlock;
import net.veroxuniverse.crystal_chronicles.block.FleshBlock;
import net.veroxuniverse.crystal_chronicles.block.FleshVeinsBlock;
import net.veroxuniverse.crystal_chronicles.block.HangingVeinsBlock;
import net.veroxuniverse.crystal_chronicles.block.HolyBeaconBlock;
import net.veroxuniverse.crystal_chronicles.block.HolyLightBlock;
import net.veroxuniverse.crystal_chronicles.block.HolyLightSegmentBlock;
import net.veroxuniverse.crystal_chronicles.block.HolyMarblePillarBlock;
import net.veroxuniverse.crystal_chronicles.block.HorizontalCrystalBlock;
import net.veroxuniverse.crystal_chronicles.block.NeuronBlock;
import net.veroxuniverse.crystal_chronicles.block.NeuronTorchBlock;
import net.veroxuniverse.crystal_chronicles.block.SkinLayerBlock;
import net.veroxuniverse.crystal_chronicles.block.SmallBlock;
import net.veroxuniverse.crystal_chronicles.block.TallBloodFlowerBlock;
import net.veroxuniverse.crystal_chronicles.block.TallSulphurClusterBlock;
import net.veroxuniverse.crystal_chronicles.block.VeinBlock;
import net.veroxuniverse.crystal_chronicles.block.VentBaseBlock;
import net.veroxuniverse.crystal_chronicles.block.VentBlock;
import net.veroxuniverse.crystal_chronicles.block.WallNeuronTorchBlock;
import net.veroxuniverse.crystal_chronicles.worldgen.tree.CCTreeGrower;

public class CCBlocks {
   public static final Blocks BLOCKS = DeferredRegister.createBlocks("crystal_chronicles");
   public static final DeferredBlock<Block> THUNDERSTONE = registerBlock(
      "thunderstone", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE))
   );
   public static final DeferredBlock<Block> CHARGED_THUNDERSTONE = registerBlock(
      "charged_thunderstone", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE))
   );
   public static final DeferredBlock<Block> CHISELED_THUNDERSTONE = registerBlock(
      "chiseled_thunderstone", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE))
   );
   public static final DeferredBlock<Block> THUNDERSTONE_BRICKS = registerBlock(
      "thunderstone_bricks", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE))
   );
   public static final DeferredBlock<Block> POLISHED_THUNDERSTONE = registerBlock(
      "polished_thunderstone", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE))
   );
   public static final DeferredBlock<Block> THUNDERSTONE_STAIRS = registerBlock(
      "thunderstone_stairs",
      () -> new StairBlock(
            ((Block)THUNDERSTONE.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> THUNDERSTONE_SLAB = registerBlock(
      "thunderstone_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> THUNDERSTONE_WALL = registerBlock(
      "thunderstone_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> CHISELED_THUNDERSTONE_STAIRS = registerBlock(
      "chiseled_thunderstone_stairs",
      () -> new StairBlock(
            ((Block)CHISELED_THUNDERSTONE.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> CHISELED_THUNDERSTONE_SLAB = registerBlock(
      "chiseled_thunderstone_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> CHISELED_THUNDERSTONE_WALL = registerBlock(
      "chiseled_thunderstone_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> THUNDERSTONE_BRICKS_STAIRS = registerBlock(
      "thunderstone_bricks_stairs",
      () -> new StairBlock(
            ((Block)THUNDERSTONE_BRICKS.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> THUNDERSTONE_BRICKS_SLAB = registerBlock(
      "thunderstone_bricks_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> THUNDERSTONE_BRICKS_WALL = registerBlock(
      "thunderstone_bricks_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> POLISHED_THUNDERSTONE_STAIRS = registerBlock(
      "polished_thunderstone_stairs",
      () -> new StairBlock(
            ((Block)POLISHED_THUNDERSTONE.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> POLISHED_THUNDERSTONE_SLAB = registerBlock(
      "polished_thunderstone_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> POLISHED_THUNDERSTONE_WALL = registerBlock(
      "polished_thunderstone_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BISMUTH_CRYSTAL = registerBlock(
      "bismuth_crystal",
      () -> new HorizontalCrystalBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.AMETHYST_BLOCK).noOcclusion().lightLevel(state -> 6))
   );
   public static final DeferredBlock<Block> PURPLE_BISMUTH_CRYSTAL = registerBlock(
      "purple_bismuth_crystal",
      () -> new HorizontalCrystalBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.AMETHYST_BLOCK).noOcclusion().lightLevel(state -> 6))
   );
   public static final DeferredBlock<Block> RAINBOW_BISMUTH_CRYSTAL = registerBlock(
      "rainbow_bismuth_crystal",
      () -> new BismuthCrystalBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.AMETHYST_BLOCK).noOcclusion().lightLevel(state -> 6))
   );
   public static final DeferredBlock<Block> VIOLET_BISMUTH_CRYSTAL = registerBlock(
      "violet_bismuth_crystal",
      () -> new HorizontalCrystalBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.AMETHYST_BLOCK).noOcclusion().lightLevel(state -> 6))
   );
   public static final DeferredBlock<Block> YELLOW_BISMUTH_CRYSTAL = registerBlock(
      "yellow_bismuth_crystal",
      () -> new HorizontalCrystalBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.AMETHYST_BLOCK).noOcclusion().lightLevel(state -> 6))
   );
   public static final DeferredBlock<Block> BISMITE = registerBlock(
      "bismite", () -> new RotatedPillarBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.END_STONE))
   );
   public static final DeferredBlock<Block> BISMITE_BRICKS = registerBlock(
      "bismite_bricks", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.END_STONE))
   );
   public static final DeferredBlock<Block> CHISELED_BISMITE = registerBlock(
      "chiseled_bismite", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.END_STONE))
   );
   public static final DeferredBlock<Block> CRACKED_BISMITE = registerBlock(
      "cracked_bismite", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.END_STONE))
   );
   public static final DeferredBlock<Block> POLISHED_BISMITE = registerBlock(
      "polished_bismite", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.END_STONE))
   );
   public static final DeferredBlock<Block> BISMITE_STAIRS = registerBlock(
      "bismite_stairs",
      () -> new StairBlock(
            ((Block)BISMITE.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> BISMITE_SLAB = registerBlock(
      "bismite_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BISMITE_WALL = registerBlock(
      "bismite_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> CHISELED_BISMITE_STAIRS = registerBlock(
      "chiseled_bismite_stairs",
      () -> new StairBlock(
            ((Block)CHISELED_BISMITE.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> CHISELED_BISMITE_SLAB = registerBlock(
      "chiseled_bismite_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> CHISELED_BISMITE_WALL = registerBlock(
      "chiseled_bismite_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> CRACKED_BISMITE_STAIRS = registerBlock(
      "cracked_bismite_stairs",
      () -> new StairBlock(
            ((Block)CRACKED_BISMITE.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> CRACKED_BISMITE_SLAB = registerBlock(
      "cracked_bismite_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> CRACKED_BISMITE_WALL = registerBlock(
      "cracked_bismite_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> POLISHED_BISMITE_STAIRS = registerBlock(
      "polished_bismite_stairs",
      () -> new StairBlock(
            ((Block)POLISHED_BISMITE.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> POLISHED_BISMITE_SLAB = registerBlock(
      "polished_bismite_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> POLISHED_BISMITE_WALL = registerBlock(
      "polished_bismite_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BISMITE_BRICKS_STAIRS = registerBlock(
      "bismite_bricks_stairs",
      () -> new StairBlock(
            ((Block)BISMITE_BRICKS.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> BISMITE_BRICKS_SLAB = registerBlock(
      "bismite_bricks_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BISMITE_BRICKS_WALL = registerBlock(
      "bismite_bricks_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BISMUTH_BRICKS = registerBlock(
      "bismuth_bricks", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.AMETHYST_BLOCK))
   );
   public static final DeferredBlock<Block> PURPLE_BISMUTH_BRICKS = registerBlock(
      "purple_bismuth_bricks", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.AMETHYST_BLOCK))
   );
   public static final DeferredBlock<Block> RAINBOW_BISMUTH_BRICKS = registerBlock(
      "rainbow_bismuth_bricks", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.AMETHYST_BLOCK))
   );
   public static final DeferredBlock<Block> VIOLET_BISMUTH_BRICKS = registerBlock(
      "violet_bismuth_bricks", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.AMETHYST_BLOCK))
   );
   public static final DeferredBlock<Block> YELLOW_BISMUTH_BRICKS = registerBlock(
      "yellow_bismuth_bricks", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.AMETHYST_BLOCK))
   );
   public static final DeferredBlock<Block> BISMUTH_BRICKS_STAIRS = registerBlock(
      "bismuth_bricks_stairs",
      () -> new StairBlock(
            ((Block)BISMUTH_BRICKS.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops().sound(SoundType.AMETHYST)
         )
   );
   public static final DeferredBlock<Block> BISMUTH_BRICKS_SLAB = registerBlock(
      "bismuth_bricks_slab",
      () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops().sound(SoundType.AMETHYST))
   );
   public static final DeferredBlock<Block> BISMUTH_BRICKS_WALL = registerBlock(
      "bismuth_bricks_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops().sound(SoundType.AMETHYST))
   );
   public static final DeferredBlock<Block> PURPLE_BISMUTH_BRICKS_STAIRS = registerBlock(
      "purple_bismuth_bricks_stairs",
      () -> new StairBlock(
            ((Block)PURPLE_BISMUTH_BRICKS.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops().sound(SoundType.AMETHYST)
         )
   );
   public static final DeferredBlock<Block> PURPLE_BISMUTH_BRICKS_SLAB = registerBlock(
      "purple_bismuth_bricks_slab",
      () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops().sound(SoundType.AMETHYST))
   );
   public static final DeferredBlock<Block> PURPLE_BISMUTH_BRICKS_WALL = registerBlock(
      "purple_bismuth_bricks_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops().sound(SoundType.AMETHYST))
   );
   public static final DeferredBlock<Block> YELLOW_BISMUTH_BRICKS_STAIRS = registerBlock(
      "yellow_bismuth_bricks_stairs",
      () -> new StairBlock(
            ((Block)YELLOW_BISMUTH_BRICKS.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops().sound(SoundType.AMETHYST)
         )
   );
   public static final DeferredBlock<Block> YELLOW_BISMUTH_BRICKS_SLAB = registerBlock(
      "yellow_bismuth_bricks_slab",
      () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops().sound(SoundType.AMETHYST))
   );
   public static final DeferredBlock<Block> YELLOW_BISMUTH_BRICKS_WALL = registerBlock(
      "yellow_bismuth_bricks_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops().sound(SoundType.AMETHYST))
   );
   public static final DeferredBlock<Block> RAINBOW_BISMUTH_BRICKS_STAIRS = registerBlock(
      "rainbow_bismuth_bricks_stairs",
      () -> new StairBlock(
            ((Block)RAINBOW_BISMUTH_BRICKS.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops().sound(SoundType.AMETHYST)
         )
   );
   public static final DeferredBlock<Block> RAINBOW_BISMUTH_BRICKS_SLAB = registerBlock(
      "rainbow_bismuth_bricks_slab",
      () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops().sound(SoundType.AMETHYST))
   );
   public static final DeferredBlock<Block> RAINBOW_BISMUTH_BRICKS_WALL = registerBlock(
      "rainbow_bismuth_bricks_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops().sound(SoundType.AMETHYST))
   );
   public static final DeferredBlock<Block> VIOLET_BISMUTH_BRICKS_STAIRS = registerBlock(
      "violet_bismuth_bricks_stairs",
      () -> new StairBlock(
            ((Block)VIOLET_BISMUTH_BRICKS.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops().sound(SoundType.AMETHYST)
         )
   );
   public static final DeferredBlock<Block> VIOLET_BISMUTH_BRICKS_SLAB = registerBlock(
      "violet_bismuth_bricks_slab",
      () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops().sound(SoundType.AMETHYST))
   );
   public static final DeferredBlock<Block> VIOLET_BISMUTH_BRICKS_WALL = registerBlock(
      "violet_bismuth_bricks_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops().sound(SoundType.AMETHYST))
   );
   public static final DeferredBlock<Block> CHISELED_BISMUTH = registerBlock(
      "chiseled_bismuth", () -> new FacingBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.AMETHYST_BLOCK))
   );
   public static final DeferredBlock<Block> PURPLE_CHISELED_BISMUTH = registerBlock(
      "purple_chiseled_bismuth", () -> new FacingBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.AMETHYST_BLOCK))
   );
   public static final DeferredBlock<Block> VIOLET_CHISELED_BISMUTH = registerBlock(
      "violet_chiseled_bismuth", () -> new FacingBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.AMETHYST_BLOCK))
   );
   public static final DeferredBlock<Block> YELLOW_CHISELED_BISMUTH = registerBlock(
      "yellow_chiseled_bismuth", () -> new FacingBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.AMETHYST_BLOCK))
   );
   public static final DeferredBlock<Block> CHISELED_BISMUTH_STAIRS = registerBlock(
      "chiseled_bismuth_stairs",
      () -> new StairBlock(
            ((Block)CHISELED_BISMUTH.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops().sound(SoundType.AMETHYST)
         )
   );
   public static final DeferredBlock<Block> CHISELED_BISMUTH_SLAB = registerBlock(
      "chiseled_bismuth_slab",
      () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops().sound(SoundType.AMETHYST))
   );
   public static final DeferredBlock<Block> CHISELED_BISMUTH_WALL = registerBlock(
      "chiseled_bismuth_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops().sound(SoundType.AMETHYST))
   );
   public static final DeferredBlock<Block> PURPLE_CHISELED_BISMUTH_STAIRS = registerBlock(
      "purple_chiseled_bismuth_stairs",
      () -> new StairBlock(
            ((Block)PURPLE_CHISELED_BISMUTH.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops().sound(SoundType.AMETHYST)
         )
   );
   public static final DeferredBlock<Block> PURPLE_CHISELED_BISMUTH_SLAB = registerBlock(
      "purple_chiseled_bismuth_slab",
      () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops().sound(SoundType.AMETHYST))
   );
   public static final DeferredBlock<Block> PURPLE_CHISELED_BISMUTH_WALL = registerBlock(
      "purple_chiseled_bismuth_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops().sound(SoundType.AMETHYST))
   );
   public static final DeferredBlock<Block> YELLOW_CHISELED_BISMUTH_STAIRS = registerBlock(
      "yellow_chiseled_bismuth_stairs",
      () -> new StairBlock(
            ((Block)YELLOW_CHISELED_BISMUTH.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops().sound(SoundType.AMETHYST)
         )
   );
   public static final DeferredBlock<Block> YELLOW_CHISELED_BISMUTH_SLAB = registerBlock(
      "yellow_chiseled_bismuth_slab",
      () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops().sound(SoundType.AMETHYST))
   );
   public static final DeferredBlock<Block> YELLOW_CHISELED_BISMUTH_WALL = registerBlock(
      "yellow_chiseled_bismuth_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops().sound(SoundType.AMETHYST))
   );
   public static final DeferredBlock<Block> VIOLET_CHISELED_BISMUTH_STAIRS = registerBlock(
      "violet_chiseled_bismuth_stairs",
      () -> new StairBlock(
            ((Block)VIOLET_CHISELED_BISMUTH.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops().sound(SoundType.AMETHYST)
         )
   );
   public static final DeferredBlock<Block> VIOLET_CHISELED_BISMUTH_SLAB = registerBlock(
      "violet_chiseled_bismuth_slab",
      () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops().sound(SoundType.AMETHYST))
   );
   public static final DeferredBlock<Block> VIOLET_CHISELED_BISMUTH_WALL = registerBlock(
      "violet_chiseled_bismuth_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops().sound(SoundType.AMETHYST))
   );
   public static final DeferredBlock<Block> BLOOD_BASES = registerBlock(
      "blood_bases", () -> new BloodFlowerBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SHORT_GRASS))
   );
   public static final DeferredBlock<Block> TALL_BLOOD_BASES = registerBlock(
      "tall_blood_bases", () -> new TallBloodFlowerBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.TALL_GRASS))
   );
   public static final DeferredBlock<Block> ALVEOLUS_BLOCK = registerBlock(
      "alveolus_block", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MUD))
   );
   public static final DeferredBlock<Block> FLESH_BLOCK = registerBlock(
      "flesh_block", () -> new FleshBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MUD))
   );
   public static final DeferredBlock<Block> MUSCLE_BLOCK = registerBlock(
      "muscle_block", () -> new RotatedPillarBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MUD))
   );
   public static final DeferredBlock<Block> TENDON_BLOCK = registerBlock(
      "tendon_block", () -> new RotatedPillarBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MUD))
   );
   public static final DeferredBlock<Block> FAT_TISSUE_BLOCK = registerBlock(
      "fat_tissue_block", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MUD))
   );
   public static final DeferredBlock<Block> ARTREE_BASE = registerBlock(
      "artree_base", () -> new ArteeBaseBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.COBBLESTONE_WALL).noOcclusion())
   );
   public static final DeferredBlock<Block> ARTREE_VEIN = registerBlock(
      "artree_vein", () -> new VeinBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.COBBLESTONE_WALL).noOcclusion())
   );
   public static final DeferredBlock<Block> ARTREE_CAPILLARY = registerBlock(
      "artree_capillary", () -> new CapillaryBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.COBBLESTONE_WALL).noOcclusion())
   );
   public static final DeferredBlock<Block> NEURON_BLOCK = registerBlock(
      "neuron_block",
      () -> new NeuronBlock(
            Properties.of()
               .lightLevel(litBlockEmission(15))
               .strength(0.3F)
               .sound(SoundType.FROGLIGHT)
               .isValidSpawn(net.minecraft.world.level.block.Blocks::always)
               .noOcclusion()
               .randomTicks()
         )
   );
   public static final DeferredBlock<Block> AXON = registerBlock(
      "axon",
      () -> new AxonBlock(
            Properties.of()
               .lightLevel(litBlockEmission(15))
               .strength(0.3F)
               .sound(SoundType.FROGLIGHT)
               .isValidSpawn(net.minecraft.world.level.block.Blocks::always)
               .noOcclusion()
               .randomTicks()
         )
   );
   public static final DeferredBlock<Block> ROTTEN_FLESH_BLOCK = registerBlock(
      "rotten_flesh_block", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MUD))
   );
   public static final DeferredBlock<Block> BRONCHUS = registerBlock(
      "bronchus", () -> new BronchusBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_PLANKS).requiresCorrectToolForDrops().noOcclusion())
   );
   public static final DeferredBlock<Block> BRONCHUS_PLANKS = registerBlock(
      "bronchus_planks", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_PLANKS).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BRONCHUS_STAIRS = registerBlock(
      "bronchus_stairs",
      () -> new StairBlock(
            ((Block)BRONCHUS_PLANKS.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> BRONCHUS_SLAB = registerBlock(
      "bronchus_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BRONCHUS_FENCE = registerBlock(
      "bronchus_fence", () -> new FenceBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_FENCE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BRONCHUS_FENCE_GATE = registerBlock(
      "bronchus_fence_gate",
      () -> new FenceGateBlock(WoodType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_FENCE_GATE).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BRONCHUS_BUTTON = registerBlock(
      "bronchus_button", () -> new ButtonBlock(BlockSetType.OAK, 10, Properties.of().strength(4.0F).requiresCorrectToolForDrops().noCollission())
   );
   public static final DeferredBlock<Block> BRONCHUS_PRESSURE_PLATE = registerBlock(
      "bronchus_pressure_plate",
      () -> new PressurePlateBlock(
            BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_PRESSURE_PLATE).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> BRONCHUS_DOOR = registerBlock(
      "bronchus_door",
      () -> new DoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_DOOR).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> BRONCHUS_TRAPDOOR = registerBlock(
      "bronchus_trapdoor",
      () -> new TrapDoorBlock(BlockSetType.OAK, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OAK_TRAPDOOR).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> NEURON_TORCH = registerBlockWithoutItem(
      "neuron_torch",
      () -> new NeuronTorchBlock(
            ParticleTypes.CRIT, Properties.of().lightLevel(state -> 14).strength(0.3F).sound(SoundType.FROGLIGHT).noOcclusion().noCollission()
         )
   );
   public static final DeferredBlock<Block> NEURON_TORCH_WALL = registerBlockWithoutItem(
      "neuron_torch_wall",
      () -> new WallNeuronTorchBlock(
            ParticleTypes.CRIT,
            Properties.of().lightLevel(state -> 14).strength(0.3F).sound(SoundType.FROGLIGHT).noOcclusion().noCollission().dropsLike(NEURON_TORCH.get())
         )
   );
   public static final DeferredBlock<Block> EYE_BLOCK = registerBlock(
      "eye_block", () -> new FacingBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MUD))
   );
   public static final DeferredBlock<Block> HANGING_VEINS = registerBlock(
      "hanging_veins", () -> new HangingVeinsBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MUD))
   );
   public static final DeferredBlock<Block> CRUSTONE = registerBlock(
      "crustone", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SANDSTONE))
   );
   public static final DeferredBlock<Block> CRUSTONE_STAIRS = registerBlock(
      "crustone_stairs",
      () -> new StairBlock(
            ((Block)CRUSTONE.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> CRUSTONE_SLAB = registerBlock(
      "crustone_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> CRUSTONE_WALL = registerBlock(
      "crustone_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> CRUSTONE_BRICKS = registerBlock(
      "crustone_bricks", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SANDSTONE))
   );
   public static final DeferredBlock<Block> CRUSTONE_BRICKS_STAIRS = registerBlock(
      "crustone_bricks_stairs",
      () -> new StairBlock(
            ((Block)CRUSTONE_BRICKS.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> CRUSTONE_BRICKS_SLAB = registerBlock(
      "crustone_bricks_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> CRUSTONE_BRICKS_WALL = registerBlock(
      "crustone_bricks_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> CRACKED_CRUSTONE_BRICKS = registerBlock(
      "cracked_crustone_bricks", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SANDSTONE))
   );
   public static final DeferredBlock<Block> CRACKED_CRUSTONE_BRICKS_STAIRS = registerBlock(
      "cracked_crustone_bricks_stairs",
      () -> new StairBlock(
            ((Block)CRACKED_CRUSTONE_BRICKS.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> CRACKED_CRUSTONE_BRICKS_SLAB = registerBlock(
      "cracked_crustone_bricks_slab",
      () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> CRACKED_CRUSTONE_BRICKS_WALL = registerBlock(
      "cracked_crustone_bricks_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> POLISHED_CRUSTONE = registerBlock(
      "polished_crustone", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SANDSTONE))
   );
   public static final DeferredBlock<Block> POLISHED_CRUSTONE_STAIRS = registerBlock(
      "polished_crustone_stairs",
      () -> new StairBlock(
            ((Block)POLISHED_CRUSTONE.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> POLISHED_CRUSTONE_SLAB = registerBlock(
      "polished_crustone_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> POLISHED_CRUSTONE_WALL = registerBlock(
      "polished_crustone_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> CHISELED_CRUSTONE = registerBlock(
      "chiseled_crustone", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SANDSTONE))
   );
   public static final DeferredBlock<Block> CHISELED_CRUSTONE_STAIRS = registerBlock(
      "chiseled_crustone_stairs",
      () -> new StairBlock(
            ((Block)CHISELED_CRUSTONE.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> CHISELED_CRUSTONE_SLAB = registerBlock(
      "chiseled_crustone_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> CHISELED_CRUSTONE_WALL = registerBlock(
      "chiseled_crustone_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> ALVEOLUS = registerBlock(
      "alveolus",
      () -> new CCSaplingBlock(
            CCTreeGrower.BRONCHUS, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRIMSON_FUNGUS).randomTicks(), (Block)FLESH_BLOCK.get()
         )
   );
   public static final DeferredBlock<Block> SKIN_LAYER = registerBlock(
      "skin_layer", () -> new SkinLayerBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MUD).randomTicks())
   );
   public static final DeferredBlock<Block> CELVER_LIGHT = registerBlock(
      "clever_light",
      () -> new CleverLightBlock(
            Properties.of()
               .lightLevel(state -> state.getValue(CleverLightBlock.LIT) ? 15 : 0)
               .strength(0.3F)
               .sound(SoundType.FROGLIGHT)
               .isValidSpawn(net.minecraft.world.level.block.Blocks::always)
               .noOcclusion()
         )
   );
   public static final DeferredBlock<Block> HEMALITE_BLOCK = registerBlock(
      "hemalite_block", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.AMETHYST_BLOCK))
   );
   public static final DeferredBlock<Block> VEINS = registerBlock(
      "veins", () -> new FleshVeinsBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.VINE).randomTicks())
   );
   public static final DeferredBlock<Block> CLOUD_BLOCK = registerBlock(
      "cloud_block", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SNOW_BLOCK))
   );
   public static final DeferredBlock<Block> CLOUD_LAYER = registerBlock(
      "cloud_layer", () -> new CloudBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SNOW_BLOCK).noOcclusion().noLootTable().noCollission())
   );
   public static final DeferredBlock<Block> DENSE_CLOUDS = registerBlock(
      "dense_cloud", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SNOW_BLOCK))
   );
   public static final DeferredBlock<Block> HOLY_MARBLE = registerBlock(
      "holy_marble", () -> new RotatedPillarBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE))
   );
   public static final DeferredBlock<Block> CHISELED_HOLY_MARBLE = registerBlock(
      "chiseled_holy_marble", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE))
   );
   public static final DeferredBlock<Block> CRACKED_HOLY_MARBLE = registerBlock(
      "cracked_holy_marble", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE))
   );
   public static final DeferredBlock<Block> POLISHED_HOLY_MARBLE = registerBlock(
      "polished_holy_marble", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE))
   );
   public static final DeferredBlock<Block> HOLY_MARBLE_BRICKS = registerBlock(
      "holy_marble_bricks", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE))
   );
   public static final DeferredBlock<Block> HOLY_MARBLE_PILLAR = registerBlock(
      "holy_marble_pillar", () -> new HolyMarblePillarBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE).noOcclusion())
   );
   public static final DeferredBlock<Block> GOLDSTONE = registerBlock(
      "goldstone", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.RAW_GOLD_BLOCK))
   );
   public static final DeferredBlock<Block> HOLY_LIGHT_BLOCK = registerBlock(
      "holy_light_block", () -> new HolyLightBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.GLOWSTONE).noOcclusion().randomTicks())
   );
   public static final DeferredBlock<Block> HOLY_LIGHT_1 = registerBlock(
      "holy_light_1",
      () -> new HolyLightSegmentBlock(
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SNOW_BLOCK)
               .noLootTable()
               .noOcclusion()
               .noCollission()
               .lightLevel(state -> 14)
               .strength(-1.0F, 3600000.0F)
               .randomTicks()
         )
   );
   public static final DeferredBlock<Block> HOLY_LIGHT_2 = registerBlock(
      "holy_light_2",
      () -> new HolyLightSegmentBlock(
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SNOW_BLOCK)
               .noLootTable()
               .noOcclusion()
               .noCollission()
               .lightLevel(state -> 14)
               .strength(-1.0F, 3600000.0F)
               .randomTicks()
         )
   );
   public static final DeferredBlock<Block> HOLY_LIGHT_3 = registerBlock(
      "holy_light_3",
      () -> new HolyLightSegmentBlock(
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SNOW_BLOCK)
               .noLootTable()
               .noOcclusion()
               .noCollission()
               .lightLevel(state -> 14)
               .strength(-1.0F, 3600000.0F)
               .randomTicks()
         )
   );
   public static final DeferredBlock<Block> HOLY_LIGHT_4 = registerBlock(
      "holy_light_4",
      () -> new HolyLightSegmentBlock(
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SNOW_BLOCK)
               .noLootTable()
               .noOcclusion()
               .noCollission()
               .lightLevel(state -> 14)
               .strength(-1.0F, 3600000.0F)
               .randomTicks()
         )
   );
   public static final DeferredBlock<Block> HOLY_BEACON = registerBlock(
      "holy_beacon", () -> new HolyBeaconBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.GLOWSTONE).noOcclusion())
   );
   public static final DeferredBlock<Block> HOLY_MARBLE_STAIRS = registerBlock(
      "holy_marble_stairs",
      () -> new StairBlock(
            ((Block)HOLY_MARBLE.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> HOLY_MARBLE_SLAB = registerBlock(
      "holy_marble_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> HOLY_MARBLE_WALL = registerBlock(
      "holy_marble_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> HOLY_MARBLE_BRICKS_STAIRS = registerBlock(
      "holy_marble_bricks_stairs",
      () -> new StairBlock(
            ((Block)HOLY_MARBLE_BRICKS.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> HOLY_MARBLE_BRICKS_SLAB = registerBlock(
      "holy_marble_bricks_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> HOLY_MARBLE_BRICKS_WALL = registerBlock(
      "holy_marble_bricks_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> CHISELED_HOLY_MARBLE_STAIRS = registerBlock(
      "chiseled_holy_marble_stairs",
      () -> new StairBlock(
            ((Block)CHISELED_HOLY_MARBLE.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> CHISELED_HOLY_MARBLE_SLAB = registerBlock(
      "chiseled_holy_marble_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> CHISELED_HOLY_MARBLE_WALL = registerBlock(
      "chiseled_holy_marble_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> CRACKED_HOLY_MARBLE_STAIRS = registerBlock(
      "cracked_holy_marble_stairs",
      () -> new StairBlock(
            ((Block)CRACKED_HOLY_MARBLE.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> CRACKED_HOLY_MARBLE_SLAB = registerBlock(
      "cracked_holy_marble_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> CRACKED_HOLY_MARBLE_WALL = registerBlock(
      "cracked_holy_marble_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> POLISHED_HOLY_MARBLE_STAIRS = registerBlock(
      "polished_holy_marble_stairs",
      () -> new StairBlock(
            ((Block)POLISHED_HOLY_MARBLE.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> POLISHED_HOLY_MARBLE_SLAB = registerBlock(
      "polished_holy_marble_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> POLISHED_HOLY_MARBLE_WALL = registerBlock(
      "polished_holy_marble_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> GOLDSTONE_STAIRS = registerBlock(
      "goldstone_stairs",
      () -> new StairBlock(
            ((Block)GOLDSTONE.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> GOLDSTONE_SLAB = registerBlock(
      "goldstone_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> GOLDSTONE_WALL = registerBlock(
      "goldstone_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> VOLCANITE_BLOCK = registerBlock(
      "volcanite_block", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.AMETHYST_BLOCK))
   );
   public static final DeferredBlock<Block> PYRITE_BLOCK = registerBlock(
      "pyrite_block", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE))
   );
   public static final DeferredBlock<Block> PUMICE = registerBlock(
      "pumice", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE))
   );
   public static final DeferredBlock<Block> PUMICE_BRICKS = registerBlock(
      "pumice_bricks", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE))
   );
   public static final DeferredBlock<Block> CHISELED_PUMICE = registerBlock(
      "chiseled_pumice", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE))
   );
   public static final DeferredBlock<Block> CRACKED_PUMICE = registerBlock(
      "cracked_pumice", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE))
   );
   public static final DeferredBlock<Block> POLISHED_PUMICE = registerBlock(
      "polished_pumice", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE))
   );
   public static final DeferredBlock<Block> GREEN_SULPHUR_POOL = registerBlock(
      "green_sulphur_pool", () -> new RotatedPillarBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE))
   );
   public static final DeferredBlock<Block> ORANGE_SULPHUR_POOL = registerBlock(
      "orange_sulphur_pool", () -> new RotatedPillarBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE))
   );
   public static final DeferredBlock<Block> RED_SULPHUR_POOL = registerBlock(
      "red_sulphur_pool", () -> new RotatedPillarBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE))
   );
   public static final DeferredBlock<Block> YELLOW_SULPHUR_POOL = registerBlock(
      "yellow_sulphur_pool", () -> new RotatedPillarBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE))
   );
   public static final DeferredBlock<Block> SMALL_SULPHUR_CLUSTER = registerBlock(
      "small_sulphur_cluster",
      () -> new DirectionalClusterBlock(7.0F, 3.0F, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.AMETHYST_CLUSTER).noOcclusion())
   );
   public static final DeferredBlock<Block> MEDIUM_SULPHUR_CLUSTER = registerBlock(
      "medium_sulphur_cluster",
      () -> new DirectionalClusterBlock(7.0F, 3.0F, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.AMETHYST_CLUSTER).noOcclusion())
   );
   public static final DeferredBlock<Block> TALL_SULPHUR_CLUSTER = registerBlock(
      "tall_sulphur_cluster", () -> new TallSulphurClusterBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.AMETHYST_CLUSTER).noOcclusion())
   );
   public static final DeferredBlock<Block> SULPHUR_CRYSTAL = registerBlock(
      "sulphur_crystal", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.AMETHYST_BLOCK))
   );
   public static final DeferredBlock<Block> SULPHUR_DUST = registerBlock(
      "sulphur_dust", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SNOW_BLOCK))
   );
   public static final DeferredBlock<Block> SULPHUR_DUST_LAYER = registerBlock(
      "sulphur_dust_layer", () -> new CarpetBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.SNOW_BLOCK).noOcclusion())
   );
   public static final DeferredBlock<Block> SULPHURIC_SOIL = registerBlock(
      "sulphuric_soil", () -> new Block(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.MUD))
   );
   public static final DeferredBlock<Block> VERMILLION_SULPHUR_POOL = registerBlock(
      "vermillion_sulphur_pool", () -> new RotatedPillarBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE))
   );
   public static final DeferredBlock<Block> RED_SULPHUR_WATER_VENT_BASE = registerBlock(
      "red_sulphur_water_vent_base", () -> new VentBaseBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE).noOcclusion())
   );
   public static final DeferredBlock<Block> RED_SULPHUR_WATER_VENT_TOP = registerBlock(
      "red_sulphur_water_vent_top", () -> new VentBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE).noOcclusion().randomTicks())
   );
   public static final DeferredBlock<Block> SULPHUR_WATER_VENT_BASE = registerBlock(
      "sulphur_water_vent_base", () -> new VentBaseBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE).noOcclusion())
   );
   public static final DeferredBlock<Block> SULPHUR_WATER_VENT_TOP = registerBlock(
      "sulphur_water_vent_top", () -> new VentBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE).noOcclusion().randomTicks())
   );
   public static final DeferredBlock<Block> SMALL_PYRITE_BLOCK = registerBlock(
      "small_pyrite_block", () -> new SmallBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE).noOcclusion().dynamicShape())
   );
   public static final DeferredBlock<Block> PUMICE_STAIRS = registerBlock(
      "pumice_stairs",
      () -> new StairBlock(
            ((Block)PUMICE.get()).defaultBlockState(), Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> PUMICE_SLAB = registerBlock(
      "pumice_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> PUMICE_WALL = registerBlock(
      "pumice_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> PUMICE_BRICKS_STAIRS = registerBlock(
      "pumice_bricks_stairs",
      () -> new StairBlock(
            ((Block)PUMICE_BRICKS.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> PUMICE_BRICKS_SLAB = registerBlock(
      "pumice_bricks_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> PUMICE_BRICKS_WALL = registerBlock(
      "pumice_bricks_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> CHISELED_PUMICE_STAIRS = registerBlock(
      "chiseled_pumice_stairs",
      () -> new StairBlock(
            ((Block)CHISELED_PUMICE.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> CHISELED_PUMICE_SLAB = registerBlock(
      "chiseled_pumice_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> CHISELED_PUMICE_WALL = registerBlock(
      "chiseled_pumice_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> CRACKED_PUMICE_STAIRS = registerBlock(
      "cracked_pumice_stairs",
      () -> new StairBlock(
            ((Block)CRACKED_PUMICE.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> CRACKED_PUMICE_SLAB = registerBlock(
      "cracked_pumice_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> CRACKED_PUMICE_WALL = registerBlock(
      "cracked_pumice_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> POLISHED_PUMICE_STAIRS = registerBlock(
      "polished_pumice_stairs",
      () -> new StairBlock(
            ((Block)POLISHED_PUMICE.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> POLISHED_PUMICE_SLAB = registerBlock(
      "polished_pumice_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> POLISHED_PUMICE_WALL = registerBlock(
      "polished_pumice_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> PYRITE_STAIRS = registerBlock(
      "pyrite_block_stairs",
      () -> new StairBlock(
            ((Block)PYRITE_BLOCK.get()).defaultBlockState(),
            Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_STAIRS).requiresCorrectToolForDrops()
         )
   );
   public static final DeferredBlock<Block> PYRITE_SLAB = registerBlock(
      "pyrite_block_slab", () -> new SlabBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE_SLAB).requiresCorrectToolForDrops())
   );
   public static final DeferredBlock<Block> PYRITE_WALL = registerBlock(
      "pyrite_block_wall", () -> new WallBlock(Properties.of().strength(4.0F).requiresCorrectToolForDrops())
   );

   private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
      DeferredBlock<T> toReturn = BLOCKS.register(name, block);
      registerBlockItem(name, toReturn);
      return toReturn;
   }

   private static <T extends Block> DeferredBlock<T> registerBlockWithoutItem(String name, Supplier<T> block) {
      return BLOCKS.register(name, block);
   }

   private static ToIntFunction<BlockState> litBlockEmission(int pLightValue) {
      return state -> state.getValue(BlockStateProperties.LIT) ? pLightValue : 0;
   }

   private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
      CCItems.ITEMS.register(name, () -> new BlockItem((Block)block.get(), new net.minecraft.world.item.Item.Properties()));
   }

   public static void register(IEventBus eventBus) {
      BLOCKS.register(eventBus);
   }
}
