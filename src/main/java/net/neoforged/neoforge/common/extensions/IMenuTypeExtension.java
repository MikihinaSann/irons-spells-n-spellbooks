package net.neoforged.neoforge.common.extensions;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.IContainerFactory;

/**
 * MenuType factory supporting the extra-data {@link IContainerFactory} contract,
 * backed by Fabric's ExtendedScreenHandlerType.
 */
public interface IMenuTypeExtension {

    static <T extends AbstractContainerMenu> MenuType<T> create(IContainerFactory<T> factory) {
        return new ExtendedScreenHandlerType<>((syncId, inventory, buf) -> factory.create(syncId, inventory, buf),
                RAW_BUFFER_CODEC);
    }

    /** Passes the raw buffer through as the "extra data" object. */
    StreamCodec<RegistryFriendlyByteBuf, RegistryFriendlyByteBuf> RAW_BUFFER_CODEC =
            StreamCodec.of((buf, value) -> buf.writeBytes(value.copy()), buf -> buf);
}
