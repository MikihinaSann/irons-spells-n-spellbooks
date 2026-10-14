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
}
