package net.alshanex.tunes_n_tomes.setup;

import net.alshanex.tunes_n_tomes.entity.aria_beam.AriaBeamRenderer;
import net.alshanex.tunes_n_tomes.entity.blue_ball.BlueBallEffectRenderer;
import net.alshanex.tunes_n_tomes.entity.blue_ball.BlueBallHitRenderer;
import net.alshanex.tunes_n_tomes.entity.blue_ball.BlueBallRenderer;
import net.alshanex.tunes_n_tomes.entity.blue_circle.BlueCircleRenderer;
import net.alshanex.tunes_n_tomes.entity.celestial_chant.AngelEntityRenderer;
import net.alshanex.tunes_n_tomes.entity.celestial_chant.HealingCircleEntityRenderer;
import net.alshanex.tunes_n_tomes.entity.chord_blast.BlackNoteEntityRenderer;
import net.alshanex.tunes_n_tomes.entity.chord_blast.DefaultNoteEntityRenderer;
import net.alshanex.tunes_n_tomes.entity.fortissimo.FortissimoChargeRenderer;
import net.alshanex.tunes_n_tomes.entity.fortissimo.FortissimoReleaseRenderer;
import net.alshanex.tunes_n_tomes.entity.green_effect.GreenEffectRenderer;
import net.alshanex.tunes_n_tomes.entity.hymn_of_hope.HopeBurstRenderer;
import net.alshanex.tunes_n_tomes.entity.hymn_of_hope.HopeShieldRenderer;
import net.alshanex.tunes_n_tomes.entity.red_arrow.RedArrowRenderer;
import net.alshanex.tunes_n_tomes.entity.red_ball.RedBallEffectRenderer;
import net.alshanex.tunes_n_tomes.entity.red_ball.RedBallHitRenderer;
import net.alshanex.tunes_n_tomes.entity.red_ball.RedBallRenderer;
import net.alshanex.tunes_n_tomes.entity.red_circle.RedCircleRenderer;
import net.alshanex.tunes_n_tomes.entity.rhapsody.BlueBirdEntityRenderer;
import net.alshanex.tunes_n_tomes.entity.segno_marker.SegnoMarkerRenderer;
import net.alshanex.tunes_n_tomes.entity.ult_strings.UltStrings1EntityRenderer;
import net.alshanex.tunes_n_tomes.entity.ult_strings.UltStrings2EntityRenderer;
import net.alshanex.tunes_n_tomes.entity.ult_strings.UltStrings3EntityRenderer;
import net.alshanex.tunes_n_tomes.entity.ult_strings.UltStrings4EntityRenderer;
import net.alshanex.tunes_n_tomes.particle.BlackNoteParticle;
import net.alshanex.tunes_n_tomes.particle.ExplosionNoteParticle;
import net.alshanex.tunes_n_tomes.particle.ExplosionParticle;
import net.alshanex.tunes_n_tomes.particle.MusicalNoteParticle;
import net.alshanex.tunes_n_tomes.particle.SleepingParticle;
import net.alshanex.tunes_n_tomes.particle.VibrationParticle;
import net.alshanex.tunes_n_tomes.registry.TEntityRegistry;
import net.alshanex.tunes_n_tomes.registry.TParticleRegistry;
import net.alshanex.tunes_n_tomes.screen.SoundMarkerOverlay;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;

