package net.hazen.hazennstuff.Setup;

import io.redspace.ironsspellbooks.entity.mobs.keeper.KeeperRenderer;
import io.redspace.ironsspellbooks.entity.spells.fiery_dagger.FieryDaggerRenderer;
import net.hazen.hazennstuff.Entity.Mobs.Mobs.Blazes.CinderousFurnace.CinderousFurnaceRenderer;
import net.hazen.hazennstuff.Entity.Mobs.Mobs.Blazes.TheInferno.TheInfernoRenderer;
import net.hazen.hazennstuff.Entity.Mobs.Mobs.VoidWanderer.VoidWandererRenderer;
import net.hazen.hazennstuff.Entity.Mobs.Summons.SummonTerraprisma.SummonedTerraprismaRenderer;
import net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.BishopOfDeciet.BishopOfDeceitRenderer;
import net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.Electromancer.ElectromancerRenderer;
import net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.Necromancers.NamelessOne.NamelessOneRenderer;
import net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.Necromancers.Necromancer.NecromancerRenderer;
import net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.PiglinPyromancer.PiglinPyromancerRenderer;
import net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.ReignOfTyros.Aegis.AegisRenderer;
import net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.ReignOfTyros.Aptos.AptosRenderer;
import net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.ReignOfTyros.Pyromus.PyromusRenderer;
import net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.ServantsOfEnder.ServantsOfEnderRenderer;
import net.hazen.hazennstuff.Entity.Mobs.Wizards.Good.Dryad.DryadRenderer;
import net.hazen.hazennstuff.Entity.Mobs.Wizards.Good.TheRecluse.TheRecluseRenderer;
import net.hazen.hazennstuff.Entity.Spells.Astral.CosmicBolt.CosmicBoltRenderer;
import net.hazen.hazennstuff.Entity.Spells.Astral.Evercomet.EvercometRenderer;
import net.hazen.hazennstuff.Entity.Spells.Astral.ShootingStar.ShootingStarRenderer;
import net.hazen.hazennstuff.Entity.Spells.Blood.LifestealKnife.LifestealKnifeRenderer;
import net.hazen.hazennstuff.Entity.Spells.Eldritch.SoulSeeker.SoulSeekerRenderer;
import net.hazen.hazennstuff.Entity.Spells.Evocation.SpectralAxe.SpectralRenderer;
import net.hazen.hazennstuff.Entity.Spells.Fire.BrimstoneHellblast.BrimstoneHellblastRenderer;
import net.hazen.hazennstuff.Entity.Spells.Fire.ImmolatingObliterator.ScorchingSlashRenderer;
import net.hazen.hazennstuff.Entity.Spells.Ice.Hailstorm.IceChunkA.IceChunkARenderer;
import net.hazen.hazennstuff.Entity.Spells.Ice.Hailstorm.IceChunkB.IceChunkBRenderer;
import net.hazen.hazennstuff.Entity.Spells.Ice.Hailstorm.IceChunkC.IceChunkCRenderer;
import net.hazen.hazennstuff.Entity.Spells.Ice.IceArrow.IceArrowRenderer;
import net.hazen.hazennstuff.Entity.Spells.Lightning.EnergyBurst.EnergyBurstRenderer;
import net.hazen.hazennstuff.Entity.Spells.Nature.DeathSentence.DeathSentenceRenderer;
import net.hazen.hazennstuff.Entity.Spells.Nature.SpiderLily.SpiderLilyRenderer;
import net.hazen.hazennstuff.Entity.Spells.Nature.ThornChakram.ThornChakramRenderer;
import net.hazen.hazennstuff.Entity.Spells.Radiance.Syringe.SyringeRenderer;
import net.hazen.hazennstuff.Entity.Spells.Radiance.Terraprisma.TerraprismaRenderer;
import net.hazen.hazennstuff.Entity.Spells.Shadow.NightsEdgeAfterSlash.NightsEdgeAfterSlashRenderer;
import net.hazen.hazennstuff.Entity.Spells.Shadow.ShadowDagger.ShadowDaggerRenderer;
import net.hazen.hazennstuff.Entity.Spells.Shadow.ShadowReaver.ShadowReaverRenderer;
import net.hazen.hazennstuff.Item.Block.HnSBlockEntities;
import net.hazen.hazennstuff.Item.Block.Statues.HazelStatue.HazelStatueRenderer;
import net.hazen.hazennstuff.Particle.HnSGenericParticle;
import net.hazen.hazennstuff.Particle.LeafParticle.LeafParticle;
import net.hazen.hazennstuff.Particle.SlashParticles.Spells.IonicSLash.IonicSlash;
import net.hazen.hazennstuff.Particle.SlashParticles.Spells.NatureSlash.NatureSlash;
import net.hazen.hazennstuff.Particle.SlashParticles.Spells.NightsEdgeStrike.NightsEdgeStrike;
import net.hazen.hazennstuff.Registries.HnSEntityRegistry;
import net.hazen.hazennstuff.Registries.HnSParticleRegistry;
import net.hazen.hazennstuff.Spells.Tooltips.LightningClientTooltipComponent;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;

