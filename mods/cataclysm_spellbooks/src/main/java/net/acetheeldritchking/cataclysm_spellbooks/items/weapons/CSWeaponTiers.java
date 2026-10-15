package net.acetheeldritchking.cataclysm_spellbooks.items.weapons;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.item.weapons.IronsWeaponTier;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
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

public class CSWeaponTiers implements Tier, IronsWeaponTier {
   public static CSWeaponTiers MONSTROUS_FLAMBERGE = new CSWeaponTiers(
      1561,
      10.0F,
      -2.7F,
      15,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{Items.NETHERITE_SCRAP}),
      new AttributeContainer(Attributes.ARMOR, 5.0, Operation.ADD_VALUE),
      new AttributeContainer(AttributeRegistry.FIRE_SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   public static CSWeaponTiers SPELLSTEALER = new CSWeaponTiers(
      1000,
      5.0F,
      -2.6F,
      20,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{Items.NETHERITE_SCRAP}),
      new AttributeContainer(AttributeRegistry.NATURE_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE)
   );
   public static CSWeaponTiers HELLFIRE_FORGE = new CSWeaponTiers(
      1561,
      13.5F,
      -2.8F,
      25,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.PYRIUM_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.FIRE_SPELL_POWER, 0.15, Operation.ADD_VALUE),
      new AttributeContainer(AttributeRegistry.FIRE_MAGIC_RESIST, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   private final int uses;
   private final float speed;
   private final float damage;
   private final int enchantmentValue;
   private final TagKey<Block> incorrectBlocksForDrops;
   private final Supplier<Ingredient> repairIngredient;
   private final AttributeContainer[] attributeContainers;

   private CSWeaponTiers(
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
