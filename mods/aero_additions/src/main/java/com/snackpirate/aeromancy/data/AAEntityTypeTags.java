package com.snackpirate.aeromancy.data;

import com.snackpirate.aeromancy.Aeromancy;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class AAEntityTypeTags {
   public static final TagKey<EntityType<?>> REFLECTION_IMMUNE = TagKey.create(Registries.ENTITY_TYPE, Aeromancy.id("reflection_immune"));
   public static final TagKey<EntityType<?>> ASPHYXIATION_IMMUNE = TagKey.create(Registries.ENTITY_TYPE, Aeromancy.id("asphyxiation_immune"));
}
