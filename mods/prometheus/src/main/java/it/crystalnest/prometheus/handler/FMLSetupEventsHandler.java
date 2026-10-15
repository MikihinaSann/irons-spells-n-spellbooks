package it.crystalnest.prometheus.handler;

import it.crystalnest.prometheus.api.Fire;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.block.CustomCampfireBlock;
import it.crystalnest.prometheus.api.block.CustomFireBlock;
import it.crystalnest.prometheus.api.block.CustomLanternBlock;
import it.crystalnest.prometheus.api.block.CustomTorchBlock;
import it.crystalnest.prometheus.api.block.CustomWallTorchBlock;
import it.crystalnest.prometheus.api.client.FireClientManager;
import java.util.Objects;
import net.minecraft.client.particle.FlameParticle.Provider;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.CampfireRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;

@EventBusSubscriber(
   modid = "prometheus",
   bus = Bus.MOD
)
public final class FMLSetupEventsHandler {
   private FMLSetupEventsHandler() {
   }

   @SubscribeEvent
   public static void handle(FMLClientSetupEvent event) {
      FireClientManager.registerFires(FireManager.getFires());
      registerCutout(Fire.Component.CAMPFIRE_BLOCK, CustomCampfireBlock.class);
      registerCutout(Fire.Component.SOURCE_BLOCK, CustomFireBlock.class);
      registerCutout(Fire.Component.LANTERN_BLOCK, CustomLanternBlock.class);
      registerCutout(Fire.Component.TORCH_BLOCK, CustomTorchBlock.class);
      registerCutout(Fire.Component.WALL_TORCH_BLOCK, CustomWallTorchBlock.class);
   }

   @SubscribeEvent
   public static void handle(FMLCommonSetupEvent event) {
      FireManager.getComponentListList(Fire.Component.FIRE_CHARGE_ITEM)
         .forEach(charges -> charges.stream().filter(Objects::nonNull).forEach(DispenserBlock::registerProjectileBehavior));
   }

   @SubscribeEvent
   public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
      FireManager.getComponentListList(Fire.Component.FLAME_PARTICLE).forEach(flames -> flames.forEach(flame -> event.registerSpriteSet(flame, Provider::new)));
   }

   @SubscribeEvent
   public static void registerRenderers(RegisterRenderers event) {
      event.registerBlockEntityRenderer((BlockEntityType)FireManager.getCustomCampfireEntityType().get(), CampfireRenderer::new);
   }

   private static void registerCutout(Fire.Component<Block, Block> component, Class<?> clazz) {
      FireManager.getComponentListList(component)
         .forEach(values -> values.stream().filter(clazz::isInstance).forEach(value -> net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap.INSTANCE.putBlock(value, RenderType.cutout())));
   }
}
