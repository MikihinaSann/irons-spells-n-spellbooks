package net.neoforged.neoforge.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.common.world.ModifiableStructureInfo.StructureInfo;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * A structure modifier, applied to every structure as datapack registries are (re)built.
 * Ported from NeoForge 21.1.
 */
public interface StructureModifier {
    /**
     * The default phase to be used by modifiers that do not specify a specific phase.
     */
    Phase DEFAULT_PHASE = Phase.AFTER_EVERYTHING;

    /**
     * The codec used to deserialize structure modifiers from datapack JSON.
     * Dispatches on the serializer's {@code type} field.
     */
    Codec<StructureModifier> DIRECT_CODEC = NeoForgeRegistries.STRUCTURE_MODIFIER_SERIALIZERS.byNameCodec()
            .dispatch(StructureModifier::codec, Function.identity());

    /**
     * Codec for referencing structure modifiers by registry key or inline.
     */
    Codec<Holder<StructureModifier>> REFERENCE_CODEC = RegistryFileCodec.create(NeoForgeRegistries.Keys.STRUCTURE_MODIFIERS, DIRECT_CODEC);

    /**
     * Codec for a set of structure modifiers referenced by registry key or inline.
     */
    Codec<HolderSet<StructureModifier>> LIST_CODEC = RegistryCodecs.homogeneousList(NeoForgeRegistries.Keys.STRUCTURE_MODIFIERS, DIRECT_CODEC);

    /**
     * Applies the modifier to the given structure (or its builder).
     *
     * @param structure the structure being modified
     * @param phase     the current phase; modifiers should only act in their own phase
     * @param builder   the builder to modify; changes are applied when {@code build()} is called
     */
    void modify(Holder<Structure> structure, Phase phase, StructureInfo.Builder builder);

    /**
     * The codec for this modifier type, used for dispatch deserialization.
     */
    MapCodec<? extends StructureModifier> codec();

    /**
     * The phases in which structure modifiers are applied, in order.
     */
    enum Phase {
        /**
         * For modifiers that need to be applied before everything else.
         */
        BEFORE_EVERYTHING,

        /**
         * For modifiers that need to add additional spawn overrides or other additive changes.
         */
        ADD,

        /**
         * For modifiers that need to remove spawns or other subtractive changes.
         */
        REMOVE,

        /**
         * For modifiers that need to make arbitrary changes to the structure.
         */
        MODIFY,

        /**
         * For modifiers that need to be applied after everything else.
         */
        AFTER_EVERYTHING
    }
}
