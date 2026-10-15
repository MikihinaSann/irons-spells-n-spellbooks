package net.alshanex.alshanex_familiars.item;

import net.alshanex.alshanex_familiars.registry.ItemRegistry;
import net.alshanex.familiarslib.util.familiars.FamiliarAdvancementsHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class MysteriousOrb extends Item {
   public MysteriousOrb(Properties properties) {
      super(properties);
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
      ItemStack stack = player.getItemInHand(usedHand);
      if (FamiliarAdvancementsHelper.hasCompletedTamingEvents(player)) {
         stack.shrink(1);
         ItemStack harp = new ItemStack((ItemLike)ItemRegistry.BARD_HARP.get(), 1);
         ItemStack bardTrinket = new ItemStack((ItemLike)ItemRegistry.BARD_TRINKET.get(), 1);
         player.getInventory().add(harp);
         player.getInventory().add(bardTrinket);
      }

      return super.use(level, player, usedHand);
   }
}
