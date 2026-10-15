package net.hazen.hazennstuff.Compat;

import net.acetheeldritchking.cataclysm_spellbooks.registries.CSAttributeRegistry;
import net.hazen.hazennstuff.HazenNStuff;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.neoforged.fml.ModList;

public class CSCompat {
   public static boolean LOADED;

   public static void init() {
      LOADED = ModList.get().isLoaded("cataclysm_spellbooks");
   }

   public static void addAbyssalSpellPowerPure(Builder attributes, EquipmentSlotGroup group) {
      if (LOADED) {
         CSCompat.LoadedOnly.addAbyssalSpellPowerPure(attributes, group);
      }
   }

   public static void addAbyssalSpellPowerParagon(Builder attributes, EquipmentSlotGroup group) {
      if (LOADED) {
         CSCompat.LoadedOnly.addAbyssalSpellPowerParagon(attributes, group);
      }
   }

   public static class LoadedOnly {
      public static void addAbyssalSpellPowerPure(Builder attributes, EquipmentSlotGroup group) {
         attributes.add(
            CSAttributeRegistry.ABYSSAL_MAGIC_POWER,
            new AttributeModifier(HazenNStuff.id("add_abyssal_spell_power_pure"), 0.15F, Operation.ADD_MULTIPLIED_BASE),
            group
         );
      }

      public static void addAbyssalSpellPowerParagon(Builder attributes, EquipmentSlotGroup group) {
         attributes.add(
            CSAttributeRegistry.ABYSSAL_MAGIC_POWER,
            new AttributeModifier(HazenNStuff.id("add_abyssal_spell_power_paragon"), 0.2F, Operation.ADD_MULTIPLIED_BASE),
            group
         );
      }
   }
}
