package com.userofbricks.expanded_combat.inventory.container;

import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SmithingMenu;

public record SmithingMenuProvider(BlockPos accessPos) implements MenuProvider {
   @Nonnull
   public Component getDisplayName() {
      return Component.translatable("container.upgrade");
   }

   public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
      return new SmithingMenu(i, playerInventory, ContainerLevelAccess.NULL);
   }
}
