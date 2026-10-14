package com.gametechbc.traveloptics.effects.frozen_sight;

import com.gametechbc.traveloptics.init.TOEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(
   modid = "traveloptics",
   value = {Dist.CLIENT}
)
public class FrozenSightClientHandler {
   private static float lockedYaw;
   private static float lockedPitch;
   private static boolean isLocked = false;

   public FrozenSightClientHandler() {
      super();
   }

   @SubscribeEvent
   public static void onRenderLevelStage(RenderLevelStageEvent event) {
      Minecraft minecraft = Minecraft.getInstance();
      Player player = minecraft.player;
      if (player != null) {
         MobEffectInstance effectInstance = player.getEffect(TOEffects.FROZEN_SIGHT_EFFECT);
         if (effectInstance != null) {
            if (!isLocked) {
               lockedYaw = player.getYRot();
               lockedPitch = player.getXRot();
               isLocked = true;
            }

            player.setYRot(lockedYaw);
            player.setXRot(lockedPitch);
         } else {
            isLocked = false;
         }
      }
   }
}
