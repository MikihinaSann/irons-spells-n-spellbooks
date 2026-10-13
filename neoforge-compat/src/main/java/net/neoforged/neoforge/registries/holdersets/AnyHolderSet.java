package net.neoforged.neoforge.registries.holdersets;

import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;

/** NeoForge AnyHolderSet - a HolderSet wrapping an arbitrary holder collection. */
public class AnyHolderSet<T> extends HolderSet.Named<T> {
    private final List<Holder<T>> contents;

    public AnyHolderSet(HolderOwner<T> owner, java.util.Collection<Holder<T>> contents) {
        super(owner, (TagKey<T>) null);
        this.contents = List.copyOf(contents);
    }

    public AnyHolderSet(HolderOwner<T> owner) {
        this(owner, List.of());
    }

    @Override
    protected List<Holder<T>> contents() {
        return contents;
    }
}
