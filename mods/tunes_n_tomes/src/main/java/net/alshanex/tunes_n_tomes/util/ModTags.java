package net.alshanex.tunes_n_tomes.util;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class ModTags {
   public static final TagKey<EntityType<?>> CANNOT_TARGET_MARKERS = TagKey.create(
      Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "cannot_target_markers")
   );
   public static final TagKey<AbstractSpell> RESONANCE_BLACKLIST = create(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "resonance_blacklist"));
   public static final TagKey<AbstractSpell> NOT_PLACE_MARKER = create(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "not_place_marker"));

   public static TagKey<AbstractSpell> create(ResourceLocation name) {
      return new TagKey(SpellRegistry.SPELL_REGISTRY_KEY, name);
   }
}
