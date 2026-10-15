package net.alshanex.familiarslib.block;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.alshanex.familiarslib.block.entity.AbstractFamiliarStorageBlockEntity;
import net.alshanex.familiarslib.network.OpenFamiliarStoragePacket;
import net.alshanex.familiarslib.network.UpdateFamiliarStoragePacket;
import net.alshanex.familiarslib.registry.FParticleRegistry;
import net.alshanex.familiarslib.util.CurioUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractFamiliarStorageBlock extends BaseEntityBlock implements EntityBlock {
   protected AbstractFamiliarStorageBlock(Properties properties) {
      super(properties);
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      super.setPlacedBy(level, pos, state, placer, stack);
      if (!level.isClientSide
         && placer instanceof ServerPlayer serverPlayer
         && level.getBlockEntity(pos) instanceof AbstractFamiliarStorageBlockEntity storageEntity) {
         storageEntity.setOwner(serverPlayer);
      }
   }

   protected int getAmountPerChunk() {
      return 1;
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      if (!level.isClientSide
         && player instanceof ServerPlayer serverPlayer
         && level.getBlockEntity(pos) instanceof AbstractFamiliarStorageBlockEntity storageEntity) {
         if (this.countStorageBlocksInRadius(level, pos, 16) > this.getAmountPerChunk()) {
            serverPlayer.connection
               .send(
                  new ClientboundSetActionBarTextPacket(Component.translatable("message.familiarslib.storage_block_chunk_limit").withStyle(ChatFormatting.RED))
               );
            return InteractionResult.FAIL;
         } else if (!storageEntity.isOwner(serverPlayer)) {
            return InteractionResult.FAIL;
         } else {
            Map<UUID, CompoundTag> storedData = storageEntity.getStoredFamiliars();
            PacketDistributor.sendToPlayer(
               serverPlayer,
               new UpdateFamiliarStoragePacket(
                  pos, storedData, storageEntity.isStoreMode(), storageEntity.canFamiliarsUseGoals(), storageEntity.getMaxDistance()
               ),
               new CustomPacketPayload[0]
            );
            PacketDistributor.sendToPlayer(serverPlayer, new OpenFamiliarStoragePacket(pos), new CustomPacketPayload[0]);
            return InteractionResult.SUCCESS;
         }
      } else {
         if (player instanceof ServerPlayer serverPlayerx && CurioUtils.isWearingMultiSelectionCurio(serverPlayerx)) {
            CurioUtils.updateSelectedFamiliarsInEquipped(serverPlayerx, Set.of());
         }

         return InteractionResult.sidedSuccess(level.isClientSide);
      }
   }

   private int countStorageBlocksInRadius(Level level, BlockPos centerPos, int radius) {
      int count = 0;
      int startX = centerPos.getX() - radius;
      int endX = centerPos.getX() + radius;
      int startZ = centerPos.getZ() - radius;
      int endZ = centerPos.getZ() + radius;
      int minY = level.getMinBuildHeight();
      int maxY = level.getMaxBuildHeight();

      for (int x = startX; x <= endX; x++) {
         for (int z = startZ; z <= endZ; z++) {
            for (int y = minY; y < maxY; y++) {
               BlockPos checkPos = new BlockPos(x, y, z);
               BlockState blockState = level.getBlockState(checkPos);
               if (blockState.getBlock() instanceof AbstractFamiliarStorageBlock) {
                  count++;
               }
            }
         }
      }

      return count;
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock())) {
         if (level.getBlockEntity(pos) instanceof AbstractFamiliarStorageBlockEntity storageEntity) {
            storageEntity.returnFamiliarsToOwner();
         }

         super.onRemove(state, level, pos, newState, isMoving);
      }
   }

   protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
      if (level.getBlockEntity(pos) instanceof AbstractFamiliarStorageBlockEntity storageEntity) {
         if (player instanceof ServerPlayer serverPlayer && !storageEntity.isOwner(serverPlayer) && !serverPlayer.getAbilities().instabuild) {
            return 0.0F;
         }

         if (player.getAbilities().instabuild) {
            return super.getDestroyProgress(state, player, level, pos);
         }

         if (!storageEntity.isStoreMode()) {
            if (!player.level.isClientSide && player instanceof ServerPlayer serverPlayer) {
               serverPlayer.displayClientMessage(Component.translatable("message.familiarslib.cannot_break_wander_mode").withStyle(ChatFormatting.RED), true);
            }

            return 0.0F;
         }

         if (storageEntity.getStoredFamiliarCount() > 0 || storageEntity.getOutsideFamiliarCount() > 0) {
            if (!player.level.isClientSide && player instanceof ServerPlayer serverPlayer) {
               serverPlayer.displayClientMessage(Component.translatable("message.familiarslib.cannot_break_has_familiars").withStyle(ChatFormatting.RED), true);
            }

            return 0.0F;
         }
      }

      return super.getDestroyProgress(state, player, level, pos);
   }

   public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
      if (level.getBlockEntity(pos) instanceof AbstractFamiliarStorageBlockEntity storageEntity) {
         if (player instanceof ServerPlayer serverPlayer && !storageEntity.isOwner(serverPlayer) && !serverPlayer.getAbilities().instabuild) {
            return false;
         }

         if (player.getAbilities().instabuild) {
            return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
         }

         if (!storageEntity.isStoreMode()) {
            return false;
         }

         if (storageEntity.getStoredFamiliarCount() > 0 || storageEntity.getOutsideFamiliarCount() > 0) {
            return false;
         }
      }

      return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
   }

   protected ParticleOptions getParticlesForAnimation() {
      return ParticleTypes.HAPPY_VILLAGER;
   }

   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      if (level.getBlockEntity(pos) instanceof AbstractFamiliarStorageBlockEntity storageEntity
         && storageEntity.getStoredFamiliarCount() > 0
         && random.nextFloat() < 0.1F) {
         if (!this.isDaytime(level) && !storageEntity.isStoreMode()) {
            double d0 = pos.getX() + random.nextDouble();
            double d1 = pos.getY() + random.nextDouble() * 0.5 + 0.5;
            double d2 = pos.getZ() + random.nextDouble();

            for (int i = 0; i < 2; i++) {
               double offsetX = random.nextDouble() * 0.6 - 0.3;
               double offsetY = random.nextDouble() * 0.3;
               double offsetZ = random.nextDouble() * 0.6 - 0.3;
               level.addParticle(
                  (ParticleOptions)FParticleRegistry.SLEEP_PARTICLE.get(), d0 + offsetX, d1 + offsetY, d2 + offsetZ, offsetX * 0.01, 0.05, offsetZ * 0.01
               );
            }
         } else {
            double d0 = pos.getX() + random.nextDouble();
            double d1 = pos.getY() + random.nextDouble() * 0.5 + 0.5;
            double d2 = pos.getZ() + random.nextDouble();
            level.addParticle(this.getParticlesForAnimation(), d0, d1, d2, 0.0, 0.0, 0.0);
         }
      }
   }

   private boolean isDaytime(Level level) {
      long time = level.getDayTime() % 24000L;
      return time >= 1000L && time <= 13000L;
   }
}
