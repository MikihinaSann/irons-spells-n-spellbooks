package net.alshanex.alshanex_familiars.util;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public class AlshanexFamiliarsTags {
   public static final TagKey<EntityType<?>> NECROMANCER_BLACKLIST = TagKey.create(
      Registries.ENTITY_TYPE, new ResourceLocation("alshanex_familiars", "necromancer_ability_blacklist")
   );
   public static final TagKey<Block> DRUID_GROWABLES = TagKey.create(
      Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "druid_growables")
   );
   public static final TagKey<EntityType<?>> FREEZING_WHITELIST = TagKey.create(
      Registries.ENTITY_TYPE, new ResourceLocation("alshanex_familiars", "freezeable_entities")
   );
   public static final TagKey<Block> BLOCK_FREEZE_BLACKLIST = TagKey.create(
      Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "block_freeze_blacklist")
   );
   public static final TagKey<Block> FREEZE_DESTROYABLE = TagKey.create(
      Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "freeze_destroyable")
   );
   public static final TagKey<Block> FREEZE_IGNORE = TagKey.create(
      Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "freeze_ignore")
   );
   public static TagKey<AbstractSpell> ARCHMAGE_CANNOT_CREATE = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "archmage_cannot_create"));
   public static TagKey<AbstractSpell> ARCHMAGE_CANNOT_REROLL = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "archmage_cannot_reroll"));
   public static final TagKey<EntityType<?>> DRUID_CANNOT_BREED = TagKey.create(
      Registries.ENTITY_TYPE, new ResourceLocation("alshanex_familiars", "druid_cannot_breed")
   );
   public static final TagKey<Block> MAGE_SPAWNABLE_ON = TagKey.create(
      Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "mage_spawnable_on")
   );
   public static final TagKey<Block> ARCHMAGE_SPAWNABLE_ON = TagKey.create(
      Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "archmage_spawnable_on")
   );
   public static final TagKey<Block> SUMMONER_SPAWNABLE_ON = TagKey.create(
      Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "summoner_spawnable_on")
   );
   public static final TagKey<Block> NECROMANCER_SPAWNABLE_ON = TagKey.create(
      Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "necromancer_spawnable_on")
   );
   public static final TagKey<Block> HUNTER_SPAWNABLE_ON = TagKey.create(
      Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "hunter_spawnable_on")
   );
   public static final TagKey<Block> DRUID_SPAWNABLE_ON = TagKey.create(
      Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "druid_spawnable_on")
   );
   public static final TagKey<Block> SCORCHER_SPAWNABLE_ON = TagKey.create(
      Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "scorcher_spawnable_on")
   );
   public static final TagKey<Block> FROSTLING_SPAWNABLE_ON = TagKey.create(
      Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "frostling_spawnable_on")
   );
   public static TagKey<AbstractSpell> ARCHMAGE_ATTACK_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "archmage/attack"));
   public static TagKey<AbstractSpell> ARCHMAGE_DEFENSE_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "archmage/defense"));
   public static TagKey<AbstractSpell> ARCHMAGE_MOVEMENT_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "archmage/movement"));
   public static TagKey<AbstractSpell> ARCHMAGE_SUPPORT_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "archmage/support"));
   public static TagKey<AbstractSpell> MAGE_ATTACK_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "mage/attack"));
   public static TagKey<AbstractSpell> MAGE_DEFENSE_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "mage/defense"));
   public static TagKey<AbstractSpell> MAGE_MOVEMENT_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "mage/movement"));
   public static TagKey<AbstractSpell> MAGE_SUPPORT_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "mage/support"));
   public static TagKey<AbstractSpell> BARD_ATTACK_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "bard/attack"));
   public static TagKey<AbstractSpell> BARD_DEFENSE_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "bard/defense"));
   public static TagKey<AbstractSpell> BARD_MOVEMENT_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "bard/movement"));
   public static TagKey<AbstractSpell> BARD_SUPPORT_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "bard/support"));
   public static TagKey<AbstractSpell> CLERIC_ATTACK_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "cleric/attack"));
   public static TagKey<AbstractSpell> CLERIC_DEFENSE_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "cleric/defense"));
   public static TagKey<AbstractSpell> CLERIC_MOVEMENT_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "cleric/movement"));
   public static TagKey<AbstractSpell> CLERIC_SUPPORT_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "cleric/support"));
   public static TagKey<AbstractSpell> CLERIC_HEALING_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "cleric/healing"));
   public static TagKey<AbstractSpell> CLERIC_BUFFING_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "cleric/buffing"));
   public static TagKey<AbstractSpell> DRUID_ATTACK_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "druid/attack"));
   public static TagKey<AbstractSpell> DRUID_DEFENSE_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "druid/defense"));
   public static TagKey<AbstractSpell> DRUID_MOVEMENT_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "druid/movement"));
   public static TagKey<AbstractSpell> DRUID_SUPPORT_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "druid/support"));
   public static TagKey<AbstractSpell> HUNTER_ATTACK_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "hunter/attack"));
   public static TagKey<AbstractSpell> HUNTER_DEFENSE_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "hunter/defense"));
   public static TagKey<AbstractSpell> HUNTER_MOVEMENT_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "hunter/movement"));
   public static TagKey<AbstractSpell> HUNTER_SUPPORT_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "hunter/support"));
   public static TagKey<AbstractSpell> ILLUSIONIST_ATTACK_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "illusionist/attack"));
   public static TagKey<AbstractSpell> ILLUSIONIST_DEFENSE_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "illusionist/defense"));
   public static TagKey<AbstractSpell> ILLUSIONIST_MOVEMENT_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "illusionist/movement"));
   public static TagKey<AbstractSpell> ILLUSIONIST_SUPPORT_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "illusionist/support"));
   public static TagKey<AbstractSpell> FROSTLING_ATTACK_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "frostling/attack"));
   public static TagKey<AbstractSpell> FROSTLING_DEFENSE_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "frostling/defense"));
   public static TagKey<AbstractSpell> FROSTLING_MOVEMENT_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "frostling/movement"));
   public static TagKey<AbstractSpell> FROSTLING_SUPPORT_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "frostling/support"));
   public static TagKey<AbstractSpell> NECROMANCER_ATTACK_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "necromancer/attack"));
   public static TagKey<AbstractSpell> NECROMANCER_DEFENSE_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "necromancer/defense"));
   public static TagKey<AbstractSpell> NECROMANCER_MOVEMENT_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "necromancer/movement"));
   public static TagKey<AbstractSpell> NECROMANCER_SUPPORT_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "necromancer/support"));
   public static TagKey<AbstractSpell> PLAGUE_ATTACK_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "plague/attack"));
   public static TagKey<AbstractSpell> PLAGUE_DEFENSE_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "plague/defense"));
   public static TagKey<AbstractSpell> PLAGUE_MOVEMENT_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "plague/movement"));
   public static TagKey<AbstractSpell> PLAGUE_SUPPORT_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "plague/support"));
   public static TagKey<AbstractSpell> SCORCHER_ATTACK_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "scorcher/attack"));
   public static TagKey<AbstractSpell> SCORCHER_DEFENSE_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "scorcher/defense"));
   public static TagKey<AbstractSpell> SCORCHER_MOVEMENT_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "scorcher/movement"));
   public static TagKey<AbstractSpell> SCORCHER_SUPPORT_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "scorcher/support"));
   public static TagKey<AbstractSpell> SUMMONER_ATTACK_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "summoner/attack"));
   public static TagKey<AbstractSpell> SUMMONER_DEFENSE_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "summoner/defense"));
   public static TagKey<AbstractSpell> SUMMONER_MOVEMENT_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "summoner/movement"));
   public static TagKey<AbstractSpell> SUMMONER_SUPPORT_SPELLS = create(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "summoner/support"));
   public static TagKey<AbstractSpell> DRAGON_WARRIOR_ATTACK_SPELLS = create(
      ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "dragon_warrior/attack")
   );
   public static TagKey<AbstractSpell> DRAGON_WARRIOR_DEFENSE_SPELLS = create(
      ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "dragon_warrior/defense")
   );
   public static TagKey<AbstractSpell> DRAGON_WARRIOR_MOVEMENT_SPELLS = create(
      ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "dragon_warrior/movement")
   );
   public static TagKey<AbstractSpell> DRAGON_WARRIOR_SUPPORT_SPELLS = create(
      ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "dragon_warrior/support")
   );

   public static TagKey<AbstractSpell> create(ResourceLocation name) {
      return new TagKey(SpellRegistry.SPELL_REGISTRY_KEY, name);
   }

   public static boolean isAlshanexFamiliarsSpell(AbstractSpell spell) {
      return spell.getSpellResource().getNamespace().equals("alshanex_familiars");
   }
}
