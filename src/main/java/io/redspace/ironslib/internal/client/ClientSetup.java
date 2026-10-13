package io.redspace.ironslib.internal.client;

import io.redspace.ironslib.registry.IronsLibRegistries;
import io.redspace.ironslib.statue.block.statue_block.player.PlayerStatueBlockRenderer;
import io.redspace.ironslib.statue.block.statue_block.player.PlayerStatueItemClientExtensions;
import io.redspace.ironslib.transmog.block.transmog_table.TransmogTableScreen;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(
   modid = "irons_lib",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class ClientSetup {
   @SubscribeEvent
   public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
      event.registerItem(new PlayerStatueItemClientExtensions(), new Holder[]{IronsLibRegistries.ItemRegistry.PLAYER_STATUE_BLOCK_ITEM});
   }

   @SubscribeEvent
   public static void registerMenuScreen(RegisterMenuScreensEvent event) {
      event.register(IronsLibRegistries.MenuRegistry.TRANSMOG_TABLE_MENU.get(), TransmogTableScreen::new);
   }

   @SubscribeEvent
   public static void registerRenderers(RegisterRenderers event) {
      event.registerBlockEntityRenderer((BlockEntityType)IronsLibRegistries.BlockRegistry.STATUE_BLOCK_ENTITY.get(), PlayerStatueBlockRenderer::new);
      event.registerBlockEntityRenderer((BlockEntityType)IronsLibRegistries.Legacy.BlockRegistry.STATUE_BLOCK_ENTITY.get(), PlayerStatueBlockRenderer::new);
   }
}
