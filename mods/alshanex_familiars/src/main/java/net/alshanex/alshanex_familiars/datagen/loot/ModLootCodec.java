package net.alshanex.alshanex_familiars.datagen.loot;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public class ModLootCodec {
   public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER_SERIALIZERS = DeferredRegister.create(
      Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, "alshanex_familiars"
   );
   public static final Supplier<MapCodec<ModLootModifier>> MOD_LOOT_MODIFIER = GLOBAL_LOOT_MODIFIER_SERIALIZERS.register(
      "mod_loot_modifier", () -> ModLootModifier.CODEC
   );
   public static final Supplier<MapCodec<AddItemsModifier>> ADD_ITEMS_MODIFIER = GLOBAL_LOOT_MODIFIER_SERIALIZERS.register(
      "add_items_modifier", () -> AddItemsModifier.CODEC
   );
   public static final Supplier<MapCodec<ConditionalAddModifier>> CONDITIONAL_ADD_MODIFIER = GLOBAL_LOOT_MODIFIER_SERIALIZERS.register(
      "conditional_add_modifier", () -> ConditionalAddModifier.CODEC
   );

   public static void register(IEventBus eventBus) {
      GLOBAL_LOOT_MODIFIER_SERIALIZERS.register(eventBus);
   }
}
