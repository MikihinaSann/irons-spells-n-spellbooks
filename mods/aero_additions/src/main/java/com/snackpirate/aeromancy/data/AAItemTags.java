package com.snackpirate.aeromancy.data;

import com.snackpirate.aeromancy.Aeromancy;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class AAItemTags {
   public static final TagKey<Item> WIND_FOCUS = TagKey.create(Registries.ITEM, Aeromancy.id("wind_focus"));
   public static final TagKey<Item> FLIGHT_LIGHT = TagKey.create(Registries.ITEM, Aeromancy.id("flight_light"));
   public static final TagKey<Item> FLIGHT_HEAVY = TagKey.create(Registries.ITEM, Aeromancy.id("flight_heavy"));
}
