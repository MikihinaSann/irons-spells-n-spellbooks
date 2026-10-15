package net.acetheeldritchking.discerning_the_eldritch.events;

import io.redspace.ironsspellbooks.fluids.SimpleClientFluidType;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import io.redspace.ironsspellbooks.render.ClientStaffItemExtensions;
import net.acetheeldritchking.discerning_the_eldritch.DiscerningTheEldritch;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.eldritch_caster.TheApostleModel;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.gaoler.GaolerModel;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.sightless_maw.SightlessMawModel;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.untold_behemoth.UntoldBehemothModel;
import net.acetheeldritchking.discerning_the_eldritch.entity.render.mobs.ApothicAcolyteRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.render.mobs.ApothicCrusaderRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.render.mobs.ApothicSummonerRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.render.mobs.ApothicTraitorRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.render.mobs.AscendedOneRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.render.mobs.BloodCultistCaptainRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.render.mobs.BloodCultistMageRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.render.mobs.BloodCultistWitchRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.render.mobs.BloodMatriarchRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.render.mobs.ElectromancerRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.render.mobs.GaolerRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.render.mobs.SightlessMawRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.render.mobs.TheApostleRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.render.mobs.UntoldBehemothRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.blade_of_rancor.BladeOfRancorRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.cataclysm_blade_projectile.CataclysmBladeBigRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.cataclysm_blade_projectile.CataclysmBladeSmallRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.crystal_carve.CrystalCarveRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.esoteric_edge.EsotericEdgeRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.esoteric_strike.EsotericStrikeRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.glacial_edge.GlacialEdgeRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.mourning_star.MourningStarRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.ravenous_jaw.RavenousJawRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.razor_blade.RazorBladeRenderer;
import net.acetheeldritchking.discerning_the_eldritch.particle.EsotericSparksParticle;
import net.acetheeldritchking.discerning_the_eldritch.particle.GlacialShadowParticle;
import net.acetheeldritchking.discerning_the_eldritch.particle.MalignantFlameParticle;
import net.acetheeldritchking.discerning_the_eldritch.particle.MalignantSoulParticle;
import net.acetheeldritchking.discerning_the_eldritch.particle.RiftSliceParticle;
import net.acetheeldritchking.discerning_the_eldritch.particle.SoulFireSlashParticle;
import net.acetheeldritchking.discerning_the_eldritch.particle.VeinRipperVerticalParticle;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEEntityRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEFluidRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEParticleRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.ItemRegistries;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(
   modid = "discerning_the_eldritch",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class ClientSetup {
   @SubscribeEvent
   public static void registerRenderer(RegisterRenderers event) {
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.ESOTERIC_EDGE.get(), EsotericEdgeRenderer::new);
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.APOTHIC_SUMMONER.get(), ApothicSummonerRenderer::new);
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.APOTHIC_CRUSADER.get(), ApothicCrusaderRenderer::new);
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.APOTHIC_ACOLYTE.get(), ApothicAcolyteRenderer::new);
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.ASCENDED_ONE.get(), AscendedOneRenderer::new);
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.ASCENDED_ONE_CULTIST.get(), AscendedOneRenderer::new);
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.GAOLER_ENTITY.get(), context -> new GaolerRenderer(context, new GaolerModel()));
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.APOSTLE_ENTITY.get(), context -> new TheApostleRenderer(context, new TheApostleModel()));
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.SIGHTLESS_MAW.get(), context -> new SightlessMawRenderer(context, new SightlessMawModel()));
      event.registerEntityRenderer(
         (EntityType)DTEEntityRegistry.UNTOLD_BEHEMOTH.get(), context -> new UntoldBehemothRenderer(context, new UntoldBehemothModel())
      );
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.GLACIAL_EDGE.get(), GlacialEdgeRenderer::new);
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.UNSTABLE_RIFT.get(), NoopRenderer::new);
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.ESOTERIC_STRIKE.get(), EsotericStrikeRenderer::new);
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.APOTHIC_TRAITOR.get(), ApothicTraitorRenderer::new);
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.CRYSTAL_CARVE.get(), CrystalCarveRenderer::new);
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.RAZOR_BLADE.get(), RazorBladeRenderer::new);
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.BLOOD_CULTIST_CAPTAIN.get(), BloodCultistCaptainRenderer::new);
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.BLADE_OF_RANCOR.get(), BladeOfRancorRenderer::new);
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.GORE_BILE_AOE.get(), NoopRenderer::new);
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.CATACLYSM_BLADE_SMALL.get(), CataclysmBladeSmallRenderer::new);
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.CATACLYSM_BLADE_BIG.get(), CataclysmBladeBigRenderer::new);
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.RAVENOUS_JAW.get(), RavenousJawRenderer::new);
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.ELECTROMANCER_MAGE.get(), ElectromancerRenderer::new);
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.BLOOD_CULTIST_MAGE.get(), BloodCultistMageRenderer::new);
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.BLOOD_CULTIST_WITCH.get(), BloodCultistWitchRenderer::new);
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.BLOOD_MATRIARCH.get(), BloodMatriarchRenderer::new);
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.SOUL_FIRE_ERUPTION_AOE.get(), NoopRenderer::new);
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.MOURNING_STAR_PROJECTILE.get(), MourningStarRenderer::new);
      event.registerEntityRenderer((EntityType)DTEEntityRegistry.RITUAL_BURN_AOE.get(), NoopRenderer::new);
   }

   @SubscribeEvent
   public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
      event.registerItem(
         new ClientStaffItemExtensions(),
         ItemRegistries.getDTEItems()
            .stream()
            .filter(item -> item.get() instanceof StaffItem staffItem && !staffItem.hasCustomRendering())
            .map(holder -> (Item)holder.get())
            .toArray(Item[]::new)
      );
      event.registerFluidType(new SimpleClientFluidType(DiscerningTheEldritch.id("block/liquid_malice")), new Holder[]{DTEFluidRegistry.LIQUID_MALICE_TYPE});
   }

   @SubscribeEvent
   public static void registerParticles(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet(DTEParticleRegistry.GLACIAL_SHADOW_PARTICLE.get(), GlacialShadowParticle.Provider::new);
      event.registerSpriteSet((ParticleType)DTEParticleRegistry.ESOTERIC_SPARKS_PARTICLE.get(), EsotericSparksParticle.Provider::new);
      event.registerSpriteSet((ParticleType)DTEParticleRegistry.RIFT_SLICE_PARTICLE.get(), RiftSliceParticle.Provider::new);
      event.registerSpriteSet((ParticleType)DTEParticleRegistry.MALIGNANT_SOUL.get(), MalignantSoulParticle.Provider::new);
      event.registerSpriteSet((ParticleType)DTEParticleRegistry.MALIGNANT_FLAME.get(), MalignantFlameParticle.Provider::new);
      event.registerSpriteSet(DTEParticleRegistry.SOUL_FIRE_SLASH_PARTICLE.get(), SoulFireSlashParticle.Provider::new);
      event.registerSpriteSet(DTEParticleRegistry.VEIN_RIPPER_VERTICAL_PARTICLE.get(), VeinRipperVerticalParticle.Provider::new);
   }
}
