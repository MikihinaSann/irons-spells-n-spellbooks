package net.hazen.hazennstuff;

import com.mojang.logging.LogUtils;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.render.SpellBookCurioRenderer;
import mod.azure.azurelib.common.animation.cache.AzIdentityRegistry;
import mod.azure.azurelib.common.render.armor.AzArmorRendererRegistry;
import mod.azure.azurelib.common.render.item.AzItemRendererRegistry;
import net.acetheeldritchking.aces_spell_utils.entity.render.items.SheathCurioRenderer;
import net.acetheeldritchking.aces_spell_utils.items.curios.SheathCurioItem;
import net.hazen.hazennstuff.Compat.AACompat;
import net.hazen.hazennstuff.Compat.ArsNoveauCompat;
import net.hazen.hazennstuff.Compat.CSCompat;
import net.hazen.hazennstuff.Compat.DTECompat;
import net.hazen.hazennstuff.Compat.ESSRCompat;
import net.hazen.hazennstuff.Compat.FLCompat;
import net.hazen.hazennstuff.Compat.MFTECompat;
import net.hazen.hazennstuff.Compat.MalumCompat;
import net.hazen.hazennstuff.Item.Armor.AzureLib.ArbitriumRobes.ArbitriumRobesArmorRenderer;
import net.hazen.hazennstuff.Item.Armor.AzureLib.Blazeborne.BlazeborneArmorRenderer;
import net.hazen.hazennstuff.Item.Armor.AzureLib.CreakingSorcerer.CreakingSorcererArmorRenderer;
import net.hazen.hazennstuff.Item.Armor.AzureLib.DarkRitualTemplar.DarkRitualTemplarArmorRenderer;
import net.hazen.hazennstuff.Item.Armor.AzureLib.EnderDragon.EnderDragonArmorRenderer;
import net.hazen.hazennstuff.Item.Armor.AzureLib.FireblossomBattlemage.FireblossomBattlemageCrownedArmorRenderer;
import net.hazen.hazennstuff.Item.Armor.AzureLib.FireblossomBattlemage.FireblossomBattlemageHelmetArmorRenderer;
import net.hazen.hazennstuff.Item.Armor.AzureLib.GabrielULTRAKILL.GabrielArmorRenderer;
import net.hazen.hazennstuff.Item.Armor.AzureLib.Infestation.InfestationArmorRenderer;
import net.hazen.hazennstuff.Item.Armor.AzureLib.LemonGod.LemonGodArmorRenderer;
import net.hazen.hazennstuff.Item.Armor.AzureLib.SLCCat.SLCCatArmorRenderer;
import net.hazen.hazennstuff.Item.Armor.AzureLib.Seraph.SeraphArmorRenderer;
import net.hazen.hazennstuff.Item.Armor.AzureLib.SoulFlame.SoulFlameArmorRenderer;
import net.hazen.hazennstuff.Item.Armor.AzureLib.SupremeWitch.SupremeWitchArmorRenderer;
import net.hazen.hazennstuff.Item.Armor.AzureLib.TheWither.TheWitherArmorRenderer;
import net.hazen.hazennstuff.Item.Block.HnSBlockEntities;
import net.hazen.hazennstuff.Item.Block.HnSBlocks;
import net.hazen.hazennstuff.Item.Curios.Sheaths.GalvanizedSheath.GalvanizedSheathItemRenderer;
import net.hazen.hazennstuff.Item.Curios.Sheaths.ScrollSheath.ScrollSheathItemRenderer;
import net.hazen.hazennstuff.Item.Curios.Spellbooks.EnergizedCoreSpellbook.EnergizedCoreSpellbookRenderer;
import net.hazen.hazennstuff.Item.Curios.Wings.ArbitriumWings.ArbitriumWingsCurioItemRenderer;
import net.hazen.hazennstuff.Item.Curios.Wings.ArbitriumWings.ArbitriumWingsCurioRenderer;
import net.hazen.hazennstuff.Item.HnSUtilities.HnSArmorMaterials;
import net.hazen.hazennstuff.Item.Staves.InsaniaAeternus.InsaniaAeternusRenderer;
import net.hazen.hazennstuff.Item.Staves.SoulcallingScepter.SoulcallingScepterRenderer;
import net.hazen.hazennstuff.Item.Weapons.Generic.HammerOfJustice.HammerOfJusticeRenderer;
import net.hazen.hazennstuff.Item.Weapons.Generic.Terraprisma.TerraprismaRenderer;
import net.hazen.hazennstuff.Item.Weapons.Generic.Volcano.VolcanoRenderer;
import net.hazen.hazennstuff.Registries.HnSAttributeRegistry;
import net.hazen.hazennstuff.Registries.HnSCreativeModeTabs;
import net.hazen.hazennstuff.Registries.HnSEffects;
import net.hazen.hazennstuff.Registries.HnSEntityRegistry;
import net.hazen.hazennstuff.Registries.HnSItemRegistry;
import net.hazen.hazennstuff.Registries.HnSParticleRegistry;
import net.hazen.hazennstuff.Registries.HnSSchoolRegistry;
import net.hazen.hazennstuff.Registries.HnSSounds;
import net.hazen.hazennstuff.Spells.HnSSpellRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod("hazennstuff")
public class HazenNStuff {
   public static final String MOD_ID = "hazennstuff";
   public static final Logger LOGGER = LogUtils.getLogger();

