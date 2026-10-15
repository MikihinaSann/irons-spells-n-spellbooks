package net.warphan.iss_magicfromtheeast.registries;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(
   modid = "iss_magicfromtheeast"
)
public class MFTECreativeTabRegistries {
   private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "iss_magicfromtheeast");
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MFTE_ITEM_TAB = TABS.register(
      "magicfromtheeast_items",
      () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
         .title(Component.translatable("itemGroup.iss_magicfromtheeast.item_tab"))
         .icon(() -> new ItemStack((ItemLike)MFTEItemRegistries.YIN_YANG_CORE.get()))
         .displayItems((enabledFeatures, entries) -> {
            entries.accept((ItemLike)MFTEItemRegistries.TAIJI_SWORD.get());
            entries.accept((ItemLike)MFTEItemRegistries.RITUAL_ORIHON.get());
            entries.accept((ItemLike)MFTEItemRegistries.JADE_GUANDAO.get());
            entries.accept((ItemLike)MFTEItemRegistries.SOUL_BREAKER.get());
            entries.accept((ItemLike)MFTEItemRegistries.SPIRIT_CRUSHER.get());
            entries.accept((ItemLike)MFTEItemRegistries.MURAMASA.get());
            entries.accept((ItemLike)MFTEItemRegistries.SOUL_KATANA.get());
            entries.accept((ItemLike)MFTEItemRegistries.SOULPIERCER.get());
            entries.accept((ItemLike)MFTEItemRegistries.REPEATING_CROSSBOW.get());
            entries.accept((ItemLike)MFTEItemRegistries.BAGUA_MIRROR.get());
            entries.accept((ItemLike)MFTEItemRegistries.COINS_SWORD.get());
            entries.accept((ItemLike)MFTEItemRegistries.RUSTED_COINS_SWORD.get());
            entries.accept((ItemLike)MFTEItemRegistries.SOULWARD_RING.get());
            entries.accept((ItemLike)MFTEItemRegistries.JADE_PENDANT.get());
            entries.accept((ItemLike)MFTEItemRegistries.JADE.get());
            entries.accept((ItemLike)MFTEItemRegistries.YIN_YANG_CORE.get());
            entries.accept((ItemLike)MFTEItemRegistries.BOTTLE_OF_SOULS.get());
            entries.accept((ItemLike)MFTEItemRegistries.ARCANE_RELICS.get());
            entries.accept((ItemLike)MFTEItemRegistries.RED_STRING.get());
            entries.accept((ItemLike)MFTEItemRegistries.COPPER_COINS.get());
            entries.accept((ItemLike)MFTEItemRegistries.RED_SHAFT.get());
            entries.accept((ItemLike)MFTEItemRegistries.REFINED_JADE_INGOT.get());
            entries.accept((ItemLike)MFTEItemRegistries.CRYSTALLIZED_SOUL.get());
            entries.accept((ItemLike)MFTEItemRegistries.TAOIST_HAT.get());
            entries.accept((ItemLike)MFTEItemRegistries.TAOIST_ROBES.get());
            entries.accept((ItemLike)MFTEItemRegistries.TAOIST_LEGGINGS.get());
            entries.accept((ItemLike)MFTEItemRegistries.TAOIST_BOOTS.get());
            entries.accept((ItemLike)MFTEItemRegistries.ONMYOJI_HAT.get());
            entries.accept((ItemLike)MFTEItemRegistries.ONMYOJI_ROBES.get());
            entries.accept((ItemLike)MFTEItemRegistries.ONMYOJI_LEGGINGS.get());
            entries.accept((ItemLike)MFTEItemRegistries.ONMYOJI_GETA.get());
            entries.accept((ItemLike)MFTEItemRegistries.JIANGSHI_HAT.get());
            entries.accept((ItemLike)MFTEItemRegistries.JADE_PAGODA_HELMET.get());
            entries.accept((ItemLike)MFTEItemRegistries.JADE_PAGODA_CHESTPLATE.get());
            entries.accept((ItemLike)MFTEItemRegistries.JADE_PAGODA_LEGGINGS.get());
            entries.accept((ItemLike)MFTEItemRegistries.JADE_PAGODA_BOOTS.get());
            entries.accept((ItemLike)MFTEItemRegistries.JADE_PAGODA_HORSE_ARMOR.get());
            entries.accept((ItemLike)MFTEItemRegistries.ELEMENTAL_COMMANDER_CHESTPLATE.get());
            entries.accept((ItemLike)MFTEItemRegistries.BOOTS_OF_MIST.get());
            entries.accept((ItemLike)MFTEItemRegistries.SYMMETRY_RUNE.get());
            entries.accept((ItemLike)MFTEItemRegistries.SPIRIT_RUNE.get());
            entries.accept((ItemLike)MFTEItemRegistries.DUNE_RUNE.get());
            entries.accept((ItemLike)MFTEItemRegistries.SYMMETRY_UPGRADE_ORB.get());
            entries.accept((ItemLike)MFTEItemRegistries.SPIRIT_UPGRADE_ORB.get());
            entries.accept((ItemLike)MFTEItemRegistries.RICE_WINE_BOTTLE.get());
            entries.accept((ItemLike)MFTEItemRegistries.TAOIST_SPAWN_EGG.get());
            entries.accept((ItemLike)MFTEItemRegistries.ONMYOJI_SPAWN_EGG.get());
            entries.accept((ItemLike)MFTEItemRegistries.JIANGSHI_SPAWN_EGG.get());
            entries.accept((ItemLike)MFTEItemRegistries.BALANCE_PATTERN.get());
         })
         .withTabsBefore(new ResourceKey[]{CreativeModeTabs.SPAWN_EGGS})
         .build()
   );
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MFTE_BLOCK_TAB = TABS.register(
      "magicfromtheeast_blocks",
      () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
         .title(Component.translatable("itemGroup.iss_magicfromtheeast.block_tab"))
         .icon(() -> new ItemStack((ItemLike)MFTEItemRegistries.JADE_BLOCK_ITEM.get()))
         .displayItems((enabledFeature, entries) -> {
            entries.accept((ItemLike)MFTEItemRegistries.JADE_ORE_ITEM.get());
            entries.accept((ItemLike)MFTEItemRegistries.JADE_ORE_DEEPSLATE_ITEM.get());
            entries.accept((ItemLike)MFTEItemRegistries.JADE_BLOCK_ITEM.get());
            entries.accept((ItemLike)MFTEItemRegistries.JADE_SLAB_ITEM.get());
            entries.accept((ItemLike)MFTEItemRegistries.JADE_STAIR_ITEM.get());
            entries.accept((ItemLike)MFTEItemRegistries.JADE_WALL_ITEM.get());
            entries.accept((ItemLike)MFTEItemRegistries.CHISELED_JADE.get());
            entries.accept((ItemLike)MFTEItemRegistries.JADE_LION_HEAD_BLOCK_ITEMS.get());
            entries.accept((ItemLike)MFTEItemRegistries.JADESTONE_BRICKS_ITEM.get());
            entries.accept((ItemLike)MFTEItemRegistries.JADESTONE_BRICKS_SLAB_ITEM.get());
            entries.accept((ItemLike)MFTEItemRegistries.JADESTONE_BRICKS_STAIR_ITEM.get());
            entries.accept((ItemLike)MFTEItemRegistries.JADESTONE_BRICKS_WALL_ITEM.get());
            entries.accept((ItemLike)MFTEItemRegistries.CHISELED_JADESTONE_BRICKS_ITEMS.get());
            entries.accept((ItemLike)MFTEItemRegistries.JADESTONE_BRICKS_PILLAR.get());
            entries.accept((ItemLike)MFTEItemRegistries.REFINED_JADE_BLOCK_ITEM.get());
            entries.accept((ItemLike)MFTEItemRegistries.VASE_OF_RICE_WINE.get());
         })
         .withTabsBefore(new ResourceKey[]{MFTE_ITEM_TAB.getKey()})
         .build()
   );

   public static void register(IEventBus eventBus) {
      TABS.register(eventBus);
   }

   @SubscribeEvent
   public static void fillCreativeTabs(BuildCreativeModeTabContentsEvent event) {
      if (event.getTab() == BuiltInRegistries.CREATIVE_MODE_TAB.get(CreativeModeTabs.NATURAL_BLOCKS)) {
         event.accept((ItemLike)MFTEItemRegistries.JADE_ORE_ITEM.get());
         event.accept((ItemLike)MFTEItemRegistries.JADE_ORE_DEEPSLATE_ITEM.get());
      }
   }
}
