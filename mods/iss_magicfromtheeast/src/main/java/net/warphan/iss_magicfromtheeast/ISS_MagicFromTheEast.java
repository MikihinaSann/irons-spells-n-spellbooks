package net.warphan.iss_magicfromtheeast;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.warphan.iss_magicfromtheeast.configs.MFTEServerConfigs;
import net.warphan.iss_magicfromtheeast.registries.MFTEArmorMaterialRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTEAttributeRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTEBlockRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTECreativeTabRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTEDataComponentRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTEEffectRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTEEnchantmentEffectRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTEEntityRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTEFeaturesRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTEFluidRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTEItemRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTELootRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTEParticleRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESoundRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESpellRegistries;
import net.warphan.iss_magicfromtheeast.setup.ModSetup;
import net.warphan.iss_magicfromtheeast.util.MFTEItemProperties;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod("iss_magicfromtheeast")
public class ISS_MagicFromTheEast {
   public static final String MOD_ID = "iss_magicfromtheeast";
   public static final Logger LOGGER = LogUtils.getLogger();

   public ISS_MagicFromTheEast(IEventBus modEventBus, ModContainer modContainer) {
      NeoForge.EVENT_BUS.register(this);
      ModSetup.setup();
      modEventBus.addListener(ModSetup::init);
      MFTEItemRegistries.register(modEventBus);
      MFTEBlockRegistries.register(modEventBus);
      MFTECreativeTabRegistries.register(modEventBus);
      MFTESchoolRegistries.register(modEventBus);
      MFTEAttributeRegistries.register(modEventBus);
      MFTESoundRegistries.register(modEventBus);
      MFTESpellRegistries.register(modEventBus);
      MFTEEntityRegistries.register(modEventBus);
      MFTEEffectRegistries.register(modEventBus);
      MFTEFluidRegistries.register(modEventBus);
      MFTEArmorMaterialRegistries.register(modEventBus);
      MFTEEnchantmentEffectRegistries.register(modEventBus);
      MFTEDataComponentRegistries.register(modEventBus);
      MFTELootRegistries.register(modEventBus);
      MFTEParticleRegistries.register(modEventBus);
      MFTEFeaturesRegistries.register(modEventBus);
      modContainer.registerConfig(Type.SERVER, MFTEServerConfigs.SPEC, String.format("%s-server.toml", "iss_magicfromtheeast"));
   }

   @SubscribeEvent
   public void onServerStarting(ServerStartingEvent event) {
   }

   public static ResourceLocation id(@NotNull String path) {
      return ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", path);
   }

   @EventBusSubscriber(
      modid = "iss_magicfromtheeast",
      value = {Dist.CLIENT}
   )
   public static class ClientModEvents {
      @SubscribeEvent
      public static void onClientSetup(FMLClientSetupEvent event) {
         MFTEItemProperties.addCustomItemProperties();
      }
   }
}
