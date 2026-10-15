package net.warphan.iss_magicfromtheeast.item.weapons;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.item.weapons.IronsWeaponTier;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import java.util.function.Supplier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.warphan.iss_magicfromtheeast.registries.MFTEAttributeRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTEItemRegistries;

public class MFTEExtendedWeaponTier implements Tier, IronsWeaponTier {
   public static MFTEExtendedWeaponTier JADE_GUANDAO = new MFTEExtendedWeaponTier(
      2031,
      11.0F,
      -2.5F,
      12,
      BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)MFTEItemRegistries.REFINED_JADE_INGOT.get()}),
      new AttributeContainer(Attributes.ARMOR_TOUGHNESS, 2.0, Operation.ADD_VALUE)
   );
   public static MFTEExtendedWeaponTier SOUL_BREAKER = new MFTEExtendedWeaponTier(
      2031,
      5.0F,
      -2.5F,
      12,
      BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)MFTEItemRegistries.CRYSTALLIZED_SOUL.get()}),
      new AttributeContainer(AttributeRegistry.SPELL_RESIST, 0.1, Operation.ADD_MULTIPLIED_BASE)
   );
   public static MFTEExtendedWeaponTier SPIRIT_CRUSHER = new MFTEExtendedWeaponTier(
      2031,
      17.0F,
      -3.4F,
      12,
      BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)MFTEItemRegistries.CRYSTALLIZED_SOUL.get()}),
      new AttributeContainer(AttributeRegistry.SPELL_RESIST, 0.2, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(Attributes.MOVEMENT_SPEED, -0.1F, Operation.ADD_MULTIPLIED_BASE)
   );
   public static MFTEExtendedWeaponTier MURAMASA = new MFTEExtendedWeaponTier(
      2031,
      7.0F,
      -2.0F,
      12,
      BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.BLOOD_RUNE.get()}),
      new AttributeContainer(AttributeRegistry.BLOOD_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE)
   );
   public static MFTEExtendedWeaponTier SOUL_KATANA = new MFTEExtendedWeaponTier(
      2031,
      6.5F,
      -1.9F,
      12,
      BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)MFTEItemRegistries.SPIRIT_RUNE.get()}),
      new AttributeContainer(MFTEAttributeRegistries.SPIRIT_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE)
   );
   int uses;
   float damage;
   float speed;
   int enchantmentValue;
   TagKey<Block> incorrectBlocksForDrops;
   Supplier<Ingredient> repairIngredient;
   AttributeContainer[] attributes;

   public MFTEExtendedWeaponTier(
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

   public int getUses() {
      return this.uses;
   }

   public float getAttackDamageBonus() {
      return this.damage;
   }

   public float getSpeed() {
      return this.speed;
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
