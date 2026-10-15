package net.acetheeldritchking.discerning_the_eldritch.registries;

import io.redspace.ironsspellbooks.item.FurledMapItem;
import java.util.function.Supplier;
import net.acetheeldritchking.discerning_the_eldritch.DiscerningTheEldritch;
import net.acetheeldritchking.discerning_the_eldritch.compat.CompatManager;
import net.acetheeldritchking.discerning_the_eldritch.compat.pastel.PastelCompatItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DTECreativeModeTabs {
   public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "discerning_the_eldritch");
   public static final Supplier<CreativeModeTab> DTE_ITEMS_TAB = CREATIVE_MODE_TAB.register(
      "dte_items_tab",
      () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
         .icon(() -> new ItemStack((ItemLike)ItemRegistries.CORRUPTED_CLOTH.get()))
         .title(Component.translatable("creative_tab.discerning_the_eldritch.items"))
         .displayItems(
            (itemDisplayParameters, output) -> {
               output.accept((ItemLike)ItemRegistries.CORRUPTED_CLOTH.get());
               output.accept((ItemLike)ItemRegistries.ELDRITCH_RUNE.get());
               output.accept((ItemLike)ItemRegistries.RITUAL_RUNE.get());
               output.accept((ItemLike)ItemRegistries.ELDRITCH_UPGRADE_ORB.get());
               output.accept((ItemLike)ItemRegistries.RITUAL_UPGRADE_ORB.get());
               output.accept((ItemLike)ItemRegistries.ELDRITCH_UPGRADE_TEMPLATE.get());
               output.accept((ItemLike)ItemRegistries.ELDRITCH_SOUL_SHARD.get());
               output.accept((ItemLike)ItemRegistries.SHARD_OF_MALICE.get());
               output.accept((ItemLike)ItemRegistries.BOTTLE_OF_LIQUID_MALICE.get());
               output.accept((ItemLike)ItemRegistries.FORBIDDEN_SPELL_IMPROVEMENT.get());
               output.accept((ItemLike)ItemRegistries.SOUL_EMBER.get());
               output.accept(
                  FurledMapItem.of(
                     DiscerningTheEldritch.id("cultist_base"),
                     ResourceKey.create(Registries.DIMENSION, ResourceLocation.withDefaultNamespace("overworld")),
                     Component.translatable("item.discerning_the_eldritch.cultist_base_map"),
                     true
                  )
               );
               output.accept((ItemLike)ItemRegistries.ECHO_VIBRATION_RING.get());
               output.accept((ItemLike)ItemRegistries.AMULET_OF_SCULK_TREASURE.get());
               output.accept((ItemLike)ItemRegistries.CASTERS_MANTLE.get());
               output.accept((ItemLike)ItemRegistries.IRONBOUND_FEATHER.get());
               output.accept((ItemLike)ItemRegistries.PYRIUM_SHEATH.get());
               output.accept((ItemLike)ItemRegistries.FROSTBOURNE_SHEATH.get());
               output.accept((ItemLike)ItemRegistries.RAZOR_SHEATH.get());
               output.accept((ItemLike)ItemRegistries.RIFT_RIPPER_EMBLEM.get());
               output.accept((ItemLike)ItemRegistries.APOTHIC_KEY.get());
               output.accept((ItemLike)ItemRegistries.ASCENDED_KEY.get());
               output.accept((ItemLike)ItemRegistries.RITUAL_KEY.get());
               output.accept((ItemLike)ItemRegistries.FADING_SCULK_LANTERN.get());
               output.accept((ItemLike)ItemRegistries.BLACK_BOOK_SPELLBOOK.get());
               output.accept((ItemLike)ItemRegistries.THE_APOCRYPHA_SPELLBOOK.get());
               output.accept((ItemLike)ItemRegistries.TEMPESTUOUS_TOME.get());
               output.accept((ItemLike)ItemRegistries.DIARY_OF_DECAY.get());
               output.accept((ItemLike)ItemRegistries.DEEP_GREATSWORD.get());
               output.accept((ItemLike)ItemRegistries.GOD_SPEAR.get());
               output.accept((ItemLike)ItemRegistries.FORSAKEN_FLAMBERGE.get());
               output.accept((ItemLike)ItemRegistries.ICE_SPEAR.get());
               output.accept((ItemLike)ItemRegistries.SNOWGRAVE.get());
               output.accept((ItemLike)ItemRegistries.SOUL_FIRE_SCYTHE.get());
               output.accept((ItemLike)ItemRegistries.RITUAL_DAGGER.get());
               output.accept((ItemLike)ItemRegistries.FORLORN_RAPIER.get());
               output.accept((ItemLike)ItemRegistries.BROKEN_LEGENDS_BLADE.get());
               output.accept((ItemLike)ItemRegistries.CATACLYSM.get());
               output.accept((ItemLike)ItemRegistries.DEVOURER.get());
               output.accept((ItemLike)ItemRegistries.MOURNING_STAR.get());
               output.accept((ItemLike)ItemRegistries.CATACLYSM_AWAKENED.get());
               output.accept((ItemLike)ItemRegistries.DEVOURER_AWAKENED.get());
               output.accept((ItemLike)ItemRegistries.MOURNING_STAR_AWAKENED.get());
               output.accept((ItemLike)ItemRegistries.YMIR.get());
               output.accept((ItemLike)ItemRegistries.STAFF_OF_VEHEMENCE.get());
               output.accept((ItemLike)ItemRegistries.STAFF_OF_ASCENSION.get());
               output.accept((ItemLike)ItemRegistries.BROKEN_LEGENDS_STAFF.get());
               output.accept((ItemLike)ItemRegistries.ELDRITCH_WARLOCK_HOOD.get());
               output.accept((ItemLike)ItemRegistries.ELDRITCH_WARLOCK_MASK.get());
               output.accept((ItemLike)ItemRegistries.ELDRITCH_WARLOCK_HELMET.get());
               output.accept((ItemLike)ItemRegistries.ELDRITCH_WARLOCK_ROBES.get());
               output.accept((ItemLike)ItemRegistries.ELDRITCH_WARLOCK_LEGGINGS.get());
               output.accept((ItemLike)ItemRegistries.ELDRITCH_WARLOCK_GREAVES.get());
               output.accept((ItemLike)ItemRegistries.CRIMSON_STAG_ANTLERS.get());
               output.accept((ItemLike)ItemRegistries.CRIMSON_STAG_ROBES.get());
               output.accept((ItemLike)ItemRegistries.CRIMSON_STAG_LEGGINGS.get());
               output.accept((ItemLike)ItemRegistries.CRIMSON_STAG_BOOTS.get());
               output.accept((ItemLike)ItemRegistries.ACOLYTE_SPAWN_EGG.get());
               output.accept((ItemLike)ItemRegistries.SUMMONER_SPAWN_EGG.get());
               output.accept((ItemLike)ItemRegistries.CRUSADER_SPAWN_EGG.get());
               output.accept((ItemLike)ItemRegistries.ASCENDED_CULTIST_SPAWN_EGG.get());
               output.accept((ItemLike)ItemRegistries.BLOOD_CULTIST_CAPTAIN_SPAWN_EGG.get());
               output.accept((ItemLike)ItemRegistries.BLOOD_CULTIST_MAGE_SPAWN_EGG.get());
               output.accept((ItemLike)ItemRegistries.BLOOD_CULTIST_WITCH_SPAWN_EGG.get());
               output.accept((ItemLike)ItemRegistries.BLOOD_MATRIARCH_SPAWN_EGG.get());
               output.accept((ItemLike)ItemRegistries.ELECTROMANCER_SPAWN_EGG.get());
               if (CompatManager.isPastelLoaded()) {
                  output.accept((ItemLike)PastelCompatItems.DREAM_REAVER_YMIR.get());
                  output.accept((ItemLike)PastelCompatItems.BUCKET_OF_MALICE.get());
               }
            }
         )
         .build()
   );

   public static void register(IEventBus eventBus) {
      CREATIVE_MODE_TAB.register(eventBus);
   }
}
