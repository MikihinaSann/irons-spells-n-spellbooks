package io.redspace.ironslib;

import com.mojang.logging.LogUtils;
import io.redspace.ironslib.attribute.AttributeRemapRegistry;
import io.redspace.ironslib.attribute.data_handler.AttributeRemapReloadListener;
import io.redspace.ironslib.internal.compat.CompatHandler;
import io.redspace.ironslib.internal.network.PlatformPacketDistributor;
import io.redspace.ironslib.internal.network.packet.SyncPatreonDataPacket;
import io.redspace.ironslib.patreon.PatreonData;
import io.redspace.ironslib.patreon.PatreonHandler;
import io.redspace.ironslib.registry.IronsLibRegistries;
import java.util.ArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.slf4j.Logger;

@Mod("irons_lib")
public class IronsLib {
   public static final String MODID = "irons_lib";
   public static final String LEGACY_MODID = "irons_patreon_lib";
   public static final Logger LOGGER = LogUtils.getLogger();
   public static String version = "unknown";

   public IronsLib(IEventBus modEventBus, ModContainer modContainer) {
      PlatformPacketDistributor.register();
      version = modContainer.getModInfo().getVersion().toString();
      IronsLibRegistries.register(modEventBus);
      modEventBus.addListener(BuildCreativeModeTabContentsEvent.class, IronsLib::onBuildCreativeModeTabContents);
      modEventBus.addListener(FMLCommonSetupEvent.class, IronsLib::commonSetup);
      NeoForge.EVENT_BUS.addListener(ServerStartingEvent.class, IronsLib::onServerStarting);
      NeoForge.EVENT_BUS.addListener(ServerStoppedEvent.class, IronsLib::onServerShutdown);
      NeoForge.EVENT_BUS.addListener(PlayerLoggedInEvent.class, IronsLib::onPlayerLoggedIn);
      NeoForge.EVENT_BUS.addListener(AddReloadListenerEvent.class, IronsLib::onAddReloadListeners);
   }

   private static void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
      if (event.getTabKey().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS)) {
         event.accept(((Item)IronsLibRegistries.ItemRegistry.TRANSMOG_TABLE_BLOCK_ITEM.get()).getDefaultInstance(), TabVisibility.PARENT_AND_SEARCH_TABS);
         event.accept(((Item)IronsLibRegistries.ItemRegistry.PLAYER_STATUE_BLOCK_ITEM.get()).getDefaultInstance(), TabVisibility.PARENT_AND_SEARCH_TABS);
      }
   }

   public static void onServerStarting(ServerStartingEvent event) {
      PatreonHandler.initialize(event.getServer());
   }

   public static void onServerShutdown(ServerStoppedEvent event) {
      PatreonHandler.shutdown();
   }

   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof ServerPlayer serverPlayer) {
         PatreonData data = PatreonData.getInstance();
         if (!data.isEmpty()) {
            PlatformPacketDistributor.sendToPlayer(
               serverPlayer, new SyncPatreonDataPacket(data.getChronicleEntries(), new ArrayList<>(data.getProfileValues()))
            );
         }

         PlatformPacketDistributor.sendToPlayer(serverPlayer, AttributeRemapRegistry.buildSyncPacket());
      }
   }

   public static void onAddReloadListeners(AddReloadListenerEvent event) {
      event.addListener(new AttributeRemapReloadListener());
   }

   public static void commonSetup(FMLCommonSetupEvent event) {
      event.enqueueWork(() -> CompatHandler.init());
   }

   public static ResourceLocation id(String path) {
      return ResourceLocation.fromNamespaceAndPath("irons_lib", path);
   }
}
