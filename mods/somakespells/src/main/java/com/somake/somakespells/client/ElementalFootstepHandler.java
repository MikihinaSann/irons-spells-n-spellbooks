package com.somake.somakespells.client;

import com.somake.somakespells.compat.BornInChaosCompat;
import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.compat.mowzie.MowzieFootprintCompat;
import com.somake.somakespells.registries.ModMobEffects;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

public final class ElementalFootstepHandler {
   private static final ResourceLocation IRONS_CHARGE_EFFECT_ID = ResourceLocation.parse("irons_spellbooks:charge");
   private static final ResourceLocation IRONS_CHARGED_EFFECT_ID = ResourceLocation.parse("irons_spellbooks:charged");
   private static final boolean MOWZIE_LOADED = ModList.get().isLoaded("mowziesmobs");
   private static int footstepDelay;
   private static int footstepCounter;

   private ElementalFootstepHandler() {
   }

   public static void tickLocalPlayer() {
      if (!MOWZIE_LOADED) {
         footstepDelay = 0;
      } else {
         Minecraft minecraft = Minecraft.getInstance();
         Player player = minecraft.player;
         if (player != null && !minecraft.isPaused()) {
            ElementalFootstepTheme theme = getActiveTheme(player);
            if (theme == null) {
               footstepDelay = 0;
            } else if (player.onGround() && !player.isSwimming() && !player.isPassenger() && !(player.getDeltaMovement().horizontalDistanceSqr() <= 0.01)) {
               if (--footstepDelay <= 0) {
                  footstepDelay = player.isSprinting() ? 4 : 6;
                  footstepCounter++;
                  boolean leftFoot = footstepCounter % 2 == 0;
                  double rotation = Math.toRadians(player.yBodyRot + 180.0F);
                  MowzieFootprintCompat.spawnFootprint(player, rotation, leftFoot, theme.red(), theme.green(), theme.blue());
               }
            }
         }
      }
   }

   private static ElementalFootstepTheme getActiveTheme(Player player) {
      if (hasModifiedChargeEffect(player)) {
         return ElementalFootstepTheme.LIGHTNING;
      } else if (player.hasEffect(ModMobEffects.COMBUSTIONED)) {
         return ElementalFootstepTheme.FIRE;
      } else if (player.hasEffect(ModMobEffects.SOUL_COMBUSTIONED)) {
         return ElementalFootstepTheme.SOUL_FIRE;
      } else if (BornInChaosCompat.isLoaded() && player.hasEffect(ModMobEffects.INFERNAL_COMBUSTIONED)) {
         return ElementalFootstepTheme.INFERNAL_FIRE;
      } else if (player.hasEffect(ModMobEffects.EARTHBOUND)) {
         return ElementalFootstepTheme.GEO;
      } else if (player.hasEffect(ModMobEffects.SUBMERGED)) {
         return ElementalFootstepTheme.AQUA;
      } else if (player.hasEffect(ModMobEffects.OVERGROWN)) {
         return ElementalFootstepTheme.NATURE;
      } else if (player.hasEffect(ModMobEffects.PERMAFROST)) {
         return ElementalFootstepTheme.ICE;
      } else if (player.hasEffect(ModMobEffects.ENDER_CORRUPTION)) {
         return ElementalFootstepTheme.ENDER;
      } else if (player.hasEffect(ModMobEffects.BLOOD_RUSH)) {
         return ElementalFootstepTheme.BLOOD;
      } else if (player.hasEffect(ModMobEffects.BLESSED)) {
         return ElementalFootstepTheme.HOLY;
      } else if (MagicFromTheEastCompat.isLoaded() && ModMobEffects.SPIRIT_EMPOWERMENT != null && player.hasEffect(ModMobEffects.SPIRIT_EMPOWERMENT)) {
         return ElementalFootstepTheme.SPIRIT;
      } else if (MagicFromTheEastCompat.isLoaded() && ModMobEffects.SYMMETRY_EMPOWERMENT != null && player.hasEffect(ModMobEffects.SYMMETRY_EMPOWERMENT)) {
         return ElementalFootstepTheme.SYMMETRY;
      } else if (player.hasEffect(ModMobEffects.EVOCATION_FORTITUDE)) {
         return ElementalFootstepTheme.EVOCATION;
      } else if (player.hasEffect(ModMobEffects.REVERBERATION)) {
         return ElementalFootstepTheme.SOUND;
      } else {
         return player.hasEffect(ModMobEffects.ELDRITCH_GAMBIT) ? ElementalFootstepTheme.ELDRITCH : null;
      }
   }

   private static boolean hasModifiedChargeEffect(Player player) {
      return hasEffect(player, IRONS_CHARGE_EFFECT_ID) || hasEffect(player, IRONS_CHARGED_EFFECT_ID);
   }

   private static boolean hasEffect(Player player, ResourceLocation effectId) {
      Optional<Reference<MobEffect>> effectHolder = BuiltInRegistries.MOB_EFFECT.getHolder(effectId);
      return effectHolder.isPresent() && player.hasEffect((Holder)effectHolder.get());
   }
}
