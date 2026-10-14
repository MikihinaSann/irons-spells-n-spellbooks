package net.neoforged.neoforge.common.world;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.Structure.StructureSettings;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Holds the original and modified {@link StructureInfo} of a {@link Structure},
 * applying {@link StructureModifier}s to produce the modified copy.
 * Ported from NeoForge 21.1.
 */
public class ModifiableStructureInfo {
    private final StructureInfo originalStructureInfo;
    @Nullable
    private StructureInfo modifiedStructureInfo = null;

    /**
     * @param originalStructureInfo the original structure settings
     */
    public ModifiableStructureInfo(final StructureInfo originalStructureInfo) {
        this.originalStructureInfo = originalStructureInfo;
    }

    /**
     * @return the modified structure info if it has been modified, otherwise the original
     */
    public StructureInfo get() {
        return this.modifiedStructureInfo == null
                ? originalStructureInfo
                : modifiedStructureInfo;
    }

    /**
     * @return the unmodified structure info
     */
    public StructureInfo getOriginalStructureInfo() {
        return this.originalStructureInfo;
    }

    /**
     * @return the modified structure info, or null if not yet modified
     */
    @Nullable
    public StructureInfo getModifiedStructureInfo() {
        return this.modifiedStructureInfo;
    }

    /**
     * Applies the given structure modifiers to produce a modified structure info.
     * Subsequent invocations are no-ops.
     *
     * @param structure          the structure being modified
     * @param structureModifiers the modifiers to apply, in {@link StructureModifier.Phase} order
     */
    @ApiStatus.Internal
    public void applyStructureModifiers(final Holder<Structure> structure, final List<StructureModifier> structureModifiers) {
        if (this.modifiedStructureInfo != null)
            return;

        StructureInfo original = this.getOriginalStructureInfo();
        final StructureInfo.Builder builder = StructureInfo.Builder.copyOf(original);
        for (StructureModifier.Phase phase : StructureModifier.Phase.values()) {
            for (StructureModifier modifier : structureModifiers) {
                modifier.modify(structure, phase, builder);
            }
        }
        this.modifiedStructureInfo = builder.build();
        // NeoForge stores this on the Structure and routes structureSettings()
        // through it; our port writes the result onto the (access-widened)
        // settings field instead.
        structure.value().settings = this.modifiedStructureInfo.structureSettings();
    }

    /**
     * The modifiable view of a structure's settings.
     */
    public record StructureInfo(StructureSettings structureSettings) {
        public static class Builder {
            private StructureSettingsBuilder structureSettings;

            /**
             * @return a builder seeded with the given original info
             */
            public static Builder copyOf(final StructureInfo original) {
                final StructureSettingsBuilder structureBuilder = StructureSettingsBuilder.copyOf(original.structureSettings());
                return new Builder(structureBuilder);
            }

            private Builder(final StructureSettingsBuilder structureSettings) {
                this.structureSettings = structureSettings;
            }

            public StructureInfo build() {
                return new StructureInfo(this.structureSettings.build());
            }

            public StructureSettingsBuilder getStructureSettings() {
                return structureSettings;
            }
        }
    }
}
