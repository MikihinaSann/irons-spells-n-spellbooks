package io.redspace.ironslib.statue.block.statue_block.player;

import com.mojang.serialization.MapCodec;
import io.redspace.ironslib.internal.network.PlatformPacketDistributor;
import io.redspace.ironslib.internal.network.packet.CritParticlesAreaPacket;
import io.redspace.ironslib.internal.network.packet.OpenStatuePoseScreenPacket;
import io.redspace.ironslib.patreon.PatreonData;
import io.redspace.ironslib.statue.StatueData;
import io.redspace.ironslib.statue.block.statue_block.AbstractStatueBlock;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerStatueBlock extends AbstractStatueBlock {
   public static final MapCodec<PlayerStatueBlock> CODEC = simpleCodec(t -> new PlayerStatueBlock());

   public PlayerStatueBlock() {
      super(1, 2, 1);
   }

   @NotNull
   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   @Nullable
   public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
      return new PlayerStatueBlockEntity(pos, state);
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      @NotNull ItemStack stack,
      @NotNull BlockState state,
      @NotNull Level level,
      @NotNull BlockPos pos,
      @NotNull Player player,
      @NotNull InteractionHand hand,
      @NotNull BlockHitResult hitResult
   ) {
      if (stack.is(Items.NAME_TAG) && stack.has(DataComponents.CUSTOM_NAME)) {
         String username = ((Component)stack.get(DataComponents.CUSTOM_NAME)).getString();
         if (level.getBlockEntity(pos) instanceof PlayerStatueBlockEntity statueBlockEntity
            && PatreonData.getPatreonPermissionsByUsername(username).supportsStatues()) {
            UUID uuid = PatreonData.profileFromUsername(username);
            if (uuid != null) {
               StatueData statueData = statueBlockEntity.getStatueData();
               if (statueData == null || !statueData.uuid().equals(uuid)) {
                  statueBlockEntity.setPlayerUuid(uuid);
                  level.playSound(null, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS);
                  if (level instanceof ServerLevel serverLevel) {
                     Vec3i offset = new Vec3i(-(Integer)state.getValue(X_POS), -(Integer)state.getValue(Y_POS), -(Integer)state.getValue(Z_POS));
                     PlatformPacketDistributor.sendToPlayersTrackingChunk(
                        serverLevel,
                        new ChunkPos(pos),
                        new CritParticlesAreaPacket(pos.offset(offset), pos.offset(offset).offset(this.xSize - 1, this.ySize - 1, this.zSize - 1))
                     );
                  }

                  statueBlockEntity.setChanged();
                  if (!player.hasInfiniteMaterials()) {
                     stack.shrink(1);
                  }

                  return ItemInteractionResult.SUCCESS;
               }
            }
         }
      }

      return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
   }

   @NotNull
   protected InteractionResult useWithoutItem(
      @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult
   ) {
      if (player.isCrouching()) {
         if (player instanceof ServerPlayer serverPlayer) {
            PlatformPacketDistributor.sendToPlayer(serverPlayer, new OpenStatuePoseScreenPacket(pos));
         }

         return InteractionResult.SUCCESS;
      } else {
         return super.useWithoutItem(state, level, pos, player, hitResult);
      }
   }
}
