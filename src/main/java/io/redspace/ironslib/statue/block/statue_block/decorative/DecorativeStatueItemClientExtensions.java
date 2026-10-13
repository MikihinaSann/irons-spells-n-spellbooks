package io.redspace.ironslib.statue.block.statue_block.decorative;

import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironslib.util.MemoizedSupplier;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

public class DecorativeStatueItemClientExtensions implements IClientItemExtensions {
   private final MemoizedSupplier<BlockEntityWithoutLevelRenderer> renderer;

   public DecorativeStatueItemClientExtensions(Supplier<Block> block, Supplier<BlockEntityType<DecorativeStatueBlockEntity>> type) {
      this.renderer = new MemoizedSupplier<>(
         () -> new BlockEntityWithoutLevelRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()) {
               public void renderByItem(
                  @NotNull ItemStack stack,
                  @NotNull ItemDisplayContext displayContext,
                  @NotNull PoseStack poseStack,
                  @NotNull MultiBufferSource buffer,
                  int packedLight,
                  int packedOverlay
               ) {
                  Minecraft.getInstance()
                     .getBlockEntityRenderDispatcher()
                     .renderItem(
                        (DecorativeStatueBlockEntity)DecorativeStatueBlockEntity.from(type).create(BlockPos.ZERO, block.get().defaultBlockState()),
                        poseStack,
                        buffer,
                        packedLight,
                        packedOverlay
                     );
               }
            }
      );
   }

   @NotNull
   public BlockEntityWithoutLevelRenderer getCustomRenderer() {
      return this.renderer.get();
   }
}
