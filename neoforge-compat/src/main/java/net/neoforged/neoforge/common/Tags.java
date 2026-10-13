package net.neoforged.neoforge.common;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEntityTypeTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalFluidTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluid;

/**
 * Maps NeoForge's conventional {@code Tags} constants onto Fabric's conventional tags.
 */
public class Tags {
    public static class Items {
        public static final TagKey<Item> INGOTS_IRON = ConventionalItemTags.IRON_INGOTS;
        public static final TagKey<Item> INGOTS_GOLD = ConventionalItemTags.GOLD_INGOTS;
        public static final TagKey<Item> INGOTS_COPPER = ConventionalItemTags.COPPER_INGOTS;
        public static final TagKey<Item> INGOTS_NETHERITE = ConventionalItemTags.NETHERITE_INGOTS;
        public static final TagKey<Item> LEATHERS = ConventionalItemTags.LEATHERS;
        public static final TagKey<Item> GEMS_AMETHYST = TagKey.create(net.minecraft.core.registries.Registries.ITEM,
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "gems/amethyst"));
    }

    public static class Fluids {
        public static final TagKey<Fluid> WATER = ConventionalFluidTags.WATER;
    }

    public static class EntityTypes {
        public static final TagKey<EntityType<?>> BOSSES = ConventionalEntityTypeTags.BOSSES;
    }

    public static class DamageTypes {
        public static final TagKey<DamageType> IS_MAGIC = TagKey.create(net.minecraft.core.registries.Registries.DAMAGE_TYPE, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("c", "is_magic"));
        public static final TagKey<DamageType> IS_PHYSICAL = TagKey.create(net.minecraft.core.registries.Registries.DAMAGE_TYPE, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("neoforge", "is_physical"));
    }

    public static class Biomes {
        public static final TagKey<Biome> NO_DEFAULT_MONSTERS = ConventionalBiomeTags.NO_DEFAULT_MONSTERS;
    }
}
