package com.somake.somakespells.setup;

import com.github.L_Ender.cataclysm.client.render.entity.Lightning_Spear_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Phantom_Arrow_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Scylla_Ceraunus_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Storm_Serpent_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Water_Spear_Renderer;
import com.somake.somakespells.client.RedSoulHudOverlay;
import com.somake.somakespells.client.model.BulwarkOfTheFlameModel;
import com.somake.somakespells.client.model.IgnisFireballModel;
import com.somake.somakespells.client.model.IncineratorSwordModel;
import com.somake.somakespells.client.model.SoulBastionShieldModel;
import com.somake.somakespells.client.model.SpiralOfRuinTridentModel;
import com.somake.somakespells.client.model.WingedRuinWingsModel;
import com.somake.somakespells.client.renderer.blockentity.PedestalOfFireRenderer;
import com.somake.somakespells.client.renderer.blockentity.UpgradeForgeBlockEntityRenderer;
import com.somake.somakespells.client.renderer.layer.GuardianConnectionSwirlLayer;
import com.somake.somakespells.client.renderer.layer.WingedRuinPlayerLayer;
import com.somake.somakespells.compat.BornInChaosCompat;
import com.somake.somakespells.compat.LegendaryMonstersCompat;
import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.gui.upgrade_forge.UpgradeForgeScreen;
import com.somake.somakespells.item.ToggleableSpellbladeItem;
import com.somake.somakespells.particle.BloodSlashParticle;
import com.somake.somakespells.particle.CustomAnimFireParticle;
import com.somake.somakespells.particle.CustomExplodeParticle;
import com.somake.somakespells.particle.CustomFlameJetParticle;
import com.somake.somakespells.particle.CustomInfernalSurgeParticle;
import com.somake.somakespells.particle.CustomStormParticle;
import com.somake.somakespells.particle.ElementalFierySmokeParticle;
import com.somake.somakespells.particle.FireParticle;
import com.somake.somakespells.particle.FireVortexParticle;
import com.somake.somakespells.particle.LightningCutParticle;
import com.somake.somakespells.particle.OrbitingWaterParticle;
import com.somake.somakespells.particle.SoulFireParticle;
import com.somake.somakespells.particle.SubmergedHitSplashParticle;
import com.somake.somakespells.particle.WitheredRosePetalParticle;
import com.somake.somakespells.particle.WitheredRoseVortexParticle;
import com.somake.somakespells.registries.ModBlockEntities;
import com.somake.somakespells.registries.ModCompatEntityTypes;
import com.somake.somakespells.registries.ModCompatItems;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModItems;
import com.somake.somakespells.registries.ModMenus;
import com.somake.somakespells.registries.ModMobEffects;
import com.somake.somakespells.registries.ModParticles;
import com.somake.somakespells.render.AbyssalOrbRenderer;
import com.somake.somakespells.render.AbyssalStrikeRenderer;
import com.somake.somakespells.render.ApocalypticBurstChargeRenderer;
import com.somake.somakespells.render.ApocalypticBurstRenderer;
import com.somake.somakespells.render.BellRenderer;
import com.somake.somakespells.render.BigWaveRenderer;
import com.somake.somakespells.render.BloodCrystalRootVisualRenderer;
import com.somake.somakespells.render.BloodboundBladeProjectileRenderer;
import com.somake.somakespells.render.BloodmarkTraceRenderer;
import com.somake.somakespells.render.BloodyLegacyVisualRenderer;
import com.somake.somakespells.render.ConnectionChainRenderer;
import com.somake.somakespells.render.ControlledWaterRenderer;
import com.somake.somakespells.render.CustodiaCaeliSigilRenderer;
import com.somake.somakespells.render.ElementalBlazeRenderer;
import com.somake.somakespells.render.ElementalChargeBeamRenderer;
import com.somake.somakespells.render.EmptyRenderer;
import com.somake.somakespells.render.FireBlastRenderer;
import com.somake.somakespells.render.FragmentedSwordRainRenderer;
import com.somake.somakespells.render.FuneralBloomBudRenderer;
import com.somake.somakespells.render.FuneralBloomControllerRenderer;
import com.somake.somakespells.render.FuneralBloomImpactRenderer;
import com.somake.somakespells.render.GeoGroundRenderer;
import com.somake.somakespells.render.IgnisShieldRenderer;
import com.somake.somakespells.render.IncineratorSlashRenderer;
import com.somake.somakespells.render.LightningBallProjectileRenderer;
import com.somake.somakespells.render.LightningFieldRenderer;
import com.somake.somakespells.render.LightningLanceDanceRenderer;
import com.somake.somakespells.render.MirrorStrikeVisualRenderer;
import com.somake.somakespells.render.RedEchoRenderer;
import com.somake.somakespells.render.RitualFireSigilRenderer;
import com.somake.somakespells.render.SacredPhoenixBlessingRenderer;
import com.somake.somakespells.render.SeaSerpentJetRenderer;
import com.somake.somakespells.render.SomakeGhostRenderer;
import com.somake.somakespells.render.SoulBastionShieldRenderer;
import com.somake.somakespells.render.SoulLatchTetherRenderer;
import com.somake.somakespells.render.SoulfireOrbRenderer;
import com.somake.somakespells.render.SovereignArmoryPortalRenderer;
import com.somake.somakespells.render.SovereignArmoryProjectileRenderer;
import com.somake.somakespells.render.SpiralOfRuinProjectileRenderer;
import com.somake.somakespells.render.ThrustLightningRenderer;
import com.somake.somakespells.render.WaterBallRenderer;
import com.somake.somakespells.render.WaterSplashRenderer;
import com.somake.somakespells.render.WitheredRoseVortexRenderer;
import io.redspace.ironsspellbooks.render.SpellBookCurioRenderer;
import io.redspace.ironsspellbooks.render.EnergySwirlLayer.Vanilla;
import net.miauczel.legendary_monsters.entity.ProjectileEntityRenderer.ThrownPhantomDaggerRenderer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.particle.LavaParticle.Provider;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.DrownedRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.PlayerSkin.Model;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.AddLayers;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod(
   value = "somakespells",
   dist = {Dist.CLIENT}
)
@EventBusSubscriber(
   modid = "somakespells",
   value = {Dist.CLIENT},
   bus = Bus.MOD
)
public class ClientSetup {
   public ClientSetup(ModContainer container) {
   }

