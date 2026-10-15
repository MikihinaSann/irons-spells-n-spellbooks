package net.acetheeldritchking.discerning_the_eldritch.utils;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class DTETags {
   public static final TagKey<Item> RITUAL_FOCUS = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "ritual_focus").toString())
   );
   public static final TagKey<Item> ARMORS_FOR_IDLE = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "armors_for_idle").toString())
   );
   public static final TagKey<Item> FROZEN_WEAPONS = TagKey.create(Registries.ITEM,
      ResourceLocation.parse(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "frozen_weapons").toString())
   );
   public static final TagKey<EntityType<?>> APOTHIC_ALLIES = TagKey.create(
      Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "apothic_allies")
   );
   public static final TagKey<EntityType<?>> BLOOD_ALLIES = TagKey.create(
      Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "blood_allies")
   );
   public static final TagKey<EntityType<?>> CRUSADER_ALLIES = TagKey.create(
      Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "crusader_allies")
   );
   public static final TagKey<MobEffect> BYPASS_ABRACADABRA = TagKey.create(
      Registries.MOB_EFFECT, ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "bypass_abracadabra")
   );
}
