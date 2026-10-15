package com.somake.somakespells.event;

import com.somake.somakespells.compat.BornInChaosCompat;
import com.somake.somakespells.item.ElementalChargeNecklaceItem;
import com.somake.somakespells.registries.ModCompatItems;
import com.somake.somakespells.registries.ModItems;
import com.somake.somakespells.util.FireElementContext;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "somakespells",
   bus = Bus.GAME
)
public class ElementalNecklaceChargeHandler {
   private static final String RITUAL_FLAME_SPELL_ID = "somakespells:ritual_flame";

   @SubscribeEvent
   public static void onSpellCast(SpellOnCastEvent event) {
      if (!event.getEntity().level().isClientSide() && event.getSchoolType() != null && SchoolRegistry.FIRE_RESOURCE.equals(event.getSchoolType().getId())) {
         if (!"somakespells:ritual_flame".equals(event.getSpellId())) {
            Player player = event.getEntity();
            if (!MagicData.getPlayerMagicData(player).getPlayerRecasts().hasRecastForSpell(event.getSpellId())) {
               FireElementContext context = FireElementContext.resolve(player);
               if (!context.fromHeldItem() && !context.isNormalFire()) {
                  Optional<ItemStack> necklaceStack = context.isInfernalFire()
                     ? (
                        BornInChaosCompat.isLoaded()
                           ? ElementalChargeNecklaceItem.findEquippedStack(player, (Item)ModCompatItems.INFERNAL_FIRE_NECKLACE.get())
                           : Optional.empty()
                     )
                     : ElementalChargeNecklaceItem.findEquippedStack(player, ModItems.SOUL_FIRE_NECKLACE.get());
                  if (!necklaceStack.isEmpty() && necklaceStack.get().getItem() instanceof ElementalChargeNecklaceItem necklaceItem) {
                     if (necklaceItem.consumeCharge(necklaceStack.get())) {
                        if (!necklaceItem.hasCharges(necklaceStack.get())) {
                           player.displayClientMessage(Component.translatable("item.somakespells.elemental_necklace.empty"), true);
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
