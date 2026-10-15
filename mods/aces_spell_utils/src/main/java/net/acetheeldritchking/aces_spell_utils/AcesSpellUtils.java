package net.acetheeldritchking.aces_spell_utils;

import com.mojang.logging.LogUtils;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.render.SpellBookCurioRenderer;
import net.acetheeldritchking.aces_spell_utils.entity.render.items.SheathCurioRenderer;
import net.acetheeldritchking.aces_spell_utils.items.curios.SheathCurioItem;
import net.acetheeldritchking.aces_spell_utils.items.example.ExampleArmorMaterialRegistry;
import net.acetheeldritchking.aces_spell_utils.registries.ASAttributeRegistry;
import net.acetheeldritchking.aces_spell_utils.registries.ASCreativeModeTabs;
import net.acetheeldritchking.aces_spell_utils.registries.ExampleItemRegistry;
import net.acetheeldritchking.aces_spell_utils.utils.AcesSpellUtilsConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod("aces_spell_utils")
public class AcesSpellUtils {
   public static final String MOD_ID = "aces_spell_utils";
   public static final Logger LOGGER = LogUtils.getLogger();

   public AcesSpellUtils(IEventBus modEventBus, ModContainer modContainer) {
      modEventBus.addListener(this::commonSetup);
      ASAttributeRegistry.register(modEventBus);
      NeoForge.EVENT_BUS.register(this);
      if (registerExamplesInDev()) {
         ASCreativeModeTabs.register(modEventBus);
      }

      ExampleItemRegistry.register(modEventBus);
      ExampleArmorMaterialRegistry.register(modEventBus);
      modContainer.registerConfig(Type.COMMON, AcesSpellUtilsConfig.SPEC);
   }

   static boolean registerExamplesInDev() {
      return !FMLEnvironment.production;
   }

   private void commonSetup(FMLCommonSetupEvent event) {
   }

   @SubscribeEvent
   public void onServerStarting(ServerStartingEvent event) {
   }

   public static ResourceLocation id(@NotNull String path) {
      return ResourceLocation.fromNamespaceAndPath("aces_spell_utils", path);
   }

   @EventBusSubscriber(
      modid = "aces_spell_utils",
      value = {Dist.CLIENT}
   )
   public static class ClientModEvents {
      @SubscribeEvent
      public static void onClientSetup(FMLClientSetupEvent event) {
         event.enqueueWork(
            () -> {
               ExampleItemRegistry.getASUItems()
                  .stream()
                  .filter(item -> item.get() instanceof SpellBook)
                  .forEach(item -> CuriosRendererRegistry.register((Item)item.get(), SpellBookCurioRenderer::new));
               ExampleItemRegistry.getASUItems()
                  .stream()
                  .filter(item -> item.get() instanceof SheathCurioItem)
                  .forEach(item -> CuriosRendererRegistry.register((Item)item.get(), SheathCurioRenderer::new));
            }
         );
      }
   }
}
