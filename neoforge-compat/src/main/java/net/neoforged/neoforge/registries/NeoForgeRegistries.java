package net.neoforged.neoforge.registries;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.fluids.FluidType;

/**
 * Stand-in for NeoForgeRegistries — registry keys and static backing registries
 * for the NeoForge registry surface this layer supports. Key paths match
 * NeoForge 21.1 exactly so registry ids and datapack directory resolution line
 * up with upstream (e.g. {@code neoforge:biome_modifier} loads from
 * {@code data/<ns>/neoforge/biome_modifier/}).
 *
 * <p>{@code BIOME_MODIFIERS}/{@code STRUCTURE_MODIFIERS} exist as keys only —
 * upstream they are dynamic datapack registries carried by the server's
 * {@link net.minecraft.core.RegistryAccess}, not static registries.
 */
public class NeoForgeRegistries {
    private NeoForgeRegistries() {
    }

    public static class Keys {
        @SuppressWarnings("unchecked")
        public static final ResourceKey<Registry<net.minecraft.network.syncher.EntityDataSerializer<?>>> ENTITY_DATA_SERIALIZERS =
                (ResourceKey<Registry<net.minecraft.network.syncher.EntityDataSerializer<?>>>) (ResourceKey<?>) key("entity_data_serializers");
        public static final ResourceKey<Registry<MapCodec<? extends IGlobalLootModifier>>> GLOBAL_LOOT_MODIFIER_SERIALIZERS = key("global_loot_modifier_serializers");
        public static final ResourceKey<Registry<MapCodec<? extends net.neoforged.neoforge.common.world.BiomeModifier>>> BIOME_MODIFIER_SERIALIZERS = key("biome_modifier_serializers");
        public static final ResourceKey<Registry<MapCodec<? extends net.neoforged.neoforge.common.world.StructureModifier>>> STRUCTURE_MODIFIER_SERIALIZERS = key("structure_modifier_serializers");
        public static final ResourceKey<Registry<FluidType>> FLUID_TYPES = key("fluid_type");
        public static final ResourceKey<Registry<net.neoforged.neoforge.registries.holdersets.HolderSetType>> HOLDER_SET_TYPES = key("holder_set_type");
        public static final ResourceKey<Registry<net.neoforged.neoforge.common.crafting.IngredientType<?>>> INGREDIENT_TYPES = key("ingredient_serializer");
        @SuppressWarnings("unchecked")
        public static final ResourceKey<Registry<?>> FLUID_INGREDIENT_TYPES = (ResourceKey<Registry<?>>) (ResourceKey<?>) key("fluid_ingredient_type");
        public static final ResourceKey<Registry<MapCodec<? extends net.neoforged.neoforge.common.conditions.ICondition>>> CONDITION_CODECS = key("condition_codecs");
        public static final ResourceKey<Registry<AttachmentType<?>>> ATTACHMENT_TYPES = key("attachment_types");
        public static final ResourceKey<Registry<net.neoforged.neoforge.registries.datamaps.DataMapType<?, ?>>> DATA_MAP_TYPES = key("data_maps");
        // Dynamic datapack registries — keys only, no static backing registry.
        public static final ResourceKey<Registry<net.neoforged.neoforge.common.world.BiomeModifier>> BIOME_MODIFIERS = key("biome_modifier");
        public static final ResourceKey<Registry<net.neoforged.neoforge.common.world.StructureModifier>> STRUCTURE_MODIFIERS = key("structure_modifier");

        private static <T> ResourceKey<Registry<T>> key(String path) {
            return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("neoforge", path));
        }
    }

    private static <T> MappedRegistry<T> makeRegistry(ResourceKey<Registry<T>> key) {
        net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder<T, MappedRegistry<T>> builder =
                net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder.createSimple(key);
        return builder.buildAndRegister();
    }

    public static final Registry<AttachmentType<?>> ATTACHMENT_TYPES = makeRegistry(Keys.ATTACHMENT_TYPES);
    public static final Registry<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER_SERIALIZERS = makeRegistry(Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS);
    public static final Registry<MapCodec<? extends net.neoforged.neoforge.common.world.BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = makeRegistry(Keys.BIOME_MODIFIER_SERIALIZERS);
    public static final Registry<MapCodec<? extends net.neoforged.neoforge.common.world.StructureModifier>> STRUCTURE_MODIFIER_SERIALIZERS = makeRegistry(Keys.STRUCTURE_MODIFIER_SERIALIZERS);
    public static final Registry<FluidType> FLUID_TYPES = makeRegistry(Keys.FLUID_TYPES);
    public static final Registry<net.neoforged.neoforge.common.crafting.IngredientType<?>> INGREDIENT_TYPES = makeRegistry(Keys.INGREDIENT_TYPES);
    public static final Registry<net.neoforged.neoforge.registries.holdersets.HolderSetType> HOLDER_SET_TYPES = makeRegistry(Keys.HOLDER_SET_TYPES);
    public static final Registry<MapCodec<? extends net.neoforged.neoforge.common.conditions.ICondition>> CONDITION_CODECS = makeRegistry(Keys.CONDITION_CODECS);
    public static final Registry<net.neoforged.neoforge.registries.datamaps.DataMapType<?, ?>> DATA_MAP_TYPES = makeRegistry(Keys.DATA_MAP_TYPES);
    public static final Registry<net.minecraft.network.syncher.EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = makeRegistry(Keys.ENTITY_DATA_SERIALIZERS);
}
