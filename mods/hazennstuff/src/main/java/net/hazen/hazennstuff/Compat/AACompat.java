package net.hazen.hazennstuff.Compat;

import com.snackpirate.aeromancy.spells.AASpells.Attributes;
import net.hazen.hazennstuff.HazenNStuff;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.neoforged.fml.ModList;

public class AACompat {
   public static boolean LOADED;

   public static void init() {
      LOADED = ModList.get().isLoaded("aero_additions");
   }

   public static void addWindSpellPowerPure(Builder attributes, EquipmentSlotGroup group) {
      if (LOADED) {
         AACompat.LoadedOnly.addWindSpellPowerPure(attributes, group);
      }
   }

   public static void addWindSpellPowerParagon(Builder attributes, EquipmentSlotGroup group) {
      if (LOADED) {
         AACompat.LoadedOnly.addWindSpellPowerParagon(attributes, group);
      }
   }

   public static class LoadedOnly {
      public static void addWindSpellPowerPure(Builder attributes, EquipmentSlotGroup group) {
         attributes.add(
            Attributes.WIND_SPELL_POWER, new AttributeModifier(HazenNStuff.id("add_wind_spell_power_pure"), 0.15F, Operation.ADD_MULTIPLIED_BASE), group
         );
      }

      public static void addWindSpellPowerParagon(Builder attributes, EquipmentSlotGroup group) {
         attributes.add(
            Attributes.WIND_SPELL_POWER, new AttributeModifier(HazenNStuff.id("add_wind_spell_power_paragon"), 0.2F, Operation.ADD_MULTIPLIED_BASE), group
         );
      }
   }
}
