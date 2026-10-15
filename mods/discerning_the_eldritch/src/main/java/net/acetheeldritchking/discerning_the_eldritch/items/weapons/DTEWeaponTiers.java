package net.acetheeldritchking.discerning_the_eldritch.items.weapons;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.item.weapons.IronsWeaponTier;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import java.util.function.Supplier;
import net.acetheeldritchking.aces_spell_utils.registries.ASAttributeRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEAttributeRegistry;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class DTEWeaponTiers implements Tier, IronsWeaponTier {
   public static DTEWeaponTiers DEEP_GREATSWORD = new DTEWeaponTiers(
      1680,
      7.5F,
      -3.0F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MITHRIL_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.ELDRITCH_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   public static DTEWeaponTiers GOD_SPEAR = new DTEWeaponTiers(
      1560,
      8.5F,
      -3.2F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MITHRIL_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.HOLY_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   public static DTEWeaponTiers ENDER_CLEAVER = new DTEWeaponTiers(
      1600,
      6.5F,
      -2.5F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MITHRIL_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.ENDER_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   public static DTEWeaponTiers ICE_SPEAR = new DTEWeaponTiers(
      1500,
      6.0F,
      -2.8F,
      8,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MITHRIL_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.ICE_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE)
   );
   public static DTEWeaponTiers YMIR = new DTEWeaponTiers(
      2670,
      15.5F,
      -3.1F,
      15,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MITHRIL_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.ELDRITCH_SPELL_POWER, 0.2, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(DTEAttributeRegistry.RITUAL_MAGIC_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(ASAttributeRegistry.MANA_STEAL, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(ASAttributeRegistry.MANA_REND, 0.25, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(ASAttributeRegistry.SPELL_RES_PENETRATION, 0.1, Operation.ADD_MULTIPLIED_BASE)
   );
   public static DTEWeaponTiers RITUAL_DAGGER = new DTEWeaponTiers(
      900,
      2.0F,
      -2.6F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MITHRIL_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.ELDRITCH_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(DTEAttributeRegistry.RITUAL_MAGIC_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   public static DTEWeaponTiers FORLORN_RAPIER = new DTEWeaponTiers(
      1061,
      5.0F,
      -1.5F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MITHRIL_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.ELDRITCH_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   public static DTEWeaponTiers BROKEN_LEGENDS_SWORD = new DTEWeaponTiers(
      500,
      6.5F,
      -2.8F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MITHRIL_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   public static DTEWeaponTiers FORSAKEN_FLAMBERGE = new DTEWeaponTiers(
      2031,
      10.0F,
      -2.7F,
      6,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MITHRIL_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.ELDRITCH_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.05, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(ASAttributeRegistry.MANA_STEAL, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   public static DTEWeaponTiers SNOWGRAVE = new DTEWeaponTiers(
      1785,
      9.5F,
      -3.2F,
      10,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MITHRIL_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.ICE_SPELL_POWER, 0.2, Operation.ADD_MULTIPLIED_BASE)
   );
   public static DTEWeaponTiers SOUL_FIRE_SCYTHE = new DTEWeaponTiers(
      2061,
      12.0F,
      -2.2F,
      15,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MITHRIL_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.FIRE_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.FIRE_MAGIC_RESIST, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(ASAttributeRegistry.SPELL_RES_PENETRATION, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   public static DTEWeaponTiers CATACLYSM = new DTEWeaponTiers(
      1281,
      10.0F,
      -2.7F,
      9,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MITHRIL_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.BLOOD_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(DTEAttributeRegistry.RITUAL_MAGIC_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(ASAttributeRegistry.GOLIATH_SLAYER, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   public static DTEWeaponTiers CATACLYSM_AWAKENED = new DTEWeaponTiers(
      2370,
      12.0F,
      -2.7F,
      15,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MITHRIL_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.BLOOD_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.ELDRITCH_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(DTEAttributeRegistry.RITUAL_MAGIC_POWER, 0.2, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(ASAttributeRegistry.GOLIATH_SLAYER, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(ASAttributeRegistry.SPELL_RES_PENETRATION, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   public static DTEWeaponTiers DEVOURER = new DTEWeaponTiers(
      1281,
      12.0F,
      -3.15F,
      15,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MITHRIL_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.BLOOD_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(DTEAttributeRegistry.RITUAL_MAGIC_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(ASAttributeRegistry.HUNGER_STEAL, 5.0, Operation.ADD_VALUE)
   );
   public static DTEWeaponTiers DEVOURER_AWAKENED = new DTEWeaponTiers(
      2370,
      13.0F,
      -3.15F,
      15,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MITHRIL_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.BLOOD_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.ELDRITCH_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(DTEAttributeRegistry.RITUAL_MAGIC_POWER, 0.2, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(ASAttributeRegistry.HUNGER_STEAL, 10.0, Operation.ADD_VALUE),
      new AttributeContainer(ASAttributeRegistry.SPELL_RES_PENETRATION, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   public static DTEWeaponTiers MOURNING_STAR = new DTEWeaponTiers(
      1281,
      10.5F,
      -2.9F,
      9,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MITHRIL_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.BLOOD_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(DTEAttributeRegistry.RITUAL_MAGIC_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(ASAttributeRegistry.MANA_REND, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   public static DTEWeaponTiers MOURNING_STAR_AWAKENED = new DTEWeaponTiers(
      2370,
      12.5F,
      -2.9F,
      15,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MITHRIL_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.BLOOD_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.ELDRITCH_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(DTEAttributeRegistry.RITUAL_MAGIC_POWER, 0.2, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(ASAttributeRegistry.MANA_REND, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(ASAttributeRegistry.SPELL_RES_PENETRATION, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   private final int uses;
   private final float speed;
   private final float damage;
   private final int enchantmentValue;
   private final TagKey<Block> incorrectBlocksForDrops;
   private final Supplier<Ingredient> repairIngredient;
   private final AttributeContainer[] attributeContainers;

   private DTEWeaponTiers(
      int uses,
      float damage,
      float speed,
      int enchantmentValue,
      TagKey<Block> incorrectBlocksForDrops,
      Supplier<Ingredient> repairIngredient,
      AttributeContainer... attributes
   ) {
      this.uses = uses;
      this.speed = speed;
      this.damage = damage;
      this.enchantmentValue = enchantmentValue;
      this.incorrectBlocksForDrops = incorrectBlocksForDrops;
      this.repairIngredient = repairIngredient;
      this.attributeContainers = attributes;
   }

   public AttributeContainer[] getAdditionalAttributes() {
      return this.attributeContainers;
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
