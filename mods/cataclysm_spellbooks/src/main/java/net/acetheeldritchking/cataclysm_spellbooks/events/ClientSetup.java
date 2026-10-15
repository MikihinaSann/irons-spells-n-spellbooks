package net.acetheeldritchking.cataclysm_spellbooks.events;

import com.github.L_Ender.cataclysm.client.render.entity.Amethyst_Crab_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Aptrgangr_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Draugr_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Elite_Draugr_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Ignited_Berserker_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Ignited_Revenant_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Kobolediator_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Koboleton_Renderer;
import com.github.L_Ender.cataclysm.client.render.entity.Royal_Draugr_Renderer;
import net.acetheeldritchking.cataclysm_spellbooks.entity.render.mobs.phantom_remnant.PhantomAncientRemnantRenderer;
import net.acetheeldritchking.cataclysm_spellbooks.entity.render.spells.HellishBladeRenderer;
import net.acetheeldritchking.cataclysm_spellbooks.entity.render.spells.InfernalBladeRenderer;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSEntityRegistry;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;

@EventBusSubscriber(
   modid = "cataclysm_spellbooks",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class ClientSetup {
   @SubscribeEvent
   public static void registerEntityRenderers(RegisterRenderers event) {
      event.registerEntityRenderer((EntityType)CSEntityRegistry.SUMMONED_IGNITED_REVENANT.get(), Ignited_Revenant_Renderer::new);
      event.registerEntityRenderer((EntityType)CSEntityRegistry.SUMMONED_IGNITED_BERSERKER.get(), Ignited_Berserker_Renderer::new);
      event.registerEntityRenderer((EntityType)CSEntityRegistry.SUMMONED_KOBOLDIATOR.get(), Kobolediator_Renderer::new);
      event.registerEntityRenderer((EntityType)CSEntityRegistry.SUMMONED_KOBOLETON.get(), Koboleton_Renderer::new);
      event.registerEntityRenderer((EntityType)CSEntityRegistry.SUMMONED_DRAUGUR.get(), Draugr_Renderer::new);
      event.registerEntityRenderer((EntityType)CSEntityRegistry.SUMMONED_ROYAL_DRAUGUR.get(), Royal_Draugr_Renderer::new);
      event.registerEntityRenderer((EntityType)CSEntityRegistry.SUMMONED_ELITE_DRAUGUR.get(), Elite_Draugr_Renderer::new);
      event.registerEntityRenderer((EntityType)CSEntityRegistry.SUMMONED_APTRGANGR.get(), Aptrgangr_Renderer::new);
      event.registerEntityRenderer((EntityType)CSEntityRegistry.SUMMONED_AMETHYST_CRAB.get(), Amethyst_Crab_Renderer::new);
      event.registerEntityRenderer((EntityType)CSEntityRegistry.INFERNAL_BLADE_PROJECTILE.get(), InfernalBladeRenderer::new);
      event.registerEntityRenderer((EntityType)CSEntityRegistry.HELLISH_BLADE_PROJECTILE.get(), HellishBladeRenderer::new);
      event.registerEntityRenderer((EntityType)CSEntityRegistry.BLAZING_AOE_ENTITY.get(), NoopRenderer::new);
      event.registerEntityRenderer((EntityType)CSEntityRegistry.PHANTOM_ANCIENT_REMNANT.get(), PhantomAncientRemnantRenderer::new);
   }
}
