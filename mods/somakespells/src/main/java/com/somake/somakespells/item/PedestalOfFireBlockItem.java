package com.somake.somakespells.item;

import com.somake.somakespells.client.renderer.item.PedestalOfFireItemRenderer;
import java.util.function.Consumer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class PedestalOfFireBlockItem extends BlockItem {
   public PedestalOfFireBlockItem(Block block, Properties properties) {
      super(block, properties);
   }

   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      consumer.accept(new IClientItemExtensions() {
         private BlockEntityWithoutLevelRenderer renderer;

         public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            if (this.renderer == null) {
               this.renderer = new PedestalOfFireItemRenderer();
            }

            return this.renderer;
         }
      });
   }
}
