package net.alshanex.familiarslib.block.entity;

import javax.annotation.Nonnull;
import net.alshanex.familiarslib.util.familiars.BedCleanupHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractFamiliarBedBlockEntity extends BlockEntity {
   protected boolean isBedTaken;
   private int safetyCheckTimer = 0;
   private static final int SAFETY_CHECK_INTERVAL = 100;

   public AbstractFamiliarBedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
      super(type, pos, blockState);
      this.isBedTaken = false;
   }

   public void tick() {
      if (this.level != null && !this.level.isClientSide) {
         this.safetyCheckTimer++;
         if (this.safetyCheckTimer >= 100) {
            this.safetyCheckTimer = 0;
            if (this.isBedTaken()) {
               BedCleanupHelper.performSafetyCheck(this, this.worldPosition, (ServerLevel)this.level);
            }
         }
      }
   }

   public boolean isBedTaken() {
      return this.isBedTaken;
   }

   public void setBedTaken(boolean bool) {
      this.isBedTaken = bool;
      this.setChanged();
      if (this.level != null && !this.level.isClientSide) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      }
   }

   protected void loadAdditional(CompoundTag pTag, Provider pRegistries) {
      super.loadAdditional(pTag, pRegistries);
      if (pTag.hasUUID("isTaken")) {
         this.isBedTaken = pTag.getBoolean("isTaken");
      }
   }

   protected void saveAdditional(@Nonnull CompoundTag tag, Provider registryAccess) {
      super.saveAdditional(tag, registryAccess);
      tag.putBoolean("isTaken", this.isBedTaken);
   }

   public CompoundTag getUpdateTag(Provider pRegistries) {
      CompoundTag tag = new CompoundTag();
      tag.putBoolean("isTaken", this.isBedTaken);
      return tag;
   }

   public void handleUpdateTag(CompoundTag tag, Provider lookupProvider) {
      this.loadAdditional(tag, lookupProvider);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, Provider lookupProvider) {
      this.handleUpdateTag(pkt.getTag(), lookupProvider);
      this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
   }

   public Vec3 getSleepPosition() {
      return new Vec3(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.125, this.worldPosition.getZ() + 0.5);
   }

   public boolean isPositionCorrectForSleeping(Vec3 entityPos) {
      Vec3 sleepPos = this.getSleepPosition();
      double distance = entityPos.distanceTo(sleepPos);
      return distance <= 1.0;
   }
}
