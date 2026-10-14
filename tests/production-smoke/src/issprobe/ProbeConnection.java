package issprobe;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

/** No-op packet listener so a probe ServerPlayer survives CombatTracker's
 * damage packets without a real network connection. */
public class ProbeConnection extends ServerGamePacketListenerImpl {
    public ProbeConnection(MinecraftServer server, ServerPlayer player) {
        super(server, new Connection(PacketFlow.SERVERBOUND), player,
                CommonListenerCookie.createInitial(player.getGameProfile(), false));
    }

    @Override
    public void send(Packet<?> packet) {
    }

    @Override
    public void send(Packet<?> packet, PacketSendListener sendListener) {
    }
}
