package net.neoforged.neoforge.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import java.util.stream.Stream;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.Nullable;

public class OnDatapackSyncEvent extends Event {
    private final PlayerList playerList;
    @Nullable
    private final ServerPlayer player;

    public OnDatapackSyncEvent(PlayerList playerList, @Nullable ServerPlayer player) {
        this.playerList = playerList;
        this.player = player;
    }

    @Nullable
    public ServerPlayer getPlayer() {
        return player;
    }

    public PlayerList getPlayerList() {
        return playerList;
    }

    /**
     * Stream of players that should receive datapack sync — the joined player, or all players
     * when triggered by a reload. Matches NeoForge's OnDatapackSyncEvent semantics.
     */
    public Stream<ServerPlayer> getRelevantPlayers() {
        return player == null ? playerList.getPlayers().stream() : Stream.of(player);
    }
}
