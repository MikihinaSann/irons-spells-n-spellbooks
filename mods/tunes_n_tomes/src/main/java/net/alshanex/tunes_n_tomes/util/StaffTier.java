package net.alshanex.tunes_n_tomes.util;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.item.weapons.IronsWeaponTier;
import net.alshanex.tunes_n_tomes.registry.TAttributeRegistry;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class StaffTier implements IronsWeaponTier {
   public static StaffTier MELODY_STAFF = new StaffTier(
      7.0F,
      -3.0F,
      new AttributeContainer(TAttributeRegistry.MELODY_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.05, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   float damage;
   float speed;
   AttributeContainer[] attributes;

   public StaffTier(float damage, float speed, AttributeContainer... attributes) {
      this.damage = damage;
      this.speed = speed;
      this.attributes = attributes;
   }

   public float getSpeed() {
      return this.speed;
   }

   public float getAttackDamageBonus() {
      return this.damage;
   }

   public AttributeContainer[] getAdditionalAttributes() {
      return this.attributes;
   }
}
