package net.alshanex.tunes_n_tomes.registry;

import io.redspace.ironsspellbooks.api.attribute.MagicPercentAttribute;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TAttributeRegistry {
   public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, "tunes_n_tomes");
   public static final DeferredHolder<Attribute, Attribute> MELODY_MAGIC_RESIST = newResistanceAttribute("melody");
   public static final DeferredHolder<Attribute, Attribute> MELODY_SPELL_POWER = newPowerAttribute("melody");

   private static DeferredHolder<Attribute, Attribute> newResistanceAttribute(String id) {
      return ATTRIBUTES.register(
         id + "_magic_resist", () -> new MagicPercentAttribute("attribute.tunes_n_tomes." + id + "_magic_resist", 1.0, -100.0, 100.0).setSyncable(true)
      );
   }

   private static DeferredHolder<Attribute, Attribute> newPowerAttribute(String id) {
      return ATTRIBUTES.register(
         id + "_spell_power", () -> new MagicPercentAttribute("attribute.tunes_n_tomes." + id + "_spell_power", 1.0, -100.0, 100.0).setSyncable(true)
      );
   }
}
