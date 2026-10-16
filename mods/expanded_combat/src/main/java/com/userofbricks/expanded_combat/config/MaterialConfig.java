package com.userofbricks.expanded_combat.config;

import com.userofbricks.expanded_combat.api.material.PlacementInShield;
import com.userofbricks.expanded_combat.config.gui.ConfigEntryGui;
import com.userofbricks.expanded_combat.config.gui.ConfigName;
import com.userofbricks.expanded_combat.config.gui.TooltipFrase;
import com.userofbricks.expanded_combat.config.gui.TooltipFrases;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.shedaniel.autoconfig.annotation.ConfigEntry.BoundedDiscrete;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Tier;

public class MaterialConfig {
   @CollapsibleObject
   @ConfigName("Durability")
   public MaterialConfig.Durability durability;
   @CollapsibleObject
   @ConfigName("Enchanting")
   public MaterialConfig.Enchanting enchanting;
   @CollapsibleObject
   @ConfigName("Offence")
   public MaterialConfig.Offense offense;
   @CollapsibleObject
   @ConfigName("Defence")
   public MaterialConfig.Defense defense;
   @CollapsibleObject
   @ConfigName("Crafting")
   public MaterialConfig.Crafting crafting;

   MaterialConfig(
      int toolDurability,
      int gauntletDurability,
      int addedShieldDurability,
      int bowDurability,
      int offenseEnchantability,
      int defenseEnchantability,
      float mendingBonus,
      double addedAttackDamage,
      float arrowDamage,
      double defaultArrowGravity,
      boolean flaming,
      boolean canBeTipped,
      float velocityMultiplier,
      int quiverStacks,
      int gauntletArmorAmount,
      double armorToughness,
      double knockbackResistance,
      ResourceLocation equipSound,
      boolean fireResistant,
      boolean makesPiglinsNeutral,
      PlacementInShield placementInShield,
      float baseProtectionAmmount,
      float afterBasePercentReduction,
      boolean isSingleAddition,
      List<ResourceLocation> onlyReplaceResource
   ) {
      this.durability = new MaterialConfig.Durability(toolDurability, gauntletDurability, addedShieldDurability, bowDurability);
      this.enchanting = new MaterialConfig.Enchanting(offenseEnchantability, defenseEnchantability, mendingBonus);
      this.offense = new MaterialConfig.Offense(addedAttackDamage, arrowDamage, defaultArrowGravity, flaming, canBeTipped, velocityMultiplier, quiverStacks);
      this.defense = new MaterialConfig.Defense(
         gauntletArmorAmount,
         armorToughness,
         knockbackResistance,
         equipSound,
         fireResistant,
         makesPiglinsNeutral,
         placementInShield,
         baseProtectionAmmount,
         afterBasePercentReduction
      );
      this.crafting = new MaterialConfig.Crafting(isSingleAddition, onlyReplaceResource);
   }

   public static class Builder {
      private int toolDurability = 0;
      private int gauntletDurability = 0;
      private int bowDurability = 0;
      private int addedShieldDurability = 0;
      private int offenseEnchantability = 0;
      private int defenseEnchantability = 0;
      private float mendingBonus = 0.0F;
      private float addedAttackDamage = 0.0F;
      private float arrowDamage = 0.0F;
      private double defaultArrowGravity = 0.05;
      private boolean flaming = false;
      private boolean canBeTipped = true;
      private float velocityMultiplier = 1.0F;
      private int quiverStacks = 0;
      private int gauntletArmorAmount = 0;
      private double armorToughness = 0.0;
      private double knockbackResistance = 0.0;
      private ResourceLocation equipSound = ResourceLocation.parse("item.armor.equip_generic");
      private boolean fireResistant = false;
      private boolean makesPiglinsNeutral = false;
      private PlacementInShield placementInShield = PlacementInShield.NONE;
      private float baseProtectionAmmount = 0.0F;
      private float afterBasePercentReduction = 0.0F;
      private boolean isSingleAddition = false;
      private final List<ResourceLocation> onlyReplaceResource = new ArrayList<>();

      public MaterialConfig.Builder fromTierNoIngredient(Tier tier) {
         return this.toolDurability(tier.getUses())
            .gauntletDurability(tier.getUses())
            .offenseEnchantability(tier.getEnchantmentValue())
            .addedAttackDamage(tier.getAttackDamageBonus());
      }

      public MaterialConfig.Builder toolDurability(int durability) {
         this.toolDurability = durability;
         return this;
      }

