package net.acetheeldritchking.aces_spell_utils.utils;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.acetheeldritchking.aces_spell_utils.AcesSpellUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class ASTags {
   public static final TagKey<EntityType<?>> BOSS_LIKE_ENTITES = TagKey.create(
      Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("aces_spell_utils", "boss_like_entities")
   );
   public static final TagKey<EntityType<?>> MANA_STEAL_WHITELIST = TagKey.create(
      Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("aces_spell_utils", "mana_steal_whitelist")
   );
   public static final TagKey<EntityType<?>> MANA_REND_WHITELIST = TagKey.create(
      Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("aces_spell_utils", "mana_rend_whitelist")
   );
   public static TagKey<AbstractSpell> STOMP_LIKE_SPELL = createSpellTag(AcesSpellUtils.id("stomp_like_spell"));
   public static TagKey<AbstractSpell> SLASH_LIKE_SPELL = createSpellTag(AcesSpellUtils.id("slash_like_spell"));

   public static TagKey<AbstractSpell> createSpellTag(ResourceLocation tag) {
      return new TagKey(SpellRegistry.SPELL_REGISTRY_KEY, tag);
   }
}
