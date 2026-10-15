package com.somake.somakespells.event;

import com.somake.somakespells.compat.LegendaryMonstersCompat;
import com.somake.somakespells.entity.RedEchoEntity;
import com.somake.somakespells.registries.ModMobEffects;
import com.somake.somakespells.util.RedSoulDurationHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.raid.Raider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(
   modid = "somakespells",
   bus = Bus.GAME
)
public final class RedSoulCombatHandler {
   private static final int NORMAL_KILL_TICKS = 40;
   private static final int ELITE_KILL_TICKS = 100;
   private static final int BOSS_KILL_TICKS = 200;

   private RedSoulCombatHandler() {
   }

   @SubscribeEvent
   public static void onLivingDeath(LivingDeathEvent event) {
      if (LegendaryMonstersCompat.isLoaded() && event.getSource().getEntity() instanceof ServerPlayer player && player.hasEffect(ModMobEffects.RED_SOUL)) {
         LivingEntity victim = event.getEntity();
         if (victim != player && !(victim instanceof RedEchoEntity) && victim instanceof Enemy) {
            int bonusTicks = getKillBonus(victim);
            if (RedSoulDurationHelper.addTime(player, bonusTicks)) {
               player.displayClientMessage(
                  Component.literal("[Red Soul] ")
                     .withStyle(new ChatFormatting[]{ChatFormatting.DARK_RED, ChatFormatting.BOLD})
                     .append(Component.translatable("message.somakespells.red_soul.time_gained", new Object[]{bonusTicks / 20}).withStyle(ChatFormatting.RED)),
                  true
               );
            }
         }
      }
   }

   private static int getKillBonus(LivingEntity victim) {
      if (victim instanceof EnderDragon || victim instanceof WitherBoss || victim.getMaxHealth() >= 200.0F) {
         return 200;
      } else {
         return !(victim instanceof Raider) && !victim.getType().is(EntityTypeTags.RAIDERS) && !(victim.getMaxHealth() >= 50.0F) ? 40 : 100;
      }
   }
}
