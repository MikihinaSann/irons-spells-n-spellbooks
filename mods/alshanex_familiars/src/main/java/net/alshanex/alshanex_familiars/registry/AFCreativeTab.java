package net.alshanex.alshanex_familiars.registry;

import io.redspace.ironsspellbooks.registries.CreativeTabRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AFCreativeTab {
   private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "alshanex_familiars");
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = TABS.register(
      "familiars_main",
      () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
         .title(Component.translatable("itemGroup.alshanex_familiars.main_tab"))
         .icon(() -> new ItemStack(ItemRegistry.FAMILIAR_TOME))
         .displayItems((enabledFeatures, entries) -> {
            entries.accept((ItemLike)ItemRegistry.PET_BED.get());
            entries.accept((ItemLike)ItemRegistry.BROKEN_SHIELD.get());
            entries.accept((ItemLike)ItemRegistry.STRANGE_MUSHROOM.get());
            entries.accept((ItemLike)ItemRegistry.POISON_VIAL.get());
            entries.accept((ItemLike)ItemRegistry.SPIDER_FANG.get());
            entries.accept((ItemLike)ItemRegistry.MYSTERIOUS_ORB.get());
            entries.accept((ItemLike)ItemRegistry.FOREST_TRINKET.get());
            entries.accept((ItemLike)ItemRegistry.HUNTER_TRINKET.get());
            entries.accept((ItemLike)ItemRegistry.NECROMANCER_TRINKET.get());
            entries.accept((ItemLike)ItemRegistry.SUMMONER_TRINKET.get());
            entries.accept((ItemLike)ItemRegistry.MAGE_TRINKET.get());
            entries.accept((ItemLike)ItemRegistry.ARCHMAGE_TRINKET.get());
            entries.accept((ItemLike)ItemRegistry.ILLUSIONIST_TRINKET.get());
            entries.accept((ItemLike)ItemRegistry.SCORCHER_TRINKET.get());
            entries.accept((ItemLike)ItemRegistry.CLERIC_TRINKET.get());
            entries.accept((ItemLike)ItemRegistry.PLAGUE_TRINKET.get());
            entries.accept((ItemLike)ItemRegistry.BARD_TRINKET.get());
            entries.accept((ItemLike)ItemRegistry.FROSTLING_TRINKET.get());
            entries.accept((ItemLike)ItemRegistry.DRAGON_WARRIOR_TRINKET.get());
            entries.accept((ItemLike)ItemRegistry.HEALING_CURIO.get());
            entries.accept((ItemLike)ItemRegistry.EVASION_CURIO.get());
            entries.accept((ItemLike)ItemRegistry.INVISIBILITY_CURIO.get());
            entries.accept((ItemLike)ItemRegistry.STATUE_CURIO.get());
            entries.accept((ItemLike)ItemRegistry.PANDORA_BOX.get());
            entries.accept((ItemLike)ItemRegistry.BARD_HARP.get());
            entries.accept((ItemLike)ItemRegistry.FAMILIAR_SPELLBOOK.get());
            entries.accept((ItemLike)ItemRegistry.SOUND_SPELL_BOOK.get());
            entries.accept((ItemLike)ItemRegistry.BLUEBERRY.get());
            entries.accept((ItemLike)ItemRegistry.MEMORY_FRAGMENT.get());
            entries.accept((ItemLike)ItemRegistry.SUMMONER_RUNE.get());
            entries.accept((ItemLike)ItemRegistry.ARCHMAGE_RUNE.get());
            entries.accept((ItemLike)ItemRegistry.DRUID_RUNE.get());
            entries.accept((ItemLike)ItemRegistry.HUNTER_RUNE.get());
            entries.accept((ItemLike)ItemRegistry.MAGE_RUNE.get());
            entries.accept((ItemLike)ItemRegistry.ILLUSIONIST_RUNE.get());
            entries.accept((ItemLike)ItemRegistry.FROSTLING_RUNE.get());
            entries.accept((ItemLike)ItemRegistry.BARD_RUNE.get());
            entries.accept((ItemLike)ItemRegistry.CLERIC_RUNE.get());
            entries.accept((ItemLike)ItemRegistry.NECROMANCER_RUNE.get());
            entries.accept((ItemLike)ItemRegistry.PLAGUE_RUNE.get());
            entries.accept((ItemLike)ItemRegistry.SCORCHER_RUNE.get());
            entries.accept((ItemLike)ItemRegistry.DRAGON_WARRIOR_RUNE.get());
            entries.accept((ItemLike)ItemRegistry.FAMILIAR_SOUL.get());
            entries.accept((ItemLike)ItemRegistry.BARD_HELMET.get());
            entries.accept((ItemLike)ItemRegistry.BARD_CHESTPLATE.get());
            entries.accept((ItemLike)ItemRegistry.BARD_LEGGINGS.get());
            entries.accept((ItemLike)ItemRegistry.BARD_BOOTS.get());
            entries.accept((ItemLike)ItemRegistry.TRUTH_MIRROR.get());
            entries.accept((ItemLike)ItemRegistry.TINY_TOTEM.get());
            entries.accept((ItemLike)ItemRegistry.FAMILIAR_STORAGE.get());
            entries.accept((ItemLike)ItemRegistry.RITUAL_CORE.get());
            entries.accept((ItemLike)ItemRegistry.ARCHMAGE_STATUE.get());
            entries.accept((ItemLike)ItemRegistry.CLERIC_STATUE.get());
            entries.accept((ItemLike)ItemRegistry.DRUID_STATUE.get());
            entries.accept((ItemLike)ItemRegistry.HUNTER_STATUE.get());
            entries.accept((ItemLike)ItemRegistry.SOUND_RUNE.get());
            entries.accept((ItemLike)ItemRegistry.SOUND_UPGRADE_ORB.get());
            entries.accept((ItemLike)ItemRegistry.SHRINKING_STATION.get());
            entries.accept((ItemLike)ItemRegistry.MAGIC_POWER_TIER_1.get());
            entries.accept((ItemLike)ItemRegistry.MAGIC_POWER_TIER_2.get());
            entries.accept((ItemLike)ItemRegistry.MAGIC_POWER_TIER_3.get());
            entries.accept((ItemLike)ItemRegistry.MAGIC_RESIST_TIER_1.get());
            entries.accept((ItemLike)ItemRegistry.MAGIC_RESIST_TIER_2.get());
            entries.accept((ItemLike)ItemRegistry.MAGIC_RESIST_TIER_3.get());
            entries.accept((ItemLike)ItemRegistry.IRON_PLATE.get());
            entries.accept((ItemLike)ItemRegistry.DIAMOND_PLATE.get());
            entries.accept((ItemLike)ItemRegistry.NETHERITE_PLATE.get());
            entries.accept((ItemLike)ItemRegistry.LIFE_FRUIT.get());
            entries.accept((ItemLike)ItemRegistry.GOLDEN_LIFE_FRUIT.get());
            entries.accept((ItemLike)ItemRegistry.DIAMOND_LIFE_FRUIT.get());
            entries.accept((ItemLike)ItemRegistry.MONOCLE.get());
         })
         .withTabsBefore(new ResourceLocation[]{CreativeTabRegistry.EQUIPMENT_TAB.getId()})
         .build()
   );

   public static void register(IEventBus eventBus) {
      TABS.register(eventBus);
   }
}
