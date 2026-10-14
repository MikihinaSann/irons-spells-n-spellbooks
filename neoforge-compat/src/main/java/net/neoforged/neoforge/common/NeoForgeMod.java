package net.neoforged.neoforge.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Stand-in for NeoForgeMod. Provides the vanilla-ish constants this mod uses.
 */
public class NeoForgeMod {
    private NeoForgeMod() {
    }

    private static <T> DeferredHolder<T, T> damageTypeHolder(String path) {
        return DeferredHolder.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("neoforge", path));
    }

    /** Damage type registered under {@code neoforge:poison} (data file ships with the mod). */
    public static final DeferredHolder<DamageType, DamageType> POISON_DAMAGE = damageTypeHolder("poison");

    /**
     * Stand-in for NeoForge's {@code neoforge:water} {@link net.neoforged.neoforge.fluids.FluidType}
     * — the only place a water fluid type is needed is passing it to the
     * FluidType-typed entity hooks ({@code canDrownInFluidType},
     * {@code isPushedByFluid}).
     */
    /**
     * NeoForge's {@code neoforge:creative_flight} BooleanAttribute. Registered
     * by {@link #registerAttributes()} during common init (before the deferred
     * drains so mob effects can attach modifiers to it).
     */
    public static net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> CREATIVE_FLIGHT;

    /**
     * NeoForge's {@code neoforge:swim_speed} attribute — RangedAttribute(1.0, 0, 1024).
     */
    public static net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> SWIM_SPEED;

    public static void registerAttributes() {
        if (CREATIVE_FLIGHT == null) {
            var attr = new net.neoforged.neoforge.common.BooleanAttribute("attribute.neoforge.creative_flight", false);
            attr.setSyncable(true);
            CREATIVE_FLIGHT = net.minecraft.core.Registry.registerForHolder(
                    net.minecraft.core.registries.BuiltInRegistries.ATTRIBUTE,
                    ResourceLocation.fromNamespaceAndPath("neoforge", "creative_flight"), attr);
        }
        if (SWIM_SPEED == null) {
            var attr = new net.minecraft.world.entity.ai.attributes.RangedAttribute("attribute.neoforge.swim_speed", 1.0, 0.0, 1024.0);
            attr.setSyncable(true);
            SWIM_SPEED = net.minecraft.core.Registry.registerForHolder(
                    net.minecraft.core.registries.BuiltInRegistries.ATTRIBUTE,
                    ResourceLocation.fromNamespaceAndPath("neoforge", "swim_speed"), attr);
        }
    }

    private static net.minecraft.core.Holder<net.neoforged.neoforge.fluids.FluidType> vanillaFluidType(
            String name, net.neoforged.neoforge.fluids.FluidType type,
            net.minecraft.world.level.material.Fluid still, net.minecraft.world.level.material.Fluid flowing) {
        var rl = net.minecraft.resources.ResourceLocation.withDefaultNamespace(name);
        var holder = net.minecraft.core.Registry.registerForHolder(
                net.neoforged.neoforge.registries.NeoForgeRegistries.FLUID_TYPES, rl, type);
        net.neoforged.neoforge.fluids.NeoForgeFluidTypeRegistry.register(still, type, rl);
        net.neoforged.neoforge.fluids.NeoForgeFluidTypeRegistry.register(flowing, type, rl);
        return holder;
    }

    // ------------------------------------------------------------------
    // HolderSet types (neoforge:and/or/not/any) — NF registers these through
    // a DeferredRegister on neoforge:holder_set_type.
    // ------------------------------------------------------------------

    private static net.minecraft.core.Holder<net.neoforged.neoforge.registries.holdersets.HolderSetType> holderSetType(
            String name, net.neoforged.neoforge.registries.holdersets.HolderSetType type) {
        return net.minecraft.core.Registry.registerForHolder(
                net.neoforged.neoforge.registries.NeoForgeRegistries.HOLDER_SET_TYPES,
                ResourceLocation.fromNamespaceAndPath("neoforge", name), type);
    }

    public static final net.minecraft.core.Holder<net.neoforged.neoforge.registries.holdersets.HolderSetType> AND_HOLDER_SET =
            holderSetType("and", new net.neoforged.neoforge.registries.holdersets.AndHolderSet.Type());
    public static final net.minecraft.core.Holder<net.neoforged.neoforge.registries.holdersets.HolderSetType> OR_HOLDER_SET =
            holderSetType("or", new net.neoforged.neoforge.registries.holdersets.OrHolderSet.Type());
    public static final net.minecraft.core.Holder<net.neoforged.neoforge.registries.holdersets.HolderSetType> NOT_HOLDER_SET =
            holderSetType("not", new net.neoforged.neoforge.registries.holdersets.NotHolderSet.Type());
    public static final net.minecraft.core.Holder<net.neoforged.neoforge.registries.holdersets.HolderSetType> ANY_HOLDER_SET =
            holderSetType("any", new net.neoforged.neoforge.registries.holdersets.AnyHolderSet.Type());

    /** NeoForge's {@code minecraft:empty} fluid type (NF registers vanilla types under the minecraft namespace). */
    public static final net.minecraft.core.Holder<net.neoforged.neoforge.fluids.FluidType> EMPTY_TYPE =
            vanillaFluidType("empty", new net.neoforged.neoforge.fluids.FluidType(
                    net.neoforged.neoforge.fluids.FluidType.Properties.create()
                            .descriptionId("block.minecraft.air")
                            .motionScale(1D)
                            .canPushEntity(false)
                            .canSwim(false)
                            .canDrown(false)
                            .fallDistanceModifier(1F)
                            .pathType(null)
                            .adjacentPathType(null)
                            .density(0)
                            .temperature(0)
                            .viscosity(0)),
                    net.minecraft.world.level.material.Fluids.EMPTY,
                    net.minecraft.world.level.material.Fluids.EMPTY);

    /** NeoForge's {@code minecraft:water} fluid type. */
    public static final net.minecraft.core.Holder<net.neoforged.neoforge.fluids.FluidType> WATER_TYPE =
            vanillaFluidType("water", new net.neoforged.neoforge.fluids.FluidType(
                    net.neoforged.neoforge.fluids.FluidType.Properties.create()
                            .descriptionId("block.minecraft.water")
                            .fallDistanceModifier(0F)
                            .canExtinguish(true)
                            .canConvertToSource(true)
                            .supportsBoating(true)),
                    net.minecraft.world.level.material.Fluids.WATER,
                    net.minecraft.world.level.material.Fluids.FLOWING_WATER);

    /** NeoForge's {@code minecraft:lava} fluid type. */
    public static final net.minecraft.core.Holder<net.neoforged.neoforge.fluids.FluidType> LAVA_TYPE =
            vanillaFluidType("lava", new net.neoforged.neoforge.fluids.FluidType(
                    net.neoforged.neoforge.fluids.FluidType.Properties.create()
                            .descriptionId("block.minecraft.lava")
                            .canSwim(false)
                            .canDrown(false)
                            .pathType(net.minecraft.world.level.pathfinder.PathType.LAVA)
                            .adjacentPathType(null)),
                    net.minecraft.world.level.material.Fluids.LAVA,
                    net.minecraft.world.level.material.Fluids.FLOWING_LAVA);

    /** NeoForge's {@code minecraft:milk} fluid type holder -- reserved for modded milk fluids. */
    public static final net.neoforged.neoforge.registries.DeferredHolder<
            net.neoforged.neoforge.fluids.FluidType, net.neoforged.neoforge.fluids.FluidType> MILK_TYPE =
            net.neoforged.neoforge.registries.DeferredHolder.create(
                    net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.FLUID_TYPES,
                    net.minecraft.resources.ResourceLocation.withDefaultNamespace("milk"));

    private static boolean mergedAttributeTooltips = false;

    /** NeoForge's flag — merged attribute tooltips enabled by mods (Apothic Attributes). */
    public static void enableMergedAttributeTooltips() {
        mergedAttributeTooltips = true;
    }

    public static boolean shouldMergeAttributeTooltips() {
        return mergedAttributeTooltips;
    }

    /** NeoForge's {@code neoforge:compound} ingredient type holder. */
    public static final net.neoforged.neoforge.registries.DeferredHolder<
            net.neoforged.neoforge.common.crafting.IngredientType<?>,
            net.neoforged.neoforge.common.crafting.IngredientType<net.neoforged.neoforge.common.crafting.CompoundIngredient>>
        COMPOUND_INGREDIENT_TYPE = net.neoforged.neoforge.registries.DeferredHolder.create(
            net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.INGREDIENT_TYPES,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "compound"));

    public static final net.neoforged.neoforge.registries.DeferredHolder<
            net.neoforged.neoforge.common.crafting.IngredientType<?>,
            net.neoforged.neoforge.common.crafting.IngredientType<net.neoforged.neoforge.common.crafting.DataComponentIngredient>>
        DATA_COMPONENT_INGREDIENT_TYPE = net.neoforged.neoforge.registries.DeferredHolder.create(
            net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.INGREDIENT_TYPES,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "data_component"));

    public static final net.neoforged.neoforge.registries.DeferredHolder<
            net.neoforged.neoforge.common.crafting.IngredientType<?>,
            net.neoforged.neoforge.common.crafting.IngredientType<net.neoforged.neoforge.common.crafting.DifferenceIngredient>>
        DIFFERENCE_INGREDIENT_TYPE = net.neoforged.neoforge.registries.DeferredHolder.create(
            net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.INGREDIENT_TYPES,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "difference"));

    public static final net.neoforged.neoforge.registries.DeferredHolder<
            net.neoforged.neoforge.common.crafting.IngredientType<?>,
            net.neoforged.neoforge.common.crafting.IngredientType<net.neoforged.neoforge.common.crafting.IntersectionIngredient>>
        INTERSECTION_INGREDIENT_TYPE = net.neoforged.neoforge.registries.DeferredHolder.create(
            net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.INGREDIENT_TYPES,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "intersection"));

    /** NeoForge's {@code neoforge:block_tag} ingredient type holder. */
    public static final net.neoforged.neoforge.registries.DeferredHolder<
            net.neoforged.neoforge.common.crafting.IngredientType<?>,
            net.neoforged.neoforge.common.crafting.IngredientType<net.neoforged.neoforge.common.crafting.BlockTagIngredient>>
        BLOCK_TAG_INGREDIENT = net.neoforged.neoforge.registries.DeferredHolder.create(
            net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.INGREDIENT_TYPES,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "block_tag"));

    // ---- biome modifier serializer types (stock NeoForge codecs) ----
    private static net.neoforged.neoforge.registries.DeferredHolder<
            com.mojang.serialization.MapCodec<? extends net.neoforged.neoforge.common.world.BiomeModifier>,
            ? extends com.mojang.serialization.MapCodec<?>> bmType(String path) {
        return net.neoforged.neoforge.registries.DeferredHolder.create(
            net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", path));
    }

    public static final net.neoforged.neoforge.registries.DeferredHolder<com.mojang.serialization.MapCodec<? extends net.neoforged.neoforge.common.world.BiomeModifier>, ?> NONE_BIOME_MODIFIER_TYPE = bmType("none");
    public static final net.neoforged.neoforge.registries.DeferredHolder<com.mojang.serialization.MapCodec<? extends net.neoforged.neoforge.common.world.BiomeModifier>, ?> ADD_FEATURES_BIOME_MODIFIER_TYPE = bmType("add_features");
    public static final net.neoforged.neoforge.registries.DeferredHolder<com.mojang.serialization.MapCodec<? extends net.neoforged.neoforge.common.world.BiomeModifier>, ?> REMOVE_FEATURES_BIOME_MODIFIER_TYPE = bmType("remove_features");
    public static final net.neoforged.neoforge.registries.DeferredHolder<com.mojang.serialization.MapCodec<? extends net.neoforged.neoforge.common.world.BiomeModifier>, ?> ADD_SPAWNS_BIOME_MODIFIER_TYPE = bmType("add_spawns");
    public static final net.neoforged.neoforge.registries.DeferredHolder<com.mojang.serialization.MapCodec<? extends net.neoforged.neoforge.common.world.BiomeModifier>, ?> REMOVE_SPAWNS_BIOME_MODIFIER_TYPE = bmType("remove_spawns");
    public static final net.neoforged.neoforge.registries.DeferredHolder<com.mojang.serialization.MapCodec<? extends net.neoforged.neoforge.common.world.BiomeModifier>, ?> ADD_CARVERS_BIOME_MODIFIER_TYPE = bmType("add_carvers");
    public static final net.neoforged.neoforge.registries.DeferredHolder<com.mojang.serialization.MapCodec<? extends net.neoforged.neoforge.common.world.BiomeModifier>, ?> REMOVE_CARVERS_BIOME_MODIFIER_TYPE = bmType("remove_carvers");
    public static final net.neoforged.neoforge.registries.DeferredHolder<com.mojang.serialization.MapCodec<? extends net.neoforged.neoforge.common.world.BiomeModifier>, ?> ADD_SPAWN_COSTS_BIOME_MODIFIER_TYPE = bmType("add_spawn_costs");
    public static final net.neoforged.neoforge.registries.DeferredHolder<com.mojang.serialization.MapCodec<? extends net.neoforged.neoforge.common.world.BiomeModifier>, ?> REMOVE_SPAWN_COSTS_BIOME_MODIFIER_TYPE = bmType("remove_spawn_costs");

    // ---- structure modifier serializer types (stock NeoForge codecs) ----
    private static net.neoforged.neoforge.registries.DeferredHolder<
            com.mojang.serialization.MapCodec<? extends net.neoforged.neoforge.common.world.StructureModifier>,
            ? extends com.mojang.serialization.MapCodec<?>> smType(String path) {
        return net.neoforged.neoforge.registries.DeferredHolder.create(
            net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", path));
    }

    public static final net.neoforged.neoforge.registries.DeferredHolder<com.mojang.serialization.MapCodec<? extends net.neoforged.neoforge.common.world.StructureModifier>, ?> NONE_STRUCTURE_MODIFIER_TYPE = smType("none");
    public static final net.neoforged.neoforge.registries.DeferredHolder<com.mojang.serialization.MapCodec<? extends net.neoforged.neoforge.common.world.StructureModifier>, ?> ADD_SPAWNS_STRUCTURE_MODIFIER_TYPE = smType("add_spawns");
    public static final net.neoforged.neoforge.registries.DeferredHolder<com.mojang.serialization.MapCodec<? extends net.neoforged.neoforge.common.world.StructureModifier>, ?> REMOVE_SPAWNS_STRUCTURE_MODIFIER_TYPE = smType("remove_spawns");
    public static final net.neoforged.neoforge.registries.DeferredHolder<com.mojang.serialization.MapCodec<? extends net.neoforged.neoforge.common.world.StructureModifier>, ?> CLEAR_SPAWNS_STRUCTURE_MODIFIER_TYPE = smType("clear_spawns");

    /** Registers the stock {@code neoforge:} structure modifier serializers. */
    public static void registerStructureModifierSerializers() {
        var reg = net.neoforged.neoforge.registries.NeoForgeRegistries.STRUCTURE_MODIFIER_SERIALIZERS;
        net.minecraft.core.Registry.register(reg,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "none"),
            com.mojang.serialization.MapCodec.unit(
                    () -> net.neoforged.neoforge.common.world.NoneStructureModifier.INSTANCE));
        net.minecraft.core.Registry.register(reg,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "add_spawns"),
            com.mojang.serialization.codecs.RecordCodecBuilder.<net.neoforged.neoforge.common.world.StructureModifiers.AddSpawnsStructureModifier>mapCodec(builder -> builder.group(
                    net.minecraft.core.RegistryCodecs.homogeneousList(
                            net.minecraft.core.registries.Registries.STRUCTURE,
                            net.minecraft.world.level.levelgen.structure.Structure.DIRECT_CODEC)
                        .fieldOf("structures")
                        .forGetter(net.neoforged.neoforge.common.world.StructureModifiers.AddSpawnsStructureModifier::structures),
                    com.mojang.serialization.Codec.either(
                            net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData.CODEC.listOf(),
                            net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData.CODEC).xmap(
                            either -> either.map(java.util.function.Function.identity(), java.util.List::of),
                            list -> list.size() == 1 ? com.mojang.datafixers.util.Either.right(list.get(0))
                                    : com.mojang.datafixers.util.Either.left(list))
                        .fieldOf("spawners")
                        .forGetter(net.neoforged.neoforge.common.world.StructureModifiers.AddSpawnsStructureModifier::spawners))
                .apply(builder, net.neoforged.neoforge.common.world.StructureModifiers.AddSpawnsStructureModifier::new)));
        net.minecraft.core.Registry.register(reg,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "remove_spawns"),
            com.mojang.serialization.codecs.RecordCodecBuilder.<net.neoforged.neoforge.common.world.StructureModifiers.RemoveSpawnsStructureModifier>mapCodec(builder -> builder.group(
                    net.minecraft.core.RegistryCodecs.homogeneousList(
                            net.minecraft.core.registries.Registries.STRUCTURE,
                            net.minecraft.world.level.levelgen.structure.Structure.DIRECT_CODEC)
                        .fieldOf("structures")
                        .forGetter(net.neoforged.neoforge.common.world.StructureModifiers.RemoveSpawnsStructureModifier::structures),
                    net.minecraft.core.RegistryCodecs.homogeneousList(net.minecraft.core.registries.Registries.ENTITY_TYPE)
                        .fieldOf("entity_types")
                        .forGetter(net.neoforged.neoforge.common.world.StructureModifiers.RemoveSpawnsStructureModifier::entityTypes))
                .apply(builder, net.neoforged.neoforge.common.world.StructureModifiers.RemoveSpawnsStructureModifier::new)));
        net.minecraft.core.Registry.register(reg,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "clear_spawns"),
            com.mojang.serialization.codecs.RecordCodecBuilder.<net.neoforged.neoforge.common.world.StructureModifiers.ClearSpawnsStructureModifier>mapCodec(builder -> builder.group(
                    net.minecraft.core.RegistryCodecs.homogeneousList(
                            net.minecraft.core.registries.Registries.STRUCTURE,
                            net.minecraft.world.level.levelgen.structure.Structure.DIRECT_CODEC)
                        .fieldOf("structures")
                        .forGetter(net.neoforged.neoforge.common.world.StructureModifiers.ClearSpawnsStructureModifier::structures),
                    com.mojang.serialization.Codec.<java.util.List<net.minecraft.world.entity.MobCategory>, net.minecraft.world.entity.MobCategory>either(
                            net.minecraft.world.entity.MobCategory.CODEC.listOf(),
                            net.minecraft.world.entity.MobCategory.CODEC).<java.util.Set<net.minecraft.world.entity.MobCategory>>xmap(
                            either -> either.map(java.util.Set::copyOf, java.util.Set::of),
                            set -> set.size() == 1 ? com.mojang.datafixers.util.Either.right(set.toArray(net.minecraft.world.entity.MobCategory[]::new)[0])
                                    : com.mojang.datafixers.util.Either.left(java.util.List.copyOf(set)))
                        .optionalFieldOf("categories", java.util.EnumSet.allOf(net.minecraft.world.entity.MobCategory.class))
                        .forGetter(net.neoforged.neoforge.common.world.StructureModifiers.ClearSpawnsStructureModifier::categories))
                .apply(builder, net.neoforged.neoforge.common.world.StructureModifiers.ClearSpawnsStructureModifier::new)));
    }

    /** Registers the stock {@code neoforge:} biome modifier serializers,
     * matching NeoForge DeferredRegister contents. */
    public static void registerBiomeModifierSerializers() {
        var reg = net.neoforged.neoforge.registries.NeoForgeRegistries.BIOME_MODIFIER_SERIALIZERS;
        net.minecraft.core.Registry.register(reg,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "none"),
            com.mojang.serialization.MapCodec.unit(net.neoforged.neoforge.common.world.NoneBiomeModifier::new));
        net.minecraft.core.Registry.register(reg,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "add_features"),
            addFeaturesCodec());
        net.minecraft.core.Registry.register(reg,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "remove_features"),
            removeFeaturesCodec());
        net.minecraft.core.Registry.register(reg,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "add_spawns"),
            addSpawnsCodec());
        net.minecraft.core.Registry.register(reg,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "remove_spawns"),
            removeSpawnsCodec());
        net.minecraft.core.Registry.register(reg,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "add_carvers"),
            addCarversCodec());
        net.minecraft.core.Registry.register(reg,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "remove_carvers"),
            removeCarversCodec());
        net.minecraft.core.Registry.register(reg,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "add_spawn_costs"),
            addSpawnCostsCodec());
        net.minecraft.core.Registry.register(reg,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "remove_spawn_costs"),
            removeSpawnCostsCodec());
    }

    private static com.mojang.serialization.MapCodec<net.neoforged.neoforge.common.world.BiomeModifiers.AddCarversBiomeModifier> addCarversCodec() {
        return com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(
            (com.mojang.serialization.codecs.RecordCodecBuilder.Instance<net.neoforged.neoforge.common.world.BiomeModifiers.AddCarversBiomeModifier> b) -> b.group(
                net.minecraft.world.level.biome.Biome.LIST_CODEC.fieldOf("biomes").forGetter(net.neoforged.neoforge.common.world.BiomeModifiers.AddCarversBiomeModifier::biomes),
                net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver.LIST_CODEC.fieldOf("carvers").forGetter(net.neoforged.neoforge.common.world.BiomeModifiers.AddCarversBiomeModifier::carvers),
                net.minecraft.world.level.levelgen.GenerationStep.Carving.CODEC.fieldOf("step").forGetter(net.neoforged.neoforge.common.world.BiomeModifiers.AddCarversBiomeModifier::step)
            ).apply(b, net.neoforged.neoforge.common.world.BiomeModifiers.AddCarversBiomeModifier::new));
    }

    private static com.mojang.serialization.MapCodec<net.neoforged.neoforge.common.world.BiomeModifiers.RemoveCarversBiomeModifier> removeCarversCodec() {
        return com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(
            (com.mojang.serialization.codecs.RecordCodecBuilder.Instance<net.neoforged.neoforge.common.world.BiomeModifiers.RemoveCarversBiomeModifier> b) -> b.group(
                net.minecraft.world.level.biome.Biome.LIST_CODEC.fieldOf("biomes").forGetter(net.neoforged.neoforge.common.world.BiomeModifiers.RemoveCarversBiomeModifier::biomes),
                net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver.LIST_CODEC.fieldOf("carvers").forGetter(net.neoforged.neoforge.common.world.BiomeModifiers.RemoveCarversBiomeModifier::carvers),
                com.mojang.serialization.Codec.either(
                    net.minecraft.world.level.levelgen.GenerationStep.Carving.CODEC.listOf(),
                    net.minecraft.world.level.levelgen.GenerationStep.Carving.CODEC).<java.util.Set<net.minecraft.world.level.levelgen.GenerationStep.Carving>>xmap(
                    e -> e.map(java.util.Set::copyOf, java.util.Set::of),
                    set -> set.size() == 1 ? com.mojang.datafixers.util.Either.right(set.toArray(net.minecraft.world.level.levelgen.GenerationStep.Carving[]::new)[0]) : com.mojang.datafixers.util.Either.left(java.util.List.copyOf(set)))
                .optionalFieldOf("steps", java.util.EnumSet.allOf(net.minecraft.world.level.levelgen.GenerationStep.Carving.class)).forGetter(net.neoforged.neoforge.common.world.BiomeModifiers.RemoveCarversBiomeModifier::steps)
            ).apply(b, net.neoforged.neoforge.common.world.BiomeModifiers.RemoveCarversBiomeModifier::new));
    }

    private static com.mojang.serialization.MapCodec<net.neoforged.neoforge.common.world.BiomeModifiers.AddSpawnCostsBiomeModifier> addSpawnCostsCodec() {
        return com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(
            (com.mojang.serialization.codecs.RecordCodecBuilder.Instance<net.neoforged.neoforge.common.world.BiomeModifiers.AddSpawnCostsBiomeModifier> b) -> b.group(
                net.minecraft.world.level.biome.Biome.LIST_CODEC.fieldOf("biomes").forGetter(net.neoforged.neoforge.common.world.BiomeModifiers.AddSpawnCostsBiomeModifier::biomes),
                net.minecraft.core.RegistryCodecs.homogeneousList(net.minecraft.core.registries.Registries.ENTITY_TYPE).fieldOf("entity_types").forGetter(net.neoforged.neoforge.common.world.BiomeModifiers.AddSpawnCostsBiomeModifier::entityTypes),
                net.minecraft.world.level.biome.MobSpawnSettings.MobSpawnCost.CODEC.fieldOf("spawn_cost").forGetter(net.neoforged.neoforge.common.world.BiomeModifiers.AddSpawnCostsBiomeModifier::spawnCost)
            ).apply(b, net.neoforged.neoforge.common.world.BiomeModifiers.AddSpawnCostsBiomeModifier::new));
    }

    private static com.mojang.serialization.MapCodec<net.neoforged.neoforge.common.world.BiomeModifiers.RemoveSpawnCostsBiomeModifier> removeSpawnCostsCodec() {
        return com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(
            (com.mojang.serialization.codecs.RecordCodecBuilder.Instance<net.neoforged.neoforge.common.world.BiomeModifiers.RemoveSpawnCostsBiomeModifier> b) -> b.group(
                net.minecraft.world.level.biome.Biome.LIST_CODEC.fieldOf("biomes").forGetter(net.neoforged.neoforge.common.world.BiomeModifiers.RemoveSpawnCostsBiomeModifier::biomes),
                net.minecraft.core.RegistryCodecs.homogeneousList(net.minecraft.core.registries.Registries.ENTITY_TYPE).fieldOf("entity_types").forGetter(net.neoforged.neoforge.common.world.BiomeModifiers.RemoveSpawnCostsBiomeModifier::entityTypes)
            ).apply(b, net.neoforged.neoforge.common.world.BiomeModifiers.RemoveSpawnCostsBiomeModifier::new));
    }

    private static com.mojang.serialization.MapCodec<net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier> addFeaturesCodec() {
        return com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(
            (com.mojang.serialization.codecs.RecordCodecBuilder.Instance<net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier> b) -> b.group(
                net.minecraft.world.level.biome.Biome.LIST_CODEC.fieldOf("biomes").forGetter(net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier::biomes),
                net.minecraft.world.level.levelgen.placement.PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier::features),
                net.minecraft.world.level.levelgen.GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier::step)
            ).apply(b, net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier::new));
    }

    private static com.mojang.serialization.MapCodec<net.neoforged.neoforge.common.world.BiomeModifiers.RemoveFeaturesBiomeModifier> removeFeaturesCodec() {
        return com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(
            (com.mojang.serialization.codecs.RecordCodecBuilder.Instance<net.neoforged.neoforge.common.world.BiomeModifiers.RemoveFeaturesBiomeModifier> b) -> b.group(
                net.minecraft.world.level.biome.Biome.LIST_CODEC.fieldOf("biomes").forGetter(net.neoforged.neoforge.common.world.BiomeModifiers.RemoveFeaturesBiomeModifier::biomes),
                net.minecraft.world.level.levelgen.placement.PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(net.neoforged.neoforge.common.world.BiomeModifiers.RemoveFeaturesBiomeModifier::features),
                com.mojang.serialization.Codec.either(
                    net.minecraft.world.level.levelgen.GenerationStep.Decoration.CODEC.listOf(),
                    net.minecraft.world.level.levelgen.GenerationStep.Decoration.CODEC).<java.util.Set<net.minecraft.world.level.levelgen.GenerationStep.Decoration>>xmap(
                    e -> e.map(java.util.Set::copyOf, java.util.Set::of),
                    set -> set.size() == 1 ? com.mojang.datafixers.util.Either.right(set.toArray(net.minecraft.world.level.levelgen.GenerationStep.Decoration[]::new)[0]) : com.mojang.datafixers.util.Either.left(java.util.List.copyOf(set)))
                .optionalFieldOf("steps", java.util.EnumSet.allOf(net.minecraft.world.level.levelgen.GenerationStep.Decoration.class)).forGetter(net.neoforged.neoforge.common.world.BiomeModifiers.RemoveFeaturesBiomeModifier::steps)
            ).apply(b, net.neoforged.neoforge.common.world.BiomeModifiers.RemoveFeaturesBiomeModifier::new));
    }

    private static com.mojang.serialization.MapCodec<net.neoforged.neoforge.common.world.BiomeModifiers.AddSpawnsBiomeModifier> addSpawnsCodec() {
        return com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(
            (com.mojang.serialization.codecs.RecordCodecBuilder.Instance<net.neoforged.neoforge.common.world.BiomeModifiers.AddSpawnsBiomeModifier> b) -> b.group(
                net.minecraft.world.level.biome.Biome.LIST_CODEC.fieldOf("biomes").forGetter(net.neoforged.neoforge.common.world.BiomeModifiers.AddSpawnsBiomeModifier::biomes),
                com.mojang.serialization.Codec.either(
                    net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData.CODEC.listOf(),
                    net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData.CODEC).xmap(
                    e -> e.map(java.util.function.Function.identity(), java.util.List::of),
                    list -> list.size() == 1 ? com.mojang.datafixers.util.Either.right(list.get(0)) : com.mojang.datafixers.util.Either.left(list)
                ).fieldOf("spawners").forGetter(net.neoforged.neoforge.common.world.BiomeModifiers.AddSpawnsBiomeModifier::spawners)
            ).apply(b, net.neoforged.neoforge.common.world.BiomeModifiers.AddSpawnsBiomeModifier::new));
    }

    private static com.mojang.serialization.MapCodec<net.neoforged.neoforge.common.world.BiomeModifiers.RemoveSpawnsBiomeModifier> removeSpawnsCodec() {
        return com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(
            (com.mojang.serialization.codecs.RecordCodecBuilder.Instance<net.neoforged.neoforge.common.world.BiomeModifiers.RemoveSpawnsBiomeModifier> b) -> b.group(
                net.minecraft.world.level.biome.Biome.LIST_CODEC.fieldOf("biomes").forGetter(net.neoforged.neoforge.common.world.BiomeModifiers.RemoveSpawnsBiomeModifier::biomes),
                net.minecraft.core.RegistryCodecs.homogeneousList(net.minecraft.core.registries.Registries.ENTITY_TYPE).fieldOf("entity_types").forGetter(net.neoforged.neoforge.common.world.BiomeModifiers.RemoveSpawnsBiomeModifier::entityTypes)
            ).apply(b, net.neoforged.neoforge.common.world.BiomeModifiers.RemoveSpawnsBiomeModifier::new));
    }

    /** Registers the stock {@code neoforge:} condition serializers,
     * matching NeoForge's CONDITION_CODECS DeferredRegister. */
    public static void registerConditionCodecs() {
        var reg = net.neoforged.neoforge.registries.NeoForgeRegistries.CONDITION_CODECS;
        net.minecraft.core.Registry.register(reg, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "and"), net.neoforged.neoforge.common.conditions.AndCondition.CODEC);
        net.minecraft.core.Registry.register(reg, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "false"), net.neoforged.neoforge.common.conditions.FalseCondition.CODEC);
        net.minecraft.core.Registry.register(reg, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "item_exists"), net.neoforged.neoforge.common.conditions.ItemExistsCondition.CODEC);
        net.minecraft.core.Registry.register(reg, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "mod_loaded"), net.neoforged.neoforge.common.conditions.ModLoadedCondition.CODEC);
        net.minecraft.core.Registry.register(reg, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "not"), net.neoforged.neoforge.common.conditions.NotCondition.CODEC);
        net.minecraft.core.Registry.register(reg, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "or"), net.neoforged.neoforge.common.conditions.OrCondition.CODEC);
        net.minecraft.core.Registry.register(reg, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "tag_empty"), net.neoforged.neoforge.common.conditions.TagEmptyCondition.CODEC);
        net.minecraft.core.Registry.register(reg, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "true"), net.neoforged.neoforge.common.conditions.TrueCondition.CODEC);
    }

    /** Registers the stock {@code neoforge:} fluid type (water). */
    /** Vanilla fluid types self-register in the static holders above; kept for the old call site. */
    public static void registerFluidTypes() {
    }

    public static void registerIngredientTypes() {
        var reg = net.neoforged.neoforge.registries.NeoForgeRegistries.INGREDIENT_TYPES;
        net.minecraft.core.Registry.register(reg, BLOCK_TAG_INGREDIENT.getId(),
            new net.neoforged.neoforge.common.crafting.IngredientType<>(
                net.neoforged.neoforge.common.crafting.BlockTagIngredient.CODEC));
        net.minecraft.core.Registry.register(reg, COMPOUND_INGREDIENT_TYPE.getId(),
            new net.neoforged.neoforge.common.crafting.IngredientType<>(
                net.neoforged.neoforge.common.crafting.CompoundIngredient.CODEC));
        net.minecraft.core.Registry.register(reg, DATA_COMPONENT_INGREDIENT_TYPE.getId(),
            new net.neoforged.neoforge.common.crafting.IngredientType<>(
                net.neoforged.neoforge.common.crafting.DataComponentIngredient.CODEC));
        net.minecraft.core.Registry.register(reg, DIFFERENCE_INGREDIENT_TYPE.getId(),
            new net.neoforged.neoforge.common.crafting.IngredientType<>(
                net.neoforged.neoforge.common.crafting.DifferenceIngredient.CODEC));
        net.minecraft.core.Registry.register(reg, INTERSECTION_INGREDIENT_TYPE.getId(),
            new net.neoforged.neoforge.common.crafting.IngredientType<>(
                net.neoforged.neoforge.common.crafting.IntersectionIngredient.CODEC));
    }
}
