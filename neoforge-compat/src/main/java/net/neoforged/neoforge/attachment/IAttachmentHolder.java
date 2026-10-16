package net.neoforged.neoforge.attachment;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Interface injected onto Entity, BlockEntity, Level, ChunkAccess via
 * Loom's {@code injected_interfaces}. Default implementations delegate to
 * Fabric's attachment API (which injects {@code AttachmentTarget} onto the
 * same classes).
 */
public interface IAttachmentHolder {
    private net.fabricmc.fabric.api.attachment.v1.AttachmentTarget self() {
        return (net.fabricmc.fabric.api.attachment.v1.AttachmentTarget) this;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> net.fabricmc.fabric.api.attachment.v1.AttachmentType<T> fabric(AttachmentType<T> type) {
        var fabric = type.fabric();
        if (fabric == null) {
            throw new IllegalStateException("AttachmentType not registered: " + type.getId());
        }
        return fabric;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> net.fabricmc.fabric.api.attachment.v1.AttachmentType<T> fabricRaw(AttachmentType<?> type) {
        return (net.fabricmc.fabric.api.attachment.v1.AttachmentType<T>) type.fabric();
    }

    default boolean hasData(AttachmentType<?> type) {
        return self().hasAttached(fabricRaw(type));
    }

    default boolean hasData(Supplier<? extends AttachmentType<?>> type) {
        return hasData(type.get());
    }

    default <T> T getData(AttachmentType<T> type) {
        var fabric = fabric(type);
        T attached = (T) self().getAttached(fabric);
        if (attached == null) {
            attached = type.createInitialValue(this);
            self().setAttached(fabric, attached);
        }
        return attached;
    }

    default <T> T getData(Supplier<? extends AttachmentType<T>> type) {
        return getData(type.get());
    }

    @Nullable
    default <T> T getExistingData(AttachmentType<T> type) {
        return (T) self().getAttached(fabric(type));
    }

    @Nullable
    default <T> T getExistingData(Supplier<? extends AttachmentType<T>> type) {
        return getExistingData(type.get());
    }

    @Nullable
    default <T> T getExistingDataOrNull(AttachmentType<T> type) {
        return getExistingData(type);
    }

    @Nullable
    default <T> T getExistingDataOrNull(Supplier<? extends AttachmentType<T>> type) {
        return getExistingData(type.get());
    }

    default <T> Optional<T> getExistingDataOptional(AttachmentType<T> type) {
        return Optional.ofNullable(getExistingData(type));
    }

    default <T> Optional<T> getExistingDataOptional(Supplier<? extends AttachmentType<T>> type) {
        return Optional.ofNullable(getExistingData(type.get()));
    }

    default <T> T setData(AttachmentType<T> type, T data) {
        T previous = self().setAttached(fabric(type), data);
        AttachmentHooks.fireSet(this, type, data);
        return previous;
    }

    default <T> T setData(Supplier<? extends AttachmentType<T>> type, T data) {
        return setData(type.get(), data);
    }

    default void removeData(AttachmentType<?> type) {
        self().removeAttached(fabricRaw(type));
    }

    default void removeData(Supplier<? extends AttachmentType<?>> type) {
        removeData(type.get());
    }
}