   @SubscribeEvent
   public static void onClientSetup(FMLClientSetupEvent event) {
      event.enqueueWork(() -> {
         CuriosRendererRegistry.register((Item)ModItems.VOIDBOUND_CODEX_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.VOIDBOUND_CODEX_2_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.VOIDBOUND_CODEX_3_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.STORMCALLER_LEXICON_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.STORMCALLER_LEXICON_2_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.STORMCALLER_LEXICON_3_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.INVOKER_CANON_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.INVOKER_CANON_2_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.INVOKER_CANON_3_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.VERDANT_TESTAMENT_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.VERDANT_TESTAMENT_2_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.VERDANT_TESTAMENT_3_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.SANGUINE_TESTAMENT_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.SANGUINE_TESTAMENT_2_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.SANGUINE_TESTAMENT_3_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         if (LegendaryMonstersCompat.isLoaded()) {
            CuriosRendererRegistry.register((Item)ModItems.SOULLIT_TESTAMENT_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
            CuriosRendererRegistry.register((Item)ModItems.SOULLIT_TESTAMENT_2_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
            CuriosRendererRegistry.register((Item)ModItems.SOULLIT_TESTAMENT_3_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         }

         if (MagicFromTheEastCompat.isLoaded()) {
            CuriosRendererRegistry.register((Item)ModItems.AXIOMATIC_CODEX_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
            CuriosRendererRegistry.register((Item)ModItems.AXIOMATIC_CODEX_2_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
            CuriosRendererRegistry.register((Item)ModItems.AXIOMATIC_CODEX_3_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         }

         CuriosRendererRegistry.register((Item)ModItems.BURNING_MAGMA_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.BURNING_MAGMA_2_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.BURNING_MAGMA_3_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.DAWNLIT_GOSPEL_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.DAWNLIT_GOSPEL_2_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.DAWNLIT_GOSPEL_3_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.FROSTVEIN_CODEX_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.FROSTVEIN_CODEX_2_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.FROSTVEIN_CODEX_3_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.AQUAMANCER_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         CuriosRendererRegistry.register((Item)ModItems.STORM_SCRIPTURES_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         if (ModList.get().isLoaded("gtbcs_geomancy_plus")) {
            CuriosRendererRegistry.register((Item)ModCompatItems.STONEHEART_CODEX_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
            CuriosRendererRegistry.register((Item)ModCompatItems.STONEHEART_CODEX_2_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
            CuriosRendererRegistry.register((Item)ModCompatItems.STONEHEART_CODEX_3_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         }

         if (ModList.get().isLoaded("tunes_n_tomes")) {
            CuriosRendererRegistry.register((Item)ModCompatItems.RESONANCE_CANTICLE_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
            CuriosRendererRegistry.register((Item)ModCompatItems.RESONANCE_CANTICLE_2_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
            CuriosRendererRegistry.register((Item)ModCompatItems.RESONANCE_CANTICLE_3_SPELL_BOOK.get(), SpellBookCurioRenderer::new);
         }

         registerActiveItemProperty((Item)ModItems.EXO_LIGHT_SABER.get());
         registerActiveItemProperty((Item)ModItems.FLARE_LIGHT_SABER.get());
         registerActiveItemProperty((Item)ModItems.FROSTFLOW_LIGHT_SABER.get());
         registerActiveItemProperty((Item)ModItems.PLAGUE_LIGHT_SABER.get());
         registerActiveItemProperty((Item)ModItems.EXO_DUAL_SABER.get());
         registerActiveItemProperty((Item)ModItems.FLARE_DUAL_SABER.get());
         registerActiveItemProperty((Item)ModItems.FROSTFLOW_DUAL_SABER.get());
         registerActiveItemProperty((Item)ModItems.PLAGUE_DUAL_SABER.get());
      });
   }

   private static void registerActiveItemProperty(Item item) {
      ItemProperties.register(
         item,
         ResourceLocation.fromNamespaceAndPath("somakespells", "active"),
         (stack, level, entity, seed) -> ToggleableSpellbladeItem.isActive(stack) ? 1.0F : 0.0F
      );
   }

   @SubscribeEvent
   public static void registerLayerDefinitions(RegisterLayerDefinitions event) {
      event.registerLayerDefinition(BulwarkOfTheFlameModel.LAYER_LOCATION, BulwarkOfTheFlameModel::createBodyLayer);
      event.registerLayerDefinition(IncineratorSwordModel.LAYER_LOCATION, IncineratorSwordModel::createBodyLayer);
      event.registerLayerDefinition(IgnisFireballModel.LAYER_LOCATION, IgnisFireballModel::createBodyLayer);
      if (LegendaryMonstersCompat.isLoaded()) {
         event.registerLayerDefinition(SoulBastionShieldModel.LAYER_LOCATION, SoulBastionShieldModel::createBodyLayer);
         event.registerLayerDefinition(SpiralOfRuinTridentModel.LAYER_LOCATION, SpiralOfRuinTridentModel::createBodyLayer);
         event.registerLayerDefinition(WingedRuinWingsModel.LAYER_LOCATION, WingedRuinWingsModel::createBodyLayer);
      }

      event.registerLayerDefinition(FireBlastRenderer.MODEL_LAYER_LOCATION, FireBlastRenderer::createBodyLayer);
   }

   @SubscribeEvent
   public static void registerMenuScreens(RegisterMenuScreensEvent event) {
      event.register(ModMenus.UPGRADE_FORGE_MENU.get(), UpgradeForgeScreen::new);
   }

   @SubscribeEvent
   public static void registerGuiLayers(RegisterGuiLayersEvent event) {
      if (LegendaryMonstersCompat.isLoaded()) {
         event.registerAbove(
            VanillaGuiLayers.EXPERIENCE_BAR, ResourceLocation.fromNamespaceAndPath("somakespells", "red_soul_timer"), RedSoulHudOverlay.INSTANCE
         );
      }
   }

   @SubscribeEvent
   public static void registerRenderers(RegisterRenderers event) {
      event.registerBlockEntityRenderer((BlockEntityType)ModBlockEntities.UPGRADE_FORGE.get(), UpgradeForgeBlockEntityRenderer::new);
      event.registerBlockEntityRenderer((BlockEntityType)ModBlockEntities.PEDESTAL_OF_FIRE.get(), PedestalOfFireRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.AQUA_GHOST.get(), SomakeGhostRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.FIRE_GHOST.get(), SomakeGhostRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.ICE_GHOST.get(), SomakeGhostRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.LIGHTNING_GHOST.get(), SomakeGhostRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.HOLY_GHOST.get(), SomakeGhostRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.ENDER_GHOST.get(), SomakeGhostRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.BLOOD_GHOST.get(), SomakeGhostRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.EVOCATION_GHOST.get(), SomakeGhostRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.NATURE_GHOST.get(), SomakeGhostRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.ELDRITCH_GHOST.get(), SomakeGhostRenderer::new);
      if (LegendaryMonstersCompat.isLoaded()) {
         event.registerEntityRenderer((EntityType)ModEntityTypes.SPIRIT_GHOST.get(), SomakeGhostRenderer::new);
      }

      if (MagicFromTheEastCompat.isLoaded()) {
         event.registerEntityRenderer((EntityType)ModEntityTypes.SYMMETRY_GHOST.get(), SomakeGhostRenderer::new);
      }

      if (ModList.get().isLoaded("gtbcs_geomancy_plus")) {
         event.registerEntityRenderer((EntityType)ModCompatEntityTypes.GEO_GHOST.get(), SomakeGhostRenderer::new);
      }

      if (ModList.get().isLoaded("tunes_n_tomes")) {
         event.registerEntityRenderer((EntityType)ModCompatEntityTypes.SOUND_GHOST.get(), SomakeGhostRenderer::new);
      }

      event.registerEntityRenderer((EntityType)ModEntityTypes.IGNIS_SHIELD.get(), IgnisShieldRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.INCINERATOR_SLASH.get(), IncineratorSlashRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.ABYSSAL_BURN.get(), EmptyRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.ABYSSAL_STRIKE.get(), AbyssalStrikeRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.APOCALYPTIC_BURST.get(), ApocalypticBurstRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.APOCALYPTIC_BURST_CHARGE.get(), ApocalypticBurstChargeRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.FIRE_BLAST_VISUAL_ENTITY.get(), FireBlastRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.SOULFIRE_ORB.get(), SoulfireOrbRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.ABYSSAL_ORB.get(), AbyssalOrbRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.LIGHTNING_BALL_PROJECTILE.get(), LightningBallProjectileRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.LIGHTNING_LANCE_DANCE_PROJECTILE.get(), LightningLanceDanceRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.LIGHTNING_FIELD.get(), LightningFieldRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.CUSTODIA_CAELI_SIGIL.get(), CustodiaCaeliSigilRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.SOVEREIGN_ARMORY_PORTAL.get(), SovereignArmoryPortalRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.SOVEREIGN_ARMORY_PROJECTILE.get(), SovereignArmoryProjectileRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.ELEMENTAL_CHARGE_BEAM.get(), ElementalChargeBeamRenderer::new);
      if (ModEntityTypes.SACRED_PHOENIX_BLESSING_CONTROLLER != null) {
         event.registerEntityRenderer((EntityType)ModEntityTypes.SACRED_PHOENIX_BLESSING_CONTROLLER.get(), SacredPhoenixBlessingRenderer::new);
      }

      event.registerEntityRenderer((EntityType)ModEntityTypes.THRUST_LIGHTNING_ENTITY.get(), ThrustLightningRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.CUSTOM_SPARK_ENTITY.get(), EmptyRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.SPELL_WATER_SPEAR.get(), Water_Spear_Renderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.SPELL_LIGHTNING_SPEAR.get(), Lightning_Spear_Renderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.SPELL_ANCHOR.get(), Scylla_Ceraunus_Renderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.SPELL_STORM_SERPENT.get(), Storm_Serpent_Renderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.SEA_SERPENT_JET.get(), SeaSerpentJetRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.WATER_BALL.get(), WaterBallRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.CONTROLLED_WATER.get(), ControlledWaterRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.CONNECTION_CHAIN_VISUAL.get(), ConnectionChainRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.RITUAL_FIRE_SIGIL.get(), RitualFireSigilRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.BLOODMARK_TRACE.get(), BloodmarkTraceRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.BLOODMARK_MARKER.get(), EmptyRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.BLOODMARK_ZONE.get(), EmptyRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.FRAGMENTED_SWORD_RAIN.get(), FragmentedSwordRainRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.BIG_WAVE.get(), BigWaveRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.GEO_GROUND.get(), GeoGroundRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.WATER_SPLASH.get(), WaterSplashRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.SUMMONED_DROWNED.get(), DrownedRenderer::new);
      event.registerEntityRenderer(
         (EntityType)ModEntityTypes.SOUL_BLAZE.get(),
         context -> new ElementalBlazeRenderer(context, ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/soul_blaze.png"))
      );
      if (BornInChaosCompat.isLoaded()) {
         event.registerEntityRenderer(
            (EntityType)ModCompatEntityTypes.INFERNAL_BLAZE.get(),
            context -> new ElementalBlazeRenderer(context, ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/infernal_blaze.png"))
         );
      }

      event.registerEntityRenderer((EntityType)ModEntityTypes.PHANTOM_BARRAGE_PROJECTILE.get(), Phantom_Arrow_Renderer::new);
      if (MagicFromTheEastCompat.isLoaded()) {
         event.registerEntityRenderer((EntityType)ModEntityTypes.MIRROR_STRIKE_VISUAL.get(), MirrorStrikeVisualRenderer::new);
      }

      event.registerEntityRenderer((EntityType)ModEntityTypes.SOUL_GRAB_CONTROLLER.get(), EmptyRenderer::new);
      if (LegendaryMonstersCompat.isLoaded()) {
         event.registerEntityRenderer((EntityType)ModEntityTypes.PROCESSION_OF_SOULS_CONTROLLER.get(), EmptyRenderer::new);
         event.registerEntityRenderer((EntityType)ModEntityTypes.GRAVE_SIGIL_CONTROLLER.get(), EmptyRenderer::new);
         event.registerEntityRenderer((EntityType)ModEntityTypes.SOUL_BASTION_CONTROLLER.get(), EmptyRenderer::new);
         event.registerEntityRenderer((EntityType)ModEntityTypes.SOUL_BASTION_SHIELD.get(), SoulBastionShieldRenderer::new);
         event.registerEntityRenderer((EntityType)ModEntityTypes.SOUL_LATCH_CONTROLLER.get(), EmptyRenderer::new);
         event.registerEntityRenderer((EntityType)ModEntityTypes.SOUL_LATCH_TETHER.get(), SoulLatchTetherRenderer::new);
         event.registerEntityRenderer((EntityType)ModEntityTypes.SPIRAL_OF_RUIN_PROJECTILE.get(), SpiralOfRuinProjectileRenderer::new);
         event.registerEntityRenderer((EntityType)ModEntityTypes.SPIRAL_OF_RUIN_CONTROLLER.get(), EmptyRenderer::new);
         event.registerEntityRenderer((EntityType)ModEntityTypes.SOULFALL_JUDGMENT_CONTROLLER.get(), EmptyRenderer::new);
         event.registerEntityRenderer((EntityType)ModEntityTypes.SOUL_REPRISAL_DAGGER.get(), ThrownPhantomDaggerRenderer::new);
         event.registerEntityRenderer((EntityType)ModEntityTypes.SPECTRAL_RONDO_CONTROLLER.get(), EmptyRenderer::new);
         event.registerEntityRenderer((EntityType)ModEntityTypes.WINGED_RUIN_CONTROLLER.get(), EmptyRenderer::new);
         event.registerEntityRenderer((EntityType)ModEntityTypes.RED_ECHO.get(), RedEchoRenderer::new);
      }

      event.registerEntityRenderer((EntityType)ModEntityTypes.DESERT_WRATH_CONTROLLER.get(), EmptyRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.PUMPKIN_BOMB.get(), ThrownItemRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.PUMPKIN_ZONE.get(), EmptyRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.FIRESTORM_VORTEX.get(), EmptyRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.WITHERED_ROSE_VORTEX.get(), WitheredRoseVortexRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.FUNERAL_BLOOM_CONTROLLER.get(), FuneralBloomControllerRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.FUNERAL_BLOOM_BUD.get(), FuneralBloomBudRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.FUNERAL_BLOOM_IMPACT.get(), FuneralBloomImpactRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.ERUPTION.get(), EmptyRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.RESONANT_PULSE_PROJECTILE.get(), EmptyRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.BELL.get(), BellRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.BLOODY_LEGACY_VISUAL.get(), BloodyLegacyVisualRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.BLOODBOUND_BLADE_PROJECTILE.get(), BloodboundBladeProjectileRenderer::new);
      event.registerEntityRenderer((EntityType)ModEntityTypes.BLOOD_CRYSTAL_ROOT_VISUAL.get(), BloodCrystalRootVisualRenderer::new);
   }

   @SubscribeEvent
   public static void onAddLayers(AddLayers event) {
      for (Model skin : event.getSkins()) {
         LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer = (LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>)event.getSkin(
            skin
         );
         if (renderer instanceof PlayerRenderer playerRenderer) {
            if (LegendaryMonstersCompat.isLoaded()) {
               ((LivingEntityRenderer)playerRenderer).addLayer(new WingedRuinPlayerLayer(playerRenderer, event.getEntityModels()));
            }

            ((LivingEntityRenderer)playerRenderer).addLayer(
               new Vanilla(
                  playerRenderer, ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/combustioned_swirl.png"), ModMobEffects.COMBUSTIONED
               )
            );
            ((LivingEntityRenderer)playerRenderer).addLayer(
               new Vanilla(
                  playerRenderer,
                  ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/soul_combustioned_swirl.png"),
                  ModMobEffects.SOUL_COMBUSTIONED
               )
            );
            if (BornInChaosCompat.isLoaded()) {
               ((LivingEntityRenderer)playerRenderer).addLayer(
                  new Vanilla(
                     playerRenderer,
                     ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/infernal_combustioned_swirl.png"),
                     ModMobEffects.INFERNAL_COMBUSTIONED
                  )
               );
            }

            ((LivingEntityRenderer)playerRenderer).addLayer(
               new Vanilla(
                  playerRenderer, ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/earthbound_swirl.png"), ModMobEffects.EARTHBOUND
               )
            );
            ((LivingEntityRenderer)playerRenderer).addLayer(
               new Vanilla(
                  playerRenderer, ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/submerged_swirl.png"), ModMobEffects.SUBMERGED
               )
            );
            ((LivingEntityRenderer)playerRenderer).addLayer(
               new Vanilla(
                  playerRenderer, ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/overgrown_swirl.png"), ModMobEffects.OVERGROWN
               )
            );
            ((LivingEntityRenderer)playerRenderer).addLayer(
               new Vanilla(
                  playerRenderer, ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/permafrost_swirl.png"), ModMobEffects.PERMAFROST
               )
            );
            ((LivingEntityRenderer)playerRenderer).addLayer(
               new Vanilla(
                  playerRenderer,
                  ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/ender_corruption_swirl.png"),
                  ModMobEffects.ENDER_CORRUPTION
               )
            );
            ((LivingEntityRenderer)playerRenderer).addLayer(
               new Vanilla(
                  playerRenderer, ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/blood_rush_swirl.png"), ModMobEffects.BLOOD_RUSH
               )
            );
            ((LivingEntityRenderer)playerRenderer).addLayer(
               new Vanilla(playerRenderer, ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/blessed_swirl.png"), ModMobEffects.BLESSED)
            );
            ((LivingEntityRenderer)playerRenderer).addLayer(
               new Vanilla(
                  playerRenderer,
                  ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/blessed_swirl.png"),
                  ModMobEffects.CUSTODIA_CAELI_BLESSING
               )
            );
            if (ModMobEffects.SPIRIT_EMPOWERMENT != null) {
               ((LivingEntityRenderer)playerRenderer).addLayer(
                  new Vanilla(
                     playerRenderer,
                     ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/spirit_empowerment_swirl.png"),
                     ModMobEffects.SPIRIT_EMPOWERMENT
                  )
               );
            }

            if (MagicFromTheEastCompat.isLoaded() && ModMobEffects.SYMMETRY_EMPOWERMENT != null) {
               ((LivingEntityRenderer)playerRenderer).addLayer(
                  new Vanilla(
                     playerRenderer,
                     ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/symmetry_empowerment_swirl.png"),
                     ModMobEffects.SYMMETRY_EMPOWERMENT
                  )
               );
            }

            ((LivingEntityRenderer)playerRenderer).addLayer(
               new Vanilla(
                  playerRenderer,
                  ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/evocation_fortitude_swirl.png"),
                  ModMobEffects.EVOCATION_FORTITUDE
               )
            );
            ((LivingEntityRenderer)playerRenderer).addLayer(
               new Vanilla(
                  playerRenderer, ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/reverberation_swirl.png"), ModMobEffects.REVERBERATION
               )
            );
            ((LivingEntityRenderer)playerRenderer).addLayer(
               new Vanilla(
                  playerRenderer,
                  ResourceLocation.fromNamespaceAndPath("somakespells", "textures/entity/eldritch_gambit_swirl.png"),
                  ModMobEffects.ELDRITCH_GAMBIT
               )
            );
            if (ModList.get().isLoaded("mowziesmobs") && ModMobEffects.GUARDIAN_CONNECTION != null) {
               ((LivingEntityRenderer)playerRenderer).addLayer(new GuardianConnectionSwirlLayer(playerRenderer));
            }
         }
      }
   }

   @SubscribeEvent
   public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet((ParticleType)ModParticles.SUBMERGED_HIT_SPLASH.get(), SubmergedHitSplashParticle.Provider::new);
      event.registerSpriteSet((ParticleType)ModParticles.LIGHTNING_CUT_PARTICLE.get(), LightningCutParticle.Provider::new);
      event.registerSpriteSet((ParticleType)ModParticles.BLOOD_SLASH_PARTICLE.get(), BloodSlashParticle.Provider::new);
      event.registerSpriteSet((ParticleType)ModParticles.ORBITING_WATER_PARTICLE.get(), OrbitingWaterParticle.Factory::new);
      event.registerSpriteSet((ParticleType)ModParticles.FIRE_ORB_SMALL_EXPLODE.get(), CustomExplodeParticle.SmallFactory::new);
      event.registerSpriteSet((ParticleType)ModParticles.SOUL_FIRE_ORB_SMALL_EXPLODE.get(), CustomExplodeParticle.SmallFactory::new);
      event.registerSpriteSet((ParticleType)ModParticles.SOUL_FIRE_FLAME_JET.get(), CustomFlameJetParticle.Factory::new);
      event.registerSpriteSet((ParticleType)ModParticles.SOUL_FIRE_LAVA.get(), Provider::new);
      event.registerSpriteSet((ParticleType)ModParticles.FIRE.get(), FireParticle::provider);
      event.registerSpriteSet((ParticleType)ModParticles.SOUL_FIRE.get(), SoulFireParticle::provider);
      event.registerSpriteSet((ParticleType)ModParticles.WITHERED_ROSE_PETAL.get(), WitheredRosePetalParticle.Provider::new);
      event.registerSpriteSet((ParticleType)ModParticles.WITHERED_ROSE_VORTEX_PARTICLE.get(), WitheredRoseVortexParticle.Provider::new);
      event.registerSpriteSet((ParticleType)ModParticles.FLAME_ANIM_FIRE.get(), CustomAnimFireParticle::provider);
      event.registerSpriteSet((ParticleType)ModParticles.FLAME_SURGE.get(), CustomInfernalSurgeParticle::provider);
      event.registerSpriteSet((ParticleType)ModParticles.SOUL_FLAME_ANIM_FIRE.get(), CustomAnimFireParticle::provider);
      event.registerSpriteSet((ParticleType)ModParticles.SOUL_SURGE.get(), CustomInfernalSurgeParticle::provider);
      event.registerSpriteSet((ParticleType)ModParticles.ELEMENTAL_FIERY_SMOKE.get(), ElementalFierySmokeParticle.Provider::new);
      event.registerSpriteSet((ParticleType)ModParticles.FIRE_VORTEX_PARTICLE.get(), FireVortexParticle.Provider::new);
      event.registerSpriteSet((ParticleType)ModParticles.SOUL_FIRE_VORTEX_PARTICLE.get(), FireVortexParticle.Provider::new);
      if (BornInChaosCompat.isLoaded()) {
         event.registerSpriteSet((ParticleType)ModParticles.INFERNAL_FIRE_EXPLODE.get(), CustomExplodeParticle.IgnisFactory::new);
         event.registerSpriteSet((ParticleType)ModParticles.INFERNAL_FIRE_ORB_SMALL_EXPLODE.get(), CustomExplodeParticle.SmallFactory::new);
         event.registerSpriteSet((ParticleType)ModParticles.INFERNAL_FIRE_FLAME.get(), net.minecraft.client.particle.FlameParticle.Provider::new);
         event.registerSpriteSet((ParticleType)ModParticles.INFERNAL_FIRE_FLAME_JET.get(), CustomFlameJetParticle.Factory::new);
         event.registerSpriteSet((ParticleType)ModParticles.INFERNAL_FIRE_LAVA.get(), Provider::new);
         event.registerSpriteSet((ParticleType)ModParticles.INFERNAL_FIRE_VORTEX_PARTICLE.get(), FireVortexParticle.Provider::new);
      }

      event.registerSpecial((ParticleType)ModParticles.CUSTOM_STORM_PARTICLE.get(), new CustomStormParticle.Factory());
   }
}
