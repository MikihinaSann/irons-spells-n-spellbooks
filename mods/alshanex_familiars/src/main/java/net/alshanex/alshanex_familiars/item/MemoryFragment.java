package net.alshanex.alshanex_familiars.item;

import net.alshanex.alshanex_familiars.entity.misc.MemoryFragmentEntity;
import net.alshanex.alshanex_familiars.util.AFStructureTags;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;

public class MemoryFragment extends Item {
   public MemoryFragment() {
      super(new Properties().stacksTo(16).rarity(Rarity.UNCOMMON));
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 0;
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack fragment = player.getItemInHand(hand);
      player.startUsingItem(hand);
      if (level instanceof ServerLevel serverlevel) {
         BlockPos blockpos = serverlevel.findNearestMapStructure(AFStructureTags.MEMORY_FRAGMENT_LOCATED, player.blockPosition(), 100, false);
         if (blockpos != null) {
            MemoryFragmentEntity memoryFragment = new MemoryFragmentEntity(level, player.getX(), player.getY(0.5), player.getZ());
            memoryFragment.signalTo(blockpos);
            level.gameEvent(GameEvent.PROJECTILE_SHOOT, memoryFragment.position(), Context.of(player));
            level.addFreshEntity(memoryFragment);
            if (player instanceof ServerPlayer serverplayer) {
               CriteriaTriggers.USED_ENDER_EYE.trigger(serverplayer, blockpos);
            }

            float f = Mth.lerp(level.random.nextFloat(), 0.33F, 0.5F);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 1.0F, f);
            fragment.consume(1, player);
            player.awardStat(Stats.ITEM_USED.get(this));
            player.swing(hand, true);
            return InteractionResultHolder.success(fragment);
         }
      }

      return InteractionResultHolder.consume(fragment);
   }
}