      public MaterialConfig.Builder gauntletDurability(int durability) {
         this.gauntletDurability = durability;
         return this;
      }

      public MaterialConfig.Builder bowDurability(int durability) {
         this.bowDurability = durability;
         return this;
      }

      public MaterialConfig.Builder addedShieldDurability(int durability) {
         this.addedShieldDurability = durability;
         return this;
      }

      public MaterialConfig.Builder offenseEnchantability(int enchantability) {
         this.offenseEnchantability = enchantability;
         return this;
      }

      public MaterialConfig.Builder defenseEnchantability(int enchantability) {
         this.defenseEnchantability = enchantability;
         return this;
      }

      public MaterialConfig.Builder equipSound(String equipSound) {
         this.equipSound = ResourceLocation.parse(equipSound);
         return this;
      }

      public MaterialConfig.Builder equipSound(ResourceLocation equipSound) {
         this.equipSound = equipSound;
         return this;
      }

      public MaterialConfig.Builder equipSound(SoundEvent equipSound) {
         this.equipSound = equipSound.getLocation();
         return this;
      }

      public MaterialConfig.Builder mendingBonus(float mendingBonus) {
         this.mendingBonus = mendingBonus;
         return this;
      }

      public MaterialConfig.Builder fireResistant() {
         this.fireResistant = true;
         return this;
      }

      public MaterialConfig.Builder makesPiglinsNeutral() {
         this.makesPiglinsNeutral = true;
         return this;
      }

      public MaterialConfig.Builder shield(PlacementInShield placementInShield) {
         this.placementInShield = placementInShield;
         return this;
      }

      public MaterialConfig.Builder shield() {
         this.placementInShield = PlacementInShield.ALL;
         return this;
      }

      public MaterialConfig.Builder addedAttackDamage(float damage) {
         this.addedAttackDamage = damage;
         return this;
      }

      public MaterialConfig.Builder arrowDamage(float damage) {
         this.arrowDamage = damage;
         return this;
      }

      public MaterialConfig.Builder arrowGravity(double defaultArrowGravity) {
         this.defaultArrowGravity = defaultArrowGravity;
         return this;
      }

      public MaterialConfig.Builder flaming() {
         this.flaming = true;
         return this;
      }

      public MaterialConfig.Builder noTippedArrows() {
         this.canBeTipped = false;
         return this;
      }

      public MaterialConfig.Builder velocityMultiplier(float velocityMultiplier) {
         this.velocityMultiplier = velocityMultiplier;
         return this;
      }

      public MaterialConfig.Builder gauntletArmorAmount(int armor) {
         this.gauntletArmorAmount = armor;
         return this;
      }

      public MaterialConfig.Builder armorToughness(double toughness) {
         this.armorToughness = toughness;
         return this;
      }

      public MaterialConfig.Builder knockbackResistance(double resistance) {
         this.knockbackResistance = resistance;
         return this;
      }

      public MaterialConfig.Builder baseProtectionAmmount(float damage) {
         this.baseProtectionAmmount = damage;
         return this;
      }

      public MaterialConfig.Builder afterBasePercentReduction(float percent) {
         this.afterBasePercentReduction = percent;
         return this;
      }

      public MaterialConfig.Builder singleAddition() {
         this.isSingleAddition = true;
         return this;
      }

      public MaterialConfig.Builder onlyReplaceResource(String... materials) {
         this.onlyReplaceResource.addAll(Arrays.stream(materials).map(ResourceLocation::parse).toList());
         return this;
      }

      public MaterialConfig.Builder onlyReplaceResource(ResourceLocation... materials) {
         this.onlyReplaceResource.addAll(Arrays.asList(materials));
         return this;
      }

      public MaterialConfig.Builder quiverSlots(int slots) {
         this.quiverStacks = slots;
         return this;
      }

      public MaterialConfig build() {
         return new MaterialConfig(
            this.toolDurability,
            this.gauntletDurability,
            this.addedShieldDurability,
            this.bowDurability,
            this.offenseEnchantability,
            this.defenseEnchantability,
            this.mendingBonus,
            this.addedAttackDamage,
            this.arrowDamage,
            this.defaultArrowGravity,
            this.flaming,
            this.canBeTipped,
            this.velocityMultiplier,
            this.quiverStacks,
            this.gauntletArmorAmount,
            this.armorToughness,
            this.knockbackResistance,
            this.equipSound,
            this.fireResistant,
            this.makesPiglinsNeutral,
            this.placementInShield,
            this.baseProtectionAmmount,
            this.afterBasePercentReduction,
            this.isSingleAddition,
            this.onlyReplaceResource
         );
      }
   }

