package dev.shadowsoffire.placebo.menu;

import dev.shadowsoffire.placebo.menu.MenuUtil.PosFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.level.Level;

/**
 * Boilerplate for creating {@link MenuProvider}s when using {@link BlockEntityMenu}.
 */
public class SimplerMenuProvider<M extends AbstractContainerMenu> implements MenuProvider, net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory<BlockPos> {
    private final Component title;
    private final MenuConstructor menuConstructor;

    private final BlockPos pos;

    public SimplerMenuProvider(Level level, BlockPos pos, PosFactory<M> factory) {
        this.pos = pos;
        this.menuConstructor = (id, inv, player) -> factory.create(id, inv, pos);
        this.title = Component.translatable(level.getBlockState(pos).getBlock().getDescriptionId());
    }

    @Override
    public Component getDisplayName() {
        return this.title;
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return this.menuConstructor.createMenu(pContainerId, pInventory, pPlayer);
    }


    @Override
    public BlockPos getScreenOpeningData(net.minecraft.server.level.ServerPlayer player) {
        return pos;
    }
}
