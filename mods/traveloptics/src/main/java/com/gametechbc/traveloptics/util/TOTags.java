package com.gametechbc.traveloptics.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class TOTags {
   public static final TagKey<Item> CAN_CAST_REVERSAL = TagKey.create(Registries.ITEM, new ResourceLocation("traveloptics", "can_cast_reversal"));
   public static final TagKey<Item> SPELL_IMBUED_CURIO = TagKey.create(Registries.ITEM, new ResourceLocation("traveloptics", "spell_imbued_curio"));
   public static final TagKey<Item> MAGIC_SCYTHE_WEAPON_TYPE = TagKey.create(Registries.ITEM, new ResourceLocation("traveloptics", "magic_scythe_weapon_type"));
   public static final TagKey<Item> MAGIC_GUNBLADE_WEAPON_TYPE = TagKey.create(
      Registries.ITEM, new ResourceLocation("traveloptics", "magic_gunblade_weapon_type")
   );
   public static final TagKey<Item> MAGIC_POLEARM_WEAPON_TYPE = TagKey.create(
      Registries.ITEM, new ResourceLocation("traveloptics", "magic_polearm_weapon_type")
   );
   public static final TagKey<Item> MAGIC_GAUNTLET_WEAPON_TYPE = TagKey.create(
      Registries.ITEM, new ResourceLocation("traveloptics", "magic_gauntlet_weapon_type")
   );
   public static final TagKey<Item> MAGIC_KATANA_WEAPON_TYPE = TagKey.create(Registries.ITEM, new ResourceLocation("traveloptics", "magic_katana_weapon_type"));
   public static final TagKey<Item> MAGIC_SWORD_WEAPON_TYPE = TagKey.create(Registries.ITEM, new ResourceLocation("traveloptics", "magic_sword_weapon_type"));
   public static final TagKey<Item> MAGIC_GREATSWORD_WEAPON_TYPE = TagKey.create(
      Registries.ITEM, new ResourceLocation("traveloptics", "magic_greatsword_weapon_type")
   );
   public static final TagKey<Item> ECHO_CURIO = TagKey.create(Registries.ITEM, new ResourceLocation("traveloptics", "echo_curio"));
   public static final TagKey<Item> AQUA_FOCUS = TagKey.create(Registries.ITEM, new ResourceLocation("traveloptics", "aqua_focus"));
   public static final TagKey<Block> TECTONIC_RIFT_DESTROYABLE = TagKey.create(
      Registries.BLOCK, new ResourceLocation("traveloptics", "tectonic_rift_destroyable")
   );
   public static final TagKey<Block> EXTINCTION_BLOCK_BLACKLIST = TagKey.create(
      Registries.BLOCK, new ResourceLocation("traveloptics", "extinction_block_blacklist")
   );
   public static final TagKey<Block> EXTREMELY_VALUABLES = TagKey.create(Registries.BLOCK, new ResourceLocation("traveloptics", "extremely_valuables"));
   public static final TagKey<EntityType<?>> AERIAL_COLLAPSE_DR = TagKey.create(
      Registries.ENTITY_TYPE, new ResourceLocation("traveloptics", "aerial_collapse_dr")
   );
   public static final TagKey<EntityType<?>> SPECTRAL_SHIFT_BLACKLIST = TagKey.create(
      Registries.ENTITY_TYPE, new ResourceLocation("traveloptics", "spectral_shift_blacklist")
   );
   public static final TagKey<EntityType<?>> SPECTRAL_BLINK_BLACKLIST = TagKey.create(
      Registries.ENTITY_TYPE, new ResourceLocation("traveloptics", "spectral_blink_blacklist")
   );
   public static final TagKey<EntityType<?>> ELEMENT_FIRE = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("traveloptics", "element_fire"));
   public static final TagKey<EntityType<?>> SENSED_ENTITIES = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("traveloptics", "sensed_entities"));
   public static final TagKey<EntityType<?>> APPROACH_ENTITY_BLACKLIST = TagKey.create(
      Registries.ENTITY_TYPE, new ResourceLocation("traveloptics", "approach_entity_blacklist")
   );

   public TOTags() {
      super();
   }
}
