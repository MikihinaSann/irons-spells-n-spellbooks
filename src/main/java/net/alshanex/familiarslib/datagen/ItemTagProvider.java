package net.alshanex.familiarslib.datagen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ItemTagProvider {
   public static final TagKey<Item> SOUND_FOCUS = TagKey.create(Registries.ITEM, new ResourceLocation("familiarslib", "sound_focus"));
}
