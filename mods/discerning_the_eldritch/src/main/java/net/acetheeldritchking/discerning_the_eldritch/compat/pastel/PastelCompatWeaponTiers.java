package net.acetheeldritchking.discerning_the_eldritch.compat.pastel;

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

public class PastelCompatWeaponTiers implements Tier, IronsWeaponTier {
   public static PastelCompatWeaponTiers DREAM_REAVER_YMIR = new PastelCompatWeaponTiers(
      2670,
      17.5F,
      -2.9F,
      15,
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)ItemRegistry.MITHRIL_INGOT.get()}),
      new AttributeContainer(AttributeRegistry.ELDRITCH_SPELL_POWER, 0.25, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(DTEAttributeRegistry.RITUAL_MAGIC_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(ASAttributeRegistry.MANA_STEAL, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(ASAttributeRegistry.MANA_REND, 0.3, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(ASAttributeRegistry.SPELL_RES_PENETRATION, 0.25, Operation.ADD_MULTIPLIED_BASE)
   );
   private final int uses;
   private final float speed;
   private final float damage;
   private final int enchantmentValue;
   private final TagKey<Block> incorrectBlocksForDrops;
   private final Supplier<Ingredient> repairIngredient;
   private final AttributeContainer[] attributeContainers;

   private PastelCompatWeaponTiers(
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
