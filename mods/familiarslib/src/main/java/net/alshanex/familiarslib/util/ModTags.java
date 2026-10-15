package net.alshanex.familiarslib.util;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class ModTags {
   public static final TagKey<Item> FAMILIAR_TAMING = TagKey.create(Registries.ITEM, new ResourceLocation("familiarslib", "familiar_taming"));
   public static final TagKey<Item> BERRY = TagKey.create(Registries.ITEM, new ResourceLocation("familiarslib", "crystal_berry"));
   public static final TagKey<Item> FRAGMENTS = TagKey.create(Registries.ITEM, new ResourceLocation("familiarslib", "fragments"));
   public static final TagKey<EntityType<?>> HUNTER_CANNOT_MARK = TagKey.create(
      Registries.ENTITY_TYPE, new ResourceLocation("familiarslib", "hunter_cannot_mark")
   );
   public static final TagKey<EntityType<?>> HUNTER_CANNOT_ATTACK_IN_HOME = TagKey.create(
      Registries.ENTITY_TYPE, new ResourceLocation("familiarslib", "hunter_cannot_attack_in_home")
   );
   public static final TagKey<Item> ILLUSIONIST_STEALS = TagKey.create(Registries.ITEM, new ResourceLocation("familiarslib", "illusionist_steals"));

   public static TagKey<AbstractSpell> create(ResourceLocation name) {
      return new TagKey(SpellRegistry.SPELL_REGISTRY_KEY, name);
   }
}
