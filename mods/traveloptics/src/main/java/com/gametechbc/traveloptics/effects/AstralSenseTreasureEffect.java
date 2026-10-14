package com.gametechbc.traveloptics.effects;

import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.util.TOTags;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.FogRenderer.FogData;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.client.renderer.FogRenderer.MobEffectFogFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class AstralSenseTreasureEffect extends MobEffect {
   private static final int TNT_RADIUS = 8;
   private static final int LOOT_RADIUS = 12;
   private static final int TRAP_RADIUS = 8;

   public AstralSenseTreasureEffect() {
      super(MobEffectCategory.BENEFICIAL, 16766720);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (entity instanceof Player player) {
         Level level = player.level();
         this.detectPrimedTNT(player, level, 8);
         this.detectLootChest(player, level, 12);
         this.detectTraps(player, level, 8);
         this.isValuableBlockNearby(player, level, 12);
         return true;
      } else {
         return false;
      }
   }

   private void detectLootChest(Player player, Level level, int radius) {
      BlockPos playerPos = player.blockPosition();
      BlockPos nearestChest = null;
      double nearestDistanceSqr = Double.MAX_VALUE;

      for (BlockPos pos : BlockPos.betweenClosed(playerPos.offset(-radius, -radius, -radius), playerPos.offset(radius, radius, radius))) {
         BlockState state = level.getBlockState(pos);
         if (state.getBlock() instanceof ChestBlock chest && level.getBlockEntity(pos) instanceof ChestBlockEntity chestEntity && !chestEntity.isEmpty()) {
            double distanceSqr = pos.distSqr(playerPos);
            if (distanceSqr < nearestDistanceSqr) {
               nearestDistanceSqr = distanceSqr;
               nearestChest = pos;
            }
         }
      }

      if (nearestChest != null) {
         int distance = (int)Math.sqrt(nearestDistanceSqr);
         this.sendMessage(
            player, Component.translatable("spell.traveloptics.message.astral_sense.loot_chest", new Object[]{distance}).withStyle(ChatFormatting.LIGHT_PURPLE)
         );
      }
   }

   private void detectTraps(Player player, Level level, int radius) {
      this.getNearbyBlocks(player.blockPosition(), radius)
         .<BlockState>map(level::getBlockState)
         .filter(state -> state.is(Blocks.TRIPWIRE) && (Boolean)state.getValue(TripWireBlock.ATTACHED))
         .anyMatch(state -> this.sendMessage(player, Component.translatable("spell.traveloptics.message.astral_sense.trap").withStyle(ChatFormatting.RED)));
   }

   private void detectPrimedTNT(Player player, Level level, int radius) {
      if (!level.getEntitiesOfClass(PrimedTnt.class, player.getBoundingBox().inflate(radius)).isEmpty()) {
         this.sendMessage(player, Component.translatable("spell.traveloptics.message.astral_sense.tnt").withStyle(ChatFormatting.RED));
      }
   }

   private Stream<BlockPos> getNearbyBlocks(BlockPos center, int radius) {
      return BlockPos.betweenClosedStream(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius));
   }

   private void isValuableBlockNearby(Player player, Level level, int radius) {
      BlockPos playerPos = player.blockPosition();
      BlockPos nearestBlock = null;
      double nearestDistanceSqr = Double.MAX_VALUE;

      for (BlockPos pos : BlockPos.betweenClosed(playerPos.offset(-radius, -radius, -radius), playerPos.offset(radius, radius, radius))) {
         if (level.getBlockState(pos).is(TOTags.EXTREMELY_VALUABLES)) {
            double distanceSqr = pos.distSqr(playerPos);
            if (distanceSqr < nearestDistanceSqr) {
               nearestDistanceSqr = distanceSqr;
               nearestBlock = pos;
            }
         }
      }

      if (nearestBlock != null) {
         int distance = (int)Math.sqrt(nearestDistanceSqr);
         this.sendMessage(
            player,
            Component.translatable("spell.traveloptics.message.astral_sense.valuable_block", new Object[]{distance}).withStyle(ChatFormatting.LIGHT_PURPLE)
         );
      }
   }

   private boolean sendMessage(Player player, Component message) {
      player.displayClientMessage(message, true);
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return duration % 5 == 0;
   }

   @OnlyIn(Dist.CLIENT)
   public static class AstralSenseFogFunction implements MobEffectFogFunction {
      public AstralSenseFogFunction() {
         super();
      }

      public Holder<MobEffect> getMobEffect() {
         return TOEffects.ASTRAL_SENSE_TREASURE;
      }

      public void setupFog(FogData fogData, LivingEntity entity, MobEffectInstance mobEffectInstance, float p_234184_, float p_234185_) {
         float f = 160.0F;
         if (fogData.mode == FogMode.FOG_SKY) {
            fogData.start = 0.0F;
            fogData.end = f * 0.25F;
         } else {
            fogData.start = -f * 0.5F;
            fogData.end = f;
         }
      }
   }
}
