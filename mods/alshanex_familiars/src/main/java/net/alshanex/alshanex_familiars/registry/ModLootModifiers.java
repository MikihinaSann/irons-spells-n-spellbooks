package net.alshanex.alshanex_familiars.registry;

import com.mojang.serialization.MapCodec;
import net.alshanex.alshanex_familiars.datagen.loot.AddItemsModifier;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public class ModLootModifiers {
   public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(
      Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, "alshanex_familiars"
   );
   public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<AddItemsModifier>> ADD_ITEM = LOOT_MODIFIERS.register(
      "add_items", () -> AddItemsModifier.CODEC
   );
}
