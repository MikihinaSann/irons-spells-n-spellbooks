package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.neoforged.neoforge.common.extensions.ICommonPacketListener;
import net.neoforged.neoforge.network.connection.ConnectionType;
import org.spongepowered.asm.mixin.Mixin;

/**
 * ICommonPacketListener members NeoForge adds to server listeners.
 * {@code connection} lives on the parent ServerCommonPacketListenerImpl -
 * widened by neoforge_compat.accesswidener.
 */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class CommonPacketListenerChannelMixin implements ICommonPacketListener {

    @Override
    public Connection getConnection() {
        return ((ServerCommonPacketListenerImpl) (Object) this).connection;
    }

    @Override
    public ConnectionProtocol protocol() {
        return ConnectionProtocol.PLAY;
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.OTHER;
    }
}
