package net.neoforged.neoforge.client.extensions.common;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

/**
 * Collects client extensions registrations (items, fluids, blocks, mob effects).
 */
public class RegisterClientExtensionsEvent extends Event {

    public void registerItem(IClientItemExtensions extensions, Supplier<? extends Item> item) {
        ClientExtensionsRegistry.registerItem(item.get(), extensions);
    }

    public void registerItem(IClientItemExtensions extensions, Item item) {
        ClientExtensionsRegistry.registerItem(item, extensions);
    }

    public void registerItem(IClientItemExtensions extensions, Item... items) {
        for (Item item : items) {
            ClientExtensionsRegistry.registerItem(item, extensions);
        }
    }

    public void registerItem(IClientItemExtensions extensions, DeferredHolder<Item, ? extends Item> item) {
        ClientExtensionsRegistry.registerItem(item.get(), extensions);
    }

    public void registerItem(IClientItemExtensions extensions, net.minecraft.core.Holder<Item>... items) {
        for (net.minecraft.core.Holder<Item> item : items) {
            ClientExtensionsRegistry.registerItem(item.value(), extensions);
        }
    }

    public void registerFluidType(IClientFluidTypeExtensions extensions, Supplier<? extends FluidType> fluidType) {
        ClientExtensionsRegistry.registerFluidType(fluidType.get(), extensions);
    }

    public void registerFluidType(IClientFluidTypeExtensions extensions, DeferredHolder<FluidType, ? extends FluidType> fluidType) {
        ClientExtensionsRegistry.registerFluidType(fluidType.get(), extensions);
    }

    public void registerFluidType(IClientFluidTypeExtensions extensions, FluidType fluidType) {
        ClientExtensionsRegistry.registerFluidType(fluidType, extensions);
    }

    public void registerFluidType(IClientFluidTypeExtensions extensions, FluidType... fluidTypes) {
        for (FluidType fluidType : fluidTypes) {
            ClientExtensionsRegistry.registerFluidType(fluidType, extensions);
        }
    }
}
