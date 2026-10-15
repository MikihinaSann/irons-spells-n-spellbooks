package com.somake.somakespells.registries;

import com.somake.somakespells.compat.BornInChaosCompat;
import com.somake.somakespells.compat.LegendaryMonstersCompat;
import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.config.Config;
import com.somake.somakespells.util.WeaponVisibilityHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeModeTabs {
   public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "somakespells");
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SOMAKE_SPELLS_TAB = CREATIVE_MODE_TABS.register(
      "somakespells_tab",
      () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
         .title(Component.translatable("itemGroup.somakespells"))
         .icon(() -> new ItemStack((ItemLike)ModItems.BURNING_MAGMA_SPELL_BOOK.get()))
         .displayItems((parameters, output) -> {
            output.accept((ItemLike)ModItems.TIDEBOUND_SPELL_BOOK.get());
            output.accept((ItemLike)ModItems.TIDEBOUND_2_SPELL_BOOK.get());
            output.accept((ItemLike)ModItems.TIDEBOUND_3_SPELL_BOOK.get());
            output.accept((ItemLike)ModItems.VOIDBOUND_CODEX_SPELL_BOOK.get());
            output.accept((ItemLike)ModItems.VOIDBOUND_CODEX_2_SPELL_BOOK.get());
            output.accept((ItemLike)ModItems.VOIDBOUND_CODEX_3_SPELL_BOOK.get());
            output.accept((ItemLike)ModItems.INVOKER_CANON_SPELL_BOOK.get());
            output.accept((ItemLike)ModItems.INVOKER_CANON_2_SPELL_BOOK.get());
            output.accept((ItemLike)ModItems.INVOKER_CANON_3_SPELL_BOOK.get());
            output.accept((ItemLike)ModItems.BURNING_MAGMA_SPELL_BOOK.get());
            output.accept((ItemLike)ModItems.BURNING_MAGMA_2_SPELL_BOOK.get());
            output.accept((ItemLike)ModItems.BURNING_MAGMA_3_SPELL_BOOK.get());
            output.accept((ItemLike)ModItems.DAWNLIT_GOSPEL_SPELL_BOOK.get());
            output.accept((ItemLike)ModItems.DAWNLIT_GOSPEL_2_SPELL_BOOK.get());
            output.accept((ItemLike)ModItems.DAWNLIT_GOSPEL_3_SPELL_BOOK.get());
            output.accept((ItemLike)ModItems.STORMCALLER_LEXICON_SPELL_BOOK.get());
            output.accept((ItemLike)ModItems.STORMCALLER_LEXICON_2_SPELL_BOOK.get());
            output.accept((ItemLike)ModItems.STORMCALLER_LEXICON_3_SPELL_BOOK.get());
            output.accept((ItemLike)ModItems.VERDANT_TESTAMENT_SPELL_BOOK.get());
            output.accept((ItemLike)ModItems.VERDANT_TESTAMENT_2_SPELL_BOOK.get());
            output.accept((ItemLike)ModItems.VERDANT_TESTAMENT_3_SPELL_BOOK.get());
            output.accept((ItemLike)ModItems.SANGUINE_TESTAMENT_SPELL_BOOK.get());
            output.accept((ItemLike)ModItems.SANGUINE_TESTAMENT_2_SPELL_BOOK.get());
            output.accept((ItemLike)ModItems.SANGUINE_TESTAMENT_3_SPELL_BOOK.get());
            if (ModList.get().isLoaded("tunes_n_tomes")) {
               output.accept((ItemLike)ModCompatItems.RESONANCE_CANTICLE_SPELL_BOOK.get());
               output.accept((ItemLike)ModCompatItems.RESONANCE_CANTICLE_2_SPELL_BOOK.get());
               output.accept((ItemLike)ModCompatItems.RESONANCE_CANTICLE_3_SPELL_BOOK.get());
            }

            output.accept((ItemLike)ModItems.UPGRADE_FORGE.get());
            output.accept((ItemLike)ModItems.PEDESTAL_OF_FIRE.get());
            output.accept((ItemLike)ModItems.AQUAMANCER_SPELL_BOOK.get());
            output.accept((ItemLike)ModItems.STORM_SCRIPTURES_SPELL_BOOK.get());
            output.accept((ItemLike)ModItems.SOUL_FIRE_NECKLACE.get());
            if (BornInChaosCompat.isLoaded()) {
               output.accept((ItemLike)ModCompatItems.INFERNAL_FIRE_NECKLACE.get());
            }

            output.accept((ItemLike)ModItems.AQUA_UPGRADE_ORB.get());
            output.accept((ItemLike)ModItems.SOUL_FIRE_UPGRADE_ORB.get());
            if (BornInChaosCompat.isLoaded()) {
               output.accept((ItemLike)ModCompatItems.INFERNAL_FIRE_UPGRADE_ORB.get());
            }

            if (BornInChaosCompat.isLoaded()) {
               output.accept((ItemLike)ModCompatItems.DARK_METAL_BATTLEMAGE_HELMET.get());
               output.accept((ItemLike)ModCompatItems.DARK_METAL_BATTLEMAGE_CHESTPLATE.get());
               output.accept((ItemLike)ModCompatItems.DARK_METAL_BATTLEMAGE_LEGGINGS.get());
               output.accept((ItemLike)ModCompatItems.DARK_METAL_BATTLEMAGE_BOOTS.get());
            }

            output.accept((ItemLike)ModItems.CERANIUM_HELMET.get());
            output.accept((ItemLike)ModItems.CERANIUM_CHESTPLATE.get());
            output.accept((ItemLike)ModItems.CERANIUM_LEGGINGS.get());
            output.accept((ItemLike)ModItems.CERANIUM_BOOTS.get());
            output.accept((ItemLike)ModItems.AQUAMANCER_HELMET.get());
            output.accept((ItemLike)ModItems.AQUAMANCER_CHESTPLATE.get());
            output.accept((ItemLike)ModItems.AQUAMANCER_LEGGINGS.get());
            output.accept((ItemLike)ModItems.AQUAMANCER_BOOTS.get());
            output.accept((ItemLike)ModItems.ABYSSIUM_HELMET.get());
            output.accept((ItemLike)ModItems.ABYSSIUM_CHESTPLATE.get());
            output.accept((ItemLike)ModItems.ABYSSIUM_LEGGINGS.get());
            output.accept((ItemLike)ModItems.ABYSSIUM_BOOTS.get());
            output.accept((ItemLike)ModItems.CERANIUM_INGOT.get());
            output.accept((ItemLike)ModItems.CERANIUM_UPGRADE_SMITHING_TEMPLATE.get());
            output.accept((ItemLike)ModItems.ABYSSIUM_INGOT.get());
            output.accept((ItemLike)ModItems.ABYSSIUM_UPGRADE_SMITHING_TEMPLATE.get());
            output.accept((ItemLike)ModItems.MAGMARIUM_INGOT.get());
            output.accept((ItemLike)ModItems.CERANIUM_PLATE.get());
            output.accept((ItemLike)ModItems.CURSIUM_PLATE.get());
            output.accept((ItemLike)ModItems.IGNITIUM_PLATE.get());
            output.accept((ItemLike)ModItems.WITHERITE_PLATE.get());
            output.accept((ItemLike)ModItems.ABYSSIUM_PLATE.get());
            output.accept((ItemLike)ModItems.ANCIENT_METAL_PLATE.get());
            output.accept((ItemLike)ModItems.MAGMARIUM_PLATE.get());
            output.accept((ItemLike)ModItems.CURSIUM_CORE.get());
            output.accept((ItemLike)ModItems.IGNITIUM_CORE.get());
            output.accept((ItemLike)ModItems.ABYSSIUM_CORE.get());
            output.accept((ItemLike)ModItems.WITHERITE_STAR.get());
            output.accept((ItemLike)ModItems.ELDER_GUARDIAN_EYE.get());
            output.accept((ItemLike)ModItems.ELDRITCH_RUNE.get());
            output.accept((ItemLike)ModItems.AQUA_RUNE.get());
            output.accept((ItemLike)ModItems.SOUL_FIRE_RUNE.get());
            if (BornInChaosCompat.isLoaded()) {
               output.accept((ItemLike)ModCompatItems.INFERNAL_FIRE_RUNE.get());
            }

            output.accept((ItemLike)ModItems.FIRE.get());
            output.accept((ItemLike)ModItems.SOUL_FIRE.get());
            output.accept((ItemLike)ModItems.TORMENTED_SPIRIT.get());
            if (BornInChaosCompat.isLoaded()) {
               output.accept((ItemLike)ModCompatItems.INFERNAL_FIRE.get());
            }

            output.accept((ItemLike)ModItems.SOUL_BLAZE_ROD.get());
            output.accept((ItemLike)ModItems.SOUL_BLAZE_POWDER.get());
            if (BornInChaosCompat.isLoaded()) {
               output.accept((ItemLike)ModCompatItems.INFERNAL_BLAZE_ROD.get());
               output.accept((ItemLike)ModCompatItems.INFERNAL_BLAZE_POWDER.get());
            }

            output.accept((ItemLike)ModItems.ELDRITCH_UPGRADE_ORB.get());
            output.accept((ItemLike)ModItems.AQUAMANCER_STAFF.get());
            if (LegendaryMonstersCompat.isLoaded()) {
               output.accept((ItemLike)ModItems.CORRUPTED_RED_SOUL.get());
               output.accept((ItemLike)ModItems.RED_SOUL_LANTERN.get());
               output.accept((ItemLike)ModItems.SPIRIT_STAFF.get());
            }

            if (MagicFromTheEastCompat.isLoaded()) {
               output.accept((ItemLike)ModItems.SYMMETRY_STAFF.get());
            }

            output.accept((ItemLike)ModItems.IGNITIUM_STAFF.get());
            output.accept((ItemLike)ModItems.SOUL_FIRE_IGNITIUM_STAFF.get());
            if (BornInChaosCompat.isLoaded()) {
               output.accept((ItemLike)ModCompatItems.INFERNAL_FIRE_IGNITIUM_STAFF.get());
            }

            output.accept((ItemLike)ModItems.ARCHANGELS_STAFF.get());
            output.accept((ItemLike)ModItems.WITHERED_ROSE_STAFF.get());
            output.accept((ItemLike)ModItems.DORMANT_GRIMOIRE.get());
            output.accept((ItemLike)ModItems.SANCTUM_GRIMOIRE.get());
            output.accept((ItemLike)ModItems.PROFANE_GRIMOIRE.get());
            WeaponVisibilityHelper.acceptIfVisible(output, (Item)ModItems.GLACIUM_GREATAXE.get());
            WeaponVisibilityHelper.acceptIfVisible(output, (Item)ModItems.CORE_SPLITTER.get());
            WeaponVisibilityHelper.acceptIfVisible(output, (Item)ModItems.COPPER_GLOVE.get());
            WeaponVisibilityHelper.acceptIfVisible(output, (Item)ModItems.HALLOW_SWORD.get());
            WeaponVisibilityHelper.acceptIfVisible(output, (Item)ModItems.FRAGMENTED_SWORD_1.get());
            WeaponVisibilityHelper.acceptIfVisible(output, (Item)ModItems.FRAGMENTED_SWORD_2.get());
            WeaponVisibilityHelper.acceptIfVisible(output, (Item)ModItems.FRAGMENTED_SWORD_3.get());
            if (!(Boolean)Config.HIDE_CENTAURI_FROM_CREATIVE_TAB.get()) {
               WeaponVisibilityHelper.acceptIfVisible(output, (Item)ModItems.CENTAURI.get());
            }

            WeaponVisibilityHelper.acceptIfVisible(output, (Item)ModItems.LAST_MOURNING.get());
            WeaponVisibilityHelper.acceptIfVisible(output, (Item)ModItems.MIRRORED_EDGE.get());
            WeaponVisibilityHelper.acceptIfVisible(output, (Item)ModItems.RADIANT_JUDGMENT.get());
            if (!(Boolean)Config.HIDE_SCORN_FROM_CREATIVE_TAB.get()) {
               WeaponVisibilityHelper.acceptIfVisible(output, (Item)ModItems.SCORN.get());
            }

            WeaponVisibilityHelper.acceptIfVisible(output, (Item)ModItems.SCULKBORNE_AXE.get());
            WeaponVisibilityHelper.acceptIfVisible(output, (Item)ModItems.TIDE_PIERCER.get());
            WeaponVisibilityHelper.acceptIfVisible(output, (Item)ModItems.TOTEM_DAGGER.get());
            WeaponVisibilityHelper.acceptIfVisible(output, (Item)ModItems.WITHERITE_GLAIVE.get());
            WeaponVisibilityHelper.acceptIfVisible(output, (Item)ModItems.RUINED_BLADE.get());
            WeaponVisibilityHelper.acceptIfVisible(output, (Item)ModItems.BOLTCUTTER_DAGGER.get());
            WeaponVisibilityHelper.acceptIfVisible(output, (Item)ModItems.FORBIDDEN_HAMMER.get());
            if (ModList.get().isLoaded("tunes_n_tomes")) {
               WeaponVisibilityHelper.acceptIfVisible(output, (Item)ModCompatItems.LULLABY_FLUTE.get());
               WeaponVisibilityHelper.acceptIfVisible(output, (Item)ModCompatItems.CLEF_SWORD.get());
               WeaponVisibilityHelper.acceptIfVisible(output, (Item)ModCompatItems.TRUMPET_AXE.get());
            }

            if (ModList.get().isLoaded("gtbcs_geomancy_plus")) {
               WeaponVisibilityHelper.acceptIfVisible(output, (Item)ModCompatItems.ROCK_SWORD.get());
               WeaponVisibilityHelper.acceptIfVisible(output, (Item)ModCompatItems.MONOLITH_SWORD.get());
            }
         })
         .build()
   );

   public static void register(IEventBus eventBus) {
      CREATIVE_MODE_TABS.register(eventBus);
   }
}
