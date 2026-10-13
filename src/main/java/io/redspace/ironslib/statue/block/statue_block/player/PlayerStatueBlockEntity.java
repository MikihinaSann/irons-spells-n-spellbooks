package io.redspace.ironslib.statue.block.statue_block.player;

import io.redspace.ironslib.registry.IronsLibRegistries;
import io.redspace.ironslib.statue.PlayerStatuePose;
import io.redspace.ironslib.statue.StatueData;
import io.redspace.ironslib.statue.block.statue_block.AbstractStatueBlock;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockBox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentMap.Builder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class PlayerStatueBlockEntity extends BlockEntity {
   @Nullable
   private StatueData statueData = null;

   public static PlayerStatueBlockEntity renderable(StatueData statueData) {
      PlayerStatueBlockEntity fakeBlock = new PlayerStatueBlockEntity(
         BlockPos.ZERO, ((Block)IronsLibRegistries.BlockRegistry.PLAYER_STATUE_BLOCK.get()).defaultBlockState()
      );
      fakeBlock.statueData = statueData;
      return fakeBlock;
   }

   public PlayerStatueBlockEntity(BlockPos pos, BlockState blockState) {
      super((BlockEntityType)IronsLibRegistries.BlockRegistry.STATUE_BLOCK_ENTITY.get(), pos, blockState);
   }

   @Nullable
   public StatueData getStatueData() {
      PlayerStatueBlockEntity statue = this.getPrimaryController();
      return statue == null ? null : statue.statueData;
   }

   public void setStatueData(@Nullable StatueData statueData) {
      this.getPrimaryControllerOpt().ifPresent(statue -> statue.setControllerStatueData(statueData));
   }

   private void setControllerStatueData(@Nullable StatueData statueData) {
      this.statueData = statueData;
      if (this.level != null && this.getBlockState().getBlock() instanceof AbstractStatueBlock statueBlock) {
         for (BlockPos childPos : BlockBox.of(
            this.getBlockPos(), this.getBlockPos().offset(statueBlock.xSize - 1, statueBlock.ySize - 1, statueBlock.zSize - 1)
         )) {
            if (!childPos.equals(this.worldPosition) && this.level.getBlockEntity(childPos) instanceof PlayerStatueBlockEntity child) {
               child.statueData = this.statueData;
               setChanged(this.level, childPos, this.level.getBlockState(childPos));
            }
         }

         this.setChanged();
      }
   }

   public void setControllerFrom(PlayerStatueBlockEntity other) {
      this.setControllerStatueData(other.statueData);
   }

   public void setPlayerUuid(UUID uuid) {
      if (this.statueData == null) {
         this.setStatueData(new StatueData(uuid, PlayerStatuePose.defaultPose(), false));
      } else {
         this.setStatueData(this.statueData.updateUUID(uuid));
      }
   }

   public void setPose(PlayerStatuePose pose) {
      if (this.statueData != null) {
         this.setStatueData(this.statueData.updatePose(pose));
      }
   }

   public void setFlipped(boolean flipped) {
      if (this.statueData != null) {
         this.setStatueData(this.statueData.updateFlipped(flipped));
      }
   }

   public boolean isPrimary() {
      BlockState state = this.getBlockState();
      return (Integer)state.getValue(AbstractStatueBlock.X_POS) == 0
         && (Integer)state.getValue(AbstractStatueBlock.Y_POS) == 0
         && (Integer)state.getValue(AbstractStatueBlock.Z_POS) == 0;
   }

   @NotNull
   public Optional<PlayerStatueBlockEntity> getPrimaryControllerOpt() {
      return Optional.ofNullable(this.getPrimaryController());
   }

   @Nullable
   public PlayerStatueBlockEntity getPrimaryController() {
      BlockState state = this.getBlockState();
      int xPos = (Integer)state.getValue(AbstractStatueBlock.X_POS);
      int yPos = (Integer)state.getValue(AbstractStatueBlock.Y_POS);
      int zPos = (Integer)state.getValue(AbstractStatueBlock.Z_POS);
      if (xPos == 0 && yPos == 0 && zPos == 0) {
         return this;
      } else {
         if (this.level != null) {
            BlockEntity var6 = this.level.getBlockEntity(this.getBlockPos().offset(-xPos, -yPos, -zPos));
            if (var6 instanceof PlayerStatueBlockEntity) {
               return (PlayerStatueBlockEntity)var6;
            }
         }

         return null;
      }
   }

   public void setChanged() {
      super.setChanged();
      if (this.level != null && !this.level.isClientSide) {
         BlockState state = this.getBlockState();
         this.level.sendBlockUpdated(this.worldPosition, state, state, 2);
      }
   }

   protected void saveAdditional(@NotNull CompoundTag tag, @NotNull Provider registries) {
      super.saveAdditional(tag, registries);
      if (this.statueData != null) {
         tag.putUUID("playerUuid", this.statueData.uuid());
         tag.putString("pose", this.statueData.pose().getSerializedName());
         if (this.statueData.flipped()) {
            tag.putBoolean("flipped", true);
         }
      }
   }

   protected void loadAdditional(@NotNull CompoundTag tag, @NotNull Provider registries) {
      super.loadAdditional(tag, registries);
      if (tag.contains("playerUuid")) {
         UUID uuid = tag.getUUID("playerUuid");
         PlayerStatuePose pose = PlayerStatuePose.defaultPose();
         boolean flipped = false;
         if (tag.contains("pose")) {
            pose = PlayerStatuePose.fromString(tag.getString("pose"));
         }

         if (tag.contains("flipped")) {
            flipped = tag.getBoolean("flipped");
         }

         this.statueData = new StatueData(uuid, pose, flipped);
      }
   }

   @NotNull
   public CompoundTag getUpdateTag(@NotNull Provider registries) {
      CompoundTag tag = new CompoundTag();
      this.saveAdditional(tag, registries);
      return tag;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   protected void applyImplicitComponents(@NotNull DataComponentInput componentInput) {
      StatueData data = (StatueData)componentInput.get((DataComponentType)IronsLibRegistries.ComponentRegistry.STATUE_ITEM_DATA.get());
      if (data != null) {
         this.statueData = data;
      }
   }

   protected void collectImplicitComponents(@NotNull Builder components) {
      if (this.statueData != null) {
         components.set((DataComponentType)IronsLibRegistries.ComponentRegistry.STATUE_ITEM_DATA.get(), this.statueData);
      }
   }
}
