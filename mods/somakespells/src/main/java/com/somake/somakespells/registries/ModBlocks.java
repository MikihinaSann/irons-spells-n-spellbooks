package com.somake.somakespells.registries;

import com.somake.somakespells.block.PedestalOfFireBlock;
import com.somake.somakespells.block.UpgradeForgeBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;

public class ModBlocks {
   public static final Blocks BLOCKS = DeferredRegister.createBlocks("somakespells");
   public static final DeferredHolder<Block, Block> UPGRADE_FORGE = BLOCKS.register("upgrade_forge", UpgradeForgeBlock::new);
   public static final DeferredHolder<Block, Block> PEDESTAL_OF_FIRE = BLOCKS.register("pedestal_of_fire", PedestalOfFireBlock::new);

   public static void register(IEventBus eventBus) {
      BLOCKS.register(eventBus);
   }
}
