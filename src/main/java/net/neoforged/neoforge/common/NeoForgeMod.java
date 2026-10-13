package net.neoforged.neoforge.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Stand-in for NeoForgeMod. Provides the vanilla-ish constants this mod uses.
 */
public class NeoForgeMod {
    private NeoForgeMod() {
    }

    private static <T> DeferredHolder<T, T> damageTypeHolder(String path) {
        return DeferredHolder.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("neoforge", path));
    }

    /** Damage type registered under {@code neoforge:poison} (data file ships with the mod). */
    public static final DeferredHolder<DamageType, DamageType> POISON_DAMAGE = damageTypeHolder("poison");

    /**
     * Stand-in for NeoForge's {@code neoforge:water} {@link net.neoforged.neoforge.fluids.FluidType}
     * — the only place a water fluid type is needed is passing it to the
     * FluidType-typed entity hooks ({@code canDrownInFluidType},
     * {@code isPushedByFluid}).
     */
    public static final net.neoforged.neoforge.fluids.FluidType WATER_TYPE =
            new net.neoforged.neoforge.fluids.FluidType(net.neoforged.neoforge.fluids.FluidType.Properties.create());
}
