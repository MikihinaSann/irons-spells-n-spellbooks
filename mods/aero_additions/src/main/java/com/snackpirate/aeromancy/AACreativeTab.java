package com.snackpirate.aeromancy;

import com.snackpirate.aeromancy.item.AAItems;
import com.snackpirate.aeromancy.spells.AASpells;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.registries.CreativeTabRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(
   modid = "aero_additions"
)
public class AACreativeTab {
   private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "aero_additions");
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = TABS.register(
      "aeromancy_main",
      () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
         .title(Component.translatable("itemGroup.aero_additions.main_tab"))
         .icon(() -> new ItemStack(AAItems.UPDRAFT_TOME))
         .displayItems((enabledFeatures, entries) -> {
            entries.accept((ItemLike)AAItems.WIND_RUNE.get());
            entries.accept((ItemLike)AAItems.WIND_UPGRADE_ORB.get());
            entries.accept((ItemLike)AAItems.UPDRAFT_TOME.get());
            entries.accept((ItemLike)AAItems.AIR_STAFF.get());
            entries.accept((ItemLike)AAItems.WIND_SWORD.get());
            entries.accept((ItemLike)AAItems.WINDMAKER_HEADPIECE.get());
            entries.accept((ItemLike)AAItems.WINDMAKER_ROBES.get());
            entries.accept((ItemLike)AAItems.WINDMAKER_SKIRT.get());
            entries.accept((ItemLike)AAItems.WINDMAKER_BOOTS.get());
         })
         .withTabsBefore(new ResourceLocation[]{CreativeTabRegistry.EQUIPMENT_TAB.getId()})
         .build()
   );

   public static void register(IEventBus eventBus) {
      TABS.register(eventBus);
   }

   @SubscribeEvent
   public static void fillCreativeTabs(BuildCreativeModeTabContentsEvent event) {
      if (event.getTab() == MAIN_TAB.get()) {
         SpellRegistry.getEnabledSpells()
            .stream()
            .filter(spellType -> spellType != SpellRegistry.none() && spellType.getSchoolType().equals(AASpells.Schools.WIND.get()))
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
