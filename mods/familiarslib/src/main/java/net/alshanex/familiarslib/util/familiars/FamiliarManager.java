package net.alshanex.familiarslib.util.familiars;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.alshanex.familiarslib.FamiliarsLib;
import net.alshanex.familiarslib.block.entity.AbstractFamiliarStorageBlockEntity;
import net.alshanex.familiarslib.data.PlayerFamiliarData;
import net.alshanex.familiarslib.entity.AbstractSpellCastingPet;
import net.alshanex.familiarslib.item.AbstractMultiSelectionCurio;
import net.alshanex.familiarslib.network.FamiliarDataPacket;
import net.alshanex.familiarslib.network.FamiliarDeathPacket;
import net.alshanex.familiarslib.network.OpenFamiliarSelectionPacket;
import net.alshanex.familiarslib.network.OpenMultiSelectionScreenPacket;
import net.alshanex.familiarslib.network.ReloadFamiliarScreenPacket;
import net.alshanex.familiarslib.network.SyncFamiliarDataPacket;
import net.alshanex.familiarslib.network.UpdateFamiliarStoragePacket;
import net.alshanex.familiarslib.registry.AttachmentRegistry;
import net.alshanex.familiarslib.screen.FamiliarSelectionScreen;
import net.alshanex.familiarslib.screen.FamiliarStorageScreen;
import net.alshanex.familiarslib.screen.FamiliarWanderScreen;
import net.alshanex.familiarslib.screen.MultiSelectionCurioScreen;
import net.alshanex.familiarslib.util.CurioUtils;
import net.alshanex.familiarslib.util.consumables.FamiliarConsumableIntegration;
import net.alshanex.familiarslib.util.consumables.FamiliarConsumableSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

public class FamiliarManager {
   private static final Set<UUID> deadFamiliars = new HashSet<>();

   public static void markFamiliarAsDead(UUID familiarId) {
      deadFamiliars.add(familiarId);
      FamiliarsLib.LOGGER.debug("Marked familiar {} as dead", familiarId);
   }

   public static boolean isFamiliarDead(UUID familiarId) {
      return deadFamiliars.contains(familiarId);
   }

   public static void unmarkFamiliarAsDead(UUID familiarId) {
      deadFamiliars.remove(familiarId);
      FamiliarsLib.LOGGER.debug("Unmarked familiar {} as dead", familiarId);
   }

   public static boolean handleFamiliarTaming(AbstractSpellCastingPet familiar, ServerPlayer player) {
      PlayerFamiliarData familiarData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
      if (!familiarData.canTameMoreFamiliars()) {
         FamiliarsLib.LOGGER
            .debug(
               "Player {} tried to tame familiar but is at max capacity ({}/{})",
               new Object[]{player.getName().getString(), familiarData.getFamiliarCount(), 10}
            );
         return false;
      } else {
         CompoundTag familiarNBT = createFamiliarNBT(familiar);
         UUID familiarId = familiar.getUUID();
         boolean success = familiarData.tryAddTamedFamiliar(familiarId, familiarNBT);
         if (success) {
            if (familiarData.getSelectedFamiliarId() == null) {
               familiarData.setSelectedFamiliarId(familiarId);
            }

            syncFamiliarData(player, familiarData);
            FamiliarsLib.LOGGER
               .debug(
                  "Player {} successfully tamed familiar {}. ({}/{})",
                  new Object[]{player.getName().getString(), familiarId, familiarData.getFamiliarCount(), 10}
               );
            return true;
         } else {
            FamiliarsLib.LOGGER.error("Failed to add familiar to player data despite limit check");
            return false;
         }
      }
   }

   public static boolean canPlayerTameMoreFamiliars(ServerPlayer player) {
      PlayerFamiliarData familiarData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
      return familiarData.canTameMoreFamiliars();
   }

   public static String getFamiliarCapacityInfo(ServerPlayer player) {
      PlayerFamiliarData familiarData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
      return familiarData.getFamiliarCount() + "/10";
   }

   public static void handleFamiliarSummoning(ServerPlayer player) {
      PlayerFamiliarData familiarData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
      UUID selectedFamiliarId = familiarData.getSelectedFamiliarId();
      FamiliarsLib.LOGGER.debug("Handling familiar summoning for player: {}", player.getName().getString());
      FamiliarsLib.LOGGER.debug("Selected familiar ID: {}", selectedFamiliarId);
      if (selectedFamiliarId == null) {
         FamiliarsLib.LOGGER.debug("No familiar selected, aborting summoning");
      } else {
         ServerLevel level = player.serverLevel();
         Entity existingEntity = level.getEntity(selectedFamiliarId);
         boolean familiarExistsInWorld = existingEntity instanceof AbstractSpellCastingPet;
         FamiliarsLib.LOGGER.debug("Familiar exists in world: {}", familiarExistsInWorld);
         if (familiarExistsInWorld) {
            FamiliarsLib.LOGGER.debug("Dessummoning familiar: {}", selectedFamiliarId);
            desummonFamiliar(player, selectedFamiliarId);
         } else {
            FamiliarsLib.LOGGER.debug("Summoning familiar: {}", selectedFamiliarId);
            summonFamiliar(player, selectedFamiliarId);
         }
      }
   }

   public static void summonFamiliar(ServerPlayer player, UUID familiarId) {
      PlayerFamiliarData familiarData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
      CompoundTag familiarNBT = familiarData.getFamiliarData(familiarId);
      FamiliarsLib.LOGGER.debug("Attempting to summon familiar: {}", familiarId);
      if (familiarNBT == null) {
         FamiliarsLib.LOGGER.debug("No NBT data found for familiar: {}", familiarId);
      } else {
         ServerLevel level = player.serverLevel();
         String entityTypeString = familiarNBT.getString("id");
         EntityType<?> entityType = (EntityType<?>)EntityType.byString(entityTypeString).orElse(null);
         if (entityType == null) {
            FamiliarsLib.LOGGER.debug("Unknown entity type: {}", entityTypeString);
         } else {
            Entity entity = entityType.create(level);
            if (entity instanceof AbstractSpellCastingPet familiar) {
               familiar.load(familiarNBT);
               familiar.setUUID(familiarId);
               applyHealthFromNBT(familiar, familiarNBT);
               Vec3 spawnPos = findSafeSpawnPosition(player, level);
               familiar.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
               familiar.setYRot(player.getYRot());
               familiar.setOldPosAndRot();
               level.addFreshEntity(familiar);
               FamiliarAttributesHelper.handleFamiliarSummoned(player, familiar);
               level.playSound(null, familiar.getX(), familiar.getY(), familiar.getZ(), SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 1.0F, 1.0F);
               familiarData.setCurrentSummonedFamiliarId(familiarId);
               familiarData.addSummonedFamiliar(familiarId);
               syncFamiliarData(player, familiarData);
            } else {
               FamiliarsLib.LOGGER.debug("Entity is not a familiar: {}", entity);
            }
         }
      }
   }

