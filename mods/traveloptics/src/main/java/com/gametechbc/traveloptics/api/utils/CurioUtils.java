package com.gametechbc.traveloptics.api.utils;

import com.gametechbc.traveloptics.api.item.AbstractRandomImbuingAttributeCurio;
import com.gametechbc.traveloptics.api.item.AdvancedRandomImbuingCurio;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

public class CurioUtils {
   public CurioUtils() {
      super();
   }

   public static boolean getWearingCurio(LivingEntity entity, Item curioItem) {
      return CuriosApi.getCuriosInventory(entity).map(curios -> !curios.findCurios(item -> item != null && item.is(curioItem)).isEmpty()).orElse(false);
   }

   public static boolean getEchoCurios(LivingEntity entity) {
      return CuriosApi.getCuriosInventory(entity)
         .map(curios -> !curios.findCurios(item -> item != null && item.getItem() instanceof AbstractRandomImbuingAttributeCurio).isEmpty())
         .orElse(false);
   }

   public static boolean getAdvancedEchoCurios(LivingEntity entity) {
      return CuriosApi.getCuriosInventory(entity)
         .map(curios -> !curios.findCurios(item -> item != null && item.getItem() instanceof AdvancedRandomImbuingCurio).isEmpty())
         .orElse(false);
   }

   public static void broadcastCurioBreakEvent(SlotContext slotContext) {
      CuriosApi.broadcastCurioBreakEvent(slotContext);
   }
}