   public HazenNStuff(IEventBus modEventBus, ModContainer modContainer) {
      NeoForge.EVENT_BUS.register(this);
      MalumCompat.init();
      ArsNoveauCompat.init();
      AACompat.init();
      CSCompat.init();
      DTECompat.init();
      ESSRCompat.init();
      FLCompat.init();
      MFTECompat.init();
      HnSCreativeModeTabs.register(modEventBus);
      HnSItemRegistry.register(modEventBus);
      HnSBlocks.register(modEventBus);
      HnSBlockEntities.register(modEventBus);
      HnSArmorMaterials.register(modEventBus);
      HnSEffects.register(modEventBus);
      HnSParticleRegistry.register(modEventBus);
      HnSSounds.register(modEventBus);
      HnSEntityRegistry.register(modEventBus);
      HnSSpellRegistries.register(modEventBus);
      HnSAttributeRegistry.register(modEventBus);
      HnSSchoolRegistry.register(modEventBus);
      modEventBus.addListener(this::commonSetup);
      modContainer.registerConfig(Type.COMMON, HnSConfig.SPEC);
   }

   @SubscribeEvent
   public void onServerStarting(ServerStartingEvent event) {
   }

   private void commonSetup(FMLCommonSetupEvent event) {
      AzIdentityRegistry.register(
         (Item)HnSItemRegistry.ARBITRIUM_ROBES_HELMET.get(),
         new Item[]{
            (Item)HnSItemRegistry.ARBITRIUM_ROBES_CHESTPLATE.get(),
            (Item)HnSItemRegistry.ARBITRIUM_ROBES_LEGGINGS.get(),
            (Item)HnSItemRegistry.ARBITRIUM_ROBES_BOOTS.get(),
            (Item)HnSItemRegistry.ASCENDED_ARBITRIUM_ROBES_HELMET.get(),
            (Item)HnSItemRegistry.ASCENDED_ARBITRIUM_ROBES_CHESTPLATE.get(),
            (Item)HnSItemRegistry.ASCENDED_ARBITRIUM_ROBES_LEGGINGS.get(),
            (Item)HnSItemRegistry.ASCENDED_ARBITRIUM_ROBES_BOOTS.get(),
            (Item)HnSItemRegistry.FIREBLOSSOM_BATTLEMAGE_HELMET.get(),
            (Item)HnSItemRegistry.FIREBLOSSOM_BATTLEMAGE_CHESTPLATE.get(),
            (Item)HnSItemRegistry.FIREBLOSSOM_BATTLEMAGE_LEGGINGS.get(),
            (Item)HnSItemRegistry.FIREBLOSSOM_BATTLEMAGE_BOOTS.get(),
            (Item)HnSItemRegistry.FIREBLOSSOM_BATTLEMAGE_BOOTS.get(),
            (Item)HnSItemRegistry.INFESTATION_HELMET.get(),
            (Item)HnSItemRegistry.INFESTATION_CHESTPLATE.get(),
            (Item)HnSItemRegistry.INFESTATION_LEGGINGS.get(),
            (Item)HnSItemRegistry.INFESTATION_BOOTS.get(),
            (Item)HnSItemRegistry.BLAZEBORNE_HELMET.get(),
            (Item)HnSItemRegistry.BLAZEBORNE_CHESTPLATE.get(),
            (Item)HnSItemRegistry.BLAZEBORNE_LEGGINGS.get(),
            (Item)HnSItemRegistry.BLAZEBORNE_BOOTS.get(),
            (Item)HnSItemRegistry.LEMON_GOD_HELMET.get(),
            (Item)HnSItemRegistry.LEMON_GOD_CHESTPLATE.get(),
            (Item)HnSItemRegistry.LEMON_GOD_LEGGINGS.get(),
            (Item)HnSItemRegistry.LEMON_GOD_BOOTS.get(),
            (Item)HnSItemRegistry.ASCENDED_LEMON_GOD_HELMET.get(),
            (Item)HnSItemRegistry.ASCENDED_LEMON_GOD_CHESTPLATE.get(),
            (Item)HnSItemRegistry.ASCENDED_LEMON_GOD_LEGGINGS.get(),
            (Item)HnSItemRegistry.ASCENDED_LEMON_GOD_BOOTS.get(),
            (Item)HnSItemRegistry.THE_WITHER_HELMET.get(),
            (Item)HnSItemRegistry.THE_WITHER_CHESTPLATE.get(),
            (Item)HnSItemRegistry.THE_WITHER_LEGGINGS.get(),
            (Item)HnSItemRegistry.THE_WITHER_BOOTS.get(),
            (Item)HnSItemRegistry.GABRIEL_ULTRAKILL_HELMET.get(),
            (Item)HnSItemRegistry.GABRIEL_ULTRAKILL_CHESTPLATE.get(),
            (Item)HnSItemRegistry.GABRIEL_ULTRAKILL_LEGGINGS.get(),
            (Item)HnSItemRegistry.GABRIEL_ULTRAKILL_BOOTS.get(),
            (Item)HnSItemRegistry.HAMMER_OF_JUSTICE.get(),
            (Item)HnSItemRegistry.VOLCANO.get(),
            (Item)HnSItemRegistry.TERRAPRISMA.get(),
            (Item)HnSItemRegistry.SOULCALLING_SCEPTER.get(),
            (Item)HnSItemRegistry.INSANIA_AETERNUS.get(),
            (Item)HnSItemRegistry.ENERGIZED_CORE_SPELLBOOK.get(),
            (Item)HnSItemRegistry.ARBITRIUM_WINGS.get(),
            (Item)HnSItemRegistry.GALVANIZED_SHEATH.get(),
            (Item)HnSItemRegistry.SCROLL_SHEATH.get()
         }
      );
   }

