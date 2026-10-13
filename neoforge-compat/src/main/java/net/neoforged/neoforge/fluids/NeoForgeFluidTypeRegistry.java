package net.neoforged.neoforge.fluids;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Links vanilla {@link Fluid}s to their {@link FluidType}. Populated when
 * fluids carrying a type are registered; vanilla fluids get defaults.
 */
public final class NeoForgeFluidTypeRegistry {
    private static final Map<Fluid, FluidType> FLUID_TO_TYPE = new ConcurrentHashMap<>();
    private static final Map<FluidType, ResourceLocation> TYPE_TO_KEY = new ConcurrentHashMap<>();

    private static FluidType waterType;
    private static FluidType lavaType;
    private static FluidType emptyType;

    private NeoForgeFluidTypeRegistry() {
    }

    public static synchronized void init() {
        if (waterType != null) {
            return;
        }
        waterType = new FluidType(FluidType.Properties.create()
                .descriptionId("block.minecraft.water")
                .canSwim(true).canDrown(true).canHydrate(true).canExtinguish(true)
                .supportsBoating(true));
        lavaType = new FluidType(FluidType.Properties.create()
                .descriptionId("block.minecraft.lava")
                .canSwim(false).canDrown(true).canHydrate(false)
                .lightLevel(15).temperature(1300).density(3000).viscosity(6000));
        emptyType = new FluidType(FluidType.Properties.create());
        register(Fluids.WATER, waterType, ResourceLocation.fromNamespaceAndPath("minecraft", "water_type"));
        register(Fluids.FLOWING_WATER, waterType, null);
        register(Fluids.LAVA, lavaType, ResourceLocation.fromNamespaceAndPath("minecraft", "lava_type"));
        register(Fluids.FLOWING_LAVA, lavaType, null);
        register(Fluids.EMPTY, emptyType, ResourceLocation.fromNamespaceAndPath("minecraft", "empty_type"));
    }

    public static void register(Fluid fluid, FluidType type, @Nullable ResourceLocation typeId) {
        FLUID_TO_TYPE.put(fluid, type);
        if (typeId != null) {
            TYPE_TO_KEY.put(type, typeId);
        }
    }

    public static void registerType(ResourceLocation id, FluidType type) {
        TYPE_TO_KEY.put(type, id);
    }

    public static FluidType getTypeFor(Fluid fluid) {
        init();
        FluidType type = FLUID_TO_TYPE.get(fluid);
        if (type != null) {
            return type;
        }
        return fluid == Fluids.EMPTY ? emptyType : waterType;
    }

    @Nullable
    public static ResourceLocation getKeyFor(FluidType type) {
        init();
        return TYPE_TO_KEY.get(type);
    }
}
