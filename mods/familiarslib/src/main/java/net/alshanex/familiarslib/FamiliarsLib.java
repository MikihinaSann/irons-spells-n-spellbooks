package net.alshanex.familiarslib;

import com.mojang.logging.LogUtils;
import net.alshanex.familiarslib.registry.AttachmentRegistry;
import net.alshanex.familiarslib.registry.AttributeRegistry;
import net.alshanex.familiarslib.registry.ComponentRegistry;
import net.alshanex.familiarslib.registry.CriteriaTriggersRegistry;
import net.alshanex.familiarslib.registry.FParticleRegistry;
import net.alshanex.familiarslib.registry.FSchoolRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod("familiarslib")
public class FamiliarsLib {
   public static final String MODID = "familiarslib";
   public static final Logger LOGGER = LogUtils.getLogger();

   public FamiliarsLib(IEventBus modEventBus, ModContainer modContainer) {
      modEventBus.addListener(this::commonSetup);
      AttachmentRegistry.register(modEventBus);
      AttributeRegistry.ATTRIBUTES.register(modEventBus);
      CriteriaTriggersRegistry.TRIGGERS.register(modEventBus);
      FParticleRegistry.register(modEventBus);
      FSchoolRegistry.SCHOOLS.register(modEventBus);
      ComponentRegistry.COMPONENTS.register(modEventBus);
      NeoForge.EVENT_BUS.register(this);
      modEventBus.addListener(this::addCreative);
   }

   private void commonSetup(FMLCommonSetupEvent event) {
   }

   private void addCreative(BuildCreativeModeTabContentsEvent event) {
   }

   @SubscribeEvent
   public void onServerStarting(ServerStartingEvent event) {
   }

   public static boolean isModLoaded(String modId) {
      return ModList.get().isLoaded(modId);
   }
}
