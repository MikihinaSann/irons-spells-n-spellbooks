package com.gametechbc.traveloptics.item;

import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.item.weapons.IronsWeaponTier;
import java.util.function.Supplier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class TOWeaponTier implements Tier, IronsWeaponTier {
   public static TOWeaponTier MECHANIZED_WRAITHBLADE = new TOWeaponTier(
      1000,
      10.0F,
      -2.7F,
      4,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{Items.NETHERITE_SCRAP}),
      new AttributeContainer(Attributes.ARMOR, 4.0, Operation.ADD_VALUE),
      new AttributeContainer(Attributes.ARMOR, 4.0, Operation.ADD_VALUE)
   );
   public static TOWeaponTier INFERNAL_DEVASTATOR = new TOWeaponTier(
      1000,
      10.0F,
      -2.7F,
      4,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{Items.NETHERITE_SCRAP}),
      new AttributeContainer(Attributes.ARMOR, 4.0, Operation.ADD_VALUE),
      new AttributeContainer(Attributes.ARMOR, 4.0, Operation.ADD_VALUE)
   );
   public static TOWeaponTier CURSED_WRAITHBLADE = new TOWeaponTier(
      1000,
      10.0F,
      -2.7F,
      4,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{Items.NETHERITE_SCRAP}),
      new AttributeContainer(Attributes.ARMOR, 4.0, Operation.ADD_VALUE),
      new AttributeContainer(Attributes.ARMOR, 4.0, Operation.ADD_VALUE)
   );
   int uses;
   float damage;
   float speed;
   int enchantmentValue;
   TagKey<Block> incorrectBlocksForDrops;
   Supplier<Ingredient> repairIngredient;
   AttributeContainer[] attributes;

   public TOWeaponTier(
      int uses,
      float damage,
      float speed,
      int enchantmentValue,
      TagKey<Block> incorrectBlocksForDrops,
      Supplier<Ingredient> repairIngredient,
      AttributeContainer... attributes
   ) {
      super();
      this.uses = uses;
      this.damage = damage;
      this.speed = speed;
      this.enchantmentValue = enchantmentValue;
      this.incorrectBlocksForDrops = incorrectBlocksForDrops;
      this.repairIngredient = repairIngredient;
      this.attributes = attributes;
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

   public AttributeContainer[] getAdditionalAttributes() {
      return this.attributes;
   }
}
