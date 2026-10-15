package net.alshanex.alshanex_familiars.event;

import net.alshanex.alshanex_familiars.item.MonocleItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RenderHandEvent;

@EventBusSubscriber(
   modid = "alshanex_familiars",
   bus = Bus.GAME,
   value = {Dist.CLIENT}
)
public class ClientEvents {
   @SubscribeEvent
   public static void onRenderHand(RenderHandEvent event) {
      Player player = Minecraft.getInstance().player;
      if (player != null && player.isUsingItem() && player.getUseItem().getItem() instanceof MonocleItem) {
         event.setCanceled(true);
      }
   }
}
