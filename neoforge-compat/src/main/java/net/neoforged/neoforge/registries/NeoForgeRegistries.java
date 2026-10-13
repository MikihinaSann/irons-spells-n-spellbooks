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
 * Stand-in for NeoForgeRegistries — provides real backing registries for the
 * registry keys this mod uses (attachment types, GLM serializers, fluid types).
 */
public class NeoForgeRegistries {
    private NeoForgeRegistries() {
    }

    public static class Keys {
        public static final ResourceKey<Registry<AttachmentType<?>>> ATTACHMENT_TYPES = key("attachment_types");
        public static final ResourceKey<Registry<MapCodec<? extends IGlobalLootModifier>>> GLOBAL_LOOT_MODIFIER_SERIALIZERS = key("loot_modifiers");
        public static final ResourceKey<Registry<net.neoforged.neoforge.common.world.BiomeModifier>> BIOME_MODIFIERS = key("biome_modifiers");
        public static final ResourceKey<Registry<MapCodec<? extends net.neoforged.neoforge.common.world.BiomeModifier>>> BIOME_MODIFIER_SERIALIZERS = key("biome_modifier_serializers");
        public static final ResourceKey<Registry<FluidType>> FLUID_TYPES = key("fluid_type");
        public static final ResourceKey<Registry<net.neoforged.neoforge.common.crafting.IngredientType<?>>> INGREDIENT_TYPES = key("ingredient_types");
        public static final ResourceKey<Registry<MapCodec<? extends net.neoforged.neoforge.common.conditions.ICondition>>> CONDITION_SERIALIZERS = key("condition_serializers");
        public static final ResourceKey<Registry<net.neoforged.neoforge.registries.datamaps.DataMapType<?, ?>>> DATA_MAP_TYPES = key("data_maps");

        private static <T> ResourceKey<Registry<T>> key(String path) {
            return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("neoforge", path));
        }
    }

    private static final ResourceKey<Registry<FluidType>> FLUID_TYPE_KEY = Keys.FLUID_TYPES;

    private static <T> MappedRegistry<T> makeRegistry(ResourceKey<Registry<T>> key) {
        net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder<T, MappedRegistry<T>> builder =
                net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder.createSimple(key);
        return builder.buildAndRegister();
    }

    public static final Registry<AttachmentType<?>> ATTACHMENT_TYPES = makeRegistry(Keys.ATTACHMENT_TYPES);
    public static final Registry<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER_SERIALIZERS = makeRegistry(Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS);
    public static final Registry<net.neoforged.neoforge.common.world.BiomeModifier> BIOME_MODIFIERS = makeRegistry(Keys.BIOME_MODIFIERS);
    public static final Registry<MapCodec<? extends net.neoforged.neoforge.common.world.BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = makeRegistry(Keys.BIOME_MODIFIER_SERIALIZERS);
    public static final Registry<FluidType> FLUID_TYPES = makeRegistry(FLUID_TYPE_KEY);
    public static final Registry<net.neoforged.neoforge.common.crafting.IngredientType<?>> INGREDIENT_TYPES = makeRegistry(Keys.INGREDIENT_TYPES);
    public static final Registry<MapCodec<? extends net.neoforged.neoforge.common.conditions.ICondition>> CONDITION_SERIALIZERS = makeRegistry(Keys.CONDITION_SERIALIZERS);
    public static final Registry<net.neoforged.neoforge.registries.datamaps.DataMapType<?, ?>> DATA_MAP_TYPES = makeRegistry(Keys.DATA_MAP_TYPES);
}
