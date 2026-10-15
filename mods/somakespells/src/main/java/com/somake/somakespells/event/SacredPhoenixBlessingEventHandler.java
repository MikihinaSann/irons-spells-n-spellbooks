package com.somake.somakespells.event;

import com.somake.somakespells.config.StartupFeatures;
import com.somake.somakespells.entity.SacredPhoenixBlessingControllerEntity;
import com.somake.somakespells.registries.ModMobEffects;
import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Start;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;

@EventBusSubscriber(
   modid = "somakespells",
   bus = Bus.GAME
)
public final class SacredPhoenixBlessingEventHandler {
   private SacredPhoenixBlessingEventHandler() {
   }

   @SubscribeEvent
   public static void onSpellPreCast(SpellPreCastEvent event) {
      if (isIncapacitated(event.getEntity())) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onAttackEntity(AttackEntityEvent event) {
      if (isIncapacitated(event.getEntity())) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onLeftClickBlock(LeftClickBlock event) {
      if (isIncapacitated(event.getEntity())) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onRightClickBlock(RightClickBlock event) {
      if (isIncapacitated(event.getEntity())) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onRightClickItem(RightClickItem event) {
      if (isIncapacitated(event.getEntity())) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onEntityInteract(EntityInteract event) {
      if (isIncapacitated(event.getEntity())) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onUseItem(Start event) {
      if (isIncapacitated(event.getEntity())) {
         event.setCanceled(true);
      }
   }

   public static boolean isIncapacitated(LivingEntity entity) {
      if (!StartupFeatures.isSacredPhoenixBlessingEnabled()) {
         return false;
      } else if (entity.hasEffect(ModMobEffects.SACRED_PHOENIX_EXHAUSTION)) {
         return true;
      } else {
         return entity instanceof Player player && entity.level() instanceof ServerLevel level
            ? SacredPhoenixBlessingControllerEntity.hasActiveLink(level, player)
            : false;
      }
   }
}
