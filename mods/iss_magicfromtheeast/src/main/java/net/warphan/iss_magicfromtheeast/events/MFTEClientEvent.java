package net.warphan.iss_magicfromtheeast.events;

import net.minecraft.client.gui.Gui.HeartType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.event.entity.player.PlayerHeartTypeEvent;
import net.warphan.iss_magicfromtheeast.registries.MFTEEffectRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTEItemRegistries;
import net.warphan.iss_magicfromtheeast.setup.MFTEHeartTypes;

@EventBusSubscriber(
   modid = "iss_magicfromtheeast",
   value = {Dist.CLIENT}
)
public class MFTEClientEvent {
   @SubscribeEvent
   public static void onComputeFovModifierEvent(ComputeFovModifierEvent event) {
      if (event.getPlayer().isUsingItem() && event.getPlayer().getUseItem().getItem() == MFTEItemRegistries.SOULPIERCER.get()) {
         float fovModifier = 1.0F;
         int ticksUsingItem = event.getPlayer().getTicksUsingItem();
         float deltaTicks = ticksUsingItem / 20.0F;
         if (deltaTicks > 1.0F) {
            deltaTicks = 1.0F;
         } else {
            deltaTicks *= deltaTicks;
         }

         fovModifier *= 1.0F - deltaTicks * 0.15F;
         event.setNewFovModifier(fovModifier);
      }
   }

   @SubscribeEvent
   public static void effectChangeHeartType(PlayerHeartTypeEvent event) {
      Player player = event.getEntity();
      if (player.hasEffect(MFTEEffectRegistries.REVERSAL_HEALING)) {
         event.setType((HeartType)MFTEHeartTypes.JADE_HEART_PROXY.getValue());
      } else if (player.hasEffect(MFTEEffectRegistries.SOULBURN)) {
         event.setType((HeartType)MFTEHeartTypes.SOUL_HEART_PROXY.getValue());
      }
   }
}
