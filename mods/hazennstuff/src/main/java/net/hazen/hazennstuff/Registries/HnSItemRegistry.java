package net.hazen.hazennstuff.Registries;

import io.redspace.ironsspellbooks.item.UpgradeOrbItem;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import io.redspace.ironsspellbooks.render.CinderousRarity;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.util.Collection;
import net.hazen.hazennstuff.Item.Armor.AzureLib.ArbitriumRobes.ArbitriumRobesArmorItem;
import net.hazen.hazennstuff.Item.Armor.AzureLib.ArbitriumRobes.ArbitriumRobesElytraArmorItem;
import net.hazen.hazennstuff.Item.Armor.AzureLib.ArbitriumRobes.Ascended.AscendedArbitriumRobesArmorItem;
import net.hazen.hazennstuff.Item.Armor.AzureLib.ArbitriumRobes.Ascended.AscendedArbitriumRobesElytraArmorItem;
import net.hazen.hazennstuff.Item.Armor.AzureLib.Blazeborne.BlazeborneArmorItem;
import net.hazen.hazennstuff.Item.Armor.AzureLib.CreakingSorcerer.CreakingSorcererArmorItem;
import net.hazen.hazennstuff.Item.Armor.AzureLib.DarkRitualTemplar.DarkRitualTemplarArmorItem;
import net.hazen.hazennstuff.Item.Armor.AzureLib.EnderDragon.EnderDragonArmorItem;
import net.hazen.hazennstuff.Item.Armor.AzureLib.FireblossomBattlemage.FireblossomBattlemageArmorItem;
import net.hazen.hazennstuff.Item.Armor.AzureLib.FireblossomBattlemage.FireblossomBattlemageCrownedArmorItem;
import net.hazen.hazennstuff.Item.Armor.AzureLib.FireblossomBattlemage.FireblossomBattlemageHelmetArmorItem;
import net.hazen.hazennstuff.Item.Armor.AzureLib.GabrielULTRAKILL.GabrielArmorItem;
import net.hazen.hazennstuff.Item.Armor.AzureLib.GabrielULTRAKILL.GabrielElytraArmorItem;
import net.hazen.hazennstuff.Item.Armor.AzureLib.Infestation.InfestationArmorItem;
import net.hazen.hazennstuff.Item.Armor.AzureLib.LemonGod.LemonGodArmorItem;
import net.hazen.hazennstuff.Item.Armor.AzureLib.LemonGod.Ascended.AscendedLemonGodArmorItem;
import net.hazen.hazennstuff.Item.Armor.AzureLib.SLCCat.SLCCatArmorItem;
import net.hazen.hazennstuff.Item.Armor.AzureLib.Seraph.SeraphArmorItem;
import net.hazen.hazennstuff.Item.Armor.AzureLib.SoulFlame.SoulFlameArmorItem;
import net.hazen.hazennstuff.Item.Armor.AzureLib.SupremeWitch.SupremeWitchArmorItem;
import net.hazen.hazennstuff.Item.Armor.AzureLib.TheWither.TheWitherArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.AbberantPredator.AbberantPredatorArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.AlchemistSupreme.AlchemistSupremeArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.ArbitriumRobes.GeckolibArbitriumRobesArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.ArbitriumRobes.GeckolibArbitriumRobesElytraItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.ArbitriumRobes.Ascended.GeckolibAscendedArbitriumRobesArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.ArbitriumRobes.Ascended.GeckolibAscendedArbitriumRobesElytraItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.Atlas.AtlasArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.BishopOfDeceitArmor.BishopOfDeceitArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.Blazeborne.GeckolibBlazeborneArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.BountyHunter.BountyHunterArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.Calamitas.CalamitasArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.Calamitas.CalamitasChestplateArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.ChargedScourge.GeckolibChargedScourgeArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.Chlorophyte.ChlorophyteArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.Chlorophyte.Headgear.ChlorophyteHeadgearArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.Chlorophyte.Helmet.ChlorophyteHelmetArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.Chlorophyte.Mask.ChlorophyteMaskArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.CreakingSorcerer.GeckolibCreakingSorcererArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.CryogenicRuler.CryogenicRulerArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.CrystalArachnid.CrystalArachnidArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.DarkRitualTemplar.GeckolibDarkRitualTemplarArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.DreadsteelKnight.DreadsteelKnightArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.EnderDragon.GeckolibEnderDragonArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.FireblossomBattlemage.GeckolibFireblossomBattlemageArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.FireblossomBattlemage.GeckolibFireblossomBattlemageCrownedArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.FireblossomBattlemage.GeckolibFireblossomBattlemageHelmetArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.FleshMass.FleshMassArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.Frieren.FrierenArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.FrostbiteHunter.FrostbiteHunterArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.FrostbiteKnight.FrostbiteKnightArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.GabrielULTRAKILL.GeckolibGabrielArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.GabrielULTRAKILL.GeckolibGabrielElytraItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.GarmentsOfTheFirstFlamebearer.GarmentsOfTheFirstFlamebearerArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.GarmentsOfTheFirstFlamebearer.GarmentsOfTheFirstFlamebearerChestplateArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.GarmentsOfTheFirstFlamebearer.SoulMode.GarmentsOfTheFirstFlamebearerSoulArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.GarmentsOfTheFirstFlamebearer.SoulMode.GarmentsOfTheFirstFlamebearerSoulChestplateArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.GiornoGiovanna.GiornoGiovannaArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.Hazel.HazelArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.Hazel.HazelHOLYMOLYArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.HertaPuppet.HertaPuppetArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.Infestation.GeckolibInfestationArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.Iron431.Iron431ArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.Iron431.Ascended.AscendedIron431ArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.Legionnaire.OldLegionnaireArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.LegionnaireCommander.LegionnaireCommanderArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.LegionnaireRuler.LegionnaireRulerArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.LegionnaireRuler.Soul.SoulLegionnaireRulerArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.LemonGod.GeckolibLemonGodArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.LemonGod.Ascended.GeckolibAscendedLemonGodArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.MageHunter.MageHunterArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.Maverick.MaverickArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.Miner.MinerArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.MithrilBattlemage.MithrilBattlemageArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.MothicWitch.MothicWitchArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.MothicWitch.NerfedMothicWitchArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.MyceliumGuardian.MyceliumGuardianArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.NamelessOneArmor.NamelessOneArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.NecromancerArmor.NecromancerArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.Neru.AkitaNeruArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.PermafrostPrince.PermafrostPrinceArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.ProjectSekai.ProjectSekaiArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.PyriumArmor.PyriumArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.PyriumBattlemage.PyriumBattlemageArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.PyromancerBrute.PyromancerBruteArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.RottenGirl.RottenGirlArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.SacredRobes.SacredRobesArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.SacredRobes.Ascended.AscendedSacredRobesArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.Seraph.GeckolibSeraphArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.ShadowScale.ShadowScaleArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.ShadowWalkerEnderman.ShadowwalkerEndermanArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.ShadowWalkerEnderman.ShadowwalkerEndermanBlindfoldItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.ShadowWalkerEnderman.ShadowwalkerEndermanHoodItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.SoulFlame.GeckolibSoulFlameArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.SpectralSpelunker.SpectralSpelunkerArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.SupremeWitch.GeckolibSupremeWitchArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.SynthesizerV.SynthesizerVArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.TheWither.GeckolibTheWitherArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.ThunderProwler.ThunderProwlerArmorItem;
import net.hazen.hazennstuff.Item.Armor.Geckolib.Utau.UtauArmorItem;
import net.hazen.hazennstuff.Item.Block.HnSBlocks;
import net.hazen.hazennstuff.Item.Curios.Crystals.LifeCrystals.SacredCurio;
import net.hazen.hazennstuff.Item.Curios.Crystals.LifeCrystals.ShatteredCurio;
import net.hazen.hazennstuff.Item.Curios.Crystals.LifeCrystals.SingularityCurio;
import net.hazen.hazennstuff.Item.Curios.Crystals.LifeCrystals.StrengthenedCurio;
import net.hazen.hazennstuff.Item.Curios.Crystals.ManaCrystals.RadianceCurio;
import net.hazen.hazennstuff.Item.Curios.Crystals.ManaCrystals.RefinedCurio;
import net.hazen.hazennstuff.Item.Curios.Crystals.ManaCrystals.ReinforcedCurio;
import net.hazen.hazennstuff.Item.Curios.Crystals.ManaCrystals.RupturedCurio;
import net.hazen.hazennstuff.Item.Curios.Crystals.SpiritCrystals.AbominationCurio;
import net.hazen.hazennstuff.Item.Curios.Crystals.SpiritCrystals.AbsoluteCurio;
import net.hazen.hazennstuff.Item.Curios.Crystals.SpiritCrystals.AbstractCurio;
import net.hazen.hazennstuff.Item.Curios.Crystals.SpiritCrystals.AdvancedCurio;
import net.hazen.hazennstuff.Item.Curios.Misc.LegionnaireCurios;
import net.hazen.hazennstuff.Item.Curios.Misc.PendantOfHarmonyCurio;
import net.hazen.hazennstuff.Item.Curios.Rings.RingOfEfficiencyCurio;
import net.hazen.hazennstuff.Item.Curios.Sheaths.GalvanizedSheath.GalvanizedSheath;
import net.hazen.hazennstuff.Item.Curios.Sheaths.ScrollSheath.ScrollSheath;
import net.hazen.hazennstuff.Item.Curios.Spellbooks.GoldenShowerSpellbook;
import net.hazen.hazennstuff.Item.Curios.Spellbooks.EnergizedCoreSpellbook.EnergizedCoreSpellbook;
import net.hazen.hazennstuff.Item.Curios.Wings.ArbitriumWings.ArbitriumWings;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSFoodProperties;
import net.hazen.hazennstuff.Item.Staves.BlossomOfTheEnchantress.BlossomOfTheEnchantress;
import net.hazen.hazennstuff.Item.Staves.DivineScepter.DivineScepter;
import net.hazen.hazennstuff.Item.Staves.EnderconicScepter.EnderconicScepter;
import net.hazen.hazennstuff.Item.Staves.Frieren.FrierenStaff;
import net.hazen.hazennstuff.Item.Staves.GrimoireStaff.GrimoireStaff;
import net.hazen.hazennstuff.Item.Staves.InsaniaAeternus.InsaniaAeternus;
import net.hazen.hazennstuff.Item.Staves.RodOfDiscord.RodOfDiscord;
import net.hazen.hazennstuff.Item.Staves.SoulcallingScepter.SoulcallingScepter;
import net.hazen.hazennstuff.Item.Staves.WhiteLilyStaff.WhiteLilyStaff;
import net.hazen.hazennstuff.Item.Staves.WisewoodCane.WisewoodCane;
import net.hazen.hazennstuff.Item.UpgradeOrbs.Concentrated.Fire.ConcentratedFireUpgradeOrb;
import net.hazen.hazennstuff.Item.UpgradeOrbs.Concentrated.Ice.ConcentratedIceUpgradeOrb;
import net.hazen.hazennstuff.Item.Util.PickaxeAxe.PickaxeAxeItem;
import net.hazen.hazennstuff.Item.Util.PickaxeAxe.DivineGold.PickaxeAxeDivineGoldItem;
import net.hazen.hazennstuff.Item.Util.SpectralPickaxe.SpectralPickaxeItem;
import net.hazen.hazennstuff.Item.Weapons.Ascended.Devastator.Dormant.Devastator;
import net.hazen.hazennstuff.Item.Weapons.Ascended.DraconicSplitter.DraconicSplitterItem;
import net.hazen.hazennstuff.Item.Weapons.Ascended.IonicSplitter.Dormant.IonicSplitterItem;
import net.hazen.hazennstuff.Item.Weapons.Ascended.IonicSplitter.T1.IonicSplitterT1Item;
import net.hazen.hazennstuff.Item.Weapons.Ascended.IonicSplitter.T2.IonicSplitterT2Item;
import net.hazen.hazennstuff.Item.Weapons.Ascended.IonicSplitter.T3.IonicSplitterT3Item;
import net.hazen.hazennstuff.Item.Weapons.Ascended.OFortuna.Dormant.OFortuna;
import net.hazen.hazennstuff.Item.Weapons.Ascended.Provocation.Dormant.Provocation;
import net.hazen.hazennstuff.Item.Weapons.Ascended.RavensBane.RavensBaneItem;
import net.hazen.hazennstuff.Item.Weapons.Ascended.Umbranova.Dormant.Umbranova;
import net.hazen.hazennstuff.Item.Weapons.Generic.AncientWarriorsAxe.AncientWarriorsAxeItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.Beongae.BeongaeItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.BladeOfGrass.BladeOfGrassItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.BountifulHarvest.BountifulHarvestItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.Dawnmaker.DawnmakerItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.DivineGreatsword.DivineGreatswordItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.Excalibur.HazenStyle.HazensExcaliburItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.Excalibur.ISSxTerraria.ISSExcaliburItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.Excalibur.Terraria.ExcaliburItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.FireblossomRapier.FireblossomRapierItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.Firebrand.FirebrandItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.FrostburnDagger.FrostburnDaggerItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.HammerOfJustice.HammerOfJusticeItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.Hammush.HammushItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.IcePike.IcePikeItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.LegionnaireWarlockAxe.LegionnaireWarlockAxeItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.LightsBane.LightsBaneItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.MageBane.MageBaneItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.Malice.MaliceItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.Meowmere.MeowmereItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.Muramasa.MuramasaItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.NightsEdge.NightsEdgeItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.ObsidianClaymore.ObsidianClaymoreItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.Skyscorcher.SkyscorcherItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.Spectrum.SpectrumItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.Starfury.StarfuryItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.Terraprisma.TerraprismaItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.TheDevourer.TheDevourerItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.ThornChakram.ThornChakramItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.TrueNightsEdge.TrueNightsEdgeItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.VampireKnives.VampireKnivesItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.Volcano.VolcanoItem;
import net.hazen.hazennstuff.Item.Weapons.Generic.WarFlamingLance.WarFlamingLanceItem;
import net.hazen.hazennstuff.Rarity.HnSRarities;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