   public static void desummonFamiliar(ServerPlayer player, UUID familiarId) {
      PlayerFamiliarData familiarData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
      ServerLevel level = player.serverLevel();
      FamiliarsLib.LOGGER.debug("Attempting to desummon familiar: {}", familiarId);
      if (level.getEntity(familiarId) instanceof AbstractSpellCastingPet familiar) {
         if (familiar.getSummoner() != null && familiar.getSummoner().is(player) && familiar.getUUID().equals(familiarId)) {
            if (familiar.isCasting()) {
               familiar.cancelCast();
               familiar.getMagicData().resetCastingState();
            }

            familiar.setTarget(null);
            familiar.setSitting(false);
            familiar.setHasUsedSingleAttack(false);
            CompoundTag updatedNBT = createFamiliarNBT(familiar);
            familiarData.addTamedFamiliar(familiarId, updatedNBT);
            familiar.remove(RemovalReason.DISCARDED);
            FamiliarAttributesHelper.handleFamiliarDismissed(player, familiar);
            level.playSound(null, familiar.getX(), familiar.getY(), familiar.getZ(), SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
            FamiliarsLib.LOGGER.debug("Familiar desummoned successfully: {}", familiarId);
            if (familiarId.equals(familiarData.getCurrentSummonedFamiliarId())) {
               familiarData.setCurrentSummonedFamiliarId(null);
            }

            familiarData.removeSummonedFamiliar(familiarId);
         } else {
            FamiliarsLib.LOGGER.debug("Familiar found but doesn't belong to player: {}", familiarId);
         }
      } else {
         FamiliarsLib.LOGGER.debug("Familiar not found in world: {}", familiarId);
         if (familiarId.equals(familiarData.getCurrentSummonedFamiliarId())) {
            familiarData.setCurrentSummonedFamiliarId(null);
         }

         familiarData.removeSummonedFamiliar(familiarId);
      }

      syncFamiliarData(player, familiarData);
   }

   public static void updateFamiliarData(AbstractSpellCastingPet familiar) {
      if (familiar.getSummoner() instanceof ServerPlayer player) {
         PlayerFamiliarData familiarData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
         UUID familiarId = familiar.getUUID();
         if (isFamiliarDead(familiarId)) {
            FamiliarsLib.LOGGER.debug("Skipping update for marked dead familiar {}", familiarId);
            return;
         }

         if (familiar.getHealth() <= 0.0F) {
            FamiliarsLib.LOGGER.debug("Skipping update for dead/dying familiar {}", familiarId);
            return;
         }

         if (!familiarData.hasFamiliar(familiarId)) {
            FamiliarsLib.LOGGER.debug("Skipping update for familiar {} - not in player data (probably dead)", familiarId);
            return;
         }

         try {
            CompoundTag updatedNBT = createFamiliarNBT(familiar);
            familiarData.addTamedFamiliar(familiarId, updatedNBT);
         } catch (Exception var5) {
            FamiliarsLib.LOGGER.error("Error updating familiar data for {}: ", familiarId, var5);
         }
      }
   }

   public static CompoundTag createFamiliarNBT(AbstractSpellCastingPet familiar) {
      CompoundTag nbt = new CompoundTag();
      familiar.saveWithoutId(nbt);
      float currentHealth = familiar.getHealth();
      nbt.putFloat("currentHealth", currentHealth);
      nbt.putFloat("baseMaxHealth", familiar.getBaseMaxHealth());
      String entityTypeId = EntityType.getKey(familiar.getType()).toString();
      nbt.putString("id", entityTypeId);
      if (familiar.hasCustomName()) {
         nbt.putString("customName", familiar.getCustomName().getString());
      }

      FamiliarConsumableIntegration.saveConsumableData(familiar, nbt);
      FamiliarConsumableSystem.ConsumableData data = FamiliarConsumableIntegration.getConsumableData(familiar);
      FamiliarsLib.LOGGER
         .debug(
            "Saving familiar {}: current health={}, max health={}, base max health={}, consumable data = {}",
            new Object[]{familiar.getUUID(), currentHealth, familiar.getMaxHealth(), familiar.getBaseMaxHealth(), data.toString()}
         );
      return nbt;
   }

   private static void applyHealthFromNBT(AbstractSpellCastingPet familiar, CompoundTag familiarNBT) {
      float savedHealth = familiarNBT.getFloat("currentHealth");
      FamiliarsLib.LOGGER.debug("Loading health for familiar {}: saved health = {}", familiar.getUUID(), savedHealth);
      FamiliarConsumableIntegration.loadConsumableData(familiar, familiarNBT);
      if (!familiar.level().isClientSide) {
         FamiliarConsumableIntegration.applyConsumableModifiers(familiar);
         float newMaxHealth = familiar.getMaxHealth();
         float restoredHealth = Math.min(savedHealth, newMaxHealth);
         FamiliarsLib.LOGGER
            .debug(
               "Applying health for summoned familiar {}: saved={}, newMax={}, restored={}",
               new Object[]{familiar.getUUID(), savedHealth, newMaxHealth, restoredHealth}
            );
         familiar.setHealth(restoredHealth);
      }

      FamiliarsLib.LOGGER
         .debug(
            "Applied health to familiar {}: {}/{} ({}%)",
            new Object[]{familiar.getUUID(), familiar.getHealth(), familiar.getMaxHealth(), familiar.getHealth() / familiar.getMaxHealth() * 100.0F}
         );
   }

   private static Vec3 findSafeSpawnPosition(ServerPlayer player, ServerLevel level) {
      Vec3 playerPos = player.getEyePosition();
      double[][] offsets = new double[][]{
         {3.0, 0.0},
         {-3.0, 0.0},
         {0.0, 3.0},
         {0.0, -3.0},
         {2.1, 2.1},
         {-2.1, 2.1},
         {2.1, -2.1},
         {-2.1, -2.1},
         {4.0, 0.0},
         {-4.0, 0.0},
         {0.0, 4.0},
         {0.0, -4.0},
         {1.5, 1.5},
         {-1.5, 1.5},
         {1.5, -1.5},
         {-1.5, -1.5}
      };
      float yrot = player.getYRot() * (float) (Math.PI / 180.0);

      for (double[] offset : offsets) {
         double cos = Math.cos(yrot);
         double sin = Math.sin(yrot);
         double worldX = offset[0] * cos - offset[1] * sin;
         double worldZ = offset[0] * sin + offset[1] * cos;
         Vec3 targetPos = playerPos.add(worldX, 0.0, worldZ);
         Vec3 safePos = findSafePositionNear(level, targetPos);
         if (safePos != null) {
            return safePos;
         }
      }

      for (int attempts = 0; attempts < 20; attempts++) {
         double randomAngle = level.random.nextDouble() * 2.0 * Math.PI;
         double distance = 2.0 + level.random.nextDouble() * 3.0;
         double x = playerPos.x + Math.cos(randomAngle) * distance;
         double z = playerPos.z + Math.sin(randomAngle) * distance;
         Vec3 randomPos = new Vec3(x, playerPos.y, z);
         Vec3 safePos = findSafePositionNear(level, randomPos);
         if (safePos != null) {
            return safePos;
         }
      }

      BlockPos playerBlock = BlockPos.containing(playerPos.add(1.0, 0.0, 0.0));
      return new Vec3(playerBlock.getX() + 0.5, playerPos.y, playerBlock.getZ() + 0.5);
   }

   private static Vec3 findSafePositionNear(ServerLevel level, Vec3 targetPos) {
      int baseY = (int)targetPos.y;
      int minY = Math.max(level.getMinBuildHeight(), baseY - 5);
      int maxY = Math.min(level.getMaxBuildHeight() - 2, baseY + 10);

      for (int y = baseY; y <= maxY; y++) {
         Vec3 testPos = new Vec3(targetPos.x, y, targetPos.z);
         if (isPositionSafeForSpawning(level, testPos)) {
            return centerOnBlock(testPos);
         }
      }

      for (int yx = baseY - 1; yx >= minY; yx--) {
         Vec3 testPos = new Vec3(targetPos.x, yx, targetPos.z);
         if (isPositionSafeForSpawning(level, testPos)) {
            return centerOnBlock(testPos);
         }
      }

      return null;
   }

   private static Vec3 centerOnBlock(Vec3 pos) {
      BlockPos blockPos = BlockPos.containing(pos);
      return new Vec3(blockPos.getX() + 0.5, pos.y, blockPos.getZ() + 0.5);
   }

   private static boolean isPositionSafeForSpawning(ServerLevel level, Vec3 pos) {
      BlockPos blockPos = BlockPos.containing(pos);
      BlockPos belowPos = blockPos.below();
      BlockPos abovePos = blockPos.above();
      if (level.isInWorldBounds(blockPos) && level.isInWorldBounds(abovePos)) {
         BlockState groundState = level.getBlockState(belowPos);
         BlockState spawnState = level.getBlockState(blockPos);
         BlockState headState = level.getBlockState(abovePos);
         if (!groundState.isSolid() || isDangerousBlock(level, belowPos, groundState)) {
            return false;
         } else if (!isSafeToOccupy(level, blockPos, spawnState)) {
            return false;
         } else if (!isSafeToOccupy(level, abovePos, headState)) {
            return false;
         } else {
            return isInLava(level, blockPos) || isInLava(level, abovePos) ? false : !isNearDangerousBlocks(level, blockPos);
         }
      } else {
         return false;
      }
   }

   private static boolean isSafeToOccupy(ServerLevel level, BlockPos pos, BlockState state) {
      if (state.isAir() || state.canBeReplaced()) {
         return true;
      } else {
         return !state.isSolid() && !state.blocksMotion() ? !isDangerousBlock(level, pos, state) : false;
      }
   }

   private static boolean isDangerousBlock(ServerLevel level, BlockPos pos, BlockState state) {
      if (state.getFluidState().is(FluidTags.LAVA)) {
         return true;
      } else if (state.is(BlockTags.FIRE)) {
         return true;
      } else if (state.is(BlockTags.CAMPFIRES)) {
         return true;
      } else {
         Block block = state.getBlock();
         if (block == Blocks.MAGMA_BLOCK) {
            return true;
         } else if (block == Blocks.CACTUS) {
            return true;
         } else if (block == Blocks.SWEET_BERRY_BUSH) {
            return true;
         } else {
            return block == Blocks.POWDER_SNOW ? true : block == Blocks.WITHER_ROSE;
         }
      }
   }

   private static boolean isInLava(ServerLevel level, BlockPos pos) {
      return level.getFluidState(pos).is(FluidTags.LAVA);
   }

   private static boolean isNearDangerousBlocks(ServerLevel level, BlockPos centerPos) {
      for (int dx = -1; dx <= 1; dx++) {
         for (int dy = -1; dy <= 1; dy++) {
            for (int dz = -1; dz <= 1; dz++) {
               if (dx != 0 || dy != 0 || dz != 0) {
                  BlockPos checkPos = centerPos.offset(dx, dy, dz);
                  BlockState state = level.getBlockState(checkPos);
                  if (isInLava(level, checkPos) || state.getBlock() == Blocks.FIRE || state.getBlock() == Blocks.SOUL_FIRE) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public static void syncFamiliarData(ServerPlayer player, PlayerFamiliarData familiarData) {
      try {
         Map<UUID, CompoundTag> familiarsData = familiarData.getAllFamiliars();
         UUID selectedId = familiarData.getSelectedFamiliarId();
         UUID summonedId = familiarData.getCurrentSummonedFamiliarId();
         Set<UUID> summonedIds = familiarData.getSummonedFamiliarIds();
         FamiliarsLib.LOGGER
            .debug(
               "Syncing familiar data - Familiars: {}, Selected: {}, Summoned: {}, All Summoned: {}",
               new Object[]{familiarsData.size(), selectedId, summonedId, summonedIds.size()}
            );
         PacketDistributor.sendToPlayer(player, new FamiliarDataPacket(familiarsData, selectedId, summonedId, summonedIds), new CustomPacketPayload[0]);
         CompoundTag syncData = familiarData.serializeNBT(player.registryAccess());
         PacketDistributor.sendToPlayer(player, new SyncFamiliarDataPacket(syncData), new CustomPacketPayload[0]);
         FamiliarsLib.LOGGER.debug("All data synced to client successfully");
      } catch (Exception var7) {
         FamiliarsLib.LOGGER.error("Error syncing familiar data: ", var7);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void handleFamiliarDataPacket(
      Map<UUID, CompoundTag> familiars, UUID selectedFamiliarId, UUID currentSummonedFamiliarId, Set<UUID> summonedFamiliarIds
   ) {
      Player player = Minecraft.getInstance().player;
      if (player != null) {
         PlayerFamiliarData data = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
         Map<UUID, CompoundTag> currentFamiliars = data.getAllFamiliars();

         for (UUID id : new HashSet<>(currentFamiliars.keySet())) {
            data.removeTamedFamiliar(id);
         }

         data.clearAllSummoned();

         for (Entry<UUID, CompoundTag> entry : familiars.entrySet()) {
            data.addTamedFamiliar(entry.getKey(), entry.getValue());
         }

         data.setSelectedFamiliarId(selectedFamiliarId);
         data.setCurrentSummonedFamiliarId(currentSummonedFamiliarId);
         if (summonedFamiliarIds != null) {
            for (UUID summonedId : summonedFamiliarIds) {
               data.addSummonedFamiliar(summonedId);
            }
         }

         FamiliarsLib.LOGGER
            .debug(
               "Client received familiar data - Count: {}, Selected: {}, Summoned: {}, All Summoned: {}",
               new Object[]{familiars.size(), selectedFamiliarId, currentSummonedFamiliarId, summonedFamiliarIds != null ? summonedFamiliarIds.size() : 0}
            );
      }
   }

   public static boolean hasSelectedFamiliar(ServerPlayer player) {
      PlayerFamiliarData familiarData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
      return familiarData.getSelectedFamiliarId() != null;
   }

   public static boolean isFamiliarSummoned(ServerPlayer player) {
      PlayerFamiliarData familiarData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
      return familiarData.getCurrentSummonedFamiliarId() != null;
   }

   public static void syncFamiliarDataForPlayer(ServerPlayer player) {
      PlayerFamiliarData familiarData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
      syncFamiliarData(player, familiarData);
   }

   public static void handleFamiliarSelection(ServerPlayer player, UUID familiarId) {
      PlayerFamiliarData familiarData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
      if (familiarData.hasFamiliar(familiarId)) {
         familiarData.setSelectedFamiliarId(familiarId);
         syncFamiliarData(player, familiarData);
      }
   }

   public static void summonSpecificFamiliarAtPosition(ServerPlayer player, UUID familiarId, int positionIndex) {
      PlayerFamiliarData familiarData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
      CompoundTag familiarNBT = familiarData.getFamiliarData(familiarId);
      FamiliarsLib.LOGGER.debug("Attempting to summon specific familiar: {} at position {}", familiarId, positionIndex);
      if (familiarNBT == null) {
         FamiliarsLib.LOGGER.debug("No NBT data found for familiar: {}", familiarId);
      } else if (familiarData.isFamiliarSummoned(familiarId)) {
         FamiliarsLib.LOGGER.debug("Familiar {} is already summoned", familiarId);
      } else {
         String entityTypeString = familiarNBT.getString("id");
         EntityType<?> entityType = (EntityType<?>)EntityType.byString(entityTypeString).orElse(null);
         if (entityType == null) {
            FamiliarsLib.LOGGER.debug("Unknown entity type: {}", entityTypeString);
         } else {
            ServerLevel level = player.serverLevel();
            Entity entity = entityType.create(level);
            if (entity instanceof AbstractSpellCastingPet familiar) {
               familiar.load(familiarNBT);
               familiar.setUUID(familiarId);
               applyHealthFromNBT(familiar, familiarNBT);
               Vec3 spawnPos = findSafeSpawnPositionWithIndex(player, level, positionIndex);
               familiar.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
               familiar.setYRot(player.getYRot());
               familiar.setOldPosAndRot();
               level.playSound(null, familiar.getX(), familiar.getY(), familiar.getZ(), SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 1.0F, 1.0F);
               level.addFreshEntity(familiar);
               FamiliarAttributesHelper.handleFamiliarSummoned(player, familiar);
               familiarData.addSummonedFamiliar(familiarId);
               syncFamiliarData(player, familiarData);
            } else {
               FamiliarsLib.LOGGER.debug("Entity is not a familiar: {}", entity);
            }
         }
      }
   }

   private static Vec3 findSafeSpawnPositionWithIndex(ServerPlayer player, ServerLevel level, int positionIndex) {
      double[][] spawnOffsets = new double[][]{
         {3.0, 0.0}, {-3.0, 0.0}, {0.0, 3.0}, {0.0, -3.0}, {2.1, 2.1}, {-2.1, 2.1}, {2.1, -2.1}, {-2.1, -2.1}, {4.0, 0.0}, {0.0, 4.0}, {1.5, 1.5}, {-1.5, -1.5}
      };
      int offsetIndex = positionIndex % spawnOffsets.length;
      double[] offset = spawnOffsets[offsetIndex];
      float yrot = player.getYRot() * (float) (Math.PI / 180.0);
      double cos = Math.cos(yrot);
      double sin = Math.sin(yrot);
      double worldX = offset[0] * cos - offset[1] * sin;
      double worldZ = offset[0] * sin + offset[1] * cos;
      Vec3 playerPos = player.getEyePosition();
      Vec3 targetPos = playerPos.add(worldX, 0.0, worldZ);
      Vec3 safePos = findSafePositionNear(level, targetPos);
      if (safePos != null) {
         return safePos;
      } else {
         for (int attempt = 0; attempt < 8; attempt++) {
            double randomX = (level.random.nextDouble() - 0.5) * 4.0;
            double randomZ = (level.random.nextDouble() - 0.5) * 4.0;
            Vec3 alternativePos = targetPos.add(randomX, 0.0, randomZ);
            safePos = findSafePositionNear(level, alternativePos);
            if (safePos != null) {
               return safePos;
            }
         }

         return findSafeSpawnPosition(player, level);
      }
   }

   private static boolean isPositionSafe(ServerLevel level, Vec3 pos) {
      BlockPos blockPos = BlockPos.containing(pos);
      boolean groundSolid = level.getBlockState(blockPos.below()).isSolid();
      boolean spaceEmpty = level.getBlockState(blockPos).isAir() && level.getBlockState(blockPos.above()).isAir();
      return groundSolid && spaceEmpty;
   }

   public static void desummonSpecificFamiliar(ServerPlayer player, UUID familiarId) {
      PlayerFamiliarData familiarData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
      ServerLevel level = player.serverLevel();
      FamiliarsLib.LOGGER.debug("Attempting to desummon specific familiar: {}", familiarId);
      if (level.getEntity(familiarId) instanceof AbstractSpellCastingPet familiar) {
         if (familiar.getSummoner() != null && familiar.getSummoner().is(player) && familiar.getUUID().equals(familiarId)) {
            if (familiar.isCasting()) {
               familiar.cancelCast();
               familiar.getMagicData().resetCastingState();
            }

            familiar.setTarget(null);
            familiar.setSitting(false);
            familiar.setHasUsedSingleAttack(false);
            CompoundTag updatedNBT = createFamiliarNBT(familiar);
            familiarData.addTamedFamiliar(familiarId, updatedNBT);
            familiar.remove(RemovalReason.DISCARDED);
            FamiliarAttributesHelper.handleFamiliarDismissed(player, familiar);
            level.playSound(null, familiar.getX(), familiar.getY(), familiar.getZ(), SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
            familiarData.removeSummonedFamiliar(familiarId);
            FamiliarsLib.LOGGER.debug("Specific familiar desummoned successfully: {}", familiarId);
         } else {
            FamiliarsLib.LOGGER.debug("Familiar found but doesn't belong to player: {}", familiarId);
         }
      } else {
         FamiliarsLib.LOGGER.debug("Familiar not found in world: {}", familiarId);
         familiarData.removeSummonedFamiliar(familiarId);
      }

      syncFamiliarData(player, familiarData);
   }

   @OnlyIn(Dist.CLIENT)
   public static void syncFamiliarData(CompoundTag familiarData) {
      Player player = Minecraft.getInstance().player;
      if (player != null) {
         PlayerFamiliarData data = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
         data.deserializeNBT(player.registryAccess(), familiarData);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void openStorageScreen(BlockPos blockPos) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level != null && minecraft.level.getBlockEntity(blockPos) instanceof AbstractFamiliarStorageBlockEntity storageEntity) {
         if (storageEntity.isStoreMode()) {
            minecraft.setScreen(new FamiliarStorageScreen(blockPos));
         } else {
            minecraft.setScreen(new FamiliarWanderScreen(blockPos));
         }
      }
   }

   public static void updateSummonedFamiliarsData(ServerPlayer player) {
      try {
         PlayerFamiliarData familiarData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
         ServerLevel level = player.serverLevel();
         Set<UUID> actualSummonedFamiliars = new HashSet<>();
         int updatedCount = 0;

         for (Entry<UUID, ?> entry : familiarData.getAllFamiliars().entrySet()) {
            UUID familiarId = entry.getKey();
            if (level.getEntity(familiarId) instanceof AbstractSpellCastingPet familiar && familiar.getSummoner() != null && familiar.getSummoner().is(player)) {
               updateFamiliarData(familiar);
               actualSummonedFamiliars.add(familiarId);
               updatedCount++;
               FamiliarsLib.LOGGER.debug("Updated data for summoned familiar {}", familiarId);
            }
         }

         familiarData.getSummonedFamiliarIds().clear();

         for (UUID summonedId : actualSummonedFamiliars) {
            familiarData.addSummonedFamiliar(summonedId);
         }

         if (updatedCount > 0) {
            FamiliarsLib.LOGGER.debug("Updated data for {} summoned familiars before opening screen", updatedCount);
         }
      } catch (Exception var10) {
         FamiliarsLib.LOGGER.error("Error updating summoned familiars data: ", var10);
      }
   }

   public static void cleanupSummonedFamiliarsOnDimensionChange(ServerPlayer player) {
      PlayerFamiliarData familiarData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
      familiarData.clearAllSummoned();
      FamiliarsLib.LOGGER.debug("Cleared summoned familiars for player {} on dimension change", player.getName().getString());
   }

   public static Set<UUID> getSummonedFamiliarIds(ServerPlayer player) {
      PlayerFamiliarData familiarData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
      return familiarData.getSummonedFamiliarIds();
   }

   public static boolean isFamiliarSummoned(ServerPlayer player, UUID familiarId) {
      PlayerFamiliarData familiarData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
      return familiarData.isFamiliarSummoned(familiarId);
   }

   public static void handleMoveFamiliar(ServerPlayer player, BlockPos blockPos, UUID familiarId, boolean toStorage) {
      if (player.level().getBlockEntity(blockPos) instanceof AbstractFamiliarStorageBlockEntity storageEntity) {
         if (!storageEntity.isOwner(player)) {
            player.connection
               .send(new ClientboundSetActionBarTextPacket(Component.translatable("message.familiarslib.not_storage_owner").withStyle(ChatFormatting.RED)));
         } else if (!storageEntity.isStoreMode()) {
            player.connection
               .send(new ClientboundSetActionBarTextPacket(Component.translatable("message.familiarslib.only_store_mode").withStyle(ChatFormatting.RED)));
         } else {
            PlayerFamiliarData familiarData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
            if (toStorage) {
               if (!familiarData.hasFamiliar(familiarId)) {
                  player.connection
                     .send(
                        new ClientboundSetActionBarTextPacket(Component.translatable("message.familiarslib.familiar_not_found").withStyle(ChatFormatting.RED))
                     );
                  return;
               }

               CompoundTag familiarNBT = familiarData.getFamiliarData(familiarId);
               if (familiarNBT == null) {
                  player.connection
                     .send(
                        new ClientboundSetActionBarTextPacket(Component.translatable("message.familiarslib.familiar_data_error").withStyle(ChatFormatting.RED))
                     );
                  return;
               }

               if (player.level() instanceof ServerLevel serverLevel && serverLevel.getEntity(familiarId) instanceof AbstractSpellCastingPet familiar) {
                  desummonSpecificFamiliar(player, familiarId);
               }

               boolean success = storageEntity.storeFamiliar(familiarId, familiarNBT, player);
               if (success) {
                  CurioUtils.removeFamiliarFromEquippedMultiSelectionCurio(player, familiarId);
                  String familiarName = getFamiliarName(familiarNBT);
                  player.connection
                     .send(
                        new ClientboundSetActionBarTextPacket(
                           Component.translatable("message.familiarslib.familiar_stored", new Object[]{familiarName}).withStyle(ChatFormatting.GREEN)
                        )
                     );
                  syncFamiliarDataForPlayer(player);
               } else {
                  player.connection
                     .send(new ClientboundSetActionBarTextPacket(Component.translatable("message.familiarslib.storage_full").withStyle(ChatFormatting.RED)));
               }
            } else {
               Map<UUID, CompoundTag> storedFamiliars = storageEntity.getStoredFamiliars();
               if (!storedFamiliars.containsKey(familiarId)) {
                  player.connection
                     .send(
                        new ClientboundSetActionBarTextPacket(
                           Component.translatable("message.familiarslib.familiar_not_in_storage").withStyle(ChatFormatting.RED)
                        )
                     );
                  return;
               }

               if (!familiarData.canTameMoreFamiliars()) {
                  player.connection
                     .send(
                        new ClientboundSetActionBarTextPacket(
                           Component.translatable("message.familiarslib.player_familiar_limit").withStyle(ChatFormatting.RED)
                        )
                     );
                  return;
               }

               CompoundTag familiarNBTx = storedFamiliars.get(familiarId);
               boolean success = storageEntity.retrieveFamiliar(familiarId, player);
               if (success) {
                  String familiarName = getFamiliarName(familiarNBTx);
                  player.connection
                     .send(
                        new ClientboundSetActionBarTextPacket(
                           Component.translatable("message.familiarslib.familiar_retrieved", new Object[]{familiarName}).withStyle(ChatFormatting.GREEN)
                        )
                     );
                  syncFamiliarDataForPlayer(player);
               } else {
                  player.connection
                     .send(new ClientboundSetActionBarTextPacket(Component.translatable("message.familiarslib.retrieval_failed").withStyle(ChatFormatting.RED)));
               }
            }

            Map<UUID, CompoundTag> updatedStoredData = storageEntity.getStoredFamiliars();
            PacketDistributor.sendToPlayer(
               player,
               new UpdateFamiliarStoragePacket(
                  blockPos, updatedStoredData, storageEntity.isStoreMode(), storageEntity.canFamiliarsUseGoals(), storageEntity.getMaxDistance()
               ),
               new CustomPacketPayload[0]
            );
         }
      }
   }

   private static String getFamiliarName(CompoundTag familiarNBT) {
      if (familiarNBT.contains("customName")) {
         return familiarNBT.getString("customName");
      } else {
         String entityTypeString = familiarNBT.getString("id");
         String[] parts = entityTypeString.split(":");
         return parts.length > 1 ? parts[1].replace("_", " ") : entityTypeString.substring(entityTypeString.lastIndexOf(58) + 1);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void handleStorageUpdate(
      BlockPos blockPos, Map<UUID, CompoundTag> storedFamiliars, boolean storeMode, boolean canFamiliarsUseGoals, int maxDistance
   ) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level != null && minecraft.level.getBlockEntity(blockPos) instanceof AbstractFamiliarStorageBlockEntity storageEntity) {
         storageEntity.storedFamiliars.clear();

         for (Entry<UUID, CompoundTag> entry : storedFamiliars.entrySet()) {
            storageEntity.storedFamiliars.put(entry.getKey(), new AbstractFamiliarStorageBlockEntity.FamiliarData(entry.getValue(), 0));
         }

         storageEntity.setClientStoreMode(storeMode);
         storageEntity.setClientCanFamiliarsUseGoals(canFamiliarsUseGoals);
         storageEntity.setClientMaxDistance(maxDistance);
         FamiliarsLib.LOGGER
            .debug(
               "Received storage update for position {} with {} stored familiars, mode: {}, goals: {}, distance: {}",
               new Object[]{blockPos, storedFamiliars.size(), storeMode ? "Store" : "Wander", canFamiliarsUseGoals, maxDistance}
            );
         if (minecraft.screen instanceof FamiliarStorageScreen storageScreen) {
            storageScreen.reloadFamiliarData();
         }
      }
   }

   public static boolean storeFamiliarInHouse(UUID familiarId, CompoundTag familiarData, ServerPlayer player, BlockPos storagePos) {
      if (player.level().getBlockEntity(storagePos) instanceof AbstractFamiliarStorageBlockEntity storageEntity) {
         if (!storageEntity.isOwner(player)) {
            return false;
         } else {
            PlayerFamiliarData playerData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
            AbstractFamiliarStorageBlockEntity.FamiliarData data = new AbstractFamiliarStorageBlockEntity.FamiliarData(familiarData, 0);
            storageEntity.storedFamiliars.put(familiarId, data);
            storageEntity.outsideFamiliars.remove(familiarId);
            if (player.level() instanceof ServerLevel serverLevel && serverLevel.getEntity(familiarId) instanceof AbstractSpellCastingPet familiar) {
               familiar.setIsInHouse(true, storagePos);
               desummonSpecificFamiliar(player, familiarId);
            }

            playerData.removeTamedFamiliar(familiarId);
            storageEntity.setChanged();
            storageEntity.syncToClient();
            FamiliarsLib.LOGGER.debug("Stored familiar {} in house at {}", familiarId, storagePos);
            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean retrieveFamiliarFromHouse(UUID familiarId, ServerPlayer player, BlockPos storagePos) {
      if (player.level().getBlockEntity(storagePos) instanceof AbstractFamiliarStorageBlockEntity storageEntity) {
         if (!storageEntity.isOwner(player)) {
            return false;
         } else {
            PlayerFamiliarData playerData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
            if (!playerData.canTameMoreFamiliars()) {
               return false;
            } else {
               AbstractFamiliarStorageBlockEntity.FamiliarData familiarData = storageEntity.storedFamiliars.remove(familiarId);
               storageEntity.outsideFamiliars.remove(familiarId);
               CompoundTag nbtData = familiarData.nbtData.copy();
               nbtData.putBoolean("isInHouse", false);
               playerData.addTamedFamiliar(familiarId, nbtData);
               if (playerData.getSelectedFamiliarId() == null) {
                  playerData.setSelectedFamiliarId(familiarId);
               }

               if (player.level() instanceof ServerLevel serverLevel && serverLevel.getEntity(familiarId) instanceof AbstractSpellCastingPet familiar) {
                  familiar.setIsInHouse(false, null);
                  familiar.remove(RemovalReason.DISCARDED);
                  FamiliarsLib.LOGGER.debug("Removed familiar {} from world as it was retrieved", familiarId);
               }

               storageEntity.setChanged();
               storageEntity.syncToClient();
               FamiliarsLib.LOGGER.debug("Retrieved familiar {} from house at {}", familiarId, storagePos);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   public static void handleSetStorageMode(ServerPlayer player, BlockPos blockPos, boolean storeMode) {
      if (player.level().getBlockEntity(blockPos) instanceof AbstractFamiliarStorageBlockEntity storageEntity) {
         if (!storageEntity.isOwner(player)) {
            player.connection
               .send(new ClientboundSetActionBarTextPacket(Component.translatable("message.familiarslib.not_storage_owner").withStyle(ChatFormatting.RED)));
         } else {
            storageEntity.setStoreMode(storeMode);
            String modeDescription = storeMode
               ? "ui.familiarslib.familiar_storage_screen.store_mode_message"
               : "ui.familiarslib.familiar_storage_screen.wander_mode_message";
            player.connection
               .send(
                  new ClientboundSetActionBarTextPacket(
                     Component.translatable(modeDescription).withStyle(storeMode ? ChatFormatting.GREEN : ChatFormatting.YELLOW)
                  )
               );
            Map<UUID, CompoundTag> storedData = storageEntity.getStoredFamiliars();
            PacketDistributor.sendToPlayer(
               player,
               new UpdateFamiliarStoragePacket(blockPos, storedData, storeMode, storageEntity.canFamiliarsUseGoals(), storageEntity.getMaxDistance()),
               new CustomPacketPayload[0]
            );
         }
      }
   }

   public static void handleUpdateStorageSettings(ServerPlayer player, BlockPos blockPos, boolean canFamiliarsUseGoals, int maxDistance) {
      if (player.level().getBlockEntity(blockPos) instanceof AbstractFamiliarStorageBlockEntity storageEntity) {
         if (!storageEntity.isOwner(player)) {
            player.connection
               .send(new ClientboundSetActionBarTextPacket(Component.translatable("message.familiarslib.not_storage_owner").withStyle(ChatFormatting.RED)));
         } else {
            storageEntity.setCanFamiliarsUseGoals(canFamiliarsUseGoals);
            storageEntity.setMaxDistance(maxDistance);
            FamiliarsLib.LOGGER
               .debug(
                  "Updated storage settings for player {}: goals={}, distance={}",
                  new Object[]{player.getName().getString(), canFamiliarsUseGoals, maxDistance}
               );
            Map<UUID, CompoundTag> storedData = storageEntity.getStoredFamiliars();
            PacketDistributor.sendToPlayer(
               player,
               new UpdateFamiliarStoragePacket(blockPos, storedData, storageEntity.isStoreMode(), canFamiliarsUseGoals, maxDistance),
               new CustomPacketPayload[0]
            );
         }
      }
   }

   public static void requestFamiliarSelectionScreen(ServerPlayer serverPlayer) {
      updateSummonedFamiliarsData(serverPlayer);
      PlayerFamiliarData familiarData = (PlayerFamiliarData)serverPlayer.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
      if (!familiarData.isEmpty()) {
         syncFamiliarDataForPlayer(serverPlayer);
         if (CurioUtils.isWearingMultiSelectionCurio(serverPlayer)) {
            PacketDistributor.sendToPlayer(serverPlayer, new OpenMultiSelectionScreenPacket(), new CustomPacketPayload[0]);
         } else {
            PacketDistributor.sendToPlayer(serverPlayer, new OpenFamiliarSelectionPacket(), new CustomPacketPayload[0]);
         }
      }
   }

   public static void handleFamiliarDeath(AbstractSpellCastingPet familiar, ServerPlayer player) {
      UUID familiarId = familiar.getUUID();
      FamiliarsLib.LOGGER.debug("Handling familiar death for familiar {} owned by player {}", familiarId, player.getName().getString());

      try {
         PlayerFamiliarData familiarData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
         if (!familiarData.hasFamiliar(familiarId)) {
            FamiliarsLib.LOGGER.warn("Familiar {} not found in player {} data during death handling", familiarId, player.getName().getString());
            return;
         }

         markFamiliarAsDead(familiarId);
         familiarData.removeTamedFamiliar(familiarId);
         cleanFamiliarFromMultiSelectionCurio(player, familiarId);
         UUID newSelectedFamiliarId = null;
         if (familiarId.equals(familiarData.getSelectedFamiliarId())) {
            Map<UUID, CompoundTag> availableFamiliars = familiarData.getAllFamiliars();
            if (!availableFamiliars.isEmpty()) {
               newSelectedFamiliarId = availableFamiliars.keySet().iterator().next();
               familiarData.setSelectedFamiliarId(newSelectedFamiliarId);
               FamiliarsLib.LOGGER.debug("Selected new familiar {} after death of {}", newSelectedFamiliarId, familiarId);
            } else {
               familiarData.setSelectedFamiliarId(null);
               FamiliarsLib.LOGGER.debug("No familiars available, cleared selection after death of {}", familiarId);
            }
         }

         Map<UUID, CompoundTag> remainingFamiliars = familiarData.getAllFamiliars();
         UUID currentSummonedFamiliarId = familiarData.getCurrentSummonedFamiliarId();
         CompoundTag familiarSyncData = familiarData.serializeNBT(player.registryAccess());
         PacketDistributor.sendToPlayer(
            player,
            new FamiliarDeathPacket(familiarId, remainingFamiliars, newSelectedFamiliarId, currentSummonedFamiliarId, familiarSyncData),
            new CustomPacketPayload[0]
         );
         FamiliarsLib.LOGGER
            .debug(
               "Successfully processed death of familiar {} for player {}. {} familiars remaining.",
               new Object[]{familiarId, player.getName().getString(), remainingFamiliars.size()}
            );
      } catch (Exception var8) {
         FamiliarsLib.LOGGER
            .error("Error handling familiar death for familiar {} and player {}: ", new Object[]{familiarId, player.getName().getString(), var8});
      }
   }

   public static void cleanFamiliarFromMultiSelectionCurio(ServerPlayer player, UUID deadFamiliarId) {
      try {
         Optional<ItemStack> equippedMultiSelectionCurio = CurioUtils.getEquippedMultiSelectionCurio(player);
         if (equippedMultiSelectionCurio.isPresent()) {
            ItemStack curio = equippedMultiSelectionCurio.get();
            Set<UUID> selectedFamiliars = AbstractMultiSelectionCurio.getSelectedFamiliars(curio);
            if (selectedFamiliars.contains(deadFamiliarId)) {
               Set<UUID> updatedSelection = new HashSet<>(selectedFamiliars);
               updatedSelection.remove(deadFamiliarId);
               AbstractMultiSelectionCurio.setSelectedFamiliars(curio, updatedSelection);
               FamiliarsLib.LOGGER
                  .debug(
                     "Removed dead familiar {} from equipped Multi Selection curio for player {}. {} familiars remaining in selection.",
                     new Object[]{deadFamiliarId, player.getName().getString(), updatedSelection.size()}
                  );
               return;
            }
         }

         ItemStack mainHandItem = player.getItemInHand(InteractionHand.MAIN_HAND);
         ItemStack offHandItem = player.getItemInHand(InteractionHand.OFF_HAND);

         for (ItemStack handItem : Arrays.asList(mainHandItem, offHandItem)) {
            if (handItem.getItem() instanceof AbstractMultiSelectionCurio) {
               Set<UUID> selectedFamiliars = AbstractMultiSelectionCurio.getSelectedFamiliars(handItem);
               if (selectedFamiliars.contains(deadFamiliarId)) {
                  Set<UUID> updatedSelection = new HashSet<>(selectedFamiliars);
                  updatedSelection.remove(deadFamiliarId);
                  AbstractMultiSelectionCurio.setSelectedFamiliars(handItem, updatedSelection);
                  FamiliarsLib.LOGGER
                     .debug(
                        "Removed dead familiar {} from held Multi Selection curio for player {}. {} familiars remaining in selection.",
                        new Object[]{deadFamiliarId, player.getName().getString(), updatedSelection.size()}
                     );
                  break;
               }
            }
         }
      } catch (Exception var9) {
         FamiliarsLib.LOGGER
            .error(
               "Error cleaning dead familiar {} from Multi Selection curio for player {}: ", new Object[]{deadFamiliarId, player.getName().getString(), var9}
            );
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void handleFamiliarDeathPacket(
      UUID deadFamiliarId, Map<UUID, CompoundTag> remainingFamiliars, UUID newSelectedFamiliarId, UUID currentSummonedFamiliarId, CompoundTag familiarData
   ) {
      Player player = Minecraft.getInstance().player;
      if (player != null) {
         FamiliarsLib.LOGGER.debug("Client received familiar death packet for familiar: {}", deadFamiliarId);
         PlayerFamiliarData playerFamiliarData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
         boolean wasRemoved = playerFamiliarData.hasFamiliar(deadFamiliarId);
         if (wasRemoved) {
            playerFamiliarData.removeTamedFamiliar(deadFamiliarId);
            FamiliarsLib.LOGGER.debug("Removed dead familiar {} from client data", deadFamiliarId);
         }

         playerFamiliarData.deserializeNBT(player.registryAccess(), familiarData);
         FamiliarsLib.LOGGER
            .debug(
               "Updated client data - Remaining familiars: {}, New selected: {}, Current summoned: {}",
               new Object[]{remainingFamiliars.size(), newSelectedFamiliarId, currentSummonedFamiliarId}
            );
         cleanFamiliarFromMultiSelectionCurioClient(player, deadFamiliarId);
         updateScreensAfterDeath(deadFamiliarId);
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static void updateScreensAfterDeath(UUID deadFamiliarId) {
      try {
         if (Minecraft.getInstance().screen instanceof FamiliarSelectionScreen familiarScreen) {
            FamiliarsLib.LOGGER.debug("Updating FamiliarSelectionScreen after familiar death: {}", deadFamiliarId);
            familiarScreen.reloadFamiliarData();
            Player player = Minecraft.getInstance().player;
            if (player != null) {
               PlayerFamiliarData familiarData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
               if (familiarData.isEmpty()) {
                  FamiliarsLib.LOGGER.debug("No familiars remaining, closing FamiliarSelectionScreen");
                  familiarScreen.onClose();
               }
            }
         }

         if (Minecraft.getInstance().screen instanceof MultiSelectionCurioScreen) {
            Minecraft.getInstance().setScreen(null);
            Minecraft.getInstance().setScreen(new MultiSelectionCurioScreen());
         }
      } catch (Exception var4) {
         FamiliarsLib.LOGGER.error("Error updating screens after familiar death: ", var4);
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static void cleanFamiliarFromMultiSelectionCurioClient(Player player, UUID deadFamiliarId) {
      try {
         Optional<ItemStack> equippedMultiSelectionCurio = CurioUtils.getEquippedMultiSelectionCurio(player);
         if (equippedMultiSelectionCurio.isPresent()) {
            ItemStack curio = equippedMultiSelectionCurio.get();
            Set<UUID> selectedFamiliars = AbstractMultiSelectionCurio.getSelectedFamiliars(curio);
            if (selectedFamiliars.contains(deadFamiliarId)) {
               Set<UUID> updatedSelection = new HashSet<>(selectedFamiliars);
               updatedSelection.remove(deadFamiliarId);
               AbstractMultiSelectionCurio.setSelectedFamiliars(curio, updatedSelection);
               FamiliarsLib.LOGGER.debug("Client: Removed dead familiar {} from equipped Multi Selection curio", deadFamiliarId);
               return;
            }
         }

         ItemStack mainHandItem = player.getItemInHand(InteractionHand.MAIN_HAND);
         ItemStack offHandItem = player.getItemInHand(InteractionHand.OFF_HAND);

         for (ItemStack handItem : Arrays.asList(mainHandItem, offHandItem)) {
            if (handItem.getItem() instanceof AbstractMultiSelectionCurio) {
               Set<UUID> selectedFamiliars = AbstractMultiSelectionCurio.getSelectedFamiliars(handItem);
               if (selectedFamiliars.contains(deadFamiliarId)) {
                  Set<UUID> updatedSelection = new HashSet<>(selectedFamiliars);
                  updatedSelection.remove(deadFamiliarId);
                  AbstractMultiSelectionCurio.setSelectedFamiliars(handItem, updatedSelection);
                  FamiliarsLib.LOGGER.debug("Client: Removed dead familiar {} from held Multi Selection curio", deadFamiliarId);
                  break;
               }
            }
         }
      } catch (Exception var9) {
         FamiliarsLib.LOGGER.error("Error cleaning dead familiar {} from Multi Selection curio on client: ", deadFamiliarId, var9);
      }
   }

   public static void handleReleaseFamiliar(ServerPlayer player, UUID familiarId) {
      PlayerFamiliarData familiarData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
      FamiliarsLib.LOGGER.debug("Attempting to release familiar {} for player {}", familiarId, player.getName().getString());
      if (!familiarData.hasFamiliar(familiarId)) {
         FamiliarsLib.LOGGER.warn("Player {} tried to release familiar {} but it doesn't exist in their data", player.getName().getString(), familiarId);
      } else {
         CompoundTag familiarNBT = familiarData.getFamiliarData(familiarId);
         String familiarName = "Unknown";
         if (familiarNBT != null) {
            if (familiarNBT.contains("customName")) {
               familiarName = familiarNBT.getString("customName");
            } else {
               String entityTypeString = familiarNBT.getString("id");
               String[] parts = entityTypeString.split(":");
               if (parts.length > 1) {
                  familiarName = parts[1].replace("_", " ");
               }
            }
         }

         if (familiarData.isFamiliarSummoned(familiarId)) {
            FamiliarsLib.LOGGER.debug("Familiar {} is summoned, desummoning before release", familiarId);
            desummonSpecificFamiliar(player, familiarId);
         }

         familiarData.removeTamedFamiliar(familiarId);
         cleanFamiliarFromMultiSelectionCurio(player, familiarId);
         Map<UUID, CompoundTag> remainingFamiliars = familiarData.getAllFamiliars();
         boolean hasRemainingFamiliars = !remainingFamiliars.isEmpty();
         if (familiarId.equals(familiarData.getSelectedFamiliarId()) && hasRemainingFamiliars) {
            Iterator shouldClose = remainingFamiliars.keySet().iterator();
            if (shouldClose.hasNext()) {
               UUID otherId = (UUID)shouldClose.next();
               familiarData.setSelectedFamiliarId(otherId);
               FamiliarsLib.LOGGER.debug("Released familiar was selected, new selected: {}", otherId);
            }
         } else if (familiarId.equals(familiarData.getSelectedFamiliarId())) {
            familiarData.setSelectedFamiliarId(null);
            FamiliarsLib.LOGGER.debug("Released familiar was selected and no familiars remain, clearing selection");
         }

         syncFamiliarData(player, familiarData);
         boolean shouldClose = !hasRemainingFamiliars;
         PacketDistributor.sendToPlayer(player, new ReloadFamiliarScreenPacket(shouldClose), new CustomPacketPayload[0]);
         FamiliarsLib.LOGGER
            .debug(
               "Successfully released familiar {} for player {}. Remaining familiars: {}. Screen action: {}",
               new Object[]{familiarId, player.getName().getString(), remainingFamiliars.size(), shouldClose ? "CLOSE" : "RELOAD"}
            );
      }
   }

   public static void handleFamiliarSummonPackage(ServerPlayer serverPlayer) {
      if (CurioUtils.isWearingMultiSelectionCurio(serverPlayer)) {
         handleMultiSelectionSummoning(serverPlayer);
      } else {
         handleFamiliarSummoning(serverPlayer);
      }
   }

   private static void handleMultiSelectionSummoning(ServerPlayer player) {
      Set<UUID> selectedFamiliars = CurioUtils.getSelectedFamiliarsFromEquipped(player);
      if (selectedFamiliars.isEmpty()) {
         player.connection
            .send(new ClientboundSetActionBarTextPacket(Component.translatable("message.familiarslib.no_familiars_selected").withStyle(ChatFormatting.YELLOW)));
      } else {
         PlayerFamiliarData familiarData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
         boolean anyAction = false;
         int spawnIndex = 0;

         for (UUID familiarId : selectedFamiliars) {
            if (familiarData.hasFamiliar(familiarId)) {
               boolean isSummoned = familiarData.isFamiliarSummoned(familiarId);
               if (isSummoned) {
                  desummonSpecificFamiliar(player, familiarId);
                  anyAction = true;
               } else {
                  summonSpecificFamiliarAtPosition(player, familiarId, spawnIndex);
                  anyAction = true;
                  spawnIndex++;
               }
            }
         }

         if (anyAction) {
            player.connection
               .send(new ClientboundSetActionBarTextPacket(Component.translatable("message.familiarslib.familiars_toggled").withStyle(ChatFormatting.GREEN)));
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void openFamiliarSelectionScreen() {
      Minecraft.getInstance().setScreen(new FamiliarSelectionScreen());
   }

   @OnlyIn(Dist.CLIENT)
   public static void openMultiSelectionScreen() {
      Minecraft.getInstance().setScreen(new MultiSelectionCurioScreen());
   }

   @OnlyIn(Dist.CLIENT)
   public static void handleFamiliarSelectionScreenUpdate(boolean shouldClose) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.screen instanceof FamiliarSelectionScreen familiarScreen) {
         if (shouldClose) {
            minecraft.setScreen(null);
            FamiliarsLib.LOGGER.debug("Closed familiar selection screen - no familiars remaining");
         } else {
            familiarScreen.reloadFamiliarData();
            FamiliarsLib.LOGGER.debug("Reloaded familiar selection screen after familiar release");
         }
      }
   }
}
