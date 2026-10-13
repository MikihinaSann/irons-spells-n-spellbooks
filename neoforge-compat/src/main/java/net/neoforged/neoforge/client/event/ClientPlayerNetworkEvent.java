package net.neoforged.neoforge.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.Nullable;

public abstract class ClientPlayerNetworkEvent extends Event {

    /** The player is logging into a server (client side). */
    public static class LoggingIn extends ClientPlayerNetworkEvent {
        private final MultiPlayerGameMode multiplayerGameMode;
        private final LocalPlayer player;
        private final ClientPacketListener connection;

        public LoggingIn(MultiPlayerGameMode multiplayerGameMode, LocalPlayer player, ClientPacketListener connection) {
            this.multiplayerGameMode = multiplayerGameMode;
            this.player = player;
            this.connection = connection;
        }

        @Nullable
        public MultiPlayerGameMode getMultiPlayerGameMode() {
            return multiplayerGameMode;
        }

        @Nullable
        public LocalPlayer getPlayer() {
            return player;
        }

        @Nullable
        public ClientPacketListener getConnection() {
            return connection;
        }
    }

    /** The player is logging out of a server. */
    public static class LoggingOut extends ClientPlayerNetworkEvent {
        private final MultiPlayerGameMode multiplayerGameMode;
        private final LocalPlayer player;
        private final ClientPacketListener connection;

        public LoggingOut(MultiPlayerGameMode multiplayerGameMode, LocalPlayer player, ClientPacketListener connection) {
            this.multiplayerGameMode = multiplayerGameMode;
            this.player = player;
            this.connection = connection;
        }

        @Nullable
        public MultiPlayerGameMode getMultiPlayerGameMode() {
            return multiplayerGameMode;
        }

        @Nullable
        public LocalPlayer getPlayer() {
            return player;
        }

        @Nullable
        public ClientPacketListener getConnection() {
            return connection;
        }
    }

    /** The player entity was recreated (respawn/dimension change). */
    public static class Clone extends ClientPlayerNetworkEvent {
        private final LocalPlayer player;
        private final LocalPlayer newPlayer;
        private final ClientPacketListener connection;

        public Clone(MultiPlayerGameMode mode, LocalPlayer player, LocalPlayer newPlayer, ClientPacketListener connection) {
            this.player = player;
            this.newPlayer = newPlayer;
            this.connection = connection;
        }

        @Nullable
        public LocalPlayer getPlayer() {
            return player;
        }

        @Nullable
        public LocalPlayer getNewPlayer() {
            return newPlayer;
        }

        @Nullable
        public ClientPacketListener getConnection() {
            return connection;
        }
    }
}
