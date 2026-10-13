package net.veroxuniverse.crystal_chronicles.lib;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.item.weapons.ExtendedWeaponTier;
import io.redspace.ironsspellbooks.item.weapons.IronsWeaponTier;
import java.util.function.Supplier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.veroxuniverse.crystal_chronicles.registry.CCItems;
import net.veroxuniverse.crystal_chronicles.util.CCTags;
import org.jetbrains.annotations.NotNull;

public class CCWeaponTiers implements Tier, IronsWeaponTier {
   public static final Tier ENDER = new ExtendedWeaponTier(
      3069,
      6.0F,
      -3.0F,
      20,
      CCTags.Blocks.INCORRECT_FOR_CRYSTAL_TOOL,
      () -> Ingredient.of((ItemLike)CCItems.VOIDSTONE_SHARD.get()),
      new AttributeContainer(AttributeRegistry.ENDER_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.MANA_REGEN, 0.25, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.05, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   public static final Tier LIGHTNING = new ExtendedWeaponTier(
      3069,
      9.0F,
      -2.6F,
      20,
      CCTags.Blocks.INCORRECT_FOR_CRYSTAL_TOOL,
      () -> Ingredient.of((ItemLike)CCItems.VOLTITE_SHARD.get()),
      new AttributeContainer(AttributeRegistry.LIGHTNING_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.CAST_TIME_REDUCTION, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.05, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   public static final Tier HOLY = new ExtendedWeaponTier(
      3069,
      8.0F,
      -2.4F,
      20,
      CCTags.Blocks.INCORRECT_FOR_CRYSTAL_TOOL,
      () -> Ingredient.of((ItemLike)CCItems.DIVINITE_SHARD.get()),
      new AttributeContainer(AttributeRegistry.HOLY_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.CAST_TIME_REDUCTION, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.05, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   public static final Tier NATURE = new ExtendedWeaponTier(
      3069,
      9.0F,
      -2.7F,
      20,
      CCTags.Blocks.INCORRECT_FOR_CRYSTAL_TOOL,
      () -> Ingredient.of((ItemLike)CCItems.FLORALITE_SHARD.get()),
      new AttributeContainer(AttributeRegistry.NATURE_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.CAST_TIME_REDUCTION, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.05, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE)
   );
   public static final Tier FIRE = new ExtendedWeaponTier(
      3069,
      7.0F,
      -3.0F,
      20,
      CCTags.Blocks.INCORRECT_FOR_CRYSTAL_TOOL,
      () -> Ingredient.of((ItemLike)CCItems.VOLCANITE_SHARD.get()),
      new AttributeContainer(AttributeRegistry.FIRE_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.CAST_TIME_REDUCTION, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.05, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(Attributes.MOVEMENT_SPEED, 0.1, Operation.ADD_MULTIPLIED_BASE)
   );
   public static final Tier BLOOD = new ExtendedWeaponTier(
      3069,
      9.0F,
      -2.7F,
      20,
      CCTags.Blocks.INCORRECT_FOR_CRYSTAL_TOOL,
      () -> Ingredient.of((ItemLike)CCItems.HEMALITE_SHARD.get()),
      new AttributeContainer(AttributeRegistry.BLOOD_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.SUMMON_DAMAGE, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.CAST_TIME_REDUCTION, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.05, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   public static final Tier ICE = new ExtendedWeaponTier(
      3069,
      11.0F,
      -3.2F,
      20,
      CCTags.Blocks.INCORRECT_FOR_CRYSTAL_TOOL,
      () -> Ingredient.of((ItemLike)CCItems.ICE_SHARD.get()),
      new AttributeContainer(AttributeRegistry.ICE_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.CAST_TIME_REDUCTION, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.05, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   public static final Tier EVOCATION = new ExtendedWeaponTier(
      3069,
      8.0F,
      -2.4F,
      20,
      CCTags.Blocks.INCORRECT_FOR_CRYSTAL_TOOL,
      () -> Ingredient.of(Items.EMERALD),
      new AttributeContainer(AttributeRegistry.EVOCATION_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.CAST_TIME_REDUCTION, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.05, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   int uses;
   float damage;
   float speed;
   int enchantmentValue;
   TagKey<Block> incorrectBlocksForDrops;
   Supplier<Ingredient> repairIngredient;
   AttributeContainer[] attributes;

   public CCWeaponTiers(
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

   @Override
   public int getUses() {
      return this.uses;
   }

   @Override
   public float getSpeed() {
      return this.speed;
   }

   @Override
   public float getAttackDamageBonus() {
      return this.damage;
   }

   @NotNull
   @Override
   public TagKey<Block> getIncorrectBlocksForDrops() {
      return this.incorrectBlocksForDrops;
   }

   @Override
   public int getEnchantmentValue() {
      return this.enchantmentValue;
   }

   @NotNull
   @Override
   public Ingredient getRepairIngredient() {
      return this.repairIngredient.get();
   }

   @Override
   public AttributeContainer[] getAdditionalAttributes() {
      return this.attributes;
   }
}
