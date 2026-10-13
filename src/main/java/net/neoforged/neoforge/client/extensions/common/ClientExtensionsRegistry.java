package net.neoforged.neoforge.client.extensions.common;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Internal registry backing the client extensions lookups.
 */
public final class ClientExtensionsRegistry {
    private static final Map<Item, IClientItemExtensions> ITEM_EXTENSIONS = new ConcurrentHashMap<>();
    private static final Map<FluidType, IClientFluidTypeExtensions> FLUID_EXTENSIONS = new ConcurrentHashMap<>();

    private static final IClientItemExtensions DEFAULT_ITEM = new IClientItemExtensions() {
    };
    private static final IClientFluidTypeExtensions DEFAULT_FLUID = new IClientFluidTypeExtensions() {
    };

    private ClientExtensionsRegistry() {
    }

    public static void registerItem(Item item, IClientItemExtensions ext) {
        ITEM_EXTENSIONS.put(item, ext);
    }

    public static Map<Item, IClientItemExtensions> getItemExtensions() {
        return java.util.Collections.unmodifiableMap(ITEM_EXTENSIONS);
    }

    public static void registerFluidType(FluidType type, IClientFluidTypeExtensions ext) {
        FLUID_EXTENSIONS.put(type, ext);
    }

    public static IClientItemExtensions getItemExtension(Item item) {
        return ITEM_EXTENSIONS.getOrDefault(item, DEFAULT_ITEM);
    }

    public static IClientFluidTypeExtensions getFluidTypeExtension(FluidType type) {
        return FLUID_EXTENSIONS.getOrDefault(type, DEFAULT_FLUID);
    }
}
