package net.alshanex.alshanex_familiars.item;

import net.alshanex.alshanex_familiars.entity.misc.SoulEntity;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class BottledSoul extends Item {
   public BottledSoul() {
      super(new Properties().stacksTo(16).rarity(Rarity.RARE));
   }

   public InteractionResult useOn(UseOnContext context) {
      Level worldIn = context.getLevel();
      Player player = context.getPlayer();
      BlockPos pos = context.getClickedPos();
      ItemStack stack = context.getItemInHand();
      stack.shrink(1);
      player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE, 1));
      SoulEntity soul = new SoulEntity((EntityType<? extends PathfinderMob>)EntityRegistry.SOUL.get(), worldIn);
      soul.setPos(pos.getCenter().add(0.0, 1.0, 0.0));
      soul.setYRot(-player.getYRot());
      worldIn.addFreshEntity(soul);
      return InteractionResult.SUCCESS;
   }
}
