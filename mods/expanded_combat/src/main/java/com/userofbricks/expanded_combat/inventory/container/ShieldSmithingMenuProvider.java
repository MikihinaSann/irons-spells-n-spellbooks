package com.userofbricks.expanded_combat.inventory.container;

import com.userofbricks.expanded_combat.datagen.LangStrings;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;

public record ShieldSmithingMenuProvider(BlockPos accessPos) implements MenuProvider {
   @Nonnull
   public Component getDisplayName() {
      return Component.translatable(LangStrings.SHIELD_UPGRADE_CONTAINER);
   }

   public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
      ContainerLevelAccess containerLevelAccess = ContainerLevelAccess.create(playerEntity.level(), this.accessPos);
      return new ShieldSmithingMenu(i, playerInventory, containerLevelAccess);
   }
}
