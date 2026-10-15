package com.somake.somakespells.event;

import com.somake.somakespells.item.spellbooks.AbstractElementalProgressSpellBook;
import com.somake.somakespells.item.spellbooks.SpellbookProgressHelper;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.util.Utils;
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
public class SpellbookProgressionHandler {
   @SubscribeEvent
   public static void onSpellCast(SpellOnCastEvent event) {
      if (!event.getEntity().level().isClientSide && event.getCastSource() == CastSource.SPELLBOOK && event.getSchoolType() != null) {
         ItemStack equippedSpellbook = Utils.getPlayerSpellbookStack(event.getEntity());
         if (equippedSpellbook != null && !equippedSpellbook.isEmpty()) {
            if (equippedSpellbook.getItem() instanceof AbstractElementalProgressSpellBook book) {
               if (book.getTrackedSchoolId().equals(event.getSchoolType().getId())) {
                  SpellbookProgressHelper.recordSpellCast(equippedSpellbook, book);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeath(LivingDeathEvent event) {
      if (!event.getEntity().level().isClientSide()) {
         if (event.getSource().getEntity() instanceof Player player) {
            ItemStack equippedSpellbook = Utils.getPlayerSpellbookStack(player);
            if (equippedSpellbook != null && !equippedSpellbook.isEmpty()) {
               if (equippedSpellbook.getItem() instanceof AbstractElementalProgressSpellBook book) {
                  LivingEntity var6 = event.getEntity();
                  SpellbookProgressHelper.recordKill(equippedSpellbook, book, var6);
               }
            }
         }
      }
   }
}
