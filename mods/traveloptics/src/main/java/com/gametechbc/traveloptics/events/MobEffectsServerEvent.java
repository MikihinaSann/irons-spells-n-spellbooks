package com.gametechbc.traveloptics.events;

import com.gametechbc.traveloptics.init.TOEffects;
import io.redspace.ironsspellbooks.api.events.ChangeManaEvent;
import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Pre;

@EventBusSubscriber(
   modid = "traveloptics"
)
public class MobEffectsServerEvent {
   public MobEffectsServerEvent() {
      super();
   }

   @SubscribeEvent
   public static void onPlayerCastInBlackout(SpellPreCastEvent event) {
      if (event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide) {
         MobEffectInstance blackoutEffect = player.getEffect(TOEffects.BLACKOUT);
         if (blackoutEffect != null) {
            event.setCanceled(true);
            player.displayClientMessage(Component.literal("Blackout Active: Spells are suppressed!").withStyle(ChatFormatting.RED), true);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerRightClickItemInBlackout(RightClickItem event) {
      if (event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide) {
         MobEffectInstance blackoutEffect = player.getEffect(TOEffects.BLACKOUT);
         if (blackoutEffect != null) {
            event.setCanceled(true);
            player.displayClientMessage(Component.literal("Blackout Active: Cannot use item!").withStyle(ChatFormatting.RED), true);
         }
      }
   }

   @SubscribeEvent
   public static void onManaRegenInBlackout(ChangeManaEvent event) {
      if (event.getEntity().hasEffect(TOEffects.BLACKOUT)) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onPlayerCastInCasting(SpellPreCastEvent event) {
      if (event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide) {
         MobEffectInstance blackoutEffect = player.getEffect(TOEffects.CASTING_EFFECT);
         if (blackoutEffect != null) {
            event.setCanceled(true);
            player.displayClientMessage(Component.translatable("effect.traveloptics.casting.warning").withStyle(ChatFormatting.RED), true);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerRightClickItemInCasting(RightClickItem event) {
      if (event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide) {
         MobEffectInstance blackoutEffect = player.getEffect(TOEffects.CASTING_EFFECT);
         if (blackoutEffect != null) {
            event.setCanceled(true);
            player.displayClientMessage(Component.translatable("effect.traveloptics.casting.warning").withStyle(ChatFormatting.RED), true);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerRightClickBlockInCasting(RightClickBlock event) {
      if (event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide) {
         MobEffectInstance blackoutEffect = player.getEffect(TOEffects.CASTING_EFFECT);
         if (blackoutEffect != null) {
            event.setCanceled(true);
            player.displayClientMessage(Component.translatable("effect.traveloptics.casting.warning").withStyle(ChatFormatting.RED), true);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerRightClickItemInFrozenSight(RightClickItem event) {
      if (event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide) {
         MobEffectInstance frozenSightEffect = player.getEffect(TOEffects.FROZEN_SIGHT_EFFECT);
         if (frozenSightEffect != null) {
            event.setCanceled(true);
            player.displayClientMessage(Component.literal("Wait till it ends!").withStyle(ChatFormatting.RED), true);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_STAND_HIT, SoundSource.PLAYERS, 0.5F, 1.0F);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerCastInFrozenSight(SpellPreCastEvent event) {
      if (event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide) {
         MobEffectInstance frozenSightEffect = player.getEffect(TOEffects.FROZEN_SIGHT_EFFECT);
         if (frozenSightEffect != null) {
            event.setCanceled(true);
            player.displayClientMessage(Component.literal("Wait till it ends!").withStyle(ChatFormatting.RED), true);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_STAND_HIT, SoundSource.PLAYERS, 0.5F, 1.0F);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerTickInFrozenSight(Pre event) {
      Player player = event.getEntity();
      MobEffectInstance effectInstance = player.getEffect(TOEffects.FROZEN_SIGHT_EFFECT);
      if (effectInstance != null) {
         player.setOnGround(true);
         player.setJumping(false);
      }
   }
}
