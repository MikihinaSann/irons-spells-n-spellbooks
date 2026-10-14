package com.gametechbc.traveloptics.init;

import com.gametechbc.traveloptics.config.SpellsConfig;
import com.gametechbc.traveloptics.item.bossweapon.cursed_wraithblade.CursedWraithbladeItem;
import com.gametechbc.traveloptics.item.bossweapon.infernal_devastator.InfernalDevastatorItem;
import com.gametechbc.traveloptics.item.bossweapon.infernal_devastator.InfernalDevastatorLevelOneItem;
import com.gametechbc.traveloptics.item.bossweapon.infernal_devastator.InfernalDevastatorLevelThreeItem;
import com.gametechbc.traveloptics.item.bossweapon.infernal_devastator.InfernalDevastatorLevelTwoItem;
import com.gametechbc.traveloptics.item.bossweapon.mechanized_wraithblade.MechanizedWraithbladeItem;
import com.gametechbc.traveloptics.item.items.AbyssalSpellweaveIngotItem;
import com.gametechbc.traveloptics.item.items.AbyssalTentacleItem;
import com.gametechbc.traveloptics.item.items.AquaScrollDummyItem;
import com.gametechbc.traveloptics.item.items.CelestialSpellweaveIngotItem;
import com.gametechbc.traveloptics.item.items.CrimsonSpellweaveItem;
import com.gametechbc.traveloptics.item.items.CryoSpellweaveIngotItem;
import com.gametechbc.traveloptics.item.items.DesertJewelFragmentItem;
import com.gametechbc.traveloptics.item.items.EchoWingletItem;
import com.gametechbc.traveloptics.item.items.EldritchSpellweaveIngotItem;
import com.gametechbc.traveloptics.item.items.EvokatedSpellweaveIngotItem;
import com.gametechbc.traveloptics.item.items.FlameTemperedHandguardItem;
import com.gametechbc.traveloptics.item.items.KyrexiClawsItem;
import com.gametechbc.traveloptics.item.items.LastGlowItem;
import com.gametechbc.traveloptics.item.items.LightningSpellweaveIngotItem;
import com.gametechbc.traveloptics.item.items.PyroSpellweaveIngotItem;
import com.gametechbc.traveloptics.item.items.VerdantSpellweaveItem;
import com.gametechbc.traveloptics.item.items.VoidSpellweaveItem;
import com.gametechbc.traveloptics.item.spellbook.ArchiveOfAbyssalSecretsSpellbook;
import com.gametechbc.traveloptics.item.spellbook.ChroniclesOfTheFirelordSpellbook;
import com.gametechbc.traveloptics.item.spellbook.TheAccusedCodexSpellbook;
import com.gametechbc.traveloptics.item.talent_curios.AetherialDespairRingItem;
import com.gametechbc.traveloptics.item.talent_curios.AmuletOfSpectralShiftItem;
import com.gametechbc.traveloptics.item.talent_curios.AzureIgnitionBraceletItem;
import com.gametechbc.traveloptics.item.talent_curios.EnergyUnboundNecklaceItem;
import com.gametechbc.traveloptics.item.talent_curios.FirestormRingItem;
import com.gametechbc.traveloptics.item.talent_curios.NighstalkersBandItem;
import com.gametechbc.traveloptics.item.talent_curios.SigilOfTheSpiderSorcererItem;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.item.curios.CurioBaseItem;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import java.util.Collection;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TOItems {
   private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, "traveloptics");
   public static final DeferredHolder<Item, Item> VERDANT_SPELLWEAVE_INGOT = ITEMS.register("verdant_spellweave_ingot", VerdantSpellweaveItem::new);
   public static final DeferredHolder<Item, Item> CRIMSON_SPELLWEAVE_INGOT = ITEMS.register("crimson_spellweave_ingot", CrimsonSpellweaveItem::new);
   public static final DeferredHolder<Item, Item> ELDRITCH_SPELLWEAVE_INGOT = ITEMS.register("eldritch_spellweave_ingot", EldritchSpellweaveIngotItem::new);
   public static final DeferredHolder<Item, Item> ABYSSAL_SPELLWEAVE_INGOT = ITEMS.register("abyssal_spellweave_ingot", AbyssalSpellweaveIngotItem::new);
   public static final DeferredHolder<Item, Item> EVOKATED_SPELLWEAVE_INGOT = ITEMS.register("evokated_spellweave_ingot", EvokatedSpellweaveIngotItem::new);
   public static final DeferredHolder<Item, Item> CELESTIAL_SPELLWEAVE_INGOT = ITEMS.register("celestial_spellweave_ingot", CelestialSpellweaveIngotItem::new);
   public static final DeferredHolder<Item, Item> PYRO_SPELLWEAVE_INGOT = ITEMS.register("pyro_spellweave_ingot", PyroSpellweaveIngotItem::new);
   public static final DeferredHolder<Item, Item> CRYO_SPELLWEAVE_INGOT = ITEMS.register("cryo_spellweave_ingot", CryoSpellweaveIngotItem::new);
   public static final DeferredHolder<Item, Item> LIGHTNING_SPELLWEAVE_INGOT = ITEMS.register("lightning_spellweave_ingot", LightningSpellweaveIngotItem::new);
   public static final DeferredHolder<Item, Item> VOID_SPELLWEAVE_INGOT = ITEMS.register("void_spellweave_ingot", VoidSpellweaveItem::new);
   public static final DeferredHolder<Item, Item> ABYSSAL_TENTACLE = ITEMS.register("abyssal_tentacle", AbyssalTentacleItem::new);
   public static final DeferredHolder<Item, Item> DESERT_JEWEL_FRAGMENT = ITEMS.register("desert_jewel_fragment", DesertJewelFragmentItem::new);
   public static final DeferredHolder<Item, Item> FLAME_TEMPERED_HANDGUARD = ITEMS.register("flame_tempered_handguard", FlameTemperedHandguardItem::new);
   public static final DeferredHolder<Item, Item> KYREXI_CLAWS = ITEMS.register("kyrexi_claws", KyrexiClawsItem::new);
   public static final DeferredHolder<Item, Item> LAST_GLOW = ITEMS.register("last_glow", LastGlowItem::new);
   public static final DeferredHolder<Item, Item> ECHO_WINGLET = ITEMS.register("echo_winglet", EchoWingletItem::new);
   public static final DeferredHolder<Item, Item> AQUA_SCROLL_DUMMY = ITEMS.register("aqua_scroll_dummy", AquaScrollDummyItem::new);
   public static final DeferredHolder<Item, Item> ARCHIVE_OF_ABYSSAL_SECRETS = ITEMS.register(
      "archive_of_abyssal_secrets", ArchiveOfAbyssalSecretsSpellbook::new
   );
   public static final DeferredHolder<Item, Item> CHRONICLES_OF_THE_FIRELORD = ITEMS.register(
      "chronicles_of_the_firelord", ChroniclesOfTheFirelordSpellbook::new
   );
   public static final DeferredHolder<Item, Item> THE_ACCUSED_CODEX = ITEMS.register("the_accused_codex", TheAccusedCodexSpellbook::new);
   public static final DeferredHolder<Item, Item> SHELLBOUND = ITEMS.register(
      "shellbound",
      () -> new SpellBook((Integer)SpellsConfig.shellboundSpellSlots.get())
         .withSpellbookAttributes(
            new AttributeContainer[]{
               new AttributeContainer(AttributeRegistry.MAX_MANA, (Double)SpellsConfig.shellboundMaxMana.get(), Operation.ADD_VALUE),
               new AttributeContainer(
                  AttributeRegistry.NATURE_SPELL_POWER, (Double)SpellsConfig.shellboundNatureSpellPower.get(), Operation.ADD_MULTIPLIED_BASE
               ),
               new AttributeContainer(
                  AttributeRegistry.COOLDOWN_REDUCTION, (Double)SpellsConfig.shellboundCooldownReduction.get(), Operation.ADD_MULTIPLIED_BASE
               )
            }
         )
   );
   public static final Supplier<CurioBaseItem> AETHERIAL_DESPAIR_RING = ITEMS.register("aetherial_despair_ring", AetherialDespairRingItem::new);
   public static final Supplier<CurioBaseItem> AMULET_OF_SPECTRAL_SHIFT = ITEMS.register("amulet_of_spectral_shift", AmuletOfSpectralShiftItem::new);
   public static final Supplier<CurioBaseItem> NIGHTSTALKERS_BAND = ITEMS.register("nightstalkers_band", NighstalkersBandItem::new);
   public static final Supplier<CurioBaseItem> AZURE_IGNITION_BRACELET = ITEMS.register("azure_ignition_bracelet", AzureIgnitionBraceletItem::new);
   public static final Supplier<CurioBaseItem> ENERGY_UNBOUND_NECKLACE = ITEMS.register("energy_unbound_necklace", EnergyUnboundNecklaceItem::new);
   public static final Supplier<CurioBaseItem> SIGIL_OF_THE_SPIDER_SORCERER = ITEMS.register("sigil_of_the_spider_sorcerer", SigilOfTheSpiderSorcererItem::new);
   public static final Supplier<CurioBaseItem> FIRESTORM_RING = ITEMS.register("firestorm_ring", FirestormRingItem::new);
   public static final DeferredHolder<Item, MechanizedWraithbladeItem> MECHANIZED_WRAITHBLADE = ITEMS.register(
      "mechanized_wraithblade", MechanizedWraithbladeItem::new
   );
   public static final DeferredHolder<Item, CursedWraithbladeItem> CURSED_WRAITHBLADE = ITEMS.register("cursed_wraithblade", CursedWraithbladeItem::new);
   public static final DeferredHolder<Item, InfernalDevastatorItem> INFERNAL_DEVASTATOR = ITEMS.register("infernal_devastator", InfernalDevastatorItem::new);
   public static final DeferredHolder<Item, InfernalDevastatorLevelOneItem> INFERNAL_DEVASTATOR_LEVEL_ONE = ITEMS.register(
      "infernal_devastator_level_one", InfernalDevastatorLevelOneItem::new
   );
   public static final DeferredHolder<Item, InfernalDevastatorLevelTwoItem> INFERNAL_DEVASTATOR_LEVEL_TWO = ITEMS.register(
      "infernal_devastator_level_two", InfernalDevastatorLevelTwoItem::new
   );
   public static final DeferredHolder<Item, InfernalDevastatorLevelThreeItem> INFERNAL_DEVASTATOR_LEVEL_THREE = ITEMS.register(
      "infernal_devastator_level_three", InfernalDevastatorLevelThreeItem::new
   );

   public TOItems() {
      super();
   }

   public static void register(IEventBus eventBus) {
      ITEMS.register(eventBus);
   }

   public static Collection<DeferredHolder<Item, ? extends Item>> getTOItems() {
      return ITEMS.getEntries();
   }
}
