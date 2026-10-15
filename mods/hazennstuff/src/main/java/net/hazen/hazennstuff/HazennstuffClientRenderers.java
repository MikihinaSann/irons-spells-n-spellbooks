package net.hazen.hazennstuff;

import java.util.function.Supplier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;

public final class HazennstuffClientRenderers {
   private HazennstuffClientRenderers() {
   }

   public static GeoRenderProvider itemProvider(Supplier<Object> supplier) {
      return new GeoRenderProvider() {
         private net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer renderer;

         @Override
         public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
            if (this.renderer == null) {
               this.renderer = (net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer) supplier.get();
            }
            return this.renderer;
         }
      };
   }

   public static GeoRenderProvider armorProvider(Supplier<Object> supplier) {
      return new GeoRenderProvider() {
         private software.bernie.geckolib.renderer.GeoArmorRenderer<?> renderer;

         @Override
         public <T extends LivingEntity> net.minecraft.client.model.HumanoidModel<?> getGeoArmorRenderer(
            @Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable net.minecraft.client.model.HumanoidModel<T> original
         ) {
            if (this.renderer == null) {
               this.renderer = (software.bernie.geckolib.renderer.GeoArmorRenderer<?>) supplier.get();
            }
            return this.renderer;
         }
      };
   }
   public static Object blossomOfTheEnchantress() {
      return new net.hazen.hazennstuff.Item.Staves.BlossomOfTheEnchantress.BlossomOfTheEnchantressRenderer();
   }
   public static Object divineScepter() {
      return new net.hazen.hazennstuff.Item.Staves.DivineScepter.DivineScepterRenderer();
   }
   public static Object enderconicScepter() {
      return new net.hazen.hazennstuff.Item.Staves.EnderconicScepter.EnderconicScepterRenderer();
   }
   public static Object frierenStaff() {
      return new net.hazen.hazennstuff.Item.Staves.Frieren.FrierenStaffRenderer();
   }
   public static Object grimoireStaff() {
      return new net.hazen.hazennstuff.Item.Staves.GrimoireStaff.GrimoireStaffRenderer();
   }
   public static Object rodOfDiscord() {
      return new net.hazen.hazennstuff.Item.Staves.RodOfDiscord.RodOfDiscordRenderer();
   }
   public static Object whiteLilyStaff() {
      return new net.hazen.hazennstuff.Item.Staves.WhiteLilyStaff.WhiteLilyStaffRenderer();
   }
   public static Object wisewoodCane() {
      return new net.hazen.hazennstuff.Item.Staves.WisewoodCane.WisewoodCaneRenderer();
   }
   public static Object concentratedFireUpgradeOrb() {
      return new net.hazen.hazennstuff.Item.UpgradeOrbs.Concentrated.Fire.ConcentratedFireUpgradeOrbRenderer();
   }
   public static Object concentratedIceUpgradeOrb() {
      return new net.hazen.hazennstuff.Item.UpgradeOrbs.Concentrated.Ice.ConcentratedIceUpgradeOrbRenderer();
   }
   public static Object pickaxeAxeItem() {
      return new net.hazen.hazennstuff.Item.Util.PickaxeAxe.PickaxeAxeRenderer();
   }
   public static Object pickaxeAxeDivineGoldItem() {
      return new net.hazen.hazennstuff.Item.Util.PickaxeAxe.DivineGold.PickaxeAxeDivineGoldRenderer();
   }
   public static Object spectralPickaxeItem() {
      return new net.hazen.hazennstuff.Item.Util.SpectralPickaxe.SpectralPickaxeRenderer();
   }
   public static Object devastator() {
      return new net.hazen.hazennstuff.Item.Weapons.Ascended.Devastator.Dormant.DevastatorRenderer();
   }
   public static Object devastatorT1() {
      return new net.hazen.hazennstuff.Item.Weapons.Ascended.Devastator.T1.DevastatorT1Renderer();
   }
   public static Object devastatorT2() {
      return new net.hazen.hazennstuff.Item.Weapons.Ascended.Devastator.T2.DevastatorT2Renderer();
   }
   public static Object devastatorT3() {
      return new net.hazen.hazennstuff.Item.Weapons.Ascended.Devastator.T3.DevastatorT3Renderer();
   }
   public static Object draconicSplitterItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Ascended.DraconicSplitter.DraconicSplitterRenderer();
   }
   public static Object ionicSplitterItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Ascended.IonicSplitter.Dormant.IonicSplitterRenderer();
   }
   public static Object ionicSplitterT1Item() {
      return new net.hazen.hazennstuff.Item.Weapons.Ascended.IonicSplitter.T1.IonicSplitterT1Renderer();
   }
   public static Object ionicSplitterT2Item() {
      return new net.hazen.hazennstuff.Item.Weapons.Ascended.IonicSplitter.T2.IonicSplitterT2Renderer();
   }
   public static Object ionicSplitterT3Item() {
      return new net.hazen.hazennstuff.Item.Weapons.Ascended.IonicSplitter.T3.IonicSplitterT3Renderer();
   }
   public static Object oFortuna() {
      return new net.hazen.hazennstuff.Item.Weapons.Ascended.OFortuna.Dormant.OFortunaRenderer();
   }
   public static Object oFortunaT1() {
      return new net.hazen.hazennstuff.Item.Weapons.Ascended.OFortuna.T1.OFortunaT1Renderer();
   }
   public static Object oFortunaT2() {
      return new net.hazen.hazennstuff.Item.Weapons.Ascended.OFortuna.T2.OFortunaT2Renderer();
   }
   public static Object oFortunaT3() {
      return new net.hazen.hazennstuff.Item.Weapons.Ascended.OFortuna.T3.OFortunaT3Renderer();
   }
   public static Object provocation() {
      return new net.hazen.hazennstuff.Item.Weapons.Ascended.Provocation.Dormant.ProvocationRenderer();
   }
   public static Object provocationT1() {
      return new net.hazen.hazennstuff.Item.Weapons.Ascended.Provocation.T1.ProvocationT1Renderer();
   }
   public static Object provocationT2() {
      return new net.hazen.hazennstuff.Item.Weapons.Ascended.Provocation.T2.ProvocationT2Renderer();
   }
   public static Object provocationT3() {
      return new net.hazen.hazennstuff.Item.Weapons.Ascended.Provocation.T3.ProvocationT3Renderer();
   }
   public static Object ravensBaneItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Ascended.RavensBane.RavensBaneRenderer();
   }
   public static Object umbranova() {
      return new net.hazen.hazennstuff.Item.Weapons.Ascended.Umbranova.Dormant.UmbranovaRenderer();
   }
   public static Object umbranovaT1() {
      return new net.hazen.hazennstuff.Item.Weapons.Ascended.Umbranova.T1.UmbranovaT1Renderer();
   }
   public static Object umbranovaT2() {
      return new net.hazen.hazennstuff.Item.Weapons.Ascended.Umbranova.T2.UmbranovaT2Renderer();
   }
   public static Object umbranovaT3() {
      return new net.hazen.hazennstuff.Item.Weapons.Ascended.Umbranova.T3.UmbranovaT3Renderer();
   }
   public static Object ancientWarriorsAxeItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.AncientWarriorsAxe.AncientWarriorsAxeRenderer();
   }
   public static Object beongaeItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.Beongae.BeongaeRenderer();
   }
   public static Object bladeOfGrassItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.BladeOfGrass.BladeOfGrassRenderer();
   }
   public static Object bountifulHarvestItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.BountifulHarvest.BountifulHarvestRenderer();
   }
   public static Object dawnmakerItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.Dawnmaker.DawnmakerRenderer();
   }
   public static Object divineGreatswordItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.DivineGreatsword.DivineGreatswordRenderer();
   }
   public static Object hazensExcaliburItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.Excalibur.HazenStyle.HazensExcaliburRenderer();
   }
   public static Object iSSExcaliburItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.Excalibur.ISSxTerraria.ISSExcaliburRenderer();
   }
   public static Object excaliburItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.Excalibur.Terraria.ExcaliburRenderer();
   }
   public static Object fireblossomRapierItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.FireblossomRapier.FireblossomRapierRenderer();
   }
   public static Object firebrandItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.Firebrand.FirebrandRenderer();
   }
   public static Object frostburnDaggerItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.FrostburnDagger.FrostburnDaggerRenderer();
   }
   public static Object hammushItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.Hammush.HammushRenderer();
   }
   public static Object icePikeItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.IcePike.IcePikeRenderer();
   }
   public static Object legionnaireWarlockAxeItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.LegionnaireWarlockAxe.LegionnaireWarlockAxeRenderer();
   }
   public static Object lightsBaneItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.LightsBane.LightsBaneRenderer();
   }
   public static Object mageBaneItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.MageBane.MageBaneRenderer();
   }
   public static Object maliceItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.Malice.MaliceRenderer();
   }
   public static Object meowmereItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.Meowmere.MeowmereRenderer();
   }
   public static Object muramasaItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.Muramasa.MuramasaRenderer();
   }
   public static Object nightsEdgeItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.NightsEdge.NightsEdgeRenderer();
   }
   public static Object obsidianClaymoreItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.ObsidianClaymore.ObsidianClaymoreRenderer();
   }
   public static Object skyscorcherItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.Skyscorcher.SkyscorcherRenderer();
   }
   public static Object spectrumItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.Spectrum.SpectrumRenderer();
   }
   public static Object starfuryItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.Starfury.StarfuryRenderer();
   }
   public static Object theDevourerItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.TheDevourer.TheDevourerRenderer();
   }
   public static Object thornChakramItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.ThornChakram.ThornChakramRenderer();
   }
   public static Object trueNightsEdgeItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.TrueNightsEdge.TrueNightsEdgeRenderer();
   }
   public static Object vampireKnivesItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.VampireKnives.VampireKnivesRenderer();
   }
   public static Object warFlamingLanceItem() {
      return new net.hazen.hazennstuff.Item.Weapons.Generic.WarFlamingLance.WarFlamingLanceRenderer();
   }
         public static Object abberantPredatorArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.AbberantPredator.AbberantPredatorArmorModel());
   }

         public static Object alchemistSupremeArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.AlchemistSupreme.AlchemistSupremeArmorModel());
   }

         public static Object geckolibArbitriumRobesArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.ArbitriumRobes.GeckolibArbitriumRobesArmorModel());
   }

         public static Object geckolibArbitriumRobesElytraItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.ArbitriumRobes.GeckolibArbitriumRobesArmorModel());
   }

         public static Object geckolibAscendedArbitriumRobesArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.ArbitriumRobes.Ascended.GeckolibAscendedArbitriumRobesArmorModel());
   }

         public static Object geckolibAscendedArbitriumRobesElytraItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.ArbitriumRobes.Ascended.GeckolibAscendedArbitriumRobesArmorModel());
   }

         public static Object atlasArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.Atlas.AtlasArmorModel());
   }

         public static Object bishopOfDeceitArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.BishopOfDeceitArmor.BishopOfDeceitArmorModel());
   }

      public static Object geckolibBlazeborneArmorItem() {
      return new net.hazen.hazennstuff.Item.Armor.Geckolib.Blazeborne.GeckolibBlazeborneArmorRenderer(new net.hazen.hazennstuff.Item.Armor.Geckolib.Blazeborne.GeckolibBlazeborneArmorModel());
   }

         public static Object bountyHunterArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.BountyHunter.BountyHunterArmorModel());
   }

         public static Object calamitasArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.Calamitas.CalamitasArmorModel());
   }

         public static Object calamitasChestplateArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.Calamitas.CalamitasArmorModel());
   }

         public static Object geckolibChargedScourgeArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.ChargedScourge.GeckolibChargedScourgeArmorModel());
   }

         public static Object chlorophyteArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.Chlorophyte.ChlorophyteArmorModel());
   }

         public static Object chlorophyteHeadgearArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.Chlorophyte.Headgear.ChlorophyteHeadgearArmorModel());
   }

         public static Object chlorophyteHelmetArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.Chlorophyte.Helmet.ChlorophyteHelmetArmorModel());
   }

         public static Object chlorophyteMaskArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.Chlorophyte.Mask.ChlorophyteMaskArmorModel());
   }

         public static Object geckolibCreakingSorcererArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.CreakingSorcerer.GeckolibCreakingSorcererArmorModel());
   }

         public static Object cryogenicRulerArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.CryogenicRuler.CryogenicRulerArmorModel());
   }

         public static Object crystalArachnidArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.CrystalArachnid.CrystalArachnidArmorModel());
   }

         public static Object geckolibDarkRitualTemplarArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.DarkRitualTemplar.GeckolibDarkRitualTemplarArmorModel());
   }

         public static Object dreadsteelKnightArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.DreadsteelKnight.DreadsteelArmorModel());
   }

         public static Object geckolibEnderDragonArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.EnderDragon.GeckolibEnderDragonArmorModel());
   }

         public static Object geckolibFireblossomBattlemageArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.FireblossomBattlemage.GeckolibFireblossomBattlemageArmorModel());
   }

         public static Object geckolibFireblossomBattlemageCrownedArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.FireblossomBattlemage.GeckolibFireblossomBattlemageArmorModel());
   }

         public static Object geckolibFireblossomBattlemageHelmetArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.FireblossomBattlemage.GeckolibFireblossomBattlemageArmorModel());
   }

         public static Object fleshMassArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.FleshMass.FleshMassArmorModel());
   }

         public static Object frierenArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.Frieren.FrierenArmorModel());
   }

         public static Object frostbiteHunterArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.FrostbiteHunter.FrostbiteHunterArmorModel());
   }

         public static Object frostbiteKnightArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.FrostbiteKnight.FrostbiteKnightArmorModel());
   }

         public static Object geckolibGabrielArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.GabrielULTRAKILL.GeckolibGabrielArmorModel());
   }

         public static Object geckolibGabrielElytraItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.GabrielULTRAKILL.GeckolibGabrielArmorModel());
   }

         public static Object garmentsOfTheFirstFlamebearerArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.GarmentsOfTheFirstFlamebearer.GarmentsOfTheFirstFlamebearerArmorModel());
   }

         public static Object garmentsOfTheFirstFlamebearerChestplateArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.GarmentsOfTheFirstFlamebearer.GarmentsOfTheFirstFlamebearerArmorModel());
   }

         public static Object garmentsOfTheFirstFlamebearerSoulArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.GarmentsOfTheFirstFlamebearer.SoulMode.GarmentsOfTheFirstFlamebearerSoulArmorModel());
   }

         public static Object garmentsOfTheFirstFlamebearerSoulChestplateArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.GarmentsOfTheFirstFlamebearer.SoulMode.GarmentsOfTheFirstFlamebearerSoulArmorModel());
   }

         public static Object giornoGiovannaArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.GiornoGiovanna.GiornoGiovannaArmorModel());
   }

         public static Object hazelArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.Hazel.HazelArmorModel());
   }

         public static Object hazelHOLYMOLYArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.Hazel.HazelArmorModel());
   }

         public static Object hertaPuppetArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.HertaPuppet.HertaPuppetArmorModel());
   }

         public static Object geckolibInfestationArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.Infestation.GeckolibInfestationArmorModel());
   }

         public static Object iron431ArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.Iron431.Iron431ArmorModel());
   }

         public static Object ascendedIron431ArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.Iron431.Ascended.AscendedIron431ArmorModel());
   }

         public static Object oldLegionnaireArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.Legionnaire.OldLegionnaireArmorModel());
   }

         public static Object legionnaireCommanderArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.LegionnaireCommander.LegionnaireCommanderArmorModel());
   }

         public static Object legionnaireRulerArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.LegionnaireRuler.LegionnaireRulerArmorModel());
   }

         public static Object soulLegionnaireRulerArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.LegionnaireRuler.Soul.SoulLegionnaireRulerArmorModel());
   }

         public static Object geckolibLemonGodArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.LemonGod.GeckolibLemonGodArmorModel());
   }

         public static Object geckolibAscendedLemonGodArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.LemonGod.Ascended.GeckolibAscendedLemonGodArmorModel());
   }

         public static Object mageHunterArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.MageHunter.MageHunterArmorModel());
   }

         public static Object maverickArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.Maverick.MaverickArmorModel());
   }

         public static Object minerArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.Miner.MinerArmorModel());
   }

         public static Object mithrilBattlemageArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.MithrilBattlemage.MithrilBattlemageArmorModel());
   }

         public static Object mothicWitchArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.MothicWitch.MothicWitchArmorModel());
   }

         public static Object nerfedMothicWitchArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.MothicWitch.MothicWitchArmorModel());
   }

         public static Object myceliumGuardianArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.MyceliumGuardian.MyceliumGuardianArmorModel());
   }

         public static Object namelessOneArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.NamelessOneArmor.NamelessOneArmorModel());
   }

         public static Object necromancerArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.NecromancerArmor.NecromancerArmorModel());
   }

         public static Object akitaNeruArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.Neru.AkitaNeruArmorModel());
   }

         public static Object permafrostPrinceArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.PermafrostPrince.PermafrostPrinceArmorModel());
   }

         public static Object projectSekaiArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.ProjectSekai.ProjectSekaiArmorModel());
   }

         public static Object pyriumArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.PyriumArmor.PyriumArmorModel());
   }

         public static Object pyriumBattlemageArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.PyriumBattlemage.PyriumBattlemageArmorModel());
   }

         public static Object pyromancerBruteArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.PyromancerBrute.PyromancerBruteArmorModel());
   }

         public static Object rottenGirlArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.RottenGirl.RottenGirlArmorModel());
   }

         public static Object sacredRobesArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.SacredRobes.SacredRobesArmorModel());
   }

         public static Object ascendedSacredRobesArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.SacredRobes.Ascended.AscendedSacredRobesArmorModel());
   }

         public static Object geckolibSeraphArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.Seraph.GeckolibSeraphArmorModel());
   }

         public static Object shadowScaleArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.ShadowScale.ShadowScaleArmorModel());
   }

         public static Object shadowwalkerEndermanArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.ShadowWalkerEnderman.ShadowwalkerEndermanArmorModel());
   }

         public static Object shadowwalkerEndermanBlindfoldItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.ShadowWalkerEnderman.ShadowwalkerEndermanArmorModel());
   }

         public static Object shadowwalkerEndermanHoodItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.ShadowWalkerEnderman.ShadowwalkerEndermanArmorModel());
   }

         public static Object geckolibSoulFlameArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.SoulFlame.GeckolibSoulFlameArmorModel());
   }

         public static Object spectralSpelunkerArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.SpectralSpelunker.SpectralSpelunkerArmorModel());
   }

         public static Object geckolibSupremeWitchArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.SupremeWitch.GeckolibSupremeWitchArmorModel());
   }

         public static Object synthesizerVArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.SynthesizerV.SynthesizerVArmorModel());
   }

         public static Object geckolibTheWitherArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.TheWither.GeckolibTheWitherArmorModel());
   }

         public static Object thunderProwlerArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.ThunderProwler.ThunderProwlerArmorModel());
   }

         public static Object utauArmorItem() {
      return new io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer<>(new net.hazen.hazennstuff.Item.Armor.Geckolib.Utau.UtauArmorModel());
   }
}
