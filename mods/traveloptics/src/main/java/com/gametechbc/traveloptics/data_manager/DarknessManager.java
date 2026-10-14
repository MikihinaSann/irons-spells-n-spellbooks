package com.gametechbc.traveloptics.data_manager;

import com.gametechbc.traveloptics.api.particle.ParticleDirection;
import com.gametechbc.traveloptics.api.particle.SphereParticleManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class DarknessManager {
   public static final int MAX_DARKNESS_POINTS = 300;
   private static final String DARKNESS_TAG = "TODarkness";

   public DarknessManager() {
      super();
   }

   public static int getDarkness(ItemStack stack) {
      return stack.has(DataComponents.CUSTOM_DATA) ? ((CustomData)stack.get(DataComponents.CUSTOM_DATA)).copyTag().getInt("TODarkness") : 0;
   }

   public static void setDarkness(ItemStack stack, int points) {
      int cappedPoints = Math.min(points, 300);
      CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt("TODarkness", cappedPoints));
   }

   public static void addDarkness(ItemStack stack, Player player, int amount) {
      int currentPoints = getDarkness(stack);
      int newPoints = Math.min(currentPoints + amount, 300);
      setDarkness(stack, newPoints);
      playSoundOnThreshold(player, currentPoints, newPoints);
      spawnParticlesOnThreshold(player, currentPoints, newPoints);
   }

   public static void displayActionBar(Player player, int points) {
      if (player instanceof ServerPlayer) {
         int percentage = (int)(points / 300.0 * 100.0);
         Component message = Component.literal("⚡ Plasma Fuel: " + percentage + "%")
            .withStyle(style -> style.withColor(points >= 300 ? 15219515 : 16476957))
            .withStyle(ChatFormatting.BOLD);
         player.displayClientMessage(message, true);
      }
   }

   private static void playSoundOnThreshold(Player player, int oldPoints, int newPoints) {
      if (!player.level().isClientSide()) {
         if (newPoints >= 300 && oldPoints < 300) {
         }
      }
   }

   private static void spawnParticlesOnThreshold(Player player, int oldPoints, int newPoints) {
      if (newPoints >= 300 && oldPoints < 300) {
         SphereParticleManager.spawnParticles(player.level(), player, 25, ParticleTypes.SMOKE, ParticleDirection.OUTWARD, 4.0);
      }
   }
}
