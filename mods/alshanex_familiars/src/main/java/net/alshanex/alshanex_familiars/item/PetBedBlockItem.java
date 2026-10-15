package net.alshanex.alshanex_familiars.item;

import net.alshanex.alshanex_familiars.block.PetBedBlock;
import net.alshanex.alshanex_familiars.registry.DataComponentRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PetBedBlockItem extends BlockItem {
   public PetBedBlockItem(Block block, Properties properties) {
      super(block, properties);
   }

   protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
      ItemStack itemStack = context.getItemInHand();
      if (itemStack.has(DataComponentRegistry.PET_BED_COLOR)) {
         String colorName = ((CompoundTag)itemStack.get(DataComponentRegistry.PET_BED_COLOR)).getString("color");
         DyeColor color = DyeColor.byName(colorName, DyeColor.WHITE);
         if (state.hasProperty(PetBedBlock.COLOR)) {
            state = (BlockState)state.setValue(PetBedBlock.COLOR, color);
         }
      }

      return super.placeBlock(context, state);
   }

   public static ItemStack withColor(ItemStack stack, DyeColor color) {
      CompoundTag colorTag = new CompoundTag();
      colorTag.putString("color", color.getName());
      stack.set(DataComponentRegistry.PET_BED_COLOR, colorTag);
      return stack;
   }
}
