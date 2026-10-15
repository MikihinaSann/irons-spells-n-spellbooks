package net.acetheeldritchking.aces_spell_utils.registries;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ASCreativeModeTabs {
   public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "aces_spell_utils");
   public static final Supplier<CreativeModeTab> ASU_ITEMS_TAB = CREATIVE_MODE_TAB.register(
      "asu_item_tabs",
      () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
         .icon(() -> new ItemStack((ItemLike)ExampleItemRegistry.EXAMPLE_SHEATH.get()))
         .title(Component.translatable("creative_tab.aces_spell_utils.items"))
         .displayItems((itemDisplayParameters, output) -> {
            output.accept((ItemLike)ExampleItemRegistry.EXAMPLE_SHEATH.get());
            output.accept((ItemLike)ExampleItemRegistry.EXAMPLE_STAFF.get());
            output.accept((ItemLike)ExampleItemRegistry.EXAMPLE_IMBUE_STAFF.get());
            output.accept((ItemLike)ExampleItemRegistry.EXAMPLE_CURIO.get());
            output.accept((ItemLike)ExampleItemRegistry.EXAMPLE_IMBUE_CURIO.get());
            output.accept((ItemLike)ExampleItemRegistry.EXAMPLE_PASSIVE_ABILITY_SPELLBOOK.get());
            output.accept((ItemLike)ExampleItemRegistry.EXAMPLE_GUN.get());
            output.accept((ItemLike)ExampleItemRegistry.EXAMPLE_AP_SWORD.get());
            output.accept((ItemLike)ExampleItemRegistry.EXAMPLE_AP_MAGIC_SWORD.get());
            output.accept((ItemLike)ExampleItemRegistry.EXAMPLE_LOOT_BAG.get());
            output.accept((ItemLike)ExampleItemRegistry.EXAMPLE_ARMOR_HELMET.get());
            output.accept((ItemLike)ExampleItemRegistry.EXAMPLE_ARMOR_CHESTPLATE.get());
            output.accept((ItemLike)ExampleItemRegistry.EXAMPLE_ARMOR_LEGGINGS.get());
            output.accept((ItemLike)ExampleItemRegistry.EXAMPLE_ARMOR_BOOTS.get());
         })
         .build()
   );

   public static void register(IEventBus eventBus) {
      CREATIVE_MODE_TAB.register(eventBus);
   }
}
