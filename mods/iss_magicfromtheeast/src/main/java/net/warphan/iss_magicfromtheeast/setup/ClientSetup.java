package net.warphan.iss_magicfromtheeast.setup;

import io.redspace.ironsspellbooks.fluids.SimpleClientFluidType;
import io.redspace.ironsspellbooks.render.SpellBookCurioRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.warphan.iss_magicfromtheeast.ISS_MagicFromTheEast;
import net.warphan.iss_magicfromtheeast.entity.mobs.bone_hands.BoneHandsRenderer;
import net.warphan.iss_magicfromtheeast.entity.mobs.jade_executioner.JadeExecutionerRenderer;
import net.warphan.iss_magicfromtheeast.entity.mobs.jiangshi.JiangshiRenderer;
import net.warphan.iss_magicfromtheeast.entity.mobs.kitsune.SummonedKitsuneRenderer;
import net.warphan.iss_magicfromtheeast.entity.mobs.mfte_wizards.onmyoji.OnmyojiRenderer;
import net.warphan.iss_magicfromtheeast.entity.mobs.mfte_wizards.taoist.TaoistRenderer;
import net.warphan.iss_magicfromtheeast.entity.mobs.spirit_ashigaru.SpiritAshigaruRenderer;
import net.warphan.iss_magicfromtheeast.entity.mobs.spirit_samurai.SpiritSamuraiRenderer;
import net.warphan.iss_magicfromtheeast.entity.spells.anchoring_kunai.AnchoringKunaiRenderer;
import net.warphan.iss_magicfromtheeast.entity.spells.bagua_array.BaguaCircleRenderer;
import net.warphan.iss_magicfromtheeast.entity.spells.dragon_glide.JadeLoongRenderer;
import net.warphan.iss_magicfromtheeast.entity.spells.jade_bullet.JadeBulletRenderer;
import net.warphan.iss_magicfromtheeast.entity.spells.jade_drape.JadeDrapesRenderer;
import net.warphan.iss_magicfromtheeast.entity.spells.jade_judgement.JadeDaoRenderer;
import net.warphan.iss_magicfromtheeast.entity.spells.nephrite_slash.NephriteCrystalRenderer;
import net.warphan.iss_magicfromtheeast.entity.spells.phantom_cavalry.PhantomCavalryRenderer;
import net.warphan.iss_magicfromtheeast.entity.spells.soul_skull.SoulSkullRenderer;
import net.warphan.iss_magicfromtheeast.entity.spells.spirit_challenging.ExtractedSoulRenderer;
import net.warphan.iss_magicfromtheeast.entity.spells.splitting_shuriken.SplittingShurikenRenderer;
import net.warphan.iss_magicfromtheeast.entity.spells.sword_dance.JadeSwordRenderer;
import net.warphan.iss_magicfromtheeast.entity.spells.verdict_circle.VerdictCircleRender;
import net.warphan.iss_magicfromtheeast.entity.spirit_arrow.SpiritArrowRenderer;
import net.warphan.iss_magicfromtheeast.particle.JadeShatterParticle;
import net.warphan.iss_magicfromtheeast.particle.JadeSlashParticle;
import net.warphan.iss_magicfromtheeast.registries.MFTEEntityRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTEFluidRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTEItemRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTEParticleRegistries;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@EventBusSubscriber(
   modid = "iss_magicfromtheeast",
   value = {Dist.CLIENT}
)
public class ClientSetup {
   @SubscribeEvent
   public static void registerParticles(RegisterParticleProvidersEvent event) {
      event.registerSpriteSet((ParticleType)MFTEParticleRegistries.JADE_SHATTER_PARTICLE.get(), JadeShatterParticle.Provider::new);
      event.registerSpriteSet(MFTEParticleRegistries.JADE_SLASH_PARTICLE.get(), JadeSlashParticle.Provider::new);
   }

