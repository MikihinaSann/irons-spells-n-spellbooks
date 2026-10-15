package net.alshanex.familiarslib.util.consumables;

import com.mojang.serialization.Codec;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.alshanex.familiarslib.entity.AbstractSpellCastingPet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class FamiliarConsumableSystem {
   private static final ResourceLocation CONSUMABLE_ARMOR_ID = ResourceLocation.fromNamespaceAndPath("familiarslib", "consumable_armor");
   private static final ResourceLocation CONSUMABLE_HEALTH_ID = ResourceLocation.fromNamespaceAndPath("familiarslib", "consumable_health");
   private static final ResourceLocation CONSUMABLE_SPELL_POWER_ID = ResourceLocation.fromNamespaceAndPath("familiarslib", "consumable_spell_power");
   private static final ResourceLocation CONSUMABLE_SPELL_RESIST_ID = ResourceLocation.fromNamespaceAndPath("familiarslib", "consumable_spell_resist");
   private static final ResourceLocation CONSUMABLE_ENRAGED_ID = ResourceLocation.fromNamespaceAndPath("familiarslib", "consumable_enraged");
   private static final ResourceLocation CONSUMABLE_BLOCKING_ID = ResourceLocation.fromNamespaceAndPath("familiarslib", "consumable_blocking");

   public static void saveConsumableDataToNBT(FamiliarConsumableSystem.ConsumableData data, CompoundTag tag) {
      data.saveToNBT(tag);
   }

   public static FamiliarConsumableSystem.ConsumableData loadConsumableDataFromNBT(CompoundTag tag) {
      return new FamiliarConsumableSystem.ConsumableData(tag);
   }

   public static void applyAttributeModifiers(AbstractSpellCastingPet familiar, FamiliarConsumableSystem.ConsumableData data) {
      removeAllConsumableModifiers(familiar);
      int armorBonus = data.getValue(FamiliarConsumableSystem.ConsumableType.ARMOR);
      if (armorBonus > 0) {
         AttributeInstance armorAttribute = familiar.getAttribute(Attributes.ARMOR);
         if (armorAttribute != null) {
            AttributeModifier modifier = new AttributeModifier(CONSUMABLE_ARMOR_ID, armorBonus, Operation.ADD_VALUE);
            armorAttribute.addPermanentModifier(modifier);
         }
      }

      int healthBonus = data.getValue(FamiliarConsumableSystem.ConsumableType.HEALTH);
      if (healthBonus > 0) {
         AttributeInstance healthAttribute = familiar.getAttribute(Attributes.MAX_HEALTH);
         if (healthAttribute != null) {
            double multiplier = healthBonus / 100.0;
            AttributeModifier modifier = new AttributeModifier(CONSUMABLE_HEALTH_ID, multiplier, Operation.ADD_MULTIPLIED_BASE);
            healthAttribute.addPermanentModifier(modifier);
         }
      }

      int spellPowerBonus = data.getValue(FamiliarConsumableSystem.ConsumableType.SPELL_POWER);
      if (spellPowerBonus > 0) {
         AttributeInstance spellPowerAttribute = familiar.getAttribute(AttributeRegistry.SPELL_POWER);
         if (spellPowerAttribute != null) {
            double multiplier = spellPowerBonus / 100.0;
            AttributeModifier modifier = new AttributeModifier(CONSUMABLE_SPELL_POWER_ID, multiplier, Operation.ADD_MULTIPLIED_BASE);
            spellPowerAttribute.addPermanentModifier(modifier);
         }
      }

      int spellResistBonus = data.getValue(FamiliarConsumableSystem.ConsumableType.SPELL_RESIST);
      if (spellResistBonus > 0) {
         AttributeInstance spellResistAttribute = familiar.getAttribute(AttributeRegistry.SPELL_RESIST);
         if (spellResistAttribute != null) {
            double multiplier = spellResistBonus / 100.0;
            AttributeModifier modifier = new AttributeModifier(CONSUMABLE_SPELL_RESIST_ID, multiplier, Operation.ADD_MULTIPLIED_BASE);
            spellResistAttribute.addPermanentModifier(modifier);
         }
      }

      int enragedStacks = data.getValue(FamiliarConsumableSystem.ConsumableType.ENRAGED);
      if (enragedStacks > 0) {
         AttributeInstance attackDamageAttribute = familiar.getAttribute(Attributes.ATTACK_DAMAGE);
         if (attackDamageAttribute != null) {
            double bonus = enragedStacks * 0.5;
            AttributeModifier modifier = new AttributeModifier(CONSUMABLE_ENRAGED_ID, bonus, Operation.ADD_VALUE);
            attackDamageAttribute.addPermanentModifier(modifier);
         }
      }
   }

   public static void removeAllConsumableModifiers(AbstractSpellCastingPet familiar) {
      AttributeInstance armor = familiar.getAttribute(Attributes.ARMOR);
      if (armor != null) {
         AttributeModifier armorModifier = armor.getModifier(CONSUMABLE_ARMOR_ID);
         if (armorModifier != null) {
            armor.removeModifier(armorModifier);
         }
      }

      AttributeInstance health = familiar.getAttribute(Attributes.MAX_HEALTH);
      if (health != null) {
         AttributeModifier healthModifier = health.getModifier(CONSUMABLE_HEALTH_ID);
         if (healthModifier != null) {
            health.removeModifier(healthModifier);
         }
      }

      AttributeInstance spellPower = familiar.getAttribute(AttributeRegistry.SPELL_POWER);
      if (spellPower != null) {
         AttributeModifier spellPowerModifier = spellPower.getModifier(CONSUMABLE_SPELL_POWER_ID);
         if (spellPowerModifier != null) {
            spellPower.removeModifier(spellPowerModifier);
         }
      }

      AttributeInstance spellResist = familiar.getAttribute(AttributeRegistry.SPELL_RESIST);
      if (spellResist != null) {
         AttributeModifier spellResistModifier = spellResist.getModifier(CONSUMABLE_SPELL_RESIST_ID);
         if (spellResistModifier != null) {
            spellResist.removeModifier(spellResistModifier);
         }
      }

      AttributeInstance attackDamage = familiar.getAttribute(Attributes.ATTACK_DAMAGE);
      if (attackDamage != null) {
         AttributeModifier enragedModifier = attackDamage.getModifier(CONSUMABLE_ENRAGED_ID);
         if (enragedModifier != null) {
            attackDamage.removeModifier(enragedModifier);
         }
      }
   }

   public static void removeLegacyModifiers(AbstractSpellCastingPet familiar) {
      ResourceLocation legacyHealthModifierId = ResourceLocation.fromNamespaceAndPath("familiarslib", "familiar_health_modifier");
      ResourceLocation legacyArmorModifierId = ResourceLocation.fromNamespaceAndPath("familiarslib", "familiar_armor_modifier");
      AttributeInstance health = familiar.getAttribute(Attributes.MAX_HEALTH);
      if (health != null) {
         AttributeModifier legacyHealthModifier = health.getModifier(legacyHealthModifierId);
         if (legacyHealthModifier != null) {
            health.removeModifier(legacyHealthModifier);
         }
      }

      AttributeInstance armor = familiar.getAttribute(Attributes.ARMOR);
      if (armor != null) {
         AttributeModifier legacyArmorModifier = armor.getModifier(legacyArmorModifierId);
         if (legacyArmorModifier != null) {
            armor.removeModifier(legacyArmorModifier);
         }
      }
   }

   public static class ConsumableData {
      private byte armor;
      private byte health;
      private byte spellPower;
      private byte spellResist;
      private byte spellLevel;
      private byte enraged;
      private byte blocking;

      public ConsumableData() {
      }

      public ConsumableData(CompoundTag tag) {
         this.loadFromNBT(tag);
      }

      public void saveToNBT(CompoundTag tag) {
         tag.putByte("consumableArmor", this.armor);
         tag.putByte("consumableHealth", this.health);
         tag.putByte("consumableSpellPower", this.spellPower);
         tag.putByte("consumableSpellResist", this.spellResist);
         tag.putByte("consumableSpellLevel", this.spellLevel);
         tag.putByte("consumableEnraged", this.enraged);
         tag.putByte("consumableBlocking", this.blocking);
      }

      public void loadFromNBT(CompoundTag tag) {
         if (tag.contains("consumableArmor")) {
            this.armor = tag.getByte("consumableArmor");
            this.health = tag.getByte("consumableHealth");
            this.spellPower = tag.getByte("consumableSpellPower");
            this.spellResist = tag.getByte("consumableSpellResist");
            this.spellLevel = tag.getByte("consumableSpellLevel");
            this.enraged = tag.getByte("consumableEnraged");
            this.blocking = tag.getByte("consumableBlocking");
         }
      }

      public int getValue(FamiliarConsumableSystem.ConsumableType type) {
         return switch (type) {
            case ARMOR -> this.armor & 255;
            case HEALTH -> this.health & 255;
            case SPELL_POWER -> this.spellPower & 255;
            case SPELL_RESIST -> this.spellResist & 255;
            case ENRAGED -> this.enraged & 255;
            case BLOCKING -> this.blocking & 255;
         };
      }

      public void setValue(FamiliarConsumableSystem.ConsumableType type, int value) {
         value = Math.max(0, Math.min(255, value));
         switch (type) {
            case ARMOR:
               this.armor = (byte)value;
               break;
            case HEALTH:
               this.health = (byte)value;
               break;
            case SPELL_POWER:
               this.spellPower = (byte)value;
               break;
            case SPELL_RESIST:
               this.spellResist = (byte)value;
               break;
            case ENRAGED:
               this.enraged = (byte)value;
               break;
            case BLOCKING:
               this.blocking = (byte)value;
         }
      }

      public boolean hasAnyData() {
         return this.armor != 0
            || this.health != 0
            || this.spellPower != 0
            || this.spellResist != 0
            || this.spellLevel != 0
            || this.enraged != 0
            || this.blocking != 0;
      }

      @Override
      public String toString() {
         return String.format(
            "ConsumableData{armor=%d, health=%d, spellPower=%d, spellResist=%d, spellLevel=%d, enraged=%d, blocking=%d}",
            this.armor & 255,
            this.health & 255,
            this.spellPower & 255,
            this.spellResist & 255,
            this.spellLevel & 255,
            this.enraged & 255,
            this.blocking & 255
         );
      }
   }

   public static enum ConsumableType implements StringRepresentable {
      ARMOR(0, new int[]{2, 3, 4}, new int[]{10, 16, 20}, "armor"),
      HEALTH(1, new int[]{10, 25, 50}, new int[]{50, 100, 150}, "health"),
      SPELL_POWER(2, new int[]{3, 5, 15}, new int[]{15, 30, 45}, "spell_power"),
      SPELL_RESIST(3, new int[]{3, 5, 15}, new int[]{15, 30, 45}, "spell_resist"),
      ENRAGED(5, new int[]{1}, new int[]{10}, "enraged"),
      BLOCKING(6, new int[]{1}, new int[]{1}, "blocking");

      public static final Codec<FamiliarConsumableSystem.ConsumableType> CODEC = StringRepresentable.fromEnum(FamiliarConsumableSystem.ConsumableType::values);
      public static final StreamCodec<RegistryFriendlyByteBuf, FamiliarConsumableSystem.ConsumableType> STREAM_CODEC = ByteBufCodecs.idMapper(id -> {
         for (FamiliarConsumableSystem.ConsumableType type : values()) {
            if (type.getId() == id) {
               return type;
            }
         }

         return ARMOR;
      }, FamiliarConsumableSystem.ConsumableType::getId).cast();
      private final int id;
      private final int[] tierBonuses;
      private final int[] tierLimits;
      private final String serializedName;

      private ConsumableType(int id, int[] tierBonuses, int[] tierLimits, String serializedName) {
         this.id = id;
         this.tierBonuses = tierBonuses;
         this.tierLimits = tierLimits;
         this.serializedName = serializedName;
      }

      public int getId() {
         return this.id;
      }

      public int getTierBonus(int tier) {
         return this.tierBonuses[tier - 1];
      }

      public int getTierLimit(int tier) {
         return this.tierLimits[tier - 1];
      }

      public int getMaxLimit() {
         return this.tierLimits[this.tierLimits.length - 1];
      }

      public int getMaxTier() {
         return this.tierBonuses.length;
      }

      public String getSerializedName() {
         return this.serializedName;
      }
   }
}
