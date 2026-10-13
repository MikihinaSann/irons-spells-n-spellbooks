package net.neoforged.neoforge.common.extensions;

import java.util.Set;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;

/**
 * NeoForge's BiomeGenerationSettings extension — exposes the carving stages
 * patched onto BiomeGenerationSettings.
 */
public interface IBiomeGenerationSettingsExtension {

    default Set<GenerationStep.Carving> getCarvingStages() {
        return ((BiomeGenerationSettings) (Object) this).carvers.keySet();
    }
}
