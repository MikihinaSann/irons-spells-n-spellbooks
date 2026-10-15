package net.acetheeldritchking.cataclysm_spellbooks.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class CSTags {
   public static final TagKey<Item> ABYSSAL_FOCUS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "abyssal_focus"));
   public static final TagKey<Item> TECHNOMANCY_FOCUS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "abyssal_focus"));
   public static final TagKey<Item> ARMORS_FOR_IDLE = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "armors_for_idle"));
   public static final TagKey<Item> ARMORS_FOR_FLIGHT = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "armors_for_flight"));
}