   public static ResourceLocation id(@NotNull String path) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", path);
   }

   @EventBusSubscriber({Dist.CLIENT})
   public static class ClientModEvents {
      @SubscribeEvent
      public static void onClientSetup(FMLClientSetupEvent event) {
         HnSItemRegistry.getHnSItems()
            .stream()
            .filter(item -> item.get() instanceof SpellBook)
            .forEach(item -> CuriosRendererRegistry.register((Item)item.get(), SpellBookCurioRenderer::new));
         HnSItemRegistry.getHnSItems()
            .stream()
            .filter(item -> item.get() instanceof SheathCurioItem)
            .forEach(item -> CuriosRendererRegistry.register((Item)item.get(), SheathCurioRenderer::new));
         AzArmorRendererRegistry.register(
            ArbitriumRobesArmorRenderer::new,
            (Item)HnSItemRegistry.ARBITRIUM_ROBES_HELMET.get(),
            new Item[]{
               (Item)HnSItemRegistry.ARBITRIUM_ROBES_CHESTPLATE.get(),
               (Item)HnSItemRegistry.ARBITRIUM_ROBES_LEGGINGS.get(),
               (Item)HnSItemRegistry.ARBITRIUM_ROBES_BOOTS.get()
            }
         );
         AzArmorRendererRegistry.register(
            CreakingSorcererArmorRenderer::new,
            (Item)HnSItemRegistry.CREAKING_HELMET.get(),
            new Item[]{
               (Item)HnSItemRegistry.CREAKING_CHESTPLATE.get(), (Item)HnSItemRegistry.CREAKING_LEGGINGS.get(), (Item)HnSItemRegistry.CREAKING_BOOTS.get()
            }
         );
         AzArmorRendererRegistry.register(
            DarkRitualTemplarArmorRenderer::new,
            (Item)HnSItemRegistry.DARK_RITUAL_TEMPLAR_HELMET.get(),
            new Item[]{
               (Item)HnSItemRegistry.DARK_RITUAL_TEMPLAR_CHESTPLATE.get(),
               (Item)HnSItemRegistry.DARK_RITUAL_TEMPLAR_LEGGINGS.get(),
               (Item)HnSItemRegistry.DARK_RITUAL_TEMPLAR_BOOTS.get()
            }
         );
         AzArmorRendererRegistry.register(
            EnderDragonArmorRenderer::new,
            (Item)HnSItemRegistry.ENDER_DRAGON_HELMET.get(),
            new Item[]{
               (Item)HnSItemRegistry.ENDER_DRAGON_CHESTPLATE.get(),
               (Item)HnSItemRegistry.ENDER_DRAGON_LEGGINGS.get(),
               (Item)HnSItemRegistry.ENDER_DRAGON_BOOTS.get()
            }
         );
         AzArmorRendererRegistry.register(
            FireblossomBattlemageHelmetArmorRenderer::new,
            (Item)HnSItemRegistry.FIREBLOSSOM_BATTLEMAGE_HELMET.get(),
            new Item[]{
               (Item)HnSItemRegistry.FIREBLOSSOM_BATTLEMAGE_CHESTPLATE.get(),
               (Item)HnSItemRegistry.FIREBLOSSOM_BATTLEMAGE_LEGGINGS.get(),
               (Item)HnSItemRegistry.FIREBLOSSOM_BATTLEMAGE_BOOTS.get()
            }
         );
         AzArmorRendererRegistry.register(FireblossomBattlemageCrownedArmorRenderer::new, (Item)HnSItemRegistry.FIREBLOSSOM_BATTLEMAGE_CROWN.get(), new Item[0]);
         AzArmorRendererRegistry.register(
            SeraphArmorRenderer::new,
            (Item)HnSItemRegistry.SERAPH_HELMET.get(),
            new Item[]{(Item)HnSItemRegistry.SERAPH_CHESTPLATE.get(), (Item)HnSItemRegistry.SERAPH_LEGGINGS.get(), (Item)HnSItemRegistry.SERAPH_BOOTS.get()}
         );
         AzArmorRendererRegistry.register(
            SoulFlameArmorRenderer::new,
            (Item)HnSItemRegistry.SOUL_FLAME_HELMET.get(),
            new Item[]{
               (Item)HnSItemRegistry.SOUL_FLAME_CHESTPLATE.get(), (Item)HnSItemRegistry.SOUL_FLAME_LEGGINGS.get(), (Item)HnSItemRegistry.SOUL_FLAME_BOOTS.get()
            }
         );
         AzArmorRendererRegistry.register(
            SupremeWitchArmorRenderer::new,
            (Item)HnSItemRegistry.SUPREME_WITCH_HELMET.get(),
            new Item[]{
               (Item)HnSItemRegistry.SUPREME_WITCH_CHESTPLATE.get(),
               (Item)HnSItemRegistry.SUPREME_WITCH_LEGGINGS.get(),
               (Item)HnSItemRegistry.SUPREME_WITCH_BOOTS.get()
            }
         );
         AzArmorRendererRegistry.register(
            SLCCatArmorRenderer::new,
            (Item)HnSItemRegistry.SLC_CAT_HELMET.get(),
            new Item[]{(Item)HnSItemRegistry.SLC_CAT_CHESTPLATE.get(), (Item)HnSItemRegistry.SLC_CAT_LEGGINGS.get(), (Item)HnSItemRegistry.SLC_CAT_BOOTS.get()}
         );
         AzArmorRendererRegistry.register(
            InfestationArmorRenderer::new,
            (Item)HnSItemRegistry.INFESTATION_HELMET.get(),
            new Item[]{
               (Item)HnSItemRegistry.INFESTATION_CHESTPLATE.get(),
               (Item)HnSItemRegistry.INFESTATION_LEGGINGS.get(),
               (Item)HnSItemRegistry.INFESTATION_BOOTS.get()
            }
         );
         AzArmorRendererRegistry.register(
            BlazeborneArmorRenderer::new,
            (Item)HnSItemRegistry.BLAZEBORNE_HELMET.get(),
            new Item[]{
               (Item)HnSItemRegistry.BLAZEBORNE_CHESTPLATE.get(), (Item)HnSItemRegistry.BLAZEBORNE_LEGGINGS.get(), (Item)HnSItemRegistry.BLAZEBORNE_BOOTS.get()
            }
         );
         AzArmorRendererRegistry.register(
            LemonGodArmorRenderer::new,
            (Item)HnSItemRegistry.LEMON_GOD_HELMET.get(),
            new Item[]{
               (Item)HnSItemRegistry.LEMON_GOD_CHESTPLATE.get(), (Item)HnSItemRegistry.LEMON_GOD_LEGGINGS.get(), (Item)HnSItemRegistry.LEMON_GOD_BOOTS.get()
            }
         );
         AzArmorRendererRegistry.register(
            LemonGodArmorRenderer::new,
            (Item)HnSItemRegistry.ASCENDED_LEMON_GOD_HELMET.get(),
            new Item[]{
               (Item)HnSItemRegistry.ASCENDED_LEMON_GOD_CHESTPLATE.get(),
               (Item)HnSItemRegistry.ASCENDED_LEMON_GOD_LEGGINGS.get(),
               (Item)HnSItemRegistry.ASCENDED_LEMON_GOD_BOOTS.get()
            }
         );
         AzArmorRendererRegistry.register(
            TheWitherArmorRenderer::new,
            (Item)HnSItemRegistry.THE_WITHER_HELMET.get(),
            new Item[]{
               (Item)HnSItemRegistry.THE_WITHER_CHESTPLATE.get(), (Item)HnSItemRegistry.THE_WITHER_LEGGINGS.get(), (Item)HnSItemRegistry.THE_WITHER_BOOTS.get()
            }
         );
         AzArmorRendererRegistry.register(
            ArbitriumRobesArmorRenderer::new,
            (Item)HnSItemRegistry.ASCENDED_ARBITRIUM_ROBES_HELMET.get(),
            new Item[]{
               (Item)HnSItemRegistry.ASCENDED_ARBITRIUM_ROBES_CHESTPLATE.get(),
               (Item)HnSItemRegistry.ASCENDED_ARBITRIUM_ROBES_LEGGINGS.get(),
               (Item)HnSItemRegistry.ASCENDED_ARBITRIUM_ROBES_BOOTS.get()
            }
         );
         AzArmorRendererRegistry.register(
            GabrielArmorRenderer::new,
            (Item)HnSItemRegistry.GABRIEL_ULTRAKILL_HELMET.get(),
            new Item[]{
               (Item)HnSItemRegistry.GABRIEL_ULTRAKILL_CHESTPLATE.get(),
               (Item)HnSItemRegistry.GABRIEL_ULTRAKILL_LEGGINGS.get(),
               (Item)HnSItemRegistry.GABRIEL_ULTRAKILL_BOOTS.get()
            }
         );
         AzItemRendererRegistry.register(HammerOfJusticeRenderer::new, (Item)HnSItemRegistry.HAMMER_OF_JUSTICE.get(), new Item[0]);
         AzItemRendererRegistry.register(VolcanoRenderer::new, (Item)HnSItemRegistry.VOLCANO.get(), new Item[0]);
         AzItemRendererRegistry.register(TerraprismaRenderer::new, (Item)HnSItemRegistry.TERRAPRISMA.get(), new Item[0]);
         AzItemRendererRegistry.register(SoulcallingScepterRenderer::new, (Item)HnSItemRegistry.SOULCALLING_SCEPTER.get(), new Item[0]);
         AzItemRendererRegistry.register(InsaniaAeternusRenderer::new, (Item)HnSItemRegistry.INSANIA_AETERNUS.get(), new Item[0]);
         AzItemRendererRegistry.register(EnergizedCoreSpellbookRenderer::new, (Item)HnSItemRegistry.ENERGIZED_CORE_SPELLBOOK.get(), new Item[0]);
         AzItemRendererRegistry.register(ScrollSheathItemRenderer::new, (Item)HnSItemRegistry.SCROLL_SHEATH.get(), new Item[0]);
         AzItemRendererRegistry.register(GalvanizedSheathItemRenderer::new, (Item)HnSItemRegistry.GALVANIZED_SHEATH.get(), new Item[0]);
         AzArmorRendererRegistry.register(ArbitriumWingsCurioItemRenderer::new, (Item)HnSItemRegistry.ARBITRIUM_WINGS.get(), new Item[0]);
         CuriosRendererRegistry.register((Item)HnSItemRegistry.ARBITRIUM_WINGS.get(), ArbitriumWingsCurioRenderer::new);
         AzIdentityRegistry.register(
            (Item)HnSItemRegistry.ARBITRIUM_ROBES_HELMET.get(),
            new Item[]{
               (Item)HnSItemRegistry.ARBITRIUM_ROBES_CHESTPLATE.get(),
               (Item)HnSItemRegistry.ARBITRIUM_ROBES_LEGGINGS.get(),
               (Item)HnSItemRegistry.ARBITRIUM_ROBES_BOOTS.get(),
               (Item)HnSItemRegistry.ASCENDED_ARBITRIUM_ROBES_HELMET.get(),
               (Item)HnSItemRegistry.ASCENDED_ARBITRIUM_ROBES_CHESTPLATE.get(),
               (Item)HnSItemRegistry.ASCENDED_ARBITRIUM_ROBES_LEGGINGS.get(),
               (Item)HnSItemRegistry.ASCENDED_ARBITRIUM_ROBES_BOOTS.get(),
               (Item)HnSItemRegistry.CREAKING_HELMET.get(),
               (Item)HnSItemRegistry.CREAKING_CHESTPLATE.get(),
               (Item)HnSItemRegistry.CREAKING_LEGGINGS.get(),
               (Item)HnSItemRegistry.CREAKING_BOOTS.get(),
               (Item)HnSItemRegistry.DARK_RITUAL_TEMPLAR_HELMET.get(),
               (Item)HnSItemRegistry.DARK_RITUAL_TEMPLAR_CHESTPLATE.get(),
               (Item)HnSItemRegistry.DARK_RITUAL_TEMPLAR_LEGGINGS.get(),
               (Item)HnSItemRegistry.DARK_RITUAL_TEMPLAR_BOOTS.get(),
               (Item)HnSItemRegistry.ENDER_DRAGON_HELMET.get(),
               (Item)HnSItemRegistry.ENDER_DRAGON_CHESTPLATE.get(),
               (Item)HnSItemRegistry.ENDER_DRAGON_LEGGINGS.get(),
               (Item)HnSItemRegistry.ENDER_DRAGON_BOOTS.get(),
               (Item)HnSItemRegistry.FIREBLOSSOM_BATTLEMAGE_HELMET.get(),
               (Item)HnSItemRegistry.FIREBLOSSOM_BATTLEMAGE_CHESTPLATE.get(),
               (Item)HnSItemRegistry.FIREBLOSSOM_BATTLEMAGE_LEGGINGS.get(),
               (Item)HnSItemRegistry.FIREBLOSSOM_BATTLEMAGE_BOOTS.get(),
               (Item)HnSItemRegistry.FIREBLOSSOM_BATTLEMAGE_BOOTS.get(),
               (Item)HnSItemRegistry.SERAPH_HELMET.get(),
               (Item)HnSItemRegistry.SERAPH_CHESTPLATE.get(),
               (Item)HnSItemRegistry.SERAPH_LEGGINGS.get(),
               (Item)HnSItemRegistry.SERAPH_BOOTS.get(),
               (Item)HnSItemRegistry.SOUL_FLAME_HELMET.get(),
               (Item)HnSItemRegistry.SOUL_FLAME_CHESTPLATE.get(),
               (Item)HnSItemRegistry.SOUL_FLAME_LEGGINGS.get(),
               (Item)HnSItemRegistry.SOUL_FLAME_BOOTS.get(),
               (Item)HnSItemRegistry.SUPREME_WITCH_HELMET.get(),
               (Item)HnSItemRegistry.SUPREME_WITCH_CHESTPLATE.get(),
               (Item)HnSItemRegistry.SUPREME_WITCH_LEGGINGS.get(),
               (Item)HnSItemRegistry.SUPREME_WITCH_BOOTS.get(),
               (Item)HnSItemRegistry.SLC_CAT_HELMET.get(),
               (Item)HnSItemRegistry.SLC_CAT_CHESTPLATE.get(),
               (Item)HnSItemRegistry.SLC_CAT_LEGGINGS.get(),
               (Item)HnSItemRegistry.SLC_CAT_BOOTS.get(),
               (Item)HnSItemRegistry.INFESTATION_HELMET.get(),
               (Item)HnSItemRegistry.INFESTATION_CHESTPLATE.get(),
               (Item)HnSItemRegistry.INFESTATION_LEGGINGS.get(),
               (Item)HnSItemRegistry.INFESTATION_BOOTS.get(),
               (Item)HnSItemRegistry.BLAZEBORNE_HELMET.get(),
               (Item)HnSItemRegistry.BLAZEBORNE_CHESTPLATE.get(),
               (Item)HnSItemRegistry.BLAZEBORNE_LEGGINGS.get(),
               (Item)HnSItemRegistry.BLAZEBORNE_BOOTS.get(),
               (Item)HnSItemRegistry.LEMON_GOD_HELMET.get(),
               (Item)HnSItemRegistry.LEMON_GOD_CHESTPLATE.get(),
               (Item)HnSItemRegistry.LEMON_GOD_LEGGINGS.get(),
               (Item)HnSItemRegistry.LEMON_GOD_BOOTS.get(),
               (Item)HnSItemRegistry.ASCENDED_LEMON_GOD_HELMET.get(),
               (Item)HnSItemRegistry.ASCENDED_LEMON_GOD_CHESTPLATE.get(),
               (Item)HnSItemRegistry.ASCENDED_LEMON_GOD_LEGGINGS.get(),
               (Item)HnSItemRegistry.ASCENDED_LEMON_GOD_BOOTS.get(),
               (Item)HnSItemRegistry.THE_WITHER_HELMET.get(),
               (Item)HnSItemRegistry.THE_WITHER_CHESTPLATE.get(),
               (Item)HnSItemRegistry.THE_WITHER_LEGGINGS.get(),
               (Item)HnSItemRegistry.THE_WITHER_BOOTS.get(),
               (Item)HnSItemRegistry.GABRIEL_ULTRAKILL_HELMET.get(),
               (Item)HnSItemRegistry.GABRIEL_ULTRAKILL_CHESTPLATE.get(),
               (Item)HnSItemRegistry.GABRIEL_ULTRAKILL_LEGGINGS.get(),
               (Item)HnSItemRegistry.GABRIEL_ULTRAKILL_BOOTS.get(),
               (Item)HnSItemRegistry.HAMMER_OF_JUSTICE.get(),
               (Item)HnSItemRegistry.VOLCANO.get(),
               (Item)HnSItemRegistry.TERRAPRISMA.get(),
               (Item)HnSItemRegistry.SOULCALLING_SCEPTER.get(),
               (Item)HnSItemRegistry.INSANIA_AETERNUS.get(),
               (Item)HnSItemRegistry.ENERGIZED_CORE_SPELLBOOK.get(),
               (Item)HnSItemRegistry.ARBITRIUM_WINGS.get(),
               (Item)HnSItemRegistry.GALVANIZED_SHEATH.get(),
               (Item)HnSItemRegistry.SCROLL_SHEATH.get()
            }
         );
      }
   }
}
