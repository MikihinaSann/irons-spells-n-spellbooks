package net.alshanex.familiarslib.block.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.alshanex.familiarslib.FamiliarsLib;
import net.alshanex.familiarslib.data.PlayerFamiliarData;
import net.alshanex.familiarslib.entity.AbstractSpellCastingPet;
import net.alshanex.familiarslib.registry.AttachmentRegistry;
import net.alshanex.familiarslib.util.familiars.FamiliarManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractFamiliarStorageBlockEntity extends BlockEntity {
   private static final int MAX_STORED_FAMILIARS = 10;
   private static final int DEFAULT_MAX_DISTANCE = 25;
   private UUID ownerUUID;
   public final Map<UUID, AbstractFamiliarStorageBlockEntity.FamiliarData> storedFamiliars = new HashMap<>();
   public final Set<UUID> outsideFamiliars = new HashSet<>();
   private boolean storeMode = true;
   private boolean canFamiliarsUseGoals = true;
   private int maxDistance = 25;

   public AbstractFamiliarStorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
      super(type, pos, blockState);
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, AbstractFamiliarStorageBlockEntity storageEntity) {
      storageEntity.tick();
   }

   protected void tick() {
      if (this.level != null && !this.level.isClientSide) {
         if (!this.storeMode) {
            if (this.level.getGameTime() % 40L == 0L) {
               this.enforceDistanceLimits();
            }

            if (this.level.getGameTime() % 100L == 0L) {
               this.cleanupDeadFamiliars();
            }
         }
      }
   }

   protected void enforceDistanceLimits() {
      if (!this.outsideFamiliars.isEmpty()) {
         ServerLevel serverLevel = (ServerLevel)this.level;
         Vec3 houseCenter = Vec3.atCenterOf(this.getBlockPos());

         for (UUID familiarId : new HashSet<>(this.outsideFamiliars)) {
            Entity entity = serverLevel.getEntity(familiarId);
            if (entity != null && entity instanceof AbstractSpellCastingPet familiar) {
               double distance = familiar.position().distanceTo(houseCenter);
               if (distance > this.maxDistance + 2.0) {
                  Vec3 direction = familiar.position().subtract(houseCenter).normalize();
                  Vec3 targetPos = houseCenter.add(direction.scale(this.maxDistance * 0.6));
                  BlockPos targetBlock = BlockPos.containing(targetPos);
                  BlockPos safePos = this.findSafePositionNear(targetBlock);
                  if (safePos != null) {
                     familiar.teleportTo(safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5);
                     familiar.getNavigation().stop();
                     FamiliarsLib.LOGGER
                        .debug(
                           "Teleported familiar {} back within range (was {} blocks away, max {})", new Object[]{familiarId, (int)distance, this.maxDistance}
                        );
                  } else {
                     Vec3 releasePos = this.findSafeReleasePosition();
                     if (releasePos != null) {
                        familiar.teleportTo(releasePos.x, releasePos.y, releasePos.z);
                        familiar.getNavigation().stop();
                        FamiliarsLib.LOGGER.debug("Teleported familiar {} to house position (no safe nearby pos found)", familiarId);
                     }
                  }
               }
            }
         }
      }
   }

   private BlockPos findSafePositionNear(BlockPos center) {
      if (this.isSafeSpawnPosition(center)) {
         return center;
      } else {
         for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
               for (int dy = -2; dy <= 2; dy++) {
                  BlockPos check = center.offset(dx, dy, dz);
                  if (this.isSafeSpawnPosition(check)) {
                     return check;
                  }
               }
            }
         }

         return null;
      }
   }

   public boolean isStoreMode() {
      return this.storeMode;
   }

   public void setStoreMode(boolean storeMode) {
      if (this.storeMode != storeMode) {
         this.storeMode = storeMode;
         if (storeMode) {
            this.recallAllOutsideFamiliars();
         } else {
            this.releaseAllStoredFamiliars();
         }

         this.setChanged();
         this.syncToClient();
         FamiliarsLib.LOGGER.debug("Storage mode changed to: {}", storeMode ? "Store" : "Wander");
      }
   }

   public boolean canFamiliarsUseGoals() {
      return this.canFamiliarsUseGoals;
   }

   public void setCanFamiliarsUseGoals(boolean canFamiliarsUseGoals) {
      if (this.canFamiliarsUseGoals != canFamiliarsUseGoals) {
         this.canFamiliarsUseGoals = canFamiliarsUseGoals;
         this.setChanged();
         this.syncToClient();
         FamiliarsLib.LOGGER.debug("Can familiars use goals changed to: {}", canFamiliarsUseGoals);
      }
   }

   public int getMaxDistance() {
      return this.maxDistance;
   }

   public void setMaxDistance(int maxDistance) {
      this.maxDistance = Math.max(3, Math.min(25, maxDistance));
      this.setChanged();
      this.syncToClient();
      FamiliarsLib.LOGGER.debug("Max distance changed to: {}", this.maxDistance);
   }

   protected void recallAllOutsideFamiliars() {
      if (!this.outsideFamiliars.isEmpty()) {
         ServerLevel serverLevel = (ServerLevel)this.level;

         for (UUID familiarId : new HashSet<>(this.outsideFamiliars)) {
            if (serverLevel.getEntity(familiarId) instanceof AbstractSpellCastingPet familiar) {
               CompoundTag nbtData = FamiliarManager.createFamiliarNBT(familiar);
               AbstractFamiliarStorageBlockEntity.FamiliarData data = new AbstractFamiliarStorageBlockEntity.FamiliarData(nbtData, 0);
               this.storedFamiliars.put(familiarId, data);
               familiar.remove(RemovalReason.DISCARDED);
               FamiliarsLib.LOGGER.debug("Recalled familiar {} due to Store Mode activation", familiarId);
            }
         }

         this.outsideFamiliars.clear();
         this.setChanged();
         this.syncToClient();
      }
   }

   protected void releaseAllStoredFamiliars() {
      if (!this.storedFamiliars.isEmpty()) {
         Map<UUID, AbstractFamiliarStorageBlockEntity.FamiliarData> familiarsToRelease = new HashMap<>(this.storedFamiliars);

         for (Entry<UUID, AbstractFamiliarStorageBlockEntity.FamiliarData> entry : familiarsToRelease.entrySet()) {
            this.releaseFamiliar(entry.getKey());
         }
      }
   }

   protected void releaseFamiliar(UUID familiarId) {
      AbstractFamiliarStorageBlockEntity.FamiliarData familiarData = this.storedFamiliars.get(familiarId);
      if (familiarData != null) {
         ServerLevel serverLevel = (ServerLevel)this.level;
         String entityTypeString = familiarData.nbtData.getString("id");
         EntityType<?> entityType = (EntityType<?>)EntityType.byString(entityTypeString).orElse(null);
         if (entityType == null) {
            FamiliarsLib.LOGGER.debug("Unknown entity type: {}", entityTypeString);
         } else {
            Entity entity = entityType.create(serverLevel);
            if (entity instanceof AbstractSpellCastingPet familiar) {
               familiar.load(familiarData.nbtData);
               familiar.setUUID(familiarId);
               float savedHealth = familiarData.nbtData.getFloat("currentHealth");
               familiar.setHealth(Math.min(savedHealth, familiar.getMaxHealth()));
               Vec3 spawnPos = this.findSafeReleasePosition();
               if (spawnPos == null) {
                  FamiliarsLib.LOGGER.debug("No safe release position found for familiar {}", familiarId);
               } else {
                  familiar.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
                  familiar.setYRot(this.level.random.nextFloat() * 360.0F);
                  familiar.setOldPosAndRot();
                  familiar.setIsInHouse(true, this.getBlockPos());
                  serverLevel.addFreshEntity(familiar);
                  serverLevel.playSound(null, spawnPos.x, spawnPos.y, spawnPos.z, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
                  serverLevel.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), Context.of(familiar, this.getBlockState()));
                  this.storedFamiliars.remove(familiarId);
                  this.outsideFamiliars.add(familiarId);
                  this.setChanged();
                  this.syncToClient();
                  FamiliarsLib.LOGGER.debug("Released familiar {} from storage at {}", familiarId, this.getBlockPos());
               }
            } else {
               FamiliarsLib.LOGGER.debug("Entity is not a familiar: {}", entity);
            }
         }
      }
   }

   protected void cleanupDeadFamiliars() {
      if (!this.outsideFamiliars.isEmpty()) {
         ServerLevel serverLevel = (ServerLevel)this.level;
         Set<UUID> familiarsToRemove = new HashSet<>();

         for (UUID familiarId : new HashSet<>(this.outsideFamiliars)) {
            Entity entity = serverLevel.getEntity(familiarId);
            if (entity != null) {
               if (entity instanceof AbstractSpellCastingPet familiar) {
                  if (!familiar.isAlive() || familiar.isRemoved()) {
                     familiarsToRemove.add(familiarId);
                     FamiliarsLib.LOGGER.debug("Familiar {} is dead or removed, cleaning up", familiarId);
                  }
               } else {
                  familiarsToRemove.add(familiarId);
                  FamiliarsLib.LOGGER.debug("Entity {} is not a familiar, removing from tracking", familiarId);
               }
            }
         }

         if (!familiarsToRemove.isEmpty()) {
            this.outsideFamiliars.removeAll(familiarsToRemove);
            this.setChanged();
            this.syncToClient();
         }
      }
   }

   protected abstract Direction getFacingDirection();

   protected Vec3 findSafeReleasePosition() {
      BlockPos storagePos = this.getBlockPos();
      Direction facing = this.getFacingDirection();
      BlockPos frontPos = storagePos.relative(facing);
      return this.isSafeSpawnPosition(frontPos) ? Vec3.atBottomCenterOf(frontPos) : null;
   }

   protected boolean isSafeSpawnPosition(BlockPos pos) {
      return this.level.getBlockState(pos).isAir() && this.level.getBlockState(pos.above()).isAir() && this.level.getBlockState(pos.below()).isSolid();
   }

   public boolean tryRecallFamiliar(AbstractSpellCastingPet familiar) {
      if (!this.canStoreFamiliar()) {
         return false;
      } else {
         UUID familiarId = familiar.getUUID();
         if (this.outsideFamiliars.contains(familiarId) && familiar.getIsInHouse() && this.getBlockPos().equals(familiar.housePosition)) {
            CompoundTag nbtData = FamiliarManager.createFamiliarNBT(familiar);
            AbstractFamiliarStorageBlockEntity.FamiliarData familiarDataObj = new AbstractFamiliarStorageBlockEntity.FamiliarData(nbtData, 0);
            this.storedFamiliars.put(familiarId, familiarDataObj);
            this.outsideFamiliars.remove(familiarId);
            familiar.remove(RemovalReason.DISCARDED);
            this.level.playSound(null, this.getBlockPos(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
            this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), Context.of(familiar, this.getBlockState()));
            this.setChanged();
            this.syncToClient();
            FamiliarsLib.LOGGER.debug("Recalled familiar {} to storage", familiarId);
            return true;
         } else {
            return false;
         }
      }
   }

   public void setOwner(ServerPlayer player) {
      this.ownerUUID = player.getUUID();
      this.setChanged();
   }

   public boolean isOwner(ServerPlayer player) {
      return this.ownerUUID != null && this.ownerUUID.equals(player.getUUID());
   }

   public UUID getOwnerUUID() {
      return this.ownerUUID;
   }

   public Map<UUID, CompoundTag> getStoredFamiliars() {
      Map<UUID, CompoundTag> result = new HashMap<>();

      for (Entry<UUID, AbstractFamiliarStorageBlockEntity.FamiliarData> entry : this.storedFamiliars.entrySet()) {
         result.put(entry.getKey(), entry.getValue().nbtData);
      }

      return result;
   }

   public boolean isFamiliarPhysicallyStored(UUID familiarId) {
      return this.storedFamiliars.containsKey(familiarId);
   }

   public Map<UUID, CompoundTag> getPhysicallyStoredFamiliars() {
      Map<UUID, CompoundTag> result = new HashMap<>();

      for (Entry<UUID, AbstractFamiliarStorageBlockEntity.FamiliarData> entry : this.storedFamiliars.entrySet()) {
         result.put(entry.getKey(), entry.getValue().nbtData);
      }

      return result;
   }

   public boolean canStoreFamiliar() {
      return this.storedFamiliars.size() + this.outsideFamiliars.size() < 10;
   }

   public int getStoredFamiliarCount() {
      return this.storedFamiliars.size();
   }

   public int getMaxStoredFamiliars() {
      return 10;
   }

   public int getOutsideFamiliarCount() {
      return this.outsideFamiliars.size();
   }

   public boolean storeFamiliar(UUID familiarId, CompoundTag familiarData, ServerPlayer player) {
      if (!this.isOwner(player)) {
         return false;
      } else {
         return !this.canStoreFamiliar() ? false : FamiliarManager.storeFamiliarInHouse(familiarId, familiarData, player, this.getBlockPos());
      }
   }

   public boolean retrieveFamiliar(UUID familiarId, ServerPlayer player) {
      if (!this.isOwner(player)) {
         return false;
      } else {
         PlayerFamiliarData playerData = (PlayerFamiliarData)player.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);
         if (!playerData.canTameMoreFamiliars()) {
            return false;
         } else {
            return this.storedFamiliars.containsKey(familiarId) ? FamiliarManager.retrieveFamiliarFromHouse(familiarId, player, this.getBlockPos()) : false;
         }
      }
   }

   public void returnFamiliarsToOwner() {
      if (this.ownerUUID != null && this.level != null && !this.level.isClientSide) {
         if (this.level instanceof ServerLevel serverLevel) {
            ServerPlayer owner = serverLevel.getServer().getPlayerList().getPlayer(this.ownerUUID);
            if (owner != null) {
               PlayerFamiliarData playerData = (PlayerFamiliarData)owner.getData(AttachmentRegistry.PLAYER_FAMILIAR_DATA);

               for (Entry<UUID, AbstractFamiliarStorageBlockEntity.FamiliarData> entry : this.storedFamiliars.entrySet()) {
                  UUID familiarId = entry.getKey();
                  CompoundTag familiarData = entry.getValue().nbtData;
                  if (playerData.canTameMoreFamiliars()) {
                     familiarData.putBoolean("isInHouse", false);
                     playerData.addTamedFamiliar(familiarId, familiarData);
                     if (playerData.getSelectedFamiliarId() == null) {
                        playerData.setSelectedFamiliarId(familiarId);
                     }

                     FamiliarsLib.LOGGER.debug("Returned familiar {} to owner {}", familiarId, owner.getName().getString());
                  }
               }

               for (UUID familiarId : this.outsideFamiliars) {
                  if (serverLevel.getEntity(familiarId) instanceof AbstractSpellCastingPet familiar) {
                     familiar.setIsInHouse(false, null);
                     familiar.remove(RemovalReason.DISCARDED);
                     FamiliarsLib.LOGGER.debug("Removed outside familiar {} from world due to house destruction", familiarId);
                  }
               }

               FamiliarManager.syncFamiliarDataForPlayer(owner);
            }
         }

         this.storedFamiliars.clear();
         this.outsideFamiliars.clear();
      }
   }

   public void setClientStoreMode(boolean storeMode) {
      if (this.level != null && this.level.isClientSide) {
         this.storeMode = storeMode;
         FamiliarsLib.LOGGER.debug("Client updated storage mode to: {}", storeMode ? "Store" : "Wander");
      }
   }

   public void setClientCanFamiliarsUseGoals(boolean canFamiliarsUseGoals) {
      if (this.level != null && this.level.isClientSide) {
         this.canFamiliarsUseGoals = canFamiliarsUseGoals;
         FamiliarsLib.LOGGER.debug("Client updated can familiars use goals to: {}", canFamiliarsUseGoals);
      }
   }

   public void setClientMaxDistance(int maxDistance) {
      if (this.level != null && this.level.isClientSide) {
         this.maxDistance = maxDistance;
         FamiliarsLib.LOGGER.debug("Client updated max distance to: {}", maxDistance);
      }
   }

   public boolean ownsFamiliar(UUID familiarId) {
      return this.storedFamiliars.containsKey(familiarId) || this.outsideFamiliars.contains(familiarId);
   }

   public void handleFamiliarDeath(UUID familiarId) {
      if (this.level != null && !this.level.isClientSide) {
         boolean wasTracked = false;
         if (this.outsideFamiliars.remove(familiarId)) {
            wasTracked = true;
            FamiliarsLib.LOGGER.debug("Removed dead familiar {} from outside tracking", familiarId);
         }

         if (this.storedFamiliars.remove(familiarId) != null) {
            wasTracked = true;
            FamiliarsLib.LOGGER.debug("Removed dead familiar {} from stored familiars (unusual case)", familiarId);
         }

         if (wasTracked) {
            this.setChanged();
            this.syncToClient();
            if (this.ownerUUID != null && this.level instanceof ServerLevel serverLevel) {
               ServerPlayer owner = serverLevel.getServer().getPlayerList().getPlayer(this.ownerUUID);
               if (owner != null) {
                  owner.displayClientMessage(Component.translatable("message.familiarslib.familiar_died_in_house").withStyle(ChatFormatting.RED), false);
               }
            }
         }
      }
   }

   public void syncToClient() {
      if (this.level != null && !this.level.isClientSide) {
         this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
      }
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      if (this.ownerUUID != null) {
         tag.putUUID("ownerUUID", this.ownerUUID);
      }

      tag.putBoolean("storeMode", this.storeMode);
      tag.putBoolean("canFamiliarsUseGoals", this.canFamiliarsUseGoals);
      tag.putInt("maxDistance", this.maxDistance);
      ListTag storedList = new ListTag();

      for (Entry<UUID, AbstractFamiliarStorageBlockEntity.FamiliarData> entry : this.storedFamiliars.entrySet()) {
         CompoundTag familiarEntry = new CompoundTag();
         familiarEntry.putUUID("id", entry.getKey());
         familiarEntry.put("data", entry.getValue().nbtData);
         familiarEntry.putInt("occupationTime", entry.getValue().occupationTime);
         storedList.add(familiarEntry);
      }

      tag.put("storedFamiliars", storedList);
      ListTag outsideList = new ListTag();

      for (UUID familiarId : this.outsideFamiliars) {
         CompoundTag outsideEntry = new CompoundTag();
         outsideEntry.putUUID("id", familiarId);
         outsideList.add(outsideEntry);
      }

      tag.put("outsideFamiliars", outsideList);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      if (tag.hasUUID("ownerUUID")) {
         this.ownerUUID = tag.getUUID("ownerUUID");
      }

      this.storeMode = tag.getBoolean("storeMode");
      this.canFamiliarsUseGoals = tag.getBoolean("canFamiliarsUseGoals");
      this.maxDistance = tag.getInt("maxDistance");
      if (this.maxDistance == 0) {
         this.maxDistance = 25;
      }

      this.storedFamiliars.clear();
      if (tag.contains("storedFamiliars", 9)) {
         ListTag storedList = tag.getList("storedFamiliars", 10);

         for (int i = 0; i < storedList.size(); i++) {
            CompoundTag familiarEntry = storedList.getCompound(i);
            if (familiarEntry.hasUUID("id")) {
               UUID id = familiarEntry.getUUID("id");
               CompoundTag data = familiarEntry.getCompound("data");
               int occupationTime = familiarEntry.getInt("occupationTime");
               this.storedFamiliars.put(id, new AbstractFamiliarStorageBlockEntity.FamiliarData(data, occupationTime));
            }
         }
      }

      this.outsideFamiliars.clear();
      if (tag.contains("outsideFamiliars", 9)) {
         ListTag outsideList = tag.getList("outsideFamiliars", 10);

         for (int ix = 0; ix < outsideList.size(); ix++) {
            CompoundTag outsideEntry = outsideList.getCompound(ix);
            if (outsideEntry.hasUUID("id")) {
               UUID id = outsideEntry.getUUID("id");
               this.outsideFamiliars.add(id);
            }
         }
      }
   }

   public CompoundTag getUpdateTag(Provider registries) {
      CompoundTag tag = super.getUpdateTag(registries);
      this.saveAdditional(tag, registries);
      return tag;
   }

   public void handleUpdateTag(CompoundTag tag, Provider registries) {
      super.handleUpdateTag(tag, registries);
      this.loadAdditional(tag, registries);
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public static class FamiliarData {
      public final CompoundTag nbtData;
      public int occupationTime;

      public FamiliarData(CompoundTag nbtData, int occupationTime) {
         this.nbtData = nbtData;
         this.occupationTime = occupationTime;
      }

      public boolean canBeReleased() {
         return true;
      }
   }
}
