package com.gametechbc.traveloptics.render;

import com.gametechbc.traveloptics.entity.spells.gyro_slash.GyroSlashProjectileRenderer;
import com.gametechbc.traveloptics.entity.spells.gyro_slash.GyroSlashVisualRenderer;
import com.gametechbc.traveloptics.entity.spells.psychic_bolt.PsychicBoltRenderer;
import com.gametechbc.traveloptics.entity.spells.reversal.ReversalRenderer;
import com.gametechbc.traveloptics.init.TOEntities;
import com.github.L_Ender.cataclysm.client.render.entity.Abyss_Blast_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Aptrgangr_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Death_Laser_beam_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Draugr_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Elite_Draugr_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Ender_Golem_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Flare_Bomb_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Ignis_Fireball_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Ignited_Berserker_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Ignited_Revenant_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Kobolediator_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Koboleton_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Royal_Draugr_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Sandstorm_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.The_Prowler_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.The_Watcher_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Wadjet_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Wither_Howitzer_Renderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;

@EventBusSubscriber(
   modid = "traveloptics",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class TOEntityRenderer {
   public TOEntityRenderer() {
      super();
   }

   @SubscribeEvent
   public static void onRegisterLayers(RegisterLayerDefinitions event) {
      event.registerLayerDefinition(PsychicBoltRenderer.MODEL_LAYER_LOCATION, PsychicBoltRenderer::createModel);
   }

   @SubscribeEvent
   public static void registerEntityRenderers(RegisterRenderers event) {
      event.registerEntityRenderer((EntityType)TOEntities.ASHEN_BREATH_PROJECTILE.get(), NoopRenderer::new);
      event.registerEntityRenderer((EntityType)TOEntities.BLACKOUT_ANTI_MAGIC_FIELD.get(), NoopRenderer::new);
      event.registerEntityRenderer((EntityType)TOEntities.ENSNARE.get(), NoopRenderer::new);
      event.registerEntityRenderer((EntityType)TOEntities.AERIAL_COLLAPSE_VISUAL_ENTITY.get(), NoopRenderer::new);
      event.registerEntityRenderer((EntityType)TOEntities.REVERSAL.get(), ReversalRenderer::new);
      event.registerEntityRenderer((EntityType)TOEntities.GYRO_SLASH_PROJECTILE.get(), GyroSlashProjectileRenderer::new);
      event.registerEntityRenderer((EntityType)TOEntities.GYRO_SLASH_VISUAL.get(), GyroSlashVisualRenderer::new);
      event.registerEntityRenderer((EntityType)TOEntities.PSYCHIC_BOLT_PROJECTILE.get(), PsychicBoltRenderer::new);
      event.registerEntityRenderer((EntityType)TOEntities.EXTENDED_ABYSS_BLAST.get(), Abyss_Blast_Renderer::new);
      event.registerEntityRenderer((EntityType)TOEntities.EXTENDED_FLARE_BOMB.get(), Flare_Bomb_Renderer::new);
      event.registerEntityRenderer((EntityType)TOEntities.EXTENDED_DEATH_LASER_BEAM.get(), Death_Laser_beam_Renderer::new);
      event.registerEntityRenderer((EntityType)TOEntities.EXTENDED_SANDSTORM.get(), Sandstorm_Renderer::new);
      event.registerEntityRenderer((EntityType)TOEntities.EXTENDED_IGNIS_FIREBALL.get(), Ignis_Fireball_Renderer::new);
      event.registerEntityRenderer((EntityType)TOEntities.EXTENDED_WITHER_HOWITZER.get(), Wither_Howitzer_Renderer::new);
      event.registerEntityRenderer((EntityType)TOEntities.SUMMONED_KOBOLETON.get(), Koboleton_Renderer::new);
      event.registerEntityRenderer((EntityType)TOEntities.SUMMONED_KOBOLEDIATOR.get(), Kobolediator_Renderer::new);
      event.registerEntityRenderer((EntityType)TOEntities.SUMMONED_WADJET.get(), Wadjet_Renderer::new);
      event.registerEntityRenderer((EntityType)TOEntities.SUMMONED_THE_PROWLER.get(), The_Prowler_Renderer::new);
      event.registerEntityRenderer((EntityType)TOEntities.SUMMONED_THE_WATCHER.get(), The_Watcher_Renderer::new);
      event.registerEntityRenderer((EntityType)TOEntities.SUMMONED_IGNITED_REVENANT.get(), Ignited_Revenant_Renderer::new);
      event.registerEntityRenderer((EntityType)TOEntities.SUMMONED_IGNITED_BERSERKER.get(), Ignited_Berserker_Renderer::new);
      event.registerEntityRenderer((EntityType)TOEntities.SUMMONED_ENDER_GOLEM.get(), Ender_Golem_Renderer::new);
      event.registerEntityRenderer((EntityType)TOEntities.SUMMONED_DRAUGR.get(), Draugr_Renderer::new);
      event.registerEntityRenderer((EntityType)TOEntities.SUMMONED_ELITE_DRAUGR.get(), Elite_Draugr_Renderer::new);
      event.registerEntityRenderer((EntityType)TOEntities.SUMMONED_ROYAL_DRAUGR.get(), Royal_Draugr_Renderer::new);
      event.registerEntityRenderer((EntityType)TOEntities.SUMMONED_APTRGANGR.get(), Aptrgangr_Renderer::new);
   }
}
