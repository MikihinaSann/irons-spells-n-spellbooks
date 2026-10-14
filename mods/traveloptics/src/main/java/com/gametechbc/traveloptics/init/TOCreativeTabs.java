package com.gametechbc.traveloptics.init;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(
   modid = "traveloptics",
   bus = Bus.MOD
)
public class TOCreativeTabs {
   private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "traveloptics");
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_CREATIVE_TAB = TABS.register(
      "traveloptics_items",
      () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
         .title(Component.translatable("item_group.traveloptics.traveloptics"))
         .icon(() -> new ItemStack((ItemLike)TOItems.ARCHIVE_OF_ABYSSAL_SECRETS.get()))
         .displayItems((enabledFeatures, tabData) -> {
            tabData.accept((ItemLike)TOItems.ABYSSAL_SPELLWEAVE_INGOT.get());
            tabData.accept((ItemLike)TOItems.PYRO_SPELLWEAVE_INGOT.get());
            tabData.accept((ItemLike)TOItems.VERDANT_SPELLWEAVE_INGOT.get());
            tabData.accept((ItemLike)TOItems.VOID_SPELLWEAVE_INGOT.get());
            tabData.accept((ItemLike)TOItems.ABYSSAL_TENTACLE.get());
            tabData.accept((ItemLike)TOItems.ECHO_WINGLET.get());
            tabData.accept((ItemLike)TOItems.FLAME_TEMPERED_HANDGUARD.get());
            tabData.accept((ItemLike)TOItems.LAST_GLOW.get());
            tabData.accept((ItemLike)TOItems.DESERT_JEWEL_FRAGMENT.get());
            tabData.accept((ItemLike)TOItems.KYREXI_CLAWS.get());
            tabData.accept((ItemLike)TOItems.ARCHIVE_OF_ABYSSAL_SECRETS.get());
            tabData.accept((ItemLike)TOItems.THE_ACCUSED_CODEX.get());
            tabData.accept((ItemLike)TOItems.CHRONICLES_OF_THE_FIRELORD.get());
            tabData.accept((ItemLike)TOItems.SHELLBOUND.get());
            tabData.accept((ItemLike)TOItems.CURSED_WRAITHBLADE.get());
            tabData.accept((ItemLike)TOItems.MECHANIZED_WRAITHBLADE.get());
            tabData.accept((ItemLike)TOItems.INFERNAL_DEVASTATOR.get());
            tabData.accept((ItemLike)TOItems.AETHERIAL_DESPAIR_RING.get());
            tabData.accept((ItemLike)TOItems.FIRESTORM_RING.get());
            tabData.accept((ItemLike)TOItems.AZURE_IGNITION_BRACELET.get());
            tabData.accept((ItemLike)TOItems.NIGHTSTALKERS_BAND.get());
            tabData.accept((ItemLike)TOItems.ENERGY_UNBOUND_NECKLACE.get());
            tabData.accept((ItemLike)TOItems.SIGIL_OF_THE_SPIDER_SORCERER.get());
            tabData.accept((ItemLike)TOItems.AMULET_OF_SPECTRAL_SHIFT.get());
         })
         .build()
   );
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TRAVELOPTICS_SCROLLS = TABS.register(
      "traveloptics_scrolls",
      () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
         .title(Component.translatable("item_group.traveloptics.traveloptics_scrolls"))
         .icon(() -> new ItemStack((ItemLike)TOItems.AQUA_SCROLL_DUMMY.get()))
         .withTabsAfter(new ResourceKey[]{MAIN_CREATIVE_TAB.getKey()})
         .build()
   );

   public TOCreativeTabs() {
      super();
   }

   public static void register(IEventBus eventBus) {
      TABS.register(eventBus);
   }

   @SubscribeEvent
   public static void fillCreativeTabs(BuildCreativeModeTabContentsEvent event) {
      if (event.getTab() == TRAVELOPTICS_SCROLLS.get()) {
         SpellRegistry.getEnabledSpells()
            .stream()
            .filter(spellType -> spellType != SpellRegistry.none())
            .filter(spell -> spell.getSpellResource().getNamespace().equals("traveloptics"))
            .forEach(spell -> {
               for (int i = spell.getMinLevel(); i <= spell.getMaxLevel(); i++) {
                  ItemStack itemstack = new ItemStack((ItemLike)ItemRegistry.SCROLL.get());
                  ISpellContainer spellList = ISpellContainer.createScrollContainer(spell, i, itemstack);
                  event.accept(itemstack);
               }
            });
      }
   }
}
