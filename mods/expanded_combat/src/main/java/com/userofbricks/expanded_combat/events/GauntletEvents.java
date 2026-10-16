package com.userofbricks.expanded_combat.events;

import com.userofbricks.expanded_combat.init.ECItems;
import com.userofbricks.expanded_combat.init.ItemDataComponents;
import com.userofbricks.expanded_combat.item.GauntletItem;
import com.userofbricks.expanded_combat.item.QuiverItem;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickEmpty;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

@EventBusSubscriber(
   modid = "expanded_combat",
   bus = Bus.GAME
)
public class GauntletEvents {
   public static void DamageGauntletEvent(AttackEntityEvent event) {
      Player player = event.getEntity();
      if (!player.isCreative()) {
         Optional<ICuriosItemHandler> handlerOptional = CuriosApi.getCuriosInventory(event.getEntity());
         if (handlerOptional.isPresent() && player.level() instanceof ServerLevel serverLevel) {
            List<SlotResult> slotResults = handlerOptional.get().findCurios(itemStack -> itemStack.getItem() instanceof GauntletItem);
            if (slotResults.isEmpty()) {
               return;
            }

            for (SlotResult slotResult : slotResults) {
               ItemStack stack = slotResult.stack();
               SlotContext slotContext = slotResult.slotContext();
               if (stack.getItem() instanceof GauntletItem) {
                  stack.hurtAndBreak(1, serverLevel, player, item -> CuriosApi.broadcastCurioBreakEvent(slotContext));
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void moreDamageSources(LivingIncomingDamageEvent ev) {
      LivingEntity target = ev.getEntity();
      Optional<SlotResult> optionalSlotResult = CuriosApi.getCuriosInventory(target)
         .flatMap(curiosInventory -> curiosInventory.findFirstCurio((Item)ECItems.BERSERK_GAUNTLETS.get()));
      if (optionalSlotResult.isPresent()) {
         SlotResult slotResult = optionalSlotResult.get();
         int charge = (Integer)slotResult.stack().getOrDefault(ItemDataComponents.CHARGE, 0);
         if (charge >= 20) {
            target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 2));
            slotResult.stack().set(ItemDataComponents.CHARGE, charge - 20);
         } else {
            slotResult.stack().set(ItemDataComponents.CHARGE, charge + 1);
         }
      }
   }

   @SubscribeEvent
   public static void pulOutArrow(RightClickEmpty event) {
      Player player = event.getEntity();
      Optional<ICuriosItemHandler> handlerOptional = CuriosApi.getCuriosInventory(player);
      Optional<SlotResult> optionalSlotResult = handlerOptional.flatMap(
         curiosInventory -> curiosInventory.findFirstCurio((Item)ECItems.FIGHTERS_GAUNTLETS.get())
      );
      if (optionalSlotResult.isPresent() && player.getArrowCount() >= 1) {
         Optional<SlotResult> optionalQuiverSlotResult = handlerOptional.get().findFirstCurio(stack -> stack.getItem() instanceof QuiverItem);
         if (optionalQuiverSlotResult.isPresent()) {
            ItemStack quiverStack = optionalQuiverSlotResult.get().stack();
            QuiverItem quiverItem = (QuiverItem)quiverStack.getItem();
            BundleContents bundlecontents = (BundleContents)quiverStack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
            QuiverItem.MutableQuiverContents bundleContents$mutable = new QuiverItem.MutableQuiverContents(
               bundlecontents, quiverItem.getMaterial().offense().quiverStacks()
            );
            int i = bundleContents$mutable.tryInsert(new ItemStack(Items.ARROW));
            if (i > 0) {
               quiverStack.set(DataComponents.BUNDLE_CONTENTS, bundleContents$mutable.toImmutable());
               quiverItem.playInsert(player);
               player.awardStat(Stats.ITEM_PICKED_UP.get(Items.ARROW), i);
            } else {
               player.getInventory().placeItemBackInInventory(new ItemStack(Items.ARROW));
            }
         } else {
            player.getInventory().placeItemBackInInventory(new ItemStack(Items.ARROW));
         }

         player.setArrowCount(player.getArrowCount() - 1);
      }
   }
}
