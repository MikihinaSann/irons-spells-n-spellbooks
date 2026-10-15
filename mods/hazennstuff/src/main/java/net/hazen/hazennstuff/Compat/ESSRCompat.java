package net.hazen.hazennstuff.Compat;


import net.hazen.hazennstuff.HazenNStuff;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.neoforged.fml.ModList;

public class ESSRCompat {
   public static boolean LOADED;

   public static void init() {
      LOADED = ModList.get().isLoaded("ess_requiem");
   }

   public static void addSpellbladeSpellPowerPure(Builder attributes, EquipmentSlotGroup group) {
      if (LOADED) {
         ESSRCompat.LoadedOnly.addSpellbladeSpellPowerPure(attributes, group);
      }
   }

   public static void addSpellbladeSpellPowerParagon(Builder attributes, EquipmentSlotGroup group) {
      if (LOADED) {
         ESSRCompat.LoadedOnly.addSpellbladeSpellPowerParagon(attributes, group);
      }
   }

   public static class LoadedOnly {
      public static void addSpellbladeSpellPowerPure(Builder attributes, EquipmentSlotGroup group) {
      }

      public static void addSpellbladeSpellPowerParagon(Builder attributes, EquipmentSlotGroup group) {
      }
   }
}
