package net.neoforged.neoforge.internal.fabric;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;
import java.util.function.Consumer;

/**
 * Opens menus backed by {@code ExtendedScreenHandlerType}. Vanilla
 * {@code openMenu} rejects plain {@link MenuProvider}s for extended types;
 * this wraps the provider and forwards the extra-data buffer NeoForge-style.
 */
public final class FabricMenuHelper {
    private FabricMenuHelper() {
    }

    public static OptionalInt openMenu(ServerPlayer player, MenuProvider provider) {
        return openMenu(player, provider, null);
    }

    public static OptionalInt openMenu(ServerPlayer player, MenuProvider provider, @Nullable Consumer<FriendlyByteBuf> extraDataWriter) {
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), player.registryAccess());
        if (extraDataWriter != null) {
            extraDataWriter.accept(buf);
        }
        return player.openMenu(new ExtendedScreenHandlerFactory<RegistryFriendlyByteBuf>() {
            @Override
            public RegistryFriendlyByteBuf getScreenOpeningData(ServerPlayer serverPlayer) {
                return buf;
            }

            @Override
            public Component getDisplayName() {
                return provider.getDisplayName();
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player1) {
                return provider.createMenu(syncId, inventory, player1);
            }
        });
    }
}
