package com.userofbricks.expanded_combat.item;

import java.util.List;

import com.userofbricks.expanded_combat.ExpandedCombat;
import com.userofbricks.expanded_combat.api.material.Material;
import com.userofbricks.expanded_combat.api.weapon_type.GripType;
import com.userofbricks.expanded_combat.api.weapon_type.WeaponType;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ECWeaponItem extends Item implements IMaterialItem {
   public final Material material;
   public final WeaponType weapon;
   public final int addedDmg;
   protected static final ResourceLocation ATTACK_KNOCKBACK_MODIFIER = ExpandedCombat.modLoc("base_attack_knockback");
   protected static final ResourceLocation ATTACK_REACH_MODIFIER = ExpandedCombat.modLoc("base_attack_reach");

   public ECWeaponItem(Material material, WeaponType weapon, Properties properties) {
      this(material, weapon, properties, 0);
   }

   public ECWeaponItem(Material material, WeaponType weapon, Properties properties, int addedDmg) {
      super(
         material.defense().fireResistant()
            ? properties.durability(Math.round(material.durability().toolBaseDurability() * (float)weapon.config().durabilityMultiplier()))
               .component(DataComponents.TOOL, createToolProperties())
               .component(DataComponents.ATTRIBUTE_MODIFIERS, getAttributeModifiers(material, weapon, addedDmg))
               .fireResistant()
            : properties.durability(Math.round(material.durability().toolBaseDurability() * (float)weapon.config().durabilityMultiplier()))
               .component(DataComponents.TOOL, createToolProperties())
               .component(DataComponents.ATTRIBUTE_MODIFIERS, getAttributeModifiers(material, weapon, addedDmg))
      );
      this.material = material;
      this.weapon = weapon;
      this.addedDmg = addedDmg;
   }

   private static Tool createToolProperties() {
      return new Tool(
         List.of(
            Tool.Rule.minesAndDrops(List.of(net.minecraft.world.level.block.Blocks.COBWEB), 15.0F),
            Tool.Rule.overrideSpeed(net.minecraft.tags.BlockTags.SWORD_EFFICIENT, 1.5F)
         ),
         1.0F,
         2
      );
   }

   @Override
   public Material getMaterial() {
      return this.material;
   }

   public WeaponType getWeapon() {
      return this.weapon;
   }

   public static ItemAttributeModifiers getAttributeModifiers(Material material, WeaponType weapon, int addedDmg) {
      EquipmentSlotGroup slotGroup = weapon.config().gripType() == GripType.DUALWIELD ? EquipmentSlotGroup.HAND : EquipmentSlotGroup.MAINHAND;
      Builder builder = ItemAttributeModifiers.builder();
      builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, getDamage(material, weapon, addedDmg), Operation.ADD_VALUE), slotGroup);
      builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, weapon.config().attackSpeed(), Operation.ADD_VALUE), slotGroup);
      builder.add(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(ATTACK_KNOCKBACK_MODIFIER, weapon.config().knockback(), Operation.ADD_VALUE), slotGroup);
      builder.add(
         Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(ATTACK_REACH_MODIFIER, weapon.config().attackRange(), Operation.ADD_VALUE), slotGroup
      );
      return builder.build();
   }

   public static double getDamage(Material material, WeaponType weapon, int addedDmg) {
      return 3.0 + material.offense().addedAttackDamage() + weapon.config().baseAttackDamage() + addedDmg;
   }

   public double getDamage() {
      return getDamage(this.material, this.weapon, this.addedDmg);
   }

   @Override
   public float getMendingBonus() {
      return this.getMaterial().enchanting().mendingBonus() + this.getWeapon().config().mendingBonus();
   }

   public boolean hurtEnemy(ItemStack weapon, LivingEntity target, LivingEntity attacker) {
      super.hurtEnemy(weapon, target, attacker);
      weapon.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
      return true;
   }

   public boolean canAttackBlock(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
      return !pPlayer.isCreative();
   }

   public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
      return ItemAbilities.DEFAULT_SWORD_ACTIONS.contains(itemAbility);
   }
}
