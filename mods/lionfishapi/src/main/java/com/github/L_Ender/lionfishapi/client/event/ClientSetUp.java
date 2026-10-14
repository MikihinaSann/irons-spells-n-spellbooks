package com.github.L_Ender.lionfishapi.client.event;

import com.github.L_Ender.lionfishapi.client.screen.OptifineWarningScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.neoforged.neoforge.client.event.ScreenEvent.Init.Post;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = "lionfishapi", value = Dist.CLIENT, bus = Bus.MOD)
public class ClientSetUp {
   public static boolean optifinePresent = false;

   @SubscribeEvent
   public static void clientSetup(FMLClientSetupEvent evt) {
      try {
         Class.forName("net.optifine.Config");
         optifinePresent = true;
      } catch (ClassNotFoundException e) {
         optifinePresent = false;
      }
   }

   @SubscribeEvent
   public static void registerEntityRenders(RegisterRenderers event) {
   }

   @EventBusSubscriber(value = Dist.CLIENT, bus = Bus.GAME, modid = "lionfishapi")
   public static class ForgeEvents {
      private static boolean firstTitleScreenShown = false;

      @SubscribeEvent
      public static void showOptifineWarning(Post event) {
         if (!firstTitleScreenShown && event.getScreen() instanceof TitleScreen) {
            if (ClientSetUp.optifinePresent) {
               Minecraft.getInstance().setScreen(new OptifineWarningScreen(event.getScreen()));
            }

            firstTitleScreenShown = true;
         }
      }
   }
}