@EventBusSubscriber({Dist.CLIENT})
public class HnSClientSetup {
   @SubscribeEvent
   public static void registerRenderer(RegisterRenderers event) {
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.BRIMSTONE_HELLBLAST.get(), BrimstoneHellblastRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.ENERGY_BURST.get(), EnergyBurstRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.SYRINGE.get(), SyringeRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.LIFESTEAL_KNIFE.get(), LifestealKnifeRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.THORN_CHAKRAM.get(), ThornChakramRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.ICE_ARROW.get(), IceArrowRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.ICE_CHUNK_A.get(), IceChunkARenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.ICE_CHUNK_B.get(), IceChunkBRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.ICE_CHUNK_C.get(), IceChunkCRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.SPECTRAL_AXE.get(), SpectralRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.SOUL_SEEKER.get(), SoulSeekerRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.SPIDER_LILY.get(), SpiderLilyRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.DEATH_SENTENCE.get(), DeathSentenceRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.NIGHTS_EDGE_AFTER_SLASH.get(), NightsEdgeAfterSlashRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.SCORCHING_SLASH.get(), ScorchingSlashRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.SHOOTING_STAR.get(), ShootingStarRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.TERRAPRISMA_PROJECTILE.get(), TerraprismaRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.FIERY_DAGGER_MAGIC_PROJECTILE.get(), FieryDaggerRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.SHADOW_DAGGER.get(), ShadowDaggerRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.COSMIC_BOLT.get(), CosmicBoltRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.SHADOW_REAVER.get(), ShadowReaverRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.EVERCOMET.get(), EvercometRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.ELECTROMANCER.get(), ElectromancerRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.APTOS.get(), AptosRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.PYROMUS.get(), PyromusRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.AEGIS.get(), AegisRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.THE_RECLUSE.get(), TheRecluseRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.PIGLIN_PYROMANCER.get(), PiglinPyromancerRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.SERVANT_OF_ENDER.get(), ServantsOfEnderRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.BISHOP_OF_DECEIT.get(), BishopOfDeceitRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.DRYAD.get(), DryadRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.NECROMANCER.get(), NecromancerRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.NAMELESS_ONE.get(), NamelessOneRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.TERRAPRISMA.get(), SummonedTerraprismaRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.CINDEROUS_FURNACE.get(), CinderousFurnaceRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.THE_INFERNO.get(), TheInfernoRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.SUMMON_KEEPER.get(), KeeperRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.VOID_WANDERER.get(), VoidWandererRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.ICE_CLOUD.get(), NoopRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.ICHOR_STREAM.get(), NoopRenderer::new);
      event.registerEntityRenderer((EntityType)HnSEntityRegistry.LIGHTNING_STRIKE.get(), NoopRenderer::new);
      event.registerBlockEntityRenderer(HnSBlockEntities.HAZEL_STATUE.get(), HazelStatueRenderer::new);
   }

   @SubscribeEvent
   public static void registerParticles(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet((ParticleType)HnSParticleRegistry.MUSHROOM_PARTICLE.get(), HnSGenericParticle.Provider::new);
      event.registerSpriteSet((ParticleType)HnSParticleRegistry.STAR_PARTICLE.get(), HnSGenericParticle.Provider::new);
      event.registerSpriteSet((ParticleType)HnSParticleRegistry.STAR_IMPACT_PARTICLE.get(), HnSGenericParticle.Provider::new);
      event.registerSpriteSet((ParticleType)HnSParticleRegistry.COSMIC_EMBER_PARTICLE.get(), HnSGenericParticle.Provider::new);
      event.registerSpriteSet((ParticleType)HnSParticleRegistry.BLADE_PARTICLES.get(), HnSGenericParticle.Provider::new);
      event.registerSpriteSet((ParticleType)HnSParticleRegistry.NIGHTS_EDGE_PARTICLE.get(), HnSGenericParticle.Provider::new);
      event.registerSpriteSet((ParticleType)HnSParticleRegistry.ROD_OF_DISCORD_PARTICLE.get(), HnSGenericParticle.Provider::new);
      event.registerSpriteSet(HnSParticleRegistry.LEAF_PARTICLE.get(), LeafParticle.Provider::new);
      event.registerSpriteSet(HnSParticleRegistry.NIGHTS_EDGE_STRIKE_PARTICLE.get(), NightsEdgeStrike.Provider::new);
      event.registerSpriteSet(HnSParticleRegistry.NATURE_SLASH_PARTICLE.get(), NatureSlash.Provider::new);
      event.registerSpriteSet(HnSParticleRegistry.IONIC_SLASH.get(), IonicSlash.Provider::new);
   }

   @SubscribeEvent
   public static void onRegisterTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
      event.register(LightningClientTooltipComponent.LightningTooltipData.class, LightningClientTooltipComponent::new);
   }
}
