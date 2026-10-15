package net.alshanex.tunes_n_tomes;

import com.mojang.logging.LogUtils;
import net.alshanex.tunes_n_tomes.registry.TArmorSets;
import net.alshanex.tunes_n_tomes.registry.TAttachmentRegistry;
import net.alshanex.tunes_n_tomes.registry.TAttributeRegistry;
import net.alshanex.tunes_n_tomes.registry.TCreativeTab;
import net.alshanex.tunes_n_tomes.registry.TEffectRegistry;
import net.alshanex.tunes_n_tomes.registry.TEntityRegistry;
import net.alshanex.tunes_n_tomes.registry.TItemRegistry;
import net.alshanex.tunes_n_tomes.registry.TParticleRegistry;
import net.alshanex.tunes_n_tomes.registry.TSchoolRegistry;
import net.alshanex.tunes_n_tomes.registry.TSoundRegistry;
import net.alshanex.tunes_n_tomes.registry.TSpellRegistry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod("tunes_n_tomes")
public class TunesNTomesMod {
   public static final String MODID = "tunes_n_tomes";
   public static final Logger LOGGER = LogUtils.getLogger();

   public TunesNTomesMod(IEventBus modEventBus, ModContainer modContainer) {
      modEventBus.addListener(this::commonSetup);
      TAttachmentRegistry.register(modEventBus);
      TAttributeRegistry.ATTRIBUTES.register(modEventBus);
      TCreativeTab.register(modEventBus);
      TEffectRegistry.register(modEventBus);
      TEntityRegistry.register(modEventBus);
      TItemRegistry.register(modEventBus);
      TParticleRegistry.register(modEventBus);
      TSchoolRegistry.SCHOOLS.register(modEventBus);
      TSoundRegistry.register(modEventBus);
      TSpellRegistry.register(modEventBus);
      NeoForge.EVENT_BUS.register(this);
      modEventBus.addListener(this::addCreative);
      modContainer.registerConfig(Type.SERVER, TunesNTomesConfig.SPEC);
   }

   private void commonSetup(FMLCommonSetupEvent event) {
      event.enqueueWork(TArmorSets::register);
   }

   private void addCreative(BuildCreativeModeTabContentsEvent event) {
   }

   @SubscribeEvent
   public void onServerStarting(ServerStartingEvent event) {
   }

   public static ResourceLocation id(@NotNull String path) {
      return ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", path);
   }
}
