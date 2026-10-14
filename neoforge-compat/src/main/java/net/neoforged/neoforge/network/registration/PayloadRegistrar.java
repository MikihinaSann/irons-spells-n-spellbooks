package net.neoforged.neoforge.network.registration;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.MainThreadPayloadHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * NeoForge-style payload registrar; drains registrations into Fabric's
 * networking during platform init.
 */
public class PayloadRegistrar {
    private final String namespace;
    private String version = "1";
    private boolean optional = false;

    private final List<Registration<?>> registrations = new ArrayList<>();

    public PayloadRegistrar(String namespace) {
        this.namespace = namespace;
    }

    public PayloadRegistrar versioned(String version) {
        this.version = version;
        return this;
    }

    public PayloadRegistrar optional() {
        this.optional = true;
        return this;
    }

    public <T extends CustomPacketPayload> PayloadRegistrar playToClient(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            IPayloadHandler<T> handler) {
        registrations.add(new Registration<>(type, codec, handler, Direction.TO_CLIENT, false));
        return this;
    }

    public <T extends CustomPacketPayload> PayloadRegistrar playToServer(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            IPayloadHandler<T> handler) {
        registrations.add(new Registration<>(type, codec, handler, Direction.TO_SERVER, false));
        return this;
    }

    public <T extends CustomPacketPayload> PayloadRegistrar playBidirectional(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            IPayloadHandler<T> handler) {
        registrations.add(new Registration<>(type, codec, handler, Direction.BOTH, false));
        return this;
    }

    public <T extends CustomPacketPayload> PayloadRegistrar configurationToClient(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            IPayloadHandler<T> handler) {
        registrations.add(new Registration<>(type, codec, handler, Direction.TO_CLIENT, true));
        return this;
    }

    public List<Registration<?>> getRegistrations() {
        return registrations;
    }

    public String getNamespace() {
        return namespace;
    }

    enum Direction {
        TO_CLIENT, TO_SERVER, BOTH
    }

    public record Registration<T extends CustomPacketPayload>(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            IPayloadHandler<T> handler,
            Direction direction,
            boolean configuration) {

        @SuppressWarnings("unchecked")
        public void register() {
            var type = this.type;
            var codec = this.codec;
            IPayloadHandler<T> handler = this.handler;
            if (direction == Direction.TO_CLIENT || direction == Direction.BOTH) {
                PayloadTypeRegistry.playS2C().register(type, codec);
            }
            if (direction == Direction.TO_SERVER || direction == Direction.BOTH) {
                PayloadTypeRegistry.playC2S().register(type, codec);
                ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
                    IPayloadContext ctx = new IPayloadContext() {
                        @Override
                        public java.util.concurrent.CompletableFuture<Void> enqueueWork(Runnable runnable) {
                            java.util.concurrent.CompletableFuture<Void> future = new java.util.concurrent.CompletableFuture<>();
                            context.server().execute(() -> {
                                try {
                                    runnable.run();
                                    future.complete(null);
                                } catch (Throwable t) {
                                    future.completeExceptionally(t);
                                }
                            });
                            return future;
                        }

                        @Override
                        public net.minecraft.world.entity.player.Player player() {
                            return context.player();
                        }

                        @Override
                        public net.minecraft.world.level.Level level() {
                            return context.player().level();
                        }

                        @Override
                        public net.minecraft.network.protocol.PacketFlow flow() {
                            return net.minecraft.network.protocol.PacketFlow.SERVERBOUND;
                        }

                        @Override
                        public net.minecraft.network.Connection connection() {
                            return context.player().connection.connection;
                        }
                    };
                    handler.handle(payload, ctx);
                });
            }
            if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType()
                    == net.fabricmc.api.EnvType.CLIENT) {
                registerClient();
            }
        }

        /** Client-side registration (call only on the client). */
        @SuppressWarnings("unchecked")
        public void registerClient() {
            if (direction == Direction.TO_CLIENT || direction == Direction.BOTH) {
                ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
                    IPayloadContext ctx = new IPayloadContext() {
                        @Override
                        public java.util.concurrent.CompletableFuture<Void> enqueueWork(Runnable runnable) {
                            java.util.concurrent.CompletableFuture<Void> future = new java.util.concurrent.CompletableFuture<>();
                            context.client().execute(() -> {
                                try {
                                    runnable.run();
                                    future.complete(null);
                                } catch (Throwable t) {
                                    future.completeExceptionally(t);
                                }
                            });
                            return future;
                        }

                        @Override
                        public net.minecraft.world.entity.player.Player player() {
                            return context.player();
                        }

                        @Override
                        public net.minecraft.world.level.Level level() {
                            return context.player().level();
                        }

                        @Override
                        public net.minecraft.network.protocol.PacketFlow flow() {
                            return net.minecraft.network.protocol.PacketFlow.CLIENTBOUND;
                        }

                        @Override
                        public net.minecraft.network.Connection connection() {
                            var listener = net.minecraft.client.Minecraft.getInstance().getConnection();
                            return listener == null ? null : listener.getConnection();
                        }
                    };
                    handler.handle(payload, ctx);
                });
            }
        }
    }
}
