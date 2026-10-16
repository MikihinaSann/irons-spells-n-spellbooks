package net.neoforged.neoforge.client.event;

import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

/**
 * Posted on the mod bus during client setup so mods can register custom
 * {@link SpriteSourceType}s before atlases are built.
 */
public class RegisterSpriteSourceTypesEvent extends Event implements IModBusEvent {
    public void register(ResourceLocation id, SpriteSourceType type) {
        // Vanilla SpriteSources.register(String, ...) feeds the whole string to
        // ResourceLocation.withDefaultNamespace as a *path*, mangling modded
        // namespaces — put into the backing map directly to keep the mod id.
        // TYPE_CODEC captures this BiMap lazily, so late puts are visible.
        SpriteSources.TYPES.put(id, type);
    }
}
