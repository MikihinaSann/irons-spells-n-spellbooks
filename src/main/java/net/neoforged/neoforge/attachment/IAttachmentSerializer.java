package net.neoforged.neoforge.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

/**
 * Stand-in for NeoForge's IAttachmentSerializer.
 *
 * @param <S> serialized representation type (NBT Tag or a network buffer form)
 * @param <T> attachment value type
 */
public interface IAttachmentSerializer<S, T> {
    T read(IAttachmentHolder holder, S s, HolderLookup.Provider provider);

    @Nullable
    S write(T attachment, HolderLookup.Provider provider);
}
