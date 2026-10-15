package net.warphan.iss_magicfromtheeast.item.weapons;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.item.weapons.IronsWeaponTier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class MFTEStaffTier implements IronsWeaponTier {
   public static final MFTEStaffTier TAIJI_SWORD = new MFTEStaffTier(
      5.0F,
      -2.2F,
      new AttributeContainer(Attributes.ARMOR, 2.0, Operation.ADD_VALUE),
      new AttributeContainer(AttributeRegistry.MANA_REGEN, 0.2, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE)
   );
   float damage;
   float speed;
   AttributeContainer[] attributes;

   public MFTEStaffTier(float damage, float speed, AttributeContainer... attributes) {
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
