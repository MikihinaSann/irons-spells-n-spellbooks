package net.acetheeldritchking.discerning_the_eldritch.items.staffs;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.item.weapons.IronsWeaponTier;
import net.acetheeldritchking.aces_spell_utils.registries.ASAttributeRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEAttributeRegistry;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class DTEStaffTier implements IronsWeaponTier {
   public static DTEStaffTier STAFF_OF_VEHEMENCE = new DTEStaffTier(
      3.0F,
      -3.0F,
      new AttributeContainer(AttributeRegistry.ELDRITCH_SPELL_POWER, 0.15F, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(DTEAttributeRegistry.RITUAL_MAGIC_POWER, 0.2F, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.MANA_REGEN, 0.15F, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.25, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(ASAttributeRegistry.MANA_STEAL, 0.25, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(ASAttributeRegistry.MANA_REND, 0.1, Operation.ADD_MULTIPLIED_BASE)
   );
   public static DTEStaffTier STAFF_OF_ASCENSION = new DTEStaffTier(
      3.0F,
      -3.0F,
      new AttributeContainer(AttributeRegistry.ELDRITCH_SPELL_POWER, 0.1F, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.CAST_TIME_REDUCTION, 0.25, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.15F, Operation.ADD_MULTIPLIED_BASE)
   );
   public static DTEStaffTier BROKEN_LEGENDS_STAFF = new DTEStaffTier(
      1.5F,
      -2.5F,
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05F, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.MANA_REGEN, 0.1F, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.1F, Operation.ADD_MULTIPLIED_BASE)
   );
   float damage;
   float speed;
   AttributeContainer[] attributeContainers;

   public DTEStaffTier(float damage, float speed, AttributeContainer... attributeContainers) {
      this.damage = damage;
      this.speed = speed;
      this.attributeContainers = attributeContainers;
   }

   public float getAttackDamageBonus() {
      return this.damage;
   }

   public float getSpeed() {
      return this.speed;
   }

   public AttributeContainer[] getAdditionalAttributes() {
      return this.attributeContainers;
   }
}
