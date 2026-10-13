package net.neoforged.neoforge.common.extensions;

import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.NeoForgeFluidTypeRegistry;

/**
 * NeoForge-style {@link Fluid} extensions — injected onto
 * {@code net.minecraft.world.level.material.Fluid}.
 */
public interface IFluidExtension {
    private Fluid self() {
        return (Fluid) (Object) this;
    }

    /**
     * The {@link FluidType} associated with this fluid (a default type for
     * vanilla fluids when none was registered).
     */
    default FluidType getFluidType() {
        return NeoForgeFluidTypeRegistry.getTypeFor(self());
    }
}