@EventBusSubscriber(
   modid = "tunes_n_tomes",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class ClientSetup {
   @SubscribeEvent
   public static void registerParticles(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet((ParticleType)TParticleRegistry.SLEEP_PARTICLE.get(), SleepingParticle.Provider::new);
      event.registerSpriteSet((ParticleType)TParticleRegistry.MUSICAL_NOTE_PARTICLE.get(), MusicalNoteParticle.Provider::new);
      event.registerSpriteSet((ParticleType)TParticleRegistry.BLACK_NOTE_PARTICLE.get(), BlackNoteParticle.Provider::new);
      event.registerSpriteSet((ParticleType)TParticleRegistry.VIBRATION_PARTICLE.get(), VibrationParticle.Provider::new);
      event.registerSpriteSet((ParticleType)TParticleRegistry.HARP_EXPLOSION_PARTICLE.get(), ExplosionParticle.Provider::new);
      event.registerSpriteSet((ParticleType)TParticleRegistry.DESTRUCTION_SYMPHONY_PARTICLE.get(), ExplosionNoteParticle.Provider::new);
   }

   @SubscribeEvent
   public static void rendererRegister(RegisterRenderers event) {
      event.registerEntityRenderer((EntityType)TEntityRegistry.DEFAULT_NOTE.get(), DefaultNoteEntityRenderer::new);
      event.registerEntityRenderer((EntityType)TEntityRegistry.BLACK_NOTE.get(), BlackNoteEntityRenderer::new);
      event.registerEntityRenderer((EntityType)TEntityRegistry.ULT_STRINGS_1.get(), UltStrings1EntityRenderer::new);
      event.registerEntityRenderer((EntityType)TEntityRegistry.ULT_STRINGS_2.get(), UltStrings2EntityRenderer::new);
      event.registerEntityRenderer((EntityType)TEntityRegistry.ULT_STRINGS_3.get(), UltStrings3EntityRenderer::new);
      event.registerEntityRenderer((EntityType)TEntityRegistry.ULT_STRINGS_4.get(), UltStrings4EntityRenderer::new);
      event.registerEntityRenderer((EntityType)TEntityRegistry.HARP_EXPLOSION.get(), NoopRenderer::new);
      event.registerEntityRenderer((EntityType)TEntityRegistry.HEALING_CIRCLE.get(), HealingCircleEntityRenderer::new);
      event.registerEntityRenderer((EntityType)TEntityRegistry.ANGEL.get(), AngelEntityRenderer::new);
      event.registerEntityRenderer((EntityType)TEntityRegistry.BLUE_BIRD.get(), BlueBirdEntityRenderer::new);
      event.registerEntityRenderer((EntityType)TEntityRegistry.SEGNO_MARKER.get(), SegnoMarkerRenderer::new);
      event.registerEntityRenderer((EntityType)TEntityRegistry.BLUE_BALL.get(), BlueBallRenderer::new);
      event.registerEntityRenderer((EntityType)TEntityRegistry.BLUE_BALL_HIT.get(), BlueBallHitRenderer::new);
      event.registerEntityRenderer((EntityType)TEntityRegistry.BLUE_BALL_EFFECT.get(), BlueBallEffectRenderer::new);
      event.registerEntityRenderer((EntityType)TEntityRegistry.RED_BALL.get(), RedBallRenderer::new);
      event.registerEntityRenderer((EntityType)TEntityRegistry.RED_BALL_HIT.get(), RedBallHitRenderer::new);
      event.registerEntityRenderer((EntityType)TEntityRegistry.RED_BALL_EFFECT.get(), RedBallEffectRenderer::new);
      event.registerEntityRenderer((EntityType)TEntityRegistry.HOPE_SHIELD.get(), HopeShieldRenderer::new);
      event.registerEntityRenderer((EntityType)TEntityRegistry.HOPE_BURST.get(), HopeBurstRenderer::new);
      event.registerEntityRenderer((EntityType)TEntityRegistry.BLUE_CIRCLE.get(), BlueCircleRenderer::new);
      event.registerEntityRenderer((EntityType)TEntityRegistry.FORTISSIMO_CHARGE.get(), FortissimoChargeRenderer::new);
      event.registerEntityRenderer((EntityType)TEntityRegistry.FORTISSIMO_RELEASE.get(), FortissimoReleaseRenderer::new);
      event.registerEntityRenderer((EntityType)TEntityRegistry.GREEN_EFFECT.get(), GreenEffectRenderer::new);
      event.registerEntityRenderer((EntityType)TEntityRegistry.ARIA_BEAM.get(), AriaBeamRenderer::new);
      event.registerEntityRenderer((EntityType)TEntityRegistry.RED_ARROW.get(), RedArrowRenderer::new);
      event.registerEntityRenderer((EntityType)TEntityRegistry.RED_CIRCLE.get(), RedCircleRenderer::new);
   }

   @SubscribeEvent
   public static void registerGuiLayers(RegisterGuiLayersEvent event) {
      event.registerAboveAll(ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "sound_marker_overlay"), new SoundMarkerOverlay());
   }
}
