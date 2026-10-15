package net.hazen.hazennstuff.Compat;

import net.alshanex.familiarslib.registry.AttributeRegistry;
import net.hazen.hazennstuff.HazenNStuff;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.neoforged.fml.ModList;

public class FLCompat {
   public static boolean LOADED;

   public static void init() {
      LOADED = ModList.get().isLoaded("familiarslib");
   }

   public static void addSoundSpellPowerPure(Builder attributes, EquipmentSlotGroup group) {
      if (LOADED) {
         FLCompat.LoadedOnly.addSoundSpellPowerPure(attributes, group);
      }
   }

   public static void addSoundSpellPowerParagon(Builder attributes, EquipmentSlotGroup group) {
      if (LOADED) {
         FLCompat.LoadedOnly.addSoundSpellPowerParagon(attributes, group);
      }
   }

   public static class LoadedOnly {
      public static void addSoundSpellPowerPure(Builder attributes, EquipmentSlotGroup group) {
         attributes.add(
            AttributeRegistry.SOUND_SPELL_POWER,
            new AttributeModifier(HazenNStuff.id("add_sound_spell_power_pure"), 0.15F, Operation.ADD_MULTIPLIED_BASE),
            group
         );
      }

      public static void addSoundSpellPowerParagon(Builder attributes, EquipmentSlotGroup group) {
         attributes.add(
            AttributeRegistry.SOUND_SPELL_POWER,
            new AttributeModifier(HazenNStuff.id("add_sound_spell_power_paragon"), 0.2F, Operation.ADD_MULTIPLIED_BASE),
            group
         );
      }
   }
}
