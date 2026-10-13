package net.neoforged.neoforge.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Extended menu factory matching NeoForge's {@code IContainerFactory}:
 * receives the extra data buffer.
 */
@FunctionalInterface
public interface IContainerFactory<T extends AbstractContainerMenu> {
    T create(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf);
}
