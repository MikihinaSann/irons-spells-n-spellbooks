package com.gametechbc.traveloptics.data_manager;

import com.gametechbc.traveloptics.api.TODataComponents;
import com.gametechbc.traveloptics.api.particle.ParticleDirection;
import com.gametechbc.traveloptics.api.particle.SphereParticleManager;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.github.L_Ender.cataclysm.init.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PhantomRageManager {
   private static final String PHANTOM_RAGE_KEY = "PhantomRage";
   private static final int MAX_RAGE_POINTS = 350;

   public PhantomRageManager() {
      super();
   }

   public static int getPhantomRage(ItemStack stack) {
      return stack.has(TODataComponents.PHANTOM_RAGE_COMPONENT) ? ((CompoundTag)stack.get(TODataComponents.PHANTOM_RAGE_COMPONENT)).getInt("PhantomRage") : 0;
   }

   public static void setPhantomRage(ItemStack stack, int points) {
      int cappedPoints = Math.min(points, 350);
      CompoundTag nbt = new CompoundTag();
      nbt.putInt("PhantomRage", cappedPoints);
      stack.set(TODataComponents.PHANTOM_RAGE_COMPONENT, nbt);
   }

   public static void addPhantomRage(ItemStack stack, Player player, int amount) {
      int currentPoints = getPhantomRage(stack);
      int newPoints = Math.min(currentPoints + amount, 350);
      setPhantomRage(stack, newPoints);
      displayActionBar(player, newPoints);
      playSoundOnThreshold(player, currentPoints, newPoints);
      spawnParticlesOnThreshold(player, currentPoints, newPoints);
   }

   private static void displayActionBar(Player player, int points) {
      if (player instanceof ServerPlayer) {
         Component message = points >= 350
            ? Component.literal("\ud83d\udca2 Phantom Rage: " + points).withStyle(style -> style.withColor(16770714)).withStyle(ChatFormatting.BOLD)
            : Component.literal("\ud83d\udca2 Phantom Rage: " + points).withStyle(style -> style.withColor(7733218));
         player.displayClientMessage(message, true);
      }
   }

   private static void playSoundOnThreshold(Player player, int oldPoints, int newPoints) {
      if (!player.level().isClientSide()) {
         if (newPoints >= 350 && oldPoints < 350) {
            player.level().playSound(null, player.blockPosition(), (SoundEvent)ModSounds.MALEDICTUS_BATTLE_CRY.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
         } else if (newPoints >= 230 && oldPoints < 230) {
            player.level().playSound(null, player.blockPosition(), (SoundEvent)ModSounds.MALEDICTUS_HURT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
         } else if (newPoints >= 115 && oldPoints < 115) {
            player.level().playSound(null, player.blockPosition(), (SoundEvent)ModSounds.MALEDICTUS_HURT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
         }
      }
   }

   private static void spawnParticlesOnThreshold(Player player, int oldPoints, int newPoints) {
      if (newPoints >= 350 && oldPoints < 350) {
         SphereParticleManager.spawnParticles(player.level(), player, 50, (ParticleOptions)ModParticle.PHANTOM_WING_FLAME.get(), ParticleDirection.OUTWARD, 4.0);
      }
   }
}
