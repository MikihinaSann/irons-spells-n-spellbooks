package io.redspace.ironslib.internal.client;

import io.redspace.ironslib.kinetic_weapon.client.KineticHitFeedback;
import io.redspace.ironslib.patreon.PatreonData;
import io.redspace.ironslib.transmog.TransmogHolder;
import io.redspace.ironslib.transmog.TransmogItemData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(
   modid = "irons_lib",
   value = {Dist.CLIENT}
)
public class ClientEvents {
   @SubscribeEvent
   public static void onLoggingOut(LoggingOut event) {
      KineticHitFeedback.reset();
   }

   @SubscribeEvent
   public static void handleItemTooltips(ItemTooltipEvent event) {
      ItemStack stack = event.getItemStack();
      if (TransmogItemData.has(stack)) {
         TransmogItemData itemData = TransmogItemData.get(stack);
         TransmogHolder transmog = itemData.transmog();
         List<Component> transmogTooltip = new ArrayList<>();
         transmogTooltip.add(
            Component.translatable(
                  "tooltip.irons_lib.transmog_title", new Object[]{Component.translatable(transmog.descriptionId()).withStyle(ChatFormatting.WHITE)}
               )
               .withStyle(ChatFormatting.LIGHT_PURPLE)
         );
         if (transmog.dyeConfig().dyeable() && (itemData.dyeColor() & 16777215) != transmog.dyeConfig().defaultColor()) {
            transmogTooltip.add(
               Component.literal(String.format("  #%s", Integer.toHexString(itemData.dyeColor() & 16777215)))
                  .withStyle(Style.EMPTY.withColor(itemData.dyeColor()))
            );
         }

         if (event.getEntity() != null && !PatreonData.getPatreonPermissions(event.getEntity()).canUse(transmog)) {
            transmogTooltip.add(Component.translatable("tooltip.irons_lib.transmog_failure").withStyle(ChatFormatting.RED));
         }

         event.getToolTip().addAll(1, transmogTooltip);
      }
   }
}
