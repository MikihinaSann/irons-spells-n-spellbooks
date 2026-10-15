package net.neoforged.neoforge.common.extensions;

import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.client.model.data.ModelData;

/**
 * NeoForge's {@code Level} extensions.
 */
public interface ILevelExtension {

    /**
     * NeoForge's block-entity {@link ModelData} lookup. Static models are
     * unaffected; custom model-data blocks would need a real data store.
     */
    default ModelData getModelData(BlockPos pos) {
        return ModelData.EMPTY;
    }

    /**
     * NeoForge's block-capability lookup — resolves through
     * {@link net.neoforged.neoforge.capabilities.CapabilityRegistry}.
     */
    @org.jetbrains.annotations.Nullable
    default <T> T getCapability(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent.Capability<T> capability,
            BlockPos pos, @org.jetbrains.annotations.Nullable net.minecraft.core.Direction context) {
        return net.neoforged.neoforge.capabilities.CapabilityRegistry.getCapability(
                capability, (net.minecraft.world.level.Level) this, pos, null, null, context);
    }
}
