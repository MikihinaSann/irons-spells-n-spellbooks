package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

/**
 * Exposes vanilla's private {@code PoiTypes.registerBlockStates} — NeoForge
 * calls this automatically whenever a modded {@link PoiType} registers
 * (NeoForgeRegistryCallbacks), wiring block states into {@code TYPE_BY_STATE}.
 */
@Mixin(PoiTypes.class)
public interface PoiTypesAccessor {

    @Invoker("registerBlockStates")
    static void invokeRegisterBlockStates(Holder<PoiType> holder, Set<BlockState> states) {
        throw new AssertionError();
    }
}