   public static class Crafting {
      @ConfigName("Single Addition")
      @Tooltip
      @TooltipFrase("Weather this material is applied to every part of a shield\nwith one item verses using one for each of the five parts")
      boolean isSingleAddition;
      @ConfigName("Can Replace Materials")
      @Tooltip
      @TooltipFrase("A list of what materials this one can replace on a shield.\nLeave empty if it can replace anything")
      @ConfigEntryGui.Material
      List<ResourceLocation> onlyReplaceResource;

      public Crafting(boolean isSingleAddition, List<ResourceLocation> onlyReplaceResource) {
         this.isSingleAddition = isSingleAddition;
         this.onlyReplaceResource = onlyReplaceResource;
      }

      public boolean isSingleAddition() {
         return this.isSingleAddition;
      }

      public List<ResourceLocation> onlyReplaceResource() {
         return this.onlyReplaceResource;
      }
   }

   public static class Defense {
      @ConfigName("Gauntlet Armor Amount")
      @BoundedDiscrete(
         max = 512L
      )
      int gauntletArmorAmount;
      @ConfigName("Armor Toughness")
      double armorToughness;
      @ConfigName("Knockback Resistance")
      double knockbackResistance;
      @ConfigName("Equip Sound")
      @ConfigEntryGui.SoundEvent
      ResourceLocation equipSound;
      @ConfigName("Fire Resistant")
      boolean fireResistant;
      @ConfigName("Makes Piglins Neutral")
      boolean makesPiglinsNeutral;
      @ConfigName("Placement in Shield")
      @EnumHandler
      @Tooltip
      @TooltipFrase("this is the possible locations in a shield that this material can be placed when crafting")
      PlacementInShield placementInShield;
      @ConfigName("Base Protection Amount")
      @Tooltip(
         count = 2
      )
      @TooltipFrases({@TooltipFrase("Defines the amount of Damage a shield entirely made of this material will block"), @TooltipFrase(
            line = 1,
            value = "Only works if PREDEFINED_AMMOUNT is selected in the Shield Protection Settings"
         )})
      float baseProtectionAmmount;
      @Tooltip(
         count = 2
      )
      @ConfigName("After Base Percent Protection")
      @TooltipFrases({@TooltipFrase("Defines the percent of Damage a shield entirely made of this material will block after the Base amount has been blocked"), @TooltipFrase(
            line = 1,
            value = "Only works if Shield Protection Percentage is enabled in the Shield Protection Settings"
         )})
      float afterBasePercentReduction;

      public Defense(
         int gauntletArmorAmount,
         double armorToughness,
         double knockbackResistance,
         ResourceLocation equipSound,
         boolean fireResistant,
         boolean makesPiglinsNeutral,
         PlacementInShield placementInShield,
         float baseProtectionAmmount,
         float afterBasePercentReduction
      ) {
         this.gauntletArmorAmount = gauntletArmorAmount;
         this.armorToughness = armorToughness;
         this.knockbackResistance = knockbackResistance;
         this.equipSound = equipSound;
         this.fireResistant = fireResistant;
         this.makesPiglinsNeutral = makesPiglinsNeutral;
         this.placementInShield = placementInShield;
         this.baseProtectionAmmount = baseProtectionAmmount;
         this.afterBasePercentReduction = afterBasePercentReduction;
      }

      public int gauntletArmorAmount() {
         return this.gauntletArmorAmount;
      }

      public double armorToughness() {
         return this.armorToughness;
      }

      public double knockbackResistance() {
         return this.knockbackResistance;
      }

      public ResourceLocation equipSound() {
         return this.equipSound;
      }

      public boolean fireResistant() {
         return this.fireResistant;
      }

      public boolean makesPiglinsNeutral() {
         return this.makesPiglinsNeutral;
      }

      public PlacementInShield placementInShield() {
         return this.placementInShield;
      }

      public float baseProtectionAmmount() {
         return this.baseProtectionAmmount;
      }

      public float afterBasePercentReduction() {
         return this.afterBasePercentReduction;
      }
   }

