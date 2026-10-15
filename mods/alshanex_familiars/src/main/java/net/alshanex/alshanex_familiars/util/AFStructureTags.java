package net.alshanex.alshanex_familiars.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public interface AFStructureTags {
   TagKey<Structure> MEMORY_FRAGMENT_LOCATED = create("memory_fragment_located");

   private static TagKey<Structure> create(String pName) {
      return TagKey.create(Registries.STRUCTURE, new ResourceLocation("alshanex_familiars", pName));
   }
}
