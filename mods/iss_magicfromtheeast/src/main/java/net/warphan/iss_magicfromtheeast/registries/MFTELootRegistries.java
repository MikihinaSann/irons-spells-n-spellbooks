package net.warphan.iss_magicfromtheeast.registries;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.warphan.iss_magicfromtheeast.loot.MFTEAppendLootModifier;

public class MFTELootRegistries {
   public static final DeferredRegister<LootItemFunctionType<?>> MFTE_LOOT_FUNCTION = DeferredRegister.create(
      Registries.LOOT_FUNCTION_TYPE, "iss_magicfromtheeast"
   );
   public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> MFTE_LOOT_MODIFIER_SERIALIZERS = DeferredRegister.create(
      NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, "iss_magicfromtheeast"
   );
   public static final Supplier<MapCodec<? extends IGlobalLootModifier>> MFTE_APPEND_LOOT_MODIFIER = MFTE_LOOT_MODIFIER_SERIALIZERS.register(
      "append_loot", MFTEAppendLootModifier.CODEC
   );

   public static void register(IEventBus eventBus) {
      MFTE_LOOT_FUNCTION.register(eventBus);
      MFTE_LOOT_MODIFIER_SERIALIZERS.register(eventBus);
   }
}
