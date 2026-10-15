package net.alshanex.alshanex_familiars.registry;

import java.util.function.Supplier;
import net.alshanex.alshanex_familiars.block.ArchmageStatue;
import net.alshanex.alshanex_familiars.block.ClericStatue;
import net.alshanex.alshanex_familiars.block.DruidStatue;
import net.alshanex.alshanex_familiars.block.FamiliarStorageBlock;
import net.alshanex.alshanex_familiars.block.HunterStatue;
import net.alshanex.alshanex_familiars.block.IceSurfaceBlock;
import net.alshanex.alshanex_familiars.block.PetBedBlock;
import net.alshanex.alshanex_familiars.block.RitualAutomatorBlock;
import net.alshanex.alshanex_familiars.block.RitualCorePedestal;
import net.alshanex.alshanex_familiars.block.ShrinkingStationBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;

public class BlockRegistry {
   public static final Blocks BLOCKS = DeferredRegister.createBlocks("alshanex_familiars");
   public static final Supplier<Block> PET_BED = BLOCKS.register(
      "pet_bed", () -> new PetBedBlock(Properties.of().mapColor(MapColor.METAL).strength(0.5F, 6.0F))
   );
   public static final Supplier<Block> ARCHMAGE_STATUE = BLOCKS.register("pet_archmage_statue_model", ArchmageStatue::new);
   public static final Supplier<Block> HUNTER_STATUE = BLOCKS.register("pet_beastmaster_statue_model", HunterStatue::new);
   public static final Supplier<Block> DRUID_STATUE = BLOCKS.register("pet_druid_statue_model", DruidStatue::new);
   public static final Supplier<Block> CLERIC_STATUE = BLOCKS.register("pet_cleric_statue_model", ClericStatue::new);
   public static final Supplier<Block> RITUAL_CORE = BLOCKS.register("ritual_core", RitualCorePedestal::new);
   public static final Supplier<Block> FAMILIAR_STORAGE = BLOCKS.register(
      "familiar_storage", () -> new FamiliarStorageBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BIRCH_WOOD).noOcclusion())
   );
   public static final Supplier<Block> SHRINKING_STATION = BLOCKS.register("shrinking_station", () -> new ShrinkingStationBlock(Properties.of()));
   public static final Supplier<Block> RITUAL_AUTOMATOR = BLOCKS.register("ritual_automator", () -> new RitualAutomatorBlock(Properties.of()));
   public static final DeferredBlock<Block> ICE_SURFACE_BLOCK = BLOCKS.register(
      "ice_surface_block",
      () -> new IceSurfaceBlock(
         Properties.of()
            .mapColor(MapColor.ICE)
            .strength(0.5F)
            .sound(SoundType.GLASS)
            .friction(0.98F)
            .noOcclusion()
            .randomTicks()
            .pushReaction(PushReaction.DESTROY)
      )
   );

   public static void register(IEventBus eventBus) {
      BLOCKS.register(eventBus);
   }
}
