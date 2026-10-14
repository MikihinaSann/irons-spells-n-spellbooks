package net.neoforged.neoforge.common.extensions;

import net.neoforged.neoforge.internal.fabric.FabricMenuHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;

import java.util.OptionalInt;
import java.util.function.Consumer;

/**
 * NeoForge-style {@link Player} extensions — injected onto
 * {@code net.minecraft.world.entity.player.Player}.
 */
public interface IPlayerExtension {
    private Player self() {
        return (Player) (Object) this;
    }

    /**
     * Opens a menu with extra data written to the open packet — mirrors
     * NeoForge's {@code openMenu(MenuProvider, Consumer)}.
     */
    default OptionalInt openMenu(MenuProvider menu, Consumer<FriendlyByteBuf> extraDataWriter) {
        if (self() instanceof ServerPlayer serverPlayer) {
            return FabricMenuHelper.openMenu(serverPlayer, menu, extraDataWriter);
        }
        return OptionalInt.empty();
    }

    default OptionalInt openMenu(MenuProvider menu, BlockPos pos) {
        return openMenu(menu, buf -> buf.writeBlockPos(pos));
    }

    /**
     * NeoForge overload of {@code Player#hasCorrectToolForDrops(BlockState)} that
     * takes the harvest context.
     */
    default boolean hasCorrectToolForDrops(net.minecraft.world.level.block.state.BlockState state,
            net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) {
        return self().hasCorrectToolForDrops(state);
    }
}
