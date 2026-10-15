package com.somake.somakespells.item;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;

public class BaseAttributeSwordItem extends SwordItem {
   private final int attackDamageBonus;
   private final float attackSpeedModifier;
   private final double entityInteractionRange;
   private final Holder<Attribute> extraAttribute;
   private final ResourceLocation modifierId;
   private final double extraAmount;
   private final Operation extraOperation;

   public BaseAttributeSwordItem(
      Tier tier,
      int attackDamageBonus,
      float attackSpeedModifier,
      Properties properties,
      Holder<Attribute> extraAttribute,
      ResourceLocation modifierId,
      double extraAmount,
      Operation extraOperation
   ) {
      this(tier, attackDamageBonus, attackSpeedModifier, properties, 3.0, extraAttribute, modifierId, extraAmount, extraOperation);
   }

   public BaseAttributeSwordItem(
      Tier tier,
      int attackDamageBonus,
      float attackSpeedModifier,
      Properties properties,
      double entityInteractionRange,
      Holder<Attribute> extraAttribute,
      ResourceLocation modifierId,
      double extraAmount,
      Operation extraOperation
   ) {
      super(tier, properties);
      this.attackDamageBonus = attackDamageBonus;
      this.attackSpeedModifier = attackSpeedModifier;
      this.entityInteractionRange = entityInteractionRange;
      this.extraAttribute = extraAttribute;
      this.modifierId = modifierId;
      this.extraAmount = extraAmount;
      this.extraOperation = extraOperation;
   }

   public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
      Builder builder = ItemAttributeModifiers.builder();
      ResourceLocation damageId = ResourceLocation.withDefaultNamespace("base_attack_damage");
      ResourceLocation speedId = ResourceLocation.withDefaultNamespace("base_attack_speed");
      builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(damageId, this.attackDamageBonus, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
      builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(speedId, this.attackSpeedModifier, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
      double rangeDelta = this.entityInteractionRange - 3.0;
      if (Math.abs(rangeDelta) > 1.0E-6) {
         builder.add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(this.modifierId, rangeDelta, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
      }

      if (this.extraAttribute != null && Math.abs(this.extraAmount) > 1.0E-6) {
         builder.add(this.extraAttribute, new AttributeModifier(this.modifierId, this.extraAmount, this.extraOperation), EquipmentSlotGroup.MAINHAND);
      }

      return builder.build();
   }
}
