package net.alshanex.alshanex_familiars.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;

public class MonocleItem extends Item {
   public MonocleItem(Properties properties) {
      super(properties);
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 1200;
   }

   public UseAnim getUseAnimation(ItemStack stack) {
      return UseAnim.SPYGLASS;
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
      player.playSound(SoundEvents.SPYGLASS_USE, 1.0F, 1.0F);
      return ItemUtils.startUsingInstantly(level, player, usedHand);
   }

   public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
      this.stopUsing(livingEntity);
      return stack;
   }

   private void stopUsing(LivingEntity user) {
      user.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0F, 1.0F);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      tooltipComponents.add(Component.translatable("item.alshanex_familiars.monocle.desc"));
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
   }
}
