package net.neoforged.neoforge.capabilities;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * The NeoForge capability table — on Fabric these map to API lookups.
 */
public final class Capabilities {

    private Capabilities() {
    }

    public static final class ItemHandler {
        public static final RegisterCapabilitiesEvent.Capability<IItemHandler> BLOCK =
                new RegisterCapabilitiesEvent.Capability<>(ResourceLocation.fromNamespaceAndPath("neoforge", "item_handler"), IItemHandler.class);
        public static final RegisterCapabilitiesEvent.Capability<IItemHandler> ITEM =
                new RegisterCapabilitiesEvent.Capability<>(ResourceLocation.fromNamespaceAndPath("neoforge", "item_handler_item"), IItemHandler.class);
        public static final RegisterCapabilitiesEvent.Capability<IItemHandler> ENTITY =
                new RegisterCapabilitiesEvent.Capability<>(ResourceLocation.fromNamespaceAndPath("neoforge", "item_handler_entity"), IItemHandler.class);
    }

    public static final class FluidHandler {
        public static final RegisterCapabilitiesEvent.Capability<IFluidHandler> BLOCK =
                new RegisterCapabilitiesEvent.Capability<>(ResourceLocation.fromNamespaceAndPath("neoforge", "fluid_handler"), IFluidHandler.class);
        public static final RegisterCapabilitiesEvent.Capability<IFluidHandler> ITEM =
                new RegisterCapabilitiesEvent.Capability<>(ResourceLocation.fromNamespaceAndPath("neoforge", "fluid_handler_item"), IFluidHandler.class);
    }
}
