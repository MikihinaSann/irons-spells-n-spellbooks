package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.neoforged.neoforge.common.extensions.ICommonPacketListener;
import net.neoforged.neoforge.network.connection.ConnectionType;
import org.spongepowered.asm.mixin.Mixin;

/**
 * ICommonPacketListener members NeoForge adds to the client listener.
 * {@code connection} lives on the parent ClientCommonPacketListenerImpl —
 * widened by neoforge_compat.accesswidener.
 */
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerChannelMixin implements ICommonPacketListener {

    @Override
    public Connection getConnection() {
        return ((ClientCommonPacketListenerImpl) (Object) this).connection;
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
