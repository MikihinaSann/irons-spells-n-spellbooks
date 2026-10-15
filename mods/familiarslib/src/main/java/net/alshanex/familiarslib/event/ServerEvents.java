package net.alshanex.familiarslib.event;

import net.alshanex.familiarslib.FamiliarsLib;
import net.alshanex.familiarslib.entity.AbstractSpellCastingPet;
import net.alshanex.familiarslib.item.AbstractMultiSelectionCurio;
import net.alshanex.familiarslib.util.CurioUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

@EventBusSubscriber(
   modid = "familiarslib"
)
public class ServerEvents {
   @SubscribeEvent
   public static void onDamageTaken(Pre event) {
      if (event.getSource().getEntity() instanceof AbstractSpellCastingPet pet && pet.isAlliedTo(event.getEntity())) {
         event.setNewDamage(0.0F);
      }
   }

   @SubscribeEvent
   public static void onCurioUnequip(CurioChangeEvent event) {
      if (event.getEntity() instanceof ServerPlayer serverPlayer && !serverPlayer.level().isClientSide) {
         ItemStack fromStack = event.getFrom();
         if (!fromStack.isEmpty() && fromStack.getItem() instanceof AbstractMultiSelectionCurio) {
            FamiliarsLib.LOGGER.debug("Detected Multi Selection curio unequip for player {}", serverPlayer.getName().getString());

            try {
               CurioUtils.handleMultiSelectionCurioUnequip(serverPlayer, fromStack);
            } catch (Exception var4) {
               FamiliarsLib.LOGGER.error("Error handling Multi Selection curio unequip for player {}: ", serverPlayer.getName().getString(), var4);
            }
         }
      }
   }
}
