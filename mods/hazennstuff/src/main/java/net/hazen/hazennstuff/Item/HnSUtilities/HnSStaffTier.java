package net.hazen.hazennstuff.Item.HnSUtilities;

import dev.shadowsoffire.apothic_attributes.api.ALObjects.Attributes;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.item.weapons.IronsWeaponTier;
import net.acetheeldritchking.aces_spell_utils.registries.ASAttributeRegistry;
import net.hazen.hazennstuff.Registries.HnSAttributeRegistry;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class HnSStaffTier implements IronsWeaponTier {
   public static HnSStaffTier ARTIFICER = new HnSStaffTier(
      3.0F,
      -3.0F,
      new AttributeContainer(AttributeRegistry.CAST_TIME_REDUCTION, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSStaffTier ROD_OF_DISCORD = new HnSStaffTier(
      3.0F,
      -3.0F,
      new AttributeContainer(AttributeRegistry.ENDER_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(HnSAttributeRegistry.SHADOW_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSStaffTier WHITE_LILY_STAFF = new HnSStaffTier(
      3.0F,
      -3.0F,
      new AttributeContainer(AttributeRegistry.NATURE_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.MANA_REGEN, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSStaffTier SOULCALLING_SCEPTER = new HnSStaffTier(
      3.0F,
      -2.4F,
      new AttributeContainer(AttributeRegistry.FIRE_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.MANA_REGEN, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.CAST_TIME_REDUCTION, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(ASAttributeRegistry.MANA_REND, 0.1, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSStaffTier DIVINE_SCEPTER = new HnSStaffTier(
      3.0F,
      -3.0F,
      new AttributeContainer(AttributeRegistry.HOLY_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.CAST_TIME_REDUCTION, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.CASTING_MOVESPEED, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSStaffTier BLOSSOM_OF_THE_ENCHANTRESS = new HnSStaffTier(
      3.0F,
      -3.0F,
      new AttributeContainer(AttributeRegistry.NATURE_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(Attributes.HEALING_RECEIVED, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_RESIST, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSStaffTier GRIMORIE_STAFF = new HnSStaffTier(
      3.0F,
      -3.0F,
      new AttributeContainer(AttributeRegistry.EVOCATION_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.CASTING_MOVESPEED, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSStaffTier ENDERCONIC_SCEPTER = new HnSStaffTier(
      3.0F,
      -3.0F,
      new AttributeContainer(AttributeRegistry.ENDER_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_RESIST, 0.05, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE)
   );
   public static HnSStaffTier INSANIA_AETERNUS = new HnSStaffTier(
      3.0F,
      -3.0F,
      new AttributeContainer(AttributeRegistry.ELDRITCH_SPELL_POWER, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, 0.15, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.CAST_TIME_REDUCTION, 0.05, Operation.ADD_MULTIPLIED_BASE),
      new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.05, Operation.ADD_MULTIPLIED_BASE)
   );
   float damage;
   float speed;
   AttributeContainer[] attributeContainers;

   public HnSStaffTier(float damage, float speed, AttributeContainer... attributeContainers) {
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
