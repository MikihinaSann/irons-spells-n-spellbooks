package net.neoforged.neoforge.fluids;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Links vanilla {@link Fluid}s to their {@link FluidType}. Vanilla mappings are
 * registered by {@link net.neoforged.neoforge.common.NeoForgeMod} static
 * initializers so {@code type == NeoForgeMod.WATER_TYPE.value()} identity
 * comparisons work; modded fluids may call {@link #register}.
 */
public final class NeoForgeFluidTypeRegistry {
    private static final Map<Fluid, FluidType> FLUID_TO_TYPE = new ConcurrentHashMap<>();
    private static final Map<FluidType, ResourceLocation> TYPE_TO_KEY = new ConcurrentHashMap<>();
    private static boolean initialized;

    private NeoForgeFluidTypeRegistry() {
    }

    /** Forces {@link net.neoforged.neoforge.common.NeoForgeMod} class-load, which registers the vanilla fluid types. */
    public static synchronized void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        net.neoforged.neoforge.common.NeoForgeMod.registerFluidTypes();
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
        return fluid == Fluids.EMPTY
                ? net.neoforged.neoforge.common.NeoForgeMod.EMPTY_TYPE.value()
                : net.neoforged.neoforge.common.NeoForgeMod.WATER_TYPE.value();
    }

    @Nullable
    public static ResourceLocation getKeyFor(FluidType type) {
        init();
        ResourceLocation key = TYPE_TO_KEY.get(type);
        if (key == null) {
            var holder = net.neoforged.neoforge.registries.NeoForgeRegistries.FLUID_TYPES.getResourceKey(type);
            key = holder.map(k -> k.location()).orElse(null);
            if (key != null) {
                TYPE_TO_KEY.put(type, key);
            }
        }
        return key;
    }
}
