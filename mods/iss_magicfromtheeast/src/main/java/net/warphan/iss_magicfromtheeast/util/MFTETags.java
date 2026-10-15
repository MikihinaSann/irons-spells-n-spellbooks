package net.warphan.iss_magicfromtheeast.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.warphan.iss_magicfromtheeast.ISS_MagicFromTheEast;

public class MFTETags {
   public static final TagKey<Item> SYMMETRY_FOCUS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "symmetry_focus"));
   public static final TagKey<Item> SPIRIT_FOCUS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "spirit_focus"));
   public static final TagKey<Item> DUNE_FOCUS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "dune_focus.json"));
   public static final TagKey<Item> SOULPIERCER = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "soulpiercer"));
   public static final TagKey<Item> AMMO_LOAD_WEAPON = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "ammo_load_weapon"));
   public static final TagKey<Item> SOUL_MELEE_WEAPON = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "melee_soul_weapon"));
   public static final TagKey<BannerPattern> BALANCE_PATTERN_ITEM = bannerTag("pattern_item/balance");
   public static final TagKey<EntityType<?>> SPIRIT_CHALLENGING_IMMUNE = TagKey.create(
      Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", "spirit_challenging_immune")
   );

   private static TagKey<BannerPattern> bannerTag(String name) {
      return TagKey.create(Registries.BANNER_PATTERN, ISS_MagicFromTheEast.id(name));
   }
}
