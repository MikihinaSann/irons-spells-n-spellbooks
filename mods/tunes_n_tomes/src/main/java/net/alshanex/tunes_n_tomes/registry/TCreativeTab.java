package net.alshanex.tunes_n_tomes.registry;

import io.redspace.ironsspellbooks.registries.CreativeTabRegistry;
import net.alshanex.tunes_n_tomes.TunesNTomesConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TCreativeTab {
   private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "tunes_n_tomes");
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = TABS.register(
      "tunes_n_tomes_main",
      () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
         .title(Component.translatable("itemGroup.tunes_n_tomes.main_tab"))
         .icon(() -> new ItemStack(TItemRegistry.BARD_HARP))
         .displayItems((enabledFeatures, entries) -> {
            entries.accept((ItemLike)TItemRegistry.MELODY_RUNE.get());
            entries.accept((ItemLike)TItemRegistry.MELODY_UPGRADE_ORB.get());
            entries.accept((ItemLike)TItemRegistry.MELODY_SPELL_BOOK.get());
            entries.accept((ItemLike)TItemRegistry.BARD_HARP.get());
            entries.accept((ItemLike)TItemRegistry.BARD_HELMET.get());
            entries.accept((ItemLike)TItemRegistry.BARD_CHESTPLATE.get());
            entries.accept((ItemLike)TItemRegistry.BARD_LEGGINGS.get());
            entries.accept((ItemLike)TItemRegistry.BARD_BOOTS.get());
            if ((Boolean)TunesNTomesConfig.ENABLE_POSTGAME_ARMORSETS.get()) {
               entries.accept((ItemLike)TItemRegistry.MELODIC_MASTERY_HELMET.get());
               entries.accept((ItemLike)TItemRegistry.MELODIC_MASTERY_CHESTPLATE.get());
               entries.accept((ItemLike)TItemRegistry.MELODIC_MASTERY_LEGGINGS.get());
               entries.accept((ItemLike)TItemRegistry.MELODIC_MASTERY_BOOTS.get());
            }
         })
         .withTabsBefore(new ResourceLocation[]{CreativeTabRegistry.EQUIPMENT_TAB.getId()})
         .build()
   );

   public static void register(IEventBus eventBus) {
      TABS.register(eventBus);
   }
}
