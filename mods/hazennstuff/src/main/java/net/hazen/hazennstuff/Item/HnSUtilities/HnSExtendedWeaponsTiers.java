package net.hazen.hazennstuff.Item.HnSUtilities;

import dev.shadowsoffire.apothic_attributes.api.ALObjects.Attributes;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.item.weapons.IronsWeaponTier;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import java.util.function.Supplier;
import net.acetheeldritchking.aces_spell_utils.registries.ASAttributeRegistry;
import net.hazen.hazennstuff.Datagen.HnSTags;
import net.hazen.hazennstuff.Registries.HnSAttributeRegistry;
import net.hazen.hazennstuff.Registries.HnSItemRegistry;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class HnSExtendedWeaponsTiers implements Tier, IronsWeaponTier {
   public static HnSExtendedWeaponsTiers ICE_PIKE = new HnSExtendedWeaponsTiers(
      8064,
      11.0F,
      -2.9F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.ICE_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(Attributes.COLD_DAMAGE, 5.0, Operation.ADD_VALUE),
      new AttributeContainer(net.minecraft.world.entity.ai.attributes.Attributes.ENTITY_INTERACTION_RANGE, 2.0, Operation.ADD_VALUE)
   );
   public static HnSExtendedWeaponsTiers FROSTBURN_DAGGER = new HnSExtendedWeaponsTiers(
      8064,
      7.0F,
      -1.7F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.ICE_SPELL_POWER, 0.2, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(Attributes.COLD_DAMAGE, 3.0, Operation.ADD_VALUE)
   );
   public static HnSExtendedWeaponsTiers DRACONIC_SPLITTER = new HnSExtendedWeaponsTiers(
      8064,
      12.0F,
      -2.6F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.FIRE_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(Attributes.FIRE_DAMAGE, 5.0, Operation.ADD_VALUE),
      new AttributeContainer(net.minecraft.world.entity.ai.attributes.Attributes.ENTITY_INTERACTION_RANGE, 2.0, Operation.ADD_VALUE)
   );
   public static HnSExtendedWeaponsTiers FIREBRAND = new HnSExtendedWeaponsTiers(
      8064,
      9.0F,
      -3.0F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.FIRE_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(Attributes.FIRE_DAMAGE, 5.0, Operation.ADD_VALUE),
      new AttributeContainer(net.minecraft.world.entity.ai.attributes.Attributes.ENTITY_INTERACTION_RANGE, 2.0, Operation.ADD_VALUE)
   );
   public static HnSExtendedWeaponsTiers VOLCANO = new HnSExtendedWeaponsTiers(
      8064,
      9.0F,
      -2.7F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.FIRE_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers FIREBLOSSOM_RAPIER = new HnSExtendedWeaponsTiers(
      8064,
      8.0F,
      -1.7F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.FIRE_SPELL_POWER, 0.2, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.NATURE_SPELL_POWER, 0.2, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(Attributes.FIRE_DAMAGE, 3.0, Operation.ADD_VALUE),
      new AttributeContainer(net.minecraft.world.entity.ai.attributes.Attributes.ENTITY_INTERACTION_RANGE, 2.0, Operation.ADD_VALUE)
   );
   public static HnSExtendedWeaponsTiers WARFLAMING_LANCE = new HnSExtendedWeaponsTiers(
      8064,
      10.0F,
      -2.7F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.FIRE_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(Attributes.FIRE_DAMAGE, 5.0, Operation.ADD_VALUE),
      new AttributeContainer(net.minecraft.world.entity.ai.attributes.Attributes.ENTITY_INTERACTION_RANGE, 3.0, Operation.ADD_VALUE)
   );
   public static HnSExtendedWeaponsTiers RAVENS_BANE = new HnSExtendedWeaponsTiers(
      8064,
      13.0F,
      -2.6F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.FIRE_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(Attributes.FIRE_DAMAGE, 5.0, Operation.ADD_VALUE),
      new AttributeContainer(net.minecraft.world.entity.ai.attributes.Attributes.ENTITY_INTERACTION_RANGE, 3.0, Operation.ADD_VALUE),
      new AttributeContainer(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED, -0.1, Operation.ADD_VALUE)
   );
   public static HnSExtendedWeaponsTiers BEONGAE = new HnSExtendedWeaponsTiers(
      8064,
      9.0F,
      -3.0F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.LIGHTNING_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(Attributes.CRIT_DAMAGE, 0.05, Operation.ADD_MULTIPLIED_TOTAL)
   );
   public static HnSExtendedWeaponsTiers SKYSCORCHER = new HnSExtendedWeaponsTiers(
      8064,
      12.0F,
      -3.4F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.LIGHTNING_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(Attributes.CRIT_DAMAGE, 0.25, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(Attributes.CRIT_CHANCE, 0.15, Operation.ADD_MULTIPLIED_TOTAL)
   );
   public static HnSExtendedWeaponsTiers IONIC_SPLITTER_DORMANT = new HnSExtendedWeaponsTiers(
      8064,
      6.0F,
      -2.0F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.LIGHTNING_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers IONIC_SPLITTER_T1 = new HnSExtendedWeaponsTiers(
      8064,
      7.0F,
      -2.0F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.LIGHTNING_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers IONIC_SPLITTER_T2 = new HnSExtendedWeaponsTiers(
      8064,
      8.0F,
      -1.9F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.LIGHTNING_SPELL_POWER, 0.2, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers IONIC_SPLITTER_T3 = new HnSExtendedWeaponsTiers(
      8064,
      9.0F,
      -1.8F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.LIGHTNING_SPELL_POWER, 0.25, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers ANCIENT_WARRIORS_AXE = new HnSExtendedWeaponsTiers(
      8064,
      9.0F,
      -3.0F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.EVOCATION_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(Attributes.CRIT_DAMAGE, 0.15, Operation.ADD_MULTIPLIED_TOTAL)
   );
   public static HnSExtendedWeaponsTiers MALICE = new HnSExtendedWeaponsTiers(
      8064,
      8.0F,
      -2.4F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(Attributes.ARMOR_SHRED, 0.1, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(Attributes.PROT_PIERCE, 0.1, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(Attributes.CRIT_CHANCE, 0.15, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(AttributeRegistry.EVOCATION_SPELL_POWER, 0.2, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers PROVOCATION_DORMANT = new HnSExtendedWeaponsTiers(
      8064,
      6.0F,
      -2.0F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.EVOCATION_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers PROVOCATION_T1 = new HnSExtendedWeaponsTiers(
      8064,
      7.0F,
      -2.0F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.EVOCATION_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers PROVOCATION_T2 = new HnSExtendedWeaponsTiers(
      8064,
      8.0F,
      -1.9F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.EVOCATION_SPELL_POWER, 0.2, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers PROVOCATION_T3 = new HnSExtendedWeaponsTiers(
      8064,
      9.0F,
      -1.8F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.EVOCATION_SPELL_POWER, 0.25, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers HAMMER_OF_JUSTICE = new HnSExtendedWeaponsTiers(
      8064,
      9.0F,
      -3.1F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(ASAttributeRegistry.SPELL_RES_PENETRATION, 0.1, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(Attributes.ARMOR_PIERCE, 0.1, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(Attributes.ARMOR_SHRED, 0.1, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(AttributeRegistry.HOLY_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers EXCALIBUR = new HnSExtendedWeaponsTiers(
      8064,
      9.0F,
      -2.7F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(ASAttributeRegistry.SPELL_RES_PENETRATION, 0.1, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(Attributes.ARMOR_SHRED, 0.1, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(Attributes.PROT_SHRED, 0.1, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(AttributeRegistry.HOLY_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers DIVINE_GREATSWORD = new HnSExtendedWeaponsTiers(
      8064,
      9.0F,
      -2.7F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(Attributes.PROT_PIERCE, 0.1, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(Attributes.ARMOR_PIERCE, 0.1, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(AttributeRegistry.HOLY_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers BOUNTIFUL_HARVEST = new HnSExtendedWeaponsTiers(
      8064,
      13.0F,
      -2.6F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.NATURE_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(Attributes.PROT_PIERCE, 0.1, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(Attributes.ARMOR_PIERCE, 0.1, Operation.ADD_MULTIPLIED_TOTAL)
   );
   public static HnSExtendedWeaponsTiers THORN_CHAKRAM = new HnSExtendedWeaponsTiers(
      8064,
      8.0F,
      -1.7F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.NATURE_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(Attributes.ARMOR_SHRED, 0.1, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(Attributes.PROT_SHRED, 0.1, Operation.ADD_MULTIPLIED_TOTAL)
   );
   public static HnSExtendedWeaponsTiers BLADE_OF_GRASS = new HnSExtendedWeaponsTiers(
      8064,
      8.0F,
      -2.7F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.NATURE_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers O_FORTUNA_DORMANT = new HnSExtendedWeaponsTiers(
      8064,
      6.0F,
      -2.0F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.NATURE_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers O_FORTUNA_T1 = new HnSExtendedWeaponsTiers(
      8064,
      7.0F,
      -2.0F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.NATURE_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers O_FORTUNA_T2 = new HnSExtendedWeaponsTiers(
      8064,
      8.0F,
      -1.9F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.NATURE_SPELL_POWER, 0.2, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers O_FORTUNA_T3 = new HnSExtendedWeaponsTiers(
      8064,
      9.0F,
      -1.8F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.NATURE_SPELL_POWER, 0.25, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers OBSIDIAN_CLAYMORE = new HnSExtendedWeaponsTiers(
      8064,
      9.0F,
      -2.7F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(ASAttributeRegistry.MANA_REND, 0.15, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(Attributes.ARMOR_PIERCE, 0.1, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(AttributeRegistry.ENDER_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers STARFURY = new HnSExtendedWeaponsTiers(
      8064,
      9.0F,
      -2.4F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(Attributes.ARMOR_SHRED, 0.1, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(Attributes.ARMOR_PIERCE, 0.1, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(HnSAttributeRegistry.COSMIC_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers UMBRANOVA_DORMANT = new HnSExtendedWeaponsTiers(
      8064,
      6.0F,
      -2.0F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.ENDER_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers UMBRANOVA_T1 = new HnSExtendedWeaponsTiers(
      8064,
      7.0F,
      -2.0F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.ENDER_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers UMBRANOVA_T2 = new HnSExtendedWeaponsTiers(
      8064,
      8.0F,
      -1.9F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.ENDER_SPELL_POWER, 0.2, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers UMBRANOVA_T3 = new HnSExtendedWeaponsTiers(
      8064,
      9.0F,
      -1.8F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.ENDER_SPELL_POWER, 0.25, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers VAMPIRE_KNIVES = new HnSExtendedWeaponsTiers(
      8064,
      8.0F,
      -1.7F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(Attributes.ARMOR_SHRED, 0.15, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(Attributes.LIFE_STEAL, 0.1, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(AttributeRegistry.BLOOD_SPELL_POWER, 0.2, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers DEVESTATOR_DORMANT = new HnSExtendedWeaponsTiers(
      8064,
      6.0F,
      -2.0F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.BLOOD_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers DEVESTATOR_T1 = new HnSExtendedWeaponsTiers(
      8064,
      7.0F,
      -2.0F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.BLOOD_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers DEVESTATOR_T2 = new HnSExtendedWeaponsTiers(
      8064,
      8.0F,
      -1.9F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.BLOOD_SPELL_POWER, 0.2, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers DEVESTATOR_T3 = new HnSExtendedWeaponsTiers(
      8064,
      9.0F,
      -1.8F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.BLOOD_SPELL_POWER, 0.25, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers THE_DEVOURER = new HnSExtendedWeaponsTiers(
      8064,
      13.0F,
      -3.1F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(Attributes.ARMOR_SHRED, 0.15, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(Attributes.PROT_SHRED, 0.15, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(ASAttributeRegistry.GOLIATH_SLAYER, 0.1, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(ASAttributeRegistry.MANA_REND, 0.1, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(AttributeRegistry.ELDRITCH_SPELL_POWER, 0.2, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(net.minecraft.world.entity.ai.attributes.Attributes.ENTITY_INTERACTION_RANGE, 3.0, Operation.ADD_VALUE)
   );
   public static HnSExtendedWeaponsTiers HAMMUSH = new HnSExtendedWeaponsTiers(
      8064,
      12.0F,
      -2.6F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(Attributes.ARMOR_SHRED, 0.15, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(HnSAttributeRegistry.RADIANCE_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(AttributeRegistry.NATURE_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers SPECTRUM = new HnSExtendedWeaponsTiers(
      8064,
      11.0F,
      -2.6F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(Attributes.ARMOR_SHRED, 0.15, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(HnSAttributeRegistry.RADIANCE_SPELL_POWER, 0.2, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(Attributes.PROT_SHRED, 0.15, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(Attributes.PROT_PIERCE, 0.15, Operation.ADD_MULTIPLIED_TOTAL)
   );
   public static HnSExtendedWeaponsTiers MEOWMERE = new HnSExtendedWeaponsTiers(
      8064,
      11.0F,
      -2.6F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(Attributes.ARMOR_SHRED, 0.15, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(Attributes.PROT_PIERCE, 0.15, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(Attributes.PROT_SHRED, 0.15, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(HnSAttributeRegistry.RADIANCE_SPELL_POWER, 0.2, Operation.ADD_MULTIPLIED_TOTAL)
   );
   public static HnSExtendedWeaponsTiers TERRAPRISMA = new HnSExtendedWeaponsTiers(
      8064,
      10.0F,
      -2.7F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(Attributes.ARMOR_SHRED, 0.15, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(Attributes.PROT_PIERCE, 0.15, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(Attributes.PROT_SHRED, 0.15, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(HnSAttributeRegistry.RADIANCE_SPELL_POWER, 0.2, Operation.ADD_MULTIPLIED_TOTAL)
   );
   public static HnSExtendedWeaponsTiers LIGHTS_BANE = new HnSExtendedWeaponsTiers(
      8064,
      8.0F,
      -1.7F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(HnSAttributeRegistry.SHADOW_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_TOTAL)
   );
   public static HnSExtendedWeaponsTiers NIGHTS_EDGE = new HnSExtendedWeaponsTiers(
      8064,
      10.0F,
      -2.7F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(Attributes.ARMOR_SHRED, 0.1, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers TRUE_NIGHTS_EDGE = new HnSExtendedWeaponsTiers(
      8064,
      12.0F,
      -2.8F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(Attributes.ARMOR_SHRED, 0.15, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(Attributes.PROT_SHRED, 0.15, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(Attributes.CRIT_CHANCE, 0.2, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.2, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers MITHRIL = new HnSExtendedWeaponsTiers(
      4032,
      6.0F,
      -3.0F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MITHRIL_SCRAP.get()}),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers SPECTRAL_PICKAXE = new HnSExtendedWeaponsTiers(
      4032,
      6.0F,
      -3.0F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MITHRIL_SCRAP.get()}),
      new AttributeContainer(AttributeRegistry.EVOCATION_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers PICKAXE_AXE = new HnSExtendedWeaponsTiers(
      4032,
      6.0F,
      -3.0F,
      10,
      HnSTags.Blocks.INCORRECT_FOR_MITHRIL_TOOl,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MITHRIL_SCRAP.get()}),
      new AttributeContainer(AttributeRegistry.HOLY_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSExtendedWeaponsTiers MAGE_BANE = new HnSExtendedWeaponsTiers(
      8064,
      8.0F,
      -2.7F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(Attributes.ARMOR_PIERCE, 0.15, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(AttributeRegistry.SPELL_RESIST, 0.15, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(ASAttributeRegistry.MANA_REND, 0.15, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(ASAttributeRegistry.SPELL_RES_PENETRATION, 0.15, Operation.ADD_MULTIPLIED_TOTAL)
   );
   public static HnSExtendedWeaponsTiers LEGIONNAIRE_WARLOCK_AXE = new HnSExtendedWeaponsTiers(
      8064,
      11.0F,
      -3.0F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(Attributes.ARMOR_PIERCE, 0.15, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(AttributeRegistry.SPELL_RESIST, 0.1, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(ASAttributeRegistry.MANA_REND, 0.15, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(ASAttributeRegistry.SPELL_RES_PENETRATION, 0.15, Operation.ADD_MULTIPLIED_TOTAL)
   );
   public static HnSExtendedWeaponsTiers DAWNMAKER = new HnSExtendedWeaponsTiers(
      8064,
      7.0F,
      -2.4F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.SPELL_RESIST, 0.1, Operation.ADD_MULTIPLIED_TOTAL),
      new AttributeContainer(ASAttributeRegistry.MANA_REND, 0.1, Operation.ADD_MULTIPLIED_TOTAL)
   );
   public static HnSExtendedWeaponsTiers MURAMASA = new HnSExtendedWeaponsTiers(
      8064,
      6.0F,
      -2.4F,
      10,
      BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)HnSItemRegistry.ZENALITE_INGOT.get()}),
      new AttributeContainer(Attributes.ARMOR_PIERCE, 0.05, Operation.ADD_MULTIPLIED_TOTAL)
   );
   int uses;
   float damage;
   float speed;
   int enchantmentValue;
   TagKey<Block> incorrectBlocksForDrops;
   Supplier<Ingredient> repairIngredient;
   AttributeContainer[] attributes;

   private HnSExtendedWeaponsTiers(
      int uses,
      float damage,
      float speed,
      int enchantmentValue,
      TagKey<Block> incorrectBlocksForDrops,
      Supplier<Ingredient> repairIngredient,
      AttributeContainer... attributes
   ) {
      this.uses = uses;
      this.damage = damage;
      this.speed = speed;
      this.enchantmentValue = enchantmentValue;
      this.incorrectBlocksForDrops = incorrectBlocksForDrops;
      this.repairIngredient = repairIngredient;
      this.attributes = attributes;
   }

   public AttributeContainer[] getAdditionalAttributes() {
      return this.attributes;
   }

   public int getUses() {
      return this.uses;
   }

   public float getSpeed() {
      return this.speed;
   }

   public float getAttackDamageBonus() {
      return this.damage;
   }

   public TagKey<Block> getIncorrectBlocksForDrops() {
      return this.incorrectBlocksForDrops;
   }

   public int getEnchantmentValue() {
      return this.enchantmentValue;
   }

   public Ingredient getRepairIngredient() {
      return this.repairIngredient.get();
   }
}
