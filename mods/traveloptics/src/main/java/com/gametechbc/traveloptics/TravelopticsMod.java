package com.gametechbc.traveloptics;

import com.gametechbc.traveloptics.api.TODataComponents;
import com.gametechbc.traveloptics.config.CommonConfig;
import com.gametechbc.traveloptics.config.SpellsConfig;
import com.gametechbc.traveloptics.init.TOAugments;
import com.gametechbc.traveloptics.init.TOCreativeTabs;
import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.init.TOEntities;
import com.gametechbc.traveloptics.init.TOItems;
import com.gametechbc.traveloptics.init.TOParticles;
import com.gametechbc.traveloptics.init.TOSounds;
import com.gametechbc.traveloptics.init.TOSpells;
import com.gametechbc.traveloptics.init.TOWildAugments;
import com.gametechbc.traveloptics.item.TOArmorMaterials;
import com.gametechbc.traveloptics.loot.TOLootModifiers;
import com.gametechbc.traveloptics.util.SummonTypes;
import com.mojang.logging.LogUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod("traveloptics")
public class TravelopticsMod {
   public static final String MODID = "traveloptics";
   private static final Logger LOGGER = LogUtils.getLogger();

   public TravelopticsMod(IEventBus modEventBus, ModContainer modContainer) {
      super();
      modEventBus.addListener(this::commonSetup);
      TOCreativeTabs.register(modEventBus);
      TOItems.register(modEventBus);
      TOAugments.register(modEventBus);
      TOWildAugments.register(modEventBus);
      TOSounds.register(modEventBus);
      TOEffects.register(modEventBus);
      TOSpells.register(modEventBus);
      TOParticles.register(modEventBus);
      TOEntities.register(modEventBus);
      TOArmorMaterials.register(modEventBus);
      TODataComponents.register(modEventBus);
      TOLootModifiers.register(modEventBus);
      NeoForge.EVENT_BUS.register(this);
      Path configPath = FMLPaths.CONFIGDIR.get().resolve("traveloptics");

      try {
         Files.createDirectories(configPath);
      } catch (Exception var5) {
         LOGGER.error("Failed to create config directory: " + configPath, var5);
      }

      modContainer.registerConfig(Type.STARTUP, SpellsConfig.SPEC, configPath.resolve("traveloptics-spells.toml").toString());
      modContainer.registerConfig(Type.STARTUP, CommonConfig.SPEC, configPath.resolve("traveloptics-common.toml").toString());
   }

   private void commonSetup(FMLCommonSetupEvent event) {
      SummonTypes.registerKaijuSummons();
      SummonTypes.registerMinibossSummons();
      SummonTypes.registerGroupSummons();
   }

   @SubscribeEvent
   public void onServerStarting(ServerStartingEvent event) {
   }

   public static ResourceLocation id(@NotNull String path) {
      return new ResourceLocation("traveloptics", path);
   }

   @EventBusSubscriber(
      modid = "traveloptics",
      bus = Bus.MOD,
      value = {Dist.CLIENT}
   )
   public static class ClientModEvents {
      public ClientModEvents() {
         super();
      }

      @SubscribeEvent
      public static void onClientSetup(FMLClientSetupEvent event) {
      }
   }
}
