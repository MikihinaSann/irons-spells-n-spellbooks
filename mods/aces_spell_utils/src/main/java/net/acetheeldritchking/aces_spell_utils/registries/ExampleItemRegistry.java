package net.acetheeldritchking.aces_spell_utils.registries;

import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.util.Collection;
import java.util.function.Supplier;
import net.acetheeldritchking.aces_spell_utils.items.example.ExampleAPMagicSwordItem;
import net.acetheeldritchking.aces_spell_utils.items.example.ExampleAPSwordItem;
import net.acetheeldritchking.aces_spell_utils.items.example.ExampleGunItem;
import net.acetheeldritchking.aces_spell_utils.items.example.ExampleImbueCurioItem;
import net.acetheeldritchking.aces_spell_utils.items.example.ExampleImbueStaffItem;
import net.acetheeldritchking.aces_spell_utils.items.example.ExampleLootBagItem;
import net.acetheeldritchking.aces_spell_utils.items.example.ExamplePassiveAbilitySpellbook;
import net.acetheeldritchking.aces_spell_utils.items.example.ExamplePresetCurioItem;
import net.acetheeldritchking.aces_spell_utils.items.example.ExamplePresetStaffItem;
import net.acetheeldritchking.aces_spell_utils.items.example.ExampleSheathCurioItem;
import net.acetheeldritchking.aces_spell_utils.items.example.ExampleWarlockArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorItem.Type;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

public class ExampleItemRegistry {
   public static final Items ITEMS = DeferredRegister.createItems("aces_spell_utils");
   public static final Supplier<CurioBaseItem> EXAMPLE_SHEATH = ITEMS.register("example_sheath", ExampleSheathCurioItem::new);
   public static final DeferredHolder<Item, Item> EXAMPLE_STAFF = ITEMS.register("example_staff", ExamplePresetStaffItem::new);
   public static final Supplier<CurioBaseItem> EXAMPLE_CURIO = ITEMS.register("example_curio", ExamplePresetCurioItem::new);
   public static final DeferredHolder<Item, Item> EXAMPLE_IMBUE_STAFF = ITEMS.register("example_imbue_staff", ExampleImbueStaffItem::new);
   public static final Supplier<CurioBaseItem> EXAMPLE_IMBUE_CURIO = ITEMS.register("example_imbue_curio", ExampleImbueCurioItem::new);
   public static final DeferredHolder<Item, Item> EXAMPLE_PASSIVE_ABILITY_SPELLBOOK = ITEMS.register(
      "example_passive_ability_spellbook", ExamplePassiveAbilitySpellbook::new
   );
   public static final DeferredHolder<Item, Item> EXAMPLE_GUN = ITEMS.register("example_gun", ExampleGunItem::new);
   public static final DeferredHolder<Item, Item> EXAMPLE_AP_SWORD = ITEMS.register("example_ap_sword", ExampleAPSwordItem::new);
   public static final DeferredHolder<Item, Item> EXAMPLE_AP_MAGIC_SWORD = ITEMS.register("example_ap_magic_sword", ExampleAPMagicSwordItem::new);
   public static final DeferredHolder<Item, Item> EXAMPLE_LOOT_BAG = ITEMS.register("example_loot_bag", ExampleLootBagItem::new);
   public static final DeferredHolder<Item, Item> EXAMPLE_ARMOR_HELMET = ITEMS.register(
      "example_armor_helmet",
      () -> new ExampleWarlockArmorItem(Type.HELMET, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.HELMET.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> EXAMPLE_ARMOR_CHESTPLATE = ITEMS.register(
      "example_armor_chestplate",
      () -> new ExampleWarlockArmorItem(Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.CHESTPLATE.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> EXAMPLE_ARMOR_LEGGINGS = ITEMS.register(
      "example_armor_leggings",
      () -> new ExampleWarlockArmorItem(Type.LEGGINGS, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.LEGGINGS.getDurability(40)))
   );
   public static final DeferredHolder<Item, Item> EXAMPLE_ARMOR_BOOTS = ITEMS.register(
      "example_armor_boots",
      () -> new ExampleWarlockArmorItem(Type.BOOTS, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.BOOTS.getDurability(40)))
   );

   public static Collection<DeferredHolder<Item, ? extends Item>> getASUItems() {
      return ITEMS.getEntries();
   }

   public static void register(IEventBus eventBus) {
      ITEMS.register(eventBus);
   }
}
