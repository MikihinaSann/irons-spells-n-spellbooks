package net.hazen.hazennstuff.Compat;

import net.acetheeldritchking.discerning_the_eldritch.registries.DTEAttributeRegistry;
import net.hazen.hazennstuff.HazenNStuff;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.neoforged.fml.ModList;

public class DTECompat {
   public static boolean LOADED;

   public static void init() {
      LOADED = ModList.get().isLoaded("discerning_the_eldritch");
   }

   public static void addRitualSpellPowerPure(Builder attributes, EquipmentSlotGroup group) {
      if (LOADED) {
         DTECompat.LoadedOnly.addRitualSpellPowerPure(attributes, group);
      }
   }

   public static void addRitualSpellPowerParagon(Builder attributes, EquipmentSlotGroup group) {
      if (LOADED) {
         DTECompat.LoadedOnly.addRitualSpellPowerParagon(attributes, group);
      }
   }

   public static class LoadedOnly {
      public static void addRitualSpellPowerPure(Builder attributes, EquipmentSlotGroup group) {
         attributes.add(
            DTEAttributeRegistry.RITUAL_MAGIC_POWER,
            new AttributeModifier(HazenNStuff.id("add_ritual_spell_power_pure"), 0.15F, Operation.ADD_MULTIPLIED_BASE),
            group
         );
      }

      public static void addRitualSpellPowerParagon(Builder attributes, EquipmentSlotGroup group) {
         attributes.add(
            DTEAttributeRegistry.RITUAL_MAGIC_POWER,
            new AttributeModifier(HazenNStuff.id("add_ritual_spell_power_paragon"), 0.2F, Operation.ADD_MULTIPLIED_BASE),
            group
         );
      }
   }
}
