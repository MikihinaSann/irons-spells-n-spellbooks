package net.alshanex.tunes_n_tomes.registry;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.function.Supplier;
import net.alshanex.tunes_n_tomes.datagen.ItemTagProvider;
import net.alshanex.tunes_n_tomes.datagen.TDamageTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TSchoolRegistry {
   public static final ResourceLocation MELODY_RESOURCE = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "melody");
   public static final DeferredRegister<SchoolType> SCHOOLS = DeferredRegister.create(SchoolRegistry.SCHOOL_REGISTRY_KEY, "tunes_n_tomes");
   public static final Supplier<SchoolType> MELODY = registerSchool(
      new SchoolType(
         MELODY_RESOURCE,
         ItemTagProvider.MELODY_FOCUS,
         Component.translatable("school.tunes_n_tomes.melody").withColor(13631442),
         TAttributeRegistry.MELODY_SPELL_POWER,
         TAttributeRegistry.MELODY_MAGIC_RESIST,
         SoundRegistry.GUST_CAST,
         TDamageTypes.MELODY_MAGIC
      )
   );

   private static Supplier<SchoolType> registerSchool(SchoolType schoolType) {
      return SCHOOLS.register(schoolType.getId().getPath(), () -> schoolType);
   }
}
