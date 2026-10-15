package net.alshanex.familiarslib.util;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class HelperMethods {
   public static float horizontalDistanceSqr(LivingEntity livingEntity, Vec3 vec3) {
      double dx = livingEntity.getX() - vec3.x;
      double dz = livingEntity.getZ() - vec3.z;
      return (float)(dx * dx + dz * dz);
   }

   public static List<AbstractSpell> getSpellsFromTag(TagKey<AbstractSpell> tag) {
      ArrayList<AbstractSpell> list = new ArrayList<>();

      for (AbstractSpell spell : SpellRegistry.getEnabledSpells()) {
         SpellRegistry.REGISTRY.getHolder(spell.getSpellResource()).ifPresent(a -> {
            if (a.is(tag)) {
               list.add(spell);
            }
         });
      }

      return list;
   }

   public static boolean isSpellInTag(AbstractSpell spell, TagKey<AbstractSpell> tag) {
      ArrayList<AbstractSpell> list = new ArrayList<>();
      SpellRegistry.REGISTRY.getHolder(spell.getSpellResource()).ifPresent(a -> {
         if (a.is(tag)) {
            list.add(spell);
         }
      });
      return !list.isEmpty();
   }
}
