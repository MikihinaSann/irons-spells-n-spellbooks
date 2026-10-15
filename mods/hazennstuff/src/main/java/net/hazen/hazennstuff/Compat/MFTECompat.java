package net.hazen.hazennstuff.Compat;

import net.hazen.hazennstuff.HazenNStuff;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.neoforged.fml.ModList;
import net.warphan.iss_magicfromtheeast.registries.MFTEAttributeRegistries;

public class MFTECompat {
   public static boolean LOADED;

   public static void init() {
      LOADED = ModList.get().isLoaded("iss_magicfromtheeast");
   }

   public static void addSpiritSpellPowerPure(Builder attributes, EquipmentSlotGroup group) {
      if (LOADED) {
         MFTECompat.LoadedOnly.addSpiritSpellPowerPure(attributes, group);
      }
   }

   public static void addSpiritSpellPower(Builder attributes, EquipmentSlotGroup group) {
      if (LOADED) {
         MFTECompat.LoadedOnly.addSpiritSpellPowerParagon(attributes, group);
      }
   }

   public static void addDuneSpellPower(Builder attributes, EquipmentSlotGroup group) {
      if (LOADED) {
         MFTECompat.LoadedOnly.addDuneSpellPowerPure(attributes, group);
      }
   }

   public static void addDuneSpellPowerParagon(Builder attributes, EquipmentSlotGroup group) {
      if (LOADED) {
         MFTECompat.LoadedOnly.addDuneSpellPowerParagon(attributes, group);
      }
   }

   public static void addSymmetrySpellPower(Builder attributes, EquipmentSlotGroup group) {
      if (LOADED) {
         MFTECompat.LoadedOnly.addSymmetrySpellPowerPure(attributes, group);
      }
   }

   public static void addSymmetrySpellPowerParagon(Builder attributes, EquipmentSlotGroup group) {
      if (LOADED) {
         MFTECompat.LoadedOnly.addSymmetrySpellPowerParagon(attributes, group);
      }
   }

   public static class LoadedOnly {
      public static void addSpiritSpellPowerPure(Builder attributes, EquipmentSlotGroup group) {
         attributes.add(
            MFTEAttributeRegistries.SPIRIT_SPELL_POWER,
            new AttributeModifier(HazenNStuff.id("add_spirit_spell_power_pure"), 0.15F, Operation.ADD_MULTIPLIED_BASE),
            group
         );
      }

      public static void addDuneSpellPowerPure(Builder attributes, EquipmentSlotGroup group) {
         attributes.add(
            MFTEAttributeRegistries.DUNE_SPELL_POWER,
            new AttributeModifier(HazenNStuff.id("add_dune_spell_power_pure"), 0.15F, Operation.ADD_MULTIPLIED_BASE),
            group
         );
      }

      public static void addSymmetrySpellPowerPure(Builder attributes, EquipmentSlotGroup group) {
         attributes.add(
            MFTEAttributeRegistries.SYMMETRY_SPELL_POWER,
            new AttributeModifier(HazenNStuff.id("add_symmetry_spell_power_pure"), 0.15F, Operation.ADD_MULTIPLIED_BASE),
            group
         );
      }

      public static void addSpiritSpellPowerParagon(Builder attributes, EquipmentSlotGroup group) {
         attributes.add(
            MFTEAttributeRegistries.SPIRIT_SPELL_POWER,
            new AttributeModifier(HazenNStuff.id("add_spirit_spell_power_paragon"), 0.2F, Operation.ADD_MULTIPLIED_BASE),
            group
         );
      }

      public static void addDuneSpellPowerParagon(Builder attributes, EquipmentSlotGroup group) {
         attributes.add(
            MFTEAttributeRegistries.DUNE_SPELL_POWER,
            new AttributeModifier(HazenNStuff.id("add_dune_spell_power_paragon"), 0.2F, Operation.ADD_MULTIPLIED_BASE),
            group
         );
      }

      public static void addSymmetrySpellPowerParagon(Builder attributes, EquipmentSlotGroup group) {
         attributes.add(
            MFTEAttributeRegistries.SYMMETRY_SPELL_POWER,
            new AttributeModifier(HazenNStuff.id("add_symmetry_spell_power_paragon"), 0.2F, Operation.ADD_MULTIPLIED_BASE),
            group
         );
      }
   }
}
