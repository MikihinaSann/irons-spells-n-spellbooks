package net.neoforged.neoforge.attachment;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Stand-in for NeoForge's {@code AttachmentType}, backed by Fabric's attachment
 * system. The underlying Fabric type is materialized when registered via
 * {@link #bindFabric(ResourceLocation)}.
 */
public class AttachmentType<T> {
    private final java.util.function.Function<IAttachmentHolder, T> initializer;
    @Nullable
    private Codec<T> codec;
    @Nullable
    private Predicate<T> persistenceCondition;
    @Nullable
    private StreamCodec<? super RegistryFriendlyByteBuf, T> syncCodec;
    @Nullable
    private AttachmentSyncPredicate syncPredicate;
    private boolean copyOnDeath;
    private ResourceLocation id;
    @Nullable
    private net.fabricmc.fabric.api.attachment.v1.AttachmentType<T> fabric;

    private AttachmentType(java.util.function.Function<IAttachmentHolder, T> initializer) {
        this.initializer = initializer;
    }

    public static <T> Builder<T> builder(Supplier<T> initializer) {
        return new Builder<>(holder -> initializer.get());
    }

    /** NeoForge's holder-aware builder overload (Function shape). */
    /** NeoForge's convenience: builder with serialization wired to the default codec. */
    public static <T> Builder<T> serializable(Supplier<T> initializer) {
        return builder(initializer);
    }

    public static <T> Builder<T> builder(java.util.function.Function<IAttachmentHolder, T> initializer) {
        return new Builder<>(initializer);
    }

    /** Functional shape NeoForge uses for holder-aware initializers. */
    @FunctionalInterface
    public interface IAttachmentHolderFactory<T> {
        T create(IAttachmentHolder holder);
    }

    /** Creates the initial value for a holder (used by {@link IAttachmentHolder#getData}). */
    public T createInitialValue(IAttachmentHolder holder) {
        return initializer.apply(holder);
    }

    /** Binds this type to a registry id and creates the backing Fabric attachment type. */
    public void bindFabric(ResourceLocation id) {
        this.id = id;
        this.fabric = AttachmentRegistry.<T>create(id, builder -> {
            builder.initializer(() -> initializer.apply(null));
            if (codec != null) {
                builder.persistent(codec);
            }
            if (syncCodec != null) {
                builder.syncWith(syncCodec, syncPredicate != null ? syncPredicate : AttachmentSyncPredicate.all());
            }
            if (copyOnDeath) {
                builder.copyOnDeath();
            }
        });
    }

    public ResourceLocation getId() {
        return id;
    }

    @Nullable
    public net.fabricmc.fabric.api.attachment.v1.AttachmentType<T> fabric() {
        return fabric;
    }

    public java.util.function.Function<IAttachmentHolder, T> getInitializer() {
        return initializer;
    }

    public static class Builder<T> {
        private final java.util.function.Function<IAttachmentHolder, T> initializer;
        private Codec<? extends T> codec;
        private Predicate<T> persistenceCondition;
        private IAttachmentSerializer<?, T> serializer;
        private StreamCodec<? super RegistryFriendlyByteBuf, T> syncCodec;
        private AttachmentSyncPredicate syncPredicate = AttachmentSyncPredicate.all();
        private boolean copyOnDeath;

        Builder(java.util.function.Function<IAttachmentHolder, T> initializer) {
            this.initializer = initializer;
        }

        public Builder<T> serialize(Codec<T> codec) {
            this.codec = codec;
            return this;
        }

        public Builder<T> serialize(Codec<T> codec, Predicate<T> persistenceCondition) {
            this.codec = codec;
            this.persistenceCondition = persistenceCondition;
            return this;
        }

        public <C> Builder<T> serialize(Codec<C> codec, java.util.function.Function<T, C> encoder, java.util.function.Function<C, T> decoder) {
            this.codec = codec.xmap(decoder, encoder);
            return this;
        }

        public <C> Builder<T> serialize(Codec<C> codec, java.util.function.Function<T, C> encoder, java.util.function.Function<C, T> decoder, Predicate<T> persistenceCondition) {
            this.codec = codec.xmap(decoder, encoder);
            this.persistenceCondition = persistenceCondition;
            return this;
        }

        @SuppressWarnings("unchecked")
        public Builder<T> serialize(IAttachmentSerializer<?, T> serializer) {
            this.serializer = serializer;
            if (serializer instanceof CodecSerializer<?> cs) {
                this.codec = (Codec<T>) cs.codec();
            }
            return this;
        }

        public Builder<T> sync(StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
            this.syncCodec = codec;
            return this;
        }

        public Builder<T> sync(StreamCodec<? super RegistryFriendlyByteBuf, T> codec, AttachmentSyncPredicate predicate) {
            this.syncCodec = codec;
            this.syncPredicate = predicate;
            return this;
        }

        /** NeoForge's parameter order: (sendToPlayer predicate, streamCodec). */
        public Builder<T> sync(java.util.function.BiPredicate<IAttachmentHolder, net.minecraft.server.level.ServerPlayer> sendToPlayer,
                StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
            this.syncCodec = codec;
            this.syncPredicate = (holder, player) -> sendToPlayer.test((IAttachmentHolder) holder, player);
            return this;
        }

        public Builder<T> copyOnDeath() {
            this.copyOnDeath = true;
            return this;
        }

        @SuppressWarnings("unchecked")
        public AttachmentType<T> build() {
            AttachmentType<T> type = new AttachmentType<>(initializer);
            type.codec = (Codec<T>) codec;
            type.persistenceCondition = persistenceCondition;
            type.syncCodec = syncCodec;
            type.syncPredicate = syncPredicate;
            type.copyOnDeath = copyOnDeath;
            return type;
        }
    }

    /** IAttachmentSerializer variant backed by a codec. */
    public interface CodecSerializer<T> {
        Codec<T> codec();
    }
}
