package net.alshanex.familiarslib.event;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.alshanex.familiarslib.FamiliarsLib;
import net.alshanex.familiarslib.data.PlayerFamiliarData;
import net.alshanex.familiarslib.entity.AbstractSpellCastingPet;
import net.alshanex.familiarslib.registry.AttachmentRegistry;
import net.alshanex.familiarslib.util.familiars.FamiliarManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;

@EventBusSubscriber(
   modid = "familiarslib"
)
public class PlayerLoginHandler {
   @SubscribeEvent
   public static void onPlayerClone(Clone event) {
      if (!event.getEntity().level().isClientSide()) {
         if (event.getEntity() instanceof ServerPlayer newPlayer && event.getOriginal() instanceof ServerPlayer oldPlayer) {
            FamiliarsLib.LOGGER.debug("Player {} cloned (death: {}), copying familiar and bed link data", newPlayer.getName().getString(), event.isWasDeath());
            PlayerFamiliarData oldFamiliarData = (PlayerFamiliarData)oldPlayer.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
            PlayerFamiliarData newFamiliarData = (PlayerFamiliarData)newPlayer.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);

            try {
               CompoundTag oldFamiliarNBT = oldFamiliarData.serializeNBT(oldPlayer.registryAccess());
               newFamiliarData.deserializeNBT(newPlayer.registryAccess(), oldFamiliarNBT);
            } catch (Exception var6) {
               FamiliarsLib.LOGGER.error("Error copying player data during clone: ", var6);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerDeath(LivingDeathEvent event) {
      if (!event.getEntity().level().isClientSide()) {
         if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerFamiliarData familiarData = (PlayerFamiliarData)serverPlayer.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
            FamiliarsLib.LOGGER.debug("Player {} died, desummoning all familiars", serverPlayer.getName().getString());
            ServerLevel level = serverPlayer.serverLevel();
            Map<UUID, CompoundTag> familiars = familiarData.getAllFamiliars();
            Set<UUID> familiarUUIDs = familiars.keySet();
            familiarData.clearAllSummoned();

            for (UUID familiarUUID : familiarUUIDs) {
               Entity existingEntity = level.getEntity(familiarUUID);
               boolean familiarExistsInWorld = existingEntity instanceof AbstractSpellCastingPet;
               if (familiarExistsInWorld) {
                  if (existingEntity instanceof AbstractSpellCastingPet familiar) {
                     if (familiar.isCasting()) {
                        familiar.cancelCast();
                        familiar.getMagicData().resetCastingState();
                     }

                     familiar.setTarget(null);
                     familiar.setHasUsedSingleAttack(false);
                     CompoundTag updatedNBT = FamiliarManager.createFamiliarNBT(familiar);
                     familiarData.addTamedFamiliar(familiarUUID, updatedNBT);
                     familiar.remove(RemovalReason.DISCARDED);
                     level.playSound(null, familiar.getX(), familiar.getY(), familiar.getZ(), SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
                     FamiliarsLib.LOGGER.debug("Familiar desummoned due to player death: {}", familiarUUID);
                  } else {
                     FamiliarsLib.LOGGER.debug("Familiar not found in world: {}", familiarUUID);
                  }
               }
            }

            FamiliarManager.syncFamiliarDataForPlayer(serverPlayer);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
      if (!event.getEntity().level().isClientSide()) {
         if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerFamiliarData familiarData = (PlayerFamiliarData)serverPlayer.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
            FamiliarsLib.LOGGER.debug("Player {} changed dimension, syncing familiar and bed link data", serverPlayer.getName().getString());
            if (!serverPlayer.level.isClientSide) {
               ServerLevel originalLevel = serverPlayer.server.getLevel(event.getFrom());
               Map<UUID, CompoundTag> familiars = familiarData.getAllFamiliars();
               Set<UUID> familiarUUIDS = familiars.keySet();
               familiarData.clearAllSummoned();

               for (UUID familiarUUID : familiarUUIDS) {
                  Entity existingEntity = originalLevel.getEntity(familiarUUID);
                  boolean familiarExistsInWorld = existingEntity instanceof AbstractSpellCastingPet;
                  if (familiarExistsInWorld) {
                     if (existingEntity instanceof AbstractSpellCastingPet familiar) {
                        if (familiar.isCasting()) {
                           familiar.cancelCast();
                           familiar.getMagicData().resetCastingState();
                        }

                        familiar.setTarget(null);
                        familiar.setHasUsedSingleAttack(false);
                        CompoundTag updatedNBT = FamiliarManager.createFamiliarNBT(familiar);
                        familiarData.addTamedFamiliar(familiarUUID, updatedNBT);
                        familiar.remove(RemovalReason.DISCARDED);
                        originalLevel.playSound(
                           null, familiar.getX(), familiar.getY(), familiar.getZ(), SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F
                        );
                        FamiliarsLib.LOGGER.debug("Specific familiar desummoned successfully: {}", familiarUUID);
                     } else {
                        FamiliarsLib.LOGGER.debug("Familiar not found in world: {}", familiarUUID);
                     }
                  }
               }

               FamiliarManager.syncFamiliarDataForPlayer(serverPlayer);
            }
         }
      }
   }
}
