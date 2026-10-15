package net.hazen.hazennstuff.Datagen;

import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import net.hazen.hazennstuff.HazenNStuff;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.BiomeBuilder;
import net.minecraft.world.level.biome.Biome.TemperatureModifier;
import net.minecraft.world.level.biome.BiomeSpecialEffects.Builder;
import net.minecraft.world.level.block.Block;

public class HnSTags {
   public static final TagKey<Item> RADIANCE_FOCUS = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "focus/radiance_focus").toString())
   );
   public static final TagKey<Item> SHADOW_FOCUS = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "focus/shadow_focus").toString())
   );
   public static final TagKey<Item> COSMIC_FOCUS = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "focus/cosmic_focus").toString())
   );
   public static final TagKey<Item> MELEE_FOCUS = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "focus/melee_focus").toString())
   );
   public static final TagKey<Item> ARCHERY_FOCUS = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "focus/archery_focus").toString())
   );
   public static final TagKey<Item> HEALTH_FOCUS = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "focus/health_focus").toString())
   );
   public static final TagKey<Item> ZENALITE = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "ores/zenalite").toString())
   );
   public static final TagKey<Item> SCHOOL_HELMET = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "armors/school/school_helmet").toString())
   );
   public static final TagKey<Item> SCHOOL_CHESTPLATE = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "armors/school/school_chestplate").toString())
   );
   public static final TagKey<Item> SCHOOL_LEGGINGS = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "armors/school/school_leggings").toString())
   );
   public static final TagKey<Item> SCHOOL_BOOTS = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "armors/school/school_boots").toString())
   );
   public static final TagKey<Item> NETHERITE_BATTLEMAGE_HELMET = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "armors/netherite_battlemage/netherite_battlemage_helmet").toString())
   );
   public static final TagKey<Item> NETHERITE_BATTLEMAGE_CHESTPLATE = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "armors/netherite_battlemage/netherite_battlemage_chestplate").toString())
   );
   public static final TagKey<Item> NETHERITE_BATTLEMAGE_LEGGINGS = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "armors/netherite_battlemage/netherite_battlemage_leggings").toString())
   );
   public static final TagKey<Item> NETHERITE_BATTLEMAGE_BOOTS = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "armors/netherite_battlemage/netherite_battlemage_boots").toString())
   );
   public static final TagKey<Item> PURE_HELMET = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "armors/pure/pure_helmet").toString())
   );
   public static final TagKey<Item> PURE_CHESTPLATE = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "armors/pure/pure_chestplate").toString())
   );
   public static final TagKey<Item> PURE_LEGGINGS = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "armors/pure/pure_leggings").toString())
   );
   public static final TagKey<Item> PURE_BOOTS = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "armors/pure/pure_boots").toString())
   );
   public static final TagKey<Item> ARMORS_FOR_IDLE = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "armors/pure/pure_boots").toString())
   );
   public static final TagKey<Item> ELDRITCH_RUNES = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "items/eldritch_runes").toString())
   );
   public static final TagKey<Item> CINDEROUS_EQUIPMENT = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "cinderous_equipment").toString())
   );
   public static final TagKey<Item> HARMONIOUS_EQUIPMENT = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "harmonious_equipment").toString())
   );
   public static final TagKey<Item> IONIC_SPLITTER = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "ionic_splitter").toString())
   );
   public static final TagKey<Item> LIGHTNING_SPELLBOOK = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "lightning_spellbook").toString())
   );
   public static final TagKey<Item> LIGHTNING_SHEATH = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "lightning_sheath").toString())
   );
   public static final ResourceKey<Biome> WISEWOOD_FOREST = create("wisewood_forest");
   public static final ResourceKey<Biome> PERMAFROST_FOREST = create("permafrost_forest");

   private static BiomeBuilder baseBiomeBuilder(
      Builder specialEffects,
      net.minecraft.world.level.biome.MobSpawnSettings.Builder mobSpawnSettings,
      net.minecraft.world.level.biome.BiomeGenerationSettings.Builder genSettings
   ) {
      return new BiomeBuilder()
         .hasPrecipitation(true)
         .temperature(0.5F)
         .downfall(0.5F)
         .specialEffects(specialEffects.build())
         .mobSpawnSettings(mobSpawnSettings.build())
         .generationSettings(genSettings.build())
         .temperatureAdjustment(TemperatureModifier.NONE);
   }

   private static Builder baseEffectsBuilder() {
      return baseEffectsBuilder(true);
   }

   private static Builder baseEffectsBuilder(boolean ambientParticle) {
      Builder builder = new Builder()
         .fogColor(5195923)
         .foliageColorOverride(5195923)
         .grassColorOverride(5195923)
         .waterColor(6187416)
         .waterFogColor(1184291)
         .skyColor(5658761);
      if (ambientParticle) {
         builder.ambientParticle(new AmbientParticleSettings((ParticleOptions)ParticleRegistry.SNOWFLAKE_PARTICLE.get(), 0.001F));
      }

      return builder;
   }

   public static ResourceKey<Biome> create(String name) {
      return ResourceKey.create(Registries.BIOME, HazenNStuff.id(name));
   }

   private static TagKey<Block> createTag(String name) {
      return TagKey.create(Registries.BLOCK,ResourceLocation.fromNamespaceAndPath("hazennstuff", name));
   }

   public static class Biomes {
      private static TagKey<Biome> createTag(String name) {
         return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("hazennstuff", name));
      }
   }

   public static class Blocks {
      public static final TagKey<Block> NEEDS_MITRHIL_TOOL = TagKey.create(Registries.BLOCK,
         ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "needs_mithril_tool").toString())
      );
      public static final TagKey<Block> INCORRECT_FOR_MITHRIL_TOOl = TagKey.create(Registries.BLOCK,
         ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("hazennstuff", "incorrect_for_mithril_tool").toString())
      );
   }

   public static class Items {
      public static final TagKey<Item> MITHRIL_REPAIRABLE = createTag("mithril_repairable");

      private static TagKey<Item> createTag(String name) {
         return TagKey.create(Registries.ITEM,ResourceLocation.fromNamespaceAndPath("hazennstuff", name));
      }
   }
}
