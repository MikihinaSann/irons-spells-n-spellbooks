package com.somake.somakespells.registries;

import com.somake.somakespells.compat.BornInChaosCompat;
import com.somake.somakespells.item.ClefSwordItem;
import com.somake.somakespells.item.InfernalFireIgnitiumStaffItem;
import com.somake.somakespells.item.InfernalFireNecklace;
import com.somake.somakespells.item.LullabyFluteItem;
import com.somake.somakespells.item.MonolithSwordItem;
import com.somake.somakespells.item.RockSwordItem;
import com.somake.somakespells.item.TrumpetAxeItem;
import com.somake.somakespells.item.armor.DarkMetalBattlemageArmorItem;
import com.somake.somakespells.item.armor.ModArmorMaterials;
import com.somake.somakespells.item.spellbooks.SonicBeats2SpellBook;
import com.somake.somakespells.item.spellbooks.SonicBeats3SpellBook;
import com.somake.somakespells.item.spellbooks.SonicBeatsSpellBook;
import com.somake.somakespells.item.spellbooks.StoneheartCodex2SpellBook;
import com.somake.somakespells.item.spellbooks.StoneheartCodex3SpellBook;
import com.somake.somakespells.item.spellbooks.StoneheartCodexSpellBook;
import io.redspace.ironsspellbooks.item.UpgradeOrbItem;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCompatItems {
   public static final DeferredRegister<Item> GEO_ITEMS = DeferredRegister.create(Registries.ITEM, "somakespells");
   public static final DeferredRegister<Item> SOUND_ITEMS = DeferredRegister.create(Registries.ITEM, "somakespells");
   public static final DeferredRegister<Item> BORN_IN_CHAOS_ITEMS = DeferredRegister.create(Registries.ITEM, "somakespells");
   public static final DeferredHolder<Item, StoneheartCodexSpellBook> STONEHEART_CODEX_SPELL_BOOK = GEO_ITEMS.register(
      "stoneheart_codex_spell_book", StoneheartCodexSpellBook::new
   );
   public static final DeferredHolder<Item, StoneheartCodex2SpellBook> STONEHEART_CODEX_2_SPELL_BOOK = GEO_ITEMS.register(
      "stoneheart_codex_2_spell_book", StoneheartCodex2SpellBook::new
   );
   public static final DeferredHolder<Item, StoneheartCodex3SpellBook> STONEHEART_CODEX_3_SPELL_BOOK = GEO_ITEMS.register(
      "stoneheart_codex_3_spell_book", StoneheartCodex3SpellBook::new
   );
   public static final DeferredHolder<Item, Item> GEO_UPGRADE_ORB_SMALL = GEO_ITEMS.register(
      "geo_upgrade_orb_small",
      () -> new UpgradeOrbItem(new Properties().rarity(Rarity.COMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, ModUpgradeOrbTypes.GEO_SPELL_POWER_SMALL))
   );
   public static final DeferredHolder<Item, Item> ROCK_SWORD = GEO_ITEMS.register(
      "rock_sword", () -> new RockSwordItem(new Properties().rarity(Rarity.RARE).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> MONOLITH_SWORD = GEO_ITEMS.register(
      "monolith_sword", () -> new MonolithSwordItem(new Properties().rarity(Rarity.RARE).stacksTo(1))
   );
   public static final DeferredHolder<Item, SonicBeatsSpellBook> RESONANCE_CANTICLE_SPELL_BOOK = SOUND_ITEMS.register(
      "resonance_canticle_spell_book", SonicBeatsSpellBook::new
   );
   public static final DeferredHolder<Item, SonicBeats2SpellBook> RESONANCE_CANTICLE_2_SPELL_BOOK = SOUND_ITEMS.register(
      "resonance_canticle_2_spell_book", SonicBeats2SpellBook::new
   );
   public static final DeferredHolder<Item, SonicBeats3SpellBook> RESONANCE_CANTICLE_3_SPELL_BOOK = SOUND_ITEMS.register(
      "resonance_canticle_3_spell_book", SonicBeats3SpellBook::new
   );
   public static final DeferredHolder<Item, Item> SOUND_UPGRADE_ORB_SMALL = SOUND_ITEMS.register(
      "sound_upgrade_orb_small",
      () -> new UpgradeOrbItem(new Properties().rarity(Rarity.COMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, ModUpgradeOrbTypes.SOUND_SPELL_POWER_SMALL))
   );
   public static final DeferredHolder<Item, Item> CLEF_SWORD = SOUND_ITEMS.register(
      "clef_sword", () -> new ClefSwordItem(new Properties().rarity(Rarity.RARE).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> TRUMPET_AXE = SOUND_ITEMS.register(
      "trumpet_axe", () -> new TrumpetAxeItem(new Properties().rarity(Rarity.RARE).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> LULLABY_FLUTE = SOUND_ITEMS.register(
      "lullaby_flute", () -> new LullabyFluteItem(new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> INFERNAL_FIRE_NECKLACE = BORN_IN_CHAOS_ITEMS.register("infernal_fire_necklace", InfernalFireNecklace::new);
   public static final DeferredHolder<Item, Item> INFERNAL_FIRE_UPGRADE_ORB = BORN_IN_CHAOS_ITEMS.register(
      "infernal_fire_upgrade_orb",
      () -> new UpgradeOrbItem(
         new Properties().rarity(Rarity.UNCOMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, ModUpgradeOrbTypes.INFERNAL_FIRE_SPELL_POWER)
      )
   );
   public static final DeferredHolder<Item, Item> INFERNAL_FIRE_RUNE = BORN_IN_CHAOS_ITEMS.register(
      "infernal_fire_rune", () -> new Item(new Properties().rarity(Rarity.COMMON))
   );
   public static final DeferredHolder<Item, Item> INFERNAL_FIRE = BORN_IN_CHAOS_ITEMS.register(
      "infernal_fire", () -> new Item(new Properties().rarity(Rarity.COMMON))
   );
   public static final DeferredHolder<Item, Item> INFERNAL_BLAZE_ROD = BORN_IN_CHAOS_ITEMS.register(
      "infernal_blaze_rod", () -> new Item(new Properties().rarity(Rarity.UNCOMMON).fireResistant())
   );
   public static final DeferredHolder<Item, Item> INFERNAL_BLAZE_POWDER = BORN_IN_CHAOS_ITEMS.register(
      "infernal_blaze_powder", () -> new Item(new Properties().rarity(Rarity.UNCOMMON).fireResistant())
   );
   public static final DeferredHolder<Item, Item> INFERNAL_BLAZE_SPAWN_EGG = BORN_IN_CHAOS_ITEMS.register(
      "infernal_blaze_spawn_egg", () -> new DeferredSpawnEggItem(ModCompatEntityTypes.INFERNAL_BLAZE, 3812133, 11067440, new Properties())
   );
   public static final DeferredHolder<Item, Item> DARK_METAL_BATTLEMAGE_HELMET = BORN_IN_CHAOS_ITEMS.register(
      "dark_metal_battlemage_helmet",
      () -> new DarkMetalBattlemageArmorItem(ModArmorMaterials.DARK_METAL, Type.HELMET, new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> DARK_METAL_BATTLEMAGE_CHESTPLATE = BORN_IN_CHAOS_ITEMS.register(
      "dark_metal_battlemage_chestplate",
      () -> new DarkMetalBattlemageArmorItem(ModArmorMaterials.DARK_METAL, Type.CHESTPLATE, new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> DARK_METAL_BATTLEMAGE_LEGGINGS = BORN_IN_CHAOS_ITEMS.register(
      "dark_metal_battlemage_leggings",
      () -> new DarkMetalBattlemageArmorItem(ModArmorMaterials.DARK_METAL, Type.LEGGINGS, new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> DARK_METAL_BATTLEMAGE_BOOTS = BORN_IN_CHAOS_ITEMS.register(
      "dark_metal_battlemage_boots",
      () -> new DarkMetalBattlemageArmorItem(ModArmorMaterials.DARK_METAL, Type.BOOTS, new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> INFERNAL_FIRE_IGNITIUM_STAFF = BORN_IN_CHAOS_ITEMS.register(
      "infernal_fire_ignitium_staff", () -> new InfernalFireIgnitiumStaffItem(new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> INFERNAL_FIRE_UPGRADE_ORB_SMALL = BORN_IN_CHAOS_ITEMS.register(
      "infernal_fire_upgrade_orb_small",
      () -> new UpgradeOrbItem(
         new Properties().rarity(Rarity.COMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, ModUpgradeOrbTypes.INFERNAL_FIRE_SPELL_POWER_SMALL)
      )
   );

   public static void registerGeo(IEventBus eventBus) {
      GEO_ITEMS.register(eventBus);
   }

   public static void registerSound(IEventBus eventBus) {
      SOUND_ITEMS.register(eventBus);
   }

   public static void registerBornInChaos(IEventBus eventBus) {
      BORN_IN_CHAOS_ITEMS.register(eventBus);
   }

   public static boolean isInfernalFireItem(Item item) {
      return BornInChaosCompat.isLoaded() && item == INFERNAL_FIRE.get();
   }

   public static boolean isInfernalFireNecklace(Item item) {
      return BornInChaosCompat.isLoaded() && item == INFERNAL_FIRE_NECKLACE.get();
   }

   public static boolean isInfernalBlazeCompatItem(Item item) {
      return BornInChaosCompat.isLoaded() && (item == INFERNAL_BLAZE_ROD.get() || item == INFERNAL_BLAZE_POWDER.get());
   }
}
