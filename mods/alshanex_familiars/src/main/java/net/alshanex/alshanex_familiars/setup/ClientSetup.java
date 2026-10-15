package net.alshanex.alshanex_familiars.setup;

import net.alshanex.alshanex_familiars.block.entity.ArchmageStatueRenderer;
import net.alshanex.alshanex_familiars.block.entity.ClericStatueRenderer;
import net.alshanex.alshanex_familiars.block.entity.DruidStatueRenderer;
import net.alshanex.alshanex_familiars.block.entity.HunterStatueRenderer;
import net.alshanex.alshanex_familiars.block.entity.RitualAutomatorRenderer;
import net.alshanex.alshanex_familiars.block.entity.RitualCorePedestalEntityRenderer;
import net.alshanex.alshanex_familiars.block.entity.ShrinkingStationBlockEntityRenderer;
import net.alshanex.alshanex_familiars.entity.misc.BlueBirdEntityRenderer;
import net.alshanex.alshanex_familiars.entity.misc.ChorusFlowerEntityRenderer;
import net.alshanex.alshanex_familiars.entity.misc.DragonEggEntityRenderer;
import net.alshanex.alshanex_familiars.entity.misc.EndStoneEntityRenderer;
import net.alshanex.alshanex_familiars.entity.misc.FrozenEntityRenderer;
import net.alshanex.alshanex_familiars.entity.misc.HikenEntityRenderer;
import net.alshanex.alshanex_familiars.entity.misc.IllusionistDecoyRenderer;
import net.alshanex.alshanex_familiars.entity.misc.PurpurBricksEntityRenderer;
import net.alshanex.alshanex_familiars.entity.misc.PurpurPilarEntityRenderer;
import net.alshanex.alshanex_familiars.entity.misc.SoulEntityRenderer;
import net.alshanex.alshanex_familiars.entity.models.ArchmagePetModel;
import net.alshanex.alshanex_familiars.entity.models.BardPetModel;
import net.alshanex.alshanex_familiars.entity.models.ClericPetModel;
import net.alshanex.alshanex_familiars.entity.models.DragonWarriorPetModel;
import net.alshanex.alshanex_familiars.entity.models.DruidPetModel;
import net.alshanex.alshanex_familiars.entity.models.FrostlingPetModel;
import net.alshanex.alshanex_familiars.entity.models.HunterPetModel;
import net.alshanex.alshanex_familiars.entity.models.IllusionistPetModel;
import net.alshanex.alshanex_familiars.entity.models.MagePetModel;
import net.alshanex.alshanex_familiars.entity.models.NecromancerPetModel;
import net.alshanex.alshanex_familiars.entity.models.PlaguePetModel;
import net.alshanex.alshanex_familiars.entity.models.ScorcherPetModel;
import net.alshanex.alshanex_familiars.entity.models.ShadowModel;
import net.alshanex.alshanex_familiars.entity.models.SummonerPetModel;
import net.alshanex.alshanex_familiars.entity.renderers.ArchmagePetRenderer;
import net.alshanex.alshanex_familiars.entity.renderers.BardPetRenderer;
import net.alshanex.alshanex_familiars.entity.renderers.ClericPetRenderer;
import net.alshanex.alshanex_familiars.entity.renderers.DragonWarriorPetRenderer;
import net.alshanex.alshanex_familiars.entity.renderers.DruidPetRenderer;
import net.alshanex.alshanex_familiars.entity.renderers.FrostlingPetRenderer;
import net.alshanex.alshanex_familiars.entity.renderers.HunterPetRenderer;
import net.alshanex.alshanex_familiars.entity.renderers.IllusionistPetRenderer;
import net.alshanex.alshanex_familiars.entity.renderers.MagePetRenderer;
import net.alshanex.alshanex_familiars.entity.renderers.NecromancerPetRenderer;
import net.alshanex.alshanex_familiars.entity.renderers.PlaguePetRenderer;
import net.alshanex.alshanex_familiars.entity.renderers.ScorcherPetRenderer;
import net.alshanex.alshanex_familiars.entity.renderers.ShadowRenderer;
import net.alshanex.alshanex_familiars.entity.renderers.SummonerPetRenderer;
import net.alshanex.alshanex_familiars.entity.sound.AngelEntityRenderer;
import net.alshanex.alshanex_familiars.entity.sound.BlackNoteEntityRenderer;
import net.alshanex.alshanex_familiars.entity.sound.DefaultNoteEntityRenderer;
import net.alshanex.alshanex_familiars.entity.sound.HealingCircleEntityRenderer;
import net.alshanex.alshanex_familiars.entity.sound.MusicBoltRenderer;
import net.alshanex.alshanex_familiars.entity.sound.ult_strings.UltStrings1EntityRenderer;
import net.alshanex.alshanex_familiars.entity.sound.ult_strings.UltStrings2EntityRenderer;
import net.alshanex.alshanex_familiars.entity.sound.ult_strings.UltStrings3EntityRenderer;
import net.alshanex.alshanex_familiars.entity.sound.ult_strings.UltStrings4EntityRenderer;
import net.alshanex.alshanex_familiars.particle.BlackNoteParticle;
import net.alshanex.alshanex_familiars.particle.ExplosionNoteParticle;
import net.alshanex.alshanex_familiars.particle.ExplosionParticle;
import net.alshanex.alshanex_familiars.particle.MusicalNoteParticle;
import net.alshanex.alshanex_familiars.particle.VibrationParticle;
import net.alshanex.alshanex_familiars.registry.BlockEntityRegistry;
import net.alshanex.alshanex_familiars.registry.BlockRegistry;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.alshanex_familiars.registry.ParticleRegistry;
import net.alshanex.alshanex_familiars.screen.MonocleOverlay;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;

