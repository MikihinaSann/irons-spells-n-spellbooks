package io.redspace.ironslib.util;

import io.redspace.ironslib.internal.client.ClientInputEvents;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class TooltipUtils {
   public static void addShiftTooltip(List<Component> currentTooltip, List<Component> tooltipToAdd) {
      addShiftTooltip(
         currentTooltip,
         Component.translatable(
               "tooltip.irons_lib.shift_tooltip", new Object[]{Component.translatable("key.keyboard.left.shift").withStyle(ChatFormatting.DARK_GRAY)}
            )
            .withStyle(ChatFormatting.GRAY),
         Optional.of(
            Component.translatable(
                  "tooltip.irons_lib.shift_tooltip", new Object[]{Component.translatable("key.keyboard.left.shift").withStyle(ChatFormatting.WHITE)}
               )
               .withStyle(ChatFormatting.GRAY)
         ),
         tooltipToAdd
      );
   }

   public static void addShiftTooltip(List<Component> currentTooltip, Component shiftHeader, List<Component> tooltipToAdd) {
      addShiftTooltip(currentTooltip, shiftHeader, Optional.empty(), tooltipToAdd);
   }

   public static void addShiftTooltip(
      List<Component> currentTooltip, Component shiftHeader, Optional<Component> shiftHeaderActive, List<Component> tooltipToAdd
   ) {
      if (ClientInputEvents.isShowExpandedTooltip()) {
         shiftHeaderActive.ifPresent(currentTooltip::add);
         currentTooltip.addAll(tooltipToAdd);
      } else {
         currentTooltip.add(shiftHeader);
      }
   }
}
