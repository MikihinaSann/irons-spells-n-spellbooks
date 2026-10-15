package com.somake.somakespells.registries;

import com.mojang.serialization.MapCodec;
import com.somake.somakespells.loot.EntityItemLootModifier;
import com.somake.somakespells.loot.SpellScrollLootModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModLootModifiers {
   private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(
      NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, "somakespells"
   );
   public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<EntityItemLootModifier>> ENTITY_ITEM = LOOT_MODIFIERS.register(
      "entity_item", () -> EntityItemLootModifier.CODEC
   );
   public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<SpellScrollLootModifier>> SPELL_SCROLL = LOOT_MODIFIERS.register(
      "spell_scroll", () -> SpellScrollLootModifier.CODEC
   );

   public static void register(IEventBus eventBus) {
      LOOT_MODIFIERS.register(eventBus);
   }
}
