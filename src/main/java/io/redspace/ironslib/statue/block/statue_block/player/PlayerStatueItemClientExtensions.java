package io.redspace.ironslib.statue.block.statue_block.player;

import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironslib.statue.StatueData;
import io.redspace.ironslib.util.MemoizedSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

public class PlayerStatueItemClientExtensions implements IClientItemExtensions {
   MemoizedSupplier<BlockEntityWithoutLevelRenderer> renderer = new MemoizedSupplier<>(
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
                  .renderItem(PlayerStatueBlockEntity.renderable(StatueData.get(stack)), poseStack, buffer, packedLight, packedOverlay);
            }
         }
   );

   @NotNull
   public BlockEntityWithoutLevelRenderer getCustomRenderer() {
      return this.renderer.get();
   }
}
