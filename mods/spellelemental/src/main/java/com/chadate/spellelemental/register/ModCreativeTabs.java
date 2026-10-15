package com.chadate.spellelemental.register;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
   public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "spellelemental");
   public static final Supplier<CreativeModeTab> SPELL_ELEMENTAL_TAB = CREATIVE_MODE_TABS.register(
      "spell_elemental",
      () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
         .title(Component.translatable("itemGroup.spellelemental"))
         .icon(() -> new ItemStack((ItemLike)ModItems.STELLAR_FRAGMENT.get()))
         .displayItems((parameters, output) -> {
            output.accept((ItemLike)ModItems.SWORD_STAND.get());
            output.accept((ItemLike)ModItems.STELLAR_FRAGMENT_ORE.get());
            output.accept((ItemLike)ModItems.DEEPSLATE_STELLAR_FRAGMENT_ORE.get());
            output.accept((ItemLike)ModItems.STELLAR_FRAGMENT.get());
            output.accept((ItemLike)ModItems.FIRE_SWORD_OIL.get());
            output.accept((ItemLike)ModItems.ICE_SWORD_OIL.get());
            output.accept((ItemLike)ModItems.LIGHTNING_SWORD_OIL.get());
            output.accept((ItemLike)ModItems.BLOOD_SWORD_OIL.get());
            output.accept((ItemLike)ModItems.NATURE_SWORD_OIL.get());
         })
         .build()
   );

   public static void register(IEventBus eventBus) {
      CREATIVE_MODE_TABS.register(eventBus);
   }
}
