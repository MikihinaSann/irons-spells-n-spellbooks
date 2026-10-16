package com.userofbricks.expanded_combat.item;

import com.userofbricks.expanded_combat.api.material.Material;
import com.userofbricks.expanded_combat.api.weapon_type.GripType;
import com.userofbricks.expanded_combat.api.weapon_type.WeaponType;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ElementalWeapon extends ECWeaponItem {
   public final Holder<Attribute> damageAttributeRegistryObject;

   public ElementalWeapon(Material material, WeaponType weapon, Properties properties, int addedDmg, Holder<Attribute> damageAttributeRegistryObject) {
      super(material, weapon, properties, addedDmg);
      this.damageAttributeRegistryObject = damageAttributeRegistryObject;
   }

   @Override
   public double getDamage() {
      return this.getMaterial().offense().addedAttackDamage() + this.getWeapon().config().baseAttackDamage();
   }

   public ItemAttributeModifiers getAttributeModifiers() {
      EquipmentSlotGroup slotGroup = this.getWeapon().config().gripType() == GripType.DUALWIELD ? EquipmentSlotGroup.HAND : EquipmentSlotGroup.MAINHAND;
      Builder builder = ItemAttributeModifiers.builder();
      builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 3 + this.addedDmg, Operation.ADD_VALUE), slotGroup);
      builder.add(this.damageAttributeRegistryObject, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, this.getDamage(), Operation.ADD_VALUE), slotGroup);
      builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, this.getWeapon().config().attackSpeed(), Operation.ADD_VALUE), slotGroup);
      builder.add(
         Attributes.ATTACK_KNOCKBACK, new AttributeModifier(ATTACK_KNOCKBACK_MODIFIER, this.getWeapon().config().knockback(), Operation.ADD_VALUE), slotGroup
      );
      builder.add(
         Attributes.ENTITY_INTERACTION_RANGE,
         new AttributeModifier(ATTACK_REACH_MODIFIER, this.getWeapon().config().attackRange(), Operation.ADD_VALUE),
         slotGroup
      );
      return builder.build();
   }
}
