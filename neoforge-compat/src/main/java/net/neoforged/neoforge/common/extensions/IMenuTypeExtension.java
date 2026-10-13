package net.neoforged.neoforge.common.extensions;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.IContainerFactory;

/**
 * NeoForge's IMenuTypeExtension - the Fabric equivalent of the patched
 * {@code MenuType(IContainerFactory)} ctor is an ExtendedScreenHandlerType with
 * the trailing packet data carried as raw bytes.
 */
public interface IMenuTypeExtension {

    static <T extends AbstractContainerMenu> MenuType<T> create(IContainerFactory<T> factory) {
        return new ExtendedScreenHandlerType<>(
                (syncId, inv, data) -> factory.create(syncId, inv,
                        new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(data), RegistryAccess.EMPTY)),
                ByteBufCodecs.BYTE_ARRAY);
    }
}
