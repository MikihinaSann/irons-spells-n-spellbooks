package net.acetheeldritchking.cataclysm_spellbooks.registries;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CSCreativeModeTabs {
   public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "cataclysm_spellbooks");
   public static final Supplier<CreativeModeTab> CS_ITEMS_TAB = CREATIVE_MODE_TAB.register(
      "cs_items_tab",
      () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
         .icon(() -> new ItemStack((ItemLike)ItemRegistries.ABYSSAL_RUNE.get()))
         .title(Component.translatable("creative_tab.cataclysm_spellbooks.items"))
         .displayItems((itemDisplayParameters, output) -> {
            output.accept((ItemLike)ItemRegistries.ABYSSAL_RUNE.get());
            output.accept((ItemLike)ItemRegistries.ABYSSAL_UPGRADE_ORB.get());
            output.accept((ItemLike)ItemRegistries.TECHNOMANCY_UPGRADE_ORB.get());
            output.accept((ItemLike)ItemRegistries.LEVIATHANS_BLESSING.get());
            output.accept((ItemLike)ItemRegistries.ABYSS_SPELL_BOOK.get());
            output.accept((ItemLike)ItemRegistries.IGNIS_SPELL_BOOK.get());
            output.accept((ItemLike)ItemRegistries.DESERT_SPELL_BOOK.get());
            output.accept((ItemLike)ItemRegistries.CODEX_OF_MALICE.get());
            output.accept((ItemLike)ItemRegistries.BLOOM_STONE_STAFF.get());
            output.accept((ItemLike)ItemRegistries.CORAL_STAFF.get());
            output.accept((ItemLike)ItemRegistries.FAKE_WUDJETS_STAFF.get());
            output.accept((ItemLike)ItemRegistries.VOID_STAFF.get());
            output.accept((ItemLike)ItemRegistries.SPIRIT_SUNDERER_STAFF.get());
            output.accept((ItemLike)ItemRegistries.SOUL_BRAZIER_STAFF.get());
            output.accept((ItemLike)ItemRegistries.MONSTROUS_FLAMBERGE.get());
            output.accept((ItemLike)ItemRegistries.HELLFIRE_FORGE.get());
            output.accept((ItemLike)ItemRegistries.IGNITIUM_WIZARD_HELMET.get());
            output.accept((ItemLike)ItemRegistries.IGNITIUM_WIZARD_CHESTPLATE.get());
            output.accept((ItemLike)ItemRegistries.IGNITIUM_WIZARD_CHESTPLATE_ELYTRA.get());
            output.accept((ItemLike)ItemRegistries.IGNITIUM_WIZARD_LEGGINGS.get());
            output.accept((ItemLike)ItemRegistries.IGNITIUM_WIZARD_BOOTS.get());
            output.accept((ItemLike)ItemRegistries.ABYSSAL_WARLOCK_HELMET.get());
            output.accept((ItemLike)ItemRegistries.ABYSSAL_WARLOCK_MASK.get());
            output.accept((ItemLike)ItemRegistries.ABYSSAL_WARLOCK_CHESTPLATE.get());
            output.accept((ItemLike)ItemRegistries.ABYSSAL_WARLOCK_LEGGINGS.get());
            output.accept((ItemLike)ItemRegistries.ABYSSAL_WARLOCK_BOOTS.get());
            output.accept((ItemLike)ItemRegistries.CURSIUM_MAGE_HELMET.get());
            output.accept((ItemLike)ItemRegistries.CURSIUM_MAGE_CHESTPLATE.get());
            output.accept((ItemLike)ItemRegistries.CURSIUM_MAGE_CHESTPLATE_ELYTRA.get());
            output.accept((ItemLike)ItemRegistries.CURSIUM_MAGE_LEGGINGS.get());
            output.accept((ItemLike)ItemRegistries.CURSIUM_MAGE_BOOTS.get());
            output.accept((ItemLike)ItemRegistries.BLOOM_STONE_HAT.get());
            output.accept((ItemLike)ItemRegistries.BLOOM_STONE_CHESTPLATE.get());
            output.accept((ItemLike)ItemRegistries.BLOOM_STONE_SKIRT.get());
            output.accept((ItemLike)ItemRegistries.BLOOM_STONE_GREAVES.get());
            output.accept((ItemLike)ItemRegistries.PHARAOH_MAGE_HELMET.get());
            output.accept((ItemLike)ItemRegistries.PHARAOH_MAGE_CHESTPLATE.get());
            output.accept((ItemLike)ItemRegistries.PHARAOH_MAGE_LEGGINGS.get());
            output.accept((ItemLike)ItemRegistries.PHARAOH_MAGE_BOOTS.get());
            output.accept((ItemLike)ItemRegistries.MONSTROUS_WIZARD_HAT.get());
            output.accept((ItemLike)ItemRegistries.ENGINEER_MAGE_HOOD.get());
            output.accept((ItemLike)ItemRegistries.ENGINEER_MAGE_SUIT.get());
            output.accept((ItemLike)ItemRegistries.ENGINEER_MAGE_LEGGINGS.get());
            output.accept((ItemLike)ItemRegistries.ENGINEER_MAGE_BOOTS.get());
            output.accept((ItemLike)ItemRegistries.EXCELSIUS_SPEED_HELMET.get());
            output.accept((ItemLike)ItemRegistries.EXCELSIUS_SPEED_CHESTPLATE.get());
            output.accept((ItemLike)ItemRegistries.EXCELSIUS_POWER_HELMET.get());
            output.accept((ItemLike)ItemRegistries.EXCELSIUS_POWER_CHESTPLATE.get());
            output.accept((ItemLike)ItemRegistries.EXCELSIUS_RESIST_HELMET.get());
            output.accept((ItemLike)ItemRegistries.EXCELSIUS_RESIST_CHESTPLATE.get());
            output.accept((ItemLike)ItemRegistries.EXCELSIUS_WARLOCK_LEGGINGS.get());
            output.accept((ItemLike)ItemRegistries.EXCELSIUS_WARLOCK_BOOTS.get());
         })
         .build()
   );

   public static void register(IEventBus eventBus) {
      CREATIVE_MODE_TAB.register(eventBus);
   }
}
