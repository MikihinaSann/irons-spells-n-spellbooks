package com.userofbricks.expanded_combat.init;

import com.userofbricks.expanded_combat.block.AbstractGasBlock;
import com.userofbricks.expanded_combat.block.PurifiedGasBlock;
import com.userofbricks.expanded_combat.block.WeaponDisplayBlock;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;

@ParametersAreNonnullByDefault
public class ECBlocks {
   public static final Blocks BLOCKS = DeferredRegister.createBlocks("expanded_combat");
   public static final DeferredBlock<PurifiedGasBlock> PURIFIED_GAS_BLOCK = BLOCKS.registerBlock("purified_gas", PurifiedGasBlock::new, () -> Properties.of());
   public static final DeferredBlock<? extends AbstractGasBlock> GAS_BLOCK = BLOCKS.registerBlock(
      "gas",
      properties -> new AbstractGasBlock(properties, () -> ParticleTypes.MYCELIUM) {
         @Override
         public boolean blockCatalistItemConversions(
            BlockState thisState, BlockState catalistState, ServerLevel world, BlockPos thisPos, BlockPos catalistPos, RandomSource random
         ) {
            return false;
         }
      },
      () -> Properties.of()
   );
   public static final DeferredBlock<WeaponDisplayBlock> HEART_STEALER_DISPLAY = BLOCKS.registerBlock(
      "heartstealer_display", WeaponDisplayBlock::new, () -> Properties.of().strength(20.0F, 2000.0F)
   );
   public static final DeferredItem<BlockItem> HEART_STEALER_DISPLAY_ITEM = ECItems.ITEMS
      .register("heartstealer_display", () -> new BlockItem(HEART_STEALER_DISPLAY.get(), new net.minecraft.world.item.Item.Properties()));

   public static void registerDataGen() {
   }

   public static void register() {
   }
}