public class HnSItemRegistry {
   public static final Items ITEMS = DeferredRegister.createItems("hazennstuff");
   public static final DeferredHolder<Item, Item> HAZEL_STATUE = ITEMS.register(
      "hazel_statue_model", () -> new BlockItem((Block)HnSBlocks.HAZEL_STATUE.get(), new Properties())
   );
   public static final DeferredHolder<Item, Item> RADIANCE_UPGRADE_ORB = ITEMS.register(
      "radiance_upgrade_orb",
      () -> new UpgradeOrbItem(
         ItemPropertiesHelper.material().rarity(Rarity.UNCOMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, HnSUpgradeOrbTypeRegistry.RADIANCE_SPELL_POWER)
      )
   );
   public static final DeferredHolder<Item, Item> SHADOW_UPGRADE_ORB = ITEMS.register(
      "shadow_upgrade_orb",
      () -> new UpgradeOrbItem(
         ItemPropertiesHelper.material().rarity(Rarity.UNCOMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, HnSUpgradeOrbTypeRegistry.SHADOW_SPELL_POWER)
      )
   );
   public static final DeferredHolder<Item, Item> COSMIC_UPGRADE_ORB = ITEMS.register(
      "cosmic_upgrade_orb",
      () -> new UpgradeOrbItem(
         ItemPropertiesHelper.material().rarity(Rarity.UNCOMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, HnSUpgradeOrbTypeRegistry.COSMIC_SPELL_POWER)
      )
   );
   public static final DeferredHolder<Item, Item> ELDRITCH_UPGRADE_ORB = ITEMS.register(
      "eldritch_upgrade_orb",
      () -> new UpgradeOrbItem(
         ItemPropertiesHelper.material().rarity(Rarity.UNCOMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, HnSUpgradeOrbTypeRegistry.ELDRITCH_SPELL_POWER)
      )
   );
   public static final DeferredHolder<Item, Item> MELEE_UPGRADE_ORB = ITEMS.register(
      "melee_upgrade_orb",
      () -> new UpgradeOrbItem(
         ItemPropertiesHelper.material().rarity(Rarity.UNCOMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, HnSUpgradeOrbTypeRegistry.MELEE_POWER)
      )
   );
   public static final DeferredHolder<Item, Item> HEALTH_UPGRADE_ORB = ITEMS.register(
      "health_upgrade_orb",
      () -> new UpgradeOrbItem(
         ItemPropertiesHelper.material().rarity(Rarity.UNCOMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, HnSUpgradeOrbTypeRegistry.HEALTH_POWER)
      )
   );
   public static final DeferredHolder<Item, Item> ARCHERY_UPGRADE_ORB = ITEMS.register(
      "archery_upgrade_orb",
      () -> new UpgradeOrbItem(
         ItemPropertiesHelper.material().rarity(Rarity.UNCOMMON).component(ComponentRegistry.UPGRADE_ORB_TYPE, HnSUpgradeOrbTypeRegistry.ARCHERY_POWER)
      )
   );
   public static final DeferredHolder<Item, Item> ENHANCED_FIRE_UPGRADE_ORB = ITEMS.register("concentrated_upgrade_orb_fire", ConcentratedFireUpgradeOrb::new);
   public static final DeferredHolder<Item, Item> ENHANCED_ICE_UPGRADE_ORB = ITEMS.register("concentrated_upgrade_orb_ice", ConcentratedIceUpgradeOrb::new);
   public static final DeferredHolder<Item, Item> MELEE_RUNE = ITEMS.register("melee_rune", () -> new Item(ItemPropertiesHelper.material()));
   public static final DeferredHolder<Item, Item> ARCHERY_RUNE = ITEMS.register("archery_rune", () -> new Item(ItemPropertiesHelper.material()));
   public static final DeferredHolder<Item, Item> HEALTH_RUNE = ITEMS.register("health_rune", () -> new Item(ItemPropertiesHelper.material()));
   public static final DeferredHolder<Item, Item> SHADOW_RUNE = ITEMS.register("shadow_rune", () -> new Item(ItemPropertiesHelper.material()));
   public static final DeferredHolder<Item, Item> RADIANCE_RUNE = ITEMS.register("radiance_rune", () -> new Item(ItemPropertiesHelper.material()));
   public static final DeferredHolder<Item, Item> COSMIC_RUNE = ITEMS.register("cosmic_rune", () -> new Item(ItemPropertiesHelper.material()));
   public static final DeferredHolder<Item, Item> ELDRITCH_RUNE = ITEMS.register("eldritch_rune", () -> new Item(ItemPropertiesHelper.material()));
   public static final DeferredItem<Item> DIVINE_MOLD = ITEMS.register(
      "divine_mold", () -> new Item(new Properties().rarity(Rarity.EPIC).fireResistant().component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true))
   );
   public static final DeferredItem<Item> FLAMING_TEMPLATE = ITEMS.register(
      "flaming_template", () -> new Item(new Properties().rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant())
   );
   public static final DeferredItem<Item> SPIDER_FANG = ITEMS.register("spider_fang", () -> new Item(new Properties().rarity(Rarity.RARE)));
   public static final DeferredItem<Item> STORM_WEAVE = ITEMS.register("storm_weave", () -> new Item(new Properties().rarity(Rarity.RARE)));
   public static final DeferredItem<Item> VOLT_CORE = ITEMS.register("volt_core", () -> new Item(new Properties().rarity(Rarity.RARE)));
   public static final DeferredItem<Item> PRISMATIC_SHARD = ITEMS.register("prismatic_shard", () -> new Item(new Properties().rarity(Rarity.RARE)));
   public static final DeferredItem<Item> EXCALIBUR_FRAGMENT = ITEMS.register("excalibur_fragment", () -> new Item(new Properties().rarity(Rarity.EPIC)));
   public static final DeferredItem<Item> DEUS_ESSENCE = ITEMS.register(
      "deus_essence", () -> new Item(new Properties().rarity(Rarity.EPIC).fireResistant().component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true))
   );
   public static final DeferredItem<Item> CATALYST = ITEMS.register(
      "catalyst", () -> new Item(new Properties().rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant())
   );
   public static final DeferredItem<Item> RUNESTONE_FRAGMENTS = ITEMS.register("runestone_fragments", () -> new Item(new Properties().rarity(Rarity.UNCOMMON)));
   public static final DeferredItem<Item> PERMAFROST_FRAGMENT = ITEMS.register("permafrost_fragment", () -> new Item(new Properties().rarity(Rarity.RARE)));
   public static final DeferredItem<Item> CHARRED_BONES = ITEMS.register("charred_bones", () -> new Item(new Properties()));
   public static final DeferredItem<Item> OVERGROWN_BONE = ITEMS.register("overgrown_bone", () -> new Item(new Properties()));
   public static final DeferredItem<Item> GLOWING_MUSHROOM = ITEMS.register("glowing_mushroom", () -> new Item(new Properties().rarity(Rarity.UNCOMMON)));
   public static final DeferredItem<Item> SHADOW_SCALE = ITEMS.register("shadow_scale", () -> new Item(new Properties().rarity(Rarity.RARE)));
   public static final DeferredItem<Item> CLOTH_OF_THE_FLAMEBEARER = ITEMS.register(
      "cloth_of_the_flamebearer", () -> new Item(new Properties().rarity(Rarity.EPIC).fireResistant())
   );
   public static final DeferredItem<Item> LEMON = ITEMS.register(
      "lemon", () -> new Item(new Properties().food(HnSFoodProperties.LEMON).rarity(Rarity.UNCOMMON))
   );
   public static final DeferredItem<Item> DIVINE_LEMON = ITEMS.register(
      "divine_lemon",
      () -> new Item(new Properties().food(HnSFoodProperties.DIVINE_LEMON).rarity(Rarity.EPIC).component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true))
   );
   public static final DeferredItem<Item> FRIED_ICE_SPIDER_EGG = ITEMS.register(
      "fried_ice_spider_egg", () -> new Item(new Properties().food(HnSFoodProperties.FRIED_ICE_SPIDER_EGG).rarity((Rarity)HnSRarities.ICE_RARITY.getValue()))
   );
   public static final DeferredItem<Item> DREADSTONE = ITEMS.register("dreadstone", () -> new Item(new Properties().rarity(Rarity.RARE)));
   public static final DeferredItem<Item> DREADSTEEL_INGOT = ITEMS.register(
      "dreadsteel_ingot", () -> new Item(new Properties().rarity(Rarity.RARE).fireResistant())
   );
   public static final DeferredItem<Item> CRUDE_METAL = ITEMS.register("crude_metal", () -> new Item(new Properties().rarity(Rarity.RARE).fireResistant()));
   public static final DeferredItem<Item> STEEL_INGOT = ITEMS.register("steel_ingot", () -> new Item(new Properties().rarity(Rarity.RARE).fireResistant()));
   public static final DeferredItem<Item> STEEL_NUGGET = ITEMS.register("steel_nugget", () -> new Item(new Properties().rarity(Rarity.RARE).fireResistant()));
   public static final DeferredItem<Item> SOLAR_CORE = ITEMS.register("solar_core", () -> new Item(new Properties().rarity(Rarity.RARE).fireResistant()));
   public static final DeferredItem<Item> CHLOROPHYTE_INGOT = ITEMS.register(
      "chlorophyte_ingot", () -> new Item(new Properties().rarity((Rarity)HnSRarities.NATURE_RARITY.getValue()).fireResistant())
   );
   public static final DeferredItem<Item> CHLOROPHYTE_CHUNK = ITEMS.register(
      "chlorophyte_chunk", () -> new Item(new Properties().rarity((Rarity)HnSRarities.NATURE_RARITY.getValue()))
   );
   public static final DeferredItem<Item> HALLOWED_INGOT = ITEMS.register(
      "hallowed_ingot", () -> new Item(new Properties().rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()))
   );
   public static final DeferredItem<Item> DEMONITE_INGOT = ITEMS.register(
      "demonite_ingot", () -> new Item(new Properties().rarity((Rarity)HnSRarities.SHADOW_RARITY.getValue()))
   );
   public static final DeferredItem<Item> SHADESTONE = ITEMS.register(
      "shadestone", () -> new Item(new Properties().rarity((Rarity)HnSRarities.SHADOW_RARITY.getValue()))
   );
   public static final DeferredItem<Item> ROSE_QUARTZ = ITEMS.register("rose_quartz", () -> new Item(new Properties().rarity(Rarity.UNCOMMON)));
   public static final DeferredItem<Item> ROSE_GOLD_INGOT = ITEMS.register("rose_gold_ingot", () -> new Item(new Properties().rarity(Rarity.UNCOMMON)));
   public static final DeferredItem<Item> ZENALITE_INGOT = ITEMS.register(
      "zenalite_ingot", () -> new Item(new Properties().rarity(Rarity.EPIC).fireResistant())
   );
   public static final DeferredItem<Item> RAW_ZENALITE = ITEMS.register("raw_zenalite", () -> new Item(new Properties().rarity(Rarity.RARE).fireResistant()));
   public static final DeferredItem<Item> STARKISSED_ZENALITE = ITEMS.register(
      "starkissed_zenalite", () -> new Item(new Properties().component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true).rarity(Rarity.EPIC).fireResistant())
   );
   public static final DeferredItem<Item> PYRIUM_NUGGET = ITEMS.register(
      "pyrium_nugget", () -> new Item(ItemPropertiesHelper.material().rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue()).fireResistant())
   );
   public static final DeferredItem<Item> SILVER_SCRAPS = ITEMS.register("silver_scraps", () -> new Item(new Properties().rarity(Rarity.UNCOMMON)));
   public static final DeferredItem<Item> STARDUST = ITEMS.register(
      "stardust", () -> new Item(new Properties().rarity((Rarity)HnSRarities.COSMIC_RARITY.getValue()))
   );
   public static final DeferredHolder<Item, Item> FIREBLOSSOM_RAPIER = ITEMS.register("fireblossom_rapier", FireblossomRapierItem::new);
   public static final DeferredHolder<Item, Item> DRACONIC_SPLITTER = ITEMS.register("draconic_splitter", DraconicSplitterItem::new);
   public static final DeferredHolder<Item, Item> FIREBRAND = ITEMS.register("firebrand", FirebrandItem::new);
   public static final DeferredHolder<Item, Item> RAVENS_BANE = ITEMS.register("ravens_bane", RavensBaneItem::new);
   public static final DeferredHolder<Item, Item> WARFLAMING_LANCE = ITEMS.register("war_flaming_lance", WarFlamingLanceItem::new);
   public static final DeferredHolder<Item, Item> VOLCANO = ITEMS.register("volcano", VolcanoItem::new);
   public static final DeferredHolder<Item, Item> ICE_PIKE = ITEMS.register("ice_pike", IcePikeItem::new);
   public static final DeferredHolder<Item, Item> FROSTBURN_DAGGER = ITEMS.register("frostburn_dagger", FrostburnDaggerItem::new);
   public static final DeferredHolder<Item, Item> BEONGAE = ITEMS.register("beongae", BeongaeItem::new);
   public static final DeferredHolder<Item, Item> SKYSCORCHER = ITEMS.register("skyscorcher", SkyscorcherItem::new);
   public static final DeferredHolder<Item, Item> IONIC_SPLITTER_DORMANT = ITEMS.register("ionic_splitter_dormant", IonicSplitterItem::new);
   public static final DeferredHolder<Item, Item> IONIC_SPLITTER_T1 = ITEMS.register("ionic_splitter_t1", IonicSplitterT1Item::new);
   public static final DeferredHolder<Item, Item> IONIC_SPLITTER_T2 = ITEMS.register("ionic_splitter_t2", IonicSplitterT2Item::new);
   public static final DeferredHolder<Item, Item> IONIC_SPLITTER_T3 = ITEMS.register("ionic_splitter_t3", IonicSplitterT3Item::new);
   public static final DeferredHolder<Item, Item> BOUNTIFUL_HARVEST = ITEMS.register("bountiful_harvest", BountifulHarvestItem::new);
   public static final DeferredHolder<Item, Item> THORN_CHAKRAM = ITEMS.register("thorn_chakram", ThornChakramItem::new);
   public static final DeferredHolder<Item, Item> BLADE_OF_GRASS = ITEMS.register("blade_of_grass", BladeOfGrassItem::new);
   public static final DeferredHolder<Item, Item> O_FORTUNA_DORMANT = ITEMS.register("o_fortuna_dormant", OFortuna::new);
   public static final DeferredHolder<Item, Item> VAMPIRE_KNIVES = ITEMS.register("vampire_knives", VampireKnivesItem::new);
   public static final DeferredHolder<Item, Item> DEVASTATOR_DORMANT = ITEMS.register("devastator_dormant", Devastator::new);
   public static final DeferredHolder<Item, Item> OBSIDIAN_CLAYMORE = ITEMS.register("obsidian_claymore", ObsidianClaymoreItem::new);
   public static final DeferredHolder<Item, Item> STARFURY = ITEMS.register("starfury", StarfuryItem::new);
   public static final DeferredHolder<Item, Item> UMBRANOVA_DORMANT = ITEMS.register("umbranova_dormant", Umbranova::new);
   public static final DeferredHolder<Item, Item> SPECTRUM = ITEMS.register("spectrum", SpectrumItem::new);
   public static final DeferredHolder<Item, Item> MEOWMERE = ITEMS.register("meowmere", MeowmereItem::new);
   public static final DeferredHolder<Item, Item> HAMMUSH = ITEMS.register("hammush", HammushItem::new);
   public static final DeferredHolder<Item, Item> TERRAPRISMA = ITEMS.register("terraprisma", TerraprismaItem::new);
   public static final DeferredHolder<Item, Item> LIGHTS_BANE = ITEMS.register("lights_bane", LightsBaneItem::new);
   public static final DeferredHolder<Item, Item> NIGHTS_EDGE = ITEMS.register("nights_edge", NightsEdgeItem::new);
   public static final DeferredHolder<Item, Item> TRUE_NIGHTS_EDGE = ITEMS.register("true_nights_edge", TrueNightsEdgeItem::new);
   public static final DeferredHolder<Item, Item> HAMMER_OF_JUSTICE = ITEMS.register("hammer_of_justice", HammerOfJusticeItem::new);
   public static final DeferredHolder<Item, Item> DIVINE_GREATSWORD = ITEMS.register("divine_greatsword", DivineGreatswordItem::new);
   public static final DeferredHolder<Item, Item> HAZENS_EXCALIBUR = ITEMS.register("hazens_excalibur", HazensExcaliburItem::new);
   public static final DeferredHolder<Item, Item> EXCALIBUR = ITEMS.register("excalibur", ExcaliburItem::new);
   public static final DeferredHolder<Item, Item> ISS_EXCALIBUR = ITEMS.register("excalibur_divine_gold", ISSExcaliburItem::new);
   public static final DeferredHolder<Item, Item> ANCIENT_WARRIORS_AXE = ITEMS.register("ancient_warriors_axe", AncientWarriorsAxeItem::new);
   public static final DeferredHolder<Item, Item> MALICE = ITEMS.register("malice", MaliceItem::new);
   public static final DeferredHolder<Item, Item> PROVOCATION_DORMANT = ITEMS.register("provocation_dormant", Provocation::new);
   public static final DeferredHolder<Item, Item> THE_DEVOURER = ITEMS.register("the_devourer", TheDevourerItem::new);
   public static final DeferredHolder<Item, Item> MAGE_BANE = ITEMS.register("mage_bane", MageBaneItem::new);
   public static final DeferredHolder<Item, Item> DAWNMAKER = ITEMS.register("dawnmaker", DawnmakerItem::new);
   public static final DeferredHolder<Item, Item> MURAMASA = ITEMS.register("muramasa", MuramasaItem::new);
   public static final DeferredHolder<Item, Item> LEGIONNAIRE_WARLOCK_AXE = ITEMS.register("legionnaire_warlock_axe", LegionnaireWarlockAxeItem::new);
   public static final DeferredHolder<Item, Item> SOULCALLING_SCEPTER = ITEMS.register("soulcalling_scepter", SoulcallingScepter::new);
   public static final DeferredHolder<Item, Item> WHITE_LILY_STAFF = ITEMS.register("white_lily_staff", WhiteLilyStaff::new);
   public static final DeferredHolder<Item, Item> BLOSSOM_OF_THE_ENCHANTRESS = ITEMS.register("blossom_of_the_enchantress", BlossomOfTheEnchantress::new);
   public static final DeferredHolder<Item, Item> ENDERCONIC_SCEPTER = ITEMS.register("enderconic_scepter", EnderconicScepter::new);
   public static final DeferredHolder<Item, Item> GRIMOIRE_STAFF = ITEMS.register("grimoire_staff", GrimoireStaff::new);
   public static final DeferredHolder<Item, Item> DIVINE_SCEPTER = ITEMS.register("divine_scepter", DivineScepter::new);
   public static final DeferredHolder<Item, Item> ROD_OF_DISCORD = ITEMS.register("rod_of_discord", RodOfDiscord::new);
   public static final DeferredHolder<Item, Item> INSANIA_AETERNUS = ITEMS.register("insania_aeternus", InsaniaAeternus::new);
   public static final DeferredHolder<Item, Item> FRIEREN_STAFF = ITEMS.register("frieren_staff", FrierenStaff::new);
   public static final DeferredHolder<Item, Item> WISEWOOD_CANE = ITEMS.register("wisewood_cane", WisewoodCane::new);
   public static final DeferredHolder<Item, Item> SPECTRAL_PICKAXE = ITEMS.register("spectral_pickaxe", SpectralPickaxeItem::new);
   public static final DeferredHolder<Item, Item> PICKAXE_AXE = ITEMS.register("pickaxe_axe", PickaxeAxeItem::new);
   public static final DeferredHolder<Item, Item> PICKAXE_AXE_DIVINE_GOLD = ITEMS.register("pickaxe_axe_divine_gold", PickaxeAxeDivineGoldItem::new);
   public static final DeferredItem<RupturedCurio> RUPTURED = ITEMS.register("ruptured", RupturedCurio::new);
   public static final DeferredItem<RefinedCurio> REFINED = ITEMS.register("refined", RefinedCurio::new);
   public static final DeferredItem<ReinforcedCurio> REINFORCED = ITEMS.register("reinforced", ReinforcedCurio::new);
   public static final DeferredItem<RadianceCurio> RADIANCE = ITEMS.register("radiance", RadianceCurio::new);
   public static final DeferredItem<ShatteredCurio> SHATTERED = ITEMS.register("shattered", ShatteredCurio::new);
   public static final DeferredItem<SacredCurio> SACRED = ITEMS.register("sacred", SacredCurio::new);
   public static final DeferredItem<StrengthenedCurio> STRENGTHENED = ITEMS.register("strengthened", StrengthenedCurio::new);
   public static final DeferredItem<SingularityCurio> SINGULARITY = ITEMS.register("singularity", SingularityCurio::new);
   public static final DeferredItem<AbstractCurio> ABSTRACT = ITEMS.register("abstract", AbstractCurio::new);
   public static final DeferredItem<AdvancedCurio> ADVANCED = ITEMS.register("advanced", AdvancedCurio::new);
   public static final DeferredItem<AbominationCurio> ABOMINATION = ITEMS.register("abomination", AbominationCurio::new);
   public static final DeferredItem<AbsoluteCurio> ABSOLUTE = ITEMS.register("absolute", AbsoluteCurio::new);
   public static final DeferredItem<GoldenShowerSpellbook> GOLDEN_SHOWER_SPELLBOOK = ITEMS.register("golden_shower_spellbook", GoldenShowerSpellbook::new);
   public static final DeferredItem<EnergizedCoreSpellbook> ENERGIZED_CORE_SPELLBOOK = ITEMS.register("energized_core_spellbook", EnergizedCoreSpellbook::new);
   public static final DeferredItem<GalvanizedSheath> GALVANIZED_SHEATH = ITEMS.register("galvanized_sheath", GalvanizedSheath::new);
   public static final DeferredItem<ScrollSheath> SCROLL_SHEATH = ITEMS.register("scroll_sheath", ScrollSheath::new);
   public static final DeferredItem<ArbitriumWings> ARBITRIUM_WINGS = ITEMS.register("arbitrium_wings", ArbitriumWings::new);
   public static final DeferredItem<RingOfEfficiencyCurio> RING_OF_EFFICIENCY = ITEMS.register("ring_of_efficiency", RingOfEfficiencyCurio::new);
   public static final DeferredItem<PendantOfHarmonyCurio> PENDANT_OF_HARMONY = ITEMS.register("pendant_of_harmony", PendantOfHarmonyCurio::new);
   public static final DeferredItem<LegionnaireCurios> BLADE_OF_THE_LEGATE = ITEMS.register("blade_of_the_legate", LegionnaireCurios::new);
   public static final DeferredItem<LegionnaireCurios> THE_PREFECTS_RING = ITEMS.register("the_prefects_ring", LegionnaireCurios::new);
   public static final DeferredItem<LegionnaireCurios> THE_TRIBUNES_MEDALLION = ITEMS.register("the_tribunes_medallion", LegionnaireCurios::new);
   public static final DeferredHolder<Item, Item> BLAZEBORNE_HELMET = ITEMS.register(
      "blazeborne_helmet",
      () -> new BlazeborneArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).fireResistant().rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> BLAZEBORNE_CHESTPLATE = ITEMS.register(
      "blazeborne_chestplate",
      () -> new BlazeborneArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> BLAZEBORNE_LEGGINGS = ITEMS.register(
      "blazeborne_leggings",
      () -> new BlazeborneArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> BLAZEBORNE_BOOTS = ITEMS.register(
      "blazeborne_boots",
      () -> new BlazeborneArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).fireResistant().rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_BLAZEBORNE_HELMET = ITEMS.register(
      "geckolib_blazeborne_helmet",
      () -> new GeckolibBlazeborneArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).fireResistant().rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_BLAZEBORNE_CHESTPLATE = ITEMS.register(
      "geckolib_blazeborne_chestplate",
      () -> new GeckolibBlazeborneArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_BLAZEBORNE_LEGGINGS = ITEMS.register(
      "geckolib_blazeborne_leggings",
      () -> new GeckolibBlazeborneArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_BLAZEBORNE_BOOTS = ITEMS.register(
      "geckolib_blazeborne_boots",
      () -> new GeckolibBlazeborneArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).fireResistant().rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SOUL_FLAME_HELMET = ITEMS.register(
      "soul_flame_helmet",
      () -> new SoulFlameArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SOUL_FLAME_CHESTPLATE = ITEMS.register(
      "soul_flame_chestplate",
      () -> new SoulFlameArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SOUL_FLAME_LEGGINGS = ITEMS.register(
      "soul_flame_leggings",
      () -> new SoulFlameArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SOUL_FLAME_BOOTS = ITEMS.register(
      "soul_flame_boots",
      () -> new SoulFlameArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_SOUL_FLAME_HELMET = ITEMS.register(
      "geckolib_soul_flame_helmet",
      () -> new GeckolibSoulFlameArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_SOUL_FLAME_CHESTPLATE = ITEMS.register(
      "geckolib_soul_flame_chestplate",
      () -> new GeckolibSoulFlameArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_SOUL_FLAME_LEGGINGS = ITEMS.register(
      "geckolib_soul_flame_leggings",
      () -> new GeckolibSoulFlameArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_SOUL_FLAME_BOOTS = ITEMS.register(
      "geckolib_soul_flame_boots",
      () -> new GeckolibSoulFlameArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> CALAMITAS_HELMET = ITEMS.register(
      "calamitas_helmet",
      () -> new CalamitasArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> CALAMITAS_CHESTPLATE = ITEMS.register(
      "calamitas_chestplate",
      () -> new CalamitasChestplateArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> CALAMITAS_LEGGINGS = ITEMS.register(
      "calamitas_leggings",
      () -> new CalamitasArmorItem(Type.LEGGINGS, ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant())
   );
   public static final DeferredHolder<Item, Item> CALAMITAS_BOOTS = ITEMS.register(
      "calamitas_boots",
      () -> new CalamitasArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> CRYOGENIC_RULER_HELMET = ITEMS.register(
      "cryogenic_ruler_helmet",
      () -> new CryogenicRulerArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ICE_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> CRYOGENIC_RULER_CHESTPLATE = ITEMS.register(
      "cryogenic_ruler_chestplate",
      () -> new CryogenicRulerArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ICE_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> CRYOGENIC_RULER_LEGGINGS = ITEMS.register(
      "cryogenic_ruler_leggings",
      () -> new CryogenicRulerArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ICE_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> CRYOGENIC_RULER_BOOTS = ITEMS.register(
      "cryogenic_ruler_boots",
      () -> new CryogenicRulerArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ICE_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> CRYSTAL_ARACHNID_HELMET = ITEMS.register(
      "crystal_arachnid_helmet",
      () -> new CrystalArachnidArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ICE_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> CRYSTAL_ARACHNID_CHESTPLATE = ITEMS.register(
      "crystal_arachnid_chestplate",
      () -> new CrystalArachnidArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ICE_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> CRYSTAL_ARACHNID_LEGGINGS = ITEMS.register(
      "crystal_arachnid_leggings",
      () -> new CrystalArachnidArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ICE_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> CRYSTAL_ARACHNID_BOOTS = ITEMS.register(
      "crystal_arachnid_boots",
      () -> new CrystalArachnidArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ICE_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> HERTA_PUPPET_HELMET = ITEMS.register(
      "herta_puppet_helmet",
      () -> new HertaPuppetArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ICE_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> HERTA_PUPPET_CHESTPLATE = ITEMS.register(
      "herta_puppet_chestplate",
      () -> new HertaPuppetArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ICE_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> HERTA_PUPPET_LEGGINGS = ITEMS.register(
      "herta_puppet_leggings",
      () -> new HertaPuppetArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ICE_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> HERTA_PUPPET_BOOTS = ITEMS.register(
      "herta_puppet_boots",
      () -> new HertaPuppetArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ICE_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> CREAKING_HELMET = ITEMS.register(
      "creaking_helmet",
      () -> new CreakingSorcererArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.NATURE_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> CREAKING_CHESTPLATE = ITEMS.register(
      "creaking_chestplate",
      () -> new CreakingSorcererArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.NATURE_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> CREAKING_LEGGINGS = ITEMS.register(
      "creaking_leggings",
      () -> new CreakingSorcererArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.NATURE_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> CREAKING_BOOTS = ITEMS.register(
      "creaking_boots",
      () -> new CreakingSorcererArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.NATURE_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_CREAKING_HELMET = ITEMS.register(
      "geckolib_creaking_helmet",
      () -> new GeckolibCreakingSorcererArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.NATURE_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_CREAKING_CHESTPLATE = ITEMS.register(
      "geckolib_creaking_chestplate",
      () -> new GeckolibCreakingSorcererArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.NATURE_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_CREAKING_LEGGINGS = ITEMS.register(
      "geckolib_creaking_leggings",
      () -> new GeckolibCreakingSorcererArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.NATURE_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_CREAKING_BOOTS = ITEMS.register(
      "geckolib_creaking_boots",
      () -> new GeckolibCreakingSorcererArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.NATURE_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> CHLOROPHYTE_HELMET = ITEMS.register(
      "chlorophyte_helmet",
      () -> new ChlorophyteHelmetArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.NATURE_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> CHLOROPHYTE_MASK = ITEMS.register(
      "chlorophyte_mask",
      () -> new ChlorophyteMaskArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.NATURE_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> CHLOROPHYTE_HEADGEAR = ITEMS.register(
      "chlorophyte_headgear",
      () -> new ChlorophyteHeadgearArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.NATURE_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> CHLOROPHYTE_CHESTPLATE = ITEMS.register(
      "chlorophyte_chestplate",
      () -> new ChlorophyteArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.NATURE_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> CHLOROPHYTE_LEGGINGS = ITEMS.register(
      "chlorophyte_leggings",
      () -> new ChlorophyteArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.NATURE_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> CHLOROPHYTE_BOOTS = ITEMS.register(
      "chlorophyte_boots",
      () -> new ChlorophyteArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.NATURE_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> CHARGED_SCOURGE_HELMET = ITEMS.register(
      "charged_scourge_helmet",
      () -> new GeckolibChargedScourgeArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.LIGHTNING_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> CHARGED_SCOURGE_CHESTPLATE = ITEMS.register(
      "charged_scourge_chestplate",
      () -> new GeckolibChargedScourgeArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1)
            .rarity((Rarity)HnSRarities.LIGHTNING_RARITY.getValue())
            .fireResistant()
            .durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> CHARGED_SCOURGE_LEGGINGS = ITEMS.register(
      "charged_scourge_leggings",
      () -> new GeckolibChargedScourgeArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.LIGHTNING_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> CHARGED_SCOURGE_BOOTS = ITEMS.register(
      "charged_scourge_boots",
      () -> new GeckolibChargedScourgeArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.LIGHTNING_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> THUNDER_PROWLER_HELMET = ITEMS.register(
      "thunder_prowler_helmet",
      () -> new ThunderProwlerArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.LIGHTNING_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> THUNDER_PROWLER_CHESTPLATE = ITEMS.register(
      "thunder_prowler_chestplate",
      () -> new ThunderProwlerArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1)
            .rarity((Rarity)HnSRarities.LIGHTNING_RARITY.getValue())
            .fireResistant()
            .durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> THUNDER_PROWLER_LEGGINGS = ITEMS.register(
      "thunder_prowler_leggings",
      () -> new ThunderProwlerArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.LIGHTNING_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> THUNDER_PROWLER_BOOTS = ITEMS.register(
      "thunder_prowler_boots",
      () -> new ThunderProwlerArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.LIGHTNING_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ENDER_DRAGON_HELMET = ITEMS.register(
      "ender_dragon_helmet",
      () -> new EnderDragonArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ENDER_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ENDER_DRAGON_CHESTPLATE = ITEMS.register(
      "ender_dragon_chestplate",
      () -> new EnderDragonArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ENDER_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ENDER_DRAGON_LEGGINGS = ITEMS.register(
      "ender_dragon_leggings",
      () -> new EnderDragonArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ENDER_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ENDER_DRAGON_BOOTS = ITEMS.register(
      "ender_dragon_boots",
      () -> new EnderDragonArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ENDER_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_ENDER_DRAGON_HELMET = ITEMS.register(
      "geckolib_ender_dragon_helmet",
      () -> new GeckolibEnderDragonArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ENDER_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_ENDER_DRAGON_CHESTPLATE = ITEMS.register(
      "geckolib_ender_dragon_chestplate",
      () -> new GeckolibEnderDragonArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ENDER_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_ENDER_DRAGON_LEGGINGS = ITEMS.register(
      "geckolib_ender_dragon_leggings",
      () -> new GeckolibEnderDragonArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ENDER_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_ENDER_DRAGON_BOOTS = ITEMS.register(
      "geckolib_ender_dragon_boots",
      () -> new GeckolibEnderDragonArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ENDER_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> FLESH_MASS_HELMET = ITEMS.register(
      "flesh_mass_helmet",
      () -> new FleshMassArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.BLOOD_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> FLESH_MASS_CHESTPLATE = ITEMS.register(
      "flesh_mass_chestplate",
      () -> new FleshMassArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.BLOOD_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> FLESH_MASS_LEGGINGS = ITEMS.register(
      "flesh_mass_leggings",
      () -> new FleshMassArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.BLOOD_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> FLESH_MASS_BOOTS = ITEMS.register(
      "flesh_mass_boots",
      () -> new FleshMassArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.BLOOD_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> THE_WITHER_HELMET = ITEMS.register(
      "the_wither_helmet",
      () -> new TheWitherArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.BLOOD_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> THE_WITHER_CHESTPLATE = ITEMS.register(
      "the_wither_chestplate",
      () -> new TheWitherArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.BLOOD_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> THE_WITHER_LEGGINGS = ITEMS.register(
      "the_wither_leggings",
      () -> new TheWitherArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.BLOOD_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> THE_WITHER_BOOTS = ITEMS.register(
      "the_wither_boots",
      () -> new TheWitherArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.BLOOD_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_THE_WITHER_HELMET = ITEMS.register(
      "geckolib_the_wither_helmet",
      () -> new GeckolibTheWitherArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.BLOOD_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_THE_WITHER_CHESTPLATE = ITEMS.register(
      "geckolib_the_wither_chestplate",
      () -> new GeckolibTheWitherArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.BLOOD_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_THE_WITHER_LEGGINGS = ITEMS.register(
      "geckolib_the_wither_leggings",
      () -> new GeckolibTheWitherArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).fireResistant().rarity((Rarity)HnSRarities.BLOOD_RARITY.getValue()).durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_THE_WITHER_BOOTS = ITEMS.register(
      "geckolib_the_wither_boots",
      () -> new GeckolibTheWitherArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).fireResistant().rarity((Rarity)HnSRarities.BLOOD_RARITY.getValue()).durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SUPREME_WITCH_HELMET = ITEMS.register(
      "supreme_witch_helmet",
      () -> new SupremeWitchArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.EVOCATION_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SUPREME_WITCH_CHESTPLATE = ITEMS.register(
      "supreme_witch_chestplate",
      () -> new SupremeWitchArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1)
            .rarity((Rarity)HnSRarities.EVOCATION_RARITY.getValue())
            .fireResistant()
            .durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SUPREME_WITCH_LEGGINGS = ITEMS.register(
      "supreme_witch_leggings",
      () -> new SupremeWitchArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.EVOCATION_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SUPREME_WITCH_BOOTS = ITEMS.register(
      "supreme_witch_boots",
      () -> new SupremeWitchArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.EVOCATION_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_SUPREME_WITCH_HELMET = ITEMS.register(
      "geckolib_supreme_witch_helmet",
      () -> new GeckolibSupremeWitchArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.EVOCATION_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_SUPREME_WITCH_CHESTPLATE = ITEMS.register(
      "geckolib_supreme_witch_chestplate",
      () -> new GeckolibSupremeWitchArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1)
            .rarity((Rarity)HnSRarities.EVOCATION_RARITY.getValue())
            .fireResistant()
            .durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_SUPREME_WITCH_LEGGINGS = ITEMS.register(
      "geckolib_supreme_witch_leggings",
      () -> new GeckolibSupremeWitchArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.EVOCATION_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_SUPREME_WITCH_BOOTS = ITEMS.register(
      "geckolib_supreme_witch_boots",
      () -> new GeckolibSupremeWitchArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.EVOCATION_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ALCHEMIST_SUPREME_HELMET = ITEMS.register(
      "alchemist_supreme_helmet",
      () -> new AlchemistSupremeArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.EVOCATION_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ALCHEMIST_SUPREME_CHESTPLATE = ITEMS.register(
      "alchemist_supreme_chestplate",
      () -> new AlchemistSupremeArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1)
            .rarity((Rarity)HnSRarities.EVOCATION_RARITY.getValue())
            .fireResistant()
            .durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ALCHEMIST_SUPREME_LEGGINGS = ITEMS.register(
      "alchemist_supreme_leggings",
      () -> new AlchemistSupremeArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.EVOCATION_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ALCHEMIST_SUPREME_BOOTS = ITEMS.register(
      "alchemist_supreme_boots",
      () -> new AlchemistSupremeArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.EVOCATION_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SERAPH_HELMET = ITEMS.register(
      "seraph_helmet",
      () -> new SeraphArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.HOLY_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SERAPH_CHESTPLATE = ITEMS.register(
      "seraph_chestplate",
      () -> new SeraphArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.HOLY_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SERAPH_LEGGINGS = ITEMS.register(
      "seraph_leggings",
      () -> new SeraphArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.HOLY_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SERAPH_BOOTS = ITEMS.register(
      "seraph_boots",
      () -> new SeraphArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.HOLY_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_SERAPH_HELMET = ITEMS.register(
      "geckolib_seraph_helmet",
      () -> new GeckolibSeraphArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.HOLY_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_SERAPH_CHESTPLATE = ITEMS.register(
      "geckolib_seraph_chestplate",
      () -> new GeckolibSeraphArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.HOLY_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_SERAPH_LEGGINGS = ITEMS.register(
      "geckolib_seraph_leggings",
      () -> new GeckolibSeraphArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.HOLY_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_SERAPH_BOOTS = ITEMS.register(
      "geckolib_seraph_boots",
      () -> new GeckolibSeraphArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.HOLY_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GABRIEL_ULTRAKILL_HELMET = ITEMS.register(
      "gabriel_ultrakill_helmet",
      () -> new GabrielArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GABRIEL_ULTRAKILL_CHESTPLATE = ITEMS.register(
      "gabriel_ultrakill_chestplate",
      () -> new GabrielElytraArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GABRIEL_ULTRAKILL_LEGGINGS = ITEMS.register(
      "gabriel_ultrakill_leggings",
      () -> new GabrielArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GABRIEL_ULTRAKILL_BOOTS = ITEMS.register(
      "gabriel_ultrakill_boots",
      () -> new GabrielArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_GABRIEL_ULTRAKILL_HELMET = ITEMS.register(
      "geckolib_gabriel_ultrakill_helmet",
      () -> new GeckolibGabrielArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.HOLY_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_GABRIEL_ULTRAKILL_CHESTPLATE = ITEMS.register(
      "geckolib_gabriel_ultrakill_chestplate",
      () -> new GeckolibGabrielElytraItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.HOLY_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_GABRIEL_ULTRAKILL_LEGGINGS = ITEMS.register(
      "geckolib_gabriel_ultrakill_leggings",
      () -> new GeckolibGabrielArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.HOLY_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_GABRIEL_ULTRAKILL_BOOTS = ITEMS.register(
      "geckolib_gabriel_ultrakill_boots",
      () -> new GeckolibGabrielArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.HOLY_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ABBERANT_PREDATOR_HELMET = ITEMS.register(
      "abberant_predator_helmet",
      () -> new AbberantPredatorArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.SHADOW_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ABBERANT_PREDATOR_CHESTPLATE = ITEMS.register(
      "abberant_predator_chestplate",
      () -> new AbberantPredatorArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.SHADOW_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ABBERANT_PREDATOR_LEGGINGS = ITEMS.register(
      "abberant_predator_leggings",
      () -> new AbberantPredatorArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.SHADOW_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ABBERANT_PREDATOR_BOOTS = ITEMS.register(
      "abberant_predator_boots",
      () -> new AbberantPredatorArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.SHADOW_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SHADOW_SCALE_HELMET = ITEMS.register(
      "shadow_scale_helmet",
      () -> new ShadowScaleArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.SHADOW_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SHADOW_SCALE_CHESTPLATE = ITEMS.register(
      "shadow_scale_chestplate",
      () -> new ShadowScaleArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.SHADOW_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SHADOW_SCALE_LEGGINGS = ITEMS.register(
      "shadow_scale_leggings",
      () -> new ShadowScaleArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.SHADOW_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SHADOW_SCALE_BOOTS = ITEMS.register(
      "shadow_scale_boots",
      () -> new ShadowScaleArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.SHADOW_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> MYCELIUM_GUARDIAN_HELMET = ITEMS.register(
      "mycelium_guardian_helmet",
      () -> new MyceliumGuardianArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.RADIANCE_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> MYCELIUM_GUARDIAN_CHESTPLATE = ITEMS.register(
      "mycelium_guardian_chestplate",
      () -> new MyceliumGuardianArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.RADIANCE_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> MYCELIUM_GUARDIAN_LEGGINGS = ITEMS.register(
      "mycelium_guardian_leggings",
      () -> new MyceliumGuardianArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.RADIANCE_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> MYCELIUM_GUARDIAN_BOOTS = ITEMS.register(
      "mycelium_guardian_boots",
      () -> new MyceliumGuardianArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.RADIANCE_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> INFESTATION_HELMET = ITEMS.register(
      "infestation_helmet",
      () -> new InfestationArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).fireResistant().rarity((Rarity)HnSRarities.ELDRITCH_RARITY.getValue()).durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> INFESTATION_CHESTPLATE = ITEMS.register(
      "infestation_chestplate",
      () -> new InfestationArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ELDRITCH_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> INFESTATION_LEGGINGS = ITEMS.register(
      "infestation_leggings",
      () -> new InfestationArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ELDRITCH_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> INFESTATION_BOOTS = ITEMS.register(
      "infestation_boots",
      () -> new InfestationArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).fireResistant().rarity((Rarity)HnSRarities.ELDRITCH_RARITY.getValue()).durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_INFESTATION_HELMET = ITEMS.register(
      "geckolib_infestation_helmet",
      () -> new GeckolibInfestationArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).fireResistant().rarity((Rarity)HnSRarities.ELDRITCH_RARITY.getValue()).durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_INFESTATION_CHESTPLATE = ITEMS.register(
      "geckolib_infestation_chestplate",
      () -> new GeckolibInfestationArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ELDRITCH_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_INFESTATION_LEGGINGS = ITEMS.register(
      "geckolib_infestation_leggings",
      () -> new GeckolibInfestationArmorItem(
         Type.LEGGINGS, ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ELDRITCH_RARITY.getValue()).fireResistant()
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_INFESTATION_BOOTS = ITEMS.register(
      "geckolib_infestation_boots",
      () -> new GeckolibInfestationArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).fireResistant().rarity((Rarity)HnSRarities.ELDRITCH_RARITY.getValue()).durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> BOUNTY_HUNTER_HELMET = ITEMS.register(
      "bounty_hunter_helmet",
      () -> new BountyHunterArmorItem(
         Type.HELMET, ItemPropertiesHelper.equipment(1).rarity(Rarity.EPIC).fireResistant().durability(Type.HELMET.getDurability(37))
      )
   );
   public static final DeferredHolder<Item, Item> BOUNTY_HUNTER_CHESTPLATE = ITEMS.register(
      "bounty_hunter_chestplate",
      () -> new BountyHunterArmorItem(
         Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).rarity(Rarity.EPIC).fireResistant().durability(Type.CHESTPLATE.getDurability(37))
      )
   );
   public static final DeferredHolder<Item, Item> BOUNTY_HUNTER_LEGGINGS = ITEMS.register(
      "bounty_hunter_leggings",
      () -> new BountyHunterArmorItem(
         Type.LEGGINGS, ItemPropertiesHelper.equipment(1).rarity(Rarity.EPIC).fireResistant().durability(Type.LEGGINGS.getDurability(37))
      )
   );
   public static final DeferredHolder<Item, Item> BOUNTY_HUNTER_BOOTS = ITEMS.register(
      "bounty_hunter_boots",
      () -> new BountyHunterArmorItem(
         Type.BOOTS, ItemPropertiesHelper.equipment(1).rarity(Rarity.EPIC).fireResistant().durability(Type.BOOTS.getDurability(37))
      )
   );
   public static final DeferredHolder<Item, Item> FROSTBITE_HUNTER_HELMET = ITEMS.register(
      "frostbite_hunter_helmet",
      () -> new FrostbiteHunterArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ICE_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> FROSTBITE_HUNTER_CHESTPLATE = ITEMS.register(
      "frostbite_hunter_chestplate",
      () -> new FrostbiteHunterArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ICE_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> FROSTBITE_HUNTER_LEGGINGS = ITEMS.register(
      "frostbite_hunter_leggings",
      () -> new FrostbiteHunterArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ICE_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> FROSTBITE_HUNTER_BOOTS = ITEMS.register(
      "frostbite_hunter_boots",
      () -> new FrostbiteHunterArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ICE_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> PERMAFROST_PRINCE_HELMET = ITEMS.register(
      "permafrost_prince_helmet",
      () -> new PermafrostPrinceArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ICE_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> PERMAFROST_PRINCE_CHESTPLATE = ITEMS.register(
      "permafrost_prince_chestplate",
      () -> new PermafrostPrinceArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ICE_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> PERMAFROST_PRINCE_LEGGINGS = ITEMS.register(
      "permafrost_prince_leggings",
      () -> new PermafrostPrinceArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ICE_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> PERMAFROST_PRINCE_BOOTS = ITEMS.register(
      "permafrost_prince_boots",
      () -> new PermafrostPrinceArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ICE_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> FROSTBITE_KNIGHT_HELMET = ITEMS.register(
      "frostbite_knight_helmet",
      () -> new FrostbiteKnightArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ICE_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> FROSTBITE_KNIGHT_CHESTPLATE = ITEMS.register(
      "frostbite_knight_chestplate",
      () -> new FrostbiteKnightArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ICE_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> FROSTBITE_KNIGHT_LEGGINGS = ITEMS.register(
      "frostbite_knight_leggings",
      () -> new FrostbiteKnightArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ICE_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> FROSTBITE_KNIGHT_BOOTS = ITEMS.register(
      "frostbite_knight_boots",
      () -> new FrostbiteKnightArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ICE_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> DREADSTEEL_KNIGHT_HELMET = ITEMS.register(
      "dreadsteel_knight_helmet",
      () -> new DreadsteelKnightArmorItem(
         Type.HELMET, ItemPropertiesHelper.equipment(1).rarity(Rarity.EPIC).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> DREADSTEEL_KNIGHT_CHESTPLATE = ITEMS.register(
      "dreadsteel_knight_chestplate",
      () -> new DreadsteelKnightArmorItem(
         Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).rarity(Rarity.EPIC).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> DREADSTEEL_KNIGHT_LEGGINGS = ITEMS.register(
      "dreadsteel_knight_leggings",
      () -> new DreadsteelKnightArmorItem(
         Type.LEGGINGS, ItemPropertiesHelper.equipment(1).rarity(Rarity.EPIC).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> DREADSTEEL_KNIGHT_BOOTS = ITEMS.register(
      "dreadsteel_knight_boots",
      () -> new DreadsteelKnightArmorItem(
         Type.BOOTS, ItemPropertiesHelper.equipment(1).rarity(Rarity.EPIC).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> LEGACY_LEGIONNAIRE_HELMET = ITEMS.register(
      "legionnaire_helmet",
      () -> new OldLegionnaireArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1)
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .fireResistant()
            .durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> LEGACY_LEGIONNAIRE_CHESTPLATE = ITEMS.register(
      "legionnaire_chestplate",
      () -> new OldLegionnaireArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1)
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .fireResistant()
            .durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> LEGACY_LEGIONNAIRE_LEGGINGS = ITEMS.register(
      "legionnaire_leggings",
      () -> new OldLegionnaireArmorItem(
         Type.LEGGINGS, ItemPropertiesHelper.equipment(1).fireResistant().rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
      )
   );
   public static final DeferredHolder<Item, Item> LEGACY_LEGIONNAIRE_BOOTS = ITEMS.register(
      "legionnaire_boots",
      () -> new OldLegionnaireArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> LEGIONNAIRE_RULER_HELMET = ITEMS.register(
      "legionnaire_ruler_helmet",
      () -> new LegionnaireRulerArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1)
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .fireResistant()
            .durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> LEGIONNAIRE_RULER_CHESTPLATE = ITEMS.register(
      "legionnaire_ruler_chestplate",
      () -> new LegionnaireRulerArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1)
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .fireResistant()
            .durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> LEGIONNAIRE_RULER_LEGGINGS = ITEMS.register(
      "legionnaire_ruler_leggings",
      () -> new LegionnaireRulerArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> LEGIONNAIRE_RULER_BOOTS = ITEMS.register(
      "legionnaire_ruler_boots",
      () -> new LegionnaireRulerArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SOUL_LEGIONNAIRE_RULER_HELMET = ITEMS.register(
      "soul_legionnaire_ruler_helmet",
      () -> new SoulLegionnaireRulerArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SOUL_LEGIONNAIRE_RULER_CHESTPLATE = ITEMS.register(
      "soul_legionnaire_ruler_chestplate",
      () -> new SoulLegionnaireRulerArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1)
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .fireResistant()
            .durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SOUL_LEGIONNAIRE_RULER_LEGGINGS = ITEMS.register(
      "soul_legionnaire_ruler_leggings",
      () -> new SoulLegionnaireRulerArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1)
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .fireResistant()
            .durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SOUL_LEGIONNAIRE_RULER_BOOTS = ITEMS.register(
      "soul_legionnaire_ruler_boots",
      () -> new SoulLegionnaireRulerArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> LEGIONNAIRE_COMMANDER_HELMET = ITEMS.register(
      "legionnaire_commander_helmet",
      () -> new LegionnaireCommanderArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> LEGIONNAIRE_COMMANDER_CHESTPLATE = ITEMS.register(
      "legionnaire_commander_chestplate",
      () -> new LegionnaireCommanderArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1)
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .fireResistant()
            .durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> LEGIONNAIRE_COMMANDER_LEGGINGS = ITEMS.register(
      "legionnaire_commander_leggings",
      () -> new LegionnaireCommanderArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1)
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .fireResistant()
            .durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> LEGIONNAIRE_COMMANDER_BOOTS = ITEMS.register(
      "legionnaire_commander_boots",
      () -> new LegionnaireCommanderArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> TYROS_HELMET = ITEMS.register(
      "garments_of_the_first_flamebearer_helmet",
      () -> new GarmentsOfTheFirstFlamebearerArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> TYROS_CHESTPLATE = ITEMS.register(
      "garments_of_the_first_flamebearer_chestplate",
      () -> new GarmentsOfTheFirstFlamebearerChestplateArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1)
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .fireResistant()
            .durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> TYROS_LEGGINGS = ITEMS.register(
      "garments_of_the_first_flamebearer_leggings",
      () -> new GarmentsOfTheFirstFlamebearerArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1)
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .fireResistant()
            .durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> TYROS_BOOTS = ITEMS.register(
      "garments_of_the_first_flamebearer_boots",
      () -> new GarmentsOfTheFirstFlamebearerArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> TYROS_SOUL_HELMET = ITEMS.register(
      "garments_of_the_first_flamebearer_soul_helmet",
      () -> new GarmentsOfTheFirstFlamebearerSoulArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> TYROS_SOUL_CHESTPLATE = ITEMS.register(
      "garments_of_the_first_flamebearer_soul_chestplate",
      () -> new GarmentsOfTheFirstFlamebearerSoulChestplateArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1)
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .fireResistant()
            .durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> TYROS_SOUL_LEGGINGS = ITEMS.register(
      "garments_of_the_first_flamebearer_soul_leggings",
      () -> new GarmentsOfTheFirstFlamebearerSoulArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1)
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .fireResistant()
            .durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> TYROS_SOUL_BOOTS = ITEMS.register(
      "garments_of_the_first_flamebearer_soul_boots",
      () -> new GarmentsOfTheFirstFlamebearerSoulArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> PYRIUM_HELMET = ITEMS.register(
      "pyrium_helmet",
      () -> new PyriumArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1)
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .fireResistant()
            .durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> PYRIUM_CHESTPLATE = ITEMS.register(
      "pyrium_chestplate",
      () -> new PyriumArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1)
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .fireResistant()
            .durability(Type.CHESTPLATE.getDurability(64))
            .durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> PYRIUM_LEGGINGS = ITEMS.register(
      "pyrium_leggings",
      () -> new PyriumArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> PYRIUM_BOOTS = ITEMS.register(
      "pyrium_boots",
      () -> new PyriumArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> PYRIUM_BATTLEMAGE_HELMET = ITEMS.register(
      "pyrium_battlemage_helmet",
      () -> new PyriumBattlemageArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1)
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .fireResistant()
            .durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> PYRIUM_BATTLEMAGE_CHESTPLATE = ITEMS.register(
      "pyrium_battlemage_chestplate",
      () -> new PyriumBattlemageArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1)
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .fireResistant()
            .durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> PYRIUM_BATTLEMAGE_LEGGINGS = ITEMS.register(
      "pyrium_battlemage_leggings",
      () -> new PyriumBattlemageArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> PYRIUM_BATTLEMAGE_BOOTS = ITEMS.register(
      "pyrium_battlemage_boots",
      () -> new PyriumBattlemageArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1)
            .fireResistant()
            .rarity((Rarity)CinderousRarity.CINDEROUS_RARITY_PROXY.getValue())
            .durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> FRIEREN_HELMET = ITEMS.register(
      "frieren_helmet", () -> new FrierenArmorItem(Type.HELMET, ItemPropertiesHelper.equipment(1).durability(Type.HELMET.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> FRIEREN_CHESTPLATE = ITEMS.register(
      "frieren_chestplate", () -> new FrierenArmorItem(Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).durability(Type.CHESTPLATE.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> FRIEREN_LEGGINGS = ITEMS.register(
      "frieren_leggings", () -> new FrierenArmorItem(Type.LEGGINGS, ItemPropertiesHelper.equipment(1).durability(Type.LEGGINGS.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> FRIEREN_BOOTS = ITEMS.register(
      "frieren_boots", () -> new FrierenArmorItem(Type.BOOTS, ItemPropertiesHelper.equipment(1).durability(Type.BOOTS.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> MINER_HELMET = ITEMS.register(
      "miner_helmet", () -> new MinerArmorItem(Type.HELMET, ItemPropertiesHelper.equipment(1).durability(Type.HELMET.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> MINER_CHESTPLATE = ITEMS.register(
      "miner_chestplate", () -> new MinerArmorItem(Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).durability(Type.CHESTPLATE.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> MINER_LEGGINGS = ITEMS.register(
      "miner_leggings", () -> new MinerArmorItem(Type.LEGGINGS, ItemPropertiesHelper.equipment(1).durability(Type.LEGGINGS.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> MINER_BOOTS = ITEMS.register(
      "miner_boots", () -> new MinerArmorItem(Type.BOOTS, ItemPropertiesHelper.equipment(1).durability(Type.BOOTS.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> SPECTRAL_SPELUNKER_HELMET = ITEMS.register(
      "spectral_spelunker_helmet",
      () -> new SpectralSpelunkerArmorItem(
         Type.HELMET, ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.EVOCATION_RARITY.getValue()).durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SPECTRAL_SPELUNKER_CHESTPLATE = ITEMS.register(
      "spectral_spelunker_chestplate",
      () -> new SpectralSpelunkerArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.EVOCATION_RARITY.getValue()).durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SPECTRAL_SPELUNKER_LEGGINGS = ITEMS.register(
      "spectral_spelunker_leggings",
      () -> new SpectralSpelunkerArmorItem(
         Type.LEGGINGS, ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.EVOCATION_RARITY.getValue()).durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SPECTRAL_SPELUNKER_BOOTS = ITEMS.register(
      "spectral_spelunker_boots",
      () -> new SpectralSpelunkerArmorItem(
         Type.BOOTS, ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.EVOCATION_RARITY.getValue()).durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> PROJECT_SEKAI_HELMET = ITEMS.register(
      "project_sekai_helmet", () -> new ProjectSekaiArmorItem(Type.HELMET, ItemPropertiesHelper.equipment(1).durability(Type.HELMET.getDurability(64)))
   );
   public static final DeferredHolder<Item, Item> PROJECT_SEKAI_CHESTPLATE = ITEMS.register(
      "project_sekai_chestplate",
      () -> new ProjectSekaiArmorItem(Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).durability(Type.CHESTPLATE.getDurability(64)))
   );
   public static final DeferredHolder<Item, Item> PROJECT_SEKAI_LEGGINGS = ITEMS.register(
      "project_sekai_leggings", () -> new ProjectSekaiArmorItem(Type.LEGGINGS, ItemPropertiesHelper.equipment(1).durability(Type.LEGGINGS.getDurability(64)))
   );
   public static final DeferredHolder<Item, Item> PROJECT_SEKAI_BOOTS = ITEMS.register(
      "project_sekai_boots", () -> new ProjectSekaiArmorItem(Type.BOOTS, ItemPropertiesHelper.equipment(1).durability(Type.BOOTS.getDurability(64)))
   );
   public static final DeferredHolder<Item, Item> ROTTEN_GIRL_HELMET = ITEMS.register(
      "rotten_girl_helmet", () -> new RottenGirlArmorItem(Type.HELMET, ItemPropertiesHelper.equipment(1).durability(Type.HELMET.getDurability(64)))
   );
   public static final DeferredHolder<Item, Item> ROTTEN_GIRL_CHESTPLATE = ITEMS.register(
      "rotten_girl_chestplate", () -> new RottenGirlArmorItem(Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).durability(Type.HELMET.getDurability(64)))
   );
   public static final DeferredHolder<Item, Item> ROTTEN_GIRL_LEGGINGS = ITEMS.register(
      "rotten_girl_leggings", () -> new RottenGirlArmorItem(Type.LEGGINGS, ItemPropertiesHelper.equipment(1).durability(Type.HELMET.getDurability(64)))
   );
   public static final DeferredHolder<Item, Item> ROTTEN_GIRL_BOOTS = ITEMS.register(
      "rotten_girl_boots", () -> new RottenGirlArmorItem(Type.BOOTS, ItemPropertiesHelper.equipment(1).durability(Type.HELMET.getDurability(64)))
   );
   public static final DeferredHolder<Item, Item> SYNTHESIZER_V_HELMET = ITEMS.register(
      "synthesizer_v_helmet", () -> new SynthesizerVArmorItem(Type.HELMET, ItemPropertiesHelper.equipment(1).durability(Type.HELMET.getDurability(64)))
   );
   public static final DeferredHolder<Item, Item> SYNTHESIZER_V_CHESTPLATE = ITEMS.register(
      "synthesizer_v_chestplate",
      () -> new SynthesizerVArmorItem(Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).durability(Type.CHESTPLATE.getDurability(64)))
   );
   public static final DeferredHolder<Item, Item> SYNTHESIZER_V_LEGGINGS = ITEMS.register(
      "synthesizer_v_leggings", () -> new SynthesizerVArmorItem(Type.LEGGINGS, ItemPropertiesHelper.equipment(1).durability(Type.LEGGINGS.getDurability(64)))
   );
   public static final DeferredHolder<Item, Item> SYNTHESIZER_V_BOOTS = ITEMS.register(
      "synthesizer_v_boots", () -> new SynthesizerVArmorItem(Type.BOOTS, ItemPropertiesHelper.equipment(1).durability(Type.BOOTS.getDurability(64)))
   );
   public static final DeferredHolder<Item, Item> UTAU_HELMET = ITEMS.register(
      "utau_helmet", () -> new UtauArmorItem(Type.HELMET, ItemPropertiesHelper.equipment(1).durability(Type.HELMET.getDurability(64)))
   );
   public static final DeferredHolder<Item, Item> UTAU_CHESTPLATE = ITEMS.register(
      "utau_chestplate", () -> new UtauArmorItem(Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).durability(Type.CHESTPLATE.getDurability(64)))
   );
   public static final DeferredHolder<Item, Item> UTAU_LEGGINGS = ITEMS.register(
      "utau_leggings", () -> new UtauArmorItem(Type.LEGGINGS, ItemPropertiesHelper.equipment(1).durability(Type.LEGGINGS.getDurability(64)))
   );
   public static final DeferredHolder<Item, Item> UTAU_BOOTS = ITEMS.register(
      "utau_boots", () -> new UtauArmorItem(Type.BOOTS, ItemPropertiesHelper.equipment(1).durability(Type.BOOTS.getDurability(64)))
   );
   public static final DeferredHolder<Item, Item> GIORNO_GIOVANNA_HELMET = ITEMS.register(
      "giorno_giovanna_helmet",
      () -> new GiornoGiovannaArmorItem(Type.HELMET, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.HELMET.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> GIORNO_GIOVANNA_CHESTPLATE = ITEMS.register(
      "giorno_giovanna_chestplate",
      () -> new GiornoGiovannaArmorItem(Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.CHESTPLATE.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> GIORNO_GIOVANNA_LEGGINGS = ITEMS.register(
      "giorno_giovanna_leggings",
      () -> new GiornoGiovannaArmorItem(Type.LEGGINGS, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.LEGGINGS.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> GIORNO_GIOVANNA_BOOTS = ITEMS.register(
      "giorno_giovanna_boots",
      () -> new GiornoGiovannaArmorItem(Type.BOOTS, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.BOOTS.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> NERU_HELMET = ITEMS.register(
      "neru_helmet", () -> new AkitaNeruArmorItem(Type.HELMET, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.HELMET.getDurability(64)))
   );
   public static final DeferredHolder<Item, Item> NERU_CHESTPLATE = ITEMS.register(
      "neru_chestplate",
      () -> new AkitaNeruArmorItem(Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.CHESTPLATE.getDurability(64)))
   );
   public static final DeferredHolder<Item, Item> NERU_LEGGINGS = ITEMS.register(
      "neru_leggings",
      () -> new AkitaNeruArmorItem(Type.LEGGINGS, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.LEGGINGS.getDurability(64)))
   );
   public static final DeferredHolder<Item, Item> NERU_BOOTS = ITEMS.register(
      "neru_boots", () -> new AkitaNeruArmorItem(Type.BOOTS, ItemPropertiesHelper.equipment(1).fireResistant().durability(Type.BOOTS.getDurability(64)))
   );
   public static final DeferredHolder<Item, Item> MITHRIL_BATTLEMAGE_HELMET = ITEMS.register(
      "mithril_battlemage_helmet",
      () -> new MithrilBattlemageArmorItem(
         Type.HELMET, ItemPropertiesHelper.equipment(1).rarity(Rarity.EPIC).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> MITHRIL_BATTLEMAGE_CHESTPLATE = ITEMS.register(
      "mithril_battlemage_chestplate",
      () -> new MithrilBattlemageArmorItem(
         Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).rarity(Rarity.EPIC).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> MITHRIL_BATTLEMAGE_LEGGINGS = ITEMS.register(
      "mithril_battlemage_leggings",
      () -> new MithrilBattlemageArmorItem(
         Type.LEGGINGS, ItemPropertiesHelper.equipment(1).rarity(Rarity.EPIC).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> MITHRIL_BATTLEMAGE_BOOTS = ITEMS.register(
      "mithril_battlemage_boots",
      () -> new MithrilBattlemageArmorItem(
         Type.BOOTS, ItemPropertiesHelper.equipment(1).rarity(Rarity.EPIC).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> MAGEHUNTER_HELMET = ITEMS.register(
      "magehunter_helmet",
      () -> new MageHunterArmorItem(
         Type.HELMET, ItemPropertiesHelper.equipment(1).rarity(Rarity.EPIC).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> MAGEHUNTER_CHESTPLATE = ITEMS.register(
      "magehunter_chestplate",
      () -> new MageHunterArmorItem(
         Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).rarity(Rarity.EPIC).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> MAGEHUNTER_LEGGINGS = ITEMS.register(
      "magehunter_leggings",
      () -> new MageHunterArmorItem(
         Type.LEGGINGS, ItemPropertiesHelper.equipment(1).rarity(Rarity.EPIC).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> MAGEHUNTER_BOOTS = ITEMS.register(
      "magehunter_boots",
      () -> new MageHunterArmorItem(Type.BOOTS, ItemPropertiesHelper.equipment(1).rarity(Rarity.EPIC).fireResistant().durability(Type.BOOTS.getDurability(64)))
   );
   public static final DeferredHolder<Item, Item> ATLAS_HELMET = ITEMS.register(
      "atlas_helmet", () -> new AtlasArmorItem(Type.HELMET, ItemPropertiesHelper.equipment(1).durability(Type.HELMET.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> ATLAS_CHESTPLATE = ITEMS.register(
      "atlas_chestplate", () -> new AtlasArmorItem(Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).durability(Type.CHESTPLATE.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> ATLAS_LEGGINGS = ITEMS.register(
      "atlas_leggings", () -> new AtlasArmorItem(Type.LEGGINGS, ItemPropertiesHelper.equipment(1).durability(Type.LEGGINGS.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> ATLAS_BOOTS = ITEMS.register(
      "atlas_boots", () -> new AtlasArmorItem(Type.BOOTS, ItemPropertiesHelper.equipment(1).durability(Type.BOOTS.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> MAVERICK_HELMET = ITEMS.register(
      "maverick_helmet",
      () -> new MaverickArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.NATURE_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> MAVERICK_CHESTPLATE = ITEMS.register(
      "maverick_chestplate",
      () -> new MaverickArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.NATURE_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> MAVERICK_LEGGINGS = ITEMS.register(
      "maverick_leggings",
      () -> new MaverickArmorItem(Type.LEGGINGS, ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.NATURE_RARITY.getValue()).fireResistant())
   );
   public static final DeferredHolder<Item, Item> MAVERICK_BOOTS = ITEMS.register(
      "maverick_boots",
      () -> new MaverickArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.NATURE_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> DARK_RITUAL_TEMPLAR_HELMET = ITEMS.register(
      "dark_ritual_templar_helmet",
      () -> new DarkRitualTemplarArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ELDRITCH_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> DARK_RITUAL_TEMPLAR_CHESTPLATE = ITEMS.register(
      "dark_ritual_templar_chestplate",
      () -> new DarkRitualTemplarArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ELDRITCH_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> DARK_RITUAL_TEMPLAR_LEGGINGS = ITEMS.register(
      "dark_ritual_templar_leggings",
      () -> new DarkRitualTemplarArmorItem(
         Type.LEGGINGS, ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ELDRITCH_RARITY.getValue()).fireResistant()
      )
   );
   public static final DeferredHolder<Item, Item> DARK_RITUAL_TEMPLAR_BOOTS = ITEMS.register(
      "dark_ritual_templar_boots",
      () -> new DarkRitualTemplarArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ELDRITCH_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_DARK_RITUAL_TEMPLAR_HELMET = ITEMS.register(
      "geckolib_dark_ritual_templar_helmet",
      () -> new GeckolibDarkRitualTemplarArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ELDRITCH_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_DARK_RITUAL_TEMPLAR_CHESTPLATE = ITEMS.register(
      "geckolib_dark_ritual_templar_chestplate",
      () -> new GeckolibDarkRitualTemplarArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ELDRITCH_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_DARK_RITUAL_TEMPLAR_LEGGINGS = ITEMS.register(
      "geckolib_dark_ritual_templar_leggings",
      () -> new GeckolibDarkRitualTemplarArmorItem(
         Type.LEGGINGS, ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ELDRITCH_RARITY.getValue()).fireResistant()
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_DARK_RITUAL_TEMPLAR_BOOTS = ITEMS.register(
      "geckolib_dark_ritual_templar_boots",
      () -> new GeckolibDarkRitualTemplarArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.ELDRITCH_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> FIREBLOSSOM_BATTLEMAGE_CROWN = ITEMS.register(
      "fireblossom_crown",
      () -> new FireblossomBattlemageCrownedArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> FIREBLOSSOM_BATTLEMAGE_HELMET = ITEMS.register(
      "fireblossom_helmet",
      () -> new FireblossomBattlemageHelmetArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> FIREBLOSSOM_BATTLEMAGE_CHESTPLATE = ITEMS.register(
      "fireblossom_chestplate",
      () -> new FireblossomBattlemageArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> FIREBLOSSOM_BATTLEMAGE_LEGGINGS = ITEMS.register(
      "fireblossom_leggings",
      () -> new FireblossomBattlemageArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> FIREBLOSSOM_BATTLEMAGE_BOOTS = ITEMS.register(
      "fireblossom_boots",
      () -> new FireblossomBattlemageArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_FIREBLOSSOM_BATTLEMAGE_CROWN = ITEMS.register(
      "geckolib_fireblossom_crown",
      () -> new GeckolibFireblossomBattlemageCrownedArmorItem(
         Type.HELMET, ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant()
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_FIREBLOSSOM_BATTLEMAGE_HELMET = ITEMS.register(
      "geckolib_fireblossom_helmet",
      () -> new GeckolibFireblossomBattlemageHelmetArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_FIREBLOSSOM_BATTLEMAGE_CHESTPLATE = ITEMS.register(
      "geckolib_fireblossom_chestplate",
      () -> new GeckolibFireblossomBattlemageArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_FIREBLOSSOM_BATTLEMAGE_LEGGINGS = ITEMS.register(
      "geckolib_fireblossom_leggings",
      () -> new GeckolibFireblossomBattlemageArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_FIREBLOSSOM_BATTLEMAGE_BOOTS = ITEMS.register(
      "geckolib_fireblossom_boots",
      () -> new GeckolibFireblossomBattlemageArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SLC_CAT_HELMET = ITEMS.register(
      "slc_cat_helmet",
      () -> new SLCCatArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.HOLY_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SLC_CAT_CHESTPLATE = ITEMS.register(
      "slc_cat_chestplate",
      () -> new SLCCatArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.HOLY_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SLC_CAT_LEGGINGS = ITEMS.register(
      "slc_cat_leggings",
      () -> new SLCCatArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.HOLY_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SLC_CAT_BOOTS = ITEMS.register(
      "slc_cat_boots",
      () -> new SLCCatArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.HOLY_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ARBITRIUM_ROBES_HELMET = ITEMS.register(
      "arbitrium_robes_helmet",
      () -> new ArbitriumRobesArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).fireResistant().rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ARBITRIUM_ROBES_CHESTPLATE = ITEMS.register(
      "arbitrium_robes_chestplate",
      () -> new ArbitriumRobesElytraArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ARBITRIUM_ROBES_LEGGINGS = ITEMS.register(
      "arbitrium_robes_leggings",
      () -> new ArbitriumRobesArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ARBITRIUM_ROBES_BOOTS = ITEMS.register(
      "arbitrium_robes_boots",
      () -> new ArbitriumRobesArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).fireResistant().rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).durability(Type.BOOTS.getDurability(128))
      )
   );
   public static final DeferredHolder<Item, Item> ASCENDED_ARBITRIUM_ROBES_HELMET = ITEMS.register(
      "ascended_arbitrium_robes_helmet",
      () -> new AscendedArbitriumRobesArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).fireResistant().rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ASCENDED_ARBITRIUM_ROBES_CHESTPLATE = ITEMS.register(
      "ascended_arbitrium_robes_chestplate",
      () -> new AscendedArbitriumRobesElytraArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ASCENDED_ARBITRIUM_ROBES_LEGGINGS = ITEMS.register(
      "ascended_arbitrium_robes_leggings",
      () -> new AscendedArbitriumRobesArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ASCENDED_ARBITRIUM_ROBES_BOOTS = ITEMS.register(
      "ascended_arbitrium_robes_boots",
      () -> new AscendedArbitriumRobesArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).fireResistant().rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_ARBITRIUM_ROBES_HELMET = ITEMS.register(
      "geckolib_arbitrium_robes_helmet",
      () -> new GeckolibArbitriumRobesArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_ARBITRIUM_ROBES_CHESTPLATE = ITEMS.register(
      "geckolib_arbitrium_robes_chestplate",
      () -> new GeckolibArbitriumRobesElytraItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_ARBITRIUM_ROBES_LEGGINGS = ITEMS.register(
      "geckolib_arbitrium_robes_leggings",
      () -> new GeckolibArbitriumRobesArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_ARBITRIUM_ROBES_BOOTS = ITEMS.register(
      "geckolib_arbitrium_robes_boots",
      () -> new GeckolibArbitriumRobesArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_ASCENDED_ARBITRIUM_ROBES_HELMET = ITEMS.register(
      "geckolib_ascended_arbitrium_robes_helmet",
      () -> new GeckolibAscendedArbitriumRobesArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_ASCENDED_ARBITRIUM_ROBES_CHESTPLATE = ITEMS.register(
      "geckolib_ascended_arbitrium_robes_chestplate",
      () -> new GeckolibAscendedArbitriumRobesElytraItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_ASCENDED_ARBITRIUM_ROBES_LEGGINGS = ITEMS.register(
      "geckolib_ascended_arbitrium_robes_leggings",
      () -> new GeckolibAscendedArbitriumRobesArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_ASCENDED_ARBITRIUM_ROBES_BOOTS = ITEMS.register(
      "geckolib_ascended_arbitrium_robes_boots",
      () -> new GeckolibAscendedArbitriumRobesArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> HAZEL_HELMET = ITEMS.register(
      "hazel_helmet",
      () -> new HazelArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> HAZEL_CHESTPLATE = ITEMS.register(
      "hazel_chestplate",
      () -> new HazelArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> HAZEL_HOLY_MOLLY_CHESTPLATE = ITEMS.register(
      "how_did_you_find_the_hazel_chestplate",
      () -> new HazelHOLYMOLYArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> HAZEL_LEGGINGS = ITEMS.register(
      "hazel_leggings",
      () -> new HazelArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> HAZEL_BOOTS = ITEMS.register(
      "hazel_boots",
      () -> new HazelArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.FIRE_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> MOTHIC_WITCH_HELMET = ITEMS.register(
      "mothic_witch_helmet",
      () -> new MothicWitchArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.HOLY_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> MOTHIC_WITCH_CHESTPLATE = ITEMS.register(
      "mothic_witch_chestplate",
      () -> new NerfedMothicWitchArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.HOLY_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> MOTHIC_WITCH_SECRET_CHESTPLATE = ITEMS.register(
      "how_did_you_find_the_mothic_witch_chestplate",
      () -> new MothicWitchArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.HOLY_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> MOTHIC_WITCH_LEGGINGS = ITEMS.register(
      "mothic_witch_leggings",
      () -> new MothicWitchArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.HOLY_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> MOTHIC_WITCH_BOOTS = ITEMS.register(
      "mothic_witch_boots",
      () -> new MothicWitchArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.HOLY_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> IRON431_HELMET = ITEMS.register(
      "iron431_helmet",
      () -> new Iron431ArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> IRON431_CHESTPLATE = ITEMS.register(
      "iron431_chestplate",
      () -> new Iron431ArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> IRON431_LEGGINGS = ITEMS.register(
      "iron431_leggings",
      () -> new Iron431ArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> IRON431_BOOTS = ITEMS.register(
      "iron431_boots",
      () -> new Iron431ArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ASCENDED_IRON431_HELMET = ITEMS.register(
      "ascended_iron431_helmet",
      () -> new AscendedIron431ArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ASCENDED_IRON431_CHESTPLATE = ITEMS.register(
      "ascended_iron431_chestplate",
      () -> new AscendedIron431ArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ASCENDED_IRON431_LEGGINGS = ITEMS.register(
      "ascended_iron431_leggings",
      () -> new AscendedIron431ArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ASCENDED_IRON431_BOOTS = ITEMS.register(
      "ascended_iron431_boots",
      () -> new AscendedIron431ArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SACRED_ROBES_HELMET = ITEMS.register(
      "sacred_robes_helmet",
      () -> new SacredRobesArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SACRED_ROBES_CHESTPLATE = ITEMS.register(
      "sacred_robes_chestplate",
      () -> new SacredRobesArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SACRED_ROBES_LEGGINGS = ITEMS.register(
      "sacred_robes_leggings",
      () -> new SacredRobesArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).fireResistant().rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> SACRED_ROBES_BOOTS = ITEMS.register(
      "sacred_robes_boots",
      () -> new SacredRobesArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).fireResistant().rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ASCENDED_SACRED_ROBES_HELMET = ITEMS.register(
      "ascended_sacred_robes_helmet",
      () -> new AscendedSacredRobesArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ASCENDED_SACRED_ROBES_CHESTPLATE = ITEMS.register(
      "ascended_sacred_robes_chestplate",
      () -> new AscendedSacredRobesArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ASCENDED_SACRED_ROBES_LEGGINGS = ITEMS.register(
      "ascended_sacred_robes_leggings",
      () -> new AscendedSacredRobesArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).fireResistant().rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ASCENDED_SACRED_ROBES_BOOTS = ITEMS.register(
      "ascended_sacred_robes_boots",
      () -> new AscendedSacredRobesArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).fireResistant().rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> LEMON_GOD_HELMET = ITEMS.register(
      "lemon_god_helmet",
      () -> new LemonGodArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> LEMON_GOD_CHESTPLATE = ITEMS.register(
      "lemon_god_chestplate",
      () -> new LemonGodArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> LEMON_GOD_LEGGINGS = ITEMS.register(
      "lemon_god_leggings",
      () -> new LemonGodArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> LEMON_GOD_BOOTS = ITEMS.register(
      "lemon_god_boots",
      () -> new LemonGodArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ASCENDED_LEMON_GOD_HELMET = ITEMS.register(
      "ascended_lemon_god_helmet",
      () -> new AscendedLemonGodArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ASCENDED_LEMON_GOD_CHESTPLATE = ITEMS.register(
      "ascended_lemon_god_chestplate",
      () -> new AscendedLemonGodArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ASCENDED_LEMON_GOD_LEGGINGS = ITEMS.register(
      "ascended_lemon_god_leggings",
      () -> new AscendedLemonGodArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ASCENDED_LEMON_GOD_BOOTS = ITEMS.register(
      "ascended_lemon_god_boots",
      () -> new AscendedLemonGodArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_LEMON_GOD_HELMET = ITEMS.register(
      "geckolib_lemon_god_helmet",
      () -> new GeckolibLemonGodArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_LEMON_GOD_CHESTPLATE = ITEMS.register(
      "geckolib_lemon_god_chestplate",
      () -> new GeckolibLemonGodArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_LEMON_GOD_LEGGINGS = ITEMS.register(
      "geckolib_lemon_god_leggings",
      () -> new GeckolibLemonGodArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_LEMON_GOD_BOOTS = ITEMS.register(
      "geckolib_lemon_god_boots",
      () -> new GeckolibLemonGodArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_ASCENDED_LEMON_GOD_HELMET = ITEMS.register(
      "geckolib_ascended_lemon_god_helmet",
      () -> new GeckolibAscendedLemonGodArmorItem(
         Type.HELMET,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.HELMET.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_ASCENDED_LEMON_GOD_CHESTPLATE = ITEMS.register(
      "geckolib_ascended_lemon_god_chestplate",
      () -> new GeckolibAscendedLemonGodArmorItem(
         Type.CHESTPLATE,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.CHESTPLATE.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_ASCENDED_LEMON_GOD_LEGGINGS = ITEMS.register(
      "geckolib_ascended_lemon_god_leggings",
      () -> new GeckolibAscendedLemonGodArmorItem(
         Type.LEGGINGS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.LEGGINGS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> GECKOLIB_ASCENDED_LEMON_GOD_BOOTS = ITEMS.register(
      "geckolib_ascended_lemon_god_boots",
      () -> new GeckolibAscendedLemonGodArmorItem(
         Type.BOOTS,
         ItemPropertiesHelper.equipment(1).rarity((Rarity)HnSRarities.DEUS_RARITY.getValue()).fireResistant().durability(Type.BOOTS.getDurability(64))
      )
   );
   public static final DeferredHolder<Item, Item> ENDERMAN_BLINDFOLD = ITEMS.register(
      "shadowwalker_enderman_blindfold",
      () -> new ShadowwalkerEndermanBlindfoldItem(Type.HELMET, ItemPropertiesHelper.equipment(1).durability(Type.HELMET.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> ENDERMAN_HOOD = ITEMS.register(
      "shadowwalker_enderman_hood",
      () -> new ShadowwalkerEndermanHoodItem(Type.HELMET, ItemPropertiesHelper.equipment(1).durability(Type.HELMET.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> ENDERMAN_MASKED = ITEMS.register(
      "shadowwalker_enderman_masked",
      () -> new ShadowwalkerEndermanArmorItem(Type.HELMET, ItemPropertiesHelper.equipment(1).durability(Type.HELMET.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> ENDERMAN_CHESTPLATE = ITEMS.register(
      "shadowwalker_enderman_chestplate",
      () -> new ShadowwalkerEndermanArmorItem(Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).durability(Type.CHESTPLATE.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> ENDERMAN_LEGGINGS = ITEMS.register(
      "shadowwalker_enderman_leggings",
      () -> new ShadowwalkerEndermanArmorItem(Type.LEGGINGS, ItemPropertiesHelper.equipment(1).durability(Type.LEGGINGS.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> ENDERMAN_BOOTS = ITEMS.register(
      "shadowwalker_enderman_boots",
      () -> new ShadowwalkerEndermanArmorItem(Type.BOOTS, ItemPropertiesHelper.equipment(1).durability(Type.BOOTS.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> PYROMANCER_BRUTE_HELMET = ITEMS.register(
      "pyromancer_brute_helmet", () -> new PyromancerBruteArmorItem(Type.HELMET, ItemPropertiesHelper.equipment(1).durability(Type.HELMET.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> PYROMANCER_BRUTE_CHESTPLATE = ITEMS.register(
      "pyromancer_brute_chestplate",
      () -> new PyromancerBruteArmorItem(Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).rarity(Rarity.EPIC).durability(Type.CHESTPLATE.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> BISHOP_OF_DECEIT_HELMET = ITEMS.register(
      "bishop_of_deceit_helmet", () -> new BishopOfDeceitArmorItem(Type.HELMET, ItemPropertiesHelper.equipment(1).durability(Type.HELMET.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> BISHOP_OF_DECEIT_CHESTPLATE = ITEMS.register(
      "bishop_of_deceit_chestplate",
      () -> new BishopOfDeceitArmorItem(Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).durability(Type.CHESTPLATE.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> NECROMANCER_HELMET = ITEMS.register(
      "necromancer_helmet", () -> new NecromancerArmorItem(Type.HELMET, ItemPropertiesHelper.equipment(1).durability(Type.HELMET.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> NECROMANCER_CHESTPLATE = ITEMS.register(
      "necromancer_chestplate",
      () -> new NecromancerArmorItem(Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).durability(Type.CHESTPLATE.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> NECROMANCER_LEGGINGS = ITEMS.register(
      "necromancer_leggings", () -> new NecromancerArmorItem(Type.LEGGINGS, ItemPropertiesHelper.equipment(1).durability(Type.LEGGINGS.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> NAMELESS_ONE_HELMET = ITEMS.register(
      "nameless_one_helmet", () -> new NamelessOneArmorItem(Type.HELMET, ItemPropertiesHelper.equipment(1).durability(Type.HELMET.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> NAMELESS_ONE_CHESTPLATE = ITEMS.register(
      "nameless_one_chestplate",
      () -> new NamelessOneArmorItem(Type.CHESTPLATE, ItemPropertiesHelper.equipment(1).durability(Type.CHESTPLATE.getDurability(37)))
   );
   public static final DeferredHolder<Item, Item> NAMELESS_ONE_LEGGINGS = ITEMS.register(
      "nameless_one_leggings", () -> new NamelessOneArmorItem(Type.LEGGINGS, ItemPropertiesHelper.equipment(1).durability(Type.LEGGINGS.getDurability(37)))
   );

   public static Collection<DeferredHolder<Item, ? extends Item>> getHnSItems() {
      return ITEMS.getEntries();
   }

   public static void register(IEventBus eventBus) {
      ITEMS.register(eventBus);
   }
}
