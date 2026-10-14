package net.neoforged.neoforge.fluids;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * NeoForge's {@code FluidInteractionRegistry} — custom fluid-on-fluid contact
 * reactions, consulted by the {@code LiquidBlock} dispatch mixin.
 */
public final class FluidInteractionRegistry {
    private static final Map<FluidType, List<InteractionInformation>> INTERACTIONS = new ConcurrentHashMap<>();

    private FluidInteractionRegistry() {
    }

    public static void addInteraction(FluidType mainType, InteractionInformation info) {
        INTERACTIONS.computeIfAbsent(mainType, k -> new CopyOnWriteArrayList<>()).add(info);
    }

    public static List<InteractionInformation> getInteractions(FluidType type) {
        return INTERACTIONS.getOrDefault(type, List.of());
    }

    public record InteractionInformation(Predicate<FluidType> predicate,
            Function<FluidState, BlockState> interactionResult) {

        public InteractionInformation(FluidType other, Function<FluidState, BlockState> interactionResult) {
            this(type -> type == other, interactionResult);
        }
    }
}
