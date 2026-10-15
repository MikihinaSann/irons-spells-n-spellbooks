package com.somake.somakespells.registries;

import com.somake.somakespells.block.entity.PedestalOfFireBlockEntity;
import com.somake.somakespells.block.entity.UpgradeForgeBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
   private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "somakespells");
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<UpgradeForgeBlockEntity>> UPGRADE_FORGE = BLOCK_ENTITY_TYPES.register(
      "upgrade_forge", () -> net.minecraft.world.level.block.entity.BlockEntityType.Builder.<UpgradeForgeBlockEntity>of(UpgradeForgeBlockEntity::new, new Block[]{(Block)ModBlocks.UPGRADE_FORGE.get()}).build(null)
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PedestalOfFireBlockEntity>> PEDESTAL_OF_FIRE = BLOCK_ENTITY_TYPES.register(
      "pedestal_of_fire", () -> net.minecraft.world.level.block.entity.BlockEntityType.Builder.<PedestalOfFireBlockEntity>of(PedestalOfFireBlockEntity::new, new Block[]{(Block)ModBlocks.PEDESTAL_OF_FIRE.get()}).build(null)
   );

   public static void register(IEventBus eventBus) {
      BLOCK_ENTITY_TYPES.register(eventBus);
   }
}
