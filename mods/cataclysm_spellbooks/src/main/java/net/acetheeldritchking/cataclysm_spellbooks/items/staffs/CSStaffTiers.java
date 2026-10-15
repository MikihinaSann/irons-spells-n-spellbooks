package net.acetheeldritchking.cataclysm_spellbooks.items.staffs;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.item.weapons.IronsWeaponTier;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSAttributeRegistry;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class CSStaffTiers implements IronsWeaponTier {
   public static CSStaffTiers BLOOM_STONE_STAFF = new CSStaffTiers(
      3.0F,
      -3.0F,
      new AttributeContainer(AttributeRegistry.NATURE_SPELL_POWER, 0.15F, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.15F, Operation.ADD_MULTIPLIED_BASE)
   );
   public static CSStaffTiers CORAL_STAFF = new CSStaffTiers(
      3.0F,
      -3.0F,
      new AttributeContainer(CSAttributeRegistry.ABYSSAL_MAGIC_POWER, 0.25, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.25, Operation.ADD_MULTIPLIED_BASE)
   );
   public static CSStaffTiers FAKE_WUDJETS_STAFF = new CSStaffTiers(
      3.0F,
      -3.0F,
      new AttributeContainer(AttributeRegistry.NATURE_SPELL_POWER, 0.1F, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.HOLY_SPELL_POWER, 0.2F, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.25, Operation.ADD_MULTIPLIED_BASE)
   );
   public static CSStaffTiers VOID_STAFF = new CSStaffTiers(
      3.0F,
      -3.0F,
      new AttributeContainer(AttributeRegistry.ENDER_SPELL_POWER, 0.25, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.25, Operation.ADD_MULTIPLIED_BASE)
   );
   public static CSStaffTiers SPIRIT_SUNDERER_STAFF = new CSStaffTiers(
      3.0F,
      -3.0F,
      new AttributeContainer(AttributeRegistry.ICE_SPELL_POWER, 0.25, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.MANA_REGEN, 0.1F, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.1F, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05F, Operation.ADD_MULTIPLIED_BASE)
   );
   public static CSStaffTiers SOUL_BRAZIER_STAFF = new CSStaffTiers(
      3.0F,
      -3.0F,
      new AttributeContainer(AttributeRegistry.ICE_SPELL_POWER, 0.15F, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.MANA_REGEN, 0.25, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.25, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.CAST_TIME_REDUCTION, 0.1F, Operation.ADD_MULTIPLIED_BASE)
   );
   float damage;
   float speed;
   AttributeContainer[] attributeContainers;

   public CSStaffTiers(float damage, float speed, AttributeContainer... attributeContainers) {
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