   public static class Durability {
      @BoundedDiscrete(
         max = 2147483647L
      )
      @ConfigName("Tool Durability")
      @Tooltip
      @TooltipFrase("This is used as the base durability for weapons, which is multiplied with the weapons durability multiplier to get the final durability")
      int toolBaseDurability;
      @BoundedDiscrete(
         max = 2147483647L
      )
      @ConfigName("Gauntlet Durability")
      int gauntletDurability;
      @BoundedDiscrete(
         max = 2147483647L
      )
      @ConfigName("Bow/Crossbow Durability")
      int bowCrossbowDurability;
      @BoundedDiscrete(
         max = 2147483647L
      )
      @ConfigName("Added Shield Durability")
      @Tooltip
      @TooltipFrase("this is the amount of durability added by each of the five sections, onto the base wood shield durability")
      int addedShieldDurability;

      public Durability(int toolBaseDurability, int gauntletDurability, int bowCrossbowDurability, int addedShieldDurability) {
         this.toolBaseDurability = toolBaseDurability;
         this.gauntletDurability = gauntletDurability;
         this.bowCrossbowDurability = bowCrossbowDurability;
         this.addedShieldDurability = addedShieldDurability;
      }

      public int addedShieldDurability() {
         return this.addedShieldDurability;
      }

      public int bowCrossbowDurability() {
         return this.bowCrossbowDurability;
      }

      public int gauntletDurability() {
         return this.gauntletDurability;
      }

      public int toolBaseDurability() {
         return this.toolBaseDurability;
      }
   }

   public static class Enchanting {
      @BoundedDiscrete(
         max = 512L
      )
      @ConfigName("Offence Enchantability")
      @Tooltip
      @TooltipFrase("The enchantability of weapons and half of a gauntlet's")
      int offenseEnchantability;
      @BoundedDiscrete(
         max = 512L
      )
      @ConfigName("Defence Enchantability")
      @Tooltip
      @TooltipFrase("The enchantability of shields and half of a gauntlet's")
      int defenseEnchantability;
      @ConfigName("Mending Bonus")
      @Tooltip
      @TooltipFrase("Added to the vanilla base mending multiplier of 2")
      float mendingBonus;

      public int offenseEnchantability() {
         return this.offenseEnchantability;
      }

      public int defenseEnchantability() {
         return this.defenseEnchantability;
      }

      public float mendingBonus() {
         return this.mendingBonus;
      }

      public Enchanting(int offenseEnchantability, int defenseEnchantability, float mendingBonus) {
         this.offenseEnchantability = offenseEnchantability;
         this.defenseEnchantability = defenseEnchantability;
         this.mendingBonus = mendingBonus;
      }
   }

   public static class Offense {
      @ConfigName("Added Attack Damage")
      @Tooltip
      @TooltipFrase("used for gauntlet damage and also added to melee weapon base damage")
      double addedAttackDamage;
      @ConfigName("Arrow Damage")
      float arrowDamage;
      @ConfigName("Arrow Gravity")
      double defaultArrowGravity;
      @ConfigName("Flaming Arrow")
      boolean flaming;
      @ConfigName("Can Arrow Be Tipped With Potions")
      boolean canBeTipped;
      @BoundedDiscrete(
         max = 100L
      )
      @ConfigName("Arrow Velocity Multiplier")
      @Tooltip
      @TooltipFrase("used when firing a bow or crossbow")
      float velocityMultiplier;
      @ConfigName("QuiverStacks")
      @BoundedDiscrete(
         min = 1L,
         max = 64L
      )
      int quiverStacks;

      public Offense(
         double addedAttackDamage,
         float arrowDamage,
         double defaultArrowGravity,
         boolean flaming,
         boolean canBeTipped,
         float velocityMultiplier,
         int quiverStacks
      ) {
         this.addedAttackDamage = addedAttackDamage;
         this.arrowDamage = arrowDamage;
         this.defaultArrowGravity = defaultArrowGravity;
         this.flaming = flaming;
         this.canBeTipped = canBeTipped;
         this.velocityMultiplier = velocityMultiplier;
         this.quiverStacks = quiverStacks;
      }

      public double addedAttackDamage() {
         return this.addedAttackDamage;
      }

      public float arrowDamage() {
         return this.arrowDamage;
      }

      public double defaultArrowGravity() {
         return this.defaultArrowGravity;
      }

      public boolean flaming() {
         return this.flaming;
      }

      public boolean canBeTipped() {
         return this.canBeTipped;
      }

      public float velocityMultiplier() {
         return this.velocityMultiplier;
      }

      public int quiverStacks() {
         return this.quiverStacks;
      }
   }
}