@EventBusSubscriber(
   modid = "alshanex_familiars",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class ClientSetup {
   @SubscribeEvent
   public static void rendererRegister(RegisterRenderers event) {
      event.registerEntityRenderer((EntityType)EntityRegistry.MAGE_PET.get(), context -> new MagePetRenderer(context, new MagePetModel()));
      event.registerEntityRenderer((EntityType)EntityRegistry.ARCHMAGE_PET.get(), context -> new ArchmagePetRenderer(context, new ArchmagePetModel()));
      event.registerEntityRenderer((EntityType)EntityRegistry.SUMMONER_PET.get(), context -> new SummonerPetRenderer(context, new SummonerPetModel()));
      event.registerEntityRenderer((EntityType)EntityRegistry.NECROMANCER_PET.get(), context -> new NecromancerPetRenderer(context, new NecromancerPetModel()));
      event.registerEntityRenderer((EntityType)EntityRegistry.HUNTER_PET.get(), context -> new HunterPetRenderer(context, new HunterPetModel()));
      event.registerEntityRenderer((EntityType)EntityRegistry.DRUID_PET.get(), context -> new DruidPetRenderer(context, new DruidPetModel()));
      event.registerEntityRenderer((EntityType)EntityRegistry.ILLUSIONIST_PET.get(), context -> new IllusionistPetRenderer(context, new IllusionistPetModel()));
      event.registerEntityRenderer((EntityType)EntityRegistry.SCORCHER_PET.get(), context -> new ScorcherPetRenderer(context, new ScorcherPetModel()));
      event.registerEntityRenderer((EntityType)EntityRegistry.CLERIC_PET.get(), context -> new ClericPetRenderer(context, new ClericPetModel()));
      event.registerEntityRenderer((EntityType)EntityRegistry.PLAGUE_PET.get(), context -> new PlaguePetRenderer(context, new PlaguePetModel()));
      event.registerEntityRenderer((EntityType)EntityRegistry.BARD_PET.get(), context -> new BardPetRenderer(context, new BardPetModel()));
      event.registerEntityRenderer((EntityType)EntityRegistry.FROSTLING_PET.get(), context -> new FrostlingPetRenderer(context, new FrostlingPetModel()));
      event.registerEntityRenderer(
         (EntityType)EntityRegistry.DRAGON_WARRIOR_PET.get(), context -> new DragonWarriorPetRenderer(context, new DragonWarriorPetModel())
      );
      event.registerEntityRenderer((EntityType)EntityRegistry.SHADOW_SUMMON.get(), context -> new ShadowRenderer(context, new ShadowModel()));
      event.registerEntityRenderer((EntityType)EntityRegistry.DECOY.get(), IllusionistDecoyRenderer::new);
      event.registerBlockEntityRenderer(BlockEntityRegistry.ARCHMAGE_STATUE.get(), ArchmageStatueRenderer::new);
      net.neoforged.neoforge.client.extensions.common.NeoForgeBlockRenderLayers.setRenderLayer(BlockRegistry.ARCHMAGE_STATUE.get(), RenderType.cutout());
      event.registerBlockEntityRenderer(BlockEntityRegistry.HUNTER_STATUE.get(), HunterStatueRenderer::new);
      net.neoforged.neoforge.client.extensions.common.NeoForgeBlockRenderLayers.setRenderLayer(BlockRegistry.HUNTER_STATUE.get(), RenderType.cutout());
      event.registerBlockEntityRenderer(BlockEntityRegistry.DRUID_STATUE.get(), DruidStatueRenderer::new);
      net.neoforged.neoforge.client.extensions.common.NeoForgeBlockRenderLayers.setRenderLayer(BlockRegistry.DRUID_STATUE.get(), RenderType.cutout());
      event.registerBlockEntityRenderer(BlockEntityRegistry.CLERIC_STATUE.get(), ClericStatueRenderer::new);
      net.neoforged.neoforge.client.extensions.common.NeoForgeBlockRenderLayers.setRenderLayer(BlockRegistry.CLERIC_STATUE.get(), RenderType.cutout());
      event.registerBlockEntityRenderer(BlockEntityRegistry.RITUAL_CORE.get(), RitualCorePedestalEntityRenderer::new);
      net.neoforged.neoforge.client.extensions.common.NeoForgeBlockRenderLayers.setRenderLayer(BlockRegistry.RITUAL_CORE.get(), RenderType.cutout());
      event.registerBlockEntityRenderer(BlockEntityRegistry.SHRINKING_STATION_BE.get(), ShrinkingStationBlockEntityRenderer::new);
      net.neoforged.neoforge.client.extensions.common.NeoForgeBlockRenderLayers.setRenderLayer(BlockRegistry.SHRINKING_STATION.get(), RenderType.cutout());
      event.registerBlockEntityRenderer(BlockEntityRegistry.RITUAL_AUTOMATOR.get(), RitualAutomatorRenderer::new);
      net.neoforged.neoforge.client.extensions.common.NeoForgeBlockRenderLayers.setRenderLayer(BlockRegistry.RITUAL_AUTOMATOR.get(), RenderType.cutout());
      event.registerEntityRenderer((EntityType)EntityRegistry.DEFAULT_NOTE.get(), DefaultNoteEntityRenderer::new);
      event.registerEntityRenderer((EntityType)EntityRegistry.BLACK_NOTE.get(), BlackNoteEntityRenderer::new);
      event.registerEntityRenderer((EntityType)EntityRegistry.ULT_STRINGS_1.get(), UltStrings1EntityRenderer::new);
      event.registerEntityRenderer((EntityType)EntityRegistry.ULT_STRINGS_2.get(), UltStrings2EntityRenderer::new);
      event.registerEntityRenderer((EntityType)EntityRegistry.ULT_STRINGS_3.get(), UltStrings3EntityRenderer::new);
      event.registerEntityRenderer((EntityType)EntityRegistry.ULT_STRINGS_4.get(), UltStrings4EntityRenderer::new);
      event.registerEntityRenderer((EntityType)EntityRegistry.HARP_EXPLOSION.get(), NoopRenderer::new);
      event.registerEntityRenderer((EntityType)EntityRegistry.HEALING_CIRCLE.get(), HealingCircleEntityRenderer::new);
      event.registerEntityRenderer((EntityType)EntityRegistry.ANGEL.get(), AngelEntityRenderer::new);
      event.registerEntityRenderer((EntityType)EntityRegistry.BLUE_BIRD.get(), BlueBirdEntityRenderer::new);
      event.registerEntityRenderer((EntityType)EntityRegistry.MUSIC_BOLT.get(), MusicBoltRenderer::new);
      event.registerEntityRenderer((EntityType)EntityRegistry.MEMORY_FRAGMENT.get(), NoopRenderer::new);
      event.registerEntityRenderer((EntityType)EntityRegistry.SOUL.get(), SoulEntityRenderer::new);
      event.registerEntityRenderer((EntityType)EntityRegistry.HIKEN_ENTITY.get(), context -> new HikenEntityRenderer(context, 2.0F));
      event.registerEntityRenderer((EntityType)EntityRegistry.FROZEN_ENTITY.get(), FrozenEntityRenderer::new);
      event.registerEntityRenderer((EntityType)EntityRegistry.END_STONE_ENTITY.get(), EndStoneEntityRenderer::new);
      event.registerEntityRenderer((EntityType)EntityRegistry.DRAGON_EGG_ENTITY.get(), DragonEggEntityRenderer::new);
      event.registerEntityRenderer((EntityType)EntityRegistry.CHORUS_FLOWER_ENTITY.get(), ChorusFlowerEntityRenderer::new);
      event.registerEntityRenderer((EntityType)EntityRegistry.PURPUR_BRICKS_ENTITY.get(), PurpurBricksEntityRenderer::new);
      event.registerEntityRenderer((EntityType)EntityRegistry.PURPUR_PILAR_ENTITY.get(), PurpurPilarEntityRenderer::new);
      event.registerEntityRenderer((EntityType)EntityRegistry.DRAGON_CIRCLE.get(), context -> new HikenEntityRenderer(context, 5.0F));
      net.neoforged.neoforge.client.extensions.common.NeoForgeBlockRenderLayers.setRenderLayer((Block)BlockRegistry.ICE_SURFACE_BLOCK.get(), RenderType.translucent());
   }

   @SubscribeEvent
   public static void registerParticles(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet((ParticleType)ParticleRegistry.MUSICAL_NOTE_PARTICLE.get(), MusicalNoteParticle.Provider::new);
      event.registerSpriteSet((ParticleType)ParticleRegistry.BLACK_NOTE_PARTICLE.get(), BlackNoteParticle.Provider::new);
      event.registerSpriteSet((ParticleType)ParticleRegistry.VIBRATION_PARTICLE.get(), VibrationParticle.Provider::new);
      event.registerSpriteSet((ParticleType)ParticleRegistry.HARP_EXPLOSION_PARTICLE.get(), ExplosionParticle.Provider::new);
      event.registerSpriteSet((ParticleType)ParticleRegistry.DESTRUCTION_SYMPHONY_PARTICLE.get(), ExplosionNoteParticle.Provider::new);
   }

   @SubscribeEvent
   public static void registerGuiLayers(RegisterGuiLayersEvent event) {
      event.registerAboveAll(ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "monocle_overlay"), new MonocleOverlay());
   }
}
