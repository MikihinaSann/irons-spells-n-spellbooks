package com.somake.somakespells.event;

import com.somake.somakespells.item.DormantGrimoireProgressHelper;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(
   modid = "somakespells",
   bus = Bus.GAME
)
public class DormantGrimoireProgressionHandler {
   @SubscribeEvent
   public static void onLivingDeath(LivingDeathEvent event) {
      if (!event.getEntity().level().isClientSide()) {
         if (event.getSource().getEntity() instanceof Player player) {
            ItemStack dormantGrimoire = DormantGrimoireProgressHelper.getHeldDormantGrimoire(player);
            if (!dormantGrimoire.isEmpty()) {
               LivingEntity target = event.getEntity();
               DormantGrimoireProgressHelper.recordKill(dormantGrimoire, target);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onSpellCast(SpellOnCastEvent event) {
      if (!event.getEntity().level().isClientSide && event.getSchoolType() != null) {
         Player dormantGrimoire = event.getEntity();
         if (dormantGrimoire instanceof Player) {
            ItemStack dormantGrimoirex = DormantGrimoireProgressHelper.getHeldDormantGrimoire(dormantGrimoire);
            if (!dormantGrimoirex.isEmpty()) {
               DormantGrimoireProgressHelper.recordSpellCast(dormantGrimoirex, event.getSchoolType().getId());
            }
         }
      }
   }
}
