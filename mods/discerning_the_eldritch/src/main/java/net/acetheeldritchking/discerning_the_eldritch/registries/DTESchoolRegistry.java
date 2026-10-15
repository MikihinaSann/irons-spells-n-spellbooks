package net.acetheeldritchking.discerning_the_eldritch.registries;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.function.Supplier;
import net.acetheeldritchking.discerning_the_eldritch.DiscerningTheEldritch;
import net.acetheeldritchking.discerning_the_eldritch.utils.DTETags;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DTESchoolRegistry {
   private static final DeferredRegister<SchoolType> DTE_SCHOOLS = DeferredRegister.create(SchoolRegistry.SCHOOL_REGISTRY_KEY, "discerning_the_eldritch");
   public static final ResourceLocation RITUAL_RESOURCE = DiscerningTheEldritch.id("ritual");
   public static final Supplier<SchoolType> RITUAL = registerSchool(
      new SchoolType(
         RITUAL_RESOURCE,
         DTETags.RITUAL_FOCUS,
         Component.translatable("school.discerning_the_eldritch.ritual").withStyle(Style.EMPTY.withColor(8850226)),
         DTEAttributeRegistry.RITUAL_MAGIC_POWER,
         DTEAttributeRegistry.RITUAL_MAGIC_RESIST,
         SoundRegistry.EVOCATION_CAST,
         DTEDamageTypes.RITUAL_MAGIC
      )
   );

   public static void register(IEventBus eventBus) {
      DTE_SCHOOLS.register(eventBus);
   }

   private static Supplier<SchoolType> registerSchool(SchoolType type) {
      return DTE_SCHOOLS.register(type.getId().getPath(), () -> type);
   }
}