   @SubscribeEvent
   public static void rendererRegister(RegisterRenderers event) {
      event.registerEntityRenderer((EntityType)MFTEEntityRegistries.BAGUA_CIRCLE.get(), BaguaCircleRenderer::new);
      event.registerEntityRenderer((EntityType)MFTEEntityRegistries.JADE_LOONG.get(), JadeLoongRenderer::new);
      event.registerEntityRenderer((EntityType)MFTEEntityRegistries.JADE_DAO.get(), JadeDaoRenderer::new);
      event.registerEntityRenderer((EntityType)MFTEEntityRegistries.JIANGSHI.get(), JiangshiRenderer::new);
      event.registerEntityRenderer((EntityType)MFTEEntityRegistries.SUMMONED_JIANGSHI.get(), JiangshiRenderer::new);
      event.registerEntityRenderer((EntityType)MFTEEntityRegistries.JADE_SWORD.get(), JadeSwordRenderer::new);
      event.registerEntityRenderer((EntityType)MFTEEntityRegistries.VERDICT_CIRCLE.get(), VerdictCircleRender::new);
      event.registerEntityRenderer((EntityType)MFTEEntityRegistries.JADE_EXECUTIONER.get(), JadeExecutionerRenderer::new);
      event.registerEntityRenderer((EntityType)MFTEEntityRegistries.JADE_DRAPES_ENTITY.get(), JadeDrapesRenderer::new);
      event.registerEntityRenderer((EntityType)MFTEEntityRegistries.SUMMON_CLOUD_ENTITY.get(), NoopRenderer::new);
      event.registerEntityRenderer((EntityType)MFTEEntityRegistries.SUMMONED_KITSUNE.get(), SummonedKitsuneRenderer::new);
      event.registerEntityRenderer((EntityType)MFTEEntityRegistries.NEPHRITE_CRYSTAL.get(), NephriteCrystalRenderer::new);
      event.registerEntityRenderer((EntityType)MFTEEntityRegistries.JADE_PROJECTILE.get(), JadeBulletRenderer::new);
      event.registerEntityRenderer((EntityType)MFTEEntityRegistries.TAOIST.get(), TaoistRenderer::new);
      event.registerEntityRenderer((EntityType)MFTEEntityRegistries.ONMYOJI.get(), OnmyojiRenderer::new);
      event.registerEntityRenderer((EntityType)MFTEEntityRegistries.SOUL_SKULL.get(), SoulSkullRenderer::new);
      event.registerEntityRenderer((EntityType)MFTEEntityRegistries.EXTRACTED_SOUL.get(), ExtractedSoulRenderer::new);
      event.registerEntityRenderer((EntityType)MFTEEntityRegistries.BONE_HAND_ENTITY.get(), BoneHandsRenderer::new);
      event.registerEntityRenderer((EntityType)MFTEEntityRegistries.REVENANT.get(), SpiritSamuraiRenderer::new);
      event.registerEntityRenderer((EntityType)MFTEEntityRegistries.ASHIGARU.get(), SpiritAshigaruRenderer::new);
      event.registerEntityRenderer((EntityType)MFTEEntityRegistries.PHANTOM_CAVALRY.get(), PhantomCavalryRenderer::new);
      event.registerEntityRenderer((EntityType)MFTEEntityRegistries.ANCHORING_KUNAI.get(), AnchoringKunaiRenderer::new);
      event.registerEntityRenderer((EntityType)MFTEEntityRegistries.SPIRIT_SHURIKEN.get(), SplittingShurikenRenderer::new);
      event.registerEntityRenderer((EntityType)MFTEEntityRegistries.SPIRIT_ARROW.get(), SpiritArrowRenderer::new);
      event.registerEntityRenderer((EntityType)MFTEEntityRegistries.SPIRIT_BULLET.get(), NoopRenderer::new);
      CuriosRendererRegistry.register((Item)MFTEItemRegistries.RITUAL_ORIHON.get(), SpellBookCurioRenderer::new);
      CuriosRendererRegistry.register((Item)MFTEItemRegistries.BAGUA_MIRROR.get(), MFTEBeltCurioRenderer::new);
      CuriosRendererRegistry.register((Item)MFTEItemRegistries.COINS_SWORD.get(), MFTEBeltCurioRenderer::new);
      CuriosRendererRegistry.register((Item)MFTEItemRegistries.RUSTED_COINS_SWORD.get(), MFTEBeltCurioRenderer::new);
   }

   @SubscribeEvent
   public static void onRegisterLayers(RegisterLayerDefinitions event) {
      event.registerLayerDefinition(NephriteCrystalRenderer.NephriteCrystalModel.LAYER_LOCATION, NephriteCrystalRenderer.NephriteCrystalModel::createBodyLayer);
      event.registerLayerDefinition(JadeBulletRenderer.MODEL_LAYER_LOCATION, JadeBulletRenderer::createBodyLayer);
   }

   @SubscribeEvent
   public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
      event.registerFluidType(new SimpleClientFluidType(ISS_MagicFromTheEast.id("block/soul")), new Holder[]{MFTEFluidRegistries.SOUL_TYPE});
   }

   @SubscribeEvent
   public static void registerClientTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
      event.register(ClientLoadableWeaponTooltip.LoadableWeaponTooltipComponent.class, ClientLoadableWeaponTooltip::new);
   }
}
