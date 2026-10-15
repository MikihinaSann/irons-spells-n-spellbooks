package com.somake.somakespells.registries;

import com.somake.somakespells.client.rarity.SomakeRarities;
import com.somake.somakespells.compat.LegendaryMonstersCompat;
import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.item.AquamancerStaffItem;
import com.somake.somakespells.item.ArchangelsStaffItem;
import com.somake.somakespells.item.BaseAttributeSwordItem;
import com.somake.somakespells.item.BoltcutterDaggerItem;
import com.somake.somakespells.item.CentauriItem;
import com.somake.somakespells.item.CopperGloveItem;
import com.somake.somakespells.item.CoreSplitterItem;
import com.somake.somakespells.item.DormantGrimoireItem;
import com.somake.somakespells.item.FragmentedSword1Item;
import com.somake.somakespells.item.FragmentedSword2Item;
import com.somake.somakespells.item.FragmentedSword3Item;
import com.somake.somakespells.item.GlaciumGreataxeItem;
import com.somake.somakespells.item.HallowSwordItem;
import com.somake.somakespells.item.IgnitiumStaffItem;
import com.somake.somakespells.item.LastMourningItem;
import com.somake.somakespells.item.MirroredEdgeItem;
import com.somake.somakespells.item.PedestalOfFireBlockItem;
import com.somake.somakespells.item.ProfaneGrimoireItem;
import com.somake.somakespells.item.RadiantJudgmentItem;
import com.somake.somakespells.item.RedSoulLanternItem;
import com.somake.somakespells.item.RuinedBladeItem;
import com.somake.somakespells.item.SanctumGrimoireItem;
import com.somake.somakespells.item.ScornItem;
import com.somake.somakespells.item.SculkborneAxeItem;
import com.somake.somakespells.item.SoulFireIgnitiumStaffItem;
import com.somake.somakespells.item.SoulFireNecklace;
import com.somake.somakespells.item.SpiritStaffItem;
import com.somake.somakespells.item.SymmetryStaffItem;
import com.somake.somakespells.item.TidePiercerItem;
import com.somake.somakespells.item.ToggleableSpellbladeItem;
import com.somake.somakespells.item.TotemDaggerItem;
import com.somake.somakespells.item.WitheredRoseStaffItem;
import com.somake.somakespells.item.WitheriteGlaiveItem;
import com.somake.somakespells.item.armor.AbyssiumArmorItem;
import com.somake.somakespells.item.armor.AquamancerArmorItem;
import com.somake.somakespells.item.armor.CeraniumArmorItem;
import com.somake.somakespells.item.armor.ModArmorMaterials;
import com.somake.somakespells.item.spellbooks.AquamancerSpellBook;
import com.somake.somakespells.item.spellbooks.AxiomaticCodex2SpellBook;
import com.somake.somakespells.item.spellbooks.AxiomaticCodex3SpellBook;
import com.somake.somakespells.item.spellbooks.AxiomaticCodexSpellBook;
import com.somake.somakespells.item.spellbooks.BurningMagma2SpellBook;
import com.somake.somakespells.item.spellbooks.BurningMagma3SpellBook;
import com.somake.somakespells.item.spellbooks.BurningMagmaSpellBook;
import com.somake.somakespells.item.spellbooks.DawnlitGospel2SpellBook;
import com.somake.somakespells.item.spellbooks.DawnlitGospel3SpellBook;
import com.somake.somakespells.item.spellbooks.DawnlitGospelSpellBook;
import com.somake.somakespells.item.spellbooks.FrostveinCodex2SpellBook;
import com.somake.somakespells.item.spellbooks.FrostveinCodex3SpellBook;
import com.somake.somakespells.item.spellbooks.FrostveinCodexSpellBook;
import com.somake.somakespells.item.spellbooks.InvokerCanon2SpellBook;
import com.somake.somakespells.item.spellbooks.InvokerCanon3SpellBook;
import com.somake.somakespells.item.spellbooks.InvokerCanonSpellBook;
import com.somake.somakespells.item.spellbooks.SanguineTestament2SpellBook;
import com.somake.somakespells.item.spellbooks.SanguineTestament3SpellBook;
import com.somake.somakespells.item.spellbooks.SanguineTestamentSpellBook;
import com.somake.somakespells.item.spellbooks.SoullitTestament2SpellBook;
import com.somake.somakespells.item.spellbooks.SoullitTestament3SpellBook;
import com.somake.somakespells.item.spellbooks.SoullitTestamentSpellBook;
import com.somake.somakespells.item.spellbooks.StarlessGrimoire2SpellBook;
import com.somake.somakespells.item.spellbooks.StarlessGrimoire3SpellBook;
import com.somake.somakespells.item.spellbooks.StarlessGrimoireSpellBook;
import com.somake.somakespells.item.spellbooks.StormScripturesSpellBook;
import com.somake.somakespells.item.spellbooks.StormcallerLexicon2SpellBook;
import com.somake.somakespells.item.spellbooks.StormcallerLexicon3SpellBook;
import com.somake.somakespells.item.spellbooks.StormcallerLexiconSpellBook;
import com.somake.somakespells.item.spellbooks.Tidebound2SpellBook;
import com.somake.somakespells.item.spellbooks.Tidebound3SpellBook;
import com.somake.somakespells.item.spellbooks.TideboundSpellBook;
import com.somake.somakespells.item.spellbooks.VerdantTestament2SpellBook;
import com.somake.somakespells.item.spellbooks.VerdantTestament3SpellBook;
import com.somake.somakespells.item.spellbooks.VerdantTestamentSpellBook;
import com.somake.somakespells.item.spellbooks.VoidboundCodex2SpellBook;
import com.somake.somakespells.item.spellbooks.VoidboundCodex3SpellBook;
import com.somake.somakespells.item.spellbooks.VoidboundCodexSpellBook;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.UpgradeOrbItem;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
   public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, "somakespells");
   private static final boolean ISS_LOADED = MagicFromTheEastCompat.isLoaded();
   private static final boolean LEGENDARY_MONSTERS_LOADED = LegendaryMonstersCompat.isLoaded();
   public static final DeferredHolder<Item, TideboundSpellBook> TIDEBOUND_SPELL_BOOK = ITEMS.register("tidebound_spell_book", TideboundSpellBook::new);
   public static final DeferredHolder<Item, Tidebound2SpellBook> TIDEBOUND_2_SPELL_BOOK = ITEMS.register("tidebound_2_spell_book", Tidebound2SpellBook::new);
   public static final DeferredHolder<Item, Tidebound3SpellBook> TIDEBOUND_3_SPELL_BOOK = ITEMS.register("tidebound_3_spell_book", Tidebound3SpellBook::new);
   public static final DeferredHolder<Item, AquamancerSpellBook> AQUAMANCER_SPELL_BOOK = ITEMS.register("aquamancer_spell_book", AquamancerSpellBook::new);
   public static final DeferredHolder<Item, StormScripturesSpellBook> STORM_SCRIPTURES_SPELL_BOOK = ITEMS.register(
      "storm_scriptures_spell_book", StormScripturesSpellBook::new
   );
   public static final DeferredHolder<Item, StarlessGrimoireSpellBook> STARLESS_GRIMOIRE_SPELL_BOOK = ITEMS.register(
      "starless_grimoire_spell_book", StarlessGrimoireSpellBook::new
   );
   public static final DeferredHolder<Item, StarlessGrimoire2SpellBook> STARLESS_GRIMOIRE_2_SPELL_BOOK = ITEMS.register(
      "starless_grimoire_2_spell_book", StarlessGrimoire2SpellBook::new
   );
   public static final DeferredHolder<Item, StarlessGrimoire3SpellBook> STARLESS_GRIMOIRE_3_SPELL_BOOK = ITEMS.register(
      "starless_grimoire_3_spell_book", StarlessGrimoire3SpellBook::new
   );
   public static final DeferredHolder<Item, VoidboundCodexSpellBook> VOIDBOUND_CODEX_SPELL_BOOK = ITEMS.register(
      "voidbound_codex_spell_book", VoidboundCodexSpellBook::new
   );
   public static final DeferredHolder<Item, VoidboundCodex2SpellBook> VOIDBOUND_CODEX_2_SPELL_BOOK = ITEMS.register(
      "voidbound_codex_2_spell_book", VoidboundCodex2SpellBook::new
   );
   public static final DeferredHolder<Item, VoidboundCodex3SpellBook> VOIDBOUND_CODEX_3_SPELL_BOOK = ITEMS.register(
      "voidbound_codex_3_spell_book", VoidboundCodex3SpellBook::new
   );
   public static final DeferredHolder<Item, InvokerCanonSpellBook> INVOKER_CANON_SPELL_BOOK = ITEMS.register(
      "invoker_canon_spell_book", InvokerCanonSpellBook::new
   );
   public static final DeferredHolder<Item, InvokerCanon2SpellBook> INVOKER_CANON_2_SPELL_BOOK = ITEMS.register(
      "invoker_canon_2_spell_book", InvokerCanon2SpellBook::new
   );
   public static final DeferredHolder<Item, InvokerCanon3SpellBook> INVOKER_CANON_3_SPELL_BOOK = ITEMS.register(
      "invoker_canon_3_spell_book", InvokerCanon3SpellBook::new
   );
   public static final DeferredHolder<Item, BurningMagmaSpellBook> BURNING_MAGMA_SPELL_BOOK = ITEMS.register(
      "burning_magma_spell_book", BurningMagmaSpellBook::new
   );
   public static final DeferredHolder<Item, BurningMagma2SpellBook> BURNING_MAGMA_2_SPELL_BOOK = ITEMS.register(
      "burning_magma_2_spell_book", BurningMagma2SpellBook::new
   );
   public static final DeferredHolder<Item, BurningMagma3SpellBook> BURNING_MAGMA_3_SPELL_BOOK = ITEMS.register(
      "burning_magma_3_spell_book", BurningMagma3SpellBook::new
   );
   public static final DeferredHolder<Item, Item> UPGRADE_FORGE = ITEMS.register(
      "upgrade_forge", () -> new BlockItem((Block)ModBlocks.UPGRADE_FORGE.get(), new Properties().rarity(Rarity.RARE))
   );
   public static final DeferredHolder<Item, Item> PEDESTAL_OF_FIRE = ITEMS.register(
      "pedestal_of_fire", () -> new PedestalOfFireBlockItem((Block)ModBlocks.PEDESTAL_OF_FIRE.get(), new Properties().rarity(Rarity.RARE))
   );
   public static final DeferredHolder<Item, DawnlitGospelSpellBook> DAWNLIT_GOSPEL_SPELL_BOOK = ITEMS.register(
      "dawnlit_gospel_spell_book", DawnlitGospelSpellBook::new
   );
   public static final DeferredHolder<Item, DawnlitGospel2SpellBook> DAWNLIT_GOSPEL_2_SPELL_BOOK = ITEMS.register(
      "dawnlit_gospel_2_spell_book", DawnlitGospel2SpellBook::new
   );
   public static final DeferredHolder<Item, DawnlitGospel3SpellBook> DAWNLIT_GOSPEL_3_SPELL_BOOK = ITEMS.register(
      "dawnlit_gospel_3_spell_book", DawnlitGospel3SpellBook::new
   );
   public static final DeferredHolder<Item, FrostveinCodexSpellBook> FROSTVEIN_CODEX_SPELL_BOOK = ITEMS.register(
      "frostvein_codex_spell_book", FrostveinCodexSpellBook::new
   );
   public static final DeferredHolder<Item, FrostveinCodex2SpellBook> FROSTVEIN_CODEX_2_SPELL_BOOK = ITEMS.register(
      "frostvein_codex_2_spell_book", FrostveinCodex2SpellBook::new
   );
   public static final DeferredHolder<Item, FrostveinCodex3SpellBook> FROSTVEIN_CODEX_3_SPELL_BOOK = ITEMS.register(
      "frostvein_codex_3_spell_book", FrostveinCodex3SpellBook::new
   );
   public static final DeferredHolder<Item, StormcallerLexiconSpellBook> STORMCALLER_LEXICON_SPELL_BOOK = ITEMS.register(
      "stormcaller_lexicon_spell_book", StormcallerLexiconSpellBook::new
   );
   public static final DeferredHolder<Item, StormcallerLexicon2SpellBook> STORMCALLER_LEXICON_2_SPELL_BOOK = ITEMS.register(
      "stormcaller_lexicon_2_spell_book", StormcallerLexicon2SpellBook::new
   );
   public static final DeferredHolder<Item, StormcallerLexicon3SpellBook> STORMCALLER_LEXICON_3_SPELL_BOOK = ITEMS.register(
      "stormcaller_lexicon_3_spell_book", StormcallerLexicon3SpellBook::new
   );
   public static final DeferredHolder<Item, VerdantTestamentSpellBook> VERDANT_TESTAMENT_SPELL_BOOK = ITEMS.register(
      "verdant_testament_spell_book", VerdantTestamentSpellBook::new
   );
   public static final DeferredHolder<Item, VerdantTestament2SpellBook> VERDANT_TESTAMENT_2_SPELL_BOOK = ITEMS.register(
      "verdant_testament_2_spell_book", VerdantTestament2SpellBook::new
   );
   public static final DeferredHolder<Item, VerdantTestament3SpellBook> VERDANT_TESTAMENT_3_SPELL_BOOK = ITEMS.register(
      "verdant_testament_3_spell_book", VerdantTestament3SpellBook::new
   );
   public static final DeferredHolder<Item, SanguineTestamentSpellBook> SANGUINE_TESTAMENT_SPELL_BOOK = ITEMS.register(
      "sanguine_testament_spell_book", SanguineTestamentSpellBook::new
   );
   public static final DeferredHolder<Item, SanguineTestament2SpellBook> SANGUINE_TESTAMENT_2_SPELL_BOOK = ITEMS.register(
      "sanguine_testament_2_spell_book", SanguineTestament2SpellBook::new
   );
   public static final DeferredHolder<Item, SanguineTestament3SpellBook> SANGUINE_TESTAMENT_3_SPELL_BOOK = ITEMS.register(
      "sanguine_testament_3_spell_book", SanguineTestament3SpellBook::new
   );
   public static final DeferredHolder<Item, SoullitTestamentSpellBook> SOULLIT_TESTAMENT_SPELL_BOOK = LEGENDARY_MONSTERS_LOADED
      ? ITEMS.register("soullit_testament_spell_book", SoullitTestamentSpellBook::new)
      : null;
   public static final DeferredHolder<Item, SoullitTestament2SpellBook> SOULLIT_TESTAMENT_2_SPELL_BOOK = LEGENDARY_MONSTERS_LOADED
      ? ITEMS.register("soullit_testament_2_spell_book", SoullitTestament2SpellBook::new)
      : null;
   public static final DeferredHolder<Item, SoullitTestament3SpellBook> SOULLIT_TESTAMENT_3_SPELL_BOOK = LEGENDARY_MONSTERS_LOADED
      ? ITEMS.register("soullit_testament_3_spell_book", SoullitTestament3SpellBook::new)
      : null;
   public static final DeferredHolder<Item, AxiomaticCodexSpellBook> AXIOMATIC_CODEX_SPELL_BOOK = ISS_LOADED
      ? ITEMS.register("axiomatic_codex_spell_book", AxiomaticCodexSpellBook::new)
      : null;
   public static final DeferredHolder<Item, AxiomaticCodex2SpellBook> AXIOMATIC_CODEX_2_SPELL_BOOK = ISS_LOADED
      ? ITEMS.register("axiomatic_codex_2_spell_book", AxiomaticCodex2SpellBook::new)
      : null;
   public static final DeferredHolder<Item, AxiomaticCodex3SpellBook> AXIOMATIC_CODEX_3_SPELL_BOOK = ISS_LOADED
      ? ITEMS.register("axiomatic_codex_3_spell_book", AxiomaticCodex3SpellBook::new)
      : null;
   public static final Supplier<Item> SOUL_FIRE_NECKLACE = ITEMS.register("soul_fire_necklace", SoulFireNecklace::new);
   public static final DeferredHolder<Item, Item> AQUA_UPGRADE_ORB = ITEMS.register(
      "aqua_upgrade_orb",
      () -> new UpgradeOrbItem(new Properties().rarity(Rarity.UNCOMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, ModUpgradeOrbTypes.AQUA_SPELL_POWER))
   );
   public static final DeferredHolder<Item, Item> SOUL_FIRE_UPGRADE_ORB = ITEMS.register(
      "soul_fire_upgrade_orb",
      () -> new UpgradeOrbItem(new Properties().rarity(Rarity.UNCOMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, ModUpgradeOrbTypes.SOUL_FIRE_SPELL_POWER))
   );
   public static final DeferredHolder<Item, Item> ELDRITCH_UPGRADE_ORB = ITEMS.register(
      "eldritch_upgrade_orb",
      () -> new UpgradeOrbItem(new Properties().rarity(Rarity.UNCOMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, ModUpgradeOrbTypes.ELDRITCH_SPELL_POWER))
   );
   public static final DeferredHolder<Item, Item> ELDRITCH_RUNE = ITEMS.register("eldritch_rune", () -> new Item(new Properties().rarity(Rarity.COMMON)));
   public static final DeferredHolder<Item, Item> AQUA_RUNE = ITEMS.register("aqua_rune", () -> new Item(new Properties().rarity(Rarity.COMMON)));
   public static final DeferredHolder<Item, Item> SOUL_FIRE_RUNE = ITEMS.register("soul_fire_rune", () -> new Item(new Properties().rarity(Rarity.COMMON)));
   public static final DeferredHolder<Item, Item> ELDER_GUARDIAN_EYE = ITEMS.register(
      "elder_guardian_eye", () -> new Item(new Properties().rarity(Rarity.RARE))
   );
   public static final DeferredHolder<Item, Item> FIRE = ITEMS.register("fire", () -> new Item(new Properties().rarity(Rarity.COMMON)));
   public static final DeferredHolder<Item, Item> SOUL_FIRE = ITEMS.register("soul_fire", () -> new Item(new Properties().rarity(Rarity.COMMON)));
   public static final DeferredHolder<Item, Item> TORMENTED_SPIRIT = ITEMS.register(
      "tormented_spirit", () -> new Item(new Properties().rarity(Rarity.UNCOMMON).fireResistant())
   );
   public static final DeferredHolder<Item, Item> CORRUPTED_RED_SOUL = LEGENDARY_MONSTERS_LOADED
      ? ITEMS.register("corrupted_red_soul", () -> new Item(new Properties().rarity(Rarity.EPIC).fireResistant()))
      : null;
   public static final DeferredHolder<Item, RedSoulLanternItem> RED_SOUL_LANTERN = LEGENDARY_MONSTERS_LOADED
      ? ITEMS.register("red_soul_lantern", () -> new RedSoulLanternItem(new Properties().rarity(Rarity.EPIC).durability(16)))
      : null;
   public static final DeferredHolder<Item, Item> SOUL_BLAZE_ROD = ITEMS.register(
      "soul_blaze_rod", () -> new Item(new Properties().rarity(Rarity.UNCOMMON).fireResistant())
   );
   public static final DeferredHolder<Item, Item> SOUL_BLAZE_POWDER = ITEMS.register(
      "soul_blaze_powder", () -> new Item(new Properties().rarity(Rarity.UNCOMMON).fireResistant())
   );
   public static final DeferredHolder<Item, Item> AQUAMANCER_STAFF = ITEMS.register(
      "aquamancer_staff", () -> new AquamancerStaffItem(new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> IGNITIUM_STAFF = ITEMS.register(
      "ignitium_staff", () -> new IgnitiumStaffItem(new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> SOUL_FIRE_IGNITIUM_STAFF = ITEMS.register(
      "soul_fire_ignitium_staff", () -> new SoulFireIgnitiumStaffItem(new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> SPIRIT_STAFF = LEGENDARY_MONSTERS_LOADED
      ? ITEMS.register("spirit_staff", () -> new SpiritStaffItem(new Properties().rarity(Rarity.EPIC).stacksTo(1)))
      : null;
   public static final DeferredHolder<Item, Item> SYMMETRY_STAFF = ISS_LOADED
      ? ITEMS.register("symmetry_staff", () -> new SymmetryStaffItem(new Properties().rarity(Rarity.EPIC).stacksTo(1)))
      : null;
   public static final DeferredHolder<Item, Item> ARCHANGELS_STAFF = ITEMS.register(
      "archangels_staff", () -> new ArchangelsStaffItem(new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> WITHERED_ROSE_STAFF = ITEMS.register(
      "withered_rose_staff", () -> new WitheredRoseStaffItem(new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> DORMANT_GRIMOIRE = ITEMS.register(
      "dormant_grimoire", () -> new DormantGrimoireItem(new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> SANCTUM_GRIMOIRE = ITEMS.register(
      "sanctum_grimoire", () -> new SanctumGrimoireItem(new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> PROFANE_GRIMOIRE = ITEMS.register(
      "profane_grimoire", () -> new ProfaneGrimoireItem(new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> GLACIUM_GREATAXE = ITEMS.register(
      "glacium_greataxe", () -> new GlaciumGreataxeItem(new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> CORE_SPLITTER = ITEMS.register(
      "core_splitter", () -> new CoreSplitterItem(new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> COPPER_GLOVE = ITEMS.register(
      "copper_glove", () -> new CopperGloveItem(new Properties().rarity(Rarity.RARE).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> HALLOW_SWORD = ITEMS.register(
      "hallow_sword", () -> new HallowSwordItem(new Properties().rarity(Rarity.RARE).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> FRAGMENTED_SWORD_1 = ITEMS.register(
      "fragmented_sword_1", () -> new FragmentedSword1Item(new Properties().rarity(Rarity.RARE).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> FRAGMENTED_SWORD_2 = ITEMS.register(
      "fragmented_sword_2", () -> new FragmentedSword2Item(new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> FRAGMENTED_SWORD_3 = ITEMS.register(
      "fragmented_sword_3", () -> new FragmentedSword3Item(new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> CENTAURI = ITEMS.register(
      "centauri", () -> new CentauriItem(new Properties().rarity(Rarity.RARE).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> LAST_MOURNING = ITEMS.register(
      "last_mourning", () -> new LastMourningItem(new Properties().rarity(Rarity.RARE).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> MIRRORED_EDGE = ITEMS.register(
      "mirrored_edge", () -> new MirroredEdgeItem(new Properties().rarity(Rarity.RARE).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> RADIANT_JUDGMENT = ITEMS.register(
      "radiant_judgment", () -> new RadiantJudgmentItem(new Properties().rarity(Rarity.RARE).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> SCORN = ITEMS.register("scorn", () -> new ScornItem(new Properties().rarity(Rarity.RARE).stacksTo(1)));
   public static final DeferredHolder<Item, Item> SCULKBORNE_AXE = ITEMS.register(
      "sculkborne_axe", () -> new SculkborneAxeItem(new Properties().rarity(Rarity.RARE).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> TIDE_PIERCER = ITEMS.register(
      "tide_piercer", () -> new TidePiercerItem(new Properties().rarity(Rarity.RARE).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> TOTEM_DAGGER = ITEMS.register(
      "totem_dagger", () -> new TotemDaggerItem(new Properties().rarity(Rarity.RARE).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> WITHERITE_GLAIVE = ITEMS.register(
      "witherite_glaive", () -> new WitheriteGlaiveItem(new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> RUINED_BLADE = ITEMS.register(
      "ruined_blade", () -> new RuinedBladeItem(new Properties().rarity(Rarity.RARE).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> BOLTCUTTER_DAGGER = ITEMS.register(
      "boltcutter_dagger", () -> new BoltcutterDaggerItem(new Properties().rarity(Rarity.RARE).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> FORBIDDEN_HAMMER = ITEMS.register(
      "forbidden_hammer",
      () -> new BaseAttributeSwordItem(
         Tiers.IRON,
         15,
         -3.0F,
         new Properties().rarity(Rarity.EPIC).stacksTo(1),
         AttributeRegistry.ENDER_SPELL_POWER,
         ResourceLocation.fromNamespaceAndPath("somakespells", "forbidden_hammer_modifier"),
         0.15,
         Operation.ADD_MULTIPLIED_BASE
      )
   );
   public static final DeferredHolder<Item, Item> EXO_LIGHT_SABER = ITEMS.register(
      "exo_light_saber",
      () -> new ToggleableSpellbladeItem(
         Tiers.IRON,
         11,
         3,
         -2.3F,
         new Properties().rarity(Rarity.EPIC).stacksTo(1),
         AttributeRegistry.LIGHTNING_SPELL_POWER,
         ResourceLocation.fromNamespaceAndPath("somakespells", "exo_light_saber_modifier"),
         0.15
      )
   );
   public static final DeferredHolder<Item, Item> FLARE_LIGHT_SABER = ITEMS.register(
      "flare_light_saber",
      () -> new ToggleableSpellbladeItem(
         Tiers.IRON,
         11,
         3,
         -2.3F,
         new Properties().rarity(Rarity.EPIC).stacksTo(1),
         AttributeRegistry.FIRE_SPELL_POWER,
         ResourceLocation.fromNamespaceAndPath("somakespells", "flare_light_saber_modifier"),
         0.15
      )
   );
   public static final DeferredHolder<Item, Item> FROSTFLOW_LIGHT_SABER = ITEMS.register(
      "frostflow_light_saber",
      () -> new ToggleableSpellbladeItem(
         Tiers.IRON,
         11,
         3,
         -2.3F,
         new Properties().rarity(Rarity.EPIC).stacksTo(1),
         AttributeRegistry.ICE_SPELL_POWER,
         ResourceLocation.fromNamespaceAndPath("somakespells", "frostflow_light_saber_modifier"),
         0.15
      )
   );
   public static final DeferredHolder<Item, Item> PLAGUE_LIGHT_SABER = ITEMS.register(
      "plague_light_saber",
      () -> new ToggleableSpellbladeItem(
         Tiers.IRON,
         11,
         3,
         -2.3F,
         new Properties().rarity(Rarity.EPIC).stacksTo(1),
         AttributeRegistry.NATURE_SPELL_POWER,
         ResourceLocation.fromNamespaceAndPath("somakespells", "plague_light_saber_modifier"),
         0.15
      )
   );
   public static final DeferredHolder<Item, Item> EXO_DUAL_SABER = ITEMS.register(
      "exo_dual_saber",
      () -> new ToggleableSpellbladeItem(
         Tiers.IRON,
         14,
         3,
         -2.3F,
         new Properties().rarity(Rarity.EPIC).stacksTo(1),
         AttributeRegistry.LIGHTNING_SPELL_POWER,
         ResourceLocation.fromNamespaceAndPath("somakespells", "exo_dual_saber_modifier"),
         0.2
      )
   );
   public static final DeferredHolder<Item, Item> FLARE_DUAL_SABER = ITEMS.register(
      "flare_dual_saber",
      () -> new ToggleableSpellbladeItem(
         Tiers.IRON,
         14,
         3,
         -2.3F,
         new Properties().rarity(Rarity.EPIC).stacksTo(1),
         AttributeRegistry.FIRE_SPELL_POWER,
         ResourceLocation.fromNamespaceAndPath("somakespells", "flare_dual_saber_modifier"),
         0.2
      )
   );
   public static final DeferredHolder<Item, Item> FROSTFLOW_DUAL_SABER = ITEMS.register(
      "frostflow_dual_saber",
      () -> new ToggleableSpellbladeItem(
         Tiers.IRON,
         14,
         3,
         -2.3F,
         new Properties().rarity(Rarity.EPIC).stacksTo(1),
         AttributeRegistry.ICE_SPELL_POWER,
         ResourceLocation.fromNamespaceAndPath("somakespells", "frostflow_dual_saber_modifier"),
         0.2
      )
   );
   public static final DeferredHolder<Item, Item> PLAGUE_DUAL_SABER = ITEMS.register(
      "plague_dual_saber",
      () -> new ToggleableSpellbladeItem(
         Tiers.IRON,
         14,
         3,
         -2.3F,
         new Properties().rarity(Rarity.EPIC).stacksTo(1),
         AttributeRegistry.NATURE_SPELL_POWER,
         ResourceLocation.fromNamespaceAndPath("somakespells", "plague_dual_saber_modifier"),
         0.2
      )
   );
   public static final DeferredHolder<Item, Item> FIRE_UPGRADE_ORB_SMALL = ITEMS.register(
      "fire_upgrade_orb_small",
      () -> new UpgradeOrbItem(new Properties().rarity(Rarity.COMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, ModUpgradeOrbTypes.FIRE_SPELL_POWER_SMALL))
   );
   public static final DeferredHolder<Item, Item> ICE_UPGRADE_ORB_SMALL = ITEMS.register(
      "ice_upgrade_orb_small",
      () -> new UpgradeOrbItem(new Properties().rarity(Rarity.COMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, ModUpgradeOrbTypes.ICE_SPELL_POWER_SMALL))
   );
   public static final DeferredHolder<Item, Item> LIGHTNING_UPGRADE_ORB_SMALL = ITEMS.register(
      "lightning_upgrade_orb_small",
      () -> new UpgradeOrbItem(
         new Properties().rarity(Rarity.COMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, ModUpgradeOrbTypes.LIGHTNING_SPELL_POWER_SMALL)
      )
   );
   public static final DeferredHolder<Item, Item> HOLY_UPGRADE_ORB_SMALL = ITEMS.register(
      "holy_upgrade_orb_small",
      () -> new UpgradeOrbItem(new Properties().rarity(Rarity.COMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, ModUpgradeOrbTypes.HOLY_SPELL_POWER_SMALL))
   );
   public static final DeferredHolder<Item, Item> ENDER_UPGRADE_ORB_SMALL = ITEMS.register(
      "ender_upgrade_orb_small",
      () -> new UpgradeOrbItem(new Properties().rarity(Rarity.COMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, ModUpgradeOrbTypes.ENDER_SPELL_POWER_SMALL))
   );
   public static final DeferredHolder<Item, Item> BLOOD_UPGRADE_ORB_SMALL = ITEMS.register(
      "blood_upgrade_orb_small",
      () -> new UpgradeOrbItem(new Properties().rarity(Rarity.COMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, ModUpgradeOrbTypes.BLOOD_SPELL_POWER_SMALL))
   );
   public static final DeferredHolder<Item, Item> EVOCATION_UPGRADE_ORB_SMALL = ITEMS.register(
      "evocation_upgrade_orb_small",
      () -> new UpgradeOrbItem(
         new Properties().rarity(Rarity.COMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, ModUpgradeOrbTypes.EVOCATION_SPELL_POWER_SMALL)
      )
   );
   public static final DeferredHolder<Item, Item> NATURE_UPGRADE_ORB_SMALL = ITEMS.register(
      "nature_upgrade_orb_small",
      () -> new UpgradeOrbItem(
         new Properties().rarity(Rarity.COMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, ModUpgradeOrbTypes.NATURE_SPELL_POWER_SMALL)
      )
   );
   public static final DeferredHolder<Item, Item> ELDRITCH_UPGRADE_ORB_SMALL = ITEMS.register(
      "eldritch_upgrade_orb_small",
      () -> new UpgradeOrbItem(
         new Properties().rarity(Rarity.COMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, ModUpgradeOrbTypes.ELDRITCH_SPELL_POWER_SMALL)
      )
   );
   public static final DeferredHolder<Item, Item> AQUA_UPGRADE_ORB_SMALL = ITEMS.register(
      "aqua_upgrade_orb_small",
      () -> new UpgradeOrbItem(new Properties().rarity(Rarity.COMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, ModUpgradeOrbTypes.AQUA_SPELL_POWER_SMALL))
   );
   public static final DeferredHolder<Item, Item> SOUL_FIRE_UPGRADE_ORB_SMALL = ITEMS.register(
      "soul_fire_upgrade_orb_small",
      () -> new UpgradeOrbItem(
         new Properties().rarity(Rarity.COMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, ModUpgradeOrbTypes.SOUL_FIRE_SPELL_POWER_SMALL)
      )
   );
   public static final DeferredHolder<Item, Item> SPIRIT_UPGRADE_ORB_SMALL = LEGENDARY_MONSTERS_LOADED
      ? ITEMS.register(
         "spirit_upgrade_orb_small",
         () -> new UpgradeOrbItem(
            new Properties().rarity(Rarity.COMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, ModUpgradeOrbTypes.SPIRIT_SPELL_POWER_SMALL)
         )
      )
      : null;
   public static final DeferredHolder<Item, Item> SYMMETRY_UPGRADE_ORB_SMALL = ISS_LOADED
      ? ITEMS.register(
         "symmetry_upgrade_orb_small",
         () -> new UpgradeOrbItem(
            new Properties().rarity(Rarity.COMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, ModUpgradeOrbTypes.SYMMETRY_SPELL_POWER_SMALL)
         )
      )
      : null;
   public static final DeferredHolder<Item, Item> SOUL_BLAZE_SPAWN_EGG = ITEMS.register(
      "soul_blaze_spawn_egg", () -> new DeferredSpawnEggItem(ModEntityTypes.SOUL_BLAZE, 3298395, 9696247, new Properties())
   );
   public static final DeferredHolder<Item, Item> AQUA_GHOST_SPAWN_EGG = ITEMS.register(
      "aqua_ghost_spawn_egg", () -> new DeferredSpawnEggItem(ModEntityTypes.AQUA_GHOST, 2846627, 9560319, new Properties())
   );
   public static final DeferredHolder<Item, Item> FIRE_GHOST_SPAWN_EGG = ITEMS.register(
      "fire_ghost_spawn_egg", () -> new DeferredSpawnEggItem(ModEntityTypes.FIRE_GHOST, 11549230, 16758605, new Properties())
   );
   public static final DeferredHolder<Item, Item> ICE_GHOST_SPAWN_EGG = ITEMS.register(
      "ice_ghost_spawn_egg", () -> new DeferredSpawnEggItem(ModEntityTypes.ICE_GHOST, 6129840, 14808574, new Properties())
   );
   public static final DeferredHolder<Item, Item> LIGHTNING_GHOST_SPAWN_EGG = ITEMS.register(
      "lightning_ghost_spawn_egg", () -> new DeferredSpawnEggItem(ModEntityTypes.LIGHTNING_GHOST, 6044563, 15194458, new Properties())
   );
   public static final DeferredHolder<Item, Item> HOLY_GHOST_SPAWN_EGG = ITEMS.register(
      "holy_ghost_spawn_egg", () -> new DeferredSpawnEggItem(ModEntityTypes.HOLY_GHOST, 14271627, 16774870, new Properties())
   );
   public static final DeferredHolder<Item, Item> ENDER_GHOST_SPAWN_EGG = ITEMS.register(
      "ender_ghost_spawn_egg", () -> new DeferredSpawnEggItem(ModEntityTypes.ENDER_GHOST, 3809631, 11558399, new Properties())
   );
   public static final DeferredHolder<Item, Item> BLOOD_GHOST_SPAWN_EGG = ITEMS.register(
      "blood_ghost_spawn_egg", () -> new DeferredSpawnEggItem(ModEntityTypes.BLOOD_GHOST, 5970458, 14051946, new Properties())
   );
   public static final DeferredHolder<Item, Item> EVOCATION_GHOST_SPAWN_EGG = ITEMS.register(
      "evocation_ghost_spawn_egg", () -> new DeferredSpawnEggItem(ModEntityTypes.EVOCATION_GHOST, 3100527, 10995687, new Properties())
   );
   public static final DeferredHolder<Item, Item> NATURE_GHOST_SPAWN_EGG = ITEMS.register(
      "nature_ghost_spawn_egg", () -> new DeferredSpawnEggItem(ModEntityTypes.NATURE_GHOST, 2972205, 9359758, new Properties())
   );
   public static final DeferredHolder<Item, Item> ELDRITCH_GHOST_SPAWN_EGG = ITEMS.register(
      "eldritch_ghost_spawn_egg", () -> new DeferredSpawnEggItem(ModEntityTypes.ELDRITCH_GHOST, 1994602, 9429211, new Properties())
   );
   public static final DeferredHolder<Item, Item> SPIRIT_GHOST_SPAWN_EGG = LEGENDARY_MONSTERS_LOADED
      ? ITEMS.register("spirit_ghost_spawn_egg", () -> new DeferredSpawnEggItem(ModEntityTypes.SPIRIT_GHOST, 2781043, 10937321, new Properties()))
      : null;
   public static final DeferredHolder<Item, Item> SYMMETRY_GHOST_SPAWN_EGG = ISS_LOADED
      ? ITEMS.register("symmetry_ghost_spawn_egg", () -> new DeferredSpawnEggItem(ModEntityTypes.SYMMETRY_GHOST, 2771836, 11126527, new Properties()))
      : null;
   public static final DeferredHolder<Item, Item> CERANIUM_HELMET = ITEMS.register(
      "ceranium_helmet", () -> new CeraniumArmorItem(ModArmorMaterials.CERANIUM, Type.HELMET, new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> CERANIUM_CHESTPLATE = ITEMS.register(
      "ceranium_chestplate", () -> new CeraniumArmorItem(ModArmorMaterials.CERANIUM, Type.CHESTPLATE, new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> CERANIUM_LEGGINGS = ITEMS.register(
      "ceranium_leggings", () -> new CeraniumArmorItem(ModArmorMaterials.CERANIUM, Type.LEGGINGS, new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> CERANIUM_BOOTS = ITEMS.register(
      "ceranium_boots", () -> new CeraniumArmorItem(ModArmorMaterials.CERANIUM, Type.BOOTS, new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> AQUAMANCER_HELMET = ITEMS.register(
      "aquamancer_helmet", () -> new AquamancerArmorItem(ModArmorMaterials.AQUAMANCER, Type.HELMET, new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> AQUAMANCER_CHESTPLATE = ITEMS.register(
      "aquamancer_chestplate", () -> new AquamancerArmorItem(ModArmorMaterials.AQUAMANCER, Type.CHESTPLATE, new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> AQUAMANCER_LEGGINGS = ITEMS.register(
      "aquamancer_leggings", () -> new AquamancerArmorItem(ModArmorMaterials.AQUAMANCER, Type.LEGGINGS, new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> AQUAMANCER_BOOTS = ITEMS.register(
      "aquamancer_boots", () -> new AquamancerArmorItem(ModArmorMaterials.AQUAMANCER, Type.BOOTS, new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> ABYSSIUM_HELMET = ITEMS.register(
      "abyssium_helmet", () -> new AbyssiumArmorItem(ModArmorMaterials.ABYSSIUM, Type.HELMET, new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> ABYSSIUM_CHESTPLATE = ITEMS.register(
      "abyssium_chestplate", () -> new AbyssiumArmorItem(ModArmorMaterials.ABYSSIUM, Type.CHESTPLATE, new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> ABYSSIUM_LEGGINGS = ITEMS.register(
      "abyssium_leggings", () -> new AbyssiumArmorItem(ModArmorMaterials.ABYSSIUM, Type.LEGGINGS, new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> ABYSSIUM_BOOTS = ITEMS.register(
      "abyssium_boots", () -> new AbyssiumArmorItem(ModArmorMaterials.ABYSSIUM, Type.BOOTS, new Properties().rarity(Rarity.EPIC).stacksTo(1))
   );
   public static final DeferredHolder<Item, Item> CERANIUM_INGOT = ITEMS.register(
      "ceranium_ingot", () -> new Item(new Properties().rarity((Rarity)SomakeRarities.CERANIUM_RARITY.getValue()))
   );
   public static final DeferredHolder<Item, Item> CERANIUM_UPGRADE_SMITHING_TEMPLATE = ITEMS.register(
      "ceranium_upgrade_smithing_template", ModItems::createCeraniumUpgradeTemplate
   );
   public static final DeferredHolder<Item, Item> CERANIUM_PLATE = ITEMS.register(
      "ceranium_plate", () -> new Item(new Properties().rarity((Rarity)SomakeRarities.CERANIUM_RARITY.getValue()).stacksTo(64))
   );
   public static final DeferredHolder<Item, Item> ABYSSIUM_INGOT = ITEMS.register(
      "abyssium_ingot", () -> new Item(new Properties().rarity((Rarity)SomakeRarities.ABYSSIUM_RARITY.getValue()))
   );
   public static final DeferredHolder<Item, Item> ABYSSIUM_UPGRADE_SMITHING_TEMPLATE = ITEMS.register(
      "abyssium_upgrade_smithing_template", ModItems::createAbyssiumUpgradeTemplate
   );
   public static final DeferredHolder<Item, Item> CURSIUM_PLATE = ITEMS.register(
      "cursium_plate", () -> new Item(new Properties().rarity((Rarity)SomakeRarities.CURSIUM_RARITY.getValue()).stacksTo(64))
   );
   public static final DeferredHolder<Item, Item> CURSIUM_CORE = ITEMS.register(
      "cursium_core", () -> new Item(new Properties().rarity((Rarity)SomakeRarities.CURSIUM_RARITY.getValue()).stacksTo(64))
   );
   public static final DeferredHolder<Item, Item> IGNITIUM_PLATE = ITEMS.register(
      "ignitium_plate", () -> new Item(new Properties().rarity((Rarity)SomakeRarities.IGNITIUM_RARITY.getValue()).stacksTo(64))
   );
   public static final DeferredHolder<Item, Item> IGNITIUM_CORE = ITEMS.register(
      "ignitium_core", () -> new Item(new Properties().rarity((Rarity)SomakeRarities.IGNITIUM_RARITY.getValue()).stacksTo(64))
   );
   public static final DeferredHolder<Item, Item> WITHERITE_PLATE = ITEMS.register(
      "witherite_plate", () -> new Item(new Properties().rarity(Rarity.COMMON).stacksTo(64))
   );
   public static final DeferredHolder<Item, Item> WITHERITE_STAR = ITEMS.register(
      "witherite_star", () -> new Item(new Properties().rarity(Rarity.COMMON).stacksTo(64))
   );
   public static final DeferredHolder<Item, Item> ABYSSIUM_PLATE = ITEMS.register(
      "abyssium_plate", () -> new Item(new Properties().rarity((Rarity)SomakeRarities.ABYSSIUM_RARITY.getValue()).stacksTo(64))
   );
   public static final DeferredHolder<Item, Item> ABYSSIUM_CORE = ITEMS.register(
      "abyssium_core", () -> new Item(new Properties().rarity((Rarity)SomakeRarities.ABYSSIUM_RARITY.getValue()).stacksTo(64))
   );
   public static final DeferredHolder<Item, Item> ANCIENT_METAL_PLATE = ITEMS.register(
      "ancient_metal_plate", () -> new Item(new Properties().rarity(Rarity.COMMON).stacksTo(64))
   );
   public static final DeferredHolder<Item, Item> MAGMARIUM_PLATE = ITEMS.register(
      "magmarium_plate", () -> new Item(new Properties().rarity(Rarity.COMMON).stacksTo(64))
   );
   public static final DeferredHolder<Item, Item> MAGMARIUM_INGOT = ITEMS.register(
      "magmarium_ingot", () -> new Item(new Properties().rarity(Rarity.COMMON).stacksTo(64))
   );

   public static void register(IEventBus eventBus) {
      ITEMS.register(eventBus);
   }

   private static Item createCeraniumUpgradeTemplate() {
      return new SmithingTemplateItem(
         Component.translatable("item.somakespells.ceranium_upgrade.applies_to.desc").withStyle(ChatFormatting.BLUE),
         Component.translatable("item.somakespells.ceranium_upgrade.ingredients.desc").withStyle(ChatFormatting.BLUE),
         Component.translatable("item.somakespells.ceranium_upgrade.desc").withStyle(ChatFormatting.GRAY),
         Component.translatable("item.minecraft.smithing_template.netherite_upgrade.base_slot_description"),
         Component.translatable("item.minecraft.smithing_template.netherite_upgrade.additions_slot_description"),
         createUpgradeTemplateBaseSlotDescriptions(),
         createUpgradeTemplateAdditionSlotDescriptions(),
         new FeatureFlag[0]
      );
   }

   private static Item createAbyssiumUpgradeTemplate() {
      return new SmithingTemplateItem(
         Component.translatable("item.somakespells.abyssium_upgrade.applies_to.desc").withStyle(ChatFormatting.BLUE),
         Component.translatable("item.somakespells.abyssium_upgrade.ingredients.desc").withStyle(ChatFormatting.BLUE),
         Component.translatable("item.somakespells.abyssium_upgrade.desc").withStyle(ChatFormatting.GRAY),
         Component.translatable("item.minecraft.smithing_template.netherite_upgrade.base_slot_description"),
         Component.translatable("item.minecraft.smithing_template.netherite_upgrade.additions_slot_description"),
         createUpgradeTemplateBaseSlotDescriptions(),
         createUpgradeTemplateAdditionSlotDescriptions(),
         new FeatureFlag[0]
      );
   }

   private static List<ResourceLocation> createUpgradeTemplateBaseSlotDescriptions() {
      return List.of(
         ResourceLocation.fromNamespaceAndPath("minecraft", "item/empty_armor_slot_helmet"),
         ResourceLocation.fromNamespaceAndPath("minecraft", "item/empty_armor_slot_chestplate"),
         ResourceLocation.fromNamespaceAndPath("minecraft", "item/empty_armor_slot_leggings"),
         ResourceLocation.fromNamespaceAndPath("minecraft", "item/empty_armor_slot_boots")
      );
   }

   private static List<ResourceLocation> createUpgradeTemplateAdditionSlotDescriptions() {
      return List.of(ResourceLocation.fromNamespaceAndPath("minecraft", "item/empty_slot_ingot"));
   }
}
