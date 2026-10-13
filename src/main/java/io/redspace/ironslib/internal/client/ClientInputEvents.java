package io.redspace.ironslib.internal.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import io.redspace.ironslib.statue.client.StatueTextureManager;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(
   modid = "irons_lib",
   bus = Bus.GAME,
   value = {Dist.CLIENT}
)
public final class ClientInputEvents {
   private static boolean showExpandedTooltip;

   @SubscribeEvent
   public static void onClientTick(Post event) {
      handleKeybinds();
      if (!FMLLoader.isProduction() && isKeyboardMouseInputDown(InputConstants.getKey(329, 0))) {
         StatueTextureManager.refresh();
      }
   }

   private static void handleKeybinds() {
      updateShowExpandedTooltip();
   }

   private static void updateShowExpandedTooltip() {
      showExpandedTooltip = isKeyboardMouseInputDown(Minecraft.getInstance().options.keyShift.getDefaultKey());
   }

   public static boolean isShowExpandedTooltip() {
      return showExpandedTooltip;
   }

   public static void setShowExpandedTooltip(boolean showExpandedTooltip) {
      ClientInputEvents.showExpandedTooltip = showExpandedTooltip;
   }

   private static boolean isKeyboardMouseInputDown(Key key) {
      int keyValue = key.getValue();
      long windowPointer = Minecraft.getInstance().getWindow().getWindow();
      if (key.getType() == Type.KEYSYM) {
         return GLFW.glfwGetKey(windowPointer, keyValue) > 0;
      } else {
         return key.getType() == Type.MOUSE ? GLFW.glfwGetMouseButton(windowPointer, keyValue) > 0 : false;
      }
   }
}
