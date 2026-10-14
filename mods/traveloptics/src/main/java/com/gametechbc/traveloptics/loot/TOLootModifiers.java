package com.gametechbc.traveloptics.loot;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public class TOLootModifiers {
   public static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, "traveloptics");
   public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS = DeferredRegister.create(
      Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, "traveloptics"
   );
   public static final Supplier<MapCodec<? extends IGlobalLootModifier>> KEY_LOOT_MODIFIER = LOOT_MODIFIER_SERIALIZERS.register(
      "key_loot", KeyLootModifier.CODEC
   );
   public static final Supplier<MapCodec<? extends IGlobalLootModifier>> UNIVERSAL_LOOT = LOOT_MODIFIER_SERIALIZERS.register(
      "universal_loot", KeyLootModifier.CODEC
   );

   public TOLootModifiers() {
      super();
   }

   public static void register(IEventBus eventBus) {
      LOOT_FUNCTIONS.register(eventBus);
      LOOT_MODIFIER_SERIALIZERS.register(eventBus);
   }
}
