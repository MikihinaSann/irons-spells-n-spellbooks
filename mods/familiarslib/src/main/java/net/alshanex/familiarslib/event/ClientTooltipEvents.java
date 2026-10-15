package net.alshanex.familiarslib.event;

import net.alshanex.familiarslib.util.consumables.ConsumableTooltipHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(
   modid = "familiarslib",
   bus = Bus.GAME,
   value = {Dist.CLIENT}
)
public class ClientTooltipEvents {
   @SubscribeEvent
   public static void onItemTooltip(ItemTooltipEvent event) {
      ConsumableTooltipHelper.addConsumableTooltip(event.getItemStack(), event.getToolTip(), event.getFlags());
   }
}
