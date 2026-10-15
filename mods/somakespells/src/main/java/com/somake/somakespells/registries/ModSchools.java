package com.somake.somakespells.registries;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSchools {
   public static final DeferredRegister<SchoolType> SCHOOLS = DeferredRegister.create(SchoolRegistry.SCHOOL_REGISTRY_KEY, "somakespells");
   public static final DeferredHolder<SchoolType, SchoolType> AQUA = SCHOOLS.register(
      "aqua",
      () -> new SchoolType(
         ResourceLocation.fromNamespaceAndPath("somakespells", "aqua"),
         TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("somakespells", "school_focus/aqua")),
         Component.translatable("school.somakespells.aqua").withStyle(ChatFormatting.AQUA),
         ModAttributes.AQUA_SPELL_POWER,
         ModAttributes.AQUA_MAGIC_RESIST,
         BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EVOKER_CAST_SPELL),
         ModDamageTypes.AQUA_MAGIC
      )
   );

   public static void register(IEventBus eventBus) {
      SCHOOLS.register(eventBus);
   }
}
