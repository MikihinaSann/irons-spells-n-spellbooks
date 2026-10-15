package net.alshanex.alshanex_familiars.registry;

import java.util.function.Supplier;
import net.alshanex.alshanex_familiars.block.entity.ArchmageStatueEntity;
import net.alshanex.alshanex_familiars.block.entity.ClericStatueEntity;
import net.alshanex.alshanex_familiars.block.entity.DruidStatueEntity;
import net.alshanex.alshanex_familiars.block.entity.FamiliarStorageBlockEntity;
import net.alshanex.alshanex_familiars.block.entity.HunterStatueEntity;
import net.alshanex.alshanex_familiars.block.entity.PetBedBlockEntity;
import net.alshanex.alshanex_familiars.block.entity.RitualAutomatorBlockEntity;
import net.alshanex.alshanex_familiars.block.entity.RitualCorePedestalEntity;
import net.alshanex.alshanex_familiars.block.entity.ShrinkingStationBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockEntityRegistry {
   public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "alshanex_familiars");
   public static final Supplier<BlockEntityType<PetBedBlockEntity>> PET_BED = TILES.register(
      "pet_bed", () -> Builder.<PetBedBlockEntity>of(PetBedBlockEntity::new, new Block[]{BlockRegistry.PET_BED.get()}).build(null)
   );
   public static final Supplier<BlockEntityType<ArchmageStatueEntity>> ARCHMAGE_STATUE = TILES.register(
      "pet_archmage_statue_model", () -> Builder.<ArchmageStatueEntity>of(ArchmageStatueEntity::new, new Block[]{BlockRegistry.ARCHMAGE_STATUE.get()}).build(null)
   );
   public static final Supplier<BlockEntityType<HunterStatueEntity>> HUNTER_STATUE = TILES.register(
      "pet_beastmaster_statue_model", () -> Builder.<HunterStatueEntity>of(HunterStatueEntity::new, new Block[]{BlockRegistry.HUNTER_STATUE.get()}).build(null)
   );
   public static final Supplier<BlockEntityType<DruidStatueEntity>> DRUID_STATUE = TILES.register(
      "pet_druid_statue_model", () -> Builder.<DruidStatueEntity>of(DruidStatueEntity::new, new Block[]{BlockRegistry.DRUID_STATUE.get()}).build(null)
   );
   public static final Supplier<BlockEntityType<ClericStatueEntity>> CLERIC_STATUE = TILES.register(
      "pet_cleric_statue_model", () -> Builder.<ClericStatueEntity>of(ClericStatueEntity::new, new Block[]{BlockRegistry.CLERIC_STATUE.get()}).build(null)
   );
   public static final Supplier<BlockEntityType<RitualCorePedestalEntity>> RITUAL_CORE = TILES.register(
      "ritual_core", () -> Builder.<RitualCorePedestalEntity>of(RitualCorePedestalEntity::new, new Block[]{BlockRegistry.RITUAL_CORE.get()}).build(null)
   );
   public static final Supplier<BlockEntityType<FamiliarStorageBlockEntity>> FAMILIAR_STORAGE = TILES.register(
      "familiar_storage", () -> Builder.<FamiliarStorageBlockEntity>of(FamiliarStorageBlockEntity::new, new Block[]{BlockRegistry.FAMILIAR_STORAGE.get()}).build(null)
   );
   public static final Supplier<BlockEntityType<ShrinkingStationBlockEntity>> SHRINKING_STATION_BE = TILES.register(
      "shrinking_station", () -> Builder.<ShrinkingStationBlockEntity>of(ShrinkingStationBlockEntity::new, new Block[]{BlockRegistry.SHRINKING_STATION.get()}).build(null)
   );
   public static final Supplier<BlockEntityType<RitualAutomatorBlockEntity>> RITUAL_AUTOMATOR = TILES.register(
      "ritual_automator", () -> Builder.<RitualAutomatorBlockEntity>of(RitualAutomatorBlockEntity::new, new Block[]{BlockRegistry.RITUAL_AUTOMATOR.get()}).build(null)
   );
}
