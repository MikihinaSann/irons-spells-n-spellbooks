package com.gametechbc.traveloptics.data_manager;

import com.gametechbc.traveloptics.api.TODataComponents;
import com.gametechbc.traveloptics.api.particle.ParticleDirection;
import com.gametechbc.traveloptics.api.particle.SphereParticleManager;
import com.github.L_Ender.cataclysm.client.particle.Options.LightningParticleOptions;
import com.github.L_Ender.cataclysm.init.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class PlasmaCoreManager {
   private static final String PLASMA_CORE_KEY = "PlasmaCore";
   private static final int MAX_PLASMA_POINTS = 250;

   public PlasmaCoreManager() {
      super();
   }

   public static int getPlasmaCore(ItemStack stack) {
      return stack.has(TODataComponents.PLASMA_CORE_COMPONENT) ? ((CompoundTag)stack.get(TODataComponents.PLASMA_CORE_COMPONENT)).getInt("PlasmaCore") : 0;
   }

   public static void setPlasmaCore(ItemStack stack, int points) {
      int cappedPoints = Math.min(points, 250);
      CompoundTag nbt = new CompoundTag();
      nbt.putInt("PlasmaCore", cappedPoints);
      stack.set(TODataComponents.PLASMA_CORE_COMPONENT, nbt);
   }

   public static void addPlasmaCore(ItemStack stack, Player player, int amount) {
      int currentPoints = getPlasmaCore(stack);
      int newPoints = Math.min(currentPoints + amount, 250);
      setPlasmaCore(stack, newPoints);
      displayActionBar(player, newPoints);
      playSoundOnThreshold(player, currentPoints, newPoints);
      spawnParticlesOnThreshold(player, currentPoints, newPoints);
   }

   private static void displayActionBar(Player player, int points) {
      if (player instanceof ServerPlayer) {
         Component message = points >= 250
            ? Component.literal("⚡ Plasma Core: " + points).withStyle(style -> style.withColor(15219515)).withStyle(ChatFormatting.BOLD)
            : Component.literal("⚡ Plasma Core: " + points).withStyle(style -> style.withColor(16476957));
         player.displayClientMessage(message, true);
      }
   }

   private static void playSoundOnThreshold(Player player, int oldPoints, int newPoints) {
      if (!player.level().isClientSide()) {
         if (newPoints >= 250 && oldPoints < 250) {
            player.level().playSound(null, player.blockPosition(), (SoundEvent)ModSounds.HARBINGER_CHARGE_PREPARE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
         } else if (newPoints >= 200 && oldPoints < 200) {
            player.level().playSound(null, player.blockPosition(), (SoundEvent)ModSounds.HARBINGER_STUN.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
         } else if (newPoints >= 100 && oldPoints < 100) {
            player.level().playSound(null, player.blockPosition(), (SoundEvent)ModSounds.HARBINGER_STUN.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
         }
      }
   }

   private static void spawnParticlesOnThreshold(Player player, int oldPoints, int newPoints) {
      if (newPoints >= 250 && oldPoints < 250) {
         Vec3 playerPosition = player.position();
         SphereParticleManager.spawnParticles(player.level(), player, 25, new LightningParticleOptions(102, 26, 204), ParticleDirection.OUTWARD, 4.0);
      }
   }
}
