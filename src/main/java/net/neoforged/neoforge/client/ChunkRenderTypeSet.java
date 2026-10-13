package net.neoforged.neoforge.client;

import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Minimal stand-in for NeoForge's ChunkRenderTypeSet — an ordered set of block
 * render types a model can render with.
 */
public final class ChunkRenderTypeSet implements Iterable<RenderType> {
    private final List<RenderType> types;

    private ChunkRenderTypeSet(List<RenderType> types) {
        this.types = types;
    }

    public static ChunkRenderTypeSet of(RenderType... types) {
        return new ChunkRenderTypeSet(List.of(types));
    }

    public static ChunkRenderTypeSet of(Set<RenderType> types) {
        return new ChunkRenderTypeSet(List.copyOf(types));
    }

    public boolean contains(RenderType type) {
        return types.contains(type);
    }

    public boolean isEmpty() {
        return types.isEmpty();
    }

    public List<RenderType> asList() {
        return types;
    }

    @NotNull
    @Override
    public Iterator<RenderType> iterator() {
        return types.iterator();
    }
}
